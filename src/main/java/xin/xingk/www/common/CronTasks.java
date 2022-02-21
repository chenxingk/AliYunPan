package xin.xingk.www.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.context.UserContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.ui.Home;
import xin.xingk.www.util.AliYunUtil;
import xin.xingk.www.util.BackupUtil;
import xin.xingk.www.util.CacheUtil;
import xin.xingk.www.util.UIUtil;

import java.util.List;

/**
 * Description: 定时任务
 * Author: 陈靖杰
 * Date: 2021/05/12
 */
public class CronTasks {

    /**
     * 开始定时任务
     */
    public static void startTask() {
        //每小时刷新阿里云盘登录信息
        CronUtil.schedule("001","0 0 0/1 * * ?", CronTasks::updateALiYunPanToken);
        List<Backup> backupList = BackupContextHolder.getBackupList();
        for (Backup backup : backupList) {
            setTimeTask(backup);
        }
        UIUtil.console("定时备份开启成功");
        //开启定时
        CronUtil.start();
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
    }

    /**
     * 设置备份任务
     * @param backup 备份任务
     */
    public static void setTimeTask(Backup backup) {
        if (StrUtil.isEmpty(backup.getBackupTime())) return;
        String id = String.valueOf(backup.getId());
        CronUtil.remove(id);
        String cronTab = DateUtil.format(DateUtil.parse(backup.getBackupTime()), "ss mm HH * * ?");
        CronUtil.schedule(id,cronTab, () -> backFileList(backup.getId()));
    }

    /**
     * 定时备份文件
     */
    public static void backFileList(int id){
        try {
            String key = CacheUtil.BACKUP_ID_KEY + id;
            if (!Home.getInstance().getStartButton().getModel().isEnabled() || ObjectUtil.isNotEmpty(CacheUtil.get(key))){
                Backup backup = BackupContextHolder.getBackupById(id);
                UIUtil.console("本地目录：{}，定时备份跳过，此目录正在备份中。。。",backup.getLocalPath());
                return;
            }
            /**
             * 没有点击左上角的开始备份
             * 也没有点击右键菜单里的开始备份
             * 设置定时备份的缓存做标记
             */
            key = CacheUtil.CRON_TASK_ID_KEY + id;
            CacheUtil.set(key,id);
            //执行上传文件操作
            ThreadUtil.execute(() -> BackupUtil.startBackup(id));
        } catch (Exception e) {
            UIUtil.console("定时备份遇到异常情况：{}",e.toString());
        }
    }

    /**
     * 更新阿里云盘token
     */
    public static void updateALiYunPanToken(){
        if (StrUtil.isNotEmpty(UserContextHolder.getToken())){
            UIUtil.console("定时更新阿里云Token");
            AliYunUtil.login();
        }
    }

}
