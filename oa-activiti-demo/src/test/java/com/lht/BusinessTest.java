package com.lht;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月13日 16:53
 */
public class BusinessTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    @Test
    public void testProcess() {

        RuntimeService runtimeService = processEngine.getRuntimeService();
        //businessKey就是填写对应业务系统的数据id
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday", "9001");
        System.out.println("instance businessKey: " + instance.getBusinessKey());
    }

    @Test
    public void testSuspendProcessInstance() {
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //查询出差流程
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("holiday")
                .singleResult();

        if (processDefinition.isSuspended()) {
            //如果是挂起状态，则激活
            //activationDate一般是前端传一个时间过来作为将要激活的时间
            repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("id: " + processDefinition.getId() + "激活");
        } else {
            //如果是激活状态，则挂起
            repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("id: " + processDefinition.getId() + "挂起");
        }
    }

    @Test
    public void testSuspendSingleProcessInstance() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //查询出差流程
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("35001")
                .singleResult();

        if (instance.isSuspended()) {
            //如果是挂起状态，则激活
            //activationDate一般是前端传一个时间过来作为将要激活的时间
            runtimeService.activateProcessInstanceById(instance.getId());
            System.out.println("id: " + instance.getId() + "激活");
        } else {
            //如果是激活状态，则挂起
            runtimeService.suspendProcessInstanceById(instance.getId());
            System.out.println("id: " + instance.getId() + "挂起");
        }
    }
}
