package com.xchinfo.erp.bsc.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.mapper.BomMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhongy
 * @date 2019/4/10
 * @update
 */
@Service
public class BomService extends BaseServiceImpl<BomMapper, BomEO> {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;


    private BomEO setBomByMaterialId(BomEO entity, String functionName){
        MaterialEO material = this.materialService.getById(entity.getMaterialId());
        entity.setFigureVersion(material.getFigureVersion());
//        entity.setMaterialModel(material.getMaterialModel());
//        entity.setWeight(material.getWeight());
        entity.setUnitId(material.getFirstMeasurementUnit());
        entity.setIsProduct(material.getIsProduct());
        entity.setIsPurchase(material.getIsPurchase());
        entity.setMaterialName(material.getMaterialName());
        entity.setElementNo(material.getElementNo());
        entity.setProjectNo(material.getProjectNo());
//        if("saveSon".equals(functionName)){
//            entity.setEbomElementNo(material.getEbomElementNo());
//        }

        return entity;
    }

    // 通过传递的参数获取数据库的实体
    private BomEO getRootBom(BomEO entity){
        QueryWrapper<BomEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("element_no", entity.getElementNo());
        queryWrapper.eq("parent_bom_id", 0);
        queryWrapper.eq("org_id", entity.getOrgId());
        BomEO bom = this.baseMapper.selectOne(queryWrapper);
        return bom!=null?bom:null;
    }

    // 通过Bom父节点实体获取该父节点其下所有的子节点Bom清单数据
    public List<BomEO> getSonBoms(BomEO parentBom, List<BomEO> boms){
        if(parentBom != null){
            List<BomEO> bomsTemp = this.baseMapper.getListByParentBomId(parentBom.getBomId());
            if(bomsTemp!=null && bomsTemp.size()>0){
                for(int i=0; i<bomsTemp.size(); i++){
                    boms.add(bomsTemp.get(i));
                    boms = getSonBoms(bomsTemp.get(i), boms);
                }
            }
        }
        return boms;
    }

    // 通过Bom根节点id获取该父节点及其下所有的子节点Bom清单数据
    private List<BomEO> getAllBoms(Long bomId){
        List<BomEO> boms = new ArrayList<>();
        BomEO parentBom = this.getById(bomId);
        if(parentBom != null){
            boms.add(parentBom);
            boms = getSonBoms(parentBom, boms);
        }
        return boms;
    }

    // 通过Bom子节点id获取该子节点的父节点及该节点的下的所有祖辈节点Bom清单数据
    private List<BomEO> getParentBoms(BomEO sonBom, List<BomEO> boms){
        if(sonBom != null){
            BomEO bom = super.getById(sonBom.getParentBomId());
            if(bom != null){
                boms.add(bom);
                boms = getParentBoms(bom, boms);
            }
        }
        return boms;
    }

