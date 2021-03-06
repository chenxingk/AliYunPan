package xin.xingk.www.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: Mr.chen
 * @date: 2022/2/13 21:46
 * @description: 备份任务（实体类）
 */
@Data
@TableName(value = "backup")
public class Backup extends BaseEntity{
    //自增主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    //本地目录
    private String localPath;
    //云盘目录
    private String cloudPath;
    //备份模式 0普通 1分类 2微信
    private Integer backupType;
    //目录检测 0关闭 1开始
    private Integer monitor;
    //目录检测 0关闭 1开始
    private Integer sync = 0;
    //定时备份时间
    private String backupTime;
    //状态 0正常 1禁用 2备份中 3同步中
    private Integer status;
    //备份文件数量
    private Integer fileNum;
}
