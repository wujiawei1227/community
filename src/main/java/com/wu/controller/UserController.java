package com.wu.controller;

import com.wu.pojo.User;
import com.wu.service.LikeService;
import com.wu.service.UserService;
import com.wu.utils.CommunityUtil;
import com.wu.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-29 12:14
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService service;
    @Autowired
    private HostHolder holder;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String updateHeader(MultipartFile headerImage, Model model)
    {
        if (headerImage==null){
            model.addAttribute("imageMsg","文件不能为空");
            return "/site/setting";
        }
        //获取原始文件名
        String originalFilename = headerImage.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //判断后缀是否合理
        if (StringUtils.isBlank(substring)){
            model.addAttribute("imageMsg","文件格式有误");
            return "/site/setting";
        }
        //生成随机文件名
        String filename = CommunityUtil.generateUUID() + substring;
        //确定文件存放路径
        File file = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
           logger.error("上传文件失败"+e.getMessage());
           throw new RuntimeException("上传文件失败",e);
        }
        //更新当前用户的头像的web路径
        User user = holder.getUser();
        String url=domain+contextPath+"/user/header/"+filename;
        int i = service.updateUserHeader(user.getId(), url);


        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String name, HttpServletResponse response)
    {
        //文件存放路径
        name=uploadPath+"/"+name;
        //文件后缀
        String substring = name.substring(name.lastIndexOf("."));
        response.setContentType("image/"+substring);
        try {
            FileInputStream fileInputStream = new FileInputStream(name);

            OutputStream outputStream = response.getOutputStream();

            byte[] bytes = new byte[1024];
            int b=0;
            while((b=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,b);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldPassword,String newPassword01,String newPassword02,Model model){
        //验证两次密码是否一致
        if (!newPassword01.equals(newPassword02)){
            model.addAttribute("newPasswordMsg","两次输入密码不一致");
            return "/site/setting";
        }
        Map<String, Object> map = service.updatePassword(oldPassword, newPassword01);
        if (!map.isEmpty()){
            model.addAttribute("oldPasswordMsg",map.get("passwordMsg"));
            return "/site/setting";
        }
        if (oldPassword.equals(newPassword01)){
            model.addAttribute("newPasswordMsg","新密码不能与旧密码一致");
            return "/site/setting";
        }

        return "redirect:/login";

    }
    //个人主页
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user=service.findUserById(userId);
        if (user==null)
        {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        int userLikeCount = likeService.findUserLikeCount(userId);
        System.out.println("likecout"+userLikeCount);
        model.addAttribute("likeCount",userLikeCount);
        return "/site/profile";
    }
}
