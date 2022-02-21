package xin.xingk.www.context;

import cn.hutool.aop.ProxyUtil;
import xin.xingk.www.mybatis.service.UserService;
import xin.xingk.www.mybatis.MybatisAspect;

/**
 * Author: 陈靖杰
 * Date: 2022/2/16 10:18
 * Description: 用户信息获取
 */
public class UserContextHolder {

    public static UserService userService = ProxyUtil.proxy(new UserService(), MybatisAspect.class);

    /**
     * 获取主题名称
     */
    public static String getUserTheme(){
        return userService.getUserInfo().getTheme();
    }

    /**
     * 获取用户的昵称
     */
    public static String getUserName(){
        return userService.getUserInfo().getName();
    }

    /**
     * 获取用户的Token
     */
    public static String getToken(){
        return userService.getUserInfo().getToken();
    }

    /**
     * 更新用户主题
     * @param theme 主题名称
     */
    public static void updateUserTheme(String theme) {
        userService.updateUserTheme(theme);
    }

    /**
     * 更新用户昵称
     * @param name 昵称
     */
    public static void updateUserName(String name){
        userService.updateUserName(name);
    }

    /**
     * 并发点
     * 更新用户 token
     * @param token token
     */
    public static void updateUserToken(String token){
        userService.updateUserToken(token);
    }
}
