package com.xchinfo.erp.controller.bsc.plm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.service.MachineService;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/machine")
public class MachineController extends BaseController {

    @Autowired
    private MachineService machineService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:machine:info")
    public Result<IPage<MachineEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MachineController.page() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MachineEO> page = this.machineService.selectPage(criteria);

        return new Result<IPage<MachineEO>>().ok(page);
    }

    /**
     * 查找所有设备
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<MachineEO>> list(){
        logger.info("======== MachineController.page() ========");

        List<MachineEO> machines = this.machineService.listAll(getUserId());

        return new Result<List<MachineEO>>().ok(machines);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:machine:info")
    public Result<MachineEO> info(@PathVariable("id") Long id){
        logger.info("======== MachineController.info(entity => "+id+") ========");

        MachineEO entity = this.machineService.getById(id);

        return new Result<MachineEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建设备")
    @RequiresPermissions("basic:machine:create")
    public Result create(@RequestBody MachineEO entity){
        logger.info("======== MachineController.create(ID => "+entity.getId()+") ========");
        entity.setOrgId(getOrgId());
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.machineService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新设备")
    @RequiresPermissions("basic:machine:update")
    public Result update(@RequestBody MachineEO entity){
        logger.info("======== MachineController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.machineService.updateById(entity);

        return new Result();
    }

    /**
     * 更新状态
     *
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:machine:setStatus")
    public Result updateStatusById(@RequestParam("id") Long id,@RequestParam("status") int status){
        logger.info("======== MachineController.updateStatusById() ========");

        this.machineService.updateStatusById(id,status);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除设备")
    @RequiresPermissions("basic:machine:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MachineController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.machineService.removeByIds(ids);

        return new Result();
    }


    /**
     * 导出报表
     * @param request
     * @param response
     * @param MachineEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("basic:machine:export")
    public Result exportMonth(HttpServletRequest request, HttpServletResponse response, @RequestBody MachineEO[] MachineEOS){

        JSONObject jsonObject= ExcelUtils.parseJsonFile("machine.json");

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(MachineEOS), jsonObject);

        return new Result();
    }
}
