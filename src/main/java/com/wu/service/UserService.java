package com.wu.service;

import com.wu.dao.UserMapper;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.User;
import com.wu.utils.CommunityUtil;
import com.wu.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-24 17:51
 **/
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper mapper;
    @Autowired
    private MailClient client;
    @Autowired
    private TemplateEngine template;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return mapper.findUserById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
       if (user!=null){
           if (StringUtils.isBlank(user.getUsername())){
               map.put("usernameMessage","用户名不能为空");
               return map;
           }
           if (StringUtils.isBlank(user.getUsername())){
               map.put("passwordMessage","密码不能为空");
               return map;
           } if (StringUtils.isBlank(user.getUsername())){
               map.put("mailMessage","邮箱不能为空");
               return map;
           }

               User userByUsername = mapper.findUserByUsername(user.getUsername());
           if (userByUsername!=null) {
               map.put("usernameMessage", "该用户名已被注册");
                return map;
           }
           User userByMail = mapper.findUserByMail(user.getEmail());
           if (userByMail!=null) {
               map.put("mailMessage","该邮箱已被注册");
               return map;
           }


           //设置user属性
           user.setSalt(CommunityUtil.generateUUID().substring(0,5));
           user.setPassword(user.getPassword()+user.getSalt());
           user.setStatus(0);
           user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
           user.setType(0);
           user.setActivationCode(CommunityUtil.generateUUID());
           user.setCreateTime(new Date());
            mapper.insertUser(user);
            //利用模板引擎生成模板
           Context context=new Context();
           context.setVariable("email",user.getEmail());
           String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
           context.setVariable("url",url);
           String content=template.process("/mail/activation",context);
           client.sendMail(user.getEmail(),"激活账号",content);

       }
        return map;

    }
    public int activation(int userId,String code){
        User user=mapper.findUserById(userId);
        if (user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            mapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FALEED;
        }


    }
}
