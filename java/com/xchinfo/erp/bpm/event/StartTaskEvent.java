package com.xchinfo.erp.bpm.event;

import com.xchinfo.erp.bpm.engine.Execution;
import org.springframework.context.ApplicationEvent;

/**
 * 启动任务节点事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class StartTaskEvent extends ApplicationEvent {
    /**
     * 传递流程执行对象
     *
     * @param execution
     */
    public StartTaskEvent(Execution execution) {
        super(execution);
    }
}
