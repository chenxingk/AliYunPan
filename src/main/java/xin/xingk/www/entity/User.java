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
    //自增主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    //用户昵称
    private String name;
    //用户token
    private String token;
    //主题名称
    private String theme;
}
