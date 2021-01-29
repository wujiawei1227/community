package com.wu.utils;

import com.wu.pojo.User;
import org.springframework.stereotype.Component;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-29 09:37
 **/
@Component
public class HostHolder  {
    private ThreadLocal<User> users =new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
