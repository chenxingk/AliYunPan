package xin.xingk.www.util;

import cn.hutool.setting.Setting;
import xin.xingk.www.common.constant.CommonConstants;

/**
 * Author: 陈靖杰
 * Date: 2022/2/28 16:53
 * Description: 配置文件工具类
 */
public class ConfigUtil {
    //配置文件路径
    public static String CONFIG_PATH = CommonConstants.CONFIG_HOME + "backup_config.setting";
    //配置文件
    public static Setting setting = new Setting(FileUtil.touch(CONFIG_PATH).getPath(), true);

    //用户昵称
    private static final String name_key = "name";
    //token
    private static final String token_key = "token";
    //主题名称
    private static final String theme_key = "theme";
    //数据库版本
    private static final String dbVersion_key = "dbVersion";
    //开机启动
    private static final String startup_key = "startup";
    //启动检测
    private static final String startup_update_key = "startup_update";

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, String value) {
        setting.set(key, value);
        setting.store();
    }

    /**
     * 获取字符串型属性值<br>
     * 无值或获取错误返回null
     *
     * @param key 属性名
     * @return 属性值
     */
    public static String getStr(String key){
        return setting.getStr(key);
    }

    /**
     * 获取int型属性值<br>
     * 无值或获取错误返回null
     *
     * @param key 属性名
     * @return 属性值
     */
    public static Integer getInt(String key) {
        return setting.getInt(key);
    }

    /**
     * 获取主题名称
     */
    public static String getTheme(){
        if (getStr(theme_key) == null){
            set(theme_key,"浅色");
        }
        return getStr(theme_key);
    }

    /**
     * 获取用户的昵称
     */
    public static String getName(){
        return getStr(name_key);
    }

    /**
     * 获取用户的Token
     */
    public static String getToken(){
        return getStr(token_key);
    }

    /**
     * 获取当前DB版本号
     * @return
     */
    public static String getDbVersion(){
        if (getStr(dbVersion_key) == null){
            set(dbVersion_key,"V2.0.20220223");
        }
        return getStr(dbVersion_key);
    }

    /**
     * 获取当前开机启动设置
     * 0启动 1不启动
     * @return 是否开机启动
     */
    public static boolean getStartup(){
        Integer startup = getInt(startup_key);
        if (startup == null) return false;
        return startup == 0;
    }

    /**
     * 获取用户启动时检查更新
     * 0更新 1不更新
     * @return 是否启动时检查更新
     */
    public static boolean getStartupUpdate(){
        Integer update = getInt(startup_update_key);
        if (update == null) return false;
        return update == 0;
    }

    /**
     * 更新用户主题
     * @param theme 主题名称
     */
    public static void setTheme(String theme) {
        set(theme_key,theme);
    }

    /**
     * 更新用户昵称
     * @param name 昵称
     */
    public static void setName(String name){
        set(name_key,name);
    }

    /**
     * 并发点
     * 更新用户 token
     * @param token token
     */
    public static void setToken(String token){
        set(token_key,token);
    }

    /**
     * 退出登录
     */
    public static void logout(){
        set(token_key,null);
    }

    /**
     * 并发点
     * 更新用户 dbVersion
     * @param dbVersion dbVersion
     */
    public static void setVersion(String dbVersion){
        set(dbVersion_key,dbVersion);
    }

    /**
     * 更新用户开机启动
     * @param startup 0启动 1不启动
     */
    public static void setStartup(Integer startup){
        set(startup_key,startup+"");
    }

    /**
     * 更新用户启动时检查更新
     * @param update 0更新 1不更新
     */
    public static void setStartupUpdate(Integer update){
        set(startup_update_key,update+"");
    }

}
