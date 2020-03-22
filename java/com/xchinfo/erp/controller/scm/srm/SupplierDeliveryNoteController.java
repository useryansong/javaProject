package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.srm.service.DeliveryNoteService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
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
@RequestMapping("/srm/supplierDeliveryNote")
public class SupplierDeliveryNoteController extends BaseController {

    @Autowired
    private DeliveryNoteService deliveryNoteService;

    @Autowired
    private SupplierService supplierService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:supplierDeliveryNote:info")
    public Result<IPage<DeliveryNoteEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SupplierDeliveryNoteController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        if(getUser().getSupplierId() == null) {
            // 供应商用户的用户名为供应商编码
            SupplierEO supplier = this.supplierService.getBySupplierCode(getUserName());
            if(supplier == null) {
                throw new BusinessException("用户不存在供应商!");
            }
            map.put("supplierId", supplier.getSupplierId());
        } else {
            map.put("supplierId", getUser().getSupplierId());
        }
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        map.put("currentIndexFlag", true);
        List<DeliveryNoteEO> pageList = this.deliveryNoteService.getPage(map);
        IPage<DeliveryNoteEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<DeliveryNoteEO>>().ok(page);
    }

    /**
     * 新增采购送货单
     * @param entity
     * @return
     */
    @PostMapping("addPurchaseOrder")
    @OperationLog("新增采购送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:addPurchaseOrder")
    public Result addPurchaseOrder(@RequestBody DeliveryNoteEO entity){
        logger.info("======== SupplierDeliveryNoteController.addPurchaseOrder(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        UserEO user = super.getUser();
        DeliveryNoteEO entityFromDb = this.deliveryNoteService.saveEntity(entity, user);
        return new Result<DeliveryNoteEO>().ok(entityFromDb);
    }

    /**
     * 新增委外送货单
     * @param entity
     * @return
     */
    @PostMapping("addOutsideOrder")
    @OperationLog("新增委外送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:addOutsideOrder")
    public Result addOutsideOrder(@RequestBody DeliveryNoteEO entity){
        logger.info("======== SupplierDeliveryNoteController.addOutsideOrder(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        UserEO user = super.getUser();
        DeliveryNoteEO entityFromDb = this.deliveryNoteService.saveEntity(entity, user);
        return new Result<DeliveryNoteEO>().ok(entityFromDb);
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:update")
    public Result update(@RequestBody DeliveryNoteEO entity){
        logger.info("======== SupplierDeliveryNoteController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryNoteService.updateById(entity);
        return new Result();
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DeliveryNoteEO> info(@PathVariable("id") Long id){
        logger.info("======== SupplierDeliveryNoteController.info(entity => "+id+") ========");
        DeliveryNoteEO entity = this.deliveryNoteService.selectDetailById(id);
        return new Result<DeliveryNoteEO>().ok(entity);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SupplierDeliveryNoteController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteService.removeByIds(ids);
        return new Result();
    }

    /**
     * 发布
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:release")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== SupplierDeliveryNoteController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        return this.deliveryNoteService.releaseByIds(ids,getUser());
    }

    /**
     * 取消发布
     * @param ids
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布送货单")
    @RequiresPermissions("srm:supplierDeliveryNote:cancelRelease")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== SupplierDeliveryNoteController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteService.cancelReleaseByIds(ids,getUser());
        return new Result();
    }

    /**
     * 查找所有符合条件的送货单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    public Result<List<DeliveryNoteEO>> list(@RequestBody Criteria criteria) {
        logger.info("======== SupplierDeliveryNoteController.list() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        return new Result<List<DeliveryNoteEO>>().ok(totalList);
    }

    /**
     * 打印(修改状态)
     * @param deliveryNote
     * @return
     */
    @PostMapping("print")
    @OperationLog("打印修改状态")
    @RequiresPermissions("srm:supplierDeliveryNote:print")
    public Result print(@RequestBody DeliveryNoteEO deliveryNote){
        logger.info("======== SupplierDeliveryNoteController.print() ========");
        this.deliveryNoteService.updateStatusByPrint(deliveryNote);
        return new Result();
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param DeliveryNoteDetailEOs 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出报表")
    //@RequiresPermissions("srm:supplierDeliveryNote:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody DeliveryNoteDetailEO[] DeliveryNoteDetailEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("supplier_delivery_note.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(DeliveryNoteDetailEOs), jsonObject);

        return new Result();
    }

//
//    /***
//     * 通过流水获取送货单明细
//     * @param voucherNo
//     * @return
//     */
//    @GetMapping("app/{voucherNo}")
//    @RequiresPermissions("srm:supplierDeliveryNote:info")
//    public Result<DeliveryNoteEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
//        logger.info("======== SupplierDeliveryNoteController.getDetailInfoByNo(entity => "+voucherNo+") ========");
//        DeliveryNoteEO entity = this.deliveryNoteService.getDetailInfoByNo(voucherNo);
//        return new Result<DeliveryNoteEO>().ok(entity);
//    }
//
//    /**
//     * 单个收货
//     * @param
//     * @return
//     */
//    @PostMapping("app/receive")
//    @OperationLog("收货")
//    @RequiresPermissions("srm:supplierDeliveryNote:update")
//    public Result receiveOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
//        logger.info("======== SupplierDeliveryNoteController.receiveOne() ========");
//        String userName = getUserName();
//        Long userId = getUserId();
//        this.deliveryNoteService.receiveOne(Id,amount,userId,userName,action);
//
//        return new Result();
//    }
}
