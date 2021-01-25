package com.wu.controller;

import com.wu.pojo.Discuss_Post;
import com.wu.pojo.Page;
import com.wu.pojo.User;
import com.wu.service.DiscussPortService;
import com.wu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class homeController {
    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPortService.findDiscussPostRows(0));
        page.setPath("/index");
        List<Discuss_Post> list = discussPortService.selectDiscussPort(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if (list!=null){
            for (Discuss_Post discuss_post:list) {
                Map<String ,Object> map=new HashMap<>();
                map.put("post",discuss_post);
                int userId = discuss_post.getUserId();
                User userById = userService.findUserById(userId);
                map.put("user",userById);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

}
