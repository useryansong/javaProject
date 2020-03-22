package com.xchinfo.erp.sys.auth.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import com.xchinfo.erp.sys.auth.entity.RoleEO;
import com.xchinfo.erp.sys.auth.mapper.RoleMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Service
public class RoleService extends BaseServiceImpl<RoleMapper, RoleEO> {
    @Override
    public RoleEO getById(Serializable id) {
        return this.baseMapper.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(RoleEO role) throws BusinessException {
        // 检查编号是否已经被使用
        if (retBool(baseMapper.selectCountByCode(role.getRoleCode())))
            throw new BusinessException("角色编码已经被使用，请重新输入!");

        // 插入角色数据
        if (!super.save(role))
            throw new BusinessException("插入角色数据失败!");

        // 插入菜单授权数据
        if (null != role.getMenuIds()){
            if (!this.updateOptAuth(role))
                throw new BusinessException("插入菜单授权数据失败!");
        }

        // 插入机构授权数据
        if (null != role.getOrgIds()){
            if (!this.updateOrgAuth(role))
                throw new BusinessException("插入机构授权数据失败!");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(RoleEO role) throws BusinessException {
        // 插入角色数据
        if (!super.updateById(role))
            throw new BusinessException("更新角色数据失败!");

        // 插入菜单授权数据
        if (null != role.getMenuIds()){
            if (!this.updateOptAuth(role))
                throw new BusinessException("插入菜单授权数据失败!");
        }

        // 插入机构授权数据
        if (null != role.getOrgIds()){
            if (!this.updateOrgAuth(role))
                throw new BusinessException("插入机构授权数据失败!");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = RoleEO.class)
    public boolean removeByIds(Serializable[] ids) throws BusinessException {
        for (Serializable roleId : ids){
            RoleEO role = this.getById(roleId);

            // 检查角色是否在使用
            if (retBool(this.baseMapper.selectCountAuthById(roleId)))
                throw new BusinessException(role.getRoleName() + " 角色已经在授权中使用，不能删除!");

            // 删除授权信息
            this.baseMapper.deleteOptAuth((Long) roleId);
            this.baseMapper.deleteOrgAuth((Long) roleId);

            if (!super.removeById(roleId))
                throw new BusinessException(role.getRoleName() + " 删除失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean authOpt(RoleEO role) throws BusinessException {
        // 插入菜单授权数据
        if (null != role.getMenuIds()){
            if (!this.updateOptAuth(role))
                throw new BusinessException("插入菜单授权数据失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean copyOpt(Map map) throws BusinessException {
        Integer fromRoleId = (Integer)map.get("fromRoleId");
        Integer toRoleId = (Integer)map.get("toRoleId");//被复制用户id
        int res = this.baseMapper.copyOpt(fromRoleId,toRoleId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean authData(RoleEO role) throws BusinessException {
        // 插入机构授权数据
        if (null != role.getOrgIds()){
            if (!this.updateOrgAuth(role))
                throw new BusinessException("插入机构授权数据失败!");
        }

        return true;
    }

    public String getRoleNames(List<Long> roleIds) {
        String roleNames = "";
        if(roleIds==null || roleIds.size()==0) {
            return roleNames;
        }
        for(Long roleId : roleIds) {
            RoleEO role = this.getById(roleId);
            roleNames += (role.getRoleName() + ",");
        }
        if(!"".equals(roleNames)) {
            return roleNames.substring(0, roleNames.length()-1);
        } else {
            return roleNames;
        }
    }

    /**
     * 更新功能授权
     *
     * @param role
     * @return
     */
    private boolean updateOptAuth(RoleEO role) {
        // 删除功能授权
        try{
            this.baseMapper.deleteOptAuth(role.getRoleId());
        } catch (Exception e){
            return false;
        }

        // 插入新的功能授权
        List<Long> menus = role.getMenuIds();
        for (Long menuId : menus){
            try{
                this.baseMapper.updateOptAuth(role.getRoleId(), menuId);
            } catch (Exception e){
                return false;
            }
        }

        return true;
    }

    /**
     * 更新机构权限
     *
     * @param role
     * @return
     */
    private boolean updateOrgAuth(RoleEO role) {
        // 删除功能授权
        try{
            this.baseMapper.deleteOrgAuth(role.getRoleId());
        } catch (Exception e){
            return false;
        }

        // 插入新的功能授权
        List<Long> orgs = role.getOrgIds();
        for (Long orgId : orgs){
            try{
                this.baseMapper.updateOrgAuth(role.getRoleId(), orgId);
            } catch (Exception e){
                return false;
            }
        }
        return true;
    }

    public List<RoleEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }

    public List<RoleEO> selectByType(Integer type) {
        return this.baseMapper.selectByType(type);
    }
}
