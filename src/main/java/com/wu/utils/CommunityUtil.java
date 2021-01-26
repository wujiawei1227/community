package com.wu.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-26 16:25
 **/

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密
    public static String md5(String key){
        if (StringUtils.isBlank(key)){//判断key是否为空
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
