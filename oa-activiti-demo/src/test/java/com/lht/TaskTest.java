package com.lht;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 多种任务类型测试
 * @author: lhtao
 * @date: 2023年12月26日 9:15
 */
public class TaskTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    /**
     * 测试ServiceTask
     */
    @Test
    public void testMyServiceTask(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String,Object> variables = new HashMap<>();
        variables.put("day",10);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("MyServiceTask",variables);
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getName() = " + processInstance.getName());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
    }

    /**
     * 测试ScriptTask
     */
    @Test
    public void testScriptTask() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> map = new HashMap<>();
        map.put("inputArray", Arrays.asList(1, 2));
        runtimeService.startProcessInstanceByKey("MyScriptTask", map);
    }

    /**
     * 测试ReceiveTask-发起流程
     * 启动流程实例，UserTask1进入当前任务。
     */
    @Test
    public void testReceiveTaskStart(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("MyReceiveTask");
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getName() = " + processInstance.getName());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
    }

    /**
     * 测试ReceiveTask-完成UserTask
     * 当UserTask1完成后进入ReceiveTask，执行了ReceiveTask对应的代理ReceiveTaskDelegate。
     * act_ru_task中竟然没有数据，而act_ru_exectuion中当然活动竟然是receiveTask。
     */
    @Test
    public void testReceiveTaskCompleteUserTask(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getName() = " + task.getName());
            System.out.println("task.getAssignee() = " + task.getAssignee());
            taskService.complete(task.getId());
        }

        //没有数据
        List<Task> runtimeTaskList = taskService.createTaskQuery().list();
        for (Task task : runtimeTaskList) {
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getName() = " + task.getName());
            System.out.println("task.getAssignee() = " + task.getAssignee());
        }

        //有数据
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<Execution> executions = runtimeService.createExecutionQuery().list();
        for (Execution execution : executions) {
            System.out.println("execution.getId() = " + execution.getId());
            System.out.println("execution.getName() = " + execution.getName());
            System.out.println("execution.getProcessInstanceId() = " + execution.getProcessInstanceId());
        }
    }

    /**
     * 测试ReceiveTask-触发触发器
     * 当UserTask1完成后进入ReceiveTask，执行了ReceiveTask对应的代理ReceiveTaskDelegate。
     * act_ru_task中竟然没有数据，而act_ru_exectuion中当然活动竟然是receiveTask。
     */
    @Test
    public void testTriggeReceiveTask() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        for (Execution execution : runtimeService.createExecutionQuery().list()) {
            System.out.println("execution.getId() = " + execution.getId());
        }
        System.out.println("****************************************************");
        HistoryService historyService = processEngine.getHistoryService();
        for (HistoricActivityInstance historicActivityInstance : historyService.createHistoricActivityInstanceQuery().activityType("receiveTask").list()) {
            System.out.println("historicActivityInstance.getId() = " + historicActivityInstance.getId());
            System.out.println("historicActivityInstance.getActivityId() = " + historicActivityInstance.getId());

        }
    }

    /**
     * 方法之间可以相互调用，同理，流程定义之间也可以相关调用，达到流程定义复用的目的。
     * 测试CallActivityTask-发起流程process2
     * 发起流程任务Task还是在Process2流程上
     */
    @Test
    public void testCallActivityTaskStart(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process2");
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getName() = " + processInstance.getName());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
    }

    /**
     * 测试CallActivityTask-处理任务
     * 处理完【发起申请】会触发Process1的【经理审批】,【经理审批】完继续走到Process2【财务审批】
     */
    @Test
    public void testCallActivityTaskApplyTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        taskService.setAssignee(task.getId(), "zhangsan");
        taskService.complete(task.getId());
    }

    /**
     * 手动任务是在没有任何业务流程执行引擎或任何应用程序帮助的情况下执行的任务。
     * 当我们完成【经理审批】会自动执行【手工任务】后到达【人事审批】：手工任务主要是能自动执行一些逻辑，比如我们可以在手工任务上加上一些监听等等。
     * 测试ManualTask-发起流程
     * 处理完【发起申请】会触发Process1的【经理审批】,【经理审批】完继续走到Process2【财务审批】
     */
    @Test
    public void testManualTaskStart(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ManualTask");
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getName() = " + processInstance.getName());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
    }

    /**
     * 测试MailTask-发起流程
     */
    @Test
    public void testMailTaskStart(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        //配置文件中设好后可以不需要设置
        // variables.put("from", "beyond_world@sina.cn");
        variables.put("to", "4407509@qq.com");
        variables.put("subject", "Hello World");
        variables.put("text", "欢迎学习Activiti邮件发送功能！");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("EmailTask",variables);
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getName() = " + processInstance.getName());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
    }
}
