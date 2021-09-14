package xin.xingk.www.ui;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CronTasks;
import xin.xingk.www.common.utils.AliYunPanUtil;
import xin.xingk.www.common.utils.FileUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Enumeration;

/**
 * Description: GUI
 * Author: 陈靖杰
 * Date: 2021/05/12
 */
public class AliYunPan extends JFrame implements ActionListener,FocusListener {


    private static final int obj_left = 100;//左边距
    private static final int text_width = 400;//文本框框
    private static final int text_high = 30;//文本框高

    private static final int title_left = 10;//标题左边距
    private static final int title_width = 166;//标题宽
    private static final int title_high = 25;//标题高

    private static final int radio_top = 150;//单选上边距
    private static final int radio_width = 100;//单选宽
    private static final int radio_high = 25;//单选高

    //阿里云工具类
    private AliYunPanUtil aliYunPanUtil=new AliYunPanUtil();
    // 日志界面
    JScrollPane consolePane = CommonConstants.consolePane;
    JScrollBar scrollBar = CommonConstants.scrollBar;
    //Token文本框
    JTextField tokenText = CommonConstants.tokenText;
    // 备份目录
    private JTextField pathText;
    // 目录名称
    private JTextField folderText;
    //选择文件夹按钮
    private JButton selectBtn;
    //选择文件夹
    private JFileChooser selectPathChooser = new JFileChooser();

    // 迭代一单选框
    private JRadioButton puTongRadio;
    // 迭代二单选框
    private JRadioButton fenLeiRadio;

    // 开始备份
    private JButton startBackup = CommonConstants.startBackup;
    // 退出登录
    private JButton logOut;

    //判断系统是否有托盘
    static SystemTray tray = SystemTray.getSystemTray();
    //托盘图标
    private static TrayIcon trayIcon = null;
    //配置文件
    Setting setting =CommonConstants.setting;
    //定时任务
    private static CronTasks cronTasks = new CronTasks();

    public AliYunPan() throws InterruptedException {
        //GUI默认配置
        initConfig();
        Thread.sleep(100);
        //初始化UI
        initUi();
        this.setVisible(true);
        Thread.sleep(100);
        //初始化变量
        CommonConstants.IS_CONSOLE=true;
        //开启目录检测
        aliYunPanUtil.monitorFolder();
        //开启定时任务
        startTask();
    }

