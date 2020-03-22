package com.xchinfo.erp.controller.scm.srm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.CheckBillDetailEO;
import com.xchinfo.erp.scm.srm.entity.CheckBillEO;
import com.xchinfo.erp.srm.service.CheckBillService;
import com.xchinfo.erp.utils.ExcelUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuanchang
 * @date 2019/10/18
 * @update
 */
@RestController
@RequestMapping("/srm/customerCheckBill")
public class CustomerCheckBillController extends BaseController {

    @Autowired
    private CheckBillService checkBillService;


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<CheckBillEO>> page(@RequestBody Criteria criteria){
        logger.info("======== CustomerCheckBillController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<CheckBillEO> page = this.checkBillService.selectPage(criteria);

        return new Result<IPage<CheckBillEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<CheckBillEO> info(@PathVariable("id") Long id){
        logger.info("======== CustomerCheckBillController.info(entity => "+id+") ========");

        CheckBillEO entity = this.checkBillService.getById(id);

        return new Result<CheckBillEO>().ok(entity);
    }

    /**
     * 根据ID查找不含明细数据
     *
     * @param checkId
     * @return
     */
    @GetMapping("checkId/{checkId}")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<CheckBillEO> checkBillinfo(@PathVariable("checkId") Long checkId){
        logger.info("======== CheckBillController.info(entity => "+checkId+") ========");

        CheckBillEO entity = this.checkBillService.getByCheckId(checkId);

        return new Result<CheckBillEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
    public Result<List<CheckBillDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== CustomerCheckBillController.info(listDetails => "+orderId+") ========");

        List<CheckBillDetailEO> details = this.checkBillService.listDetailsByOrder(orderId);

        return new Result<List<CheckBillDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建入库订单")
    //@RequiresPermissions("wms:receiveOrder:create")
    public Result create(@RequestBody CheckBillEO entity){
        logger.info("======== CustomerCheckBillController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        CheckBillEO checkBillEO = this.checkBillService.saveEntity(entity);

        return new Result<CheckBillEO>().ok(checkBillEO);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新入库订单")
    public Result update(@RequestBody CheckBillEO entity){
        logger.info("======== CustomerCheckBillController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        CheckBillEO checkBillEO = this.checkBillService.updateById(entity,getUserId());

        return new Result<CheckBillEO>().ok(checkBillEO);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除对账单")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== CustomerCheckBillController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.checkBillService.removeByIds(ids);

        return new Result();
    }


    /**
     * 明细分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("detail/page")
    public Result<IPage<CheckBillDetailEO>> detailPage(@RequestBody Criteria criteria){
        logger.info("======== CustomerCheckBillController.list() ========");

        IPage<CheckBillDetailEO> page = this.checkBillService.selectDetailPage(criteria);

        return new Result<IPage<CheckBillDetailEO>>().ok(page);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("detail/{checkId}")
    @OperationLog("删除入库订单明细")
    public Result deleteDetail(@RequestBody Long[] ids,@PathVariable("checkId") Long checkId){
        logger.info("======== CustomerCheckBillController.deleteDetail() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.checkBillService.removeByDetailIds(ids,checkId);

        return new Result();
    }



    /**
     * 根据ID查找明细
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result<CheckBillDetailEO> detailInfo(@PathVariable("id") Long id){
        logger.info("======== CustomerCheckBillController.info(entity => "+id+") ========");

        CheckBillDetailEO entity = this.checkBillService.getDetailById(id);

        return new Result<CheckBillDetailEO>().ok(entity);
    }


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("select/page")
    public Result<List<CheckBillDetailEO>> selectPage(@RequestBody Criteria criteria){
        logger.info("======== CustomerCheckBillController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        List<CheckBillDetailEO> page = this.checkBillService.selectCustomerPageByCondition(criteria);

        return new Result<List<CheckBillDetailEO>>().ok(page);
    }

    /**
     * 已加入条目查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("hasSelect/page")
    public Result<List<CheckBillDetailEO>> hasSelectPage(@RequestBody Criteria criteria){
        logger.info("======== CustomerCheckBillController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        List<CheckBillDetailEO> page = this.checkBillService.hasSelectPage(criteria);

        return new Result<List<CheckBillDetailEO>>().ok(page);
    }

    /**
     * 查找有审批权限的用户
     *
     * @param
     * @return
     */
    @GetMapping("getApprovers")
    public Result<List<Map>> getApprovers(){

        List<Map> list = this.checkBillService.getApprovers();

        return new Result<List<Map>>().ok(list);
    }

    /**
     * 提交
     *
     * @param
     * @return
     */
    @PostMapping("submit")
    public Result submit(@RequestBody Map map){
        logger.info("======== CustomerCheckBillController.submit(map) ========");

        boolean res = this.checkBillService.submit(map);

        return new Result().ok(res);
    }

    /**
     * 审批完成
     *
     * @param
     * @return
     */
    @PostMapping("approve")
    public Result approve(@RequestBody Map map){
        logger.info("======== CustomerCheckBillController.approve(map) ========");

        boolean res = this.checkBillService.approve(map);

        return new Result().ok(res);
    }

    /**
     * 审批并提交
     *
     * @param
     * @return
     */
    @PostMapping("approveSubmit")
    public Result approveSubmit(@RequestBody Map map){
        logger.info("======== CustomerCheckBillController.approveSubmit(map) ========");

        boolean res = this.checkBillService.approveSubmit(map);

        return new Result().ok(res);
    }

    /**
     * 查找需打印的订单明细
     *
     * @param id
     * @return
     */
    @PostMapping("exportData")
    //@RequiresPermissions("wms:receiveOrder:info")
    public Result<List<CheckBillDetailEO>> exportDate(@RequestParam("id") Long id){
        logger.info("======== CustomerCheckBillController.info(listDetails => "+id+") ========");

        List<CheckBillDetailEO> details = this.checkBillService.listexportDataByCheckId(id);

        return new Result<List<CheckBillDetailEO>>().ok(details);
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param CheckBillDetailEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody CheckBillDetailEO[] CheckBillDetailEOS){
        logger.info("======== CustomerCheckBillController.exportMonth ========");
        JSONObject jsonObject= ExcelUtils.parseJsonFile("CustomerCheckBillDetail.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(CheckBillDetailEOS), jsonObject);

        return new Result();
    }

    /**
     * 查找对账开始日期
     * @return
     */
    @PostMapping("getBeginDate")
    public Result<CheckBillEO> getBeginDate(@RequestParam("id") Long id,@RequestParam("endDate") String endDate,@RequestParam("checkType") Integer checkType){
        logger.info("======== CustomerCheckBillController.getBeginDate(entity => "+id+") ========");

        CheckBillEO entity = this.checkBillService.getBeginDate(id,endDate,checkType);

        return new Result<CheckBillEO>().ok(entity);
    }

}
