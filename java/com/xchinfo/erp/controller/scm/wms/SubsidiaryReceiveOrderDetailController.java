package com.xchinfo.erp.controller.scm.wms;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderDetailEO;
import com.xchinfo.erp.wms.service.SubsidiaryReceiveOrderDetailService;
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
@RequestMapping("/wms/receiveConsumedDetail")
public class SubsidiaryReceiveOrderDetailController extends BaseController {

    @Autowired
    private SubsidiaryReceiveOrderDetailService subsidiaryReceiveOrderDetailService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("wms:receiveOrder:info")
    public Result<IPage<SubsidiaryReceiveOrderDetailEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ReceiveOrderDetailController.list() ========");

        IPage<SubsidiaryReceiveOrderDetailEO> page = this.subsidiaryReceiveOrderDetailService.selectPage(criteria);

        return new Result<IPage<SubsidiaryReceiveOrderDetailEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("wms:receiveOrder:info")
    public Result<SubsidiaryReceiveOrderDetailEO> info(@PathVariable("id") Long id){
        logger.info("======== ReceiveOrderDetailController.info(entity => "+id+") ========");

        SubsidiaryReceiveOrderDetailEO entity = this.subsidiaryReceiveOrderDetailService.getById(id);

        return new Result<SubsidiaryReceiveOrderDetailEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建入库明细订单")
    @RequiresPermissions("wms:receiveOrder:create")
    public Result create(@RequestBody SubsidiaryReceiveOrderDetailEO entity){
        logger.info("======== ReceiveOrderDetailController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.subsidiaryReceiveOrderDetailService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新入库明细订单")
    @RequiresPermissions("wms:receiveOrder:update")
    public Result update(@RequestBody SubsidiaryReceiveOrderDetailEO entity){
        logger.info("======== ReceiveOrderDetailController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.subsidiaryReceiveOrderDetailService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除入库明细订单")
    @RequiresPermissions("wms:receiveOrder:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== ReceiveOrderDetailController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.subsidiaryReceiveOrderDetailService.removeByIds(ids);

        return new Result();
    }


}
