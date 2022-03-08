package xin.xingk.www.ui.menu;

import cn.hutool.core.thread.ThreadUtil;
import xin.xingk.www.ui.Home;

import javax.swing.*;

/**
 * @author: Mr.chen
 * @date: 2022/2/12 16:44
 * @description:
 */
public class TableMenuBar extends JPopupMenu {

    private static JMenuItem update = new JMenuItem();
    private static JMenuItem delete = new JMenuItem();
    private static JMenuItem startBackUp = new JMenuItem();

    private static TableMenuBar tableMenuBar;

    private TableMenuBar() {
    }

    public static TableMenuBar getInstance() {
        if (tableMenuBar == null) {
            tableMenuBar = new TableMenuBar();
        }
        return tableMenuBar;
    }

    public void init() {
        TableMenuBar tableMenuBar = getInstance();
        tableMenuBar.removeAll();
        //---- 修改 ----
        update.setText("修改");
        update.addActionListener(e -> Home.updateTable());
        tableMenuBar.add(update);

        //---- 删除 ----
        delete.setText("删除");
        delete.addActionListener(e -> Home.delTable());
        tableMenuBar.add(delete);
        tableMenuBar.addSeparator();

        //---- 开始备份 ----
        startBackUp.setText("开始备份");
        startBackUp.addActionListener(e -> ThreadUtil.execute(Home::backupTable));
        tableMenuBar.add(startBackUp);
    }




}
