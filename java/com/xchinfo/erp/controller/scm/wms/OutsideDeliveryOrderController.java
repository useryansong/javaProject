package com.xchinfo.erp.controller.scm.wms;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.DeliveryOrderDetailService;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/outsideDeliveryOrder")
public class OutsideDeliveryOrderController extends BaseController {
    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<DeliveryOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== OutsideDeliveryOrderController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<DeliveryOrderEO> totalList = this.deliveryOrderService.getPage(map);
        map.put("currentIndexFlag", true);
        List<DeliveryOrderEO> pageList = this.deliveryOrderService.getPage(map);
        this.deliveryOrderService.getSumDeliveryAmount(pageList);
        IPage<DeliveryOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<DeliveryOrderEO>>().ok(page);
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("deliveryDetail/page")
    public Result<IPage<DeliveryOrderDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== OutsideDeliveryOrderController.detailPage() ========");
        IPage<DeliveryOrderDetailEO> page = this.deliveryOrderDetailService.selectPage(criteria);
        return new Result<IPage<DeliveryOrderDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DeliveryOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== OutsideDeliveryOrderController.info(entity => "+id+") ========");
        DeliveryOrderEO entity = this.deliveryOrderService.getById(id);
        return new Result<DeliveryOrderEO>().ok(entity);
    }

    /**
     * 创建出库单
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单")
    public Result create(@RequestBody DeliveryOrderEO entity){
        logger.info("======== OutsideDeliveryOrderController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        DeliveryOrderEO entityFromDb = this.deliveryOrderService.saveEntity(entity);
        return new Result<DeliveryOrderEO>().ok(entityFromDb);
    }

    /**
     * 更新出库单
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单")
    public Result update(@RequestBody DeliveryOrderEO entity){
        logger.info("======== OutsideDeliveryOrderController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryOrderService.updateById(entity);
        return new Result();
    }

    /**
     * 删除出库单
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除出库单")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== OutsideDeliveryOrderController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.deliveryOrderService.removeByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 批量加入出库单明细
     * @param purchaseOrderIds
     * @param deliveryOrderId
     * @return
     */
    @PostMapping("addBatch")
    @OperationLog("批量加入出库单明细")
    public Result addBatch(@RequestParam("purchaseOrderIds") Long[] purchaseOrderIds,
                           @RequestParam("deliveryOrderId") Long deliveryOrderId){
        logger.info("======== OutsideDeliveryOrderController.addBatch ========");
        String msg = this.deliveryOrderService.addBatchDetail(purchaseOrderIds, deliveryOrderId);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 删除出库单明细
     * @param ids
     * @return
     */
    @DeleteMapping("deliveryDetail")
    @OperationLog("删除出库单明细")
    public Result deleteDeliveryDetail(@RequestBody Long[] ids){
        logger.info("======== OutsideDeliveryOrderController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.deliveryOrderDetailService.removeByIds(ids);
        return new Result();
    }

    /**
     * 更新出库单明细
     * @param entity
     * @return
     */
    @PutMapping("deliveryDetail")
    @OperationLog("更新出库单明细")
    public Result updateDeliveryDetail(@RequestBody DeliveryOrderDetailEO entity){
        logger.info("======== OutsideDeliveryOrderController.updateDeliveryDetail(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        return this.deliveryOrderService.updateDeliveryDetail(entity);
    }


     /**
     * 发布
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布出库单")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== OutsideDeliveryOrderController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        String msg = this.deliveryOrderService.releaseByIds(ids);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 取消发布
     * @param ids
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布出库单")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== OutsideDeliveryOrderController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        String msg = this.deliveryOrderService.cancelReleaseByIds(ids);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 导出
     * @param deliveryOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody Long[] deliveryOrderIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== OutsideDeliveryOrderController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            DeliveryOrderEO deliveryOrder = this.deliveryOrderService.getById(deliveryOrderIds[0]);
            String exportFileName = deliveryOrder.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            String[] fileNames = fileName.split("/");
            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailService.getByDeliveryOrderIdsAndUserId(deliveryOrderIds, getUserId(), fileNames[1]);
            ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, deliveryOrderDetails);
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
