package com.xchinfo.erp.controller.sys.auth;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.RoleEO;
import com.xchinfo.erp.sys.auth.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@RestController
@RequestMapping("/sys/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 根据组合条件查找数据
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:role:info")
    public Result<IPage<RoleEO>> page(@RequestBody Criteria criteria){
        logger.info("======== RoleController.page() ========");

        IPage<RoleEO> page = this.roleService.selectPage(criteria);

        return new Result<IPage<RoleEO>>().ok(page);
    }

    /**
     * 用户选择角色
     *
     * @return
     */
    @GetMapping("select")
    @RequiresPermissions("sys:role:info")
    public Result<List<RoleEO>> select(){
        logger.info("======== RoleController.select() ========");

        List<RoleEO> roles = this.roleService.listAll(getUserId());

        return new Result<List<RoleEO>>().ok(roles);
    }

    /**
     * 根据角色类型选择角色
     *
     * @return
     */
    @GetMapping("selectByType/{type}")
    @RequiresPermissions("sys:role:info")
    public Result<List<RoleEO>> selectByType(@PathVariable("type") Integer type){
        logger.info("======== RoleController.selectByType() ========");

        List<RoleEO> roles = this.roleService.selectByType(type);

        return new Result<List<RoleEO>>().ok(roles);
    }

    /**
     * 根据主键获取数据
     *
     * @param roleId
     * @return
     */
    @GetMapping("{roleId}")
    @RequiresPermissions("sys:role:info")
    public Result<RoleEO> info(@PathVariable("roleId") Long roleId){
        logger.info("======== RoleController.info(roleId => "+roleId+") ========");

        RoleEO role = this.roleService.getById(roleId);

        return new Result<RoleEO>().ok(role);
    }

    /**
     * 创建
     *
     * @param role
     * @return
     */
    @PostMapping
    @OperationLog("创建角色")
    @RequiresPermissions("sys:role:create")
    public Result create(@RequestBody RoleEO role){
        logger.info("======== RoleController.create(roleCode => "+role.getRoleId()+") ========");

        ValidatorUtils.validateEntity(role, AddGroup.class, DefaultGroup.class);

        this.roleService.save(role);

        return new Result();
    }

    /**
     * 更新
     *
     * @param role
     * @return
     */
    @PutMapping
    @OperationLog("更新角色")
    @RequiresPermissions("sys:role:update")
    public Result update(@RequestBody RoleEO role){
        logger.info("======== RoleController.update(roleCode => "+role.getRoleId()+") ========");

        ValidatorUtils.validateEntity(role, UpdateGroup.class, DefaultGroup.class);

        this.roleService.updateById(role);

        return new Result();
    }

    /**
     * 功能授权
     *
     * @param role
     * @return
     */
    @PostMapping("authOpt")
    @OperationLog("更新角色")
    @RequiresPermissions("sys:role:update")
    public Result authOpt(@RequestBody RoleEO role){
        logger.info("======== RoleController.authOpt(roleId => "+role.getRoleId()+") ========");

        // 检查菜单列表是否为空
        AssertUtils.isListEmpty(role.getMenuIds(), "menuId");

        this.roleService.authOpt(role);

        return new Result();
    }

    /**
     * 复制功能权限
     *
     * @param map
     * @return
     */
    @PostMapping("copyOpt")
    @OperationLog("更新角色")
    @RequiresPermissions("sys:role:update")
    public Result copyOpt(@RequestBody Map map){
        logger.info("======== RoleController.copyOpt() ========");

        this.roleService.copyOpt(map);

        return new Result();
    }

    /**
     * 数据授权
     *
     * @param role
     * @return
     */
    @PostMapping("authData")
    @OperationLog("更新角色")
    @RequiresPermissions("sys:role:update")
    public Result authData(@RequestBody RoleEO role){
        logger.info("======== RoleController.authData(roleId => "+role.getRoleId()+") ========");

        // 检查机构列表是否为空
        AssertUtils.isListEmpty(role.getOrgIds(), "orgId");

        this.roleService.authData(role);

        return new Result();
    }

    /**
     * 删除
     *
     * @param roleIds
     * @return
     */
    @DeleteMapping
    @OperationLog("更新角色")
    @RequiresPermissions("sys:role:delete")
    public Result delete(@RequestBody Long[] roleIds){
        logger.info("======== RoleController.delete() ========");

        AssertUtils.isArrayEmpty(roleIds, "id");

        this.roleService.removeByIds(roleIds);

        return new Result();
    }

    /**
     * 生效角色
     *
     * @param roleId
     * @return
     */
    @PostMapping("enable")
    @RequiresPermissions("sys:role:enable")
    public Result enable(@RequestBody Long roleId){
        logger.info("======== RoleController.enable(roleId => "+roleId+") ========");

        RoleEO role = this.roleService.getById(roleId);
        role.setStatus(1);
        this.roleService.updateById(role);

        return new Result();
    }

    /**
     * 失效用户
     *
     * @param roleId
     * @return
     */
    @PostMapping("disable")
    @RequiresPermissions("sys:role:disable")
    public Result disable(@RequestBody Long roleId){
        logger.info("======== RoleController.disable(roleId => "+roleId+") ========");

        RoleEO role = this.roleService.getById(roleId);
        role.setStatus(0);
        this.roleService.updateById(role);

        return new Result();
    }
}
