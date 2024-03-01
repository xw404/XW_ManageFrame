package com.xiaowu.controller;

/**
 * @Author 吴策
 * @Date 2024/02/25 12:07
 * @Description
 */

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowu.common.Result;
import com.xiaowu.common.exception.GlobalExceptionHandler;
import com.xiaowu.entity.SysIotDevice;
import com.xiaowu.entity.SysIotDeviceData;
import com.xiaowu.entity.SysNotice;
import com.xiaowu.service.SysIotDeviceDataService;
import com.xiaowu.service.SysIotDeviceService;
import com.xiaowu.service.SysNoticeService;
import com.xiaowu.util.MqttUtils;
import com.xiaowu.util.OneNetUtils;
import com.xiaowu.util.StringUtil;
import com.xiaowu.util.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaowu.common.enums.ResultCodeEnum.SYSTEM_ERROR;

/**
 * 微信数据接口
 */

@RestController
@RequestMapping("/weChat")
public class WeChatController {

    @Value("${oneNet.userId}")
    private static String oneNetUserId="369075";
    @Value("${oneNet.userAccesskey}")
    private static String oneNeuserAccesskey="3XmyUxtVDKuFoR+rr3QYUciCiJEcBfFM4NeFrMgku4nqknzoDRehomaCvDvHXxE1O9tPKgmcECT/gyAAk73CAQ==";
    private int t =1708688970;

    @Resource
    private SysNoticeService sysNoticeService;

    @Resource
    private SysIotDeviceService sysIotDeviceService;

    @Resource
    private SysIotDeviceDataService sysIotDeviceDataService;

    /**
     * 查询展示的公告1条
     */
    @GetMapping("/notice")
    public Result notice() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysNotice::getUpdateTime);
        wrapper.last("LIMIT 1");
        List<SysNotice> list = sysNoticeService.list(wrapper);
        return Result.success(list.get(0));
    }

    /**
     * 查询设备实时数据
     * @return
     */
    @GetMapping("/queryDeviceData/{userId}")
    public Result queryDeviceData(@PathVariable(value = "userId") String userId) throws Exception {
        //根据用户id找到自己的设备
        String deviceName ="";
        String productId ="";
        List<SysIotDevice> list = sysIotDeviceService.list();
        if(list.size() < 1 || list.isEmpty()){
            return Result.error("505","该用户未绑定设备");
        }
        for (SysIotDevice sysIotDevice : list) {
            String userListStr = sysIotDevice.getUserList();
            if(userListStr.contains(userId)){
                deviceName =sysIotDevice.getDeviceName();
                productId =sysIotDevice.getProductId();
                break;
            }
        }
        //从mqtt服务器获取数据，更加准确，不从数据库
        String res = MqttUtils.queryDeviceDataInfo(productId, deviceName);
        if(StringUtil.isEmpty(res)){
            Result.error("500","设备不存在没有数据或者其他情况--");
        }else {
            //解析数据并返回
            ObjectMapper mapper = new ObjectMapper();
            try {
                // JSON数组，这里将其转换为List<Map<String, Object>>
                List<Map<String, Object>> jsonDataList = mapper.readValue(res, List.class);
                // 输出转换后的数据
                SysIotDeviceData sysIotDeviceData = new SysIotDeviceData();
                sysIotDeviceData.setProductId(productId);
                sysIotDeviceData.setDeviceName(deviceName);
                for (Map<String, Object> item : jsonDataList) {
                    Object identifier = item.get("identifier");
                    Object time = item.get("time");
                    Object value = item.get("value");
                    System.out.println("identifier="+identifier+" time="+time+"value="+value+"-------------/n");
                    if(identifier.equals("humi")){
                        sysIotDeviceData.setHumi((String) value);
                    }else if (identifier.equals("light")){
                        sysIotDeviceData.setLight((String) value);
                    }else if (identifier.equals("mq2")){
                        sysIotDeviceData.setMq2((String) value);
                    }else if (identifier.equals("temp")){
                        sysIotDeviceData.setTemp((String) value);
                    }
                    //TODO
                    //else if (identifier.equals("fan")){
                    //    sysIotDeviceData.setFan(value.equals("true")? 1:0);
                    //}
                    else if (identifier.equals("has_fire")){
                        sysIotDeviceData.setHasFire(value.equals("true")? 1:0);
                    }else if (identifier.equals("has_people")){
                        sysIotDeviceData.setHasPeople(value.equals("true")? 1:0);
                    }else if (identifier.equals("led")){
                        sysIotDeviceData.setLed(value.equals("true")? 1:0);
                    }
                    sysIotDeviceData.setFan(0);
                    // 将时间戳转换为LocalDateTime
                    String timeStr = TimeUtils.formatTimestamp(t);
                    t++;
                    sysIotDeviceData.setTime(timeStr);
                }
                return Result.success(sysIotDeviceData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.error(SYSTEM_ERROR);//系统异常
    }

    @GetMapping("/led/{userId}/{flag}")
    public Result changeLedState(@PathVariable(value = "userId") String userId ,@PathVariable(value = "flag")Long flag) throws Exception {
        //根据用户id找到自己的设备
        String deviceName ="";
        String productId ="";
        List<SysIotDevice> list = sysIotDeviceService.list();
        if(list.size() < 1 || list.isEmpty()){
            return Result.error(SYSTEM_ERROR);
        }
        for (SysIotDevice sysIotDevice : list) {
            String userListStr = sysIotDevice.getUserList();
            if(userListStr.contains(userId)){
                deviceName =sysIotDevice.getDeviceName();
                productId =sysIotDevice.getProductId();
                break;
            }
        }
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

    @GetMapping("/fan/{userId}/{flag}")
    public Result changeFanState(@PathVariable(value = "userId") String userId ,@PathVariable(value = "flag")Long flag) throws Exception {
        //根据用户id找到自己的设备
        String deviceName ="";
        String productId ="";
        List<SysIotDevice> list = sysIotDeviceService.list();
        if(list.size() < 1 || list.isEmpty()){
            return Result.error(SYSTEM_ERROR);
        }
        for (SysIotDevice sysIotDevice : list) {
            String userListStr = sysIotDevice.getUserList();
            if(userListStr.contains(userId)){
                deviceName =sysIotDevice.getDeviceName();
                productId =sysIotDevice.getProductId();
                break;
            }
        }
        String token = OneNetUtils.creatOneNetToken(oneNetUserId, oneNeuserAccesskey);
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",token);
        //参数
        Map<String, Object> param = new HashMap<>();
        param.put("product_id", productId);
        param.put("device_name", deviceName);
        Map<String, Boolean> fan = new HashMap<>();
        fan.put("fan",flag==1? true:false);
        param.put("Params", fan);
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
