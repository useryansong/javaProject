package com.xchinfo.erp.bpm.event;

import com.xchinfo.erp.bpm.engine.Execution;
import org.springframework.context.ApplicationEvent;

/**
 * 结束流程事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class EndProcessEvent extends ApplicationEvent {

    /**
     * 传递流程执行对象
     *
     * @param execution
     */
    public EndProcessEvent(Execution execution) {
        super(execution);
    }
}
