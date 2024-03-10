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
import com.xiaowu.entity.*;
import com.xiaowu.mapper.SysLinedataMsgMapper;
import com.xiaowu.service.*;
import com.xiaowu.util.MqttUtils;
import com.xiaowu.util.OneNetUtils;
import com.xiaowu.util.StringUtil;
import com.xiaowu.util.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
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
    @Resource
    private SysNoticeService sysNoticeService;

    @Resource
    private SysIotDeviceService sysIotDeviceService;

    @Resource
    private SysLinedataMsgService sysLinedataMsgService;

    @Resource
    private SysIotDeviceLinedataService sysIotDeviceLinedataService;

    /**
     * 查询用户所绑定的设备预警消息
     */
    @GetMapping("/queryLineMsg/{userId}")
    public Result changeLedState(@PathVariable(value = "userId") String userId) throws Exception {
        //根据用户id找到自己的设备
        String deviceName = "";
        String productId = "";
        List<SysIotDevice> list = sysIotDeviceService.list();
        if (list.size() < 1 || list.isEmpty()) {
            return Result.error(SYSTEM_ERROR);
        }
        for (SysIotDevice sysIotDevice : list) {
            String userListStr = sysIotDevice.getUserList();
            if (userListStr.contains(userId)) {
                deviceName = sysIotDevice.getDeviceName();
                productId = sysIotDevice.getProductId();
                break;
            }
        }
        LambdaQueryWrapper<SysLinedataMsg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysLinedataMsg::getDeviceName,deviceName);
        wrapper.eq(SysLinedataMsg::getProductId,productId);
        wrapper.orderByDesc(SysLinedataMsg::getId);
        wrapper.last("LIMIT 10");
        List<SysLinedataMsg> resList = sysLinedataMsgService.list(wrapper);
        return Result.success(resList);
    }
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
    @Transactional
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
                    else if (identifier.equals("fan")){
                        sysIotDeviceData.setFan(value.equals("true")? 1:0);
                    }
                    else if (identifier.equals("has_fire")){
                        sysIotDeviceData.setHasFire(value.equals("true")? 1:0);
                    }else if (identifier.equals("has_people")){
                        sysIotDeviceData.setHasPeople(value.equals("true")? 1:0);
                    }else if (identifier.equals("led")){
                        sysIotDeviceData.setLed(value.equals("true")? 1:0);
                    }
                    // 将时间戳转换为LocalDateTime
                    String timeStr = TimeUtils.formatTimestampInMillis(Long.parseLong(time+""));
                    sysIotDeviceData.setTime(timeStr);

                }

                //TODO 设置报警阈值，
                SysLinedataMsg sysLinedataMsg = new SysLinedataMsg();
                sysLinedataMsg.setDeviceName(deviceName);
                sysLinedataMsg.setProductId(productId);
                /**
                 * 查询设置的阈值
                 */
                SysIotDeviceLinedata sysIotDeviceLinedata = sysIotDeviceLinedataService.list().get(0);
                if (Float.parseFloat(sysIotDeviceLinedata.getTempH()) <= Float.parseFloat(sysIotDeviceData.getTemp())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="高温预警";
                    String tipMsg ="当前温度达到了【"+sysIotDeviceData.getTemp()+"℃】,可能会导致蚕叶片失水，蚕宝宝食欲减弱，生长缓慢慢，甚至停止摄食哟！请及时调整温度。";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getTempL()) > Float.parseFloat(sysIotDeviceData.getTemp())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="低温预警";
                    String tipMsg ="当前温度低至【"+sysIotDeviceData.getTemp()+"℃】,可能会导致蚕宝宝患病或变得易感，要及时调节温度哟！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getHumiH()) <= Float.parseFloat(sysIotDeviceData.getHumi())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="高湿度预警";
                    String tipMsg ="环境湿度达到了【"+sysIotDeviceData.getTemp()+"%】,过高的湿度会限制蚕体水分的散发，导致蚕的体温过高，呼吸量增多，脉搏加快，不利于生长，请及时通风或者采取其他降湿措施哟！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getHumiL()) > Float.parseFloat(sysIotDeviceData.getHumi())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="低湿度预警";
                    String tipMsg ="环境湿度达到了【"+sysIotDeviceData.getTemp()+"%】,桑叶可能会迅速失去水分，变得干燥，这不仅降低了其口感和营养价值，还可能使蚕宝宝难以下咽，导致摄食量减少！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getLightH()) <= Float.parseFloat(sysIotDeviceData.getLight())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="强光预警";
                    String tipMsg ="当前光强达到了【"+sysIotDeviceData.getLight()+"%】过高的光照强度会使桑蚕感到不适，桑蚕和桑叶都会水分蒸发哟!";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getLightL()) > Float.parseFloat(sysIotDeviceData.getLight())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="黑暗预警";
                    String tipMsg ="当前环境可见度太低了【"+sysIotDeviceData.getTemp()+"%】长时间光照不足可能影响桑蚕的代谢过程，降低其抵抗力，使其更容易受到疾病和寄生虫的侵害哟，要及时调整室内光照哟";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getMq2H()) <= Float.parseFloat(sysIotDeviceData.getMq2())) {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="烟雾预警";
                    String tipMsg ="当前温烟雾浓度过高【"+sysIotDeviceData.getTemp()+"%】请尽快通风并查看，避免损失！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (Float.parseFloat(sysIotDeviceLinedata.getMq2L()) > Float.parseFloat(sysIotDeviceData.getMq2())) {
                    System.out.println("低浓度不处理");
                }

                if (sysIotDeviceLinedata.getPeople() == sysIotDeviceData.getHasPeople()+"") {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="人体预警";
                    String tipMsg ="人体检测异常，请查看养殖场所是否有人";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (sysIotDeviceLinedata.getFire() == sysIotDeviceData.getHasFire()+"") {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="火焰预警";
                    String tipMsg ="火焰产生，请查看养殖场所火源情况";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (sysIotDeviceLinedata.getFan() == sysIotDeviceData.getFan()+"") {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="风扇预警";
                    String tipMsg ="请及时调整并查看风扇状态哟！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
                }

                if (sysIotDeviceLinedata.getLed() == sysIotDeviceData.getLed()+"") {
                    sysLinedataMsg.setCreateTime(new Date());
                    String tipTitle ="电灯";
                    String tipMsg ="请及时调整并查看电灯状态哟！";
                    sysLinedataMsg.setTitle(tipTitle);
                    sysLinedataMsg.setMsg(tipMsg);
                    //移除之前的信息，以免数据太重复
                    sysLinedataMsgService.remove(new LambdaQueryWrapper<SysLinedataMsg>().eq(SysLinedataMsg::getTitle,tipTitle));
                    sysLinedataMsgService.save(sysLinedataMsg);
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
