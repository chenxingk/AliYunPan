package xin.xingk.www.ui;


import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;
import xin.xingk.www.common.CronTasks;
import xin.xingk.www.util.AliYunPanUtil;
import xin.xingk.www.util.ConfigUtil;
import xin.xingk.www.util.FileUtil;
import xin.xingk.www.util.UploadLogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

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

    /**
     * 主页面
     */
    // 日志界面
    JScrollPane consolePane = CommonUI.consolePane;
    JScrollBar scrollBar = CommonUI.scrollBar;
    // Token文本框
    JTextField tokenText = CommonUI.tokenText;
    // 备份目录
    private JTextField pathText;
    // 目录名称
    private JTextField folderText;
    // 选择文件夹按钮
    private JButton selectBtn;
    // 选择文件夹
    private JFileChooser selectPathChooser = new JFileChooser();
    // 普通备份
    private JRadioButton puTongRadio;
    // 分类备份
    private JRadioButton fenLeiRadio;
    // 开始备份
    private JButton startBackup = CommonUI.startBackup;
    // 退出登录
    private JButton logOut;

    /**
     * 更多设置
     */
    //开启目录监控
    private JRadioButton startMonitor;
    //关闭目录监控
    private JRadioButton stopMonitor;
    //定时备份时间
    private JTextField timeTaskText;
    //保存定时备份
    private JButton saveTime;


    //判断系统是否有托盘
    static SystemTray tray = SystemTray.getSystemTray();
    //托盘图标
    private static TrayIcon trayIcon = null;
    //定时任务
    private static CronTasks cronTasks = new CronTasks();

    public AliYunPan(){
        //GUI默认配置
        initConfig();
        //初始化UI
        initUi();
        //初始化变量
        CommonConstants.IS_CONSOLE=true;
        //显示窗口
        this.setVisible(true);
        //开启定时任务
        CronTasks.startTask();
        //开启目录检测
        if (ConfigUtil.getMonitorFolder()){
            startMonitorFolder();
        }
    }

    /**
     * 程序默认配置
     */
    private void initConfig() {
        // 设置界面使用字体
        CommonUI.setFont();
        //设置显示窗口标题
        setTitle("备份助手");
        //设置标题栏的图标
        this.setIconImage(new ImageIcon(getClass().getResource("/images/logo.png")).getImage());
        //设置窗口显示尺寸
        setSize(800,625);
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
        JTabbedPane mainTab = new JTabbedPane();

        Container index = setIndex();
        Container config = setConfig();
        //index.setLayout(null);
        mainTab.add("主界面", index);
        mainTab.add("更多设置", config);
        this.setContentPane(mainTab);
    }

    public Container setIndex(){
        // 初始化控件
        Container container = new Container();
        /**
         * 左侧标题
         */
        JLabel pathTitle = new JLabel("本地目录");
        pathText = new JTextField();
        JLabel tokenTitle = new JLabel("阿里云Token");
        //tokenText = new JTextField();
        JLabel folderTitle = new JLabel("云盘备份目录");
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
        pathText.setText(ConfigUtil.getPath());
        pathText.setBounds(obj_left, 20, text_width, text_high);
        pathText.setEditable(false);
        container.add(pathText);

        //token框
        tokenTitle.setBounds(title_left, 60, title_width, title_high);
        container.add(tokenTitle);
        tokenText.setText(StrUtil.hide(ConfigUtil.getRefreshToken(),10,20));
        tokenText.setBounds(obj_left, 60, text_width, text_high);
        tokenText.setEditable(false);
        container.add(tokenText);

        //目录名称框
        folderTitle.setBounds(title_left, 100, title_width, title_high);
        container.add(folderTitle);
        folderText.setText(ConfigUtil.getBackName());
        folderText.setBounds(obj_left, 100, text_width, text_high);
        folderText.addFocusListener(this);
        container.add(folderText);

        //选择文件按钮
        selectBtn = new JButton("选择...");
        selectBtn.setBounds(501, 22, 90, 25);
        selectBtn.addActionListener(this);
        container.add(selectBtn);

        //模式选择
        puTongRadio = new JRadioButton("普通备份",ConfigUtil.getBackType());
        puTongRadio.setBounds(100, radio_top, radio_width, radio_high);
        puTongRadio.setBackground(container.getBackground());
        puTongRadio.addActionListener(this);
        container.add(puTongRadio);
        fenLeiRadio = new JRadioButton("分类备份",!ConfigUtil.getBackType());
        fenLeiRadio.setBounds(250, radio_top, radio_width, radio_high);
        fenLeiRadio.setBackground(container.getBackground());
        fenLeiRadio.addActionListener(this);
        container.add(fenLeiRadio);
        //设置为单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(puTongRadio);
        typeGroup.add(fenLeiRadio);

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
        scrollBar.setSize(100,355);
        consolePane.setBounds(0, 240, 800, 360);
        container.add(consolePane);
        return container;
    }

    public Container setConfig(){
        //初始化控件
        Container container = new Container();

        //目录检测
        JLabel monitor = new JLabel("目录检测");
        monitor.setBounds(title_left, 20, title_width, title_high);
        container.add(monitor);

        //开启目录检测
        startMonitor = new JRadioButton("开启",ConfigUtil.getMonitorFolder());
        startMonitor.setBounds(100, 20, radio_width, radio_high);
        startMonitor.setBackground(container.getBackground());
        startMonitor.addActionListener(this);
        container.add(startMonitor);
        //停止目录检测
        stopMonitor = new JRadioButton("关闭",!ConfigUtil.getMonitorFolder());
        stopMonitor.setBounds(250, 20, radio_width, radio_high);
        stopMonitor.setBackground(container.getBackground());
        stopMonitor.addActionListener(this);
        container.add(stopMonitor);
        //设置为单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(startMonitor);
        typeGroup.add(stopMonitor);

        //备份时间
        JLabel timeTask = new JLabel("备份时间");
        timeTask.setBounds(title_left, 60, title_width, title_high);
        container.add(timeTask);
        timeTaskText = new JTextField();
        if (StrUtil.isEmpty(ConfigUtil.getBackupTime())){
            timeTaskText.setForeground(Color.GRAY);
            timeTaskText.setText("请输入时分秒，如:[20:30:00]");
        }else {
            timeTaskText.setText(ConfigUtil.getBackupTime());
        }
        timeTaskText.setBounds(obj_left, 60, 200, text_high);
        timeTaskText.addFocusListener(this);
        container.add(timeTaskText);

        saveTime = new JButton("保存");
        saveTime.setBounds(305, 62, 60, 25);
        saveTime.addActionListener(this);
        container.add(saveTime);
        return container;
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
                ConfigUtil.set(CommonConstants.PATH,pathText.getText());
            }
        }else {
            //开始按钮
            if (e.getSource() == startBackup) {
                if (!checkText()){//验证
                    return;
                }
                startBackup.setEnabled(false);
                startBackup.setText("正在备份");
                CommonConstants.CLEAN_CONSOLE = 0;
                //获取用户输入的目录
                ConfigUtil.set(CommonConstants.PATH,pathText.getText());
                //获取用户输入的目录名称
                ConfigUtil.set(CommonConstants.BACKUP_NAME,folderText.getText());
                //获取上传模式
                ConfigUtil.set(CommonConstants.BACKUP_TYPE,puTongRadio.isSelected() ? "0" : "1");
                //输出模式
                CommonUI.console("备份模式：{}",(puTongRadio.isSelected() ? "普通模式" : "分类模式"));
                //执行上传文件操作
                try {
                    Thread backup = new Thread(() -> aliYunPanUtil.startBackup());
                    backup.start();
                } catch (Exception exc) {
                    CommonUI.console("遇到异常情况：{}",exc.toString());
                }
            }

            //退出按钮
            if (e.getSource() == logOut) {
                ConfigUtil.set(CommonConstants.REFRESH_TOKEN,"");
                this.setVisible(false);
                if (ObjectUtil.isNotEmpty(CommonConstants.monitor)) CommonConstants.monitor.close();
                new Login();
            }

            //普通模式
            if (e.getSource() == puTongRadio) {
                ConfigUtil.set(CommonConstants.BACKUP_TYPE, 0 + "");
            }

            //分类模式
            if (e.getSource() == fenLeiRadio) {
                ConfigUtil.set(CommonConstants.BACKUP_TYPE, 1 + "");
            }

            //开启目录监控
            if (e.getSource() == startMonitor) {
                startMonitorFolder();
                if (ConfigUtil.getInt(CommonConstants.MONITOR_FOLDER)==1){
                    JOptionPane.showMessageDialog(null, "已开启目录检测", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            //关闭目录监控
            if (e.getSource() == stopMonitor) {
                if (ObjectUtil.isNotEmpty(CommonConstants.monitor)) CommonConstants.monitor.close();
                ConfigUtil.set(CommonConstants.MONITOR_FOLDER,"0");
                JOptionPane.showMessageDialog(null, "已关闭目录检测", "提示", JOptionPane.INFORMATION_MESSAGE);
                CommonUI.console("关闭目录检测");
            }

            if (e.getSource().equals(saveTime)){
                String time = timeTaskText.getText();
                boolean contains = ReUtil.contains("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", time);
                if (contains){
                    ConfigUtil.set(CommonConstants.BACKUP_TIME,time);
                    CronTasks.startTimeBackupFile(time);
                    JOptionPane.showMessageDialog(null, "开启定时任务成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "请按格式输入定时备份时间", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }


        }
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
        JTextField tempJText = (JTextField) e.getSource();
        tempJText.setForeground(Color.BLACK);
        //文件目录
        if (tempJText == folderText) {
            //获取用户输入的目录名称
            ConfigUtil.set(CommonConstants.BACKUP_NAME,folderText.getText());
        }
        //定时时间
        if(tempJText == timeTaskText) {
            if (StrUtil.isEmpty(ConfigUtil.getBackupTime())){
                timeTaskText.setText("20:30:00");
            }
        }
    }

    //失去焦点的时候
    @Override
    public void focusLost(FocusEvent e) {
        JTextField tempJText = (JTextField) e.getSource();
        if (StrUtil.isEmpty(tempJText.getText())){
            tempJText.setForeground(Color.GRAY);
        }
        //文件目录
        if (tempJText == folderText) {
            //获取用户输入的目录名称
            ConfigUtil.set(CommonConstants.BACKUP_NAME,folderText.getText());
        }

        //定时时间
        if (tempJText == timeTaskText) {

        }
    }

    /**
     * 开启目录监控
     */
    public void startMonitorFolder() {
        if (!checkText()){//验证
            stopMonitor.setSelected(true);
            return;
        }else {
            ConfigUtil.set(CommonConstants.MONITOR_FOLDER,"1");
        }
        //开启目录检测 开始获取文件夹
        CommonUI.console("目录检测已开启");
        CommonConstants.monitor = WatchMonitor.createAll(ConfigUtil.getPath(), new DelayWatcher(new Watcher() {
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                String path = currentPath.toString();//文件路径
                String fileName = event.context().toString();//文件名
                String fileSuffix = FileUtil.getSuffix(fileName);//文件后缀
                //备份方法不执行时候执行监听
                if (!CommonConstants.BACK_STATE && fileSuffix.length()<=8 && !fileName.startsWith("~$") && !"tmp".equals(fileSuffix)){
                    String fileId = UploadLogUtil.getFileUploadFileId(path + FileUtil.FILE_SEPARATOR + fileName);
                    if (StrUtil.isNotEmpty(fileId)){
                        String filePath = path + FileUtil.FILE_SEPARATOR + fileName;
                        CommonUI.console("{} 发生变化，删除后上传新版",filePath);
                        aliYunPanUtil.deleteFile(fileId);//如果文件存在 先删除在重新上传
                        UploadLogUtil.removeFileUploadLog(filePath);//删除文件上传日志
                    }
                    aliYunPanUtil.monitorUpload(path,fileName);
                }
            }

            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {

            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {

            }
        }, 500));
        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        //监听所有目录
        CommonConstants.monitor.setMaxDepth(Integer.MAX_VALUE);
        //启动监听
        CommonConstants.monitor.start();
    }



}