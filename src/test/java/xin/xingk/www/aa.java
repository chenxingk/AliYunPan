package xin.xingk.www;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;



public class aa extends JFrame {

    private static final long serialVersionUID = 1L;
    private static int i = 0;
    static int runTime = 0;
    static TextArea ta = new TextArea();
    static boolean regStatus = false;
    private static TrayIcon trayIcon = null;
    static JFrame mf = new JFrame();
    static SystemTray tray = SystemTray.getSystemTray();

    public static void myFrame() { // 窗体

        mf.setLocation(300, 100);
        mf.setSize(500, 300);
        mf.setTitle("XXXX系统");
        mf.setLayout(new BorderLayout());

        ImageIcon ll = new ImageIcon(JFrame.class.getClass().getResource("/images/logo.png"));//在JFrame中使用图片
        JLabel i = new JLabel(ll);

        JLabel j = new JLabel("XXXX系统v2.02", SwingConstants.CENTER);//设置标签，显示文本居中
        JLabel k = new JLabel("软件开发,All Rights Reserved", SwingConstants.CENTER);//设置标签，显示文本居中
        j.setFont(new java.awt.Font("", 1, 18));//设置标签J显示字体

        Panel p1 = new Panel();//实例化面板P1
        p1.setLayout(new BorderLayout());//设置面板P1中控件排列方式

        final Panel p11 = new Panel();
        p11.setLayout(new BorderLayout());//设置P11控件排列方式
        p11.add(j, BorderLayout.NORTH);//P11上方显示控件J
        p11.add(k, BorderLayout.SOUTH);//P11下方显示控件K

        final JLabel t = new JLabel("",SwingConstants.CENTER);//设置标签t用于显示时钟
        t.setFont(new java.awt.Font("", 1, 26));//设置标签t字体
        t.setForeground(Color.blue);//设置标签t前景色彩

        p11.add(t,BorderLayout.CENTER);//标签t显示在中间位置

        Timer timer_date = new Timer(1000,  new ActionListener(){ //设置数字时钟
            public void actionPerformed(ActionEvent evt) {
                t.setText( new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date()));
            }//actionPerformed
        }); //Timer
        timer_date.start();


        p1.add(i, BorderLayout.WEST);//左侧图标
        p1.add(p11, BorderLayout.CENTER);//p11在P1中间排列

        mf.add(p1, BorderLayout.NORTH);//将p1显示在窗口上方

        mf.add(ta, BorderLayout.CENTER);//将一个多行文本区域显示在文体中间

        mf.setVisible(true);//使窗口可见

        mf.addWindowListener(new WindowAdapter() { // 窗口关闭事件
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            };//windowClosing

            public void windowIconified(WindowEvent e) { // 窗口最小化事件

                mf.setVisible(false);
                aa.miniTray();

            }//windowIconified

        });//addWindowListener

    }//myFrame

    private static void miniTray() {  //窗口最小化到任务栏托盘

        ImageIcon trayImg = new ImageIcon("c://tyjkdb//leida.gif");//托盘图标

        PopupMenu pop = new PopupMenu();  //增加托盘右击菜单
        MenuItem show = new MenuItem("还原");
        MenuItem exit = new MenuItem("退出");

        show.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) { // 按下还原键

                tray.remove(trayIcon);
                mf.setVisible(true);
                mf.setExtendedState(JFrame.NORMAL);
                mf.toFront();
            }//actionPerformed

        });//addActionListener

        exit.addActionListener(new ActionListener() { // 按下退出键

            public void actionPerformed(ActionEvent e) {

                tray.remove(trayIcon);
                System.exit(0);

            }//actionPerformed

        });//addActionListener

        pop.add(show);
        pop.add(exit);

        trayIcon = new TrayIcon(trayImg.getImage(), "xxxx系统", pop);
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) { // 鼠标器双击事件

                if (e.getClickCount() == 2) {

                    tray.remove(trayIcon); // 移去托盘图标
                    mf.setVisible(true);
                    mf.setExtendedState(JFrame.NORMAL); // 还原窗口
                    mf.toFront();
                }//if

            }//mouseClicked

        });//addMouseListener

        try {

            tray.add(trayIcon);

        } catch (AWTException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }//try

    }//miniTray

    public static void main(String[] args) {

        new aa();
        aa.myFrame();

    }//main
}//ssss