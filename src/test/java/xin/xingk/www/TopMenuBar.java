package xin.xingk.www;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import lombok.extern.slf4j.Slf4j;

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

    private static String name;

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
        JMenu appearanceMenu = new JMenu();
        appearanceMenu.setText("设置");

        themeMenu = new JMenu();
        themeMenu.setText("主题");

        initThemesMenu();

        appearanceMenu.add(themeMenu);

        topMenuBar.add(appearanceMenu);

        // ---------帮助
        JMenu aboutMenu = new JMenu();
        aboutMenu.setText("帮助");

        // Check for Updates
        JMenuItem checkForUpdatesItem = new JMenuItem();
        checkForUpdatesItem.setText("检查更新");
        checkForUpdatesItem.addActionListener(e -> checkForUpdatesActionPerformed());
        aboutMenu.add(checkForUpdatesItem);

        JMenuItem problemItem = new JMenuItem();
        problemItem.setText("常见问题");
        problemItem.addActionListener(e -> checkForUpdatesActionPerformed());
        aboutMenu.add(problemItem);
        aboutMenu.addSeparator();

        // About
        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("关于");
        //aboutMenuItem.addActionListener(e -> aboutActionPerformed());
        aboutMenu.add(aboutMenuItem);

        topMenuBar.add(aboutMenu);
    }

    private void checkForUpdatesActionPerformed() {
        //ThreadUtil.execute(() -> UpgradeUtil.checkUpdate(false));
    }

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
            //item.setSelected(themeName.equals(App.config.getTheme()));
            item.setSelected(themeName.equals(name));
            item.addActionListener(this::themeChanged);
            themeMenu.add(item);
        }
    }

    private void themeChanged(ActionEvent actionEvent) {
        try {
//            String selectedThemeName = actionEvent.getActionCommand();
            name = actionEvent.getActionCommand();
            FlatAnimatedLafChange.showSnapshot();

//            App.config.setTheme(selectedThemeName);
//            App.config.save();
//
//            Init.initTheme();
//            SwingUtilities.updateComponentTreeUI(App.mainFrame);
//            SwingUtilities.updateComponentTreeUI(MainWindow.getInstance().getTabbedPane());
            if ("Flat Light".equals(name)){
                FlatLightLaf.setup();
            }else {
                FlatDarkLaf.setup();
            }
//            item.setSelected(false);
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            initThemesMenu();

        } catch (Exception e1) {
//            JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(), "Save failed!\n\n" + e1.getMessage(), "Failed",
//                    JOptionPane.ERROR_MESSAGE);
//            log.error(ExceptionUtils.getStackTrace(e1));
        }
    }

//    private void aboutActionPerformed() {
//        try {
//            AboutDialog dialog = new AboutDialog();
//
//            dialog.pack();
//            dialog.setVisible(true);
//        } catch (Exception e2) {
//            log.error(ExceptionUtils.getStackTrace(e2));
//        }
//    }

}
