package xin.xingk.www.common;

import javax.swing.*;
import java.awt.*;

/**
 * Description: 模拟控制台类
 * Author: 陈靖杰
 * Date: 2021/05/12
 */
public class MyConsole extends JTextArea {
    public MyConsole() {
        String info="温馨提示：备份目录名称是要备份到阿里云盘那个目录下(不存在则自动创建)\n";
        info+="普通备份：会按本地目录结构上传文件\n";
        info+="分类备份：会将本地文件按文档、压缩包、软件、音乐、图片、视频等分类上传\n";
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
