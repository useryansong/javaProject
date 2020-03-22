package com.xchinfo.erp.controller.plm.mes;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.WorkingGroupEO;
import com.xchinfo.erp.mes.service.WorkingGroupService;
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

import java.util.Map;

@RestController
@RequestMapping("/mes/workingGroup")
public class WorkingGroupController extends BaseController {
    @Autowired
    private WorkingGroupService workingGroupService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("mes:workingGroup:info")
    public Result<IPage<WorkingGroupEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WorkingGroupController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<WorkingGroupEO> page = this.workingGroupService.selectPage(criteria);

        return new Result<IPage<WorkingGroupEO>>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("mes:workingGroup:info")
    public Result<WorkingGroupEO> info(@PathVariable("id") Long id){
        logger.info("======== WorkingGroupController.info(workingGroup => "+id+") ========");

        WorkingGroupEO entity = this.workingGroupService.getById(id);

        return new Result<WorkingGroupEO>().ok(entity);
    }

    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @OperationLog("设置状态-启用")
    @RequiresPermissions("mes:workingGroup:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== WorkingGroupController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingGroupService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @OperationLog("设置状态-禁用")
    @RequiresPermissions("mes:workingGroup:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== WorkingGroupController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingGroupService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建班组")
    @RequiresPermissions("mes:workingGroup:create")
    public Result create(@RequestBody WorkingGroupEO entity){
        logger.info("======== WorkingGroupController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setStatus(0L);
        this.workingGroupService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新班组")
    @RequiresPermissions("mes:workingGroup:update")
    public Result update(@RequestBody WorkingGroupEO entity){
        logger.info("======== WorkingGroupController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.workingGroupService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除班组")
    @RequiresPermissions("mes:workingGroup:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WorkingGroupController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workingGroupService.removeByIds(ids);

        return new Result();
    }

    /**
     * 删除
     *
     * @param map
     * @return
     */
    @DeleteMapping("deleteWorkingGroupEmployees")
    @OperationLog("删除班组人员")
    @RequiresPermissions("mes:workingGroup:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result deleteWorkingGroupEmployees(@RequestBody Map map){
        logger.info("======== WorkingGroupController.deleteWorkingGroupEmployees() ========");


        int count =this.workingGroupService.removeWorkingGroupEmployeeByIds(map);

        return new Result();
    }

    /**
     * 添加班组人员
     *
     * @param map
     * @return
     */
    @DeleteMapping("addWorkingGroupEmployees")
    @OperationLog("添加班组人员")
    @RequiresPermissions("mes:workingGroup:create")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result addWorkingGroupEmployees(@RequestBody Map map){
        logger.info("======== WorkingGroupController.addWorkingGroupEmployees() ========");


        int count =this.workingGroupService.addWorkingGroupEmployeeByIds(map);

        return new Result();
    }
}
