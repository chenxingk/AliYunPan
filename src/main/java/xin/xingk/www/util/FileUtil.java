package xin.xingk.www.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import xin.xingk.www.common.constant.CommonConstants;
import xin.xingk.www.entity.aliyun.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Description: 文件操作工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {
    static List<String> docTypes = new ArrayList<>();//文档类型
    static List<String> compressTypes = new ArrayList<>();//压缩包类型
    static List<String> imageTypes = new ArrayList<>();//图片类型
    static List<String> musicTypes = new ArrayList<>();//音乐类型
    static List<String> videoTypes = new ArrayList<>();//视频类型
    static List<String> applyTypes = new ArrayList<>();//软件类型
    static List<String> devTypes = new ArrayList<>();//开发类型

    //文件类型
    static {
        /*
          文档类型
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
        devTypes.add("md");

        /*
          压缩包类型
         */
        compressTypes.add("zip");
        compressTypes.add("rar");
        compressTypes.add("7z");
        compressTypes.add("tar.gz");
        compressTypes.add("gz");

        /*
          图片类型
         */
        imageTypes.add("png");
        imageTypes.add("jpg");
        imageTypes.add("jpeg");
        imageTypes.add("gif");
        imageTypes.add("ico");
        imageTypes.add("psd");

        /*
          音频类型
         */
        musicTypes.add("mp3");
        musicTypes.add("wma");
        musicTypes.add("aac");
        musicTypes.add("m4a");
        musicTypes.add("amr");

        /*
          视频类型
         */
        videoTypes.add("mp4");
        videoTypes.add("avi");
        videoTypes.add("3gp");
        videoTypes.add("flv");
        videoTypes.add("mkv");
        videoTypes.add("mov");
        videoTypes.add("wmv");

        /*
          软件类型
         */
        applyTypes.add("1");//微信发送的APK文件
        applyTypes.add("apk");
        applyTypes.add("exe");
        applyTypes.add("ipa");
        applyTypes.add("app");
        applyTypes.add("dmg");
        applyTypes.add("rpm");

        /*
          开发类型
         */
        devTypes.add("properties");
        devTypes.add("java");
        devTypes.add("php");
        devTypes.add("jsp");
        devTypes.add("js");
        devTypes.add("vue");
        devTypes.add("sh");
        devTypes.add("sql");
        devTypes.add("db");
        devTypes.add("xml");
        devTypes.add("html");
        devTypes.add("conf");
        devTypes.add("jar");
        devTypes.add("bat");
        devTypes.add("yml");
        devTypes.add("css");
        devTypes.add("json");
        devTypes.add("out");
        devTypes.add("class");
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
     * 获取文件信息
     * @param path 文件路径
     * @return 文件信息
     */
    public static FileInfo getFileInfo(String path) {
        String contentType = getMimeType(path);
        if (StrUtil.isEmpty(contentType)) contentType="application/octet-stream";
        FileInfo fileInfo = new FileInfo();
        File file = file(path);
        String type = getFileTypes(file.getName());
        long max = file.length() / CommonConstants.DEFAULT_SIZE;
        if (max%10480000!=0) max++;
        String md5Hex = DigestUtil.md5Hex(CommonConstants.ACCESS_TOKEN).substring(0, 16);
        BigInteger n1 = HexUtil.toBigInteger(md5Hex);
        long n2 = file.length();
        long n3 = n2 == 0 ? n2 : n1.mod(BigInteger.valueOf(n2)).longValueExact();
        long min = NumberUtil.min(n3 + 8, n2);
        byte[] proofCodeBytes = FileUtil.readBytes(file, (int)n3, (int)min);
        //String proofCode = Base64Encoder.encode(proofCodeBytes);
        String proofCode = Base64.encode(proofCodeBytes);
        fileInfo.setName(file.getName());
        fileInfo.setPath(file.getPath());
        fileInfo.setType(type);
        fileInfo.setContentType(contentType);
        fileInfo.setContentHash(DigestUtil.sha1Hex(file).toUpperCase());
        fileInfo.setSize(file.length());
        fileInfo.setMax(max == 0 ? 1 : max);
        fileInfo.setProofCode(proofCode);
        return fileInfo;
    }

    /**
     * 分片读取文件块
     *
     * @param path      文件路径
     * @param position  角标
     * @param blockSize 文件块大小
     * @return 文件块内容
     */
    public static byte[] readBytes(String path, long position, int blockSize){
        // ----- 校验文件，当文件不存在时，抛出文件不存在异常
        // ----- 读取文件
        ByteBuffer block = ByteBuffer.allocate(blockSize);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            Future<Integer> read = channel.read(block, position);
            while (!read.isDone()) {
                // ----- 睡1毫秒， 不抢占资源
                Thread.sleep(1L);
            }
        }catch (Exception e){
            throw new IORuntimeException("读取文件流，发生异常...请联系作者..."+ ExceptionUtil.stacktraceToString(e));
        }
        return block.array();
    }

    /**
     * 获取目录下所有文件
     * @param path 目录
     * @return 目录下所有文件
     */
    public static List<String> getFileList(String path) {
        List<String> List = new ArrayList<>();
        File[] files = ls(path);
        for (File file : files) if (file.isFile()) List.add(file.getPath());
        return List;
    }

    /**
     * 获取目录下所有文件夹
     * @param path 目录
     * @return 目录下所有文件夹
     */
    public static List<String> getFileFolder(String path) {
        List<String> List = new ArrayList<>();
        File[] files = ls(path);
        for (File file : files) if (file.isDirectory()) List.add(file.getName());
        return List;
    }


    public static byte[] readBytes(File file, int start, int end){
        byte[] bytes = new byte[end - start];
        try (FileInputStream stream = new FileInputStream(file)){
            for (int i = 0; i < start; i++) {
                stream.read();
            }
            int j = 0;
            for (int i = start; i < end; i++) {
                bytes[j++] = (byte) stream.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
