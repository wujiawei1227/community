package com.wu.dao;

import com.wu.pojo.Login_Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface LoginTicketMapper {
    @Select({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertLoginTicket(Login_Ticket login_ticket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where status!=1 and ticket=#{ticket} "
    })
    Login_Ticket findLoginTicket(String ticket);
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    void updateStatus(String ticket,int status);

}
