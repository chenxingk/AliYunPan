package xin.xingk.www.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.ui.Home;

import javax.swing.*;

/**
 * Author: 陈靖杰
 * Date: 2022/2/16 10:48
 * Description:
 */
@Slf4j
public class UIUtil {

    /**
     * 初始化主题
     */
    public static void initTheme(){
        String theme = ConfigUtil.getTheme();
        if ("浅色".equals(theme)){
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
        String format = StrUtil.format(date+text,params);
        console.append(format+"\n");
        log.info(format);
        console.setCaretPosition(console.getText().length());
    }
}
