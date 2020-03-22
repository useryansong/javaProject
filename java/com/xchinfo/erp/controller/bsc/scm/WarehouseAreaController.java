package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseAreaEO;
import com.xchinfo.erp.bsc.service.WarehouseAreaService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.UserEO;
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
@RequestMapping("/basic/warehouseArea")
public class WarehouseAreaController extends BaseController {
    @Autowired
    private WarehouseAreaService warehouseAreaService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:warehouseArea:info")
    public Result<IPage<WarehouseAreaEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WarehouseAreaController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<WarehouseAreaEO> page = this.warehouseAreaService.selectPage(criteria);
        return new Result<IPage<WarehouseAreaEO>>().ok(page);
    }

    /**
     * 查询所有库区
     * @return
     */
    @GetMapping("list")
    public Result<List<WarehouseAreaEO>> list(){
        logger.info("======== WarehouseAreaController.list() ========");
        List<WarehouseAreaEO> warehouseAreas = this.warehouseAreaService.listAll(getUserId());
        return new Result<List<WarehouseAreaEO>>().ok(warehouseAreas);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<WarehouseAreaEO> info(@PathVariable("id") Long id){
        logger.info("======== WarehouseAreaController.info(entity => "+id+") ========");
        WarehouseAreaEO entity = this.warehouseAreaService.getById(id);
        return new Result<WarehouseAreaEO>().ok(entity);
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增库区")
    @RequiresPermissions("basic:warehouseArea:create")
    public Result create(@RequestBody WarehouseAreaEO entity){
        logger.info("======== WarehouseAreaController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.warehouseAreaService.save(entity, getUserId());
        return new Result();
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改库区")
    @RequiresPermissions("basic:warehouseArea:update")
    public Result update(@RequestBody WarehouseAreaEO entity){
        logger.info("======== WarehouseAreaController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.warehouseAreaService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除库区")
    @RequiresPermissions("basic:warehouseArea:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WarehouseAreaController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.warehouseAreaService.removeByIds(ids, getUserId());
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
    @RequiresPermissions("basic:warehouseArea:setStatus")
    public Result setStatus(@RequestBody WarehouseAreaEO entity){
        logger.info("======== WarehouseAreaController.setStatus ========");
        entity.setStatus(entity.getStatus()==0?1:0);
        this.warehouseAreaService.updateStatus(entity, getUserId());
        return new Result();
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    @RequiresPermissions("basic:warehouseArea:import")
    public Result importFromExcel(HttpServletRequest request){
        logger.info("======== WarehouseAreaController.import ========");
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.parseLong(request.getParameter("orgId"));
        this.warehouseAreaService.importFromExcel(list,orgId);
        return new Result();
    }
}
