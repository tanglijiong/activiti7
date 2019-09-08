package com.example.activitidemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tlj
 * @date 2019/9/4
 */


/**
 * <p>Activiti控制器</p>
 * @author FRH
 * @time 2018年12月10日上午9:30:18
 * @version 1.0
 */
@RestController
@Slf4j
//@RequestMapping("/demo")
public class DemoController {


    /** 流程定义和部署相关的存储服务 */
    @Autowired
    private RepositoryService repositoryService;

    /** 流程运行时相关的服务 */
    @Autowired
    private RuntimeService runtimeService;

    /** 节点任务相关操作接口 */
    @Autowired
    private TaskService taskService;

    /** 流程图生成器 */
    @Autowired
    private ProcessDiagramGenerator processDiagramGenerator;

    /** 历史记录相关服务接口 */
    @Autowired
    private HistoryService historyService;



    /**
     * <p>跳转到测试主页面</p>
     * @return String 测试主页面
     * @author FRH
     * @time 2018年12月10日上午11:12:28
     * @version 1.0
     */
    @RequestMapping(value="/demo/toIndex.html")
    public String toTestPage() {
        return "/index";
    }



    /**
     * <p>跳转到上级审核页面</p>
     * @return String 上级审核页面
     * @author FRH
     * @time 2018年12月5日下午2:31:42
     * @version 1.0
     */
    @RequestMapping(value="/demo/toLeave")
    public String employeeLeave() {
        return "/employeeLeave";
    }



    /**
     * <p>启动请假流程（流程key即xml中定义的ID为leaveProcess）</p>
     * @return String 启动的流程ID
     * @author FRH
     * @time 2018年12月10日上午11:12:50
     * @version 1.0
     */
    @PostMapping(value="/demo/start")
    @ResponseBody
    public String start() {
        /*
         *  xml中定义的ID
         */
//        String instanceKey = "leaveProcess";
        String instanceKey = "myProcess_4";
        log.info("开启请假流程...");

        /*
         *  设置流程参数，开启流程
         */
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("jobNumber","A1001");
        map.put("busData","bus data");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceKey, map);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动

        log.info("启动流程实例成功:{}", instance);
        log.info("流程实例ID:{}", instance.getId());
        log.info("流程定义ID:{}", instance.getProcessDefinitionId());


        /*
         * 验证是否启动成功
         */
        //通过查询正在运行的流程实例来判断
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        //根据流程实例ID来查询
        List<ProcessInstance> runningList = processInstanceQuery.processInstanceId(instance.getProcessInstanceId()).list();
        log.info("根据流程ID查询条数:{}", runningList.size());


