package com.xiaowu.util;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 吴策
 * @Date 2024/02/21 21:29
 * @Description
 */
public class MqttUtils {

    @Value("${oneNet.userId}")
    private static String oneNetUserId="369075";
    @Value("${oneNet.userAccesskey}")
    private static String oneNeuserAccesskey="3XmyUxtVDKuFoR+rr3QYUciCiJEcBfFM4NeFrMgku4nqknzoDRehomaCvDvHXxE1O9tPKgmcECT/gyAAk73CAQ==";

    public static String queryDeviceDataInfo(String productId, String deviceName) throws Exception {

        String token = OneNetUtils.creatOneNetToken(oneNetUserId, oneNeuserAccesskey);
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",token);
        //参数
        Map<String, Object> param = new HashMap<>();
        param.put("product_id", productId);
        param.put("device_name", deviceName);
        String url = "https://iot-api.heclouds.com/thingmodel/query-device-property?product_id={product_id}&device_name={device_name}";
        //请求地址、请求体以及返回参数类型
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<String>(httpHeaders),
                Map.class,
                param);
        //System.out.println(JSONUtil.toJsonStr(response.getBody()));
        if(response.getBody().get("code").toString().equals("0")){
            return JSONUtil.toJsonStr(response.getBody().get("data"));
        }
        //设备不存在（没有数据）
        return null;
    }
}
