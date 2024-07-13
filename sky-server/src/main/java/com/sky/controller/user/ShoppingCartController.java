package com.sky.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "c端购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看用户购物车所有商品
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> result = shoppingCartService.list(wrapper);
        return Result.success(result);
    }

    /**
     * 清空用户购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result clean(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(wrapper);
        return Result.success();
    }

    /**
     * 删除购物车找中的一个商品
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
