package xin.xingk.www.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * @author: Mr.chen
 * @date: 2021/11/27 14:03
 * @description: UI公共组件
 */
public class CommonUI {
    //日志面板
    public static JTextArea console = initLogArea();
    public static JScrollPane consolePane = new JScrollPane(console);
    public static JScrollBar scrollBar = consolePane.getVerticalScrollBar();
    // Token文本框
    public static JTextField tokenText = new JTextField();
    //开始备份按钮
    public static JButton startBackup = new JButton("开始备份");

    /**
     * 设置字体
     */
    public static void setFont() {
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 13));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

    /**
     * 添加控制台日志
     * @param text
     */
    public static void console(String text,Object... params){
        String date="["+DateUtil.now()+"] ";
        if (!CommonConstants.IS_CONSOLE){//不打印日志
            return;
        }
        if (CommonConstants.CLEAN_CONSOLE==0){
            console.setText(date+"开始运行"+"\n");
        }else {
            String format = StrUtil.format(date+text,params);
            console.append(format+"\n");
            console.setCaretPosition(console.getText().length());
            //console.selectAll();
            //console.paintImmediately(console.getBounds());
            //scrollBar.setValue(scrollBar.getMaximum());
        }
    }


    /**
     * 修改开始按钮状态
     * @param text 按钮名称 开始备份 正在备份
     * @param enabled 按钮状态
     */
    public static void modifyStartBtnStatus(String text,Boolean enabled) {
        startBackup.setText(text);
        startBackup.setEnabled(enabled);
    }

    /**
     * 初始化日志面板
     * @return
     */
    public static JTextArea initLogArea() {
        JTextArea logArea = new JTextArea();
        String info="温馨提示：云盘备份目录是要备份到阿里云盘那个目录下(不存在则自动创建)\n";
        info+="普通备份：会按本地目录结构上传文件\n";
        info+="分类备份：会将本地文件按文档、压缩包、软件、音乐、图片、视频等分类上传\n";
        info+="目录检测：开启目录检测后，会根据文件变更实时备份\n";
        info+="备份时间：输入备份时间后会根据指定时间定时备份\n";
        info+="设置开机启动教程：https://gitee.com/xingk-code/AliYunPan/wikis/pages\n";
        info+="如有问题可以联系作者微信：chen151363，QQ：850222153，反馈问题Q群：878678008\n";
        info+="运行日志...\n";
        logArea.setText(info);
        logArea.setForeground(Color.white);
        logArea.setBackground(Color.BLACK);
        //启动自动换行
        logArea.setLineWrap(true);
        //换行不断字
        logArea.setWrapStyleWord(true);
        logArea.setVisible(true);
        return logArea;
    }

}
