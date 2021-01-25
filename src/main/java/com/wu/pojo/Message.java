package com.wu.pojo;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 10:55
 **/

public class Message {
    private int id;
    private int from_id;
    private int to_id;
    private String conversastion_id;
    private String content;
    private int status;//0表示未读 1表示已读 2表示删除
    private Date create_time;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from_id=" + from_id +
                ", to_id=" + to_id +
                ", conversastion_id='" + conversastion_id + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", create_time=" + create_time +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public String getConversastion_id() {
        return conversastion_id;
    }

    public void setConversastion_id(String conversastion_id) {
        this.conversastion_id = conversastion_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}

