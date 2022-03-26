package xin.xingk.www.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.App;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.ui.Home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    /**
     * 窗口最小化到任务栏托盘
     */
    public static void miniTray() {
        if (SystemTray.isSupported()){
            SystemTray tray = SystemTray.getSystemTray();
            //-Dfile.encoding=GB18030
            PopupMenu popupMenu = new PopupMenu();
            popupMenu.setFont(App.mainFrame.getContentPane().getFont());

            MenuItem openItem = new MenuItem("备份助手");
            MenuItem exitItem = new MenuItem("退出");

            openItem.addActionListener(e -> {
                App.mainFrame.setExtendedState(JFrame.NORMAL);
                App.mainFrame.setVisible(true);
                App.mainFrame.requestFocus();
            });
            exitItem.addActionListener(e -> {
//            if (!PushForm.getInstance().getPushStartButton().isEnabled()) {
//                JOptionPane.showMessageDialog(MainWindow.getInstance().getPushPanel(),
//                        "有推送任务正在进行！\n\n为避免数据丢失，请先停止!\n\n", "Sorry~",
//                        JOptionPane.WARNING_MESSAGE);
//            } else {
//                App.config.save();
//                App.sqlSession.close();
                App.mainFrame.dispose();
                System.exit(0);
//            }
            });

            popupMenu.add(openItem);
            popupMenu.add(exitItem);

            ImageIcon trayImg = null;
            if (SystemInfo.isMacOS){
                trayImg = new ImageIcon(ResourceUtil.getResource("icons/logo_mac.png"));//托盘图标
            }else {
                trayImg = new ImageIcon(ResourceUtil.getResource("icons/logo.png"));//托盘图标
            }
            TrayIcon trayIcon = new TrayIcon(trayImg.getImage(), CommonConstants.TITLE,popupMenu);
            trayIcon.setImageAutoSize(true);

            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(e -> {
                App.mainFrame.setExtendedState(JFrame.NORMAL);
                App.mainFrame.setVisible(true);
                App.mainFrame.requestFocus();
            });
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1: {
                            App.mainFrame.setExtendedState(JFrame.NORMAL);
                            App.mainFrame.setVisible(true);
                            App.mainFrame.requestFocus();
                            break;
                        }
                        case MouseEvent.BUTTON2: {
                            log.debug("托盘图标被鼠标中键被点击");
                            break;
                        }
                        case MouseEvent.BUTTON3: {
                            log.debug("托盘图标被鼠标右键被点击");
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            });

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }
    }
}
