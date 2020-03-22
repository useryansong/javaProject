package com.xchinfo.erp.controller.sys.log;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.log.entity.ErrorLogEO;
import com.xchinfo.erp.log.service.ErrorLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

/**
 * 错误日志
 *
 * @author roman.li
 * @date 2018/11/12
 * @update
 */
@RestController
@RequestMapping("/sys/errorLog")
public class ErrorLogController extends BaseController {

    @Autowired
    private ErrorLogService errorLogService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:errorLog:info")
    public Result<IPage<ErrorLogEO>> page(@RequestBody Criteria criteria){
        logger.info("======== ErrorLogController.page() ========");

        IPage<ErrorLogEO> page = this.errorLogService.selectPage(criteria);

        return new Result<IPage<ErrorLogEO>>().ok(page);
    }
}
