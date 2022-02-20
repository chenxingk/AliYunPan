package xin.xingk.www.mybatis;


import cn.hutool.aop.aspects.SimpleAspect;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.context.service.BackupService;
import xin.xingk.www.context.service.UserService;
import xin.xingk.www.mybatis.mapper.BackupMapper;
import xin.xingk.www.mybatis.mapper.UserMapper;

import java.lang.reflect.Method;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 11:00
 * Description: Mybatis切面 用于SQLSession获取和关闭
 */
@Slf4j
public class MybatisAspect extends SimpleAspect {

    @Override
    public boolean before(Object target, Method method, Object[] args) {
        MybatisPlusUtil.getSqlSession();
        UserService.userMapper = MybatisPlusUtil.getMapper(UserMapper.class);
        BackupService.backupMapper = MybatisPlusUtil.getMapper(BackupMapper.class);
        log.debug(">>> SqlSession进行初始化。。。");
        return true;
    }

    @Override
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        MybatisPlusUtil.closeSqlSession();
        log.debug(">>> SqlSession关闭。。。");
        return true;
    }
}
