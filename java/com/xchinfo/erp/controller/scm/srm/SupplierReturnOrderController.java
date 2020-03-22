package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.srm.service.ReturnOrderService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/6/11
 */
@RestController
@RequestMapping("/srm/supplierReturnOrder")
public class SupplierReturnOrderController extends BaseController {

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private SupplierService supplierService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:supplierReturnOrder:info")
    public Result<IPage<ReturnOrderEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== SupplierReturnOrderController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        if(getUser().getSupplierId() == null) {
            // 供应商用户的用户名为供应商编码
            SupplierEO supplier = this.supplierService.getBySupplierCode(getUserName());
            if(supplier == null) {
                throw new BusinessException("用户不存在供应商!");
            }
            map.put("supplierId", supplier.getSupplierId()+"");
        } else {
            map.put("supplierId", getUser().getSupplierId()+"");
        }

        List<ReturnOrderEO> totalList = this.returnOrderService.getPage(map);
        map.put("currentIndexFlag", true);
        List<ReturnOrderEO> pageList = this.returnOrderService.getPage(map);
        IPage<ReturnOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<ReturnOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ReturnOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== SupplierDeliveryNoteController.info(entity => "+id+") ========");
        ReturnOrderEO entity = this.returnOrderService.getById(id);
        return new Result<ReturnOrderEO>().ok(entity);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param ReturnOrderDetailEOs 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出报表")
    //@RequiresPermissions("srm:supplierDeliveryNote:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody ReturnOrderDetailEO[] ReturnOrderDetailEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("supplier_return_order.json");
        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(ReturnOrderDetailEOs), jsonObject);

        return new Result();
    }
}
