package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.StockAccountService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/stockAccount")
public class StockAccountController extends BaseController {

    @Autowired
    private StockAccountService stockAccountService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:stockAccount:info")
    public Result<IPage<StockAccountEO>> page(@RequestBody Criteria criteria){
        logger.info("======== stockAccountController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<StockAccountEO> page = this.stockAccountService.selectPageByView(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }

    /**
     * 查看台账分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    @RequiresPermissions("wms:stockAccount:info")
    public Result<IPage<StockAccountEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== stockAccountController.detailPage() ========");

        IPage<StockAccountEO> page = this.stockAccountService.selectDetailPage(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }

    /**
     * 查看台账查询出入库总数
     *
     * @return
     */
    @PostMapping("detail/count")
    @OperationLog("查询出入库总数")
    @RequiresPermissions("wms:stockAccount:info")
    public Result<StockAccountEO> countNumById(@RequestParam("id") Long id){
        logger.info("======== stockAccountController.countNumById() ========");

        StockAccountEO entity =  this.stockAccountService.countNumById(id);

        return new Result<StockAccountEO>().ok(entity);
    }


    /**
     * 导出报表
     * @param request
     * @param response
     * @param StockAccountEOs 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出报表")
    @RequiresPermissions("wms:stockAccount:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody StockAccountEO[] StockAccountEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("stockAccount.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(StockAccountEOs), jsonObject);

        return new Result();
    }

    /**
     * 辅料台账导出报表
     * @param request
     * @param response
     * @param StockAccountEOs 需要导出的数据
     * @return
     */
    @PostMapping("consumedExport")
    public Result exportMonthS(HttpServletRequest request, HttpServletResponse response, @RequestBody StockAccountEO[] StockAccountEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("consumedAccount.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(StockAccountEOs), jsonObject);

        return new Result();
    }

    /**
     * 物料按库位分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("stockDetail/page")
    public Result<IPage<StockAccountEO>> checkStockByLocation(@RequestBody Criteria criteria){
        logger.info("======== stockAccountController.checkStockByLocation() ========");

        IPage<StockAccountEO> page = this.stockAccountService.checkStockByLocation(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }

    /**
     * 按库位查找物料数量
     *
     * @param criteria
     * @return
     */
    @PostMapping("location/page")
    @RequiresPermissions("wms:stockAccount:info")
    public Result<IPage<StockAccountEO>> locationPage(@RequestBody Criteria criteria){
        logger.info("======== stockAccountController.locationPage() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<StockAccountEO> page = this.stockAccountService.locationPage(criteria);

        return new Result<IPage<StockAccountEO>>().ok(page);
    }

    /**
     * 每天晚上同步库存表voucher_date
     *
     */
    @Scheduled(cron="0 30 23 * * ?")
    public void trySynchro(){
        logger.info("======== 同步库存表voucher_date ========");

        this.stockAccountService.trySynchro();

    }
}
