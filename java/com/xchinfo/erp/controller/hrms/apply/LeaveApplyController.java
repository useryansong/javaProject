package com.xchinfo.erp.controller.hrms.apply;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.hrms.entity.LeaveApplyEO;
import com.xchinfo.erp.hrms.service.LeaveApplyService;
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
@RequestMapping("/hrms/leave")
public class LeaveApplyController extends BaseController {

    @Autowired
    private LeaveApplyService leaveApplyService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("hrms:leave:info")
    public Result<IPage<LeaveApplyEO>> page(@RequestBody Criteria criteria){
        logger.info("======== LeaveApplyController.page() ========");

        IPage<LeaveApplyEO> page = this.leaveApplyService.selectPage(criteria);

        return new Result<IPage<LeaveApplyEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("hrms:leave:info")
    public Result<LeaveApplyEO> info(@PathVariable("id") Long id){
        logger.info("======== LeaveApplyController.info(employee => "+id+") ========");

        LeaveApplyEO entity = this.leaveApplyService.getById(id);

        return new Result<LeaveApplyEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("hrms:leave:create")
    @OperationLog("创建请假申请")
    public Result create(@RequestBody LeaveApplyEO entity){
        logger.info("======== LeaveApplyController.create() ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.leaveApplyService.save(entity);

        return new Result();
    }

    /**
     * 更新用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("hrms:leave:update")
    @OperationLog("修改请假申请")
    public Result update(@RequestBody LeaveApplyEO entity){
        logger.info("======== LeaveApplyController.update(employee => " + entity.getEmployeeId() + ") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.leaveApplyService.updateById(entity);

        return new Result();
    }

    /**
     * 批量删除员工
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除请假申请")
    @RequiresPermissions("hrms:leave:delete")
    public Result delete(@RequestBody Serializable[] ids){
        logger.info("======== LeaveApplyController.delete() ========");

        this.leaveApplyService.removeByIds(ids);

        return new Result();
    }
}
