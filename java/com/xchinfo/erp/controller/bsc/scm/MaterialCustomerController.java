package com.xchinfo.erp.controller.bsc.scm;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.service.MaterialCustomerService;
import com.xchinfo.erp.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.utils.Criterion;

import java.util.List;

/**
 * @author roman.c
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/materialCustomer")
public class MaterialCustomerController extends BaseController {

    @Autowired
    private MaterialCustomerService materialCustomerService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("basic:materialCustomer:info")
    public Result<IPage<MaterialCustomerEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialCustomerController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialCustomerEO> page = this.materialCustomerService.selectPage(criteria);

        return new Result<IPage<MaterialCustomerEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<MaterialCustomerEO>> list(){
        logger.info("======== MaterialCustomerController.list() ========");

        List<MaterialCustomerEO> customers = this.materialCustomerService.listAll();

        return new Result<List<MaterialCustomerEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
   // @RequiresPermissions("basic:materialCustomer:info")
    public Result<MaterialCustomerEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialCustomerController.info(entity => "+id+") ========");

        MaterialCustomerEO entity = this.materialCustomerService.getById(id);

        return new Result<MaterialCustomerEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建客户物料")
   // @RequiresPermissions("basic:materialCustomer:create")
    public Result create(@RequestBody MaterialCustomerEO entity){
        logger.info("======== MaterialCustomerController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.materialCustomerService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新客户物料")
   // @RequiresPermissions("basic:materialCustomer:update")
    public Result update(@RequestBody MaterialCustomerEO entity){
        logger.info("======== MaterialCustomerController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialCustomerService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除客户物料")
   // @RequiresPermissions("basic:materialCustomer:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialCustomerController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialCustomerService.removeByIds(ids);

        return new Result();
    }

    /**
     * 设置默认
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置默认")
   // @RequiresPermissions("basic:materialCustomer:update")
    public Result updateStatus(@RequestParam("id") Long id){
        logger.info("======== MaterialCustomerController.updateStatus(id => "+id+") ========");


        this.materialCustomerService.updateStatusById(id);

        return new Result();
    }
}
