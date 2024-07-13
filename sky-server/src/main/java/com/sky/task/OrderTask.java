package com.sky.task;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;


    /**
     * 处理超时未支付订单的方法
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder() {

        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getStatus, Orders.PENDING_PAYMENT);
        //下单后15分钟未支付，修改状态为取消
        wrapper.lt(Orders::getOrderTime, LocalDateTime.now().minusMinutes(15));
        wrapper.set(Orders::getStatus, Orders.CANCELLED);
        wrapper.set(Orders::getCancelReason,"订单超时未支付，自动取消");
        wrapper.set(Orders::getCancelTime,LocalDateTime.now());
        ordersMapper.update(null, wrapper);

    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点触发一次
    public void processDeliveryOrder(){

        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Orders::getStatus, Orders.DELIVERY_IN_PROGRESS);
        //订单一直未送达，修改状态为完成
        wrapper.lt(Orders::getOrderTime, LocalDateTime.now().minusMinutes(60));
        wrapper.set(Orders::getStatus, Orders.COMPLETED);

        ordersMapper.update(null, wrapper);
    }
}
