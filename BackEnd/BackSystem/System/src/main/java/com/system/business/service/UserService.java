package com.system.business.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.User;
import com.system.business.mapper.UserMapper;
import com.system.exception.ServiceException;
import com.system.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean save(User entity) {
        if(StrUtil.isBlank(entity.getName())){
            entity.setName(entity.getUsername());
        }
        if(StrUtil.isBlank(entity.getPassword())){
            entity.setPassword("123"); //默认密码：123
        }
        if(StrUtil.isBlank(entity.getRole())){
            entity.setRole("用户"); //默认角色身份：用户
        }
        return super.save(entity);
    }

    public User selectByUsername(String username){
        // QueryWrapper : 条件查询器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        // 根据用户名查询数据库的用户信息
        return userMapper.selectOne(queryWrapper);
    }

    //验证用户账户是否合法
    public User login(User user) {
        User dbUser = selectByUsername(user.getUsername());
        if(null == dbUser){
            // 抛出一个自定义异常
            throw new ServiceException("用户名或密码错误");
        }
        if(!user.getPassword().equals(dbUser.getPassword())){
            // 抛出一个自定义异常
            throw new ServiceException("用户名或密码错误");
        }
        // 生成 token
        String token = TokenUtils.createToken(dbUser.getId(), dbUser.getPassword());
        dbUser.setToken(token);

        // 将token存入redis中
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(dbUser.getId().toString(),token); // UserID设置为键，token设置为值：避免覆盖问题

        return dbUser;
    }

    public User register(User user) {
        // 根据用户名查询数据库的用户信息
        User dbUser = selectByUsername(user.getUsername());
        if(null != dbUser){
            // 抛出一个自定义异常
            throw new ServiceException("用户名已存在");
        }
        user.setName(user.getUsername());
        userMapper.insert(user);
        return user;
    }

    public void resetPassword(User user) {
        User dbUser = selectByUsername(user.getUsername());
        if(null == dbUser){
            // 抛出一个自定义异常
            throw new ServiceException("用户名不存在");
        }
        if(!user.getPhone().equals(dbUser.getPhone())){
            throw new ServiceException("验证错误");
        }
        dbUser.setPassword("123"); //重置密码
        // 删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(dbUser.getId().toString());
        userMapper.updateById(dbUser);
    }
}