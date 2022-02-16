package xin.xingk.www.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import xin.xingk.www.context.UserContextHolder;

/**
 * Author: 陈靖杰
 * Date: 2022/2/16 10:48
 * Description:
 */
public class UIUtil {

    public static String EDIT_TITLE;

    /**
     * 初始化主题
     */
    public static void initTheme(){
        String theme = UserContextHolder.getUserTheme();
        if ("Flat Light".equals(theme)){
            FlatLightLaf.setup();
        }else {
            FlatDarkLaf.setup();
        }
    }
}
