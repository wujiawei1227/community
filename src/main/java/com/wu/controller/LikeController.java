package com.wu.controller;

import com.wu.event.EventProducer;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Event;
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
public class LikeController  implements CommunityConstant {
    @Autowired
    private LikeService service;
    @Autowired
    private HostHolder holder;
    @Autowired
    private EventProducer eventProducer;
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId)
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
        //出发点赞事件
        if (entityLikeStatus==1)
        {
            Event event=new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(holder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }
        return CommunityUtil.getJSONString(0,null,map);


    }
}
