package com.system.common.Logs;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLogs {
    // 操作模块
    String operation();
    // 操作类型
    LogType type();
}