package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.bsc.entity.ShiftEO;
import com.xchinfo.erp.bsc.mapper.ShiftMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class ShiftService extends BaseServiceImpl<ShiftMapper, ShiftEO> {

    public List<ShiftEO> listAll() {
        return this.baseMapper.selectAll();
    }
}
