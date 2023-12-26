package com.lht;

import com.lht.oa.entity.Holidays;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月14日 13:50
 */
public class GroupTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();


    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday-group.bpmn20.xml") //增加bpmn.xml文件
                .name("出差审批流程-组任务") //名称
                .deploy();
    }

    /**
     * 启动流程实例，设置流程全局变量的值
     */
    @Test
    public void testStartProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday-group");

        System.out.println("ProcessDefinitionId: " + instance.getProcessDefinitionId());
        System.out.println("Id: " + instance.getId());
        System.out.println("ActivityId: " + instance.getActivityId());
    }

    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                //.processInstanceId("55001")
                .processDefinitionKey("holiday-group")
                .taskAssignee("钱晓琦")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 查询组任务
     */
    @Test
    public void findGroupTaskList() {

        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday-group")
                .taskCandidateUser("朱江")
                .list();
        list.forEach(task -> {
            System.out.println("=====================");
            System.out.println("InstanceId: " + task.getProcessInstanceId());
            System.out.println("Id: " + task.getId());
            System.out.println("Assignee: " + task.getAssignee());
            System.out.println("task: " + task.getName());
        });
    }

    /**
     * 查询组任务
     */
    @Test
    public void findGroupTaskList2() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateGroup("group")
                .list();
        list.forEach(task -> {
            System.out.println("=====================");
            System.out.println("InstanceId: " + task.getProcessInstanceId());
            System.out.println("Id: " + task.getId());
            System.out.println("Assignee: " + task.getAssignee());
            System.out.println("task: " + task.getName());
        });
    }

    /**
     * 查询个人任务
     */
    @Test
    public void queryPersonalTask() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday-group")
                .taskAssignee("朱江")
                .list();
        list.forEach(task -> {
            System.out.println("=================");
            System.out.println("InstanceId: " + task.getProcessInstanceId());
            System.out.println("Id: " + task.getId());
            System.out.println("Assignee: " + task.getAssignee());
            System.out.println("TaskName: " + task.getName());
        });
    }

    /**
     * 认领组任务
     */
    @Test
    public void claimTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId("82502")
                .taskCandidateUser("朱江")
                .singleResult();
        Optional.ofNullable(task).ifPresent(t -> taskService.claim(t.getId(), "朱江"));

    }

    /**
     * 完成组任务
     */
    @Test
    public void completeGroupTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holiday-group")
                .taskAssignee("朱江")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 归还组任务 个人任务重新还原成组任务
     */
    @Test
    public void restoreToGroupTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("80001")
                .taskAssignee("朱江")
                .singleResult();
        if (task != null) {
            taskService.setAssignee(task.getId(), null);
        }
    }
}
