package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MeasurementUnitEO;
import com.xchinfo.erp.bsc.service.MeasurementUnitService;
import com.xchinfo.erp.controller.BaseController;
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
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/measurement")
public class MeasurementUnitController extends BaseController {

    @Autowired
    private MeasurementUnitService measurementUnitService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:measurement:info")
    public Result<IPage<MeasurementUnitEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MeasurementUnitController.page() ========");

        IPage<MeasurementUnitEO> page = this.measurementUnitService.selectPage(criteria);

        return new Result<IPage<MeasurementUnitEO>>().ok(page);
    }

    /**
     * 查找列表
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<MeasurementUnitEO>> list(){
        logger.info("======== MeasurementUnitController.page() ========");

        List<MeasurementUnitEO> units = this.measurementUnitService.listAll();

        return new Result<List<MeasurementUnitEO>>().ok(units);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:measurement:info")
    public Result<MeasurementUnitEO> info(@PathVariable("id") Long id){
        logger.info("======== MeasurementUnitController.info(entity => "+id+") ========");

        MeasurementUnitEO entity = this.measurementUnitService.getById(id);

        return new Result<MeasurementUnitEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建计量单位")
    @RequiresPermissions("basic:measurement:create")
    public Result create(@RequestBody MeasurementUnitEO entity){
        logger.info("======== MeasurementUnitController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.measurementUnitService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新计量单位")
    @RequiresPermissions("basic:measurement:update")
    public Result update(@RequestBody MeasurementUnitEO entity){
        logger.info("======== MeasurementUnitController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.measurementUnitService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除计量单位")
    @RequiresPermissions("basic:measurement:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MeasurementUnitController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.measurementUnitService.removeByIds(ids);

        return new Result();
    }
}
