package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
* @author 灵均
* @description 针对表【dish(菜品)】的数据库操作Service
* @createDate 2024-07-03 22:27:17
*/
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void saveDish(DishDTO dishDTO);

    /**
     * 根据条件分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 回显菜品相关信息
     * @param id
     * @return
     */
    DishVO show(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 修改菜品发布状态
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据分类id查询分类的所有菜品
     * @param id
     * @return
     */
    List<Dish> getDishesByCategoryId(Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
