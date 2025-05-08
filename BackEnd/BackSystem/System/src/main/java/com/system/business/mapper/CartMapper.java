package com.system.business.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.business.entity.Cart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CartMapper extends BaseMapper<Cart> {

    Page<Cart> page(Page<Object> objectPage, Integer userId, String role, String name);

    @Update("update cart set num = #{num} where id = #{id}")
    void updateNum(@Param("num") Integer num,@Param("id") Integer id);
}
