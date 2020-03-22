package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.ProductionOrderDetailEO;
import com.xchinfo.erp.mes.entity.ProductionOrderEO;
import com.xchinfo.erp.mes.service.ProductionOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/mes/order")
public class ProductionOrderController extends BaseController {

    @Autowired
    private ProductionOrderService orderService;



    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:order:info")
    public Result<IPage<ProductionOrderEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductionOrderController.list() ========");

        IPage<ProductionOrderEO> page = this.orderService.selectPage(criteria);

        return new Result<IPage<ProductionOrderEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:order:info")
    public Result<ProductionOrderEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductionOrderController.info(entity => "+id+") ========");

        ProductionOrderEO entity = this.orderService.getById(id);

        return new Result<ProductionOrderEO>().ok(entity);
    }

    /**
     * 查找订单明细
     *
     * @param orderId
     * @return
     */
    @GetMapping("listDetails/{orderId}")
    @RequiresPermissions("mes:order:info")
    public Result<List<ProductionOrderDetailEO>> listDetails(@PathVariable("orderId") Long orderId){
        logger.info("======== ProductionOrderController.info(listDetails => "+orderId+") ========");

        List<ProductionOrderDetailEO> details = this.orderService.listDetailsByOrder(orderId);

        return new Result<List<ProductionOrderDetailEO>>().ok(details);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建订单")
    @RequiresPermissions("mes:order:create")
    public Result create(@RequestBody ProductionOrderEO entity){
        logger.info("======== ProductionOrderController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.orderService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新订单")
    @RequiresPermissions("mes:order:update")
    public Result update(@RequestBody ProductionOrderEO entity){
        logger.info("======== ProductionOrderController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.orderService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除订单")
    @RequiresPermissions("mes:order:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProductionOrderController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.orderService.removeByIds(ids);

        return new Result();
    }

    /**
     * 排产
     *
     * @param ids
     * @return
     */
    @PostMapping("schedule")
    @OperationLog("生产排产")
    @RequiresPermissions("mes:order:schedule")
    public Result schedule(@RequestBody Long[] ids){
        logger.info("======== ProductionOrderController.schedule() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        // 检查订单状态是否可排产
        for (Long id : ids){
            ProductionOrderEO order = this.orderService.getById(id);
            if (order.getStatus() != 1){
                throw new BusinessException("订单[" + order.getOrderNo() + "]状态不是新建或已排产，不能排产，请重新选择!");
            }
        }

        // 更新订单状态
        this.orderService.updateStatusForSchedule(ids);

        // 生产排产
        //this.jobService.schedule(ids, "delivery");

        return new Result();
    }
}
