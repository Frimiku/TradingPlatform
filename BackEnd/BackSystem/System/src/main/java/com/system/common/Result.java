package com.system.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    //请求返回编码(成功与否)：0表示成功，1表示失败
    private Integer code;
    //提示信息
    private String msg;
    //响应数据：向前端返回后端数据(成功时才返回)
    private T data;

    //快速返回操作成功响应数据（带响应数据）
    public static <E> Result<E> success(E data){
        return new Result<>(200,"请求成功",data);
    }

    //快速返回操作成功响应数据
    public static Result success(){
        return new Result(200,"请求成功",null);
    }

    public static Result error(String msg){
        return new Result(401,msg,null);
    }

    public static Result error(Integer code,String msg){
        return new Result(code,msg,null);
    }

    public static Result error(){
        return new Result(500,"系统错误",null);
    }
}
