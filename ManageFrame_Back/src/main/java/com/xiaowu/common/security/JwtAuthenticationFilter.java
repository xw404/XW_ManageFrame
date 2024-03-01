package com.xiaowu.common.security;


import com.xiaowu.common.CheckResult;
import com.xiaowu.common.constant.JwtConstant;
import com.xiaowu.entity.SysUser;
import com.xiaowu.service.SysUserService;
import com.xiaowu.util.JwtUtils;
import com.xiaowu.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 过滤器web层面 servlet    BasicAuthenticationFilter extend OncePerRequestFilter 请求只经过这个过滤器一次
 */
@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UserDetailServiceImpl userDetailService;
    private static final String URL_WHITELIST[] ={
            "/login",
            "/logout",
            "/captcha",
            "/weChat",
            "/password",
            "/image/**"
    } ;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token=request.getHeader("token");
        ////请求地址
        String requestURI = request.getRequestURI();
        System.out.println("请求url:"+requestURI);
        // 如果token是空或者url在白名单里 则放行 让后面的springsecurity认证过滤器去认证
        if(StringUtil.isEmpty(token)|| new ArrayList<String>(Arrays.asList(URL_WHITELIST)).contains(request.getRequestURI())){
            chain.doFilter(request,response);
            return;
        }
        //验证jwt
        CheckResult checkResult = JwtUtils.validateJWT(token);
        if(!checkResult.isSuccess()){
            switch (checkResult.getErrCode()){
                case JwtConstant.JWT_ERRCODE_NULL: throw new JwtException("Token 不存在");
                case JwtConstant.JWT_ERRCODE_FAIL: throw new JwtException("Token 验证不通过");
                case JwtConstant.JWT_ERRCODE_EXPIRE: throw new JwtException("Token过期");
            }
        }
        //认证成功
        Claims claims=JwtUtils.parseJWT(token);
        String username=claims.getSubject();
        //获取用户实体信息
        SysUser sysUser = sysUserService.getByUsername(username);
        //封装Authentication的实现类对象，
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,null,userDetailService.getUserAuthority(sysUser.getId()));
        //存入上下文，实现原理ThreadLocal
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request,response);
    }
}
