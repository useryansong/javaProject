package com.xchinfo.erp.controller.bsc.plm;


import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ProcessEO;
import com.xchinfo.erp.bsc.service.ProcessService;
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

import java.util.HashMap;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/process")
public class ProcessController extends BaseController {

    @Autowired
    private ProcessService processService;

    /**
     * 查找
     *
     * @return
     */
    @GetMapping("list")
    @RequiresPermissions("basic:process:info")
    public Result<List<ProcessEO>> list(){
        logger.info("======== ProcessController.list() ========");

        List<ProcessEO> list = this.processService.listForTree(new HashMap<>(1));

        return new Result<List<ProcessEO>>().ok(list);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:process:info")
    public Result<ProcessEO> info(@PathVariable("id") Long id){
        logger.info("======== ProcessController.info(entity => "+id+") ========");

        ProcessEO entity = this.processService.getById(id);

        return new Result<ProcessEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建工序")
    @RequiresPermissions("basic:process:create")
    public Result create(@RequestBody ProcessEO entity){
        logger.info("======== ProcessController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.processService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新工序")
    @RequiresPermissions("basic:process:update")
    public Result update(@RequestBody ProcessEO entity){
        logger.info("======== ProcessController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.processService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除工序")
    @RequiresPermissions("basic:process:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProcessController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.processService.removeByIds(ids);

        return new Result();
    }
}
