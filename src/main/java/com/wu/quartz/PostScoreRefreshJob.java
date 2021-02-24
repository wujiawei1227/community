package com.wu.quartz;

import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Discuss_Post;
import com.wu.service.DiscussPortService;
import com.wu.service.ElasticsearchService;
import com.wu.service.LikeService;
import com.wu.utils.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-23 12:26
 **/

public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1998-02-04 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元事件失败",e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations=redisTemplate.boundSetOps(postScoreKey);
        if (operations.size()==0)
        {
            logger.info("[任务取消] 没有需要刷新的帖子");
            return;
        }
        logger.info("[任务开始] 开始刷新帖子分数");
        while (operations.size()>0)
        {
            this.refresh((Integer)operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕");

    }
    private void refresh(int id){
        Discuss_Post byId = discussPortService.findById(id);
        if (byId==null)
        {
            logger.error("该帖子不存在：id="+id);
            return;
        }
        //是否精华
        boolean iswonderful = byId.getStatus() == 1;
        //点赞数量
        long likecount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        //评论数量
        int commentCount = byId.getCommentCount();
        //计算权重
        double w = (iswonderful ? 75 : 0) + commentCount * 10 + likecount * 2;
        //分数=帖子权重+距离天数
        double score=Math.log10(Math.max(w,1))+(byId.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);
        discussPortService.updateScore(byId.getId(),score);
        //同步搜索数据
        byId.setScore(score);
        elasticsearchService.saveDiscussPost(byId);
    }
}
