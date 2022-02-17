package xin.xingk.www.ui.menu;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.swing.DesktopUtil;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.App;
import xin.xingk.www.context.UserContextHolder;
import xin.xingk.www.ui.dialog.About;
import xin.xingk.www.util.UIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

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

    private static String[] themeNames = {"Flat Light","Flat Dark"};

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
        // ---------设置
        JMenu setUp = new JMenu();
        setUp.setText("设置");

        themeMenu = new JMenu();
        themeMenu.setText("主题");

        initThemesMenu();

        setUp.add(themeMenu);

        topMenuBar.add(setUp);

        // ---------帮助
        JMenu aboutMenu = new JMenu();
        aboutMenu.setText("帮助");

        // Check for Updates
        JMenuItem checkForUpdatesItem = new JMenuItem();
        checkForUpdatesItem.setText("检查更新");
        checkForUpdatesItem.addActionListener(e -> problemActionPerformed());
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
            item.setSelected(themeName.equals(UserContextHolder.getUserTheme()));
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
        UserContextHolder.updateUserTheme(selectedThemeName);
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

    private void aboutActionPerformed() {
        try {
            About about = new About();
            about.pack();
            about.setVisible(true);
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

}
