package com.fortunate.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description
 */
public class HttpClientUtil {
  
  /**
   * 发送GET方式请求
   * @param url 请求地址
   * @param paramMap 请求参数
   * @return json格式的字符串请求结果
   */
  public static String doGet(String url, Map<String, String> paramMap) {
    // 创建Httpclient对象
    CloseableHttpClient httpClient = HttpClients.createDefault();
    
    String result = "";
    CloseableHttpResponse response = null;
    
    try {
      URIBuilder builder = new URIBuilder(url);
      if (paramMap != null) {
        for (String key : paramMap.keySet()) {
          builder.addParameter(key, paramMap.get(key));
        }
      }
      URI uri = builder.build();
      
      // 创建GET请求
      HttpGet httpGet = new HttpGet(uri);
      
      // 发送请求
      response = httpClient.execute(httpGet);
      
      // 判断响应状态（200）
      int statusCode = response.getStatusLine()
              .getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        // 借助httpclient工具类，获取响应内容
        result = EntityUtils.toString(response.getEntity(), "UTF-8");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        assert response != null;
        response.close();
        httpClient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return result;
  }
}
