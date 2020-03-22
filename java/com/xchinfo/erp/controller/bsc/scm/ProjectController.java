package com.xchinfo.erp.controller.bsc.scm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ProjectEO;
import com.xchinfo.erp.bsc.service.ProjectService;
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
import org.yecat.mybatis.utils.Criterion;
@RestController
@RequestMapping("/basic/project")
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService projectService;


    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:project:info")
    public Result<IPage<ProjectEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProjectController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<ProjectEO> page = this.projectService.selectPage(criteria);

        return new Result<IPage<ProjectEO>>().ok(page);
    }
 
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:project:info")
    public Result<ProjectEO> info(@PathVariable("id") Long id){
        logger.info("======== ProjectController.info(project => "+id+") ========");

        ProjectEO entity = this.projectService.getById(id);

        return new Result<ProjectEO>().ok(entity);
    }

    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @RequiresPermissions("basic:project:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== ProjectController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.projectService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @RequiresPermissions("basic:project:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== ProjectController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.projectService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("basic:project:create")
    public Result create(@RequestBody ProjectEO entity){
        logger.info("======== ProjectController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setStatus(0L);
        this.projectService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("basic:project:update")
    public Result update(@RequestBody ProjectEO entity){
        logger.info("======== ProjectController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.projectService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("basic:project:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProjectController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.projectService.removeByIds(ids);

        return new Result();
    }
}
