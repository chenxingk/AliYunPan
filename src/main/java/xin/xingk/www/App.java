package xin.xingk.www;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.system.SystemUtil;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.util.SystemInfo;
import com.sun.deploy.ui.AboutDialog;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.MainFrame;
import xin.xingk.www.ui.dialog.About;
import xin.xingk.www.util.UIUtil;

import javax.swing.*;

/**
 * 备份程序
 *
 */
@Slf4j
public class App {
    public static MainFrame mainFrame;

    public static void main( String[] args ) {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "备份助手");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "备份助手");
            System.setProperty("apple.awt.application.appearance", "system");

            FlatDesktop.setAboutHandler(() -> {
                try {
                    About about = new About();
                    about.pack();
                    about.setVisible(true);
                } catch (Exception e2) {
                    log.error(ExceptionUtil.stacktraceToString(e2));
                }
            });
            FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);
        }
        UIUtil.initTheme();
        String version = SystemUtil.getJavaSpecInfo().getVersion();
        if (!"1.8".equals(version)){
            JOptionPane.showMessageDialog(null, "你当前的JDK版本为【"+version+"】\n" +
                    "建议您使用JDK【1.8】来运行本程序", "温馨提示", JOptionPane.ERROR_MESSAGE);
        }
        TimeInterval timer = DateUtil.timer();
        mainFrame = new MainFrame();
        mainFrame.initUpdate();
        //初始化主窗口UI
        Home.initUi();

        ThreadUtil.execute(Login::initUpdate);

        //mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        log.info("启动耗时："+timer.interval()+" ms");

        //系统托盘设置
        UIUtil.miniTray();
    }



}
