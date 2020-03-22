package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.StockGroupEO;
import com.xchinfo.erp.bsc.service.StockGroupService;
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
 * @author yuanchang
 * @date 2019/4/6
 * @update
 */
@RestController
@RequestMapping("basic/stockGroup")
public class StockGroupController extends BaseController {


    @Autowired
    private StockGroupService stockGropService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:stockGroup:info")
    public Result<IPage<StockGroupEO>> page(@RequestBody Criteria criteria){
        logger.info("======== StockGroupController.page() ========");

        IPage<StockGroupEO> page = this.stockGropService.selectPage(criteria);

        return new Result<IPage<StockGroupEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<StockGroupEO>> list(){
        logger.info("======== StockGroupController.list() ========");

        List<StockGroupEO> customers = this.stockGropService.listAll();

        return new Result<List<StockGroupEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:stockGroup:info")
    public Result<StockGroupEO> info(@PathVariable("id") Long id){
        logger.info("======== StockGroupController.info(entity => "+id+") ========");

        StockGroupEO entity = this.stockGropService.getById(id);

        return new Result<StockGroupEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建仓库组")
    @RequiresPermissions("basic:stockGroup:create")
    public Result create(@RequestBody StockGroupEO entity){
        logger.info("======== StockGroupController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.stockGropService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新仓库组")
    @RequiresPermissions("basic:stockGroup:update")
    public Result update(@RequestBody StockGroupEO entity){
        logger.info("======== StockGroupController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.stockGropService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除仓库组")
    @RequiresPermissions("basic:stockGroup:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== StockGroupController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.stockGropService.removeByIds(ids);

        return new Result();
    }
}
