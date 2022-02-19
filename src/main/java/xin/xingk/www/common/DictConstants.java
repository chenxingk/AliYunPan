package xin.xingk.www.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Mr.chen
 * @date: 2022/2/19 12:11
 * @description: 字典翻译
 */
public class DictConstants {
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

    static {
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_ORDINARY,"普通备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_CLASSIFY,"分类备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_WECHAT,"微信备份");

        MONITOR_DICT.put(MONITOR_OPEN,"开启");
        MONITOR_DICT.put(MONITOR_CLOSE,"关闭");
    }
}
