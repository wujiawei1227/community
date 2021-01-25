package com.wu.dao;

import com.wu.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:42
 **/
@Mapper
@Component
public interface UserMapper {
    User findUserById(int id);
}
