package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialRelationshipEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.bsc.entity.ProjectEO;
import com.xchinfo.erp.bsc.mapper.BomMapper;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.mapper.MaterialRelationshipMapper;
import com.xchinfo.erp.bsc.mapper.MaterialSupplierMapper;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.DeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.ReceiveOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.StockAccountMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.StringUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class MaterialService extends BaseServiceImpl<MaterialMapper, MaterialEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private MaterialRelationshipMapper materialRelationshipMapper;

    @Autowired
    private BomMapper bomMapper;

    @Autowired
    private OrgService orgService;

//    @Autowired
//    private ISupplierService supplierService;

    @Autowired
    @Lazy
    private MaterialSupplierService materialSupplierService;

    @Autowired
    private MaterialSupplierMapper materialSupplierMapper;

    @Autowired
    private StockAccountMapper stockAccountMapper;

    @Autowired
    private ReceiveOrderDetailMapper receiveOrderDetailMapper;

    @Autowired
    private DeliveryOrderDetailMapper deliveryOrderDetailMapper;


    public List<MaterialEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialEO.class)
    public boolean save(MaterialEO entity) throws BusinessException {

        //校验机构权限
        if(!checkPer(entity.getOrgId())){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        //校验同一归属机构是否存在相同的存货编号、零件号
        List<MaterialEO> materialList = this.baseMapper.selectDuplicateNo(entity);

        if (null != materialList && materialList.size() > 0) {
            throw new BusinessException("新增的存货编号、零件号在同机构内唯一！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_material", entity);
        AssertUtils.isBlank(code);

        // 获取项目id,有则插入，无则暂时为null
        if(!StringUtils.isEmpty(entity.getProjectNo())){
            List<ProjectEO> list = this.baseMapper.getProject(entity.getProjectNo(),entity.getOrgId());
            if(list!=null&&list.size()>0){
                if(!StringUtils.isEmpty(list.get(0).getProjectName())){
                    entity.setProjectId(list.get(0).getProjectId());
                }
            }
        }
        entity.setMaterialCode(code);
        super.save(entity);

        //新增默认供应商
//        SupplierEO supplierEO = this.supplierService.getById(entity.getSupplierId());

        MaterialSupplierEO materialSupplierEO = new MaterialSupplierEO();
        materialSupplierEO.setMaterialId(entity.getMaterialId());
        materialSupplierEO.setSupplierId(entity.getSupplierId());
        materialSupplierEO.setSupplierMaterialCode(entity.getMaterialCode());
        materialSupplierEO.setSupplierMaterialName(entity.getMaterialName());
        materialSupplierEO.setIsDefault(1);
        materialSupplierEO.setSupplyType(1);
        this.materialSupplierService.save(materialSupplierEO);

//        if (null != entity.getParentMaterialRelations() && entity.getParentMaterialRelations().size()> 0) {
//            //保存父物料
//            for (MaterialRelationshipEO materialRelationshipEO : entity.getParentMaterialRelations()) {
//                materialRelationshipEO.setChildMaterialId((Long) entity.getId());
//                if (!retBool(this.materialRelationshipMapper.saveRelationship(materialRelationshipEO))) {
//                    throw new BusinessException("保存父物料失败！");
//                }
//            }
//        }
//        if (null !=entity.getChileMaterialRelations() && entity.getChileMaterialRelations().size() > 0) {
//            //保存子物料
//            for (MaterialRelationshipEO materialRelationshipEO : entity.getChileMaterialRelations()) {
//                materialRelationshipEO.setParentMaterialId((Long) entity.getId());
//                if (!retBool(this.materialRelationshipMapper.saveRelationship(materialRelationshipEO))) {
//                    throw new BusinessException("保存子物料失败！");
//                }
//            }
//        }

        // 相同的物料的归属机构下，bom根据零件号同步新增物料的id
        this.bomMapper.synchAddMaterial(entity.getElementNo(), entity.getOrgId());

        return true;
    }

    @Override
    public MaterialEO getById(Serializable id) {

        MaterialEO materialEO = this.baseMapper.selectById(id);

        //校验机构权限
        if(!checkPer(materialEO.getOrgId())){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        //查询父物料
//        List<MaterialRelationshipEO> parentMaterialRelations = this.materialRelationshipMapper.selectByChildId((Long) id);
//        materialEO.setParentMaterialRelations(parentMaterialRelations);

        //查询子物料
        List<MaterialRelationshipEO> childMaterialRelations = this.materialRelationshipMapper.selectByPartnerId((Long) id);
        materialEO.setChileMaterialRelations(childMaterialRelations);

        return materialEO;

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MaterialEO entity) throws BusinessException {

        //校验机构权限
        if(!checkPer(entity.getOrgId())){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        //更新前校验同一归属机构是否存在相同的存货编号、（零件号+版本号）
        List<MaterialEO> materialList = this.baseMapper.selectDuplicateNo(entity);

        if (null != materialList && materialList.size() > 0) {
            throw new BusinessException("修改时存货编号、（零件号+版本号）在同机构内唯一！");
        }
        // 获取项目id
        if(!StringUtils.isEmpty(entity.getProjectNo())){
            List<ProjectEO> list = this.baseMapper.getProject(entity.getProjectNo(),entity.getOrgId());
            if(list!=null&&list.size()>0){
                if(!StringUtils.isEmpty(list.get(0).getProjectName())){
                    entity.setProjectId(list.get(0).getProjectId());
                }
            }
        }

        MaterialEO oldMaterialEO = this.baseMapper.selectById(entity.getMaterialId());
        //不是辅料才校验供应商
        if(entity.getConsumedProducts() == 0) {
            if (null !=entity.getSupplierId() && !entity.getSupplierId().equals(oldMaterialEO.getSupplierId())) {
                MaterialSupplierEO materialSupplierEO = new MaterialSupplierEO();
                materialSupplierEO.setSupplierId(entity.getSupplierId());
                materialSupplierEO.setMaterialId(entity.getMaterialId());

                //若是供应商变化了判断新供应商是否存在（不存在则新增，存在着修改默认）
                Integer count = this.materialSupplierMapper.selectIsExistCount(materialSupplierEO);
                if (count > 0) {
                    materialSupplierEO = this.baseMapper.selectIsExistCount(materialSupplierEO);
                    materialSupplierEO.setIsDefault(1);
                    this.materialSupplierService.updateById(materialSupplierEO);
                } else {
                    materialSupplierEO.setSupplierMaterialCode(entity.getMaterialCode());
                    materialSupplierEO.setSupplierMaterialName(entity.getMaterialName());
                    materialSupplierEO.setIsDefault(1);
                    materialSupplierEO.setSupplyType(1);
                    this.materialSupplierService.save(materialSupplierEO);
                }


                this.materialSupplierMapper.updateOtherStatusById(materialSupplierEO.getMaterialSupplierId(), materialSupplierEO.getMaterialId());

            }
        }

//        Integer result = 0;

//        //先删除，再保存
//        result  = this.materialRelationshipMapper.removeByMaterialId((Long) entity.getId());
//
//        //父物料
//        int parentInt = entity.getParentMaterialRelations().size();
//        //子物料
//        int childInt = entity.getChileMaterialRelations().size();
//
//        if (null != result && result >= 0){
//
//            //保存父物料
//            if (parentInt > 0){
//                for (MaterialRelationshipEO materialRelationshipEO : entity.getParentMaterialRelations()) {
//                    materialRelationshipEO.setChildMaterialId((Long) entity.getId());
//                    if (!retBool(this.materialRelationshipMapper.saveRelationship(materialRelationshipEO))) {
//                        throw new BusinessException("保存父物料失败！");
//                    }
//                }
//            }
//
//            //保存父物料
//            if (childInt > 0){
//                //保存子物料
//                for (MaterialRelationshipEO materialRelationshipEO : entity.getChileMaterialRelations()) {
//                    materialRelationshipEO.setParentMaterialId((Long) entity.getId());
//                    if (!retBool(this.materialRelationshipMapper.saveRelationship(materialRelationshipEO))) {
//                        throw new BusinessException("保存子物料失败！");
//                    }
//                }
//            }
//
//
//        }
//        else
//        {
//            throw new BusinessException("删除物料失败！");
//        }

        boolean flag = super.updateById(entity);

        // 若零件号发生变化则同步零件号到对应的Bom数据
        if(!oldMaterialEO.getElementNo().equals(entity.getElementNo())) {
            this.bomMapper.synchElementNoUpdateMaterial(entity.getMaterialId(), entity.getOrgId());
        }
        // 若是否生产发生变化则同步是否生产到对应的Bom数据
        if(oldMaterialEO.getIsProduct()!=null && entity.getIsProduct()!=null) {
            if(!oldMaterialEO.getIsProduct().equals(entity.getIsProduct())) {
                this.bomMapper.synchIsProductUpdateMaterial(entity.getMaterialId(), entity.getOrgId());
            }
        }
        // 若是否采购发生变化则同步是否采购到对应的Bom数据
        if(oldMaterialEO.getIsPurchase()!=null && entity.getIsPurchase()!=null) {
            if (!oldMaterialEO.getIsPurchase().equals(entity.getIsPurchase())) {
                this.bomMapper.synchIsPurchaseUpdateMaterial(entity.getMaterialId(), entity.getOrgId());
            }
        }
        return flag;
    }

    @Override
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = MaterialEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {

        Integer result = 0;
        // 删除关系
        for (Serializable id : idList) {
            MaterialEO materialEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(materialEO.getOrgId())){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            //是否存在物料台账记录
            Integer count = this.baseMapper.selectCountStock((Long) id);
            if(count > 0){
                throw new BusinessException("物料 ["+materialEO.getMaterialCode()+" - "+materialEO.getMaterialName()+"] 存在物料台账信息，不允许删除操作，只允许禁用！");
            }

            result = this.materialRelationshipMapper.removeByMaterialId((Long) id);

            if (null == result || result < 0) {
                throw new BusinessException("删除物料关系失败！");
            }

            super.removeById(id);
        }

        return true;
    }

    /**
     * 修改状态
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long Id, Integer status) throws BusinessException {

        MaterialEO materialEO = this.baseMapper.selectById(Id);
        //校验机构权限
        if(!checkPer(materialEO.getOrgId())){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        if(status.intValue() == 0) { // 禁用需要判断库存是否为0
            Double stock = this.baseMapper.getStockByMaterialId(Id);
            if(stock!=null && stock.doubleValue()!=0) {
                throw new BusinessException("库存为0才可禁用，请确认！");
            }
        }

        return this.baseMapper.updateStatusById(Id, status);
    }

    /**
     * 新增物料关系
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addRelation(MaterialRelationshipEO entity) throws BusinessException {

        MaterialEO pMaterialEO = this.baseMapper.selectById(entity.getParentMaterialId());
        MaterialEO cMaterialEO = this.baseMapper.selectById(entity.getChildMaterialId());
        //校验机构权限
        if(!checkPer(pMaterialEO.getOrgId())){
            throw new BusinessException("父物料 ["+pMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        //校验机构权限
        if(!checkPer(cMaterialEO.getOrgId())){
            throw new BusinessException("子物料 ["+cMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        int count = this.materialRelationshipMapper.selectRelationCount(entity);

        if (count > 0) {
            throw new BusinessException("已存在子物料关系，不能进行新增操作！");
        }

        MaterialEO materialEO = this.baseMapper.selectById(entity.getChildMaterialId());
        if(materialEO.getStatus() == 0){
            throw new BusinessException("该物料为禁用状态，不允许维护物料关系，请刷新！");
        }

        //更新子节点不存在
        this.baseMapper.updateIsExistChildById(entity.getParentMaterialId(), 1);

        return retBool(this.materialRelationshipMapper.saveRelationship(entity));
    }

    /**
     * 删除单个物料关系
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRelation(MaterialRelationshipEO entity) throws BusinessException {

        MaterialEO pMaterialEO = this.baseMapper.selectById(entity.getParentMaterialId());
        MaterialEO cMaterialEO = this.baseMapper.selectById(entity.getChildMaterialId());
        //校验机构权限
        if(!checkPer(pMaterialEO.getOrgId())){
            throw new BusinessException("父物料 ["+pMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        //校验机构权限
        if(!checkPer(cMaterialEO.getOrgId())){
            throw new BusinessException("子物料 ["+cMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        //更新子节点不存在
        this.baseMapper.updateIsExistChildById(entity.getParentMaterialId(), 0);

        return this.materialRelationshipMapper.deleteRelation(entity);
    }

    /**
     * 更新单个物料关系
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRelation(Long oldPId, Long oldCId, Long newPId, Long newCId) throws BusinessException {

//        MaterialRelationshipEO newEntity = new MaterialRelationshipEO();
//        //设置参数
//        newEntity.setParentMaterialId(newPId);
//        newEntity.setChildMaterialId(newCId);
//
//        if(oldPId != newPId || oldCId != newCId){
//
//            //看更新后的物料是否存在
//            int count = this.materialRelationshipMapper.selectRelationCount(newEntity);
//
//            if(count > 0){
//                throw new BusinessException("此物料关系已存在！");
//            }
//        }
        if(oldCId.equals(newCId)){
            return true;
        }

        MaterialEO pMaterialEO = this.baseMapper.selectById(newPId);
        MaterialEO cMaterialEO = this.baseMapper.selectById(newCId);
        //校验机构权限
        if(!checkPer(pMaterialEO.getOrgId())){
            throw new BusinessException("父物料 ["+pMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        //校验机构权限
        if(!checkPer(cMaterialEO.getOrgId())){
            throw new BusinessException("子物料 ["+cMaterialEO.getMaterialName()+"] 的归属机构的机构权限不存在该用户，请确认！");
        }

        MaterialEO materialEO = this.baseMapper.selectById(newCId);
        if(materialEO.getStatus() == 0){
            throw new BusinessException("该物料为禁用状态，不允许维护物料关系，请刷新！");
        }

        return this.materialRelationshipMapper.updateRelation(oldPId, oldCId, newPId, newCId);
    }

    public IPage<MaterialEO> selectInventoryPage(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<MaterialEO> wrapper = new QueryWrapper<MaterialEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<MaterialEO> totalList = this.baseMapper.selectInventoryPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<MaterialEO> list = this.baseMapper.selectInventoryPage(param);

        IPage<MaterialEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    public MaterialEO getByMaterialCode(String materialCode) {
        return this.baseMapper.getByMaterialCode(materialCode);
    }


    public Boolean checkPer(Long orgId){
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public String uploadPhoto(String extName,String materialId,byte[] content,File rootloaclPath) throws BusinessException {


        MaterialEO materialEO = this.baseMapper.selectById(Long.valueOf(materialId));
        String localFileName = materialEO.getMaterialId()+"_"+materialEO.getElementNo();

        try {
            // 生成上传目录
            File upload = new File(rootloaclPath.getAbsolutePath(),"statics/upload/imgs/");
            if(!upload.exists()) {
                upload.mkdirs();
            }
            String filePath = upload + File.separator + localFileName+extName;
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(content);
            outputStream.close();
            materialEO.setCustomStringField5(filePath);
//            materialEO.setUrl(filePath);
            materialEO.setUrl("/statics/upload/imgs/"+localFileName+extName);
            super.updateById(materialEO);
        } catch (Exception e) {
            throw new BusinessException("解析图片出错！");
        }
        return materialEO.getUrl();
    }

    public MaterialEO getByElementNoAndOrgId(String elementNo, Long orgId) throws BusinessException {
        return this.baseMapper.getByElementNoAndOrgId(elementNo, orgId);
    }

    public MaterialEO getSonMaterialForW(Long materialId, MaterialEO material) {
        material = this.baseMapper.getSonMaterial(materialId);
//        if(material.getElementNo().endsWith("-W")) {
//            this.getSonMaterialForW(material.getMaterialId(), material);
//        }
        if(material != null) {
            MaterialEO materialTemp = this.baseMapper.getSonMaterial(material.getMaterialId());
            if(materialTemp != null) {
                getSonMaterialForW(material.getMaterialId(), material);
            }
        }

        return material;
    }

    public MaterialEO getSonMaterial(Long materialId) {
        return this.baseMapper.getSonMaterial(materialId);
    }

    public MaterialEO getParentMaterial(Long materialId) {
        return this.baseMapper.getParentMaterial(materialId);
    }

    public Map getAllMaterialsForReleaseWeekPlan(Long tempMaterialId, boolean isQueryMaterialRelationship, String materialName, String elementNo) {
        String errorMsg = "";
        String resultMsg = "";
        Map map = new HashedMap();

        MaterialEO material = this.getById(tempMaterialId);

        // 是否采购，是否制造值为空时默认为否
        if(material != null) {
            if(material.getIsProduct() == null) {
                material.setIsProduct(0);
            }
            if(material.getIsPurchase() == null) {
                material.setIsPurchase(0);
            }

            if(material.getIsPurchase() == 0) {
                if(material.getIsProduct() == 0) { // 非采购，非制造
                    errorMsg += ("物料[" + material.getMaterialName() + " " + material.getElementNo() + "]的是否制造与是否采购值都为否!<br/>");
                } else {
                    map.put("productMaterial", material);
                }
            } else if(material.getIsPurchase() == 1) {
                if(material.getIsProduct() == 0) { // 是采购，非制造
                    if(material.getSupplierId() == null) {
                        errorMsg += ("物料[" + material.getMaterialName() + " " + material.getElementNo() + "]未设置默认供应商!<br/>");
                    } else {
                        map.put("purchaseMaterial", material);
                    }
                } else if(material.getIsProduct() == 1) { // 是采购，是制造
                    errorMsg += ("物料[" + material.getMaterialName() + " " + material.getElementNo() + "]的是否制造与是否采购值都为是!<br/>");
                }
            }

            // 委外相关(非采购，是制造)
            if((material.getIsProduct()==1 && material.getIsPurchase()==0)) {
//                if(material.getElementNo().toUpperCase().endsWith("-W")) {
                MaterialEO materialTemp;

                if(isQueryMaterialRelationship) { // 查询物料关系中对应的黑件(支持多次委外)
                    materialTemp = this.getSonMaterialForW(material.getMaterialId(), new MaterialEO());
                } else { // 直接去掉-W(或者-A)查询物料表
                    materialTemp = this.getByElementNoAndOrgId(material.getElementNo().substring(0, material.getElementNo().length()-2), material.getOrgId());
                }

                if(materialTemp == null) {
//                    resultMsg += "物料[" + material.getMaterialName() + " " + material.getElementNo() + "]对应的关系物料不存在!<br/>";
                } else {
                    if(materialTemp.getSupplierId() == null) {
                        errorMsg += ("物料[" + materialTemp.getMaterialName() + " " + materialTemp.getElementNo() + "]未设置默认供应商!<br/>");
                    } else {
                        map.put("outsideMaterial", materialTemp);
                    }
                }
//                }
            }
        } else {
            errorMsg += ("物料[" + materialName + " " + elementNo + "]不存在!<br/>");
        }


        map.put("errorMsg", errorMsg);
        map.put("resultMsg", resultMsg);
        return map;
    }

    public List<MaterialEO> selectAllMaterialByOrgId(Long orgId) {
        return this.baseMapper.selectAllMaterialByOrgId(orgId);
    }

    public List<MaterialEO> getByMaterialIds(Long[] ids) throws BusinessException {
        if(ids==null || ids.length==0) {
            throw new BusinessException("请先选择数据!");
        }

        String sqlStr = "";
        for(Long id : ids) {
            sqlStr += (id + ",");
        }
        if(!sqlStr.equals("")) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }

        return this.baseMapper.getByMaterialIds(sqlStr);
    }

//    public List<MaterialEO> getSendReceiveStore(List<MaterialEO> materials, String month) {
//        List<MaterialEO> list = new ArrayList<>();
//
//        List<Long> materialIds = new ArrayList<>();
//        if(materials!=null && materials.size()>0) {
//            for(MaterialEO material : materials) {
//                materialIds.add(material.getMaterialId());
//            }
//            if(materialIds!=null && materialIds.size()>0) {
//                materialIds.add(Long.valueOf(-1));
//            }
//
//            Date monthDate = DateUtils.stringToDate(month, "yyyy-MM");
//            String preMonthStr = DateUtils.format(DateUtils.addDateMonths(monthDate, -1), "yyyy-MM");
//
//            // 获取本月入库量
//            List<ReceiveOrderDetailEO> receiveOrderDetails = this.receiveOrderDetailMapper.getByMaterialIdsAndReceiveDate(materialIds, month);
//            // 获取本月出库量
//            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailMapper.getByMaterialIdsAndDeliveryDate(materialIds, month);
//            // 获取本月期初账上结余量(即上月账上结余量)
//            List<StockAccountEO> preStockAccounts = this.stockAccountMapper.getByMaterialIdsAndMonth(materialIds, preMonthStr.substring(0,4) + preMonthStr.substring(5,7));
//            // 获取本月期末账上结余量(即本月账上结余量)
//            List<StockAccountEO> currentStockAccounts = this.stockAccountMapper.getByMaterialIdsAndMonth(materialIds, month.substring(0,4) + month.substring(5,7));
//
//            for(MaterialEO material : materials) {
//                if(preStockAccounts!=null && preStockAccounts.size()>0) {
//                    for(StockAccountEO preStockAccount : preStockAccounts) {
//                        if(preStockAccount.getMaterialId().longValue() == material.getMaterialId().longValue()) {
//                            material.setPreCount(preStockAccount.getCount());
//                        }
//                    }
//                }
//
//                if(currentStockAccounts!=null && currentStockAccounts.size()>0) {
//                    for(StockAccountEO currentStockAccount : currentStockAccounts) {
//                        if(currentStockAccount.getMaterialId().longValue() == material.getMaterialId().longValue()) {
//                            material.setCurrentCount(currentStockAccount.getCount());
//                        }
//                    }
//                }
//
//                if(receiveOrderDetails!=null && receiveOrderDetails.size()>0) {
//                    for(ReceiveOrderDetailEO receiveOrderDetail : receiveOrderDetails) {
//                        if(receiveOrderDetail.getMaterialId().longValue() == material.getMaterialId().longValue()) {
//                            material.setSumReceiveAmount(receiveOrderDetail.getSumRelReceiveAmount());
//                        }
//                    }
//                }
//
//                if(deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
//                    for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
//                        if(deliveryOrderDetail.getMaterialId().longValue() == material.getMaterialId().longValue()) {
//                            material.setSumDeliveryAmount(deliveryOrderDetail.getSumRelDeliveryAmount());
//                        }
//                    }
//                }
//
//                if((material.getPreCount()!=null && material.getPreCount().doubleValue()!=0) ||
//                    (material.getCurrentCount()!=null && material.getCurrentCount().doubleValue()!=0) ||
//                    (material.getSumReceiveAmount()!=null && material.getSumReceiveAmount().doubleValue()!=0) ||
//                    (material.getSumDeliveryAmount()!=null && material.getSumDeliveryAmount().doubleValue()!=0)) {
//                    list.add(material);
//                }
//
//            }
//        }
//
//        return list;
//    }



    public List<MaterialEO> getSendReceiveStore(Map map, String month) {
        Date monthDate = DateUtils.stringToDate(month, "yyyy-MM");
//        map.put("receiveDate", month);
//        map.put("deliveryDate", month);
//        map.put("deliveryDate", month);

        map.put("curMonth", month.substring(0,4) + month.substring(5,7));
        String preMonthStr = DateUtils.format(DateUtils.addDateMonths(monthDate, -1), "yyyy-MM");
        map.put("preMonth", preMonthStr.substring(0,4) + preMonthStr.substring(5,7));
        int preYear = Integer.parseInt(preMonthStr.substring(0,4));
        int preMonth = Integer.parseInt(preMonthStr.substring(5,7));
        int curYear = Integer.parseInt(month.substring(0,4));
        int curMonth = Integer.parseInt(month.substring(5,7));
        map.put("preMonthLastDay", DateUtils.getLastDayOfMonth(preYear, preMonth));
        map.put("curMonthLastDay", DateUtils.getLastDayOfMonth(curYear, curMonth));
        return this.baseMapper.getSendReceiveStore(map);
    }

    public MaterialEO getByElementNo(String elementNo, UserEO user) {
        return this.baseMapper.getByElementNoAndOrgId(elementNo, user.getOrgId());
    }
}



