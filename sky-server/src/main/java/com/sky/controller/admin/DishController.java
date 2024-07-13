package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.saveDish(dishDTO);
        String key="dish"+dishDTO.getCategoryId();
        cleanCache(key) ;

        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page( DishPageQueryDTO dishPageQueryDTO) {
        PageResult result=dishService.pageDish(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result deleteDishes(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        cleanCache("dish*");
        return Result.success();
    }

    /**
     * 根据id查询菜品的所有信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DishVO> show(@PathVariable Long id) {
        DishVO result=dishService.show(id);
        return Result.success(result);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateDish(dishDTO);
        cleanCache("dish*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,@RequestParam Long id){
        dishService.updateStatus(status,id);
        cleanCache("dish*");
        return Result.success();
    }


    /**
     * 根据分类的id查询该分类下的菜品
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam(value = "categoryId") Long id){
       List<Dish>result= dishService.getDishesByCategoryId(id);
       return Result.success(result);
    }

    private void cleanCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
