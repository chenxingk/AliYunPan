package xin.xingk.www;


import xin.xingk.www.util.FileUtil;

/**
 * 测试类
 */
public class Test {

    public static void main(String[] args)  {
        byte[] bytes = FileUtil.readBytes("E:\\用户目录\\桌面\\测试目录\\168907-赵安娜丶\\录制-168907-20211205-164157-535-新人开播第五天.flv");
        System.out.println(bytes);


        // TODO exe 先通过配置文件读取当前目录是否有.exe结尾的备份程序 如果有则从服务器下载新的exe文件 找不到对应版本的文件则提示未找到本地的旧版客户端 请到xxx下载
        // TODO jar 获取jar包的完整路径 从服务器下载新的jar文件
        /* System.out.println(new App().getClass().getProtectionDomain().getCodeSource().getLocation());
        //输入新版本
        BufferedInputStream newFile = FileUtil.getInputStream(FileUtil.touch("E:\\code\\AliYunPan\\out\\artifacts\\AliYunPan_jar\\xxx.jar"));
        //老版本
        BufferedOutputStream oldFile = FileUtil.getOutputStream("E:\\code\\AliYunPan\\out\\artifacts\\AliYunPan_jar\\AliYunPan.jar");
        IoUtil.copy(inputStream,outputStream);
        System.exit(0);
        String path = new App().getClass().getResource("/").getPath();
        path = path.substring(1);
        System.out.println(path);
        */


        //
//        System.out.println(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath()+"\\");
//        System.out.println(new Test().getClass().getResource("/").getPath());
//        System.out.println(new Test().getClass().getProtectionDomain().getCodeSource().getLocation().toString());
//        boolean contains = ReUtil.contains("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", "16:50:00");
//        System.out.println(contains);
    }
}