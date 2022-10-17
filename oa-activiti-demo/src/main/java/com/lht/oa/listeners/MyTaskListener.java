package com.lht.oa.listeners;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Description: 自定义任务监听器
 * @author: lhtao
 * @date: 2022年10月14日 10:55
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("创建申请".equals(delegateTask.getName()) && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("钱晓琦");
        }
    }
}
