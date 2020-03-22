package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.ProductionTaskEO;
import com.xchinfo.erp.mes.entity.ProductionTaskTreeEO;
import com.xchinfo.erp.mes.service.ProductionTaskService;
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

import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/mes/task")
public class ProductionTaskController extends BaseController {

    @Autowired
    private ProductionTaskService taskService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:task:info")
    public Result<IPage<ProductionTaskEO>> page(@RequestBody Criteria criteria){
        logger.info("======== TaskController.list() ========");

        IPage<ProductionTaskEO> page = this.taskService.selectPage(criteria);

        return new Result<IPage<ProductionTaskEO>>().ok(page);
    }

    /**
     * 以树形结构查询
     *
     * @return
     */
    @GetMapping("tree")
    @RequiresPermissions("mes:task:info")
    public Result<List<ProductionTaskTreeEO>> tree(){
        logger.info("======== TaskController.tree() ========");

        List<ProductionTaskTreeEO> tasks = this.taskService.listForTree();

        return new Result<List<ProductionTaskTreeEO>>().ok(tasks);
    }

    /**
     * 根据日期查找任务
     *
     * @param queryDate
     * @return
     */
    @GetMapping("listForDate")
    @RequiresPermissions("mes:task:info")
    public Result<List<ProductionTaskEO>> listForDate(@RequestParam("queryDate") String queryDate){
        logger.info("======== TaskController.listForDate(date => " + queryDate+ ") ========");

        List<ProductionTaskEO> tasks = this.taskService.listForDate(queryDate);

        return new Result<List<ProductionTaskEO>>().ok(tasks);
    }

    /**
     * 根据订单查找任务
     *
     * @param orderId
     * @return
     */
    @GetMapping("listForOrder")
    @RequiresPermissions("mes:task:info")
    public Result<List<ProductionTaskEO>> listForOrder(@RequestParam("orderId") Long orderId){
        logger.info("======== TaskController.listForDate() ========");

        List<ProductionTaskEO> tasks = this.taskService.listForOrder(orderId);

        return new Result<List<ProductionTaskEO>>().ok(tasks);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:task:info")
    public Result<ProductionTaskEO> info(@PathVariable("id") Long id){
        logger.info("======== TaskController.info(entity => "+id+") ========");

        ProductionTaskEO entity = this.taskService.getById(id);

        return new Result<ProductionTaskEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建任务")
    @RequiresPermissions("mes:task:create")
    public Result create(@RequestBody ProductionTaskEO entity){
        logger.info("======== TaskController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.taskService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新任务")
    @RequiresPermissions("mes:task:update")
    public Result update(@RequestBody ProductionTaskEO entity){
        logger.info("======== TaskController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.taskService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除任务")
    @RequiresPermissions("mes:task:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== TaskController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.taskService.removeByIds(ids);

        return new Result();
    }
}
