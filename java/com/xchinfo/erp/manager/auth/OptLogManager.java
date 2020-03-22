package com.xchinfo.erp.manager.auth;

import com.xchinfo.erp.log.entity.OptLogEO;
import com.xchinfo.erp.log.service.OptLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author roman.li
 * @date 2019/4/29
 * @update
 */
@Component
public class OptLogManager {

    @Autowired
    private OptLogService optLogService;

    /**
     * 保存数据
     *
     * @param optLog
     * @return
     */
    public boolean save(OptLogEO optLog){
        return this.optLogService.save(optLog);
    }
}
