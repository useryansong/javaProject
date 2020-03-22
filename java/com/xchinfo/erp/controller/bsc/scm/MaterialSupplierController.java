package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.bsc.service.MaterialSupplierService;
import com.xchinfo.erp.controller.BaseController;
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

import java.util.List;


/**
 * @author roman.c
 * @date 2019/4/11
 * @update
 */
@RestController
@RequestMapping("/basic/materialSupplier")
public class MaterialSupplierController  extends BaseController {

    @Autowired
    private MaterialSupplierService materialSupplierService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("basic:materialSupplier:info")
    public Result<IPage<MaterialSupplierEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialSupplierController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialSupplierEO> page = this.materialSupplierService.selectPage(criteria);

        return new Result<IPage<MaterialSupplierEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<MaterialSupplierEO>> list(){
        logger.info("======== MaterialSupplierController.list() ========");

        List<MaterialSupplierEO> customers = this.materialSupplierService.listAll();

        return new Result<List<MaterialSupplierEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
   // @RequiresPermissions("basic:materialSupplier:info")
    public Result<MaterialSupplierEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialSupplierController.info(entity => "+id+") ========");

        MaterialSupplierEO entity = this.materialSupplierService.getById(id);

        return new Result<MaterialSupplierEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建供应商物料")
   // @RequiresPermissions("basic:materialSupplier:create")
    public Result create(@RequestBody MaterialSupplierEO entity){
        logger.info("======== MaterialSupplierController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.materialSupplierService.save(entity,getUserId());

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新供应商物料")
   // @RequiresPermissions("basic:materialSupplier:update")
    public Result update(@RequestBody MaterialSupplierEO entity){
        logger.info("======== MaterialSupplierController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialSupplierService.updateById(entity,getUserId());

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除供应商物料")
    //@RequiresPermissions("basic:materialSupplier:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialSupplierController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialSupplierService.removeByIds(ids);

        return new Result();
    }

    /**
     * 设置默认
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置状态")
    //@RequiresPermissions("basic:materialSupplier:update")
    public Result updateStatus(@RequestParam("id") Long id){
        logger.info("======== MaterialSupplierController.updateStatus(id => "+id+") ========");


        this.materialSupplierService.updateStatusById(id,getUserId());

        return new Result();
    }


}

