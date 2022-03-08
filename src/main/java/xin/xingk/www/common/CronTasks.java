package xin.xingk.www.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.util.*;

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
        String id = String.valueOf(backup.getId());
        CronUtil.remove(id);
        if (StrUtil.isEmpty(backup.getBackupTime())) return;
        String cronTab = DateUtil.format(DateUtil.parse(backup.getBackupTime()), "ss mm HH * * ?");
        CronUtil.schedule(id,cronTab, () -> backFileList(backup.getId()));
        UIUtil.console("目录：{}，将在每天【{}】自动备份",backup.getLocalPath(),backup.getBackupTime());
    }

    /**
     * 定时备份文件
     */
    public static void backFileList(int id){
        try {
            CacheUtil.setBackupStatus(id, DictConstants.STATUS_BACKUP_RUN);
            //执行备份目录操作
            ThreadUtil.execute(() -> BackupUtil.startBackup(id));
        } catch (Exception e) {
            UIUtil.console("定时备份遇到异常情况：{}",e.toString());
        }
    }

    /**
     * 更新阿里云盘token
     */
    public static void updateALiYunPanToken(){
        if (StrUtil.isNotEmpty(ConfigUtil.getToken())){
            UIUtil.console("定时更新阿里云Token");
            AliYunUtil.login();
        }
    }

}
