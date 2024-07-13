package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
* @author 灵均
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2024-07-09 14:57:38
*/
public interface OrdersService extends IService<Orders> {

    /**
     * 下单接口
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult historyOrders(int page, int pageSize, Integer status);

    /**
     * 取消订单
     * @param id
     */
    void userCancelById(Long id);

    OrderVO details(Long id);

    void repetition(Long id);

    /**
     * 根据条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult queryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计各个状态订单数量
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成派送
     * @param id
     */
    void complete(Long id);


    /**
     * 催单
     * @param id
     */
    void reminder(Long id);

}
