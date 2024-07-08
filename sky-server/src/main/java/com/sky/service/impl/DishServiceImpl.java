package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.vo.DishVO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author 灵均
* @description 针对表【dish(菜品)】的数据库操作Service实现
* @createDate 2024-07-03 22:27:17
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void saveDish(DishDTO dishDTO) {
        //插入菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        //主键回显
        for(DishFlavor temp:dishDTO.getFlavors()){
            temp.setDishId(dish.getId());
        }
        //在dish_flavor中插入该菜品的特点
        dishFlavorService.saveBatch(dishDTO.getFlavors());
    }

    @Override
    public PageResult pageDish(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page=dishMapper.pageDish(dishPageQueryDTO);

        Long total=page.getTotal();
        List<DishVO> records=page.getResult();
        PageResult pageResult=new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        return pageResult;
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //如果菜品在售，不能删除
        for(Long id:ids){
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus()== StatusConstant.ENABLE){
                //在售，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //如果菜品被关联到某套餐，则不能删除
        for (Long id:ids){
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getDishId,id);
            SetmealDish flag = setmealDishMapper.selectOne(wrapper);
            if(flag!=null){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        //删除菜品信息
        dishMapper.deleteBatchIds(ids);

        //删除菜品的口味信息
        for (Long id:ids){
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(wrapper);
        }
    }

    @Override
    public DishVO show(Long id) {
        return dishMapper.getDishVoById(id);
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        //修改菜品的基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        //删除原先的菜品口味信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDTO.getId());
        dishFlavorService.remove(wrapper);
        //插入新的口味信息
        dishFlavorService.saveBatch(dishDTO.getFlavors());
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Dish::getId,id);
        wrapper.set(Dish::getStatus,status);
        dishMapper.update(null,wrapper);
    }

    @Override
    public List<Dish> getDishesByCategoryId(Long id) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,id);
        return dishMapper.selectList(wrapper);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味

            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,d.getId());

            List<DishFlavor> flavors = dishFlavorMapper.selectList(wrapper);

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}




