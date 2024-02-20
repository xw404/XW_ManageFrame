package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowu.entity.SysMenu;
import com.xiaowu.entity.SysRole;
import com.xiaowu.entity.SysUser;
import com.xiaowu.mapper.SysMenuMapper;
import com.xiaowu.mapper.SysRoleMapper;
import com.xiaowu.mapper.SysUserMapper;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 86152
* @description 针对表【sys_user】的数据库操作Service实现
* @createDate 2023-12-28 22:21:04
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{


    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username",username));
    }

    /**
     * 根据用户ID返回权限信息
     * @param userId
     * @return 返回权限信息格式如下： 角色编码+权限编码
     * ROLE_admin, ROLE_common, system:user:resetPwd,system:role:delete,system:user:list,system:menu:query,system:menu:list,system:menu:add,system:user:delete
     */
    @Override
    public String getUserAuthorityInfo(Long userId) {
        StringBuffer authority = new StringBuffer();
        // 根据用户id获取所有的角色
        List<SysRole> roleList = sysRoleMapper.selectList(new QueryWrapper<SysRole>
                ().inSql("id", "SELECT ROLE_ID FROM SYS_USER_ROLE WHERE USER_ID=" + userId));
        if(roleList.size()>0){
            String roleCodeStrs = roleList.stream().map(r -> "ROLE_" +
                    r.getCode()).collect(Collectors.joining(","));
            authority.append(roleCodeStrs);
        }
        //根据角色 获取菜单权限 要求去重复 所以使用SET集合
        Set<String> menuCodeSet=new HashSet<>();
        for(SysRole sysRole:roleList){
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>()
                    .inSql("id", "SELECT MENU_ID FROM SYS_ROLE_MENU WHERE ROLE_ID=" + sysRole.getId()));
            for(SysMenu sysMenu:sysMenuList){
                String perms=sysMenu.getPerms();
                if(StringUtil.isNotEmpty(perms)){
                    menuCodeSet.add(perms);
                }
            }
        }
        if(menuCodeSet.size()>0){
            authority.append(",");
            //权限编码拼接
            String menuCodeStrs = menuCodeSet.stream().collect(Collectors.joining(","));
            authority.append(menuCodeStrs);
        }
        System.out.println("-----------------authority--------------------:"+authority.toString());
        return authority.toString();
    }
}




