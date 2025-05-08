package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.system.business.entity.User;
import com.system.business.mapper.UserMapper;
import com.system.business.service.UserService;
import com.system.common.AuthAccess;
import com.system.common.Logs.LogType;
import com.system.common.Logs.SystemLogs;
import com.system.common.Result;
import com.system.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;

    @AuthAccess
    @PostMapping("/login")
    @SystemLogs(operation = "用户",type = LogType.LOGIN)
    public Result login(@RequestBody User user) {
        //数据校验
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            return Result.error("数据输入不合法");
        }
        user = userService.login(user);
        return Result.success(user);
    }

    @AuthAccess
    @PostMapping("/register")
    @SystemLogs(operation = "用户",type = LogType.REGISTER)
    public Result register(@RequestBody User user) {
        //数据校验
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword()) || StrUtil.isBlank(user.getRole())) {
            return Result.error("数据输入不合法");
        }
        if (user.getUsername().length() < 1 || user.getPassword().length() < 2) {
            return Result.error("数据长度太短");
        }
        user = userService.register(user);
        return Result.success(user);
    }

    // 忘记密码：重置密码操作
    @AuthAccess
    @PutMapping("/password")
    @SystemLogs(operation = "用户",type = LogType.UPDATE)
    public Result password(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPhone())) {
            return Result.error("数据输入不合法");
        }
        userService.resetPassword(user);
        return Result.success();
    }

    @AuthAccess
    @PostMapping("/validateToken")
    public Result validateToken(@RequestBody User user) {
        User reuser=new User();
        String token = user.getToken();
        System.out.println("前端返回的token："+token);
        System.out.println(user);
        try {
            // 检查 Redis 中是否存在该 Token
            Integer currentUserID = user.getId();// 获取当前用户信息
            System.out.println("用户ID:"+currentUserID);
            if (null==currentUserID){
                throw new ServiceException(500,"用户未登录");
            }
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(currentUserID.toString());
            System.out.println(redisToken);
            if (redisToken != null && redisToken.equals(token)){
                reuser = userMapper.selectById(currentUserID);
                System.out.println("用户信息："+reuser);
            }
        }catch (Exception e) {
            // Token 无效
            throw new ServiceException(500, "Token无效");
        }
        return Result.success(reuser);
    }

}
