package com.wu.controller;

import com.wu.pojo.Message;
import com.wu.pojo.Page;
import com.wu.pojo.User;
import com.wu.service.MessageService;
import com.wu.service.UserService;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-03 10:10
 **/

@Controller
public class MessageController {
    @Autowired
    private MessageService service;
    @Autowired
    private HostHolder holder;
    @Autowired
    private UserService userService;


    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page)
    {
        User user=holder.getUser();
        //分页信息
         page.setLimit(5);
         page.setRows(service.findConversationCount(user.getId()));
         page.setPath("/letter/list");
         //会话列表
        List<Message> conversationsList = service.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversation=new ArrayList<>();
        if (conversationsList!=null)
        {
            for (Message m :conversationsList) {
                Map<String,Object> map=new HashMap<>();
                map.put("conversation",m);
                map.put("letterCount",service.findLetterCount(m.getConversationId()));
                map.put("unreadCount",service.findLetterUnreadCount(user.getId(),m.getConversationId()));
                int targetId=user.getId()==m.getFromId()?m.getToId():m.getFromId();
                map.put("target",userService.findUserById(targetId));
                //此时已获取显示一条会话的所有信息
                conversation.add(map);
            }
        }
        model.addAttribute("conversations",conversation);
        //查询未读信息数量
        int letterUnReadCount=service.findLetterUnreadCount(user.getId(),null);

        model.addAttribute("letterUnreadCount",letterUnReadCount);
        return "/site/letter";
    }
    @RequestMapping(path = "/letter/delete" ,method = RequestMethod.GET)
    public String  delete(int messageId,String conversationId){
        service.deleteMessage(messageId);


        return "redirect:/letter/detail/"+conversationId;
    }
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId,Page page,Model model)
    {
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(service.findLetterCount(conversationId));

        //私信列表
        List<Message> lettersList = service.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if (lettersList!=null)
        {
            for (Message m :lettersList) {
                Map<String,Object> map=new HashMap<>();
                map.put("letter",m);
                map.put("fromUser",userService.findUserById(m.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));
        List<Integer> letterIds = getLetterIds(lettersList);
        if (letterIds!=null&&!letterIds.isEmpty())
        {
            service.readMessage(letterIds);
        }
        return "/site/letter-detail";

    }
    //获取未读信息id
    private List<Integer> getLetterIds(List<Message> list)
    {
        List<Integer> ids=new ArrayList<>();

        if (list!=null)
        {
            for (Message m :list) {

                if (holder.getUser().getId()==m.getToId()&&m.getStatus()==0)
                {
                    ids.add(m.getId());
                }

            }
        }
        return ids;
    }
    private User getLetterTarget(String conversationId)
    {
        String[] ids=conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);
        if (holder.getUser().getId()==id0)
        {
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content)
    {
        User target = userService.findUserByUsername(toName);
        if (target==null)
        {
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message=new Message();
        message.setFromId(holder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId()<message.getToId())
        {
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        service.addMessage(message);

        return CommunityUtil.getJSONString(0);

    }
}
