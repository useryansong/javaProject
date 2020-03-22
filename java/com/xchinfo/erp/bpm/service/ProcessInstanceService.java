package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.engine.Execution;
import com.xchinfo.erp.bpm.entity.ProcessDefinitionEO;
import com.xchinfo.erp.bpm.entity.ProcessInstanceEO;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import com.xchinfo.erp.bpm.entity.WorkItemEO;
import com.xchinfo.erp.bpm.event.EndProcessEvent;
import com.xchinfo.erp.bpm.event.ExecuteTaskEvent;
import com.xchinfo.erp.bpm.event.StartProcessEvent;
import com.xchinfo.erp.bpm.event.StartSubprocessEvent;
import com.xchinfo.erp.bpm.helper.JsonHelper;
import com.xchinfo.erp.bpm.helper.ProcessConstant;
import com.xchinfo.erp.bpm.mapper.ProcessInstanceMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;
import java.util.HashMap;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class ProcessInstanceService extends BaseServiceImpl<ProcessInstanceMapper, ProcessInstanceEO> {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @Autowired
    private WorkItemService workItemService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Transactional(rollbackFor = Exception.class)
    public boolean backupFinishedInstance(Long instanceId) throws BusinessException{
        // 数据迁移至历史表
        if (!retBool(this.baseMapper.insertHisInstance(instanceId)))
            throw new BusinessException("迁移至历史数据表时出错!");

        if (!retBool(this.baseMapper.insertHistWorkItem(instanceId)))
            throw new BusinessException("迁移至历史数据表时出错!");

        if (!retBool(this.baseMapper.insertHisWorkItemActor(instanceId)))
            throw new BusinessException("迁移至历史数据表时出错!");

        // 删除表中执行完成的数据
        removeById(instanceId);

        if (!retBool(this.baseMapper.deleteWorkItemByInstance(instanceId)))
            throw new BusinessException("迁移至历史数据表时出错!");

        if (!retBool(this.baseMapper.deleteWorkItemActorByInstance(instanceId)))
            throw new BusinessException("迁移至历史数据表时出错!");

        return true;
    }

    /**
     * 监听流程结束事件
     *
     * @param event
     * @throws BusinessException
     */
    @EventListener(EndProcessEvent.class)
    public void endProcess(EndProcessEvent event) throws BusinessException {
        // 获取流程执行对象
        Execution execution = (Execution) event.getSource();

        // 获取流程实例
        ProcessInstanceEO instance = execution.getProcessInstance();
        instance.setStatus(ProcessConstant.PROCESS_INSTANCE_STATUS_FINISHED);
        instance.setFinishTime(new Date());

        this.save(instance);

        // 检查是否子流程，如果子流程则将父流程对应的工作项结束
        if (instance.getIsSubProcess() == 1){
            // 查询工作项
            WorkItemEO parentWorkItem = this.workItemService.getById(instance.getWorkItemId());

            // 检查工作项的状态
            if (null == parentWorkItem || parentWorkItem.getStatus() != 0){
                throw new BusinessException("工作项不存在或不是新建状态，无法执行!");
            }

            // 查询流程实例
            ProcessInstanceEO parentInstance = this.getById(instance.getParentInstanceId());
            // 检查实例状态
            if (null == parentInstance || parentInstance.getStatus() != 1){
                throw new BusinessException("流程实例不存在或状态为非执行（已完成或已终止），无法执行!");
            }

            // 发布工作项执行事件
            Execution parentExecution = new Execution(parentInstance, parentWorkItem, null,
                    JsonHelper.fromJson(parentInstance.getProcessParams(), HashMap.class));

            publisher.publishEvent(new ExecuteTaskEvent(parentExecution));
        }

        // 数据归档
        this.backupFinishedInstance(instance.getProcessInstanceId());
    }

    /**
     * 监听子流程启动事件
     *
     * @param event
     * @throws BusinessException
     */
    @EventListener(StartSubprocessEvent.class)
    public void startSubProcess(StartSubprocessEvent event) throws BusinessException {
        Execution execution = (Execution) event.getSource();

        // 1、创建工作项
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

        workItem.setStatus(ProcessConstant.WORK_ITEM_STATUS_DOING);

        // 保存工作项
        workItemService.save(workItem);

        // 2、启动子流程
        // 查找流程定义，检查流程是否存在
        ProcessDefinitionEO process = this.processDefinitionService.getByKey(execution.getNode().getSubProcessKey());

        if (null == process || process.getStatus() == 0){
            throw new BusinessException("流程定义不存在或流程定义无效!");
        }

        // 创建流程实例
        ProcessInstanceEO subInstance = new ProcessInstanceEO();
        subInstance.setProcessId(process.getProcessId());
        subInstance.setProcessKey(process.getProcessKey());
        subInstance.setProcessInstanceName(instance.getProcessInstanceName() + "-" + workItem.getWorkItemName());
        subInstance.setCreator(instance.getCreator());
        subInstance.setCreateTime(new Date());
        subInstance.setStatus(0);

        subInstance.setParentInstanceId(instance.getParentInstanceId());
        subInstance.setIsSubProcess(1);
        subInstance.setWorkItemId(workItem.getWorkItemId());

        // 设置流程参数
        instance.setProcessParams(instance.getProcessParams());

        this.save(instance);

        // 发布启动事件，启动子流程首个任务
        Execution subExecution = new Execution(instance, instance.getCreator(), JsonHelper.fromJson(instance.getProcessParams(), HashMap.class));

        publisher.publishEvent(new StartProcessEvent(subExecution));
    }
}
