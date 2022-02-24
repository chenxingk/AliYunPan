package xin.xingk.www.context;

import xin.xingk.www.entity.UploadRecord;
import xin.xingk.www.mybatis.service.UploadRecordService;

import java.util.List;

/**
 * Author: 陈靖杰
 * Date: 2022/2/17 10:19
 * Description: 备份任务信息获取
 */
public class UploadRecordContextHolder {

    public static UploadRecordService uploadRecordService = new UploadRecordService();

    /**
     * 新增备份任务
     * @return
     */
    public static void addUploadRecord(UploadRecord uploadRecord){
        uploadRecordService.addUploadRecord(uploadRecord);
    }

    /**
     * 删除备份任务
     * @param id
     */
    public static void delUploadRecord(Integer id){
        uploadRecordService.delUploadRecord(id);
    }

    /**
     * 修改备份任务
     * @param uploadRecord
     */
    public static void updateUploadRecord(UploadRecord uploadRecord){
        uploadRecordService.updateUploadRecord(uploadRecord);
    }

    /**
     * 根据ID获取备份任务信息
     * @param id
     */
    public static UploadRecord getUploadRecordById(Integer id){
        return uploadRecordService.getUploadRecordById(id);
    }

    /**
     * 根据本地目录 获取备份任务信息
     * @param filePath
     * @return
     */
    public static UploadRecord getUploadRecordByFilePath(String filePath){
        return uploadRecordService.getUploadRecordByFilePath(filePath);
    }

    /**
     * 并发点
     * 获取备份任务信息列表
     */
    public static List<UploadRecord> getUploadRecordList(){
        return uploadRecordService.getUploadRecordList();
    }

}
