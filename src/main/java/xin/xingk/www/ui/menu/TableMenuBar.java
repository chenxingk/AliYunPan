package xin.xingk.www.ui.menu;

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
        tableMenuBar = getInstance();
        //---- 修改 ----
        update.setText("修改");
        tableMenuBar.add(update);

        //---- 删除 ----
        delete.setText("删除");
        tableMenuBar.add(delete);
        tableMenuBar.addSeparator();

        //---- 开始备份 ----
        startBackUp.setText("开始备份");
        tableMenuBar.add(startBackUp);
    }

}
