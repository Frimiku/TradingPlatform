package com.system.common.Logs;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import com.system.business.entity.Logs;
import com.system.business.entity.User;
import com.system.business.mapper.UserMapper;
import com.system.business.service.LogsService;
import com.system.utils.IpUtils;
import com.system.utils.ThreadLocalUtil;
import com.system.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

// 切面处理
@Component
@Aspect
@Slf4j
public class LogsAspect {

    @Resource
    private UserMapper userMapper;
    @Resource
    private LogsService logsService;

    @AfterReturning(pointcut =" @annotation(systemLogs)",returning = "jsonResult")
    public void recordLog(JoinPoint joinPoint, SystemLogs systemLogs, Object jsonResult){
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User loginUser = userMapper.selectById(currentUserID);
        // User loginUser = TokenUtils.getCurrentUser();
        // 用户未登录情况下，loginUser为null，就要从参数里获取操作人信息
        if (loginUser == null){
            // 登录，注册
            Object[] args = joinPoint.getArgs(); // 获取到所有参数
            if (ArrayUtil.isNotEmpty(args)){
                if (args[0] instanceof User){
                    loginUser = (User) args[0];
                }
            }
        }
        if (loginUser == null){
            log.error("记录日志信息报错，未获取到当前操作用户信息");
            return;
        }

        // 获取HttpServletRequest对象
        ServletRequestAttributes servletRequestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // 获取IP信息
        String ipAddress = IpUtils.getIpAddress(request);
        // 组装日志的实体对象
        Logs logs = Logs.builder()
                .user(loginUser.getUsername())
                .operation(systemLogs.operation()) // 操作模块
                .type(systemLogs.type().getValue()) // 获取到用户操作信息
                .ip(ipAddress)
                .time(DateUtil.now())
                .build();

        // 插入数据到数据库
        ThreadUtil.execAsync(() -> {
            logsService.save(logs);
        });
    }
}
