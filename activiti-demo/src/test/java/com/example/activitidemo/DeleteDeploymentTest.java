package com.example.activitidemo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
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
public class DeleteDeploymentTest {
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
        // 定义流程定义的key
        String processDefinitionKey="myProcess_1";
        // 先使用流程定义的key, 查询出所有版本的流程定义
        List lists=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey) // 使用key查询
                .list();
        // 遍历,获取流程定义的id
        if(lists!=null && lists.size()>0){
            for (Object processDefinition : lists) {
                // 获取部署ID
                ProcessDefinition p=(ProcessDefinition)processDefinition;
                String deploymentId=p.getDeploymentId();
                repositoryService.deleteDeployment(deploymentId, true);

            }



        }
    }
}
