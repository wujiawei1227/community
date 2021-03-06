package com.wu.event;

import com.alibaba.fastjson.JSONObject;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Discuss_Post;
import com.wu.pojo.Event;
import com.wu.pojo.Message;
import com.wu.service.DiscussPortService;
import com.wu.service.ElasticsearchService;
import com.wu.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-21 09:42
 **/
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics ={TOPIC_LIKE, TOPIC_FOLLOW, TOPIC_COMMENT })
    public void handleCommentMessage(ConsumerRecord record)
    {
        if (record==null||record.value()==null)
        {
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        //发送站内通知
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        Map<String,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
         messageService.addMessage(message);

    }
    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record)
    {
        if (record==null||record.value()==null)
        {
            logger.error("消息的内容不能为空");
            return;
        }
        Event event=JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null)
        {
            logger.error("消息格式错误");
            return;
        }
        Discuss_Post post=discussPortService.findById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
    //消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record)
    {
        if (record==null||record.value()==null)
        {
            logger.error("消息的内容不能为空");
            return;
        }
        Event event=JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null)
        {
            logger.error("消息格式错误");
            return;
        }
        elasticsearchService.deleteDiscusspost(event.getEntityId());
    }

}
