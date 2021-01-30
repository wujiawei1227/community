package com.wu.controller.interceptor;

import com.wu.annotation.LoginRequired;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-30 09:24
 **/

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder holder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       if (handler instanceof HandlerMethod)
       {
           HandlerMethod handlerMethod=(HandlerMethod) handler;
           Method method = handlerMethod.getMethod();
           LoginRequired annotation = method.getAnnotation(LoginRequired.class);
           if (annotation!=null&&holder.getUser()==null)
           {
               response.sendRedirect(request.getContextPath()+"/login");
               return false;
           }
       }

        return true;
    }
}
