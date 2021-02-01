package com.wu.service;

import com.wu.dao.CommentMapper;
import com.wu.pojo.Comment;
import com.wu.pojo.CommunityConstant;
import com.wu.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-31 18:07
 **/
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper mapper;
    @Autowired
    private SensitiveFilter filter;
    @Autowired
    private DiscussPortService discussPortService;

    public List<Comment> findCommentByEntity(int entityType,int entityId,int offset,int limit)
    {
        return mapper.findCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId)
    {
        return mapper.selectCountByEntity(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //过滤敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(filter.filter(comment.getContent()));
        int i = mapper.insertComment(comment);
        //更新帖子评论数量
        if (comment.getEntityType()==ENTITY_TYPE_POST){
            int count = mapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
             discussPortService.updateCommentCount(comment.getEntityId(), count);
        }

        return i;
    }
}
