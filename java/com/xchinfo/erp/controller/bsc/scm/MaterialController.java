package com.xchinfo.erp.controller.bsc.scm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialRelationshipEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.vo.MaterialEOFiedesVO;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.JsonUtil;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/material")
public class MaterialController extends BaseController {

    @Autowired
    private MaterialService materialService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:material:info")
    public Result<IPage<MaterialEO>> page(@RequestBody Criteria criteria){
        logger.info("======== MaterialController.page() ========");
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);

        IPage<MaterialEO> page = this.materialService.selectPage(criteria);

        return new Result<IPage<MaterialEO>>().ok(page);
    }

    /**
     * 查找所有物料
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<MaterialEO>> list(){
        logger.info("======== MachineController.list() ========");

        List<MaterialEO> entities = this.materialService.listAll(getUserId());

        return new Result<List<MaterialEO>>().ok(entities);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:material:info")
    public Result<MaterialEO> info(@PathVariable("id") Long id){
        logger.info("======== MaterialController.info(entity => "+id+") ========");

        MaterialEO entity = this.materialService.getById(id);

        return new Result<MaterialEO>().ok(entity);
    }

    /**
     * 根据零件号查找
     *
     * @param elementNo
     * @return
     */
    @GetMapping("getByElementNo")
    public Result<MaterialEO> getByElementNo(@RequestParam("elementNo") String elementNo){
        logger.info("======== MaterialController.getByElementNo ==> "+elementNo+") ========");

        MaterialEO entity = this.materialService.getByElementNo(elementNo, getUser());

        return new Result<MaterialEO>().ok(entity);
    }

    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建物料")
    @RequiresPermissions("basic:material:create")
    public Result create(@RequestBody MaterialEO entity){
        logger.info("======== MaterialController.create(ID => "+entity.getId()+") ========");
        entity.setOrgId(getOrgId());
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.materialService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新物料")
    @RequiresPermissions("basic:material:update")
    public Result update(@RequestBody MaterialEO entity){
        logger.info("======== MaterialController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除物料")
    @RequiresPermissions("basic:material:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== MaterialController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.materialService.removeByIds(ids);

        return new Result();
    }

    /**
     * 设置状态
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:material:updateStatus")
    public Result updateStatus(@RequestParam("id") Long id,@RequestParam("status") Integer status){
        logger.info("======== MaterialController.updateStatus(id => "+id+") ========");

        logger.info("================"+status);

        this.materialService.updateStatusById(id,status);

        return new Result();
    }

    /**
     * 物料关系
     *
     * @param
     * @return
     */
    @PostMapping("addRelation")
    @OperationLog("新增物料关系")
    @RequiresPermissions("basic:material:updateRelation")
    public Result addRelation(@RequestBody MaterialRelationshipEO entity){
        logger.info("======== MaterialController.addRelation========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialService.addRelation(entity);

        return new Result();
    }

    /**
     * 物料关系
     *
     * @param
     * @return
     */
    @DeleteMapping("deleteRelation")
    @OperationLog("删除物料关系")
    public Result deleteRelation(@RequestBody MaterialRelationshipEO entity) {

        logger.info("======== MaterialController.deleteRelation========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.materialService.deleteRelation(entity);

        return new Result();
    }

    /**
     * 更新物料关系
     *
     * @param
     * @return
     */
    @PostMapping("updateRelation")
    @OperationLog("更新物料关系")
    public Result updateRelation(@RequestParam("oldPId") Long oldPId,@RequestParam("oldCId") Long oldCId,@RequestParam("newPId") Long newPId,@RequestParam("newCId") Long newCId) {

        logger.info("======== MaterialController.updateRelation========");

        this.materialService.updateRelation(oldPId,oldCId,newPId,newCId);

        return new Result();
    }



    /**
     * 上传图片
     *
     * @param
     * @return
     */
    @PostMapping("import")
    @OperationLog("上传图片")
    public Result importPhoto(HttpServletRequest request) {
        logger.info("======== MaterialController.importPhoto========");

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartHttpServletRequest.getFile("file");

        if(mf == null){
            throw new BusinessException("没有文件对象,请正确设置！");
        }

        String extName = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));
        if(!".png".equals(extName) && !".jpg".equals(extName) && !".jpeg".equals(extName) && !".bmp".equals(extName)){
            throw new BusinessException("上传图片格式不对,请上传正确的图片！");
        }
        String materialId = request.getParameter("materialId");
        byte[] content = null;
        String url = "";
        try {
            content = mf.getBytes();
            // //文件放置本地/获取项目根目录
            File rootloaclPath = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!rootloaclPath.exists()) {
                rootloaclPath = new File("");
            }

            url = this.materialService.uploadPhoto(extName,materialId,content,rootloaclPath);
            logger.info("======== ========"+rootloaclPath);

        }catch (Exception e) {
            throw new BusinessException("图片解析失败,请上传正确的图片！");
        }
        return new Result<String>().ok(url);
    }


    @PostMapping("resourceUrl")
    @OperationLog("获取包的根路径")
    public Result resourceUrl() {
        logger.info("======== MaterialController.resourceUrl========");
        String rootPath = "";
        try {
            rootPath = ResourceUtils.getURL("classpath:").getPath();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new Result<String>().ok(rootPath);
    }

    /**
     * 导出
     * @param request
     * @param response
     * @param materialEOFiedesVO 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("basic:material:export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody MaterialEOFiedesVO materialEOFiedesVO){

        //配置的.json文件
        JSONObject jsonObject = ExcelUtils.parseJsonFile("material.json");
        Integer[] ids = materialEOFiedesVO.getIds();
        List<Integer> idList=Arrays.asList(ids);
        Map map = JsonUtil.fromJson(jsonObject.toJSONString(),Map.class);
        List<Map> fieldes = (List<Map>)map.get("fields");
        List<Map> choosees = new ArrayList<Map>();
        for(Map temp:fieldes){
            Integer cellIndex = (Integer)temp.get("cellIndex");
            if(idList.contains(cellIndex)){
                choosees.add(temp);
            }
        }
        for(int i=0;i<choosees.size();i++){
            choosees.get(i).put("cellIndex",i);
        }
        map.put("fields",choosees);

        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(materialEOFiedesVO.getMaterialEOs()), JSONObject.parseObject(JsonUtil.toJson(map)));
    }

    /**
     * 查找所有符合条件的物料(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    public Result<List<MaterialEO>> list(@RequestBody Criteria criteria) {
        Criterion criterion = new Criterion();
        criterion.setField("x.user_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        criteria.setSize(10000000);

        IPage<MaterialEO> page = this.materialService.selectPage(criteria);

        return new Result<List<MaterialEO>>().ok(page.getRecords());
    }

    /**
     * 获取物料
     *
     * @param ids
     * @return
     */
    @PostMapping("getByMaterialIds")
    @OperationLog("获取物料")
    public Result<List<MaterialEO>> getByMaterialIds(@RequestBody Long[] ids){
        logger.info("======== MaterialController.getByMaterialIds() ========");
        AssertUtils.isArrayEmpty(ids, "id");
        List<MaterialEO> list = this.materialService.getByMaterialIds(ids);
        return new Result<List<MaterialEO>>().ok(list);
    }

    /**
     * 获取物料收发存(分页)
     *
     * @param criteria
     * @return
     */
    @PostMapping("getSendReceiveStorePage")
    @OperationLog("获取物料收发存")
    public Result<IPage<MaterialEO>> getSendReceiveStorePage(@RequestBody Criteria criteria, @RequestParam("month") String month){
        logger.info("======== MaterialController.getSendReceiveStorePage() ========");
//        int pageSize = criteria.getSize();
//        int currentPage = criteria.getSize();
//        Criterion criterion = new Criterion();
//        criterion.setField("x.user_id");
//        criterion.setOp("eq");
//        criterion.setValue(getUserId()+"");
//        criteria.getCriterions().add(criterion);
//        criteria.setSize(10000000);
//        criteria.setCurrentPage(1);
////        IPage<MaterialEO> tempPage = this.materialService.selectPage(criteria);
//        List<MaterialEO> list = this.materialService.getSendReceiveStore(tempPage.getRecords(), month);

        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<MaterialEO> list = this.materialService.getSendReceiveStore(map, month);
        IPage<MaterialEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());
        return new Result<IPage<MaterialEO>>().ok(page);
    }


    /**
     * 获取物料收发存(所有)
     *
     * @param criteria
     * @return
     */
    @PostMapping("getSendReceiveStoreList")
    @OperationLog("获取物料收发存")
    public Result<List<MaterialEO>> getSendReceiveStoreList(@RequestBody Criteria criteria, @RequestParam("month") String month){
        logger.info("======== MaterialController.getSendReceiveStoreList() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<MaterialEO> list = this.materialService.getSendReceiveStore(map, month);
        return new Result<List<MaterialEO>>().ok(list);
    }


    /**
     * 物料收发存导出
     * @param request
     * @param response
     * @param materials 需要导出的数据
     * @return
     */
    @PostMapping("exportMaterialSendReceiveStore")
    @OperationLog("导出")
    public void exportMaterialSendReceiveStore(HttpServletRequest request, HttpServletResponse response,
                       @RequestBody MaterialEO[] materials, @RequestParam("fileName") String fileName){
        //配置的.json文件
        JSONObject jsonObject = ExcelUtils.parseJsonFile(fileName);
        //导出Excel
        ExcelUtils.exportExcel(response,  Arrays.asList(materials), jsonObject);
    }
}
