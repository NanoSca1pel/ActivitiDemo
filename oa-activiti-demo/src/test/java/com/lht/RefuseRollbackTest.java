package com.lht;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 流程驳回/回退
 * @author: lhtao
 * @date: 2023年12月26日 11:04
 */
public class RefuseRollbackTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    /**
     * 测试流程驳回-经理同意
     * @throws IOException
     */
    @Test
    public void testRefusePmCompleteTask() throws IOException {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        Map<String,Object> variables = new HashMap<>();
        variables.put("pmresult",1);
        taskService.complete(task.getId(),variables);
    }

    /**
     * 测试流程驳回-人事驳回
     * @throws IOException
     */
    @Test
    public void testRefuseHrCompleteTask() throws IOException {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        Map<String,Object> variables = new HashMap<>();
        variables.put("hrresult",0);
        taskService.complete(task.getId(),variables);
    }


    /**
     * 测试流程回退-请假申请
     * @throws IOException
     */
    @Test
    public void testRollbackApplyTask() throws IOException {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        taskService.complete(task.getId());
        // generateImage();
    }

    /**
     * 测试流程回退-经理不同意并回退
     * @throws IOException
     */
    @Test
    public void testRollbackCompleteTask() throws IOException {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        Map<String,Object> variables = new HashMap<>();
        variables.put("result",0);
        taskService.complete(task.getId(),variables);
        // generateImage();
    }
}
