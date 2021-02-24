package com.wu.config;

import com.wu.pojo.CommunityConstant;
import com.wu.utils.CommunityUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-22 15:54
 **/


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discusspost/insert",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                ).hasAnyAuthority(
                AUTHORITY_ADMIN,
                AUTHORITY_USER,
                AUTHORITY_MODERATOR
        ).antMatchers(
                "/discusspost/top",
                            "/discusspost/wonderful"
        ).hasAnyAuthority(
                AUTHORITY_MODERATOR
        ).antMatchers(
                "/discusspost/delete"
        ).hasAnyAuthority(
                AUTHORITY_ADMIN
        ).anyRequest().permitAll().and().csrf().disable();
        //权限不足时
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登录时
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestWith))
                        {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer=response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"您还没有登录"));
                        }else {
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足时
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        //判断是否为异步请求，如果是异步请求则返回JSON
                        String xRequestWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestWith))
                        {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer=response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"您还没有访问此功能的权限"));
                        }else {
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });
        //Security底层默认会拦截logout请求 进行退出处理
        //覆盖它的默认逻辑，才能执行我们自己的代码
        http.logout().logoutUrl("/securitylogout");
    }
}
