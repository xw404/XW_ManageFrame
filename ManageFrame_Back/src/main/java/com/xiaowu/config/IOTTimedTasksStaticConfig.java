package com.xiaowu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaowu.entity.SysIotDevice;
import com.xiaowu.entity.SysIotDeviceData;
import com.xiaowu.service.SysIotDeviceDataService;
import com.xiaowu.service.SysIotDeviceService;
import com.xiaowu.util.MqttUtils;
import com.xiaowu.util.StringUtil;
import com.xiaowu.util.TimeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 物联网基本定时任务  间隔一定时间向数据库保存数据
 * （单线程）
 * 来源：
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
//@EnableScheduling   // 2.开启定时任务
public class IOTTimedTasksStaticConfig {
    private int t =1708688970;

    @Resource
    private SysIotDeviceService sysIotDeviceService;

    @Resource
    private SysIotDeviceDataService sysIotDeviceDataService;

    //3.添加定时任务
    //@Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    @Scheduled(fixedRate=30000)
    private void configureTasks() throws Exception {
        //查询所有设备
        List<SysIotDevice> list = sysIotDeviceService.list();
        for (SysIotDevice sysIotDevice : list) {
            String productId = sysIotDevice.getProductId();
            String deviceName = sysIotDevice.getDeviceName();
            String res = MqttUtils.queryDeviceDataInfo(productId, deviceName);
            if(StringUtil.isEmpty(res)){
                //保存到数据库
                System.out.println("设备不存在没有数据或者其他情况--");
            }else {
                //System.out.println("执行静态定时任务" + res);
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
                    sysIotDeviceDataService.save(sysIotDeviceData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}