package xin.xingk.www;

import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.MainFrame;
import xin.xingk.www.util.FileUtil;
import xin.xingk.www.util.UIUtil;
import xin.xingk.www.util.UploadLogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 备份程序
 *
 */
@Slf4j
public class App {
    public static MainFrame mainFrame;


    public static void main( String[] args ) {
        UIUtil.initTheme();
        mainFrame = new MainFrame();
        mainFrame.init();
        //删除旧的运行日志
        FileUtil.del(UploadLogUtil.RUN_LOG);
        //检查是否有更新
        //if (checkForUpdate()) return;
        //boolean login = new AliYunPanUtil().getAliYunPanInfo();
        if(true){
            mainFrame.initHome();
            Home.initUi();
            mainFrame.add(Home.getInstance().getHomePanel());
//            mainFrame.add(About.getInstance().getAboutPanel());
        }else{
            mainFrame.initLogin();
            mainFrame.add(Login.getInstance().getLoginPanel());
            Login.getInstance().initUi();
        }
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getWidth() <= 1366) {
            // The window is automatically maximized at low resolution
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    /**
     * 检查更新
     */
    private static boolean checkForUpdate() {
        String result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
        while (StrUtil.isEmpty(result)){
            result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
        }
        JSONObject versionJson = JSONUtil.parseObj(result);
        String url = versionJson.getStr("url");
        String desc = versionJson.getStr("desc");
        double version = (double) versionJson.get("version");
        int update = versionJson.getInt("update");
        if (version > CommonConstants.VERSION){//检测到有新版
            CommonUI.setFont();//设置字体
            int button = JOptionPane.showConfirmDialog(null, desc, "检测到有新版，是否更新？", JOptionPane.YES_NO_OPTION);
            if (button==0){//选择是打开浏览器
                DesktopUtil.browse(url);
                return true;
            }else {
                if (update==1){//强更新
                    System.exit(0);
                }
            }
        }
        return false;
    }

}
