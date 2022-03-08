package xin.xingk.www.ui.menu;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.App;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.dialog.About;
import xin.xingk.www.util.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * The top menu bar
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/10.
 */
@Slf4j
public class TopMenuBar extends JMenuBar {

    private static TopMenuBar menuBar;

    private static JMenu themeMenu;

    private static int initialThemeItemCount = -1;

    private static String[] themeNames = {"浅色","深色"};

    private TopMenuBar() {
    }

    public static TopMenuBar getInstance() {
        if (menuBar == null) {
            menuBar = new TopMenuBar();
        }
        return menuBar;
    }

    public void init() {
        TopMenuBar topMenuBar = getInstance();
        topMenuBar.removeAll();
        // ---------应用
        JMenu application = new JMenu();
        application.setText("应用");

        //---------退出登录
        if (CommonConstants.LOGIN_STATUS){
            JMenuItem logOut = new JMenuItem();
            logOut.setText("退出登录");
            logOut.addActionListener(e -> logOutActionPerformed());
            application.add(logOut);
        }

        //---------查看日志
        JMenuItem viewLog = new JMenuItem();
        viewLog.setText("查看日志");
        viewLog.addActionListener(e -> DesktopUtil.open(new File(CommonConstants.LOG_DIR)));
        application.add(viewLog);
        topMenuBar.add(application);

        // ---------设置
        JMenu setUp = new JMenu();
        setUp.setText("设置");

        //---------主题
        themeMenu = new JMenu();
        themeMenu.setText("主题");
        initThemesMenu();
        setUp.add(themeMenu);

        //---------开机启动
        JCheckBoxMenuItem startup = new JCheckBoxMenuItem();
        startup.setSelected(ConfigUtil.getStartup());
        startup.setText("开机启动");
        startup.addActionListener(e -> updateStartup(startup));
        setUp.add(startup);

        topMenuBar.add(setUp);

        // ---------帮助
        JMenu aboutMenu = new JMenu();
        aboutMenu.setText("帮助");

        // Check for Updates
        JMenuItem checkForUpdatesItem = new JMenuItem();
        checkForUpdatesItem.setText("检查更新");
        checkForUpdatesItem.addActionListener(e -> ThreadUtil.execute(() -> {
            boolean update = UpdateUtil.checkForUpdate();
            if (!update) JOptionPane.showMessageDialog(null, "暂无发现更新", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
        }));
        aboutMenu.add(checkForUpdatesItem);

        JMenuItem problemItem = new JMenuItem();
        problemItem.setText("常见问题");
        problemItem.addActionListener(e -> problemActionPerformed());
        aboutMenu.add(problemItem);
        aboutMenu.addSeparator();

        // About
        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("关于");
        aboutMenuItem.addActionListener(e -> aboutActionPerformed());
        aboutMenu.add(aboutMenuItem);

        topMenuBar.add(aboutMenu);
        UIManager.put("TitlePane.unifiedBackground", false);
    }

    /**
     * 初始化主题菜单
     */
    private void initThemesMenu() {
        if (initialThemeItemCount < 0)
            initialThemeItemCount = themeMenu.getItemCount();
        else {
            // remove old items
            for (int i = themeMenu.getItemCount() - 1; i >= initialThemeItemCount; i--)
                themeMenu.remove(i);
        }
        for (String themeName : themeNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(themeName);
            item.setSelected(themeName.equals(ConfigUtil.getTheme()));
            item.addActionListener(this::themeChanged);
            themeMenu.add(item);
        }
    }

    /**
     * 选择主题事件
     * @param actionEvent
     */
    private void themeChanged(ActionEvent actionEvent) {
        String selectedThemeName = actionEvent.getActionCommand();
        FlatAnimatedLafChange.showSnapshot();
        ConfigUtil.setTheme(selectedThemeName);
//        SwingUtilities.updateComponentTreeUI(MainWindow.getInstance().getTabbedPane());
        SwingUtilities.updateComponentTreeUI(App.mainFrame);
        UIUtil.initTheme();
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
        initThemesMenu();
    }

    private void problemActionPerformed() {
        DesktopUtil.browse("https://gitee.com/xingk-code/AliYunPan/wikis");
    }

    /**
     * 退出登录
     */
    private void logOutActionPerformed() {
        CommonConstants.LOGIN_STATUS = false;
        ConfigUtil.logout();
        Login.getInstance().getTipsLabel().setText("");
        App.mainFrame.initLogin();
    }

    /**
     * 打开关于
     */
    private void aboutActionPerformed() {
        try {
            About about = new About();
            about.pack();
            about.setVisible(true);
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新开机启动
     * @param startup
     */
    private void updateStartup(JCheckBoxMenuItem startup) {
        if (!FileUtil.isWindows() || !FileUtil.isDirectory(ShortCutUtil.startup)){
            JOptionPane.showMessageDialog(null, "您的系统暂不支持开机启动，请联系作者", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
        }else {
            if (ConfigUtil.getStartup()){
                startup.setSelected(false);
                ShortCutUtil.cancelStartup();
            }else {
                boolean setStartup = ShortCutUtil.setStartup();
                startup.setSelected(setStartup);
            }
        }
    }

}
