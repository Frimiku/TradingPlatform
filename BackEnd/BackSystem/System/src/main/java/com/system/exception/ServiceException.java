package com.system.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private Integer code;

    public ServiceException(String msg){
        super(msg);
        this.code = 500;
    }

    public ServiceException(Integer code,String msg){
        super(msg);
        this.code = code;
    }
}
