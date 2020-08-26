package com.luxx.web.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.luxx.web.mapper.**", sqlSessionFactoryRef = "dcSqlSessionFactory")
public class DataSourceConfiguration {
    @Bean(name = "dcDataSource")
    @ConfigurationProperties("spring.datasource.dc")
    @Primary
    public DataSource dcDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "dcSqlSessionFactory")
    @Primary
    public SqlSessionFactory dcSqlSessionFactory(@Qualifier("dcDataSource") DataSource dataSource,
                                                 @Value("${spring.datasource.dc.mapperLocations}") String configLocationResource)
            throws Exception {
        return MybatisConfiguration.buildSqlSessionFactory(dataSource, configLocationResource, null);
    }

}
