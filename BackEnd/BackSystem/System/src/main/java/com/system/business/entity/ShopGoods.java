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
@TableName("shop_goods")
public class ShopGoods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private BigDecimal price;
    private String description;
    private String unit;
    private Integer store;
    private String img;
    private String time;
    @TableField(exist = false)
    private Integer num;
}
