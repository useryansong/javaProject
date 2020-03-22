package com.xchinfo.erp.controller.scm.srm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.srm.service.PurchaseOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.utils.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/5/9
 */
@RestController
@RequestMapping("/srm/outsideOrder")
public class OutsideOrderController extends BaseController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:outsideOrder:info")
    public Result<IPage<PurchaseOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== OutsideOrderController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<PurchaseOrderEO> page = this.purchaseOrderService.selectPage(criteria);
        return new Result<IPage<PurchaseOrderEO>>().ok(page);
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request, @RequestParam("orgId") Long orgId){
        logger.info("======== OutsideOrderController.import ========");
        List list = ExcelUtils.getExcelData(request);
        UserEO user = super.getUser();
        this.purchaseOrderService.importFromExcel(list, 2, user);
        return new Result();
    }

    /**
     * 新增送货计划
     * @param entity
     * @return
     */
    @PostMapping("addDeliveryPlan")
    @OperationLog("新增送货计划")
    @RequiresPermissions("srm:outsideOrder:addDeliveryPlan")
    public Result addDeliveryPlan(@RequestBody DeliveryPlanEO entity){
        logger.info("======== OutsideOrderController.addDeliveryPlan(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        UserEO user = super.getUser();
        DeliveryPlanEO entityFromDb = this.purchaseOrderService.createDeliveryPlan(entity, user);
        return new Result<DeliveryPlanEO>().ok(entityFromDb);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<PurchaseOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== OutsideOrderController.info(entity => "+id+") ========");
        PurchaseOrderEO entity = this.purchaseOrderService.getById(id);
        return new Result<PurchaseOrderEO>().ok(entity);
    }

    /**
     * 批量创建送货计划
     * @param ids
     * @param planDeliveryDate
     * @return
     */
    @PostMapping("addBatchDeliveryPlan")
    @OperationLog("批量创建送货计划")
    @RequiresPermissions("srm:outsideOrder:addBatchDeliveryPlan")
    public Result addBatchDeliveryPlan(@RequestParam("ids") Long[] ids, @RequestParam("planDeliveryDate") String planDeliveryDate){
        logger.info("======== OutsideOrderController.addBatchDeliveryPlan ========");
        UserEO user = super.getUser();
        Map map = this.purchaseOrderService.createBatchDeliveryPlan(ids, planDeliveryDate, user);
        return new Result<Map>().ok(map);
    }

    /**
     * 下载模板文件
     * @param req
     * @param resp
     * @param id
     * @return
     */
    @GetMapping("downloadFile/{id}")
    public Result downloadFile(HttpServletRequest req, HttpServletResponse resp, @PathVariable("id") Long id) {
        logger.info("======== OutsideOrderController.downloadFile() ========");
        Result result = new Result();
        AttachmentEO attachment = this.attachmentService.getById(id);
        if(attachment != null){
            FileUtils.downloadFile(resp, attachment.getAttachmentName(), attachment.getAttachmentUrl());
        }else{
            result.setCode(500);
            result.setMsg("数据库配置出错");
        }
        return result;
    }

    /**
     * 关闭委外单
     * @param ids
     * @return
     */
    @PostMapping("close")
    @OperationLog("关闭委外单")
    @RequiresPermissions("srm:outsideOrder:close")
    public Result close(@RequestBody Long[] ids){
        logger.info("======== OutsideOrderController.close() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.purchaseOrderService.close(ids, getUserId());
        return new Result();
    }

    /**
     * 取消关闭委外单
     * @param ids
     * @return
     */
    @PostMapping("cancelClose")
    @OperationLog("取消关闭委外单")
    @RequiresPermissions("srm:outsideOrder:cancelClose")
    public Result cancelClose(@RequestBody Long[] ids){
        logger.info("======== OutsideOrderController.cancelClose() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.purchaseOrderService.cancelClose(ids, getUserId());
        return new Result();
    }

    /**
     * 变更数量
     * @param entity
     * @return
     */
    @PostMapping("changeQuantity")
    @OperationLog("变更数量")
    @RequiresPermissions("srm:outsideOrder:changeQuantity")
    public Result changeQuantity(@RequestBody PurchaseOrderEO entity){
        logger.info("======== PurchaseOrderController.changeQuantity(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.purchaseOrderService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 发布送货计划
     * @param deliveryPlanId
     * @return
     */
    @PostMapping("releaseDeliveryPlan")
    @OperationLog("发布送货计划")
    public Result releaseDeliveryPlan(@RequestParam("deliveryPlanId") Long deliveryPlanId){
        logger.info("======== OutsideOrderController.releaseDeliveryPlan========");
        this.purchaseOrderService.releaseDeliveryPlan(deliveryPlanId, getUserId());
        return new Result();
    }

    /**
     * 关闭送货计划
     * @param deliveryPlanId
     * @return
     */
    @PostMapping("closeDeliveryPlan")
    @OperationLog("关闭送货计划")
    public Result closeDeliveryPlan(@RequestParam("deliveryPlanId") Long deliveryPlanId){
        logger.info("======== OutsideOrderController.closeDeliveryPlan========");
        this.purchaseOrderService.closeDeliveryPlan(deliveryPlanId, getUserId());
        return new Result();
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("getDeliveryPlan/{id}")
    public Result<DeliveryPlanEO> getDeliveryPlan(@PathVariable("id") Long id){
        logger.info("======== OutsideOrderController.getDeliveryPlanById(entity => "+id+") ========");
        DeliveryPlanEO entity = this.purchaseOrderService.getDeliveryPlanById(id);
        return new Result<DeliveryPlanEO>().ok(entity);
    }

    /**
     * 更新送货计划
     * @param entity
     * @return
     */
    @PutMapping("updateDeliveryPlan")
    @OperationLog("更新送货计划")
    public Result updateDeliveryPlan(@RequestBody DeliveryPlanEO entity){
        logger.info("======== OutsideOrderController.updateDeliveryPlan========");
        this.purchaseOrderService.updateDeliveryPlan(entity, getUserId());
        return new Result();
    }

    /**
     * 删除送货计划
     * @param deliveryPlanId
     * @return
     */
    @PostMapping("deleteDeliveryPlan")
    @OperationLog("删除送货计划")
    public Result deleteDeliveryPlan(@RequestParam("deliveryPlanId") Long deliveryPlanId){
        logger.info("======== OutsideOrderController.deleteDeliveryPlan========");
        this.purchaseOrderService.deleteDeliveryPlan(deliveryPlanId, getUserId());
        return new Result();
    }


    /**
     * 根据机构和起止日期和选中的数据发布和取消发布
     *
     * @param
     * @return
     */
    @PostMapping("operateById")
    public Result operateById( @RequestParam("action") String action,@RequestParam("ids") Long[] ids,@RequestParam("beginDate") String beginDate,@RequestParam("endDate") String endDate){
        logger.info("======== OutsideOrderController.operateById========");
        this.purchaseOrderService.operateById(action,beginDate,endDate,ids,getUser());
        return  new Result();
    }


    /**
     * 根据机构和起止日期发布和取消发布
     *
     * @param
     * @return
     */
    @PostMapping("operateAll")
    public Result operateAll( @RequestParam("action") String action,@RequestParam("beginDate") String beginDate,@RequestParam("endDate") String endDate){
        logger.info("======== OutsideOrderController.operateAll========");

        this.purchaseOrderService.operateOutAll(action,beginDate,endDate,getUser());
        return  new Result();
    }

    /**
     * 确认委外单
     * @param ids
     * @return
     */
    @PostMapping("confirm")
    @OperationLog("确认委外单")
    public Result confirm(@RequestBody Long[] ids){
        logger.info("======== OutsideOrderController.confirm() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        return this.purchaseOrderService.confirm(ids, getUser());
    }
}
