package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.AdjustInventoryEO;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.InventoryEO;
import com.xchinfo.erp.scm.wms.entity.TempInventoryEO;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.AdjustInventoryService;
import com.xchinfo.erp.wms.service.InventoryDetailService;
import com.xchinfo.erp.wms.service.InventoryService;
import com.xchinfo.erp.wms.service.TempInventoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/inventory")
public class InventoryController extends BaseController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryDetailService inventoryDetailService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private AdjustInventoryService adjustInventoryService;

    @Autowired
    private TempInventoryService tempInventoryService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("wms:inventory:info")
    public Result<IPage<InventoryEO>> page(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        
        IPage<InventoryEO> page = this.inventoryService.selectPage(criteria);

        return new Result<IPage<InventoryEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:inventory:info")
    public Result<InventoryEO> info(@PathVariable("id") Long id){
        logger.info("======== InventoryController.info(entity => "+id+") ========");

        InventoryEO entity = this.inventoryService.getById(id);

        return new Result<InventoryEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param inventoryId
     * @return
     */
    @GetMapping("listDetails/{inventoryId}")
    @RequiresPermissions("wms:receiveOrder:info")
    public Result<List<InventoryDetailEO>> listDetails(@PathVariable("inventoryId") Long inventoryId){
        logger.info("======== ReceiveOrderController.info(listDetails => "+inventoryId+") ========");

        List<InventoryDetailEO> details = this.inventoryService.listDetailsByInventory(inventoryId);

        return new Result<List<InventoryDetailEO>>().ok(details);
    }


    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建盘点单")
    @RequiresPermissions("wms:inventory:create")
    public Result create(@RequestBody InventoryEO entity){
        logger.info("======== InventoryController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        InventoryEO entityFromDb =this.inventoryService.saveEntity(entity);

        return new Result<InventoryEO>().ok(entityFromDb);
    }

    /**
     * 批量加入调节表
     * @param inventoryDetailIds
     * @param inventoryId
     * @return
     */
    @PostMapping("insertAdjust")
    @OperationLog("批量加入调节表")
    public Result addBatch(@RequestParam("inventoryDetailIds") Long[] inventoryDetailIds,
                           @RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addBatch ========");
        String msg = this.adjustInventoryService.insertAdjust(inventoryDetailIds, inventoryId,getUser());
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 全部加入调节表
     * @param inventoryId
     * @return
     */
    @PostMapping("insertAllAdjust")
    @OperationLog("全部加入调节表")
    public Result addAll(@RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addAll ========");
        String msg = this.adjustInventoryService.insertAllAdjust(inventoryId,getUser());
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 批量删除调节表
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deleteAdjust")
    @OperationLog("删除调节表")
    public Result deleteBatch(@RequestParam("ids") Long[] ids,
                              @RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.adjustInventoryService.deleteAdjust(ids,inventoryId);

        return new Result();
    }

    /**
     * 批量更新调节表信息
     * @param adjustInventoryEOS
     * @return
     */
    @PostMapping("updatewaitingAdjust")
    @OperationLog("批量更新送货单明细")
    public Result updatewaitingAdjust(@RequestBody AdjustInventoryEO[] adjustInventoryEOS){
        logger.info("======== InventoryController.updatewaitingAdjust ========");
        String msg = this.adjustInventoryService.updatewaitingAdjust(adjustInventoryEOS);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 批量加入库存表
     * @param adjustIds
     * @param inventoryId
     * @return
     */
    @PostMapping("insertStock")
    @OperationLog("批量加入库存表")
    public Result addBatchtoStock(@RequestParam("adjustIds") Long[] adjustIds,
                           @RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addBatch ========");
        String msg = this.adjustInventoryService.insertStock(adjustIds, inventoryId,getUser());
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 全部加入库存表
     * @param inventoryId
     * @return
     */
    @PostMapping("insertAllStock")
    @OperationLog("批量加入库存表")
    public Result addBatchtoStock(@RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addBatch ========");
        String msg = this.adjustInventoryService.insertAllStock(inventoryId,getUser());
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 调账完成按钮，更改调账状态为已完成
     * @param inventoryId
     * @return
     */
    @PostMapping("doneStock")
    @OperationLog("调账完成按钮，更改调账状态为已完成")
    public Result doneStock(@RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addBatch ========");
        String msg = this.adjustInventoryService.doneStock(inventoryId);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 批量删除库存表
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deleteStock")
    @OperationLog("批量删除库存信息")
    public Result deleteBatchStock(@RequestParam("ids") Long[] ids,
                                   @RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.adjustInventoryService.deleteStock(ids,inventoryId);

        return new Result();
    }

    /**
     * 全部删除库存表
     * @param inventoryId
     * @return
     */
    @PostMapping("deleteAllStock")
    @OperationLog("全部删除库存表")
    public Result deleteAllStock(@RequestParam("inventoryId") Long inventoryId){
        logger.info("======== InventoryController.addAll ========");
        String msg = this.adjustInventoryService.deleteAllStock(inventoryId);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 创建
     *
     * @param entitys
     * @return
     */
    @PostMapping("batch")
    @OperationLog("创建盘点单明细")
    @RequiresPermissions("wms:inventory:create")
    public Result create(@RequestBody InventoryDetailEO[] entitys){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryDetailService.saveBatch(entitys);

        return new Result();
    }


    @PostMapping("batch/all")
    @OperationLog("创建盘点单明细")
    @RequiresPermissions("wms:inventory:create")
    public Result insertAllById(@RequestParam("id") Long id,@RequestParam("warehouseType") String warehouseType,@RequestParam("materialName") String materialName){
        logger.info("======== InventoryController.create(ALL => ) ========");

        this.inventoryDetailService.insertAllById(id,warehouseType,materialName);

        return new Result();
    }



    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新盘点单")
    @RequiresPermissions("wms:inventory:update")
    public Result update(@RequestBody InventoryEO entity){
        logger.info("======== InventoryController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.inventoryService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除盘点单")
    @RequiresPermissions("wms:inventory:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== InventoryController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.removeByIds(ids);

        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除盘点单明细")
    @RequiresPermissions("wms:inventory:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== InventoryController.deleteDetail() ========");

        this.inventoryService.deleteDetailById(id);

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
    @RequiresPermissions("wms:inventory:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== InventoryController.updateStatusRelease ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.updateStatusById(ids,1);

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
    @RequiresPermissions("wms:inventory:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== InventoryController.updateStatusCancelRelease ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.updateStatusById(ids,0);

        return new Result();
    }

    /**
     * 设置状态（完成）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/finish")
    @OperationLog("设置状态-取消发布")
    @RequiresPermissions("wms:inventory:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== InventoryController.updateStatusCancelRelease ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.updateStatusById(ids,2);

        return new Result();
    }

    /**
     * 设置状态（关闭）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/close")
    @OperationLog("设置状态-关闭")
    @RequiresPermissions("wms:inventory:updateClose")
    public Result updateStatusClose(@RequestBody Long[] ids){
        logger.info("======== InventoryController.updateStatusClose ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.updateStatusById(ids,9);

        return new Result();
    }

    /**
     * 设置状态（打开）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/open")
    @OperationLog("设置状态-打开")
    @RequiresPermissions("wms:inventory:updateOpen")
    public Result updateStatusOpen(@RequestBody Long[] ids){
        logger.info("======== InventoryController.updateStatusOpen ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.updateStatusById(ids,0);

        return new Result();
    }

    /**
     * 已选择物料明细单分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<InventoryDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        IPage<InventoryDetailEO> page = this.inventoryService.selectDetailPage(criteria);

        return new Result<IPage<InventoryDetailEO>>().ok(page);
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail")
    @OperationLog("删除盘点单明细")
    @RequiresPermissions("wms:inventory:delete")
    public Result deleteDetail(@RequestBody Long[] ids){
        logger.info("======== InventoryController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.inventoryService.removeByDetailIds(ids);

        return new Result();
    }


    /**
     * 待选择物料分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("material/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<MaterialEO>> selectInventoryPage(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        IPage<MaterialEO> page = this.materialService.selectInventoryPage(criteria);

        return new Result<IPage<MaterialEO>>().ok(page);
    }

    /**
     * 待加入调节表分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("waitingJoin/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<InventoryDetailEO>> waitingJoinPage(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        IPage<InventoryDetailEO> page = this.inventoryDetailService.waitingJoinPage(criteria);

        return new Result<IPage<InventoryDetailEO>>().ok(page);
    }


    /**
     * 待调节物料信息分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("waitingAdjust/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<AdjustInventoryEO>> waitingAdjustPage(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        IPage<AdjustInventoryEO> page = this.inventoryDetailService.waitingAdjustPage(criteria);

        return new Result<IPage<AdjustInventoryEO>>().ok(page);
    }

    /**
     * 已调节物料信息分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("doneAdjust/page")
    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<AdjustInventoryEO>> doneAdjustPage(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.list() ========");

        IPage<AdjustInventoryEO> page = this.inventoryDetailService.doneAdjustPage(criteria);

        return new Result<IPage<AdjustInventoryEO>>().ok(page);
    }


    /***
     * 通过盘点单ID获取待盘点明细和已盘点明细
     * @param Id
     * @return
     */
    @GetMapping("app/{Id}")
    //@RequiresPermissions("wms:inventory:info")
    public Result<InventoryEO> getDetailInfoByNo(@PathVariable("Id") Long Id){
        logger.info("======== InventoryController.getDetailInfoByNo(entity => "+Id+") ========");
        InventoryEO entity = this.inventoryService.getDetailInfoByNo(Id);
        return new Result<InventoryEO>().ok(entity);
    }


    /**
     * 单个盘点完成
     * @param
     * @return
     */
    @PostMapping("app/inventory")
    @OperationLog("单个盘点")
    //@RequiresPermissions("wms:inventory:update")
    public Result inventoryOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== DeliveryOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.inventoryService.inventoryOne(Id,amount,action,userId,userName);

        return new Result();
	}
	
    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    @RequiresPermissions("wms:inventory:info")
    public Result<InventoryDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== InventoryController.info(entity => "+id+") ========");

        InventoryDetailEO entity = this.inventoryService.getDetailById(id);

        return new Result<InventoryDetailEO>().ok(entity);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("detail")
    @OperationLog("更新入库明细")
    @RequiresPermissions("wms:receiveOrder:update")
    public Result updateDetail(@RequestBody InventoryDetailEO entity){
        logger.info("======== InventoryController.updateDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.inventoryService.updateDetailById(entity);

        return new Result();
    }


    /**
     * 开始盘点
     *
     * @return
     */
    @PostMapping("begin")
    @OperationLog("创建盘点单及其明细")
    @RequiresPermissions("wms:inventory:create")
    public Result beginInventory(@RequestParam("beginDate") String beginDate){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryService.beginInventory(getUser(),beginDate);

        return new Result();
    }

    /**
     * 盘点完成
     *
     * @return
     */
    @PostMapping("finish")
    @OperationLog("完成盘点单及其明细")
    @RequiresPermissions("wms:inventory:update")
    public Result finishInventory(@RequestParam("id") Long id){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryService.finishInventory(getUser(),id);

        return new Result();
    }

    /**
     * 更新线边盘点数量到盘点明细中
     *
     * @return
     */
    @PostMapping("updateLinedge")
    @OperationLog("更新盘点单及其明细")
    @RequiresPermissions("wms:inventory:update")
    public Result updateLinedge(@RequestParam("id") Long id){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryService.updateLinedge(getUser(),id);

        return new Result();
    }

    /**
     * 盘点单解锁
     *
     * @return
     */
    @PostMapping("unlock")
    @OperationLog("盘点单解锁")
    //@RequiresPermissions("wms:inventory:update")
    public Result unlockInventory(@RequestParam("id") Long id){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryService.unlockInventory(id,0);

        return new Result();
    }

    /**
     * 查找需打印的订单明细
     *
     * @param id
     * @return
     */
    @PostMapping("exportDate")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<List<InventoryDetailEO>> exportDate(@RequestParam("id") Long id){
        logger.info("======== ReceiveOrderController.info(listDetails => "+id+") ========");

        List<InventoryDetailEO> details = this.inventoryDetailService.listexportDateByInventory(id);

        return new Result<List<InventoryDetailEO>>().ok(details);
    }



    /**
     * 导出报表
     * @param request
     * @param response
     * @param InventoryDetailEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    //@RequiresPermissions("basic:machine:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody InventoryDetailEO[] InventoryDetailEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("inventoryDetail.json");

        //导出Excel
        ExcelUtils.exportExcels(response, Arrays.asList(InventoryDetailEOS),InventoryDetailEOS[0].getFullName(),InventoryDetailEOS[0].getInventoryDate(), jsonObject);

        return new Result();
    }

    /**
     * 查找导出盘点单的明细数据
     *
     * @param id
     * @return
     */
    @PostMapping("getExportData")
    public Result<List<InventoryDetailEO>> getExportData(@RequestParam("id") Long id){
        logger.info("======== InventoryController.getExportData(getExportData => "+id+") ========");

        List<InventoryDetailEO> details = this.inventoryDetailService.getByInventoryId(id);

        return new Result<List<InventoryDetailEO>>().ok(details);
    }

    /**
     * 导出盘点单
     * @param request
     * @param response
     * @param InventoryDetailEOS 需要导出的数据
     * @return
     */
    @PostMapping("exportInventoryDetail")
    @OperationLog("导出盘点单")
    public Result export(HttpServletRequest request, HttpServletResponse response, @RequestBody InventoryDetailEO[] InventoryDetailEOS){
        JSONObject jsonObject= ExcelUtils.parseJsonFile("inventory_detail.json");
        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(InventoryDetailEOS), jsonObject);
        return new Result();
    }

    /**
     * 查找临时盘点单数据
     *
     * @param id
     * @return
     */
    @PostMapping("getExportTempInventory")
    public Result<List<TempInventoryEO>> getExportTempInventory(@RequestParam("id") Long id){
        logger.info("======== InventoryController.getExportTempInventory => "+id+") ========");

        List<TempInventoryEO> tempInventorys = this.tempInventoryService.getByInventoryId(id);

        return new Result<List<TempInventoryEO>>().ok(tempInventorys);
    }

    /**
     * 导出临时盘点单
     * @param request
     * @param response
     * @param tempInventorys 需要导出的数据
     * @return
     */
    @PostMapping("exportTempInventory")
    @OperationLog("导出临时盘点单")
    public Result exportTempInventory(HttpServletRequest request, HttpServletResponse response, @RequestBody TempInventoryEO[] tempInventorys){
        JSONObject jsonObject= ExcelUtils.parseJsonFile("temp_inventory.json");
        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(tempInventorys), jsonObject);
        return new Result();
    }

    /**
     * 更新最新的物料信息到盘点明细中
     *
     * @return
     */
    @PostMapping("updateInventory")
    @OperationLog("更新最新的物料信息")
    @RequiresPermissions("wms:inventory:update")
    public Result updateInventory(@RequestParam("id") Long id){
        logger.info("======== InventoryController.create(ID => ) ========");

        this.inventoryService.updateInventory(id,getUser());

        return new Result();
    }


    /**
     * 根据物料ID查找盘点明细单的相关信息（临时盘点表,盲盘）
     *
     * @param
     * @return
     */
    @GetMapping("app/inventory/{id}")
    @RequiresPermissions("wms:inventory:info")
    public Result<InventoryDetailEO> inventoryDetailInfo(@PathVariable("id") String elementNo){
        logger.info("======== InventoryController.inventoryDetailInfo(entity => "+elementNo+") ========");

        InventoryDetailEO entity = this.inventoryService.getInventoryDetailInfo(elementNo,getOrgId());

        return new Result<InventoryDetailEO>().ok(entity);
    }



    /**
     * 查询用户的盘点信息（临时盘点表,盲盘）
     *
     * @param
     * @return
     */
    @PostMapping("app/allInventory")
//    @RequiresPermissions("wms:inventory:info")
    public Result<IPage<TempInventoryEO>> inventoryAllDetailInfo(@RequestBody Criteria criteria){
        logger.info("======== InventoryController.inventoryAllDetailInfo ========");

        Criterion criterion = new Criterion();
        criterion.setField("t.inventory_user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<TempInventoryEO> page = this.inventoryService.inventoryAllDetailInfo(criteria);


        return new Result<IPage<TempInventoryEO>>().ok(page);
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
        String userName = getUserName();
        Long userId = getUserId();
        this.inventoryService.inventorBlindyOne(Id,amount,locationId,action,userId,userName,inventoryNo);

        return new Result();
    }


    @PostMapping("import")
    public Result importExcel(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String inventoryId = request.getParameter("inventoryId");

        Result result =this.inventoryService.importFromExcel(list,inventoryId,getUser());

        return  result;
    }
}
