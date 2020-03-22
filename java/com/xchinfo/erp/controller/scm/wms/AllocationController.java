package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.AllocationDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.AllocationDetailService;
import com.xchinfo.erp.wms.service.StockAccountService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author yuanchang
 * @date 2019/5/9
 * @update
 */
@RestController
@RequestMapping("/wms/allocation")
public class AllocationController extends BaseController {

    @Autowired
    private AllocationDetailService allocationDetailService;

    @Autowired
    private StockAccountService stockAccountService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:allocation:info")
    public Result<IPage<AllocationDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== AllocationController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<AllocationDetailEO> page = this.allocationDetailService.selectPage(criteria);

        return new Result<IPage<AllocationDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:allocation:info")
    public Result<AllocationDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== AllocationController.info(entity => "+id+") ========");

        AllocationDetailEO entity = this.allocationDetailService.getById(id);

        return new Result<AllocationDetailEO>().ok(entity);
    }


    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建调拨单明细")
    @RequiresPermissions("wms:allocation:create")
    public Result create(@RequestBody AllocationDetailEO entity){
        logger.info("======== AllocationDetailController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.allocationDetailService.save(entity);

        return new Result();
    }


    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新调拨单明细")
    @RequiresPermissions("wms:allocation:update")
    public Result update(@RequestBody AllocationDetailEO entity){
        logger.info("======== AllocationController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.allocationDetailService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除调拨单明细")
    @RequiresPermissions("wms:allocation:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== AllocationController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.allocationDetailService.removeByIds(ids);

        return new Result();
    }

    /**
     * 设置状态（发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/release")
    @OperationLog("设置状态-发布")
    @RequiresPermissions("wms:allocation:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== AllocationController.updateStatusRelease ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.allocationDetailService.updateStatusById(ids,1);

        /*this.allocationDetailService.addStock(ids);*/

        return new Result();
    }


    /**
     * 设置状态（取消发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelRelease")
    @OperationLog("设置状态-取消发布")
    @RequiresPermissions("wms:allocation:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== AllocationController.updateStatusCancelRelease ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.allocationDetailService.updateStatusById(ids,0);

        /*this.allocationDetailService.deleteStock(ids);*/

        return new Result();
    }

    /***
     *获取物料列表
     * @param criteria
     * @return
     */
    @PostMapping("material/page")
    @OperationLog("物料选择")
    @RequiresPermissions("wms:allocation:update")
    public Result<IPage<StockAccountEO>> getMaterialPage(@RequestBody Criteria criteria){
        logger.info("======== AllocationController.list() ========");

        IPage<StockAccountEO> page = this.allocationDetailService.getMaterialPage(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);

    }

    /***
     *获取出库物料列表
     * @param criteria
     * @return
     */
    @PostMapping("deliveryMaterial/page")
    @OperationLog("物料选择")
    @RequiresPermissions("wms:allocation:update")
    public Result<IPage<StockAccountEO>> getDeliveryMaterialPage(@RequestBody Criteria criteria){
        logger.info("======== AllocationController.list() ========");

        IPage<StockAccountEO> page = this.allocationDetailService.getDeliveryMaterialPage(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);

    }

    /***
     *获取出库物料列表
     * @param criteria
     * @return
     */
    @PostMapping("deliveryProductMaterial/page")
    @OperationLog("物料选择")
    @RequiresPermissions("wms:allocation:update")
    public Result<IPage<StockAccountEO>> getDeliveryProductMaterialPage(@RequestBody Criteria criteria){
        logger.info("======== AllocationController.list() ========");

        IPage<StockAccountEO> page = this.allocationDetailService.getDeliveryProductMaterialPage(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);

    }

    /***
     *获取仓库列表
      * @return
     */
    @GetMapping("warehouse/list")
    public Result<List<WarehouseEO>> listWarehouse(@RequestParam("id") Long id){
        logger.info("======== AllocationController.listWarehouse() ========");

        List<WarehouseEO> warehouses = this.allocationDetailService.listWarehouse(id);

        return new Result<List<WarehouseEO>>().ok(warehouses);
    }

    /***
     *获取库位列表
     * @return
     */
    @GetMapping("warehouseLocation/list")
    public Result<List<WarehouseLocationEO>> listWarehouseLocation(@RequestParam("id") Long id,@RequestParam("warehouseId") Long warehouseId){
        logger.info("======== AllocationController.listWarehouseLocation() ========");

        List<WarehouseLocationEO> warehouses = this.allocationDetailService.listWarehouseLocation(id,warehouseId);

        return new Result<List<WarehouseLocationEO>>().ok(warehouses);
    }

    /***
     *获取物料数量
     * @return
     */
    @GetMapping("realamount")
    public Result getAmount(@RequestParam("id") Long id,@RequestParam("warehouseId") Long warehouseId,@RequestParam("locationId") Long locationId){
        logger.info("======== AllocationController.getAmount() ========");

        int amount = this.allocationDetailService.getAmount(id,warehouseId,locationId);

        return new Result<Integer>().ok(amount);
    }

    /***
     *获取调入库位列表
     * @return
     */
    @GetMapping("towarehouseLocation/list")
    public Result<List<WarehouseLocationEO>> getToWarehouseLocation(@RequestParam("warehouseId") Long warehouseId){
        logger.info("======== AllocationController.listWarehouseLocation() ========");

        List<WarehouseLocationEO> warehouses = this.allocationDetailService.getToWarehouseLocation(warehouseId);

        return new Result<List<WarehouseLocationEO>>().ok(warehouses);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param allocationDetailEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出报表")
    @RequiresPermissions("wms:allocation:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody AllocationDetailEO[] allocationDetailEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("allocation.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(allocationDetailEOS), jsonObject);

        return new Result();
    }



}
