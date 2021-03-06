package com.wu.service;

import com.wu.aspect.ServiceLogAspect;
import com.wu.dao.LoginTicketMapper;
import com.wu.dao.UserMapper;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.Login_Ticket;
import com.wu.pojo.User;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;
import com.wu.utils.MailClient;
import com.wu.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
   /* @Autowired
    private LoginTicketMapper loginTicketMapper;*/
    @Autowired
    private HostHolder holder;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserById(int id) {
      //  return mapper.findUserById(id);
        User user=getCache(id);
        if (user==null)
        {
            user=initCache(id);
        }
        return user;
    }
    //优先从缓存中取值
    private User getCache(int userId)
    {
        String redisKey=RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }
    //娶不到值时初始化缓存数据
    private User initCache(int userId)
    {
        User user=mapper.findUserById(userId);
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //数据变更时清除缓存数据
    private void clearCache(int userId)
    {
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public User findUserByUsername(String username) {
        return mapper.findUserByUsername(username);
    }

    /*
     *
     * @Description //TODO 注册用户
     * @Param [user]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            if (StringUtils.isBlank(user.getUsername())) {
                map.put("usernameMessage", "用户名不能为空");
                return map;
            }
            if (StringUtils.isBlank(user.getUsername())) {
                map.put("passwordMessage", "密码不能为空");
                return map;
            }
            if (StringUtils.isBlank(user.getUsername())) {
                map.put("mailMessage", "邮箱不能为空");
                return map;
            }

            User userByUsername = mapper.findUserByUsername(user.getUsername());
            if (userByUsername != null) {
                map.put("usernameMessage", "该用户名已被注册");
                return map;
            }
            User userByMail = mapper.findUserByMail(user.getEmail());
            if (userByMail != null) {
                map.put("mailMessage", "该邮箱已被注册");
                return map;
            }
            //设置user属性
            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
            user.setPassword(user.getPassword() + user.getSalt());
            user.setStatus(0);
            user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
            user.setType(0);
            user.setActivationCode(CommunityUtil.generateUUID());
            user.setCreateTime(new Date());
            mapper.insertUser(user);
            //利用模板引擎生成模板
            Context context = new Context();
            context.setVariable("email", user.getEmail());
            String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
            context.setVariable("url", url);
            String content = template.process("/mail/activation", context);
            client.sendMail(user.getEmail(), "激活账号", content);

        }
        return map;

    }

    /*
     *
     * @Description //TODO 激活用户
     * @Param [userId, code]
     * @return int
     **/
    public int activation(int userId, String code) {
        User user = mapper.findUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            mapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FALEED;
        }
    }

    /*
     *
     * @Description //TODO 注册用户
     * @Param [username, password, expiredSeconds]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMessage", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMessage", "密码不能为空");
            return map;
        }
        //验证账号
        User userByUsername = mapper.findUserByUsername(username);
        if (userByUsername == null) {
            map.put("usernameMsg", "该用户不存在");
            return map;
        }
        if (userByUsername.getStatus() == 0) {
            map.put("usernameMsg", "该用户未激活");
            return map;
        }
        //验证密码
        String s = password + userByUsername.getSalt();
        if (!userByUsername.getPassword().equals(s)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //生成登录凭证
        Login_Ticket login_ticket = new Login_Ticket();
        login_ticket.setUserId(userByUsername.getId());
        login_ticket.setTicket(CommunityUtil.generateUUID());
        login_ticket.setStatus(0);
        login_ticket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds + 1000));
        //loginTicketMapper.insertLoginTicket(login_ticket);
        String redisKey= RedisKeyUtil.getTicketKey(login_ticket.getTicket());
        redisTemplate.opsForValue().set(redisKey,login_ticket);
        map.put("ticket", login_ticket.getTicket());
        return map;
    }

    public void logout(String ticket) {

        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        Login_Ticket login_ticket=(Login_Ticket)redisTemplate.opsForValue().get(redisKey);
        login_ticket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,login_ticket);
    }

    public Login_Ticket getTicketByTicket(String ticket) {
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (Login_Ticket)redisTemplate.opsForValue().get(redisKey);
        //return loginTicketMapper.findLoginTicket(ticket);
    }
    public int updateUserHeader(int userId,String headerUrl ){
       int rows= mapper.updateHeader(userId, headerUrl);
       clearCache(userId);
       return rows;
       // return
    }
    public Map<String,Object> updatePassword(String oldPassword,String newPassword){
        Map<String,Object> map=new HashMap<>();
        User user = holder.getUser();
        System.out.println(user);
        //验证密码
        String s=oldPassword+user.getSalt();
        if (!s.equals(user.getPassword())){
            System.out.println(s);
            System.out.println(user.getPassword());
            map.put("passwordMsg","旧密码错误，请重新输入");
            return map;
        }
        mapper.updatePassword(user.getId(),newPassword+user.getSalt());
        return map;
    }
    public User findUserByName(String name)
    {
        return  mapper.findUserByUsername(name);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId)
    {
        User user=this.findUserById(userId);
        List<GrantedAuthority> list=new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                switch (user.getType()){
                    case 1:return AUTHORITY_ADMIN;
                    case 2:return AUTHORITY_MODERATOR;
                    default:return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
