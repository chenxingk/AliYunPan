package xin.xingk.www;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;
import xin.xingk.www.ui.AliYunPan;
import xin.xingk.www.ui.Login;
import xin.xingk.www.util.AliYunPanUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.net.URI;

/**
 * 备份程序
 *
 */
public class App {

    public static void main( String[] args ) {
        String result = HttpUtil.get("http://yunpan.xingk.xin/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8B/upload.json");
        while (StrUtil.isEmpty(result)){
            result = HttpUtil.get("http://yunpan.xingk.xin/%E5%A4%87%E4%BB%BD%E5%8A%A9%E6%89%8B/upload.json");
        }
        JSONObject versionJson = JSONUtil.parseObj(result);
        String url = versionJson.getStr("url");
        String desc = versionJson.getStr("desc");
        Object version = versionJson.get("version");
        int update = versionJson.getInt("update");
        boolean isUpdate = NumberUtil.isGreater((BigDecimal) version, CommonConstants.VERSION);
        if (isUpdate){//检测到有新版
            CommonUI.setFont();//设置字体
            int btnNum = JOptionPane.showConfirmDialog(null, desc, "检测到有新版，是否更新？", JOptionPane.YES_NO_OPTION);
            if (btnNum==0){//选择是打开浏览器
                openUrl(url);
                return;
            }else {
                if (update==1){
                    System.exit(0);
                }
            }
        }
        boolean login = new AliYunPanUtil().getAliYunPanInfo();
        if(login){
            new AliYunPan();
        }else{
            new Login();
        }
    }



    /**
     * 打开默认浏览器
     * @param url
     */
    private static void openUrl(String url){
        try {
            //创建一个URI实例
            URI uri = URI.create(url);
            //判断当前系统是否支持Java AWT Desktop扩展
            if(Desktop.isDesktopSupported()) {
                //获取当前系统桌面扩展
                Desktop desktop = Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if (ObjectUtil.isNotEmpty(desktop) && desktop.isSupported(Desktop.Action.BROWSE)){
                    //获取系统默认浏览器打开链接
                    desktop.browse(uri);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("无法打开默认浏览器，"+e.getMessage());
        }
    }


}
