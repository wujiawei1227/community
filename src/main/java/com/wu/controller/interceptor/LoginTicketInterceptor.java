package com.wu.controller.interceptor;

import com.wu.pojo.Login_Ticket;
import com.wu.pojo.Message;
import com.wu.pojo.User;
import com.wu.service.MessageService;
import com.wu.service.UserService;
import com.wu.utils.CookieUtil;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-29 09:12
 **/

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

        @Autowired
        private UserService service;
        @Autowired
        private HostHolder holder;
        @Autowired
        private MessageService messageService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket!=null)
        {
            Login_Ticket ticketByTicket = service.getTicketByTicket(ticket);
            //检测凭证是否有效
            if (ticketByTicket!=null&&ticketByTicket.getStatus()==0&&ticketByTicket.getExpired().after(new Date()))
            {//根据凭证查询用户
                User userById = service.findUserById(ticketByTicket.getUserId());
                //在本次请求中持有该用户
                holder.setUser(userById);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user=holder.getUser();
        if (user!=null&&modelAndView!=null)
        {
            modelAndView.addObject("loginuser",user);
            modelAndView.addObject("unReadMessageCount",messageService.findLetterUnreadCount(user.getId(),null));

        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        holder.clear();
    }
}
