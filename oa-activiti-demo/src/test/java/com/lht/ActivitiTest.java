package com.lht;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月12日 17:31
 */
public class ActivitiTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    @Test
    public void testProcess() {
        //创建流程引擎配置文件
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti-spring.cfg.xml");
        //也可以指定bean名称引入
        //ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml", beanName);

        //通过配置文件创建流程
        ProcessEngine processEngine = config.buildProcessEngine();
        //如果将 activiti.cfg.xml 文件名及路径固定，且 activiti.cfg.xml 文件中有 processEngineConfiguration 的配置, 还可以使用默认方法创建
        //ProcessEngine processEngine = ProcessEngines.getprocessEngine();


        //Activiti的资源管理类,提供了管理和控制流程发布包和流程定义的操作
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //Activiti的流程运行管理类。可以从这个服务类中获取很多关于流程执行相关的信息
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //Activiti的任务管理类。可以从这个类中获取任务的信息。
        TaskService taskService = processEngine.getTaskService();

        //Activiti的历史管理类，可以查询历史信息，执行流程时，引擎会保存很多数据（根据配置），比如流程实例启动时间，任务的参与者， 完成任务的时间，每个流程实例的执行路径，等等。 这个服务主要通过查询功能来获得这些数据
        HistoryService historyService = processEngine.getHistoryService();

        //Activiti的引擎管理类，提供了对 Activiti 流程引擎的管理和维护功能，这些功能不在工作流驱动的应用程序中使用，主要用于 Activiti 系统的日常维护
        ManagementService managementService = processEngine.getManagementService();

        System.out.println(processEngine);
    }

    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday.bpmn20.xml") //增加bpmn.xml文件
                .addClasspathResource("bpmn/holiday.png") //增加图片文件
                .name("出差审批流程") //名称
                .deploy();

        System.out.println(deployment.toString());
    }

    @Test
    public void testZipDeploy() throws Exception{
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "bpmn/holiday.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 获取repositoryService
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("出差审批流程-压缩包") //如果zip包包含多个流程，则不需要这个name
                .deploy();
        System.out.println("id：" + deployment.getId());
        System.out.println("Name：" + deployment.getName());
    }

    @Test
    public void testStartProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday");

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
