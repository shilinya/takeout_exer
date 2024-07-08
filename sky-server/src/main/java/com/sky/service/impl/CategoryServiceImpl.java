package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.mapper.CategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 灵均
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2024-07-03 13:58:46
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        Category temp = this.getById(categoryDTO.getId());
        if (temp != null) {
            //如果要修改的不为空在修改
            BeanUtils.copyProperties(categoryDTO,category);
            //修改  修改时间和修改人id
            category.setUpdateTime(LocalDateTime.now());
            category.setUpdateUser(BaseContext.getCurrentId());

            //修改
            super.saveOrUpdate(category);
        }
    }

    @Override
    public Result<PageResult> listCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page= categoryMapper.listCategory(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        PageResult pageResult = new PageResult();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        return Result.success(pageResult);
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setCreateUser(BaseContext.getCurrentId());
        super.saveOrUpdate(category);

    }

    @Override
    public List<Category> listCategoryByType(int type) {
        return categoryMapper.listCategoryByType( type);
    }

}




