package com.lht;

import com.lht.oa.entity.Holidays;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 并行网关
 * @author: lhtao
 * @date: 2022年10月14日 15:21
 */
public class ParallelGatewayTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday-parallel-gateway.bpmn20.xml") //增加bpmn.xml文件
                .name("出差审批流程-并行网关") //名称
                .deploy();
    }

    @Test
    public void testStartProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> map = new HashMap<>();
        Holidays holidays = new Holidays();
        holidays.setNum(5d);
        map.put("holidays", holidays);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday-parallel-gateway", map);

        System.out.println("ProcessDefinitionId: " + instance.getProcessDefinitionId());
        System.out.println("Id: " + instance.getId());
        System.out.println("ActivityId: " + instance.getActivityId());
    }

    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();

        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holiday-parallel-gateway")
                .taskAssignee("陆洪涛")
                //.processInstanceId("100001")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }
}
