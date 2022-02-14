package xin.xingk.www.service;

import xin.xingk.www.entity.User;
import xin.xingk.www.mapper.UserMapper;
import xin.xingk.www.mybatis.MybatisPlusUtil;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 15:24
 * Description: 用户信息表 业务处理
 */
public class UserService {

    UserMapper userMapper = MybatisPlusUtil.getMapper(UserMapper.class);

    public void insert(){
        User user = new User();
        user.setName("陈靖杰");
        user.setToken("666666666");
        user.setTheme(0);
        int insert = userMapper.insert(user);
        System.out.println("更新了"+insert+"条");
    }

}