    @EnableBusinessLog(BusinessLogType.CREATE)
    @Transactional(rollbackFor = Exception.class)
    public boolean save(BomEO entity, Long userId) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "Bom的归属机构权限不存在该用户权限,请确认!");

        BomEO bom = this.getRootBom(entity);
        if(bom != null){
            throw new BusinessException("请勿添加相同物料!");
        }

        entity.setParentBomId(Long.valueOf(0));
        entity.setStatus(1);
        entity.setAmount(Double.valueOf(1));
        entity = this.setBomByMaterialId(entity, "save");

        return super.save(entity);
    }

    @EnableBusinessLog(BusinessLogType.CREATE)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSon(BomEO entity, Long userId) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "Bom的归属机构权限不存在该用户权限,请确认!");

        BomEO bom = super.getById(entity.getParentBomId());
        if(bom.getStatus().intValue() == 0) {
            throw new BusinessException("禁用Bom不能添加子Bom!");
        }

        if(bom != null){
            if(bom.getMaterialId().longValue() == entity.getMaterialId().longValue()){
                throw new BusinessException("物料自身不能作为自己的原材料!");
            }
        }

        // 查找传进的实体的perentBomId的所有后代节点
        List<BomEO> boms = this.baseMapper.getListByParentBomId(entity.getParentBomId());
        if(boms!=null && boms.size()>0){
            for(int i=0; i<boms.size(); i++){
                boms = getSonBoms(boms.get(i), boms);
            }
        }

        // 查找传进的实体的perentBomId的所有父代节点
        boms = getParentBoms(entity, boms);

        // 判断传进实体的后代节点及父代节点都没有选择相同的物料
        for(BomEO bomTemp : boms){
            if(bomTemp.getMaterialId().longValue() == entity.getMaterialId().longValue()){
                throw new BusinessException("请勿添加已添加过的物料!");
            }
        }

        entity.setOrgId(bom.getOrgId());
        entity.setStatus(1);
        entity = this.setBomByMaterialId(entity, "saveSon");
        return super.save(entity);
    }

    @Override
    public BomEO getById(Serializable id) throws BusinessException {
        BomEO bom = super.getById(id);
        if(bom!=null) {
            if(bom.getParentBomId() == 0) {
                String parentMaterialName = bom.getMaterialCode();
                if(parentMaterialName!=null && !parentMaterialName.trim().equals("")) {
                    if(null!=bom.getMaterialName() && !"".equals(bom.getMaterialName().trim())) {
                        parentMaterialName += (" / " + bom.getMaterialName());
                    }
                    if(bom.getInventoryCode()!=null && !bom.getInventoryCode().trim().equals("")) {
                        parentMaterialName += (" / " + bom.getInventoryCode());
                    }
                } else {
                    parentMaterialName = "名称未定义";
                }
                bom.setParentMaterialName(parentMaterialName);
            } else {
                BomEO parentBom = super.getById(bom.getParentBomId());
                if(parentBom!=null) {
                    String parentMaterialName = parentBom.getMaterialCode();
                    if(parentMaterialName!=null && !parentMaterialName.trim().equals("")) {
                        if(parentBom.getMaterialName()!=null && !parentBom.getMaterialName().trim().equals("")) {
                            parentMaterialName += (" / " + parentBom.getMaterialName());
                        }
                        if(parentBom.getInventoryCode()!=null && !parentBom.getInventoryCode().trim().equals("")) {
                            parentMaterialName += (" / " + parentBom.getInventoryCode());
                        }
                    } else {
                        parentMaterialName = "名称未定义";
                    }
                    bom.setParentMaterialName(parentMaterialName);
                } else {
                    bom.setParentMaterialName("名称未定义");
                }
            }
        }

        return bom;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(BomEO entity) throws BusinessException {
        BomEO bom = super.getById(entity.getParentBomId());
        if(bom != null){
            if(bom.getMaterialId().longValue() == entity.getMaterialId().longValue()){
                throw new BusinessException("物料自身不能作为自己的原材料!");
            }
        }

        return super.updateById(entity);
    }

    public List<BomEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }

    public List<BomEO> getTreeListByBomId(Long bomId) throws BusinessException {
        List<BomEO> boms = getAllBoms(bomId);
        return TreeUtils.build(boms, Long.valueOf(0));
    }

    public List<BomEO> listForTree(Map<String, Object> map) throws BusinessException {
        List<BomEO> boms = this.baseMapper.selectList(new QueryWrapper<>());
        return TreeUtils.build(boms, Long.valueOf(0));
    }


    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(BomEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "Bom的归属机构权限不存在该用户权限,请确认!");

        return this.baseMapper.updateStatusById(entity.getBomId(), entity.getStatus());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateReleaseStatus(BomEO entity, Long userId) {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "Bom的归属机构权限不存在该用户权限,请确认!");
