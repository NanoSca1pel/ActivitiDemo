package com.lht.oa.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2023年12月26日 9:13
 */
//@Service如果没有指定名字，默认是类名的首字母小写，即myTaskService。
@Service
public class MyServiceTask {

    public void hasRole(DelegateExecution delegateExecution){
        Integer day = Integer.parseInt(delegateExecution.getVariable("day").toString());
        if(day > 3){
            delegateExecution.setVariable("type",1);
        }else{
            delegateExecution.setVariable("type",0);
        }
    }
}
