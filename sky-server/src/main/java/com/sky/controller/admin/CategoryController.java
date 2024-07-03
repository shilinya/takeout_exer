package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜品分类管理接口")
@RestController
@RequestMapping("admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品分类
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品分类")
    public Result<PageResult> listCategory(CategoryPageQueryDTO categoryPageQueryDTO) {

        return categoryService.listCategory(categoryPageQueryDTO);
    }

    /**
     * 启用或禁用菜品分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品分类")
    public Result updateCategoryStatus(@PathVariable int status,int id){
        LambdaUpdateWrapper<Category> wraper = new LambdaUpdateWrapper<>();
        wraper.eq(Category::getId, id);
        wraper.set(Category::getStatus, status);
        categoryService.update(wraper);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据id删除菜品分类")
    public Result deleteCategory(int id) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        long count = dishService.count(dishWrapper);
        if (count > 0) {
            //该分类下还绑定有其他东西，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        }
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        count=setmealService.count(setmealWrapper);
        if (count > 0) {
            //该分类下还绑定有其他东西，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryService.removeById(id);
        return Result.success();
    }

    /**
     * 新增菜品分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 根据分类查询已启用的分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类查询")
    public Result<List<Category>> listCategory(int type) {
         List<Category> result=categoryService.listCategoryByType(type);
         return Result.success(result);
    }


}
