package xin.xingk.www.ui;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.ui.menu.TopMenuBar;
import xin.xingk.www.util.ComponentUtil;
import xin.xingk.www.util.ConfigUtil;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author: Mr.chen
 * @date: 2022/2/12 11:54
 * @description:
 */
@Slf4j
public class MainFrame extends JFrame {

    public MainFrame(){
        //设置图标
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/logo.svg"));
        // 窗口最小化事件
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                //System.exit(0);
            }
            public void windowIconified(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    public void initTopMenuBar() {
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
    }

    public void initUpdate() {
        this.initTopMenuBar();
        this.setTitle("检测更新");
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.4);
        this.add(Login.getInstance().getLoginPanel());
        this.remove(Home.getInstance().getHomePanel());
    }

    public void initLogin() {
        this.initTopMenuBar();
        this.setTitle("扫码登录");
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.4);
        this.add(Login.getInstance().getLoginPanel());
        this.remove(Home.getInstance().getHomePanel());
        Login.initQrCode();
    }

    public void initHome() {
        this.initTopMenuBar();
        this.setTitle(CommonConstants.TITLE +"，欢迎您："+ ConfigUtil.getName());
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.6, 0.8);
        this.add(Home.getInstance().getHomePanel());
        this.remove(Login.getInstance().getLoginPanel());
        Home.initTableData();
//        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.5);
    }
}
