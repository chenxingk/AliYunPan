package xin.xingk.www.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.entity.UploadRecord;
import xin.xingk.www.mybatis.mapper.UploadRecordMapper;

import java.util.List;

/**
 * Author: 陈靖杰
 * Date: 2022/2/17 09:57
 * Description: 上传记录表 业务处理
 */
@Slf4j
public class UploadRecordService {

    public static UploadRecordMapper uploadRecordMapper;

    /**
     * 新增上传记录
     */
    public void addUploadRecord(UploadRecord uploadRecord){
        uploadRecordMapper.insert(uploadRecord);
    }

    /**
     * 删除上传记录
     * @param id
     */
    public void delUploadRecord(Integer id){
        uploadRecordMapper.deleteById(id);
    }

    /**
     * 修改上传记录
     * @param uploadRecord
     */
    public void updateUploadRecord(UploadRecord uploadRecord){
        uploadRecordMapper.updateById(uploadRecord);
    }

    /**
     * 根据ID获取上传记录信息
     * @param id
     */
    public UploadRecord getUploadRecordById(Integer id){
        return uploadRecordMapper.selectById(id);
    }

    /**
     * 根据文件路径 获取上传记录信息
     * @param filePath 文件路径
     */
    public UploadRecord getUploadRecordByFilePath(String filePath){
        LambdaQueryWrapper<UploadRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UploadRecord::getFilePath, filePath);
        return uploadRecordMapper.selectOne(queryWrapper);
    }

    /**
     * 获取备份任务信息列表
     */
    public List<UploadRecord> getUploadRecordList(){
        return uploadRecordMapper.selectList(null);
    }




}
