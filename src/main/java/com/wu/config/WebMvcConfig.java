package com.wu.config;

import com.wu.controller.interceptor.LoginRequiredInterceptor;
import com.wu.controller.interceptor.LoginTicketInterceptor;
import com.wu.controller.interceptor.MessageInterceptor;
import com.wu.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-29 10:08
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor interceptor;
@Autowired
private MessageInterceptor messageInterceptor;
@Autowired
private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
               registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");

    }
}
