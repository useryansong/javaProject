package com.xchinfo.erp.controller.bsc.plm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WorkEndDateEO;
import com.xchinfo.erp.bsc.service.WorkEndDateService;
import com.xchinfo.erp.controller.BaseController;
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

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/workEndDate")
public class WorkEndDateController extends BaseController {

    @Autowired
    private WorkEndDateService workEndDateService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:workEndDate:info")
    public Result<IPage<WorkEndDateEO>> page(@RequestBody Criteria criteria){
        logger.info("======== WorkEndDateController.list() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<WorkEndDateEO> page = this.workEndDateService.selectPage(criteria);

        return new Result<IPage<WorkEndDateEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:workEndDate:info")
    public Result<WorkEndDateEO> info(@PathVariable("id") Long id){
        logger.info("======== WorkEndDateController.info(entity => "+id+") ========");

        WorkEndDateEO entity = this.workEndDateService.getById(id);

        return new Result<WorkEndDateEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建非工作日")
    @RequiresPermissions("basic:workEndDate:create")
    public Result create(@RequestBody WorkEndDateEO entity){
        logger.info("======== WorkEndDateController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.workEndDateService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新非工作日")
    @RequiresPermissions("basic:workEndDate:update")
    public Result update(@RequestBody WorkEndDateEO entity){
        logger.info("======== WorkEndDateController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.workEndDateService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除非工作日")
    @RequiresPermissions("basic:workEndDate:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== WorkEndDateController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workEndDateService.removeByIds(ids);

        return new Result();
    }
    /**
     * 启用
     * @param
     * @return
     */
    @PostMapping("updateStatus/enable")
    @OperationLog("设置状态-启用")
    @RequiresPermissions("basic:workEndDate:update")
    public Result updateStatusEnable(@RequestBody Long[] ids){
        logger.info("======== WorkEndDateController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workEndDateService.updateStatusById(ids,0);

        return new Result();
    }
    /**
     * 禁用
     * @param
     * @return
     */
    @PostMapping("updateStatus/disEnable")
    @OperationLog("设置状态-禁用")
    @RequiresPermissions("basic:workEndDate:update")
    public Result updateStatusDisEnable(@RequestBody Long[] ids){
        logger.info("======== WorkEndDateController.updateStatus========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.workEndDateService.updateStatusById(ids,1);

        return new Result();
    }

    /**
     * 导出
     * @param
     * @return
     */
    @PostMapping("export")
    @RequiresPermissions("basic:workEndDate:export")
    public Result<IPage<WorkEndDateEO>> export(@RequestBody Criteria criteria, HttpServletResponse response){
        logger.info("======== WorkEndDateController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        criteria.setSize(999999999);
        criteria.setCurrentPage(1);
        List<WorkEndDateEO> records = this.workEndDateService.selectPage(criteria).getRecords();
        if(records!=null){
            String[] column = {"日期", "类型", "描述", "所属机构", "状态","最后更新人","最后更新时间"};
            List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();

            for (int i = 0; i < records.size(); i++) {
                WorkEndDateEO item = records.get(i);
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.clear();
                map.put("日期", CommonUtil.convertNullToEmpty(DateUtils.format(item.getWorkEndDate()))+"");
                map.put("类型", CommonUtil.convertNullToEmpty(item.getDateTypeName())+"");
                map.put("描述", CommonUtil.convertNullToEmpty(item.getMessage())+"");
                map.put("所属机构", CommonUtil.convertNullToEmpty(item.getOrgName())+"");
                map.put("最后更新人", item.getLastModifiedBy()+"");
                map.put("最后更新时间", DateUtils.format(item.getLastModifiedTime(),DateUtils.DATE_TIME_PATTERN)+"");

                if (item.getStatus().equals("1")) {
                    map.put("状态", "禁用");
                } else {
                    map.put("状态", "启用");
                }
                list.add(map);
            }
            ExcelExportUtils.print(column, list, response,"非工作日数据");
        }
        return null;
    }
}
