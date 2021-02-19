package com.wu.pojo;

public interface CommunityConstant {
    /*
    *
     * @Description //TODO 激活成功
     * @Param 
     * @return 
     **/
    int ACTIVATION_SUCCESS=0;
    /*
    *
     * @Description //TODO 重复激活
     * @Param 
     * @return 
     **/
    int ACTIVATION_REPEAT=1;
    /*
    *
     * @Description //TODO 激活失败
     * @Param 
     * @return 
     **/
    int ACTIVATION_FALEED=2;
    /*
    *
     * @Description //TODO默认状态登录凭证的超时时间
     * @Param
     * @return
     **/
    int DEFAULT_EXPIRED_SECONDS=3600*12;

    /*
    *
     * @Description //TODO 记住状态下的登录超时时间
     * @Param 
     * @return 
     **/
    int REMEMBER_EXPIRED_SECONDS=3600*24*30;
    /*
    *
     * @Description //TODO 实体类型:帖子
     * @Param 
     * @return 
     **/
    int ENTITY_TYPE_POST=1;
    /*
    *
     * @Description //TODO 实体类型 评论
     * @Param
     * @return
     **/
    int ENTITY_TYPE_COMMENT=2;
    /*
     *
     * @Description //TODO 实体类型 用户
     * @Param
     * @return
     **/
    int ENTITY_TYPE_USER=3;

}
