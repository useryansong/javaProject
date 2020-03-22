package com.xchinfo.erp.sys.dict.service;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.sys.dict.entity.DictEO;
import com.xchinfo.erp.sys.dict.mapper.DictMapper;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@Service
public class DictService extends BaseServiceImpl<DictMapper, DictEO>{

    public List<DictEO> selectByDictCode(String dictCode) {
        logger.info("========= DictServiceImpl.selectByDictCode(dictCode => "+dictCode+") =========");

        List<DictEO> dicts = baseMapper.selectByDictCode(dictCode);

        return dicts;
    }

    public List<DictEO> list() {
        QueryWrapper<DictEO> wrapper = new QueryWrapper<DictEO>()
                .eq("type", 1)
                .eq("status", 1);

        return super.list(wrapper);
    }

    public boolean updateStatus(DictEO entity, Long userId) {
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(DictEO dict) throws BusinessException {
        // 检查DictCode是否被使用
        int count = baseMapper.selectCountByDictCodeInDictGroup(dict.getTypeId());
        String code = (count+1)+"";
        if(code.length()<2){
            code = "0"+code;
        }
        dict.setDictCode(code);
        dict.setStatus(1);
        if(dict.getTypeId() == 0) {
            dict.setType(1);
            dict.setOrderNum(0);
        }
        if(dict.getTypeId() != 0) {
            dict.setType(2);
            List<DictEO> dictEntries = this.baseMapper.selectDictsByType(dict.getTypeId());
            dict.setOrderNum(dictEntries.size()+1);
        }

        return super.save(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = DictEO.class)
    public boolean removeByIds(Serializable[] ids) throws BusinessException {
        for (Serializable id : ids){
            // 删除字典项
            if (!retBool(this.baseMapper.deleteByTypeId((Long) id))) {
                throw new BusinessException("字典删除失败：删除字典项失败!");
            }
            // 删除字典
            if (!this.removeById(id)){
                throw new BusinessException("字典删除失败!");
            }
        }

        return true;
    }

    @Override
    public DictEO getById(Serializable id) {
        return this.baseMapper.getById(id);
    }
}
