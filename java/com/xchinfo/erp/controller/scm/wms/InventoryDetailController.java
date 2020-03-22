package com.xchinfo.erp.controller.scm.wms;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import com.xchinfo.erp.wms.service.InventoryDetailService;
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

/**
 * @author roman.c
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/inventoryDetail")
public class InventoryDetailController extends BaseController {

    @Autowired
    private InventoryDetailService iInventoryDetailService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<InventoryDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== InventoryDetailController.list() ========");

        IPage<InventoryDetailEO> page = this.iInventoryDetailService.selectPage(criteria);

        return new Result<IPage<InventoryDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:inventory:info")
    public Result<InventoryDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== InventoryDetailController.info(entity => "+id+") ========");

        InventoryDetailEO entity = this.iInventoryDetailService.getById(id);

        return new Result<InventoryDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建盘点明细单")
    @RequiresPermissions("wms:inventory:create")
    public Result create(@RequestBody InventoryDetailEO entity){
        logger.info("======== InventoryDetailController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.iInventoryDetailService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新盘点明细单")
    @RequiresPermissions("wms:inventory:update")
    public Result update(@RequestBody InventoryDetailEO entity){
        logger.info("======== InventoryDetailController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.iInventoryDetailService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除盘点明细单")
    @RequiresPermissions("wms:inventory:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== InventoryDetailController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.iInventoryDetailService.removeByIds(ids);

        return new Result();
    }
}
