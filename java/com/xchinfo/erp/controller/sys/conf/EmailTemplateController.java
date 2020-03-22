package com.xchinfo.erp.controller.sys.conf;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import com.xchinfo.erp.sys.conf.service.EmailTemplateService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelExportUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/emailTemplate")
public class EmailTemplateController  extends BaseController {
    @Autowired
    private EmailTemplateService emailTemplateService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:emailTemplate:info")
    public Result<IPage<EmailTemplateEO>> page(@RequestBody Criteria criteria){
        logger.info("======== EmailTemplateController.page() ========");
        /*Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);*/
        IPage<EmailTemplateEO> page = this.emailTemplateService.selectPage(criteria);

        return new Result<IPage<EmailTemplateEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:emailTemplate:info")
    public Result<EmailTemplateEO> info(@PathVariable("id") Long id){
        logger.info("======== EmailTemplateController.info(emailTemplate => "+id+") ========");

        EmailTemplateEO entity = this.emailTemplateService.getById(id);

        return new Result<EmailTemplateEO>().ok(entity);
    }

    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @OperationLog("设置状态-启用")
    @RequiresPermissions("basic:emailTemplate:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== EmailTemplateController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.emailTemplateService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @OperationLog("设置状态-禁用")
    @RequiresPermissions("basic:emailTemplate:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== EmailTemplateController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.emailTemplateService.updateStatusById(ids,1);

        return new Result();
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建邮件模板")
    @RequiresPermissions("basic:emailTemplate:create")
    public Result create(@RequestBody EmailTemplateEO entity){
        logger.info("======== EmailTemplateController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setStatus(0);
        this.emailTemplateService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新邮件模板")
    @RequiresPermissions("basic:emailTemplate:update")
    public Result update(@RequestBody EmailTemplateEO entity){
        logger.info("======== EmailTemplateController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.emailTemplateService.updateById(entity);

        return new Result();
    }


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除邮件模板")
    @RequiresPermissions("basic:emailTemplate:delete")
    //@EnableBusinessLog(BusinessLogType.DELETE)
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== EmailTemplateController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.emailTemplateService.removeByIds(ids);

        return new Result();
    }
}
