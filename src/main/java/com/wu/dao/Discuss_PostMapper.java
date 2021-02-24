package com.wu.dao;

import com.wu.pojo.Discuss_Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 11:07
 **/
@Mapper
@Component
public interface Discuss_PostMapper {

  List<Discuss_Post> selctDiscuss_post(int user_id,int offset,int limit,int orderMode);
  int Discuss_portCount(@Param("userId") int user_id);
  int insertDiscussPost(Discuss_Post post);
  Discuss_Post findById(int id);
  int  updateCommentCount(int id,int commentCount);
  int updateType(int id,int type);
  int updateStatus(int id,int status);
  int updateScore(int id,double score);

}
