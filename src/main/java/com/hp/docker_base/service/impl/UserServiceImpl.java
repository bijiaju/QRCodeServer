package com.hp.docker_base.service.impl;

import com.hp.docker_base.bean.User;
import com.hp.docker_base.mapper.UserMapper;
import com.hp.docker_base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public User selectUserByNameAndPassword(String name, String password) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("password",password);
        return userMapper.selectUserByNameAndPassword(params);
    }
}
