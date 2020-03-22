package com.xchinfo.erp.controller.bsc.scm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.SupplierErpCodeEO;
import com.xchinfo.erp.bsc.service.SupplierErpCodeService;
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
import org.yecat.mybatis.utils.Criterion;

@RestController
@RequestMapping("/basic/supplierErpCode")
public class SupplierErpCodeController extends BaseController {
    @Autowired
    private SupplierErpCodeService supplierErpCodeService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:supplier:setErpCode")
    public Result<IPage<SupplierErpCodeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SupplierErpCodeController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("t.org_id");
        criterion.setOp("eq");
        criterion.setValue(getOrgId()+"");
        criteria.getCriterions().add(criterion);
        IPage<SupplierErpCodeEO> page = this.supplierErpCodeService.selectPage(criteria);

        return new Result<IPage<SupplierErpCodeEO>>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:supplier:setErpCode")
    public Result<SupplierErpCodeEO> info(@PathVariable("id") Long id){
        logger.info("======== SupplierErpCodeController.info(supplierErpCode => "+id+") ========");

        SupplierErpCodeEO entity = this.supplierErpCodeService.getById(id);

        return new Result<SupplierErpCodeEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("basic:supplier:setErpCode")
    public Result create(@RequestBody SupplierErpCodeEO entity){
        logger.info("======== SupplierErpCodeController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        if(this.supplierErpCodeService.checkRepeat(entity.getSupplierId(),entity.getOrgId())>0){
            return new Result().error("当前机构已有此供应商erp编码");
        }
        this.supplierErpCodeService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("basic:supplier:setErpCode")
    public Result update(@RequestBody SupplierErpCodeEO entity){
        logger.info("======== SupplierErpCodeController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        SupplierErpCodeEO old = this.supplierErpCodeService.getById(entity.getSupplierErpCodeId());
        old.setErpcode(entity.getErpcode());
        this.supplierErpCodeService.updateById(old);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("basic:supplier:setErpCode")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SupplierErpCodeController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.supplierErpCodeService.removeByIds(ids);

        return new Result();
    }

}
