package com.xchinfo.erp.controller.sys.conf;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.entity.EmailSendEO;
import com.xchinfo.erp.sys.conf.service.EmailSendService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

@RestController
@RequestMapping("/sys/emailSend")
public class EmailSendController  extends BaseController {
    @Autowired
    private EmailSendService emailSendService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:emailSend:info")
    public Result<IPage<EmailSendEO>> page(@RequestBody Criteria criteria){
        logger.info("======== EmailSendController.page() ========");
        /*Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);*/
        IPage<EmailSendEO> page = this.emailSendService.selectPage(criteria);

        return new Result<IPage<EmailSendEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:emailSend:info")
    public Result<EmailSendEO> info(@PathVariable("id") Long id){
        logger.info("======== EmailSendController.info(emailSend => "+id+") ========");

        EmailSendEO entity = this.emailSendService.getById(id);

        return new Result<EmailSendEO>().ok(entity);
    }

    /**
     * 发送邮件
     *
     * @param id
     * @return
     */
    @GetMapping("trySend/{id}")
    @RequiresPermissions("basic:emailSend:trySend")
    public Result<Boolean> trySend(@PathVariable("id") Long id){
        logger.info("======== EmailSendController.trySend(emailSend => "+id+") ========");

        boolean entity = this.emailSendService.trySend(id);

        return new Result<Boolean>().ok(entity);
    }
}
