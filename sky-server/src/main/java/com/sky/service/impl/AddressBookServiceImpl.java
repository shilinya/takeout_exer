package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.service.AddressBookService;
import com.sky.mapper.AddressBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 灵均
* @description 针对表【address_book(地址簿)】的数据库操作Service实现
* @createDate 2024-07-09 10:48:10
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Transactional
    public void setDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookMapper.update(addressBook, wrapper);

        //2、将当前地址改为默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper=new LambdaUpdateWrapper<AddressBook>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.eq(AddressBook::getId,addressBook.getId());
        updateWrapper.set(AddressBook::getIsDefault,1);
        addressBookMapper.update(addressBook, updateWrapper);

    }
}




