package com.example.demo.service.impl;


import com.example.demo.config.DataSourceContextHolder;
import com.example.demo.config.DynamicRoutingDataSource;
import com.example.demo.entity.DataSourceEntity;
import com.example.demo.mapper.DataSourceEntityMapper;
import com.example.demo.service.ChangeDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author YZY
 * @date 2021年05月28日 9:45
 */

@Service
public class ChangeDataSourceServiceImpl implements ChangeDataSourceService {

    @Autowired
    private DataSourceEntityMapper dataSourceMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public boolean changeDS(String datasourceId) {
        //切到默认数据源
        DataSourceContextHolder.removeDataSource();
        //找到所有的配置
        List<DataSourceEntity> dataSourceList = dataSourceMapper.selectList();

        if(!CollectionUtils.isEmpty(dataSourceList)){
            for (DataSourceEntity dataSource : dataSourceList) {
                if(dataSource.getDatasourceId().equals(datasourceId)){
                    System.out.println("已找到数据源,datasourceId是：" + dataSource.getDatasourceId());
                    //判断连接是否存在，不存在就创建
                    dynamicRoutingDataSource.checkCreateDataSource(dataSource);
                    //切换数据源
                    DataSourceContextHolder.setDataSource(dataSource.getDatasourceId());
                    return true;
                }
            }
        }
        return false;
    }
}