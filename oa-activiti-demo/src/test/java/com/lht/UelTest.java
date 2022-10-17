package com.lht;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月12日 17:31
 */
public class UelTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday-uel.bpmn20.xml") //增加bpmn.xml文件
                .name("出差审批流程-uel") //名称
                .deploy();

        System.out.println(deployment.toString());
    }

    @Test
    public void testStartProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> map = new HashMap<>();
        map.put("assignee0", "钱晓琦");
        map.put("assignee1", "孙宁");
        map.put("assignee2", "陆洪涛");
        map.put("assignee3", "朱小婷");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday-uel", map);

        System.out.println("ProcessDefinitionId: " + instance.getProcessDefinitionId());
        System.out.println("Id: " + instance.getId());
        System.out.println("ActivityId: " + instance.getActivityId());
    }

    @Test
    public void testFindProcessInstance() {
        TaskService taskService = processEngine.getTaskService();

        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("钱晓琦")
                .list();

        list.forEach(task -> {
            System.out.println("分配的流程定义id: " + task.getProcessDefinitionId());
            System.out.println("分配的任务id: " + task.getId());
            System.out.println("分配的任务执行者: " + task.getAssignee());
            System.out.println("分配的任务名称: " + task.getName());
        });
    }

    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("35001")
                //.processDefinitionKey("holiday")
                //.taskAssignee("朱小婷")
                .singleResult();
        taskService.complete(task.getId());
    }

    @Test
    public void queryProcessDefinition() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("holiday")
                .orderByProcessDefinitionVersion().desc()
                .list();

        list.forEach(pd -> {
            System.out.println("=======================");
            System.out.println("id: " + pd.getId());
            System.out.println("name: " + pd.getName());
            System.out.println("key: " + pd.getKey());
            System.out.println("version: " + pd.getVersion());
            System.out.println("deployment: " + pd.getDeploymentId());
        });
    }

    @Test
    public void deleteProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //只删除人物信息，历史表信息不会删除
        repositoryService.deleteDeployment("17501");
        //，则不可以
        //设置为false非级别删除方式，如果该流程定义下存在已经运行的流程，使用普通删除报错
        //则可以设置true级联删除流程定义，即使该流程有流程实例启动也可以将流程及相关记录全部删除，包含历史信息
        //repositoryService.deleteDeployment("2501", true);
    }

    @Test
    public void downloadFile() throws Exception {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取对应的资源数据
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("holiday")
                .singleResult();

        InputStream xmlIs = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        InputStream pngIs = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());


        //转成输出流，放到本地文件夹下
        FileOutputStream xmlOps = new FileOutputStream(new File("E:\\data\\oa\\holiday.bpmn20.xml"));
        FileOutputStream pngOps = new FileOutputStream(new File("E:\\data\\oa\\holiday.png"));

        //使用工具把输入流转成输出流
        IOUtils.copy(xmlIs, xmlOps);
        IOUtils.copy(pngIs, pngOps);

        //关流
        xmlIs.close();
        xmlOps.close();
        pngIs.close();
        pngOps.close();
    }

    @Test
    public void findHistoryInfo() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                //.processDefinitionId("holiday:1:17504")
                //.processInstanceId("20001")
                .activityName("填写出差单")
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        list.forEach(hai -> {
            System.out.println("=============================");
            System.out.println(hai.getActivityId());
            System.out.println(hai.getActivityName());
            System.out.println(hai.getProcessDefinitionId());
            System.out.println(hai.getProcessInstanceId());
        });
    }
}
