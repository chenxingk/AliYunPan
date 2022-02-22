package xin.xingk.www.util;

/**
 * Author: 陈靖杰
 * Date: 2022/2/22 18:03
 * Description:版本更新工具类
 */
public class UpdateUtil {

    /**
     * 检查更新
     */
    public static boolean checkForUpdate() {
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
