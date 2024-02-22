package com.xiaowu.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaowu.common.Result;
import com.xiaowu.util.JwtUtils;
import com.xiaowu.util.OneNetUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 吴策
 * @Date 2023/12/26 21:38
 * @Description   测试MQTT通信
 */

@RestController
@RequestMapping("/test")
public class MqttController {


    @Value("${oneNet.userId}")
    private  String oneNetUserId="369075";
    @Value("${oneNet.userAccesskey}")
    private String oneNeuserAccesskey="3XmyUxtVDKuFoR+rr3QYUciCiJEcBfFM4NeFrMgku4nqknzoDRehomaCvDvHXxE1O9tPKgmcECT/gyAAk73CAQ==";
    private String productId="P9mkMEjYjc";
    private String deviceName= "Iot-monitor";

    @GetMapping("/test")
    public Result queryDeviceDataInfo() throws Exception {

        //TODO
        // 获取当前用户的详细信息
        //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (principal instanceof UserDetails) {
        //    // 直接获取UserDetails对象
        //    UserDetails userDetails = (UserDetails) principal;
        //    // 获取用户名
        //    String username = userDetails.getUsername();
        //}else {
        //    //匿名用户，不可能
        //}

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
        System.out.println(JSONUtil.toJsonStr(response));
        return Result.success();
   }
    @GetMapping("/led/{flag}")
    public Result changeLedState(@PathVariable(value = "flag")Long flag) throws Exception {
        String token = OneNetUtils.creatOneNetToken(oneNetUserId, oneNeuserAccesskey);
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",token);
        //参数
        Map<String, Object> param = new HashMap<>();
        param.put("product_id", productId);
        param.put("device_name", deviceName);
        Map<String, Boolean> led = new HashMap<>();
        led.put("led",flag==1? true:false);
        param.put("Params", led);
        String url = "https://iot-api.heclouds.com/thingmodel/set-device-property";
        JSONObject jsonObject = new JSONObject(param);
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toString(), httpHeaders);
        ResponseEntity<String> responsebody = restTemplate.exchange(
                url,
                HttpMethod.POST,
                formEntity,
                String.class);
        JSONObject res = JSONObject.parseObject(responsebody.getBody(), JSONObject.class);
        System.out.println(JSONUtil.toJsonStr(responsebody));
        System.out.println(JSONUtil.toJsonStr(res));
        return Result.success();
    }
}
