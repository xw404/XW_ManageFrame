package com.xiaowu.common.security;

import cn.hutool.json.JSONUtil;
import com.xiaowu.common.Result;
import com.xiaowu.common.enums.ResultCodeEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 吴策
 * @Date 2023/12/29 17:07
 * @Description
 * 认证失败异常处理
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 认证失败处理
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Result result = new Result(ResultCodeEnum.UNAUTHORIZED.code,ResultCodeEnum.UNAUTHORIZED.msg, "");
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(JSONUtil.toJsonStr(result).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}