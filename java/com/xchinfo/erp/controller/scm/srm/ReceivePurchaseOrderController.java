package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.srm.service.DeliveryNoteDetailService;
import com.xchinfo.erp.srm.service.DeliveryNoteService;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/6/11
 */
@RestController
@RequestMapping("/srm/receivePurchaseOrder")
public class ReceivePurchaseOrderController extends BaseController {

    @Autowired
    private DeliveryNoteService deliveryNoteService;

    @Autowired
    private DeliveryNoteDetailService deliveryNoteDetailService;

    @Autowired
    private AttachmentService attachmentService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:receivePurchaseOrder:info")
    public Result<IPage<DeliveryNoteEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ReceivePurchaseOrderController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
//        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        Integer count = this.deliveryNoteService.getCount(map);
        map.put("currentIndexFlag", true);
        List<DeliveryNoteEO> pageList = this.deliveryNoteService.getPage(map);
        IPage<DeliveryNoteEO> page = CommonUtil.listToPage(count, pageList, map);
        return new Result<IPage<DeliveryNoteEO>>().ok(page);
    }

    // 私有导出方法
    private void export(HttpServletResponse resp, String fileName, Long[] deliveryNoteIds, Boolean isFilterByActualReceiveQuantity) {
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            DeliveryNoteEO deliveryNote = this.deliveryNoteService.getById(deliveryNoteIds[0]);
            String exportFileName = deliveryNote.getOrgName() + jsonObject.get("fileName") + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
            jsonObject.put("fileName", exportFileName);
            String[] fileNames = fileName.split("/");
            List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getByDeliveryNoteIdsAndUserId(deliveryNoteIds, getUserId(), isFilterByActualReceiveQuantity, fileNames[1]);
            ExcelUtils.exportByTemplate(resp, attachment.getAttachmentUrl(), attachment.getAttachmentName(),
                    jsonObject, deliveryNoteDetails);
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
     * @param deliveryNoteIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("srm:receivePurchaseOrder:export")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody Long[] deliveryNoteIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== ReceivePurchaseOrderController.export ========");
        export(resp, fileName, deliveryNoteIds, false);
    }

    /**
     * 导出为采购订单
     * @param deliveryNoteIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("exportToPurchaseOrder")
    @OperationLog("导出为采购订单")
    @RequiresPermissions("srm:receivePurchaseOrder:exportToPurchaseOrder")
    public void exportToPurchaseOrder(HttpServletRequest req, HttpServletResponse resp,
                       @RequestBody Long[] deliveryNoteIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== ReceivePurchaseOrderController.exportToPurchaseOrder ========");
        export(resp, fileName, deliveryNoteIds, true);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DeliveryNoteEO> info(@PathVariable("id") Long id){
        logger.info("======== ReceivePurchaseOrderController.info(entity => "+id+") ========");
        DeliveryNoteEO entity = this.deliveryNoteService.selectDetailById(id);
        return new Result<DeliveryNoteEO>().ok(entity);
    }

    /**
     * 查找所有符合条件的送货单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    public Result<List<DeliveryNoteEO>> list(@RequestBody Criteria criteria) {
        logger.info("======== ReceivePurchaseOrderController.list() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        return new Result<List<DeliveryNoteEO>>().ok(totalList);
    }

    /**
     * 修改U8采购订单单据号
     * @param deliveryNote
     * @return
     */
    @PostMapping("changeErpVoucherNo2")
    public Result changeErpVoucherNo2(@RequestBody DeliveryNoteEO deliveryNote) {
        logger.info("======== ReceivePurchaseOrderController.changeErpVoucherNo2() ========");
        this.deliveryNoteService.changeErpVoucherNo2(deliveryNote);
        return new Result();
    }

    @PostMapping("synU8ArrivedGoods")
    public Result synU8ArrivedGoods(@RequestBody Long[] deliveryNoteIds) {
        return this.deliveryNoteService.synU8ArrivedGoods(deliveryNoteIds, getUser());
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    public Result<IPage<DeliveryNoteDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== ReceivePurchaseOrderController.detailPage() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        Integer count = this.deliveryNoteDetailService.getCount(map);
        map.put("currentIndexFlag", true);
        List<DeliveryNoteDetailEO> pageList = this.deliveryNoteDetailService.getPage(map);
        IPage<DeliveryNoteDetailEO> page = CommonUtil.listToPage(count, pageList, map);
        return new Result<IPage<DeliveryNoteDetailEO>>().ok(page);
    }
}
