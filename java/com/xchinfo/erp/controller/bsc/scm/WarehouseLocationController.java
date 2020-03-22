package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.utils.ExcelUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/16
 * @update
 */
@RestController
@RequestMapping("/basic/warehouseLocation")
public class WarehouseLocationController extends BaseController {
    @Autowired
    private WarehouseLocationService warehouseLocationService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:warehouseLocation:info")
    public Result<IPage<WarehouseLocationEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WarehouseLocationController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<WarehouseLocationEO> page = this.warehouseLocationService.selectPage(criteria);
        return new Result<IPage<WarehouseLocationEO>>().ok(page);
    }

    /**
     * 查询所有库位
     * @return
     */
    @GetMapping("list")
    public Result<List<WarehouseLocationEO>> list(){
        logger.info("======== WarehouseLocationController.list() ========");
        List<WarehouseLocationEO> warehouseLocations = this.warehouseLocationService.listAll(getUserId());
        return new Result<List<WarehouseLocationEO>>().ok(warehouseLocations);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<WarehouseLocationEO> info(@PathVariable("id") Long id){
        logger.info("======== WarehouseLocationController.info(entity => "+id+") ========");
        WarehouseLocationEO entity = this.warehouseLocationService.getById(id);
        return new Result<WarehouseLocationEO>().ok(entity);
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增库位")
    @RequiresPermissions("basic:warehouseLocation:create")
    public Result create(@RequestBody WarehouseLocationEO entity){
        logger.info("======== WarehouseLocationController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.warehouseLocationService.save(entity, getUserId());
        return new Result();
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改库位")
    @RequiresPermissions("basic:warehouseLocation:update")
    public Result update(@RequestBody WarehouseLocationEO entity){
        logger.info("======== WarehouseLocationController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.warehouseLocationService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除库位")
    @RequiresPermissions("basic:warehouseLocation:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WarehouseLocationController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.warehouseLocationService.removeByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 设置状态
     *
     * @param entity
     * @return
     */
    @PostMapping("setStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:warehouseLocation:setStatus")
    public Result setStatus(@RequestBody WarehouseLocationEO entity){
        logger.info("======== WarehouseLocationController.setStatus ========");
        entity.setStatus(entity.getStatus()==0?1:0);
        this.warehouseLocationService.updateStatus(entity, getUserId());
        return new Result();
    }


    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    @RequiresPermissions("basic:warehouseLocation:import")
    public Result importFromExcel(HttpServletRequest request){
        logger.info("======== WarehouseLocationController.import ========");
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.parseLong(request.getParameter("orgId"));
        this.warehouseLocationService.importFromExcel(list,orgId);
        return new Result();
    }
}
