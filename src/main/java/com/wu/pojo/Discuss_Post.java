package com.wu.pojo;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 10:46
 **/

public class Discuss_Post {
   private int id;
   private int userId;
   private String title;
   private String content;
   private int type;//o表示普通，1表示置顶
   private int status;//0表示正常 1表示精华 2表示拉黑
   private Date createTime;
   private int comment_count;
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
              ", comment_count=" + comment_count +
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

   public int getComment_count() {
      return comment_count;
   }

   public void setComment_count(int comment_count) {
      this.comment_count = comment_count;
   }

   public double getScore() {
      return score;
   }

   public void setScore(double score) {
      this.score = score;
   }
}
