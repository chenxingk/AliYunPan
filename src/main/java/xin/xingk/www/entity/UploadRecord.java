package xin.xingk.www.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Author: 陈靖杰
 * Date: 2022/2/21 13:58
 * Description: 上传记录表
 */
@Data
@TableName(value = "upload_record")
public class UploadRecord extends BaseEntity{
    //自增主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    //本地文件路径
    private String filePath;
    //云盘文件ID
    private String fileId;
    //文件hash值
    private String fileHash;
}
