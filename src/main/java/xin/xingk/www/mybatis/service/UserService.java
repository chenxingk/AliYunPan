package xin.xingk.www.mybatis.service;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.entity.User;
import xin.xingk.www.mybatis.config.MybatisPlusConfig;
import xin.xingk.www.mybatis.mapper.UserMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 15:24
 * Description: 用户信息表 业务处理
 */
@Slf4j
public class UserService {

    public static UserMapper userMapper;

    /**
     * 获取用户信息
     * @return
     */
    public User getUserInfo(){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        User user = userMapper.selectOne(null);
        MybatisPlusConfig.closeSqlSession();
        if (ObjectUtil.isNotEmpty(user)) return user;
        user = new User();
        user.setTheme("Flat Light");
        user.setVersion(CommonConstants.VERSION);
        this.addUserInfo(user);
        return user;
    }

    /**
     * 添加用户信息
     * @param user
     */
    public void addUserInfo(User user){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        userMapper.insert(user);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 更新用户主题
     * @param theme 主题名称
     */
    public void updateUserTheme(String theme){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        User user = new User();
        user.setTheme(theme);
        userMapper.update(user,null);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 更新用户昵称
     * @param name 昵称
     */
    public void updateUserName(String name){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        User user = new User();
        user.setName(name);
        userMapper.update(user,null);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 更新用户 token
     * @param token token
     */
    public void updateUserToken(String token){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        User user = new User();
        user.setToken(token);
        userMapper.update(user,null);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 退出登录
     */
    public void logout(){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        userMapper.logout();
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * 更新用户 version
     * @param version version
     */
    public void updateUserVersion(String version){
        userMapper = MybatisPlusConfig.getMapper(UserMapper.class);
        User user = new User();
        user.setVersion(version);
        userMapper.update(user,null);
        MybatisPlusConfig.closeSqlSession();
    }

    /**
     * SQL执行器
     * @param sql sql语句
     */
    public void executeSql(String sql) throws SQLException {
        MybatisPlusConfig.getSqlSession();
        SqlSession sqlSession = MybatisPlusConfig.sqlSession;
        Connection connection = sqlSession.getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        MybatisPlusConfig.closeSqlSession();
    }

}
