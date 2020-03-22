package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.BusinessAgentEO;
import com.xchinfo.erp.bsc.service.BusinessAgentService;
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
@RequestMapping("basic/businessAgent")
public class BusinessAgentController extends BaseController {
    @Autowired
    private BusinessAgentService businessAgentService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:businessAgent:info")
    public Result<IPage<BusinessAgentEO>> page(@RequestBody Criteria criteria){
        logger.info("======== BusinessAgentController.page() ========");

        IPage<BusinessAgentEO> page = this.businessAgentService.selectPage(criteria);

        return new Result<IPage<BusinessAgentEO>>().ok(page);
    }

    /**
     * 查询所有业务员
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<BusinessAgentEO>> list(){
        logger.info("======== BusinessAgentController.list() ========");

        List<BusinessAgentEO> businessagent = this.businessAgentService.listAll();

        return new Result<List<BusinessAgentEO>>().ok(businessagent);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:businessAgent:info")
    public Result<BusinessAgentEO> info(@PathVariable("id") Long id){
        logger.info("======== BusinessAgentController.info(entity => "+id+") ========");

        BusinessAgentEO entity = this.businessAgentService.getById(id);

        return new Result<BusinessAgentEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建业务组")
    @RequiresPermissions("basic:businessAgent:create")
    public Result create(@RequestBody BusinessAgentEO entity){
        logger.info("======== BusinessAgentController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.businessAgentService.save(entity);

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
    @RequiresPermissions("basic:businessAgent:update")
    public Result update(@RequestBody BusinessAgentEO entity){
        logger.info("======== BusinessAgentController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.businessAgentService.updateById(entity);

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
    @RequiresPermissions("basic:businessAgent:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== BusinessAgentController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.businessAgentService.removeByIds(ids);

        return new Result();
    }
}
