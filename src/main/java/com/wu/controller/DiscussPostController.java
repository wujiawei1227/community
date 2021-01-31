package com.wu.controller;

import com.wu.pojo.Discuss_Post;
import com.wu.pojo.User;
import com.wu.service.DiscussPortService;
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

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-31 11:14
 **/

@Controller
@RequestMapping("/discusspost")
public class DiscussPostController {
    @Autowired
    private HostHolder holder;
    @Autowired
    private DiscussPortService service;
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(path = "/insert",method = RequestMethod.POST)
    public String insertDiscuss(String title,String content){
        User user = holder.getUser();
        if (user==null){
            return CommunityUtil.getJSONString(403,"您还没有登录");
        }
        Discuss_Post post=new Discuss_Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setUserId(user.getId());
        service.addDiscussPost(post);

        return CommunityUtil.getJSONString(0,"发布成功");
    }
    @RequestMapping(path = "detail/{discussPostId}",method = RequestMethod.GET)
    public String getDetial(@PathVariable("discussPostId") int discussPostId, Model model)
    {
        Discuss_Post byId = service.findById(discussPostId);
        model.addAttribute("post",byId);
        User userById = userService.findUserById(byId.getUserId());
        System.out.println(userById);
        model.addAttribute("user",userById);
        return "/site/discuss-detail";
    }
}
