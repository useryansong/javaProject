package com.xchinfo.erp.controller.bsc.scm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.CustomerErpCodeEO;
import com.xchinfo.erp.bsc.service.CustomerErpCodeService;
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
@RequestMapping("/basic/customerErpCode")
public class CustomerErpCodeController extends BaseController {
    @Autowired
    private CustomerErpCodeService customerErpCodeService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:customer:setErpCode")
    public Result<IPage<CustomerErpCodeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== CustomerErpCodeController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("t.org_id");
        criterion.setOp("eq");
        criterion.setValue(getOrgId()+"");
        criteria.getCriterions().add(criterion);
        IPage<CustomerErpCodeEO> page = this.customerErpCodeService.selectPage(criteria);

        return new Result<IPage<CustomerErpCodeEO>>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:customer:setErpCode")
    public Result<CustomerErpCodeEO> info(@PathVariable("id") Long id){
        logger.info("======== CustomerErpCodeController.info(customerErpCode => "+id+") ========");

        CustomerErpCodeEO entity = this.customerErpCodeService.getById(id);

        return new Result<CustomerErpCodeEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("basic:customer:setErpCode")
    public Result create(@RequestBody CustomerErpCodeEO entity){
        logger.info("======== CustomerErpCodeController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        if(this.customerErpCodeService.checkRepeat(entity.getCustomerId(),entity.getOrgId())>0){
            return new Result().error("当前机构已有此客户erp编码");
        }
        this.customerErpCodeService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("basic:customer:setErpCode")
    public Result update(@RequestBody CustomerErpCodeEO entity){
        logger.info("======== CustomerErpCodeController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        CustomerErpCodeEO old = this.customerErpCodeService.getById(entity.getCustomerErpCodeId());
        old.setErpcode(entity.getErpcode());
        this.customerErpCodeService.updateById(old);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @RequiresPermissions("basic:customer:setErpCode")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== CustomerErpCodeController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.customerErpCodeService.removeByIds(ids);

        return new Result();
    }

}
