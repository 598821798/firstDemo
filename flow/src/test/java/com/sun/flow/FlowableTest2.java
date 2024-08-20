package com.sun.flow;

import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
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

/**
 * 流程变量
 */
@SpringBootTest(classes = FlowApplication.class)
public class FlowableTest2 {
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
                .name("流程变量案例")
                .deploy();
        System.out.println(deploy1.getId());
    }

    /**
     * 启动一个流程
     */
    @Test
    void startFlow() {

        // act_re_procdef 表中的id  还有key
        String processId = "FirstFlow:1:1855f182-5dd4-11ef-b072-38d57a108ea9";

        //在启动流程实例的时候 可以绑定对应的流程变量或者表达式的值
        Map<String,Object> map = new HashMap<>();
        map.put("var1","test1");
        map.put("var2","test2");
        map.put("var3","test3");
        runtimeService.startProcessInstanceById(processId,map);

    }

    /**
     * 赋值
     */
    @Test
    void  setVariable(){
        String id = "7cf9a4a0-5dd4-11ef-a507-38d57a108ea9";
        runtimeService.setVariable(id,"var4","test4");

        runtimeService.setVariableLocal(id,"local","localTest1");


        String taskID= "7cfc63c8-5dd4-11ef-a507-38d57a108ea9";
        taskService.setVariable(taskID,"taskVar","var1");
        taskService.setVariableLocal(taskID,"taskVarLocal","var1Local");  //是和taskid绑定的 是局部变量 任务当前节点有关
    }
    /**
     * 获取定义的变量
     */
    @Test
    void getVariables(){
        String executionId = "7cf9a4a0-5dd4-11ef-a507-38d57a108ea9";
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        System.out.println(variables+"//////////");

        String taskID= "52b5a8bb-5e0c-11ef-a6e0-38d57a108ea9";
        Map<String, Object> variables1 = taskService.getVariables(taskID);
        System.out.println(variables1+"~~~~~~~~");

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
        map.put("taskVar1com","var1"); // 绑定一个变量 动态传参审批人
        // taskService.complete("3ce3c69e-5ba0-11ef-8ae0-38d57a108ea9",map);



        // 完成任务的审批 根据id
        taskService.complete("52b5a8bb-5e0c-11ef-a6e0-38d57a108ea9",map); //myAssignee1 //3ce3c69e-5ba0-11ef-8ae0-38d57a108ea9
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
