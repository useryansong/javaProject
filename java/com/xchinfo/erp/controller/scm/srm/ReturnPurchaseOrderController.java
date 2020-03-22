package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.srm.service.ReturnOrderDetailService;
import com.xchinfo.erp.srm.service.ReturnOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.mybatis.utils.Criteria;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/6/11
 */
@RestController
@RequestMapping("/srm/returnPurchaseOrder")
public class ReturnPurchaseOrderController extends BaseController {

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ReturnOrderDetailService returnOrderDetailService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:returnPurchaseOrder:info")
    public Result<IPage<ReturnOrderEO>> page(@RequestBody Criteria criteria) {
        logger.info("======== ReturnPurchaseOrderController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<ReturnOrderEO> totalList = this.returnOrderService.getPage(map);
        map.put("currentIndexFlag", true);
        List<ReturnOrderEO> pageList = this.returnOrderService.getPage(map);
        IPage<ReturnOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<ReturnOrderEO>>().ok(page);
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增退货单")
    @RequiresPermissions("srm:returnPurchaseOrder:create")
    public Result create(@RequestBody ReturnOrderEO entity) {
        logger.info("======== ReturnPurchaseOrderController.create(ID => " + entity.getId() + ") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        UserEO user = super.getUser();
        entity.setOrgId(user.getOrgId());
        ReturnOrderEO entityFromDb = this.returnOrderService.saveEntity(entity, user);
        return new Result<ReturnOrderEO>().ok(entityFromDb);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ReturnOrderEO> info(@PathVariable("id") Long id) {
        logger.info("======== ReturnPurchaseOrderController.info(entity => " + id + ") ========");
        ReturnOrderEO entity = this.returnOrderService.getById(id);
        return new Result<ReturnOrderEO>().ok(entity);
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping("update")
    @OperationLog("修改退货单")
    @RequiresPermissions("srm:returnPurchaseOrder:update")
    public Result update(@RequestBody ReturnOrderEO entity){
        logger.info("======== ReturnPurchaseOrderController.update(entity => " + entity.getId() + ") ========");
        this.returnOrderService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除退货单")
    @RequiresPermissions("srm:returnPurchaseOrder:delete")
    public Result delete(@RequestBody Long[] ids) {
        logger.info("======== ReturnPurchaseOrderController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.returnOrderService.removeByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 发布
     *
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布退货单")
    @RequiresPermissions("srm:returnPurchaseOrder:release")
    public Result release(@RequestBody Long[] ids) {
        logger.info("======== ReturnPurchaseOrderController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.returnOrderService.releaseByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 取消发布
     *
     * @param ids
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布退货单")
    @RequiresPermissions("srm:returnPurchaseOrder:cancelRelease")
    public Result cancelRelease(@RequestBody Long[] ids) {
        logger.info("======== ReturnPurchaseOrderController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.returnOrderService.cancelReleaseByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 打印(修改状态)
     * @param returnOrder
     * @return
     */
    @PostMapping("print")
    @OperationLog("打印")
    @RequiresPermissions("srm:returnPurchaseOrder:print")
    public Result print(@RequestBody ReturnOrderEO returnOrder){
        logger.info("======== ReturnPurchaseOrderController.print() ========");
        this.returnOrderService.updateStatusByPrint(returnOrder, getUserId());
        return new Result();
    }

    /**
     * 导出
     * @param request
     * @param response
     * @param returnOrders 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("srm:returnPurchaseOrder:export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody ReturnOrderEO[] returnOrders){
        this.returnOrderService.checkExport(returnOrders, getUserId());

        //配置的.json文件
        JSONObject jsonObject = ExcelUtils.parseJsonFile("return_purchase_order.json");
        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(returnOrders), jsonObject);
    }

    /**
     * 导出采购退货单
     * @param returnOrderIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("exportToReturnOrder")
    @OperationLog("导出为采购订单")
    @RequiresPermissions("srm:returnPurchaseOrder:exportToReturnOrder")
    public void exportToReturnOrder(HttpServletRequest req, HttpServletResponse resp,
                                      @RequestBody Long[] returnOrderIds,
                                      @RequestParam("fileName") String fileName){
        logger.info("======== ReturnPurchaseOrderController.exportToReturnOrder ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            ReturnOrderEO returnOrder = this.returnOrderService.getById(returnOrderIds[0]);
            String exportFileName = returnOrder.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            String[] fileNames = fileName.split("/");
            List<ReturnOrderDetailEO> returnOrderDetails = this.returnOrderDetailService.getByReturnOrderIdsAndUserId(returnOrderIds, getUserId(), true, fileNames[1]);
            ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, returnOrderDetails);
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查找所有符合条件的退货单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    public Result<List<ReturnOrderEO>> list(@RequestBody Criteria criteria) {
        logger.info("======== ReturnPurchaseOrderController.list() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<ReturnOrderEO> totalList = this.returnOrderService.getPage(map);
        return new Result<List<ReturnOrderEO>>().ok(totalList);
    }

    /**
     * 确认
     * @param ids
     * @return
     */
    @PostMapping("confirm")
    @OperationLog("确认")
    @RequiresPermissions("srm:returnPurchaseOrder:update")
    public Result confirm(@RequestBody Long[] ids){
        logger.info("======== ReturnPurchaseOrderController.confirm ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        UserEO user = super.getUser();
        this.returnOrderService.confirm(ids, user);
        return new Result();
    }


    /***
     * 通过流水获取退货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    @RequiresPermissions("srm:returnPurchaseOrder:info")
    public Result<ReturnOrderEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== ReturnPurchaseOrderController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        ReturnOrderEO entity = this.returnOrderService.getDetailInfoByNo(voucherNo);
        return new Result<ReturnOrderEO>().ok(entity);
    }

    /**
     * 单个退货（采购，委外）
     * @param
     * @return
     */
    @PostMapping("app/return")
    @OperationLog("退货(采购，委外)")
    @RequiresPermissions("srm:returnPurchaseOrder:update")
    public Result returnOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== ReturnPurchaseOrderController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.returnOrderService.returnOne(Id,amount,userId,userName,action);

        return new Result();
	}
}
