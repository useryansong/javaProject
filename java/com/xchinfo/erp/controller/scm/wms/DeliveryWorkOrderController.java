package com.xchinfo.erp.controller.scm.wms;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.wms.service.DeliveryWorkOrderService;
import com.xchinfo.erp.wms.service.ReceiveWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/12/16
 */
@RestController
@RequestMapping("/wms/deliveryWorkOrder")
public class DeliveryWorkOrderController extends BaseController {

    @Autowired
    private DeliveryWorkOrderService deliveryWorkOrderService;

    @Autowired
    private ScheduleOrderService scheduleOrderService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<DeliveryWorkOrderEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== DeliveryWorkOrderController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("u.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<DeliveryWorkOrderEO> page = this.deliveryWorkOrderService.selectPage(criteria);

        return new Result<IPage<DeliveryWorkOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DeliveryWorkOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryWorkOrderController.info(entity => "+id+") ========");
        DeliveryWorkOrderEO entity = this.deliveryWorkOrderService.getById(id);
        return new Result<DeliveryWorkOrderEO>().ok(entity);
    }

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("listAll")
    public Result<List<DeliveryWorkOrderEO>> listAll(@RequestBody Criteria criteria) {
        logger.info("======== DeliveryWorkOrderController.list() ========");

        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUser().getUserId());

        List<DeliveryWorkOrderEO> list = this.deliveryWorkOrderService.listAll(map);

        return new Result<List<DeliveryWorkOrderEO>>().ok(list);
    }

    /**
     * 根据ID查找
     *
     * @param deliveryWorkOrders
     * @return
     */
    @PostMapping("addBatch")
    public Result<ScheduleOrderEO> addBatch(@RequestBody List<DeliveryWorkOrderEO> deliveryWorkOrders){
        logger.info("======== DeliveryWorkOrderController.addBatch ========");
        this.deliveryWorkOrderService.addBatch(deliveryWorkOrders, getUser());
        ScheduleOrderEO scheduleOrder = null;
        if (deliveryWorkOrders.get(0).getRelationId()>0){
            if(deliveryWorkOrders!=null && deliveryWorkOrders.size()>0) {
                scheduleOrder = this.scheduleOrderService.getWorkOrderById(deliveryWorkOrders.get(0).getRelationId());
            }
            return new Result<ScheduleOrderEO>().ok(scheduleOrder);
        }else{
            return new Result();
        }
    }

    /**
     * 新增单个临时出库工单明细
     *
     * @param workOrderDetail
     * @return
     */
    @PostMapping("addTempDetail")
    public Result addTempDetail(@RequestBody WorkOrderDetailEO workOrderDetail){
        logger.info("======== DeliveryWorkOrderController.addTempDetail ========");
        return this.deliveryWorkOrderService.addTempDetail(workOrderDetail, getUser());
    }

    /**
     * 新增单个出库工单明细
     *
     * @param workOrderDetail
     * @return
     */
    @PostMapping("addDetail")
    public Result addDetail(@RequestBody WorkOrderDetailEO workOrderDetail){
        logger.info("======== DeliveryWorkOrderController.addDetail ========");
        return this.deliveryWorkOrderService.addDetail(workOrderDetail, getUser());
    }

    /**
     * 关闭
     *
     * @param workOrderId
     * @return
     */
    @GetMapping("closeByWorkOrderId")
    public Result closeByWorkOrderId(@RequestParam("workOrderId") String workOrderId){
        logger.info("======== DeliveryWorkOrderController.closeByWorkOrderId ========");
        return this.deliveryWorkOrderService.closeByWorkOrderId(workOrderId, getUser());
    }

    /**
     * 催料
     *
     * @param workOrderId
     * @return
     */
    @GetMapping("urgeByWorkOrderId")
    public Result urgeByWorkOrderId(@RequestParam("workOrderId") String workOrderId){
        logger.info("======== DeliveryWorkOrderController.urgeByWorkOrderId ========");
        return this.deliveryWorkOrderService.urgeByWorkOrderId(workOrderId, getUser());
    }
}
