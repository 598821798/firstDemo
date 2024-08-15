package com.sun.flow;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

public class FlowableTest1 {
    private static final String url  = "jdbc:mysql://localhost:3306/flowable-learn?serverTimezone=UTC&nullCatalogMeansCurrent=true";
    /**
     * 部署流程到数据库
     * 在非spring环境下的使用
     */
    @Test
    void deployFlow(){
        // 流程引擎的配置对象 关联相关的数据源
        ProcessEngineConfiguration configuration =new StandaloneInMemProcessEngineConfiguration()
                .setJdbcUrl(url)
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取流程引擎对象
        ProcessEngine processEngine = configuration.buildProcessEngine();

        Deployment deploy = processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("process-01/FirstFlow.bpmn20.xml")
                .name("第一个流程案例")
                .deploy();
        System.out.println(deploy.getId());
    }
}
