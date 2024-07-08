package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.mapper.SetmealMapper;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 灵均
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2024-07-05 09:38:55
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public Result<PageResult> listSetmeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page=setmealMapper.listSetmeal(setmealPageQueryDTO);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRecords(page.getResult());
        return Result.success(result);
    }

    @Override
    public SetmealVO getSetmealVoById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        //查询套餐所在分类的名称
        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealVO.setCategoryName(category.getName());
        //查询套菜和菜品的联系
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> dishes = setmealDishMapper.selectList(wrapper);
        setmealVO.setSetmealDishes(dishes);
        return setmealVO;
    }

    @Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        //新增套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);

        //新增套餐与菜品的相关信息
        //先回显套餐id赋值费setmealDIsh
        for(SetmealDish st:setmealDTO.getSetmealDishes()){
            st.setSetmealId(setmeal.getId());
        }
        for(SetmealDish st:setmealDTO.getSetmealDishes()){
            setmealDishMapper.insert(st);
        }
    }

    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //更新套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        super.saveOrUpdate(setmeal);
        //更新套餐的菜品相关信息

        //先删除
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        setmealDishMapper.delete(wrapper);
        //插入新关系
        //先回显setmealid给setmealDish
        for(SetmealDish st:setmealDTO.getSetmealDishes()){
            st.setSetmealId(setmeal.getId());
        }
        for(SetmealDish st:setmealDTO.getSetmealDishes()){
            setmealDishMapper.insert(st);
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for(Long id:ids) {
            //必须先停售才能删除
            Setmeal flag = setmealMapper.selectById(id);
            if (flag.getStatus()== StatusConstant.ENABLE){
                //在售不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐信息
        setmealMapper.deleteBatchIds(ids);

        //删除套餐和菜品的关系信息
        for(Long id:ids){
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId,id);
            setmealDishMapper.delete(wrapper);
        }
    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> listBySetmeal(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}




