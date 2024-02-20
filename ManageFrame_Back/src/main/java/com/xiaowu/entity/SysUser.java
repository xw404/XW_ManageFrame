package com.xiaowu.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * (SysUser)表实体类
 */
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity implements Serializable {
    //用户名
    private String username;
    //密码
    private String password;
    //用户头像
    private String avatar;
    //用户邮箱
    private String email;
    //手机号码
    private String phonenumber;
    //最后登录时间
    private String loginDate;
    //帐号状态（0正常 1停用）
    private String status;
    //角色信息（可以是多个）
    @TableField(exist = false)
    private String roles;
    //角色信息
    @TableField(exist = false)
    private List<SysRole> sysRoleList;
    //确认新密码
    @TableField(exist = false)
    private String newPassword;
    //旧密码（前端传来的）
    @TableField(exist = false)
    private String oldPassword;

}
