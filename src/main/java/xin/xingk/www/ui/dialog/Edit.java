package xin.xingk.www.ui.dialog;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lombok.Data;
import xin.xingk.www.common.CronTasks;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.common.DirWatcher;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.ui.Home;
import xin.xingk.www.util.ComponentUtil;
import xin.xingk.www.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static xin.xingk.www.App.mainFrame;

/**
 * @author: Mr.chen
 * @date: 2022/2/13 10:28
 * @description:
 */
@Data
public class Edit extends JDialog implements FocusListener {
    private JPanel editPanel;
    private JTextField localText;
    private JTextField cloudText;
    private JRadioButton ordinaryRadio;
    private JRadioButton classifyRadio;
    private JRadioButton weChatRadio;
    private JRadioButton checkOpenRadio;
    private JRadioButton checkCloseRadio;
    private JTextField timeText;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel localLabel;
    private JLabel cloudLabel;
    private JLabel typeLabel;
    private JLabel monitorLabel;
    private JLabel timeLable;
    private JLabel syncLabel;
    private JRadioButton syncOpenRadio;
    private JRadioButton syncCloseRadio;

    //当前对象
    private static Edit edit;
    //字体默认颜色
    private static Color defaultColor;

    //初始化对象
    public static Edit getInstance() {
        if (edit == null) {
            edit = new Edit();
        }
        return edit;
    }

