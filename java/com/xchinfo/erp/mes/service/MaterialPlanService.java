package com.xchinfo.erp.mes.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.service.BomService;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.entity.MaterialRequirementEO;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.mes.mapper.MaterialPlanMapper;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.srm.entity.WeekProductOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.service.ProductOrderService;
import com.xchinfo.erp.srm.service.PurchaseOrderTempService;
import com.xchinfo.erp.srm.service.WeekProductOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;
import org.yecat.mybatis.utils.LogUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialPlanService extends BaseServiceImpl<MaterialPlanMapper, MaterialPlanEO>   {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private OrgService orgService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private DubDeliveryPlanService dubDeliveryPlanService;

    @Autowired
    private MaterialRequirementService materialRequirementService;

    @Autowired
    private MaterialDistributeService materialDistributeService;

    @Autowired
    private BomService bomService;

    @Autowired
    private StampingMaterialConsumptionQuotaService stampingMaterialConsumptionQuotaService;

    @Autowired
    private PurchaseOrderTempService purchaseOrderService;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private WeekProductOrderService weekproductOrderService;

    @Transactional(rollbackFor = Exception.class)
    public String updateStatusById(Long[] ids, Integer status,String action) throws BusinessException{

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();


        //状态已被更新的记录
        String oneMsg = "";

        //成功的信息
        String successMsg = "";

        //失败的信息
        String failMsg = "";

        for(Long id:ids){
            //判断是否存在不是相应状态的数据,符合状态的数据做更新
            MaterialPlanEO entity = this.getById(id);

            //校验机构权限(权限不足，则是因为机构权限被收回，操作后会刷新数据，机构权限不存在的数据将看不到了)
            if(!checkPer(entity.getOrgId(),userId)){
                continue;
            }

            //发布
            if ("release".equals(action) && entity.getStatus() == 0 ){

//                //月计划生产
//                if(entity.getRequireProductCount() > 0 && entity.getPlanType() == 1) {
//                    //推送到物料月需求表
//                    MaterialRequirementEO saveEntity = new MaterialRequirementEO();
//                    transForm(entity, saveEntity);
//                    this.materialRequirementService.save(saveEntity);
//
//                    //推送到生产计划中
//                    MaterialDistributeEO productEntity = new MaterialDistributeEO();
//                    transFormChild(entity,productEntity,3);
//                    this.materialDistributeService.save(productEntity);
//                }
//
//                //月计划采购和委外
//                if(entity.getRequirePurchaseCount() > 0 && entity.getPlanType() == 1){
//                    //推送到采购计划中
//                    MaterialDistributeEO purchaseEntity = new MaterialDistributeEO();
//                    transFormChild(entity,purchaseEntity,1);
//                    this.materialDistributeService.save(purchaseEntity);
//
//                    //推送到委外计划中
//                    MaterialDistributeEO outEntity = new MaterialDistributeEO();
//                    transFormChild(entity,outEntity,2);
//                    this.materialDistributeService.save(outEntity);
//                }else if(entity.getRequireCount() > 0 && entity.getPlanType() == 2){
                //周计划发布
                //先查询月计划
//                    MaterialPlanEO monthPlan = this.baseMapper.selectExistEntity(entity);
//                    //查询订单是否存在
//                    List<PurchaseOrderEO> purchaseOrderEOList =  this.baseMapper.selectPurchaseOrderList(monthPlan.getSerialId());
//                    if(null == purchaseOrderEOList || purchaseOrderEOList.size() == 0){
//                        //发布信息失败
//                        failMsg = failMsg + "计划"+entity.getSerialCode()+" 不存在采购/委外订单数据，发布失败!<br/>";
//                        continue;
//                    }else{
//
//                        for(PurchaseOrderEO orderEO:purchaseOrderEOList){
//
//                            //校验送货计划是否存在
//                            Integer deliveryPlanCount = this.baseMapper.selectDeliveryPlanCount(orderEO.getPurchaseOrderId(),orderEO.getType(),entity.getWeekDate());
//                            if(null != deliveryPlanCount && deliveryPlanCount > 0){
//                                //发布信息失败
//                            }else{
//                                DeliveryPlanEO deliveryPlan = new DeliveryPlanEO();
//                                deliveryPlan.setPlanWeekId(entity.getSerialId());
//                                deliveryPlan.setPurchaseOrderId(orderEO.getPurchaseOrderId());
//                                deliveryPlan.setSupplierId(orderEO.getSupplierId());
//                                deliveryPlan.setPlanDeliveryQuantity(entity.getRequirePurchaseCount());
//                                deliveryPlan.setPlanDeliveryDate(entity.getWeekDate());
//                                deliveryPlan.setStatus(0);
//                                deliveryPlan.setType(orderEO.getType());
//                                deliveryPlan.setCreateUserId(userId);
//                                deliveryPlan.setCreateUserName(userName);
//                                deliveryPlan.setChargeUserId(userId);
//                                deliveryPlan.setChargeUserName(userName);
//                                deliveryPlan.setOrgId(orderEO.getOrgId());
//                                deliveryPlan.setActualDeliveryQuantity(Double.valueOf(0));
//                                deliveryPlan.setActualReceiveQuantity(Double.valueOf(0));
//                                deliveryPlan.setQualifiedQuantity(Double.valueOf(0));
//                                deliveryPlan.setNotQualifiedQuantity(Double.valueOf(0));
//                                deliveryPlan.setReturnedQuantity(Double.valueOf(0));
//                                deliveryPlan.setIntransitQuantity(Double.valueOf(0));
//                                deliveryPlan.setCreatedTime(new Date());
//                                String  voucherNo = this.generateNextCode("srm_delivery_plan",deliveryPlan);
//                                deliveryPlan.setVoucherNo(voucherNo);
//
//                                this.dubDeliveryPlanService.save(deliveryPlan);
//                            }
//
//
//                        }
//
//                    }

//                }
                //若是发布，则需要推送到相应的计划中新增计划明细
                this.baseMapper.updateStatusById(id,status);

                //发布信息
                if("".equals(successMsg)){
                    successMsg = "计划"+entity.getSerialCode()+" 发布成功!<br/>";
                }else{
                    successMsg = successMsg + "计划"+entity.getSerialCode()+" 发布成功!<br/>";
                }

                //取消发布
            }else if ("cancelRelease".equals(action) &&  entity.getStatus() == 1){

                if(entity.getPlanType() == 1) {
                    //先判断子计划是否存在
                    Integer ppoCount = this.baseMapper.selectCountPPOPlan(id);

                    //存在则不允许取消
                    if (ppoCount == 0) {
                        //取消发布，则需要推送去相应的计划中删除计划明细
                        this.baseMapper.updateStatusById(id, status);
                        //若是月计划，则需要删除相应子计划的数据(月需求和生产计划)
                        if (entity.getRequireProductCount() > 0 && entity.getPlanType() == 1) {
                            this.materialRequirementService.deleteBySerialId(id);
                            this.materialDistributeService.deleteBySerialId(id);
                        }
                        //若是月计划，则需要删除相应子计划的数据(采购计划和委外计划)
                        if (entity.getRequirePurchaseCount() > 0 && entity.getPlanType() == 1) {
                            this.materialDistributeService.deleteBySerialId(id);
                        }

                        //取消发布信息
                        if ("".equals(successMsg)) {
                            successMsg = "计划" + entity.getSerialCode() + " 取消发布成功!<br/>";
                        } else {
                            successMsg = successMsg + "计划" + entity.getSerialCode() + " 取消发布成功!<br/>";
                        }
                    } else {
                        //取消发布信息失败
                        if ("".equals(failMsg)) {
                            failMsg = "计划" + entity.getSerialCode() + " 存在子计划不是新建状态，取消发布失败!<br/>";
                        } else {
                            failMsg = failMsg + "计划" + entity.getSerialCode() + " 存在子计划不是新建状态，取消发布失败!<br/>";
                        }

                    }
                }else{
                    //周计划取消发布
                    //先查询月计划
//                    MaterialPlanEO monthPlan = this.baseMapper.selectExistEntity(entity);
//                    //采购和委外才需要验证
//                    if(entity.getRequirePurchaseCount() > 0) {
//                        //判断送货计划是否新建状态
//                        List<DeliveryPlanEO> tempPlanEOS = this.baseMapper.selectExistCountOrderPlan(entity.getSerialId());
//                        if (null != tempPlanEOS && tempPlanEOS.size() > 0) {
//                            failMsg = failMsg +"计划" + entity.getSerialCode() + " 存在送货计划不是新建状态，取消发布失败!<br/>";
//                            continue;
//                        }
//
//
//                        //根据周计划ID删除送货计划
//                        this.baseMapper.deleteDeliveryPlanById(entity.getSerialId());
//
                    successMsg = successMsg + "计划" + entity.getSerialCode() + " 取消发布成功!<br/>";
//                    }

                    this.baseMapper.updateStatusById(id, status);

                }

                //关闭
            }else if("close".equals(action) && entity.getStatus() != 0){


                //先判断子计划是否存在，不存在则可以重新
//                Integer ppoCount = this.baseMapper.selectCountPPOPlan(id);
//                if(ppoCount == 0) {
                //先保存关闭之前的状态
                this.baseMapper.updatePreStatusByStatus(id);
                //先更新月状态
                this.baseMapper.updateStatusById(id, status);

                if("".equals(successMsg)){
                    successMsg = "计划"+entity.getSerialCode()+" 关闭成功!<br/>";
                }else{
                    successMsg = successMsg + "计划"+entity.getSerialCode()+" 关闭成功!<br/>";
                }

//                    //若是月计划，则需要删除相应子计划的数据(采购计划和委外计划)
//                    if (entity.getRequirePurchaseCount() > 0 && entity.getPlanType() == 1) {
//                        this.materialDistributeService.deleteBySerialId(id);
//                    }
//
//                    //若是月计划，则需要删除相应子计划的数据(月需求和生产计划)
//                    if (entity.getRequireProductCount() > 0 && entity.getPlanType() == 1) {
//                        this.materialRequirementService.deleteBySerialId(id);
//                        this.materialDistributeService.deleteBySerialId(id);
//                    }
//                }
                //打开
            }else if("open".equals(action) &&  entity.getStatus() == 9){

                this.baseMapper.updateStatusByPreStatus(id);

                if("".equals(successMsg)){
                    successMsg = "计划"+entity.getSerialCode()+" 打开成功!<br/>";
                }else{
                    successMsg = successMsg + "计划"+entity.getSerialCode()+" 打开成功!<br/>";
                }
                //完成
            }else if("finish".equals(action)){

                this.baseMapper.updateStatusById(id,status);
            }else{

                if("".equals(oneMsg)){
                    oneMsg = "计划"+entity.getSerialCode()+" 已不是最新状态，操作失败!<br/>";
                }else{
                    oneMsg = oneMsg + "计划"+entity.getSerialCode()+" 已不是最新状态，操作失败!<br/>";
                }

            }


        }

        String msg = "";
        if(!"".equals(successMsg)){
            msg = msg + successMsg;
        }
        if(!"".equals(failMsg)){
            msg = msg + failMsg;
        }
        if(!"".equals(oneMsg)){
            msg = msg + oneMsg;
        }

        return msg;
    }

    //设置参数
    public void transForm(MaterialPlanEO entity,MaterialRequirementEO saveEntity){

        //设置参数
        saveEntity.setSerialId(entity.getSerialId());
        saveEntity.setSerialCode("0");
        saveEntity.setCustomerId(entity.getCustomerId());
        saveEntity.setCustomerCode(entity.getCustomerCode());
        saveEntity.setCustomerName(entity.getCustomerName());
        saveEntity.setMonthDate(entity.getMonthDate());
        saveEntity.setPlanType(1);
        saveEntity.setOrgId(entity.getOrgId());
        saveEntity.setMaterialId(entity.getMaterialId());
        saveEntity.setMaterialCode(entity.getMaterialCode());
        saveEntity.setMaterialName(entity.getMaterialName());
        saveEntity.setElementNo(entity.getElementNo());
        saveEntity.setInventoryCode(entity.getInventoryCode());
        saveEntity.setSpecification(entity.getSpecification());
        saveEntity.setMaterialModel(entity.getMaterialModel());
        saveEntity.setMaterialModelSpecific(entity.getMaterialModelSpecific());
        if(null != entity.getWeight() && !"".equals(entity.getWeight())){
            saveEntity.setWeight(entity.getWeight());
            saveEntity.setTotalWeight(entity.getWeight()*entity.getRequireProductCount());
        }
        saveEntity.setRequireCount(entity.getRequireProductCount());
        saveEntity.setStatus(0);

    }


    public void transFormChild(MaterialPlanEO entity,MaterialDistributeEO saveEntity,Integer type){
        //设置参数
        if(type == 1){
            saveEntity.setSerialPurchaseCode("0");
            saveEntity.setRequireCount(entity.getRequirePurchaseCount());
        }else if(type == 2){
            saveEntity.setSerialOutSourceCode("0");
            saveEntity.setRequireCount(entity.getRequirePurchaseCount());
        }else{
            saveEntity.setSerialProductCode("0");
            saveEntity.setRequireCount(entity.getRequireProductCount());
        }
        saveEntity.setSerialId(entity.getSerialId());
        saveEntity.setCustomerId(entity.getCustomerId());
        saveEntity.setCustomerCode(entity.getCustomerCode());
        saveEntity.setCustomerName(entity.getCustomerName());
        saveEntity.setMonthDate(entity.getMonthDate());
        saveEntity.setPlanType(entity.getPlanType());
        saveEntity.setDistributeType(type);
        saveEntity.setWeekDate(entity.getWeekDate());
        saveEntity.setOrgId(entity.getOrgId());
        saveEntity.setMaterialId(entity.getMaterialId());
        saveEntity.setMaterialCode(entity.getMaterialCode());
        saveEntity.setMaterialName(entity.getMaterialName());
        //供应商
        if(type != 3) {
            saveEntity.setSupplierId(entity.getSupplierId());
            saveEntity.setSupplierCode(entity.getSupplierCode());
            saveEntity.setSupplierName(entity.getSupplierName());
        }
        saveEntity.setElementNo(entity.getElementNo());
        saveEntity.setInventoryCode(entity.getInventoryCode());
        saveEntity.setSpecification(entity.getSpecification());
        saveEntity.setPlanVersion(1d);
        saveEntity.setStatus(0);

    }



    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialPlanEO.class)
    public MaterialPlanEO saveEntity(MaterialPlanEO entity,Long userId) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

        //校验机构权限
