package xin.xingk.www.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传文件记录工具类
 */
public class UploadLogUtil {

    //上传文件日志
    public static String UPLOAD_LOG = CommonConstants.SYSTEM_PATH + "uploadLog.json";
    //上传文件日志
    public static String RUN_LOG = CommonConstants.SYSTEM_PATH + "run_log.log";
    //写入日志
    public static FileWriter uploadLog = FileWriter.create(FileUtil.touch(UPLOAD_LOG), CharsetUtil.CHARSET_UTF_8);
    //写入日志
    public static FileWriter runLog = FileWriter.create(FileUtil.touch(RUN_LOG), CharsetUtil.CHARSET_UTF_8);
    //读日志
    public static FileReader readerLog = new FileReader(UPLOAD_LOG);
    //日志对象
    public static JSONObject logObject = new JSONObject();
    //文件信息
    private static JSONObject fileInfo = new JSONObject();
    //文件列表
    private static JSONArray fileList = new JSONArray();



    /**
     * 加载文件上传日志
     */
    private static void load() {
        if (StrUtil.isNotEmpty(readerLog.readString())){
            logObject = new JSONObject(readerLog.readString());
            fileInfo = logObject.getJSONObject("fileInfo");
            fileList = logObject.getJSONArray("fileList");
        }
    }

    /**
     * 添加文件上传日志
     * @param path 文件路径
     * @param fileId 文件ID
     */
    public static void addFileUploadLog(String path,String fileId){
        load();
        fileList.add(path);
        fileInfo.set(path,fileId);
        logObject.set("fileInfo",fileInfo);
        logObject.set("fileList",fileList);
        uploadLog.write(logObject.toString());
    }

    /**
     * 删除上传文件日志
     * @param path 文件路径
     */
    public static void removeFileUploadLog(String path){
        load();
        fileInfo.remove(path);
        fileList.remove(path);
        uploadLog.write(logObject.toString());
    }

    /**
     * 获取文件上传的ID
     * @param path 文件路径
     * @return 文件ID
     */
    public static String getFileUploadFileId(String path){
        if (StrUtil.isEmpty(readerLog.readString())) return "";
        JSONObject logObject = new JSONObject(readerLog.readString());
        return logObject.getJSONObject("fileInfo").getStr(path);
    }

    /**
     * 获取上传文件List
     * @return 上传文件List
     */
    public static List<String> getFileUploadList(){
        if (StrUtil.isEmpty(readerLog.readString())) return new ArrayList<>();
        JSONObject logObject = new JSONObject(readerLog.readString());
        return logObject.getJSONArray("fileList").toList(String.class);
    }

}
