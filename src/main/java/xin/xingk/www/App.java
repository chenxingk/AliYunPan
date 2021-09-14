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
        AliYunPanUtil aliYunPanUtil = new AliYunPanUtil();
        boolean login = aliYunPanUtil.getAliYunPanInfo();
        if(login){
            try {
                new AliYunPan();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            new Login();
        }
    }

}
