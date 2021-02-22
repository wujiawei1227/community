package com.wu.controller;

import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Discuss_Post;
import com.wu.pojo.Page;
import com.wu.service.ElasticsearchService;
import com.wu.service.LikeService;
import com.wu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-22 11:27
 **/

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model)
    {
        //搜索帖子
        org.springframework.data.domain.Page<Discuss_Post> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        //聚合数据
        List<Map<String,Object>> discussposts=new ArrayList<>();
        if (searchResult!=null)
        {
            for (Discuss_Post p :searchResult) {
                Map<String,Object> map=new HashMap<>();
                map.put("post",p);
                map.put("user",userService.findUserById(p.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,p.getId()));
                discussposts.add(map);

            }
        }
        model.addAttribute("discussPosts",discussposts);
        model.addAttribute("keyword",keyword);
        //分页信息

        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult==null?0:(int)searchResult.getTotalElements());
        return "site/search";

    }
}
