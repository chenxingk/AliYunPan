package xin.xingk.www.context;

import cn.hutool.aop.ProxyUtil;
import xin.xingk.www.mybatis.service.BackupService;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.mybatis.config.MybatisAspect;

import java.sql.SQLException;
import java.util.List;

/**
 * Author: 陈靖杰
 * Date: 2022/2/17 10:19
 * Description: 备份任务信息获取
 */
public class BackupContextHolder {

    public static BackupService backupService = ProxyUtil.proxy(new BackupService(), MybatisAspect.class);

    /**
     * 新增备份任务
     * @return
     */
    public static void addBackup(Backup backup){
        backupService.addBackup(backup);
    }

    /**
     * 删除备份任务
     * @param id
     */
    public static void delBackup(Integer id){
        backupService.delBackup(id);
    }

    /**
     * 修改备份任务
     * @param backup
     */
    public static void updateBackup(Backup backup){
        backupService.updateBackup(backup);
    }

    /**
     * 并发点
     * 根据ID获取备份任务信息
     * @param id
     */
    public static Backup getBackupById(Integer id){
        return backupService.getBackupById(id);
    }

    /**
     * 根据本地目录 获取备份任务信息
     * @param localPath
     */
    public static Backup getBackupByLocalPath(String localPath){
        return backupService.getBackupByLocalPath(localPath);
    }

    /**
     * 并发点
     * 获取备份任务信息列表
     */
    public static List<Backup> getBackupList(){
        return backupService.getBackupList();
    }

    /**
     * SQL执行器
     * @param sql sql语句
     */
    public static void executeSql(String sql) throws SQLException {
        backupService.executeSql(sql);
    }

}
