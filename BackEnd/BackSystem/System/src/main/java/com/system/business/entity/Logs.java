package com.system.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@TableName("logs")
public class Logs {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String operation;
    private String type;
    private String ip;
    private String user;
    private String time;
}
