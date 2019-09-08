package com.example.activitidemo.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tlj
 * @date 2019/7/19
 */


@Service("activityService")
public class ActivityConsumerServiceImpl implements ActivityConsumerService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public boolean startActivityDemo() {
        /*System.out.println("method startActivityDemo begin....");

        System.out.println( "调用流程存储服务，查询部署数量："
                + repositoryService.createDeploymentQuery().count());



        Map<String,Object> map = new HashMap<String,Object>();
        map.put("apply","zhangsan");
        map.put("approve","lisi");

        //流程启动
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("leave",map);

        List<Task> tq=taskService.createTaskQuery().taskAssignee("zhangsan").list();
        System.out.println(tq.size());
        String assignee = "zhangsan";//当前任务办理人
        List<Task> tasks = taskService//与任务相关的Service
                .createTaskQuery()//创建一个任务查询对象
                .taskAssignee(assignee)
                .list();
        if(tasks !=null && tasks.size()>0){
            for(Task task:tasks){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());

                System.out.println("流程实例ID:"+task.getProcessInstanceId());
                System.out.println("#####################################");
            }
        }

        System.out.println("method startActivityDemo end....");*/
        return false;
    }
}