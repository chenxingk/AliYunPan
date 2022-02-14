package xin.xingk.www.entity;

import lombok.Data;

/**
 * @author: Mr.chen
 * @date: 2022/2/13 21:46
 * @description: 备份任务（实体类）
 */
@Data
public class Backup extends BaseEntity{
    //本地目录
    private String localPath;
    //云盘目录
    private String cloudDiskPath;
    //备份模式 0普通 1分类 2微信
    private Integer backupType;
    //目录检测 0关闭 1开始
    private Integer monitor;
    //定时备份时间
    private String backupTime;
    //状态 0禁用 1开启
    private Integer status;
    //备份文件数量
    private Integer fileNum;
}
