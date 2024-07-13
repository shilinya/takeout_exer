package com.sky.controller.user;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;

@RestController
@Api(tags = "订单管理接口")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrdersService ordersService;


    /**
     * 用户下单接口
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {

        OrderSubmitVO result=ordersService.submitOrder(ordersSubmitDTO);

        return Result.success(result);
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);

        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(int page,int pageSize,Integer status) {
       PageResult result= ordersService.historyOrders(page,pageSize,status);
       return Result.success(result);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id) {
        //将订单状态修改为已取消
        ordersService.userCancelById(id);
        return Result.success();
    }
    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = ordersService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        ordersService.repetition(id);
        return Result.success();
    }


    /**
     * 客户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        ordersService.reminder(id);
        return Result.success();
    }
}
