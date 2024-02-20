package com.xiaowu.config;

import com.xiaowu.common.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

/**
 * @Author 吴策
 * @Date 2023/12/29 11:20
 * @Description SpringSecurity的配置类
 */
@Configuration
@EnableWebSecurity
//全局方法配置
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailServiceImpl userDetailService;

    //登录成功
    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    //登录失败
    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Resource
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    //不需要拦截的请求
    private static final String URL_WHITELIST[] = {
            "/login",
            "/logout",
            "/captcha",
            "/password",
            "/image/**",
            "/test/**"
    };

    //密码加密方式注入容器中
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
// 开启跨域 和 csrf攻击 关闭
        http
                //开启跨域和防止跨站攻击
                .cors()
                .and()
                .csrf().disable()
                //登录登出配置
                .formLogin()
                .successHandler(loginSuccessHandler)//成功
                .failureHandler(loginFailureHandler)//失败
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)//成功
                .and()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //请求认证的配置
                .authorizeRequests()
                //允许允许匿名访问的白名单
                .antMatchers(URL_WHITELIST).permitAll()
                // 配置权限 TODO
                //.antMatchers("/hello2").hasAuthority("/hello2")  在配置里面添加
                //除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                //异常处理器
                .and()
                .exceptionHandling()
                //认证失败处理器
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //自定义过滤器（在默认的springsecurity之前过滤）
                .and()
                .addFilter(jwtAuthenticationFilter());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //super.configure(auth);
        auth.userDetailsService(userDetailService);
    }
}
