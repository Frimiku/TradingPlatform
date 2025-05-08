package com.system.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.Cart;
import com.system.business.entity.User;
import com.system.business.mapper.CartMapper;
import com.system.business.mapper.UserMapper;
import com.system.business.service.CartService;
import com.system.business.service.ShopGoodsService;
import com.system.business.service.UserService;
import com.system.common.Logs.LogType;
import com.system.common.Logs.SystemLogs;
import com.system.common.Result;
import com.system.utils.ThreadLocalUtil;
import com.system.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private UserMapper userMapper;

    // 新增操作
    @PostMapping("/add")
    @SystemLogs(operation = "购物车",type = LogType.ADD)
    public Result add(@RequestBody Cart cart){
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User currentUser = userMapper.selectById(currentUserID);
        // 更新购物车中以相同的数量（去重）
        //Integer userId = TokenUtils.getCurrentUser().getId();
        Integer userId = currentUser.getId();
        Integer goodsId = cart.getGoodsId();
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("goods_id",goodsId);
        Cart db = cartService.getOne(queryWrapper);
        if (db != null){
            db.setNum(db.getNum() + cart.getNum());
            cartService.updateById(db);
            return Result.success();
        }
        // 获取当前登录的用户信息
        //User currentUser = TokenUtils.getCurrentUser();
        cart.setUserId(currentUser.getId());
        cart.setTime(DateUtil.now()); //2024-06-25 21:24:12
        cartService.save(cart);
        return Result.success();
    }

    // 更新数量
    @PostMapping("/num/{id}/{num}")
    @SystemLogs(operation = "购物车",type = LogType.UPDATE)
    public Result updateNum(@PathVariable Integer id, @PathVariable Integer num) {
        cartMapper.updateNum(num, id);
        return Result.success();
    }

    // 修改操作
    @PutMapping("/update")
    @SystemLogs(operation = "购物车",type = LogType.UPDATE)
    public Result update(@RequestBody Cart cart){
        cartService.updateById(cart);
        return Result.success();
    }

    // 单个删除操作
    @DeleteMapping("/delete/{id}")
    @SystemLogs(operation = "购物车",type = LogType.DELETE)
    public Result delete(@PathVariable Integer id){
        cartService.removeById(id);
        return Result.success();
    }

    // 批量删除操作
    @DeleteMapping("/delete/batch")
    @SystemLogs(operation = "购物车",type = LogType.BATCH_DELETE)
    public Result batchDelete(@RequestBody List<Integer> ids){ // [7,6,..]
        cartService.removeBatchByIds(ids);
        return Result.success();
    }

    // 查询所有信息
    @GetMapping("/selectAll")
    public Result selectAll(){
        List<Cart> news = cartService.list(new QueryWrapper<Cart>().orderByDesc("id"));
        return Result.success(news);
    }

    // 根据ID查询信息
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        return Result.success(cartService.getById(id));
    }

    //分页查询
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name){
        // 默认倒序，让最新的数据在最上方
        /*QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(name),"name",name);
        Page<Cart> page = cartService.page(new Page<>(pageNum,pageSize),queryWrapper);
        List<Cart> records = page.getRecords();
        List<User> list = userService.list();
        for (Cart record : records) {
            Integer userId = record.getUserId();
            Integer goodsId = record.getGoodsId();
            User user = userService.getById(userId);
            ShopGoods shopGood = shopGoodsService.getById(goodsId);
            if (null != user){
                record.setUser(user.getName());
            }
            if (null != shopGood){
                record.setName(shopGood.getName());
            }
        }
        return Result.success(page);*/

        // 多表联查
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User currentUser = userMapper.selectById(currentUserID);
        //User currentUser = TokenUtils.getCurrentUser();
        Integer userId = currentUser.getId();
        String role = currentUser.getRole();
        // name指的是商品名称
        return Result.success(cartMapper.page(new Page<>(pageNum,pageSize),userId,role,name));
    }

}
