package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;

import java.util.List;

/**
* @author 灵均
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2024-07-03 13:58:46
*/
public interface CategoryService extends IService<Category> {

    /**
     * 修改菜品分类
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);




    /**
     * 根据条件分页查询菜品分类
     * @param
     * @param categoryPageQueryDTO
     * @return
     */
    Result<PageResult> listCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增菜品分类
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    List<Category> listCategoryByType(int type);

}
