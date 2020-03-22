package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.mes.entity.ProductTaskEO;
import com.xchinfo.erp.mes.mapper.ProductTaskMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Map;

@Service
public class ProductTaskService extends BaseServiceImpl<ProductTaskMapper, ProductTaskEO> {

    public boolean publish(Long[] ids){
        for(Long productTaskId:ids){
            this.baseMapper.publish(productTaskId);
        }
        return true;
    }

    public boolean canclePublish(Long[] ids){
        for(Long productTaskId:ids){
            this.baseMapper.canclePublish(productTaskId);
        }
        return true;
    }

    public boolean comfirm(Map map){
        long productTaskId = Long.parseLong(map.get("productTaskId").toString());//任务id
        long serialDistributeId = Long.parseLong(map.get("serialDistributeId").toString());//计划id
        ProductTaskEO entity = this.getById(productTaskId);
        if (entity.getPlanStatus()==0){
            //直接确认
            super.baseMapper.comfirn(productTaskId);
        }
        if (entity.getPlanStatus()==2){
            //计划变更确认
            super.baseMapper.planChangeComfirm(serialDistributeId,productTaskId);
        }
        return true;
    }

    public boolean close(Long[] ids){
        for(Long productTaskId:ids){
            this.baseMapper.close(productTaskId);
        }
        return true;
    }

    public boolean updateNumber(Map map){
        long productTaskId = Long.parseLong(map.get("productTaskId").toString());
        double productionCount = Double.parseDouble(map.get("productionCount").toString());
        this.baseMapper.updateNumber(productionCount,productTaskId);
        return true;
    }
}
