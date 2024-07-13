package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author 灵均
* @description 针对表【setmeal(套餐)】的数据库操作Mapper
* @createDate 2024-07-05 09:38:55
* @Entity com.sky.Setmeal
*/
public interface SetmealMapper extends BaseMapper<Setmeal> {

    /**
     * 根据条件分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<Setmeal> listSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    Integer countByMap(Map map);
}




