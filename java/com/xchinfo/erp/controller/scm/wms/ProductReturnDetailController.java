package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.ProductReturnDetailEO;
import com.xchinfo.erp.wms.service.ProductReturnDetailService;
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
 * @author roman.c
 * @date 2019/4/18
 * @update
 */
@RestController
@RequestMapping("/wms/productReturnDetail")
public class ProductReturnDetailController extends BaseController {


    @Autowired
    private ProductReturnDetailService productReturnDetailService;
    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:productReturn:info")
    public Result<IPage<ProductReturnDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ProductReturnDetailController.list() ========");

        IPage<ProductReturnDetailEO> page = this.productReturnDetailService.selectPage(criteria);

        return new Result<IPage<ProductReturnDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:productReturn:info")
    public Result<ProductReturnDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== ProductReturnDetailController.info(entity => "+id+") ========");

        ProductReturnDetailEO entity = this.productReturnDetailService.getById(id);

        return new Result<ProductReturnDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建退货订单明细")
    @RequiresPermissions("wms:productReturn:create")
    public Result create(@RequestBody ProductReturnDetailEO entity){
        logger.info("======== ProductReturnDetailController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.productReturnDetailService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新退货订单明细")
    @RequiresPermissions("wms:productReturn:update")
    public Result update(@RequestBody ProductReturnDetailEO entity){
        logger.info("======== ProductReturnDetailController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.productReturnDetailService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除退货订单明细")
    @RequiresPermissions("wms:productReturn:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ProductReturnDetailController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.productReturnDetailService.removeByIds(ids);

        return new Result();
    }
}
