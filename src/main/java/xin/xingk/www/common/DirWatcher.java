package xin.xingk.www.common;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.context.UploadRecordContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.UploadRecord;
import xin.xingk.www.ui.Home;
import xin.xingk.www.util.*;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;

/**
 * @author: Mr.chen
 * @date: 2022/2/20 19:41
 * @description: 目录检测
 */
@Data
public class DirWatcher implements Watcher {

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
        remove(backup.getId());
        if (!DictConstants.MONITOR_ENABLE.equals(backup.getMonitor())) return;
        String key = CacheUtil.WATCHER_KEY + backup.getId();
        WatchMonitor monitor = WatchMonitor.createAll(backup.getLocalPath(), new DelayWatcher(new DirWatcher(backup.getId()), 500));
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
    public static void remove(int id){
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
        System.out.println("新增文件目录："+path);
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
        System.out.println("修改文件目录："+path);
        dirMonitorFileUpload(path, fileName);
    }

    /**
     * 目录监控文件上传
     * @param path
     * @param fileName
     */
    private synchronized void dirMonitorFileUpload(String path, String fileName) {
        String filePath = path + FileUtil.FILE_SEPARATOR + fileName;
        System.out.println("目录检测："+filePath);
        if (FileUtil.isFile(filePath)){
            /**
             * 没有点击左上角的开始备份
             * 也没有点击右键菜单里的开始备份
             * 定时备份也没有执行
             */

            if (Home.getInstance().getStartButton().getModel().isEnabled()){
                //单个备份
                String key = CacheUtil.BACKUP_ID_KEY + this.backId;
                //定时备份
                String cronKey = CacheUtil.BACKUP_ID_KEY + this.backId;
                if (ObjectUtil.isEmpty(CacheUtil.get(key)) && ObjectUtil.isEmpty(CacheUtil.get(cronKey))) {
                    String fileSuffix = FileUtil.getSuffix(fileName);//文件后缀
                    //备份方法不执行时候执行监听
                    if (fileSuffix.length()<=8 && !fileName.startsWith("~$") && !"tmp".equals(fileSuffix)){
                        UploadRecord uploadRecord = UploadRecordContextHolder.getUploadRecordByFilePath(filePath);
                        if (ObjectUtil.isNotEmpty(uploadRecord)){
                            UIUtil.console("{} 发生变化，删除后上传新版",filePath);
                            //如果文件存在 先删除在重新上传
                            AliYunUtil.deleteFile(uploadRecord.getFileId());
                            //删除文件上传记录
                            UploadRecordContextHolder.delUploadRecord(uploadRecord.getId());
                        }
                        Backup backup = BackupContextHolder.getBackupById(this.backId);
                        BackupUtil.monitorUpload(path,fileName,backup);
                    }
                }
            }
        }
    }

    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {

    }

    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {

    }
}
