package com.wu.controller;

import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Discuss_Post;
import com.wu.pojo.Page;
import com.wu.pojo.User;
import com.wu.service.DiscussPortService;
import com.wu.service.LikeService;
import com.wu.service.UserService;
import com.wu.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:53
 **/
@Controller
public class homeController implements CommunityConstant {
    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,@RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        page.setRows(discussPortService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<Discuss_Post> list = discussPortService
                .selectDiscussPort(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
            if (list!=null){
            for (Discuss_Post discuss_post:list) {
                Map<String ,Object> map=new HashMap<>();
                map.put("post",discuss_post);
                int userId = discuss_post.getUserId();
                User userById = userService.findUserById(userId);
                map.put("user",userById);
                long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,discuss_post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
            model.addAttribute("orderNode",orderMode);
        return "/index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }

}
