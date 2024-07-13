package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 灵均
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2024-07-09 14:57:38
* @Entity com.sky.Orders
*/
public interface OrdersMapper extends BaseMapper<Orders> {

    Page<Orders> listOrders(Integer status, Long currentId);

    /**
     * 根据条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> queryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据条件动态统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据条件动态查询订单
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 统计之指定时间区间销量前十
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}




