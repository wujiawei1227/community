package com.wu.controller;

import com.wu.event.EventProducer;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Event;
import com.wu.pojo.Page;
import com.wu.pojo.User;
import com.wu.service.FollowService;
import com.wu.service.UserService;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-19 09:48
 **/
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService service;
    @Autowired
    private HostHolder holder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId)
    {
        User user=holder.getUser();
        service.follow(user.getId(),entityType,entityId);
        //出发关注事件
        Event event=new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(holder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0,"已关注");
    }
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId)
    {
        User user=holder.getUser();
        service.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取关");
    }
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model)
    {
        User userById = userService.findUserById(userId);
        if (userById == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",userById);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)service.findFolloweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String,Object>> userList=service.findFollowees(userId,page.getOffset(),page.getLimit());
        if (userList!=null)
        {
            for (Map<String, Object> map:userList){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/followee";
    }
    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model)
    {
        User userById = userService.findUserById(userId);
        if (userById == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",userById);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int)service.findFollowerCount(ENTITY_TYPE_USER,userId));
        List<Map<String,Object>> userList=service.findFollowers(userId,page.getOffset(),page.getLimit());
        if (userList!=null)
        {
            for (Map<String, Object> map:userList){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/follower";
    }
    private  boolean hasFollowed(int userId)
    {
        if (holder.getUser()==null)
        {
            return false;
        }
        return service.hasFollowed(holder.getUser().getId(),CommunityConstant.ENTITY_TYPE_USER,userId);
    }
}
