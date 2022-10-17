package com.lht.spring;

import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月14日 16:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:activiti-spring.xml")
public class Application {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testBasic() {
        System.out.println("repository: " + repositoryService);
    }
}
