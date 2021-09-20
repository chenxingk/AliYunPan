package xin.xingk.www.common;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.utils.FileUtil;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 公共变量
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class CommonConstants {

    //日志面板
    public static MyConsole console = new MyConsole();
    public static JScrollPane consolePane = new JScrollPane(console);
    public static JScrollBar scrollBar = consolePane.getVerticalScrollBar();
    // Token文本框
    public static JTextField tokenText = new JTextField();
    //开始备份按钮
    public static JButton startBackup = new JButton("开始备份");
    //当前路径
    public static String SYSTEM_PATH = System.getProperty("user.dir") + File.separator;
    //配置文件路径
    public static String CONFIG_PATH = SYSTEM_PATH + "back_config.setting";
    //配置文件
    public static Setting setting = new Setting(FileUtil.touch(CONFIG_PATH).getPath(), true);
    //上传文件日志
    public static String UPLOAD_LOG = SYSTEM_PATH + "uploadLog.json";
    //写入日志
    public static FileWriter writerLog = FileWriter.create(FileUtil.touch(UPLOAD_LOG), CharsetUtil.CHARSET_UTF_8);
    //读日志
    public static FileReader readerLog = new FileReader(UPLOAD_LOG);
    //获取token
    public final static String TOKEN_URL="https://websv.aliyundrive.com/token/refresh";
    //文件列表
    public final static String FILE_LIST_URL="https://api.aliyundrive.com/v2/file/list";
    //文件上传
    public final static String CREATE_FILE_URL="https://api.aliyundrive.com/v2/file/create";
    //完成上传
    public final static String COMPLETE_FILE_URL="https://api.aliyundrive.com/v2/file/complete";
    //删除文件
    public final static String DELETE_FILE_URL="https://api.aliyundrive.com/v2/recyclebin/trash";
    //refresh_token
    public static String REFRESH_TOKEN = setting.getStr("tokenText");
    //Token
    public static String TOKEN="";
    //driveId
    public static String DriveId="";
    //根目录
    public final static String ROOT="root";
    //上传目录
    public static String PATH = setting.getStr("pathText");
    //备份目录名称
    public static String BACK_NAME = setting.getStr("folderText");
    //备份目录名称
    public static int BACK_TYPE= Integer.parseInt(ObjectUtil.isNull(setting.getStr("backType")) ? "0" : setting.getStr("backType"));//0是普通备份 1是分类备份
    //输出日志模式 0是覆盖 1追加
    public static int CLEAN_CONSOLE=1;
    //备份状态
    public static boolean BACK_STATE = false;
    //是否打印日志
    public static boolean IS_CONSOLE = false;
    //文件监听
    public static WatchMonitor monitor;
    //上传日志
    public static JSONObject json = new JSONObject();

    /**
     * 添加控制台日志
     * @param text
     */
    public static void addConsole(String text){
        if (!IS_CONSOLE){//不打印日志
            return;
        }
        if (CLEAN_CONSOLE==0){
            console.setText("开始运行"+"\n");
        }else {
            console.append("["+DateUtil.now()+"] "+text+"\n");
            console.paintImmediately(console.getBounds());
            scrollBar.setValue(scrollBar.getMaximum());
        }
    }


    /**
     * 修改开始按钮状态
     * @param text 按钮名称 开始备份 正在备份
     * @param enabled 按钮状态
     */
    public static void modifyStartBtnStatus(String text,Boolean enabled) {
        startBackup.setText(text);
        startBackup.setEnabled(enabled);
    }

    /**
     * 添加文件上传日志
     * @param path
     * @param fileId
     */
    public static void addFileUploadLog(String path,String fileId){
        JSONObject fileInfo = new JSONObject();
        JSONArray fileList = new JSONArray();
        if (StrUtil.isNotEmpty(readerLog.readString())){
            json = new JSONObject(readerLog.readString());
            fileInfo = json.getJSONObject("fileInfo");
            fileList = json.getJSONArray("fileList");
        }
        fileList.add(path);
        fileInfo.set(path,fileId);
        json.set("fileInfo",fileInfo);
        json.set("fileList",fileList);
        writerLog.write(json.toString());
    }

    /**
     * 删除上传文件日志
     * @param path
     */
    public static void removeFileUploadLog(String path){
        JSONObject fileInfo = new JSONObject();
        JSONArray fileList = new JSONArray();
        if (StrUtil.isNotEmpty(readerLog.readString())){
            json = new JSONObject(readerLog.readString());
            fileInfo = json.getJSONObject("fileInfo");
            fileList = json.getJSONArray("fileList");
        }
        fileInfo.remove(path);
        fileList.remove(path);
        writerLog.write(json.toString());
    }

    /**
     * 获取文件上传日志
     * @param path
     * @return
     */
    public static String getFileUploadFileId(String path){
        if (StrUtil.isEmpty(readerLog.readString())){
            return "";
        }
        JSONObject json = new JSONObject(readerLog.readString());
        return json.getJSONObject("fileInfo").getStr(path);
    }

    /**
     * 获取文件上传list
     * @return
     */
    public static List<String> getFileUploadList(){
        if (StrUtil.isEmpty(readerLog.readString())){
            return new ArrayList<>();
        }
        JSONObject json = new JSONObject(readerLog.readString());
        return json.getJSONArray("fileList").toList(String.class);
    }


}
