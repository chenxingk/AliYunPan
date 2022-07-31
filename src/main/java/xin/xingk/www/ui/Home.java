package xin.xingk.www.ui;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.cron.CronUtil;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Data;
import xin.xingk.www.common.DirWatcher;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.ui.dialog.Edit;
import xin.xingk.www.ui.menu.TableMenuBar;
import xin.xingk.www.util.BackupUtil;
import xin.xingk.www.util.CacheUtil;
import xin.xingk.www.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

/**
 * @author: Mr.chen
 * @date: 2022/2/12 9:58
 * @description:
 */
@Data
public class Home {
    private JPanel homePanel;
    private JPanel btnPanel;
    private JButton addButton;
    private JButton updateButton;
    private JButton delButton;
    private JButton startButton;
    private JTable table;
    private JPanel tableTitlePanel;
    private JLabel tableTitle;
    private JScrollPane tablePane;
    private JLabel logTitle;
    private JTextArea logTextArea;
    private JScrollPane logPane;
    private JButton stopButton;

    //表头
//    private static final String[] TABLE_HEAD = {"ID", "本地目录", "云盘备份目录", "备份模式", "目录检测", "自动备份时间", "状态", "备份数量"};
    private static final String[] TABLE_HEAD = {"ID", "本地目录", "云盘备份目录", "备份模式", "目录检测", "自动备份时间", "状态"};

    //编辑任务标题
    public static String EDIT_TITLE;
    //编辑任务ID
    public static Integer EDIT_ID = 0;

    //当前对象
    private static Home home;

    //初始化对象
    public static Home getInstance() {
        if (home == null) {
            home = new Home();
        }
        return home;
    }

    /**
     * 初始化窗口UI
     */
    public static void initUi() {
        home = getInstance();
        //设置默认聚焦在面板上
        home.getHomePanel().setFocusable(true);
        home.getTableTitle().setFont(FlatUIUtils.nonUIResource(UIManager.getFont("medium.font")));
        home.getLogTitle().setFont(FlatUIUtils.nonUIResource(UIManager.getFont("small.font")));
        //设置表格基本样式
        JTable table = home.getTable();
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        //设置表数据居中显示
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
        //只能选中一行
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //表格右键菜单
        TableMenuBar tableMenuBar = TableMenuBar.getInstance();
        tableMenuBar.init();
        table.setComponentPopupMenu(tableMenuBar);
        initConsole();

        //新增
        home.getAddButton().addActionListener(e -> {
            initTableData();
            EDIT_TITLE = "新增备份任务";
            EDIT_ID = 0;
            Edit edit = new Edit();
            edit.pack();
            edit.setVisible(true);
        });

        //修改
        home.getUpdateButton().addActionListener(e -> updateTable());

        //删除
        home.getDelButton().addActionListener(e -> delTable());

        //开始备份
        home.getStartButton().addActionListener(e -> {
            JButton startButton = home.getStartButton();
            startButton.setEnabled(false);
            ThreadUtil.execute(() -> BackupUtil.startBackup(null));
        });

        //停止备份
        home.getStopButton().addActionListener(e -> {
            List<Backup> backupList = BackupContextHolder.getBackupList();
            for (Backup backup : backupList) {
                Integer status = CacheUtil.getBackupStatus(backup.getId());
                if (!Home.getInstance().getStartButton().getModel().isEnabled() || DictConstants.STATUS_BACKUP_RUN.equals(status)) {
                    CommonConstants.stopUpload = true;
                    UIUtil.console("正在停止备份...");
                    return;
                }
            }
            CommonConstants.stopUpload = false;
            UIUtil.console("当前没有可以停止的备份...");
        });
    }

    /**
     * 初始化控制台
     */
    public static void initConsole() {
        home = getInstance();
        JTextArea logArea = home.getLogTextArea();
        String info = "温馨提示：云盘备份目录是要备份到阿里云盘那个目录下(不存在则自动创建)\n";
        info += "普通备份：会按本地目录结构上传文件\n";
        info += "分类备份：会将本地文件按文档、压缩包、软件、音乐、图片、视频等分类上传\n";
        info += "目录检测：开启目录检测后，会根据文件变更实时备份\n";
        info += "备份时间：输入备份时间后会根据指定时间定时备份\n";
        info += "在设置中可以切换主题，修改是否开机启动\n";
        info += "运行日志...\n";
        logArea.setText(info);
        logArea.setEditable(false);
        //启动自动换行
        logArea.setLineWrap(true);
        //换行不断字
        logArea.setWrapStyleWord(true);
    }

