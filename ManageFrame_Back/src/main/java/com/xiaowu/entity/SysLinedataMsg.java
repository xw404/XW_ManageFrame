package com.xiaowu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName sys_linedata_msg
 */
@Data
@TableName(value ="sys_linedata_msg")
public class SysLinedataMsg extends BaseEntity implements Serializable {
    private String productId;
    private String deviceName;
    /**
     * 标题
     */
    private String title;

    /**
     * 预警提示内容
     */
    private String msg;

}