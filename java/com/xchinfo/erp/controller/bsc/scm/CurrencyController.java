package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.CurrencyEO;
import com.xchinfo.erp.bsc.service.CurrencyService;
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
 * @date 2019/4/5
 * @update
 */
@RestController
@RequestMapping("/basic/currency")
public class CurrencyController extends BaseController {

    @Autowired
    private CurrencyService currencyService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:currency:info")
    public Result<IPage<CurrencyEO>> page(@RequestBody Criteria criteria){
        logger.info("======== CurrencyController.page() ========");

        IPage<CurrencyEO> page = this.currencyService.selectPage(criteria);

        return new Result<IPage<CurrencyEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<CurrencyEO>> list(){
        logger.info("======== CurrencyController.list() ========");

        List<CurrencyEO> customers = this.currencyService.listAll();

        return new Result<List<CurrencyEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:currency:info")
    public Result<CurrencyEO> info(@PathVariable("id") Long id){
        logger.info("======== CurrencyController.info(entity => "+id+") ========");

        CurrencyEO entity = this.currencyService.getById(id);

        return new Result<CurrencyEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建币种")
    @RequiresPermissions("basic:currency:create")
    public Result create(@RequestBody CurrencyEO entity){
        logger.info("======== CurrencyController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.currencyService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新币种")
    @RequiresPermissions("basic:currency:update")
    public Result update(@RequestBody CurrencyEO entity){
        logger.info("======== CurrencyController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.currencyService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除币种")
    @RequiresPermissions("basic:currency:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== CurrencyController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.currencyService.removeByIds(ids);

        return new Result();
    }
}
