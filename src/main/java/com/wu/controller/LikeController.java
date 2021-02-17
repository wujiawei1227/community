package com.wu.controller;

import com.wu.pojo.User;
import com.wu.service.LikeService;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-13 10:43
 **/
@Controller
public class LikeController {
    @Autowired
    private LikeService service;
    @Autowired
    private HostHolder holder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId)
    {
        User user =holder.getUser();
        //点赞
        service.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likecount=service.findEntityLikeCount(entityType,entityId);
        //状态
        int entityLikeStatus = service.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String,Object> map=new HashMap<>();
        map.put("likeCount",likecount);
        map.put("likeStatus",entityLikeStatus);
        return CommunityUtil.getJSONString(0,null,map);


    }
}
