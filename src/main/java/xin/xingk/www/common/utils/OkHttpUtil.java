package xin.xingk.www.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.MyConsole;

import java.util.concurrent.TimeUnit;

/**
 * Description: http请求工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class OkHttpUtil {

    // 日志界面
    MyConsole console = CommonConstants.console;

   static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.MINUTES).build();
   static MediaType mediaType = MediaType.parse("application/json");
   static RequestBody body;
   static Request request;


    /**
     * 阿里云盘交互的POST请求
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject doPost(String url, JSONObject data){
        int errNum=0;
        try {
            body = RequestBody.create(mediaType,data.toString());
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("authorization", CommonConstants.TOKEN)
                    .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            console.append("请求状态码："+response.code()+"\n");
            //System.out.println("result：>>>>>>>>>>>>>>>>>>>"+result);
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            if (e.toString().contains("A JSONObject text")){
                console.append("普通请求遇到异常："+e.toString()+"\n");
                return null;
            }else {
                errNum++;
                console.append("普通请求遇到异常："+e.toString()+"\n");
                if (errNum>5){
                    console.append("普通请求失败次数超过："+errNum+" 次....已停止\n");
                    return null;
                }else{
                    console.append("普通请求发起第："+errNum+" 次重试\n");
                    return doPost(url,data);
                }
            }
        }
    }

    /**
     * 阿里云盘上传文件的POST请求
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject doFilePost(String url,JSONObject data){
        int errNum=0;
        try {
            RequestBody body = RequestBody.create(mediaType, data.toString());
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("authorization",CommonConstants.TOKEN)
                    .addHeader("Content-Type", "multipart/form-data").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            console.append("请求状态码："+response.code()+"\n");
            //System.out.println("result：>>>>>>>>>>>>>>>>>>>"+result);
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            errNum++;
            console.append("上传请求遇到异常："+e.toString()+"\n");
            if (errNum>5){
                console.append("上传请求失败次数超过："+errNum+" 次....已停止\n");
                return null;
            }else{
                console.append("上传请求发起第："+errNum+" 次重试\n");
                return doFilePost(url,data);
            }
        }
    }

    /**
     * 阿里云盘上传二进制文件
     * @param url
     * @param fileBytes
     * @return
     * @throws Exception
     */
    public void uploadFileBytes(String url,byte[] fileBytes){
        int errNum=0;
        try {
            RequestBody body = RequestBody.create(fileBytes);
            Request request = new Request.Builder().url(url).method("PUT",body).build();
            Response response = client.newCall(request).execute();
            //String result=response.body().string();
            console.append("上传文件请求状态码："+response.code()+"\n");
        } catch (Exception e) {
            errNum++;
            console.append("上传文件遇到异常："+e.toString()+"\n");
            if (errNum>5){
                console.append("上传文件失败次数超过："+errNum+" 次....已停止\n");
                return;
            }else{
                console.append("上传文件发起第："+errNum+" 次重试\n");
                uploadFileBytes(url,fileBytes);
            }
        }
    }
}
