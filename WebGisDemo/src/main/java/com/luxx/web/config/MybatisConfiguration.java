package com.luxx.web.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MybatisConfiguration {

    public static SqlSessionFactory buildSqlSessionFactory(DataSource source, String configLocationResource, String mybatisPlugin)
            throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(source);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> sourceList = new ArrayList<>();
        for (String resourceLocation : configLocationResource.split(",")) {
            sourceList.addAll(Arrays.asList(resolver.getResources(resourceLocation)));
        }
        sqlSessionFactoryBean.setMapperLocations(sourceList.toArray(new Resource[sourceList.size()]));
        if (mybatisPlugin != null) {
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(mybatisPlugin));
        }
        return sqlSessionFactoryBean.getObject();
    }

}
