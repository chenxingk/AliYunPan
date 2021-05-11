package xin.xingk.www;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AliYunPan extends JFrame implements ActionListener {

    // 日志界面
    private JTextArea logArea;
    // 根路径
    private JTextField rootPathFiled;
    // 项目路径
    private JTextField childPathFiled;
    // 需求路径
    private JTextField secondPathFiled;


    private JButton rootSelectbtn;
    private JButton childSelectbtn;
    private JButton secondSelectbtn;

    // 迭代一单选框
    private JRadioButton iterOneRadio;
    // 迭代二单选框
    private JRadioButton iterTwoRadio;

    // 开始备份
    private JButton startBackup;
    // 暂停备份
    private JButton pauseBackup;

    public AliYunPan(){
        initConfig();
        initUi();
        this.setVisible(true);
    }

    /**
     * 程序默认配置
     */
    private void initConfig() {
        //设置显示窗口标题
        setTitle("备份助手");
        //设置标题栏的图标为
        this.setIconImage(new ImageIcon("src/main/resources/logo.png").getImage());
        //设置窗口显示尺寸
        setSize(800,600);
        //窗口是否可以关闭
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //默认居中显示
        setLocationRelativeTo(null);
    }

    public void initUi() {
        Container container = getContentPane();
        container.setLayout(null);
        // 初始化控件
        JLabel rootPathLable = new JLabel("选择目录");
        rootPathFiled = new JTextField();
        JLabel childPathLable = new JLabel("阿里云Token");
        childPathFiled = new JTextField();
        //JLabel secondPathLable = new JLabel("备份目录名称");
        //secondPathFiled = new JTextField();

        // 路径选择区域
        JPanel northPanle = new JPanel();
        northPanle.setLayout(null);

        rootPathLable.setBounds(10, 20, 166, 25);
        container.add(rootPathLable);
        rootPathFiled.setBounds(80, 20, 560, 35);
        container.add(rootPathFiled);

        childPathLable.setBounds(10, 60, 166, 25);
        container.add(childPathLable);
        childPathFiled.setBounds(80, 60, 560, 35);
        container.add(childPathFiled);

        //secondPathLable.setBounds(10, 100, 166, 25);
        //container.add(secondPathLable);
        //secondPathFiled.setBounds(80, 100, 560, 35);
        //container.add(secondPathFiled);

        rootSelectbtn = new JButton("...");
        rootSelectbtn.setBounds(640, 25, 30, 25);
        rootSelectbtn.addActionListener(this);
        container.add(rootSelectbtn);

        iterOneRadio = new JRadioButton("普通备份");
        iterOneRadio.setBounds(100, 150, 100, 25);
        iterOneRadio.setBackground(container.getBackground());
        container.add(iterOneRadio);
        iterTwoRadio = new JRadioButton("分类备份", true);
        iterTwoRadio.setBounds(250, 150, 100, 25);
        iterTwoRadio.setBackground(container.getBackground());
        container.add(iterTwoRadio);

        startBackup = new JButton("开始备份");
        startBackup.setBounds(100, 195, 100, 30);
        startBackup.addActionListener(this);
        container.add(startBackup);

        pauseBackup = new JButton("暂停备份");
        pauseBackup.setBounds(250, 195, 100, 30);
        pauseBackup.addActionListener(this);
        container.add(pauseBackup);


        logArea = new JTextArea("运行日志...\n");
        logArea.setForeground(Color.white);
        logArea.setBackground(Color.BLACK);
        //启动自动换行
        logArea.setLineWrap(true);
        //换行不断字
        logArea.setWrapStyleWord(true);


        JScrollPane js = new JScrollPane(logArea);
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollBar jb = js.getVerticalScrollBar();
        jb.setSize(100,100);
        js.setBounds(0, 240, 800, 600);
        container.add(js);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }


    class MyDialog extends JDialog{
        public MyDialog(){
            this.setTitle("测试");
            //设置弹窗可见
            this.setVisible(true);
            //设置弹出大小
            this.setBounds(100,100,500,500);
            //容器
            Container container = this.getContentPane();
            container.add(new JLabel("我最牛逼"));
        }
    }

}