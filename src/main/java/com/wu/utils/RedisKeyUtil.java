package com.wu.utils;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-13 09:52
 **/

public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    //某个实体的赞
    //like:entity:entityType:entityid->set(userId)
    public static String getEntityLikeKey(int entityType,int entityId)
    {
        return PREFIX_ENTITY_LIKE+entityType+SPLIT+entityId;
    }
    //某个用户的赞
    public static String getUserLikeKey(int userId)
    {
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

}
