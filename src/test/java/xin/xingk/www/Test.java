package xin.xingk.www;


import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.context.BackupContextHolder;
import xin.xingk.www.entity.Backup;
import xin.xingk.www.entity.aliyun.CloudFile;
import xin.xingk.www.util.AliYunUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试类
 */
public class Test{

    public static void main(String[] args)  {
        AliYunUtil.login();
        Backup backup = BackupContextHolder.getBackupById(1);
        //备份目录ID
        String fileId = AliYunUtil.getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));
        List<CloudFile> cloudFileList = AliYunUtil.getCloudFileList(fileId);
        for (CloudFile cloudFile : cloudFileList) {

        }
        /*CommonConstants.DriveId = "31065021";
        CommonConstants.TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCJdLFwicm9sZVwiOlwidXNlclwiLFwicmVmXCI6XCJodHRwczovL3d3dy5hbGl5dW5kcml2ZS5jb20vXCIsXCJkZXZpY2VfaWRcIjpcImViMjFjMWM1ZmJhYzRjMTQ4YTkyMzExN2IyMTc4ZDEzXCJ9IiwiZXhwIjoxNjQ2MjMxNDY2LCJpYXQiOjE2NDYyMjQyMDZ9.eTY5ex1wQlrXu1UPtwCny0mfjJnkT7UU_y-YC0mGzR3u4hsLMpkSzJaMB4r2auEXJHzUhRMO3UXG872u4JENcLA_gYlLgfXV3ur_hTDo1KsBqlq8pVGKNQRWn9HA1_2-jEqs1T8LI0jfNxXjtnSEbycLhwjpJXuioIufUgo7PDo";
        AliYunUtil.downloadCloudFile("61ab0ea81b3130df01554fe29e685c90b57f6139","E:\\用户目录\\桌面");*/
    }



}