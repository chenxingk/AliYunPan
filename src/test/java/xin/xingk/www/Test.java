package xin.xingk.www;


import xin.xingk.www.util.ShortCutUtil;

/**
 * 测试类
 */
public class Test{

    public static void main(String[] args)  {
        System.out.println(ShortCutUtil.startup);
//        ShortCutUtil.setAppStartup("D:\\Program Files\\AliYunPan\\AliYunPan.exe");
        ShortCutUtil.cancelAppStartup("C:\\Users\\Mr.chen\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\AliYunPan.lnk");
    }



}