package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.wms.service.SubsidiaryDeliveryOrderService;
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
@RequestMapping("/wms/deliveryConsumed")
public class SubsidiaryDeliveryOrderController extends BaseController {
    @Autowired
    private SubsidiaryDeliveryOrderService subsidiaryDeliveryOrderService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:deliveryConsumed:info")
    public Result<IPage<SubsidiaryDeliveryOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SubsidiaryDeliveryOrderController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<SubsidiaryDeliveryOrderEO> page = this.subsidiaryDeliveryOrderService.selectPage(criteria);
        return new Result<IPage<SubsidiaryDeliveryOrderEO>>().ok(page);
    }

    /**
     * 查询所有出库单
     * @return
     */
    @GetMapping("list")
    public Result<List<SubsidiaryDeliveryOrderEO>> list(){
        logger.info("======== SubsidiaryDeliveryOrderController.list() ========");
        List<SubsidiaryDeliveryOrderEO> deliveryOrders = this.subsidiaryDeliveryOrderService.listAll();
        return new Result<List<SubsidiaryDeliveryOrderEO>>().ok(deliveryOrders);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:deliveryConsumed:info")
    public Result<SubsidiaryDeliveryOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== SubsidiaryDeliveryOrderController.info(entity => "+id+") ========");
        SubsidiaryDeliveryOrderEO entity = this.subsidiaryDeliveryOrderService.getById(id);
        return new Result<SubsidiaryDeliveryOrderEO>().ok(entity);
    }

    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单")
    @RequiresPermissions("wms:deliveryConsumed:create")
    public Result create(@RequestBody SubsidiaryDeliveryOrderEO entity){
        logger.info("======== SubsidiaryDeliveryOrderController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
//        this.subsidiaryDeliveryOrderService.save(entity);
        SubsidiaryDeliveryOrderEO entityFromDb = this.subsidiaryDeliveryOrderService.saveEntity(entity);
        return new Result<SubsidiaryDeliveryOrderEO>().ok(entityFromDb);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单")
    @RequiresPermissions("wms:deliveryConsumed:update")
    public Result update(@RequestBody SubsidiaryDeliveryOrderEO entity){
        logger.info("======== SubsidiaryDeliveryOrderController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.subsidiaryDeliveryOrderService.updateById(entity);
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除出库单")
    @RequiresPermissions("wms:deliveryConsumed:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryDeliveryOrderController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.subsidiaryDeliveryOrderService.removeByIds(ids);
        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除出库订单明细")
    @RequiresPermissions("wms:deliveryConsumed:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== SubsidiaryDeliveryOrderController.deleteDetail() ========");

        this.subsidiaryDeliveryOrderService.deleteDetailById(id);

        return new Result();
    }

    /**
     * 查询出库单明细
     *
     * @param deliveryId
     * @return
     */
    @GetMapping("listDetails/{deliveryId}")
    @RequiresPermissions("wms:deliveryConsumed:info")
    public Result<List<SubsidiaryDeliveryOrderDetailEO>> listDetails(@PathVariable("deliveryId") Long deliveryId){
        logger.info("======== SubsidiaryDeliveryOrderController.info(listDetails => "+deliveryId+") ========");
        List<SubsidiaryDeliveryOrderDetailEO> details = this.subsidiaryDeliveryOrderService.listDetailsByDelivery(deliveryId);
        return new Result<List<SubsidiaryDeliveryOrderDetailEO>>().ok(details);
    }

    /**
     * 设置状态（发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/release")
    @OperationLog("设置状态-发布")
    @RequiresPermissions("wms:deliveryConsumed:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryDeliveryOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryDeliveryOrderService.updateStatusById(ids,1,0);

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
    @RequiresPermissions("wms:deliveryConsumed:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryDeliveryOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryDeliveryOrderService.updateStatusById(ids,0,1);

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
    @RequiresPermissions("wms:deliveryConsumed:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryDeliveryOrderController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryDeliveryOrderService.updateStatusById(ids,2,1);

        return new Result();
    }


    /***
     * 通过流水获取出货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    @RequiresPermissions("wms:deliveryConsumed:info")
    public Result<SubsidiaryDeliveryOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== SubsidiaryDeliveryOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        SubsidiaryDeliveryOrderEO entity = this.subsidiaryDeliveryOrderService.getDetailInfoByNo(voucherNo);
        return new Result<SubsidiaryDeliveryOrderEO>().ok(entity);
    }


    /**
     * 单个发货
     * @param
     * @return
     */
    @PostMapping("app/delivery")
    @OperationLog("成品发货")
    @RequiresPermissions("wms:deliveryConsumed:update")
    public Result deliveryOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== SubsidiaryDeliveryOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.subsidiaryDeliveryOrderService.deliveryOne(Id,amount,action,userId,userName);

        return new Result();
    }

    /**
     * 查找所有符合条件的出库单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("getList")
    @RequiresPermissions("wms:deliveryConsumed:info")
    public Result<List<SubsidiaryDeliveryOrderEO>> getList(@RequestBody Criteria criteria) {
        logger.info("======== SubsidiaryDeliveryOrderController.getList() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<SubsidiaryDeliveryOrderEO> list = this.subsidiaryDeliveryOrderService.getList(map);
        return new Result<List<SubsidiaryDeliveryOrderEO>>().ok(list);
    }
}
