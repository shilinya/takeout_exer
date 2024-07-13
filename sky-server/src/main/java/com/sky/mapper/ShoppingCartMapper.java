package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
* @author 灵均
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2024-07-08 17:36:57
* @Entity com.sky.ShoppingCart
*/
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    /**
     * 根据条件动态查询购物车的商品记录
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> query(ShoppingCart shoppingCart);

    /**
     * 批量插入
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);

}




