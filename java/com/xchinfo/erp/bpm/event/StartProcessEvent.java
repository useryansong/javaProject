package com.xchinfo.erp.bpm.event;

import com.xchinfo.erp.bpm.engine.Execution;
import org.springframework.context.ApplicationEvent;

/**
 * 启动流程实例事件
 *
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public class StartProcessEvent extends ApplicationEvent {

    /**
     * 传递流程执行对象
     *
     * @param execution
     */
    public StartProcessEvent(Execution execution) {
        super(execution);
    }
}
