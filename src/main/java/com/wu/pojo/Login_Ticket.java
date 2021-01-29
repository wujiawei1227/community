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
    private int userId;
    private String ticket;
    private int status;//0-有效 1-无效
    private Date expired;

    @Override
    public String toString() {
        return "Login_Ticket{" +
                "id=" + id +
                ", userId=" + userId +
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
