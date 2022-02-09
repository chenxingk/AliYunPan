package xin.xingk.www;


import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.kitfox.svg.Style;

import javax.swing.*;


/**
 * 测试类
 */
public class Test{

    public static MainFrame mainFrame;

    public static void main(String[] args)  {
        FlatDarculaLaf.setup();
        mainFrame = new MainFrame();
        mainFrame.init();
        mainFrame.pack();
        mainFrame.setVisible(true);
    }



}