package com.xchinfo.erp.controller.plm.mes;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.form.sys.MaterialPlanForm;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.service.MaterialPlanService;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.utils.FileUtils;
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
import java.lang.reflect.Array;
import java.util.*;


/**
 * @author roman.c
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/cmp/materialPlan")
public class MaterialPlanController extends BaseController {

    @Autowired
    private MaterialPlanService materialPlanService;


    @Autowired
    private AttachmentService attachmentService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    // @RequiresPermissions("cmp:materialPlan:info")
    public Result<IPage<MaterialPlanEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialPlanEO> page = this.materialPlanService.selectPage(criteria);

        return new Result<IPage<MaterialPlanEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    //@RequiresPermissions("cmp:materialPlan:info")
    public Result<MaterialPlanEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialPlanController.info(entity => "+id+") ========");

        MaterialPlanEO entity = this.materialPlanService.getById(id);

        return new Result<MaterialPlanEO>().ok(entity);
    }


    /**
     * 创建
     *
     * @param entitys
     * @return
     */
    @PostMapping
    @OperationLog("创建物料计划")
    //@RequiresPermissions("cmp:materialPlan:create")
    public Result create(@RequestBody MaterialPlanEO[] entitys){
        logger.info("======== MaterialPlanController.create ========");
        Long userId = getUserId();
        List<MaterialPlanEO> materialPlanEOList = new ArrayList<>();
        for(MaterialPlanEO entity:entitys) {
            entity.setOrgId(getUser().getOrgId());
            MaterialPlanEO tempEntity =  this.materialPlanService.saveEntity(entity,userId);
            materialPlanEOList.add(tempEntity);
        }
        return  new Result<List<MaterialPlanEO>>().ok(materialPlanEOList);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping("addMaterialPlan")
    @OperationLog("创建物料计划")
    //@RequiresPermissions("cmp:materialPlan:create")
    public Result addMaterialPlan(@RequestBody MaterialCustomerEO entity){
        logger.info("======== MaterialPlanController.create ========");


        this.materialPlanService.saveData(entity,getUser());

        return  new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新物料计划")
    // @RequiresPermissions("cmp:materialPlan:update")
    public Result update(@RequestBody MaterialPlanEO entity){
        logger.info("======== MaterialPlanController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialPlanService.updateById(entity,getUserId());

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除物料计划")
    //@RequiresPermissions("cmp:materialPlan:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialPlanService.removeByIds(ids);

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
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        String msg =  this.materialPlanService.updateStatusById(ids,1,"release");

        Result result = new Result();
        result.setMsg(msg);

        return result;
    }

    /**
     * 月计划发布
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/releaseMonth")
    @OperationLog("设置状态-发布")
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result releaseMonth(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.releaseMonth ========");
        AssertUtils.isArrayEmpty(ids, "id");
        return this.materialPlanService.releaseMonth(ids);
    }

    /**
     * 设置状态（取消发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelRelease")
    @OperationLog("设置状态-取消发布")
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");

        String msg =  this.materialPlanService.updateStatusById(ids,0,"cancelRelease");

        Result result = new Result();
        result.setMsg(msg);

        return result;
    }

    /**
     * 设置状态（取消发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelReleaseMonth")
    @OperationLog("设置状态-取消发布")
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result cancelReleaseMonth(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.cancelReleaseMonth ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.materialPlanService.cancelReleaseMonth(ids);
        return new Result();
    }


    /**
     * 设置状态（关闭）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/close")
    @OperationLog("设置状态-关闭")
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result updateStatusClose(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        String msg = this.materialPlanService.updateStatusById(ids,9,"close");

        Result result = new Result();
        result.setMsg(msg);

        return result;
    }

    /**
     * 设置状态（打开）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/open")
    @OperationLog("设置状态-打开")
    // @RequiresPermissions("cmp:materialPlan:update")
    public Result updateStatusOpen(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        String msg = this.materialPlanService.updateStatusById(ids,0,"open");

        Result result = new Result();
        result.setMsg(msg);

        return result;
    }

    /**
     * 设置状态（完成）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/finish")
    @OperationLog("设置状态-完成")
    //@RequiresPermissions("cmp:materialPlan:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        String msg = this.materialPlanService.updateStatusById(ids,3,"finish");

        Result result = new Result();
        result.setMsg(msg);

        return result;
    }


    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String monthDate = request.getParameter("monthDate");
//        String customerName = request.getParameter("customerName");
//        String customerId = request.getParameter("customerId");

        Date date = DateUtils.stringToDate(monthDate,"yyyy-MM");
//        Long Id = Long.valueOf(customerId);
        String title = DateUtils.format(date,"yyyy年M月");
        logger.info("======="+title);
        List<MaterialCustomerEO> entityList =  this.materialPlanService.importFromExcel(list,title,date,"",0l,getUser());

        return new Result<List<MaterialCustomerEO>>().ok(entityList);
    }

    /**
     * 下载模板文件
     * @param req
     * @param resp
     * @param id
     * @return
     */
    @GetMapping("downloadFile/{id}")
    public Result downloadPlanFile(HttpServletRequest req, HttpServletResponse resp, @PathVariable("id") Long id) {
        Result result = new Result();
        AttachmentEO attachmentEO = this.attachmentService.getById(id);
        if(attachmentEO != null){
            FileUtils.downloadFile(resp, attachmentEO.getAttachmentName(), attachmentEO.getAttachmentUrl());
        }else{
            result.setCode(500);
            result.setMsg("数据库配置出错");
        }
        return result;
    }


    /**
     * 查询物料计划中的客户信息+月份分页
     *
     * @param criteria
     * @return
     */
    @PostMapping("customerDate/Page")
    // @RequiresPermissions("wms:inventory:info")
    public Result<IPage<MaterialPlanEO>> selectMPlanCustomPage(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("userId");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialPlanEO> page = this.materialPlanService.selectMPlanCustomPage(criteria);

        return new Result<IPage<MaterialPlanEO>>().ok(page);
    }


    @PostMapping("week/import")
    public Result importFromWeekExcel(HttpServletRequest request){

        List weeklist = ExcelUtils.getExcelData(request);
        String monthDate = request.getParameter("monthDate");
        String customerName = request.getParameter("customerName");
        String customerId = request.getParameter("customerId");

        Date date = DateUtils.stringToDate(monthDate,"yyyy-MM");
        Long Id = Long.valueOf(customerId);

        List<MaterialCustomerEO> entityList =  this.materialPlanService.importFromWeekExcel(weeklist,date,customerName,Id);

        return new Result<List<MaterialCustomerEO>>().ok(entityList);
    }


    /**
     * 根据ID数组查询是否存在子计划
     *
     * @param
     * @return
     */
    @PostMapping("exist")
    // @RequiresPermissions("cmp:materialPlan:info")
    public Result selectExistCount(@RequestBody Long[] ids){

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialPlanService.selectExistCount(ids);

        return new Result();
    }


    /**
     * 导出报表
     * @param request
     * @param response
     * @param materialPlanEOS 需要导出的数据
     * @return
     */
    @PostMapping("week/export")
    public Result exportWeek(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialPlanEO[] materialPlanEOS){

        List<MaterialPlanEO> list = new ArrayList<>();

        JSONObject jsonObject = ExcelUtils.parseJsonFile("week_plan.json");
        //周计划需要根据月计划ID获取周计划具体数据
        for(MaterialPlanEO entity:materialPlanEOS){
            List<MaterialPlanEO> tempList =  this.materialPlanService.getWeekDetailList(entity.getParentSerialId(),entity.getOrgId());
            if(null != tempList && tempList.size() > 0){
                list.addAll(tempList);
            }
        }


        ExcelUtils.exportExcel(response, list, jsonObject);

        return new Result();
    }


    /**
     * 导出报表
     * @param request
     * @param response
     * @param materialPlanEOS 需要导出的数据
     * @return
     */
    @PostMapping("month/export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialPlanEO[] materialPlanEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("month_plan.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(materialPlanEOS), jsonObject);

        return new Result();
    }



    @PostMapping("plan/import")
    public Result importPlanExecl(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String monthDate = request.getParameter("monthDate");

        Result result =  this.materialPlanService.importPlanExecl(list,monthDate,getUser());

        return result;
    }


    /**
     * 创建
     *
     * @param entitys
     * @return
     */
    @PostMapping("batcahSave")
    @OperationLog("批量创建周计划")
    //@RequiresPermissions("cmp:materialPlan:create")
    public Result batcahSave(@RequestBody MaterialCustomerEO[] entitys){
        logger.info("======== PurchaseOrderController.batcahSave ========");
        Long userId = getUserId();

        this.materialPlanService.batcahSave(entitys,getUser());

        return  new Result();
    }




    /**
     * 周需求矩阵分页
     *
     * @param criteria
     * @return
     */
    @PostMapping("pageWeek")
    // @RequiresPermissions("cmp:materialPlan:info")
    public Result<IPage<MaterialPlanEO>> pageWeek(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.pageWeek() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialPlanEO> page = this.materialPlanService.pageWeek(criteria);

        return new Result<IPage<MaterialPlanEO>>().ok(page);
    }


    /**
     * 月需求矩阵分页
     *
     * @param criteria
     * @return
     */
    @PostMapping("pageMonth")
    // @RequiresPermissions("cmp:materialPlreleaseMonan:info")
    public Result<IPage<MaterialPlanEO>> pageMonth(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.pageMonth() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialPlanEO> page = this.materialPlanService.pageMonth(criteria);

        return new Result<IPage<MaterialPlanEO>>().ok(page);
    }




    /**
     * 根据月份查询所有未发布的id,再发布
     *
     * @param
     * @return
     */
    @PostMapping("getIdList")
    //@RequiresPermissions("cmp:materialPlan:info")
    public Result getIdList( @RequestParam("action") String action,@RequestParam("monthDate") String monthDate){
        logger.info("======== MaterialPlanController.getIdList========");

        return  this.materialPlanService.getIdList(action,monthDate,getUser());
    }

    /**
     * 查询导出
     *
     * @param ids
     * @return
     */
    @PostMapping("getPlanInfoByIds")
    public Result getPlanInfoByIds(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.getPlanInfoByIds() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        List<MaterialPlanEO> list  = this.materialPlanService.getPlanInfoByIds(ids);

        return new Result<List<MaterialPlanEO>>().ok(list);
    }

    /**
     * 根据月份删除所有的未发布的
     *
     * @param
     * @return
     */
    @PostMapping("deleteByIdList")
    public Result deleteByIdList(@RequestParam("monthDate") String monthDate){
        logger.info("======== MaterialPlanController.getIdList========");

        this.materialPlanService.deleteByIdList(monthDate,getUser());

        return new Result();
    }


    /**
     * 删除选择数据的月计划(非发布的)
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deleteByIds")
    @OperationLog("删除物料计划")
    public Result deleteByIds(@RequestBody Long[] ids){
        logger.info("======== MaterialPlanController.deleteByIds() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialPlanService.deleteByIds(ids,getUser());

        return new Result();
    }

    /**
     * 周计划发布
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/releaseWeek")
    @OperationLog("周计划发布")
    public Result releaseWeek(@RequestBody Long[] serialIds){
        logger.info("======== MaterialPlanController.releaseWeek ========");
        AssertUtils.isArrayEmpty(serialIds, "serialIds");
        return this.materialPlanService.releaseWeek(serialIds, getUser());
    }

    /**
     * 周计划子列表发布
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/releaseWeekSon")
    @OperationLog("周计划子列表发布")
    public Result releaseWeekSon(@RequestBody Long[] serialIds){
        logger.info("======== MaterialPlanController.releaseWeekSon ========");
        AssertUtils.isArrayEmpty(serialIds, "serialIds");
        return this.materialPlanService.releaseWeekSon(serialIds, getUser());
    }


    /**
     * 周计划取消发布
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelReleaseWeek")
    @OperationLog("周计划取消发布")
    public Result cancelReleaseWeek(@RequestBody Long[] serialIds){
        logger.info("======== MaterialPlanController.cancelReleaseWeek ========");
        AssertUtils.isArrayEmpty(serialIds, "parentSerialIds");
        return this.materialPlanService.cancelReleaseWeek(serialIds);
    }

    /**
     * 周计划子列表取消发布
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelReleaseWeekSon")
    @OperationLog("周计划子列表取消发布")
    public Result cancelReleaseWeekSon(@RequestBody Long[] serialIds){
        logger.info("======== MaterialPlanController.cancelReleaseWeekSon ========");
        AssertUtils.isArrayEmpty(serialIds, "serialIds");
        return this.materialPlanService.cancelReleaseWeekSon(serialIds);
    }

    /**
     * 周计划子列表修改数量
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/changeRequireCount")
    @OperationLog("周计划子列表修改数量")
    public Result changeRequireCount(@RequestBody MaterialPlanEO entity){
        logger.info("======== MaterialPlanController.changeRequireCount ========");
        return this.materialPlanService.changeRequireCount(entity);
    }

//    @PostMapping("updateStatus/insertOrders")
//    public Result insertOrders(@RequestBody MaterialPlanForm materialPlanForm){
//        logger.info("======== MaterialPlanController.insertOrders ========");
//        this.materialPlanService.insertOrders(materialPlanForm.getPurchaseOrders(),materialPlanForm.getOutsideOrders(),materialPlanForm.getProductOrders(),materialPlanForm.getSerialIds());
//        return new Result();
//    }
//
//    @PostMapping("getReleaseOrderCount")
//    public Result getReleaseOrderCount(@RequestBody Long[] serialIds){
//        logger.info("======== MaterialPlanController.getReleaseOrderCount ========");
//        return this.materialPlanService.getReleaseOrderCount(serialIds);
//    }




    /**
     * 客户月报
     * @param criteria
     * @return
     */
    @PostMapping("customerMonthReport")
    public Result selectCustomerMonthReport(@RequestBody Criteria criteria){
        logger.info("======== MaterialPlanController.selectCustomerMonthReport() ========");

        List<DeliveryOrderDetailEO> page = this.materialPlanService.selectCustomerMonthReport(criteria);

        return new Result<List<DeliveryOrderDetailEO>>().ok(page);
    }
}

