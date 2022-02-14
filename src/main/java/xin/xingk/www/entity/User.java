package xin.xingk.www.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 10:07
 * Description:
 */

@Data
@TableName(value = "user")
public class User extends BaseEntity{
    @TableId(type = IdType.AUTO)
    private String id;

    private String name;

    private String token;

    private Integer theme;
}
