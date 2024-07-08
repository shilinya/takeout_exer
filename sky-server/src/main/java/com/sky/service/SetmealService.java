package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
* @author 灵均
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2024-07-05 09:38:55
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * 根据条件分页查询套餐列表
     * @param setmealPageQueryDTO
     * @return
     */
    Result<PageResult> listSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐的相关信息
     * @param id
     * @return
     */
    SetmealVO getSetmealVoById(Long id);

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void addSetmeal(SetmealDTO setmealDTO);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐相关信息
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    List<Setmeal> listBySetmeal(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);

}
