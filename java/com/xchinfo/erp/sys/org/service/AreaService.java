package com.xchinfo.erp.sys.org.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.sys.org.mapper.AreaMapper;
import com.xchinfo.erp.sys.org.entity.AreaEO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/6/8 16:48
 * @update
 */
@Service
public class AreaService extends BaseServiceImpl<AreaMapper, AreaEO> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AreaEO area) throws BusinessException {
        // 检查编码是否已经被使用
        if (retBool(baseMapper.countByAreaCode(area.getAreaCode()))){
            throw new BusinessException("机构编码已被使用，请重新输入!");
        }

        return super.save(area);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable areaId) throws BusinessException {
        AreaEO area = this.getById(areaId);

        // 检查机构是否已被使用
        if (retBool(baseMapper.countByParent(area.getAreaId()))){
            throw new BusinessException(area.getAreaName() + "已经被使用不能删除");
        }

        return super.removeById(areaId);
    }

    public List<AreaEO> selectTreeList(Map<String, Object> params) {
        String name = (String) params.get("name");
        String parentCode = (String) params.get("parentCode");

        List<AreaEO> areaList =
                this.baseMapper.getList(new QueryWrapper<AreaEO>()
                .like(StringUtils.isNotBlank(name), "a.name", name)
                        .eq(StringUtils.isNotBlank(parentCode), "a.parent_code", parentCode));

        return TreeUtils.build(areaList, Long.valueOf(0));
    }

    @Override
    public AreaEO getById(Serializable id) {
        return this.baseMapper.getById(id);
    }
}
