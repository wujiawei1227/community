package com.wu.service;

import com.wu.pojo.CommunityConstant;
import com.wu.pojo.User;
import com.wu.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-19 09:29
 **/

@Service
public class FollowService  implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    public void follow(int userId,int entityType,int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
               String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
               String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
               redisOperations.multi();
               redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
               redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }
    public void unfollow(int userId,int entityType,int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations)  {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);
                return redisOperations.exec();
            }
        });
    }
    //查询关注的实体数量
    public long findFolloweeCount(int userId,int entityType)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //查询当前用户是否已关注该实体
    public long findFollowerCount(int entityType,int entityId)
    {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //查询当前用户是否关注了该实体
    public boolean hasFollowed(int userId,int entityType,int entityId)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }
    //查询某用户关注的人
    public List<Map<String,Object>>findFollowees(int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds==null)
        {
            return null;
        }
        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer i :targetIds) {
            Map<String,Object> map=new HashMap<>();
            User userById = userService.findUserById(i);
            map.put("user",userById);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetIds);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);

        }
        return list;
    }
    //查询某用户的粉丝
    public List<Map<String,Object>>findFollowers(int userId,int offset,int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (targetIds==null)
        {
            return null;
        }
        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer i :targetIds) {
            Map<String,Object> map=new HashMap<>();
            User userById = userService.findUserById(i);
            map.put("user",userById);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetIds);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);

        }
        return list;

    }
}
