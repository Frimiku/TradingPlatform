package com.system.common;

import java.lang.annotation.*;

// 自定义注解 AuthAccess : 直接掉过token过滤器
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {
}
