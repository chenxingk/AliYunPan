package xin.xingk.www.common.constant;

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
    public static Integer MONITOR_ENABLE = 0;
    //关闭目录检测
    public static Integer MONITOR_DISABLE = 1;

    /**
     * 备份任务状态
     */
    public static Map<Integer, String> STATUS_DICT = new HashMap<>();
    //备份任务正常
    public static Integer STATUS_ENABLE = 0;
    //备份任务运行中
    public static Integer STATUS_RUN = 1;
    //备份任务禁用
    public static Integer STATUS_DISABLE = 2;

    static {
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_ORDINARY,"普通备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_CLASSIFY,"分类备份");
        BACKUP_TYPE_DICT.put(BACKUP_TYPE_WECHAT,"微信备份");

        MONITOR_DICT.put(MONITOR_ENABLE,"开启");
        MONITOR_DICT.put(MONITOR_DISABLE,"关闭");

        STATUS_DICT.put(STATUS_ENABLE,"正常");
        STATUS_DICT.put(STATUS_RUN,"备份中");
        STATUS_DICT.put(STATUS_DISABLE,"已禁用");
    }
}
