package xin.xingk.www;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGUtils;

import javax.swing.*;

/**
 * @Author: 陈靖杰
 * @Date: 2022/2/9 14:08
 * @Description:
 */
public class MainFrame extends JFrame {

    public void init() {
        this.setTitle("扫码登录");
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/logo.svg"));
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        UIManager.put( "TitlePane.unifiedBackground", false );
//        FlatLaf.updateUI();
        setJMenuBar(topMenuBar);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.3, 0.4);
    }
}
