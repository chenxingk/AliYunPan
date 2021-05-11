package xin.xingk.www.common;

import javax.swing.*;
import java.awt.*;

public class MyConsole extends JTextArea {
    public MyConsole() {
        String info="温馨提示：备份目录名称是要备份到阿里云盘那个目录下(不存在则自动创建)\n";
        info+="如有问题可以联系作者微信：chen151363，QQ：850222153\n";
        info+="运行日志...\n";
        this.setText(info);
        this.setForeground(Color.white);
        this.setBackground(Color.BLACK);
        //启动自动换行
        this.setLineWrap(true);
        //换行不断字
        this.setWrapStyleWord(true);
        this.setVisible(true);
    }
}
