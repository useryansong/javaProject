package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.service.ReturnOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.mybatis.utils.Criteria;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@RestController
@RequestMapping("/srm/returnOrder")
public class ReturnOrderController extends BaseController {

    @Autowired
    private ReturnOrderService returnOrderService;


    /**
     * 确认
     * @param ids
     * @return
     */
    @PostMapping("confirm")
    @OperationLog("确认")
    public Result confirm(@RequestBody Long[] ids){
        logger.info("======== ReturnOrderController.confirm ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        UserEO user = super.getUser();
        this.returnOrderService.confirm(ids, user);
        return new Result();
    }



    /***
     * 通过流水获取退货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    public Result<ReturnOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== ReturnOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        ReturnOrderEO entity = this.returnOrderService.getDetailInfoByNo(voucherNo);
        return new Result<ReturnOrderEO>().ok(entity);
    }

    /**
     * 单个退货（采购，委外）
     * @param
     * @return
     */
    @PostMapping("app/return")
    @OperationLog("退货(采购，委外)")
    public Result returnOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== ReturnOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.returnOrderService.returnOne(Id,amount,userId,userName,action);

        return new Result();
	}

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ReturnOrderEO> info(@PathVariable("id") Long id) {
        logger.info("======== ReturnOrderController.info(entity => " + id + ") ========");
        ReturnOrderEO entity = this.returnOrderService.getById(id);
        return new Result<ReturnOrderEO>().ok(entity);
    }

    @PostMapping("page")
    public Result<IPage<ReturnOrderEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== ReturnOrderController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        map.put("orgId", getOrgId());
        List<ReturnOrderEO> totalList = this.returnOrderService.getPage(map);
        map.put("currentIndexFlag", true);
        List<ReturnOrderEO> pageList = this.returnOrderService.getPage(map);
        IPage<ReturnOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<ReturnOrderEO>>().ok(page);
    }

    /**
     * 物料按库位分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("checkStock/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<StockAccountEO>> checkStockByLocation(@RequestBody Criteria criteria){
        logger.info("======== ReturnOrderController.checkStockByLocation() ========");

        IPage<StockAccountEO> page = this.returnOrderService.checkStockByLocation(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }


    /**
     * 按库位单个发货（采购，委外）
     * @param
     * @return
     */
    @PostMapping("appLocation/return")
    @OperationLog("成品发货")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result returnOneByLocation(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action,@RequestParam("locationId") Long locationId,@RequestParam("stockFinishFlag") Long stockFinishFlag){
        logger.info("======== ReturnOrderController.returnOneByLocation() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.returnOrderService.returnOneByLocation(Id,amount,userId,userName,action,locationId,stockFinishFlag);

        return new Result();
    }
}
