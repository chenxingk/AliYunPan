package xin.xingk.www.ui;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.json.JSONObject;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lombok.Data;
import xin.xingk.www.App;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.context.UserContextHolder;
import xin.xingk.www.util.AliYunUtil;
import xin.xingk.www.util.ComponentUtil;
import xin.xingk.www.util.OkHttpUtil;
import xin.xingk.www.util.UpdateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: Mr.chen
 * @date: 2022/2/11 23:07
 * @description: 登录窗口
 */
@Data
public class Login {
    //登录窗口面板
    private JPanel loginPanel;
    //二维码
    private JLabel qrCodeLabel;
    //提示
    private JLabel tipsLabel;
    //说明
    private JLabel infoLabel;

    //CK码
    private static String ck;
    //时间戳
    private static String t;

    //当前对象
    private static Login login;

    //初始化对象
    public static Login getInstance() {
        if (login == null) {
            login = new Login();
        }
        return login;
    }

    /**
     * 初始化窗口UI
     */
    public static void initUpdate() {
        login = getInstance();
        login.getInfoLabel().setText("");
        login.getQrCodeLabel().setText("检测更新……");
        CommonConstants.LOGIN_STATUS = AliYunUtil.login();
        Home.initUi();
        //检查是否有更新
        if (UpdateUtil.checkForUpdate()) return;

        if (CommonConstants.LOGIN_STATUS) {
            login.getQrCodeLabel().setText("正在登录……");
            App.mainFrame.initHome();
        } else {
            login.getQrCodeLabel().setText("加载二维码……");
            App.mainFrame.initLogin();
        }
    }


    /**
     * 初始化二维码
     */
    public static void initQrCode() {
        login = getInstance();
        login.getTipsLabel().setFont(FlatUIUtils.nonUIResource(UIManager.getFont("small.font")));
        login.getInfoLabel().setText("注：请使用阿里云盘APP，扫描二维码");
        //获取二维码
        getQrCodeImg();
        //定时刷新二维码
        new Timer().schedule(new TimerTask() {
            public void run() {
                JLabel tipsLabel = login.getTipsLabel();
                JSONObject qrCode = OkHttpUtil.queryQrCode(t, ck);
                String status = qrCode.getJSONObject("content").getJSONObject("data").getStr("qrCodeStatus");
                if ("CONFIRMED".equals(status)) {
                    try {
                        JSONObject json = OkHttpUtil.doLogin(qrCode);
                        if (!checkLoginJson(json)) return;
                        String refreshToken = json.getStr("refresh_token");
                        if (StrUtil.isNotEmpty(refreshToken)) {
                            tipsLabel.setText("登录成功，正在跳转中，请稍后...");
                            UserContextHolder.updateUserToken(refreshToken);
                            this.cancel();
                            CommonConstants.LOGIN_STATUS = true;
                            App.mainFrame.initHome();
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, exc.getMessage(), "二维码验证错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                //二维码过期 刷新二维码
                if ("EXPIRED".equals(status)) {
                    getQrCodeImg();
                }
                if ("SCANED".equals(status)) {
                    tipsLabel.setText("已扫描二维码，等待确认...（确认后需等待1-2s）");
                }
            }
        }, 0, 500);
    }

    /**
     * 验证JSON 是否有误
     *
     * @param json
     * @return
     */
    private static boolean checkLoginJson(JSONObject json) {
        if (ObjectUtil.isNotNull(json.getJSONObject("content"))) {
            String titleMsg = json.getJSONObject("content").getJSONObject("data").getStr("titleMsg");
            if (StrUtil.isNotEmpty(titleMsg)) {
                JOptionPane.showMessageDialog(null, titleMsg, "异常错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String redirectUrl = json.getJSONObject("content").getJSONObject("data").getStr("iframeRedirectUrl");
            if (StrUtil.isNotEmpty(redirectUrl)) {//异常信息返回
                JOptionPane.showMessageDialog(null, "请您先在电脑网页登录成功后，再登录本软件", "登录错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取二维码图片
     */
    private static void getQrCodeImg() {
        login = getInstance();
        JSONObject qrCodeUrl = OkHttpUtil.getQrCodeUrl();
        //二维码地址
        String codeContent = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("codeContent");
        ck = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("ck");
        t = qrCodeUrl.getJSONObject("content").getJSONObject("data").getStr("t");
        int qrCodeSize = (int) (ComponentUtil.screenHeight * 0.25);
        byte[] qrCode = QrCodeUtil.generatePng(codeContent, qrCodeSize, qrCodeSize);
        //二维码图片
        ImageIcon qrCodeImg = new ImageIcon(qrCode);
        JLabel codeLabel = login.getQrCodeLabel();
        codeLabel.setIcon(qrCodeImg);
        codeLabel.setText(null);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        qrCodeLabel = new JLabel();
        qrCodeLabel.setIcon(new ImageIcon(getClass().getResource("/icons/loading_dark.gif")));
        qrCodeLabel.setText("Loading……");
        loginPanel.add(qrCodeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tipsLabel = new JLabel();
        tipsLabel.setText("");
        loginPanel.add(tipsLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoLabel = new JLabel();
        infoLabel.setText("注：请使用阿里云盘APP，扫描二维码");
        loginPanel.add(infoLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return loginPanel;
    }

}
