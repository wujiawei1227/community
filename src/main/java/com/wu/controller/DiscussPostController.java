package com.wu.controller;

import com.wu.event.EventProducer;
import com.wu.pojo.*;
import com.wu.service.CommentService;
import com.wu.service.DiscussPortService;
import com.wu.service.LikeService;
import com.wu.service.UserService;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;

import com.wu.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-31 11:14
 **/

@Controller
@RequestMapping("/discusspost")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private HostHolder holder;
    @Autowired
    private DiscussPortService service;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @ResponseBody
    @RequestMapping(path = "/insert", method = RequestMethod.POST)
    public String insertDiscuss(String title, String content) {
        User user = holder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录");
        }
        Discuss_Post post = new Discuss_Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setUserId(user.getId());
        service.addDiscussPost(post);
        //出发发帖事件
        Event event=new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        //计算帖子分数
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey,post.getId());

        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDetial(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //查找帖子信息
        Discuss_Post byId = service.findById(discussPostId);
        model.addAttribute("post", byId);
        //查找作者信息
        User userById = userService.findUserById(byId.getUserId());
        model.addAttribute("user", userById);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int entityLikeStatus = holder.getUser()==null?0:likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus",entityLikeStatus);

        //评论分页信息
        page.setRows(byId.getCommentCount());
        page.setPath("/discusspost/detail/" + discussPostId);
        page.setLimit(5);
        //评论列表
        List<Comment> list = commentService.findCommentByEntity
                (ENTITY_TYPE_POST, byId.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (list != null) {
            for (Comment c : list) {
                //评论VO
                Map<String, Object> map = new HashMap<>();
                //具体某一个评论
                map.put("comment", c);
                //评论的作者
                map.put("user", userService.findUserById(c.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, c.getId());
                map.put("likeCount",likeCount);
                //点赞状态
                entityLikeStatus = holder.getUser()==null?0:likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_COMMENT, c.getId());
                map.put("likeStatus",entityLikeStatus);
                //评论的回复
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, c.getId(), 0, Integer.MAX_VALUE);
                //回复列表 VO
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment co : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply", co);
                        replyVo.put("user", userService.findUserById(co.getUserId()));
                        User target = co.getTargetId() == 0 ? null : userService.findUserById(co.getTargetId());
                        replyVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, co.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        entityLikeStatus = holder.getUser()==null?0:likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_COMMENT, co.getId());
                        replyVo.put("likeStatus",entityLikeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                map.put("replys", replyVoList);
                //回复数量
                int commentCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, c.getId());
                map.put("replyCount", commentCount);
                commentVOList.add(map);

            }
        }
        model.addAttribute("comments", commentVOList);
        return "/site/discuss-detail";
    }
    //置顶
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        service.updateType(id,1);
        //出发发帖事件
        Event event=new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(holder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);

    }
    //加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        service.updateStatus(id,1);
        //出发发帖事件
        Event event=new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(holder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        //计算帖子分数
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey,id);

        return CommunityUtil.getJSONString(0);

    }
    //删除
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        service.updateStatus(id,2);
        //出发删帖事件
        Event event=new Event().setTopic(TOPIC_DELETE)
                .setUserId(holder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);

    }
}
