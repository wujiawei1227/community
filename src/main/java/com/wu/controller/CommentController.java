package com.wu.controller;

import com.wu.event.EventProducer;
import com.wu.pojo.Comment;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Discuss_Post;
import com.wu.pojo.Event;
import com.wu.service.CommentService;
import com.wu.service.DiscussPortService;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-01 11:39
 **/

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService service;
    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private HostHolder holder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussId,
                             String entityType,String entityId,String targetId,String content)
    {   Comment comment=new Comment();
    comment.setEntityId(Integer.parseInt(entityId));
    comment.setEntityType(Integer.parseInt(entityType));
    comment.setTargetId(Integer.parseInt(targetId));
    comment.setContent(content);
        comment.setUserId(holder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        service.addComment(comment);
        //触发评论事件
        Event event=new Event().setTopic(TOPIC_COMMENT)
                .setUserId(holder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussId);
        if (comment.getEntityType()==ENTITY_TYPE_POST)
        {
            Discuss_Post target = discussPortService.findById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType()==ENTITY_TYPE_COMMENT)
        {
            Comment target = service.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);
        if (comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //出发发帖事件
         event=new Event().setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussId);
            eventProducer.fireEvent(event);
        }
        return "redirect:/discusspost/detail/"+discussId;
    }
}
