package xin.xingk.www;


import com.formdev.flatlaf.FlatDarkLaf;


/**
 * 测试类
 */
public class Test{

    public static MainFrame mainFrame;

    public static void main(String[] args)  {
        FlatDarkLaf.setup();
        mainFrame = new MainFrame();
        mainFrame.init();
        mainFrame.setContentPane(Qcode.getInstance().getCode());
        Qcode.getInstance().init();
        mainFrame.pack();
        mainFrame.setVisible(true);
    }



}