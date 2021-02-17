package com.wu.service;

import com.wu.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-13 10:02
 **/

@Service
public class LikeService {
    @Autowired
    private RedisTemplate template;

    //点赞
    public void like(int userId, int entityType, int entityId,int entityUserId) {

        template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean member = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                if (member)
                {
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }
    //查询某个用户获得的赞
    public int findUserLikeCount(int userId)
    {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) template.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }

    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return template.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return template.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
