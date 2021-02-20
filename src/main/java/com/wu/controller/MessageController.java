package com.wu.controller;

import com.alibaba.fastjson.JSONObject;
import com.wu.pojo.CommunityConstant;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-03 10:10
 **/

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService service;
    @Autowired
    private HostHolder holder;
    @Autowired
    private UserService userService;


    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {

        User user = holder.getUser();
        //分页信息
        page.setLimit(5);
        page.setRows(service.findConversationCount(user.getId()));
        page.setPath("/letter/list");
        //会话列表
        List<Message> conversationsList = service.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversation = new ArrayList<>();
        if (conversationsList != null) {
            for (Message m : conversationsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", m);
                map.put("letterCount", service.findLetterCount(m.getConversationId()));
                map.put("unreadCount", service.findLetterUnreadCount(user.getId(), m.getConversationId()));
                int targetId = user.getId() == m.getFromId() ? m.getToId() : m.getFromId();
                map.put("target", userService.findUserById(targetId));
                //此时已获取显示一条会话的所有信息
                conversation.add(map);
            }
        }
        model.addAttribute("conversations", conversation);
        //查询未读信息数量
        int letterUnReadCount = service.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnReadCount);
        int noticeUnreadCount=service.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/delete", method = RequestMethod.GET)
    public String delete(int messageId, String conversationId) {
        service.deleteMessage(messageId);


        return "redirect:/letter/detail/" + conversationId;
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(service.findLetterCount(conversationId));

        //私信列表
        List<Message> lettersList = service.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (lettersList != null) {
            for (Message m : lettersList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", m);
                map.put("fromUser", userService.findUserById(m.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        List<Integer> letterIds = getLetterIds(lettersList);
        if (letterIds != null && !letterIds.isEmpty()) {
            service.readMessage(letterIds);
        }
        return "/site/letter-detail";

    }

    //获取未读信息id
    private List<Integer> getLetterIds(List<Message> list) {
        List<Integer> ids = new ArrayList<>();

        if (list != null) {
            for (Message m : list) {

                if (holder.getUser().getId() == m.getToId() && m.getStatus() == 0) {
                    ids.add(m.getId());
                }

            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (holder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {

        User target = userService.findUserByUsername(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(holder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        service.addMessage(message);

        return CommunityUtil.getJSONString(0);

    }

    //显示通知
    @RequestMapping(path = "notice/list", method = RequestMethod.GET)
    private String getNoticeList(Model model) {
        User user = holder.getUser();
        //查询评论类通知
        Message message = service.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            System.out.println("去标签前:" + message.getContent());
            String content = HtmlUtils.htmlUnescape(message.getContent());
            System.out.println("去标签后：" + content);
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int count = service.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = service.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVO);
        //查询点赞类通知
        message = service.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());

            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int count = service.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = service.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVO);
        //查询关注类通知
        message = service.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            int count = service.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unread = service.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVO);
        //查询未读消息数量
        int letterUnreadCount=service.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount=service.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";

    }
    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Page page,Model model){
        User user=holder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(service.findNoticeCount(user.getId(),topic));
        List<Message> noticeList=service.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVOList=new ArrayList<>();
        if (noticeList!=null)
        {
            for (Message notice :noticeList) {
                Map<String,Object> map=new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                //通知作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);

            }
        }
        model.addAttribute("notices",noticeVOList);

        //设置已读
        List<Integer> ids=getLetterIds(noticeList);
        if (!ids.isEmpty()){
            service.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
