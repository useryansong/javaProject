package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.PbomEO;
import com.xchinfo.erp.bsc.service.PbomService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryEO;
import com.xchinfo.erp.scm.wms.entity.LinedgeInventoryMaterialAssignmentEO;
import com.xchinfo.erp.scm.wms.entity.TempInventoryEO;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.LinedgeInventoryMaterialAssignmentService;
import com.xchinfo.erp.wms.service.LinedgeInventoryService;
import com.xchinfo.erp.wms.service.TempInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


/**
 * @author roman.c
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/wms/linedgeInventory")
public class LinedgeInventoryController extends BaseController {

    @Autowired
    private LinedgeInventoryService linedgeInventoryService;


    @Autowired
    private PbomService pbomService;

    @Autowired
    private TempInventoryService tempInventoryService;

    @Autowired
    private LinedgeInventoryMaterialAssignmentService linedgeInventoryMaterialAssignmentService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<LinedgeInventoryEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<LinedgeInventoryEO> page = this.linedgeInventoryService.selectPage(criteria);

        return new Result<IPage<LinedgeInventoryEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<LinedgeInventoryEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialPlanController.info(entity => "+id+") ========");

        LinedgeInventoryEO entity = this.linedgeInventoryService.getById(id);

        return new Result<LinedgeInventoryEO>().ok(entity);
    }


    /**
     * 盘点明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    public Result<IPage<LinedgeInventoryDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        IPage<LinedgeInventoryDetailEO> page = this.linedgeInventoryService.selectDetailPage(criteria);

        return new Result<IPage<LinedgeInventoryDetailEO>>().ok(page);
    }

    /**
     * 临时盘点明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("tempDetail/page")
    public Result<IPage<TempInventoryEO>> tempDetailPage(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        IPage<TempInventoryEO> page = this.tempInventoryService.selectTempDetailPage(criteria);

        return new Result<IPage<TempInventoryEO>>().ok(page);
    }

    /**
     * pbnm原材料物料分解分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("orginalDetail/page")
    public Result<IPage<PbomEO>> orginalDetailPage(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        IPage<PbomEO> page = this.pbomService.selectPage(criteria);

        return new Result<IPage<PbomEO>>().ok(page);
    }

    /**
     * 盘点总明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("allPbom/page")
    public Result<IPage<LinedgeInventoryMaterialAssignmentEO>> allPbomPage(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnController.list() ========");

        IPage<LinedgeInventoryMaterialAssignmentEO> page = this.linedgeInventoryMaterialAssignmentService.selectAllPage(criteria);

        return new Result<IPage<LinedgeInventoryMaterialAssignmentEO>>().ok(page);
    }

    /**
     * 查找需打印的订单明细
     *
     * @param id
     * @return
     */
    @PostMapping("exportDate")
    public Result<List<LinedgeInventoryMaterialAssignmentEO>> exportDate(@RequestParam("id") Long id){
        logger.info("======== ReceiveOrderController.info(listDetails => "+id+") ========");

        List<LinedgeInventoryMaterialAssignmentEO> details = this.linedgeInventoryMaterialAssignmentService.listexportDateByInventory(id);

        return new Result<List<LinedgeInventoryMaterialAssignmentEO>>().ok(details);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @return
     */
    @PostMapping("exports")
    @OperationLog("导出")
    //@RequiresPermissions("basic:machine:export")
    public Result exportMaterialDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody LinedgeInventoryMaterialAssignmentEO[] linedgeInventoryMaterialAssignmentEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("linedgeMaterialDetail.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(linedgeInventoryMaterialAssignmentEOS),jsonObject);

        return new Result();
    }

    /**
     * 查找需打印的所有订单
     *
     * @param id
     * @return
     */
    @PostMapping("exportDateAll")
    public Result<List<LinedgeInventoryMaterialAssignmentEO>> exportDateAll(@RequestParam("id") Long id){
        logger.info("======== ReceiveOrderController.info(listDetails => "+id+") ========");

        List<LinedgeInventoryMaterialAssignmentEO> details = this.linedgeInventoryMaterialAssignmentService.listexportDateByInventoryAll(id);

        return new Result<List<LinedgeInventoryMaterialAssignmentEO>>().ok(details);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    //@RequiresPermissions("basic:machine:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody LinedgeInventoryMaterialAssignmentEO[] linedgeInventoryMaterialAssignmentEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("linedgeInventoryDetail.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(linedgeInventoryMaterialAssignmentEOS),jsonObject);

        return new Result();
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request){
        logger.info("======== WarehouseAreaController.import ========");
        List list = ExcelUtils.getExcelData(request);
        String inventoryMonth = request.getParameter("inventoryMonth");
        this.linedgeInventoryService.importFromExcel(list,inventoryMonth);
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
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.linedgeInventoryService.updateStatusById(ids,1,2);

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
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.linedgeInventoryService.updateStatusById(ids,2,1);

        return new Result();
    }


    /**
     * 新增盘点
     */
    @PostMapping("begin")
    @OperationLog("新增盘点")
    public Result beginInventory(@RequestParam("inventoryDate") String inventoryDate){
        logger.info("======== InventoryController.addBatch ========");

        this.linedgeInventoryService.beginInventory(inventoryDate, getUser());

        return new Result();
    }

    /**
     * 盘点完成
     *
     * @return
     */
    @PostMapping("finish")
    @OperationLog("盘点完成")
    public Result finishInventory(@RequestParam("id") Long id){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.linedgeInventoryService.finishInventory(getUser(),id);

        return new Result();
    }

    /**
     * 根据物料零件号查找线边盘点单和物料明细的相关信息（临时盘点表,盲盘）
     *
     * @param
     * @return
     */
    @GetMapping("app/inventory/{id}")
    public Result<MaterialEO> materialInfo(@PathVariable("id") String elementNo){
        logger.info("======== InventoryController.inventoryDetailInfo(entity => "+elementNo+") ========");

        MaterialEO entity = this.linedgeInventoryService.getMaterialInfo(elementNo,getOrgId());

        return new Result<MaterialEO>().ok(entity);
    }

    /**
     * 盲盘
     * @param
     * @return
     */
    @PostMapping("app/inventory/blind")
    @OperationLog("盲盘/撤回")
    public Result inventorBlindyOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("locationId") Long locationId,@RequestParam("action") String action,@RequestParam("inventoryNo") String inventoryNo){
        logger.info("======== DeliveryOrderController.inventorBlindyOne() ========");

        this.linedgeInventoryService.inventorBlindyOne(Id,amount,locationId,action,getUser(),inventoryNo);

        return new Result();
    }

}


