package com.xchinfo.erp.controller.hrms.apply;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.hrms.entity.ResignationApplyEO;
import com.xchinfo.erp.hrms.service.ResignationApplyService;
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
@RequestMapping("/hrms/resignation")
public class ResignationApplyController extends BaseController {

    @Autowired
    private ResignationApplyService resignationApplyService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("hrms:resignation:info")
    public Result<IPage<ResignationApplyEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ResignationApplyController.page() ========");

        IPage<ResignationApplyEO> page = this.resignationApplyService.selectPage(criteria);

        return new Result<IPage<ResignationApplyEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("hrms:resignation:info")
    public Result<ResignationApplyEO> info(@PathVariable("id") Long id){
        logger.info("======== ResignationApplyController.info(employee => "+id+") ========");

        ResignationApplyEO entity = this.resignationApplyService.getById(id);

        return new Result<ResignationApplyEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("hrms:resignation:create")
    @OperationLog("创建离职申请")
    public Result create(@RequestBody ResignationApplyEO entity){
        logger.info("======== ResignationApplyController.create() ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.resignationApplyService.save(entity);

        return new Result();
    }

    /**
     * 更新用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("hrms:resignation:update")
    @OperationLog("修改离职申请")
    public Result update(@RequestBody ResignationApplyEO entity){
        logger.info("======== ResignationApplyController.update(employee => " + entity.getEmployeeId() + ") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.resignationApplyService.updateById(entity);

        return new Result();
    }

    /**
     * 批量删除员工
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除离职申请")
    @RequiresPermissions("hrms:resignation:delete")
    public Result delete(@RequestBody Serializable[] ids){
        logger.info("======== ResignationApplyController.delete() ========");

        this.resignationApplyService.removeByIds(ids);

        return new Result();
    }
}
