package com.wu.service;

import com.wu.dao.Discuss_PostMapper;
import com.wu.pojo.Discuss_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:07
 **/
@Service
public class DiscussPortService {
    @Autowired
    private Discuss_PostMapper mapper;
    public List<Discuss_Post> selectDiscussPort(int user_id,int offset,int limit){

        return mapper.selctDiscuss_post(user_id,offset,limit);
    }
    public int findDiscussPostRows(int id){
        return mapper.Discuss_portCount(id);
    }
}
