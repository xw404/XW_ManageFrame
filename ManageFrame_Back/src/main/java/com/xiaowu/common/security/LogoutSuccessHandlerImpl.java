package com.xiaowu.common.security;

import cn.hutool.json.JSONUtil;
import com.xiaowu.common.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 吴策
 * @Date 2023/12/29 17:24
 * @Description 自定义Logout处理
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Authentication authentication) throws
                                                                                                        IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream =
                httpServletResponse.getOutputStream();
        outputStream.write(JSONUtil.toJsonStr(Result.success()).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
}
