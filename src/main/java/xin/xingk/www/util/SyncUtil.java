package xin.xingk.www.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.common.constant.DictConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.aliyun.CloudFile;
import xin.xingk.www.entity.aliyun.FileInfo;
import xin.xingk.www.ui.Home;

import java.util.List;

/**
 * @author: Mr.chen
 * @date: 2022/3/5 23:14
 * @description: 阿里云盘同步工具类
 */
public class SyncUtil {
    
    /**
     * 开始同步
     * @param id 同步任务ID
     */
    public static void startSync(Integer id) {
        boolean login = AliYunUtil.login();//登录阿里云
        if (!login) {
            UIUtil.console("Token已过期，请退出重新登录。。。");
            return;
        }
        if (id == null){
            List<Backup> backupList = BackupContextHolder.getBackupList();
            for (Backup backup : backupList) {
                SyncTask(backup);
            }
            Home.getInstance().getStartButton().setEnabled(true);
        }else {
            Backup backup = BackupContextHolder.getBackupById(id);
            SyncTask(backup);
        }
    }

    /**
     * 同步任务
     * @param backup 同步任务
     */
    public static void SyncTask(Backup backup) {
        if (BackupUtil.checkBackupStatus(backup)) return;
        CacheUtil.setBackupStatus(backup.getId(), DictConstants.STATUS_SYNC_RUN);
        //同步目录ID
        String fileId = AliYunUtil.getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));
        downloadCloudFolder(backup.getLocalPath(),fileId,fileId);
        UIUtil.console("本次同步：{} 下所有文件成功！...", backup.getLocalPath());
        CacheUtil.removeBackupStatus(backup.getId());
    }

    /**
     * 下载云盘目录文件到本地
     * @param localPath 本地目录（启始目录）
     * @param pathId 启始目录id（启始目录id）
     * @param fileId 云盘目录ID
     */
    public static void downloadCloudFolder(String localPath,String pathId,String fileId) {
        //获取下载目标目录
        String path = getLocalPath(localPath,pathId,fileId);
        //获取云端列表
        List<CloudFile> cloudFileList = AliYunUtil.getCloudFileList(fileId);
        //处理云端文件和云端目录
        for (CloudFile cloudFile : cloudFileList) {
            if (DictConstants.FILE_TYPE_FILE.equals(cloudFile.getType())){
                String filePath = path + FileUtil.FILE_SEPARATOR + cloudFile.getName();
                if (FileUtil.isFile(filePath)){
                    //本地存在此文件
                    FileInfo fileInfo = FileUtil.getFileInfo(filePath);
                    if (fileInfo.getContentHash().equals(cloudFile.getContentHash())){
                        //本地文件与云端文件一致
                        continue;
                    }
                }
                AliYunUtil.downloadCloudFile(cloudFile.getFileId(),path);
            }else {
                downloadCloudFolder(localPath,pathId,cloudFile.getFileId());
            }
        }
    }

    /**
     * 获取下载到本地的路径
     * @param localPath 本地目录（启始目录）
     * @param pathId 启始目录id（启始目录id）
     * @param fileId 云盘目录ID
     * @return 完整的本地目录
     */
    public static String getLocalPath(String localPath,String pathId,String fileId) {
        JSONObject folderPath = AliYunUtil.getFolderPath(fileId);
        JSONArray items = folderPath.getJSONArray("items");
        StringBuilder localPathBuilder = new StringBuilder(localPath);
        for (int i = items.size() - 1; i >= 0; i--) {
            JSONObject result = (JSONObject) items.get(i);
            //不等于开始目录的时候追加目录
            if (!pathId.equals(result.getStr("file_id"))){
                localPathBuilder.append(FileUtil.FILE_SEPARATOR).append(result.getStr("name"));
            }
        }
        return localPathBuilder.toString();
    }
}
