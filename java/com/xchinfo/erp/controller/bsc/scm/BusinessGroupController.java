package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.BusinessGroupEO;
import com.xchinfo.erp.bsc.service.BusinessGroupService;
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
 * @author yuanchang
 * @date 2019/4/11
 * @update
 */
@RestController
@RequestMapping("basic/businessGroup")
public class BusinessGroupController extends BaseController {
    @Autowired
    private BusinessGroupService businessGroupService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:businessGroup:info")
    public Result<IPage<BusinessGroupEO>> page(@RequestBody Criteria criteria){
        logger.info("======== BusinessGroupController.page() ========");

        IPage<BusinessGroupEO> page = this.businessGroupService.selectPage(criteria);

        return new Result<IPage<BusinessGroupEO>>().ok(page);
    }

    /**
     * 查询所有业务组
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<BusinessGroupEO>> list(){
        logger.info("======== BusinessGroupController.list() ========");

        List<BusinessGroupEO> businessgroup = this.businessGroupService.listAll();

        return new Result<List<BusinessGroupEO>>().ok(businessgroup);
    }

    /**
     * 查询所有业务组
     * @param groupType
     * @return
     */
    @PostMapping("list")
    public Result<List<BusinessGroupEO>> listByGroupType(@RequestParam Integer groupType){
        logger.info("======== BusinessGroupController.listByGroupType() ========");

        List<BusinessGroupEO> businessgroup = this.businessGroupService.listByGroupType(groupType);

        return new Result<List<BusinessGroupEO>>().ok(businessgroup);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:businessGroup:info")
    public Result<BusinessGroupEO> info(@PathVariable("id") Long id){
        logger.info("======== BusinessGroupController.info(entity => "+id+") ========");

        BusinessGroupEO entity = this.businessGroupService.getById(id);

        return new Result<BusinessGroupEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建业务组")
    @RequiresPermissions("basic:businessGroup:create")
    public Result create(@RequestBody BusinessGroupEO entity){
        logger.info("======== BusinessGroupController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.businessGroupService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新业务组")
    @RequiresPermissions("basic:businessGroup:update")
    public Result update(@RequestBody BusinessGroupEO entity){
        logger.info("======== BusinessGroupController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.businessGroupService.updateById(entity);

        Result result = new Result();
        return result;
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除业务组")
    @RequiresPermissions("basic:businessGroup:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== BusinessGroupController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.businessGroupService.removeByIds(ids);

        return new Result();
    }
}
