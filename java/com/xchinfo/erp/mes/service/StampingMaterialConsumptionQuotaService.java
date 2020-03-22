package com.xchinfo.erp.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.mes.mapper.StampingMaterialConsumptionQuotaMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.xchinfo.erp.utils.CommonUtil.criteriaToMap;

@Service
public class StampingMaterialConsumptionQuotaService
        extends BaseServiceImpl<StampingMaterialConsumptionQuotaMapper, StampingMaterialConsumptionQuotaEO>{
    @Autowired
    private StampingMaterialConsumptionQuotaMapper stampingMaterialConsumptionQuotaMapper;


    public List<Map> hasProject(Long userId){
        return stampingMaterialConsumptionQuotaMapper.hasProject(userId);
    }


    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }

    public StampingMaterialConsumptionQuotaEO getByElementNoAndOrg(String elementNo, Long orgId) {
        return this.baseMapper.getByElementNoAndOrg(elementNo, orgId);
    }

    public List<StampingMaterialConsumptionQuotaEO> selectInandoutputsReport(Criteria criteria) throws BusinessException {

        List<StampingMaterialConsumptionQuotaEO> pageList = new ArrayList<>();
        //int current = criteria.getCurrentPage();
        // int size = criteria.getSize();
        Map mapCondition = criteriaToMap(criteria);
        // Calendar calendar = Calendar.getInstance();
        pageList = this.baseMapper.selectInandoutputsReport(mapCondition);

        return pageList;
    }

    /*public List<StampingMaterialConsumptionQuotaEO> biwReport(Criteria criteria) throws BusinessException {

        Map mapCondition = criteriaToMap(criteria);
        List<StampingMaterialConsumptionQuotaEO> pageList = this.baseMapper.biwReport(mapCondition);

        return pageList;
    }*/
    public IPage<StampingMaterialConsumptionQuotaEO> biwReport(Criteria criteria) throws BusinessException {
        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<StampingMaterialConsumptionQuotaEO> wrapper = new QueryWrapper<StampingMaterialConsumptionQuotaEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<StampingMaterialConsumptionQuotaEO> totalList = this.baseMapper.biwReport(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StampingMaterialConsumptionQuotaEO> list = this.baseMapper.biwReport(param);

        IPage<StampingMaterialConsumptionQuotaEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }

    public StampingMaterialConsumptionQuotaEO getByMaterialIdAndOriginalMaterialId(Long materialId, Long originalMaterialId) {
        return this.baseMapper.getByMaterialIdAndOriginalMaterialId(materialId, originalMaterialId);
    }
}
