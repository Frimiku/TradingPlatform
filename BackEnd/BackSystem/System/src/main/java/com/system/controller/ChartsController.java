package com.system.controller;

import com.system.business.mapper.ShopOrdersMapper;
import com.system.business.service.ShopGoodsService;
import com.system.business.service.ShopOrdersService;
import com.system.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Charts")
public class ChartsController {

    @Autowired
    private ShopOrdersService shopOrdersService;

    @Autowired
    private ShopGoodsService shopGoodsService;

    @GetMapping("/pieCharts")
    public Result pieCharts(){
        List<Map<String, Object>> data = shopOrdersService.SelectState();
        return Result.success(data);
    }

    @GetMapping("/barCharts")
    public Result barCharts() {
        List<Map<String, Object>> data = shopGoodsService.getGoodsSalesRank();
        return Result.success(data);
    }

    @GetMapping("/lineCharts")
    public Result lineCharts() {
        List<Map<String, Object>> data = shopOrdersService.getDailyOrderCount();
        return Result.success(data);
    }
}
