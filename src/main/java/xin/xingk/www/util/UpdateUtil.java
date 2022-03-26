package xin.xingk.www.util;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.ui.Login;
import xin.xingk.www.ui.dialog.UpdateDialog;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Author: 陈靖杰
 * Date: 2022/2/22 18:03
 * Description:版本更新工具类
 */
@Slf4j
public class UpdateUtil {


    /**
     * 获取版本信息
     * @return 版本信息
     */
    public static JSONObject getVersionInfo(){
        //获取
        String result;
        try {
            result = HttpRequest.get("https://gitee.com/xingk-code/AliYunPan/raw/master/src/main/resources/version.json")
                    .header("Referer", "https://gitee.com/xingk-code/AliYunPan/blob/master/src/main/resources/version.json")
                    .timeout(5000)
                    .execute().body();
        } catch (Exception e) {
            log.error(">>> 检测更新，从gitee获取版本信息失败，返回不更新...");
            return null;
        }
        //版本信息
        return JSONUtil.parseObj(result);
    }

    /**
     * 检查是否有更新
     */
    public static boolean checkForUpdate() {
        JSONObject versionInfo = getVersionInfo();
        if (ObjectUtil.isEmpty(versionInfo)) return false;
        //服务端版本号
        String newVersion = versionInfo.getStr("version");
        //判断是否有新版
        return VersionComparator.INSTANCE.compare(newVersion, CommonConstants.VERSION) > 0;
    }

    /**
     * 更新对话框
     */
    public static void doUpdateDialog(){
        JSONObject versionInfo = getVersionInfo();
        if (ObjectUtil.isNotEmpty(versionInfo)){
            //下载地址
            String url = versionInfo.getStr("url");
            //版本描述
            String desc = versionInfo.getStr("desc");
            //更新模式 1为强更新
            //updateType = versionInfo.getInt("updateType");
            int button = JOptionPane.showConfirmDialog(null, desc, "检测到有新版，是否更新？", JOptionPane.YES_NO_OPTION);
            if (button==0){//选择是打开浏览器
                /*DesktopUtil.browse(url);
                if (updateType == 1) System.exit(0);*/
                UpdateDialog updateDialog = new UpdateDialog();
                updateDialog.pack();
                updateDialog.downLoad(url);
                updateDialog.setVisible(true);
            }
            //强更新
            //if (updateType == 1) System.exit(0);
        }
    }

    /**
     * 更新DB版本
     */
    public static void updateDb(){
        //当前数据库版本
        String dbVersion = ConfigUtil.getDbVersion();
        //当前软件版本
        String version = CommonConstants.VERSION;
        //判断是否需要更新DB
        if (VersionComparator.INSTANCE.compare(version,dbVersion)>0){
            Login.getInstance().getQrCodeLabel().setText("正在更新程序DB……");
            JSONObject versionInfo = JSONUtil.parseObj(ResourceUtil.readUtf8Str("version.json"));
            JSONObject versionIndex = versionInfo.getJSONObject("versionIndex");
            //当前db版本下标
            Integer indexInt = versionIndex.getInt(dbVersion);
            //当前版本下标
            Integer indexSize = versionIndex.getInt(version);
            //从当前版本之后开始
            indexInt++;
            for (int i = indexInt; i <= indexSize; i++) {
                String sqlPath = "db/" + i + ".sql";
                if (ObjectUtil.isNotEmpty(ResourceUtil.getStreamSafe(sqlPath))){
                    String sql = ResourceUtil.readUtf8Str(sqlPath);
                    try {
                        BackupContextHolder.executeSql(sql);
                        log.info(">>> 更新索引为【{}】版本对应的sql完毕", i);
                    } catch (SQLException e) {
                        log.error(">>> 索引为【{}】版本对应的SQL执行器发生异常：{}",i,e.getMessage());
                        Login.getInstance().getQrCodeLabel().setIcon(null);
                        Login.getInstance().getQrCodeLabel().setText("更新程序DB出现异常……请联系作者");
                        ThreadUtil.sleep(600000);
                        System.exit(0);
                    }
                }
            }
            //更新库里DB版本
            ConfigUtil.setVersion(version);
        }
    }

}
