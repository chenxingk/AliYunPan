package xin.xingk.www.common;

import cn.hutool.core.util.StrUtil;
import xin.xingk.www.common.utils.AliYunPanUtil;

public class CronTask {

    // 日志界面
    MyConsole console = CommonConstants.console;

    //阿里云工具类
    private AliYunPanUtil aliYunPanUtil=new AliYunPanUtil();

    /**
     * 更新阿里云盘token
     */
    public void updateALiYunPanToken(){
        if (StrUtil.isNotEmpty(CommonConstants.REFRESH_TOKEN)){
            console.append("定时更新阿里云Token\n");
            aliYunPanUtil.getAliYunPanInfo();
        }
    }

}
