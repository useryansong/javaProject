package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.PbomEO;
import com.xchinfo.erp.bsc.service.PbomService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/basic/pbom")
public class PbomController extends BaseController {

    @Autowired
    private PbomService pbomService;


    /**
     * 分页查询
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<PbomEO>> page(@RequestBody Criteria criteria){
        logger.info("======== PbomController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<PbomEO> page = this.pbomService.selectPage(criteria);

        return new Result<IPage<PbomEO>>().ok(page);
    }

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<PbomEO> info(@PathVariable("id") Long id){
        logger.info("======== PbomController.info(project => "+id+") ========");

        PbomEO entity = this.pbomService.getById(id);

        return new Result<PbomEO>().ok(entity);
    }


    @PostMapping("import")
    public Result importExcel(HttpServletRequest request){

        List list = ExcelUtils.getExcelData(request);
        Result result = this.pbomService.importExcel(list,getUser());

        return result;
    }

}
