package xin.xingk.www;

import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.ui.Home;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.MainFrame;
import xin.xingk.www.util.AliYunPanUtil;
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
        CommonConstants.LOGIN_STATUS = new AliYunPanUtil().getAliYunPanInfo();
        mainFrame = new MainFrame();
        Home.initUi();
        Login.initUi();
//        ThreadUtil.execute(Home::initUi);
//        ThreadUtil.execute(Login::initUi);
        //检查是否有更新
        //if (checkForUpdate()) return;
        //初始化俩页面 回头只做切换
        //生成二维码和获取表格单独调用
        if(CommonConstants.LOGIN_STATUS){
            mainFrame.initHome();
        }else{
            mainFrame.initLogin();
        }

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
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
