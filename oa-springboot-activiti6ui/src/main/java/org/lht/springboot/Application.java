package org.lht.springboot;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.lht.springboot.enums.DataSourceKey;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * @Description:
 * @author: lhtao
 * @date: 2022年10月14日 16:53
 */
/*//activiti6版本使用
@SpringBootApplication(exclude = {
        org.activiti.spring.boot.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})*/
//activiti7版本使用
@SpringBootApplication(exclude = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
@MapperScan(basePackages={"org.lht.springboot.**.mapper*"})
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();

    }

    // @Bean(name = "processEngine6")
    // public ProcessEngine getProcessEngine6() {
    //     ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
    //     //连接数据库的配置
    //     config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    //     config.setJdbcDriver("com.mysql.cj.jdbc.Driver");
    //     config.setJdbcUrl("jdbc:mysql://localhost:3306/activiti6ui?nullCatalogMeansCurrent=true&serverTimezone=GMT%2B8");
    //     config.setJdbcUsername("root");
    //     config.setJdbcPassword("123456");
    //     config.setDatabaseType("mysql");
    //     //创建工作流引擎
    //     ProcessEngine processEngine = config.buildProcessEngine();
    //     return processEngine;
    // }

    @Bean(name = "processEngine7")
    @Primary
    public ProcessEngine getProcessEngine7() {
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        //连接数据库的配置
        config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        config.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?nullCatalogMeansCurrent=true&serverTimezone=GMT%2B8");
        config.setJdbcUsername("root");
        config.setJdbcPassword("123456");
        config.setDatabaseType("mysql");
        //创建工作流引擎
        ProcessEngine processEngine = config.buildProcessEngine();
        return processEngine;
    }
}
