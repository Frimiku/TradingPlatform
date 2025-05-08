package com.system.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.User;
import com.system.business.mapper.UserMapper;
import com.system.business.service.UserService;
import com.system.common.Logs.LogType;
import com.system.common.Logs.SystemLogs;
import com.system.common.Result;
import com.system.exception.ServiceException;
import com.system.utils.ThreadLocalUtil;
import com.system.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    /**
     * 新添用户
     * @param user
     * @return
     */
    @PostMapping("/add")
    @SystemLogs(operation = "用户",type = LogType.ADD)
    public Result add(@RequestBody User user){
        try {
            userService.save(user);
        }catch (Exception e){
            if(e instanceof DuplicateKeyException){
                return Result.error("插入数据库错误");
            }else{
                return Result.error("系统错误");
            }
        }
        return Result.success();
    }

    @PutMapping("/update")
    @SystemLogs(operation = "用户",type = LogType.UPDATE)
    public Result update(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @SystemLogs(operation = "用户",type = LogType.DELETE)
    public Result delete(@PathVariable Integer id){
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User currentUser = userMapper.selectById(currentUserID);
        //User currentUser = TokenUtils.getCurrentUser();
        if (id.equals(currentUser.getId())) {
            // 如果删除的id 等于 当前的id
            throw new ServiceException("不能删除当前用户");
        }
        userService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    @SystemLogs(operation = "用户",type = LogType.BATCH_DELETE)
    public Result batchDelete(@RequestBody List<Integer> ids){ // [7,6,..]
        // 获取当前用户信息
        Integer currentUserID = ThreadLocalUtil.get();
        User currentUser = userMapper.selectById(currentUserID);
        //User currentUser = TokenUtils.getCurrentUser();
        if (null != currentUser && null != currentUser.getId() && ids.contains(currentUser.getId())){
            throw new ServiceException("不能删除当前用户");
        }
        userService.removeBatchByIds(ids);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(){
        /*List<User> userList = userService.list();
        Collections.sort(userList, Comparator.comparing(User::getId).reversed());*/
        List<User> users = userService.list();
        return Result.success(users);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        User user = userService.getById(id);
        return Result.success(user);
    }

    //分页查询
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String username,
                               @RequestParam String name){
        // 默认倒序，让最新的数据在最上方
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(username),"username",username);
        queryWrapper.like(StrUtil.isNotBlank(name),"name",name);
        // select * from user where username like '%#{username}%' and name like '%#{name}%'
        Page<User> page = userService.page(new Page<>(pageNum,pageSize), queryWrapper);
        return Result.success(page);
    }

    // 数据导出至excel
    @GetMapping("/export")
    public void exportData(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String ids,
                           HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<User> list;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(); // 导出结果按照升序排
        if (StrUtil.isNotBlank(ids)){
            // 第一种：根据选中的id导出【选择框】
            // ["1","2","3"] => [1,2,3]
            List<Integer> idsArr1 = Arrays.stream(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
            queryWrapper.in("id",idsArr1);
        }else{
            // 第二种：全部导出（queryWrapper中元素为空） 或 条件导出（queryWrapper中元素不为空）
            queryWrapper.like(StrUtil.isNotBlank(username),"username",username);
            queryWrapper.like(StrUtil.isNotBlank(name),"name",name);
        }
        list = userService.list(queryWrapper);

        writer.write(list,true); // 将数据全部放置输出对象writer中

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        // 设置导出的文件的名称
        response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode("用户信息表","UTF-8") + ".xlsx");

        ServletOutputStream outputStream = response.getOutputStream(); //生成输出流对象，目的就是导出至excel
        writer.flush(outputStream,true); // 将writer中数据全部刷新至数据流中，一并传至excel中,并关闭
        writer.close();
        // 保险起见，再次对【数据流】进行刷新并关闭
        outputStream.flush();
        outputStream.close();
    }

    // excel批量导入
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws IOException {
        // 1、获取流：file.getInputStream()
        // 2、将流写入到reader中
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        // 3、通过readAll方法将数据读出
        List<User> userList = reader.readAll(User.class);
        // 4、写入数据到数据库
        try {
            userService.saveBatch(userList);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("批量导入失败");
        }
        return Result.success();
    }
}
