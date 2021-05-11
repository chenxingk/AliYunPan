package xin.xingk.www;

import cn.hutool.core.util.StrUtil;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.MyConsole;
import xin.xingk.www.common.utils.AliYunPanUtil;
import xin.xingk.www.common.utils.FileUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;


public class AliYunPan extends JFrame implements ActionListener {

    private static final int obj_left = 100;//左边距
    private static final int text_width = 400;//文本框框
    private static final int text_high = 30;//文本框高
    private static final int title_left = 10;//标题左边距
    private static final int title_width = 166;//标题宽
    private static final int title_high = 25;//标题高
    private static final int radio_top = 150;//标题高
    private static final int radio_width = 100;//标题高
    private static final int radio_high = 25;//标题高

    //阿里云工具类
    private AliYunPanUtil aliYunPanUtil=new AliYunPanUtil();
    // 日志界面
    MyConsole console = CommonConstants.console;
    // 备份目录
    private JTextField pathText;
    // Token
    private JTextField tokenText;
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
        //设置标题栏的图标为
        this.setIconImage(new ImageIcon("src/main/resources/logo.png").getImage());
        //设置窗口显示尺寸
        setSize(800,600);
        //窗口是否可以关闭
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //默认居中显示
        setLocationRelativeTo(null);
        //禁止改变窗口大小
        this.setResizable(false);
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
        tokenText = new JTextField();
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
        pathText.setBounds(obj_left, 20, text_width, text_high);
        container.add(pathText);

        //token框
        tokenTitle.setBounds(title_left, 60, title_width, title_high);
        container.add(tokenTitle);
        tokenText.setBounds(obj_left, 60, text_width, text_high);
        container.add(tokenText);

        //目录名称框
        folderTitle.setBounds(title_left, 100, title_width, title_high);
        container.add(folderTitle);
        folderText.setBounds(obj_left, 100, text_width, text_high);
        container.add(folderText);

        //选择文件按钮
        selectBtn = new JButton("选择...");
        selectBtn.setBounds(501, 22, 90, 25);
        selectBtn.addActionListener(this);
        container.add(selectBtn);

        //模式选择
        puTongRadio = new JRadioButton("普通备份",true);
        puTongRadio.setBounds(100, radio_top, radio_width, radio_high);
        puTongRadio.setBackground(container.getBackground());
        container.add(puTongRadio);
        fenLeiRadio = new JRadioButton("分类备份");
        fenLeiRadio.setBounds(250, radio_top, radio_width, radio_high);
        fenLeiRadio.setBackground(container.getBackground());
        container.add(fenLeiRadio);
        //设置为单选
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(puTongRadio);
        typeGroup.add(fenLeiRadio);


        startBackup = new JButton("开始备份");
        startBackup.setBounds(100, 195, 100, 30);
        startBackup.addActionListener(this);
        container.add(startBackup);
        pauseBackup = new JButton("暂停备份");
        pauseBackup.setBounds(250, 195, 100, 30);
        pauseBackup.addActionListener(this);
        container.add(pauseBackup);

        //日志面板
        JScrollPane consolePane = new JScrollPane(console);
        consolePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollBar jb = consolePane.getVerticalScrollBar();
        jb.setSize(100,100);
        consolePane.setBounds(-1, 240, 800, 600);
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
            }
        }else {
            if (!checkText()){//验证
                return;
            }

            //开始按钮
            if (e.getSource() == startBackup) {
                if (tokenText.getText().length()!=32){
                    JOptionPane.showMessageDialog(null, "您输入的token不正确", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //获取用户输入的token
                CommonConstants.REFRESH_TOKEN=tokenText.getText();
                //获取用户输入的目录
                CommonConstants.PATH=pathText.getText();
                //获取用户输入的目录名称
                CommonConstants.BACK_NAME=folderText.getText();
                //获取上传模式
                CommonConstants.BACK_TYPE = puTongRadio.isSelected() ? 0 : 1;
                //输出模式
                console.append("备份模式："+(puTongRadio.isSelected() ? "普通模式" : "分类模式")+"\n");
                //执行上传文件操作
                aliYunPanUtil.startBackup();
            }

            //暂停按钮
            if (e.getSource() == pauseBackup) {
                JOptionPane.showMessageDialog(null, "此功能正在开发中...", "提示", JOptionPane.INFORMATION_MESSAGE);
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
        return true;
    }

}