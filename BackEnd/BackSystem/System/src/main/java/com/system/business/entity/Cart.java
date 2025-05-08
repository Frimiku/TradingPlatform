package com.system.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private Integer userId;
    private Integer num;
    private String time;
    // 这个字段不在数据库中，用于做业务处理用的,用于显示作者id对应的名称
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String name;
    // 商品名称
    @TableField(exist = false)
    private String goodsName;
    @TableField(exist = false)
    private String goodsImg;
    @TableField(exist = false)
    private BigDecimal price;
}