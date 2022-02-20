package xin.xingk.www;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CronTasks;
import xin.xingk.www.common.DirWatcher;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.MainFrame;
import xin.xingk.www.util.AliYunUtil;
import xin.xingk.www.util.UIUtil;

import javax.swing.*;

/**
 * 备份程序
 *
 */
@Slf4j
public class App {
    public static MainFrame mainFrame;


    public static void main( String[] args ) {
        UIUtil.initTheme();
        CommonConstants.LOGIN_STATUS = AliYunUtil.login();
        mainFrame = new MainFrame();
        Home.initUi();
        Login.initUi();
        //检查是否有更新
        if (checkForUpdate()) return;
        if(CommonConstants.LOGIN_STATUS){
            mainFrame.initHome();
        }else{
            mainFrame.initLogin();
        }

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

        /**
         * 并发点...
         */
        //开启定时任务
        ThreadUtil.execute(CronTasks::startTask);
        //开启目录检测
        ThreadUtil.execute(DirWatcher::startWatcher);
    }

    /**
     * 检查更新
     */
    private static boolean checkForUpdate() {
//        String result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
//        while (StrUtil.isEmpty(result)){
//            result = HttpUtil.get("http://yunpan.xingk.xin/备份助手/upload.json");
//        }
//        JSONObject versionJson = JSONUtil.parseObj(result);
//        String url = versionJson.getStr("url");
//        String desc = versionJson.getStr("desc");
//        double version = (double) versionJson.get("version");
//        int update = versionJson.getInt("update");
//        if (version > CommonConstants.VERSION){//检测到有新版
//            CommonUI.setFont();//设置字体
//            int button = JOptionPane.showConfirmDialog(null, desc, "检测到有新版，是否更新？", JOptionPane.YES_NO_OPTION);
//            if (button==0){//选择是打开浏览器
//                DesktopUtil.browse(url);
//                return true;
//            }else {
//                if (update==1){//强更新
//                    System.exit(0);
//                }
//            }
//        }
        return false;
    }

}
