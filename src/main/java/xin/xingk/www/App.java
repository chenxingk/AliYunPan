package xin.xingk.www;

import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.utils.AliYunPanUtil;
import xin.xingk.www.common.utils.FileUtil;

import java.util.List;
import java.util.Map;

/**
 * 微信备份程序
 *
 */
public class App {
    public static void main( String[] args ) {
        //AliYunPan aliYunPan = new AliYunPan();
//        CronUtil.start();
//        System.out.println("ok");
        AliYunPanUtil.getAliYunPanInfo();//登录阿里云
        String wxFileId=AliYunPanUtil.getFileId(CommonConstants.ROOT, "微信备份");//备份目录ID
        List<String> folderList = FileUtil.fileFolderList(CommonConstants.PATH,FileUtil.FOLDER);
        for (String folder :  folderList) {
            System.out.println("folder:"+folder);
            List<String> fileList = FileUtil.fileFolderList(folder,FileUtil.FILE);//本地文件夹下文件
            for (String filePath :  fileList) {
                Map<String, Object> map = FileUtil.getFileInfo(filePath);
                String type = map.get("type").toString();
                String typeFileId=AliYunPanUtil.getFileId(wxFileId,type);//微信备份-类型
                String dateFileId=AliYunPanUtil.getFileId(typeFileId,folder);//微信备份-类型-日期
                AliYunPanUtil.doUploadFile(dateFileId,map);
            }
            String folderFileId = AliYunPanUtil.getFileId(wxFileId, "文件夹");//微信备份-文件夹
            String dateFileId = AliYunPanUtil.getFileId(folderFileId, folder);//微信备份-文件夹-日期
            AliYunPanUtil.scanFolders(folder,dateFileId,false);
        }
        System.out.println("ok");
    }

    public static void Test(){
        System.out.println(111);
    }
}
