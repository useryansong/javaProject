package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ShiftTypeEO;
import com.xchinfo.erp.bsc.service.ShiftTypeService;
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
@RequestMapping("/basic/shiftType")
public class ShiftTypeController extends BaseController {

    @Autowired
    private ShiftTypeService shiftTypeService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:shiftType:info")
    public Result<IPage<ShiftTypeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ShiftTypeController.page() ========");

        IPage<ShiftTypeEO> page = this.shiftTypeService.selectPage(criteria);

        return new Result<IPage<ShiftTypeEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:shiftType:info")
    public Result<ShiftTypeEO> info(@PathVariable("id") Long id){
        logger.info("======== ShiftTypeController.info(entity => "+id+") ========");

        ShiftTypeEO entity = this.shiftTypeService.getById(id);

        return new Result<ShiftTypeEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建班组")
    @RequiresPermissions("basic:shiftType:create")
    public Result create(@RequestBody ShiftTypeEO entity){
        logger.info("======== ShiftTypeController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.shiftTypeService.save(entity);

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
    @RequiresPermissions("basic:shiftType:update")
    public Result update(@RequestBody ShiftTypeEO entity){
        logger.info("======== ShiftTypeController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.shiftTypeService.updateById(entity);

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
    @RequiresPermissions("basic:shiftType:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ShiftTypeController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.shiftTypeService.removeByIds(ids);

        return new Result();
    }

    /**
     * 查询所有班组
     * @return
     */
    @GetMapping("list")
    public Result<List<ShiftTypeEO>> list(){
        logger.info("======== ShiftTypeController.list() ========");
        List<ShiftTypeEO> shiftTypeEOS = this.shiftTypeService.listAll();
        return new Result<List<ShiftTypeEO>>().ok(shiftTypeEOS);
    }
}
