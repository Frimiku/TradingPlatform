package com.system.exception;

import com.system.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result serviceException(ServiceException e){
        return Result.error(e.getCode(),e.getMessage());
    }

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result globalException(Exception e){
        e.printStackTrace(); //打印错误信息
        return Result.error(500,"系统错误");
    }
}