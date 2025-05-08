package com.system.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("shop_orders")
public class ShopOrders {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String no;
    private BigDecimal totalPrice;
    private String state;
    private String time;
    private String payTime;
    private Integer userId;

    @TableField(exist = false)
    private List<Cart> carts;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private Integer goodsId;
    @TableField(exist = false)
    private Integer num;
}
