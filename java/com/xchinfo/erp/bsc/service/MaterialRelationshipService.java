package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.MaterialRelationshipEO;
import com.xchinfo.erp.bsc.mapper.MaterialRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author zhongy
 * @date 2019/8/9
 */
@Service
public class MaterialRelationshipService extends BaseServiceImpl<MaterialRelationshipMapper, MaterialRelationshipEO> {

    @Autowired
    private MaterialRelationshipMapper materialRelationshipMapper;

    public MaterialRelationshipEO get(Long parentMaterialId, Long childMaterialId) {
        return this.baseMapper.get(parentMaterialId, childMaterialId);
    }
}
