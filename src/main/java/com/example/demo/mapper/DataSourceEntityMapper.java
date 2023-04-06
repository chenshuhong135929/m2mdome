package com.example.demo.mapper;

import com.example.demo.entity.DataSourceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSourceEntityMapper {
    List<DataSourceEntity> selectList();
}
