package com.xchinfo.erp.bpm.event;

import com.xchinfo.erp.bpm.engine.Execution;
import org.springframework.context.ApplicationEvent;

/**
 * 执行任务节点事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class ExecuteTaskEvent extends ApplicationEvent {
    /**
     * 流程执行对象
     *
     * @param execution
     */
    public ExecuteTaskEvent(Execution execution) {
        super(execution);
    }
}
