package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
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
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/deliveryOrder")
public class DeliveryOrderController extends BaseController {
    @Autowired
    private DeliveryOrderService deliveryOrderService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<IPage<DeliveryOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<DeliveryOrderEO> page = this.deliveryOrderService.selectPage(criteria);
        return new Result<IPage<DeliveryOrderEO>>().ok(page);
    }

    /**
     * 查询所有出库单
     * @return
     */
    @GetMapping("list")
    public Result<List<DeliveryOrderEO>> list(){
        logger.info("======== DeliveryOrderController.list() ========");
        List<DeliveryOrderEO> deliveryOrders = this.deliveryOrderService.listAll(getUserId());
        return new Result<List<DeliveryOrderEO>>().ok(deliveryOrders);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<DeliveryOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryOrderController.info(entity => "+id+") ========");
        DeliveryOrderEO entity = this.deliveryOrderService.getById(id);
        return new Result<DeliveryOrderEO>().ok(entity);
    }

    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单")
    @RequiresPermissions("wms:deliveryOrder:create")
    public Result create(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOrderController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
//        this.deliveryOrderService.save(entity);
        DeliveryOrderEO entityFromDb = this.deliveryOrderService.saveEntity(entity);
        return new Result<DeliveryOrderEO>().ok(entityFromDb);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result update(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOrderController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryOrderService.updateById(entity);
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除出库单")
    @RequiresPermissions("wms:deliveryOrder:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.deliveryOrderService.removeByIds(ids);
        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除入库订单明细")
    @RequiresPermissions("wms:deliveryOrder:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== DeliveryOrderController.deleteDetail() ========");

        this.deliveryOrderService.deleteDetailById(id);

        return new Result();
    }

    /**
     * 查询出库单明细
     *
     * @param deliveryId
     * @return
     */
    @GetMapping("listDetails/{deliveryId}")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<List<DeliveryOrderDetailEO>> listDetails(@PathVariable("deliveryId") Long deliveryId){
        logger.info("======== DeliveryOrderController.info(listDetails => "+deliveryId+") ========");
        List<DeliveryOrderDetailEO> details = this.deliveryOrderService.listDetailsByDelivery(deliveryId);
        return new Result<List<DeliveryOrderDetailEO>>().ok(details);
    }

/*    *//**
     * 更新状态
     *
     * @param entity
     * @return
     *//*
    @PostMapping("updateStatusById")
    @OperationLog("更新状态")
    public Result updateStatusById(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOrderController.updateStatusById ========");

        this.deliveryOrderService.updateStatusById(entity.getDeliveryId(), entity.getStatus());
        return new Result();
    }*/
/*
    *//**
     * 发布
     * @param ids
     * @return
     *//*
    @PostMapping("release")
    @OperationLog("发布出库单")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderService.releaseByIds(ids);
        return new Result();
    }

    *//**
     * 取消发布
     * @param ids
     * @return
     *//*
    @PostMapping("cancelRelease")
    @OperationLog("取消发布出库单")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderService.cancelReleaseByIds(ids);
        return new Result();
    }*/

    /**
     * 设置状态（发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/release")
    @OperationLog("设置状态-发布")
    @RequiresPermissions("wms:deliveryOrder:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,1,0);

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
    @RequiresPermissions("wms:deliveryOrder:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,0,1);

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
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,2,1);

        return new Result();
    }


    /***
     * 通过流水获取出货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("wms:deliveryOrder:info")
    public Result<DeliveryOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== DeliveryOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        DeliveryOrderEO entity = this.deliveryOrderService.getDetailInfoByNo(voucherNo);
        return new Result<DeliveryOrderEO>().ok(entity);
    }


    /**
     * 单个发货
     * @param
     * @return
     */
    @PostMapping("app/delivery")
    @OperationLog("成品发货")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result deliveryOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== DeliveryOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryOrderService.deliveryOne(Id,amount,action,userId,userName);

        return new Result();
    }

    /**
     * 查找所有符合条件的出库单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("getList")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<List<DeliveryOrderEO>> getList(@RequestBody Criteria criteria) {
        logger.info("======== DeliveryOrderController.getList() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        map.put("deliveryType", 1);
        List<DeliveryOrderEO> list = this.deliveryOrderService.getList(map);
        return new Result<List<DeliveryOrderEO>>().ok(list);
    }

    /**
     * 设置车次
     *
     * @param
     * @return
     */
    @PostMapping("setTran")
    @OperationLog("设置车次")
    public Result setTran(@RequestParam("trainNumber") String trainNumber,@RequestParam("ids") Long[] ids){
        logger.info("======== DeliveryOrderController.setTran ========");

        this.deliveryOrderService.setTran(ids,trainNumber,getUser());
        return new Result();
    }

    /**
     * 查询车次相应的架数
     *
     * @param
     * @return
     */
    @GetMapping("getTran")
    @OperationLog("查询车次相应的架数")
    public Result getTran(@RequestParam("trainNumber") String trainNumber,@RequestParam("date") String date){
        logger.info("======== DeliveryOrderController.setTran ========");

        Double count = this.deliveryOrderService.getTran(date,trainNumber,getUser());
        return new Result<Double>().ok(count);
    }


    @PostMapping("app/page")
    public Result<IPage<DeliveryOrderEO>> pageApp(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderController.pageApp() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        Criterion criterionTwo = new Criterion();
        criterionTwo.setField("do.org_id");
        criterionTwo.setOp("eq");
        criterionTwo.setValue(getOrgId()+"");
        criteria.getCriterions().add(criterionTwo);

        IPage<DeliveryOrderEO> page = this.deliveryOrderService.selectPage(criteria);
        return new Result<IPage<DeliveryOrderEO>>().ok(page);
    }

    /**
     * 物料按库位分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("checkStock/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<StockAccountEO>> checkStockByLocation(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderController.checkStockByLocation() ========");

        IPage<StockAccountEO> page = this.deliveryOrderService.checkStockByLocation(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }


    /**
     * 按库位单个发货
     * @param
     * @return
     */
    @PostMapping("appLocation/delivery")
    @OperationLog("成品发货")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result deliveryOneByLocation(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action,@RequestParam("locationId") Long locationId,@RequestParam("stockFinishFlag") Long stockFinishFlag){
        logger.info("======== DeliveryOrderController.deliveryOneByLocation() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryOrderService.deliveryOneByLocation(Id,amount,action,userId,userName,locationId,stockFinishFlag);

        return new Result();
    }

    @PostMapping("changeMaterial")
    @OperationLog("变更物料")
    public Result changeMaterial(@RequestParam("Id") Long Id,@RequestParam("materialId") Long materialId){
        logger.info("======== DeliveryOrderController.changeMaterial() ========");

        this.deliveryOrderService.changeMaterial(Id,materialId,getUser());

        return new Result();
    }

    @PostMapping("synU8SaleOutStock")
    public Result synU8SaleOutStock(@RequestBody Long[] deliveryOrderIds) {
        return this.deliveryOrderService.synU8SaleOutStock(deliveryOrderIds, getUser());
    }
}
