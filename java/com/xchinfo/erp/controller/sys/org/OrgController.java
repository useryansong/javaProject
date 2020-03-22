package com.xchinfo.erp.controller.sys.org;


import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@RestController
@RequestMapping("/sys/org")
public class OrgController extends BaseController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    /**
     * 查找
     *
     * @return
     */
    @GetMapping("list")
    @RequiresPermissions("sys:org:info")
    public Result<List<OrgEO>>list(){
        logger.info("======== OrgController.list() ========");

        List<OrgEO> orgs = this.orgService.selectTreeList(new HashMap<>(1));

        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 至查询机构信息（类型为1）
     *
     * @return
     */
    @GetMapping("selectOrgList")
    public Result<List<OrgEO>> selectOrgList(){
        logger.info("======== OrgController.selectOrgList() ========");

        List<OrgEO> orgs = this.orgService.selectOrgTreeList(new HashMap<>(1));

        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 下拉框选择
     *
     * @return
     */
    @GetMapping("selectList")
    public Result<List<OrgEO>> selectList(){
        logger.info("======== OrgController.selectList() ========");

        List<OrgEO> orgs = this.orgService.selectTreeSelectList(new HashMap<>(1));

        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 获取用户的机构下拉框
     *
     * @return
     */
    @GetMapping("selectUserOrg")
    public Result<List<OrgEO>> selectUserOrg(){
        logger.info("======== OrgController.selectUserOrg() ========");

        List<OrgEO> orgs = this.orgService.selectUserOrg(getUserId());

        return new Result<List<OrgEO>>().ok(orgs);
    }


    /**
     * 权限用户机构
     *
     * @return
     */
    @GetMapping("selectPermissiontList")
    @RequiresPermissions("sys:org:info")
    public Result<List<OrgEO>> selectPermissionsTreeSelectList(){
        logger.info("======== OrgController.selectList() ========");
        List<OrgEO> orgs = this.orgService.selectPermissionsTreeSelectList(getUserId());

        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 归属机构(只显示二级部门,给供应商业务使用)
     *
     * @return
     */
    @GetMapping("selectBySupplier")
    public Result<List<OrgEO>> selectBySupplier(){
        logger.info("======== OrgController.selectList() ========");
        List<OrgEO> orgs = this.orgService.selectBySupplier();
        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 归属机构(只显示二级部门,给供应商业务-送货管理使用)
     *
     * @return
     */
    @GetMapping("selectBySupplierDeliveryNote")
    public Result<List<OrgEO>> selectBySupplierDeliveryNote(){
        logger.info("======== OrgController.selectBySupplierDeliveryNote() ========");
        String userName = super.getUserName();
        List<OrgEO> orgs = this.orgService.selectBySupplierDeliveryNote(userName);
        return new Result<List<OrgEO>>().ok(orgs);
    }

    /**
     * 权限用户部门
     *
     * @return
     */
    @GetMapping("selectPermissiontListAll")
    @RequiresPermissions("sys:org:info")
    public Result<List<OrgEO>> selectPermissionsTreeSelectListAll(){
        logger.info("======== OrgController.selectList() ========");

        List<OrgEO> orgs = this.orgService.selectPermissionsTreeSelectListAll(getUserId());

        return new Result<List<OrgEO>>().ok(orgs);
    }
    /**
     * 权限用户部门
     *
     * @return
     */
    @GetMapping("selectPermissiontListAllNew")
    @RequiresPermissions("sys:org:info")
    public Result<List<OrgEO>> selectPermissiontListAllNew(){
        logger.info("======== OrgController.selectPermissiontListAllNew() ========");

        List<OrgEO> orgs = this.orgService.selectPermissionsTreeSelectListAllNew(getUserId());

        return new Result<List<OrgEO>>().ok(orgs);
    }
    @GetMapping("{orgId}")
    @RequiresPermissions("sys:org:info")
    public Result<OrgEO> info(@PathVariable("orgId") Long orgId){
        logger.info("======== OrgController.info(orgId => "+orgId+") ========");

        OrgEO org = this.orgService.getById(orgId);

        return new Result<OrgEO>().ok(org);
    }

    /**
     * 创建
     *
     * @param org
     * @return
     */
    @PostMapping
    @RequiresPermissions("sys:org:create")
    @OperationLog("创建机构")
    public Result create(@RequestBody OrgEO org){
        logger.info("======== OrgController.create(orgId => "+org.getOrgId()+") ========");

        ValidatorUtils.validateEntity(org, AddGroup.class, DefaultGroup.class);

        this.orgService.save(org);

        return new Result();
    }

    /**
     * 更新
     *
     * @param org
     * @return
     */
    @PutMapping
    @RequiresPermissions("sys:org:update")
    @OperationLog("更新机构")
    public Result update(@RequestBody OrgEO org){
        logger.info("======== OrgController.update(orgId => "+org.getOrgId()+") ========");

        ValidatorUtils.validateEntity(org, UpdateGroup.class, DefaultGroup.class);

        this.orgService.updateById(org);

        return new Result();
    }

    /**
     * 删除
     *
     * @param orgId
     * @return
     */
    @DeleteMapping("{orgId}")
    @RequiresPermissions("sys:org:delete")
    @OperationLog("删除机构")
    public Result delete(@PathVariable("orgId") Long orgId){
        logger.info("======== OrgController.delete() ========");

        AssertUtils.isNull(orgId, "id");

        this.orgService.removeById(orgId);

        return new Result();
    }

    /***
     *获取调入库位列表
     * @return
     */
    @GetMapping("sonOrg/list")
    @RequiresPermissions("sys:org:info")
    public Result<List<OrgEO>> getSonOrgList(@RequestParam("orgId") Long orgId){
        logger.info("======== OrgController.getSonOrgList() ========");

        List<OrgEO> orgEOS = this.orgService.getSonOrgList(orgId);

        return new Result<List<OrgEO>>().ok(orgEOS);
    }

    /**
     * 获取所有的归属机构
     *
     * @return
     */
    @GetMapping("listAll")
    public Result<List<OrgEO>> listAll(){
        logger.info("======== OrgController.listAll() ========");
        List<OrgEO> orgs = this.orgService.listAll();
        return new Result<List<OrgEO>>().ok(orgs);
    }
    /**
     * 同步数据
     *
     * @return
     */
    @GetMapping("sync")
    @RequiresPermissions("sys:org:sync")
    public Result syncHR(){
        logger.info("======== OrgController.syncHR() ========");

        return this.orgService.syncHR();
    }

    /**
     * 获取所有的归属机构
     *
     * @return
     */
    @PostMapping("setStart")
    public Result setStart(@RequestBody Map map){
        logger.info("======== OrgController.setStart(map) ========");
        return this.orgService.setStart(map);
    }

    /**
     * 首页报表，安全生产日
     *
     * @return
     */
    @GetMapping("selectSafeProductionDays")
    public Result<Map> selectSafeProductionDays(){
        logger.info("======== OrgController.selectSafeProductionDays() ========");
        Map res = this.orgService.selectSafeProductionDays();
        return new Result<Map>().ok(res);
    }
    

    
}
