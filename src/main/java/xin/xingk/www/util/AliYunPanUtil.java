package xin.xingk.www.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 操作阿里云盘工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class AliYunPanUtil{

    //Token文本框
    JTextField tokenText = CommonUI.tokenText;
    //请求工具类
    OkHttpUtil okHttpUtil = new OkHttpUtil();
    //二级目录ID存放
    Map<String, String> fileIdMap = new HashMap<>();

    /**
     * 开始备份
     */
    public void startBackup() {
        CommonConstants.CLEAN_CONSOLE = 1;
        CommonConstants.BACK_STATE = true;
        boolean login = this.getAliYunPanInfo();//登录阿里云
        if (!login) return;
        if (!checkConfig()) return;
        String fileId = this.getFileId(CommonConstants.ROOT, ConfigUtil.getBackName());//备份目录ID
        fileIdMap.put(ConfigUtil.getPath(),fileId);//备份目录的文件夹ID
        scanFolders(ConfigUtil.getPath(),fileId,ConfigUtil.getBackupType());
        fileIdMap = new HashMap<>();
        CommonUI.console("本次备份：{} 下所有文件成功！...",ConfigUtil.getPath());
        CommonUI.modifyStartBtnStatus("开始备份",true);
        CommonConstants.BACK_STATE = false;
    }

    /**
     * 扫描子目录
     * @param path 路径
     * @param fileId 文件夹ID
     * @param backupType true 普通备份 false 分类备份 是否分类备份
     */
    public void scanFolders(String path,String fileId,int backupType){
        CommonUI.console("开始获取：{}",path);
        //获取文件夹下所有文件
        List<String> fileList = FileUtil.getFileList(path);
        //上传文件
        uploadFileList(fileList,fileId,backupType);
        //获得目录下所有文件夹
        List<String> folderList = FileUtil.getFileFolder(path);
        //循环文件夹
        for (String folder : folderList){
            String filePath = path + FileUtil.FILE_SEPARATOR + folder;//完整路径
            if (backupType==0){//普通备份
                fileId = getPathFileId(path,fileId,folder);
            }else if (backupType==2){//微信备份
                //获取月份文件夹
                String month = getMonth(filePath);
                if (StrUtil.isNotEmpty(month)){
                    //备份目录ID
                    String rootId = fileIdMap.get(ConfigUtil.getPath());
                    //获取月份文件夹ID
                    fileId = getPathFileId(ConfigUtil.getPath()+FileUtil.FILE_SEPARATOR+month,rootId,month);
                }
                if (twoFolder(path)){//微信下的二级目录
                    fileId = getPathFileId(path,fileId,"文件夹");
                    backupType = 0;
                }else{
                    backupType = 2;
                }
            }
            scanFolders(filePath,fileId,backupType);
        }
    }

    /**
     * 获取月份文件夹
     * @param path
     * @return
     */
    public String getMonth(String path) {
        if (twoFolder(path)){
            return StrUtil.subBetween(path, ConfigUtil.getPath()+ FileUtil.FILE_SEPARATOR,FileUtil.FILE_SEPARATOR);
        }else{
            return StrUtil.subSuf(path, ConfigUtil.getPath().length()+1);
        }
    }

    /**
     * 根据path 获取文件夹ID
     * @param path 路径
     * @param fileId 当前文件夹ID
     * @param folder 文件夹名称
     * @return
     */
    public String getPathFileId(String path, String fileId, String folder) {
        //普通备份
        if (StrUtil.isEmpty(fileIdMap.get(path))){
            fileIdMap.put(path,fileId);
            fileId = getFileId(fileId, folder);//获取文件夹ID
        }else{
            fileId = getFileId(fileIdMap.get(path), folder);
        }
        return fileId;
    }

    /**
     * 是否为二级目录
     * @param path
     * @return
     */
    public boolean twoFolder(String path) {
        String str = StrUtil.subAfter(path, ConfigUtil.getPath(), false);
        return StrUtil.count(str, FileUtil.FILE_SEPARATOR)>1;
    }

    /**
     * 上传文件夹下文件到某个目录
     * @param fileList 文件list
     * @param pathId 文件夹ID
     * @param backupType true 普通备份 false 分类备份 是否分类备份
     */
    public void uploadFileList(List<String> fileList, String pathId,int backupType){
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
                    String fileId=this.getFileId(pathId,type);//微信备份-类型
                    doUploadFile(fileId,map);
                }
            } catch (Exception e) {
                CommonUI.console("遇到异常情况：{}",e.toString());
            }
        }
    }

    /**
     * 上传监控目录
     * @param path 文件路径
     * @param fileName 文件名称
     */
    public void monitorUpload(String path,String fileName) {
        CommonUI.console("检测到：{} 目录有新文件...",path);
        if (checkConfig()){
            Thread backup = new Thread(() -> {
                boolean login = getAliYunPanInfo();//登录阿里云
                if (!login){
                    return;
                }
                String fileId = this.getFileId(CommonConstants.ROOT, ConfigUtil.getBackName());//备份目录ID
                //uploadFiles(fileId,ConfigUtil.getPath());
            });
            backup.start();
        }
    }

    /**
     * 验证配置文件
     */
    public Boolean checkConfig(){
        if (StrUtil.isEmpty(ConfigUtil.getPath())){
            CommonUI.console("您没有选择需要备份的目录");
            return false;
        }
        if (StrUtil.isEmpty(ConfigUtil.getRefreshToken())){
            CommonUI.console( "您没有输入阿里云token");
            return false;
        }
        if (StrUtil.isEmpty(ConfigUtil.getBackName())){
            CommonUI.console("您没有输入需要备份到阿里云的目录");
            return false;
        }
        if (ConfigUtil.getRefreshToken().length()!=32){
            CommonUI.console("您输入的token不正确");
            return false;
        }
        if (!FileUtil.isDirectory(ConfigUtil.getPath())){
            CommonUI.console("请选择正确目录");
            return false;
        }
        return true;
    }

    /**
     * 获取阿里云用户信息
     * @return
     */
    public boolean getAliYunPanInfo(){
        if (StrUtil.isEmpty(ConfigUtil.getRefreshToken())){
            CommonUI.modifyStartBtnStatus("开始备份",true);
            return false;
        }
        CommonUI.console("开始登录阿里云盘...");
        CommonConstants.TOKEN="";
        JSONObject data = new JSONObject().set("refresh_token",ConfigUtil.getRefreshToken());
        JSONObject aliYunPanInfo = okHttpUtil.doPost(CommonConstants.TOKEN_URL, data);
        if (ObjectUtil.isNull(aliYunPanInfo) || "InvalidParameter.RefreshToken".equals(aliYunPanInfo.getStr("code"))){
            CommonUI.console("登录失败...请检查Token填写是否正确...");
            CommonUI.modifyStartBtnStatus("开始备份",true);
            return false;
        }
        doSetAliYunInfo(aliYunPanInfo);
        if (StrUtil.isNotEmpty(CommonConstants.TOKEN)){
            CommonUI.console("登录阿里云盘成功...");
        }
        return true;
    }

    /**
     * 设置阿里云盘登录信息
     * @param json
     */
    private void doSetAliYunInfo(JSONObject json) {
        CommonConstants.TOKEN = json.getStr("token_type") + " " + json.getStr("access_token");
        CommonConstants.DriveId = json.getStr("default_drive_id");
        ConfigUtil.set(CommonConstants.REFRESH_TOKEN, json.getStr("refresh_token"));
        tokenText.setText(StrUtil.hide(ConfigUtil.getRefreshToken(),10,20));
    }

    /**
     * 获取阿里云文件列表
     * @param fileId
     * @return
     */
    public JSONObject getFileList(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("parent_file_id",fileId);
        data.set("limit",100);
        data.set("all",false);
        data.set("image_thumbnail_process","image/resize,w_400/format,jpeg");
        data.set("image_url_process","image/resize,w_1920/format,jpeg");
        data.set("video_thumbnail_process","video/snapshot,t_0,f_jpg,ar_auto,w_300");
        data.set("fields","*");
        data.set("url_expire_sec",1600);
        data.set("order_by","updated_at");
        data.set("order_direction","DESC");
        return okHttpUtil.doPost(CommonConstants.FILE_LIST_URL,data);
    }

    /**
     * 创建文件夹
     * @param fileId 父级目录ID 根目录为root
     * @param name 新文件夹名称
     * @return
     */
    public JSONObject createFolder(String fileId,String name){
        //创建文件夹
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("parent_file_id",fileId);
        data.set("name",name);
        data.set("type","folder");
        data.set("check_name_mode","refuse");
        return okHttpUtil.doPost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 上传文件到阿里云盘
     * @return
     */
    public JSONObject uploadFile(String fileId, Map<String, String> fileInfo){
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        long max = Long.parseLong(fileInfo.get("max"));
        for (int i = 1; i <= max; i++) {
            JSONObject list = new JSONObject();
            list.set("part_number",i);
            array.add(list);
        }
        data.set("drive_id",CommonConstants.DriveId);
        data.set("name",fileInfo.get("name"));
        data.set("type","file");
        data.set("content_type",fileInfo.get("content_type"));
        data.set("size",Long.valueOf(fileInfo.get("size")));
        data.set("parent_file_id",fileId);
        data.set("part_info_list",array);
        data.set("content_hash_name","sha1");
        data.set("content_hash",fileInfo.get("content_hash"));
        data.set("ignoreError",false);
        data.set("check_name_mode","refuse");
        return okHttpUtil.doFilePost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 完成文件上传
     * @param fileId
     * @param uploadId
     * @return
     */
    public JSONObject completeFile(String fileId,String uploadId){
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        data.set("ignoreError",true);
        data.set("part_info_list",array);
        data.set("upload_id",uploadId);
        return okHttpUtil.doPost(CommonConstants.COMPLETE_FILE_URL,data);
    }

    /**
     * 删除阿里云盘文件
     * @param fileId
     * @returnn
     */
    public void deleteFile(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        okHttpUtil.deleteFile(data);
    }

    /**
     * 获取文件夹ID
     * @param parentFileId 父级文件夹ID
     * @param folderName 文件夹名称
     * @return
     */
    public String getFileId(String parentFileId,String folderName){
        String fileId="";
        JSONObject fileList = getFileList(parentFileId);//获取文件目录
        JSONArray fileArray = fileList.getJSONArray("items");
        if (ObjectUtil.isNotNull(fileArray) && fileArray.size()>0){
            for (int i = 0; i < fileArray.size(); i++) {
                JSONObject folder = fileArray.getJSONObject(i);
                if ("folder".equals(folder.getStr("type")) && folderName.equals(folder.getStr("name"))){
                    return folder.getStr("file_id");
                }
            }
        }
        if (StrUtil.isEmpty(fileId)){
            JSONObject folder = createFolder(parentFileId, folderName); //创建备份目录
            return folder.getStr("file_id");
        }
        return fileId;
    }

    /**
     * 执行文件上传
     * @param fileId 文件夹ID
     * @param fileInfo 文件信息
     */
    public void doUploadFile(String fileId,Map<String, String> fileInfo){
        //写入上传文件的路径
        String id = UploadLogUtil.getFileUploadFileId(fileInfo.get("path"));
        if (StrUtil.isEmpty(id)){
            CommonUI.console("开始上传：{}",fileInfo.get("path"));
            JSONObject uploadFile = uploadFile(fileId,fileInfo);
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
                    int code = okHttpUtil.uploadFileBytes(uploadUrl, fileBytes);
                    double progress = ((double) (i+1) / part_info_list.size()) * 100;
                    CommonUI.console("文件：{} 上传进度：{}% 状态码： {}",fileInfo.get("name"), NumberUtil.roundStr(progress,2),code);
                    position += CommonConstants.DEFAULT_SIZE;
                    size -= CommonConstants.DEFAULT_SIZE;
                }
            }
            String upFileId = uploadFile.getStr("file_id");//文件id
            String uploadId = uploadFile.getStr("upload_id");//上传ID
            if (StrUtil.isEmpty(uploadFile.getStr("exist"))){//上传完成
                JSONObject result = completeFile(upFileId, uploadId);
                if ("available".equals(result.getStr("status")) || StrUtil.isNotEmpty(result.getStr("created_at"))){
                    UploadLogUtil.addFileUploadLog(fileInfo.get("path"),upFileId);
                    CommonUI.console("上传文件成功：{}",fileInfo.get("name"));
                }
            }else if ("available".equals(uploadFile.getStr("status"))){//已经存在的文件
                UploadLogUtil.addFileUploadLog(fileInfo.get("path"),upFileId);
                CommonUI.console("上传文件成功：{}",fileInfo.get("name"));
            }
        }else {
            CommonUI.console("{} 已上传 跳过",fileInfo.get("path"));
        }
        return;
    }
    
    
}
