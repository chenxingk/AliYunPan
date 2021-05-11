package xin.xingk.www.common;


import javax.swing.*;

/**
 * Description: 公共变量
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class CommonConstants {

    //日志面板
    public static MyConsole console = new MyConsole();

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
    //public final static String PATH="E:\\用户目录\\文档\\WeChat Files\\wxid_3wc96wg6zgf022\\FileStorage\\File";
    public static String PATH="";
    //备份目录名称
    public static String BACK_NAME="";
    //备份目录名称
    public static int BACK_TYPE=0;//0是普通备份 1是分类备份



}
