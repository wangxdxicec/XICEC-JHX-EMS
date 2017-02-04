package com.zhenhappy.ems.action;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/1/12.
 */
public class SMSUtil {
    public static final String VALIDATE_PHONE_CODE = "VALIDATE_PHONE_CODE";
    public static final String VALIDATE_PHONE = "VALIDATE_PHONE";
    public static final String SEND_CODE_TIME = "SEND_CODE_TIME";
    static String httpUrl = "http://apis.baidu.com/kingtto_media/106sms/106sms";
    static String httpUrlEx = "http://113.106.91.228:9000/WebService.asmx/mt2?Sn=SDK100&Pwd=123321";
    final static String API_KEY = "xxxx";

    @Autowired
    TaskExecutor taskExecutor;// 注入Spring封装的异步执行器

    public static String send(String phone, String content) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            String httpArg = "mobile=" + phone + "&content=" + URLEncoder.encode(content, "UTF-8") + "&tag=2";
            httpUrl = httpUrl + "?" + httpArg;
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", API_KEY);
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void sendEx(final String phone, final String content) {
        StringBuffer sbf = new StringBuffer();

        taskExecutor.execute(new Runnable() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String httpArg = "mobile=" + phone + "&content=" + content;
                    httpUrl = httpUrlEx + "?" + httpArg;
                    HttpGet post = new HttpGet(httpArg);
                    HttpResponse response = httpClient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        System.out.println("短信验证码发送成功");
                    } else {
                        System.out.println("短信验证码发送失败");
                    }
                } catch (Exception e) {
                    System.out.println("短信验证码发送失败");
                }
            }
        });
    }
}
