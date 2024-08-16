package com.sun.flow;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class FlowApplicationTests {
    // 启动mysql  mysql -u root -p

    // flowable-ui  启动 tomcat startup.bat

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;         // 发起流程需要通过RuntimeService来实现

    @Autowired
    private TaskService taskService;         //任务服务

    /**
     * 流程部署
     */
    @Test
    void deployFlow() {
//        Deployment deploy = processEngine.getRepositoryService().createDeployment()
//                .addClasspathResource("process-01/FirstFlow.bpmn20.xml") // 部署一个流程
//                .name("第一个流程案例")1
//                .deploy();
//        System.out.println(deploy.getId());


        Deployment deploy1 = repositoryService.createDeployment()
                .addClasspathResource("process-01/FirstFlow.bpmn20.xml") // 部署一个流程
                .name("第2个流程案例")
                .deploy();
        System.out.println(deploy1.getId());
    }

    @Test
    void startFlow() {
        // act_re_procdef 表中的id
        String processId = "FirstFlow:1:b9b7bb53-59ea-11ef-a650-38d57a108ea9";

        String processKey = "FirstFlow";

        runtimeService.startProcessInstanceById(processId);

//        runtimeService.startProcessInstanceByKey(processKey);

    }

    /**
     * 根据用户查询信息
     */
    @Test
    void findFlow() {
        List<Task> zhangsan = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .list();
        zhangsan.forEach(vo-> System.out.println(vo.getId()));
    }

    /**
     * 任务审批
     */
    @Test
    void completeTask(){
        // 完成任务的审批 根据id
        taskService.complete("7c443862-5b7d-11ef-bc18-38d57a108ea9"); //3f417a2c-5b7d-11ef-926c-38d57a108ea9
    }

    /**
     * 流程挂起
     */
    @Test
    void suspendedAct(){
        // 流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId("FirstFlow:1:b9b7bb53-59ea-11ef-a650-38d57a108ea9").singleResult();

        // 获取当前流程的状态
        boolean suspended = processDefinition.isSuspended();
        if (suspended){
            System.out.println("激活流程");
            repositoryService.activateProcessDefinitionById("FirstFlow:1:b9b7bb53-59ea-11ef-a650-38d57a108ea9");
        }else {
            System.out.println("挂起流程");
            repositoryService.suspendProcessDefinitionById("FirstFlow:1:b9b7bb53-59ea-11ef-a650-38d57a108ea9");
        }

    }

    /**
     * 挂起流程实例
     */
    @Test
    void suspendInstance(){
        // 挂起流程实例
        runtimeService.suspendProcessInstanceById("a7ae5680-7ba3-11ee-809a-c03c59ad2248");
        // 激活流程实例
        //runtimeService.activateProcessInstanceById("a7ae5680-7ba3-11ee-809a-c03c59ad2248");
    }
}
