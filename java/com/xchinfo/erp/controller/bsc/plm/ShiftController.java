package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ShiftEO;
import com.xchinfo.erp.bsc.service.ShiftService;
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
@RequestMapping("/basic/shift")
public class ShiftController extends BaseController {

    @Autowired
    private ShiftService shiftService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:shift:info")
    public Result<IPage<ShiftEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ShiftController.page() ========");

        IPage<ShiftEO> page = this.shiftService.selectPage(criteria);

        return new Result<IPage<ShiftEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:shift:info")
    public Result<ShiftEO> info(@PathVariable("id") Long id){
        logger.info("======== ShiftController.info(entity => "+id+") ========");

        ShiftEO entity = this.shiftService.getById(id);

        return new Result<ShiftEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建班次")
    @RequiresPermissions("basic:shift:create")
    public Result create(@RequestBody ShiftEO entity){
        logger.info("======== ShiftController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.shiftService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新班次")
    @RequiresPermissions("basic:shift:update")
    public Result update(@RequestBody ShiftEO entity){
        logger.info("======== ShiftController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.shiftService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除班次")
    @RequiresPermissions("basic:shift:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ShiftController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.shiftService.removeByIds(ids);

        return new Result();
    }

    /**
     * 查询所有班次
     * @return
     */
    @GetMapping("list")
    public Result<List<ShiftEO>> list(){
        logger.info("======== ShiftTypeController.list() ========");
        List<ShiftEO> shiftTypeEOS = this.shiftService.listAll();
        return new Result<List<ShiftEO>>().ok(shiftTypeEOS);
    }
}
