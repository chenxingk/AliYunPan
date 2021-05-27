package xin.xingk.www.ui;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.Setting;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.utils.OkHttpUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录-UI
 */
public class Login extends JFrame implements ActionListener {

    private static final int title_left = 10;//标题左边距
    private static final int title_width = 166;//标题宽
    private static final int title_high = 25;//标题高

    private static final int input_left = 60;//输入框左边距
    private static final int input_width = 200;//输入框宽
    private static final int input_high = 35;//输入框高

    private static final int user_top = 20;//用户名上边距
    private static final int pass_top = 60;//密码上边距

    private static final int login_left = 80;//密码上边距
    private static final int login_top = 130;//密码上边距
    private static final int login_width = 60;//密码上边距
    private static final int login_high = 30;//密码上边距

    private static final int reset_left = 180;//密码上边距
    private static final int reset_top = 130;//密码上边距
    private static final int reset_width = 60;//密码上边距
    private static final int reset_high = 30;//密码上边距

    private static final int info_left = 10;//密码上边距
    private static final int info_top = 180;//密码上边距
    private static final int info_width = 380;//密码上边距
    private static final int info_high = 25;//密码上边距


    //用户名
    private JTextField userText;
    //密码
    private JPasswordField passText;
    //说明
    private JLabel info;
    //手机号
    private JTextField phoneText;
    //验证码
    private JTextField codeText;

    //登录按钮
    private JButton loginBtn;
    private JButton smsLoginBtn;
    //重置按钮
    private JButton resetBtn;
    private JButton smsResetBtn;
    // 发送验证码按钮
    private JButton smsSendBtn;

    //TAB面板
    private JTabbedPane mainTab = new JTabbedPane();

    //验证码倒计时
    int second = 60;
    //配置文件
    Setting setting = CommonConstants.setting;




    public Login() {
        initConfig();
        initUi();
        this.setVisible(true);
    }

    private void initConfig() {
        // 设置界面使用字体
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体", Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }

        setSize(380, 270);
        setTitle("备份助手-登录");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置标题栏的图标
        setIconImage(new ImageIcon(getClass().getResource("/images/logo.png")).getImage());
        //默认居中显示
        setLocationRelativeTo(null);
    }

    public void initUi() {
        Container passLogin = new Container();

        //账号密码登录
        JLabel user = new JLabel("账号");
        userText = new JTextField();
        JLabel passWord = new JLabel("密码");
        passText = new JPasswordField();

        info=new JLabel("注：请输入您的阿里云账号密码！by QQ:850222153 有bug请反馈");
        info.setBounds(info_left, info_top, info_width, info_high);
        passLogin.add(info);

        user.setBounds(title_left, user_top, title_width, title_high);
        passLogin.add(user);
        userText.setBounds(input_left, user_top, input_width, input_high);
        userText.setText(setting.getStr("userName"));
        passLogin.add(userText);

        passWord.setBounds(title_left, pass_top, title_width, title_high);
        passLogin.add(passWord);
        passText.setBounds(input_left, pass_top, input_width, input_high);
        passText.setText(setting.getStr("password"));
        passLogin.add(passText);

        loginBtn = new JButton("登录");
        loginBtn.setBounds(login_left, login_top, login_width, login_high);
        loginBtn.addActionListener(this);
        passLogin.add(loginBtn);

        resetBtn = new JButton("重置");
        resetBtn.setBounds(reset_left, reset_top, reset_width, reset_high);
        resetBtn.addActionListener(this);
        passLogin.add(resetBtn);

        mainTab.add("账号登录", passLogin);
        Container smsLogin = new Container();

        //手机号登录
        JLabel phone = new JLabel("手机号");
        phoneText = new JTextField();
        JLabel code = new JLabel("验证码");
        codeText = new JTextField();

        phone.setBounds(title_left, user_top, title_width, title_high);
        smsLogin.add(phone);
        phoneText.setBounds(input_left, user_top, input_width, input_high);
        phoneText.setText(setting.get("phone"));
        smsLogin.add(phoneText);

        code.setBounds(title_left, pass_top, title_width, title_high);
        smsLogin.add(code);
        codeText.setBounds(input_left, pass_top, input_width, input_high);
        smsLogin.add(codeText);

        smsSendBtn = new JButton("获取验证码");
        smsSendBtn.setFont(new Font("宋体", Font.PLAIN, 12));
        smsSendBtn.setBounds(265, 23, 100, 25);
        smsSendBtn.addActionListener(this);
        smsLogin.add(smsSendBtn);

        smsLoginBtn = new JButton("登录");
        smsLoginBtn.setBounds(login_left, login_top, login_width, login_high);
        smsLoginBtn.addActionListener(this);
        smsLogin.add(smsLoginBtn);

        smsResetBtn = new JButton("重置");
        smsResetBtn.setBounds(reset_left, reset_top, reset_width, reset_high);
        smsResetBtn.addActionListener(this);
        smsLogin.add(smsResetBtn);

        info=new JLabel("注：请输入您的阿里云手机号！by 微信:chen151363 有bug请反馈");
        info.setBounds(info_left, info_top, info_width, info_high);
        smsLogin.add(info);
        mainTab.add("手机登录", smsLogin);

        // 配置界面
        this.setContentPane(mainTab);
    }