//        if(!checkPer(entity.getOrgId(),userId)){
//            entity.setSaveMsg("物料计划的归属机构权限不存在该用户权限，操作失败！");
//            return entity;
//        }

        MaterialEO materialEO = this.baseMapper.getByMaterialId(entity.getMaterialId());
        if(!entity.getOrgId().equals(materialEO.getOrgId())){
            entity.setSaveMsg("物料计划归属机构和物料的归属机构不一致，操作失败！");
            return entity;
        }

        Integer count = 0;

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("cmp_material_plan", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        entity.setSerialCode(code);

        if(entity.getPlanType() == 1){
            //判断同一个客户，同一物料，当前月份，是否存在，存在则报错,非当前月份，存在则实时更新
            count = this.baseMapper.selectExistCount(entity);
            if(entity.getIsCurrentMonth().intValue() == 1) { // 当前月份数据
                if(count > 0){
                    entity.setSaveMsg("新增失败，物料计划已存在");
                    return entity;
                }
                //保存月计划
                super.save(entity);
                entity.setSaveMsg("新增成功");
            } else { // 非当前月份数据
                if(count > 0){ // 存在则更新
                    entity = this.baseMapper.selectExistMaterialPlan(entity);
                    super.updateById(entity);
                    entity.setSaveMsg("更新成功");
                } else { // 不存在则添加
                    //保存月计划
                    super.save(entity);
                    entity.setSaveMsg("新增成功");
                }
            }
        }else if (entity.getPlanType() == 2){
            //判断同一个客户，同一物料，同一日，是否存在，存在则更新版本
            MaterialPlanEO materialPlanEO = this.baseMapper.selectExistWeekCount(entity);
            if(null != materialPlanEO){
                materialPlanEO.setRequireCount(entity.getRequireCount());
                materialPlanEO.setRequireProductCount(entity.getRequireProductCount());
                materialPlanEO.setRequirePurchaseCount(entity.getRequirePurchaseCount());
                Double pVersion = Double.valueOf(materialPlanEO.getPlanVersion()) + 0.1;
                materialPlanEO.setPlanVersion(pVersion);
                //保存周计划
                super.updateById(materialPlanEO);
                entity.setSaveMsg("更新成功");
            }else{
                //保存周计划
                super.save(entity);
                entity.setSaveMsg("新增成功");
            }

        }else{
            throw new BusinessException("计划类型不对");
        }



        return entity;
    }


    public List<MaterialCustomerEO> importFromExcel(List list,String title, Date monthDate, String customerName, Long customerId,UserEO userEO) throws BusinessException {
        List<MaterialCustomerEO> planList = new ArrayList<>();
        Integer monthCount = 0;
        logger.info("======== ========"+title);

        List<StockAccountEO> allStock = this.baseMapper.selectAllStockByOrgId(userEO.getOrgId());
        Map<Long,Double> stockMap = new HashMap<>();
        for (StockAccountEO stockAccountEO:allStock){
            if (stockAccountEO.getCount() == null){
                stockAccountEO.setCount(0d);
            }

            stockMap.put(stockAccountEO.getMaterialId(),stockAccountEO.getCount());
        }

        if(list!=null){
            if(list.size()>0){
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                if(mapList!=null && mapList.size()>1){
                    for(int i=0;i < mapList.size();i++){
                        Map  map = mapList.get(i);

                        //标题匹配
                        if (i == 0){
                            Integer size = map.size();
                            logger.info("========长度 ========"+size);
                            for(int j=6;j<size;j++){
                                String time = (String) map.get(j+"");
                                logger.info("======== ========"+time.trim().contains(title));
                                if(time.trim().contains(title)){
                                    monthCount = j;
                                    break;
                                }
                            }

                            if (monthCount == 0){
                                throw new BusinessException("请确认文件标题头的年月是否存在选择的年月！");
                            }
                        } else {
                            // 从选中的月份开始,数据导入滚动3个月,选择月份当月锁定,后两月预测数量,实时修改,数据覆盖
                            for(int p=monthCount; p<monthCount+3; p++) {
                                //计划数量
                                String planCount = (String) map.get(""+p);
                                boolean flag = false;
                                if(planCount==null || "".equals(planCount.trim())) {
                                    flag = true;
                                }

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(monthDate);
                                calendar.add(Calendar.MONTH, +(p-monthCount));

                                //初始参数校验
                                //零件号
                                String elementNo = (String) map.get("1");

                                //客户名称
                                String name  = (String) map.get("2");
                                if (null == name || name.isEmpty()){
                                    continue;
                                }


                                MaterialCustomerEO materialCustomerEO;
                                if(null==elementNo || elementNo.isEmpty()){
                                    materialCustomerEO =  new MaterialCustomerEO();
                                    materialCustomerEO.setSaveMsg("零件号为空!");
                                    materialCustomerEO.setMonthDate(calendar.getTime());
                                    materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
                                    planList.add(materialCustomerEO);
                                    continue;
                                } else {
                                    //查询零件号和客户的关系是否存在，以及物流计划是否存在（还需在sql中获取库存和在途数量）
                                    materialCustomerEO =  this.baseMapper.selectCustomerMaterialPlan(name,elementNo,userEO.getOrgId());
                                    if(null == materialCustomerEO){
                                        logger.info("========物料客户关系不存在========"+elementNo);
                                        materialCustomerEO =  new MaterialCustomerEO();
                                        materialCustomerEO.setSaveMsg("物料客户关系不存在,请确认!");
                                        materialCustomerEO.setErrImportType(1);
                                        materialCustomerEO.setCustomerName(name);
                                        materialCustomerEO.setElementNo(elementNo);
                                        materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
                                        materialCustomerEO.setMonthDate(calendar.getTime());
                                        planList.add(materialCustomerEO);
                                        continue;
                                    }
                                }

                                if (null ==  stockMap.get(materialCustomerEO.getMaterialId())){
                                    materialCustomerEO.setStockCount(0d);
                                }else{
                                    materialCustomerEO.setStockCount(stockMap.get(materialCustomerEO.getMaterialId()));
                                }

                                String stock = String.valueOf(materialCustomerEO.getStockCount());
//                                String tranCount =  String.valueOf(materialCustomerEO.getInTransitCount());
                                if(null==stock || "null".equals(stock) || stock.isEmpty()){
                                    stock = "0";
                                }
                                //需求数量
                                Double productCount = Double.valueOf(flag?0d:Double.valueOf(planCount))-Double.valueOf(stock)+Double.valueOf(materialCustomerEO.getMinStock()==null?0d:materialCustomerEO.getMinStock());
//                                if (productCount > 0){
//                                    materialCustomerEO.setRequireProductCount(productCount);
//                                }else{
//                                    materialCustomerEO.setRequireProductCount(0d);
//                                }



//                                if(!name.equals(customerName)){
//                                    logger.info("========客户不匹配========"+elementNo);
//                                    materialCustomerEO.setSaveMsg("客户不匹配!");
//                                    materialCustomerEO.setMonthDate(calendar.getTime());
//                                    materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
//                                    materialCustomerEO.setRequireProductCount(productCount>0?productCount:0d);
//                                    planList.add(materialCustomerEO);
//                                    continue;
//                                }


                                if(null == planCount || planCount.isEmpty() || "0".equals(planCount)){
                                    logger.info("========计划数量值不存在========"+elementNo);
                                    materialCustomerEO.setSaveMsg("计划数量值不存在!");
                                    materialCustomerEO.setMonthDate(calendar.getTime());
                                    materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
                                    materialCustomerEO.setRequireProductCount(productCount>0?productCount:0d);
                                    planList.add(materialCustomerEO);
                                    continue;
                                }


                                //不允许采购，不允许生产
//                                if(materialCustomerEO.getIsProduct() != 1 && materialCustomerEO.getIsPurchase()!=1){
//                                    logger.info("========匹配客户但不允许采购和生产========"+elementNo);
//                                    materialCustomerEO.setErrImportType(1);
//                                    materialCustomerEO.setSaveMsg("匹配客户但不允许采购和生产!");
//                                    materialCustomerEO.setMonthDate(calendar.getTime());
//                                    planList.add(materialCustomerEO);
//                                    continue;
//                                }


                                MaterialPlanEO materialPlanEO = new MaterialPlanEO();
                                materialPlanEO.setCustomerId(materialCustomerEO.getCustomerId());
                                materialPlanEO.setMonthDate(calendar.getTime());
                                materialPlanEO.setMaterialId(materialCustomerEO.getMaterialId());
                                Integer existCount = this.baseMapper.selectExistCount(materialPlanEO);
                                if(existCount > 0){
                                    logger.info("========物料计划已存在========"+elementNo);
                                    materialCustomerEO.setErrImportType(2);
                                    materialCustomerEO.setSaveMsg("物料计划已存在!");
                                    materialCustomerEO.setMonthDate(calendar.getTime());
                                    materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
                                    materialCustomerEO.setRequireProductCount(productCount>0?productCount:0d);
                                    planList.add(materialCustomerEO);
                                    continue;
                                }

                                //设置年份
                                int year = calendar.get(Calendar.YEAR);
                                materialCustomerEO.setYearSum(year);

                                //查询
                                materialCustomerEO.setRequireCount(flag?0d:Double.valueOf(planCount));
                                materialCustomerEO.setRequireProductCount(productCount>0?productCount:0d);

//                                if(null==tranCount || "null".equals(tranCount) || tranCount.isEmpty()){
//                                    tranCount = "0";
//                                }

//                                logger.info("========stock========"+stock+"=======tranCount===="+tranCount);


//                                if(materialCustomerEO.getIsProduct() == 1){
//                                    //生产数量
//                                    Double productCount = Double.valueOf(planCount)-Double.valueOf(stock)-Double.valueOf(tranCount);
//                                    if (productCount > 0){
//                                        materialCustomerEO.setRequireProductCount(productCount);
//                                    }else{
//                                        materialCustomerEO.setRequireProductCount(0d);
//                                    }
//                                    materialCustomerEO.setRequirePurchaseCount(0d);
//
//                                }

//                                if(materialCustomerEO.getIsProduct() != 1 && materialCustomerEO.getIsPurchase()==1){
//                                    //采购数量
//                                    Double purCount = Double.valueOf(planCount)-Double.valueOf(stock)-Double.valueOf(tranCount);
//                                    if(purCount > 0){
//                                        materialCustomerEO.setRequirePurchaseCount(purCount);
//                                    }else{
//                                        materialCustomerEO.setRequirePurchaseCount(0d);
//                                    }
//                                    materialCustomerEO.setRequireProductCount(0d);
//                                }

                                materialCustomerEO.setErrImportType(0);
                                materialCustomerEO.setMonthDate(calendar.getTime());
                                planList.add(materialCustomerEO);
                            }
                        }
                    }
                }
            }else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }

        return planList;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MaterialPlanEO entity,Long userId) throws BusinessException {


        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("物料计划的归属机构权限不存在该用户权限，请确认！");
        }

        MaterialEO materialEO = this.baseMapper.getByMaterialId(entity.getMaterialId());
        if(!entity.getOrgId().equals(materialEO.getOrgId())){
            throw new BusinessException("物料计划归属机构和物料的归属机构不一致，请确认！");
        }

        Integer count = 0;

        if(entity.getPlanType() == 1) {

            //判断客户物料关系是否存在
            count = this.baseMapper.selectExistCustomerMaterial(entity.getCustomerId(), entity.getMaterialId());
            if (count == 0) {
                throw new BusinessException("物料和客户关系不匹配，请确认！");
            }

            //判断修改后的同一个客户，同一物料，同一月份，是否存在
            count = this.baseMapper.selectUpdateExistCount(entity);
            if (count > 0) {
                throw new BusinessException("物料编码为：" + entity.getMaterialCode() + " 的物料计划已存在，请确认！");
            }
        }else if (entity.getPlanType() == 2){
            //直接更新，不需要去多余的判断
            MaterialPlanEO oldPlan = this.baseMapper.selectById(entity.getSerialId());
            MaterialPlanEO monthPlanEO = this.baseMapper.selectById(oldPlan.getParentSerialId());

            Double less =monthPlanEO.getRequireCount() - oldPlan.getRequireCount() + entity.getRequireCount();
            monthPlanEO.setRequireCount(less);
            super.updateById(monthPlanEO);

        }else{
            throw new BusinessException("计划类型不对");
        }

        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Serializable[] ids) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        Integer result = 0;
        // 删除关系
        for (Serializable id : ids) {
            MaterialPlanEO materialPlanEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(materialPlanEO.getOrgId(),userId)){
                continue;
            }

            if(materialPlanEO.getPlanType() == 2){
                MaterialPlanEO monthPlanEO = this.baseMapper.selectById(materialPlanEO.getParentSerialId());
                Double less = monthPlanEO.getRequireCount() - materialPlanEO.getRequireCount();
                if(less == 0d){
                    super.removeById(id);
                    super.removeById(monthPlanEO.getSerialId());
                }else{
                    monthPlanEO.setRequireCount(less);
                    super.updateById(monthPlanEO);
                    super.removeById(id);
                }

            }else{
                super.removeById(id);
            }

        }

        return true;
    }

    public IPage<MaterialPlanEO> selectMPlanCustomPage(Criteria criteria) throws BusinessException {
        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<MaterialPlanEO> wrapper = new QueryWrapper<MaterialPlanEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<MaterialPlanEO> totalList = this.baseMapper.selectMPlanCustomPage(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<MaterialPlanEO> list = this.baseMapper.selectMPlanCustomPage(param);

        IPage<MaterialPlanEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }


    public List<MaterialCustomerEO> importFromWeekExcel(List weekList, Date monthDate, String customerName, Long customerId) throws BusinessException {
        List<MaterialCustomerEO> planWeekList = new ArrayList<>();
//        String  monthDateStr = DateUtils.format(monthDate,"yyyy-MM");
//        String dateStr = "";
//        if(weekList!=null){
//            if(weekList.size()>0){
//                List<Map> mapList = (List<Map>) weekList.get(0); //根据sheet获取
//                if(mapList!=null && mapList.size()>1){
//                    for(int i=0;i < mapList.size();i++){
//                        Map  map = mapList.get(i);
//                        if(i < 1){
//                            continue;
//                        }
//
//
//                        //初始参数校验
//                        //零件号
//                        String elementNo = (String) map.get("0");
//                        if(null==elementNo || elementNo.isEmpty()){
//                            continue;
//                        }
//
//
//                        //查询零件号和客户的关系是否存在
//                        MaterialCustomerEO materialCustomerEO =  this.baseMapper.selectCustomerMaterial(Long.valueOf(customerId),elementNo);
//                        if(null == materialCustomerEO){
//                            logger.info("========物料客户关系不存在========"+elementNo);
//                            continue;
//                        }
//
//                        //不允许采购，不允许生产
//                        if(materialCustomerEO.getIsProduct() != 1 && materialCustomerEO.getIsPurchase()!=1){
//                            logger.info("========不允许采购和生产========"+elementNo);
//                            materialCustomerEO.setErrImportType(1);
//                            planWeekList.add(materialCustomerEO);
//                            continue;
//                        }
//
//
//                        //查看物料月计划
//                        MaterialPlanEO materialPlanEO = new MaterialPlanEO();
//                        materialPlanEO.setCustomerId(Long.valueOf(customerId));
//                        //月计划日期
//                        dateStr = monthDateStr + "-01";
//                        Date dateMonth = DateUtils.stringToDate(dateStr,"yyyy-MM-dd");
//                        materialPlanEO.setMonthDate(dateMonth);
//                        materialPlanEO.setMaterialId(materialCustomerEO.getMaterialId());
//                        MaterialPlanEO entity = this.baseMapper.selectExistEntity(materialPlanEO);
//                        if(null == entity){
//                            logger.info("========月计划已被删除========"+elementNo);
//                            materialCustomerEO.setErrImportType(2);
//                            planWeekList.add(materialCustomerEO);
//                            continue;
//                        }
//
//                        //遍历查询单元格数据
//                        for(int h=1;h<=31;h++){
//                            //单元格数据
//                            int j = h + 3;
//
//                            //计划数量
//                            String planCount = (String) map.get(""+j);
//                            if(null == planCount || "0".equals(planCount)  || planCount.isEmpty()){
//                                logger.info("========计划数量值不存在========"+elementNo);
//                                continue;
//                            }
//
//                            //生产新的实体
//                            MaterialCustomerEO one =  this.baseMapper.selectCustomerMaterial(Long.valueOf(customerId),elementNo);
//
//                            //设置月计划归属机构到日计划
//                            one.setOrgId(entity.getOrgId());
//                            //设置日计划日期
//                            if (h<10){
//                                dateStr = monthDateStr + "-0" +h;
//                            }else{
//                                dateStr = monthDateStr + "-" +h;
//                            }
//                            Date date = DateUtils.stringToDate(dateStr,"yyyy-MM-dd");
//                            one.setWeekDate(date);
//
//                            one.setRequireCount(Double.valueOf(planCount));
//                            if(one.getIsProduct() == 1){
//                                //生产数量
//                                Double productCount = Double.valueOf(planCount);
//                                if (productCount > 0){
//                                    one.setRequireProductCount(productCount);
//                                }else{
//                                    one.setRequireProductCount(0d);
//                                }
//                                one.setRequirePurchaseCount(0d);
//
//                            }
//
//                            if(one.getIsProduct() != 1 && one.getIsPurchase()==1){
//                                //采购数量
//                                Double purCount = Double.valueOf(planCount);
//                                if(purCount > 0){
//                                    one.setRequirePurchaseCount(purCount);
//                                }else{
//                                    one.setRequirePurchaseCount(0d);
//                                }
//                                one.setRequireProductCount(0d);
//                            }
//
//                            one.setErrImportType(0);
//                            planWeekList.add(one);
//
//                        }
//                    }
//                }
//            }else{
//                throw new BusinessException("请确认文件有内容！");
//            }
//        }else{
//            throw new BusinessException("服务器解析文件出错！");
//        }

        return planWeekList;
    }



    public boolean selectExistCount(Long[] ids) throws BusinessException {
        String msg = "";
        // 删除关系
        for (Serializable id : ids) {
            MaterialPlanEO result = this.baseMapper.selectChildPlan((Long) id);
            if(null != result){
                if ("".equals(msg)){
                    msg = result.getSerialCode();
                }else{
                    msg = msg+"、"+result.getSerialCode();
                }
            }

        }
        if(!"".equals(msg)){
            throw new BusinessException("您选择的物料计划中有物料存在周计划，不能进行删除操作,流水号："+msg);
        }
        return true;
    }


    public List<MaterialPlanEO> getWeekDetailList(Long parentSerialId,Long orgId) {

        List<MaterialPlanEO> list = this.baseMapper.getWeekDetailList(parentSerialId,orgId);
        return list;
    }


    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public String generateNextCode(String ruleCode, Object entity,Long orgId) throws BusinessException{
        String nextCode;

        //查询是否存在这个编码规则
        int count = this.baseMapper.selectCountByCode(ruleCode,orgId);
        if(count == 0) {
            throw new BusinessException("请先配置-"+ruleCode+"-的编码规则");
        }
        // 获取前缀
        String prefix = "";
        String maxCode;
        // 获取编码规则
        CodeRuleEO codeRule = this.baseMapper.selectByRuleCode(ruleCode,orgId);
        AssertUtils.isNull(codeRule);

        // 第一级编码
        if (StringUtils.isNotBlank(codeRule.getLevel1()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel1(), entity));

        // 第二级编码
        if (StringUtils.isNotBlank(codeRule.getLevel2()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel2(), entity));

        // 第三级编码
        if (StringUtils.isNotBlank(codeRule.getLevel3()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel3(), entity));

        // 第四级编码
        if (StringUtils.isNotBlank(codeRule.getLevel4()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel4(), entity));

        // 第五级编码
        if (StringUtils.isNotBlank(codeRule.getLevel5()))
            prefix = prefix.concat(formatModifier(codeRule.getLevel5(), entity));



        if (StringUtils.isBlank(prefix)){// 全数字格式编码
            // 获取最大编码
            maxCode = this.baseMapper.selectMaxCode(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    codeRule.getSeqRule().length(),orgId);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);
        } else {
            // 带有前缀编码
            int seqLength = codeRule.getSeqRule().length();
            int prefixLength = prefix.length();
            int codeLength = seqLength + prefixLength;

            maxCode = this.baseMapper.selectMaxCodeWithPrefix(
                    codeRule.getTableName(),
                    codeRule.getColumnName(),
                    seqLength, prefixLength, codeLength, prefix,orgId);

            if (StringUtils.isBlank(maxCode))
                maxCode = "0";

            // 获取最大编码
            nextCode = formatNumber(codeRule.getSeqRule(), maxCode);

            // 加上前缀
            nextCode = prefix.concat(nextCode);
        }

        return nextCode;
    }

    /**
     * 检查编码规则是否全数字规则
     *
     * @param codeRule
     * @return
     */
    private static boolean isNumber(String codeRule){
        String regEx = "^[9]*$";
        Pattern patter = Pattern.compile(regEx);
        Matcher matcher = patter.matcher(codeRule);

        return matcher.matches();
    }

    /**
     * 根据指定格式格式化流水码
     *
     * @param rule
     * @param code
     * @return
     */
    private static String formatNumber(String rule, String code){
        // 使用“0”填充编码至指定长度
        String formatter = "%0"+rule.length()+"d";

        return String.format(formatter, Long.valueOf(code) + 1);
    }

    /**
     * 替换除流水码以外的编码规则
     *
     * @param modifier
     * @param obj
     * @return
     */
    private static String formatModifier(String modifier, Object obj){

        if (StringUtils.isBlank(modifier))
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date());

        // 判断修饰符是不是由变量组成
        if ("{{yyyymm}}".equals(modifier)){
            // 年月
            return date.substring(0, 6);
        } else if ("{{yyyy}}".equals(modifier)){
            // 年
            return date.substring(0, 4);
        }else if ("{{yymmdd}}".equals(modifier)){
            //获取年后两位+月+日
            return date.substring(2, 8);
        }
        else if ("{{yyyymmdd}}".equals(modifier)){
            // 年月日
            return date;
        }

        // 使用对象属性进行替换"{{xxxx}}"
        if (modifier.startsWith("{{") && modifier.endsWith("}}")){

            String prop = modifier.substring(2, modifier.indexOf("}}"));
            try {
                if (null != PropertyUtils.getPropertyDescriptor(obj, prop)) {
                    modifier = PropertyUtils.getProperty(obj, prop).toString();
                }

                return modifier;
            } catch (Exception ex) {

                // 发生异常则反馈空
                modifier = "";
            }
        }

        return modifier;
    }



    public Result importPlanExecl(List list, String monthDate, UserEO userEO) throws BusinessException {

        List<MaterialCustomerEO> planList = new ArrayList<>();
        String dateStr = "";

        String errorMsg = "";
        //日期和字符串转换
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String tempDateStr = monthDate + "-01";
        //转换日期
        Date tempDate = null;
        try {
            tempDate = sdf.parse(tempDateStr);
        }catch (Exception e){
            throw new BusinessException("日期转换有误！");
        }

        calendar.setTime(tempDate);
        //当月最大天数
        int days =calendar.getActualMaximum(Calendar.DATE);

        if(list!=null){
            if(list.size()>0){
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                if(mapList!=null && mapList.size()>1){

                    for(int i=0;i < mapList.size();i++) {
                        Map map = mapList.get(i);
                        if (i < 1) {
                            continue;
                        }


                        //初始参数校验
                        //零件号
                        String elementNo = (String) map.get("0");
                        if(null==elementNo || elementNo.isEmpty()){
                            errorMsg = errorMsg + "第 ["+(i+1)+"] 行：零件号为空\n";
                            continue;
                        }

                        //校验物料档案中是否存在这个零件
                        MaterialEO materialEO = this.baseMapper.selectMaterialInfo(elementNo,userEO.getOrgId());
                        if(null == materialEO){
                            errorMsg = errorMsg + "第 ["+(i+1)+"] 行：用户所属机构物料不存在\n";
                            continue;
                        }

                        //查询物料默认客户
                        CustomerEO customerDefault = this.baseMapper.selectDefaultCustomer(materialEO.getMaterialId());

                        MaterialCustomerEO materialCustomerEO = new MaterialCustomerEO();
                        //名称
                        String customerName = (String) map.get("2");
                        if(null==customerName || customerName.isEmpty()){
                            if(null != customerDefault){
                                materialCustomerEO = this.baseMapper.selectCustomerMaterialPlan(customerDefault.getCustomerName(),elementNo,userEO.getOrgId());
                                if(null == materialCustomerEO){
                                    errorMsg = errorMsg + "第 ["+(i+1)+"] 行：物料客户关系不存在\n";
                                    continue;
                                }
                            }else{
                                logger.info("========客户物料不存在========"+elementNo);
                                errorMsg = errorMsg + "第 ["+(i+1)+"] 行：物料客户关系不存在\n";
                                continue;
                            }
                        }else{
                            //查询客户，物料关系是否存在
                            materialCustomerEO = this.baseMapper.selectCustomerMaterialPlan(customerName,elementNo,userEO.getOrgId());
                            if(null == materialCustomerEO){
                                errorMsg = errorMsg + "第 ["+(i+1)+"] 行：物料客户关系不存在\n";
                                logger.info("========客户物料不存在========"+elementNo);
                                continue;
                            }
                        }



                        //供应商物料是否用户所属机构
                        if(!userEO.getOrgId().equals(materialCustomerEO.getOrgId())){
                            logger.info("========物料所属机构和用户所属机构不一致========"+elementNo);
                            continue;
                        }

                        //不允许采购
//                        if(materialCustomerEO.getIsPurchase()!=1){
//                            logger.info("========不允许采购========"+elementNo);
//                            //materialSupplierEO.setErrImportType(1);
//                            //planList.add(materialSupplierEO);
//                            continue;
//                        }

                        //下周一日期
//                        Date nestWeek = sdf.parse(sdf.format(calendar.getTime()));
//                        Integer week = 0;
                        MaterialCustomerEO one = materialCustomerEO;
                        Boolean flag = false;

                        //遍历查询单元格数据
                        for(int h=1;h<=days;h++){
                            //单元格数据
                            int j = h + 4;

                            //设置日计划日期
                            if (h<10){
                                dateStr = monthDate + "-0" +h;
                            }else{
                                dateStr = monthDate + "-" +h;
                            }
                            Date date = DateUtils.stringToDate(dateStr,"yyyy-MM-dd");

                            //计划数量
                            String planCount = (String) map.get(""+j);
                            if(null == planCount || "0".equals(planCount)  || planCount.isEmpty()){
                                logger.info("========计划数量值不存在========"+elementNo);
                                planCount = "0";
                                continue;
                            }

                            flag = true;
                            if(h == 1){
                                one.setPlanCount01(Double.valueOf(planCount));
                            }else if(h == 2){
                                one.setPlanCount02(Double.valueOf(planCount));
                            }else if(h == 3){
                                one.setPlanCount03(Double.valueOf(planCount));
                            }else if(h == 4){
                                one.setPlanCount04(Double.valueOf(planCount));
                            }else if(h == 5){
                                one.setPlanCount05(Double.valueOf(planCount));
                            }else if(h == 6){
                                one.setPlanCount06(Double.valueOf(planCount));
                            }else if(h == 7){
                                one.setPlanCount07(Double.valueOf(planCount));
                            }else if(h == 8){
                                one.setPlanCount08(Double.valueOf(planCount));
                            }else if(h == 9){
                                one.setPlanCount09(Double.valueOf(planCount));
                            }else if(h == 10){
                                one.setPlanCount10(Double.valueOf(planCount));
                            }else if(h == 11){
                                one.setPlanCount11(Double.valueOf(planCount));
                            }else if(h == 12){
                                one.setPlanCount12(Double.valueOf(planCount));
                            }else if(h == 13){
                                one.setPlanCount13(Double.valueOf(planCount));
                            }else if(h == 14){
                                one.setPlanCount14(Double.valueOf(planCount));
                            }else if(h == 15){
                                one.setPlanCount15(Double.valueOf(planCount));
                            }else if(h == 16){
                                one.setPlanCount16(Double.valueOf(planCount));
                            }else if(h == 17){
                                one.setPlanCount17(Double.valueOf(planCount));
                            }else if(h == 18){
                                one.setPlanCount18(Double.valueOf(planCount));
                            }else if(h == 19){
                                one.setPlanCount19(Double.valueOf(planCount));
                            }else if(h == 20){
                                one.setPlanCount20(Double.valueOf(planCount));
                            }else if(h == 21){
                                one.setPlanCount21(Double.valueOf(planCount));
                            }else if(h == 22){
                                one.setPlanCount22(Double.valueOf(planCount));
                            }else if(h == 23){
                                one.setPlanCount23(Double.valueOf(planCount));
                            }else if(h == 24){
                                one.setPlanCount24(Double.valueOf(planCount));
                            }else if(h == 25){
                                one.setPlanCount25(Double.valueOf(planCount));
                            }else if(h == 26){
                                one.setPlanCount26(Double.valueOf(planCount));
                            }else if(h == 27){
                                one.setPlanCount27(Double.valueOf(planCount));
                            }else if(h == 28){
                                one.setPlanCount28(Double.valueOf(planCount));
                            }else if(h == 29){
                                one.setPlanCount29(Double.valueOf(planCount));
                            }else if(h == 30){
                                one.setPlanCount30(Double.valueOf(planCount));
                            }else if(h == 31){
                                one.setPlanCount31(Double.valueOf(planCount));
                            }


//                            calendar.setTime(date);
//                            int weektemp = calendar.get(Calendar.WEEK_OF_YEAR);
//                            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
//                            if(weekday == 1){
//                                weektemp = weektemp -1;
//                            }
//                            //1号时初赋值
//                            if(h==1){
//                                week = weektemp;
//                            }
//
//                            if(week != weektemp || h == days) {
//                                //若存在数据，则增加到列表中
//                                if (flag) {
//                                    MaterialCustomerEO temp = this.baseMapper.selectCustomerMaterialPlan(customerName, elementNo,userEO.getOrgId());
//                                    String tempTDate = monthDate+"-01";
//                                    temp.setMonthDate(DateUtils.stringToDate(tempTDate,"yyyy-MM-dd"));
//
//                                    //一
//                                    if (null != one.getRequireCountOne() && one.getRequireCountOne() > 0) {
//                                        temp.setWeekOne(one.getWeekOne());
//                                        temp.setRequireCountOne(one.getRequireCountOne());
//                                    }else{
//                                        temp.setWeekOne(one.getWeekOne());
//                                    }
//                                    //二
//                                    if (null != one.getRequireCountTwo() && one.getRequireCountTwo() > 0) {
//                                        temp.setWeekTwo(one.getWeekTwo());
//                                        temp.setRequireCountTwo(one.getRequireCountTwo());
//                                    }else{
//                                        temp.setWeekTwo(one.getWeekTwo());
//                                    }
//                                    //三
//                                    if (null != one.getRequireCountThree() && one.getRequireCountThree() > 0) {
//                                        temp.setWeekThree(one.getWeekThree());
//                                        temp.setRequireCountThree(one.getRequireCountThree());
//                                    }else{
//                                        temp.setWeekThree(one.getWeekThree());
//                                    }
//                                    //四
//                                    if (null != one.getRequireCountFour() && one.getRequireCountFour() > 0) {
//                                        temp.setWeekFour(one.getWeekFour());
//                                        temp.setRequireCountFour(one.getRequireCountFour());
//                                    }else{
//                                        temp.setWeekFour(one.getWeekFour());
//                                    }
//                                    //五
//                                    if (null != one.getRequireCountFive() && one.getRequireCountFive() > 0) {
//                                        temp.setWeekFive(one.getWeekFive());
//                                        temp.setRequireCountFive(one.getRequireCountFive());
//                                    }else{
//                                        temp.setWeekFive(one.getWeekFive());
//                                    }
//                                    //六
//                                    if (null != one.getRequireCountSix() && one.getRequireCountSix() > 0) {
//                                        temp.setWeekSix(one.getWeekSix());
//                                        temp.setRequireCountSix(one.getRequireCountSix());
//                                    }else {
//                                        temp.setWeekSix(one.getWeekSix());
//                                    }
//                                    //七
//                                    if (null != one.getRequireCountSeven() && one.getRequireCountSeven() > 0) {
//                                        temp.setWeekSeven(one.getWeekSeven());
//                                        temp.setRequireCountSeven(one.getRequireCountSeven());
//                                    }else {
//                                        temp.setWeekSeven(one.getWeekSeven());
//                                    }
//
////                                    temp.setWeekSum(week);
//                                    //将数据加到列表中
//                                    planList.add(temp);
//                                }
//
//                                //初始化
//                                one = this.baseMapper.selectCustomerMaterialPlan(customerName, elementNo,userEO.getOrgId());
//
//                                week = weektemp;
//                                flag = false;
//                            }
//                            //周相同时，只需更新字段数字
//                            if(week == weektemp){
//                                //星期天
//                                if(weekday == 1){
//
//                                    one.setWeekSeven(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountSeven(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountSeven(Double.valueOf(planCount));
//                                    }
//                                }else if(weekday == 2){//一
//
//                                    one.setWeekOne(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountOne(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountOne(Double.valueOf(planCount));
//                                    }
//
//                                }else if(weekday == 3){//二
//
//                                    one.setWeekTwo(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountTwo(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountTwo(Double.valueOf(planCount));
//                                    }
//                                }else if(weekday == 4){//三
//
//                                    one.setWeekThree(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountThree(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountThree(Double.valueOf(planCount));
//                                    }
//
//                                }else if(weekday == 5){//四
//
//                                    one.setWeekFour(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountFour(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountFour(Double.valueOf(planCount));
//                                    }
//
//                                }else if(weekday == 6){//五
//
//                                    one.setWeekFive(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountFive(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountFive(Double.valueOf(planCount));
//                                    }
//
//                                }else if(weekday == 7){//六
//
//                                    one.setWeekSix(date);
//                                    if("0".equals(planCount)){
//                                        one.setRequireCountSix(null);
//                                    }else{
//                                        flag =true;
//                                        one.setRequireCountSix(Double.valueOf(planCount));
//                                    }
//                                }
                        }//1

                        if(flag){
                            one.setMonthDate(DateUtils.stringToDate(tempDateStr,"yyyy-MM-dd"));
                            planList.add(one);
                        }


                        //判断日计划是否存在
//                            Integer count = this.baseMapper.selectExistCount(materialSupplierEO.getMaterialId(),materialSupplierEO.getSupplierId(),date,1);
//                            if(count > 0){
//                                one.setErrImportType(2);
//                                planList.add(one);
//                                continue;
//                            }
//                            one.setWeekDate(date);
//                            one.setRequireCount(Double.valueOf(planCount));
//                            one.setWeeks(week);
//                            one.setErrImportType(0);

                    }//3
                }//4
            }else{//5
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }

        //将错误信息返回，同时让客户必须保证数据的正确性
        Result result =  new Result<List<MaterialCustomerEO>>().ok(planList);
        if(!errorMsg.isEmpty()){
            result.setCode(100);
            result.setMsg(errorMsg);
        }

        return result;

    }



    @Transactional(rollbackFor = Exception.class)
    public boolean batcahSave(MaterialCustomerEO[] entitys, UserEO userEO) throws BusinessException {
        for (MaterialCustomerEO materialCustomerEO:entitys){

            String monthDate = DateUtils.format(materialCustomerEO.getMonthDate(),"yyyy-MM");

            if(null != materialCustomerEO.getPlanCount01() && materialCustomerEO.getPlanCount01()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-01","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount01());
                saveData(materialCustomerEO,userEO);
            }

            if(null != materialCustomerEO.getPlanCount02() && materialCustomerEO.getPlanCount02()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-02","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount02());
                saveData(materialCustomerEO,userEO);
            }

            if(null != materialCustomerEO.getPlanCount03() && materialCustomerEO.getPlanCount03()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-03","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount03());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount04() && materialCustomerEO.getPlanCount04()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-04","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount04());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount05() && materialCustomerEO.getPlanCount05()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-05","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount05());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount06() && materialCustomerEO.getPlanCount06()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-06","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount06());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount07() && materialCustomerEO.getPlanCount07()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-07","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount07());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount08() && materialCustomerEO.getPlanCount08()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-08","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount08());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount09() && materialCustomerEO.getPlanCount09()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-09","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount09());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount10() && materialCustomerEO.getPlanCount10()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-10","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount10());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount11() && materialCustomerEO.getPlanCount11()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-11","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount11());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount12() && materialCustomerEO.getPlanCount12()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-12","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount12());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount13() && materialCustomerEO.getPlanCount13()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-13","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount13());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount14() && materialCustomerEO.getPlanCount14()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-14","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount14());
                saveData(materialCustomerEO,userEO);
            }if(null != materialCustomerEO.getPlanCount15() && materialCustomerEO.getPlanCount15()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-15","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount15());
                saveData(materialCustomerEO,userEO);
            }if(null != materialCustomerEO.getPlanCount16() && materialCustomerEO.getPlanCount16()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-16","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount16());
                saveData(materialCustomerEO,userEO);
            }if(null != materialCustomerEO.getPlanCount17() && materialCustomerEO.getPlanCount17()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-17","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount17());
                saveData(materialCustomerEO,userEO);
            }if(null != materialCustomerEO.getPlanCount18() && materialCustomerEO.getPlanCount18()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-18","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount18());
                saveData(materialCustomerEO,userEO);
            }if(null != materialCustomerEO.getPlanCount19() && materialCustomerEO.getPlanCount19()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-19","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount19());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount20() && materialCustomerEO.getPlanCount20()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-20","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount20());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount21() && materialCustomerEO.getPlanCount21()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-21","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount21());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount22() && materialCustomerEO.getPlanCount22()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-22","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount22());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount23() && materialCustomerEO.getPlanCount23()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-23","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount23());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount24() && materialCustomerEO.getPlanCount24()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-24","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount24());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount25() && materialCustomerEO.getPlanCount25()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-25","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount25());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount26() && materialCustomerEO.getPlanCount26()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-26","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount26());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount27() && materialCustomerEO.getPlanCount27()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-27","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount27());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount28() && materialCustomerEO.getPlanCount28()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-28","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount28());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount29() && materialCustomerEO.getPlanCount29()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-29","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount29());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount30() && materialCustomerEO.getPlanCount30()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-30","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount30());
                saveData(materialCustomerEO,userEO);
            }
            if(null != materialCustomerEO.getPlanCount31() && materialCustomerEO.getPlanCount31()>0){
                materialCustomerEO.setWeekDate(DateUtils.stringToDate(monthDate+"-31","yyyy-MM-dd"));
                materialCustomerEO.setRequireCount(materialCustomerEO.getPlanCount31());
                saveData(materialCustomerEO,userEO);
            }


        }

        return true;
    }

    public void saveData(MaterialCustomerEO materialCustomerEO,UserEO userEO){
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Boolean firstFlag = false;
        //周计划
        MaterialPlanEO materialPlanEO = new MaterialPlanEO();
        //判断周计划是否存在
        Integer count = this.baseMapper.selectWeekPlanCount(materialCustomerEO.getCustomerId(),materialCustomerEO.getMaterialId(),materialCustomerEO.getWeekDate(),materialCustomerEO.getOrgId());
        if (count > 0){
            return;
        }
        //判断周计划的上级月计划是否存在
        MaterialPlanEO monthPlanEO = this.baseMapper.selectParernPlan(materialCustomerEO.getCustomerId(),materialCustomerEO.getMaterialId(),materialCustomerEO.getMonthDate(),materialCustomerEO.getOrgId());
        if(null == monthPlanEO){

            monthPlanEO = new MaterialPlanEO();
            monthPlanEO.setMonthDate(materialCustomerEO.getMonthDate());
            monthPlanEO.setCustomerId(materialCustomerEO.getCustomerId());
            monthPlanEO.setCustomerCode(materialCustomerEO.getCustomerCode());
            monthPlanEO.setCustomerName(materialCustomerEO.getCustomerName());
            monthPlanEO.setOrgId(materialCustomerEO.getOrgId());
            monthPlanEO.setPlanType(3);
            monthPlanEO.setMaterialId(materialCustomerEO.getMaterialId());
            monthPlanEO.setMaterialCode(materialCustomerEO.getMaterialCode());
            monthPlanEO.setMaterialName(materialCustomerEO.getMaterialName());
            monthPlanEO.setElementNo(materialCustomerEO.getElementNo());
            monthPlanEO.setInventoryCode(materialCustomerEO.getInventoryCode());
            monthPlanEO.setRequireCount(materialCustomerEO.getRequireCount());
            monthPlanEO.setStatus(0);
            monthPlanEO.setPreStatus(0);
            // 生成业务编码
            String code = this.generateNextCode("cmp_material_plan", monthPlanEO,user.getOrgId());
            AssertUtils.isBlank(code);
            monthPlanEO.setSerialCode(code);
            super.save(monthPlanEO);

            firstFlag = true;
        }

        //新增周计划
        materialPlanEO.setMonthDate(materialCustomerEO.getMonthDate());
        materialPlanEO.setWeekDate(materialCustomerEO.getWeekDate());
        materialPlanEO.setCustomerId(materialCustomerEO.getCustomerId());
        materialPlanEO.setCustomerCode(materialCustomerEO.getCustomerCode());
        materialPlanEO.setCustomerName(materialCustomerEO.getCustomerName());
        materialPlanEO.setOrgId(materialCustomerEO.getOrgId());
        materialPlanEO.setPlanType(2);

        materialPlanEO.setMaterialId(materialCustomerEO.getMaterialId());
        materialPlanEO.setMaterialCode(materialCustomerEO.getMaterialCode());
        materialPlanEO.setMaterialName(materialCustomerEO.getMaterialName());
        materialPlanEO.setElementNo(materialCustomerEO.getElementNo());
        materialPlanEO.setInventoryCode(materialCustomerEO.getInventoryCode());
        materialPlanEO.setRequireCount(materialCustomerEO.getRequireCount());
        materialPlanEO.setPreStatus(0);
        materialPlanEO.setStatus(0);
        materialPlanEO.setParentSerialId(monthPlanEO.getSerialId());
        materialPlanEO.setPlanVersion(1d);
//        materialPlanEO.setWeekSum(materialCustomerEO.getWeekSum());
        // 生成业务编码
        String code = this.generateNextCode("cmp_material_plan", materialPlanEO,user.getOrgId());
        AssertUtils.isBlank(code);
        materialPlanEO.setSerialCode(code);
        super.save(materialPlanEO);

        if (!firstFlag)
        {
            monthPlanEO.setRequireCount(monthPlanEO.getRequireCount()+materialPlanEO.getRequireCount());
            super.updateById(monthPlanEO);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public Result releaseMonth(Long[] ids) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();
        int successCount = 0;
        int errorCount = 0;
        for(Long id:ids) {
            // 判断是否存在不是相应状态的数据,符合状态的数据做更新
            MaterialPlanEO entity = this.getById(id);
            if(entity == null) {
                errorCount ++;
                continue;
            }
            // 更新数据状态
            if(entity.getStatus().intValue() == 0) {
                this.baseMapper.updateStatusById(id,1);
            }else{
                errorCount ++;
                continue;
            }
            // 校验机构权限(权限不足，则是因为机构权限被收回，操作后会刷新数据，机构权限不存在的数据将看不到了)
            if (!checkPer(entity.getOrgId(), userId)) {
                errorCount ++;
                continue;
            }

            //最优先级查找 该零件号在“冲压原材料耗用表”中能否找到对应记录，如果找到了，就生成原材料耗用记录，并结束,如果找不到则去bom拆分
            StampingMaterialConsumptionQuotaEO smcqEo = this.stampingMaterialConsumptionQuotaService.getByElementNoAndOrg(entity.getElementNo(), entity.getOrgId());
            if(smcqEo != null) {
                MaterialRequirementEO materialRequirement = new MaterialRequirementEO();
                //设置参数
                materialRequirement.setSerialId(entity.getSerialId());
                materialRequirement.setCustomerId(entity.getCustomerId());
                materialRequirement.setCustomerCode(entity.getCustomerCode());
                materialRequirement.setCustomerName(entity.getCustomerName());
                materialRequirement.setMonthDate(entity.getMonthDate());
                materialRequirement.setPlanType(1);
                materialRequirement.setMaterialId(smcqEo.getMaterialId());
                materialRequirement.setOrgId(smcqEo.getOrgId());
                materialRequirement.setMaterialName(smcqEo.getMaterialName());
                materialRequirement.setMaterialCode(smcqEo.getMaterialCode());
                materialRequirement.setElementNo(smcqEo.getElementNo());
                materialRequirement.setMaterialModelSpecific(smcqEo.getMaterialPcode());
                materialRequirement.setWeight(smcqEo.getGrossWeight());
                materialRequirement.setSpecification(smcqEo.getMaterialSpecific());
                materialRequirement.setInventoryCode(smcqEo.getInventoryCoding());
                materialRequirement.setMaterialId(smcqEo.getOriginalMaterialId());
                materialRequirement.setRequireCount(entity.getRequireProductCount());
                double totalWeigth = entity.getRequireProductCount() * smcqEo.getGrossWeight();
                materialRequirement.setTotalWeight(totalWeigth);
                materialRequirement.setStatus(0);
                boolean saveFlag = this.materialRequirementService.save(materialRequirement);
                if(saveFlag) {
                    successCount ++;
                } else {
                    errorCount ++;
                }
                Result result = new Result();
                result.setMsg("操作成功" + ids.length + "条；原材料需求生成" + successCount + "条，请在“原材料月需求计划”中查看!");
                return result;
            }

            // 查询月计划零件号对应的bom(零件号,归属机构需要一样)
            List<BomEO> bomList = this.bomService.getByElementNoAndOrg(entity.getElementNo(), entity.getOrgId());
            if(bomList==null || bomList.size()==0) {
                errorCount ++;
                continue;
            }
            BomEO bom = bomList.get(0); // 若真有多条数据,只取第一条数据
            List<BomEO> boms = new ArrayList<>();
            // 如果bom是最上层的节点,即parent_bom_id字段值为0,则查询该bom下的所有自制(即制造)叶子节点,根据这些叶子节点去查询冲压材料耗用定额中对应的数据
            if(bom.getParentBomId().intValue() == 0) {
//                // 查询月计划零件号对应的bom下的所有自制(即制造)叶子节点(不包括本身)
//                boms = this.bomService.getAllLeaves(bom, "product");

                // 查询月计划零件号对应的bom下的所有节点(包括本身)
                boms = this.bomService.getNewAllLeaves(bom, "product");
            } else { // 如果不是最上层的节点,则将bom本身作为数据去查询冲压材料耗用定额中对应的数据
                boms.add(bom);
            }

            if(boms==null || boms.size()==0) {
                errorCount ++;
                continue;
            }
            // 查询冲压材料耗用定额中对应上述boms数据(零件号,归属机构需要一样)
            for(int i=0; i<boms.size(); i++) {
                BomEO bomTemp = boms.get(i);
                StampingMaterialConsumptionQuotaEO smcq = this.stampingMaterialConsumptionQuotaService.getByElementNoAndOrg(bomTemp.getElementNo(), bomTemp.getOrgId());
                if(smcq == null) {
                    errorCount ++;
                    continue;
                }
                MaterialRequirementEO materialRequirement = new MaterialRequirementEO();
                //设置参数
                materialRequirement.setSerialId(entity.getSerialId());
                materialRequirement.setCustomerId(entity.getCustomerId());
                materialRequirement.setCustomerCode(entity.getCustomerCode());
                materialRequirement.setCustomerName(entity.getCustomerName());
                materialRequirement.setMonthDate(entity.getMonthDate());
                materialRequirement.setPlanType(1);
                materialRequirement.setMaterialId(bomTemp.getMaterialId());
                materialRequirement.setOrgId(smcq.getOrgId());
                materialRequirement.setMaterialName(smcq.getMaterialName());
                materialRequirement.setMaterialCode(smcq.getMaterialCode());
                materialRequirement.setElementNo(smcq.getElementNo());
                materialRequirement.setMaterialModelSpecific(smcq.getMaterialPcode());
                materialRequirement.setWeight(smcq.getGrossWeight());
                materialRequirement.setSpecification(smcq.getMaterialSpecific());
                materialRequirement.setInventoryCode(smcq.getInventoryCoding());
                materialRequirement.setMaterialId(smcqEo.getOriginalMaterialId());
                materialRequirement.setMaterialModel(bomTemp.getMaterialModel());
                materialRequirement.setRequireCount(entity.getRequireProductCount() * bomTemp.getAmount());
                double totalWeigth = entity.getRequireProductCount() * bomTemp.getAmount() * smcq.getGrossWeight();
                materialRequirement.setTotalWeight(totalWeigth);
                materialRequirement.setStatus(0);
                boolean saveFlag = this.materialRequirementService.save(materialRequirement);
                if(saveFlag) {
                    successCount ++;
                } else {
                    errorCount ++;
                }
            }
        }
        Result result = new Result();
        result.setMsg("操作成功" + ids.length + "条；原材料需求生成" + successCount + "条，请在“原材料月需求计划”中查看!");
        return result;
    }



    public IPage<MaterialPlanEO> pageWeek(Criteria criteria) throws BusinessException {
        List<MaterialPlanEO> pageList = new ArrayList<>();

        List<MaterialPlanEO> returnRecords = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Map<Object,MaterialPlanEO> map = new LinkedHashMap();

        int current = criteria.getCurrentPage();
        int size = criteria.getSize();

        //获取所有数据，再进行组装
        criteria.setCurrentPage(1);
        criteria.setSize(10000000);
        IPage<MaterialPlanEO> page = super.selectPage(criteria);
        if(null != page.getRecords() && page.getRecords().size() > 0){
            for(MaterialPlanEO planEO:page.getRecords()){
                if(null != map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId())){
                    //若存在则更新已存在数据的需求数
                    calendar.setTime(planEO.getWeekDate());
                    int monthday = calendar.get(Calendar.DAY_OF_MONTH);
                    map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()).setRequireCount(planEO.getRequireCount());
                    setWeek(map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()),monthday);
                }else{
                    calendar.setTime(planEO.getWeekDate());
                    int monthday = calendar.get(Calendar.DAY_OF_MONTH);
                    setWeek(planEO,monthday);
                    map.put(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId(),planEO);

                }

            }
        }

        for(Object key : map.keySet()){
            returnRecords.add(map.get(key));
        }

        IPage<MaterialPlanEO> weekPage = new Page<MaterialPlanEO>();
        weekPage.setSize(size);
        weekPage.setCurrent(current);
        weekPage.setTotal(returnRecords.size());

        int pages =  returnRecords.size()/size;
        if(returnRecords.size() % size > 0){
            pages = pages +1;
        }
        weekPage.setPages(pages);

        for(int i = size*(current -1);i<size*current;i++){
            if(i < returnRecords.size()){
                pageList.add(returnRecords.get(i));
            }
        }

        weekPage.setRecords(pageList);
        return weekPage;
    }


    public void cancelReleaseMonth(Long[] ids) {
        for(Long id : ids) {
            MaterialPlanEO entity = this.getById(id);
            if(entity.getStatus().intValue() == 1) {
                this.baseMapper.updateStatusById(id, 0);
                this.materialRequirementService.deleteBySerialId(id);
            }
        }
    }

    public void setWeek(MaterialPlanEO planEO,int monthday){

        //1号
        if(monthday == 1){
            planEO.setRequireCount01(planEO.getRequireCount());
        }else if(monthday == 2){//2号
            planEO.setRequireCount02(planEO.getRequireCount());
        }else if(monthday == 3){//3号
            planEO.setRequireCount03(planEO.getRequireCount());
        }else if(monthday == 4){//4号
            planEO.setRequireCount04(planEO.getRequireCount());
        }else if(monthday == 5){//5号
            planEO.setRequireCount05(planEO.getRequireCount());
        }else if(monthday == 6){//6号
            planEO.setRequireCount06(planEO.getRequireCount());
        }else if(monthday == 7){//7号
            planEO.setRequireCount07(planEO.getRequireCount());
        }else if(monthday == 8){//8号
            planEO.setRequireCount08(planEO.getRequireCount());
        }else if(monthday == 9){//9号
            planEO.setRequireCount09(planEO.getRequireCount());
        }else if(monthday == 10){//10号
            planEO.setRequireCount10(planEO.getRequireCount());
        }else if(monthday == 11){//11号
            planEO.setRequireCount11(planEO.getRequireCount());
        }else if(monthday == 12){//12号
            planEO.setRequireCount12(planEO.getRequireCount());
        }else if(monthday == 13){//13号
            planEO.setRequireCount13(planEO.getRequireCount());
        }else if(monthday == 14){//14号
            planEO.setRequireCount14(planEO.getRequireCount());
        }else if(monthday == 15){//15号
            planEO.setRequireCount15(planEO.getRequireCount());
        }else if(monthday == 16){//16号
            planEO.setRequireCount16(planEO.getRequireCount());
        }else if(monthday == 17){//17号
            planEO.setRequireCount17(planEO.getRequireCount());
        }else if(monthday == 18){//18号
            planEO.setRequireCount18(planEO.getRequireCount());
        }else if(monthday == 19){//19号
            planEO.setRequireCount19(planEO.getRequireCount());
        }else if(monthday == 20){//20号
            planEO.setRequireCount20(planEO.getRequireCount());
        }else if(monthday == 21){//21号
            planEO.setRequireCount21(planEO.getRequireCount());
        }else if(monthday == 22){//22号
            planEO.setRequireCount22(planEO.getRequireCount());
        }else if(monthday == 23){//23号
            planEO.setRequireCount23(planEO.getRequireCount());
        }else if(monthday == 24){//24号
            planEO.setRequireCount24(planEO.getRequireCount());
        }else if(monthday == 25){//25号
            planEO.setRequireCount25(planEO.getRequireCount());
        }else if(monthday == 26){//26号
            planEO.setRequireCount26(planEO.getRequireCount());
        }else if(monthday == 27){//27号
            planEO.setRequireCount27(planEO.getRequireCount());
        }else if(monthday == 28){//28号
            planEO.setRequireCount28(planEO.getRequireCount());
        }else if(monthday == 29){//29号
            planEO.setRequireCount29(planEO.getRequireCount());
        }else if(monthday == 30){//30号
            planEO.setRequireCount30(planEO.getRequireCount());
        }else if(monthday == 31){//31号
            planEO.setRequireCount31(planEO.getRequireCount());
        }

    }


    public IPage<MaterialPlanEO> pageMonth(Criteria criteria) throws BusinessException {
        List<MaterialPlanEO> list = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Map<Object,MaterialPlanEO> map = new LinkedHashMap();

        int current = criteria.getCurrentPage();
        int size = criteria.getSize();

        List<MaterialPlanEO> records = new ArrayList<>();

        //获取所有数据，再进行组装
        criteria.setCurrentPage(1);
        criteria.setSize(10000000);
        IPage<MaterialPlanEO> page = super.selectPage(criteria);

        if(null != page.getRecords() && page.getRecords().size() > 0){
            for(MaterialPlanEO planEO:page.getRecords()){

                if(null != map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId())){
                    calendar.setTime(planEO.getMonthDate());
                    int month = calendar.get(Calendar.MONTH) +1;
                    map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()).setRequireCount(planEO.getRequireCount());
                    map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()).setSerialId(planEO.getSerialId());
                    map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()).setRequireProductCount(planEO.getRequireProductCount());
                    map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()).setStatus(planEO.getStatus());
                    setMonth(map.get(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId()),month);
                }else{
                    calendar.setTime(planEO.getMonthDate());
                    int month = calendar.get(Calendar.MONTH) +1;
                    setMonth(planEO,month);
                    map.put(planEO.getYearSum()+"==="+planEO.getMaterialId()+"==="+planEO.getCustomerId()+"==="+planEO.getOrgId(),planEO);
                }

