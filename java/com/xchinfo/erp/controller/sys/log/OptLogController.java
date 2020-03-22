package com.xchinfo.erp.controller.sys.log;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.log.entity.OptLogEO;
import com.xchinfo.erp.log.service.OptLogService;
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
@RequestMapping("/sys/optLog")
public class OptLogController extends BaseController {

    @Autowired
    private OptLogService optLogService;

    /**
     * 查找列表
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:optLog:info")
    public Result<IPage<OptLogEO>> page(@RequestBody Criteria criteria){
        logger.info("======== OptLogController.page() ========");

        IPage<OptLogEO> page = this.optLogService.selectPage(criteria);

        return new Result<IPage<OptLogEO>>().ok(page);
    }
}
