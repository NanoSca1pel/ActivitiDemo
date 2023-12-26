package com.lht;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月12日 17:31
 */
public class ActivitiTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    /**
     * 通过代码配置数据库加载流程引擎
     */
    @Test
    public void testProcessWithJdbcInCode() {
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        //连接数据库的配置
        config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        config.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?nullCatalogMeansCurrent=true&serverTimezone=GMT%2B8");
        config.setJdbcUsername("root");
        config.setJdbcPassword("123456");
        config.setDatabaseType("mysql");
        //创建工作流引擎
        ProcessEngine processEngine = config.buildProcessEngine();
        //打印流程引擎
        System.out.println("processEngine:" + processEngine);
    }


    /**
     * 通过配置文件加载流程引擎
     */
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
        // ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


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

    /**
     * 部署流程
     */
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

    /**
     * 部署压缩包格式的流程
     * @throws Exception
     */
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

    /**
     * 部署本地磁盘上的流程
     * @throws FileNotFoundException
     */
    @Test
    public void testDiskDeploy() throws FileNotFoundException {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream("diskHoliday", new FileInputStream("E:\\myprojects\\oa\\oa-activiti-demo\\src\\main\\resources\\bpmn\\holiday.bpmn20.xml")) //增加bpmn.xml文件
                .name("出差审批流程") //名称
                .deploy();

        System.out.println(deployment.toString());
    }

    /**
     * 启动流程
     */
    @Test
    public void testStartProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday");

        System.out.println("ProcessDefinitionId: " + instance.getProcessDefinitionId());
        System.out.println("Id: " + instance.getId());
        System.out.println("ActivityId: " + instance.getActivityId());
    }

    /**
     * 查询钱晓琦的所有流程实例
     */
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

    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("2501")
                //.processDefinitionKey("holiday")
                .taskAssignee("钱晓琦")
                .singleResult();
        taskService.complete(task.getId());
    }

    /**
     * 查询流程定义
     */
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

    /**
     * 删除流程
     */
    @Test
    public void deleteProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //只删除任务信息，历史表信息不会删除
        repositoryService.deleteDeployment("17501");
        //，则不可以
        //设置为false非级别删除方式，如果该流程定义下存在已经运行的流程，使用普通删除报错
        //则可以设置true级联删除流程定义，即使该流程有流程实例启动也可以将流程及相关记录全部删除，包含历史信息
        //repositoryService.deleteDeployment("2501", true);
    }

    /**
     * 下载流程对应的资源文件
     * @throws Exception
     */
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

    /**
     * 将工作流的图生成SVG图片
     * @throws IOException
     */
    @Test
    public void testGenerateSVG() throws IOException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        BpmnModel bpmnModel = repositoryService.getBpmnModel("test:1:2503");

        ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        //InputStream inputStream = generator.generateDiagram(bpmnModel, lastTask, Collections.emptyList(), "宋体", "宋体", "宋体", true, "test");
        InputStream inputStream = generator.generateDiagram(bpmnModel, "endEvent", "宋体", "宋体");

        String imageName = "image-" + Instant.now().getEpochSecond() + ".svg";
        FileUtils.copyInputStreamToFile(inputStream, new File("processes/" + imageName));

    }

    /**
     * 生成流程图
     *
     * @throws IOException 生成流程图异常！
     */
    @Test
    public void generateImage() throws IOException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        HistoryService historyService = processEngine.getHistoryService();

        HistoricTaskInstance historicTaskInstance = historyService
                .createHistoricTaskInstanceQuery()
                .orderByTaskCreateTime().desc()
                .list().get(0);

        BpmnModel model = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());

        ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        //lastTask是当前任务执行到的位置
        List<HistoricActivityInstance> lastTasks =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(historicTaskInstance.getProcessInstanceId())
                        .orderByHistoricActivityInstanceStartTime()
                        .desc()
                        .list();
        List<String> lastTask = lastTasks.stream()
                .map(HistoricActivityInstance::getActivityId)
                .limit(1)
                .collect(Collectors.toList());
        lastTask.add(lastTasks.get(0).getActivityId());

        //七个参数分别是：
        //  BPMNModel
        //  高光节点
        //  高光顺序流
        //  活动字体名称
        //  标签字体名称
        //  批注字体名称
        //  生成默认关系图
        //  默认关系图映像文件名
        InputStream inputStream = generator.generateDiagram(
                model, lastTask, Collections.emptyList(), "宋体", "宋体", "宋体", true, "holiday");

        String imageName = "image-" + Instant.now().getEpochSecond() + ".svg";
        FileUtils.copyInputStreamToFile(inputStream, new File("processes/" + imageName));
    }

    /**
     * 查询历史流程实例
     */
    @Test
    public void testQueryHistoryProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            System.out.println("historicProcessInstance.getId() = " + historicProcessInstance.getId());
            System.out.println("historicProcessInstance.getProcessDefinitionId() = " + historicProcessInstance.getProcessDefinitionId());
            System.out.println("historicProcessInstance.getProcessDefinitionKey() = " + historicProcessInstance.getProcessDefinitionKey());
        }
    }

    /**
     * 查询历史活动
     */
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

    /**
     * 查询历史任务
     */
    @Test
    public void testQueryHistoryTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().list();

        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            System.out.println("historicTaskInstance.getId() = " + historicTaskInstance.getId());
            System.out.println("historicTaskInstance.getProcessInstanceId() = " + historicTaskInstance.getProcessInstanceId());
            System.out.println("historicTaskInstance.getStartTime() = " + historicTaskInstance.getStartTime());
            System.out.println("historicTaskInstance.getEndTime() = " + historicTaskInstance.getEndTime());
            System.out.println("historicTaskInstance.getName() = " + historicTaskInstance.getName());
        }
    }

    /**
     * 指定代办人
     */
    @Test
    public void delegateTask() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().list();
        String taskId = list.get(0).getId();
        String loginName = "李四";
        //指定代办人
        taskService.delegateTask(taskId, loginName);
    }

    /**
     * 正在运行的任务表中被委派人办理后的任务会回到委派人 ，历史任务表中也一样,只是多了一个人进行审批
     */
    @Test
    public void resolveTask() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().list();
        String taskId = list.get(0).getId();
        //委派人解决任务
        taskService.resolveTask(taskId);
    }

    /**
     * 任务转办,将任务交给其他人处理
     */
    @Test
    public void turnTask() {
        String taskId =  "7511";
        String assignee = "李四";
        TaskService taskService = processEngine.getTaskService();
        taskService.setAssignee(taskId, assignee);
    }

    /**
     * 设置任务持有人
     */
    @Test
    public void setOwner(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().list();
        String taskId = list.get(0).getId();
        String loginName = "王五";
        taskService.setOwner(taskId, loginName);
    }

    /**
     * 反向查询任务：根据持有人查询任务
     */
    @Test
    public void queryByOwner(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskOwner("王五").list();
        for (Task task : list) {
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getName() = " + task.getName());
            System.out.println("task.getOwner() = " + task.getOwner());
        }
    }

    /**
     * 添加审批意见
     */
    @Test
    public void addComment() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService
                .createTaskQuery()
                .singleResult();
        //任务ID、流程实例ID、审批意见
        taskService.addComment(task.getId(),"232504","同意审批");
    }

    /**
     * 查询任务审批意见
     */
    @Test
    public void getTaskComments() {
        TaskService taskService = processEngine.getTaskService();
        List<Comment> taskComments = taskService.getTaskComments("20134");
        for (Comment taskComment : taskComments) {
            System.out.println("taskComment.getFullMessage() = " + taskComment.getFullMessage());
        }
    }
}
