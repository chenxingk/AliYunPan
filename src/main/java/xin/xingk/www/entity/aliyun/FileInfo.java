package xin.xingk.www.entity.aliyun;

import lombok.Data;

/**
 * Author: 陈靖杰
 * Date: 2022/3/1 14:24
 * Description: 本地文件信息
 */
@Data
public class FileInfo {

    //文件名
    private String name;

    //文件路径
    private String path;

    //文件类型
    private String type;

    //文件MimeType
    private String contentType;

    //文件Hash
    private String contentHash;

    //文件大小
    private long size;

    //文件块
    private long max;


}
