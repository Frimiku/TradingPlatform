package com.system.business.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.ShopOrders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ShopOrdersMapper extends BaseMapper<ShopOrders> {
    @Select("SELECT state, COUNT(*) as count FROM shop_orders GROUP BY state;")
    List<Map<String, Object>> selectState();

    @Select("SELECT DATE_FORMAT(time, '%Y-%m-%d') AS date, COUNT(*) AS count " +
            "FROM shop_orders " +
            "GROUP BY date " +
            "ORDER BY date")
    List<Map<String, Object>> selectDailyOrderCount();

    Page<ShopOrders> page(Page<ShopOrders> page, @Param("name") String name, @Param("role") String role, @Param("userId") Integer userId);
}
