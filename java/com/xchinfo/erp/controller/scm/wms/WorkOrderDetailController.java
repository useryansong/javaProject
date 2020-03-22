package com.xchinfo.erp.controller.scm.wms;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.WorkOrderDetailEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.wms.service.WorkOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/12/16
 */
@RestController
@RequestMapping("/wms/workOrderDetail")
public class WorkOrderDetailController extends BaseController {

    @Autowired
    private WorkOrderDetailService workOrderDetailService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<WorkOrderDetailEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== WorkOrderDetailController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId());
        List<WorkOrderDetailEO> list = this.workOrderDetailService.getPage(map);
        IPage<WorkOrderDetailEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());
        return new Result<IPage<WorkOrderDetailEO>>().ok(page);
    }

    /**
     * 分页查找(按物料查找库存)
     *
     * @param criteria
     * @return
     */
    @PostMapping("getAmountPage")
    public Result<IPage<WorkOrderDetailEO>> getAmountPage(@RequestBody Criteria criteria) {
        logger.info("======== WorkOrderDetailController.getAmountPage() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId());
        List<WorkOrderDetailEO> list = this.workOrderDetailService.getAmountPage(map);
        IPage<WorkOrderDetailEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());
        return new Result<IPage<WorkOrderDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param materialId
     * @return
     */
    @GetMapping("getWorkOrderDetails")
    public Result<WorkOrderDetailEO> getWorkOrderDetails(@RequestParam("materialId") Long materialId){
        logger.info("======== WorkOrderDetailController.getWorkOrderDetails => "+materialId+") ========");
        WorkOrderDetailEO entity = this.workOrderDetailService.getWorkOrderDetails(materialId);
        return new Result<WorkOrderDetailEO>().ok(entity);
    }

    /**
     * 移库
     *
     * @param
     * @return
     */
    @PostMapping("transfer")
    public Result transfer(@RequestBody WorkOrderDetailEO workOrderDetail) {
        logger.info("======== WorkOrderDetailController.transfer() ========");

        this.workOrderDetailService.transfer(workOrderDetail, getUser());

        return new Result();
    }

    /**
     * 查看库位物料
     *
     * @param
     * @return
     */
    @PostMapping("getByWarehouseLocationBarCode")
    public Result getByWarehouseLocationBarCode(@RequestParam("warehouseLocationBarCode") String warehouseLocationBarCode) {
        logger.info("======== WorkOrderDetailController.getByWarehouseLocationBarCode() ========");
        List<WorkOrderDetailEO> list = this.workOrderDetailService.getByWarehouseLocationBarCode(warehouseLocationBarCode);
        return new Result<List<WorkOrderDetailEO>>().ok(list);
    }
}
