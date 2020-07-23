package org.example.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.enums.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author liuwg-a
 * @date 2020/7/23 10:44
 * @description 拦截自定义 mapper 的方法，进行切面织入
 */
@Component
@Order(value = -10000)
@Aspect
public class DatasourceSwitchAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceSwitchAspect.class);

    /**
     * 定义注入所有 org.example.dao.db1.mapper 下所有类的方法
     *
     */
    @Pointcut("execution(* org.example.dao.db1.mapper..*.*(..))")
    private void db1Aspect() {
    }

    /**
     * 定义注入所有 org.example.dao.db2.mapper 下所有类的方法
     */
    @Pointcut("execution(* org.example.dao.db2.mapper..*.*(..))")
    private void db2Aspect() {
    }

    @Before("db1Aspect()")
    public void db1() {
        LOGGER.info("============ switch to db1 datasource ============");
        DBContextHolder.set(DBType.db1);
    }

    @Before("db2Aspect()")
    public void db2() {
        LOGGER.info("============ switch to db2 datasource ============");
        DBContextHolder.set(DBType.db2);
    }

}
