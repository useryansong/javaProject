package com.xchinfo.erp.controller.scm.srm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.srm.service.ReturnOrderDetailService;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.utils.Criteria;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/24
 */
@RestController
@RequestMapping("/srm/returnOrderDetail")
public class ReturnOrderDetailController extends BaseController {

    @Autowired
    private ReturnOrderDetailService returnOrderDetailService;

    /**
     * 分页查找(退货单明细)
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<ReturnOrderDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ReturnOrderDetailController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<ReturnOrderDetailEO> totalList = this.returnOrderDetailService.getPage(map);
        map.put("currentIndexFlag", true);
        List<ReturnOrderDetailEO> pageList = this.returnOrderDetailService.getPage(map);
        IPage<ReturnOrderDetailEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<ReturnOrderDetailEO>>().ok(page);
    }

    /**
     * 批量加入退货单明细
     * @param purchaseOrderIds
     * @param returnOrderId
     * @return
     */
    @PostMapping("addBatch")
    @OperationLog("批量加入退货单明细")
    public Result addBatch(@RequestParam("purchaseOrderIds") Long[] purchaseOrderIds,
                           @RequestParam("returnOrderId") Long returnOrderId){
        logger.info("======== ReturnOrderDetailController.addBatch ========");
        String msg = this.returnOrderDetailService.addBatch(purchaseOrderIds, returnOrderId);
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ReturnOrderDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== ReturnOrderDetailController.info(entity => "+id+") ========");
        ReturnOrderDetailEO entity = this.returnOrderDetailService.getById(id);
        return new Result<ReturnOrderDetailEO>().ok(entity);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除退货单明细")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ReturnOrderDetailController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.returnOrderDetailService.removeByIds(ids);
        return new Result();
    }

    /**
     * 更新退货单明细
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新退货单明细")
    public Result update(@RequestBody ReturnOrderDetailEO entity){
        logger.info("======== ReturnOrderDetailController.update========");
        this.returnOrderDetailService.updateById(entity);
        return new Result();
    }

    /**
     * 更新状态
     * @param id
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("取消发布出库单明细")
    public Result finishRelease(@RequestBody Long id){
        logger.info("======== ReturnOrderDetailController.finishRelease() ========");
        this.returnOrderDetailService.updateStatusById(id, 1);

        this.returnOrderDetailService.updateReturnOrderStatusById(id,3);

        return new Result();
    }
}
