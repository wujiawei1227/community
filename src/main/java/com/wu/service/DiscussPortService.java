package com.wu.service;

import com.wu.dao.Discuss_PostMapper;
import com.wu.pojo.Discuss_Post;
import com.wu.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:07
 **/
@Service
public class DiscussPortService {
    @Autowired
    private Discuss_PostMapper mapper;
    @Autowired
    private SensitiveFilter filter;
    public List<Discuss_Post> selectDiscussPort(int user_id,int offset,int limit){

        return mapper.selctDiscuss_post(user_id,offset,limit);
    }
    public int findDiscussPostRows(int id){
        return mapper.Discuss_portCount(id);
    }
    public int addDiscussPost(Discuss_Post post)
    {
        if (post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(filter.filter(post.getTitle()));
        post.setContent(filter.filter(post.getContent()));

        return  mapper.insertDiscussPost(post);
    }
    public Discuss_Post findById(int id){
        return mapper.findById(id);
    }

}
