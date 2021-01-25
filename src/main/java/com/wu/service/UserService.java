package com.wu.service;

import com.wu.dao.UserMapper;
import com.wu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:51
 **/
@Service
public class UserService {
    @Autowired
    private UserMapper mapper;
    public User findUserById(int id){
        return mapper.findUserById(id);
    }
}
