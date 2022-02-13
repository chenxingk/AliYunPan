package com.lhstack.mybatis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName(value = "user")
@Data
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String token;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;
}

