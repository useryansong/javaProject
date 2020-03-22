package com.xchinfo.erp.controller.bsc.hrms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.hrms.entity.PositionEO;
import com.xchinfo.erp.hrms.service.PositionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@RestController
@RequestMapping("/hrms/position")
public class PositionController extends BaseController {

    @Autowired
    private PositionService positionService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("hrms:position:info")
    public Result<IPage<PositionEO>> page(@RequestBody Criteria criteria){
        logger.info("======== PositionController.page() ========");

        IPage<PositionEO> page = this.positionService.selectPage(criteria);

        return new Result<IPage<PositionEO>>().ok(page);
    }
    /**
     * 分页查询
     *
     * @param map
     * @return
     */
    @PostMapping("pageNew")
    @RequiresPermissions("hrms:employee:info")
    public Result<Pagination> pageNew(@RequestBody Map map){
        logger.info("======== EmployeeController.page() ========");

        Pagination page = this.positionService.selectPage(map);

        return new Result<Pagination>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("hrms:position:info")
    public Result<PositionEO> info(@PathVariable("id") Long id){
        logger.info("======== PositionController.info(position => "+id+") ========");

        PositionEO entity = this.positionService.getById(id);

        return new Result<PositionEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("hrms:position:create")
    @OperationLog("创建岗位")
    public Result create(@RequestBody PositionEO entity){
        logger.info("======== PositionController.create() ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.positionService.save(entity);

        return new Result();
    }

    /**
     * 更新用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("hrms:position:update")
    @OperationLog("修改岗位")
    public Result update(@RequestBody PositionEO entity){
        logger.info("======== PositionController.update(position => " + entity.getPositionId() + ") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.positionService.updateById(entity);

        return new Result();
    }

    /**
     * 批量删除员工
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除岗位")
    @RequiresPermissions("hrms:position:delete")
    public Result delete(@RequestBody Serializable[] ids){
        logger.info("======== PositionController.delete() ========");

        this.positionService.removeByIds(ids);

        return new Result();
    }

    /**
     * 同步数据
     *
     * @return
     */
    @GetMapping("sync")
    @RequiresPermissions("hrms:position:sync")
    public Result syncHR(){
        logger.info("======== PositionController.syncHR() ========");

        return this.positionService.syncHR();
    }
}
