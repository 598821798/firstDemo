package com.sun.flow;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class FlowApplicationTests {

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;
    /**
     * 流程部署
     */
    @Test
    void deployFlow(){
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

}