    public Edit() {
        super(mainFrame, Home.EDIT_TITLE);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.35, 0.6);
        setContentPane(editPanel);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        JButton fileButton = new JButton();
        fileButton.setText(" ");
        fileButton.setIcon(UIManager.getIcon("Tree.openIcon"));
        localText.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, fileButton);

        //打开文件夹窗口
        fileButton.addActionListener(e -> {
            JFileChooser selectPathChooser = new JFileChooser();
            selectPathChooser.setDialogTitle("选择文件夹");
            selectPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = selectPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                localText.setText(selectPathChooser.getSelectedFile().getPath());
            }
        });
        defaultColor = localText.getForeground();
        localText.setForeground(Color.GRAY);

        //备份模式 单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(ordinaryRadio);
        typeGroup.add(classifyRadio);
        typeGroup.add(weChatRadio);

        //目录检测开关 单选
        ButtonGroup monitorGroup = new ButtonGroup();
        monitorGroup.add(checkOpenRadio);
        monitorGroup.add(checkCloseRadio);

        //同步开关 单选
        ButtonGroup syncGroup = new ButtonGroup();
        syncGroup.add(syncOpenRadio);
        syncGroup.add(syncCloseRadio);

        if ("新增备份任务".equals(Home.EDIT_TITLE)) {
            ordinaryRadio.setSelected(true);
            checkCloseRadio.setSelected(true);
            syncCloseRadio.setSelected(true);
            cloudText.addFocusListener(this);
            timeText.addFocusListener(this);
            cloudText.setForeground(Color.GRAY);
            timeText.setForeground(Color.GRAY);
            localText.setText("请点击右侧文件夹图标选择目录");
            cloudText.setText("请输入阿里云盘目录（多级请用\\分隔）");
            timeText.setText("请输入时分秒，如:[20:30:00]");
        } else {
            Integer id = Home.EDIT_ID;
            Backup backup = BackupContextHolder.getBackupById(id);
            localText.setText(backup.getLocalPath());
            cloudText.setText(backup.getCloudPath());
            timeText.setText(backup.getBackupTime());
            Integer backupType = backup.getBackupType();
            if (backupType == 0) ordinaryRadio.setSelected(true);
            if (backupType == 1) classifyRadio.setSelected(true);
            if (backupType == 2) weChatRadio.setSelected(true);
            Integer monitor = backup.getMonitor();
            if (monitor == 0) checkOpenRadio.setSelected(true);
            if (monitor == 1) checkCloseRadio.setSelected(true);
            Integer sync = backup.getSync();
            if (sync == 0) syncOpenRadio.setSelected(true);
            if (sync == 1) syncCloseRadio.setSelected(true);
        }

        //保存按钮
        saveButton.addActionListener(e -> {
            if (checkData()) {//校验数据
                Backup backup = new Backup();
                backup.setLocalPath(localText.getText());
                backup.setCloudPath(cloudText.getText());
                backup.setBackupType(getBackupType());
                backup.setMonitor(getMonitor());
                backup.setBackupTime(timeText.getText());
                backup.setStatus(0);
                backup.setFileNum(0);
                if (Home.EDIT_ID != 0) {//修改
                    backup.setId(Home.EDIT_ID);
                    BackupContextHolder.updateBackup(backup);
                } else {//新增
                    BackupContextHolder.addBackup(backup);
                }
                //设置定时任务
                CronTasks.setTimeTask(backup);
                //设置目录检测
                DirWatcher.setWatchMonitor(backup);
                dispose();
                Home.initTableData();
            }
        });

        //取消按钮
        cancelButton.addActionListener(e -> {
            dispose();
        });

    }

    //获得焦点的时候
    @Override
    public void focusGained(FocusEvent e) {
        JTextField textField = (JTextField) e.getSource();
        textField.setForeground(defaultColor);
        if (textField == cloudText) {
            textField.setText("");
        } else if (textField == timeText) {
            textField.setText("20:30:00");
        }
    }

    //失去焦点的时候
    @Override
    public void focusLost(FocusEvent e) {
        JTextField textField = (JTextField) e.getSource();
//        if (StrUtil.isEmpty(textField.getText())) {
//            textField.setForeground(Color.GRAY);
//            if (textField == cloudText) {
//                textField.setText("请输入阿里云盘目录（多级请用\\分隔）");
//            } else if (textField == timeText) {
//                textField.setText("请输入时分秒，如:[20:30:00]");
//            }
//        }
    }

    /**
     * 获取当前备份模式
     *
     * @return 备份模式
     */
    private Integer getBackupType() {
        if (ordinaryRadio.isSelected()) return DictConstants.BACKUP_TYPE_ORDINARY;
        if (classifyRadio.isSelected()) return DictConstants.BACKUP_TYPE_CLASSIFY;
        if (weChatRadio.isSelected()) return DictConstants.BACKUP_TYPE_WECHAT;
        return DictConstants.BACKUP_TYPE_ORDINARY;
    }

    /**
     * 获取是否开启目录检测
     *
     * @return 目录检测
     */
    private Integer getMonitor() {
        if (checkOpenRadio.isSelected()) return DictConstants.ON;
        if (checkCloseRadio.isSelected()) return DictConstants.OFF;
        return DictConstants.ON;
    }

    /**
     * 验证文本框输入
     */
    private Boolean checkData() {
        if (StrUtil.isEmpty(localText.getText()) || "请点击右侧文件夹图标选择目录".equals(localText.getText())) {
            JOptionPane.showMessageDialog(null, "您没有选择本地目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (StrUtil.isEmpty(cloudText.getText()) || "请输入阿里云盘目录（多级请用\\分隔）".equals(cloudText.getText())) {
            JOptionPane.showMessageDialog(null, "您没有输入云盘备份目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cloudText.getText().startsWith("\\")) {
            cloudText.setText(cloudText.getText().substring(1));
        }
        if (!FileUtil.isDirectory(localText.getText())) {
            JOptionPane.showMessageDialog(null, "请选择正确的目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("请输入时分秒，如:[20:30:00]".equals(timeText.getText())) {
            timeText.setText(null);
        }
        if (StrUtil.isNotEmpty(timeText.getText()) && !ReUtil.contains("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", timeText.getText())) {
            JOptionPane.showMessageDialog(null, "请按格式输入定时备份时间", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
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
        editPanel = new JPanel();
        editPanel.setLayout(new GridLayoutManager(7, 4, new Insets(20, 20, 20, 20), -1, -1));
        localLabel = new JLabel();
        localLabel.setText("本地目录");
        editPanel.add(localLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        localText = new JTextField();
        localText.setEditable(false);
        editPanel.add(localText, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cloudLabel = new JLabel();
        cloudLabel.setText("云盘备份目录");
        editPanel.add(cloudLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cloudText = new JTextField();
        editPanel.add(cloudText, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        typeLabel = new JLabel();
        typeLabel.setText("备份模式");
        editPanel.add(typeLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ordinaryRadio = new JRadioButton();
        ordinaryRadio.setText("普通备份");
        editPanel.add(ordinaryRadio, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        monitorLabel = new JLabel();
        monitorLabel.setText("目录检测");
        editPanel.add(monitorLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeLable = new JLabel();
        timeLable.setText("自动备份时间");
        editPanel.add(timeLable, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeText = new JTextField();
        editPanel.add(timeText, new GridConstraints(5, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        classifyRadio = new JRadioButton();
        classifyRadio.setText("分类备份");
        editPanel.add(classifyRadio, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weChatRadio = new JRadioButton();
        weChatRadio.setText("微信备份");
        editPanel.add(weChatRadio, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("保存");
        editPanel.add(saveButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("取消");
        editPanel.add(cancelButton, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkOpenRadio = new JRadioButton();
        checkOpenRadio.setText("开启");
        editPanel.add(checkOpenRadio, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkCloseRadio = new JRadioButton();
        checkCloseRadio.setText("关闭");
        editPanel.add(checkCloseRadio, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncLabel = new JLabel();
        syncLabel.setText("目录同步");
        editPanel.add(syncLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncOpenRadio = new JRadioButton();
        syncOpenRadio.setEnabled(false);
        syncOpenRadio.setText("开启");
        editPanel.add(syncOpenRadio, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        syncCloseRadio = new JRadioButton();
        syncCloseRadio.setEnabled(false);
        syncCloseRadio.setText("关闭");
        editPanel.add(syncCloseRadio, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return editPanel;
    }

}
