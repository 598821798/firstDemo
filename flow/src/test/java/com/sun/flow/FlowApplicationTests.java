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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .addClasspathResource("process-01/Example02.bpmn20.xml") // 部署一个流程
                .name("监听器")
                .deploy();
        System.out.println(deploy1.getId());
    }

    /**
     * 启动一个流程
     */
    @Test
    void startFlow() {

        // act_re_procdef 表中的id  还有key
        String processId = "Example02:1:6b63dcbb-5ba6-11ef-aeb5-38d57a108ea9";

        String processKey = "FirstFlow";

//        runtimeService.startProcessInstanceById(processId);
//        runtimeService.startProcessInstanceByKey(processKey);

        //在启动流程实例的时候 可以绑定对应的流程变量或者表达式的值
        Map<String,Object> map = new HashMap<>();
        map.put("myAsssign2","zhangsan");
        runtimeService.startProcessInstanceById(processId,map);



    }

    /**
     * 根据用户查询信息
     */
    @Test
    void findFlow() {
        //act_ru_task  这张表里去找
        List<Task> zhangsan = taskService.createTaskQuery()
                .taskAssignee("lisi")
                .list();
        zhangsan.forEach(vo-> System.out.println(vo.getId()));
    }

    /**
     * 任务审批
     */
    @Test
    void completeTask(){
        Map<String,Object> map= new HashMap<>();
        map.put("myAssignee1","lisi"); // 绑定一个变量 动态传参审批人
        // taskService.complete("3ce3c69e-5ba0-11ef-8ae0-38d57a108ea9",map);



        // 完成任务的审批 根据id
        taskService.complete("52386c26-5ba7-11ef-963c-38d57a108ea9"); //myAssignee1 //3ce3c69e-5ba0-11ef-8ae0-38d57a108ea9
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
