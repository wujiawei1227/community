package com.wu.controller;

import com.google.code.kaptcha.Producer;
import com.wu.pojo.CommunityConstant;
import com.wu.pojo.User;
import com.wu.service.UserService;
import com.wu.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-26 17:26
 **/
@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger=LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Producer producer;
    @Autowired
    private UserService service;
    @Autowired
    private HostHolder holder;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /*
    *
     * @Description //TODO 跳转至注册页面
     * @Param []
     * @return java.lang.String
     **/
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
    /*
    *
     * @Description //TODO 注册用户
     * @Param [model, user]
     * @return java.lang.String
     **/
@RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = service.register(user);
        if (map==null||map.isEmpty()){
            model.addAttribute("msg","您已注册成功，我们已向您的邮箱发验证邮件，请注意查收");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMessage"));
            model.addAttribute("passwordMsg",map.get("passwordMessage"));
            model.addAttribute("mailMsg",map.get("mailMessage"));
            return "/site/register";
        }

    }
    /*
    *
     * @Description //TODO 跳转至登录页面
     * @Param []
     * @return java.lang.String
     **/
    @RequestMapping(path ="/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    /*
    *
     * @Description //TODO 激活账号
     * @Param [model, userId, code]
     * @return java.lang.String
     **/
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = service.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

   @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberMe,
                        Model model,HttpSession session,HttpServletResponse response){
       String kaptcha = (String)session.getAttribute("kaptcha");
       if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!code.equalsIgnoreCase(kaptcha))
       {
           model.addAttribute("codeMsg","验证码不正确");
           return "site/login";
       }
       //检测账号
       int expiredSeconds=rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
       Map<String, Object> login = service.login(username, password, expiredSeconds);
       if(login.containsKey("ticket")){
           Cookie cookie=new Cookie("ticket",login.get("ticket").toString());
           cookie.setPath(contextPath);
           cookie.setMaxAge(expiredSeconds);
           response.addCookie(cookie);
           return "redirect:/index";
       }else {
           System.out.println("验证失败");
           model.addAttribute("usernameMsg",login.get("usernameMsg"));
           model.addAttribute("passwordMsg",login.get("passwordMsg"));
           return "/site/login";
       }
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket)
    {
        service.logout(ticket);
        holder.clear();
        return "redirect:/login";
    }

    /*
    *
     * @Description //TODO 生成验证码
     * @Param [response, session]
     * @return void
     **/
    @RequestMapping(path = "/kaptcha" ,method = RequestMethod.GET)
    public void getCode(HttpServletResponse response, HttpSession session){
            //生成验证码
        String text=producer.createText();
        BufferedImage image = producer.createImage(text);
        //将验证码写入session
        session.setAttribute("kaptcha",text);
        //将图片输出
        response.setContentType("image/png");

        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败" +e.getMessage());
        }
    }


}
