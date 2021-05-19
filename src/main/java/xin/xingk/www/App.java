package xin.xingk.www;

import cn.hutool.cron.CronUtil;

/**
 * 微信备份程序
 *
 */
public class App {
    public static void main( String[] args ) {
//        Thread threadOne = new Thread(() -> new AliYunPan());
//        threadOne.start();
        //加载UI
        new AliYunPan();
        //开启定时
        CronUtil.start();
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);



        //获取用户输入的token
        /*CommonConstants.REFRESH_TOKEN="ec06dae699d14c8e87e55372d92f4664";
        //获取用户输入的目录
        CommonConstants.PATH="F:\\用户目录\\文档\\WeChat Files\\wxid_3wc96wg6zgf022\\FileStorage\\File\\2021-05";
        //获取用户输入的目录名称
        CommonConstants.BACK_NAME="测试2";
        //获取上传模式
        CommonConstants.BACK_TYPE = 1;
        AliYunPanUtil aliYunPanUtil=new AliYunPanUtil();
        try {
            aliYunPanUtil.startBackup();
        } catch (Exception e) {
            System.out.println("遇到异常情况："+e.toString());
        }*/

//        System.out.println("ok");
        /*AliYunPanUtil.getAliYunPanInfo();//登录阿里云
        String wxFileId=AliYunPanUtil.getFileId(CommonConstants.ROOT, "公司-微信备份");//备份目录ID
        List<String> folderList = FileUtil.fileFolderList(CommonConstants.PATH,FileUtil.FOLDER);
        for (String folderName :  folderList) {
            System.out.println("folder:"+folderName);
            String path = CommonConstants.PATH + FileUtil.FILE_SEPARATOR + folderName;//路径
            List<String> fileList = FileUtil.fileFolderList(path,FileUtil.FILE);//本地文件夹下文件
            for (String filePath :  fileList) {
                Map<String, Object> map = FileUtil.getFileInfo(filePath);
                String type = map.get("type").toString();
                String typeFileId=AliYunPanUtil.getFileId(wxFileId,type);//微信备份-类型
                String dateFileId=AliYunPanUtil.getFileId(typeFileId,folderName);//微信备份-类型-日期
                AliYunPanUtil.doUploadFile(dateFileId,map);
            }
            String folderFileId = AliYunPanUtil.getFileId(wxFileId, "文件夹");//微信备份-文件夹
            String dateFileId = AliYunPanUtil.getFileId(folderFileId, folderName);//微信备份-文件夹-日期
            AliYunPanUtil.scanFolders(path,dateFileId,false);
        }
        System.out.println("ok");*/
        /*String CONFIG_PATH = System.getProperty("user.dir") + File.separator + "uploadLog.txt";
        FileWriter fileWriter = FileWriter.create(FileUtil.touch(CONFIG_PATH), CharsetUtil.CHARSET_UTF_8);
        fileWriter.append("测试");
        fileWriter.append("测试");
        fileWriter.append("测试");
        fileWriter.append("测试");
        fileWriter.append("测试");
        FileReader fileReader = new FileReader(CONFIG_PATH);
        List<String> list = fileReader.readLines();
        System.out.println(111);*/
    }

    public static void Test(){
        System.out.println(111);
    }
}
