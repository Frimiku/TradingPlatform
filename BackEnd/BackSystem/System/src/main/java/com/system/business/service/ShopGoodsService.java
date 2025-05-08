package com.system.business.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.ShopGoods;
import com.system.business.mapper.ShopGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShopGoodsService extends ServiceImpl<ShopGoodsMapper, ShopGoods> {
    private ShopGoodsMapper shopGoodsMapper;

    @Autowired
    public ShopGoodsService(ShopGoodsMapper shopGoodsMapper) {
        this.shopGoodsMapper = shopGoodsMapper;
    }

    public List<Map<String, Object>> getGoodsSalesRank() {
        return shopGoodsMapper.selectGoodsSalesRank();
    }

}
