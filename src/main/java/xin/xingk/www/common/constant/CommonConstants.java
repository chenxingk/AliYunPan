package xin.xingk.www.common.constant;


import java.io.File;

/**
 * Description: 公共变量
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class CommonConstants {

    //软件版本
    public static final String VERSION = "V2.1.20220228";
    //软件标题
    public static final String TITLE = "备份助手"+VERSION;
    //用户目录
    private static final String USER_HOME = System.getProperty("user.home");
    //配置文件路径
    public static final String CONFIG_HOME = USER_HOME + File.separator + ".backupAider" + File.separator;
    //日志文件路径
    public final static String LOG_DIR = USER_HOME + File.separator + ".backupAider" + File.separator + "logs" + File.separator;
    //当前路径
    public static String SYSTEM_PATH = System.getProperty("user.dir") + File.separator;

    /**
     * 阿里云接口
     */
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
    //登录状态
    public static boolean LOGIN_STATUS = false;

    //默认大小
    public static final Integer DEFAULT_SIZE = 10480000;





}
