package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.PurchaseGroupEO;
import com.xchinfo.erp.bsc.service.PurchaseGroupService;
import com.xchinfo.erp.controller.BaseController;
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

/**
 * @author zhongy
 * @date 2019/4/6
 * @update
 */
@RestController
@RequestMapping("/basic/purchaseGroup")
public class PurchaseGroupController extends BaseController {
    @Autowired
    private PurchaseGroupService purchaseGroupService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:purchaseGroup:info")
    public Result<IPage<PurchaseGroupEO>> page(@RequestBody Criteria criteria){
        logger.info("======== PurchaseGroupController.page() ========");
        IPage<PurchaseGroupEO> page = this.purchaseGroupService.selectPage(criteria);
        return new Result<IPage<PurchaseGroupEO>>().ok(page);
    }

    /**
     * 查询所有采购组
     * @return
     */
    @GetMapping("list")
    public Result<List<PurchaseGroupEO>> list(){
        logger.info("======== PurchaseGroupController.list() ========");
        List<PurchaseGroupEO> purchaseGroups = this.purchaseGroupService.listAll();
        return new Result<List<PurchaseGroupEO>>().ok(purchaseGroups);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:purchaseGroup:info")
    public Result<PurchaseGroupEO> info(@PathVariable("id") Long id){
        logger.info("======== PurchaseGroupController.info(entity => "+id+") ========");
        PurchaseGroupEO entity = this.purchaseGroupService.getById(id);
        return new Result<PurchaseGroupEO>().ok(entity);
    }

    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建采购组")
    @RequiresPermissions("basic:purchaseGroup:create")
    public Result create(@RequestBody PurchaseGroupEO entity){
        logger.info("======== PurchaseGroupController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.purchaseGroupService.save(entity);
        return new Result();
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新采购组")
    @RequiresPermissions("basic:purchaseGroup:update")
    public Result update(@RequestBody PurchaseGroupEO entity){
        logger.info("======== PurchaseGroupController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.purchaseGroupService.updateById(entity);
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除采购组")
    @RequiresPermissions("basic:purchaseGroup:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== PurchaseGroupController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.purchaseGroupService.removeByIds(ids);
        return new Result();
    }
}
