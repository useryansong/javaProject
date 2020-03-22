package com.xchinfo.erp.bsc.service;


import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.bsc.mapper.MaterialSupplierMapper;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class MaterialSupplierService extends BaseServiceImpl<MaterialSupplierMapper, MaterialSupplierEO>{

    @Autowired
    private OrgService orgService;

    @Autowired
    @Lazy
    private MaterialService materialService;

    public List<MaterialSupplierEO> listAll() {
        return this.baseMapper.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialSupplierEO.class)
    public boolean save(MaterialSupplierEO entity, Long userId) throws BusinessException {

        //校验机构权限
        MaterialEO materialEO = this.materialService.getById(entity.getMaterialId());

        if(!checkPer(materialEO.getOrgId(),userId)){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        //若是第一次创建，则设置默认为1
        int count = this.baseMapper.selectSupplierMaterilaCount(entity.getMaterialId());
        if(count == 0) {
            entity.setIsDefault(1);
        }

        int isCount = this.baseMapper.selectIsExistCount(entity);

        if(isCount > 0){
            throw new BusinessException("该供应商已存在！");
        }

        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MaterialSupplierEO entity, Long userId) throws BusinessException {

        //校验机构权限
        MaterialEO materialEO = this.materialService.getById(entity.getMaterialId());

        if(!checkPer(materialEO.getOrgId(),userId)){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        //更新后的数据不能一样
        int count = this.baseMapper.selectUpdateIsExistCount(entity);

        if(count > 0){
            throw new BusinessException("更新后的供应商已存在！");
        }

        return super.updateById(entity);
    }


    /**
     * 设置默认
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long Id,Long userId){
        //校验机构权限
        MaterialSupplierEO materialSupplierEO = this.baseMapper.selectById(Id);
        MaterialEO materialEO = this.materialService.getById(materialSupplierEO.getMaterialId());
        if(!checkPer(materialEO.getOrgId(),userId)){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        //设置其他非默认
        this.baseMapper.updateOtherStatusById(Id,materialSupplierEO.getMaterialId());

        return this.baseMapper.updateStatusById(Id);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public MaterialSupplierEO getByMaterialIdAndSupplierId(Long materialId, Long supplierId) {
        return this.baseMapper.getByMaterialIdAndSupplierId(materialId, supplierId);
    }
}


