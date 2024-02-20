package com.xiaowu.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (SysRoleMenu)表实体类
 *
 * @author makejava
 * @since 2023-12-29 17:54:58
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {
    //角色菜单主键ID@TableId
    private Long id;

    //角色ID
    private Long roleId;
    //菜单ID
    private Long menuId;

}
