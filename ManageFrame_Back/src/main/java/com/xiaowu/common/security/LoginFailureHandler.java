package com.xiaowu.common.security;

import cn.hutool.json.JSONUtil;
import com.xiaowu.common.Result;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 吴策
 * @Date 2023/12/29 11:45
 * @Description  登陆失败拦截器
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        String message = e.getMessage();
        if (e instanceof BadCredentialsException) {
            message = "用户名或者密码错误！";
        }
        outputStream.write(JSONUtil.toJsonStr(Result.error("500",message)).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
}
