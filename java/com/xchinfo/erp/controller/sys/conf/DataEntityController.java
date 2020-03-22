package com.xchinfo.erp.controller.sys.conf;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.entity.DataEntityEO;
import com.xchinfo.erp.sys.conf.service.DataEntityService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

/**
 * 数据实体配置
 *
 * @author roman.li
 * @date 2018/11/22
 * @update
 */
@RestController
@RequestMapping("/sys/entity")
public class DataEntityController extends BaseController {

    @Autowired
    protected DataEntityService dataEntityService;

    /**
     * 分页查找信息
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:entity:info")
    public Result<IPage<DataEntityEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SysParamsController.page() ========");

        IPage<DataEntityEO> page = this.dataEntityService.selectPage(criteria);

        return new Result<IPage<DataEntityEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param code
     * @return
     */
    @GetMapping("{code}")
    @RequiresPermissions("sys:entity:info")
    public Result<DataEntityEO> info(@PathVariable("code") String code){
        logger.info("======== SysParamsController.info(key => "+code+") ========");

        DataEntityEO dataEntity = this.dataEntityService.getById(code);

        return new Result<DataEntityEO>().ok(dataEntity);
    }

    /**
     * 更新指定对象
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新数据实体配置")
    @RequiresPermissions("sys:entity:update")
    public Result update(@RequestBody DataEntityEO entity){
        logger.info("======== SysParamsController.save() ========");

        this.dataEntityService.updateById(entity);

        return new Result();
    }
}
