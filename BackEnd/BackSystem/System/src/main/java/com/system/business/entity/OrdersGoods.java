package com.system.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("orders_goods")
public class OrdersGoods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;
    private Integer GoodsId;
    private Integer num;
}
