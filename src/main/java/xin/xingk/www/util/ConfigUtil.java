
package xin.xingk.www.util;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.CommonConstants;

/**
 * 配置工具类
 */
public class ConfigUtil {
    //配置文件路径
    public static String CONFIG_PATH = CommonConstants.SYSTEM_PATH + "back_config.setting";
    //配置文件
    public static Setting setting = new Setting(FileUtil.touch(CONFIG_PATH).getPath(), true);

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
     * 获取refresh_token
     * @return refresh_token
     */
    public static String getRefreshToken() {
        return getStr(CommonConstants.REFRESH_TOKEN);
    }

    /**
     * 获取上传目录
     * @return 上传目录
     */
    public static String getPath() {
        return getStr(CommonConstants.PATH);
    }

    /**
     * 获取云盘备份目录
     * @return 云盘备份目录
     */
    public static String getBackName() {
        return getStr(CommonConstants.BACKUP_NAME);
    }

    /**
     * 获取备份模式
     * @return 备份模式
     */
    public static Integer getBackupType() {
        return getInt(CommonConstants.BACKUP_TYPE);
    }

    /**
     * 获取定时任务时间
     * @return 上传目录
     */
    public static String getBackupTime() {
        return getStr(CommonConstants.BACKUP_TIME);
    }

    /**
     * 获取监听目录
     * @return 上传目录
     */
    public static Boolean getMonitorFolder() {
        if (ObjectUtil.isEmpty(getInt(CommonConstants.MONITOR_FOLDER))){
            return false;
        }else {
            return getInt(CommonConstants.MONITOR_FOLDER)==1;
        }
    }


}
