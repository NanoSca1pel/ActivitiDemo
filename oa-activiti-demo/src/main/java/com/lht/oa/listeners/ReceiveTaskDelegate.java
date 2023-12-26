package com.lht.oa.listeners;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.Map;

/**
 * @Description: 接收任务
 * ReceiveTask 称之为接收任务、等待任务等。当执行流进入ReceiveTask时会先执行执行监听器，然后整个执行流进入wait等待状态，
 * 而且在act_ru_task中不没有当前正在进行中的任务，当执行流执行trigger()方法时才会触发正在睡眠的ReceiveTask，进入下一个节点。
 * @author: lhtao
 * @date: 2023年12月26日 9:30
 */
public class ReceiveTaskDelegate implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        System.out.println(execution.getEventName() + "-" + execution.getCurrentFlowElement().getId());
    }
}
