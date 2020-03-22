package com.xchinfo.erp.controller.plm.mes;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.service.MaterialPlanService;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.srm.service.ProductOrderService;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.service.AttachmentService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author roman.c
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/mes/procedureImport")
public class ProcedureImportController extends BaseController {

    @Autowired
    private MaterialPlanService materialPlanService;


    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ProductOrderService productOrderService;


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



    @PostMapping("import")
    public Result importExcel(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        String robot = request.getParameter("robot");
        String rivet = request.getParameter("rivet");
        String handwork = request.getParameter("handwork");
        String punching = request.getParameter("punching");
        Date begindate = DateUtils.stringToDate(beginDate,"yyyy-MM-dd");
        Date enddate = DateUtils.stringToDate(endDate,"yyyy-MM-dd");
        String begintitle = DateUtils.format(begindate,"M月d日");

       /* this.productOrderService.importFromExcel(list,begintitle,getUser(),robot,rivet,handwork,punching);

        return  new Result();*/
        Result result =this.productOrderService.importFromExcel(list,begintitle,begindate,enddate,getUser(),robot,rivet,handwork,punching);

        return  result;
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

}

