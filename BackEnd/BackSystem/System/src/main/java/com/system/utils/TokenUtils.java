package com.system.utils;


import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.system.business.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

@Component
public class TokenUtils {
    private static UserMapper staticUserMapper;

    @Resource
    UserMapper userMapper;

    @PostConstruct
    public void setUserService() {
        staticUserMapper = userMapper;
    }

    /**
     * 生成token
     */
    public static String createToken(Integer userId, String sign) {
        return JWT.create().withAudience(userId.toString()) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }
}
