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
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月14日 13:50
 */
public class VariableTest {

    private ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

    @Test
    public void testDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/holiday-global.bpmn20.xml") //增加bpmn.xml文件
                .name("出差审批流程-参数变量") //名称
                .deploy();
    }

    /**
     * 启动流程实例，设置流程全局变量的值
     */
    @Test
    public void testStartProcessSetGlobalVariable() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> map = new HashMap<>();

        Holidays holidays = new Holidays();
        holidays.setNum(5d);
        map.put("holidays", holidays);
        map.put("assignee0", "钱晓琦2");
        map.put("assignee1", "孙宁2");
        map.put("assignee2", "陆洪涛2");
        map.put("assignee3", "朱小婷2");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("holiday-global", map);

        System.out.println("ProcessDefinitionId: " + instance.getProcessDefinitionId());
        System.out.println("Id: " + instance.getId());
        System.out.println("ActivityId: " + instance.getActivityId());
    }

    @Test
    public void testCompleteTask() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                //.processInstanceId("55001")
                .processDefinitionKey("holiday-global")
                .taskAssignee("孙宁")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成任务，设置剩余流程全局变量的值
     */
    @Test
    public void testCompleteTaskSetGlobalVariable() {
        TaskService taskService = processEngine.getTaskService();
        Map<String, Object> map = new HashMap<>();
        Holidays holidays = new Holidays();
        holidays.setNum(2d);
        map.put("holidays", holidays);
        Task task = taskService.createTaskQuery()
                //.processInstanceId("55001")
                .processDefinitionKey("holiday-global")
                .taskAssignee("孙宁2")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId(), map);
        }
    }

    /**
     * 完成任务，设置当前流程局部变量的值
     * Local变量在任务结束后无法在当前流程实例执行中使用，如果后续的流程执行需要用到此变量则会报错。
     */
    @Test
    public void testCompleteTaskSetLocalVariable() {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                //.processInstanceId("55001")
                .processDefinitionKey("holiday-global")
                .taskAssignee("孙宁2")
                .singleResult();
        if (task != null) {

            Holidays holidays = new Holidays();
            holidays.setNum(2d);

            //taskService.setVariableLocal(task.getId(), "holidays", holidays);
            //taskService.complete(task.getId());

            //Map<String, Object> map = new HashMap<>();
            //map.put("holidays", holidays);
            //taskService.setVariablesLocal(task.getId(), map);
            //taskService.complete(task.getId());


            Map<String, Object> map = new HashMap<>();
            map.put("holidays", holidays);
            taskService.complete(task.getId(), map, true);
        }
    }
}
