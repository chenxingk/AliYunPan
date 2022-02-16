package xin.xingk.www.context;

import cn.hutool.aop.ProxyUtil;
import xin.xingk.www.context.service.UserService;
import xin.xingk.www.mybatis.MybatisAspect;

/**
 * Author: 陈靖杰
 * Date: 2022/2/16 10:18
 * Description:
 */
public class UserContextHolder {

    public static UserService userService = ProxyUtil.proxy(new UserService(), MybatisAspect.class);

    /**
     * 获取主题名称
     */
    public static String getUserTheme(){
        return userService.getUserTheme();
    }

    /**
     * 更新用户主题
     * @param theme 主题名称
     */
    public static void updateUserTheme(String theme) {
        userService.updateUserTheme(theme);
    }
}
