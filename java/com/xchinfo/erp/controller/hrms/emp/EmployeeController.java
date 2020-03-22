package com.xchinfo.erp.controller.hrms.emp;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.controller.page.PageController;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import com.xchinfo.erp.hrms.service.EmployeeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@RestController
@RequestMapping("/hrms/employee")
public class EmployeeController extends BaseController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("hrms:employee:info")
    public Result<IPage<EmployeeEO>> page(@RequestBody Criteria criteria){
        logger.info("======== EmployeeController.page() ========");

        IPage<EmployeeEO> page = this.employeeService.selectPage(criteria);

        return new Result<IPage<EmployeeEO>>().ok(page);
    }

    /**
     * 查找所有员工
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<EmployeeEO>> list(){
        logger.info("======== EmployeeController.list() ========");

        List<EmployeeEO> machines = this.employeeService.listAll(getUserId());

        return new Result<List<EmployeeEO>>().ok(machines);
    }

    /**
     * 分页查询
     *
     * @param map
     * @return
     */
    @PostMapping("pageNew")
    @RequiresPermissions("hrms:employee:info")
    public Result<Pagination> pageNew(@RequestBody Map map){
        logger.info("======== EmployeeController.pageNew() ========");

        Pagination page = this.employeeService.selectPage(map);

        return new Result<Pagination>().ok(page);
    }

    /**
     * 分页查询
     *
     * @param map
     * @return
     */
    @PostMapping("pageNewNoPerm")
    public Result<Pagination> pageNewNoPerm(@RequestBody Map map){
        logger.info("======== EmployeeController.pageNewNoPerm() ========");

        Pagination page = this.employeeService.selectPage(map);

        return new Result<Pagination>().ok(page);
    }

    /**
     * 班组选人
     *
     * @param map
     * @return
     */
    @PostMapping("workingGroupEmployeePage")
    @RequiresPermissions("hrms:employee:info")
    public Result<Pagination> selectWorkingGroupEmployeePage(@RequestBody Map map){
        logger.info("======== EmployeeController.page() ========");

        Pagination page = this.employeeService.selectWorkingGroupEmployeePage(map);

        return new Result<Pagination>().ok(page);
    }
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("hrms:employee:info")
    public Result<EmployeeEO> info(@PathVariable("id") Long id){
        logger.info("======== EmployeeController.info(employee => "+id+") ========");

        EmployeeEO entity = this.employeeService.getById(id);

        return new Result<EmployeeEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @RequiresPermissions("hrms:employee:create")
    @OperationLog("创建员工")
    public Result create(@RequestBody EmployeeEO entity){
        logger.info("======== EmployeeController.create() ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.employeeService.save(entity);

        return new Result();
    }

    /**
     * 更新用户
     *
     * @param entity
     * @return
     */
    @PutMapping
    @RequiresPermissions("hrms:employee:update")
    @OperationLog("修改员工")
    public Result update(@RequestBody EmployeeEO entity){
        logger.info("======== EmployeeController.update(employee => " + entity.getEmployeeId() + ") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.employeeService.updateById(entity);

        return new Result();
    }

    /**
     * 启用用户
     *
     * @param employeeId
     * @return
     */
    @GetMapping("enable/{id}")
    @RequiresPermissions("hrms:employee:update")
    @OperationLog("生效员工")
    public Result enable(@PathVariable("id") Long employeeId){
        logger.info("======== EmployeeController.update(employee => " + employeeId + ") ========");

        return new Result();
    }

    /**
     * 批量删除员工
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @OperationLog("删除员工")
    @RequiresPermissions("hrms:employee:delete")
    public Result delete(@RequestBody Serializable id){
        logger.info("======== EmployeeController.delete() ========");

        this.employeeService.removeById(id);

        return new Result();
    }

    /**
     * 同步数据
     *
     * @return
     */
    @GetMapping("sync")
    @RequiresPermissions("hrms:employee:sync")
    public Result syncHR(){
        logger.info("======== EmployeeController.syncHR() ========");

        return this.employeeService.syncHR();
    }
}
