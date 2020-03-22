package com.xchinfo.erp.bpm.engine;

import com.xchinfo.erp.bpm.entity.ProcessInstanceEO;
import com.xchinfo.erp.bpm.entity.ProcessNodeEO;
import com.xchinfo.erp.bpm.entity.WorkItemEO;

import java.io.Serializable;
import java.util.Map;

/**
 * 流程执行对象，携带流程信息
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class Execution implements Serializable {

    private ProcessInstanceEO processInstance;/** 实例 */

    private WorkItemEO workItem;/** 工作项ID */

    private ProcessNodeEO node;/** 执行的当前流程节点 */

    private String operator;/** 操作员 */

    private Map<String, Object> params;/** 参数 */

    public Execution(ProcessInstanceEO processInstance, String operator, Map<String, Object> params) {
        this.processInstance = processInstance;
        this.operator = operator;
        this.params = params;
    }

    public Execution(ProcessInstanceEO processInstance, WorkItemEO workItem, String operator, Map<String, Object> params) {
        this.processInstance = processInstance;
        this.workItem = workItem;
        this.operator = operator;
        this.params = params;
    }

    public ProcessInstanceEO getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstanceEO processInstance) {
        this.processInstance = processInstance;
    }

    public WorkItemEO getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItemEO workItem) {
        this.workItem = workItem;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public ProcessNodeEO getNode() {
        return node;
    }

    public void setNode(ProcessNodeEO node) {
        this.node = node;
    }
}
