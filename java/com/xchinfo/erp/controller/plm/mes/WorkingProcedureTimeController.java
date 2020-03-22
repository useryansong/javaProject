package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.mes.service.WorkingProcedureTimeService;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelExportUtils;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.JsonUtil;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mes/workingProcedureTime")
public class WorkingProcedureTimeController extends BaseController {
    @Autowired
    private WorkingProcedureTimeService workingProcedureTimeService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:workingProcedureTime:info")
    public Result<IPage<WorkingProcedureTimeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WorkingProcedureTimeController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<WorkingProcedureTimeEO> page = this.workingProcedureTimeService.selectPage(criteria);

        return new Result<IPage<WorkingProcedureTimeEO>>().ok(page);
    }
    /**
     * 导出
     * @param
     * @return
     */
    @PostMapping("export")
    @RequiresPermissions("mes:workingProcedureTime:export")
    public Result<IPage<WorkingProcedureTimeEO>> export(@RequestBody Criteria criteria, HttpServletResponse response){
        logger.info("======== WorkingProcedureTimeController.page() ========");
        //Criteria criteria = JsonUtil.fromJson(paramStr,Criteria.class);
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        criteria.setSize(999999999);
        criteria.setCurrentPage(1);
        List<WorkingProcedureTimeEO> records = this.workingProcedureTimeService.selectPage(criteria).getRecords();
        if(records!=null){
            String[] column = {"项目", "产品编码", "零部件名称", "工序号", "工序名称", "生产车间", "设备编号", "设备名称",
                    "设备名称","操作人数","人员性质","辆份数量","模腔数量","现有CT(S)","单件CT(S)","状态","最后更新人","最后更新时间"
            };
            List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();

            for (int i = 0; i < records.size(); i++) {
                WorkingProcedureTimeEO item = records.get(i);
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.clear();
                map.put("项目", CommonUtil.convertNullToEmpty(item.getProject())+"");
                map.put("产品编码", CommonUtil.convertNullToEmpty(item.getElementNo())+"");
                map.put("零部件名称", CommonUtil.convertNullToEmpty(item.getMaterialName())+"");
                map.put("工序号", CommonUtil.convertNullToEmpty(item.getWorkingProcedureCode())+"");
                map.put("工序名称", CommonUtil.convertNullToEmpty(item.getWorkingProcedureName())+"");
                map.put("工序类型", CommonUtil.convertNullToEmpty(item.getWorkingProcedureTypeName())+"");
                map.put("生产车间", CommonUtil.convertNullToEmpty(item.getWorkshopName())+"");
                map.put("设备编号", CommonUtil.convertNullToEmpty(item.getCapitalAssetsNo())+"");
                map.put("设备名称", CommonUtil.convertNullToEmpty(item.getMachineName())+"");
                map.put("操作人数", CommonUtil.convertNullToEmpty(item.getOperationNumber())+"");
                map.put("人员性质", CommonUtil.convertNullToEmpty(item.getMenTypeName())+"");
                map.put("辆份数量", CommonUtil.convertNullToEmpty(item.getLfNumber())+"");
                map.put("模腔数量", CommonUtil.convertNullToEmpty(item.getMqNumber())+"");
                map.put("现有CT(S)", CommonUtil.convertNullToEmpty(item.getCt())+"");
                map.put("单件CT(S)", CommonUtil.convertNullToEmpty(item.getCtPer())+"");
                map.put("人员性质", CommonUtil.convertNullToEmpty(item.getMenTypeName())+"");
                map.put("最后更新人", item.getLastModifiedBy()+"");
                map.put("最后更新时间", DateUtils.format(item.getLastModifiedTime(),DateUtils.DATE_TIME_PATTERN)+"");
                if (item.getStatus().equals("1")) {
                    map.put("状态", "禁用");
                } else {
                    map.put("状态", "启用");
                }
                list.add(map);
            }
            ExcelExportUtils.print(column, list, response,"工序工时数据");
        }
        return null;
    }
    /**
     * 分页查询
     *
     * @param map
     * @return
     */
    @PostMapping("pageNew")
    @RequiresPermissions("mes:workingProcedureTime:info")
    public Result<Pagination> pageNew(@RequestBody Map map){
        logger.info("======== WorkingProcedureTimeController.page() ========");

        Pagination page = this.workingProcedureTimeService.selectPage(map);

        return new Result<Pagination>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:workingProcedureTime:info")
    public Result<WorkingProcedureTimeEO> info(@PathVariable("id") Long id){
        logger.info("======== WorkingProcedureTimeController.info(workingProcedureTime => "+id+") ========");

        WorkingProcedureTimeEO entity = this.workingProcedureTimeService.getById(id);

        return new Result<WorkingProcedureTimeEO>().ok(entity);
    }

    @GetMapping("hasProject")
    public Result<List<Map>> hasProject(){
        UserEO user = ShiroUtils.getUser();
        List<Map> list = this.workingProcedureTimeService.hasProject(user.getUserId());

        return new Result<List<Map>>().ok(list);
    }

    @GetMapping("hasWorkShop")
    public Result<List<Map>> hasWorkShop(@Param("orgId") Long orgId){
        UserEO user = ShiroUtils.getUser();
        Map map = new HashMap();
        map.put("orgId",orgId);
        map.put("userId",user.getUserId());
        List<Map> list = this.workingProcedureTimeService.hasWorkShop(map);

        return new Result<List<Map>>().ok(list);
    }
    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @OperationLog("设置状态-启用")
    @RequiresPermissions("mes:workingProcedureTime:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== WorkingProcedureTimeController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingProcedureTimeService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @OperationLog("设置状态-禁用")
    @RequiresPermissions("mes:workingProcedureTime:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== WorkingProcedureTimeController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingProcedureTimeService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建工序工时")
    @RequiresPermissions("mes:workingProcedureTime:create")
    public Result create(@RequestBody WorkingProcedureTimeEO entity){
        logger.info("======== WorkingProcedureTimeController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setStatus(0L);
        this.workingProcedureTimeService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新工序工时")
    @RequiresPermissions("mes:workingProcedureTime:update")
    public Result update(@RequestBody WorkingProcedureTimeEO entity){
        logger.info("======== WorkingProcedureTimeController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        return this.workingProcedureTimeService.update(entity);

    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除工序工时")
    @RequiresPermissions("mes:workingProcedureTime:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WorkingProcedureTimeController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingProcedureTimeService.removeByIds(ids);

        return new Result();
    }

}
