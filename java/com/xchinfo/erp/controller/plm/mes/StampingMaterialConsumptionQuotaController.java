package com.xchinfo.erp.controller.plm.mes;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.mes.service.StampingMaterialConsumptionQuotaService;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelExportUtils;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.utils.StringUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/mes/stampingMaterialConsumptionQuota")
public class StampingMaterialConsumptionQuotaController  extends BaseController {
    @Autowired
    private StampingMaterialConsumptionQuotaService stampingMaterialConsumptionQuotaService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:info")
    public Result<IPage<StampingMaterialConsumptionQuotaEO>> page(@RequestBody Criteria criteria){
        logger.info("======== StampingMaterialConsumptionQuotaController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<StampingMaterialConsumptionQuotaEO> page = this.stampingMaterialConsumptionQuotaService.selectPage(criteria);

        return new Result<IPage<StampingMaterialConsumptionQuotaEO>>().ok(page);
    }
    /**
     * 导出
     * @param
     * @return
     */
    @PostMapping("export")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:export")
    public Result<IPage<StampingMaterialConsumptionQuotaEO>> export(@RequestBody Criteria criteria, HttpServletResponse response){
        logger.info("======== StampingMaterialConsumptionQuotaController.page() ========");
        //Criteria criteria = JsonUtil.fromJson(paramStr,Criteria.class);
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        criteria.setSize(999999999);
        criteria.setCurrentPage(1);
        List<StampingMaterialConsumptionQuotaEO> records = this.stampingMaterialConsumptionQuotaService.selectPage(criteria).getRecords();
        if(records!=null){
            String[] column = {"项目", "零件号", "零部件名称", "材料牌号", "材料规格", "料厚", "料宽", "公差",
                    "卷料","板料","步距","每条板可冲零件数量","单个零件净重（kg）","单个零件毛重（kg）","状态","最后更新人","最后更新时间"
            };
            List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
            for (int i = 0; i < records.size(); i++) {
                StampingMaterialConsumptionQuotaEO item = records.get(i);
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.clear();
                map.put("项目", CommonUtil.convertNullToEmpty(item.getProject())+"");
                map.put("零件号", CommonUtil.convertNullToEmpty(item.getElementNo())+"");
                map.put("零部件名称", CommonUtil.convertNullToEmpty(item.getMaterialName())+"");
                map.put("材料牌号", CommonUtil.convertNullToEmpty(item.getMaterialPcode())+"");
                map.put("材料规格", CommonUtil.convertNullToEmpty(item.getMaterialSpecific())+"");
                map.put("料厚", CommonUtil.convertNullToEmpty(item.getThickness())+"");
                map.put("料宽", CommonUtil.convertNullToEmpty(item.getWidth())+"");
                map.put("公差", CommonUtil.convertNullToEmpty(item.getTolerance())+"");
                map.put("卷料", CommonUtil.convertNullToEmpty(item.getCoilMaterial())+"");
                map.put("板料", CommonUtil.convertNullToEmpty(item.getSheetMetal())+"");
                map.put("步距", CommonUtil.convertNullToEmpty(item.getStepDistance())+"");
                map.put("每条板可冲零件数量", CommonUtil.convertNullToEmpty(item.getNumberOfPunchablePartsPerBoard())+"");
                map.put("单个零件净重（kg）", CommonUtil.convertNullToEmpty(item.getNetWeight())+"");
                map.put("单个零件毛重（kg）", CommonUtil.convertNullToEmpty(item.getGrossWeight())+"");
                map.put("最后更新人", item.getLastModifiedBy()+"");
                map.put("最后更新时间", DateUtils.format(item.getLastModifiedTime(),DateUtils.DATE_TIME_PATTERN)+"");
                if (item.getStatus().equals("1")) {
                    map.put("状态", "禁用");
                } else {
                    map.put("状态", "启用");
                }
                list.add(map);
            }
            ExcelExportUtils.print(column, list, response,"冲压材料耗用定额数据");
        }
        return null;
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:info")
    public Result<StampingMaterialConsumptionQuotaEO> info(@PathVariable("id") Long id){
        logger.info("======== StampingMaterialConsumptionQuotaController.info(stampingMaterialConsumptionQuota => "+id+") ========");

        StampingMaterialConsumptionQuotaEO entity = this.stampingMaterialConsumptionQuotaService.getById(id);

        return new Result<StampingMaterialConsumptionQuotaEO>().ok(entity);
    }

    @GetMapping("hasProject")
    public Result<List<Map>> hasProject(){
        UserEO user = ShiroUtils.getUser();
        List<Map> list = this.stampingMaterialConsumptionQuotaService.hasProject(user.getUserId());

        return new Result<List<Map>>().ok(list);
    }
    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @OperationLog("设置状态-启用")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== StampingMaterialConsumptionQuotaController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.stampingMaterialConsumptionQuotaService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @OperationLog("设置状态-禁用")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== StampingMaterialConsumptionQuotaController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.stampingMaterialConsumptionQuotaService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建冲压材料耗用定额")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:create")
    public Result create(@RequestBody StampingMaterialConsumptionQuotaEO entity){
        logger.info("======== StampingMaterialConsumptionQuotaController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        // 总成物料ID+原材料物料ID判断唯一性
        StampingMaterialConsumptionQuotaEO smcq = this.stampingMaterialConsumptionQuotaService.getByMaterialIdAndOriginalMaterialId(entity.getMaterialId(), entity.getOriginalMaterialId());
        if(smcq != null) {
            throw new BusinessException("该数据已存在,请检查是否重复!");
        }

        entity.setStatus(0L);
        this.stampingMaterialConsumptionQuotaService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新冲压材料耗用定额")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:update")
    public Result update(@RequestBody StampingMaterialConsumptionQuotaEO entity){
        logger.info("======== StampingMaterialConsumptionQuotaController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        // 总成物料ID+原材料物料ID判断唯一性
        StampingMaterialConsumptionQuotaEO smcq = this.stampingMaterialConsumptionQuotaService.getByMaterialIdAndOriginalMaterialId(entity.getMaterialId(), entity.getOriginalMaterialId());
        if(smcq!=null && smcq.getStampingMaterialConsumptionQuotaId().longValue()!=entity.getStampingMaterialConsumptionQuotaId().longValue()) {
            throw new BusinessException("该数据已存在,请检查是否重复!");
        }

        this.stampingMaterialConsumptionQuotaService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除冲压材料耗用定额")
    @RequiresPermissions("mes:stampingMaterialConsumptionQuota:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== StampingMaterialConsumptionQuotaController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.stampingMaterialConsumptionQuotaService.removeByIds(ids);

        return new Result();
    }

    /**
     * 投入产出(冲压)
     * @param criteria
     * @return
     */
    @PostMapping("inandoutputsReport")
    public Result selectInandoutputsReport(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.selectCustomerMonthReport() ========");

        List<StampingMaterialConsumptionQuotaEO> page = this.stampingMaterialConsumptionQuotaService.selectInandoutputsReport(criteria);

        return new Result<List<StampingMaterialConsumptionQuotaEO>>().ok(page);
    }

    /**
     * 投入产出(焊装)
     * @param criteria
     * @return
     */
    @PostMapping("biwReport")
    public Result biwReport(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.selectCustomerMonthReport() ========");

        IPage<StampingMaterialConsumptionQuotaEO> page = this.stampingMaterialConsumptionQuotaService.biwReport(criteria);

        return new Result<IPage<StampingMaterialConsumptionQuotaEO>>().ok(page);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @return
     */
    @PostMapping("exportbiw")
    @OperationLog("导出报表")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody StampingMaterialConsumptionQuotaEO[] StampingMaterialConsumptionQuotaEOs){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("stampingMaterialConsumptionQuota.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(StampingMaterialConsumptionQuotaEOs), jsonObject);

        return new Result();
    }
}
