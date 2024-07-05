package com.sky.controller.admin;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜品套餐管理")
@RestController
@RequestMapping("admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 跟剧条件分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> listSetmeal(SetmealPageQueryDTO setmealPageQueryDTO){
        return setmealService.listSetmeal(setmealPageQueryDTO);
    }

    /**
     * 根据id查询套餐信息及其相关菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){

        SetmealVO result = setmealService.getSetmealVoById(id);
        return Result.success(result);
    }


    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐的发售状态
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateSetmealStatus(@PathVariable String status,Long id){
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Setmeal::getId,id).set(Setmeal::getStatus,status);
        setmealService.update(wrapper);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();
    }







}
