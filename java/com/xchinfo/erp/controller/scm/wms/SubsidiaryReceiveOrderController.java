package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderEO;
import com.xchinfo.erp.wms.service.SubsidiaryReceiveOrderService;
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
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/receiveConsumed")
public class SubsidiaryReceiveOrderController extends BaseController {
    @Autowired
    private SubsidiaryReceiveOrderService subsidiaryReceiveOrderService;



    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<IPage<SubsidiaryReceiveOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SubsidiaryReceiveOrderController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<SubsidiaryReceiveOrderEO> page = this.subsidiaryReceiveOrderService.selectPage(criteria);

        return new Result<IPage<SubsidiaryReceiveOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<SubsidiaryReceiveOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== SubsidiaryReceiveOrderController.info(entity => "+id+") ========");

        SubsidiaryReceiveOrderEO entity = this.subsidiaryReceiveOrderService.getById(id);

        return new Result<SubsidiaryReceiveOrderEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<List<SubsidiaryReceiveOrderDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== SubsidiaryReceiveOrderController.info(listDetails => "+orderId+") ========");

        List<SubsidiaryReceiveOrderDetailEO> details = this.subsidiaryReceiveOrderService.listDetailsByOrder(orderId);

        return new Result<List<SubsidiaryReceiveOrderDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建入库订单")
    @RequiresPermissions("wms:receiveConsumed:create")
    public Result create(@RequestBody SubsidiaryReceiveOrderEO entity){
        logger.info("======== SubsidiaryReceiveOrderController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        SubsidiaryReceiveOrderEO SubsidiaryReceiveOrderEO = this.subsidiaryReceiveOrderService.saveEntity(entity);

        return new Result<SubsidiaryReceiveOrderEO>().ok(SubsidiaryReceiveOrderEO);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新入库订单")
    @RequiresPermissions("wms:receiveConsumed:update")
    public Result update(@RequestBody SubsidiaryReceiveOrderEO entity){
        logger.info("======== SubsidiaryReceiveOrderController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.subsidiaryReceiveOrderService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除入库订单")
    @RequiresPermissions("wms:receiveConsumed:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryReceiveOrderController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderService.removeByIds(ids);

        return new Result();
    }


    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除入库订单明细")
    @RequiresPermissions("wms:receiveConsumed:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== SubsidiaryReceiveOrderController.deleteDetail() ========");

        this.subsidiaryReceiveOrderService.deleteDetailById(id);

        return new Result();
    }

    /**
     * 明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<IPage<SubsidiaryReceiveOrderDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== SubsidiaryReceiveOrderController.list() ========");

        IPage<SubsidiaryReceiveOrderDetailEO> page = this.subsidiaryReceiveOrderService.selectDetailPage(criteria);

        return new Result<IPage<SubsidiaryReceiveOrderDetailEO>>().ok(page);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail")
    @OperationLog("删除入库订单明细")
    @RequiresPermissions("wms:receiveConsumed:delete")
    public Result deleteDetail(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryReceiveOrderController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderService.removeByDetailIds(ids);

        return new Result();
    }


    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<SubsidiaryReceiveOrderDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== SubsidiaryReceiveOrderController.info(entity => "+id+") ========");

        SubsidiaryReceiveOrderDetailEO entity = this.subsidiaryReceiveOrderService.getDetailById(id);

        return new Result<SubsidiaryReceiveOrderDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping("detail")
    @OperationLog("创建入库明细")
    @RequiresPermissions("wms:receiveConsumed:create")
    public Result createDetail(@RequestBody SubsidiaryReceiveOrderDetailEO entity){
        logger.info("======== SubsidiaryReceiveOrderController.createDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.subsidiaryReceiveOrderService.saveDetail(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("detail")
    @OperationLog("更新入库明细")
    @RequiresPermissions("wms:receiveConsumed:update")
    public Result updateDetail(@RequestBody SubsidiaryReceiveOrderDetailEO entity){
        logger.info("======== SubsidiaryReceiveOrderController.updateDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.subsidiaryReceiveOrderService.updateDetailById(entity);

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
    @RequiresPermissions("wms:receiveConsumed:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryReceiveOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderService.updateStatusById(ids,1,0);

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
    @RequiresPermissions("wms:receiveConsumed:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryReceiveOrderController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderService.updateStatusById(ids,0,1);

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
    @RequiresPermissions("wms:receiveConsumed:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== SubsidiaryReceiveOrderController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderService.updateStatusById(ids,2,1);

        return new Result();
    }

    /***
     * 通过流水获取入库单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    @RequiresPermissions("wms:receiveConsumed:info")
    public Result<SubsidiaryReceiveOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== SubsidiaryReceiveOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        SubsidiaryReceiveOrderEO entity = this.subsidiaryReceiveOrderService.getDetailInfoByNo(voucherNo);
        return new Result<SubsidiaryReceiveOrderEO>().ok(entity);
    }


    /**
     * 单个收货
     * @param
     * @return
     */
    @PostMapping("app/receive")
    @OperationLog("零件入库")
    @RequiresPermissions("wms:receiveConsumed:update")
    public Result receiveOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== SubsidiaryReceiveOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.subsidiaryReceiveOrderService.receiveOne(Id,amount,action,userId,userName);

        return new Result();
    }



}
