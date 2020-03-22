package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.srm.service.DeliveryPlanService;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author zhongye
 * @date 2019/5/13
 */
@RestController
@RequestMapping("/srm/supplierDeliveryPlan")
public class SupplierDeliveryPlanController extends BaseController {

    @Autowired
    private DeliveryPlanService deliveryPlanService;

    @Autowired
    private SupplierService supplierService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:supplierDeliveryPlan:info")
    public Result<IPage<DeliveryPlanEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SupplierDeliveryPlanController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("dp.supplier_id");
        criterion.setOp("eq");

        // 供应商用户的用户名为供应商编码
        if(getUser().getSupplierId() == null) {
            SupplierEO supplier = this.supplierService.getBySupplierCode(getUserName());
            if(supplier == null) {
                throw new BusinessException("用户不存在供应商!");
            }
            criterion.setValue(supplier.getSupplierId()+"");
        } else {
            criterion.setValue(getUser().getSupplierId()+"");
        }

        criteria.getCriterions().add(criterion);

        IPage<DeliveryPlanEO> page = this.deliveryPlanService.selectPage(criteria);
        return new Result<IPage<DeliveryPlanEO>>().ok(page);
    }

    /**
     * 供应商确认
     * @param ids
     * @return
     */
    @PostMapping("supplierConfirm")
    @OperationLog("供应商确认")
    @RequiresPermissions("srm:supplierDeliveryPlan:supplierConfirm")
    public Result supplierConfirm(@RequestBody Long[] ids){
        logger.info("======== SupplierDeliveryPlanController.supplierConfirm ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryPlanService.supplierConfirm(ids, userId, userName);
        return new Result();
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param DeliveryPlanEOs 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出报表")
    //@RequiresPermissions("srm:supplierDeliveryPlan:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody DeliveryPlanEO[] DeliveryPlanEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("supplier_delivery_plan.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(DeliveryPlanEOs), jsonObject);

        return new Result();
    }
}
