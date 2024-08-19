package com.sun.flow.listener;


import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.springframework.stereotype.Component;

public class MyListener01 implements TaskListener {
    /**
     * 监听器触发的回调方法
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("---->自定义的监听器执行了");
        if(EVENTNAME_CREATE.equals(delegateTask.getEventName())){
            // 表示是Task的创建事件被触发了
            // 指定当前Task节点的处理人
            delegateTask.setAssignee("boge666");
        }

    }
}
