package org.lht.springboot.service;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.lht.springboot.entity.ActDeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月04日 9:48
 */
@Service
public class ActivitiServiceImpl {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    @Qualifier(value = "processEngine7")
    private ProcessEngine processEngine;

    @Autowired
    private ActDeModelServiceImpl actDeModelService;

    public void deploy(String id) {
        ActDeModel actDeModel = actDeModelService.listModelById(id);
        Deployment deploy = repositoryService.createDeployment()
                .addInputStream(actDeModel.getName(), new ByteArrayInputStream(actDeModel.getThumbnail())) //增加bpmn.xml文件
                .name(actDeModel.getName())
                .key(actDeModel.getModelKey())
                .deploy();
        System.out.println(deploy.toString());
    }
}
