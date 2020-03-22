package com.xchinfo.erp.controller.bsc.scm;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.SaleGroupEO;
import com.xchinfo.erp.bsc.service.SaleGroupService;
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
 * @author roman.c
 * @date 2019/4/6
 * @update
 */
@RestController
@RequestMapping("/basic/saleGroup")
public class SaleGroupController extends BaseController {

    @Autowired
    private SaleGroupService saleGroupService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:saleGroup:info")
    public Result<IPage<SaleGroupEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SafeGroupController.page() ========");

        IPage<SaleGroupEO> page = this.saleGroupService.selectPage(criteria);

        return new Result<IPage<SaleGroupEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<SaleGroupEO>> list(){
        logger.info("======== SafeGroupController.list() ========");

        List<SaleGroupEO> customers = this.saleGroupService.listAll();

        return new Result<List<SaleGroupEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:saleGroup:info")
    public Result<SaleGroupEO> info(@PathVariable("id") Long id){
        logger.info("======== SafeGroupController.info(entity => "+id+") ========");

        SaleGroupEO entity = this.saleGroupService.getById(id);

        return new Result<SaleGroupEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建销售组")
    @RequiresPermissions("basic:saleGroup:create")
    public Result create(@RequestBody SaleGroupEO entity){
        logger.info("======== SafeGroupController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.saleGroupService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新销售组")
    @RequiresPermissions("basic:saleGroup:update")
    public Result update(@RequestBody SaleGroupEO entity){
        logger.info("======== SafeGroupController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.saleGroupService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除销售组")
    @RequiresPermissions("basic:saleGroup:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SafeGroupController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.saleGroupService.removeByIds(ids);

        return new Result();
    }
}
