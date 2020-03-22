package com.xchinfo.erp.controller.scm.wms;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.ReceiveWorkOrderEO;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.wms.service.ReceiveWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.List;

/**
 * @author zhongy
 * @date 2019/12/16
 */
@RestController
@RequestMapping("/wms/receiveWorkOrder")
public class ReceiveWorkOrderController extends BaseController {

    @Autowired
    private ReceiveWorkOrderService receiveWorkOrderService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<ReceiveWorkOrderEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== ReceiveWorkOrderController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("u.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ReceiveWorkOrderEO> page = this.receiveWorkOrderService.selectPage(criteria);

        return new Result<IPage<ReceiveWorkOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ReceiveWorkOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== ReceiveWorkOrderController.info(entity => "+id+") ========");
        ReceiveWorkOrderEO entity = this.receiveWorkOrderService.getById(id);
        return new Result<ReceiveWorkOrderEO>().ok(entity);
    }


    /**
     * 新增单个入库工单明细
     *
     * @param workOrderDetail
     * @return
     */
    @PostMapping("addDetail")
    public Result addDetail(@RequestBody WorkOrderDetailEO workOrderDetail){
        logger.info("======== ReceiveWorkOrderController.addDetail ========");
        return this.receiveWorkOrderService.addDetail(workOrderDetail, getUser());
    }

    /**
     * 新增单个临时入库工单明细
     *
     * @param workOrderDetail
     * @return
     */
    @PostMapping("addTempDetail")
    public Result addTempDetail(@RequestBody WorkOrderDetailEO workOrderDetail){
        logger.info("======== ReceiveWorkOrderController.addTempDetail ========");
        return this.receiveWorkOrderService.addTempDetail(workOrderDetail, getUser());
    }

    /**
     * 批量新增入库工单
     *
     * @param receiveWorkOrders
     * @return
     */
    @PostMapping("addBatch")
    public Result addBatch(@RequestBody List<ReceiveWorkOrderEO> receiveWorkOrders){
        logger.info("======== ReceiveWorkOrderController.addBatch ========");
        return this.receiveWorkOrderService.addBatch(receiveWorkOrders, getUser());
    }
}
