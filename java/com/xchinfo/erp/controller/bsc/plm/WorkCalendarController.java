package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WorkCalendarEO;
import com.xchinfo.erp.bsc.service.WorkCalendarService;
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

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/workCalendar")
public class WorkCalendarController extends BaseController {

    @Autowired
    private WorkCalendarService workCalendarService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:workCalendar:info")
    public Result<IPage<WorkCalendarEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WorkCalendarController.list() ========");

        IPage<WorkCalendarEO> page = this.workCalendarService.selectPage(criteria);

        return new Result<IPage<WorkCalendarEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:workCalendar:info")
    public Result<WorkCalendarEO> info(@PathVariable("id") Long id){
        logger.info("======== WorkCalendarController.info(entity => "+id+") ========");

        WorkCalendarEO entity = this.workCalendarService.getById(id);

        return new Result<WorkCalendarEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建工作日历")
    @RequiresPermissions("basic:workCalendar:create")
    public Result create(@RequestBody WorkCalendarEO entity){
        logger.info("======== WorkCalendarController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.workCalendarService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新工作日历")
    @RequiresPermissions("basic:workCalendar:update")
    public Result update(@RequestBody WorkCalendarEO entity){
        logger.info("======== WorkCalendarController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.workCalendarService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除工作日历")
    @RequiresPermissions("basic:workCalendar:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WorkCalendarController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workCalendarService.removeByIds(ids);

        return new Result();
    }
}
