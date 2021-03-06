package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.wms.service.ReceiveOrderService;
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
@RequestMapping("/wms/receiveProduct")
public class ReceiveProductController extends BaseController {
    @Autowired
    private ReceiveOrderService receiveOrderService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<IPage<ReceiveOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ReceiveProductController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ReceiveOrderEO> page = this.receiveOrderService.selectPage(criteria);

        return new Result<IPage<ReceiveOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<ReceiveOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== ReceiveProductController.info(entity => "+id+") ========");

        ReceiveOrderEO entity = this.receiveOrderService.getById(id);

        return new Result<ReceiveOrderEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<List<ReceiveOrderDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== ReceiveProductController.info(listDetails => "+orderId+") ========");

        List<ReceiveOrderDetailEO> details = this.receiveOrderService.listDetailsByOrder(orderId);

        return new Result<List<ReceiveOrderDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建入库订单")
    //@RequiresPermissions("wms:receiveOrder:create")
    public Result create(@RequestBody ReceiveOrderEO entity){
        logger.info("======== ReceiveProductController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        ReceiveOrderEO receiveOrderEO = this.receiveOrderService.saveEntity(entity);

        return new Result<ReceiveOrderEO>().ok(receiveOrderEO);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新入库订单")
    //@RequiresPermissions("wms:receiveOrder:update")
    public Result update(@RequestBody ReceiveOrderEO entity){
        logger.info("======== ReceiveProductController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.receiveOrderService.updateById(entity,getUserId());

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
    //@RequiresPermissions("wms:receiveOrder:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ReceiveProductController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.removeByIds(ids);

        return new Result();
    }


    /**
     * 明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<IPage<ReceiveOrderDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== ReceiveProductController.list() ========");

        IPage<ReceiveOrderDetailEO> page = this.receiveOrderService.selectDetailPage(criteria);

        return new Result<IPage<ReceiveOrderDetailEO>>().ok(page);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail")
    @OperationLog("删除入库订单明细")
    //@RequiresPermissions("wms:receiveOrder:delete")
    public Result deleteDetail(@RequestBody Long[] ids){
        logger.info("======== ReceiveProductController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.removeByDetailIds(ids);

        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除入库订单明细")
    //@RequiresPermissions("wms:receiveOrder:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== ReceiveProductController.deleteDetail() ========");

        this.receiveOrderService.deleteDetailById(id);

        return new Result();
    }


    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<ReceiveOrderDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== ReceiveProductController.info(entity => "+id+") ========");

        ReceiveOrderDetailEO entity = this.receiveOrderService.getDetailById(id);

        return new Result<ReceiveOrderDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping("detail")
    @OperationLog("创建入库明细")
    //@RequiresPermissions("wms:receiveOrder:create")
    public Result createDetail(@RequestBody ReceiveOrderDetailEO entity){
        logger.info("======== ReceiveProductController.createDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.receiveOrderService.saveDetail(entity);

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
    //@RequiresPermissions("wms:receiveOrder:update")
    public Result updateDetail(@RequestBody ReceiveOrderDetailEO entity){
        logger.info("======== ReceiveProductController.updateDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.receiveOrderService.updateDetailById(entity);

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
    //@RequiresPermissions("wms:receiveOrder:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== ReceiveProductController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,1,0);

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
    //@RequiresPermissions("wms:receiveOrder:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== ReceiveProductController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,0,1);

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
    //@RequiresPermissions("wms:receiveOrder:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== ReceiveProductController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,2,1);

        return new Result();
    }

    /***
     * 通过流水获取入库单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("wms:deliveryOrder:info")
    public Result<ReceiveOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== ReceiveProductController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        ReceiveOrderEO entity = this.receiveOrderService.getDetailInfoByNo(voucherNo);
        return new Result<ReceiveOrderEO>().ok(entity);
    }


    /**
     * 单个收货
     * @param
     * @return
     */
    @PostMapping("app/receive")
    @OperationLog("零件入库")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result receiveOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== ReceiveProductController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.receiveOrderService.receiveOne(Id,amount,action,userId,userName);

        return new Result();
    }
}
