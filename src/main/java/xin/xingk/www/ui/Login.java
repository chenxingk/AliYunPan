package xin.xingk.www.ui;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
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
public class Login extends JFrame{

    //二维码面板
    private Container qrCodeLogin;
    //二维码
    private JLabel qrCodeLab;
    //二维码
    private ImageIcon qrCodeImg;
    //二维码地址
    private String codeContent;
    //CK码
    private String ck;
    //时间戳
    private String t;
    //说明
    private JLabel info;
    //二维码定时刷新
    Timer qrTimer;

    //TAB面板
    private JTabbedPane mainTab = new JTabbedPane();

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
        //二维码登录
        qrCodeLogin = new Container();
        qrCodeImg=new ImageIcon(getClass().getResource("/images/logo.png"));

        qrCodeLab = new JLabel(qrCodeImg);
        qrCodeLab.setBounds(92, 3, 180, 180);
        qrCodeLogin.add(qrCodeLab);

        info=new JLabel("注：请使用阿里云盘APP，扫描二维码");
        info.setBounds(10, 185, 380, 25);
        qrCodeLogin.add(info);
        mainTab.add("二维码登录", qrCodeLogin);
        // 配置界面
        this.setContentPane(mainTab);

        getQrCodeImg();
        qrTimer = new Timer();
        qrTimer.schedule(new TimerTask() {
            public void run() {
                JSONObject qrCode = OkHttpUtil.queryQrCode(t, ck);
                String status = qrCode.getJSONObject("content").getJSONObject("data").getStr("qrCodeStatus");
                if ("CONFIRMED".equals(status)){
                    try {
                        JSONObject json = OkHttpUtil.doLogin(qrCode);
                        if (!checkLoginJson(json)) return;
                        String refreshToken = json.getStr("refresh_token");
                        if (StrUtil.isNotEmpty(refreshToken)){
                            CommonConstants.REFRESH_TOKEN = refreshToken;
                            info.setText("登录成功，正在跳转中，请稍后...");
                            setting.set("tokenText",refreshToken);
                            setting.store();
                            setVisible(false);
                            this.cancel();
                            new AliYunPan();
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                //二维码过期 刷新二维码
                if ("EXPIRED".equals(status)){
                    getQrCodeImg();
                }
                if ("SCANED".equals(status)){
                    info.setText("已扫描二维码，等待确认...（确认后需等待1-2s）");
                }
            }
        }, 0, 500);
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

    /**
     * 获取二维码图片
     * @throws Exception
     */
    private void getQrCodeImg() {
        try {
            JSONObject qrCodeUrl = OkHttpUtil.getQrCodeUrl();
            codeContent = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("codeContent");
            ck = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("ck");
            t = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("t");
            byte[] qrCode = QrCodeUtil.generatePng(codeContent, 180, 180);
            qrCodeImg=new ImageIcon(qrCode);
            qrCodeLab.setIcon(qrCodeImg);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}