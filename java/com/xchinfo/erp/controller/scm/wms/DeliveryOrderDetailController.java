package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.MaterialReceiveEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.mapper.DeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.MaterialReceiveMapper;
import com.xchinfo.erp.wms.service.DeliveryOrderDetailService;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
import com.xchinfo.erp.wms.service.MaterialReceiveService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.exception.BusinessException;
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
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/5/6
 * @update
 */
@RestController
@RequestMapping("/wms/deliveryOrderDetail")
public class DeliveryOrderDetailController extends BaseController {
    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private MaterialReceiveService materialReceiveService;

    @Autowired
    private MaterialReceiveMapper materialReceiveMapper;

    @Autowired
    private DeliveryOrderDetailMapper deliveryOrderDetailMapper;
    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单明细")
    @RequiresPermissions("wms:deliveryOrder:create")
    public Result create(@RequestBody DeliveryOrderDetailEO entity){
        logger.info("======== DeliveryOrderDetailController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.deliveryOrderDetailService.save(entity);
        return new Result();
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<IPage<DeliveryOrderDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderDetailController.page() ========");
        IPage<DeliveryOrderDetailEO> page = this.deliveryOrderDetailService.selectPage(criteria);
        return new Result<IPage<DeliveryOrderDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<DeliveryOrderDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryOrderDetailController.info(entity => "+id+") ========");
        DeliveryOrderDetailEO entity = this.deliveryOrderDetailService.getById(id);
        return new Result<DeliveryOrderDetailEO>().ok(entity);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单明细")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result update(@RequestBody DeliveryOrderDetailEO entity){
        logger.info("======== DeliveryOrderDetailController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryOrderDetailService.updateById(entity);

        DeliveryOrderEO deliveryOrderEO = this.deliveryOrderService.getById(entity.getDeliveryOrderId());
        //如果是生产领料的，还需要修改原材料领料表
        if (deliveryOrderEO.getVoucherType()==2 && deliveryOrderEO.getDeliveryType()==2 ) {
            Date date = new Date();
            MaterialReceiveEO materialReceiveEO = this.materialReceiveMapper.getByDeliveryDetailId(entity.getDeliveryDetailId());
            if (entity.getStampingMaterialConsumptionQuotaId() !=0){
                materialReceiveEO.setStampingMaterialConsumptionQuotaId(entity.getStampingMaterialConsumptionQuotaId());
            }
            materialReceiveEO.setMaterialId(entity.getMaterialId());
            MaterialReceiveEO temp = this.deliveryOrderDetailMapper.selectorigina(entity.getStampingMaterialConsumptionQuotaId());
            materialReceiveEO.setMaterialId(temp.getMaterialId());
            materialReceiveEO.setMaterialName(temp.getMaterialName());
            materialReceiveEO.setElementNo(temp.getElementNo());
            materialReceiveEO.setInventoryCode(temp.getInventoryCode());

            MaterialReceiveEO temp2 = this.deliveryOrderDetailMapper.selectstamping(entity.getStampingMaterialConsumptionQuotaId());
            if (temp2 != null){
                materialReceiveEO.setStampingMaterialId(temp2.getMaterialId());
                materialReceiveEO.setStampingMaterialName(temp2.getMaterialName());
                materialReceiveEO.setStampingElementNo(temp2.getElementNo());
                materialReceiveEO.setInventoryCoding(temp.getInventoryCode());
            }else{
                throw new BusinessException("对应物料不存在冲压件!");
            }

            materialReceiveEO.setDeliveryAmount(entity.getDeliveryAmount());
            this.materialReceiveService.updateById(materialReceiveEO);

        }
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除出库单明细")
    @RequiresPermissions("wms:deliveryOrder:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderDetailController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");

        //如果是生产领料的，还需要删除原材料领料表
        for(Long id :ids){
            DeliveryOrderDetailEO entity = this.deliveryOrderDetailService.getById(id);
            DeliveryOrderEO deliveryOrderEO = this.deliveryOrderService.getById(entity.getDeliveryOrderId());
            if(deliveryOrderEO.getVoucherType()==2 && deliveryOrderEO.getDeliveryType()==2 ){
                this.materialReceiveMapper.removeByDetailId(id);
            } else{
                break;
            }
        }

        this.deliveryOrderDetailService.removeByIds(ids);
        return new Result();
    }

    /**
     * 发布
     * @param ids
     * @return
     */
    @PostMapping("updateStatus/release")
    @OperationLog("发布出库单明细")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderDetailController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderDetailService.updateStatusByIds(ids, 2);
        /*this.deliveryOrderDetailService.addDetailToStock(ids);*/
        return new Result();
    }

    /**
     * 取消发布
     * @param ids
     * @return
     */
    @PostMapping("updateStatus/cancelRelease")
    @OperationLog("取消发布出库单明细")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderDetailController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderDetailService.updateStatusByIds(ids, 1);
        /*this.deliveryOrderDetailService.deleteStock(ids);*/
        return new Result();
    }

    /**
     * 查找所有销售出库明细
     *
     * @return
     */
    @GetMapping("listDetailId")
    public Result<List<DeliveryOrderDetailEO>> getDeliveryDetailId(){
        logger.info("======== DeliveryOrderDetailController.listDetailId() ========");

        List<DeliveryOrderDetailEO> warehouses = this.deliveryOrderDetailService.getDeliveryDetailId();

        return new Result<List<DeliveryOrderDetailEO>>().ok(warehouses);
    }

    /**
     * 导出
     * @param deliveryOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
//    @RequiresPermissions("wms:deliveryOrder:export")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody Long[] deliveryOrderIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== DeliveryOrderDetailController.export ========");
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

    /**
     * 导出
     * @param deliveryOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("printExport")
    @OperationLog("导出")
//    @RequiresPermissions("wms:deliveryOrder:export")
    public void printOrExport(HttpServletRequest req, HttpServletResponse resp,
                              @RequestBody Long[] deliveryOrderIds,
                              @RequestParam("fileName") String fileName) throws FileNotFoundException {
        logger.info("======== DeliveryOrderDetailController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            DeliveryOrderEO deliveryOrder = this.deliveryOrderService.getById(deliveryOrderIds[0]);
            String exportFileName = deliveryOrder.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailService.getByDeliveryOrderIdsAndUserId(deliveryOrderIds, getUserId(), null);
            ExcelUtils.printExport(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, deliveryOrderDetails);
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 待出库分页查找
     * @param criteria
     * @return
     */
    @PostMapping("rendererPage")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<IPage<DeliveryOrderDetailEO>> rendererPage(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderDetailController.rendererPage() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<DeliveryOrderDetailEO> page = this.deliveryOrderDetailService.selectRendererPage(criteria);
        return new Result<IPage<DeliveryOrderDetailEO>>().ok(page);
    }


    /**
     * 更新状态
     * @param id
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("取消发布出库单明细")
    public Result finishRelease(@RequestBody Long id){
        logger.info("======== DeliveryOrderDetailController.finishRelease() ========");
        this.deliveryOrderDetailService.updateStatusById(id, 2);

        this.deliveryOrderDetailService.updateDeliveryOrderStatusById(id, 2);

        return new Result();
    }

}
