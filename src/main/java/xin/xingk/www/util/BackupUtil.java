package xin.xingk.www.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.ui.Home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 操作阿里云盘工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class BackupUtil {


    /**
     * 开始备份
     * @param id 备份任务ID
     */
    public static void startBackup(Integer id) {
        CommonConstants.CLEAN_CONSOLE = 1;
        boolean login = AliYunUtil.login();//登录阿里云
        if (!login) return;
        if (id == null){
            List<Backup> backupList = BackupContextHolder.getBackupList();
            for (Backup backup : backupList) {
                backupTask(backup);
            }
            Home.getInstance().getStartButton().setEnabled(true);
        }else {
            Backup backup = BackupContextHolder.getBackupById(id);
            backupTask(backup);
        }
    }

    /**
     * 备份任务
     * @param backup
     */
    public static void backupTask(Backup backup) {
        //备份目录ID
        String fileId = AliYunUtil.getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));
        backup.setFileId(fileId);
        scanFolders(backup.getLocalPath(), fileId, backup.getBackupType(),backup);
        UIUtil.console("本次备份：{} 下所有文件成功！...", backup.getLocalPath());
    }


    /**
     * 扫描子目录
     * @param path 路径
     * @param fileId 文件夹ID
     * @param backupType true 普通备份 false 分类备份 是否分类备份
     */
    public static void scanFolders(String path,String fileId,int backupType,Backup backup){
        UIUtil.console("开始获取：{}",path);
        //获取文件夹下所有文件
        List<String> fileList = FileUtil.getFileList(path);
        //上传文件
        uploadFileList(fileList,fileId,backupType);
        //获得目录下所有文件夹
        List<String> folderList = FileUtil.getFileFolder(path);
        //循环文件夹
        for (String folder : folderList){
            String filePath = path + FileUtil.FILE_SEPARATOR + folder;//完整路径
            Map<String, Object> result = AliYunUtil.getFileIdByPath(filePath,backup);
            scanFolders(filePath,Convert.toStr(result.get("fileId")),Convert.toInt(result.get("backupType")),backup);
        }
    }


    /**
     * 上传文件夹下文件到某个目录
     * @param fileList 文件list
     * @param pathId 文件夹ID
     * @param backupType true 普通备份 false 分类备份 是否分类备份
     */
    public static void uploadFileList(List<String> fileList, String pathId,int backupType){
        if (ObjectUtil.isEmpty(fileList)) return;
        List<String> logList = UploadLogUtil.getFileUploadList();
        fileList.removeAll(logList);
        for (String filePath :  fileList) {
            String fileSuffix = FileUtil.getSuffix(filePath);//文件后缀
            if (FileUtil.getPrefix(filePath).startsWith("~$") || fileSuffix.length()>=8){
                continue;
            }
            try {
                Map<String, String> map = FileUtil.getFileInfo(filePath);
                if (backupType==0){//普通备份
                    doUploadFile(pathId,map);
                }else {
                    String type = map.get("type");
                    String fileId = AliYunUtil.getFileId(pathId,type);//微信备份-类型
                    doUploadFile(fileId,map);
                }
            } catch (Exception e) {
                UIUtil.console("遇到异常情况：{}",e.toString());
            }
        }
    }

    /**
     * 上传监控目录
     * @param path 文件路径
     * @param fileName 文件名称
     */
    public static void monitorUpload(String path,String fileName,Backup backup) {
            boolean login = AliYunUtil.login();//登录阿里云
            if (!login) return;
            //获取文件ID
            Map<String, Object> result = AliYunUtil.getFileIdByPath(path,backup);
            //上传文件
            List<String> fileList = new ArrayList<>();
            fileList.add(path+FileUtil.FILE_SEPARATOR+fileName);
            uploadFileList(fileList, Convert.toStr(result.get("fileId")),Convert.toInt(result.get("backupType")));
    }

    /**
     * 执行文件上传
     * @param fileId 文件夹ID
     * @param fileInfo 文件信息
     */
    public static void doUploadFile(String fileId,Map<String, String> fileInfo){
        //写入上传文件的路径
        String id = UploadLogUtil.getFileUploadFileId(fileInfo.get("path"));
        if (StrUtil.isEmpty(id)){
            UIUtil.console("开始上传：{}",fileInfo.get("path"));
            JSONObject uploadFile = AliYunUtil.uploadFile(fileId,fileInfo);
            JSONArray part_info_list = uploadFile.getJSONArray("part_info_list");
            if(ObjectUtil.isNotEmpty(part_info_list)){//上传新文件
                int position = 0;//文件流位置
                Long size = Long.parseLong(fileInfo.get("size"));//文件大小
                for (int i = 0; i < part_info_list.size(); i++) {
                    byte[] fileBytes;
                    if (size>CommonConstants.DEFAULT_SIZE.longValue()){
                        fileBytes = FileUtil.readByte(fileInfo.get("path"), position, CommonConstants.DEFAULT_SIZE);
                    }else{
                        fileBytes = FileUtil.readByte(fileInfo.get("path"), position, size.intValue());
                    }
                    String uploadUrl = part_info_list.getJSONObject(i).getStr("upload_url");
                    int code = OkHttpUtil.uploadFileBytes(uploadUrl, fileBytes);
                    double progress = ((double) (i+1) / part_info_list.size()) * 100;
                    UIUtil.console("文件：{} 上传进度：{}% 状态码： {}",fileInfo.get("name"), NumberUtil.roundStr(progress,2),code);
                    position += CommonConstants.DEFAULT_SIZE;
                    size -= CommonConstants.DEFAULT_SIZE;
                }
            }
            String upFileId = uploadFile.getStr("file_id");//文件id
            String uploadId = uploadFile.getStr("upload_id");//上传ID
            if (StrUtil.isEmpty(uploadFile.getStr("exist"))){//上传完成
                JSONObject result = AliYunUtil.completeFile(upFileId, uploadId);
                if ("available".equals(result.getStr("status")) || StrUtil.isNotEmpty(result.getStr("created_at"))){
                    UploadLogUtil.addFileUploadLog(fileInfo.get("path"),upFileId);
                    UIUtil.console("上传文件成功：{}",fileInfo.get("name"));
                }
            }else if ("available".equals(uploadFile.getStr("status"))){//已经存在的文件
                UploadLogUtil.addFileUploadLog(fileInfo.get("path"),upFileId);
                UIUtil.console("上传文件成功：{}",fileInfo.get("name"));
            }
        }else {
            UIUtil.console("{} 已上传 跳过",fileInfo.get("path"));
        }

    }
    
    
}
