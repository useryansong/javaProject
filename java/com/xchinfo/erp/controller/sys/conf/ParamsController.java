package com.xchinfo.erp.controller.sys.conf;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.entity.ParamsEO;
import com.xchinfo.erp.sys.conf.service.ParamsService;
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
 * @author roman.li
 * @date 2017/10/12
 * @update
 */
@RestController
@RequestMapping("/sys/params")
public class ParamsController extends BaseController {

    @Autowired
    private ParamsService paramsService;

    /**
     * 分页查找信息
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:param:info")
    public Result<IPage<ParamsEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SysParamsController.page() ========");

        IPage<ParamsEO> page = this.paramsService.selectPage(criteria);

        return new Result<IPage<ParamsEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param key
     * @return
     */
    @GetMapping("{key}")
    @RequiresPermissions("sys:param:info")
    public Result<ParamsEO> info(@PathVariable("key") String key){
        logger.info("======== SysParamsController.info(key => "+key+") ========");

        ParamsEO param = this.paramsService.getById(key);

        return new Result<ParamsEO>().ok(param);
    }

    /**
     * 创建指定对象
     *
     * @param
     * @return
     */
    @PostMapping
    @OperationLog("创建系统参数")
    @RequiresPermissions("sys:param:create")
    public Result create(@RequestBody ParamsEO params){
        logger.info("======== SysParamsController.save() ========");

        // 校验输入合法性
        ValidatorUtils.validateEntity(params, AddGroup.class, DefaultGroup.class);

        this.paramsService.save(params);

        return new Result();
    }

    /**
     * 更新指定对象
     *
     * @param params
     * @return
     */
    @PutMapping
    @OperationLog("更新系统参数")
    @RequiresPermissions("sys:param:update")
    public Result update(@RequestBody ParamsEO params){
        logger.info("======== SysParamsController.save() ========");

        // 校验输入合法性
        ValidatorUtils.validateEntity(params, UpdateGroup.class, DefaultGroup.class);

        this.paramsService.updateById(params);

        return new Result();
    }

    /**
     * 删除指定的配置参数
     *
     * @param keys
     * @return
     */
    @DeleteMapping
    @OperationLog("删除系统参数")
    @RequiresPermissions("sys:param:delete")
    public Result delete(@RequestBody String[] keys){
        logger.info("======== SysParamsController.delete() ========");

        AssertUtils.isArrayEmpty(keys, "key");

        this.paramsService.removeByIds(keys);

        return new Result();
    }
}
