package xin.xingk.www.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.mybatis.config.MybatisPlusConfig;
import xin.xingk.www.mybatis.mapper.BackupMapper;

import java.util.List;

/**
 * Author: 陈靖杰
 * Date: 2022/2/17 09:57
 * Description: 备份任务表 业务处理
 */
@Slf4j
public class BackupService {

    public static BackupMapper backupMapper;

    /**
     * 新增备份任务
     * @return
     */
    public void addBackup(Backup backup){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        backupMapper.insert(backup);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 删除备份任务
     * @param id
     */
    public void delBackup(Integer id){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        backupMapper.deleteById(id);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 修改备份任务
     * @param backup
     */
    public void updateBackup(Backup backup){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        backupMapper.updateById(backup);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 根据ID获取备份任务信息
     * @param id
     */
    public Backup getBackupById(Integer id){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        Backup backup = backupMapper.selectById(id);
        MybatisPlusConfig.closeSqlSession();
        return backup;
    }

    /**
     * 根据本地目录 获取备份任务信息
     * @param localPath
     */
    public Backup getBackupByLocalPath(String localPath){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        LambdaQueryWrapper<Backup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Backup::getLocalPath,localPath);
        Backup backup = backupMapper.selectOne(queryWrapper);
        MybatisPlusConfig.closeSqlSession();
        return backup;
    }


    /**
     * 获取备份任务信息列表
     */
    public List<Backup> getBackupList(){
        backupMapper = MybatisPlusConfig.getMapper(BackupMapper.class);
        List<Backup> backupList = backupMapper.selectList(null);
        MybatisPlusConfig.closeSqlSession();
        return backupList;
    }




}
