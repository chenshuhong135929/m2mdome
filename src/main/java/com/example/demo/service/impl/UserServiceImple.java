package com.example.demo.service.impl;


import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImple implements UserService {

    @Autowired
    UserMapper userMapper;



    @Override
    public List<User> listUser() {
        return userMapper.listUser();
    }

    @Override
    public void saveUser(User userEntity) {

    }
}
