package com.xchinfo.erp.controller.plm.mes;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.mes.service.MaterialDistributeService;
import com.xchinfo.erp.utils.ExcelUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * @author roman.c
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/cmp/materialDistribute")
public class MaterialDistributeController extends BaseController {


    @Autowired
    private MaterialDistributeService materialDistributeService;
    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("cmp:materialDistribute:info")
    public Result<IPage<MaterialDistributeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialDistributeController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialDistributeEO> page = this.materialDistributeService.selectPage(criteria);

        return new Result<IPage<MaterialDistributeEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    //@RequiresPermissions("cmp:materialDistribute:info")
    public Result<MaterialDistributeEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialDistributeController.info(entity => "+id+") ========");

        MaterialDistributeEO entity = this.materialDistributeService.getById(id);

        return new Result<MaterialDistributeEO>().ok(entity);
    }


    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建计划")
    //@RequiresPermissions("cmp:materialDistribute:create")
    public Result create(@RequestBody MaterialDistributeEO entity){
        logger.info("======== MaterialDistributeController.create ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.materialDistributeService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新计划")
    //@RequiresPermissions("cmp:materialDistribute:update")
    public Result update(@RequestBody MaterialDistributeEO entity){
        logger.info("======== MaterialDistributeController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialDistributeService.updateById(entity,getUserId());

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除计划")
    //@RequiresPermissions("cmp:materialDistribute:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialDistributeController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialDistributeService.removeByIds(ids);

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
    //@RequiresPermissions("cmp:materialDistribute:update")
    public Result updateStatusRelease(@RequestBody Long[] ids){
        logger.info("======== MaterialDistributeController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");
        String userName = getUserName();
        Long userId = getUserId();
        String resultMsg = this.materialDistributeService.updateStatusById(ids,1,"release",userId,userName);

        Result result = new Result();
        result.setMsg(resultMsg);

        return result;
    }

    /**
     * 设置状态（取消发布）
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus/cancelRelease")
    @OperationLog("设置状态-取消发布")
    //@RequiresPermissions("cmp:materialDistribute:update")
    public Result updateStatusCancelRelease(@RequestBody Long[] ids){
        logger.info("======== MaterialDistributeController.updateStatus ========");

        AssertUtils.isArrayEmpty(ids, "id");
        String userName = getUserName();
        Long userId = getUserId();
        String resultMsg = this.materialDistributeService.updateStatusById(ids,0,"cancelRelease",userId,userName);

        Result result = new Result();
        result.setMsg(resultMsg);

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
    //@RequiresPermissions("cmp:materialDistribute:update")
    public Result updateStatusFinish(@RequestBody Long[] ids){
        logger.info("======== MaterialDistributeController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");
        String userName = getUserName();
        Long userId = getUserId();
        String resultMsg = this.materialDistributeService.updateStatusById(ids,3,"finish",userId,userName);

        return new Result();
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param materialDistributeEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    public Result export(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialDistributeEO[] materialDistributeEOS){

        Integer type = materialDistributeEOS[0].getDistributeType();
        JSONObject jsonObject;

        //子类型;1-采购；2-委外；3-生产
        if (type == 3){
            jsonObject =  ExcelUtils.parseJsonFile("product_plan.json");
        }else if(type == 1){
            jsonObject = ExcelUtils.parseJsonFile("purchase_plan.json");
        }else{
            jsonObject = ExcelUtils.parseJsonFile("out_plan.json");
        }

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(materialDistributeEOS), jsonObject);


        return new Result();
    }


    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request){
        List list = ExcelUtils.getExcelData(request);
        String monthDate = request.getParameter("monthDate");

        List<MaterialSupplierEO> entityList =  this.materialDistributeService.importPlanExecl(list,monthDate);

        return new Result<List<MaterialSupplierEO>>().ok(entityList);
    }




}
