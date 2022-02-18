package xin.xingk.www.common;


import cn.hutool.core.io.watch.WatchMonitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 公共变量
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class CommonConstants {


    //软件版本
    public static final String VERSION = "v_2.0.0_220218";
    public static final String TITLE = "备份助手v"+VERSION;
    private static final String USER_HOME = System.getProperty("user.home");
    public static final String CONFIG_HOME = USER_HOME + File.separator + ".backupAider" + File.separator;
    /**
     * 日志文件路径
     */
    public final static String LOG_DIR = USER_HOME + File.separator + ".backupAider" + File.separator + "logs" + File.separator;


    /**
     * 备份模式
     */
    public static Map<Integer, String> BACKUP_TYPE_DICT = new HashMap<>();
    //普通备份
    public static Integer BACKUP_TYPE_ORDINARY = 0;
    //分类备份
    public static Integer BACKUP_TYPE_CLASSIFY = 1;
    //微信备份
    public static Integer BACKUP_TYPE_WECHAT = 2;

    /**
     * 目录检测
     */
    public static Map<Integer, String> MONITOR_DICT = new HashMap<>();
    //开启目录检测
    public static Integer MONITOR_OPEN = 0;
    //关闭目录检测
    public static Integer MONITOR_CLOSE = 1;

    //当前路径
    public static String SYSTEM_PATH = System.getProperty("user.dir") + File.separator;

    //获取token
    public final static String TOKEN_URL="https://websv.aliyundrive.com/token/refresh";
    //文件列表
    public final static String FILE_LIST_URL="https://api.aliyundrive.com/adrive/v3/file/list";
    //文件上传
    public final static String CREATE_FILE_URL="https://api.aliyundrive.com/v2/file/create";
    //完成上传
    public final static String COMPLETE_FILE_URL="https://api.aliyundrive.com/v2/file/complete";
    //删除文件
    public final static String DELETE_FILE_URL="https://api.aliyundrive.com/v2/recyclebin/trash";
    //Token
    public static String TOKEN="";
    //driveId
    public static String DriveId="";
    //根目录
    public final static String ROOT="root";

    //refresh_token
    public static String REFRESH_TOKEN = "tokenText";
    //上传目录
    public static String PATH = "pathText";
    //云盘备份目录
    public static String BACKUP_NAME = "folderText";
    //备份模式
    public static String BACKUP_TYPE = "backType";//0是普通备份 1是分类备份
    //定时任务时间
    public static String BACKUP_TIME = "backupTime";
    //是否监听目录
    public static String MONITOR_FOLDER= "monitorFolder";//0是关闭 1是开启

    //输出日志模式 0是覆盖 1追加
    public static int CLEAN_CONSOLE=1;
    //备份状态
    public static boolean BACK_STATE = false;
    //是否打印日志
    public static boolean IS_CONSOLE = false;
    //文件监听
    public static WatchMonitor monitor;
    //默认大小
    public static final Integer DEFAULT_SIZE = 10480000;


    static {
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_ORDINARY,"普通备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_CLASSIFY,"分类备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_WECHAT,"微信备份");

        MONITOR_DICT.put(MONITOR_OPEN,"开启");
        MONITOR_DICT.put(MONITOR_CLOSE,"关闭");
    }


}
