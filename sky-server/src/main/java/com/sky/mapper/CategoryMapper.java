package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;

import java.util.List;

/**
* @author 灵均
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2024-07-03 13:58:46
* @Entity com.sky.Category
*/
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 分页查询菜品分类
     * @param
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> listCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据type查询启用的菜品分类
     * @param type
     * @return
     */
    List<Category> listCategoryByType(int type);

}




