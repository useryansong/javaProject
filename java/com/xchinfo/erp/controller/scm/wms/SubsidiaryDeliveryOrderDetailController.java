package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderDetailEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.SubsidiaryDeliveryOrderDetailService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/5/6
 * @update
 */
@RestController
@RequestMapping("/wms/deliveryConsumedDetail")
public class SubsidiaryDeliveryOrderDetailController extends BaseController {
    @Autowired
    private SubsidiaryDeliveryOrderDetailService subsidiaryDeliveryOrderDetailService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单明细")
    @RequiresPermissions("wms:deliveryOrder:create")
    public Result create(@RequestBody SubsidiaryDeliveryOrderDetailEO entity){
        logger.info("======== DeliveryOrderDetailController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.subsidiaryDeliveryOrderDetailService.save(entity);
        return new Result();
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<IPage<SubsidiaryDeliveryOrderDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderDetailController.page() ========");
        IPage<SubsidiaryDeliveryOrderDetailEO> page = this.subsidiaryDeliveryOrderDetailService.selectPage(criteria);
        return new Result<IPage<SubsidiaryDeliveryOrderDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:deliveryOrder:info")
        public Result<SubsidiaryDeliveryOrderDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryOrderDetailController.info(entity => "+id+") ========");
        SubsidiaryDeliveryOrderDetailEO entity = this.subsidiaryDeliveryOrderDetailService.getById(id);
        return new Result<SubsidiaryDeliveryOrderDetailEO>().ok(entity);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单明细")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result update(@RequestBody SubsidiaryDeliveryOrderDetailEO entity){
        logger.info("======== DeliveryOrderDetailController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.subsidiaryDeliveryOrderDetailService.updateById(entity);
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
        this.subsidiaryDeliveryOrderDetailService.removeByIds(ids);
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
        this.subsidiaryDeliveryOrderDetailService.updateStatusByIds(ids, 2);
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
        this.subsidiaryDeliveryOrderDetailService.updateStatusByIds(ids, 1);
        return new Result();
    }

    /**
     * 查找所有销售出库明细
     *
     * @return
     */
    @GetMapping("listDetailId")
    public Result<List<SubsidiaryDeliveryOrderDetailEO>> getDeliveryDetailId(){
        logger.info("======== DeliveryOrderDetailController.listDetailId() ========");

        List<SubsidiaryDeliveryOrderDetailEO> warehouses = this.subsidiaryDeliveryOrderDetailService.getDeliveryDetailId();

        return new Result<List<SubsidiaryDeliveryOrderDetailEO>>().ok(warehouses);
    }

    /**
     * 导出
     * @param deliveryOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("wms:deliveryOrder:info")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                                  @RequestParam("deliveryOrderIds") Long[] deliveryOrderIds,
                                  @RequestParam("fileName") String fileName){
        logger.info("======== DeliveryOrderDetailController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            List<SubsidiaryDeliveryOrderDetailEO> deliveryOrderDetails = this.subsidiaryDeliveryOrderDetailService.getByDeliveryOrderIds(deliveryOrderIds);
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
     * 待出库分页查找
     * @param criteria
     * @return
     */
    @PostMapping("rendererPage")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<IPage<SubsidiaryDeliveryOrderDetailEO>> rendererPage(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOrderDetailController.rendererPage() ========");
        IPage<SubsidiaryDeliveryOrderDetailEO> page = this.subsidiaryDeliveryOrderDetailService.selectRendererPage(criteria);
        return new Result<IPage<SubsidiaryDeliveryOrderDetailEO>>().ok(page);
    }
}
