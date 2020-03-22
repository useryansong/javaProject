package com.xchinfo.erp.bpm.engine;

import com.xchinfo.erp.bpm.entity.ProcessDefinitionEO;
import com.xchinfo.erp.bpm.entity.ProcessInstanceEO;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import com.xchinfo.erp.bpm.entity.WorkItemEO;
import com.xchinfo.erp.bpm.event.*;
import com.xchinfo.erp.bpm.helper.JsonHelper;
import com.xchinfo.erp.bpm.helper.ProcessConstant;
import com.xchinfo.erp.bpm.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.yecat.core.exception.BusinessException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 引擎对外暴露的服务类，负责发布工作流事件，由各个监听器负责处理事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class BpmEngine{

    @Autowired
    private ApplicationEventPublisher publisher;/** 事件发布对象 */

    @Autowired
    private ProcessDefinitionService processDefinitionService;/** 流程定义服务 */

    @Autowired
    private ProcessInstanceService processInstanceService;/** 流程实例服务 */

    @Autowired
    private ProcessNodeService processNodeService;/** 流程节点服务 */

    @Autowired
    private WorkItemService workItemService;/** 工作项服务 */

    public Long startInstanceById(Long processId,
                                  String instanceName,
                                  String creator,
                                  Map<String, Object> params) throws BusinessException {
        // 查找流程定义，检查流程是否存在
        ProcessDefinitionEO process = this.processDefinitionService.getById(processId);

        if (null == process || process.getStatus() == 0){
            throw new BusinessException("流程定义不存在或流程定义无效!");
        }

        // 创建流程实例
        ProcessInstanceEO instance = new ProcessInstanceEO();
        instance.setProcessId(process.getProcessId());
        instance.setProcessKey(process.getProcessKey());
        instance.setProcessInstanceName(instanceName);
        instance.setCreator(creator);
        instance.setCreateTime(new Date());
        instance.setStatus(ProcessConstant.PROCESS_INSTANCE_STATUS_NEW);
        instance.setIsSubProcess(0);

        // 设置流程参数
        instance.setProcessParams(JsonHelper.toJson(params));

        this.processInstanceService.save(instance);

        // 发布启动事件，启动首个任务
        Execution execution = new Execution(instance, creator, params);

        // 执行首个节点
        // 获取流程开始节点
        ProcessNodeEO startNode = this.processNodeService.getStartNode(execution.getProcessInstance().getProcessId());
        if (null == startNode){
            throw new BusinessException("获取开始节点失败!");
        }

        ProcessNodeEO nextNode = this.processNodeService.getNextNode(startNode, execution.getParams());
        execution.setNode(nextNode);

        // 判断节点的类型
        int nodeType = nextNode.getNodeType();

        switch (nodeType){
            case ProcessConstant.NODE_TYPE_TASK:
                // 发布任务开始事件
                publisher.publishEvent(new StartTaskEvent(execution));
                break;
            case ProcessConstant.NODE_TYPE_ROUTING:
                // 发布路由执行事件
                publisher.publishEvent(new ExecuteRoutingEvent(execution));
                break;
            case ProcessConstant.NODE_TYPE_SUBPROCESS:
                // 发布子流程启动事件
                publisher.publishEvent(new StartSubprocessEvent(execution));
                break;
        }

        // 返回流程实例ID
        return instance.getProcessInstanceId();
    }

    public Long startInstanceByKey(String processKey,
                                   String instanceName,
                                   String creator,
                                   Map<String, Object> params) throws BusinessException {
        ProcessDefinitionEO process = this.processDefinitionService.getByKey(processKey);

        return startInstanceById(process.getProcessId(), instanceName, creator, params);
    }

    public boolean executeTask(Long taskId,
                               String operator,
                               Map<String, Object> params) throws BusinessException {
        // 检查执行人是否为空
        if (StringUtils.isBlank(operator)){
            throw new BusinessException("执行人为空，无法执行!");
        }

        // 查询工作项
        WorkItemEO workItem = this.workItemService.getById(taskId);

        // 检查工作项的状态
        if (null == workItem || workItem.getStatus() != 0){
            throw new BusinessException("工作项不存在或不是新建状态，无法执行!");
        }

        // 查询流程实例
        ProcessInstanceEO processInstance = this.processInstanceService.getById(workItem.getProcessInstanceId());
        // 检查实例状态
        if (null == processInstance || processInstance.getStatus() != 1){
            throw new BusinessException("流程实例不存在或状态为非执行（已完成或已终止），无法执行!");
        }

        // 发布工作项执行事件
        Execution execution = new Execution(processInstance, workItem, operator, params);

        ProcessNodeEO node = this.processNodeService.getBykey(workItem.getNodeKey());
        execution.setNode(node);

        publisher.publishEvent(new ExecuteTaskEvent(execution));

        return true;
    }

    public Map<String, Object> getInstanceParams(Long instanceId) {
        ProcessInstanceEO instance = this.processInstanceService.getById(instanceId);
        String params = instance.getProcessParams();

        // 返回参数的Map对象
        return JsonHelper.fromJson(params, HashMap.class);
    }
}