    public void actionPerformed(ActionEvent e) {
        //mainTab.getSelectedIndex() 0 是账号密码 1验证码
        //账号密码登录
        if (e.getSource() == loginBtn) {
            doLogin();
        }
        //发送验证码
        if (e.getSource() == smsSendBtn) {
            smsSend();
        }
        //验证码登录
        if (e.getSource() == smsLoginBtn) {
            codeLogin();
        }
        //账号密码重置
        if (e.getSource() == resetBtn) {
            userText.setText("");
            passText.setText("");
        }
        //手机号重置
        if (e.getSource() == resetBtn) {
            phoneText.setText("");
            codeText.setText("");
        }
    }



    /**
     * 账号密码登录方法
     */
    private void doLogin() {
        String userName = userText.getText();
        String password = passText.getText();
        if (StrUtil.isEmpty(userName) || StrUtil.isEmpty(password)){
            JOptionPane.showMessageDialog(null, "账号或密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JSONObject json = OkHttpUtil.passwordLogin(userText.getText(), passText.getText());
            if (!checkLoginJson(json)) return;
            String refreshToken = json.getStr("refresh_token");
            System.out.println(json);
            if (StrUtil.isNotEmpty(refreshToken)){
                CommonConstants.REFRESH_TOKEN = refreshToken;
                setting.set("tokenText",refreshToken);
                setting.set("userName",userName);
                setting.set("password",password);
                setting.store(CommonConstants.CONFIG_PATH);
                setVisible(false);
                new AliYunPan();
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "登录遇到异常", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * 发送验证码
     */
    private void smsSend() {
        String phone = phoneText.getText();
        if (!Validator.isMobile(phone)){
            JOptionPane.showMessageDialog(null, "请输入正确手机号", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            //发送验证码
            String cookie = OkHttpUtil.getCookie();
            JSONObject json = OkHttpUtil.smsSend(phone,cookie);
            System.out.println(json.toString());
            String smsToken = json.getJSONObject("content").getJSONObject("data").getStr("smsToken");
            if (StrUtil.isNotEmpty(smsToken)){
                setting.set("phone", phone);
                setting.set("smsToken",smsToken);
                setting.set("Cookie",cookie);
                setting.store(CommonConstants.CONFIG_PATH);
            }else{
                String titleMsg = json.getJSONObject("content").getJSONObject("data").getStr("titleMsg");
                if (StrUtil.isNotEmpty(titleMsg)){
                    JOptionPane.showMessageDialog(null, titleMsg, "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "发送验证码失败，请稍后重试", "错误", JOptionPane.ERROR_MESSAGE);
        }
        smsSendBtn.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                smsSendBtn.setText(second+"秒后重发");
                second--;
                if (second==0){
                    this.cancel();
                    second = 60;
                    smsSendBtn.setText("获取验证码");
                    smsSendBtn.setEnabled(true);
                }
            }
        }, 0, 1000);
    }

    /**
     * 验证码登录
     */
    private void codeLogin() {
        String phone = phoneText.getText();
        String code = codeText.getText();
        if (!Validator.isMobile(phone) || (Validator.isNumber(code) && code.length()!=6)){
            JOptionPane.showMessageDialog(null, "手机号或验证码为空\n或格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (StrUtil.isEmpty(setting.getStr("smsToken"))){
            JOptionPane.showMessageDialog(null, "请先获取验证码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            //验证码登录
            JSONObject json = OkHttpUtil.smsLogin(phone, setting.getStr("smsToken"), code,setting.getStr("Cookie"));
            System.out.println(json.toString());
            if (!checkLoginJson(json)) return;
            String refreshToken = json.getStr("refresh_token");
            if (StrUtil.isNotEmpty(refreshToken)){
                CommonConstants.REFRESH_TOKEN = refreshToken;
                setting.set("tokenText",refreshToken);
                setting.set("smsToken","");
                setting.set("Cookie","");
                setting.store(CommonConstants.CONFIG_PATH);
                setVisible(false);
                new AliYunPan();
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, exc.toString(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * 验证JSON 是否有误
     * @param json
     * @return
     */
    private boolean checkLoginJson(JSONObject json) {
        if (ObjectUtil.isNotNull(json.getJSONObject("content"))) {
            String titleMsg = json.getJSONObject("content").getJSONObject("data").getStr("titleMsg");
            if (StrUtil.isNotEmpty(titleMsg)) {
                JOptionPane.showMessageDialog(null, titleMsg, "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String redirectUrl = json.getJSONObject("content").getJSONObject("data").getStr("iframeRedirectUrl");
            if (StrUtil.isNotEmpty(redirectUrl)) {//异常信息返回
                JOptionPane.showMessageDialog(null, "请您先在电脑网页登录成功后，再登录本软件", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}