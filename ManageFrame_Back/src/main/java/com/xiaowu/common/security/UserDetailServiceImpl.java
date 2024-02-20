package com.xiaowu.common.security;

import com.xiaowu.entity.SysUser;
import com.xiaowu.service.SysUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义UserDetailsService
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws
                                                           UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名或者密码错误！");
        } else if ("1".equals(sysUser.getStatus())) {
            //也可以自定义一账户被锁定的异常
            throw new RuntimeException("该用户账号已被禁用，具体联系管理员！");
        }
        //返回一个UserDetails对象
        return new User(sysUser.getUsername(), sysUser.getPassword(), getUserAuthority(sysUser.getId()));
    }

    //用户的权限信息 TODO
    public List<GrantedAuthority> getUserAuthority(Long userId) {
        // 格式 角色编码+权限编码 ROLE_admin,ROLE_common,system:user:resetPwd,system:role:delete,system:user:list
        String authority=sysUserService.getUserAuthorityInfo(userId);
        //返回字符格式的权限编码
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}