    /**
     * 程序默认配置
     */
    private void initConfig() {
        // 设置界面使用字体
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 13));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
        //设置显示窗口标题
        setTitle("备份助手");
        //设置标题栏的图标
        this.setIconImage(new ImageIcon(getClass().getResource("/images/logo.png")).getImage());
        //设置窗口显示尺寸
        setSize(800,600);
        //窗口是否可以关闭
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //默认居中显示
        setLocationRelativeTo(null);
        //禁止改变窗口大小
        this.setResizable(false);

        // 窗口最小化事件
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowIconified(WindowEvent e) {
                setVisible(false);
                miniTray();
            }

        });
    }

    public void initUi() {
        // 初始化控件
        Container container = getContentPane();
        container.setLayout(null);

        /**
         * 左侧标题
         */
        JLabel pathTitle = new JLabel("选择目录");
        pathText = new JTextField();
        JLabel tokenTitle = new JLabel("阿里云Token");
        //tokenText = new JTextField();
        JLabel folderTitle = new JLabel("备份目录名称");
        folderText = new JTextField();
        JLabel backTitle = new JLabel("备份模式");
        backTitle.setBounds(title_left, 150, title_width, title_high);
        container.add(backTitle);
        // 路径选择区域
        JPanel selectPanle = new JPanel();
        selectPanle.setLayout(null);

        //选择目录框
        pathTitle.setBounds(title_left, 20, title_width, title_high);
        container.add(pathTitle);
        pathText.setText(CommonConstants.PATH);
        pathText.setBounds(obj_left, 20, text_width, text_high);
        pathText.setEditable(false);
        container.add(pathText);

        //token框
        tokenTitle.setBounds(title_left, 60, title_width, title_high);
        container.add(tokenTitle);
        tokenText.setText(CommonConstants.REFRESH_TOKEN.substring(0,16)+"****************");
        tokenText.setBounds(obj_left, 60, text_width, text_high);
        tokenText.setEditable(false);
        container.add(tokenText);

        //目录名称框
        folderTitle.setBounds(title_left, 100, title_width, title_high);
        container.add(folderTitle);
        folderText.setText(CommonConstants.BACK_NAME);
        folderText.setBounds(obj_left, 100, text_width, text_high);
        folderText.addFocusListener(this);
        container.add(folderText);

        //选择文件按钮
        selectBtn = new JButton("选择...");
        selectBtn.setBounds(501, 22, 90, 25);
        selectBtn.addActionListener(this);
        container.add(selectBtn);


        //读取设置中选中的模式
        int backType = CommonConstants.BACK_TYPE;
        boolean pt_checked=false;
        boolean fl_checked=false;
        if (backType==0){
            pt_checked=true;
        }else{
            fl_checked=true;
        }
        //模式选择
        puTongRadio = new JRadioButton("普通备份",pt_checked);
        puTongRadio.setBounds(100, radio_top, radio_width, radio_high);
        puTongRadio.setBackground(container.getBackground());
        puTongRadio.addActionListener(this);
        container.add(puTongRadio);
        fenLeiRadio = new JRadioButton("分类备份",fl_checked);
        fenLeiRadio.setBounds(250, radio_top, radio_width, radio_high);
        fenLeiRadio.setBackground(container.getBackground());
        fenLeiRadio.addActionListener(this);
        container.add(fenLeiRadio);
        //设置为单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(puTongRadio);
        typeGroup.add(fenLeiRadio);


        //startBackup = new JButton("开始备份");
        startBackup.setFont(new Font("宋体", Font.PLAIN, 13));
        startBackup.setBounds(100, 195, 100, 30);
        startBackup.addActionListener(this);
        container.add(startBackup);
        logOut = new JButton("退出登录");
        logOut.setBounds(250, 195, 100, 30);
        logOut.addActionListener(this);
        container.add(logOut);
        //日志面板
        consolePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollBar.setSize(100,100);
        consolePane.setBounds(0, 240, 800, 348);
        container.add(consolePane);
    }

    /**
     * 事件处理器
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //选择文件夹
        if (e.getSource() == selectBtn) {
            selectPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int intRetVal = selectPathChooser.showOpenDialog(this);
            if (intRetVal == JFileChooser.APPROVE_OPTION) {
                pathText.setText(selectPathChooser.getSelectedFile().getPath());
                pathText.setEditable(false);
                //保存选择的目录
                CommonConstants.PATH=pathText.getText();
                setting.set("pathText",CommonConstants.PATH);
                setting.store();
            }
        }else {
            //开始按钮
            if (e.getSource() == startBackup) {
                if (!checkText()){//验证
                    return;
                }
                startBackup.setEnabled(false);
                startBackup.setText("正在备份");
                CommonConstants.CLEAN_CONSOLE=0;
                //获取用户输入的目录
                CommonConstants.PATH=pathText.getText();
                setting.set("pathText",CommonConstants.PATH);
                //获取用户输入的目录名称
                CommonConstants.BACK_NAME=folderText.getText();
                setting.set("folderText",CommonConstants.BACK_NAME);
                //获取上传模式
                CommonConstants.BACK_TYPE = puTongRadio.isSelected() ? 0 : 1;
                setting.set("backType",CommonConstants.BACK_TYPE+"");
                //输出模式
                CommonConstants.addConsole("备份模式："+(puTongRadio.isSelected() ? "普通模式" : "分类模式"));
                setting.store();
                //执行上传文件操作
                try {
                    Thread backup = new Thread(() -> aliYunPanUtil.startBackup());
                    backup.start();
                } catch (Exception exc) {
                    CommonConstants.addConsole("遇到异常情况："+exc.toString());
                }
            }

            //退出按钮
            if (e.getSource() == logOut) {
                setting.set("tokenText","");
                setting.store();
                setVisible(false);
                CommonConstants.monitor.close();
                new Login();
            }

            //普通模式
            if (e.getSource() == puTongRadio) {
                doSetBackType(0);
            }

            //普通模式
            if (e.getSource() == fenLeiRadio) {
                doSetBackType(1);
            }
        }
    }

    /**
     * 设置备份模式
     * @param type
     */
    public void doSetBackType(int type) {
        CommonConstants.BACK_TYPE = type;
        setting.set("backType", CommonConstants.BACK_TYPE + "");
        setting.store();
    }

    /**
     * 验证文本框输入
     */
    public Boolean checkText(){
        if (StrUtil.isEmpty(pathText.getText())){
            JOptionPane.showMessageDialog(null, "您没有选择需要备份的目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (StrUtil.isEmpty(tokenText.getText())){
            JOptionPane.showMessageDialog(null, "您没有输入阿里云token", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (StrUtil.isEmpty(folderText.getText())){
            JOptionPane.showMessageDialog(null, "您没有输入需要备份到阿里云的目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tokenText.getText().length()!=32){
            JOptionPane.showMessageDialog(null, "您输入的token不正确", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!FileUtil.isDirectory(pathText.getText())){
            JOptionPane.showMessageDialog(null, "请选择正确目录", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    //窗口最小化到任务栏托盘
    private void miniTray() {
        ImageIcon trayImg = new ImageIcon(JFrame.class.getClass().getResource("/images/logo.png"));//托盘图标
        trayIcon = new TrayIcon(trayImg.getImage(), "备份助手");
        trayIcon.setImageAutoSize(true);

        //鼠标点击事件处理器
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //if (e.getClickCount() ==2 ) {
                // 鼠标点击一次打开软件
                if (e.getClickCount() == 1) {
                    // 移去托盘图标
                    tray.remove(trayIcon);
                    setVisible(true);
                    //还原窗口
                    setExtendedState(JFrame.NORMAL);
                    toFront();
                }
            }
        });

        try {
            tray.add(trayIcon);
        } catch (Exception e) {
            System.out.println("发生异常："+e);
        }
    }

    //获得焦点的时候
    @Override
    public void focusGained(FocusEvent e) {
        //文件目录
        if (e.getSource() == folderText) {
            //获取用户输入的目录名称
            CommonConstants.BACK_NAME=folderText.getText();
            setting.set("folderText",CommonConstants.BACK_NAME);
            setting.store();
        }
    }

    //失去焦点的时候
    @Override
    public void focusLost(FocusEvent e) {
        //文件目录
        if (e.getSource() == folderText) {
            //获取用户输入的目录名称
            CommonConstants.BACK_NAME=folderText.getText();
            setting.set("folderText",CommonConstants.BACK_NAME);
            setting.store();
        }
    }

    /**
     * 定时任务开始
     */
    private void startTask() {
        //每小时刷新阿里云盘登录信息
        CronUtil.schedule("0 0 0/1 * * ?", (Task) () -> cronTasks.updateALiYunPanToken());
        //设置定时备份时间
        String backupTime="";
        if (StrUtil.isEmpty(setting.getStr("backupTime"))){
            backupTime = DateUtil.format(DateUtil.parse("20:30:00"), "HH:mm:ss");
            setting.set("backupTime",backupTime);
            setting.store();
        }
        try {
            backupTime = DateUtil.format(DateUtil.parse(setting.getStr("backupTime")), "HH:mm:ss");
            String cronTab = DateUtil.format(DateUtil.parse(setting.getStr("backupTime")), "ss mm HH * * ?");
            CronUtil.schedule(cronTab, (Task) () -> cronTasks.backFileList());
            CommonConstants.addConsole("自动备份定时任务开启成功，每天【"+backupTime+"】执行");
        } catch (Exception e) {
            CommonConstants.addConsole("自动备份定时任务开启失败，请检查backupTime参数格式是否为(hh:mm:ss)");
        }
        CommonConstants.addConsole("如需修改定时请到back_config.setting文件中修改backupTime参数，注意格式不要写错哦~");
        //开启定时
        CronUtil.start();
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
    }

}