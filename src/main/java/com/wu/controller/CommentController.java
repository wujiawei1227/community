package com.wu.controller;

import com.wu.pojo.Comment;
import com.wu.service.CommentService;
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
public class CommentController {
    @Autowired
    private CommentService service;
    @Autowired
    private HostHolder holder;

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

        return "redirect:/discusspost/detail/"+discussId;
    }
}
