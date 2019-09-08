package com.example.activitidemo;

/**
 * @author tlj
 * @date 2019/7/23
 */

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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
public class ActivitiStartInstanceTest {
    @org.junit.Test
    public void activitiStartEngine(){
        //1.得到ProcessEngine对象-把引擎对象作为全局变量
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RunService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.创建流程实例(关键步骤)即 启动流程实例
        //需要知道流程定义的Key：holiday（找key的方法  1：bpmn文件中的id，它对应的值就是key
        // 2：直接看数据库中流程定义表act_re_procdet的key值）
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Definitions_03lyd9r");
        //4.输出实例的相关信息
        System.out.println("流程部署ID="+processInstance.getDeploymentId());//null
        System.out.println("流程定义ID="+processInstance.getProcessDefinitionId());//holiday:1:4
        System.out.println("流程实例ID="+processInstance.getId());//2501
        System.out.println("流程活动ID="+processInstance.getActivityId());//获取当前具体执行的某一个节点的ID(null)

        String assignee= "zhangsan";
        List<Task> tasks = processEngine.getTaskService()
                .createTaskQuery()// 创建任务查询对象
                .taskAssignee(assignee) // 指定个人任务办理人
                .list();
        // 遍历节点查看任务
        for(Task task: tasks){
            System.out.println("taskID:"+task.getId());
            System.out.println("taskName:"+task.getName());
        }
    }
}
