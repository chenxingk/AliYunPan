package xin.xingk.www.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.CommonUI;
import xin.xingk.www.context.UserContextHolder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: http请求工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class OkHttpUtil {
    //错误次数
    int errNum=0;

   static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.MINUTES).build();
   static MediaType mediaType = MediaType.parse("application/json");
   static RequestBody body;
   static Request request;
   static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.66";


    /**
     * 阿里云盘交互的POST请求
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject doPost(String url, JSONObject data){
        try {
            body = RequestBody.create(mediaType,data.toString());
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("authorization", CommonConstants.TOKEN)
                    .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            CommonUI.console("请求状态码：{}",response.code());
            if (429==response.code()){
                CommonUI.console("请求频繁了，休息一下。。。。正在准备重试中。。。");
                ThreadUtil.sleep(3000);
                return doPost(url,data);
            }
            JSONObject json = JSONUtil.parseObj(result);
            errNum=0;
            return json;
        } catch (Exception e) {
            if (e.toString().contains("A JSONObject text")){
                CommonUI.console("{} 请求遇到异常：{}",url,e);
                return null;
            }else{
                errNum++;
                CommonUI.console("{} 请求遇到异常：{}",url,e);
                if (errNum>5){
                    CommonUI.console("普通请求失败次数超过：{} 次....已停止",errNum);
                    return null;
                }else{
                    CommonUI.console("普通请求发起第：{} 次重试",errNum);
                    return doPost(url,data);
                }
            }
        }
    }

    /**
     * 阿里云盘上传文件的POST请求
     * @param url
     * @param data
     * @return netstat -anp|grep 61617
     * @throws Exception
     */
    public JSONObject doFilePost(String url,JSONObject data){
        try {
            RequestBody body = RequestBody.create(mediaType, data.toString());
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("authorization",CommonConstants.TOKEN)
                    .addHeader("Content-Type", "multipart/form-data").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            CommonUI.console("文件请求状态码：{}",response.code());
            //System.out.println("result：>>>>>>>>>>>>>>>>>>>"+result);
            JSONObject json = JSONUtil.parseObj(result);
            errNum=0;
            return json;
        } catch (Exception e) {
            errNum++;
            CommonUI.console("上传请求遇到异常：{}",e);
            if (errNum>5){
                CommonUI.console("上传请求失败次数超过：{} 次....已停止",errNum);
                return null;
            }else{
                CommonUI.console("上传请求发起第：{} 次重试",errNum);
                return doFilePost(url,data);
            }
        }
    }

    /**
     * 删除阿里云盘文件
     * @param data
     * @return
     */
    public void deleteFile(JSONObject data){
        HttpRequest request = HttpRequest.post(CommonConstants.DELETE_FILE_URL);
        request.body(data.toString());
        request.header("Content-Type", "application/json");
        request.header("authorization", CommonConstants.TOKEN);
        request.execute().body();
    }

    /**
     * 阿里云盘上传二进制文件
     * @param url
     * @param fileBytes
     * @return
     * @throws Exception
     */
    public int uploadFileBytes(String url, byte[] fileBytes){
        try {
            RequestBody body = RequestBody.create(fileBytes);
            Request request = new Request.Builder().url(url).method("PUT",body).build();
            Response response = client.newCall(request).execute();
            //String result=response.body().string();
            //CommonUI.console("上传文件请求状态码：{}",response.code());
            errNum=0;
            return response.code();
        } catch (Exception e) {
            errNum++;
            CommonUI.console("上传文件遇到异常：{}",e.toString());
            if (errNum>5){
                CommonUI.console("上传文件失败次数超过：{} 次....已停止",errNum);
                return 0;
            }else{
                CommonUI.console("上传文件发起第：{} 次重试",errNum);
                uploadFileBytes(url,fileBytes);
            }
        }
        return 0;
    }



    /**
     * 执行登录
     * @param json
     * @return
     * @throws Exception
     */
    public static JSONObject doLogin(JSONObject json) throws Exception {
        String response;
        String dataStr = json.getJSONObject("content").getJSONObject("data").getStr("bizExt");
        //解密返回的数据
        response = Base64.decodeStr(dataStr, CharsetUtil.CHARSET_GBK);
        //完成登录流程
        JSONObject loginResult = JSONUtil.parseObj(response).getJSONObject("pds_login_result");
        String accessToken = loginResult.getStr("accessToken");
        String nickName = loginResult.getStr("nickName");
        UserContextHolder.updateUserName(nickName);
        String cookie = getCookie();
        String code = getCode(accessToken, cookie);
        //返回真正的Token信息
        return getToken(code);
    }

    /**
     * 获取授权地址
     * @return
     */
    public static String getAuthorizeUrl() {
        String clientId = getClientId();
        String url = "https://auth.aliyundrive.com/v2/oauth/authorize?client_id={}&redirect_uri=https://www.aliyundrive.com/sign/callback&response_type=code&login_type=custom&state={\"origin\":\"https://aliyundrive.com\"}";
        return StrUtil.format(url, clientId);
    }

    /**
     * 获取 client_id
     * @return
     * @throws IOException
     */
    public static String getClientId(){
        Connection connect = Jsoup.connect("https://aliyundrive.com/sign/in");
        Connection data = connect.header("User-Agent",USER_AGENT).header("referer","https://aliyundrive.com/");
        try {
            Document doc = data.get();
            return StrUtil.subBetween(doc.html(), "client_id: '", "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取code
     * @param accessToken
     * @param cookie
     * @return
     * @throws IOException
     */
    private static String getCode(String accessToken, String cookie) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "{\"token\":\""+ accessToken +"\"}");
        Request request = new Request.Builder()
                .url("https://auth.aliyundrive.com/v2/oauth/token_login")
                .method("POST", body)
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("content-type", "application/json;charset=UTF-8")
                .addHeader("cookie", cookie)
                .build();
        Response response = client.newCall(request).execute();
        String code = JSONUtil.parseObj(response.body().string()).getStr("goto");
        code = StrUtil.subBetween(URLUtil.decode(code), "code=", "&");
        return code;
    }

    /**
     * 获取登录的 Token
     * @param code
     * @throws IOException
     * @return
     */
    private static JSONObject getToken(String code) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "{\"code\":\""+ code +"\"}");
        Request request = new Request.Builder()
                .url("https://websv.aliyundrive.com/token/get")
                .method("POST", body)
                .addHeader("content-type", "application/json; charset=UTF-8")
                .addHeader("accept", "*/*")
                .build();
        Response response = client.newCall(request).execute();
        return JSONUtil.parseObj(response.body().string());
    }

    /**
     * 获取阿里云 Cookie
     * @return
     * @throws Exception
     */
    public static String getCookie() throws Exception {
        String sessionURL = getAuthorizeUrl();
        Map<String, String> map2 = initCookie(sessionURL);
        Map<String, String> map3  = initCookie("https://passport.aliyundrive.com/mini_login.htm?lang=zh_cn&appName=aliyun_drive");
        String cookie = mapToString(map2)+";";
        cookie+= mapToString(map3);
        return cookie;
    }

    /**
     * map转 String
     * @param map
     * @return
     */
    public static String mapToString(Map<String, String> map) {
        return MapUtil.join(map, "; ", "=", null);
    }

    /**
     * 初始化阿里云 Cookie
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, String> initCookie(String url) throws Exception {
        Connection connect = Jsoup.connect(url);
        Connection data = connect.header("User-Agent",USER_AGENT)
                .header("referer","https://aliyundrive.com/");
        Document doc = data.get();
        data.response().headers();
        return data.response().cookies();
    }



    /**
     * 获取阿里云二维码
     */
    public static JSONObject getQrCodeUrl(){
        try {
            Request request = new Request.Builder()
                    .url("https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            return JSONUtil.parseObj(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询二维码状态
     * @param t
     * @param ck
     * @return
     * @throws Exception
     */
    public static JSONObject queryQrCode(String t,String ck){
        try {
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("t", t)
                    .addFormDataPart("ck", ck)
                    .build();
            Request request = new Request.Builder()
                    .url("https://passport.aliyundrive.com/newlogin/qrcode/query.do?appName=aliyun_drive")
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            return JSONUtil.parseObj(response.body().string());
        } catch (IOException e) {
            return null;
        }
    }






}
