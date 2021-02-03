package com.wu.service;

import com.wu.dao.MessageMapper;
import com.wu.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
