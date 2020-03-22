package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.DeliveryOrderDetailService;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/deliveryOther")
public class DeliveryOtherController extends BaseController {
    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private OrgService orgService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:deliveryOther:info")
    public Result<IPage<DeliveryOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryOtherController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<DeliveryOrderEO> page = this.deliveryOrderService.selectPage(criteria);
        return new Result<IPage<DeliveryOrderEO>>().ok(page);
    }

    /**
     * 查询所有出库单
     * @return
     */
    @GetMapping("list")
    public Result<List<DeliveryOrderEO>> list(){
        logger.info("======== DeliveryOtherController.list() ========");
        List<DeliveryOrderEO> deliveryOrders = this.deliveryOrderService.listAll(getUserId());
        return new Result<List<DeliveryOrderEO>>().ok(deliveryOrders);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:deliveryOther:info")
    public Result<DeliveryOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryOtherController.info(entity => "+id+") ========");
        DeliveryOrderEO entity = this.deliveryOrderService.getById(id);
        return new Result<DeliveryOrderEO>().ok(entity);
    }

    /**
     * 创建
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建出库单")
    @RequiresPermissions("wms:deliveryOther:create")
    public Result create(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOtherController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
//        this.deliveryOrderService.save(entity);
        DeliveryOrderEO entityFromDb = this.deliveryOrderService.saveEntity(entity);
        return new Result<DeliveryOrderEO>().ok(entityFromDb);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新出库单")
    @RequiresPermissions("wms:deliveryOther:update")
    public Result update(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOtherController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryOrderService.updateById(entity);
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除出库单")
    @RequiresPermissions("wms:deliveryOther:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.deliveryOrderService.removeByIds(ids);
        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除入库订单明细")
    @RequiresPermissions("wms:deliveryOther:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== DeliveryOtherController.deleteDetail() ========");

        this.deliveryOrderService.deleteDetailById(id);

        return new Result();
    }

    /**
     * 查询出库单明细
     *
     * @param deliveryId
     * @return
     */
    @GetMapping("listDetails/{deliveryId}")
    @RequiresPermissions("wms:deliveryOther:info")
    public Result<List<DeliveryOrderDetailEO>> listDetails(@PathVariable("deliveryId") Long deliveryId){
        logger.info("======== DeliveryOtherController.info(listDetails => "+deliveryId+") ========");
        List<DeliveryOrderDetailEO> details = this.deliveryOrderService.listDetailsByDelivery(deliveryId);
        return new Result<List<DeliveryOrderDetailEO>>().ok(details);
    }

    /*    *//**
     * 更新状态
     *
     * @param entity
     * @return
     *//*
    @PostMapping("updateStatusById")
    @OperationLog("更新状态")
    public Result updateStatusById(@RequestBody DeliveryOrderEO entity){
        logger.info("======== DeliveryOrderController.updateStatusById ========");

        this.deliveryOrderService.updateStatusById(entity.getDeliveryId(), entity.getStatus());
        return new Result();
    }*/
    /*
     *//**
     * 发布
     * @param ids
     * @return
     *//*
    @PostMapping("release")
    @OperationLog("发布出库单")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderService.releaseByIds(ids);
        return new Result();
    }

    *//**
     * 取消发布
     * @param ids
     * @return
     *//*
    @PostMapping("cancelRelease")
    @OperationLog("取消发布出库单")
    @RequiresPermissions("wms:deliveryOrder:update")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOrderController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryOrderService.cancelReleaseByIds(ids);
        return new Result();
    }*/

    /**
     * 设置状态（发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/release")
    @OperationLog("设置状态-发布")
    @RequiresPermissions("wms:deliveryOther:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,1,0);

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
    @RequiresPermissions("wms:deliveryOther:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,0,1);

        return new Result();
    }

    /**
     * 设置状态（完成）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/finish")
    @OperationLog("设置状态-完成")
    @RequiresPermissions("wms:deliveryOther:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== DeliveryOtherController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.deliveryOrderService.updateStatusById(ids,2,1);

        return new Result();
    }


    /***
     * 通过流水获取出货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("wms:deliveryOrder:info")
    public Result<DeliveryOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== DeliveryOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        DeliveryOrderEO entity = this.deliveryOrderService.getDetailInfoByNo(voucherNo);
        return new Result<DeliveryOrderEO>().ok(entity);
    }


    /**
     * 单个发货
     * @param
     * @return
     */
    @PostMapping("app/delivery")
    @OperationLog("成品发货")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result deliveryOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== DeliveryOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryOrderService.deliveryOne(Id,amount,action,userId,userName);

        return new Result();
    }

    /**
     * 查找所有符合条件的出库单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("getList")
    @RequiresPermissions("wms:deliveryOrder:info")
    public Result<List<DeliveryOrderEO>> getList(@RequestBody Criteria criteria) {
        logger.info("======== DeliveryOrderController.getList() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        map.put("deliveryType", 1);
        List<DeliveryOrderEO> list = this.deliveryOrderService.getList(map);
        return new Result<List<DeliveryOrderEO>>().ok(list);
    }

    /**
     * 设置车次
     *
     * @param
     * @return
     */
    @PostMapping("setTran")
    @OperationLog("设置车次")
    public Result setTran(@RequestParam("trainNumber") String trainNumber,@RequestParam("ids") Long[] ids){
        logger.info("======== DeliveryOrderController.setTran ========");

        this.deliveryOrderService.setTran(ids,trainNumber,getUser());
        return new Result();
    }

    /**
     * 查询车次相应的架数
     *
     * @param
     * @return
     */
    @GetMapping("getTran")
    @OperationLog("查询车次相应的架数")
    public Result getTran(@RequestParam("trainNumber") String trainNumber,@RequestParam("date") String date){
        logger.info("======== DeliveryOrderController.setTran ========");

        Double count = this.deliveryOrderService.getTran(date,trainNumber,getUser());
        return new Result<Double>().ok(count);
    }

    @PostMapping("getExportData")
    public List<DeliveryOrderDetailEO> getExportData(@RequestBody Long[] deliveryOrderIds) {
        List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailService.getFromDeliveryOrderIds(deliveryOrderIds, 5);
        return deliveryOrderDetails;
    }

    /**
     * 导出
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody DeliveryOrderDetailEO[] deliveryOrderDetails,
                       @RequestParam("orgId") Long orgId,
                       @RequestParam("fileName") String fileName){
        logger.info("======== DeliveryOtherController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null) {
            OrgEO org = this.orgService.getById(orgId);
            String exportFileName = org.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, Arrays.asList(deliveryOrderDetails));
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
