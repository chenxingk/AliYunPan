package xin.xingk.www;

import cn.hutool.cron.CronUtil;
import xin.xingk.www.common.utils.AliYunPanUtil;
import xin.xingk.www.ui.AliYunPan;
import xin.xingk.www.ui.Login;

/**
 * 微信备份程序
 *
 */
public class App {

    public static void main( String[] args ) {
        boolean login = new AliYunPanUtil().getAliYunPanInfo();
        if(login){
            new AliYunPan();
        }else{
            new Login();
        }
    }

}
