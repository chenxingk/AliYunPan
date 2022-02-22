package xin.xingk.www;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.constant.CommonConstants;
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
        String version = SystemUtil.getJavaSpecInfo().getVersion();
        if (!"1.8".equals(version)){
            JOptionPane.showMessageDialog(null, "你当前的JDK版本为【"+version+"】\n" +
                    "请您使用JDK【1.8】来运行本程序", "温馨提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TimeInterval timer = DateUtil.timer();
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

        log.info("启动耗时："+timer.interval()+" ms");

        /**
         * 需要改为登录成功开启...
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
