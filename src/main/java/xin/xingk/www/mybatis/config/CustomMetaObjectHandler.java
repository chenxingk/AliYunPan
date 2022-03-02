package xin.xingk.www.mybatis.config;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 09:56
 * Description:自定义sql字段填充器，自动填充创建修改相关字段
 */
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        setFieldValByName("createTime",DateUtil.now(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updateTime",DateUtil.now(),metaObject);
    }
}
