package com.lht.oa.listeners;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Description: 子流程任务监听器
 * @author: lhtao
 * @date: 2023年12月26日 11:01
 */
public class MyChildListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("delegateTask.getName() = " + delegateTask.getName());
        // create,assignment,delete,all
        System.out.println("delegateTask.getEventName() = " + delegateTask.getEventName());
        // delegateTask.setAssignee("xxx");
    }
}
