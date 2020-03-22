package com.xchinfo.erp.controller.sys.conf;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import com.xchinfo.erp.sys.conf.service.CodeRuleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

/**
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@RestController
@RequestMapping("/sys/codeRule")
public class CodeRuleController extends BaseController {

    @Autowired
    private CodeRuleService codeRuleService;

    /**
     * 查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:code:info")
    public Result<IPage<CodeRuleEO>> page(@RequestBody Criteria criteria){
        logger.info("======== CodeRuleController.list() ========");

        IPage<CodeRuleEO> page = this.codeRuleService.selectPage(criteria);

        return new Result<IPage<CodeRuleEO>>().ok(page);
    }

    /**
     * 根据主键查找
     *
     * @param code
     * @return
     */
    @GetMapping("{code}")
    @RequiresPermissions("sys:code:info")
    public Result<CodeRuleEO> info(@PathVariable String code){
        logger.info("======== CodeRuleController.info(code => "+code+") ========");

        CodeRuleEO rule = codeRuleService.getById(code);

        return new Result<CodeRuleEO>().ok(rule);
    }

    /**
     * 创建
     *
     * @param code
     * @return
     */
    @PostMapping
    @OperationLog("创建编码配置")
    @RequiresPermissions("sys:code:create")
    public Result create(@RequestBody CodeRuleEO code){
        logger.info("======== CodeRuleController.create(id => "+code.pkVal()+") ========");

        ValidatorUtils.validateEntity(code, AddGroup.class, DefaultGroup.class);

        this.codeRuleService.save(code);

        return new Result();
    }

    /**
     * 更新
     *
     * @param code
     * @return
     */
    @PutMapping
    @OperationLog("更新编码配置")
    @RequiresPermissions("sys:code:update")
    public Result update(@RequestBody CodeRuleEO code){
        logger.info("======== CodeRuleController.update(id => "+code.pkVal()+") ========");

        ValidatorUtils.validateEntity(code, UpdateGroup.class, DefaultGroup.class);

        this.codeRuleService.updateById(code);

        return new Result();
    }

    /**
     * 删除
     *
     * @param codes
     * @return
     */
    @DeleteMapping
    @OperationLog("删除编码配置")
    @RequiresPermissions("sys:code:delete")
    public Result delete(@RequestBody String[] codes){
        logger.info("======== CodeRuleController.delete() ========");

        this.codeRuleService.removeByIds(codes);

        return new Result();
    }
}
