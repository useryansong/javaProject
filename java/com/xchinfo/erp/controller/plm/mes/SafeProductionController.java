package com.xchinfo.erp.controller.plm.mes;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.SafeProductionEO;
import com.xchinfo.erp.mes.service.SafeProductionService;
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
/**
 * 安全生产记录controller
 */
@RestController
@RequestMapping("/mes/safeProduction")
public class SafeProductionController extends BaseController {
    @Autowired
    private SafeProductionService safeProductionService;
    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:safeProduction:info")
    public Result<IPage<SafeProductionEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SafeProductionController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<SafeProductionEO> page = this.safeProductionService.selectPage(criteria);

        return new Result<IPage<SafeProductionEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:safeProduction:info")
    public Result<SafeProductionEO> info(@PathVariable("id") Long id){
        logger.info("======== SafeProductionController.info(safeProduction => "+id+") ========");

        SafeProductionEO entity = this.safeProductionService.getById(id);

        return new Result<SafeProductionEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建工序工时")
    @RequiresPermissions("mes:safeProduction:create")
    public Result create(@RequestBody SafeProductionEO entity){
        logger.info("======== SafeProductionController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.safeProductionService.save(entity);

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
    @RequiresPermissions("mes:safeProduction:update")
    public Result update(@RequestBody SafeProductionEO entity){
        logger.info("======== SafeProductionController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.safeProductionService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除工序工时")
    @RequiresPermissions("mes:safeProduction:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SafeProductionController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.safeProductionService.removeByIds(ids);

        return new Result();
    }
}
