package xin.xingk.www.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import xin.xingk.www.util.AliYunPanUtil;
import xin.xingk.www.util.ConfigUtil;

/**
 * Description: 定时任务
 * Author: 陈靖杰
 * Date: 2021/05/12
 */
public class CronTasks {

    private static AliYunPanUtil aliYunPanUtil = new AliYunPanUtil();;

    /**
     * 更新阿里云盘token
     */
    public static void updateALiYunPanToken(){
        if (StrUtil.isNotEmpty(ConfigUtil.getRefreshToken())){
            CommonUI.console("定时更新阿里云Token");
            aliYunPanUtil.getAliYunPanInfo();
        }
    }

    /**
     * 定时备份文件
     */
    public static void backFileList(){
        try {
            //执行上传文件操作
            Thread backup = new Thread(() -> aliYunPanUtil.startBackup());
            backup.start();
        } catch (Exception e) {
            CommonUI.console("定时备份遇到异常情况：{}",e.toString());
        }
    }

    /**
     * 定时任务开始
     */
    public static void startTask() {
        //每小时刷新阿里云盘登录信息
        CronUtil.schedule("001","0 0 0/1 * * ?", () -> updateALiYunPanToken());
        String backupTime = ConfigUtil.getBackupTime();
        boolean contains = ReUtil.contains("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", backupTime);
        if (StrUtil.isNotEmpty(backupTime) && contains){
            startTimeBackupFile(backupTime);
        }else {
            CommonUI.console("定时备份任务暂未开启，请检查您的配置");
        }
        //开启定时
        CronUtil.start();
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
    }

    /**
     * 开启定时备份文件
     * @param backupTime
     */
    public static void startTimeBackupFile(String backupTime) {
        CronUtil.remove("002");
        String cronTab = DateUtil.format(DateUtil.parse(backupTime), "ss mm HH * * ?");
        CronUtil.schedule("002",cronTab, CronTasks::backFileList);
        CommonUI.console("定时备份任务开启成功，每天【{}】执行",backupTime);
    }

}
