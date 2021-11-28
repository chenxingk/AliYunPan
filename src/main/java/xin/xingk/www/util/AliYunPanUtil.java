package xin.xingk.www.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;

import javax.swing.*;
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
        uploadFiles(fileId,ConfigUtil.getPath());
        CommonUI.console("本次备份：{} 下所有文件成功！...",ConfigUtil.getPath());
        CommonUI.modifyStartBtnStatus("开始备份",true);
        CommonConstants.BACK_STATE = false;
    }

    /**
     * 上传文件
     */
    private void uploadFiles(String fileId,String rootPath) {
        if (!ConfigUtil.getBackType()){//普通备份
            this.scanFolders(rootPath,fileId,true);
        }else {
            //开始获取文件
            List<String> folderList = FileUtil.fileFolderList(rootPath,FileUtil.FOLDER);//获取用户目录下所有目录
            List<String> folderFileList = FileUtil.fileFolderList(rootPath,FileUtil.FILE);//本地文件夹下文件

            //上传文件夹下文件
            if (folderFileList.size()!=0){
                CommonUI.console("获取：{} 下所有文件成功",rootPath);
                uploadFileList(folderFileList,fileId,true);
            }

            //上传文件夹下所有目录
            if (folderList.size()!=0){
                for (String folderName :  folderList) {
                    String path = rootPath + FileUtil.FILE_SEPARATOR + folderName;//路径
                    uploadTwoLevelFolder(fileId,path);
                }
            }
        }
    }

    /**
     * 上传二级文件夹
     * @param fileId
     * @param path
     */
    private void uploadTwoLevelFolder(String fileId, String path) {
        //CommonConstants.addConsole("开始获取："+path);
        List<String> fileList = FileUtil.fileFolderList(path,FileUtil.FILE);//本地文件夹下文件
        uploadFileList(fileList,fileId,true);
        String folderFileId = this.getFileId(fileId, "文件夹");//微信备份-文件夹
        String dateFileId = this.getFileId(folderFileId, getFolderName(path));//微信备份-文件夹-folderName
        this.scanFolders(path,dateFileId,false);
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
        if (ObjectUtil.isNull(aliYunPanInfo)){
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
        JSONObject list = new JSONObject();
        JSONArray array = new JSONArray();
        list.set("part_number",1);
        array.add(list);
        data.set("drive_id",CommonConstants.DriveId);
        data.set("name",fileInfo.get("name"));
        data.set("type","file");
        data.set("content_type",fileInfo.get("content_type"));
        data.set("size",Integer.parseInt(fileInfo.get("size")));
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
        //CommonConstants.addConsole("开始获取文件夹："+folderName);
        String fileId="";
        JSONObject fileList = getFileList(parentFileId);//获取文件目录
        JSONArray fileArray = fileList.getJSONArray("items");
        if (ObjectUtil.isNotNull(fileArray) && fileArray.size()>0){
            for (int i = 0; i < fileArray.size(); i++) {
                JSONObject folder = fileArray.getJSONObject(i);
                if ("folder".equals(folder.getStr("type")) && folderName.equals(folder.getStr("name"))){
                    fileId = folder.getStr("file_id");
                    return fileId;
                }
            }
        }
        if (StrUtil.isEmpty(fileId)){
            JSONObject folder = createFolder(parentFileId, folderName); //创建备份目录
            fileId=folder.getStr("file_id");
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
            if(ObjectUtil.isNotNull(uploadFile.getJSONArray("part_info_list"))){//上传新文件
                byte[] fileBytes = FileUtil.readBytes(fileInfo.get("path"));
                String uploadUrl = uploadFile.getJSONArray("part_info_list").getJSONObject(0).getStr("upload_url");
                okHttpUtil.uploadFileBytes(uploadUrl,fileBytes);
            }
            String upFileId = uploadFile.getStr("file_id");//文件id
            String uploadId = uploadFile.getStr("upload_id");//上传ID
            if (StrUtil.isEmpty(uploadFile.getStr("exist"))){//上传完成
                completeFile(upFileId, uploadId);
            }
            UploadLogUtil.addFileUploadLog(fileInfo.get("path"),upFileId);
            CommonUI.console("上传文件成功：{}",fileInfo.get("name"));
        }else {
            CommonUI.console("{} 已上传 跳过",fileInfo.get("path"));
        }
        return;
    }

    /**
     * 扫描子目录
     * @param path 本地路径
     * @param pathId 备份目录 ID
     * @param isUploadFile 是否上传文件
     */
    public void scanFolders(String path,String pathId,Boolean isUploadFile){
        CommonUI.console("开始获取：{}",path);
        //获取文件夹下所有文件
        List<String> fileList = FileUtil.fileFolderList(path,FileUtil.FILE);
        //CommonConstants.addConsole("获取："+path+" 下所有文件成功");
        if (isUploadFile){
            uploadFileList(fileList,pathId,false);
        }
        //获得目录下所有文件夹
        List<String> folderList = FileUtil.fileFolderList(path,FileUtil.FOLDER);
        //循环文件夹
        for (String folder : folderList){
            String fileId = getFileId(pathId, folder);//创建文件夹-文件夹ID
            String filePath = path + FileUtil.FILE_SEPARATOR + folder;//路径
            fileList = FileUtil.fileFolderList(path,FileUtil.FILE);//获取当前文件夹下所有文件
            uploadFileList(fileList,fileId,false);//上传当前文件夹内的文件
            //CommonConstants.addConsole("扫描新文件夹："+filePath);
            scanFolders(filePath,fileId,true);
        }
    }

    /**
     * 上传文件夹下文件到某个目录
     * @param fileList 文件list
     * @param pathId 阿里云文件夹ID
     * @param backType 是否开启分类
     */
    public void uploadFileList(List<String> fileList, String pathId,Boolean backType){
        List<String> logList = UploadLogUtil.getFileUploadList();
        fileList.removeAll(logList);
        for (String filePath :  fileList) {
            String fileSuffix = FileUtil.getSuffix(filePath);//文件后缀
            if (FileUtil.getPrefix(filePath).startsWith("~$") || fileSuffix.length()>=8){
                continue;
            }
            try {
                Map<String, String> map = FileUtil.getFileInfo(filePath);
                if (backType){//开启分类
                    String type = map.get("type");
                    String typeFileId=this.getFileId(pathId,type);//微信备份-类型
                    String dateFileId=this.getFileId(typeFileId,getFolderName(filePath));//微信备份-类型-文件夹
                    doUploadFile(dateFileId,map);
                }else {
                    doUploadFile(pathId,map);
                }
            } catch (Exception e) {
                CommonUI.console("遇到异常情况：{}",e.toString());
            }
        }
    }

    /**
     * 获取文件夹名称
     * @param thisPath
     * @return
     */
    public String getFolderName(String thisPath) {
        String folderName = StrUtil.subAfter(thisPath , ConfigUtil.getPath()+"\\", false);
        if (StrUtil.isNotEmpty(folderName) && !folderName.contains("\\")){
            //写入文件目录
            //writerLog.append(CommonConstants.PATH + FileUtil.FILE_SEPARATOR+folderName + "");
        }else{
            folderName = StrUtil.subBefore(folderName , "\\", false);
        }
        return folderName;
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
                uploadFiles(fileId,ConfigUtil.getPath());
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
    
    
}
