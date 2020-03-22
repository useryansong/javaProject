package com.xchinfo.erp.bpm.service;

import com.xchinfo.erp.bpm.entity.ProcessTransitionEO;
import com.xchinfo.erp.bpm.mapper.ProcessTransitionMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
@Service
public class ProcessTransitionService extends BaseServiceImpl<ProcessTransitionMapper, ProcessTransitionEO> {

    public List<ProcessTransitionEO> listNextNodes(Long fromNode) {
        return this.baseMapper.selectByFromNode(fromNode);
    }
}
