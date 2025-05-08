package com.system.business.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.OrdersGoods;
import com.system.business.mapper.OrdersGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersGoodsService extends ServiceImpl<OrdersGoodsMapper, OrdersGoods> {
    private OrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    public OrdersGoodsService(OrdersGoodsMapper ordersGoodsMapper) {
        this.ordersGoodsMapper = ordersGoodsMapper;
    }
}
