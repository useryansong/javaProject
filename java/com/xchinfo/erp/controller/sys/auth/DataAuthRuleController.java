package com.xchinfo.erp.controller.sys.auth;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.DataAuthRuleEO;
import com.xchinfo.erp.sys.auth.service.DataAuthRuleService;
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
 * @date 2019/4/24
 * @update
 */
@RestController
@RequestMapping("/sys/dataAuthRule")
public class DataAuthRuleController extends BaseController {

    @Autowired
    private DataAuthRuleService dataAuthRuleService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:dataAuthRule:info")
    public Result<IPage<DataAuthRuleEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DataAuthRuleController.page() ========");

        IPage<DataAuthRuleEO> page = this.dataAuthRuleService.selectPage(criteria);

        return new Result<IPage<DataAuthRuleEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<DataAuthRuleEO>> list(@RequestParam("dataEntry") String dataEntry){
        logger.info("======== DataAuthRuleController.list() ========");

        List<DataAuthRuleEO> customers = this.dataAuthRuleService.listAll(dataEntry);

        return new Result<List<DataAuthRuleEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("sys:dataAuthRule:info")
    public Result<DataAuthRuleEO> info(@PathVariable("id") Long id){
        logger.info("======== DataAuthRuleController.info(entity => "+id+") ========");

        DataAuthRuleEO entity = this.dataAuthRuleService.getById(id);

        return new Result<DataAuthRuleEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建客户")
    @RequiresPermissions("sys:dataAuthRule:create")
    public Result create(@RequestBody DataAuthRuleEO entity){
        logger.info("======== DataAuthRuleController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.dataAuthRuleService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新客户")
    @RequiresPermissions("sys:dataAuthRule:update")
    public Result update(@RequestBody DataAuthRuleEO entity){
        logger.info("======== DataAuthRuleController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.dataAuthRuleService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除客户")
    @RequiresPermissions("sys:dataAuthRule:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DataAuthRuleController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.dataAuthRuleService.removeByIds(ids);

        return new Result();
    }
}
