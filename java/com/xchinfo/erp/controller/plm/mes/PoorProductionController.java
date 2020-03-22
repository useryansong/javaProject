package com.xchinfo.erp.controller.plm.mes;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.PoorMaterialVO;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.PoorProductionEO;
import com.xchinfo.erp.mes.service.PoorProductionService;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
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

@RestController
@RequestMapping("/mes/poorProduction")
public class PoorProductionController  extends BaseController {
    @Autowired
    private PoorProductionService poorProductionService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private OrgService orgService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:poorProduction:info")
    public Result<IPage<PoorProductionEO>> page(@RequestBody Criteria criteria){
        logger.info("======== PoorProductionController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<PoorProductionEO> page = this.poorProductionService.selectPage(criteria);

        return new Result<IPage<PoorProductionEO>>().ok(page);
    }
 
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:poorProduction:info")
    public Result<PoorProductionEO> info(@PathVariable("id") Long id){
        logger.info("======== PoorProductionController.info(poorProduction => "+id+") ========");

        PoorProductionEO entity = this.poorProductionService.getById(id);

        return new Result<PoorProductionEO>().ok(entity);
    }
    @PostMapping("getExportData")
    public Result<List<PoorMaterialVO>> getExportData(@RequestBody Long[] ids) {
        return this.poorProductionService.getExportData(ids);
    }

    /**
     * 导出
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("exportPoorProduction")
    @OperationLog("导出")
    public Result export(HttpServletRequest req, HttpServletResponse resp, @RequestBody PoorProductionEO[] poorProductions, @RequestParam("fileName") String fileName){
        logger.info("======== ProductionReportController.exportPoorProduction ========");
        JSONObject jsonObject= ExcelUtils.parseJsonFile(fileName);
        List<PoorProductionEO> list = Arrays.asList(poorProductions);
        for(PoorProductionEO poorProduction : list) {
            if(poorProduction.getPoorHandle().intValue() == 0) {
                poorProduction.setPoorHandleName("报废");
            }
            if(poorProduction.getPoorHandle().intValue() == 1) {
                poorProduction.setPoorHandleName("返修");
            }
        }

        //导出Excel
        ExcelUtils.exportExcel(resp, list, jsonObject);

        return new Result();
    }

    /**
     * 导出生产领料单
     * @param fileName 配置的.json文件名称(带后缀)
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出不良物料单")
    @RequiresPermissions("mes:poorProduction:export")
    public void exportDeliveryOrder(HttpServletRequest req, HttpServletResponse resp,
                                    @RequestBody List<PoorMaterialVO> list,
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
                    jsonObject, list);
        }else{
            try {
                resp.addHeader("msg", URLEncoder.encode("数据库配置出错", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发布
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @RequiresPermissions("mes:poorProduction:release")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== PoorProductionController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.poorProductionService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 撤销
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @RequiresPermissions("mes:poorProduction:revoke")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== PoorProductionController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.poorProductionService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("mes:poorProduction:create")
    public Result create(@RequestBody PoorProductionEO entity){
        logger.info("======== PoorProductionController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setStatus(1);//
        this.poorProductionService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("mes:poorProduction:update")
    public Result update(@RequestBody PoorProductionEO entity){
        logger.info("======== PoorProductionController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.poorProductionService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("mes:poorProduction:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== PoorProductionController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.poorProductionService.removeByIds(ids);

        return new Result();
    }

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("listAll")
    public Result<List<PoorProductionEO>> listAll(@RequestBody Criteria criteria){
        logger.info("======== PoorProductionController.list() ========");

        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId());

        List<PoorProductionEO> list = this.poorProductionService.listAll(map);

        return new Result<List<PoorProductionEO>>().ok(list);
    }
}
