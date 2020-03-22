package com.xchinfo.erp.controller.scm.wms;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.DeliveryOrderService;
import com.xchinfo.erp.wms.service.ReceiveOrderDetailService;
import com.xchinfo.erp.wms.service.ReceiveOrderService;
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
@RequestMapping("mes/productionReport")
public class ProductionReportController extends BaseController {
    @Autowired
    private ReceiveOrderService receiveOrderService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private ReceiveOrderDetailService receiveOrderDetailService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
//    @RequiresPermissions("mes:productionReport:info")
    public Result<IPage<ReceiveOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductionReportController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ReceiveOrderEO> page = this.receiveOrderService.selectPage(criteria);

        return new Result<IPage<ReceiveOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
//    @RequiresPermissions("mes:productionReport:info")
    public Result<ReceiveOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductionReportController.info(entity => "+id+") ========");

        ReceiveOrderEO entity = this.receiveOrderService.getById(id);

        return new Result<ReceiveOrderEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
//    @RequiresPermissions("mes:productionReport:info")
    public Result<List<ReceiveOrderDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== ProductionReportController.info(listDetails => "+orderId+") ========");

        List<ReceiveOrderDetailEO> details = this.receiveOrderService.listDetailsByOrder(orderId);

        return new Result<List<ReceiveOrderDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建入库订单")
//    @RequiresPermissions("mes:productionReport:create")
    public Result create(@RequestBody ReceiveOrderEO entity){
        logger.info("======== ProductionReportController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        ReceiveOrderEO receiveOrderEO = this.receiveOrderService.saveEntity(entity);

        return new Result<ReceiveOrderEO>().ok(receiveOrderEO);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新入库订单")
//    @RequiresPermissions("mes:productionReport:update")
    public Result update(@RequestBody ReceiveOrderEO entity){
        logger.info("======== ProductionReportController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.receiveOrderService.updateById(entity,getUserId());

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除入库订单")
//    @RequiresPermissions("mes:productionReport:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProductionReportController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.removeByIds(ids);

        return new Result();
    }


    /**
     * 明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
//    @RequiresPermissions("mes:productionReport:info")
    public Result<IPage<ReceiveOrderDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== ProductionReportController.list() ========");

        IPage<ReceiveOrderDetailEO> page = this.receiveOrderService.selectDetailPage(criteria);

        return new Result<IPage<ReceiveOrderDetailEO>>().ok(page);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail")
    @OperationLog("删除入库订单明细")
//    @RequiresPermissions("mes:productionReport:delete")
    public Result deleteDetail(@RequestBody Long[] ids){
        logger.info("======== ProductionReportController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.removeByDetailIds(ids);

        return new Result();
    }

    /**
     * 机构变更后删除订单明细
     *
     * @return
     */
    @PostMapping("deleteDetail")
    @OperationLog("删除入库订单明细")
//    @RequiresPermissions("mes:productionReport:delete")
    public Result deleteDetailById(@RequestParam("id") Long id){
        logger.info("======== ProductionReportController.deleteDetail() ========");

        this.receiveOrderService.deleteDetailById(id);

        return new Result();
    }


    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
//    @RequiresPermissions("mes:productionReport:info")
    public Result<ReceiveOrderDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== ProductionReportController.info(entity => "+id+") ========");

        ReceiveOrderDetailEO entity = this.receiveOrderService.getDetailById(id);

        return new Result<ReceiveOrderDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping("detail")
    @OperationLog("创建入库明细")
//    @RequiresPermissions("mes:productionReport:create")
    public Result createDetail(@RequestBody ReceiveOrderDetailEO entity){
        logger.info("======== ProductionReportController.createDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.receiveOrderService.saveDetail(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("detail")
    @OperationLog("更新入库明细")
//    @RequiresPermissions("mes:productionReport:update")
    public Result updateDetail(@RequestBody ReceiveOrderDetailEO entity){
        logger.info("======== ProductionReportController.updateDetail(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.receiveOrderService.updateDetailById(entity);

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
//    @RequiresPermissions("mes:productionReport:updateRelease")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== ProductionReportController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,1,0);

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
//    @RequiresPermissions("mes:productionReport:updateCancelRelease")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== ProductionReportController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,0,1);

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
//    @RequiresPermissions("mes:productionReport:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== ProductionReportController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.receiveOrderService.updateStatusById(ids,2,1);

        return new Result();
    }

    /***
     * 通过流水获取入库单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("wms:deliveryOrder:info")
    public Result<ReceiveOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== ProductionReportController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        ReceiveOrderEO entity = this.receiveOrderService.getDetailInfoByNo(voucherNo);
        return new Result<ReceiveOrderEO>().ok(entity);
    }


    /**
     * 单个收货
     * @param
     * @return
     */
    @PostMapping("app/receive")
    @OperationLog("零件入库")
    //@RequiresPermissions("wms:deliveryOrder:update")
    public Result receiveOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== ProductionReportController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.receiveOrderService.receiveOne(Id,amount,action,userId,userName);

        return new Result();
    }

    /**
     * 导出
     * @param receiveOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("mes:productionReport:export")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody Long[] receiveOrderIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== ProductionReportController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            ReceiveOrderEO receiveOrder = this.receiveOrderService.getById(receiveOrderIds[0]);
            String exportFileName = receiveOrder.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            List<ReceiveOrderDetailEO> receiveOrderDetails = this.receiveOrderService.getByReceiveOrderIds(receiveOrderIds, getUserId());
            ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, receiveOrderDetails);
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

//    /**
//     * 导出生产领料单
//     * @param receiveOrderIds
//     * @param fileName 配置的.json文件名称(带后缀)
//     * @return
//     */
//    @PostMapping("exportDeliveryOrder")
//    @OperationLog("导出")
//    @RequiresPermissions("mes:productionReport:exportDeliveryOrder")
//    public void exportDeliveryOrder(HttpServletRequest req, HttpServletResponse resp,
//                       @RequestParam("receiveOrderIds") Long[] receiveOrderIds,
//                       @RequestParam("fileName") String fileName){
//        logger.info("======== ProductionReportController.exportDeliveryOrder ========");
//        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
//        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
//        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
//        if(attachment != null) {
//            ReceiveOrderEO receiveOrder = this.receiveOrderService.getById(receiveOrderIds[0]);
//            String exportFileName = receiveOrder.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
//            jsonObject.put("fileName", exportFileName);
//            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.receiveOrderService.getDeliveryOrderDetails(receiveOrderIds, getUserId());
//            if(deliveryOrderDetails==null || deliveryOrderDetails.size()==0) {
//                try {
//                    resp.addHeader("msg", URLEncoder.encode("未找到对应的领料数据", "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
//                        jsonObject, deliveryOrderDetails);
//            }
//        }else{
//            try {
//                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 导出生产领料单
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("exportDeliveryOrder")
    @OperationLog("导出生产领料单")
    @RequiresPermissions("mes:productionReport:exportDeliveryOrder")
    public void exportDeliveryOrder(HttpServletRequest req, HttpServletResponse resp,
                                    @RequestBody DeliveryOrderDetailEO[] deliveryOrderDetails,
                                    @RequestParam("orgId") Long orgId,
                                    @RequestParam("fileName") String fileName){
        logger.info("======== ProductionReportController.exportDeliveryOrder ========");
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

    @PostMapping("getExportDeliveryOrder")
    public List<DeliveryOrderDetailEO> getExportDeliveryOrder(@RequestBody Long[] receiveOrderIds) {
        List<DeliveryOrderDetailEO> deliveryOrderDetails = this.receiveOrderService.getDeliveryOrderDetails(receiveOrderIds, getUserId());
        return deliveryOrderDetails;
    }

    /**
     * 查找所有符合条件的数据(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    public Result<List<ReceiveOrderEO>> list(@RequestBody Criteria criteria) {
        logger.info("======== ProductionReportController.list() ========");
        Integer countAll = this.receiveOrderService.countAll();
        criteria.setCurrentPage(1);
        criteria.setSize(countAll.intValue());
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<ReceiveOrderEO> page = this.receiveOrderService.selectPage(criteria);
        List<ReceiveOrderEO> list = page.getRecords();
        return new Result<List<ReceiveOrderEO>>().ok(list);
    }

    @PostMapping("syncU8ProductInStock")
    public Result syncU8ProductInStock(@RequestBody Long[] receiveOrderIds) {
        return this.receiveOrderService.syncU8ProductInStock(receiveOrderIds, getUser());
    }

    @PostMapping("syncU8MaterialOutStock")
    public Result syncU8MaterialOutStock(@RequestBody Long[] receiveOrderIds) {
        return this.deliveryOrderService.syncU8MaterialOutStock(receiveOrderIds, getUser());
    }

    /**
     * 完工日报
     *
     * @param criteria
     * @return
     */
    @PostMapping("produceInReport")
    public Result<IPage<ReceiveOrderDetailEO>> selectProduceInReport(@RequestBody Criteria criteria){
        logger.info("======== ProductOrderController.selectProduceInReport() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<ReceiveOrderDetailEO> page = this.receiveOrderService.selectProduceInReport(criteria);

        return new Result<IPage<ReceiveOrderDetailEO>>().ok(page);
    }

    /**
     * 查找所有符合条件的数据完工日报(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("listAll")
    public Result<List<ReceiveOrderDetailEO>> listAll(@RequestBody Criteria criteria) {
        logger.info("======== ProductionReportController.listAll() ========");
        Integer countAll = this.receiveOrderDetailService.countAll();
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("user_id", getUserId());
        map.put("currIndex", 1);
        map.put("pageSize", countAll);

        List<ReceiveOrderDetailEO> list = this.receiveOrderService.listAllDetails(map);

        return new Result<List<ReceiveOrderDetailEO>>().ok(list);
    }

    /**
     * 入库日报导出
     * @param request
     * @param response
     * @param receiveOrderDetails 需要导出的数据
     * @return
     */
    @PostMapping("exportProduceInReport")
    @OperationLog("导出")
    public Result exportProduceInReport(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody ReceiveOrderDetailEO[] receiveOrderDetails,
                              @RequestParam("fileName") String fileName){

        JSONObject jsonObject= ExcelUtils.parseJsonFile(fileName);

        ExcelUtils.exportExcel(response, Arrays.asList(receiveOrderDetails), jsonObject);

        return new Result();
    }
}
