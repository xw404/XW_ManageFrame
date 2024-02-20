package com.xiaowu.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (SysUserRole)表实体类
 *
 * @author makejava
 * @since 2023-12-29 17:54:58
 */
@Data
@TableName("sys_user_role")
public class SysUserRole  implements Serializable{
    //用户角色主键ID@TableId
    private Long id;
    //用户ID
    private Long userId;
    //角色ID
    private Long roleId;

}
