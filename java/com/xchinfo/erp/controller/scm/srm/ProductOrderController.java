package com.xchinfo.erp.controller.scm.srm;

/**
 * @author zhongy
 * @date 2019/9/11
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.srm.service.ProductOrderService;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/5/9
 */
@RestController
@RequestMapping("/srm/productOrder")
public class ProductOrderController extends BaseController {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private ScheduleOrderService scheduleOrderService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:productOrder:info")
    public Result<IPage<ProductOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ProductOrderEO> page = this.productOrderService.selectPage(criteria);
        return new Result<IPage<ProductOrderEO>>().ok(page);
    }

    /**
     * 获取排产单(生产订单详情)
     * @return
     */
    @PostMapping("getScheduleOrder")
    public Result<IPage<ScheduleOrderEO>> getScheduleOrder(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderController.getScheduleOrder() ========");
        IPage<ScheduleOrderEO> page = this.scheduleOrderService.selectPage(criteria);
        return new Result<IPage<ScheduleOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ProductOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductOrderController.info(entity => "+id+") ========");
        ProductOrderEO entity = this.productOrderService.getById(id);
        return new Result<ProductOrderEO>().ok(entity);
    }

    @PostMapping("getWorkingProcedureTime")
    public Result getWorkingProcedureTime(@RequestBody ProductOrderEO productOrder){
        logger.info("======== ProductOrderController.getWorkingProcedureTime ========");
        List<ScheduleOrderEO> scheduleOrders = this.scheduleOrderService.getWorkingProcedureTime(productOrder);
        return new Result<List<ScheduleOrderEO>>().ok(scheduleOrders);
    }

    @PostMapping("addScheduleOrder")
    public Result addScheduleOrder(@RequestBody ScheduleOrderEO scheduleOrder){
        logger.info("======== ProductOrderController.addScheduleOrder(ID => "+scheduleOrder.getId()+") ========");
        ValidatorUtils.validateEntity(scheduleOrder, AddGroup.class, DefaultGroup.class);
        this.scheduleOrderService.saveEntity(scheduleOrder, getUser());
        return new Result();
    }

    @PostMapping("addScheduleOrders")
    public Result addScheduleOrders(@RequestBody ScheduleOrderEO[] scheduleOrders){
        logger.info("======== ProductOrderController.addScheduleOrders ========");
        this.scheduleOrderService.saveEntities(scheduleOrders, getUser());
        return new Result();
    }

    /**
     * 修改单个排产单
     * @return
     */
    @PostMapping("deleteScheduleOrder")
    public Result deleteScheduleOrder(@RequestBody Long[] scheduleOrderIds){
        logger.info("======== ProductOrderController.deleteScheduleOrder ========");
        this.scheduleOrderService.deleteScheduleOrder(scheduleOrderIds);
        return new Result();
    }

    /**
     * 确认生产订单
     * @param ids
     * @return
     */
    @PostMapping("confirm")
    @OperationLog("确认生产订单")
    public Result confirm(@RequestBody Long[] ids){
        logger.info("======== ProductOrderController.confirm() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        return this.productOrderService.confirm(ids, getUser());
    }

    /**
     * 获取单个排产单
     * @return
     */
    @GetMapping("getScheduleOrder/{scheduleOrderById}")
    public Result getScheduleOrderById(@PathVariable("scheduleOrderById") Long scheduleOrderById){
        logger.info("======== ProductOrderController.getScheduleOrderById() ========");
        ScheduleOrderEO scheduleOrder = this.scheduleOrderService.getById(scheduleOrderById);
        return new Result<ScheduleOrderEO>().ok(scheduleOrder);
    }

    /**
     * 修改单个排产单
     * @return
     */
    @PostMapping("updateScheduleOrder")
    public Result updateScheduleOrder(@RequestBody ScheduleOrderEO scheduleOrder){
        logger.info("======== ProductOrderController.updateScheduleOrder() ========");
        this.scheduleOrderService.updateScheduleOrder(scheduleOrder);
        return new Result();
    }

    /**
     * 批量排产
     * @return
     */
    @PostMapping("addBatchScheduleOrder")
    public Result addBatchScheduleOrder(@RequestBody Long[] productOrderIds){
        logger.info("======== ProductOrderController.addBatchScheduleOrder ========");
        this.scheduleOrderService.addBatchScheduleOrder(productOrderIds, getUser());
        return new Result();
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("getPageByParentSerialId")
    public Result<IPage<ProductOrderEO>> getPageByParentSerialId(@RequestBody Criteria criteria,
                                                                  @RequestParam("parentSerialId") Long parentSerialId){
        logger.info("======== ProductOrderController.getPageByParentSerialId() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("parentSerialId", parentSerialId);
        List<ProductOrderEO> list = this.productOrderService.getPageByParentSerialId(map);
        IPage<ProductOrderEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());
        return new Result<IPage<ProductOrderEO>>().ok(page);
    }


    /**
     * 完工日报
     *
     * @param criteria
     * @return
     */
    @PostMapping("produceDoneReport")
    public Result<IPage<ScheduleOrderEO>> selectProduceDoneReport(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderController.selectProduceDoneReport() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ScheduleOrderEO> page = this.scheduleOrderService.selectProduceDoneReport(criteria);

        return new Result<IPage<ScheduleOrderEO>>().ok(page);
    }

    /**
     * 发布物料订单
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布物料订单")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== ProductOrderController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        return this.productOrderService.release(ids, getUser());
    }
}
