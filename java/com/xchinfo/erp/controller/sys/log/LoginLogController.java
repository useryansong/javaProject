package com.xchinfo.erp.controller.sys.log;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.log.entity.LoginLogEO;
import com.xchinfo.erp.log.service.LoginLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

/**
 * @author roman.li
 * @date 2017/11/10
 * @update
 */
@RestController
@RequestMapping("/sys/loginLog")
public class LoginLogController extends BaseController {

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 查找列表
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:loginLog:info")
    public Result<IPage<LoginLogEO>> page(@RequestBody Criteria criteria){
        logger.info("======== LoginLogController.page() ========");

        IPage<LoginLogEO> page = this.loginLogService.selectPage(criteria);

        return new Result<IPage<LoginLogEO>>().ok(page);
    }
}
