package com.xchinfo.erp.controller.bsc.plm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.service.BomService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.ExcelUtils;
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
import org.yecat.mybatis.utils.Criterion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhongy
 * @date 2019/4/10
 * @update
 */
@RestController
@RequestMapping("/basic/bom")
public class BomController extends BaseController {
    @Autowired
    private BomService bomService;

    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:bom:info")
    public Result<IPage<BomEO>> page(@RequestBody Criteria criteria){
        logger.info("======== BomController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("ua.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<BomEO> page = this.bomService.selectPage(criteria);
        return new Result<IPage<BomEO>>().ok(page);
    }

//    /**
//     * 查询所有bom
//     * @return
//     */
//    @GetMapping("list")
//    public Result<List<BomEO>> list(){
//        logger.info("======== BomController.list() ========");
//        List<BomEO> list = this.bomService.listForTree(new HashMap<>(1));
//        return new Result<List<BomEO>>().ok(list);
//    }

    /**
     * 根据Bom清单id查询其下的子数据
     * @return
     */
    @GetMapping("listForTree/{bomId}")
    public Result<List<BomEO>> listForTree(@PathVariable("bomId") Long bomId){
        logger.info("======== BomController.listForTree() ========");
        List<BomEO> boms = this.bomService.getTreeListByBomId(bomId);
        return new Result<List<BomEO>>().ok(boms);
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<BomEO> info(@PathVariable("id") Long id){
        logger.info("======== BomController.info(entity => "+id+") ========");
        BomEO entity = this.bomService.getById(id);
        return new Result<BomEO>().ok(entity);
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增bom")
    @RequiresPermissions("basic:bom:create")
    public Result create(@RequestBody BomEO entity){
        logger.info("======== BomController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        entity.setOrgId(getUser().getOrgId());
        this.bomService.save(entity, getUserId());
        return new Result();
    }

    /**
     * 新增
     * @param entity
     * @return
     */
    @PostMapping("createSon")
    @OperationLog("新增bom")
    @RequiresPermissions("basic:bom:create")
    public Result createSon(@RequestBody BomEO entity){
        logger.info("======== BomController.createSon(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.bomService.saveSon(entity, getUserId());
        return new Result();
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改bom")
    @RequiresPermissions("basic:bom:update")
    public Result update(@RequestBody BomEO entity){
        logger.info("======== BomController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.bomService.updateById(entity, getUserId());
        return new Result();
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除bom")
    @RequiresPermissions("basic:bom:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== BomController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        this.bomService.removeByIds(ids, getUserId());
        return new Result();
    }

    /**
     * 设置状态
     *
     * @param entity
     * @return
     */
    @PostMapping("setStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:bom:setStatus")
    public Result setStatus(@RequestBody BomEO entity){
        logger.info("======== BomController.setStatus ========");
        this.bomService.updateStatus(entity, getUserId());
        return new Result();
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request){
        logger.info("======== BomController.import ========");
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId"));
        String projectNo = request.getParameter("projectNo");
        this.bomService.newImportFromExcel(list, orgId, projectNo);
        return new Result();
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("importFromPbom")
    public Result importFromPbomExcel(HttpServletRequest request){
        logger.info("======== BomController.importFromPbomExcel ========");
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId"));
        this.bomService.importFromPbomExcel(list, orgId);
        return new Result();
    }

    /**
     * 发布
     *
     * @param entity
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布")
    @RequiresPermissions("basic:bom:release")
    public Result release(@RequestBody BomEO entity){
        logger.info("======== BomController.release ========");
        this.bomService.updateReleaseStatus(entity, getUserId());
        return new Result();
    }

    /**
     * 取消发布
     *
     * @param entity
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布")
    @RequiresPermissions("basic:bom:cancelRelease")
    public Result cancelRelease(@RequestBody BomEO entity){
        logger.info("======== BomController.cancelRelease ========");
        this.bomService.updateReleaseStatus(entity, getUserId());
        return new Result();
    }

    /**
     * 导出
     * @param bomIds
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("basic:bom:export")
    public void export(HttpServletRequest req, HttpServletResponse resp,
                       @RequestParam("bomIds") Long[] bomIds){
        logger.info("======== BomController.export ========");
        //配置的.json文件
        JSONObject jsonObject = ExcelUtils.parseJsonFile("bom.json");
        List<BomEO> list = this.bomService.getAllBomsByBomIds(bomIds);
        //导出Excel
        ExcelUtils.exportExcel(resp, list, jsonObject);
    }


    @GetMapping("getBomsByMaterialId")
    public Result getBomsByMaterialId(@RequestParam("materialId") Long materialId){
        List<BomEO> boms = this.bomService.getBomsByMaterialId(materialId);
        return new Result<List<BomEO>>().ok(boms);
    }
}
