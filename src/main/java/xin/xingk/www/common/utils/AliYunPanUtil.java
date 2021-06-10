package xin.xingk.www.common.utils;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.CommonConstants;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Map;

/**
 * Description: 操作阿里云盘工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class AliYunPanUtil{

    //配置文件
    Setting setting =CommonConstants.setting;
    //Token文本框
    JTextField tokenText = CommonConstants.tokenText;
    // 开始备份
    private JButton startBackup = CommonConstants.startBackup;
    //请求工具类
    OkHttpUtil okHttpUtil = new OkHttpUtil();
    FileWriter writerLog = CommonConstants.writerLog;
    FileReader readerLog = CommonConstants.readerLog;

    /**
     * 开始备份
     */
    public void startBackup() {
        CommonConstants.CLEAN_CONSOLE=1;
        CommonConstants.BACK_STATE = true;
        boolean login = this.getAliYunPanInfo();//登录阿里云
        if (!login) return;
        if (!checkConfig()) return;
        CommonConstants.FILE_ID = this.getFileId(CommonConstants.ROOT, CommonConstants.BACK_NAME);//备份目录ID
        if (CommonConstants.BACK_TYPE==0){//普通备份
            this.scanFolders(CommonConstants.PATH,CommonConstants.FILE_ID,true);
        }else {
            //开始获取文件
            List<String> folderList = FileUtil.fileFolderList(CommonConstants.PATH,FileUtil.FOLDER);//获取用户目录下所有目录
            List<String> folderFileList = FileUtil.fileFolderList(CommonConstants.PATH,FileUtil.FILE);//本地文件夹下文件

            //上传文件夹下文件
            if (folderFileList.size()!=0){
                CommonConstants.addConsole("获取："+CommonConstants.PATH+" 下所有文件成功");
                uploadFileList(folderFileList,CommonConstants.FILE_ID,true);
            }

            //上传文件夹下所有目录
            if (folderList.size()!=0){
                for (String folderName :  folderList) {
                    String path = CommonConstants.PATH + FileUtil.FILE_SEPARATOR + folderName;//路径
                    uploadTwoLevelFolder(CommonConstants.FILE_ID,path);
                }
            }

        }
        CommonConstants.addConsole("本次备份："+CommonConstants.PATH+" 下所有文件成功！...");
        startBackup.setText("开始备份");
        startBackup.setEnabled(true);
        CommonConstants.BACK_STATE = false;
        return;
    }

    /**
     * 上传二级文件夹
     * @param fileId
     * @param path
     */
    private void uploadTwoLevelFolder(String fileId, String path) {
        CommonConstants.addConsole("开始获取："+path);
        List<String> fileList = FileUtil.fileFolderList(path,FileUtil.FILE);//本地文件夹下文件
        uploadFileList(fileList,fileId,true);
        String folderFileId = this.getFileId(fileId, "文件夹");//微信备份-文件夹
        String dateFileId = this.getFileId(folderFileId, getFolderName(path));//微信备份-文件夹-folderName
        this.scanFolders(path,dateFileId,false);
    }

    /**
     * 获取阿里云用户信息
     * @return
     * @throws Exception
     */
    public boolean getAliYunPanInfo(){
        if (StrUtil.isEmpty(setting.getStr("tokenText"))){
            startBackup.setText("开始备份");
            startBackup.setEnabled(true);
            return false;
        }
        CommonConstants.addConsole("开始登录阿里云盘...");
        CommonConstants.TOKEN="";
        JSONObject data = new JSONObject();
        data.set("refresh_token",setting.getStr("tokenText"));
        JSONObject aliYunPanInfo = okHttpUtil.doPost(CommonConstants.TOKEN_URL, data);
        if (ObjectUtil.isNull(aliYunPanInfo)){
            CommonConstants.addConsole("登录失败...请检查Token填写是否正确...");
            startBackup.setText("开始备份");
            startBackup.setEnabled(true);
            return false;
        }
        doSetAliYunInfo(aliYunPanInfo);
        if (StrUtil.isNotEmpty(CommonConstants.TOKEN)){
            CommonConstants.addConsole("登录阿里云盘成功...");
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
        CommonConstants.REFRESH_TOKEN = json.getStr("refresh_token");
        setting.set("tokenText", json.getStr("refresh_token"));
        tokenText.setText(setting.getStr("tokenText").substring(0,16)+"****************");
        setting.store(CommonConstants.CONFIG_PATH);
    }

    /**
     * 获取阿里云文件列表
     * @param fileId
     * @return
     * @throws Exception
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
        data.set("order_by","updated_at");
        data.set("order_direction","DESC");
        return okHttpUtil.doPost(CommonConstants.FILE_LIST_URL,data);
    }

    /**
     * 创建文件夹
     * @param fileId 父级目录ID 根目录为root
     * @param name 新文件夹名称
     * @return
     * @throws Exception
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
     * @throws Exception
     */
    public JSONObject uploadFile(String fileId, Map<String, Object> fileInfo){
        JSONObject data = new JSONObject();
        JSONObject list = new JSONObject();
        JSONArray array = new JSONArray();
        list.set("part_number",1);
        array.add(list);
        data.set("drive_id",CommonConstants.DriveId);
        data.set("name",fileInfo.get("name"));
        data.set("type","file");
        data.set("content_type",fileInfo.get("content_type"));
        data.set("size",fileInfo.get("size"));
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
     * @throws Exception
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
     * @return
     * @throws Exception
     */
    public JSONObject deleteFile(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        return okHttpUtil.doPost(CommonConstants.DELETE_FILE_URL,data);
    }

    /**
     * 获取文件夹ID
     * @param parentFileId 父级文件夹ID
     * @param folderName 文件夹名称
     * @return
     * @throws Exception
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
     * @throws Exception
     */
    public void doUploadFile(String fileId,Map<String, Object> fileInfo){
        if (fileInfo.get("name").toString().startsWith("~$")){
            return;
        }
        //写入上传文件的路径
        List<String> logList = readerLog.readLines();
        if (!logList.contains(fileInfo.get("path"))){
            CommonConstants.addConsole("开始上传："+fileInfo.get("path"));
            JSONObject uploadFile = uploadFile(fileId,fileInfo);
            if(ObjectUtil.isNotNull(uploadFile.getJSONArray("part_info_list"))){//上传新文件
                byte[] fileBytes = FileUtil.readBytes(fileInfo.get("path").toString());
                String uploadUrl = uploadFile.getJSONArray("part_info_list").getJSONObject(0).getStr("upload_url");
                okHttpUtil.uploadFileBytes(uploadUrl,fileBytes);
            }
            if (StrUtil.isEmpty(uploadFile.getStr("exist"))){//上传完成
                String upFileId = uploadFile.getStr("file_id");
                String uploadId = uploadFile.getStr("upload_id");
                completeFile(upFileId, uploadId);
            }
            writerLog.append(fileInfo.get("path")+ "\n");
            CommonConstants.addConsole("上传文件成功："+fileInfo.get("name"));
        }else {
            CommonConstants.addConsole(fileInfo.get("path")+" 已上传 跳过");
        }
        return;
    }

    /**
     * 扫描子目录
     * @param path 本地路径
     * @param pathId 备份目录 ID
     * @param isUploadFile 是否上传文件
     * @throws Exception
     */
    public void scanFolders(String path,String pathId,Boolean isUploadFile){
        CommonConstants.addConsole("开始获取："+path);
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
     * @throws Exception
     */
    public void uploadFileList(List<String> fileList, String pathId,Boolean backType){
        List<String> logList = readerLog.readLines();
        fileList.removeAll(logList);
        for (String filePath :  fileList) {
            try {
                Map<String, Object> map = FileUtil.getFileInfo(filePath);
                if (backType){//开启分类
                    String type = map.get("type").toString();
                    String typeFileId=this.getFileId(pathId,type);//微信备份-类型
                    String dateFileId=this.getFileId(typeFileId,getFolderName(filePath));//微信备份-类型-文件夹
                    doUploadFile(dateFileId,map);
                }else {
                    doUploadFile(pathId,map);
                }
            } catch (Exception e) {
                CommonConstants.addConsole("遇到异常情况："+e.toString());
            }
        }
    }

    /**
     * 获取文件夹名称
     * @param thisPath
     * @return
     */
    public String getFolderName(String thisPath) {
        String folderName = StrUtil.subAfter(thisPath , CommonConstants.PATH+"\\", false);
        if (StrUtil.isNotEmpty(folderName) && !folderName.contains("\\")){
            //写入文件目录
            //writerLog.append(CommonConstants.PATH + FileUtil.FILE_SEPARATOR+folderName + "");
        }else{
            folderName = StrUtil.subBefore(folderName , "\\", false);
        }
        return folderName;
    }

    /**
     * 监控目录
     */
    public void monitorFolder() {
        if (StrUtil.isEmpty(setting.getStr("pathText"))) return;
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //开启目录检测 开始获取文件夹
        Console.log("开启目录检测");
        CommonConstants.addConsole("开启目录检测");
        WatchMonitor monitor = WatchMonitor.createAll(setting.getStr("pathText"), new DelayWatcher(new SimpleWatcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("创建：{}-> {}", currentPath, obj);
                //获取文件后缀
                String fileSuffix = FileUtil.getSuffix(obj.toString());
                //备份方法不执行时候执行监听
                //排除掉未下载完的文件 后缀长度超过8个字符的不处理
                if (!CommonConstants.BACK_STATE && fileSuffix.length()<=8 && !obj.toString().startsWith("~$")){
                     uploadMonitor(currentPath, obj);
                }
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("修改：{}-> {}", currentPath, obj);
                //备份方法不执行时候执行监听
                /*if (!CommonConstants.BACK_STATE){
                    String path = currentPath.toString() + "\\" + obj.toString();
                    //CommonConstants.addConsole("检测到："+path+" 文件发生变化！");
                    List<String> logList = readerLog.readLines();
                    logList.remove(path);
                    writerLog.writeLines(logList);
                    uploadMonitor(currentPath,obj);
                }*/
            }
        }, 500));
        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        //监听所有目录
        monitor.setMaxDepth(Integer.MAX_VALUE);
        //启动监听
        monitor.start();
    }

    /**
     * 上传监控目录
     * @param currentPath
     */
    public void uploadMonitor(Path currentPath,Object obj) {
        CommonConstants.addConsole("检测到："+currentPath.toString()+" 目录有新文件...");
        if (checkConfig()){
            Thread backup = new Thread(() -> {
                boolean login = getAliYunPanInfo();//登录阿里云
                if (!login){
                    return;
                }
                CommonConstants.FILE_ID = getFileId(CommonConstants.ROOT, CommonConstants.BACK_NAME);//备份目录ID
                uploadTwoLevelFolder(CommonConstants.FILE_ID,currentPath.toString());
                CommonConstants.addConsole("上传 "+obj.toString()+" 成功！");
            });
            backup.start();
        }
    }

    /**
     * 验证配置文件
     */
    public Boolean checkConfig(){
        if (StrUtil.isEmpty(setting.getStr("pathText"))){
            CommonConstants.addConsole("您没有选择需要备份的目录");
            return false;
        }
        if (StrUtil.isEmpty(setting.getStr("tokenText"))){
            CommonConstants.addConsole( "您没有输入阿里云token");
            return false;
        }
        if (StrUtil.isEmpty(setting.getStr("folderText"))){
            CommonConstants.addConsole("您没有输入需要备份到阿里云的目录");
            return false;
        }
        if (setting.getStr("tokenText").length()!=32){
            CommonConstants.addConsole("您输入的token不正确");
            return false;
        }
        if (!FileUtil.isDirectory(setting.getStr("pathText"))){
            CommonConstants.addConsole("请选择正确目录");
            return false;
        }
        CommonConstants.PATH = setting.getStr("pathText");
        CommonConstants.REFRESH_TOKEN = setting.getStr("tokenText");
        CommonConstants.BACK_NAME = setting.getStr("folderText");
        CommonConstants.BACK_TYPE = Integer.parseInt(setting.getStr("backType"));
        return true;
    }
    
    
}