    public static void initTableData() {
        home = getInstance();
        TableModel model = new DefaultTableModel(getBackupList(), TABLE_HEAD) {
            public boolean isCellEditable(int row, int column) {
//                if (column == 0) {
//                    return true;
//                }
                return false;
            }
            /*@Override
            public Class<?> getColumnClass(int columnIndex) {
                return this.getValueAt(0, columnIndex).getClass();
            }*/
        };
        JTable table = home.getTable();
        table.setModel(model);
        //隐藏ID列
        TableColumn idColumn = table.getColumnModel().getColumn(0);
        idColumn.setWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setMinWidth(0);
        //设置表的标题的宽高度也为0
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(0);
    }

    /**
     * 获取备份任务列表
     *
     * @return 获取备份任务列表
     */
    private static Object[][] getBackupList() {
        List<Backup> backupList = BackupContextHolder.getBackupList();
        Object[][] dataArr = new Object[backupList.size()][TABLE_HEAD.length];
        for (int i = 0; i < backupList.size(); i++) {
            //dataArr[i][0] = false;
            dataArr[i][0] = backupList.get(i).getId();
            dataArr[i][1] = backupList.get(i).getLocalPath();
            dataArr[i][2] = backupList.get(i).getCloudPath();
            dataArr[i][3] = DictConstants.BACKUP_TYPE_DICT.get(backupList.get(i).getBackupType());
            dataArr[i][4] = DictConstants.ON_OFF_DICT.get(backupList.get(i).getMonitor());
            dataArr[i][5] = backupList.get(i).getBackupTime();
            dataArr[i][6] = DictConstants.STATUS_DICT.get(backupList.get(i).getStatus());
//            dataArr[i][7] = backupList.get(i).getFileNum();
        }
        return dataArr;
    }

    /**
     * 修改表格数据
     */
    public static void updateTable() {
        JTable table = Home.getInstance().getTable();
        //获取选中的行
        int row = table.getSelectedRow();
        if (row > -1) {
            Home.EDIT_TITLE = "修改备份任务";
            Home.EDIT_ID = (int) table.getValueAt(row, 0);
            if (checkBackupById(Home.EDIT_ID)) return;
            Edit edit = new Edit();
            edit.pack();
            edit.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "请您先选中一行", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 删除表格数据
     */
    public static void delTable() {
        //先判断有没有选中
        JTable table = Home.getInstance().getTable();
        //获取选中的行
        int row = table.getSelectedRow();
        if (row > -1) {
            int id = (int) table.getValueAt(row, 0);
            if (checkBackupById(id)) return;
            int button = JOptionPane.showConfirmDialog(null, "确定要删除吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
            if (button == 0) {
                BackupContextHolder.delBackup(id);
                Home.initTableData();
                //删除定时任务
                CronUtil.remove(String.valueOf(id));
                //删除目录检测
                DirWatcher.removeWatcher(id);
                //删除备份任务状态
                CacheUtil.removeBackupStatus(id);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请您先选中一行", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 备份表格数据
     */
    public static void backupTable() {
        //先判断有没有选中
        JTable table = Home.getInstance().getTable();
        //获取选中的行
        int row = table.getSelectedRow();
        if (row > -1) {
            int id = (int) table.getValueAt(row, 0);
            if (checkBackupById(id)) return;
            BackupUtil.startBackup(id);
        } else {
            JOptionPane.showMessageDialog(null, "请您先选中一行", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 根据ID 检查当前是否在备份中
     *
     * @return
     */
    private static boolean checkBackupById(int id) {
        Integer status = CacheUtil.getBackupStatus(id);
        if (!Home.getInstance().getStartButton().getModel().isEnabled() || DictConstants.STATUS_BACKUP_RUN.equals(status)) {
            JOptionPane.showMessageDialog(null, "此任务当前正在备份中。。。", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else if (DictConstants.STATUS_SYNC_RUN.equals(status)) {
            JOptionPane.showMessageDialog(null, "此任务当前正在同步中。。。", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        homePanel = new JPanel();
        homePanel.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        homePanel.add(btnPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setText("新增");
        btnPanel.add(addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateButton = new JButton();
        updateButton.setText("修改");
        btnPanel.add(updateButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delButton = new JButton();
        delButton.setText("删除");
        btnPanel.add(delButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("开始备份");
        btnPanel.add(startButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        btnPanel.add(spacer1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setText("停止备份");
        btnPanel.add(stopButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tableTitlePanel = new JPanel();
        tableTitlePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        homePanel.add(tableTitlePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tableTitle = new JLabel();
        tableTitle.setText("任务列表");
        tableTitlePanel.add(tableTitle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        tableTitlePanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tablePane = new JScrollPane();
        homePanel.add(tablePane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table = new JTable();
        tablePane.setViewportView(table);
        logTitle = new JLabel();
        logTitle.setText("运行日志");
        homePanel.add(logTitle, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logPane = new JScrollPane();
        homePanel.add(logPane, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logTextArea = new JTextArea();
        logPane.setViewportView(logTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return homePanel;
    }

}
