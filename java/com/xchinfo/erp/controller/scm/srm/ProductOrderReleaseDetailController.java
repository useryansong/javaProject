package com.xchinfo.erp.controller.scm.srm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ProductOrderReleaseDetailEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.srm.service.ProductOrderReleaseDetailService;
import com.xchinfo.erp.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2020/1/9
 */
@RestController
@RequestMapping("/srm/productOrderReleaseDetail")
public class ProductOrderReleaseDetailController extends BaseController {

    @Autowired
    private ProductOrderReleaseDetailService productOrderReleaseDetailService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<ProductOrderReleaseDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderReleaseDetailController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId());

        List<ProductOrderReleaseDetailEO> list = this.productOrderReleaseDetailService.getList(map);
        IPage<ProductOrderReleaseDetailEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());

        return new Result<IPage<ProductOrderReleaseDetailEO>>().ok(page);
    }

    /**
     * 获取最小日期跟最大日期
     * @return
     */
    @GetMapping("getMinAndMaxPlanArriveDate")
    public Result getMinAndMaxPlanArriveDate(@RequestParam("type") Integer type){
        logger.info("======== ProductOrderReleaseDetailController.getMinAndMaxPlanArriveDate() ========");

        return this.productOrderReleaseDetailService.getMinAndMaxPlanArriveDate(type);
    }

    /**
     * 按照物料汇总
     * @param criteria
     * @return
     */
    @PostMapping("getSummary")
    public Result getSummary(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderReleaseDetailController.getSummary() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId());
        return this.productOrderReleaseDetailService.getSummary(map);
    }

    /**
     * 发布为采购订单/委外订单
     * @return
     */
    @PostMapping("releaseToPurchaseOrder")
    public Result releaseToPurchaseOrder(@RequestBody PurchaseOrderEO[] purchaseOrders, @RequestParam("type") Integer type){
        logger.info("======== ProductOrderReleaseDetailController.releaseToPurchaseOrder() ========");
        return this.productOrderReleaseDetailService.releaseToPurchaseOrder(Arrays.asList(purchaseOrders), getUser(), type);
    }

    /**
     * 修改状态
     * @return
     */
    @PostMapping("updateStatusByIds")
    public Result updateStatusByIds(@RequestBody Long[] productOrderReleaseDetailIds, @RequestParam("status") Integer status){
        logger.info("======== ProductOrderReleaseDetailController.updateStatusByIds() ========");
        this.productOrderReleaseDetailService.updateStatusByIds(Arrays.asList(productOrderReleaseDetailIds), status);
        return new Result();
    }
}
