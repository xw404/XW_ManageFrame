package com.xiaowu.common.security;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaowu.common.Result;
import com.xiaowu.entity.SysMenu;
import com.xiaowu.entity.SysRole;
import com.xiaowu.entity.SysUser;
import com.xiaowu.service.SysMenuService;
import com.xiaowu.service.SysRoleService;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 吴策
 * @Date 2023/12/29 11:37
 * @Description  登陆成功拦截器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse, Authentication authentication)
            throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        String username = authentication.getName();
        String token = JwtUtils.genJwtToken(username);
        SysUser currentUser = sysUserService.getByUsername(username);
        //设置currentUser的角色属性
        List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>()
                .inSql("id", "SELECT ROLE_ID FROM SYS_USER_ROLE WHERE USER_ID=" + currentUser.getId()));
        String roles = roleList.stream().map(sysRole -> sysRole.getName()).collect(Collectors.joining(","));
        currentUser.setRoles(roles);
        //根据用户id获取所有的菜单
        List<SysMenu> menuList = getMenu(currentUser.getId());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token",token);//token
        hashMap.put("currentUser",currentUser); //当前用户信息
        hashMap.put("menuList",menuList); //当前用户的菜单权限
        outputStream.write(JSONUtil.toJsonStr(Result.success(hashMap)).getBytes());
        outputStream.flush();//刷新
        outputStream.close();
    }

    /**
     * 得到用户的菜单列表
     *
     * @return
     */
    List<SysMenu> getMenu(Long userId){
        // 根据用户id获取所有的角色
        List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>()
                .inSql("id", "SELECT ROLE_ID FROM SYS_USER_ROLE WHERE USER_ID=" + userId));
        // 遍历角色，获取所有菜单权限
        Set<SysMenu> menuSet=new HashSet<>();
        for(SysRole sysRole:roleList){
            List<SysMenu> sysMenuList = sysMenuService.list(new  QueryWrapper<SysMenu>()
                    .inSql("id", "SELECT MENU_ID FROM SYS_ROLE_MENU WHERE ROLE_ID=" + sysRole.getId()));
            for(SysMenu sysMenu:sysMenuList){
                menuSet.add(sysMenu);
            }
        }
        List<SysMenu> sysMenuList=new ArrayList<>(menuSet);
        // 排序(根据顺序排序)
        sysMenuList.sort(Comparator.comparing(SysMenu::getOrderNum));
        //构建树形结构菜单
        List<SysMenu> menuList=sysMenuService.buildTreeMenu(sysMenuList);
        return menuList;
    }
}