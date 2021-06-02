package xin.xingk.www.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xin.xingk.www.common.CommonConstants;
import xin.xingk.www.common.MyConsole;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: http请求工具类
 * Author: 陈靖杰
 * Date: 2021/05/10
 */
public class OkHttpUtil {

    // 日志界面
    MyConsole console = CommonConstants.console;
    //错误次数
    int errNum=0;

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
        try {
            body = RequestBody.create(mediaType,data.toString());
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("authorization", CommonConstants.TOKEN)
                    .addHeader("Content-Type", "application/json").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            CommonConstants.addConsole("请求状态码："+response.code());
            //System.out.println("result：>>>>>>>>>>>>>>>>>>>"+result);
            JSONObject json = JSONUtil.parseObj(result);
            errNum=0;
            return json;
        } catch (Exception e) {
            if (e.toString().contains("A JSONObject text")){
                CommonConstants.addConsole("普通请求遇到异常："+e.toString());
                return null;
            }else{
                errNum++;
                CommonConstants.addConsole("普通请求遇到异常："+e.toString());
                if (errNum>5){
                    CommonConstants.addConsole("普通请求失败次数超过："+errNum+" 次....已停止");
                    return null;
                }else{
                    CommonConstants.addConsole("普通请求发起第："+errNum+" 次重试");
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
            CommonConstants.addConsole("请求状态码："+response.code());
            //System.out.println("result：>>>>>>>>>>>>>>>>>>>"+result);
            JSONObject json = JSONUtil.parseObj(result);
            errNum=0;
            return json;
        } catch (Exception e) {
            errNum++;
            CommonConstants.addConsole("上传请求遇到异常："+e.toString());
            if (errNum>5){
                CommonConstants.addConsole("上传请求失败次数超过："+errNum+" 次....已停止");
                return null;
            }else{
                CommonConstants.addConsole("上传请求发起第："+errNum+" 次重试");
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
        try {
            RequestBody body = RequestBody.create(fileBytes);
            Request request = new Request.Builder().url(url).method("PUT",body).build();
            Response response = client.newCall(request).execute();
            //String result=response.body().string();
            CommonConstants.addConsole("上传文件请求状态码："+response.code());
            errNum=0;
        } catch (Exception e) {
            errNum++;
            CommonConstants.addConsole("上传文件遇到异常："+e.toString());
            if (errNum>5){
                CommonConstants.addConsole("上传文件失败次数超过："+errNum+" 次....已停止");
                return;
            }else{
                CommonConstants.addConsole("上传文件发起第："+errNum+" 次重试");
                uploadFileBytes(url,fileBytes);
            }
        }
    }

    /**
     * 账号密码登录阿里云
     * @param phone
     * @param password
     */
    public static JSONObject passwordLogin(String phone, String password) throws Exception {
        RSA rsa = new RSA(null, CommonConstants.PUBLIC_KEY);
        password = rsa.encryptBcd(password, KeyType.PublicKey);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "loginId="+phone+"&password2="+password+"&keepLogin=true");
        Request request = new Request.Builder()
                .url(CommonConstants.PASS_URL)
                .method("POST", body)
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        return getAliLoginInfo(response.body().string());
    }

    /**
     * 手机登录发送验证码
     * @param phone
     */
    public static JSONObject smsSend(String phone,String cookie) throws Exception {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("phoneCode", "86")
                .addFormDataPart("loginId", phone)
                .addFormDataPart("countryCode", "CN")
                .build();
        Request request = new Request.Builder()
                .url("https://passport.aliyundrive.com/newlogin/sms/send.do?appName=aliyun_drive")
                .method("POST", body)
                .addHeader("Cookie",cookie)
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        return JSONUtil.parseObj(result);
    }

    /**
     * 手机验证码登录阿里云
     * @param phone
     */
    public static JSONObject smsLogin(String phone,String token,String smsCode,String Cookie) throws Exception {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("phoneCode", "86")
                .addFormDataPart("loginId", phone)
                .addFormDataPart("countryCode", "CN")
                .addFormDataPart("smsCode", smsCode)
                .addFormDataPart("smsToken", token)
                .addFormDataPart("keepLogin", "false")
                .build();
        Request request = new Request.Builder()
                .url("https://passport.aliyundrive.com/newlogin/sms/login.do?appName=aliyun_drive")
                .method("POST", body)
                .addHeader("Cookie", Cookie)
                .build();
        Response response = client.newCall(request).execute();
        return getAliLoginInfo(response.body().string());
    }


    /**
     * 获得阿里云登录信息
     * @param response
     * @return
     */
    public static JSONObject getAliLoginInfo(String response) throws Exception {
        JSONObject json = JSONUtil.parseObj(response);
        String titleMsg = json.getJSONObject("content").getJSONObject("data").getStr("titleMsg");
        if (StrUtil.isNotEmpty(titleMsg)){//异常信息返回
            return json;
        }
        String redirectUrl = json.getJSONObject("content").getJSONObject("data").getStr("iframeRedirectUrl");
        if (StrUtil.isNotEmpty(redirectUrl)){//异常信息返回
            return json;
        }
        return doLogin(json);
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
        String accessToken = JSONUtil.parseObj(response).getJSONObject("pds_login_result").getStr("accessToken");
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
        HttpResponse res = HttpRequest.get("https://aliyundrive.com/sign/in").execute();
        return StrUtil.subBetween(res.body(), "client_id: '", "'");
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
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
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
        Connection data = connect.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.66")
                .header("referer","https://aliyundrive.com/");
        Document doc = data.get();
        data.response().headers();
        return data.response().cookies();
    }



    /**
     * 获取阿里云二维码
     */
    public static JSONObject getQrCodeUrl() throws Exception {
        Request request = new Request.Builder()
                .url("https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return JSONUtil.parseObj(response.body().string());
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
