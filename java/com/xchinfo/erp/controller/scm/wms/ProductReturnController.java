package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.ProductReturnDetailEO;
import com.xchinfo.erp.scm.wms.entity.ProductReturnEO;
import com.xchinfo.erp.wms.service.ProductReturnService;
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
 * @author yuanchang
 * @date 2019/5/18
 * @update
 */
@RestController
@RequestMapping("/wms/productReturn")
public class ProductReturnController extends BaseController {
    @Autowired
    private ProductReturnService productReturnService;



    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:productReturn:info")
    public Result<IPage<ProductReturnEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ProductReturnEO> page = this.productReturnService.selectPage(criteria);

        return new Result<IPage<ProductReturnEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:productReturn:info")
    public Result<ProductReturnEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductReturnController.info(entity => "+id+") ========");

        ProductReturnEO entity = this.productReturnService.getById(id);

        return new Result<ProductReturnEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
    @RequiresPermissions("wms:productReturn:info")
    public Result<List<ProductReturnDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== ProductReturnController.info(listDetails => "+orderId+") ========");

        List<ProductReturnDetailEO> details = this.productReturnService.listDetailsByOrder(orderId);

        return new Result<List<ProductReturnDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建退货订单")
    @RequiresPermissions("wms:productReturn:create")
    public Result create(@RequestBody ProductReturnEO entity){
        logger.info("======== ProductReturnController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        ProductReturnEO receiveOrderEO = this.productReturnService.saveEntity(entity);

        return new Result<ProductReturnEO>().ok(receiveOrderEO);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新退货订单")
    @RequiresPermissions("wms:productReturn:update")
    public Result update(@RequestBody ProductReturnEO entity){
        logger.info("======== ProductReturnController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.productReturnService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除退货订单")
    @RequiresPermissions("wms:productReturn:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.removeByIds(ids);

        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除退货订单明细")
    @RequiresPermissions("wms:productReturn:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== ProductReturnController.deleteDetailById() ========");

        this.productReturnService.deleteDetailById(id);

        return new Result();
    }


    /**
     * 明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    @RequiresPermissions("wms:productReturn:info")
    public Result<IPage<ProductReturnDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        IPage<ProductReturnDetailEO> page = this.productReturnService.selectDetailPage(criteria);

        return new Result<IPage<ProductReturnDetailEO>>().ok(page);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail")
    @OperationLog("删除退货订单明细")
    @RequiresPermissions("wms:productReturn:delete")
    public Result deleteDetail(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.removeByDetailIds(ids);

        return new Result();
    }


    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    @RequiresPermissions("wms:productReturn:info")
    public Result<ProductReturnDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== ProductReturnController.info(entity => "+id+") ========");

        ProductReturnDetailEO entity = this.productReturnService.getDetailById(id);

        return new Result<ProductReturnDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping("detail")
    @OperationLog("创建退货明细")
    @RequiresPermissions("wms:productReturn:create")
    public Result createDetail(@RequestBody ProductReturnDetailEO entity){
        logger.info("======== ProductReturnController.createDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.productReturnService.saveDetail(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("detail")
    @OperationLog("更新退货明细")
    @RequiresPermissions("wms:productReturn:update")
    public Result updateDetail(@RequestBody ProductReturnDetailEO entity){
        logger.info("======== ProductReturnController.updateDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.productReturnService.updateDetailById(entity);

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
    @RequiresPermissions("wms:productReturn:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.updateStatusById(ids,1,0);

        /*this.receiveOrderService.addDetailToStock(ids);*/

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
    @RequiresPermissions("wms:productReturn:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.updateStatusById(ids,0,1);

        /*this.receiveOrderService.deleteStock(ids);*/

        return new Result();
    }

    /**
     * 设置状态（完成）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/finish")
    @OperationLog("设置状态-完成")
    @RequiresPermissions("wms:productReturn:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.updateStatusById(ids,2,1);

        return new Result();
    }

    /**
     * 设置状态（关闭）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/close")
    @OperationLog("设置状态-关闭")
    @RequiresPermissions("wms:productReturn:updateClose")
    public Result updateStatusClose(@RequestBody Long[] ids){
        logger.info("======== ProductReturnController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnService.updateStatusById(ids,9,0);

        return new Result();
    }


    /***
     * 通过流水获取产品退货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("wms:productReturn:info")
    public Result<ProductReturnEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== ProductReturnController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        ProductReturnEO entity = this.productReturnService.getDetailInfoByNo(voucherNo);
        return new Result<ProductReturnEO>().ok(entity);
    }


    /**
     * 单个产成品退货
     * @param
     * @return
     */
    @PostMapping("app/return")
    @OperationLog("单个退货")
    //@RequiresPermissions("wms:productReturn:update")
    public Result returnOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== ProductReturnController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.productReturnService.returnOne(Id,amount,action,userId,userName);

        return new Result();
    }


    @PostMapping("app/page")
    public Result<IPage<ProductReturnEO>> pageAPP(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.pageAPP() ========");

        Criterion criterion2 = new Criterion();
        criterion2.setField("p.org_id");
        criterion2.setOp("eq");
        criterion2.setValue(getOrgId()+"");
        criteria.getCriterions().add(criterion2);

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ProductReturnEO> page = this.productReturnService.selectPage(criteria);

        return new Result<IPage<ProductReturnEO>>().ok(page);
    }

    /***
     *获取调入库位列表
     * @return id
     */
    @GetMapping("warehouseLocation/list")
    public Result<List<WarehouseLocationEO>> getWarehouseLocation(@RequestParam("id") Long id){
        logger.info("======== ProductReturnController.getWarehouseLocation() ========");

        List<WarehouseLocationEO> warehouses = this.productReturnService.getWarehouseLocation(id);

        return new Result<List<WarehouseLocationEO>>().ok(warehouses);
    }

    /**
     * 单个收货按库位
     * @param
     * @return
     */
    @PostMapping("appLocation/return")
    @OperationLog("单个退货")
    //@RequiresPermissions("wms:productReturn:update")
    public Result returnOnelocation(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action,@RequestParam("locationId") Long locationId){
        logger.info("======== ProductReturnController.returnOnelocation() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.productReturnService.returnOnelocation(Id,amount,action,userId,userName,locationId);

        return new Result();
    }
}
