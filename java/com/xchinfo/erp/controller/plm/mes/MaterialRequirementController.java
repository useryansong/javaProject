package com.xchinfo.erp.controller.plm.mes;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.mes.entity.MaterialRequirementEO;
import com.xchinfo.erp.mes.service.MaterialRequirementService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.*;


/**
 * @author roman.c
 * @date 2019/3/12
 * @update
 */
@RestController
@RequestMapping("/cmp/materialRequirement")
public class MaterialRequirementController extends BaseController {


    @Autowired
    private MaterialRequirementService materialRequirementService;
    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    //@RequiresPermissions("cmp:materialRequirement:info")
    public Result<IPage<MaterialRequirementEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialRequirementController.list() ========");

        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialRequirementEO> page = this.materialRequirementService.selectPage(criteria);

        return new Result<IPage<MaterialRequirementEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    //@RequiresPermissions("cmp:materialRequirement:info")
    public Result<MaterialRequirementEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialRequirementController.info(entity => "+id+") ========");

        MaterialRequirementEO entity = this.materialRequirementService.getById(id);

        return new Result<MaterialRequirementEO>().ok(entity);
    }


    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建原材料月需求")
   // @RequiresPermissions("cmp:materialRequirement:create")
    public Result create(@RequestBody MaterialRequirementEO entity){
        logger.info("======== MaterialRequirementController.create ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.materialRequirementService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新原材料月需求")
    //@RequiresPermissions("cmp:materialRequirement:update")
    public Result update(@RequestBody MaterialRequirementEO entity){
        logger.info("======== MaterialRequirementController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialRequirementService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除原材料月需求")
    //@RequiresPermissions("cmp:materialRequirement:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialRequirementController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialRequirementService.removeByIds(ids);

        return new Result();
    }


//    /**
//     * 导出报表
//     * @param request
//     * @param response
//     * @param requirementEOS 需要导出的数据
//     * @return
//     */
//    @PostMapping("export")
//    public Result export(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialRequirementEO[] requirementEOS){
//        JSONObject jsonObject= ExcelUtils.parseJsonFile("requirement_plan.json");
//
//        //导出Excel
//        ExcelUtils.exportExcel(response, Arrays.asList(requirementEOS), jsonObject);
//
//        return new Result();
//    }

    // 规格,牌号相同的数据进行合并
    private List<MaterialRequirementEO> getNewList(List<MaterialRequirementEO> list) {
        List<MaterialRequirementEO> newList = new ArrayList<>();
        String[] strArr = new String[list.size()];
        for(int i=0; i<list.size(); i++) {
            MaterialRequirementEO mri = list.get(i);
            String str = i+"";
            for(int j=i+1; j<list.size(); j++) {
                MaterialRequirementEO mrj = list.get(j);
//                if(mrj.getMaterialModelSpecific().equals(mri.getMaterialModelSpecific()) &&
//                    mrj.getSpecification().equals(mri.getSpecification())) {
                if(mrj.getInventoryCode()!=null && mri.getInventoryCode()!=null && mrj.getInventoryCode().equals(mri.getInventoryCode())) {
                    str += ("|" +j);
                }
            }
            strArr[i] = str;
        }

        for(int k=0; k<strArr.length; k++) {
            String s = strArr[k];
            for (int p=0; p<strArr.length && p!=k; p++){
                Set sSet = new HashSet(Arrays.asList(s.split("\\|")));
                Set pSet = new HashSet(Arrays.asList(strArr[p].split("\\|")));
                if(pSet.containsAll(sSet)) {
                    strArr[k] = "";
                }
            }
        }

        List<String> listTemp = new ArrayList<>();
        for(int f=0; f<strArr.length; f++) {
            if(!"".equals(strArr[f])) {
                listTemp.add(strArr[f]);
            }
        }

        for(int q=0; q<listTemp.size(); q++) {
            String str = listTemp.get(q);
            String[] strs = str.split("\\|");
            MaterialRequirementEO mr = list.get(Integer.parseInt(String.valueOf(strs[0])));
            for(int l=1; l<strs.length; l++) {
                MaterialRequirementEO mrTemp = list.get(Integer.parseInt(String.valueOf(strs[l])));
                mr.setRequireCount(mrTemp.getRequireCount() + mr.getRequireCount());
                mr.setTotalWeight(mrTemp.getTotalWeight() + mr.getTotalWeight());
            }
            newList.add(mr);
        }
        return newList;
    }

    /**
     * 导出报表
     * @param request
     * @param response
     * @param requirementEOS 需要导出的数据
     * @return
     */
    @PostMapping("export")
    public Result export(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialRequirementEO[] requirementEOS){
        // 合并数据导出：牌号，材料规格完全一致才能合并
        List<MaterialRequirementEO> list = getNewList(Arrays.asList(requirementEOS));
        JSONObject jsonObject= ExcelUtils.parseJsonFile("requirement_plan_new.json");
        String fileName = requirementEOS[0].getOrgName() + "原材料需求——" + DateUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
        jsonObject.put("fileName", fileName);
        //导出Excel
        ExcelUtils.exportExcel(response, list, jsonObject);
        return new Result();
    }
}
