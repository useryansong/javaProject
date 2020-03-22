package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ToolEO;
import com.xchinfo.erp.bsc.service.ToolService;
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
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/tool")
public class ToolController extends BaseController {

    @Autowired
    private ToolService toolService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:tool:info")
    public Result<IPage<ToolEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ToolController.list() ========");

        IPage<ToolEO> page = this.toolService.selectPage(criteria);

        return new Result<IPage<ToolEO>>().ok(page);
    }

    /**
     * 查找所有工具
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<ToolEO>> list(){
        logger.info("======== MachineController.page() ========");

        List<ToolEO> tools = this.toolService.listAll();

        return new Result<List<ToolEO>>().ok(tools);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:tool:info")
    public Result<ToolEO> info(@PathVariable("id") Long id){
        logger.info("======== ToolController.info(entity => "+id+") ========");

        ToolEO entity = this.toolService.getById(id);

        return new Result<ToolEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建工具")
    @RequiresPermissions("basic:tool:create")
    public Result create(@RequestBody ToolEO entity){
        logger.info("======== ToolController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.toolService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新工具")
    @RequiresPermissions("basic:tool:update")
    public Result update(@RequestBody ToolEO entity){
        logger.info("======== ToolController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.toolService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除工具")
    @RequiresPermissions("basic:tool:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ToolController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.toolService.removeByIds(ids);

        return new Result();
    }
}
