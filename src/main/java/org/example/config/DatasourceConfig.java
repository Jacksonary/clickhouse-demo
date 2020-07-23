package org.example.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.example.enums.DBType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * @author liuwg-a
 * @date 2020/7/23 10:14
 * @description 启动加载多个数据源
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = {"org.example.dao"})
public class DatasourceConfig {

    @Bean(name = "db1")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource db1() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "db2")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource db2() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 动态数据源配置
     * @Primary 优先让 DataSource 指向这个 Datasource 的配置
     * @return
     */
    @Bean
    public DataSource multipleDataSource(@Autowired DataSource db1, @Autowired DataSource db2) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 这里的k-v一定要和DB_CONTEXT_HOLDER ThreadLocal中放置的属性保持一致，否则获取不到会去拿默认的执行
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(DBType.db1, db1);
        targetDataSources.put(DBType.db2, db2);
        dynamicDataSource.setTargetDataSources(targetDataSources);

        // 配置默认的数据源
        dynamicDataSource.setDefaultTargetDataSource(db2);

        return dynamicDataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        // 设置数据源
        sqlSessionFactoryBean.setDataSource(multipleDataSource(db1(), db2()));

        // 设置自定义的配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactoryBean.setConfiguration(configuration);

        // 返回 SqlSessionFactory
        return sqlSessionFactoryBean.getObject();
    }
}
