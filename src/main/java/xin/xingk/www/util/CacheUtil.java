package xin.xingk.www.util;

import cn.hutool.cache.Cache;
import cn.hutool.core.convert.Convert;

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

    //备份任务状态 KEY
    public static String BACKUP_STATUS_KEY = "BACKUP_STATUS_KEY_";

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
     * 获取缓存的值
     * @param key key
     * @return 获取的值
     */
    public static Integer getInt(String key){
        return Convert.toInt(cache.get(key));
    }

    /**
     * 删除缓存
     * @param key key
     */
    public static void remove(String key){
        cache.remove(key);
    }


    /**
     * 设置备份任务状态
     * @param id 备份任务ID
     * @param status 备份任务状态
     */
    public static void setBackupStatus(Integer id,Integer status) {
        set(BACKUP_STATUS_KEY + id,status);
    }

    /**
     * 获取备份任务状态
     * @param id 备份任务ID
     * @return 状态
     */
    public static Integer getBackupStatus(Integer id){
        return getInt(BACKUP_STATUS_KEY+id);
    }

    /**
     * 清除备份任务状态
     * @param id 备份任务ID
     */
    public static void removeBackupStatus(Integer id) {
        remove(BACKUP_STATUS_KEY + id);
    }

}
