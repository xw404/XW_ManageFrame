package com.xiaowu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName sys_iot_device_data
 */
@Data
@TableName(value ="sys_iot_device_data")
public class SysIotDeviceData implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 风扇状态
     */
    private Integer fan;

    /**
     * 是否检测到火源
     */
    private Integer hasFire;

    /**
     * 是否检测到人
     */
    private Integer hasPeople;

    /**
     * 灯状态
     */
    private Integer led;

    /**
     * 光照强度%
     */
    private String light;

    /**
     * 湿度%
     */
    private String humi;

    /**
     * 可燃气体浓度
     */
    private String mq2;

    /**
     * 温度%
     */
    private String temp;

    /**
     * 时间
     */
    private String time;

}