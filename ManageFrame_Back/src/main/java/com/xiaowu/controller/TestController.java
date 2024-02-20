package com.xiaowu.controller;

import com.xiaowu.common.Result;
import com.xiaowu.entity.SysUser;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.JwtUtils;
import com.xiaowu.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/user/list")
    //@PreAuthorize("hasAnyAuthority('system2:user:list')")
    @PreAuthorize("hasRole('ROLE2_common')")
    public Result userList(@RequestHeader(required = false) String token){
        if(StringUtil.isNotEmpty(token)){
            Map<String,Object> resutMap=new HashMap<>();
            List<SysUser> userList = sysUserService.list();
            resutMap.put("userList",userList);
            return Result.success(resutMap);
        }else{
            return Result.error("401","没有权限访问");
        }
    }
    @GetMapping("/login")
    public Result token() {
        String token = JwtUtils.genJwtToken("xiahfud");
        return Result.success(token);
    }
}