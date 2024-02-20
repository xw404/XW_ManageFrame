package com.xiaowu.common.exception;

import com.xiaowu.common.Result;
import com.xiaowu.util.StringUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author 吴策
 * @Date 2023/12/29 13:23
 * @Description
 */
//全局异常的捕获处理和请求参数的增强。
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        System.out.println("运行时异常：--------------:" + e.getMessage());
        if(e.getMessage().equals("不允许访问")){
            return Result.error("500", "抱歉，您没有权限，请联系管理员");
        }
        return Result.error("500", e.getMessage());
    }
}