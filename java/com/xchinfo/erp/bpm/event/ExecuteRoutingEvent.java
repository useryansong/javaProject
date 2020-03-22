package com.xchinfo.erp.bpm.event;

import com.xchinfo.erp.bpm.engine.Execution;
import org.springframework.context.ApplicationEvent;

/**
 * 执行路由节点事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class ExecuteRoutingEvent extends ApplicationEvent {
    /**
     * 传递流程执行对象
     *
     * @param execution
     */
    public ExecuteRoutingEvent(Execution execution) {
        super(execution);
    }
}
