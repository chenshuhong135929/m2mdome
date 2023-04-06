package com.example.demo.controller;


import com.example.demo.config.DataSourceContextHolder;
import com.example.demo.entity.User;
import com.example.demo.service.ChangeDataSourceService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YZY
 * @date 2021年05月28日 16:05
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private ChangeDataSourceService changeDataSourceService;

    @Autowired
    private UserService userService;

    @GetMapping("test")
    private String test(){
        String datasourceId = "db2";
        changeDataSourceService.changeDS(datasourceId);
        List<User> list1 = userService.listUser();
        System.out.println(list1);


        datasourceId = "db3";
        changeDataSourceService.changeDS(datasourceId);
        List<User> list2 = userService.listUser();
        System.out.println(list2);
        //切回主数据源
        DataSourceContextHolder.removeDataSource();
        return "ok";
    }

    @GetMapping("test2")
    private String test2(){
        String datasourceId = "db2";
        changeDataSourceService.changeDS(datasourceId);
        User userEntity = new User();
        userEntity.setName("11");
        userService.saveUser(userEntity);
        DataSourceContextHolder.removeDataSource();
        return "ok";
    }
}