package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.service.WarehouseService;
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
import org.yecat.mybatis.utils.Criterion;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/11
 * @update
 */
@RestController
@RequestMapping("/basic/warehouse")
public class WarehouseController extends BaseController {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:warehouse:info")
    public Result<IPage<WarehouseEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WarehouseController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<WarehouseEO> page = this.warehouseService.selectPage(criteria);

        return new Result<IPage<WarehouseEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:warehouse:info")
    public Result<WarehouseEO> info(@PathVariable("id") Long id){
        logger.info("======== WarehouseController.info(entity => "+id+") ========");

        WarehouseEO entity = this.warehouseService.getById(id);

        return new Result<WarehouseEO>().ok(entity);
    }

    /**
     * 查找所有仓库
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<WarehouseEO>> list(){
        logger.info("======== WarehouseController.page() ========");

        List<WarehouseEO> warehouses = this.warehouseService.listALL(getUser());

        return new Result<List<WarehouseEO>>().ok(warehouses);
    }

    /**
     * 更新状态
     *
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:warehouse:setStatus")
    public Result updateStatusById(@RequestParam("id") Long id,@RequestParam("status") int status){
        logger.info("======== WarehouseController.updateStatusById() ========");

        this.warehouseService.updateStatusById(id,status);

        return new Result();
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建仓库")
    @RequiresPermissions("basic:warehouse:create")
    public Result create(@RequestBody WarehouseEO entity){
        logger.info("======== WarehouseController.create(ID => "+entity.getId()+") ========");
        entity.setOrgId(getOrgId());
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.warehouseService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新仓库")
    @RequiresPermissions("basic:warehouse:update")
    public Result update(@RequestBody WarehouseEO entity){
        logger.info("======== WarehouseController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.warehouseService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除仓库")
    @RequiresPermissions("basic:warehouse:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WarehouseController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.warehouseService.removeByIds(ids);

        return new Result();
    }

    /**
     * 查找所有仓库及其下库区,以及库区下的库位
     *
     * @return
     */
    @GetMapping("getList")
    public Result<List<WarehouseEO>> getList(){
        logger.info("======== WarehouseController.getList() ========");

        List<WarehouseEO> warehouses = this.warehouseService.getList(getUser());

        return new Result<List<WarehouseEO>>().ok(warehouses);
    }
}
