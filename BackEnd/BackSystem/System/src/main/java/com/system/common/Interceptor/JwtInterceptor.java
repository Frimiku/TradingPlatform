package com.system.common.Interceptor;


import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.system.business.entity.User;
import com.system.business.mapper.UserMapper;
import com.system.common.AuthAccess;
import com.system.exception.ServiceException;
import com.system.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 自定义JWT拦截器
public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //放行OPTIONS请求
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        // 1、拿到token
        String token = request.getHeader("token");  // 前端header内传过来的参数
        //token 是否为 null、空字符串，或者只包含空白字符
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token"); // url参数： ?token=xxx
        }
        // 如果不是映射到方法直接通过【Controller层的方法上添加@AuthAccess】
        if (handler instanceof HandlerMethod) {
            AuthAccess annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if (annotation != null) {
                return true;
            }
        }
        //2、 执行认证——判断token是否存在
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(401, "请登录");
        }
        //3、 获取 token 中的 user id
        String userId;
        try {
            //JWT.decode(token) 解码 jwt token
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(401, "请登录");
        }
        //4、 根据token中的userid进行数据库的查询
        User user = userMapper.selectById(Integer.parseInt(userId));
        // 判断user是否存在
        if (user == null) {
            throw new ServiceException(401, "请登录");
        }
        //5、 从redis中获取相同的token并验证
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String redisToken = operations.get(userId.toString());
        if (redisToken == null || !redisToken.equals(token)){
            throw new ServiceException(401,"token错误");
        }
        //6、 通过【用户密码】加密后生成一个验证器
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            //7、通过 验证器 验证 token
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new ServiceException(401, "请登录");
        }
        //8、将用户ID存入 ThreadLocal
        ThreadLocalUtil.set(Integer.parseInt(userId));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清空ThreadLocal中数据,防止冗余
        ThreadLocalUtil.remove();
    }
}
