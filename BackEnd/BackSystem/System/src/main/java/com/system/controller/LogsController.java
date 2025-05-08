package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.Logs;
import com.system.business.service.LogsService;
import com.system.business.service.UserService;
import com.system.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 系统日志相关接口
@RestController
@RequestMapping("/logs")
public class LogsController {
    @Autowired
    private LogsService logsService;

    @Autowired
    private UserService userService;

    // 单个删除操作
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        logsService.removeById(id);
        return Result.success();
    }

    // 批量删除操作
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids){ // [7,6,..]
        logsService.removeBatchByIds(ids);
        return Result.success();
    }

    //分页查询
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String operation,
                               @RequestParam String type,
                               @RequestParam String user){
        // 默认倒序，让最新的数据在最上方
        QueryWrapper<Logs> queryWrapper = new QueryWrapper<Logs>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(operation),"operation",operation);
        queryWrapper.like(StrUtil.isNotBlank(type),"type",type);
        queryWrapper.like(StrUtil.isNotBlank(user),"user",user);
        Page<Logs> page = logsService.page(new Page<>(pageNum,pageSize), queryWrapper);
        return Result.success(page);
    }

}
