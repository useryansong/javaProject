package com.xchinfo.erp.bsc.service;


import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.mapper.MaterialCustomerMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.List;

/**
 * @author roman.c
 * @date 2019/4/11
 * @update
 */
@Service
public class MaterialCustomerService extends BaseServiceImpl<MaterialCustomerMapper, MaterialCustomerEO> {

    @Autowired
    private OrgService orgService;


    @Autowired
    private MaterialService materialService;

    public List<MaterialCustomerEO> listAll() {
        return this.baseMapper.selectAll();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialCustomerEO.class)
    public boolean save(MaterialCustomerEO entity) throws BusinessException {
        //校验机构权限
        MaterialEO materialEO = this.materialService.getById(entity.getMaterialId());

        if(!checkPer(materialEO.getOrgId())){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        int count = this.baseMapper.selectCustomerMaterilaCount(entity.getMaterialId());
        if(count == 0) {
            entity.setIsDefault(1);
        }

        int isCount = this.baseMapper.selectIsExistCount(entity);

        if(isCount > 0){
            throw new BusinessException("该客户已存在！");
        }

        return super.save(entity);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MaterialCustomerEO entity) throws BusinessException {

        //校验机构权限
        MaterialEO materialEO = this.materialService.getById(entity.getMaterialId());

        if(!checkPer(materialEO.getOrgId())){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        //更新后的数据不能一样
        int count = this.baseMapper.selectUpdateIsExistCount(entity);

        if(count > 0){
            throw new BusinessException("更新后的客户已存在！");
        }

        return super.updateById(entity);
    }


    /**
     * 设置默认
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long Id) throws BusinessException{

        //校验机构权限
        MaterialCustomerEO materialCustomerEO = this.baseMapper.selectById(Id);
        MaterialEO materialEO = this.materialService.getById(materialCustomerEO.getMaterialId());
        if(!checkPer(materialEO.getOrgId())){
            throw new BusinessException("物料的归属机构机构权限不存在该用户，请确认！");
        }

        //先把其他设置为非默认
        this.baseMapper.updateOtherStatusById(Id,materialCustomerEO.getMaterialId());

        return this.baseMapper.updateStatusById(Id);
    }

    public Boolean checkPer(Long orgId){

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public MaterialCustomerEO getByMaterialIdAndCustomerId(Long materialId, Long customerId) {
        return this.baseMapper.getByMaterialIdAndCustomerId(materialId, customerId);
    }
}
