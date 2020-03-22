package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.engine.Expression;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import com.xchinfo.erp.bpm.entity.ProcessTransitionEO;
import com.xchinfo.erp.bpm.helper.ProcessConstant;
import com.xchinfo.erp.bpm.mapper.ProcessNodeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.SpringContextUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class ProcessNodeService extends BaseServiceImpl<ProcessNodeMapper, ProcessNodeEO> {

    @Autowired
    private ProcessTransitionService processTransitionService;

    public ProcessNodeEO getStartNode(Long processId) throws BusinessException {
        return this.baseMapper.selectStartNode(processId);
    }

    public ProcessNodeEO getNextNode(ProcessNodeEO currentNode, Map<String, Object> params) throws BusinessException {
        // 获取当前节点的后续节点
        List<ProcessTransitionEO> transitions = this.processTransitionService.listNextNodes(currentNode.getNodeId());
        if (null == transitions || transitions.size() == 0)
            throw new BusinessException("节点未定义后续节点!");

        // 下个流程节点
        Long nextNodeId = null;

        // 根据当前节点类型获取下节点
        int nodeType = currentNode.getNodeType();
        switch (nodeType){
            case ProcessConstant.NODE_TYPE_START:
                if (transitions.size() > 1){
                    throw new BusinessException("开始节点只能关联一个节点，请重新配置!");
                }

                nextNodeId = transitions.get(0).getToNode();

                break;
            case ProcessConstant.NODE_TYPE_END:
                throw new BusinessException("结束节点后不能关联节点，请重新配置!");
            case ProcessConstant.NODE_TYPE_TASK:
                if (transitions.size() > 1){
                    throw new BusinessException("任务节点只能关联一个节点，请重新配置!");
                }

                nextNodeId = transitions.get(0).getToNode();

                break;
            case ProcessConstant.NODE_TYPE_ROUTING:
                // 根据表达式选择节点
                for (ProcessTransitionEO transition : transitions){
                    // 判断节点是否有表达式
                    if (StringUtils.isNotBlank(transition.getExpr())){
                        // 执行节点表达式
                        Expression expression = SpringContextUtils.getBean(Expression.class);
                        if (null == expression)
                            throw new BusinessException("未配置表达式解析器!");

                        if (expression.eval(Boolean.class, transition.getExpr(), params)){
                            // 找到后续节点则终止查找
                            nextNodeId = transition.getToNode();
                            break;
                        }
                    } else {
                        // 无表达式默认节点
                        nextNodeId = transition.getToNode();
                    }
                }

                break;
            case ProcessConstant.NODE_TYPE_SUBPROCESS:
                if (transitions.size() > 1){
                    throw new BusinessException("子流程节点只能关联一个节点，请重新配置!");
                }

                nextNodeId = transitions.get(0).getToNode();

                break;
            default:
                throw new BusinessException("节点类型有误，请重新配置!");
        }

        if (null == nextNodeId)
            throw new BusinessException("未找到下一节点!");

        ProcessNodeEO nextNode = this.getById(nextNodeId);
        if (null == nextNode)
            throw new BusinessException("未找到下一节点!");

        return nextNode;
    }

    public ProcessNodeEO getBykey(String nodeKey) throws BusinessException {
        return this.baseMapper.selectbyKey(nodeKey);
    }
}
