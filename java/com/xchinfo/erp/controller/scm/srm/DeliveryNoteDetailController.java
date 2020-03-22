package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.srm.service.DeliveryNoteDetailService;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.utils.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@RestController
@RequestMapping("/srm/deliveryNoteDetail")
public class DeliveryNoteDetailController extends BaseController {

    @Autowired
    private DeliveryNoteDetailService deliveryNoteDetailService;

    @Autowired
    private AttachmentService attachmentService;


    /**
     * 批量加入送货单明细
     * @param deliveryPlanIds
     * @param deliveryNoteId
     * @return
     */
    @PostMapping("addBatch")
    @OperationLog("批量加入送货单明细")
    public Result addBatch(@RequestParam("deliveryPlanIds") Long[] deliveryPlanIds,
                           @RequestParam("deliveryNoteId") Long deliveryNoteId){
        logger.info("======== DeliveryNoteDetailController.addBatch ========");
        String msg = this.deliveryNoteDetailService.addBatch(deliveryPlanIds, deliveryNoteId);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<DeliveryNoteDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryNoteDetailController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<DeliveryNoteDetailEO> totalList = this.deliveryNoteDetailService.getList(map);
        map.put("currentIndexFlag", true);
        List<DeliveryNoteDetailEO> pageList = this.deliveryNoteDetailService.getList(map);
        IPage<DeliveryNoteDetailEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<DeliveryNoteDetailEO>>().ok(page);
    }

    /**
     * 根据送货单id查询全部送货单明细
     * @param deliveryNoteId
     * @return
     */
    @PostMapping("list")
    public Result<List<DeliveryNoteDetailEO>> list(@RequestBody Long deliveryNoteId){
        logger.info("======== DeliveryNoteDetailController.list() ========");
        Map map = new HashMap();
        map.put("deliveryNoteId", deliveryNoteId);
        List<DeliveryNoteDetailEO> list = this.deliveryNoteDetailService.getList(map);
        return new Result<List<DeliveryNoteDetailEO>>().ok(list);
    }

    /**
     * 批量更新送货单明细
     * @param deliveryNoteDetails
     * @return
     */
    @PostMapping("updateBatch")
    @OperationLog("批量更新送货单明细")
    public Result updateBatch(@RequestBody DeliveryNoteDetailEO[] deliveryNoteDetails){
        logger.info("======== DeliveryNoteDetailController.updateBatch ========");
        String msg = this.deliveryNoteDetailService.updateBatch(deliveryNoteDetails);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除送货单明细")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DeliveryNoteDetailController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteDetailService.removeByIds(ids);
        return new Result();
    }

    /**
     * 根据送货计划id查询全部送货单明细
     * @param deliveryPlanId
     * @return
     */
    @GetMapping("deliveryPlan/{deliveryPlanId}")
    public Result<List<DeliveryNoteDetailEO>> listByDeliveryPlanId(@PathVariable("deliveryPlanId") Long deliveryPlanId){
        logger.info("======== DeliveryNoteDetailController.listByDeliveryPlanId() ========");
        Map map = new HashMap();
        map.put("deliveryPlanId", deliveryPlanId);
        List<DeliveryNoteDetailEO> list = this.deliveryNoteDetailService.getList(map);
        return new Result<List<DeliveryNoteDetailEO>>().ok(list);
    }


    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DeliveryNoteDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryNoteDetailController.info(entity => "+id+") ========");
        DeliveryNoteDetailEO entity = this.deliveryNoteDetailService.getById(id);
        return new Result<DeliveryNoteDetailEO>().ok(entity);
    }

    /**
     * 更新送货明细
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新送货明细")
    public Result update(@RequestBody DeliveryNoteDetailEO entity){
        logger.info("======== DeliveryNoteDetailController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.deliveryNoteDetailService.updateById(entity);

        return new Result();
    }


    /**
     * 导出
     * @param deliveryNoteIds
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestParam("deliveryNoteIds") Long[] deliveryNoteIds,
                       @RequestParam("fileName") String fileName){
        logger.info("======== DeliveryNoteDetailController.export ========");
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        Long attachmentId = ((Integer)jsonObject.get("templateId")).longValue();
        AttachmentEO attachment = this.attachmentService.getById(attachmentId);
        if(attachment != null){
            List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getByDeliveryNoteIds(deliveryNoteIds);
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
}
