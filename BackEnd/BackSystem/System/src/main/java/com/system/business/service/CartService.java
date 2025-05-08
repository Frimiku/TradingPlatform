package com.system.business.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.business.entity.Cart;
import com.system.business.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {
    private CartMapper cartMapper;

    @Autowired
    public CartService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }
}
