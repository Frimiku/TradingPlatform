package com.system.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.*;
import com.system.business.mapper.ShopOrdersMapper;
import com.system.business.mapper.UserMapper;
import com.system.business.service.*;
import com.system.common.Logs.LogType;
import com.system.common.Logs.SystemLogs;
import com.system.common.Result;
import com.system.utils.ThreadLocalUtil;
import com.system.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/shopOrders")

public class ShopOrdersController {
    @Autowired
    private ShopOrdersService shopOrdersService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersGoodsService ordersGoodsService;

    @Autowired
    private ShopOrdersMapper shopOrdersMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private UserMapper userMapper;

    // 新增操作
    @PostMapping("/add")
    @SystemLogs(operation = "订单",type = LogType.ADD)
    public Result add(@RequestBody ShopOrders shopOrders){
        if (shopOrders.getId() == null){
            // 获取当前登录的用户信息
            Integer currentUserID = ThreadLocalUtil.get();
            User currentUser = userMapper.selectById(currentUserID);
            // User currentUser = TokenUtils.getCurrentUser();
            Date date = new Date();
            shopOrders.setUserId(currentUser.getId());
            shopOrders.setTime(DateUtil.formatDateTime(date)); //2024-06-25 21:24:12
            shopOrders.setNo(DateUtil.format(date,"yyyyMMdd")+System.currentTimeMillis());
            //ShopGoods shopGoods = new ShopGoods();
            // 创建订单
            shopOrdersService.save(shopOrders);
            List<Cart> carts = shopOrders.getCarts();
            for (Cart cart : carts) {
                OrdersGoods ordersGoods = new OrdersGoods();
                ordersGoods.setGoodsId(cart.getGoodsId());
                ordersGoods.setNum(cart.getNum());
                ordersGoods.setOrderId(shopOrders.getId());
                ordersGoodsService.save(ordersGoods);
                // 删除购物车数据
                cartService.removeById(cart.getId());
            }
        }else{
            shopOrdersService.updateById(shopOrders);
        }
        return Result.success();
    }

    // 新增操作（直接购买）
    @PostMapping("/buy")
    @SystemLogs(operation = "订单",type = LogType.ADD)
    public Result buy(@RequestBody ShopOrders orders){
        // 加入购物车
        Date date = new Date();
        orders.setTime(DateUtil.now()); //2024-06-25 21:24:12
        orders.setNo(DateUtil.format(date,"yyyyMMdd")+System.currentTimeMillis());
        orders.setState("未付款");
        shopOrdersService.save(orders);
        // 添加到关联表中
        OrdersGoods ordersGoods = new OrdersGoods();
        ordersGoods.setGoodsId(orders.getGoodsId());
        ordersGoods.setNum(orders.getNum());
        ordersGoods.setOrderId(orders.getId());
        ordersGoodsService.save(ordersGoods);
        return Result.success();
    }

    // 修改操作(付款)
    @SystemLogs(operation = "订单",type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody ShopOrders shopOrders){
        Date date = new Date();
        shopOrders.setPayTime(DateUtil.formatDateTime(date));
        shopOrdersService.updateById(shopOrders);
        return Result.success();
    }

    // 单个删除操作
    @DeleteMapping("/delete/{id}")
    @SystemLogs(operation = "订单",type = LogType.DELETE)
    public Result delete(@PathVariable Integer id){
        shopOrdersService.removeById(id);
        return Result.success();
    }

    // 批量删除操作
    @DeleteMapping("/delete/batch")
    @SystemLogs(operation = "订单",type = LogType.BATCH_DELETE)
    public Result batchDelete(@RequestBody List<Integer> ids){ // [7,6,..]
        shopOrdersService.removeBatchByIds(ids);
        return Result.success();
    }

    // 查询所有信息
    @GetMapping("/selectAll")
    public Result selectAll(){
        List<ShopOrders> news = shopOrdersService.list(new QueryWrapper<ShopOrders>().orderByDesc("id"));
        return Result.success(news);
    }

    @GetMapping("/getGoodsById/{id}")
    public Result getGoodsById(@PathVariable Integer id) {
        QueryWrapper<OrdersGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", id);
        ArrayList<ShopGoods> goodsList = new ArrayList<>();
        List<OrdersGoods> ordersGoodsList = ordersGoodsService.list(queryWrapper);// 可能有好几个
        for (OrdersGoods ordersGoods : ordersGoodsList) {
            Integer goodsId = ordersGoods.getGoodsId();
            ShopGoods goods = shopGoodsService.getById(goodsId);
            goods.setNum(ordersGoods.getNum());
            goodsList.add(goods);
        }
        return Result.success(goodsList);
    }

    // 根据ID查询信息
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        ShopOrders shopOrders = shopOrdersService.getById(id);
        User user = userService.getById(shopOrders.getUserId());
        if (null != user){
            shopOrders.setName(user.getName());
        }
        return Result.success(shopOrders);
    }

    //分页查询
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name){
        // 默认倒序，让最新的数据在最上方
        QueryWrapper<ShopOrders> queryWrapper = new QueryWrapper<ShopOrders>().orderByDesc("id");
        if (!"".equals(name)){
            queryWrapper.like("name",name);
        }
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User currentUser = userMapper.selectById(currentUserID);
        // User currentUser = TokenUtils.getCurrentUser();
        return Result.success(shopOrdersMapper.page(new Page<>(pageNum, pageSize), name, currentUser.getRole(), currentUser.getId()));
    }

}
