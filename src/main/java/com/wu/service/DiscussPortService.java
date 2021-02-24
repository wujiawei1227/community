package com.wu.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wu.aspect.ServiceLogAspect;
import com.wu.dao.Discuss_PostMapper;
import com.wu.pojo.Discuss_Post;
import com.wu.utils.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:07
 **/
@Service
public class DiscussPortService {
    private static final Logger logger=LoggerFactory.getLogger(DiscussPortService.class);
    @Autowired
    private Discuss_PostMapper mapper;
    @Autowired
    private SensitiveFilter filter;
    @Value("${caffeine.posts.math-size}")
    private int maxSize;
    @Value("${caffeine.post.expire-seconds}")
    private int expireSeconds;
    //caffeine核心接口 Cache,LoadingCache,AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String,List<Discuss_Post>> postListCache;
    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;
    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Discuss_Post>>() {
                    @Nullable
                    @Override
                    public List<Discuss_Post> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql
                        logger.info("load post list from DB. cache初始化");
                        return mapper.selctDiscuss_post(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.info("load post list from DB. cache初始化");
                        return mapper.Discuss_portCount(key);
                    }
                });
    }

    public List<Discuss_Post> selectDiscussPort(int user_id,int offset,int limit,int orderMode){
        if(user_id==0&&orderMode==1)
        {
            return postListCache.get(offset+":"+limit);
        }
        logger.info("load post list from DB");
        return mapper.selctDiscuss_post(user_id,offset,limit,orderMode);
    }
    public int findDiscussPostRows(int id)
    {
        if (id==0){
            return postRowsCache.get(id);
        }
        logger.info("load post rows from DB");
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
    public int updateCommentCount(int id,int commentCount){
        return mapper.updateCommentCount(id,commentCount);
    }
    public int updateType(int id,int type)
    {
        return mapper.updateType(id,type);
    }
    public int updateStatus(int id,int status)
    {
        return mapper.updateStatus(id,status);
    }
    public int updateScore(int id,double score)
    {
        return mapper.updateScore(id,score);
    }
}
