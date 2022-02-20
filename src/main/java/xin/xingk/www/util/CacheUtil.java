package xin.xingk.www.util;

import cn.hutool.cache.Cache;

/**
 * @author: Mr.chen
 * @date: 2022/2/20 18:19
 * @description: 缓存工具类
 */
public class CacheUtil extends cn.hutool.cache.CacheUtil{

    //缓存对象
    public static Cache<String, Object> cache = CacheUtil.newLFUCache(999);

    //备份ID KEY
    public static String BACKUP_ID_KEY = "BACKUP_ID";

    //目录检测 KEY
    public static String WATCHER_KEY = "WATCHER_KEY";

    /**
     * 设置缓存内容
     * @param key 缓存key
     * @param val 缓存值
     */
    public static void set(String key,Object val){
        cache.put(key,val,0);
    }

    /**
     * 设置缓存内容
     * @param key 缓存key
     */
    public static Object get(String key){
         return cache.get(key);
    }

}
