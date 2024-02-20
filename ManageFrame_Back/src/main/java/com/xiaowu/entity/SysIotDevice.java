package com.xiaowu.entity;

import java.util.Date;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (SysIotDevice)表实体类
 *
 * @author makejava
 * @since 2024-01-29 21:29:26
 */
@Data
@TableName("sys_iot_device")
public class SysIotDevice extends BaseEntity implements Serializable  {
    //产品id(对应oneNet)
    private String productId;
    //设备名称(对应oneNet)
    private String deviceName;
    //拥有此设备的用户id
    private String userList;
    //拥有此设备的用户集合
    @TableField(exist = false)
    private List<SysUser> sysUserList;
}
