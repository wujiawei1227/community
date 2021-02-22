package com.wu.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 10:46
 **/
/*
*
discusspost 索引名
shards:分片
*replicas:副本
 **/
@Document(indexName = "discusspost",type = "_doc",shards = 6,replicas =3 )
public class Discuss_Post {
   @Id
   private int id;
   @Field(type = FieldType.Integer)
   private int userId;
   //abalyzer:存储分词器 searchAnalyzer:搜索分词器
   @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
   private String title;
   @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
   private String content;
   @Field(type = FieldType.Integer)
   private int type;//o表示普通，1表示置顶
   @Field(type = FieldType.Integer)
   private int status;//0表示正常 1表示精华 2表示拉黑
   @Field(type = FieldType.Date)
   private Date createTime;
   @Field(type = FieldType.Integer)
   private int commentCount;
   @Field(type = FieldType.Double)
   private double score;

   @Override
   public String toString() {
      return "Discuss_Post{" +
              "id=" + id +
              ", userId=" + userId +
              ", title='" + title + '\'' +
              ", content='" + content + '\'' +
              ", type=" + type +
              ", status=" + status +
              ", createTime=" + createTime +
              ", commentCount=" + commentCount +
              ", score=" + score +
              '}';
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getUserId() {
      return userId;
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getStatus() {
      return status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

   public int getCommentCount() {
      return commentCount;
   }

   public void setCommentCount(int commentCount) {
      this.commentCount = commentCount;
   }

   public double getScore() {
      return score;
   }

   public void setScore(double score) {
      this.score = score;
   }
}
