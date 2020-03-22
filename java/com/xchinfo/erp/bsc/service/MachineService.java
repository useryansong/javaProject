package com.xchinfo.erp.bsc.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.mapper.MachineMapper;
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
 * @date 2019/3/7
 * @update
 */
@Service
public class MachineService extends BaseServiceImpl<MachineMapper, MachineEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private OrgService orgService;

    public List<MachineEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }

    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(MachineEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        //校验机构权限
        if (!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        Integer count = this.baseMapper.selectCountByName(entity);

        if (count > 0) {
            throw new BusinessException("固定资产编号在同一机构已存在，请确认！");
        }


        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_machine", entity);
        AssertUtils.isBlank(code);

        entity.setMachineCode(code);

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MachineEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        //校验机构权限
        if (!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        Integer count = this.baseMapper.selectCountByName(entity);

        if (count > 0) {
            throw new BusinessException("固定资产编号在同一机构已存在，请确认！");
        }

        return super.updateById(entity);

    }

    @Override
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = MachineEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        // 删除关系
        for (Serializable id : idList) {
            MachineEO machineEO = this.baseMapper.selectById(id);
            //校验机构权限
            if (!checkPer(machineEO.getOrgId(),userId)) {
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }


            super.removeById(id);
        }

        return true;
    }

    public boolean updateStatusById(Long id, int status) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        MachineEO machineEO = this.baseMapper.selectById(id);
        //校验机构权限
        if (!checkPer(machineEO.getOrgId(), userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }
        return this.baseMapper.updateStatusById(id, status);
    }



/*    @Override
    public List<MachineEO> machineinfo(){
        return this.baseMapper.machineinfo();}*/

    public Boolean checkPer(Long orgId, Long userId) {

        return this.orgService.checkUserPermissions(orgId, userId);
    }
}
