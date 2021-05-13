package xin.xingk.www.common;

import cn.hutool.core.util.StrUtil;
import xin.xingk.www.common.utils.AliYunPanUtil;

/**
 * Description: 定时任务类
 * Author: 陈靖杰
 * Date: 2021/05/12
 */
public class CronTask {

    //阿里云工具类
    private AliYunPanUtil aliYunPanUtil=new AliYunPanUtil();

    /**
     * 更新阿里云盘token
     */
    public void updateALiYunPanToken(){
        if (StrUtil.isNotEmpty(CommonConstants.REFRESH_TOKEN)){
            CommonConstants.addConsole("定时更新阿里云Token");
            aliYunPanUtil.getAliYunPanInfo();
        }
    }

}
