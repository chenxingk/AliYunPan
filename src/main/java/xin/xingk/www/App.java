package xin.xingk.www;

import xin.xingk.www.util.AliYunPanUtil;
import xin.xingk.www.ui.AliYunPan;
import xin.xingk.www.ui.Login;

/**
 * 备份程序
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
