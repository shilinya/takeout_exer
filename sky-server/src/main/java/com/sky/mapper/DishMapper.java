package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import java.util.List;

/**
* @author 灵均
* @description 针对表【dish(菜品)】的数据库操作Mapper
* @createDate 2024-07-03 22:27:17
* @Entity com.sky.Dish
*/
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据条件分页查询dish
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询dishVo
     * @param id
     * @return
     */
    DishVO getDishVoById(Long id);

    List<Dish> list(Dish dish);
}




