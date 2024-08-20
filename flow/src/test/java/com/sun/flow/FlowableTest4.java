package com.sun.flow;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 候选人组
 */
@SpringBootTest(classes = FlowApplication.class)
public class FlowableTest4 {
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

    @Autowired
    private IdentityService identityService;
    /**
     * 维护用户
     */
    @Test
    void createUser() {
        User user = identityService.newUser("lisi");
        user.setEmail("lisi@qq.com");
        user.setFirstName("li");
        user.setLastName("si");
        user.setPassword("1234");
        identityService.saveUser(user);
    }



    /**
     * 用户组的维护
     */
    @Test
    void createGroup() {
        Group group = identityService.newGroup("销售部");
        group.setName("销售部");
        group.setType("type1");
        identityService.saveGroup(group);

        Group group1 = identityService.newGroup("行政部");
        group.setName("行政部");
        group.setType("type2");
        identityService.saveGroup(group1);

    }


    /**
     * 用户组和用户的维护
     */
    @Test
    void createGroupAndUser() {
        //查询对应的用户组
        Group xsb = identityService.createGroupQuery().groupId("销售部").singleResult();
        List<User> list = identityService.createUserQuery().list();
        list.forEach((User user) -> {
            identityService.createMembership(user.getId(),xsb.getId());
        });

    }









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
                .addClasspathResource("process-01/HolidayDemo3.bpmn20.xml") // 部署一个流程
                .name("候选人组案例")
                .deploy();
        System.out.println(deploy1.getId());
    }

    @Test
    void  deleteDeployFlow(){
        repositoryService.deleteDeployment("f70ddbf4-5eca-11ef-b7d3-38d57a108ea9",true);
    }

    /**
     * 启动一个流程
     */
    @Test
    void startFlow() {

        // act_re_procdef 表中的id  还有key
        String processId = "HolidayDemo3:1:669f42ab-5ecb-11ef-a8d3-38d57a108ea9";

        //在启动流程实例的时候 可以绑定对应的流程变量或者表达式的值
//        Map<String,Object> map = new HashMap<>();
//        map.put("candidate1","zhangsan");
//        map.put("candidate2","lisi");
//        map.put("candidate3","wangwu");
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);

    }

    /**
     *  候选人任务的查询
     */
    @Test
    void  findCandidateTask(){
        Group group = identityService.createGroupQuery().groupMember("zhangsan").singleResult();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(group.getId()).list();
        for (Task task : tasks) {
            taskService.claim(task.getId(),"zhangsan");
        }
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
     * 根据登录的用户查询对应的可以拾取的任务
     * 候选人不是审批人  需要通过拾取操作 把候选人变为审批人
     * 多个候选人只有一个可以变成审批人
     * 审批人不想审批了 可以归还 从审批人变成-----> 候选人
     */
    @Test
    void findFlow() {
        //act_ru_task  这张表里去找
        List<Task> zhangsan = taskService.createTaskQuery()
                .taskCandidateOrAssigned("lisi") //根据候选人或者待办人查询任务
                .list();
        zhangsan.forEach(vo-> System.out.println(vo.getId()+"////"));


        List<Task> zhangsan1 = taskService.createTaskQuery()
                .taskCandidateUser("wangwu") //根据候选人查询任务
                .list();
        zhangsan1.forEach(vo-> System.out.println(vo.getId()+"~~~~"));
    }


    /**
     * 指派任务
     */
    @Test
    void setAssigneeTask() {

        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("zhaoliu") //根据候选人查询任务
                .list();

        list.forEach(vo->
                // 归还任务 指派他人
                taskService.setAssignee(vo.getId(),"wangqi"));
    }





    /**
     * 拾取任务
     */
    @Test
    void claimTask() {

        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhaoliu") //根据候选人查询任务
                .list();

        list.forEach(vo->

              taskService.claim(vo.getId(),"zhaoliu"));
    }

    /**
     * 归还任务
     */
    @Test
    void unClaimTask() {

        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("zhaoliu") //根据候选人查询任务
                .list();

        list.forEach(vo->
            // 归还操作
                taskService.unclaim(vo.getId()));
    }



    /**
     * 任务审批
     */
    @Test
    void completeTask(){

//        List<Task> list = taskService.createTaskQuery()
//                .taskAssignee("zhangsan") //根据候选人查询任务
//                .list();
//        Map<String, Object> map = new HashMap<>();
//        map.put("candidate10", "zhaoliu");
//        map.put("candidate11", "wangqi");
//        list.forEach(task -> {
//            System.out.println(task.getId() + "11111111111111111");
//            taskService.complete(task.getId(), map);
//        });
        taskService.complete("cfc5f134-5ecc-11ef-827a-38d57a108ea9");
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
