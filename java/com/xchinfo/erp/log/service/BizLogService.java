package com.xchinfo.erp.log.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.log.entity.BizLogEO;
import com.xchinfo.erp.log.mapper.BizLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2018/11/20
 * @update
 */
@Service
public class BizLogService extends BaseServiceImpl<BizLogMapper, BizLogEO> {

    public List<BizLogEO> selectList(String optEntity, String entityId) {
        List<BizLogEO> list = this.list(
                new QueryWrapper<BizLogEO>()
                .eq(StringUtils.isNotBlank(optEntity), "opt_entity", optEntity)
                        .eq(StringUtils.isNotBlank(entityId), "entity_id", entityId)
                        .orderByDesc("created_time"));

        return list;
    }

}
