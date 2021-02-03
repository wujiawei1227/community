package com.wu.service;

import com.wu.dao.MessageMapper;
import com.wu.pojo.Message;
import com.wu.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-02 10:53
 **/
@Service
public class MessageService {

    @Autowired
    private SensitiveFilter filter;
    @Autowired
    private MessageMapper mapper;

    public List<Message> findConversations(int userId,int offset,int limit)
    {
        return mapper.selectConversations(userId,offset,limit);
    }
    public int findConversationCount(int userId)
    {
        return mapper.selectConversationCount(userId);
    }
    public List<Message> findLetters(String conversationId,int offset,int limit)
    {
        return mapper.selectLetters(conversationId,offset,limit);
    }
    public int findLetterCount(String conversationId)
    {
        return mapper.selectLetterCount(conversationId);
    }
    public int findLetterUnreadCount(int userId,String conversationId)
    {
        return mapper.selectLetterUnreadCount(userId,conversationId);
    }
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(filter.filter(message.getContent()));
        return mapper.insertMessage(message);
    }
    public int readMessage(List<Integer> ids)
    {

        return mapper.updateStatus(ids,1);
    }
   public void deleteMessage(int id){
        List<Integer> list=new ArrayList<>();
        list.add(id);
        mapper.updateStatus(list,2);
   }
}
