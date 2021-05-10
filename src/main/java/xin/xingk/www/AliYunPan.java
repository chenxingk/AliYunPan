package xin.xingk.www;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AliYunPan extends JFrame implements Icon {

    public AliYunPan() throws HeadlessException {
        setTitle("备份小助手-星空");    //设置显示窗口标题
        // 设置标题栏的图标为
        this.setIconImage(new ImageIcon("src/main/resources/logo.png").getImage());
        setSize(600,350);    //设置窗口显示尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        setVisible(true);   //设置窗口是否可见
        setLocationRelativeTo(null);    //默认居中显示

        //放东西，容器
        Container container = getContentPane();
        //绝对定位
        container.setLayout(null);

        //按钮
        JButton button = new JButton("点击弹出一个对话框");//创建
        button.setBounds(30,30,200,50);

        //点击这个按钮的时候,弹出一个弹窗
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MyDialog();
            }
        });

        container.add(button);

    }


    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

    }

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
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