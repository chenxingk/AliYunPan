package xin.xingk.www;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.CronTasks;
import xin.xingk.www.common.DirWatcher;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.MainFrame;
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
