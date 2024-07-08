package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
* @author 灵均
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2024-07-07 21:25:06
*/
public interface UserService extends IService<User> {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
