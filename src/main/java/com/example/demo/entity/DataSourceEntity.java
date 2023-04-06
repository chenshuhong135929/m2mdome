package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSourceEntity implements Serializable {

    private String id;


    private String datasourceId;

    private String url;


    private String userName;


    private String passWord;

}