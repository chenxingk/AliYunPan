package xin.xingk.www.context.service;

import cn.hutool.core.util.ObjectUtil;
import xin.xingk.www.entity.User;
import xin.xingk.www.mapper.UserMapper;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 15:24
 * Description: 用户信息表 业务处理
 */
public class UserService {

    public static UserMapper userMapper;

    /**
     * 获取用户信息
     * @return
     */
    public User getUserInfo(){
        User user = userMapper.selectOne(null);
        if (ObjectUtil.isNotEmpty(user)) return user;
        user = new User();
        user.setName("");
        user.setToken("");
        user.setTheme("Flat Light");
        this.addUserInfo(user);
        return user;
    }

    /**
     * 添加用户信息
     * @param user
     */
    public void addUserInfo(User user){
        userMapper.insert(user);
    }

    /**
     * 获取用户使用的模板
     * @return 模板名称
     */
    public String getUserTheme(){
        return this.getUserInfo().getTheme();
    }

    /**
     * 更新用户主题
     * @param theme 主题名称
     */
    public void updateUserTheme(String theme){
        User userInfo = this.getUserInfo();
        userInfo.setTheme(theme);
        userMapper.updateById(userInfo);
    }

}
