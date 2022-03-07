package xin.xingk.www.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.aliyun.CloudFile;
import xin.xingk.www.entity.aliyun.FileInfo;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        CommonConstants.ACCESS_TOKEN = result.getStr("access_token");
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
     * 搜索文件
     * @param fileId 云盘目录ID
     * @param name 文件名
     * @param type 类型 文件或文件夹
     * @return 是否返回
     */
    public static List<CloudFile> search(String fileId, String name, String type) {
        JSONObject data = new JSONObject();
        data.set("drive_id", CommonConstants.DriveId);
        data.set("limit", 100);
        data.set("order_by", "name ASC");
        //模糊查询
        //String format = StrUtil.format("parent_file_id = '{}' and name match '{}' and type = '{}'", fileId, name, type);
        String format = StrUtil.format("parent_file_id = '{}' and name = '{}' and type = '{}'", fileId, name, type);
        data.set("query",format);
        JSONObject result = OkHttpUtil.doPost(CommonConstants.FILE_SEARCH_URL,data);
        List<CloudFile> cloudFileList = JSONUtil.toList(result.getJSONArray("items"), CloudFile.class);
        return cloudFileList;
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
     * 检测文件是否存在
     * @param fileId
     * @param fileInfo
     * @return
     */
    public static JSONObject fileExists(String fileId, FileInfo fileInfo) {
        JSONObject data = new JSONObject();
        data.set("drive_id", CommonConstants.DriveId);
        data.set("part_info_list", getPartNumber(fileInfo.getMax()));
        data.set("parent_file_id", fileId);
        data.set("name", fileInfo.getName());
        data.set("type", "file");
        data.set("check_name_mode", "refuse");
        data.set("size",fileInfo.getSize());
        data.set("proof_code", fileInfo.getProofCode());
        data.set("proof_version", "v1");
//        data.set("pre_hash", fileInfo.getContentHash());
        return OkHttpUtil.doFilePost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 上传文件到阿里云盘
     * @return 上传结果
     */
    public static JSONObject uploadFile(String fileId,FileInfo fileInfo){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("name", fileInfo.getName());
        data.set("parent_file_id", fileId);
        data.set("type","file");
        data.set("part_info_list",getPartNumber(fileInfo.getMax()));
        data.set("check_name_mode", "refuse");
        data.set("size", fileInfo.getSize());
        data.set("content_hash", fileInfo.getContentHash());
        data.set("content_hash_name", "sha1");
        data.set("proof_code", fileInfo.getProofCode());
        data.set("proof_version", "v1");
        return OkHttpUtil.doFilePost(CommonConstants.CREATE_FILE_URL,data);
    }

    /**
     * 获取 PartNumber
     * @param max 次数
     * @return
     */
    private static JSONArray getPartNumber(long max) {
        JSONArray array = new JSONArray();
        for (int i = 1; i <= max; i++) {
            JSONObject list = new JSONObject();
            list.set("part_number",i);
            array.add(list);
        }
        return array;
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
     * 获取下载地址
     * @param fileId 云盘文件ID
     * @return 云盘目录ID
     */
    public static JSONObject getDownloadUrl(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
//        HttpResponse response = HttpRequest.post("https://api.aliyundrive.com/v2/file/get_download_url")
//                .auth(CommonConstants.TOKEN)
//                .body(data.toString())
//                .execute();
//        String body = response.body();
//        return JSONUtil.parseObj(body);
        return OkHttpUtil.doPost(CommonConstants.DOWNLOAD_FILE_URL, data);
    }

    /**
     * 获取下载地址
     * @param fileId 云盘文件ID
     * @param path 文件保存目录
     */
    public static void downloadCloudFile(String fileId,String path){
        JSONObject downloadUrl = getDownloadUrl(fileId);
        BigDecimal size = downloadUrl.getBigDecimal("size");
        HttpResponse response = HttpUtil.createGet(downloadUrl.getStr("url"), true)
//                .header("Connection", "keep-alive")
//                .header("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"")
//                .header("sec-ch-ua-mobile", "?0")
//                .header("sec-ch-ua-platform", "\"Windows\"")
//                .header("Upgrade-Insecure-Requests", "1")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .header("Sec-Fetch-Site", "cross-site")
//                .header("Sec-Fetch-Mode", "navigate")
//                .header("Sec-Fetch-Dest", "iframe")
                .header("Referer", "https://www.aliyundrive.com/")
//                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .executeAsync();
        if (response.isOk()){
            String fileName = StrUtil.subAfter(response.header("Content-Disposition"), "''", false);
            fileName = URLUtil.decode(fileName);
            File newFile = FileUtil.file(path + FileUtil.FILE_SEPARATOR + fileName);
            response.writeBody(newFile,new StreamProgress() {
                @Override
                public void start() {
                    System.out.println("开始下载");
                }

                @Override
                public void progress(long progressSize) {
                    BigDecimal bigDecimal = new BigDecimal(progressSize);
                    BigDecimal div = NumberUtil.div(bigDecimal, size);
                    BigDecimal mul = NumberUtil.mul(div, 100).setScale(2, RoundingMode.DOWN);
                    System.out.println("当前已下载："+ mul + "％" + "---" + FileUtil.readableFileSize(progressSize));
                }

                @Override
                public void finish() {
                    //写入下载完成表
                    //没有下载完成的下次继续下载
                    System.out.println("下载完成");
                }
            });
        }else {
            System.out.println("下载失败："+response.getStatus());
        }
    }

    /**
     * 获取目录层级信息
     * @param fileId 云盘文件ID
     * @return 目录层级信息
     */
    public static JSONObject getFolderPath(String fileId){
        JSONObject data = new JSONObject();
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        return OkHttpUtil.doPost(CommonConstants.FOLDER_PATH_URL, data);
    }

    /**
     * 修改文件目录名称
     * @param fileId 目录ID
     * @param name 目录新名称
     * @return 修改结果
     */
    public static JSONObject updateFolder(String fileId,String name){
        JSONObject data = new JSONObject();
        data.set("check_name_mode","refuse");
        data.set("drive_id",CommonConstants.DriveId);
        data.set("file_id",fileId);
        data.set("name",name);
        return OkHttpUtil.doPost(CommonConstants.UPDATE_FOLDER_URL, data);
    }

    /**
     * 获取文件夹ID
     * @param parentFileId 父级文件夹ID
     * @param folderName 文件夹名称
     * @return 云盘目录ID
     */
    public static String getFileId(String parentFileId,String folderName){
        List<CloudFile> cloudFileList = search(parentFileId, folderName, DictConstants.FILE_TYPE_FOLDER);//获取文件目录
        if (ObjectUtil.isNotEmpty(cloudFileList)) return cloudFileList.get(0).getFileId();
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