//        List<BomEO> boms = this.getSonBoms(entity, new ArrayList<>());
//        if(boms!=null && boms.size()>0) {
//            for(BomEO bom : boms) {
//                bom.setReleaseStatus(entity.getReleaseStatus());
//                super.updateById(bom);
//            }
//        }
        return super.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(Long[] idList, Long userId) throws BusinessException {
        for(Serializable id: idList){
            List<BomEO> boms = getAllBoms((Long) id);
            for(BomEO bomTemp : boms) {
                Boolean flag = this.orgService.checkUserPermissions(bomTemp.getOrgId(), userId);
                if(flag.booleanValue()) {
                    super.removeById(bomTemp.getBomId());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(BomEO entity, Long userId) {
        BomEO bomFromDb = this.getById(entity.getBomId());
        if(entity.getMaterialId().longValue() != bomFromDb.getMaterialId().longValue()) {
            MaterialEO material = this.materialService.getById(entity.getMaterialId());
            entity.setElementNo(material.getElementNo());
        }
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "Bom的归属机构权限不存在该用户权限,请确认!");

        return super.updateById(entity);
    }

    public List<BomEO> getByElementNoAndOrg(String elementNo, Long orgId) throws BusinessException {
        return this.baseMapper.getByElementNoAndOrg(elementNo, orgId);
    }

    private List<BomEO> getLeaves(BomEO bom, List<BomEO> list, String productOrPurchase) {
        bom.setAmount(bom.getAmount()==null?1d:bom.getAmount());
        List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
        if(boms==null || boms.size()==0) {
            if(productOrPurchase == null) {
                list.add(bom);
            } else {
                if("product".equals(productOrPurchase)) {
                    if(bom.getIsProduct().intValue() == 1) {
                        list.add(bom);
                    }
                } else if("purchase".equals(productOrPurchase)) {
                    if(bom.getIsPurchase().intValue() == 1) {
                        list.add(bom);
                    }
                }
            }
        } else {
            for(int i=0; i<boms.size(); i++) {
                BomEO bomTemp = boms.get(i);
                bomTemp.setAmount(bomTemp.getAmount()==null?1d:bomTemp.getAmount());
                bomTemp.setAmount(bom.getAmount() * bomTemp.getAmount());
                getLeaves(bomTemp, list, productOrPurchase);
            }
        }
        return list;
    }

    public List<BomEO> getAllLeaves(BomEO bom, String productOrPurchase) throws BusinessException {
        List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
        List<BomEO> allLeaves = new ArrayList<>();
        if(boms!=null && boms.size()>0) {
            for(int i=0; i<boms.size(); i++) {
                getLeaves(boms.get(i), allLeaves, productOrPurchase);
            }
        }
        return allLeaves;
    }

    private List<BomEO> getNewLeaves(BomEO bom, List<BomEO> list, String productOrPurchase) {
        bom.setAmount(bom.getAmount()==null?1d:bom.getAmount());
        List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
        if(boms!=null && boms.size()>0) {
            for(int i=0; i<boms.size(); i++) {
                BomEO bomTemp = boms.get(i);
                bomTemp.setAmount(bomTemp.getAmount()==null?1d:bomTemp.getAmount());
                bomTemp.setAmount(bom.getAmount() * bomTemp.getAmount());
                if("product".equals(productOrPurchase)) {
                    if(bomTemp.getIsProduct().intValue() == 1) {
                        list.add(bomTemp);
                    }
                } else if("purchase".equals(productOrPurchase)) {
                    if(bomTemp.getIsPurchase().intValue() == 1) {
                        list.add(bomTemp);
                    }
                }
                getNewLeaves(bomTemp, list, productOrPurchase);
            }
        }
        return list;
    }

    public List<BomEO> getNewAllLeaves(BomEO bom, String productOrPurchase) throws BusinessException {
        List<BomEO> allNewLeaves = new ArrayList<>();
        bom.setAmount(bom.getAmount()==null?1d:bom.getAmount());
        if("product".equals(productOrPurchase)) {
            if(bom.getIsProduct().intValue() == 1) {
                allNewLeaves.add(bom);
            }
        } else if("purchase".equals(productOrPurchase)) {
            if(bom.getIsPurchase().intValue() == 1) {
                allNewLeaves.add(bom);
            }
        }
        List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
        if(boms!=null && boms.size()>0) {
            for(int i=0; i<boms.size(); i++) {
                getNewLeaves(boms.get(i), allNewLeaves, productOrPurchase);
            }
        }
        return allNewLeaves;
    }


    private List<BomEO> getLeavesForW(BomEO bom, List<BomEO> list, boolean isQueryMaterialRelationship) {
        bom.setAmount(bom.getAmount()==null?1d:bom.getAmount());
        // 物料零件号是带-W的,不继续往下找,去找-W的对应的黑件,忽略物料的委外属性
        if(bom.getElementNo().toUpperCase().endsWith("-W")) {
            // 判断Bom的领料规则,0不做处理,1领取本身
            if(bom.getPickRule().intValue() == 1) {
                list.add(bom);
            } else {
                MaterialEO material;

                if(isQueryMaterialRelationship) { // 查询物料关系中-W对应的黑件
                    material = this.materialService.getSonMaterial(bom.getMaterialId());
                } else { // 直接去掉-W查询物料表
                    material = this.materialService.getByElementNoAndOrgId(bom.getElementNo().substring(0, bom.getElementNo().length()-2), bom.getOrgId());
                }

                if(material != null) {
                    bom.setMaterialName(material.getMaterialName());
                    bom.setMaterialId(material.getMaterialId());
                    bom.setInventoryCode(material.getInventoryCode());
                    bom.setElementNo(material.getElementNo());
                    bom.setFigureVersion(material.getInventoryCode());
                    bom.setFigureNumber(material.getFigureNumber());
                    bom.setMainWarehouseId(material.getMainWarehouseId());
                    bom.setErpCode(material.getErpCode());
                    list.add(bom);
                }
            }
        } else {
            // 物料是委外属性的,不继续往下找,直接使用该物料
            if(bom.getIsOutside()!=null && bom.getIsOutside().intValue()==1) {
                list.add(bom);
            } else { // 物料不是委外属性的,也不是带-W零件号
                List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
                if(boms==null || boms.size()==0) { // bom无子节点,即该bom是叶子节点
                    list.add(bom);
                } else {
                    for(int i=0; i<boms.size(); i++) { // bom有子节点,即该bom不是叶子节点
                        BomEO bomTemp = boms.get(i);
                        bomTemp.setAmount(bomTemp.getAmount()==null?1d:bomTemp.getAmount());
                        bomTemp.setAmount(bom.getAmount() * bomTemp.getAmount());
                        getLeavesForW(bomTemp, list, isQueryMaterialRelationship);
                    }
                }
            }
        }

        return list;
    }

    public List<BomEO> getAllLeavesForW(BomEO bom) throws BusinessException {
        List<BomEO> allLeavesForW = new ArrayList<>();

        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
        JSONObject bomJsonObject = jsonObject.getJSONObject("bom");
        boolean isQueryMaterialRelationship = bomJsonObject.getBoolean("isQueryMaterialRelationship");

        List<BomEO> boms = this.baseMapper.getListByParentBomId(bom.getBomId());
        if(boms!=null && boms.size()>0) {
            for(int i=0; i<boms.size(); i++) {
                getLeavesForW(boms.get(i), allLeavesForW, isQueryMaterialRelationship);
            }
        }
        return allLeavesForW;
    }

    public List<BomEO> getAllBomsByBomIds(Long[] bomIds) throws BusinessException {
        if(bomIds==null || bomIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        List<BomEO> list = new ArrayList<>();
        for(Long bomId : bomIds) {
            List<BomEO> listTemp = getAllBoms(bomId);
            if(listTemp!=null && listTemp.size()>0) {
                list.addAll(listTemp);
            }
        }

        return list;
    }

    // 需手动设置父子关系方法(暂时弃用)
    public void importFromExcel(List list, Long orgId, String projectNo) {
        List<Map> mapList = (List<Map>) list.get(0);
        for (int i = 1; i < mapList.size(); i++) {
            Map map = mapList.get(i);
            QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
            wrapper.eq("element_no", ((String) map.get("1")).trim());
            wrapper.eq("org_id", orgId);
            wrapper.eq("status", 1);
            MaterialEO material = this.materialService.getOne(wrapper);
            BomEO bom = new BomEO();
            if(material != null) {
                bom.setMaterialId(material.getMaterialId());
                bom.setMaterialName(material.getMaterialName());
            } else {
                bom.setMaterialId(Long.valueOf(0));
            }
            bom.setParentBomId(Long.valueOf((String) map.get("0")));
            bom.setElementNo(((String) map.get("1")).trim());  // 零件号
            bom.setEbomElementNo(((String) map.get("2")).trim());   // EBOM零件号
            bom.setFigureNumber(((String) map.get("3")).trim());   // 图号
            bom.setFigureVersion(((String) map.get("4")).trim());   // 图纸版本
            String productOrPurchase = ((String) map.get("5")).trim(); // 制造/采购
            if(productOrPurchase.equals("M")) {
                bom.setIsProduct(1);
                bom.setIsPurchase(0);
            }
            if(productOrPurchase.equals("B")) {
                bom.setIsProduct(0);
                bom.setIsPurchase(1);
            }
            bom.setMaterialName(((String) map.get("6")).trim());   // 零件名称
            bom.setMaterialModel(((String) map.get("7")).trim()); // 图纸材料
            bom.setSubstituteMaterialModel(((String) map.get("8")).trim()); // 替代材料
            String weight = ((String) map.get("9")).trim();
            if(weight!=null && !"".equals(weight)) {
                bom.setWeight(Double.valueOf(weight));  // 重量(kg)
            } else {
                bom.setWeight(Double.valueOf(0));  // 重量(kg)
            }
            String amount = ((String) map.get("11")).trim();
            if(amount!=null && !"".equals(amount)) {
                bom.setAmount(Double.valueOf(amount));  // 数量
            } else {
                bom.setAmount(Double.valueOf(1));  // 数量
            }
            bom.setMemo(map.get("12")==null?null:((String) map.get("12")).trim()); // 备注
            bom.setOrgId(orgId);
            bom.setStatus(1);
            bom.setProjectNo(projectNo);
            super.save(bom);

        }
    }

    // 无需手动设置父子关系方法(启用)
    public void newImportFromExcel(List list, Long orgId, String projectNo) {
        List<Map> mapList = (List<Map>) list.get(0);
        Map relationMap = new HashedMap();
        for (int i = 1; i < mapList.size(); i++) {
            Map map = mapList.get(i);
            QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
            wrapper.eq("element_no", ((String) map.get("2")).trim());
            wrapper.eq("org_id", orgId);
            wrapper.eq("status", 1);
            MaterialEO material = this.materialService.getOne(wrapper);
            BomEO bom = new BomEO();
            if(material != null) {
                bom.setMaterialId(material.getMaterialId());
                bom.setMaterialName(material.getMaterialName());
            } else {
                bom.setMaterialId(Long.valueOf(0));
            }
            Long num = Long.valueOf((String) map.get("0")); // 序号
            Long preNum = (map.get("1")==null||((String)map.get("1")).trim().equals(""))?null:Long.valueOf((String) map.get("1")); // 上级ID
            if((preNum==null||preNum==Long.valueOf(0)) && num==Long.valueOf(1)) {
                bom.setParentBomId(Long.valueOf(0));
            } else {
                bom.setParentBomId((Long) relationMap.get(preNum+""));
            }
            bom.setElementNo(((String) map.get("2")).trim());  // 零件号
            bom.setEbomElementNo(((String) map.get("3")).trim());   // EBOM零件号
            bom.setFigureNumber(((String) map.get("4")).trim());   // 图号
            bom.setFigureVersion(((String) map.get("5")).trim());   // 图纸版本
            String productOrPurchase = ((String) map.get("6")).trim(); // 制造/采购
            if(productOrPurchase.equals("M")) {
                bom.setIsProduct(1);
                bom.setIsPurchase(0);
            }
            if(productOrPurchase.equals("B")) {
                bom.setIsProduct(0);
                bom.setIsPurchase(1);
            }
            bom.setMaterialName(((String) map.get("7")).trim());   // 零件名称
            bom.setMaterialModel(((String) map.get("8")).trim()); // 图纸材料
            bom.setSubstituteMaterialModel(((String) map.get("9")).trim()); // 替代材料
            String weight = ((String) map.get("10")).trim();
            if(weight!=null && !"".equals(weight) && !"N/A".equals(weight)) {
                bom.setWeight(Double.valueOf(weight));  // 重量(kg)
            } else {
                bom.setWeight(Double.valueOf(0));  // 重量(kg)
            }
            String amount = ((String) map.get("12")).trim();
            if(amount!=null && !"".equals(amount)) {
                bom.setAmount(Double.valueOf(amount));  // 数量
            } else {
                bom.setAmount(Double.valueOf(1));  // 数量
            }
            bom.setMemo(map.get("13")==null?null:((String) map.get("13")).trim()); // 备注
            bom.setOrgId(orgId);
            bom.setStatus(1);
            bom.setProjectNo(projectNo);
            super.save(bom);
            relationMap.put(num+"", bom.getBomId());
        }
    }

    // 无需手动设置父子关系方法且文件为PBOM格式只有1层Bom的父子关系(启用)
    public void importFromPbomExcel(List list, Long orgId) throws BusinessException {
        List<Map> mapList = (List<Map>) list.get(0);
        int column = mapList.get(0).size(); // 总列数
        List<BomEO> boms = new ArrayList<>();
        // 先获取当前机构所有物料信息
        List<MaterialEO> materials = this.materialService.selectAllMaterialByOrgId(orgId);
        Map<String,MaterialEO> materialsMap = new HashMap<>();
        // 把所有机构下的物料放进Map
        for(MaterialEO materialEO : materials){
            materialsMap.put(materialEO.getElementNo(),materialEO);
        }

        String errorMsg = "";
        for(int j=5; j<=column; j++) {
            // 总成Bom
            BomEO bom = new BomEO();
            Long bomId = this.businessCodeGenerator.getNextval("bsc_bom");
            bom.setBomId(bomId);
            Map inventoryCodeMap = mapList.get(3); // 存货编码
            if(inventoryCodeMap!=null && inventoryCodeMap.size()>0) {
                bom.setInventoryCode((String) inventoryCodeMap.get(j+""));
            } else {
                errorMsg += ("总成Bom: 第" + j + "列的存货编码为空!\n");
            }
            Map elementNoMap = mapList.get(4); // 零件号
            if(elementNoMap!=null && elementNoMap.size()>0) {
                bom.setElementNo((String) elementNoMap.get(j+""));
            } else {
                errorMsg += ("总成Bom: 第" + j + "列的零件号为空!\n");
            }
            Map materialNameMap = mapList.get(5);// 物料名称
            if(materialNameMap!=null && materialNameMap.size()>0) {
                bom.setMaterialName((String) materialNameMap.get(j+""));
            } else {
                errorMsg += ("总成Bom: 第" + j + "列的零件名称为空!\n");
            }
            bom.setAmount(Double.valueOf(1));
            bom.setParentBomId(Long.valueOf(0));
            bom.setOrgId(orgId);
            bom.setStatus(1);
            MaterialEO material = materialsMap.get(elementNoMap.get(j+""));
            if(material != null) {
                bom.setMaterialId(material.getMaterialId());
                bom.setIsPurchase(material.getIsPurchase()!=null?0:material.getIsPurchase());
                bom.setIsProduct(material.getIsProduct()!=null?0:material.getIsProduct());
            } else {
                bom.setMaterialId(Long.valueOf(0));
                bom.setIsPurchase(0);
                bom.setIsProduct(0);
            }

            for(int i=6; i<mapList.size(); i++) {
                Map map = mapList.get(i);
                if(map.get("1")==null || map.get("1").toString().trim().equals("")) {
                    errorMsg += ("分成Bom: 第" + i + "行的项目号为空!\n");
                }
                if(map.get("3")==null || map.get("3").toString().trim().equals("")) {
                    errorMsg += ("分成Bom: 第" + i + "行的零件号为空!\n");
                } else {
                    String elementNo = (String) map.get("3");
                    if (elementNoMap.get(j + "")!=null && elementNoMap.get(j + "").equals(elementNo)) {
                        bom.setProjectNo((String) mapList.get(i).get("1")); // 项目号
                        break;
                    }
                }
            }
            boms.add(bom);

            // 总成Bom下的子Bom
            for(int i=6; i<mapList.size(); i++) {
                Map map = mapList.get(i);
                String amount = (String) map.get(j+"");
                String elementNo = null;
                if(map.get("3")!=null && !map.get("3").toString().trim().equals("")) {
                    elementNo = (String) map.get("3");
                }
                if(amount!=null && !amount.trim().equals("") && Double.valueOf(amount)>0) {
                    BomEO sonBom = new BomEO();
                    Long sonBomId = this.businessCodeGenerator.getNextval("bsc_bom");
                    sonBom.setBomId(sonBomId);
                    sonBom.setInventoryCode((String) map.get("2"));
                    sonBom.setElementNo(elementNo);
                    sonBom.setMaterialName((String) map.get("4"));
                    sonBom.setProjectNo((String) map.get("1"));
                    sonBom.setParentBomId(bomId);
                    sonBom.setOrgId(orgId);
                    sonBom.setStatus(1);
                    sonBom.setAmount(Double.valueOf(amount));
                    MaterialEO sonMaterial = materialsMap.get(elementNo);
                    if(sonMaterial != null) {
                        sonBom.setMaterialId(sonMaterial.getMaterialId());
                        sonBom.setIsPurchase(sonMaterial.getIsPurchase()!=null?0:sonMaterial.getIsPurchase());
                        sonBom.setIsProduct(sonMaterial.getIsProduct()!=null?0:sonMaterial.getIsProduct());
                    } else {
                        sonBom.setMaterialId(Long.valueOf(0));
                        sonBom.setIsProduct(0);
                        sonBom.setIsPurchase(0);
                    }
                    boms.add(sonBom);
                }
            }
        }

        this.baseMapper.addBatch(boms);
    }

    public List<BomEO> getByMaterialId(Long materialId) {
        return this.baseMapper.getByMaterialId(materialId);
    }

    private Map getBomsForReleaseWeekPlan(BomEO bom, boolean isQueryMaterialRelationship, Map map) {
        List<BomEO> purchaseBoms = (List<BomEO>) map.get("purchaseBoms"); // 需产生采购订单的Bom
        List<BomEO> outsideBoms = (List<BomEO>) map.get("outsideBoms");  // 需产生委外订单的Bom
        List<BomEO> productBoms = (List<BomEO>) map.get("productBoms");  // 需产生生产订单的Bom
        String errorMsg = (String) map.get("errorMsg");
        String resultMsg = (String) map.get("resultMsg");

        // 是否采购，是否制造值为空时默认为否
        if(bom.getIsPurchase() == null) {
            bom.setIsPurchase(0);
        }
        if(bom.getIsProduct() == null) {
            bom.setIsProduct(0);
        }
        // 数量为空时默认为1
        bom.setAmount(bom.getAmount()==null?1d:bom.getAmount());

        if(bom.getSupplierId() == null) {
            if(bom.getIsPurchase()!=null && bom.getIsPurchase()==1) {
                errorMsg += ("物料[" + bom.getMaterialName() + " " + bom.getElementNo() + "]未设置默认供应商!<br/>");
                map.put("errorMsg", errorMsg);
            }
        }

        if(bom.getIsPurchase() == 0) {
            if(bom.getIsProduct() == 1) { // 非采购，是制造
                // 生产订单Bom
                productBoms.add(bom);
                map.put("productBoms", productBoms);
            }
            List<BomEO> list = this.baseMapper.getListByParentBomId(bom.getBomId());
            if(list!=null && list.size()>0) {
                for(BomEO bomItem : list) {
                    bomItem.setAmount(bomItem.getAmount()==null?1d:bomItem.getAmount());
                    bomItem.setAmount(bom.getAmount() * bomItem.getAmount());
                    getBomsForReleaseWeekPlan(bomItem, isQueryMaterialRelationship, map);
                }
            }
        } else if(bom.getIsPurchase() == 1) {
            if(bom.getIsProduct() == 0) { // 是采购，非制造
                List<BomEO> list = this.baseMapper.getListByParentBomId(bom.getBomId()); // 判断该采购节点是否为叶子节点
                if(list==null || list.size()==0) { // 如果节点时叶子节点,则直接创建采购订单
                    // 采购订单Bom
                    purchaseBoms.add(bom);
                    map.put("purchaseBoms", purchaseBoms);
                } else { // 如果遇到的是非叶子节点,继续向下分解
                    for(BomEO bomItem : list) {
                        bomItem.setAmount(bomItem.getAmount()==null?1d:bomItem.getAmount());
                        bomItem.setAmount(bom.getAmount() * bomItem.getAmount());
                        getBomsForReleaseWeekPlan(bomItem, isQueryMaterialRelationship, map);
                    }
                }
            } else if(bom.getIsProduct() == 1) { // 是采购，是制造
                errorMsg += ("物料["  + bom.getMaterialName() + " " + bom.getElementNo() + "]的是否制造与是否采购值都为是!<br/>");
                map.put("errorMsg", errorMsg);
            }
        }

        // 委外订单Bom(非采购，是制造)
        if((bom.getIsProduct()==1 && bom.getIsPurchase()==0)) {
//            if(bom.getElementNo().toUpperCase().endsWith("-W")) {
                MaterialEO material;

                if(isQueryMaterialRelationship) { // 查询物料关系中对应的黑件(支持多次委外)
                    material = this.materialService.getSonMaterialForW(bom.getMaterialId(), new MaterialEO());
                } else { // 直接去掉-W(或者-A)查询物料表
                    material = this.materialService.getByElementNoAndOrgId(bom.getElementNo().substring(0, bom.getElementNo().length()-2), bom.getOrgId());
                }

                if(material != null) {
                    BomEO bomTemp = new BomEO();
                    bomTemp.setSupplierId(bom.getSupplierId());
                    bomTemp.setMaterialId(material.getMaterialId());
                    bomTemp.setMaterialName(material.getMaterialName());
                    bomTemp.setSpecification(bom.getSpecification());
                    bomTemp.setMaterialCode(bom.getMaterialCode());
                    bomTemp.setInventoryCode(material.getInventoryCode());
                    bomTemp.setElementNo(material.getElementNo());
                    bomTemp.setUnitName(bom.getUnitName());
                    bomTemp.setAmount(bom.getAmount());
                    outsideBoms.add(bomTemp);
                    map.put("outsideBoms", outsideBoms);
                } else {
//                    resultMsg += ("物料[" + bom.getMaterialName() + " " + bom.getElementNo() + "]对应的关系物料不存在!<br/>");
//                    map.put("resultMsg", resultMsg);
                }
//            }
        }

        return map;
    }

    public Map getAllBomsForReleaseWeekPlan(BomEO bom, boolean isQueryMaterialRelationship) throws BusinessException {
        List<BomEO> purchaseBoms = new ArrayList<>(); // 需产生采购订单的Bom
        List<BomEO> outsideBoms = new ArrayList<>();  // 需产生委外订单的Bom
        List<BomEO> productBoms = new ArrayList<>();  // 需产生生产订单的Bom
        String errorMsg = "";
        String resultMsg = "";

        Map map = new HashedMap();
        map.put("purchaseBoms", purchaseBoms);
        map.put("outsideBoms", outsideBoms);
        map.put("productBoms", productBoms);
        map.put("errorMsg", errorMsg);
        map.put("resultMsg", resultMsg);

        map = getBomsForReleaseWeekPlan(bom, isQueryMaterialRelationship, map);

        return map;
    }

    public List<BomEO> getBomsByMaterialId(Long materialId) throws BusinessException {
        BomEO bom = this.baseMapper.getParentByMaterialId(materialId);
        if(bom == null) {
            throw new BusinessException("总成不存在!");
        } else {
            return this.getAllBoms(bom.getBomId());
        }
    }
}
