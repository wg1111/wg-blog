package com.wg.blog.controller;

import com.wg.blog.mail.SendEmail;
import com.wg.blog.model.User;
import com.wg.blog.result.Result;
import com.wg.blog.result.ResultFactory;
import com.wg.blog.service.UserService;
import com.wg.blog.util.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/api/user")
@Api(tags = "用户管理相关接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @CrossOrigin
    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",required = true,paramType = "String"),
            @ApiImplicitParam(name = "password",value = "密码",required =true,paramType = "String")
    })
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody User user, HttpSession session){
        String username = user.getUsername();
        String password = user.getPassword();
        log.info("----开始登录----");
        User user1  = userService.getUserByUsernameAndPassword(username,password);
        try {
            if (user1 == null || "".equals(user1)) {
                String message = "用户不存在，请注册后再进行登录";
                return ResultFactory.buildFailResult(message);
            }
            if (null==user1.getEnable() || "0".equals(user1.getEnable())) {
                String message = "该用户已被禁用";
                return ResultFactory.buildFailResult(message);
            }
            String message = "登陆成功";
            //加入缓存，用来判断是否进行登录拦截
            session.setAttribute("user",user1);
            log.info("登陆成功");
            return ResultFactory.buildSuccessResult(message);
        }catch (AuthenticationException e){
            e.printStackTrace();
            String message  = "用户名或密码错误，请重新登录！";
            log.info("登陆失败");
            return ResultFactory.buildFailResult(message);
        }
    }

    @CrossOrigin
    @ApiOperation("用户注册接口")
    @ApiImplicitParam(name="user",value="用户",required=true, paramType = "Object")
    @PostMapping("/register")
    @ResponseBody

    public Result register(@RequestBody User user){
        String username = user.getUsername();
        String password = user.getPassword();
        String phone = user.getPhone();
        String email = user.getEmail();
        username = HtmlUtils.htmlEscape(username);
        password = HtmlUtils.htmlEscape(password);
        phone = HtmlUtils.htmlEscape(phone);
        email = HtmlUtils.htmlEscape(email);
        //验证该用户名是否已经存在
        User flag = userService.getUserByName(username);
        if (flag != null ){
            String message = "该用户名已被使用，请重新注册";
            return ResultFactory.buildFailResult(message);
        }
        //对密码进行加盐加密
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        //设置hash加密的次数
        int times = 2;
        //加密
        String encodePassword = new SimpleHash("md5",password,salt,times).toString();
        user.setUsername(username);
        user.setPassword(encodePassword);
        user.setSalt(salt);
        user.setPhone(phone);
        user.setEmail(email);
        user.setCreatTime(new Date());
        //邮件激活码
        String validateCode = MD5Util.encodeToHex("salt"+email+password);
        //将邮件验证码保存到redis，24小时内有效
        redisTemplate.opsForValue().set(email, validateCode, 24, TimeUnit.HOURS);
        userService.insert(user);
        log.info("注册成功");
        SendEmail.sendEmailMessage(email,validateCode);
        String message = email + "," + validateCode;
        return ResultFactory.buildSuccessResult(message);
    }

    @CrossOrigin
    @ApiOperation("重新发送邮件接口")
    @PostMapping("/sendEmail")
    @ResponseBody
    public Result sendEmail(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String validateCode = attributes.getRequest().getParameter("validateCode");
        String email = attributes.getRequest().getParameter("email");
        SendEmail.sendEmailMessage(email,validateCode);
        String message = "邮件验证码发送成功";
        return ResultFactory.buildSuccessResult(message);
    }

    @ApiOperation("邮箱激活")
    @RequestMapping("/activeCode")
    @ResponseBody
    public Result activeCode(){
        log.info("邮箱激活验证");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String validateCode = attributes.getRequest().getParameter("validateCode");
        String email = attributes.getRequest().getParameter("email");
        String code = (String) redisTemplate.opsForValue().get(email);
        log.info( "验证邮箱为："+email+",redis中邮箱激活码为："+code+",用户链接的激活码为："+validateCode );
        User user = userService.getUserByEmail(email);
        if (user != null && "1".equals(user.getEnable())){
            String message = "该用户已激活，请直接前往登录";
            return ResultFactory.buildResult(201,"已激活",message);
        }if (code == null){
            String message = "验证码已失效，请重新注册";
            return ResultFactory.buildResult(202,"验证码失效",message);
        }if (validateCode != null && validateCode.equals(code)){
            user.setEnable(1);
            userService.update(user);
            String message = "激活成功，请前往登录";
            return ResultFactory.buildSuccessResult(message);
        }else {
            String message = "激活失败，请重新操作";
            return ResultFactory.buildFailResult(message);
        }
    }
}
