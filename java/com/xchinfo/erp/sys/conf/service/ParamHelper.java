package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.mapper.ParamsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author roman.li
 * @date 2018/12/13
 * @update
 */
@Service
public class ParamHelper {
    @Autowired
    private ParamsMapper paramsMapper;

    /**
     * 根据Key获取参数
     *
     * @param key
     * @return
     */
    public String getParamByKey(String key) {
        return this.paramsMapper.getParamByKey(key);
    }
}
