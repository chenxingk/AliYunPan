package xin.xingk.www.common;

import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.util.BackupUtil;
import xin.xingk.www.util.CacheUtil;
import xin.xingk.www.util.FileUtil;
import xin.xingk.www.util.UIUtil;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;

/**
 * @author: Mr.chen
 * @date: 2022/2/20 19:41
 * @description: 目录检测
 */
@Data
public class DirWatcher extends SimpleWatcher {

    /**
     * 备份任务ID
     */
    private Integer backId;

    public DirWatcher(Integer backId){
        this.backId = backId;
    }

    /**
     * 开启目录检测
     */
    public static void startWatcher() {
        List<Backup> backupList = BackupContextHolder.getBackupList();
        if (ObjectUtil.isEmpty(backupList)) return;
        for (Backup backup : backupList) {
            setWatchMonitor(backup);
        }
    }

    /**
     * 设置目录检测
     * @param backup 备份任务
     */
    public static void setWatchMonitor(Backup backup) {
        removeWatcher(backup.getId());
        if (!DictConstants.ON.equals(backup.getMonitor())) return;
        String key = CacheUtil.WATCHER_KEY + backup.getId();
        WatchMonitor monitor = WatchMonitor.createAll(backup.getLocalPath(), new DelayWatcher(new DirWatcher(backup.getId()), 1000));
        CacheUtil.set(key,monitor);
        //监听所有目录
        monitor.setMaxDepth(Integer.MAX_VALUE);
        //启动监听
        monitor.start();
        UIUtil.console("目录：{}，检测开启成功",backup.getLocalPath());
    }

    /**
     * 删除目录检测
     * @param id 备份任务ID
     */
    public static void removeWatcher(int id){
        String key = CacheUtil.WATCHER_KEY + id;
        if (ObjectUtil.isNotEmpty(CacheUtil.get(key))){
            WatchMonitor monitor = (WatchMonitor) CacheUtil.get(key);
            monitor.close();
        }
    }


    /**
     * 监听新增
     * @param event
     * @param currentPath
     */
    @Override
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        String path = currentPath.toString();//文件路径
        String fileName = event.context().toString();//文件名
        dirMonitorFileUpload(path, fileName);
    }

    /**
     * 监听修改
     * @param event
     * @param currentPath
     */
    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {//监听
        String path = currentPath.toString();//文件路径
        String fileName = event.context().toString();//文件名
        dirMonitorFileUpload(path, fileName);
    }

    /**
     * 目录监控文件上传
     * @param path
     * @param fileName
     */
    private synchronized void dirMonitorFileUpload(String path, String fileName) {
        String filePath = path + FileUtil.FILE_SEPARATOR + fileName;
        UIUtil.console("监控到目录：{} 文件发生变化",path);
            if (FileUtil.isFile(filePath)){
                Backup backup = BackupContextHolder.getBackupById(this.getBackId());
                if (BackupUtil.checkBackupStatus(backup)) return;
                CacheUtil.setBackupStatus(backup.getId(), DictConstants.STATUS_BACKUP_RUN);
                String fileSuffix = FileUtil.getSuffix(fileName);//文件后缀
                //备份方法不执行时候执行监听
                if (fileSuffix.length()<=8 && !fileName.startsWith("~$") && !"tmp".equals(fileSuffix)){
                    BackupUtil.monitorUpload(path,fileName,backup);
                }
                CacheUtil.removeBackupStatus(this.getBackId());
        }
    }
}
