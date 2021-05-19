package xin.xingk.www.common;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.utils.FileUtil;

import javax.swing.*;
import java.io.File;

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
    //设置工具
    public static String SYSTEM_PATH = System.getProperty("user.dir") + File.separator;
    //配置文件路径
    public static String CONFIG_PATH = SYSTEM_PATH + "back_config.setting";
    //配置文件
    public static Setting setting = new Setting(FileUtil.touch(CONFIG_PATH).getPath(), true);
    //上传文件日志
    public static String UPLOAD_LOG = SYSTEM_PATH + "uploadLog.txt";
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
    public static String REFRESH_TOKEN="";
    //Token
    public static String TOKEN="";
    //driveId
    public static String DriveId="";
    //根目录
    public final static String ROOT="root";
    //上传目录
    //public static String PATH="E:\\用户目录\\文档\\WeChat Files\\wxid_3wc96wg6zgf022\\FileStorage\\File";
    public static String PATH="";
    //备份目录名称
    public static String BACK_NAME="";
    //备份目录名称
    public static int BACK_TYPE=0;//0是普通备份 1是分类备份
    public static int CLEAN_CONSOLE=0;//0是情况 1不清空

    /**
     * 添加控制台日志
     * @param text
     */
    public static void addConsole(String text){
        if (CLEAN_CONSOLE==0){
            console.setText("开始运行"+"\n");
        }else {
            console.append("["+DateUtil.now()+"] "+text+"\n");
            console.paintImmediately(console.getBounds());
            scrollBar.setValue(scrollBar.getMaximum());
        }
    }

}
