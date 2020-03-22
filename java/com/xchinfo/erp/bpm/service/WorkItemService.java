package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.engine.Execution;
import com.xchinfo.erp.bpm.entity.ProcessInstanceEO;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import com.xchinfo.erp.bpm.entity.WorkItemEO;
import com.xchinfo.erp.bpm.event.*;
import com.xchinfo.erp.bpm.helper.JsonHelper;
import com.xchinfo.erp.bpm.helper.ProcessConstant;
import com.xchinfo.erp.bpm.mapper.WorkItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.xchinfo.erp.bpm.engine.ActorAssignmentService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.SpringContextUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

/**
 * 工作项服务类，监听工作项相关事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class WorkItemService extends BaseServiceImpl<WorkItemMapper, WorkItemEO> {

    @Autowired
    private ProcessNodeService processNodeService;

    @Autowired
    private ActorAssignmentService actorAssginmentService;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 监听任务节点启动事件，处理由监听器发起的事件
     *
     * @param event
     */
    @EventListener(StartTaskEvent.class)
    public void startTask(StartTaskEvent event){
        // 获取流程执行对象
        Execution execution = (Execution) event.getSource();

        // 启动任务节点
        startTask(execution);
    }

    /**
     * 启动任务节点
     *
     * @param execution
     */
    private void startTask(Execution execution){
        // 获取里哭成实例
        ProcessInstanceEO instance = execution.getProcessInstance();

        // 获取节点对象
        ProcessNodeEO currentNode = execution.getNode();

        // 创建当前节点对应的任务
        WorkItemEO workItem = new WorkItemEO();

        workItem.setWorkItemName(instance.getProcessInstanceName() + "-" +currentNode.getNodeName());
        workItem.setWorkItemType(ProcessConstant.WORK_ITEM_TYPE_NORMAL);
        workItem.setPerformType(ProcessConstant.WORK_ITEM_PERFORM_TYPE_NORMAL);

        workItem.setProcessInstanceId(instance.getProcessInstanceId());
        workItem.setProcessInstanceName(instance.getProcessInstanceName());

        workItem.setNodeKey(currentNode.getNodeKey());

        workItem.setCreateTime(new Date());
        if (null != currentNode.getExpireTime() && currentNode.getExpireTime() > 0){
            // 期望完成时间 = 当前时间 + 定义的期望完成时间（小时）
            workItem.setExpireTime(DateUtils.addDateHours(workItem.getCreateTime(), currentNode.getExpireTime()));
        }
        if (null != currentNode.getReminderTime() && currentNode.getReminderTime() > 0){
            // 提醒时间 = 完成时间 - 定义的提醒时间（小时）
            workItem.setRemindTime(DateUtils.addDateHours(workItem.getExpireTime(), -currentNode.getReminderTime()));
        }
        if (StringUtils.isNotBlank(currentNode.getActionUrl()))
            workItem.setActionUrl(currentNode.getActionUrl());

        // 查找流程的参与者
        int actorType = currentNode.getActorType();
        List<String> actors = new ArrayList<>();

        // 获取参与者查询接口
        if (null == actorAssginmentService){
            throw new BusinessException("未发现参与者查询接口，请提供参与者查询接口!");
        }

        switch (actorType){
            case ProcessConstant.ACTOR_TYPE_CREATOR:
                // 流程启动着
                actors.add(instance.getCreator());

                workItem.setOperator(instance.getCreator());
                break;
            case ProcessConstant.ACTOR_TYPE_GROUP:
                // 通过参与者查询接口查询参与者
                actors = actorAssginmentService.assignByGroup(currentNode.getActor());
                break;
            case ProcessConstant.ACTOR_TYPE_ROLE:
                // 通过参与者查询接口查询参与者
                actors = actorAssginmentService.assginByRole(currentNode.getActor());
                break;
            case ProcessConstant.ACTOR_TYPE_USER:
                String actor = actorAssginmentService.assginByUser(currentNode.getActor());

                actors.add(actor);
                workItem.setOperator(actor);
                break;
        }

        workItem.setActors(actors);
        workItem.setStatus(ProcessConstant.WORK_ITEM_STATUS_NEW);

        // 保存工作项
        save(workItem);
    }

    /**
     * 监听任务完成事件，处理由监听器发起的事件
     *
     * @param event
     */
    @EventListener(ExecuteTaskEvent.class)
    public void executeTask(ExecuteTaskEvent event){
        Execution execution = (Execution) event.getSource();

        executeTask(execution);
    }

    /**
     * 完成当前任务节点
     *
     * @param execution
     */
    private void executeTask(Execution execution){
        WorkItemEO workItem = execution.getWorkItem();

        // 结束当前任务
        workItem.setStatus(ProcessConstant.WORK_ITEM_STATUS_FINISHED);
        workItem.setOperator(execution.getOperator());
        workItem.setFinishTime(new Date());

        updateById(workItem);

        // 执行下个节点
        executeNextNode(execution);
    }

    /**
     * 监听路由节点执行事件，处理由监听器发起的事件
     *
     * @param event
     */
    @EventListener(ExecuteRoutingEvent.class)
    public void executeRouting(ExecuteRoutingEvent event){
        // 获取流程执行对象
        Execution execution = (Execution) event.getSource();

        executeRouting(execution);
    }

    /**
     * 处理路由节点
     *
     * @param execution
     */
    private void executeRouting(Execution execution){
        // 获取当前节点
        ProcessNodeEO currentNode = execution.getNode();

        // 获取流程实例
        ProcessInstanceEO instance = execution.getProcessInstance();

        // 执行下个节点
        executeNextNode(execution);
    }

    /**
     * 处理下一节点
     *
     * @param execution
     * @throws BusinessException
     */
    private void executeNextNode(Execution execution) throws BusinessException{
        // 获取参数
        Map<String, Object> params = JsonHelper.fromJson(execution.getProcessInstance().getProcessParams(), HashMap.class);

        ProcessNodeEO nextNode = processNodeService.getNextNode(execution.getNode(), params);
        if (null == nextNode)
            throw new BusinessException("未找到下一节点!");

        // 设置执行节点为下一节点
        execution.setNode(nextNode);

        // 执行下一节点
        int nodeType = nextNode.getNodeType();
        switch (nodeType){
            case ProcessConstant.NODE_TYPE_START:
                // 路由节点后面不能是开始节点
                throw new BusinessException("节点类型有误，请重新配置!");
            case ProcessConstant.NODE_TYPE_END:
                // 发布流程结束事件
                publisher.publishEvent(new EndProcessEvent(execution));
                break;
            case ProcessConstant.NODE_TYPE_TASK:
                // 启动任务节点
                startTask(execution);
                break;
            case ProcessConstant.NODE_TYPE_ROUTING:
                // 处理路由节点
                executeRouting(execution);
                break;
            case ProcessConstant.NODE_TYPE_SUBPROCESS:
                // 启动子流程
                publisher.publishEvent(new StartSubprocessEvent(execution));
                break;
            default:
                throw new BusinessException("节点类型有误，请重新配置!");
        }
    }

    @Override
    public boolean save(WorkItemEO entity) throws BusinessException {
        // 保存工作项
        if (!retBool(this.baseMapper.insert(entity))){
            throw new BusinessException("工作项保存失败!");
        }

        for (String actor : entity.getActors()){
            if (!retBool(this.baseMapper.insertActor(entity.getWorkItemId(), actor))){
                throw new BusinessException("工作项保存失败!");
            }
        }

        return true;
    }
}
