package com.wg.blog.controller;

import com.wg.blog.model.User;
import com.wg.blog.result.Result;
import com.wg.blog.result.ResultFactory;
import com.wg.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api/user")
@Api(tags = "用户管理相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    @CrossOrigin
    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",value = "用户名",required = true,paramType = "String"),
            @ApiImplicitParam(name = "password",value = "密码",required =true,paramType = "String")
    })
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody User user){
        String username = user.getUsername();
        String password = user.getPassword();
        log.info("----开始登录----");
        User user1  = userService.getUserByUsernameAndPassword(username,password);
        try {
            if (user1 == null || "".equals(user1)) {
                String message = "请注册后再进行登录";
                return ResultFactory.buildFailResult(message);
            }
            if (!user1.isEnabled()) {
                String message = "该用户已被禁用";
                return ResultFactory.buildFailResult(message);
            }
            String message = "登陆成功";
            log.info("登陆成功");
            return ResultFactory.buildSuccessResult(message);
        }catch (AuthenticationException e){
            e.printStackTrace();
            String message  = "用户名或密码错误，请重新登录！";
            log.info("登陆失败");
            return ResultFactory.buildFailResult(message);
        }
    }
}
