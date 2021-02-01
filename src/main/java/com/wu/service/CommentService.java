package com.wu.service;

import com.wu.dao.CommentMapper;
import com.wu.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-31 18:07
 **/
@Service
public class CommentService {
    @Autowired
    private CommentMapper mapper;


    public List<Comment> findCommentByEntity(int entityType,int entityId,int offset,int limit)
    {
        return mapper.findCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId)
    {
        return mapper.selectCountByEntity(entityType,entityId);
    }
}
