package xin.xingk.www.ui;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import xin.xingk.www.ui.menu.TopMenuBar;
import xin.xingk.www.util.ComponentUtil;

import javax.swing.*;

/**
 * @author: Mr.chen
 * @date: 2022/2/12 11:54
 * @description:
 */
public class MainFrame extends JFrame {

    public void init() {
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/logo.svg"));
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
    }

    public void initLogin() {
        this.setTitle("扫码登录");
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.4);
    }

    public void initHome() {
        this.setTitle("备份助手V1.3，欢迎您：xxx");
//        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.6, 0.8);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.5);
    }
}
