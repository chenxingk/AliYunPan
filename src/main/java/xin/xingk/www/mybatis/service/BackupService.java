package xin.xingk.www.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.mybatis.config.MybatisPlusConfig;
import xin.xingk.www.mybatis.mapper.BackupMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
        backupMapper.insert(backup);
    }

    /**
     * 删除备份任务
     * @param id
     */
    public void delBackup(Integer id){
        backupMapper.deleteById(id);
    }

    /**
     * 修改备份任务
     * @param backup
     */
    public void updateBackup(Backup backup){
        backupMapper.updateById(backup);
    }

    /**
     * 根据ID获取备份任务信息
     * @param id
     */
    public Backup getBackupById(Integer id){
        return backupMapper.selectById(id);
    }

    /**
     * 根据本地目录 获取备份任务信息
     * @param localPath
     */
    public Backup getBackupByLocalPath(String localPath){
        LambdaQueryWrapper<Backup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Backup::getLocalPath,localPath);
        return backupMapper.selectOne(queryWrapper);
    }


    /**
     * 获取备份任务信息列表
     */
    public List<Backup> getBackupList(){
        return backupMapper.selectList(null);
    }

    /**
     * SQL执行器
     * @param sql sql语句
     */
    public void executeSql(String sql) throws SQLException {
        SqlSession sqlSession = MybatisPlusConfig.sqlSession;
        Connection connection = sqlSession.getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }




}
