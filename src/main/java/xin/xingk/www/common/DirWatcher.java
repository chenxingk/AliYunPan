package xin.xingk.www.common;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.mybatis.MybatisPlusUtil;
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

    private String localPath;

    public DirWatcher(String localPath){
        this.localPath = localPath;
    }

    /**
     * 开启目录检测
     */
    public static void startWatcher() {
        List<Backup> backupList = BackupContextHolder.getBackupList();
        for (Backup backup : backupList) {
            setWatchMonitor(backup);
        }
        if (ObjectUtil.isNotEmpty(backupList)) UIUtil.console("目录检测开启成功");
    }

    /**
     * 设置目录检测
     * @param backup 备份任务
     */
    public static void setWatchMonitor(Backup backup) {
        if (backup.getMonitor() != DictConstants.MONITOR_ENABLE) return;
        String key = CacheUtil.WATCHER_KEY + "_" + backup.getId();
        remove(backup.getId());
        WatchMonitor monitor = WatchMonitor.createAll(backup.getLocalPath(), new DelayWatcher(new DirWatcher(backup.getLocalPath()), 500));
        CacheUtil.set(key,monitor);
        //监听所有目录
        monitor.setMaxDepth(Integer.MAX_VALUE);
        //启动监听
        monitor.start();
    }

    /**
     * 删除目录检测
     * @param id 备份任务ID
     */
    public static void remove(int id){
        String key = CacheUtil.WATCHER_KEY + "_" + id;
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
        /*String path = currentPath.toString();//文件路径
        String fileName = event.context().toString();//文件名
        String filePath = path + FileUtil.FILE_SEPARATOR + fileName;
        if (FileUtil.isFile(filePath)){
            String localPath = this.getLocalPath();
            Backup backup = BackupContextHolder.getBackupByLocalPath(localPath);
            String fileSuffix = FileUtil.getSuffix(fileName);//文件后缀
            //备份方法不执行时候执行监听
            if (!CommonConstants.BACK_STATE && fileSuffix.length()<=8 && !fileName.startsWith("~$") && !"tmp".equals(fileSuffix)){
                String fileId = UploadLogUtil.getFileUploadFileId(path + FileUtil.FILE_SEPARATOR + fileName);
                if (StrUtil.isEmpty(fileId)){
                    UIUtil.console("{} 准备上传",filePath);
                    BackupUtil.monitorUpload(path,fileName,backup);
                }
            }
        }*/
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
        String filePath = path + FileUtil.FILE_SEPARATOR + fileName;
        if (FileUtil.isFile(filePath)){
            String localPath = this.getLocalPath();
            Backup backup = BackupContextHolder.getBackupByLocalPath(localPath);
            System.out.println(backup);
//            String fileSuffix = FileUtil.getSuffix(fileName);//文件后缀
//            //备份方法不执行时候执行监听
//            if (!CommonConstants.BACK_STATE && fileSuffix.length()<=8 && !fileName.startsWith("~$") && !"tmp".equals(fileSuffix)){
//                String fileId = UploadLogUtil.getFileUploadFileId(path + FileUtil.FILE_SEPARATOR + fileName);
//                if (StrUtil.isNotEmpty(fileId)){
//                    UIUtil.console("{} 发生变化，删除后上传新版",filePath);
//                    AliYunUtil.deleteFile(fileId);//如果文件存在 先删除在重新上传
//                    UploadLogUtil.removeFileUploadLog(filePath);//删除文件上传日志
//                }
//                BackupUtil.monitorUpload(path,fileName,backup);
//            }
        }
    }

    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {

    }

    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {

    }
}
