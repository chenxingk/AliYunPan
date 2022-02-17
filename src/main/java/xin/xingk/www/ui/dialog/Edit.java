package xin.xingk.www.ui.dialog;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lombok.Data;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.ui.Home;
import xin.xingk.www.util.ComponentUtil;
import xin.xingk.www.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private JRadioButton openRadio;
    private JRadioButton closeRadio;
    private JTextField timeText;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel localLabel;
    private JLabel cloudLabel;
    private JLabel typeLabel;
    private JLabel monitorLabel;
    private JLabel timeLable;

    //当前对象
    private static Edit edit;

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
            selectPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = selectPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                localText.setText(selectPathChooser.getSelectedFile().getPath());
                System.out.println(localText.getText());
            }
        });

        //备份模式 单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(ordinaryRadio);
        typeGroup.add(classifyRadio);
        typeGroup.add(weChatRadio);

        //备份模式 单选
        ButtonGroup monitorGroup = new ButtonGroup();
        monitorGroup.add(openRadio);
        monitorGroup.add(closeRadio);

        if ("新增备份任务".equals(Home.EDIT_TITLE)) {
            ordinaryRadio.setSelected(true);
            closeRadio.setSelected(true);
            cloudText.addFocusListener(this);
            timeText.addFocusListener(this);
            localText.setForeground(Color.GRAY);
            cloudText.setForeground(Color.GRAY);
            timeText.setForeground(Color.GRAY);
            localText.setText("请点击右侧文件夹图标选择目录");
            cloudText.setText("请输入阿里云盘目录（多级请用\\分隔）");
            timeText.setText("请输入时分秒，如:[20:30:00]");
        }

        //保存按钮
        saveButton.addActionListener(e -> {
            if (checkData()) {//校验数据
                Backup backup = new Backup();
                backup.setLocalPath(localText.getText());
                backup.setCloudDiskPath(cloudText.getText());
                backup.setBackupType(getBackupType());
                backup.setMonitor(getMonitor());
                backup.setBackupTime(timeText.getText());
                backup.setStatus(1);
                backup.setFileNum(0);
                BackupContextHolder.addBackup(backup);
                dispose();
                Home.initTable();
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
        textField.setForeground(Color.BLACK);
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
        if (ordinaryRadio.isSelected()) return CommonConstants.BACKUP_TYPE_ORDINARY;
        if (classifyRadio.isSelected()) return CommonConstants.BACKUP_TYPE_CLASSIFY;
        if (weChatRadio.isSelected()) return CommonConstants.BACKUP_TYPE_WECHAT;
        return CommonConstants.BACKUP_TYPE_ORDINARY;
    }

    /**
     * 获取是否开启目录检测
     *
     * @return 目录检测
     */
    private Integer getMonitor() {
        if (openRadio.isSelected()) return CommonConstants.MONITOR_OPEN;
        if (closeRadio.isSelected()) return CommonConstants.MONITOR_CLOSE;
        return CommonConstants.MONITOR_OPEN;
    }

    /**
     * 验证文本框输入
     */
    private Boolean checkData() {
        if (StrUtil.isEmpty(localText.getText())) {
            JOptionPane.showMessageDialog(null, "您没有选择本地目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (StrUtil.isEmpty(cloudText.getText())) {
            JOptionPane.showMessageDialog(null, "您没有输入云盘备份目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!FileUtil.isDirectory(localText.getText())) {
            JOptionPane.showMessageDialog(null, "请选择正确的目录", "错误", JOptionPane.ERROR_MESSAGE);
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
        editPanel.setLayout(new GridLayoutManager(6, 4, new Insets(20, 20, 20, 20), -1, -1));
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
        openRadio = new JRadioButton();
        openRadio.setText("开启");
        editPanel.add(openRadio, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeLable = new JLabel();
        timeLable.setText("自动备份时间");
        editPanel.add(timeLable, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeText = new JTextField();
        editPanel.add(timeText, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        classifyRadio = new JRadioButton();
        classifyRadio.setText("分类备份");
        editPanel.add(classifyRadio, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        closeRadio = new JRadioButton();
        closeRadio.setText("关闭");
        editPanel.add(closeRadio, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weChatRadio = new JRadioButton();
        weChatRadio.setText("微信备份");
        editPanel.add(weChatRadio, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("保存");
        editPanel.add(saveButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("取消");
        editPanel.add(cancelButton, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return editPanel;
    }

}
