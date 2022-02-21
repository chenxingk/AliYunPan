package xin.xingk.www.util;

import cn.hutool.cache.Cache;

/**
 * @author: Mr.chen
 * @date: 2022/2/20 18:19
 * @description: 缓存工具类
 */
public class CacheUtil extends cn.hutool.cache.CacheUtil{

    /**
     * 缓存对象过期时间（24小时） 单位ms
     */
    public static final long excelDataTime = 86400000;

    /**
     * 缓存对象 默认大小为10（只能存放10个KEY）
     */
    public static final Cache<String, Object> cache = CacheUtil.newFIFOCache(9999);

    //备份ID KEY
    public static String BACKUP_ID_KEY = "BACKUP_ID_KEY_";

    //定时备份ID KEY
    public static String CRON_TASK_ID_KEY = "CRON_TASK_ID_KEY_";

    //目录检测 KEY
    public static String WATCHER_KEY = "WATCHER_KEY_";

    /**
     * 设置缓存 过期时间默认不过期
     * @param key key
     * @param value value
     */
    public static void set(String key,Object value){
        cache.put(key,value,0);
    }

    /**
     * 获取缓存的值
     * @param key key
     * @return 获取的值
     */
    public static Object get(String key){
        return cache.get(key);
    }

    /**
     * 删除缓存
     * @param key key
     */
    public static void remove(String key){
        cache.remove(key);
    }

}
