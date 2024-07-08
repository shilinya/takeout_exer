package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.mapper.UserMapper;
import com.sky.utils.HttpClientUtil;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
* @author 灵均
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2024-07-07 21:25:06
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    //微信服务接口地址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties properties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        //调用微信接口服务，获取当前用户的openId
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", properties.getAppid());
        map.put("secret", properties.getSecret());
        map.put("grant_type", "authorization_code");
        map.put("js_code", userLoginDTO.getCode());

        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        //判断openId是否为空，如果为空表示登录失败，抛出业务异常

        if (openid == null || openid.equals("")) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            //新用户，完成注册
             user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //如果是新用户，完成自动注册

        //返回这个用户对象
        //mp自动完成主键回显
        return user;
    }
}




