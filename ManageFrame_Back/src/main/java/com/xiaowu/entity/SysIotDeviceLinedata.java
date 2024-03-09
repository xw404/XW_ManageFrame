package com.xiaowu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName sys_iot_device_linedata
 */
@Data
@TableName(value ="sys_iot_device_linedata")
public class SysIotDeviceLinedata implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String tempH;

    private String tempL;

    private String humiH;

    private String humiL;

    private String lightH;

    private String lightL;

    private String mq2H;

    private String mq2L;

    private String fire;

    private String people;

    private String led;

    private String fan;

}