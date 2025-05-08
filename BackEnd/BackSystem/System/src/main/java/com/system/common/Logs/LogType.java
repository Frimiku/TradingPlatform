package com.system.common.Logs;

// 枚举：系统日志的操作类型
public enum LogType {
    // 日志状态（自定义）：记录对数据有影响操作（增删改）
    ADD("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    BATCH_DELETE("批量删除"),
    LOGIN("登录"),
    REGISTER("注册");

    private String value;
    public String getValue() {
        return value;
    }
    LogType(String value) {
        this.value = value;
    }
}
