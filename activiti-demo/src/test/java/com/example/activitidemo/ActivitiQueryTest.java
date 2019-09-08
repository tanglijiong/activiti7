package com.example.activitidemo;

/**
 * @author tlj
 * @date 2019/7/23
 */

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * 启动流程实例:
 *      前提是先已经完成流程定义的部署工作
 *
 *      背后影响的表：
 *      act_hi_actinst      已完成的活动信息
 *      act_hi_identitylink   参与者信息
 *      act_hi_procinst     流程实例
 *      act_hi_taskinst     任务实例
 *      act_ru_execution    执行表
 *      act_ru_identitylink   参与者信息
 *      act_ru_task   任务表
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiDemoApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class ActivitiQueryTest {
    @org.junit.Test
    public void activitiStartEngine(){
        //1.得到ProcessEngine对象-把引擎对象作为全局变量
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //  查 查询最新版本的流程定义
//        processEngine.getRepositoryService().activateProcessDefinitionById();
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .latestVersion()
                .list();
        // 查询的是 流程定义数据表act_re_procdef
        for (ProcessDefinition pd : list) {
            System.out.println("ID_：" + pd.getId());
            System.out.println("NAME_：" + pd.getName());
            System.out.println("KEY_：" + pd.getKey());
            System.out.println("VERSION_：" + pd.getVersion());
            System.out.println("===================");
        }


    }
}
