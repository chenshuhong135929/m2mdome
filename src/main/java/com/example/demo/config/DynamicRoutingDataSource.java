package com.example.demo.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.StringUtils;

import com.example.demo.entity.DataSourceEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.*;
import java.util.Map;
import java.util.Set;

/**
 * @author YZY
 * @date 2021年05月27日 10:25
 */
@Slf4j
@Data
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private boolean debug = true;
    /**
     * 存储我们注册的数据源
     */
    private volatile Map<Object, Object> customDataSources;

    @Override
    protected Object determineCurrentLookupKey() {
        String datasourceId = DataSourceContextHolder.getDataSource();
        if(!StringUtils.isEmpty(datasourceId)){
            Map<Object, Object> map = this.customDataSources;
            if(map.containsKey(datasourceId)){
                log.info("当前数据源是：{}",datasourceId);
            }else{
                log.info("不存在数据源：{}",datasourceId);
                return null;
            }
        }else{
            log.info("当前是默认数据源");
        }
        return datasourceId;
    }




    @Override
    public void setTargetDataSources(Map<Object, Object> param) {

        super.setTargetDataSources(param);
        this.customDataSources = param;
    }


    /**
     * @Description: 检查数据源是否已经创建
     * @author YZY
     * @date 2021/5/27 10:52
     * @param dataSource
     */
    public void checkCreateDataSource(DataSourceEntity dataSource){
        String datasourceId = dataSource.getDatasourceId();
        Map<Object, Object> map = this.customDataSources;
        if(map.containsKey(datasourceId)){
            //这里检查一下之前创建的数据源，现在是否连接正常
            DruidDataSource druidDataSource = (DruidDataSource) map.get(datasourceId);
            boolean flag = true;
            DruidPooledConnection connection = null;
            try {
                connection = druidDataSource.getConnection();
            } catch (SQLException throwables) {
                //抛异常了说明连接失效吗，则删除现有连接
                log.error(throwables.getMessage());
                flag = false;
                delDatasources(datasourceId);
                //
            }finally {
                //如果连接正常记得关闭
                if(null != connection){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        log.error(e.getMessage());
                    }
                }
            }
            if(!flag){
                createDataSource(dataSource);
            }
        }else {
            createDataSource(dataSource);
        }
    }

    /**
     * @Description: 创建数据源
     * @author YZY
     * @date 2021/5/27 11:15
     * @param dataSource
     */
    private void createDataSource(DataSourceEntity dataSource) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(), dataSource.getPassWord());
            if(connection==null){
                log.error("数据源配置有错误，DataSource：{}",dataSource);
            }else{
                connection.close();
            }

            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setName(dataSource.getDatasourceId());
            druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            druidDataSource.setUrl(dataSource.getUrl());
            druidDataSource.setUsername(dataSource.getUserName());
            druidDataSource.setPassword(dataSource.getPassWord());
            druidDataSource.setMaxActive(20);
            druidDataSource.setMinIdle(5);
            //获取连接最大等待时间，单位毫秒
            druidDataSource.setMaxWait(6000);
            String validationQuery = "select 1 from dual";
            //申请连接时执行validationQuery检测连接是否有效，防止取到的连接不可用
            druidDataSource.setTestOnBorrow(true);
            druidDataSource.setValidationQuery(validationQuery);
            druidDataSource.init();
            this.customDataSources.put(dataSource.getDatasourceId(),druidDataSource);
            // 将map赋值给父类的TargetDataSources
            setTargetDataSources(this.customDataSources);
            // 将TargetDataSources中的连接信息放入resolvedDataSources管理
            super.afterPropertiesSet();

        } catch (Exception e) {
            log.error("数据源创建失败",e);
        }
    }

    /**
     * @Description: 删除数据源
     * @author YZY
     * @date 2021/5/27 11:02
     * @param datasourceId
     */
    private void delDatasources(String datasourceId) {
        Map<Object, Object> map = this.customDataSources;
        Set<DruidDataSource> druidDataSourceInstances = DruidDataSourceStatManager.getDruidDataSourceInstances();
        for (DruidDataSource dataSource : druidDataSourceInstances) {
            if (datasourceId.equals(dataSource.getName())) {
                map.remove(datasourceId);
                //从实例中移除当前dataSource
                DruidDataSourceStatManager.removeDataSource(dataSource);
                // 将map赋值给父类的TargetDataSources
                setTargetDataSources(map);
                // 将TargetDataSources中的连接信息放入resolvedDataSources管理
                super.afterPropertiesSet();
            }
        }
    }


}
