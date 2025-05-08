package com.system.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.ShopGoods;
import com.system.business.service.ShopGoodsService;
import com.system.common.AuthAccess;
import com.system.common.Logs.LogType;
import com.system.common.Logs.SystemLogs;
import com.system.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopGoods")
public class ShopGoodsController {
    @Autowired
    private ShopGoodsService shopGoodsService;

    // 新增操作
    @PostMapping("/add")
    @SystemLogs(operation = "商品",type = LogType.ADD)
    public Result add(@RequestBody ShopGoods goods){
        goods.setTime(DateUtil.now()); //2024-06-25 21:24:12
        shopGoodsService.save(goods);
        return Result.success();
    }

    // 修改操作
    @PutMapping("/update")
    @SystemLogs(operation = "商品",type = LogType.UPDATE)
    public Result update(@RequestBody ShopGoods goods){
        shopGoodsService.updateById(goods);
        return Result.success();
    }

    // 单个删除操作
    @DeleteMapping("/delete/{id}")
    @SystemLogs(operation = "商品",type = LogType.DELETE)
    public Result delete(@PathVariable Integer id){
        shopGoodsService.removeById(id);
        return Result.success();
    }

    // 批量删除操作
    @DeleteMapping("/delete/batch")
    @SystemLogs(operation = "商品",type = LogType.BATCH_DELETE)
    public Result batchDelete(@RequestBody List<Integer> ids){ // [7,6,..]
        shopGoodsService.removeBatchByIds(ids);
        return Result.success();
    }

    // 查询所有信息
    @GetMapping("/selectAll")
    public Result selectAll(){
        List<ShopGoods> news = shopGoodsService.list(new QueryWrapper<ShopGoods>().orderByDesc("id"));
        return Result.success(news);
    }

    // 根据ID查询信息
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        ShopGoods goods = shopGoodsService.getById(id);
        return Result.success(goods);
    }

    //分页查询
    @AuthAccess
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String name){
        // 默认倒序，让最新的数据在最上方
        QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<ShopGoods>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(name),"name",name);
        Page<ShopGoods> page = shopGoodsService.page(new Page<>(pageNum,pageSize), queryWrapper);
        return Result.success(page);
    }
}
