package com.lht.oa.listeners;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Description: 自定义流程监听器
 * 任务监听器只能监听UserTask，流程监听器用在流程的不同的阶段上：
 * ●开始事件和结束事件的开始和结束
 * ●经过输出顺序流
 * ●流程活动的开始和结束
 * ●流程网关的开始和结束
 * ●中间事假的开始和结束
 * @author: lhtao
 * @date: 2022年10月14日 10:55
 */
public class MyExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("execution.getCurrentFlowElement().getId() = " + execution.getCurrentFlowElement().getId());
        System.out.println("execution.getCurrentFlowElement().getName() = " + execution.getCurrentFlowElement().getName());
        System.out.println("execution.getEventName() = " + execution.getEventName());
        System.out.println("execution.getProcessDefinitionId() = " + execution.getProcessDefinitionId());
        System.out.println("execution.getProcessInstanceId() = " + execution.getProcessInstanceId());
    }
}
