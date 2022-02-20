package xin.xingk.www.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.context.UserContextHolder;
import xin.xingk.www.ui.Home;

import javax.swing.*;

/**
 * Author: 陈靖杰
 * Date: 2022/2/16 10:48
 * Description:
 */
public class UIUtil {

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

    /**
     * 添加控制台日志
     * @param text 日志模板
     * @param params 内容
     */
    public static void console(String text,Object... params){
        JTextArea console = Home.getInstance().getLogTextArea();
        String date="["+ DateUtil.now()+"] ";
        if (CommonConstants.CLEAN_CONSOLE==0){
            console.setText(date+"开始运行"+"\n");
            UploadLogUtil.runLog.append(date+"开始运行"+"\n");
        }else {
            String format = StrUtil.format(date+text,params);
            console.append(format+"\n");
            UploadLogUtil.runLog.append(format+"\n");
            console.setCaretPosition(console.getText().length());
        }
    }
}
