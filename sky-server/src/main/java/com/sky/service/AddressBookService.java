package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.AddressBook;

/**
* @author 灵均
* @description 针对表【address_book(地址簿)】的数据库操作Service
* @createDate 2024-07-09 10:48:10
*/
public interface AddressBookService extends IService<AddressBook> {

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);
}
