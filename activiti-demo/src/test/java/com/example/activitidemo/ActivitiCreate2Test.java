package com.example.activitidemo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * @author tlj
 * @date 2019/7/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiDemoApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class ActivitiCreate2Test {
    @org.junit.Test
    public void createActivitiEngine(){
        /**
         * 3. 通过ProcessEngines 来获取默认的流程引擎
         */
        //  默认会加载类路径下的 activiti.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println("通过ProcessEngines 来获取流程引擎");

        // 产生RepositoryService 是Activiti的仓库服务类。所谓的仓库指流程定义文档的两个文件：bpmn文件和流程图片。
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //----------- 可以产生DeploymentBuilder，用来定义流程部署的相关参数
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
//        deploymentBuilder.addClasspathResource("processes/leave-process.bpmn");//bpmn文件的名称
        deploymentBuilder.addClasspathResource("processes/leave2.bpmn");//bpmn文件的名称
        deploymentBuilder.key("myProcess_3");
        deploymentBuilder.name("1请假申请单流程");
        Deployment deployment = deploymentBuilder.deploy();
        //4.输出部署的一些信息
        System.out.println(deployment.getName());
//        System.out.println(deployment.get);

        // select * from `ACT_GE_PROPERTY`;这时这个表中会多条数据

        List<ProcessDefinition> p = repositoryService.createProcessDefinitionQuery().list();
        for(int i=0;i<p.size();i++){
            System.out.println(p.get(i).getKey());
        }
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess_3");//启动流程，ID必须与你配置的一致

        System.out.println("ok......");






    }
}
