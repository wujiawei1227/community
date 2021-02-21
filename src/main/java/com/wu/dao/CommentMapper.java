package com.wu.dao;

import com.wu.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentMapper {
    List<Comment> findCommentByEntity(int entityType,int entityId,int offset,int limit);
    int selectCountByEntity(int  entityType,int entityId);
    int insertComment(Comment comment);
    Comment selectCommentById(int id);
}
