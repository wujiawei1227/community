package com.wu.pojo;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 11:00
 **/

public class Login_Ticket {
    private int id;
    private int user_id;
    private String ticket;
    private int status;//0-有效 1-无效
    private Date expired;

    @Override
    public String toString() {
        return "Login_Ticket{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }
}
