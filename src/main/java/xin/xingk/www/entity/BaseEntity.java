package xin.xingk.www.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 10:02
 * Description: 通用基础字段，需要此通用字段的实体可继承此类
 */
public class BaseEntity {

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;
}
