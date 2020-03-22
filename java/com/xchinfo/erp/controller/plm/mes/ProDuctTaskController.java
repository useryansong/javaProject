package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.ProductTaskEO;
import com.xchinfo.erp.mes.service.ProductTaskService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelExportUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mes/productTask")
public class ProDuctTaskController extends BaseController {
    @Autowired
    private ProductTaskService productTaskService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:productTask:info")
    public Result<IPage<ProductTaskEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductTaskController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<ProductTaskEO> page = this.productTaskService.selectPage(criteria);

        return new Result<IPage<ProductTaskEO>>().ok(page);
    }
    /**
     * 导出
     * @param
     * @return
     */
    @PostMapping("export")
    @RequiresPermissions("mes:productTask:export")
    public Result<IPage<ProductTaskEO>> export(@RequestBody Criteria criteria, HttpServletResponse response){
        logger.info("======== ProductTaskController.page() ========");
        //Criteria criteria = JsonUtil.fromJson(paramStr,Criteria.class);
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        criteria.setSize(999999999);
        criteria.setCurrentPage(1);
        List<ProductTaskEO> records = this.productTaskService.selectPage(criteria).getRecords();
        if(records!=null){
            String[] column = {"采购流水号", "生产流水号", "委外流水号", "采购流水号", "父物料计划ID", "客户编码","客户名称",
                    "月份","计划类型","周计划日期","归属机构","物料编码","物料名称","零件号", "存货编码","规格型号",
                    "供应商编码","供应商名称","需求数量","生产数量","计划版本号","任务版本号","计划状态","执行状态","完成日期"
            };
            List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
            for (int i = 0; i < records.size(); i++) {
                ProductTaskEO item = records.get(i);
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.clear();
                map.put("采购流水号", CommonUtil.convertNullToEmpty(item.getSerialPurchaseCode())+"");
                map.put("生产流水号", CommonUtil.convertNullToEmpty(item.getSerialProductCode())+"");
                map.put("委外流水号", CommonUtil.convertNullToEmpty(item.getSerialOutSourceCode())+"");
                map.put("采购流水号", CommonUtil.convertNullToEmpty(item.getSerialPurchaseCode())+"");
                map.put("父物料计划ID", CommonUtil.convertNullToEmpty(item.getSerialId())+"");
                map.put("客户编码", CommonUtil.convertNullToEmpty(item.getCustomerCode())+"");
                map.put("客户名称", CommonUtil.convertNullToEmpty(item.getCustomerName())+"");
                map.put("月份", item.getMonthDate()==null?"":DateUtils.format(item.getMonthDate(),"yyyy-MM"));
                map.put("周计划日期", item.getWeekDate()==null?"":DateUtils.format(item.getWeekDate(),"yyyy-MM-dd"));
                map.put("归属机构", CommonUtil.convertNullToEmpty(item.getOrgName())+"");
                map.put("物料编码", CommonUtil.convertNullToEmpty(item.getMaterialCode())+"");
                map.put("物料名称", CommonUtil.convertNullToEmpty(item.getMaterialName())+"");
                map.put("零件号", CommonUtil.convertNullToEmpty(item.getElementNo())+"");
                map.put("存货编码", CommonUtil.convertNullToEmpty(item.getInventoryCode())+"");
                map.put("规格型号", CommonUtil.convertNullToEmpty(item.getSpecification())+"");
                map.put("供应商编码", CommonUtil.convertNullToEmpty(item.getSupplierCode())+"");
                map.put("供应商名称", CommonUtil.convertNullToEmpty(item.getSupplierName())+"");
                map.put("需求数量", CommonUtil.convertNullToEmpty(item.getRequireCount())+"");
                map.put("生产数量", CommonUtil.convertNullToEmpty(item.getProductionCount())+"");
                map.put("计划版本号", CommonUtil.convertNullToEmpty(item.getPlanVersion())+"");
                map.put("任务版本号", CommonUtil.convertNullToEmpty(item.getTaskVersion())+"");
                map.put("完成日期", item.getFinishDate()==null?"":DateUtils.format(item.getFinishDate(),"yyyy-MM-dd"));
                if (item.getPlanStatus()==0) {
                    map.put("计划状态", "待确认");
                }else if (item.getPlanStatus()==1) {
                    map.put("计划状态", "已确认");
                }else if (item.getPlanStatus()==2) {
                    map.put("计划状态", "计划变更");
                }

                if (item.getExeStatus()==null) {
                    map.put("执行状态", "未发布");
                }else{
                    if (item.getExeStatus()==0) {
                        map.put("执行状态", "待排产");
                    }else if (item.getExeStatus()==1) {
                        map.put("执行状态", "已排产");
                    }else if (item.getExeStatus()==2) {
                        map.put("执行状态", "执行中");
                    }else if (item.getExeStatus()==3) {
                        map.put("执行状态", "已完工");
                    }else if (item.getExeStatus()==4) {
                        map.put("执行状态", "已关闭");
                    }
                }
                String planTypeName = "";
                String distributeTypeName  = "";
                if (item.getPlanType()==1) {
                    planTypeName="月计划";
                }else if (item.getPlanType()==2) {
                    planTypeName="周计划";
                }
                if (item.getDistributeType()==1) {
                    distributeTypeName="采购";
                }else if (item.getDistributeType()==2) {
                    distributeTypeName="委外";
                }else if (item.getDistributeType()==3) {
                    distributeTypeName="生产";
                }
                String type = distributeTypeName+planTypeName;
                map.put("计划类型", type);
                list.add(map);
            }
            ExcelExportUtils.print(column, list, response,"生产任务");
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
    @RequiresPermissions("mes:productTask:info")
    public Result<ProductTaskEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductTaskController.info(productTask => "+id+") ========");

        ProductTaskEO entity = this.productTaskService.getById(id);

        return new Result<ProductTaskEO>().ok(entity);
    }

    /**
     * 发布
     * @param
     * @return
     */
    @PostMapping("publish")
    @OperationLog("发布")
    @RequiresPermissions("mes:productTask:publish")
    public Result publish(@RequestBody Long[] ids){
        logger.info("======== ProductTaskController.publish========");

        this.productTaskService.publish(ids);

        return new Result();
    }
    /**
     * 取消发布
     * @param
     * @return
     */
    @PostMapping("canclePublish")
    @OperationLog("取消发布")
    @RequiresPermissions("mes:productTask:canclePublish")
    public Result canclePublish(@RequestBody Long[] ids){
        logger.info("======== ProductTaskController.canclePublish========");

        this.productTaskService.publish(ids);

        return new Result();
    }
    /**
     * 确认计划
     * @param /*productTaskId 任务id
     * @param /*serialDistributeId 计划id
     * @return
     */
    @PostMapping("comfirm")
    @OperationLog("确认计划")
    @RequiresPermissions("mes:productTask:comfirm")
    public Result comfirm(@RequestBody Map map){
        logger.info("======== ProductTaskController.comfirm========");

        this.productTaskService.comfirm(map);

        return new Result();
    }

    /**
     * 关闭
     * @param /*productTaskId 任务id
     * @return
     */
    @PostMapping("close")
    @OperationLog("关闭")
    @RequiresPermissions("mes:productTask:close")
    public Result close(@RequestBody Long[] ids){
        logger.info("======== ProductTaskController.close========");

        this.productTaskService.close(ids);

        return new Result();
    }

    /**
     * 修改生产数量
     * @param /*productTaskId 任务id
     * @param /*productionCount 生产数量
     * @return
     */
    @PostMapping("updateNumber")
    @OperationLog("修改生产数量")
    @RequiresPermissions("mes:productTask:updateNumber")
    public Result updateNumber(@RequestBody Map map){
        logger.info("======== ProductTaskController.updateNumber========");

        this.productTaskService.updateNumber(map);

        return new Result();
    }
}
