package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.WarehouseAreaEO;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.mapper.WarehouseMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/11
 * @update
 */
@Service
public class WarehouseService extends BaseServiceImpl<WarehouseMapper, WarehouseEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;
    @Autowired
    private OrgService orgService;
    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(WarehouseEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        Integer count = this.baseMapper.selectCountByName(entity);

        if(count > 0){
            throw new BusinessException("仓库名称在此机构下已存在，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_warehouse", entity);
        AssertUtils.isBlank(code);

        entity.setWarehouseCode(code);

        //仓库编码重复校验
        if(this.baseMapper.checkErpCode(entity.getOrgId(),entity.getErpCode())>0){
            throw new BusinessException("ERP编码已存在!");
        }
        //条码重复校验
        if(this.baseMapper.checkBarCode(entity.getOrgId(),entity.getBarCode())>0){
            throw new BusinessException("条码已存在!");
        }
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WarehouseEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }


        Integer count = this.baseMapper.selectCountByName(entity);

        if(count > 0){
            throw new BusinessException("仓库名称在此机构下已存在，请确认！");
        }
        //ERP编码重复校验
        WarehouseEO old = this.getById(entity.getWarehouseId());
        if(old.getOrgId().longValue() !=entity.getOrgId().longValue()
           ||!old.getErpCode().equals(entity.getErpCode())){
            if(this.baseMapper.checkErpCode(entity.getOrgId(),entity.getErpCode())>0){
                throw new BusinessException("ERP编码已存在!");
            }
        }
        //条码重复校验
        if(old.getOrgId().longValue() !=entity.getOrgId().longValue()
                ||!old.getBarCode().equals(entity.getBarCode())){
            if(this.baseMapper.checkBarCode(entity.getOrgId(),entity.getBarCode())>0){
                throw new BusinessException("条码已存在!");
            }
        }


        boolean res = super.updateById(entity);
        if(res){
            if(!old.getBarCode().equals(entity.getBarCode())){
                //条码产生变化。同步更改该仓库下面的库区、库位的条码
                this.baseMapper.updateBarCodeForWarehouseArea(entity.getWarehouseId(),entity.getBarCode()+"-",old.getBarCode()+"-");
                this.baseMapper.updateBarCodeForWarehouseLocation(entity.getWarehouseId(),entity.getBarCode()+"-",old.getBarCode()+"-");
            }
        }
        return  res;

    }

    @Override
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = WarehouseEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        // 删除关系
        for (Serializable id : idList) {
            WarehouseEO warehouseEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(warehouseEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }


            super.removeById(id);
        }

        return true;
    }

    public List<WarehouseEO> listALL(UserEO user) { return this.baseMapper.selectAll(user.getUserId(),user.getOrgId()); }

    public boolean updateStatusById(Long id,int status) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        WarehouseEO warehouseEO = this.baseMapper.selectById(id);
        //校验机构权限
        if(!checkPer(warehouseEO.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }
        return this.baseMapper.updateStatusById(id,status);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public List<WarehouseEO> getList(UserEO user) {
        return this.baseMapper.getList(user.getUserId());
    }
}
