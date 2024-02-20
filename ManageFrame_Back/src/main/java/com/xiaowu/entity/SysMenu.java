package com.xiaowu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * (SysMenu)表实体类
 *
 * @author makejava
 * @since 2023-12-29 17:54:58
 */
@Data
@TableName("sys_menu")
public class SysMenu extends BaseEntity implements Serializable{

    //菜单名称
    private String name;
    //菜单图标
    private String icon;
    //父菜单ID
    private Long parentId;
    //显示顺序
    private Integer orderNum;
    //路由地址
    private String path;
    //组件路径
    private String component;
    //菜单类型（M目录 C菜单 F按钮）
    private String menuType;
    //权限标识
    private String perms;

    //子菜单(数据库表中不存在)
    @TableField(exist = false)
    private ArrayList<SysMenu> children = new ArrayList<>();  //默认菜单设置初值

}