        /*
         *  返回流程ID
         */
        return instance.getId();
    }



    /**
     * <p>查看当前流程图</p>
     * @param instanceId 流程实例
     * @param response void 响应
     * @author FRH
     * @time 2018年12月10日上午11:14:12
     * @version 1.0
     */
    @ResponseBody
    @RequestMapping(value="/demo/showImg")
    public void showImg(String instanceId, HttpServletResponse response) {
        /*
         * 参数校验
         */
        log.info("查看完整流程图！流程实例ID:{}", instanceId);
        if(StringUtils.isBlank(instanceId)) return;


        /*
         *  获取流程实例
         */
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if(processInstance == null) {
            log.error("流程实例ID:{}没查询到流程实例！", instanceId);
            return;
        }

        // 根据流程对象获取流程对象模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());


        /*
         *  查看已执行的节点集合
         *  获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
         */
        // 构造历史流程查询
        HistoricActivityInstanceQuery historyInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId);
        // 查询历史节点
        List<HistoricActivityInstance> historicActivityInstanceList = historyInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();
        if(historicActivityInstanceList == null || historicActivityInstanceList.size() == 0) {
            log.info("流程实例ID:{}没有历史节点信息！", instanceId);
            outputImg(response, bpmnModel, null, null);
            return;
        }
        // 已执行的节点ID集合(将historicActivityInstanceList中元素的activityId字段取出封装到executedActivityIdList)
        List<String> executedActivityIdList = historicActivityInstanceList.stream().map(item -> item.getActivityId()).collect(Collectors.toList());

        /*
         *  获取流程走过的线
         */
        // 获取流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        List<String> flowIds = ActivitiUtils.getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);


        /*
         * 输出图像，并设置高亮
         */
        outputImg(response, bpmnModel, flowIds, executedActivityIdList);
    }



    /**
     * <p>员工提交申请</p>
     * @param request 请求
     * @return String 申请受理结果
     * @author FRH
     * @time 2018年12月10日上午11:15:09
     * @version 1.0
     */
    @RequestMapping(value="/demo/employeeApply")
    @ResponseBody
    public String employeeApply(HttpServletRequest request){
        /*
         * 获取请求参数
         */
        String taskId = request.getParameter("taskId"); // 任务ID
        String jobNumber = request.getParameter("jobNumber"); // 工号
        String leaveDays = request.getParameter("leaveDays"); // 请假天数
        String leaveReason = request.getParameter("leaveReason"); // 请假原因
        String reason1 = request.getParameter("reason1"); // 请假原因


        /*
         *  查询任务
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null) {
            log.info("任务ID:{}查询到任务为空！", taskId);
            return "fail";
        }


        /*
         * 参数传递并提交申请
         */
        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("days", leaveDays);
//        map.put("date", new Date());
        map.put("reason1", leaveReason);
//        map.put("jobNumber", jobNumber);
        taskService.complete(task.getId(), map);
        log.info("执行【员工申请】环节，流程推动到【上级审核】环节");

        /*
         * 返回成功
         */
        return "success";
    }


    /**
     * <p>跳转到上级审核页面</p>
     * @return String 页面
     * @author FRH
     * @time 2018年12月5日下午2:31:42
     * @version 1.0
     */
    @RequestMapping(value="/demo/viewTask")
    public String toHigherAudit(String taskId, HttpServletRequest request) {
        /*
         * 获取参数
         */
        log.info("跳转到任务详情页面，任务ID:{}", taskId);
        if(StringUtils.isBlank(taskId)) return "/higherAudit";


        /*
         *  查看任务详细信息
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null) {
            log.info("任务ID:{}不存在！", taskId);
            return "/higherAudit";
        }


        /*
         * 完成任务
         */
        Map<String, Object> paramMap = taskService.getVariables(taskId);
        request.setAttribute("task", task);
        request.setAttribute("paramMap", paramMap);
        return "higherAudit";
    }



    /**
     * <p>跳转到部门经理审核页面</p>
     * @param taskId 任务ID
     * @param request 请求
     * @return String 响应页面
     * @author FRH
     * @time 2018年12月6日上午9:54:34
     * @version 1.0
     */
    @RequestMapping(value="/demo/viewTaskManager")
    public String viewTaskManager(String taskId, HttpServletRequest request) {
        /*
         * 获取参数
         */
        log.info("跳转到任务详情页面，任务ID:{}", taskId);
        if(StringUtils.isBlank(taskId)) return "/manageAudit";


        /*
         *  查看任务详细信息
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null) {
            log.info("任务ID:{}不存在！", taskId);
            return "/manageAudit";
        }


        /*
         * 完成任务
         */
        Map<String, Object> paramMap = taskService.getVariables(taskId);
        request.setAttribute("task", task);
        request.setAttribute("paramMap", paramMap);
        return "manageAudit";
    }



    /**
     * <p>上级审核</p>
     * @param request 请求
     * @return String 受理结果
     * @author FRH
     * @time 2018年12月10日上午11:19:44
     * @version 1.0
     */
    @ResponseBody
    @RequestMapping(value="/demo/higherLevelAudit")
    public String higherLevelAudit(HttpServletRequest request) {
        /*
         * 获取请求参数
         */
        String taskId = request.getParameter("taskId");
        String higherLevelOpinion = request.getParameter("sug");
        String auditStr = request.getParameter("audit");
        log.info("上级审核任务ID:{}", taskId);
        if(StringUtils.isBlank(taskId)) return "fail";


        /*
         * 查找任务
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null) {
            log.info("审核任务ID:{}查询到任务为空！", taskId);
            return "fail";
        }


        /*
         * 设置局部变量参数，完成任务
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("audit", "1".equals(auditStr) ? false : true);
        map.put("higherLevelOpinion", higherLevelOpinion);
        taskService.complete(taskId, map);
        return "success";
    }



    /**
     * <p>部门经理审核</p>
     * @param request 请求
     * @return String 受理结果
     * @author FRH
     * @time 2018年12月10日上午11:20:44
     * @version 1.0
     */
    @ResponseBody
    @RequestMapping(value="/demo/divisionManagerAudit")
    public String divisionManagerAudit(HttpServletRequest request) {
        /*
         * 获取请求参数
         */
        String taskId = request.getParameter("taskId");
        String opinion = request.getParameter("sug");
        String auditStr = request.getParameter("audit");
        log.info("上级审核任务ID:{}", taskId);
        if(StringUtils.isBlank(taskId)) return "fail";


        /*
         * 查找任务
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null) {
            log.info("审核任务ID:{}查询到任务为空！", taskId);
            return "fail";
        }


        /*
         * 设置局部变量参数，完成任务
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("audit", "1".equals(auditStr) ? false : true);
        map.put("managerOpinion", opinion);
        taskService.complete(taskId, map);
        return "success";
    }


    /**
     * <p>查看任务</p>
     * @param request 请求
     * @return String  任务展示页面
     * @author FRH
     * @time 2018年12月10日上午11:21:33
     * @version 1.0
     */
    @RequestMapping(value="/demo/toShowTask")
    public String toShowTask(HttpServletRequest request) {
        /*
         * 获取请求参数
         */
        List<Task> taskList = taskService.createTaskQuery().list();
        if(taskList == null || taskList.size() == 0) {
            log.info("查询任务列表为空！");
            return "/task";
        }


        /*
         * 查询所有任务，并封装
         */
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for(Task task : taskList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("taskId", task.getId());
            map.put("name", task.getName());
            map.put("createTime", task.getCreateTime().toString());
            map.put("assignee", task.getAssignee());
            map.put("instanceId", task.getProcessInstanceId());
            map.put("executionId", task.getExecutionId());
            map.put("definitionId", task.getProcessDefinitionId());
            resultList.add(map);
        }


        /*
         * 返回结果想·
         */
        log.info("返回集合:{}", resultList.toString());
        request.setAttribute("resultList", resultList);
        return "/task";
    }



    /**
     * <p>输出图像</p>
     * @param response 响应实体
     * @param bpmnModel 图像对象
     * @param flowIds 已执行的线集合
     * @param executedActivityIdList void 已执行的节点ID集合
     * @author FRH
     * @time 2018年12月10日上午11:23:01
     * @version 1.0
     */
    private void outputImg(HttpServletResponse response, BpmnModel bpmnModel, List<String> flowIds, List<String> executedActivityIdList) {
        InputStream imageStream = null;
        try {
            imageStream = processDiagramGenerator.generateDiagram(bpmnModel, executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", true, "png");
            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.getOutputStream().flush();
        }catch(Exception e) {
            log.error("流程图输出异常！", e);
        } finally { // 流关闭
            try {
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * <p>判断流程是否完成</p>
     * @param processInstanceId 流程实例ID
     * @return boolean 已完成-true，未完成-false
     * @author FRH
     * @time 2018年12月10日上午11:23:26
     * @version 1.0
     */
    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId).count() > 0;
    }

}
