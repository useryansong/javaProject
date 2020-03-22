package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ConstraintEO;
import com.xchinfo.erp.bsc.service.ConstraintService;
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
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/basic/constraint")
public class ConstraintController extends BaseController {
    
    @Autowired
    private ConstraintService constraintService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:constraint:info")
    public Result<IPage<ConstraintEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ConstraintController.list() ========");

        IPage<ConstraintEO> page = this.constraintService.selectPage(criteria);

        return new Result<IPage<ConstraintEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:constraint:info")
    public Result<ConstraintEO> info(@PathVariable("id") Long id){
        logger.info("======== ConstraintController.info(entity => "+id+") ========");

        ConstraintEO entity = this.constraintService.getById(id);

        return new Result<ConstraintEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建约束")
    @RequiresPermissions("basic:constraint:create")
    public Result create(@RequestBody ConstraintEO entity){
        logger.info("======== ConstraintController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.constraintService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新约束")
    @RequiresPermissions("basic:constraint:update")
    public Result update(@RequestBody ConstraintEO entity){
        logger.info("======== ConstraintController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.constraintService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除约束")
    @RequiresPermissions("basic:constraint:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ConstraintController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.constraintService.removeByIds(ids);

        return new Result();
    }
}
