package com.system.business.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.business.entity.ShopGoods;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ShopGoodsMapper extends BaseMapper<ShopGoods> {
    // 商品销量排名（前10）
    @Select("SELECT g.name, SUM(og.num) AS total_sold " +
            "FROM orders_goods og " +
            "JOIN shop_goods g ON og.goods_id = g.id " +
            "GROUP BY g.id " +
            "ORDER BY total_sold DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectGoodsSalesRank();

}
