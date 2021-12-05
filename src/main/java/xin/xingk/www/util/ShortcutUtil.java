package xin.xingk.www.util;

import javax.swing.filechooser.FileSystemView;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: Mr.chen
 * @date: 2021/11/29 21:17
 * @description: 快捷方式工具类 参考：https://blog.csdn.net/weixin_43217817/article/details/104821787
 */
public class ShortcutUtil {

    /**
     * 开机启动目录
     */
    public final static String startup=System.getProperty("user.home")+ "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\";

    /**
     * 桌面目录
     */
    public final static String desktop= FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"\\";

    /**
     * 文件头，固定字段
     */
    private static final byte[] headFile={0x4c,0x00,0x00,0x00,
            0x01, 0x14,0x02,0x00,0x00,0x00,0x00,0x00,
            (byte) 0xc0,0x00,0x00,0x00,0x00,0x00,0x00,0x46
    };

    /**
     * 文件头属性
     */
    private static final byte[] fileAttributes={(byte) 0x93,0x00,0x08,0x00,//可选文件属性
            0x20, 0x00, 0x00, 0x00,//目标文件属性
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//文件创建时间
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//文件修改时间
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//文件最后一次访问时间
            0x00,0x00,0x00,0x00,//文件长度
            0x00,0x00,0x00,0x00,//自定义图标个数
            0x01,0x00,0x00,0x00,//打开时窗口状态
            0x00,0x00,0x00,0x00,//热键
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00//未知
    };
    /**
     * 固定字段1
     */
    static byte[] fixedValueOne={
            (byte) 0x83 ,0x00 ,0x14 ,0x00
            ,0x1F ,0x50 ,(byte)0xE0 ,0x4F
            ,(byte)0xD0 ,0x20 ,(byte)0xEA
            ,0x3A ,0x69 ,0x10 ,(byte)0xA2
            ,(byte)0xD8 ,0x08 ,0x00 ,0x2B
            ,0x30,0x30,(byte)0x9D,0x19,0x00,0x2f
    };
    /**
     * 固定字段2
     */
    static byte[] fixedValueTwo={
            0x3A ,0x5C ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
            ,0x00 ,0x54 ,0x00 ,0x32 ,0x00 ,0x04
            ,0x00 ,0x00 ,0x00 ,0x67 ,0x50 ,(byte)0x91 ,0x3C ,0x20 ,0x00
    };


    /**
     * 生成快捷方式
     * @param filePath 待生成的真实文件地址
     * @param lnkPath 快捷方式
     */
    public static void createLnk(String filePath,String lnkPath){
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(FileUtil.touch(lnkPath));
            fos.write(headFile);
            fos.write(fileAttributes);
            fos.write(fixedValueOne);
            fos.write((filePath.toCharArray()[0]+"").getBytes());
            fos.write(fixedValueTwo);
            fos.write(filePath.substring(3).getBytes("gbk"));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home")+ "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\");
        //createLnk("E:\\用户目录\\桌面\\备份助手\\备份助手-0.3-win-不含JDK.exe",startup+"test.lnk");
    }
}
