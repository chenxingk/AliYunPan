package xin.xingk.www;

import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.formdev.flatlaf.FlatDarculaLaf;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;
import xin.xingk.www.ui.AliYunPan;
import xin.xingk.www.ui.Login;
import xin.xingk.www.util.AliYunPanUtil;
import xin.xingk.www.util.FileUtil;
import xin.xingk.www.util.UploadLogUtil;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * 备份程序
 *
 */
public class App {


    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        //删除旧的运行日志
        FileUtil.del(UploadLogUtil.RUN_LOG);
        //检查是否有更新
        if (checkForUpdate()) return;
        boolean login = new AliYunPanUtil().getAliYunPanInfo();
        if(login){
            new AliYunPan();
        }else{
            new Login();
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
        Object version = versionJson.get("version");
        int update = versionJson.getInt("update");
        boolean isUpdate = NumberUtil.isGreater((BigDecimal) version, CommonConstants.VERSION);
        if (isUpdate){//检测到有新版
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
