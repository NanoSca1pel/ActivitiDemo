package com.lht.springboot;

import com.lht.springboot.config.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.GetTasksPayloadBuilder;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.payloads.GetTasksPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月14日 17:22
 */
@SpringBootTest(classes = com.lht.springboot.Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class ActivitiTest {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private TaskRuntime taskRuntime;

    /**
     * 查看流程定义，体验自动部署：check-process-definitions: true
     */
    @Test
    public void processDef() {
        securityConfig.logInAs("admin");
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 20));
        log.info(processDefinitionPage.getTotalItems() + "");
        processDefinitionPage.getContent().forEach(p -> {
            log.info("===================");
            log.info(p.toString());
        });
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {
        securityConfig.logInAs("admin");
        ProcessInstance holiday = processRuntime.start(
                ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("holiday")
                .build()
        );
        log.info(holiday.getId());
        log.info(holiday.toString());
    }

    /**
     * 拾取任务,完成任务
     */
    @Test
    public void testClaimAndCompleteTask() {
        securityConfig.logInAs("朱小婷");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 20));
        //Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 20), new GetTasksPayloadBuilder().withAssignee("other").build());
        if (tasks.getTotalItems() > 0) {
            tasks.getContent().forEach(task -> {

                //拾取
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                log.info("朱小婷 拾取任务: " + task.getId());

                //完成
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            });
        }
    }
}
