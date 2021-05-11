package xin.xingk.www.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.CommonConstants;

import java.util.List;
import java.util.Map;

/**
 * Description: 操作阿里云盘工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class AliYunPanUtil {

    /**
     * 获取阿里云用户信息
     * @return
     * @throws Exception
     */
    public static void getAliYunPanInfo(){
        CommonConstants.TOKEN="";
        JSONObject data = new JSONObject();
        data.set("refresh_token",CommonConstants.REFRESH_TOKEN);
        JSONObject aliYunPanInfo = OkHttpUtil.doPost(CommonConstants.TOKEN_URL, data);
        CommonConstants.TOKEN = aliYunPanInfo.getStr("token_type") + " " + aliYunPanInfo.getStr("access_token");
        CommonConstants.DriveId = aliYunPanInfo.getStr("default_drive_id");
        CommonConstants.REFRESH_TOKEN = aliYunPanInfo.getStr("refresh_token");
    }

    /**
     * 获取阿里云文件列表
     * @param fileId
     * @return
     * @throws Exception
     */
    public static JSONObject getFileList(String fileId){
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
        return OkHttpUtil.doPost(CommonConstants.FILE_LIST_URL,data);
    }

    /**
     * 创建文件夹
     * @param fileId 父级目录ID 根目录为root
     * @param name 新文件夹名称
     * @return
     * @throws Exception
     */
    public static JSONObject createFolder(String fileId,String name){
        //创建文件夹
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("parent_file_id",fileId);
        data.set("name",name);
        data.set("type","folder");
        data.set("check_name_mode","refuse");
        return OkHttpUtil.doPost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 上传文件到阿里云盘
     * @return
     * @throws Exception
     */
    public static JSONObject uploadFile(String fileId, Map<String, Object> fileInfo){
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
        return OkHttpUtil.doFilePost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 完成文件上传
     * @param fileId
     * @param uploadId
     * @return
     * @throws Exception
     */
    public static JSONObject completeFile(String fileId,String uploadId){
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        data.set("ignoreError",true);
        data.set("part_info_list",array);
        data.set("upload_id",uploadId);
        return OkHttpUtil.doPost(CommonConstants.COMPLETE_FILE_URL,data);
    }

    /**
     * 删除阿里云盘文件
     * @param fileId
     * @return
     * @throws Exception
     */
    public static JSONObject deleteFile(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        return OkHttpUtil.doPost(CommonConstants.DELETE_FILE_URL,data);
    }

    /**
     * 获取文件夹ID
     * @param parentFileId 父级文件夹ID
     * @param folderName 文件夹名称
     * @return
     * @throws Exception
     */
    public static String getFileId(String parentFileId,String folderName){
        String fileId="";
        JSONObject fileList = getFileList(parentFileId);//获取文件目录
        JSONArray fileArray = fileList.getJSONArray("items");
        if (ObjectUtil.isNotNull(fileArray)){
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
    public static void doUploadFile(String fileId,Map<String, Object> fileInfo){
        JSONObject uploadFile = uploadFile(fileId,fileInfo);
        if(ObjectUtil.isNotNull(uploadFile.getJSONArray("part_info_list"))){//上传新文件
            byte[] fileBytes = FileUtil.readBytes(fileInfo.get("path").toString());
            String uploadUrl = uploadFile.getJSONArray("part_info_list").getJSONObject(0).getStr("upload_url");
            OkHttpUtil.uploadFileBytes(uploadUrl,fileBytes);
        }
        if (StrUtil.isEmpty(uploadFile.getStr("exist"))){//上传完成
            String upFileId = uploadFile.getStr("file_id");
            String uploadId = uploadFile.getStr("upload_id");
            completeFile(upFileId, uploadId);
        }
        System.out.println("上传完成："+fileInfo.toString());
    }

    /**
     * 扫描子目录
     * @param path 本地路径
     * @param pathId 备份目录 ID
     * @param isUploadFile 是否上传文件
     * @throws Exception
     */
    public static void scanFolders(String path,String pathId,Boolean isUploadFile){
        //获取文件夹下所有文件
        List<String> fileList = FileUtil.fileFolderList(path,FileUtil.FILE);
        if (isUploadFile){
            uploadFileList(fileList,pathId);
        }
        //获得目录下所有文件夹
        List<String> folderList = FileUtil.fileFolderList(path,FileUtil.FOLDER);
        //循环文件夹
        for (String folder : folderList){
            String fileId = getFileId(pathId, folder);//创建文件夹-文件夹ID
            String filePath = path + FileUtil.FILE_SEPARATOR + folder;//路径
            fileList = FileUtil.fileFolderList(path,FileUtil.FILE);//获取当前文件夹下所有文件
            uploadFileList(fileList,fileId);//上传当前文件夹内的文件
            System.out.println("--------------扫描新文件夹："+filePath);
            scanFolders(filePath,fileId,true);
        }
    }

    /**
     * 上传文件夹下文件到某个目录
     * @param fileList 文件list
     * @param pathId 阿里云文件夹ID
     * @throws Exception
     */
    public static void uploadFileList(List<String> fileList, String pathId){
        for (String filePath :  fileList) {
            Map<String, Object> map = FileUtil.getFileInfo(filePath);
            doUploadFile(pathId,map);
        }
    }
    
    
}
