package com.xiaowu.entity;


import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xiaowu.entity.dateTime.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (SysNotice)表实体类
 *
 * @author makejava
 * @since 2024-01-02 20:49:28
 */
@Data
@TableName("sys_notice")
public class SysNotice implements Serializable {
    private Integer id;
    
    private String title;
    
    private String content;

    @JsonSerialize(using= CustomDateTimeSerializer.class)
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    private String createUser;

}
