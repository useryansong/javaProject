package com.xchinfo.erp.controller.sys.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.log.entity.BizDataLogEO;
import com.xchinfo.erp.sys.log.service.BizDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

@RestController
@RequestMapping("/sys/bizDataLog")
public class BizDataLogController extends BaseController{
    @Autowired
    private BizDataLogService bizDataLogService;

    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<BizDataLogEO>> page(@RequestBody Criteria criteria){
        logger.info("======== BizDataLogController.page() ========");
        /*Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);*/
        IPage<BizDataLogEO> page = this.bizDataLogService.selectPage(criteria);

        return new Result<IPage<BizDataLogEO>>().ok(page);
    }
}
