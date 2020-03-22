package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.mapper.IdGeneratorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author roman.li
 * @date 2019/3/5
 * @update
 */
@Service
public class IdGenerator {

    @Autowired
    private IdGeneratorMapper idGeneratorMapper;

    public Long nextId(String seqName) {
        return this.idGeneratorMapper.nextId(seqName);
    }
}
