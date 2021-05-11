package xin.xingk.www.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.MyConsole;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 文件操作工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    public static final String FILE="File";//文件
    public static final String FOLDER="Folder";//文件夹
    static List<String> docTypes = new ArrayList<>();//文档类型
    static List<String> compressTypes = new ArrayList<>();//压缩包类型
    static List<String> imageTypes = new ArrayList<>();//图片类型
    static List<String> musicTypes = new ArrayList<>();//音乐类型
    static List<String> videoTypes = new ArrayList<>();//视频类型
    static List<String> applyTypes = new ArrayList<>();//软件类型
    static List<String> devTypes = new ArrayList<>();//开发类型

    //文件类型
    static {
        /**
         * 文档类型
         */
        docTypes.add("xls");
        docTypes.add("xlsx");
        docTypes.add("xlsm");
        docTypes.add("doc");
        docTypes.add("docx");
        docTypes.add("ppt");
        docTypes.add("pptx");
        docTypes.add("txt");
        docTypes.add("pdf");
        docTypes.add("chm");
        docTypes.add("xmind");

        /**
         * 压缩包类型
         */
        compressTypes.add("zip");
        compressTypes.add("rar");
        compressTypes.add("7z");
        compressTypes.add("tar.gz");
        compressTypes.add("gz");

        /**
         * 图片类型
         */
        imageTypes.add("png");
        imageTypes.add("jpg");
        imageTypes.add("jpeg");
        imageTypes.add("gif");
        imageTypes.add("ico");
        imageTypes.add("psd");

        /**
         * 音频类型
         */
        musicTypes.add("mp3");
        musicTypes.add("wma");
        musicTypes.add("aac");
        musicTypes.add("m4a");
        musicTypes.add("amr");

        /**
         * 视频类型
         */
        videoTypes.add("mp4");
        videoTypes.add("avi");
        videoTypes.add("3gp");
        videoTypes.add("flv");
        videoTypes.add("mkv");
        videoTypes.add("mov");
        videoTypes.add("wmv");

        /**
         * 软件类型
         */
        applyTypes.add("1");
        applyTypes.add("apk");
        applyTypes.add("exe");
        applyTypes.add("ipa");
        applyTypes.add("app");
        applyTypes.add("dmg");

        /**
         * 开发类型
         */
        devTypes.add("properties");
        devTypes.add("java");
        devTypes.add("php");
        devTypes.add("jsp");
        devTypes.add("js");
        devTypes.add("vue");
        devTypes.add("sh");
        devTypes.add("sql");
        devTypes.add("xml");
        devTypes.add("html");
        devTypes.add("conf");
        devTypes.add("jar");
        devTypes.add("bat");
        devTypes.add("yml");
        devTypes.add("css");
        devTypes.add("json");
    }

    /**
     * 获取文件类型
     * @param name 文件名
     * @return 返回文件类型
     */
    public static String getFileTypes(String name){
        //获取文件的后缀名
        String suffix = getSuffix(name).toLowerCase();
        if (docTypes.contains(suffix)) return "文档";
        if (imageTypes.contains(suffix)) return "图片";
        if (musicTypes.contains(suffix)) return "音频";
        if (videoTypes.contains(suffix)) return "视频";
        if (applyTypes.contains(suffix)) return "软件";
        if (devTypes.contains(suffix)) return "开发";
        if (compressTypes.contains(suffix)) return "压缩包";
        return "其它";
    }

    /**
     * 读取目录下的文件 或 文件夹
     * 不含子目录
     * @param path 目录绝对路径或者相对路径
     * @param type 文件或文件夹 为空默认返回文件夹
     * @return 列表
     */
    public static List<String> fileFolderList(String path,String type) {
        List<String> List = new ArrayList<>();
        final File[] files = ls(path);
        for (File file : files) {
            if(FILE.equals(type) && file.isFile()){
                List.add(file.getPath());
            }
            if (FOLDER.equals(type) && file.isDirectory()){
                List.add(file.getName());
            }
        }
        return List;
    }

    /**
     * 获取文件信息
     * @param path 文件路径
     */
    public static Map<String, Object> getFileInfo(String path) {
        String contentType= new MimetypesFileTypeMap().getContentType(new File(path));
        if (StrUtil.isEmpty(contentType)) contentType="application/octet-stream";
        Map<String, Object> map = new HashMap();
        File file = file(path);
        String type = getFileTypes(file.getName());
        map.put("name",file.getName());
        map.put("path",file.getPath());
        map.put("size",file.length());
        map.put("type",type);
        map.put("content_type",contentType);
        map.put("content_hash",DigestUtil.sha1Hex(file).toUpperCase());
        return map;
    }

}
