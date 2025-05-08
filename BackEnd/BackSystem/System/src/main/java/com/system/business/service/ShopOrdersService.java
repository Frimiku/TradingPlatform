package com.system.business.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.ShopOrders;
import com.system.business.mapper.ShopOrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShopOrdersService extends ServiceImpl<ShopOrdersMapper, ShopOrders> {
    private ShopOrdersMapper shopOrdersMapper;

    @Autowired
    public ShopOrdersService(ShopOrdersMapper shopOrdersMapper) {
        this.shopOrdersMapper = shopOrdersMapper;
    }

    public List<Map<String, Object>> SelectState() {
        return shopOrdersMapper.selectState();
    }

    public List<Map<String, Object>> getDailyOrderCount() {
        return baseMapper.selectDailyOrderCount();
    }
}
