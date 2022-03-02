package xin.xingk.www.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.aliyun.CloudFile;
import xin.xingk.www.entity.aliyun.FileInfo;

import java.util.*;

/**
 * @author: Mr.chen
 * @date: 2022/2/20 9:46
 * @description: 阿里云盘交互工具类
 */
public class AliYunUtil {

    /**
     * 获取阿里云信息
     * @return 登录状态
     */
    public static boolean login(){
        String token = ConfigUtil.getToken();
        if (StrUtil.isEmpty(token)){
            return false;
        }
        JSONObject data = new JSONObject().set("refresh_token", token);
        JSONObject result = OkHttpUtil.doPost(CommonConstants.TOKEN_URL, data);
        if (ObjectUtil.isNull(result) || "InvalidParameter.RefreshToken".equals(result.getStr("code"))){
            return false;
        }
        CommonConstants.TOKEN = result.getStr("token_type") + " " + result.getStr("access_token");
        CommonConstants.DriveId = result.getStr("default_drive_id");
        ConfigUtil.setToken(result.getStr("refresh_token"));
        return true;
    }

    /**
     * 获取阿里云文件列表
     * @param fileId 云盘目录ID
     * @return 目录下所有内容
     */
    public static List<CloudFile> getCloudFileList(String fileId){
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

        //下一页 标记
        String nextMarker = "";
        JSONArray resultArr = new JSONArray();
        do {
            if (StrUtil.isNotEmpty(nextMarker)){
                data.set("marker",nextMarker);
            }
            JSONObject result = OkHttpUtil.doPost(CommonConstants.FILE_LIST_URL, data);
            resultArr.addAll(result.getJSONArray("items"));
            nextMarker = result.getStr("next_marker");
        }while (StrUtil.isNotEmpty(nextMarker));
        return JSONUtil.toList(resultArr, CloudFile.class);
    }

    /**
     * 创建文件夹
     * @param fileId 父级目录ID 根目录为root
     * @param name 新文件夹名称
     * @return 文件夹信息
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
     * @return 上传结果
     */
    public static JSONObject uploadFile(String fileId,FileInfo fileInfo){
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        long max = fileInfo.getMax();
        for (int i = 1; i <= max; i++) {
            JSONObject list = new JSONObject();
            list.set("part_number",i);
            array.add(list);
        }
        data.set("drive_id",CommonConstants.DriveId);
        data.set("name",fileInfo.getName());
        data.set("type","file");
        data.set("content_type",fileInfo.getContentType());
        data.set("size",fileInfo.getSize());
        data.set("parent_file_id",fileId);
        data.set("part_info_list",array);
        data.set("content_hash_name","sha1");
        data.set("content_hash",fileInfo.getContentHash());
        data.set("ignoreError",false);
        data.set("check_name_mode","refuse");
        return OkHttpUtil.doFilePost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 完成文件上传
     * @param fileId 云盘文件ID
     * @param uploadId 上传ID
     * @return 上传完成信息
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
     * @param fileId 云盘文件ID
     */
    public static void deleteFile(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        OkHttpUtil.deleteFile(data);
    }

    /**
     * 获取文件夹ID
     * @param parentFileId 父级文件夹ID
     * @param folderName 文件夹名称
     * @return 云盘目录ID
     */
    public static String getFileId(String parentFileId,String folderName){
        List<CloudFile> cloudFileList = getCloudFileList(parentFileId);//获取文件目录
        //过滤文件夹
        Optional<CloudFile> cloudFile = cloudFileList.stream().filter(f ->
                "folder".equals(f.getType()) && folderName.equals(f.getName())
        ).findFirst();
        if (cloudFile.isPresent()) return cloudFile.get().getFileId();
        //没有找到则创建一个返回
        JSONObject folder = createFolder(parentFileId, folderName); //创建备份目录
        return folder.getStr("file_id");
    }

    /**
     * 根据文件路径获取文件夹ID
     * @param path 路径
     * @return 文件夹ID
     */
    public static Map<String, Object> getFileIdByPath(String path,Backup backup){
        Map<String, Object> result = new HashMap<>();
        String fileId = getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));//备份目录ID
        Integer backupType = backup.getBackupType();
        if (backupType==0){//普通备份
            String folderDir = StrUtil.subAfter(path, backup.getLocalPath() + FileUtil.FILE_SEPARATOR, false);
            if(StrUtil.isNotEmpty(folderDir)){
                String[] folderArr = folderDir.split("\\\\");
                fileId = getFileIdByArr(fileId, folderArr);
            }
        }else if (backupType==2){//微信备份
            //获取月份文件夹
            String month = getMonth(path,backup.getLocalPath());
            if (StrUtil.isNotEmpty(month)){
                //获取月份文件夹ID
                fileId = AliYunUtil.getFileId(fileId,month);
            }
            if (twoFolder(path,backup.getLocalPath())){//微信下的二级目录
                fileId = AliYunUtil.getFileId(fileId,"文件夹");
                String[] folderArr = StrUtil.subAfter(path, backup.getLocalPath() + FileUtil.FILE_SEPARATOR + month + FileUtil.FILE_SEPARATOR, false).split("\\\\");
                fileId = getFileIdByArr(fileId, folderArr);
                backupType = 0;
            }else{
                backupType = 2;
            }
        }
        result.put("backupType",backupType);
        result.put("fileId",fileId);
        return result;
    }

    /**
     * 根据文件夹数组获取上传文件夹ID
     * @param fileId 上级文件夹ID
     * @param folderArr 文件夹数组
     * @return 文件夹ID
     */
    public static String getFileIdByArr(String fileId, String[] folderArr) {
        ArrayList<String> folderList = ListUtil.toList(folderArr);
        for (String folder : folderList) {
            fileId = AliYunUtil.getFileId(fileId, folder);
        }
        return fileId;
    }

    /**
     * 获取月份文件夹
     * @param path 本地路径
     * @return 月份目录
     */
    public static String getMonth(String path,String localPath) {
        if (twoFolder(path,localPath)){
            return StrUtil.subBetween(path, localPath + FileUtil.FILE_SEPARATOR,FileUtil.FILE_SEPARATOR);
        }else{
            return StrUtil.subSuf(path, localPath.length()+1);
        }
    }

    /**
     * 是否为二级目录
     * @param path 本地路径
     * @return true 二级目录
     */
    public static boolean twoFolder(String path,String localPath) {
        String str = StrUtil.subAfter(path, localPath, false);
        return StrUtil.count(str, FileUtil.FILE_SEPARATOR)>1;
    }
}
