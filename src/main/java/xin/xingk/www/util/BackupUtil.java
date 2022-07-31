package xin.xingk.www.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.aliyun.CloudFile;
import xin.xingk.www.entity.aliyun.FileInfo;
import xin.xingk.www.ui.Home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 阿里云盘备份工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class BackupUtil {


    /**
     * 开始备份
     * @param id 备份任务ID
     */
    public static void startBackup(Integer id) {
        boolean login = AliYunUtil.login();//登录阿里云
        if (!login) {
            UIUtil.console("Token已过期，请退出重新登录。。。");
            return;
        }
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
     * @param backup 备份任务
     */
    public static void backupTask(Backup backup) {
        if (checkBackupStatus(backup)) return;
        CacheUtil.setBackupStatus(backup.getId(), DictConstants.STATUS_BACKUP_RUN);
        //备份目录ID
        String fileId = AliYunUtil.getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));
        scanFolders(backup.getLocalPath(), fileId, backup.getBackupType(),backup);
        UIUtil.console("本次备份：{} 下所有文件成功！...", backup.getLocalPath());
        CacheUtil.removeBackupStatus(backup.getId());
    }

    /**
     * 验证备份任务状态
     * @param backup 备份任务
     * @return
     */
    public static boolean checkBackupStatus(Backup backup) {
        Integer status = CacheUtil.getBackupStatus(backup.getId());
        if (ObjectUtil.isEmpty(status)) return false;
        if (DictConstants.STATUS_BACKUP_RUN.equals(status)){
            UIUtil.console("本地目录：{}，正在备份中，本次备份跳过。。。",backup.getLocalPath());
            return true;
        }else if (DictConstants.STATUS_SYNC_RUN.equals(status)){
            UIUtil.console("本地目录：{}，正在同步中，本次备份跳过。。。",backup.getLocalPath());
            return true;
        }
        return false;
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
        List<CloudFile> cloudFileList = AliYunUtil.getCloudFileList(pathId);
        Map<String, CloudFile> cloudFileMaps = cloudFileList.stream().collect(Collectors.toMap(CloudFile::getName, Function.identity(), (key1, key2) -> key2));
        for (String filePath :  fileList) {
            if (CommonConstants.stopUpload){
                CommonConstants.stopUpload = false;
                List<Backup> backupList = BackupContextHolder.getBackupList();
                for (Backup backup : backupList) {
                    CacheUtil.removeBackupStatus(backup.getId());
                }
                Home.getInstance().getStartButton().setEnabled(true);
                Integer a = null;
                System.out.println(backupType + a);
            }
            String fileSuffix = FileUtil.getSuffix(filePath);//文件后缀
            if (FileUtil.getPrefix(filePath).startsWith("~$") || fileSuffix.length()>=8) continue;
            try {
                //获取本地文件信息
                FileInfo fileInfo = FileUtil.getFileInfo(filePath);
                //获取云文件信息
                CloudFile cloudFile = cloudFileMaps.get(fileInfo.getName());
                if(ObjectUtil.isNotEmpty(cloudFile)){
                    //Hash一致 上传过
                    if (fileInfo.getContentHash().equals(cloudFile.getContentHash())){
                        UIUtil.console("{} 云盘已存在 跳过",fileInfo.getPath());
                        return;
                    }
                }
                if (backupType==0){//普通备份
                    doUploadFile(pathId,fileInfo);
                }else {
                    String fileId = AliYunUtil.getFileId(pathId,fileInfo.getType());//微信备份-类型
                    doUploadFile(fileId,fileInfo);
                }
            } catch (Exception e) {
                UIUtil.console("遇到异常情况：{}",ExceptionUtil.stacktraceToString(e));
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
        if (!login) {
            UIUtil.console("Token已过期，请退出重新登录。。。");
            return;
        }
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
    public static void doUploadFile(String fileId,FileInfo fileInfo){
        //if (!getCloudFileExist(fileInfo,fileId)){
            UIUtil.console("开始上传：{}",fileInfo.getPath());
            JSONObject uploadFile = AliYunUtil.uploadFile(fileId,fileInfo);
            String upFileId = uploadFile.getStr("file_id");//文件id
            if (uploadFile.getBool("rapid_upload")){
                UIUtil.console("{} 秒传完成",fileInfo.getPath());
            }else {
                JSONArray part_info_list = uploadFile.getJSONArray("part_info_list");
                if(ObjectUtil.isNotEmpty(part_info_list)){//上传新文件
                    //文件流位置
                    long position = 0;
                    //文件大小
                    long size = fileInfo.getSize();
                    for (int i = 0; i < part_info_list.size(); i++) {
                        byte[] fileBytes;
                        if (size>CommonConstants.DEFAULT_SIZE.longValue()){
                            fileBytes = FileUtil.readBytes(fileInfo.getPath(), position, CommonConstants.DEFAULT_SIZE);
                        }else{
                            fileBytes = FileUtil.readBytes(fileInfo.getPath(), position, (int) size);
                        }
                        String uploadUrl = part_info_list.getJSONObject(i).getStr("upload_url");
                        int code = OkHttpUtil.uploadFileBytes(uploadUrl, fileBytes);
                        double progress = ((double) (i+1) / part_info_list.size()) * 100;
                        UIUtil.console("文件：{} 上传进度：{}% 状态码： {}",fileInfo.getName(), NumberUtil.roundStr(progress,2),code);
                        position += CommonConstants.DEFAULT_SIZE;
                        size -= CommonConstants.DEFAULT_SIZE;
                    }
                    //上传ID
                    String uploadId = uploadFile.getStr("upload_id");
                    //完成文件上传
                    JSONObject result = AliYunUtil.completeFile(upFileId, uploadId);
                    if ("available".equals(result.getStr("status")) || StrUtil.isNotEmpty(result.getStr("created_at"))){
                        UIUtil.console("{}，上传完成！",fileInfo.getPath());
                    }
                }
            }
        /*}else {
            UIUtil.console("{} 云盘已存在 跳过",fileInfo.getPath());
        }*/
    }

    /**
     * 判断文件是否存在云盘
     * @param fileInfo 本地文件信息
     * @param fileId 云盘目录ID
     * @return true 存在
     */
    private static boolean getCloudFileExist(FileInfo fileInfo, String fileId) {
        List<CloudFile> cloudFileList = AliYunUtil.search(fileId, fileInfo.getName(), DictConstants.FILE_TYPE_FILE);
        //搜索云盘无此文件
        if(ObjectUtil.isEmpty(cloudFileList)){
            return false;
        }
        Optional<CloudFile> cloudFile = cloudFileList.stream().filter(f ->
                DictConstants.FILE_TYPE_FILE.equals(f.getType())
                        && f.getName().equals(fileInfo.getName())
        ).findFirst();
        if (cloudFile.isPresent()){
            if (fileInfo.getContentHash().equals(cloudFile.get().getContentHash())){
                //Hash一致 上传过
                return true;
            }
            //并且Hash不一致 先删除在重新上传
            AliYunUtil.deleteFile(cloudFile.get().getFileId());
            return false;
        }
        return false;
    }

}