//                if(map.size() > size*current){
//                    break;
//                }

            }

        }

        for(Object key : map.keySet()){
            records.add(map.get(key));
        }

        IPage<MaterialPlanEO> monthPage = new Page<MaterialPlanEO>();
        monthPage.setCurrent(current);
        monthPage.setSize(size);
        monthPage.setTotal(records.size());

        int pages =  records.size()/size;
        if(records.size() % size > 0){
            pages = pages +1;
        }
        for(int i = size*(current -1);i<size*current;i++){
            if(i < records.size()){
                list.add(records.get(i));
            }
        }

        monthPage.setPages(pages);
        monthPage.setRecords(list);

        return monthPage;
    }


    public void setMonth(MaterialPlanEO planEO,int month){

        if(month == 1){
            //需求数
            planEO.setRequireCount01(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount01(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId01(planEO.getSerialId());
            //状态
            planEO.setStatus01(planEO.getStatus());

        }else if(month == 2){
            //需求数
            planEO.setRequireCount02(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount02(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId02(planEO.getSerialId());
            //状态
            planEO.setStatus02(planEO.getStatus());
        }else if(month == 3){
            //需求数
            planEO.setRequireCount03(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount03(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId03(planEO.getSerialId());
            //状态
            planEO.setStatus03(planEO.getStatus());
        }else if(month == 4){
            //需求数
            planEO.setRequireCount04(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount04(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId04(planEO.getSerialId());
            //状态
            planEO.setStatus04(planEO.getStatus());
        }else if(month == 5){
            //需求数
            planEO.setRequireCount05(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount05(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId05(planEO.getSerialId());
            //状态
            planEO.setStatus05(planEO.getStatus());
        }else if(month == 6){
            //需求数
            planEO.setRequireCount06(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount06(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId06(planEO.getSerialId());
            //状态
            planEO.setStatus06(planEO.getStatus());
        }else if(month == 7){
            //需求数
            planEO.setRequireCount07(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount07(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId07(planEO.getSerialId());
            //状态
            planEO.setStatus07(planEO.getStatus());
        }else if(month == 8){
            //需求数
            planEO.setRequireCount08(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount08(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId08(planEO.getSerialId());
            //状态
            planEO.setStatus08(planEO.getStatus());
        }else if(month == 9){
            //需求数
            planEO.setRequireCount09(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount09(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId09(planEO.getSerialId());
            //状态
            planEO.setStatus09(planEO.getStatus());
        }else if(month == 10){
            //需求数
            planEO.setRequireCount10(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount10(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId10(planEO.getSerialId());
            //状态
            planEO.setStatus10(planEO.getStatus());
        }else if(month == 11){
            //需求数
            planEO.setRequireCount11(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount11(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId11(planEO.getSerialId());
            //状态
            planEO.setStatus11(planEO.getStatus());
        }else if(month == 12){
            //需求数
            planEO.setRequireCount12(planEO.getRequireProductCount());
            //计划数
            planEO.setPlanCount12(planEO.getRequireCount());
            //主键ID
            planEO.setSerialId12(planEO.getSerialId());
            //状态
            planEO.setStatus12(planEO.getStatus());
        }
    }



    public Result getIdList(String action, String monthDate, UserEO userEO) throws BusinessException {
        List<Long> idList = new ArrayList<>();
        Long[] ids = new Long[]{};
        Result result = new Result();

        if("releaseMonth".equals(action)){
            idList = this.baseMapper.selectIdList(DateUtils.stringToDate(monthDate,"yyyy-MM-dd"),0,userEO.getOrgId());
            result = this.releaseMonth(idList.toArray(ids));

        }else if ("cancelReleaseMonth".equals(action)){
            idList = this.baseMapper.selectIdList(DateUtils.stringToDate(monthDate,"yyyy-MM-dd"),1,userEO.getOrgId());
            this.cancelReleaseMonth(idList.toArray(ids));
        }


        return result;
    }


    public List<MaterialPlanEO> getPlanInfoByIds(Long[] ids) {
        String sqlStr= "";
        List<MaterialPlanEO> records = new ArrayList<>();
        for(Long id:ids){
            if("".equals(sqlStr)){
                sqlStr = "('"+id+"'";
            }else{
                sqlStr = sqlStr + ",'"+id+"'";
            }
        }

        if(!sqlStr.isEmpty()){
            sqlStr = sqlStr +")";
            records = this.baseMapper.selectPlanInfoByIds(sqlStr);
        }
        return records;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIdList(String monthDate, UserEO userEO) throws BusinessException {

        return this.baseMapper.deleteByIdList(DateUtils.stringToDate(monthDate,"yyyy-MM-dd"),userEO.getOrgId());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(Long[] ids,UserEO userEO) throws BusinessException {
        String sqlStr= "";
        for(Long id:ids){
            if("".equals(sqlStr)){
                sqlStr = "('"+id+"'";
            }else{
                sqlStr = sqlStr + ",'"+id+"'";
            }
        }

        if(!sqlStr.isEmpty()){
            sqlStr = sqlStr +")";
            this.baseMapper.deleteByIds(sqlStr,userEO.getOrgId());
        }

        return true;
    }

    public void updateStatusByIds(String sqlStr, Integer status) {
        this.baseMapper.updateStatusByIds(sqlStr, status);
    }

    public void updateStatusByParentSerialIds(String sqlStr, Integer status) {
        this.baseMapper.updateStatusByParentSerialIds(sqlStr, status);
    }

    // 生成采购订单，委外订单，生产订单
//    private Map generateOrders(MaterialPlanEO materialPlan, UserEO user, Map mapObj, Map map) {
//        List<PurchaseOrderEO> purchaseOrders = (List<PurchaseOrderEO>) map.get("purchaseOrders");
//        List<PurchaseOrderEO> outsideOrders = (List<PurchaseOrderEO>) map.get("outsideOrders");
//        List<ProductOrderEO> productOrders = (List<ProductOrderEO>) map.get("productOrders");
//
//        List<BomEO> purchaseBoms = (List<BomEO>) mapObj.get("purchaseBoms"); // 需产生采购订单的Bom
//        if(purchaseBoms!=null && purchaseBoms.size()>0) {
//            for(BomEO bomItem : purchaseBoms) {
//                PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
//                purchaseOrder.setType(1);
//                purchaseOrder.setSupplierId(bomItem.getSupplierId());
//                purchaseOrder.setSerialId(materialPlan.getSerialId());
//                purchaseOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//                purchaseOrder.setOrgId(materialPlan.getOrgId());
//                purchaseOrder.setMaterialId(bomItem.getMaterialId());
//                purchaseOrder.setMaterialName(bomItem.getMaterialName());
//                purchaseOrder.setSpecification(bomItem.getSpecification());
//                purchaseOrder.setMaterialCode(bomItem.getMaterialCode());
//                purchaseOrder.setInventoryCode(bomItem.getInventoryCode());
//                purchaseOrder.setElementNo(bomItem.getElementNo());
//                purchaseOrder.setUnitName(bomItem.getUnitName());
//                purchaseOrder.setCreateDate(new Date());
//                purchaseOrder.setMonthDate(materialPlan.getMonthDate());
//                purchaseOrder.setPlanArriveDate(materialPlan.getWeekDate());
//                purchaseOrder.setPlanDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                purchaseOrder.setNotDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
//                purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
//                purchaseOrder.setReturnedQuantity(Double.valueOf(0));
//                purchaseOrder.setStatus(0);
//                purchaseOrder.setCreateUserId(user.getUserId());
//                purchaseOrder.setCreateUserName(user.getUserName());
//                purchaseOrder.setIsChangeConfirm(1);
////                this.purchaseOrderService.save(purchaseOrder);
//                purchaseOrders.add(purchaseOrder);
//            }
//        }
//
//        List<BomEO> outsideBoms = (List<BomEO>) mapObj.get("outsideBoms");  // 需产生委外订单的Bom
//        if(outsideBoms!=null && outsideBoms.size()>0) {
//            for(BomEO bomItem : outsideBoms) {
//                PurchaseOrderEO outsideOrder = new PurchaseOrderEO();
//                outsideOrder.setType(2);
//                outsideOrder.setSupplierId(bomItem.getSupplierId());
//                outsideOrder.setSerialId(materialPlan.getSerialId());
//                outsideOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//                outsideOrder.setOrgId(materialPlan.getOrgId());
//                outsideOrder.setMaterialId(bomItem.getMaterialId());
//                outsideOrder.setMaterialName(bomItem.getMaterialName());
//                outsideOrder.setSpecification(bomItem.getSpecification());
//                outsideOrder.setMaterialCode(bomItem.getMaterialCode());
//                outsideOrder.setInventoryCode(bomItem.getInventoryCode());
//                outsideOrder.setElementNo(bomItem.getElementNo());
//                outsideOrder.setUnitName(bomItem.getUnitName());
//                outsideOrder.setCreateDate(new Date());
//                outsideOrder.setMonthDate(materialPlan.getMonthDate());
//                outsideOrder.setPlanArriveDate(materialPlan.getWeekDate());
//                outsideOrder.setPlanDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                outsideOrder.setNotDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                outsideOrder.setActualDeliveryQuantity(Double.valueOf(0));
//                outsideOrder.setQualifiedQuantity(Double.valueOf(0));
//                outsideOrder.setReturnedQuantity(Double.valueOf(0));
//                outsideOrder.setStatus(0);
//                outsideOrder.setCreateUserId(user.getUserId());
//                outsideOrder.setCreateUserName(user.getUserName());
//                outsideOrder.setIsChangeConfirm(1);
////                this.purchaseOrderService.save(outsideOrder);
//                outsideOrders.add(outsideOrder);
//            }
//        }
//
//        List<BomEO> productBoms = (List<BomEO>) mapObj.get("productBoms");  // 需产生生产订单的Bom
//        if(productBoms!=null && productBoms.size()>0) {
//            for(BomEO bomItem : productBoms) {
//                ProductOrderEO productOrder = new ProductOrderEO();
//                productOrder.setOrgId(materialPlan.getOrgId());
//                productOrder.setSerialId(materialPlan.getSerialId());
//                productOrder.setMaterialId(bomItem.getMaterialId());
//                productOrder.setElementNo(bomItem.getElementNo());
//                productOrder.setPlanFinishDate(materialPlan.getWeekDate());
//                productOrder.setPlanProduceQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                productOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//                productOrder.setHasProduceQuantity(Double.valueOf(0));
//                productOrder.setHasScheduleQuantity(Double.valueOf(0));
//                productOrder.setProduceStatus(0);
//                productOrder.setScheduleStatus(0);
//                productOrder.setIsChangeConfirm(1);
////                this.productOrderService.save(productOrder);
//                productOrders.add(productOrder);
//            }
//        }
//
//        MaterialEO purchaseMaterial = (MaterialEO) mapObj.get("purchaseMaterial");
//        if(purchaseMaterial != null) { // 需产生采购订单的物料
//            // 采购订单
//            PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
//            purchaseOrder.setType(1);
//            purchaseOrder.setSupplierId(purchaseMaterial.getSupplierId());
//            purchaseOrder.setSerialId(materialPlan.getSerialId());
//            purchaseOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setOrgId(materialPlan.getOrgId());
//            purchaseOrder.setMaterialId(purchaseMaterial.getMaterialId());
//            purchaseOrder.setMaterialName(purchaseMaterial.getMaterialName());
//            purchaseOrder.setSpecification(purchaseMaterial.getSpecification());
//            purchaseOrder.setMaterialCode(purchaseMaterial.getMaterialCode());
//            purchaseOrder.setInventoryCode(purchaseMaterial.getInventoryCode());
//            purchaseOrder.setElementNo(purchaseMaterial.getElementNo());
//            purchaseOrder.setUnitName(purchaseMaterial.getUnitName());
//            purchaseOrder.setCreateDate(new Date());
//            purchaseOrder.setMonthDate(materialPlan.getMonthDate());
//            purchaseOrder.setPlanArriveDate(materialPlan.getWeekDate());
//            purchaseOrder.setPlanDeliveryQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setNotDeliveryQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
//            purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
//            purchaseOrder.setReturnedQuantity(Double.valueOf(0));
//            purchaseOrder.setStatus(0);
//            purchaseOrder.setCreateUserId(user.getUserId());
//            purchaseOrder.setCreateUserName(user.getUserName());
//            purchaseOrder.setIsChangeConfirm(1);
////            this.purchaseOrderService.save(purchaseOrder);
//            purchaseOrders.add(purchaseOrder);
//        }
//
//        MaterialEO outsideMaterial = (MaterialEO) mapObj.get("outsideMaterial");
//        if(outsideMaterial != null) { // 需产生委外订单的物料
//            PurchaseOrderEO outsideOrder = new PurchaseOrderEO();
//            outsideOrder.setType(2);
//            outsideOrder.setSupplierId(outsideMaterial.getSupplierId());
//            outsideOrder.setSerialId(materialPlan.getSerialId());
//            outsideOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//            outsideOrder.setOrgId(materialPlan.getOrgId());
//            outsideOrder.setMaterialId(outsideMaterial.getMaterialId());
//            outsideOrder.setMaterialName(outsideMaterial.getMaterialName());
//            outsideOrder.setSpecification(outsideMaterial.getSpecification());
//            outsideOrder.setMaterialCode(outsideMaterial.getMaterialCode());
//            outsideOrder.setInventoryCode(outsideMaterial.getInventoryCode());
//            outsideOrder.setElementNo(outsideMaterial.getElementNo());
//            outsideOrder.setUnitName(outsideMaterial.getUnitName());
//            outsideOrder.setCreateDate(new Date());
//            outsideOrder.setMonthDate(materialPlan.getMonthDate());
//            outsideOrder.setPlanArriveDate(materialPlan.getWeekDate());
//            outsideOrder.setPlanDeliveryQuantity(materialPlan.getRequireCount());
//            outsideOrder.setNotDeliveryQuantity(materialPlan.getRequireCount());
//            outsideOrder.setActualDeliveryQuantity(Double.valueOf(0));
//            outsideOrder.setQualifiedQuantity(Double.valueOf(0));
//            outsideOrder.setReturnedQuantity(Double.valueOf(0));
//            outsideOrder.setStatus(0);
//            outsideOrder.setCreateUserId(user.getUserId());
//            outsideOrder.setCreateUserName(user.getUserName());
//            outsideOrder.setIsChangeConfirm(1);
////            this.purchaseOrderService.save(outsideOrder);
//            outsideOrders.add(outsideOrder);
//        }
//
//        MaterialEO productMaterial = (MaterialEO) mapObj.get("productMaterial");
//        if(productMaterial != null) { // 需产生生产订单的物料
//            ProductOrderEO productOrder = new ProductOrderEO();
//            productOrder.setOrgId(materialPlan.getOrgId());
//            productOrder.setSerialId(materialPlan.getSerialId());
//            productOrder.setMaterialId(productMaterial.getMaterialId());
//            productOrder.setElementNo(productMaterial.getElementNo());
//            productOrder.setPlanFinishDate(materialPlan.getWeekDate());
//            productOrder.setPlanProduceQuantity(materialPlan.getRequireCount());
//            productOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//            productOrder.setHasProduceQuantity(Double.valueOf(0));
//            productOrder.setHasScheduleQuantity(Double.valueOf(0));
//            productOrder.setProduceStatus(0);
//            productOrder.setScheduleStatus(0);
//            productOrder.setIsChangeConfirm(1);
////            this.productOrderService.save(productOrder);
//            productOrders.add(productOrder);
//        }
//
//        map.put("purchaseOrders", purchaseOrders);
//        map.put("outsideOrders", outsideOrders);
//        map.put("productOrders", productOrders);
//
//        return map;
//    }

    // 生成采购订单，委外订单，生产订单
    private void generateOrders(MaterialPlanEO materialPlan, UserEO user, Map mapObj) {
//        List<BomEO> purchaseBoms = (List<BomEO>) mapObj.get("purchaseBoms"); // 需产生采购订单的Bom
//        if(purchaseBoms!=null && purchaseBoms.size()>0) {
//            for(BomEO bomItem : purchaseBoms) {
//                PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
//                purchaseOrder.setType(1);
//                purchaseOrder.setSupplierId(bomItem.getSupplierId());
//                purchaseOrder.setSerialId(materialPlan.getSerialId());
//                purchaseOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//                purchaseOrder.setOrgId(materialPlan.getOrgId());
//                purchaseOrder.setMaterialId(bomItem.getMaterialId());
//                purchaseOrder.setMaterialName(bomItem.getMaterialName());
//                purchaseOrder.setSpecification(bomItem.getSpecification());
//                purchaseOrder.setMaterialCode(bomItem.getMaterialCode());
//                purchaseOrder.setInventoryCode(bomItem.getInventoryCode());
//                purchaseOrder.setElementNo(bomItem.getElementNo());
//                purchaseOrder.setUnitName(bomItem.getUnitName());
//                purchaseOrder.setCreateDate(new Date());
//                purchaseOrder.setMonthDate(materialPlan.getMonthDate());
//                purchaseOrder.setPlanArriveDate(materialPlan.getWeekDate());
//                purchaseOrder.setPlanDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                purchaseOrder.setNotDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
//                purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
//                purchaseOrder.setReturnedQuantity(Double.valueOf(0));
//                purchaseOrder.setStatus(0);
//                purchaseOrder.setCreateUserId(user.getUserId());
//                purchaseOrder.setCreateUserName(user.getUserName());
//                purchaseOrder.setIsChangeConfirm(1);
//                this.purchaseOrderService.save(purchaseOrder);
//            }
//        }
//
//        List<BomEO> outsideBoms = (List<BomEO>) mapObj.get("outsideBoms");  // 需产生委外订单的Bom
//        if(outsideBoms!=null && outsideBoms.size()>0) {
//            for(BomEO bomItem : outsideBoms) {
//                PurchaseOrderEO outsideOrder = new PurchaseOrderEO();
//                outsideOrder.setType(2);
//                outsideOrder.setSupplierId(bomItem.getSupplierId());
//                outsideOrder.setSerialId(materialPlan.getSerialId());
//                outsideOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//                outsideOrder.setOrgId(materialPlan.getOrgId());
//                outsideOrder.setMaterialId(bomItem.getMaterialId());
//                outsideOrder.setMaterialName(bomItem.getMaterialName());
//                outsideOrder.setSpecification(bomItem.getSpecification());
//                outsideOrder.setMaterialCode(bomItem.getMaterialCode());
//                outsideOrder.setInventoryCode(bomItem.getInventoryCode());
//                outsideOrder.setElementNo(bomItem.getElementNo());
//                outsideOrder.setUnitName(bomItem.getUnitName());
//                outsideOrder.setCreateDate(new Date());
//                outsideOrder.setMonthDate(materialPlan.getMonthDate());
//                outsideOrder.setPlanArriveDate(materialPlan.getWeekDate());
//                outsideOrder.setPlanDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                outsideOrder.setNotDeliveryQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
//                outsideOrder.setActualDeliveryQuantity(Double.valueOf(0));
//                outsideOrder.setQualifiedQuantity(Double.valueOf(0));
//                outsideOrder.setReturnedQuantity(Double.valueOf(0));
//                outsideOrder.setStatus(0);
//                outsideOrder.setCreateUserId(user.getUserId());
//                outsideOrder.setCreateUserName(user.getUserName());
//                outsideOrder.setIsChangeConfirm(1);
//                this.purchaseOrderService.save(outsideOrder);
//            }
//        }

        List<BomEO> productBoms = (List<BomEO>) mapObj.get("productBoms");  // 需产生生产订单的Bom
        if(productBoms!=null && productBoms.size()>0) {
            for(BomEO bomItem : productBoms) {
                WeekProductOrderEO productOrder = new WeekProductOrderEO();
                productOrder.setOrgId(materialPlan.getOrgId());
                productOrder.setSerialId(materialPlan.getSerialId());
                productOrder.setMaterialId(bomItem.getMaterialId());
                productOrder.setElementNo(bomItem.getElementNo());
                productOrder.setPlanFinishDate(materialPlan.getWeekDate());
                productOrder.setPlanProduceQuantity(bomItem.getAmount() * materialPlan.getRequireCount());
                productOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
                productOrder.setHasProduceQuantity(Double.valueOf(0));
                productOrder.setHasScheduleQuantity(Double.valueOf(0));
                productOrder.setProduceStatus(0);
                productOrder.setScheduleStatus(0);
                productOrder.setIsChangeConfirm(1);
                this.weekproductOrderService.save(productOrder);
            }
        }

//        MaterialEO purchaseMaterial = (MaterialEO) mapObj.get("purchaseMaterial");
//        if(purchaseMaterial != null) { // 需产生采购订单的物料
//            // 采购订单
//            PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
//            purchaseOrder.setType(1);
//            purchaseOrder.setSupplierId(purchaseMaterial.getSupplierId());
//            purchaseOrder.setSerialId(materialPlan.getSerialId());
//            purchaseOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setOrgId(materialPlan.getOrgId());
//            purchaseOrder.setMaterialId(purchaseMaterial.getMaterialId());
//            purchaseOrder.setMaterialName(purchaseMaterial.getMaterialName());
//            purchaseOrder.setSpecification(purchaseMaterial.getSpecification());
//            purchaseOrder.setMaterialCode(purchaseMaterial.getMaterialCode());
//            purchaseOrder.setInventoryCode(purchaseMaterial.getInventoryCode());
//            purchaseOrder.setElementNo(purchaseMaterial.getElementNo());
//            purchaseOrder.setUnitName(purchaseMaterial.getUnitName());
//            purchaseOrder.setCreateDate(new Date());
//            purchaseOrder.setMonthDate(materialPlan.getMonthDate());
//            purchaseOrder.setPlanArriveDate(materialPlan.getWeekDate());
//            purchaseOrder.setPlanDeliveryQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setNotDeliveryQuantity(materialPlan.getRequireCount());
//            purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
//            purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
//            purchaseOrder.setReturnedQuantity(Double.valueOf(0));
//            purchaseOrder.setStatus(0);
//            purchaseOrder.setCreateUserId(user.getUserId());
//            purchaseOrder.setCreateUserName(user.getUserName());
//            purchaseOrder.setIsChangeConfirm(1);
//            this.purchaseOrderService.save(purchaseOrder);
//        }
//
//        MaterialEO outsideMaterial = (MaterialEO) mapObj.get("outsideMaterial");
//        if(outsideMaterial != null) { // 需产生委外订单的物料
//            PurchaseOrderEO outsideOrder = new PurchaseOrderEO();
//            outsideOrder.setType(2);
//            outsideOrder.setSupplierId(outsideMaterial.getSupplierId());
//            outsideOrder.setSerialId(materialPlan.getSerialId());
//            outsideOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
//            outsideOrder.setOrgId(materialPlan.getOrgId());
//            outsideOrder.setMaterialId(outsideMaterial.getMaterialId());
//            outsideOrder.setMaterialName(outsideMaterial.getMaterialName());
//            outsideOrder.setSpecification(outsideMaterial.getSpecification());
//            outsideOrder.setMaterialCode(outsideMaterial.getMaterialCode());
//            outsideOrder.setInventoryCode(outsideMaterial.getInventoryCode());
//            outsideOrder.setElementNo(outsideMaterial.getElementNo());
//            outsideOrder.setUnitName(outsideMaterial.getUnitName());
//            outsideOrder.setCreateDate(new Date());
//            outsideOrder.setMonthDate(materialPlan.getMonthDate());
//            outsideOrder.setPlanArriveDate(materialPlan.getWeekDate());
//            outsideOrder.setPlanDeliveryQuantity(materialPlan.getRequireCount());
//            outsideOrder.setNotDeliveryQuantity(materialPlan.getRequireCount());
//            outsideOrder.setActualDeliveryQuantity(Double.valueOf(0));
//            outsideOrder.setQualifiedQuantity(Double.valueOf(0));
//            outsideOrder.setReturnedQuantity(Double.valueOf(0));
//            outsideOrder.setStatus(0);
//            outsideOrder.setCreateUserId(user.getUserId());
//            outsideOrder.setCreateUserName(user.getUserName());
//            outsideOrder.setIsChangeConfirm(1);
//            this.purchaseOrderService.save(outsideOrder);
//        }

        MaterialEO productMaterial = (MaterialEO) mapObj.get("productMaterial");
        if(productMaterial != null) { // 需产生生产订单的物料
            WeekProductOrderEO productOrder = new WeekProductOrderEO();
            productOrder.setOrgId(materialPlan.getOrgId());
            productOrder.setSerialId(materialPlan.getSerialId());
            productOrder.setMaterialId(productMaterial.getMaterialId());
            productOrder.setElementNo(productMaterial.getElementNo());
            productOrder.setPlanFinishDate(materialPlan.getWeekDate());
            productOrder.setPlanProduceQuantity(materialPlan.getRequireCount());
            productOrder.setPlanRequireQuantity(materialPlan.getRequireCount());
            productOrder.setHasProduceQuantity(Double.valueOf(0));
            productOrder.setHasScheduleQuantity(Double.valueOf(0));
            productOrder.setProduceStatus(0);
            productOrder.setScheduleStatus(0);
            productOrder.setIsChangeConfirm(1);
            this.weekproductOrderService.save(productOrder);
        }
    }

    /**
     *   发布客户需求周计划
     * 一. Bom存在：
     *  1. 若Bom节点   是采购，非自制
     *    不拆分Bom，生成节点本身采购订单
     *    若Bom零件号为-W结尾，再去查找对应的物料关系零件并生成委外订单
     *  2. 若Bom节点   是自制，非采购
     *    拆分Bom，生成节点本身生产订单
     *    若Bom零件号为-W结尾，再去查找对应的物料关系零件并生成委外订单
     *  3. 若Bom节点   非自制，非采购
     *    拆分Bom但不对此Bom节点生成任何订单，只对此Bom拆分后得到的Bom数据进行上述1,2,3的逻辑。
     *  4. 若Bom节点   是自制，是采购
     *    提示
     *
     * 二. Bom不存在：
     *  1. 若对应物料  是采购，非自制
     *    生成对应物料采购订单*    对应物料零件号为-W结尾，再去查找对应的物料关系零件并生成委外订单
     *  2. 若对应物料  是自制，非采购
     *    生成对应物料生产订单
     *    对应物料零件号为-W结尾，再去查找对应的物料关系零件并生成委外订单
     *  3. 若对应物料   非自制，非采购
     *    提示
     *  4. 若对应物料   是自制，是采购
     *    提示
     * */
    @Transactional(rollbackFor = Exception.class)
    public Result releaseWeek(Long[] serialIds, UserEO user) throws BusinessException {
        Result result = new Result();

        // 是否需要查询物料关系表
        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
        JSONObject bomJsonObject = jsonObject.getJSONObject("bom");
        boolean isQueryMaterialRelationship = bomJsonObject.getBoolean("isQueryMaterialRelationship");

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        } else {
            throw new BusinessException("请选择数据!");
        }

        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        String errorMsg = "";
        String resultMsg = "";

        // 查询选中的主信息
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        // 判断主信息是否是新建数据
        if(materialPlans!=null && materialPlans.size()>0) {
            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getStatus() != 0) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不是新建状态!<br/>");
                }
            }
        }
        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
            return result;
        }

        // 查询选中的矩阵信息
        List<MaterialPlanEO> list = this.baseMapper.getByParentSerialIds(sqlStr);
        if(list!=null && list.size()>0) {
            for(MaterialPlanEO materialPlan : list) {
                if(materialPlan.getStatus() != 0) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                            "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]不是新建状态!<br/>");
                }
            }
        }
        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
            return result;
        }

        // 获取需要生成采购订单，委外订单，生产订单的数据
        Map map = new HashedMap();
        if(materialPlans!=null && materialPlans.size()>0) {
            for(MaterialPlanEO materialPlan : materialPlans) {
                Long tempMaterialId = materialPlan.getTempMaterialId();
                // 判断周计划的物料是否存在
                if(tempMaterialId == null) {
                    errorMsg += ("物料[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不存在!<br/>");
                } else {
                    List<BomEO> boms = this.bomService.getByMaterialId(tempMaterialId);
                    // 查询物料对应的Bom是否存在
                    if(boms!=null && boms.size()>0) {
                        //Bom存在则取一个Bom(分总成Bom会重复)拆解
                        Map bomsMap = this.bomService.getAllBomsForReleaseWeekPlan(boms.get(0), isQueryMaterialRelationship);
                        errorMsg += bomsMap.get("errorMsg");
                        resultMsg += bomsMap.get("resultMsg");
                        map.put(""+materialPlan.getSerialId(), bomsMap);
                    } else {
                        // Bom不存在则取物料档案
                        Map materialMap = this.materialService.getAllMaterialsForReleaseWeekPlan(tempMaterialId, isQueryMaterialRelationship, materialPlan.getMaterialName(), materialPlan.getElementNo());
                        errorMsg += materialMap.get("errorMsg");
                        resultMsg += materialMap.get("resultMsg");
                        map.put(""+materialPlan.getSerialId(), materialMap);
                    }
                }
            }

            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            } else {
                if(!"".equals(resultMsg)) {
                    result.setMsg(resultMsg);
                }
            }

//            Map newMap = new HashedMap();
//            newMap.put("purchaseOrders", new ArrayList<>());
//            newMap.put("outsideOrders", new ArrayList<>());
//            newMap.put("productOrders", new ArrayList<>());
            if(list!=null && list.size()>0) {
                for(MaterialPlanEO materialPlan : list) {
                    Map mapObj = (Map) map.get(""+materialPlan.getParentSerialId());
//                    newMap = generateOrders(materialPlan, user, mapObj, newMap);
                    generateOrders(materialPlan, user, mapObj);
                }
//                result.setData(newMap);
            }
        } else {
            throw new BusinessException("选择的数据不存在,请刷新!");
        }

        // 更新客户需求周计划状态
        this.updateStatusByIds(sqlStr, 1);
        this.updateStatusByParentSerialIds(sqlStr, 1);

        return result;
    }

    public void insertOrders(List<PurchaseOrderEO> purchaseOrders, List<PurchaseOrderEO> outsideOrders, List<ProductOrderEO> productOrders, Long[] serialIds) {
        if(purchaseOrders!=null && purchaseOrders.size()>0) {
            for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
                this.purchaseOrderService.save(purchaseOrder);
            }
        }

        if(outsideOrders!=null && outsideOrders.size()>0) {
            for(PurchaseOrderEO outsideOrder : outsideOrders) {
                this.purchaseOrderService.save(outsideOrder);
            }
        }

        if(productOrders!=null && productOrders.size()>0) {
            for(ProductOrderEO productOrder : productOrders) {
                this.productOrderService.save(productOrder);
            }
        }

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        }

        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        this.updateStatusByIds(sqlStr, 1);
        this.updateStatusByParentSerialIds(sqlStr, 1);
    }


    public Result getReleaseOrderCount(Long[] serialIds) {
        Result result = new Result();

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        }
        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        String sqlStr1 = "(";
        List<MaterialPlanEO> list = this.baseMapper.getByParentSerialIds(sqlStr);
        if(list!=null && list.size()>0) {
            for(MaterialPlanEO materialPlan : list) {
                sqlStr1 += (materialPlan.getSerialId() + ",");
            }
        }
        if("(".equals(sqlStr1)) {
            sqlStr1 += ")";
        } else {
            sqlStr1 = sqlStr1.substring(0, sqlStr1.length()-1) +")";
        }

        List<PurchaseOrderEO> purchaseOrders = this.purchaseOrderService.getBySerialIds(sqlStr1);
        List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr1);

        result.setData((purchaseOrders==null?0:purchaseOrders.size()) + (productOrders==null?0:productOrders.size()));

        return result;
    }

    /**
     *  客户需求周计划取消发布
     * */
    @Transactional(rollbackFor = Exception.class)
    public Result cancelReleaseWeek(Long[] serialIds) throws BusinessException {
        Result result = new Result();

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        } else {
            throw new BusinessException("请选择数据!");
        }

        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        String errorMsg = "";
        // 查询选中的主信息
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        // 判断主信息是否是新建数据
        if(materialPlans!=null && materialPlans.size()>0) {
            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getStatus() != 1) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不是已发布状态!<br/>");
                }
            }
        }

        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
            return result;
        }

        // 查询选中的矩阵信息
        String sqlStr1 = "(";
        List<MaterialPlanEO> list = this.baseMapper.getByParentSerialIds(sqlStr);
        if(list!=null && list.size()>0) {
            for(MaterialPlanEO materialPlan : list) {
                sqlStr1 +=  materialPlan.getSerialId() + ",";
                if(materialPlan.getStatus() != 1) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                            "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]不是已发布状态!<br/>");
                }
            }
        }

        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
            return result;
        }

        if("(".equals(sqlStr1)) {
            sqlStr1 += ")";
        } else {
            sqlStr1 = sqlStr1.substring(0, sqlStr1.length()-1) +")";
        }

        // 获取经客户需求周计划发布得到的采购订单和委外订单(非新建状态)
        Set hasRelease1 = new HashSet();
        Set willDelete1 = new HashSet();
        List<PurchaseOrderEO> purchaseOrOutsideOrders = this.purchaseOrderService.getBySerialIds(sqlStr1);
        if(purchaseOrOutsideOrders!=null && purchaseOrOutsideOrders.size()>0) {
            for(PurchaseOrderEO purchaseOrOutsideOrder : purchaseOrOutsideOrders) {
                if(purchaseOrOutsideOrder.getStatus() != 0) {
                    hasRelease1.add(purchaseOrOutsideOrder.getParentSerialId());
                }
                willDelete1.add(purchaseOrOutsideOrder.getPurchaseOrderId());
            }
        }
        // 提示哪些客户需求周计划的采购订单或委外订单非新建状态
        if(hasRelease1!=null && hasRelease1.size()>0) {
            for(Object item : hasRelease1){
                Long parentSerialId = (Long) item;
                if(materialPlans!=null && materialPlans.size()>0) {
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(parentSerialId.longValue() == materialPlan.getSerialId().longValue()) {
                            errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]生成的采购订单或委外订单存在非新建状态数据!<br/>");
                        }
                    }
                }
            }
        }

        // 获取经客户需求周计划发布得到的生产订单(非新建状态)
        Set hasRelease2 = new HashSet();
        Set willDelete2 = new HashSet();
        List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr1);
        if(productOrders!=null && productOrders.size()>0) {
            for(ProductOrderEO productOrder : productOrders) {
                if(productOrder.getProduceStatus() != 0) {
                    hasRelease2.add(productOrder.getParentSerialId());
                }
                willDelete2.add(productOrder.getProductOrderId());
            }
        }
        // 提示哪些客户需求周计划的生产订单非新建状态
        if(hasRelease2!=null && hasRelease2.size()>0) {
            for(Object item : hasRelease2){
                Long parentSerialId = (Long) item;
                if(materialPlans!=null && materialPlans.size()>0) {
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(parentSerialId.longValue() == materialPlan.getSerialId().longValue()) {
                            errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]生成的生产订单存在非新建状态数据!<br/>");
                        }
                    }
                }
            }
        }

        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
        } else {
            // 删除生成的采购订单和委外订单
            String deleteSqlStr1 = "(";
            if(willDelete1!=null && willDelete1.size()>0) {
                for(Object item : willDelete1){
                    Long purchaseOrderId = (Long) item;
                    deleteSqlStr1 += (purchaseOrderId + ",");
                }
            }
            if(!"(".equals(deleteSqlStr1)) {
                deleteSqlStr1 = deleteSqlStr1.substring(0, deleteSqlStr1.length()-1) +")";
                this.purchaseOrderService.deleteByPurchaseOrderIds(deleteSqlStr1);
            }

            // 删除生成的生产订单
            String deleteSqlStr2 = "(";
            if(willDelete2!=null && willDelete2.size()>0) {
                for(Object item : willDelete2){
                    Long productOrderId = (Long) item;
                    deleteSqlStr2 += (productOrderId + ",");
                }
            }
            if(!"(".equals(deleteSqlStr2)) {
                deleteSqlStr2 = deleteSqlStr2.substring(0, deleteSqlStr2.length()-1) +")";
                this.productOrderService.deleteByProductOrderIds(deleteSqlStr2);
            }

            // 更新客户需求周计划状态
            this.updateStatusByIds(sqlStr, 0);
            this.updateStatusByParentSerialIds(sqlStr, 0);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result releaseWeekSon(Long[] serialIds, UserEO user) throws BusinessException {
        Result result = new Result();

        // 是否需要查询物料关系表
        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
        JSONObject bomJsonObject = jsonObject.getJSONObject("bom");
        boolean isQueryMaterialRelationship = bomJsonObject.getBoolean("isQueryMaterialRelationship");

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        } else {
            throw new BusinessException("请选择数据!");
        }

        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        String errorMsg = "";

        // 查询选中的信息
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        // 判断选中的信息是否是新建状态数据
        if(materialPlans!=null && materialPlans.size()>0) {
            // 查询选中的信息的父数据是否为新建或已发布状态
            List<MaterialPlanEO> parentMaterialPlans = this.baseMapper.getBySerialIds("(" + materialPlans.get(0).getParentSerialId()+")");
            for(MaterialPlanEO materialPlan : parentMaterialPlans) {
                if(materialPlan.getStatus() > 1) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不是新建或已发布状态!<br/>");
                }
            }
            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }

            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getStatus() != 0) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                            "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]不是新建状态!<br/>");
                }
            }
            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }
        }

        // 获取需要生成采购订单，委外订单，生产订单的数据
        if(materialPlans!=null && materialPlans.size()>0) {
            Map map = new HashedMap();
            MaterialPlanEO materialPlan = materialPlans.get(0);
            Long tempMaterialId = materialPlan.getTempMaterialId();
            // 判断周计划的物料是否存在
            if(tempMaterialId == null) {
                errorMsg += ("物料[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不存在!<br/>");
            } else {
                List<BomEO> boms = this.bomService.getByMaterialId(tempMaterialId);
                // 查询物料对应的Bom是否存在
                if(boms!=null && boms.size()>0) {
                    //Bom存在则取一个Bom(分总成Bom会重复)拆解
                    Map bomsMap = this.bomService.getAllBomsForReleaseWeekPlan(boms.get(0), isQueryMaterialRelationship);
                    errorMsg += bomsMap.get("errorMsg");
                    map.put(""+materialPlan.getParentSerialId(), bomsMap);
                } else {
                    // Bom不存在则取物料档案
                    Map materialMap = this.materialService.getAllMaterialsForReleaseWeekPlan(materialPlan.getMaterialId(), isQueryMaterialRelationship, materialPlan.getMaterialName(), materialPlan.getElementNo());
                    errorMsg += materialMap.get("errorMsg");
                    map.put(""+materialPlan.getParentSerialId(), materialMap);
                }
            }

            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }

            Map mapObj = (Map) map.get(""+materialPlan.getParentSerialId());
            for(MaterialPlanEO item : materialPlans) {
                generateOrders(item, user, mapObj);
            }
        } else {
            throw new BusinessException("选择的数据不存在,请刷新!");
        }

        // 更新客户需求周计划状态
        this.updateStatusByIds(sqlStr, 1);
        this.updateStatusByIds("(" + materialPlans.get(0).getParentSerialId()+")", 1);

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result cancelReleaseWeekSon(Long[] serialIds) throws BusinessException {
        Result result = new Result();

        String sqlStr= "(";
        if(serialIds!=null && serialIds.length>0) {
            for(Long serialId : serialIds){
                sqlStr += (serialId + ",");
            }
        } else {
            throw new BusinessException("请选择数据!");
        }

        if("(".equals(sqlStr)) {
            sqlStr += ")";
        } else {
            sqlStr = sqlStr.substring(0, sqlStr.length()-1) +")";
        }

        String errorMsg = "";

        // 查询选中的信息
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        if(materialPlans!=null && materialPlans.size()>0) {
            // 查询选中的信息的父数据是否为已发布状态
            List<MaterialPlanEO> parentMaterialPlans = this.baseMapper.getBySerialIds("(" + materialPlans.get(0).getParentSerialId()+")");
            for(MaterialPlanEO materialPlan : parentMaterialPlans) {
                if(materialPlan.getStatus() != 1) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() + "]不是已发布状态!<br/>");
                }
            }
            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }

            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getStatus() != 1) {
                    errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                            "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]不是已发布状态!<br/>");
                }
            }
            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }
        }

        // 获取经客户需求周计划发布得到的采购订单和委外订单(非新建状态)
        Set hasRelease1 = new HashSet();
        Set willDelete1 = new HashSet();
        List<PurchaseOrderEO> purchaseOrOutsideOrders = this.purchaseOrderService.getBySerialIds(sqlStr);
        if(purchaseOrOutsideOrders!=null && purchaseOrOutsideOrders.size()>0) {
            for(PurchaseOrderEO purchaseOrOutsideOrder : purchaseOrOutsideOrders) {
                if(purchaseOrOutsideOrder.getStatus() != 0) {
                    hasRelease1.add(purchaseOrOutsideOrder.getSerialId());
                }
                willDelete1.add(purchaseOrOutsideOrder.getPurchaseOrderId());
            }
        }
        // 提示哪些客户需求周计划的采购订单或委外订单非新建状态
        if(hasRelease1!=null && hasRelease1.size()>0) {
            for(Object item : hasRelease1){
                Long serialId = (Long) item;
                if(materialPlans!=null && materialPlans.size()>0) {
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(serialId.longValue() == materialPlan.getSerialId().longValue()) {
                            errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                                    "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]生成的采购订单或委外订单存在非新建状态数据!<br/>");
                        }
                    }
                }
            }
        }

        // 获取经客户需求周计划发布得到的生产订单(非新建状态)
        Set hasRelease2 = new HashSet();
        Set willDelete2 = new HashSet();
        List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr);
        if(productOrders!=null && productOrders.size()>0) {
            for(ProductOrderEO productOrder : productOrders) {
                if(productOrder.getProduceStatus() != 0) {
                    hasRelease2.add(productOrder.getSerialId());
                }
                willDelete2.add(productOrder.getProductOrderId());
            }
        }
        // 提示哪些客户需求周计划的生产订单非新建状态
        if(hasRelease2!=null && hasRelease2.size()>0) {
            for(Object item : hasRelease2){
                Long serialId = (Long) item;
                if(materialPlans!=null && materialPlans.size()>0) {
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(serialId.longValue() == materialPlan.getSerialId().longValue()) {
                            errorMsg += ("客户需求周计划[" + materialPlan.getMaterialName() + " " + materialPlan.getElementNo() +
                                    "]中[" + DateUtils.format(materialPlan.getWeekDate(),"yyyy-MM-dd") + "]生成的生产订单存在非新建状态数据!<br/>");
                        }
                    }
                }
            }
        }

        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
        } else {
            // 删除生成的采购订单和委外订单
            String deleteSqlStr1 = "(";
            if(willDelete1!=null && willDelete1.size()>0) {
                for(Object item : willDelete1){
                    Long purchaseOrderId = (Long) item;
                    deleteSqlStr1 += (purchaseOrderId + ",");
                }
            }
            if(!"(".equals(deleteSqlStr1)) {
                deleteSqlStr1 = deleteSqlStr1.substring(0, deleteSqlStr1.length()-1) +")";
                this.purchaseOrderService.deleteByPurchaseOrderIds(deleteSqlStr1);
            }

            // 删除生成的生产订单
            String deleteSqlStr2 = "(";
            if(willDelete2!=null && willDelete2.size()>0) {
                for(Object item : willDelete2){
                    Long productOrderId = (Long) item;
                    deleteSqlStr2 += (productOrderId + ",");
                }
            }
            if(!"(".equals(deleteSqlStr2)) {
                deleteSqlStr2 = deleteSqlStr2.substring(0, deleteSqlStr2.length()-1) +")";
                this.productOrderService.deleteByProductOrderIds(deleteSqlStr2);
            }

            // 更新客户需求周计划状态
            this.updateStatusByIds(sqlStr, 0);

            // 获取选中的信息的主列表下的所有数据，判断数据是否全部为新建状态，若是，则修改主列表数据状态为新建
            List<MaterialPlanEO> allMaterialPlans = this.baseMapper.getByParentSerialIds("(" + materialPlans.get(0).getParentSerialId() + ")");
            boolean flag = true;
            if(allMaterialPlans!=null && allMaterialPlans.size()>0) {
                for(MaterialPlanEO materialPlan : allMaterialPlans) {
                    if(materialPlan.getStatus() != 0) {
                        flag = false;
                        break;
                    }
                }
            }

            if(flag) {
                this.updateStatusByIds("(" + materialPlans.get(0).getParentSerialId() + ")", 0);
            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result changeRequireCount(MaterialPlanEO entity) throws BusinessException {
        Result result = new Result();

        List<MaterialPlanEO> list = this.baseMapper.getBySerialIds("(" + entity.getSerialId() + ")");
        if(list==null || list.size()==0) {
            throw new BusinessException("选择数据已不存在，请刷新!");
        }

        MaterialPlanEO materialPlan = list.get(0);
        if(entity.getRequireCount().doubleValue() == materialPlan.getRequireCount().doubleValue()) {
            throw new BusinessException("修改的数量值和原数量值相同，无需变更!");
        }

        // 获取修改数量的下限值，下限值不小于生成的采购订单，委外订单的实际送货数量且不小于生产订单的已生产数量
        String sqlStr = "(" + entity.getSerialId() + ")";
        Double minChangeQutity = Double.valueOf(0);
        List<PurchaseOrderEO> purchaseOrOutsideOrders = this.purchaseOrderService.getBySerialIds(sqlStr);
        if(purchaseOrOutsideOrders!=null && purchaseOrOutsideOrders.size()>0) {
            for(PurchaseOrderEO purchaseOrder : purchaseOrOutsideOrders) {
                Double bomOrMaterialAmount = purchaseOrder.getPlanDeliveryQuantity()/purchaseOrder.getPlanRequireQuantity();
                if(purchaseOrder.getActualDeliveryQuantity() > (minChangeQutity*bomOrMaterialAmount)) {
                    minChangeQutity = Math.ceil(purchaseOrder.getActualDeliveryQuantity()/bomOrMaterialAmount);
                }
            }
        }
        List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr);
        if(productOrders!=null && productOrders.size()>0) {
            for(ProductOrderEO productOrder : productOrders) {
                Double bomOrMaterialAmount = productOrder.getPlanProduceQuantity()/productOrder.getPlanRequireQuantity();
                if(productOrder.getHasProduceQuantity() > (minChangeQutity*bomOrMaterialAmount)) {
                    minChangeQutity = Math.ceil(productOrder.getHasProduceQuantity()/bomOrMaterialAmount);
                }
            }
        }

        if(minChangeQutity.doubleValue()!=Double.valueOf(0) && entity.getRequireCount()>minChangeQutity) {
            throw new BusinessException("数量不得超过" + minChangeQutity + "!");
        } else {
            entity.setPreRequireCount(materialPlan.getRequireCount());
            entity.setIsChange(1);
            entity.setLastChangeTime(new Date());
            if(materialPlan.getVersion() == null) {
                materialPlan.setVersion(1);
            }
            entity.setVersion(materialPlan.getVersion() + 1);
            entity.setPlanVersion(materialPlan.getPlanVersion() + 0.1);
            String changeInfo = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") +
                    " : 需求数量由 " + materialPlan.getRequireCount() + " 变更为 " + entity.getRequireCount();
            entity.setChangeInfo(changeInfo);
            this.updateById(entity);
            // 记录日志
            LogUtils.saveLog(changeInfo, "修改数据", "MaterialPlanEO", entity.getChangeInfo(), changeInfo, entity.getOrgId(), entity.getSerialId());

            if(purchaseOrOutsideOrders!=null && purchaseOrOutsideOrders.size()>0) {
                for(PurchaseOrderEO purchaseOrder : purchaseOrOutsideOrders) {
                    if(purchaseOrder.getStatus() < 2) { // 已完成或已关闭状态的不能改变数量
                        Double bomOrMaterialAmount = purchaseOrder.getPlanDeliveryQuantity()/purchaseOrder.getPlanRequireQuantity();
                        purchaseOrder.setPlanDeliveryQuantity(entity.getRequireCount() * bomOrMaterialAmount);
                        purchaseOrder.setNotDeliveryQuantity(entity.getRequireCount() * bomOrMaterialAmount);
                        purchaseOrder.setPlanRequireQuantity(entity.getRequireCount());
                        purchaseOrder.setIsChangeConfirm(0);
                        if(purchaseOrder.getConfirmVersion() == null) {
                            purchaseOrder.setConfirmVersion(0);
                        }
                        purchaseOrder.setConfirmVersion(purchaseOrder.getConfirmVersion() + 1);
                    }
                }
                this.purchaseOrderService.saveOrUpdateBatch(purchaseOrOutsideOrders);
            }
            if(productOrders!=null && productOrders.size()>0) {
                for(ProductOrderEO productOrder : productOrders) {
                    if(productOrder.getProduceStatus() < 2) { // 已完成状态的不能改变数量
                        Double bomOrMaterialAmount = productOrder.getPlanProduceQuantity()/productOrder.getPlanRequireQuantity();
                        productOrder.setPlanProduceQuantity(entity.getRequireCount() * bomOrMaterialAmount);
                        productOrder.setPlanRequireQuantity(entity.getRequireCount());
                        productOrder.setIsChangeConfirm(0);
                        if(productOrder.getConfirmVersion() == null) {
                            productOrder.setConfirmVersion(0);
                        }
                        productOrder.setConfirmVersion(productOrder.getConfirmVersion() + 1);
                    }
                }
                this.productOrderService.saveOrUpdateBatch(productOrders);
            }
        }

        return result;
    }

    public void confirmChange(Set serialIds, UserEO user) {
        // 周计划的变更状态计算逻辑:
        // 首先判断周计划的是否变更字段,如果为0则跳过,
        // 如果为1,则判断分解出的采购/委外/生产订单是否都已经确认了,如果都确认了,则把是否变更字段设置为0,变更详情字段设置为空，
        // 如果还有单据未确认,则统计出未确认的数量,比如采购订单未确认:xxx,委外订单未确认:xxx,生产订单未确认:xxx,写入变更详情字段
        String sqlStr = "";
        if(serialIds!=null && serialIds.size()>0) {
            for(Object obj : serialIds) {
                sqlStr += ((Long) obj + ",");
            }

            if(!"".equals(sqlStr)) {
                sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
            }

            sqlStr = "(" + sqlStr + ")";
        }
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        Set serialIds1 = new HashSet();
        if(materialPlans!=null && materialPlans.size()>0) {
            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getIsChange().intValue() == 1) {
                    serialIds1.add(materialPlan.getSerialId());
                }
            }
        }

        String sqlStr1 = "";
        if(serialIds1!=null && serialIds1.size()>0) {
            for(Object obj : serialIds) {
                sqlStr1 += ((Long) obj + ",");
            }

            if(!"".equals(sqlStr1)) {
                sqlStr1 = sqlStr1.substring(0, sqlStr1.length() - 1);
            }

            sqlStr1 = "(" + sqlStr1 + ")";

            List<PurchaseOrderEO> purchaseOrders = this.purchaseOrderService.getBySerialIds(sqlStr1);

            int notConfirmPurchaseCount = 0;
            int notConfirmOutsideCount = 0;
            int notConfirmProductCount = 0;
            Map map = new HashedMap();

            if(purchaseOrders!=null && purchaseOrders.size()>0) {
                for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
                    if(purchaseOrder.getIsChangeConfirm().intValue() == 0) {
                        if(purchaseOrder.getType().intValue() == 1) {
                            notConfirmPurchaseCount += 1;
                        } else if(purchaseOrder.getType().intValue() == 2) {
                            notConfirmOutsideCount += 1;
                        }
                    }
                    map.put("purchase-"+purchaseOrder.getSerialId(), notConfirmPurchaseCount);
                    map.put("outside-"+purchaseOrder.getSerialId(), notConfirmOutsideCount);
                }
            }

            List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr1);
            if(productOrders!=null && productOrders.size()>0) {
                for(ProductOrderEO productOrder : productOrders) {
                    if(productOrder.getIsChangeConfirm().intValue() == 0) {
                        notConfirmProductCount += 1;
                    }
                    map.put("product-"+productOrder.getSerialId(), notConfirmProductCount);
                }
            }

            List<MaterialPlanEO> list = new ArrayList();
            if(map!=null && map.size()>0) {
                for(Object obj : map.keySet()) {
                    String msg = "";
                    Long serialId = Long.valueOf(obj.toString().split("-")[1]);
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(materialPlan.getSerialId().longValue() == serialId.longValue()) {
                            if(((Integer) map.get("purchase-" + serialId)).intValue() != 0) {
                                msg += ("采购订单未确认: " + (String) map.get("purchase-" + serialId) + "条,");
                            }
                            if(((Integer) map.get("outside-" + serialId)).intValue() != 0) {
                                msg += ("委外订单未确认: " + (String) map.get("outside-" + serialId) + "条,");
                            }
                            if(((Integer) map.get("product-" + serialId)).intValue() != 0) {
                                msg += ("生产订单未确认: " + (String) map.get("product-" + serialId) + "条,");
                            }

                            if(!"".equals(msg)) {
                                materialPlan.setChangeInfo(msg);
                            } else {
                                materialPlan.setChangeInfo(null);
                                materialPlan.setIsChange(0);
                            }
                            list.add(materialPlan);
                        }
                    }
                }

                if(list!=null && list.size()>0) {
                    this.saveOrUpdateBatch(list);
                    for(MaterialPlanEO materialPlan : list) {
                        // 记录日志
                        LogUtils.saveLog(user.getUserName()+"已确认周计划变更: " +
                                        "确认时间: " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "," +
                                        "周计划版本号 " + materialPlan.getVersion()==null?"1":materialPlan.getVersion() + "," +
                                        "周计划新需求数量: " + materialPlan.getRequireCount(),
                                "修改数据", "MaterialPlanEO",
                                materialPlan.getPreRequireCount()==null?"":materialPlan.getRequireCount()+"",
                                materialPlan.getRequireCount()+"", materialPlan.getOrgId(), materialPlan.getSerialId());
                    }
                }
            }
        }
    }


    public Map criteriaToMap(Criteria criteria) {
        Map<String, Object> map = new HashedMap();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        if(criterions!=null && criterions.size()>0){
            for (Criterion criterion : criterions) {
                if (null!=criterion.getValue() && !"".equals(criterion.getValue())) {
                    map.put(criterion.getField().substring(criterion.getField().indexOf(".")+1), criterion.getValue());
                }
            }
        }
        map.put("currentPage", criteria.getCurrentPage());
        map.put("pageSize", criteria.getSize());
        map.put("currentIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        map.put("order", criteria.getOrder());
        map.put("orderField", criteria.getOrderField().substring(criteria.getOrderField().indexOf(".")+1));
        return map;
    }



    public List<DeliveryOrderDetailEO> selectCustomerMonthReport(Criteria criteria) throws BusinessException{
        List<DeliveryOrderDetailEO> pageList = new ArrayList<>();
        List<DeliveryOrderDetailEO> tempList = new ArrayList<>();
        List<DeliveryOrderDetailEO> returnList = new ArrayList<>();
        int current = criteria.getCurrentPage();
        int size = criteria.getSize();
        Map mapCondition = criteriaToMap(criteria);
//        Calendar calendar = Calendar.getInstance();
        pageList = this.baseMapper.selectCustomerMonthReport(mapCondition);

        Map<Object,DeliveryOrderDetailEO> map = new LinkedHashMap();
        Date monthDateStart = DateUtils.stringToDate((String) mapCondition.get("monthDateStart"), "yyyy-MM-dd");

        if(null != pageList && pageList.size() > 0){
            for(DeliveryOrderDetailEO detailEO:pageList){
                Date currentDay = detailEO.getCurrentDay();
                int day = DateUtils.getDayBetween(monthDateStart, currentDay).intValue() + 1;
                if(null != map.get(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getCustomerId())){
                    //若存在则更新已存在数据的需求数
//                    calendar.setTime(detailEO.getCurrentDay());
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    setCurrentDay(map.get(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getCustomerId()),detailEO,day);
                }else{
//                    calendar.setTime(detailEO.getCurrentDay());
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    detailEO.setTotalActualReceiveQuantity(0d);
                    detailEO.setTotalDeliveryQuantity(0d);
                    detailEO.setTotalNotQualifiedQuantity(0d);
                    setCurrentDay(detailEO,detailEO,day);
                    map.put(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getCustomerId(),detailEO);
                }
            }
        }

        for(Object key : map.keySet()){
            tempList.add(map.get(key));
        }

        Integer countTotal = tempList.size();

        for(int i = size*(current -1);i<size*current;i++){
            if(i < countTotal){
                tempList.get(i).setTotalCount(countTotal);
                returnList.add(tempList.get(i));
            }
        }

        return returnList;
    }

    public void setCurrentDay(DeliveryOrderDetailEO entity,DeliveryOrderDetailEO temp,int day){

        //交付总数
        if (null != temp.getActualReceiveQuantity()){
            entity.setTotalActualReceiveQuantity(entity.getTotalActualReceiveQuantity() + temp.getActualReceiveQuantity());
        }
        //订单总数
        if (null != temp.getDeliveryQuantity()){
            entity.setTotalDeliveryQuantity(entity.getTotalDeliveryQuantity() + temp.getDeliveryQuantity());
        }
        //不良总数
        if (null != temp.getNotQualifiedQuantity()){
            entity.setTotalNotQualifiedQuantity(entity.getTotalNotQualifiedQuantity() + temp.getNotQualifiedQuantity());
        }

        //1号
        if(day == 1){
            entity.setDeliveryQuantity01(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity01(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity01(temp.getNotQualifiedQuantity());
        }else if(day == 2){

            entity.setDeliveryQuantity02(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity02(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity02(temp.getNotQualifiedQuantity());

        }else if(day == 3){
            entity.setDeliveryQuantity03(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity03(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity03(temp.getNotQualifiedQuantity());

        }else if(day == 4){
            entity.setDeliveryQuantity04(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity04(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity04(temp.getNotQualifiedQuantity());

        }else if(day == 5){
            entity.setDeliveryQuantity05(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity05(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity05(temp.getNotQualifiedQuantity());

        }else if(day == 6){
            entity.setDeliveryQuantity06(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity06(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity06(temp.getNotQualifiedQuantity());

        }else if(day == 7){
            entity.setDeliveryQuantity07(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity07(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity07(temp.getNotQualifiedQuantity());

        }else if(day == 8){
            entity.setDeliveryQuantity08(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity08(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity08(temp.getNotQualifiedQuantity());

        }else if(day == 9){
            entity.setDeliveryQuantity09(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity09(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity09(temp.getNotQualifiedQuantity());

        }else if(day == 10){
            entity.setDeliveryQuantity10(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity10(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity10(temp.getNotQualifiedQuantity());

        }else if(day == 11){
            entity.setDeliveryQuantity11(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity11(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity11(temp.getNotQualifiedQuantity());

        }else if(day == 12){
            entity.setDeliveryQuantity12(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity12(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity12(temp.getNotQualifiedQuantity());

        }else if(day == 13){
            entity.setDeliveryQuantity13(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity13(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity13(temp.getNotQualifiedQuantity());

        }else if(day == 14){
            entity.setDeliveryQuantity14(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity14(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity14(temp.getNotQualifiedQuantity());

        }else if(day == 15){
            entity.setDeliveryQuantity15(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity15(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity15(temp.getNotQualifiedQuantity());

        }else if(day == 16){
            entity.setDeliveryQuantity16(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity16(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity16(temp.getNotQualifiedQuantity());

        }else if(day == 17){
            entity.setDeliveryQuantity17(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity17(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity17(temp.getNotQualifiedQuantity());

        }else if(day == 18){
            entity.setDeliveryQuantity18(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity18(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity18(temp.getNotQualifiedQuantity());

        }else if(day == 19){
            entity.setDeliveryQuantity19(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity19(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity19(temp.getNotQualifiedQuantity());

        }else if(day == 20){
            entity.setDeliveryQuantity20(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity20(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity20(temp.getNotQualifiedQuantity());

        }else if(day == 21){
            entity.setDeliveryQuantity21(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity21(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity21(temp.getNotQualifiedQuantity());

        }else if(day == 22){
            entity.setDeliveryQuantity22(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity22(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity22(temp.getNotQualifiedQuantity());

        }else if(day == 23){
            entity.setDeliveryQuantity23(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity23(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity23(temp.getNotQualifiedQuantity());

        }else if(day == 24){
            entity.setDeliveryQuantity24(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity24(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity24(temp.getNotQualifiedQuantity());

        }else if(day == 25){
            entity.setDeliveryQuantity25(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity25(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity25(temp.getNotQualifiedQuantity());

        }else if(day == 26){
            entity.setDeliveryQuantity26(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity26(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity26(temp.getNotQualifiedQuantity());

        }else if(day == 27){
            entity.setDeliveryQuantity27(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity27(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity27(temp.getNotQualifiedQuantity());

        }else if(day == 28){
            entity.setDeliveryQuantity28(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity28(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity28(temp.getNotQualifiedQuantity());

        }else if(day == 29){
            entity.setDeliveryQuantity29(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity29(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity29(temp.getNotQualifiedQuantity());

        }else if(day == 30){
            entity.setDeliveryQuantity30(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity30(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity30(temp.getNotQualifiedQuantity());

        }else if(day == 31){
            entity.setDeliveryQuantity31(temp.getDeliveryQuantity());
            entity.setActualReceiveQuantity31(temp.getActualReceiveQuantity());
            entity.setNotQualifiedQuantity31(temp.getNotQualifiedQuantity());
        }
    }

//    public void setCurrentDay(DeliveryOrderDetailEO entity,DeliveryOrderDetailEO temp,int day){
//
//        //交付总数
//        if (null != temp.getActualReceiveQuantity()){
//            entity.setTotalActualReceiveQuantity(entity.getTotalActualReceiveQuantity() + temp.getActualReceiveQuantity());
//        }
//        //订单总数
//        if (null != temp.getDeliveryQuantity()){
//            entity.setTotalDeliveryQuantity(entity.getTotalDeliveryQuantity() + temp.getDeliveryQuantity());
//        }
//        //不良总数
//        if (null != temp.getNotQualifiedQuantity()){
//            entity.setTotalNotQualifiedQuantity(entity.getTotalNotQualifiedQuantity() + temp.getNotQualifiedQuantity());
//        }
//
//        //1号
//        if(day == 1){
//            entity.setDeliveryQuantity01(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity01(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity01(temp.getNotQualifiedQuantity());
//        }else if(day == 2){
//
//            entity.setDeliveryQuantity02(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity02(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity02(temp.getNotQualifiedQuantity());
//
//        }else if(day == 3){
//            entity.setDeliveryQuantity03(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity03(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity03(temp.getNotQualifiedQuantity());
//
//        }else if(day == 4){
//            entity.setDeliveryQuantity04(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity04(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity04(temp.getNotQualifiedQuantity());
//
//        }else if(day == 5){
//            entity.setDeliveryQuantity05(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity05(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity05(temp.getNotQualifiedQuantity());
//
//        }else if(day == 6){
//            entity.setDeliveryQuantity06(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity06(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity06(temp.getNotQualifiedQuantity());
//
//        }else if(day == 7){
//            entity.setDeliveryQuantity07(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity07(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity07(temp.getNotQualifiedQuantity());
//
//        }else if(day == 8){
//            entity.setDeliveryQuantity08(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity08(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity08(temp.getNotQualifiedQuantity());
//
//        }else if(day == 9){
//            entity.setDeliveryQuantity09(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity09(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity09(temp.getNotQualifiedQuantity());
//
//        }else if(day == 10){
//            entity.setDeliveryQuantity10(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity10(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity10(temp.getNotQualifiedQuantity());
//
//        }else if(day == 11){
//            entity.setDeliveryQuantity11(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity11(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity11(temp.getNotQualifiedQuantity());
//
//        }else if(day == 12){
//            entity.setDeliveryQuantity12(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity12(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity12(temp.getNotQualifiedQuantity());
//
//        }else if(day == 13){
//            entity.setDeliveryQuantity13(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity13(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity13(temp.getNotQualifiedQuantity());
//
//        }else if(day == 14){
//            entity.setDeliveryQuantity14(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity14(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity14(temp.getNotQualifiedQuantity());
//
//        }else if(day == 15){
//            entity.setDeliveryQuantity15(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity15(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity15(temp.getNotQualifiedQuantity());
//
//        }else if(day == 16){
//            entity.setDeliveryQuantity16(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity16(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity16(temp.getNotQualifiedQuantity());
//
//        }else if(day == 17){
//            entity.setDeliveryQuantity17(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity17(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity17(temp.getNotQualifiedQuantity());
//
//        }else if(day == 18){
//            entity.setDeliveryQuantity18(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity18(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity18(temp.getNotQualifiedQuantity());
//
//        }else if(day == 19){
//            entity.setDeliveryQuantity19(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity19(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity19(temp.getNotQualifiedQuantity());
//
//        }else if(day == 20){
//            entity.setDeliveryQuantity20(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity20(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity20(temp.getNotQualifiedQuantity());
//
//        }else if(day == 21){
//            entity.setDeliveryQuantity21(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity21(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity21(temp.getNotQualifiedQuantity());
//
//        }else if(day == 22){
//            entity.setDeliveryQuantity22(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity22(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity22(temp.getNotQualifiedQuantity());
//
//        }else if(day == 23){
//            entity.setDeliveryQuantity23(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity23(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity23(temp.getNotQualifiedQuantity());
//
//        }else if(day == 24){
//            entity.setDeliveryQuantity24(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity24(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity24(temp.getNotQualifiedQuantity());
//
//        }else if(day == 25){
//            entity.setDeliveryQuantity25(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity25(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity25(temp.getNotQualifiedQuantity());
//
//        }else if(day == 26){
//            entity.setDeliveryQuantity26(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity26(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity26(temp.getNotQualifiedQuantity());
//
//        }else if(day == 27){
//            entity.setDeliveryQuantity27(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity27(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity27(temp.getNotQualifiedQuantity());
//
//        }else if(day == 28){
//            entity.setDeliveryQuantity28(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity28(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity28(temp.getNotQualifiedQuantity());
//
//        }else if(day == 29){
//            entity.setDeliveryQuantity29(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity29(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity29(temp.getNotQualifiedQuantity());
//
//        }else if(day == 30){
//            entity.setDeliveryQuantity30(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity30(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity30(temp.getNotQualifiedQuantity());
//
//        }else if(day == 31){
//            entity.setDeliveryQuantity31(temp.getDeliveryQuantity());
//            entity.setActualReceiveQuantity31(temp.getActualReceiveQuantity());
//            entity.setNotQualifiedQuantity31(temp.getNotQualifiedQuantity());
//        }
//    }

}