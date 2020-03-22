package com.xchinfo.erp.controller.sys.log;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.log.entity.BizLogEO;
import com.xchinfo.erp.log.service.BizLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;

/**
 * @author roman.li
 * @date 2018/11/20
 * @update
 */
@RestController
@RequestMapping("/sys/bizLog")
public class BizLogController extends BaseController {

    @Autowired
    private BizLogService bizLogService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:bizLog:info")
    public Result<IPage<BizLogEO>> page(@RequestBody Criteria criteria){
        logger.info("======== BizLogController.page() ========");

        IPage<BizLogEO> page = this.bizLogService.selectPage(criteria);

        return new Result<IPage<BizLogEO>>().ok(page);
    }

    /**
     * 查找单个实体的操作日志
     *
     * @param optEntity
     * @param entityId
     * @return
     */
    @GetMapping("find")
    public Result<List<BizLogEO>> find(@RequestParam("optEntity") String optEntity,
                                       @RequestParam("entityId") String entityId){
        logger.info("======== BizLogController.find(optEntity => " + optEntity + ", entityId => " + entityId + ") ========");

        List<BizLogEO> bizLogs = this.bizLogService.selectList(optEntity, entityId);

        return new Result<List<BizLogEO>>().ok(bizLogs);
    }
}
