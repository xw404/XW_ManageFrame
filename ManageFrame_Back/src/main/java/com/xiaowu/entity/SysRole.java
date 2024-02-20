package com.xiaowu.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (SysRole)表实体类
 *
 * @author makejava
 * @since 2023-12-29 17:54:58
 */
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity implements Serializable {
    //角色名称
    private String name;
    //角色权限字符串
    private String code;

}
