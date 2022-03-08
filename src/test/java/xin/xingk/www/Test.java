package xin.xingk.www;



import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.digest.DigestUtil;
import xin.xingk.www.util.FileUtil;

import java.io.File;
import java.math.BigInteger;


/**
 * 测试类
 */
public class Test{

    public static void main(String[] args)  {
        File file = FileUtil.file("D:\\用户目录\\桌面\\日志文件\\测试秒传.txt");

        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCJdLFwicm9sZVwiOlwidXNlclwiLFwicmVmXCI6XCJodHRwczovL3d3dy5hbGl5dW5kcml2ZS5jb20vXCIsXCJkZXZpY2VfaWRcIjpcImJkMTgxNGQ2NDU0YzQ4MTNhOTJiNWRkMTMzNmNiNGQ4XCJ9IiwiZXhwIjoxNjQ2NjQ5OTM3LCJpYXQiOjE2NDY2NDI2Nzd9.i28TyKTncNXRjuAa0HvMOEkNF_Inf4XKSvD7wRDncXZ4W98OJlC_nQ9UPanI5g8mwHx2pivN7V_yTtq76oMoeK6WB79tTKycq72QdivUpP9MoKqcn2VHGrcd_Ww4Rn_97Iqb4X_l_DOK5NMDT4IOkyvoDghI-oL75HG_818tRxk";
        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCJdLFwicm9sZVwiOlwidXNlclwiLFwicmVmXCI6XCJodHRwczovL3d3dy5hbGl5dW5kcml2ZS5jb20vXCIsXCJkZXZpY2VfaWRcIjpcImEzMzIxMjQ3MjIwNDRlOGRhNTBhNTY1OThmZWVmNTI3XCJ9IiwiZXhwIjoxNjQ2NjUzNDA5LCJpYXQiOjE2NDY2NDYxNDl9.NtTxTSQ_4t-97ENGQKuNthTdu1AwtGjeyw48Xi4_cGJMF9nm1mkv9taIMMClYCOCHlH5FFy9u9VZtyILsp6M3rgqXT6-4SctItXC81gEUiK5tvNO5ukRsyxU6cf1X1PYW4eLf3pRV1pdyuxHNG9wogWdQCbB3R0eB4tZ2E7x_po";
        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwicEpaSW5OSE4yZFpXazhxZ1wiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJGSUxFLkFMTFwiLFwiVklFVy5BTExcIixcIlNIQVJFLkFMTFwiLFwiU1RPUkFHRS5BTExcIixcIlNUT1JBR0VGSUxFLkxJU1RcIixcIlVTRVIuQUxMXCIsXCJCQVRDSFwiLFwiQUNDT1VOVC5BTExcIixcIklNQUdFLkFMTFwiLFwiSU5WSVRFLkFMTFwiXSxcInJvbGVcIjpcInVzZXJcIixcInJlZlwiOlwiXCIsXCJkZXZpY2VfaWRcIjpcImRlZTZhYzhlNTAyNTQwZmU5YjQ5ZjY0MjVlMWJkMTZiXCJ9IiwiZXhwIjoxNjQ2NjUzNDA4LCJpYXQiOjE2NDY2NDYxNDh9.NbxXpo33N0Ck_9kouNvhFUAHX1w-for9vxeXpDo3CfOflE2KmX9k1JhwzzaOzAiLUGkSgbNVndsWZQs0VpFlp3v1x2hbQJAHccb_UiJGqzvinKv2nnkermndDXNeXF90JgNbd0GHaVNbULhETevOIakHIDrgpwXyh0qOkL2gyjU";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCJdLFwicm9sZVwiOlwidXNlclwiLFwicmVmXCI6XCJodHRwczovL3d3dy5hbGl5dW5kcml2ZS5jb20vXCIsXCJkZXZpY2VfaWRcIjpcIjQwZWYyNGU1NzY5YjQxOGZiZThjMTFmOWE2YWQ5MjNiXCJ9IiwiZXhwIjoxNjQ2NjU0MzgzLCJpYXQiOjE2NDY2NDcxMjN9.ghcC6NiG9CyQvvgycgPqmjrusgoFyNWEqAtKBkbSRnQvqmjyCtEzEGaQ_jiPZsfREM9CnhVv_CpKLjuiM0auBU7HbSmZ8crmVoKrCRT4qatUcRGysZrl8voNQl1vHL2W7T2OJrMI8UNr2xV-MCDFhZxOZ0Kl3tA7hj3VQNTYjdQ";
        System.out.println(token.length());
        String md5Hex = DigestUtil.md5Hex(token).substring(0, 16);
        BigInteger n1 = HexUtil.toBigInteger(md5Hex);
        long n2 = file.length();
        long n3 = n2 == 0 ? n2 : n1.mod(BigInteger.valueOf(n2)).longValueExact();
        long min = NumberUtil.min(n3 + 8, n2);
        byte[] proofCodeBytes = FileUtil.readBytes(file, (int)n3, (int)min);
        String proofCode = Base64Encoder.encode(proofCodeBytes);
        System.out.println(proofCode);
        System.out.println(Base64.encode(proofCodeBytes));

//        AliYunUtil.login();
//        Backup backup = BackupContextHolder.getBackupById(1);
//        //备份目录ID
//        String fileId = AliYunUtil.getFileIdByArr(CommonConstants.ROOT,backup.getCloudPath().split("\\\\"));
//        List<CloudFile> cloudFileList = AliYunUtil.getCloudFileList(fileId);
//        for (CloudFile cloudFile : cloudFileList) {
//
//        }
        /*CommonConstants.DriveId = "31065021";
        CommonConstants.TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzYmViOTEwZDYwOGQ0MTI0YWVkNTA1OGRjZmU1MGM1MCIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiVklFVy5BTExcIixcIlNUT1JBR0UuQUxMXCIsXCJTVE9SQUdFRklMRS5MSVNUXCIsXCJCQVRDSFwiLFwiT0FVVEguQUxMXCIsXCJJTUFHRS5BTExcIixcIklOVklURS5BTExcIixcIkFDQ09VTlQuQUxMXCJdLFwicm9sZVwiOlwidXNlclwiLFwicmVmXCI6XCJodHRwczovL3d3dy5hbGl5dW5kcml2ZS5jb20vXCIsXCJkZXZpY2VfaWRcIjpcImViMjFjMWM1ZmJhYzRjMTQ4YTkyMzExN2IyMTc4ZDEzXCJ9IiwiZXhwIjoxNjQ2MjMxNDY2LCJpYXQiOjE2NDYyMjQyMDZ9.eTY5ex1wQlrXu1UPtwCny0mfjJnkT7UU_y-YC0mGzR3u4hsLMpkSzJaMB4r2auEXJHzUhRMO3UXG872u4JENcLA_gYlLgfXV3ur_hTDo1KsBqlq8pVGKNQRWn9HA1_2-jEqs1T8LI0jfNxXjtnSEbycLhwjpJXuioIufUgo7PDo";
        AliYunUtil.downloadCloudFile("61ab0ea81b3130df01554fe29e685c90b57f6139","E:\\用户目录\\桌面");*/
    }



}