package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.VehiclePlanEO;
import com.xchinfo.erp.mes.entity.VehicleTrainPlanEO;
import com.xchinfo.erp.mes.service.VehiclePlanService;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/6/20
 */
@RestController
@RequestMapping("/cmp/vehiclePlan")
public class VehiclePlanController extends BaseController {

    @Autowired
    private VehiclePlanService vehiclePlanService;



    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增")
    @RequiresPermissions("cmp:vehiclePlan:create")
    public Result create(@RequestBody VehiclePlanEO entity){
        logger.info("======== VehiclePlanController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setOrgId(getUser().getOrgId());
        this.vehiclePlanService.save(entity, getUserId());
        return new Result();
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<VehiclePlanEO> info(@PathVariable("id") Long id){
        logger.info("======== VehiclePlanController.info(entity => "+id+") ========");
        VehiclePlanEO entity = this.vehiclePlanService.getById(id);
        return new Result<VehiclePlanEO>().ok(entity);
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改")
    @RequiresPermissions("cmp:vehiclePlan:update")
    public Result update(@RequestBody VehiclePlanEO entity){
        logger.info("======== VehiclePlanController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.vehiclePlanService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除")
    @RequiresPermissions("cmp:vehiclePlan:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== VehiclePlanController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.vehiclePlanService.removeByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 发布
     *
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布")
    @RequiresPermissions("cmp:vehiclePlan:release")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== VehiclePlanController.release ========");
        this.vehiclePlanService.release(ids, getUserId());
        return new Result();
    }

    /**
     * 取消发布
     *
     * @param ids
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布")
    @RequiresPermissions("cmp:vehiclePlan:cancelRelease")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== VehiclePlanController.cancelRelease ========");
        this.vehiclePlanService.cancelRelease(ids, getUserId());
        return new Result();
    }


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("cmp:vehiclePlan:info")
    public Result page(@RequestBody Criteria criteria){
        logger.info("======== VehiclePlanController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        Map map= this.vehiclePlanService.selectNewPage(criteria,getUser());
        return new Result<Map>().ok(map);
    }


    /**
     * 生成排车计划
     * @param
     * @return
     */
    @PostMapping("createAllPlan/{date}")
    @OperationLog("生成排车计划")
    @RequiresPermissions("cmp:vehiclePlan:create")
    public Result createAllPlan(@PathVariable("date") String date){
        logger.info("======== VehiclePlanController.createAllPlan(date => "+date+") ========");

        this.vehiclePlanService.createAllPlan(date, getUser());
        return new Result();
    }



    /**
     * 修改保存
     *
     * @param
     * @return
     */
    @PostMapping("saveBatch")
    @OperationLog("修改保存")
    public Result saveBatch(@RequestBody VehiclePlanEO[] entitys){
        logger.info("======== VehiclePlanController.saveBatch ========");


        this.vehiclePlanService.saveOrUpdateBatch(Arrays.asList(entitys));

        return new Result();
    }

    /**
     * 设置车次信息
     *
     * @param
     * @return
     */
    @PostMapping("setTrain")
    @OperationLog("设置车次信息")
    public Result setTrainInfo(@RequestBody VehicleTrainPlanEO entity){
        logger.info("======== VehiclePlanController.setTrainInfo ========");


        this.vehiclePlanService.saveTrain(entity);

        return new Result();
    }


    /**
     * 根据日期找车次下拉
     * @param
     * @return
     */
    @GetMapping("selectTrains")
    public Result selectTrains(@RequestParam("date") String date,@RequestParam("setType") String setType){
        logger.info("======== VehiclePlanController.selectTrains(entity => "+date+") ========");

        List list = this.vehiclePlanService.selectTrains(date,setType,getUser());

        return new Result<List>().ok(list);
    }


    /**
     * 生产出库单
     * @param
     * @return
     */
    @PostMapping("addDeliveryOrder")
    @OperationLog("生存出库单")
    public Result addDeliveryOrder(@RequestParam("trainNumber") String trainNumber,@RequestBody Long[] ids){
        logger.info("======== VehiclePlanController.addDeliveryOrder ========");

        this.vehiclePlanService.addDeliveryOrder(trainNumber,ids,getUser());

        return new Result();
    }

    /**
     * 撤回出库单
     * @param
     * @return
     */
    @PostMapping("deleteDeliveryOrder")
    @OperationLog("撤回出库单")
    public Result deleteDeliveryOrder(@RequestParam("voucherNo") String voucherNo,@RequestParam("trainNumber") String trainNumber,@RequestParam("date") String date){
        logger.info("======== VehiclePlanController.deleteDeliveryOrder ========");

        this.vehiclePlanService.deleteDeliveryOrder(voucherNo,trainNumber,date);

        return new Result();
    }

    /**
     * projectNo 过滤条件查询
     * @param
     * @return
     */
    @GetMapping("projectNo/{date}")
    public Result getProjectNo(@PathVariable("date") String date){
        logger.info("======== VehiclePlanController.getProjectNo ========");


        List<VehiclePlanEO> list = this.vehiclePlanService.getProjectNo(date,getUser());

        return new Result<List<VehiclePlanEO>>().ok(list);
    }

    @PostMapping("import")
    @OperationLog("导入排车计划")
    public Result importExecl(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String date = request.getParameter("date");

        List<VehiclePlanEO> planEOList=  this.vehiclePlanService.importExecl(list,date,getUser());

        return new Result<List<VehiclePlanEO>>().ok(planEOList);
    }



    @PostMapping("importInsert")
    @OperationLog("批量新增排车计划")
    public Result importInsert(@RequestBody VehiclePlanEO[] entitys){

       String msg =  this.vehiclePlanService.importInsert(entitys,getUser());

       Result result = new Result();
       if(!msg.isEmpty()){
           result.setMsg(msg);
       }

        return result;
    }


    /**
     * 获取发货单信息
     * @param
     * @return
     */
    @GetMapping("getVoucherInfo/{value}")
    @OperationLog("获取发货单信息")
    public Result getVoucherInfo(@PathVariable("value") String voucherNo){
        logger.info("======== VehiclePlanController.getVoucherInfo(date => "+voucherNo+") ========");

        DeliveryOrderEO deliveryOrderEO = this.vehiclePlanService.getVoucherInfoByNo(voucherNo, getUser());

        return new Result<DeliveryOrderEO>().ok(deliveryOrderEO);
    }

    /**
     * 生成发货单U8导出单据号
     * @param
     * @return
     */
    @GetMapping("generateErpVoucherNo3/{value}")
    @OperationLog("生成发货单U8导出单据号")
    public Result generateErpVoucherNo3(@PathVariable("value") String voucherNo){
        logger.info("======== VehiclePlanController.generateErpVoucherNo3(voucherNo => "+voucherNo+") ========");
        DeliveryOrderEO deliveryOrderEO = this.vehiclePlanService.generateErpVoucherNo3(voucherNo);
        return new Result<DeliveryOrderEO>().ok(deliveryOrderEO);
    }

    @PostMapping("getMonthSum")
    public Result getMonthSum(@RequestParam("vehiclePlanIds") Long[] vehiclePlanIds, @RequestParam("date") String date) {
        List<VehiclePlanEO> vehiclePlans = this.vehiclePlanService.getByVehiclePlanIds(vehiclePlanIds);
        Map map = this.vehiclePlanService.getMonthSum(vehiclePlans, date, getUser(), new HashedMap());
        return new Result<Map>().ok(map);
    }
}
