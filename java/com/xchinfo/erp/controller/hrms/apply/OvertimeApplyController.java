package com.xchinfo.erp.controller.hrms.apply;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.hrms.entity.OvertimeApplyEO;
import com.xchinfo.erp.hrms.service.OvertimeApplyService;
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

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@RestController
@RequestMapping("/hrms/overtime")
public class OvertimeApplyController extends BaseController {

    @Autowired
    private OvertimeApplyService overtimeApplyService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("hrms:overtime:info")
    public Result<IPage<OvertimeApplyEO>> page(@RequestBody Criteria criteria){
        logger.info("======== OvertimeApplyController.page() ========");

        IPage<OvertimeApplyEO> page = this.overtimeApplyService.selectPage(criteria);

        return new Result<IPage<OvertimeApplyEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("hrms:overtime:info")
    public Result<OvertimeApplyEO> info(@PathVariable("id") Long id){
        logger.info("======== OvertimeApplyController.info(employee => "+id+") ========");

        OvertimeApplyEO entity = this.overtimeApplyService.getById(id);

        return new Result<OvertimeApplyEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("hrms:overtime:create")
    @OperationLog("创建加班申请")
    public Result create(@RequestBody OvertimeApplyEO entity){
        logger.info("======== OvertimeApplyController.create() ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.overtimeApplyService.save(entity);

        return new Result();
    }

    /**
     * 更新用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("hrms:overtime:update")
    @OperationLog("修改加班申请")
    public Result update(@RequestBody OvertimeApplyEO entity){
        logger.info("======== OvertimeApplyController.update(employee => " + entity.getEmployeeId() + ") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.overtimeApplyService.updateById(entity);

        return new Result();
    }

    /**
     * 批量删除员工
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除加班申请")
    @RequiresPermissions("hrms:overtime:delete")
    public Result delete(@RequestBody Serializable[] ids){
        logger.info("======== OvertimeApplyController.delete() ========");

        this.overtimeApplyService.removeByIds(ids);

        return new Result();
    }
}
