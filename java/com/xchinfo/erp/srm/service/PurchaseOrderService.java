package com.xchinfo.erp.srm.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.service.MaterialDistributeService;
import com.xchinfo.erp.mes.service.MaterialPlanService;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.srm.mapper.PurchaseOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.CommonUtil;
import jdk.nashorn.internal.ir.annotations.Reference;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhongye
 * @date 2019/5/9
 */
 @Service
public class PurchaseOrderService extends BaseServiceImpl<PurchaseOrderMapper, PurchaseOrderEO> {

     @Autowired
    private SupplierService supplierService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private MaterialDistributeService materialDistributeService;

    @Autowired
    private MaterialPlanService materialPlanService;

    @Autowired
    private DeliveryPlanService deliveryPlanService;

    @Autowired
    private DeliveryNoteDetailService deliveryNoteDetailService;

    @Autowired
    private OrgService orgService;


    // 格式化日期字符串
    private Date formatDateString(String str, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(str.replaceAll("/","-"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }


    @Transactional(rollbackFor = Exception.class)
    public void importFromExcel(List list, Integer type, UserEO user) throws BusinessException{
//        this.orgService.checkUserPermissions(orgId, user.getUserId(), "选择的归属机构权限不存在该用户权限,请确认!");

        if(list != null){
            if(list.size()>0){
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

                // 判断物料是否属于用户的归属机构
                String errorMsg = "";
                if(mapList!=null && mapList.size()>0){
                    for(int j=1; j<mapList.size(); j++){
                        Map mapTemp = mapList.get(j);
                        MaterialEO materialTemp = this.materialService.getByMaterialCode((String) mapTemp.get("8"));
                        if(materialTemp != null) {
                            Boolean flag = this.orgService.checkUserPermissions(materialTemp.getOrgId(), user.getUserId());
                            if(!flag.booleanValue()) {
                                errorMsg += ("第" + j + "行的物料编码的归属机构权限不存在登录用户权限!<br/>");
                            }
                        } else {
                            errorMsg += ("第" + j + "行的物料编码不存在于数据库!<br/>");
                        }

                        SupplierEO supplierTemp = this.supplierService.getBySupplierCode((String) mapTemp.get("4"));
                        if(supplierTemp == null){
                            errorMsg += ("第" + j + "行的供应商编码不存在于数据库!<br/>");
                        }
                    }
                }

                if(!errorMsg.equals("")) {
                    throw new BusinessException(errorMsg);
                }

                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);
                        SupplierEO supplier = this.supplierService.getBySupplierCode((String) map.get("4"));
                        MaterialEO material = this.materialService.getByMaterialCode((String) map.get("8"));
                        PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
                        purchaseOrder.setSupplierId(supplier.getSupplierId());
                        purchaseOrder.setMaterialId(material.getMaterialId());
                        purchaseOrder.setInventoryCode(material.getInventoryCode());
                        purchaseOrder.setElementNo(material.getElementNo());
                        // 设置属性值
                        // 生成业务编码
                        String voucherNo = this.businessCodeGenerator.generateNextCode("srm_purchase_order", purchaseOrder,user.getOrgId());
                        AssertUtils.isBlank(voucherNo);
                        purchaseOrder.setVoucherNo(voucherNo);
                        if(map.get("2")==null ||
                                "".equals(((String) map.get("2")).replaceAll(" ",""))){
                            purchaseOrder.setPurchaseOrderNo(voucherNo);
                        }else{
                            purchaseOrder.setPurchaseOrderNo((String) map.get("2"));
                        }
                        purchaseOrder.setCreateDate(formatDateString((String) map.get("3"), "yyyy-MM-dd"));

                        purchaseOrder.setDepartment((String) map.get("6"));
                        purchaseOrder.setCurrencyName((String) map.get("7"));
                        purchaseOrder.setMaterialCode((String) map.get("8"));
                        purchaseOrder.setMaterialName((String) map.get("9"));
                        purchaseOrder.setSpecification((String) map.get("10"));
                        purchaseOrder.setUnitName((String) map.get("11"));
                        purchaseOrder.setPlanDeliveryQuantity(Double.parseDouble((String) map.get("12")));
                        purchaseOrder.setNotDeliveryQuantity(Double.parseDouble((String) map.get("12")));
                        purchaseOrder.setPlanArriveDate(formatDateString((String) map.get("13"), "yyyy-MM-dd"));
                        purchaseOrder.setType(type);
                        purchaseOrder.setStatus(0);
                        purchaseOrder.setCreateUserId(user.getUserId());
                        purchaseOrder.setCreateUserName(user.getUserName());
                        purchaseOrder.setChargeUserId(user.getUserId());
                        purchaseOrder.setChargeUserName(user.getUserName());
                        purchaseOrder.setOrgId(user.getOrgId());
                        // 各数量默认值为0
                        purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
                        super.save(purchaseOrder);
                    }
                }
            }else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }
    }

    private void checkDeliveryPlan(DeliveryPlanEO entity) {
        PurchaseOrderEO entityFromDb = this.getById(entity.getPurchaseOrderId());
        List<DeliveryPlanEO>  deliveryPlans = entityFromDb.getDeliveryPlans();

        // 判断数量是否超过计划数量
        int sum = 0; // 已有的送货计划数量总和
        if(deliveryPlans!=null && deliveryPlans.size()>0){
            for(DeliveryPlanEO deliveryPlan : deliveryPlans){
                if(deliveryPlan.getStatus() != 9){ //送货计划未被关闭
                    sum += deliveryPlan.getPlanDeliveryQuantity();
                }else{ //送货计划已被关闭
                    sum += (deliveryPlan.getPlanDeliveryQuantity() - deliveryPlan.getActualReceiveQuantity() - deliveryPlan.getIntransitQuantity());
                }

            }
        }
        if((sum+entity.getPlanDeliveryQuantity())  > entityFromDb.getPlanDeliveryQuantity()){
            throw new BusinessException("送货数量错误!数量不能超出订单数量["+ entityFromDb.getPlanDeliveryQuantity() +"]" +
                    "或剩余未送数量[" + (entityFromDb.getPlanDeliveryQuantity()-sum) + "](去除已收、在途)!");
        }

        // 判断送货数量是否正确
        if(entity.getPlanDeliveryQuantity() <= 0){
            throw new BusinessException("错误的送货数量!");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public DeliveryPlanEO createDeliveryPlan(DeliveryPlanEO entity, UserEO user) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), user.getUserId(), "订单的归属机构权限不存在该用户权限,请确认!");

        checkDeliveryPlan(entity);
        PurchaseOrderEO purchaseOrder = this.getById(entity.getPurchaseOrderId());
        if(purchaseOrder.getSerialDistributeId() != null) {
            MaterialDistributeEO materialDistribute = this.materialDistributeService.getById(purchaseOrder.getSerialDistributeId());
            if(materialDistribute!=null && materialDistribute.getStatus()<2) {
                materialDistribute.setStatus(2);
                materialDistributeService.updateById(materialDistribute);
                if(materialDistribute.getSerialId() != null) {
                    MaterialPlanEO materialPlan = this.materialPlanService.getById(materialDistribute.getSerialId());
                    if(materialPlan!=null && materialPlan.getStatus()<2) {
                        logger.info("创建计划");
                        materialPlan.setStatus(2);
                        materialPlanService.updateById(materialPlan);
                    }
                }
            }
        }
        entity.setType(purchaseOrder.getType());
        entity.setCreateUserId(user.getUserId());
        entity.setCreateUserName(user.getUserName());
        entity.setChargeUserId(user.getUserId());
        entity.setChargeUserName(user.getUserName());
        entity.setOrgId(purchaseOrder.getOrgId());
        return this.deliveryPlanService.saveEntity(entity);
    }

    @Override
    public PurchaseOrderEO getById(Serializable id) throws BusinessException {
        PurchaseOrderEO purchaseOrder = this.baseMapper.selectById(id);
        List<DeliveryPlanEO> deliveryPlans = this.deliveryPlanService.getByPurchaseOrderId((Long) id);
        purchaseOrder.setDeliveryPlans(deliveryPlans);
        return purchaseOrder;
    }


    @Transactional(rollbackFor = Exception.class)
    public Map createBatchDeliveryPlan(Long[] ids, String planDeliveryDate, UserEO user) throws BusinessException {
        int ignoreNum = 0; // 忽略的数量
        int insertNum = 0; // 插入的数量
        Map map = new HashMap();
        if(ids!=null && ids.length>0){
            // 判断选择的订单登录用户是否有归属机构权限
            String errorMsg = "";
            for(Long id : ids) {
                PurchaseOrderEO purchaseOrderTemp = this.getById(id);
                Boolean flag = this.orgService.checkUserPermissions(purchaseOrderTemp.getOrgId(), user.getUserId());
                if(!flag.booleanValue()) {
                    errorMsg += ("订单【" + purchaseOrderTemp.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
                }
            }

            if(!"".equals(errorMsg)) {
                throw new BusinessException(errorMsg);
            }

            for(Long id : ids){
                PurchaseOrderEO purchaseOrder = this.getById(id);
                List<DeliveryPlanEO>  deliveryPlans = purchaseOrder.getDeliveryPlans();
                if(deliveryPlans!=null && deliveryPlans.size()>0){
                    ignoreNum += 1;
                }else{
                    DeliveryPlanEO deliveryPlan = new DeliveryPlanEO();
                    deliveryPlan.setPurchaseOrderId(id);
                    deliveryPlan.setSupplierId(purchaseOrder.getSupplierId());
                    deliveryPlan.setPlanDeliveryQuantity(purchaseOrder.getPlanDeliveryQuantity());
                    deliveryPlan.setPlanDeliveryDate(formatDateString(planDeliveryDate, "yyyy-MM-dd"));
                    deliveryPlan.setStatus(1);
                    deliveryPlan.setType(purchaseOrder.getType());
                    deliveryPlan.setCreateUserId(user.getUserId());
                    deliveryPlan.setCreateUserName(user.getUserName());
                    deliveryPlan.setChargeUserId(user.getUserId());
                    deliveryPlan.setChargeUserName(user.getUserName());
                    deliveryPlan.setOrgId(purchaseOrder.getOrgId());
                    DeliveryPlanEO saveEntity = this.deliveryPlanService.saveEntity(deliveryPlan);
                    if(saveEntity != null){
                        purchaseOrder.setStatus(1);
                        this.baseMapper.updateById(purchaseOrder);
                        insertNum += 1;

                        if(purchaseOrder.getSerialDistributeId() != null) {
                            MaterialDistributeEO materialDistribute = this.materialDistributeService.getById(purchaseOrder.getSerialDistributeId());
                            if(materialDistribute!=null && materialDistribute.getStatus()<2) {
                                materialDistribute.setStatus(2);
                                materialDistributeService.updateById(materialDistribute);
                                if(materialDistribute.getSerialId() != null) {
                                    MaterialPlanEO materialPlan = this.materialPlanService.getById(materialDistribute.getSerialId());
                                    if(materialPlan!=null && materialPlan.getStatus()<2) {
                                        logger.info("批量创建计划");
                                        materialPlan.setStatus(2);
                                        materialPlanService.updateById(materialPlan);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            throw new BusinessException("请选择数据后再操作!");
        }
        map.put("ignoreNum", ignoreNum);
        map.put("insertNum", insertNum);
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public void close(Long[] ids, Long userId) throws BusinessException {
        if(ids==null && ids.length==0){
            throw new BusinessException("请选择数据后再操作!");
        }

        // 判断选择的订单登录用户是否有归属机构权限
        String errorMsg = "";
        for(Long id : ids) {
            PurchaseOrderEO purchaseOrderTemp = this.getById(id);
            Boolean flag = this.orgService.checkUserPermissions(purchaseOrderTemp.getOrgId(), userId);
            if(!flag.booleanValue()) {
                errorMsg += ("订单【" + purchaseOrderTemp.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(errorMsg)) {
            throw new BusinessException(errorMsg);
        }

        for(Long id : ids){
            PurchaseOrderEO purchaseOrder = this.getById(id);
            purchaseOrder.setPreStatus(purchaseOrder.getStatus());
            purchaseOrder.setStatus(9);
            this.baseMapper.updateById(purchaseOrder);

            //更新送货计划状态
            this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),9,1);
            this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),9,0);
            this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),9,2);

        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelClose(Long[] ids, Long userId) throws BusinessException {
        if(ids==null && ids.length==0){
            throw new BusinessException("请选择数据后再操作!");
        }

        // 判断选择的订单登录用户是否有归属机构权限
        String errorMsg = "";
        for(Long id : ids) {
            PurchaseOrderEO purchaseOrderTemp = this.getById(id);
            Boolean flag = this.orgService.checkUserPermissions(purchaseOrderTemp.getOrgId(), userId);
            if(!flag.booleanValue()) {
                errorMsg += ("订单【" + purchaseOrderTemp.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(errorMsg)) {
            throw new BusinessException(errorMsg);
        }

        for(Long id : ids){
            PurchaseOrderEO purchaseOrder = this.getById(id);
            purchaseOrder.setStatus(purchaseOrder.getPreStatus());
            this.baseMapper.updateById(purchaseOrder);

            //更新送货计划状态
            this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),purchaseOrder.getPreStatus(),9);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void releaseDeliveryPlan(Long deliveryPlanId, Long userId) throws BusinessException {
        DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryPlanId);

        this.orgService.checkUserPermissions(deliveryPlan.getOrgId(), userId, "送货计划的归属机构权限不存在该用户权限,请确认!");

        if(deliveryPlan == null){
            throw new BusinessException("数据已发生变化,请刷新!");
        }
        if(deliveryPlan.getStatus() != 0){
            throw new BusinessException("非新建状态数据不允许发布!");
        }
        deliveryPlan.setStatus(1);
        this.deliveryPlanService.updateById(deliveryPlan);
    }


    @Transactional(rollbackFor = Exception.class)
    public void closeDeliveryPlan(Long deliveryPlanId, Long userId) throws BusinessException {
        DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryPlanId);

        this.orgService.checkUserPermissions(deliveryPlan.getOrgId(), userId, "送货计划的归属机构权限不存在该用户权限,请确认!");

        if(deliveryPlan == null){
            throw new BusinessException("数据已发生变化,请刷新!");
        }
        if(deliveryPlan.getStatus() == 9){
            throw new BusinessException("已完成状态不允许关闭!");
        }
        deliveryPlan.setStatus(9);
        this.deliveryPlanService.updateById(deliveryPlan);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryPlan(DeliveryPlanEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "送货计划的归属机构权限不存在该用户权限,请确认!");

        checkDeliveryPlan(entity);
        this.deliveryPlanService.updateById(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteDeliveryPlan(Long deliveryPlanId, Long userId) throws BusinessException {
        DeliveryPlanEO deliveryPlan = this.deliveryPlanService.getById(deliveryPlanId);

        this.orgService.checkUserPermissions(deliveryPlan.getOrgId(), userId, "送货计划的归属机构权限不存在该用户权限,请确认!");

        Map map = new HashMap();
        map.put("deliveryPlanId", deliveryPlanId);
        List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getList(map);
        if(deliveryNoteDetails!=null && deliveryNoteDetails.size()>0) {
            throw new BusinessException("送货计划下存在送货单明细,不可删除!");
        }
        this.deliveryPlanService.removeById(deliveryPlanId);
    }


    public DeliveryPlanEO getDeliveryPlanById(Long id) throws BusinessException {
        return this.deliveryPlanService.getById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(PurchaseOrderEO entity, Long userId) {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "订单的归属机构权限不存在该用户权限,请确认!");

        entity.setNotDeliveryQuantity(entity.getPlanDeliveryQuantity());
        super.updateById(entity);

        //同时变更送货计划的数量
        this.baseMapper.updateDPmount(entity.getPurchaseOrderId(),entity.getPlanDeliveryQuantity());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = PurchaseOrderEO.class)
    public boolean save(PurchaseOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

        // 生成业务编码
        String voucherNo = this.generateNextCode("srm_purchase_order", entity,user.getOrgId());
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);
        if(null == entity.getPurchaseOrderNo() || "".equals(entity.getPurchaseOrderNo())) {
            entity.setPurchaseOrderNo(voucherNo);
        }

        return super.save(entity);
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
        List<MaterialSupplierEO> planList = new ArrayList<>();
        String dateStr = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        String tempDateStr = monthDate + "-01";
        Date tempDate = null;
        try {
            tempDate = sdf.parse(tempDateStr);
        }catch (Exception e){
            throw new BusinessException("日期转换有误！");
        }

        calendar.setTime(tempDate);
        //当月最大天数
        int days =calendar.getActualMaximum(Calendar.DATE);

        String errorMsg = "";



        //日历
//        Calendar calendar = Calendar.getInstance(Locale.CHINA);
//        Date date = calendar.getTime();
//        //获取当前月天数、最大天数
//        int months = calendar.get(Calendar.MONTH) + 1;
//        int days =calendar.getActualMaximum(Calendar.DATE);
//        int currentDays = calendar.get(Calendar.DAY_OF_MONTH);
//
//
//        //获取下周一日期
//        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
//        System.out.println("====="+weekday);
//        if(weekday == 1){
//            calendar.add(Calendar.DATE, 1);
//        }else{
//            calendar.add(Calendar.DATE, 9-weekday);
//        }
//
//        int nextDays = calendar.get(Calendar.DAY_OF_MONTH);




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

                        //查询物料是否存在
                        MaterialEO materialEO = this.baseMapper.selectMaterialInfo(elementNo,userEO.getOrgId());
                        if(null == materialEO){
                            errorMsg = errorMsg + "第 ["+(i+1)+"] 行：用户归属机构下该物料不存在或者被禁用\n";
                            continue;
                        }

                        MaterialSupplierEO materialSupplierEO = new MaterialSupplierEO();
                        //供应商名称
                        String supplierName = (String) map.get("2");
                        if(null==supplierName || supplierName.isEmpty()){
                            //查询默认供应商
                            materialSupplierEO = this.baseMapper.selectDefaultSupplier(elementNo,userEO.getOrgId());
                            if(null == materialSupplierEO){
                                logger.info("========物料供应商不存在========"+elementNo);
                                errorMsg = errorMsg + "第 ["+(i+1)+"] 行：未设置关联供应商\n";
                                continue;
                            }
                        }else{
                            //查询供应商，物料关系是否存在
                            materialSupplierEO = this.baseMapper.selectSupplierMaterial(supplierName,elementNo,userEO.getOrgId());
                            if(null == materialSupplierEO){
                                logger.info("========物料供应商不存在========"+elementNo);
                                errorMsg = errorMsg + "第 ["+(i+1)+"] 行：未设置关联供应商\n";
                                continue;
                            }
                        }

                        //供应商物料是否用户所属机构
                        if(!userEO.getOrgId().equals(materialSupplierEO.getOrgId())){
                            logger.info("========物料所属机构和用户所属机构不一致========"+elementNo);
                            errorMsg = errorMsg + "第 ["+(i+1)+"] 行：物料所属机构和用户所属机构不一致\n";
                            continue;
                        }

                        MaterialSupplierEO one = materialSupplierEO;
                        Boolean flag = false;
                        //遍历查询单元格数据
                        for(int h=1;h<=days;h++){
                            //单元格数据
                            int j = h + 3;

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
                        }

                        if(flag){
                            one.setMonthDate(DateUtils.stringToDate(tempDateStr,"yyyy-MM-dd"));
                            planList.add(one);
                        }
                    }
                }
            }else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }

        //将错误信息返回，同时让客户必须保证数据的正确性
        Result result =  new Result<List<MaterialSupplierEO>>().ok(planList);
        if(!errorMsg.isEmpty()){
            result.setCode(100);
            result.setMsg(errorMsg);
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean saveEntitys(MaterialSupplierEO[] entitys, UserEO userEO) throws BusinessException {

        for (MaterialSupplierEO materialSupplierEO:entitys){

            String monthDate = DateUtils.format(materialSupplierEO.getMonthDate(),"yyyy-MM");
            //一
            if(null != materialSupplierEO.getPlanCount01() && materialSupplierEO.getPlanCount01()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-01","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount01());
                saveData(materialSupplierEO,userEO);
            }

            if(null != materialSupplierEO.getPlanCount02() && materialSupplierEO.getPlanCount02()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-02","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount02());
                saveData(materialSupplierEO,userEO);
            }

            if(null != materialSupplierEO.getPlanCount03() && materialSupplierEO.getPlanCount03()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-03","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount03());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount04() && materialSupplierEO.getPlanCount04()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-04","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount04());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount05() && materialSupplierEO.getPlanCount05()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-05","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount05());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount06() && materialSupplierEO.getPlanCount06()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-06","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount06());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount07() && materialSupplierEO.getPlanCount07()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-07","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount07());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount08() && materialSupplierEO.getPlanCount08()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-08","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount08());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount09() && materialSupplierEO.getPlanCount09()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-09","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount09());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount10() && materialSupplierEO.getPlanCount10()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-10","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount10());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount11() && materialSupplierEO.getPlanCount11()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-11","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount11());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount12() && materialSupplierEO.getPlanCount12()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-12","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount12());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount13() && materialSupplierEO.getPlanCount13()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-13","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount13());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount14() && materialSupplierEO.getPlanCount14()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-14","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount14());
                saveData(materialSupplierEO,userEO);
            }if(null != materialSupplierEO.getPlanCount15() && materialSupplierEO.getPlanCount15()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-15","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount15());
                saveData(materialSupplierEO,userEO);
            }if(null != materialSupplierEO.getPlanCount16() && materialSupplierEO.getPlanCount16()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-16","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount16());
                saveData(materialSupplierEO,userEO);
            }if(null != materialSupplierEO.getPlanCount17() && materialSupplierEO.getPlanCount17()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-17","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount17());
                saveData(materialSupplierEO,userEO);
            }if(null != materialSupplierEO.getPlanCount18() && materialSupplierEO.getPlanCount18()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-18","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount18());
                saveData(materialSupplierEO,userEO);
            }if(null != materialSupplierEO.getPlanCount19() && materialSupplierEO.getPlanCount19()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-19","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount19());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount20() && materialSupplierEO.getPlanCount20()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-20","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount20());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount21() && materialSupplierEO.getPlanCount21()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-21","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount21());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount22() && materialSupplierEO.getPlanCount22()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-22","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount22());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount23() && materialSupplierEO.getPlanCount23()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-23","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount23());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount24() && materialSupplierEO.getPlanCount24()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-24","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount24());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount25() && materialSupplierEO.getPlanCount25()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-25","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount25());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount26() && materialSupplierEO.getPlanCount26()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-26","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount26());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount27() && materialSupplierEO.getPlanCount27()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-27","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount27());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount28() && materialSupplierEO.getPlanCount28()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-28","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount28());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount29() && materialSupplierEO.getPlanCount29()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-29","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount29());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount30() && materialSupplierEO.getPlanCount30()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-30","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount30());
                saveData(materialSupplierEO,userEO);
            }
            if(null != materialSupplierEO.getPlanCount31() && materialSupplierEO.getPlanCount31()>0){
                materialSupplierEO.setWeekDate(DateUtils.stringToDate(monthDate+"-31","yyyy-MM-dd"));
                materialSupplierEO.setRequireCount(materialSupplierEO.getPlanCount31());
                saveData(materialSupplierEO,userEO);
            }


        }

        return true;
    }


    public void saveData(MaterialSupplierEO materialSupplierEO,UserEO userEO){
        //新增订单
        PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
        purchaseOrder.setSupplierId(materialSupplierEO.getSupplierId());
        purchaseOrder.setMaterialId(materialSupplierEO.getMaterialId());
        purchaseOrder.setInventoryCode(materialSupplierEO.getInventoryCode());
        purchaseOrder.setElementNo(materialSupplierEO.getElementNo());
        String voucherNo = this.generateNextCode("srm_purchase_order", purchaseOrder,userEO.getOrgId());
        // 设置属性值
        // 生成业务编码
        AssertUtils.isBlank(voucherNo);
        purchaseOrder.setVoucherNo(voucherNo);
        purchaseOrder.setPurchaseOrderNo(voucherNo);
        purchaseOrder.setCreateDate(new Date());

        purchaseOrder.setCurrencyName("人民币");
        purchaseOrder.setMaterialCode(materialSupplierEO.getMaterialCode());
        purchaseOrder.setMaterialName(materialSupplierEO.getMaterialName());
        purchaseOrder.setUnitName(materialSupplierEO.getUnitName());
        purchaseOrder.setPlanDeliveryQuantity(materialSupplierEO.getRequireCount());
        purchaseOrder.setNotDeliveryQuantity(materialSupplierEO.getRequireCount());
        purchaseOrder.setPlanArriveDate(materialSupplierEO.getWeekDate());
        purchaseOrder.setType(1);

        purchaseOrder.setCreateUserId(userEO.getUserId());
        purchaseOrder.setCreateUserName(userEO.getUserName());
        purchaseOrder.setChargeUserId(userEO.getUserId());
        purchaseOrder.setChargeUserName(userEO.getUserName());
        purchaseOrder.setOrgId(userEO.getOrgId());
        purchaseOrder.setStatus(0);
        purchaseOrder.setMonthDate(materialSupplierEO.getMonthDate());
        // 各数量默认值为0
        purchaseOrder.setActualDeliveryQuantity(0d);
        purchaseOrder.setQualifiedQuantity(0d);
        purchaseOrder.setReturnedQuantity(0d);
        super.save(purchaseOrder);


        DeliveryPlanEO deliveryPlan = new DeliveryPlanEO();
        deliveryPlan.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        deliveryPlan.setSupplierId(purchaseOrder.getSupplierId());
        deliveryPlan.setPlanDeliveryQuantity(purchaseOrder.getPlanDeliveryQuantity());
        deliveryPlan.setPlanDeliveryDate(purchaseOrder.getPlanArriveDate());
        deliveryPlan.setStatus(0);
        deliveryPlan.setType(purchaseOrder.getType());
        deliveryPlan.setCreateUserId(userEO.getUserId());
        deliveryPlan.setCreateUserName(userEO.getUserName());
        deliveryPlan.setChargeUserId(userEO.getUserId());
        deliveryPlan.setChargeUserName(userEO.getUserName());
        deliveryPlan.setOrgId(purchaseOrder.getOrgId());
        this.deliveryPlanService.saveEntity(deliveryPlan);
    }


    @Transactional(rollbackFor = Exception.class)
    public void release(Long[] ids, Long userId) throws BusinessException {

        // 判断选择的订单登录用户是否有归属机构权限
        String errorMsg = "";
        for(Long id : ids) {
            PurchaseOrderEO purchaseOrderTemp = this.getById(id);
            Boolean flag = this.orgService.checkUserPermissions(purchaseOrderTemp.getOrgId(), userId);
            if(!flag.booleanValue()) {
                errorMsg += ("发布失败，订单流水【" + purchaseOrderTemp.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(errorMsg)) {
            throw new BusinessException(errorMsg);
        }

        for(Long id : ids){
            PurchaseOrderEO purchaseOrder = this.getById(id);
            if(purchaseOrder.getStatus() == 0){
                DeliveryPlanEO  deliveryPlanEO = this.baseMapper.selectDeliveryPlan(purchaseOrder.getPurchaseOrderId());
                //送货计划不存在则不允许发布
                if(null == deliveryPlanEO){
                    continue;
                }
                purchaseOrder.setStatus(1);
                this.baseMapper.updateById(purchaseOrder);
                //更新送货计划状态
                this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),1,0);
            }

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelRealase(Long[] ids, Long userId) throws BusinessException {
        String errorMsg = "";
        for(Long id : ids) {
            PurchaseOrderEO purchaseOrderTemp = this.getById(id);

            Boolean flag = this.orgService.checkUserPermissions(purchaseOrderTemp.getOrgId(), userId);
            if(!flag.booleanValue()) {
                errorMsg += ("取消发布失败，订单流水【" + purchaseOrderTemp.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(errorMsg)) {
            throw new BusinessException(errorMsg);
        }

        for(Long id : ids){
            PurchaseOrderEO purchaseOrder = this.getById(id);
            if(purchaseOrder.getStatus() == 1){
                purchaseOrder.setStatus(0);
                this.baseMapper.updateById(purchaseOrder);

                DeliveryPlanEO  deliveryPlanEO = this.baseMapper.selectDeliveryPlan(purchaseOrder.getPurchaseOrderId());
                if(null!= deliveryPlanEO && deliveryPlanEO.getIsSupplierConfirm() == 1){
                    throw new BusinessException("取消发布失败，订单流水【" + purchaseOrder.getVoucherNo() +" 】的订单供应商已确认!");
                }else{
                    //更新送货计划状态
                    this.baseMapper.updateDPlanStatus(purchaseOrder.getPurchaseOrderId(),0,1);
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


    public IPage<PurchaseOrderEO> pagePurchase(Criteria criteria) throws BusinessException {
        List<PurchaseOrderEO> pageList = new ArrayList<>();
        List<PurchaseOrderEO> returnRecords = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Map<Object,PurchaseOrderEO> map = new LinkedHashMap();
        int current = criteria.getCurrentPage();
        int size = criteria.getSize();

        //获取分组数据，再进行组装
        // 构造分页对象
        Map mapCondition = criteriaToMap(criteria);

        // 查询数据(总数据)
        List<PurchaseOrderEO> pageTotal = this.baseMapper.selectNewPage(mapCondition);
        mapCondition.put("currentIndexFlag", true);
        List<PurchaseOrderEO> page = this.baseMapper.selectNewPage(mapCondition);

        criteria.setCurrentPage(1);
        criteria.setSize(10000);
        for(int i = 0;i<page.size();i++){
            //根据每个获取出来的条件组装查询返回子数据
            PurchaseOrderEO entityTemp = page.get(i);
            //设置参数值
            for (Criterion c:criteria.getCriterions()){
                if(c.getField().equals("po.supplier_id")){
                    c.setValue(entityTemp.getSupplierId()+"");
                }else if (c.getField().equals("po.material_id")){
                    c.setValue(entityTemp.getMaterialId()+"");
                }else if (c.getField().equals("po.month_date")){
                    c.setValue(DateUtils.format(entityTemp.getMonthDate(),"yyyy-MM-dd"));
                }else if (c.getField().equals("po.org_id")){
                    c.setValue(entityTemp.getOrgId()+"");
                }else if (c.getField().equals("po.plan_start_date") || c.getField().equals("po.plan_end_date")){
                    c.setField("po.plan_arrive_date");
                }else if (c.getField().equals("inputCondition")){
                    c.setField("po.voucher_no|po.material_code|po.material_name|po.element_no");
                }
            }

            IPage<PurchaseOrderEO> pageChild = super.selectPage(criteria);
            if(null != pageChild.getRecords() && pageChild.getRecords().size() > 0){
                for(PurchaseOrderEO planEO:pageChild.getRecords()){
                    if(null != map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId())){
                        //若存在则更新已存在数据的需求数
                        calendar.setTime(planEO.getPlanArriveDate());
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setPlanDeliveryQuantity(planEO.getPlanDeliveryQuantity());
                        //状态
                        map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setStatus(planEO.getStatus());
                        //ID
                        map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setPurchaseOrderId(planEO.getPurchaseOrderId());
                        setDay(map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()),day);
                    }else{
                        calendar.setTime(planEO.getPlanArriveDate());
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        setDay(planEO,day);
                        map.put(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId(),planEO);
                    }

                }
            }
        }

        for(Object key : map.keySet()){
            returnRecords.add(map.get(key));
        }
        IPage<PurchaseOrderEO> purchasePage = new Page<PurchaseOrderEO>();
        purchasePage.setSize(size);
        purchasePage.setCurrent(current);
        purchasePage.setTotal(pageTotal.size());

        int pages =  pageTotal.size()/size;
        if(pageTotal.size() % size > 0){
            pages = pages +1;
        }

        purchasePage.setPages(pages);
        purchasePage.setRecords(returnRecords);
        return purchasePage;
    }

//    public IPage<PurchaseOrderEO> pagePurchase(Criteria criteria) throws BusinessException {
//        List<PurchaseOrderEO> pageList = new ArrayList<>();
//
//        List<PurchaseOrderEO> returnRecords = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        Map<Object,PurchaseOrderEO> map = new LinkedHashMap();
//
//        int current = criteria.getCurrentPage();
//        int size = criteria.getSize();
//
//        //获取所有数据，再进行组装
//        criteria.setCurrentPage(1);
//        criteria.setSize(10000000);
//        IPage<PurchaseOrderEO> page = super.selectPage(criteria);
//
//        if(null != page.getRecords() && page.getRecords().size() > 0){
//            for(PurchaseOrderEO planEO:page.getRecords()){
//                if(null != map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId())){
//                    //若存在则更新已存在数据的需求数
//                    calendar.setTime(planEO.getPlanArriveDate());
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setPlanDeliveryQuantity(planEO.getPlanDeliveryQuantity());
//                    //状态
//                    map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setStatus(planEO.getStatus());
//                    //ID
//                    map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()).setPurchaseOrderId(planEO.getPurchaseOrderId());
//                    setDay(map.get(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId()),day);
//                }else{
//                    calendar.setTime(planEO.getPlanArriveDate());
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    setDay(planEO,day);
//                    map.put(planEO.getMonthDate()+"==="+planEO.getMaterialId()+"==="+planEO.getSupplierId()+"==="+planEO.getOrgId(),planEO);
//
//                }
//
////                if(map.size() > size*current){
////                    break;
////                }
//
//            }
//        }
//
//        for(Object key : map.keySet()){
//            returnRecords.add(map.get(key));
//        }
//
//        IPage<PurchaseOrderEO> purchasePage = new Page<PurchaseOrderEO>();
//        purchasePage.setSize(size);
//        purchasePage.setCurrent(current);
//        purchasePage.setTotal(returnRecords.size());
//
//        int pages =  returnRecords.size()/size;
//        if(returnRecords.size() % size > 0){
//            pages = pages +1;
//        }
//        purchasePage.setPages(pages);
//
//        for(int i = size*(current -1);i<size*current;i++){
//            if(i < returnRecords.size()){
//                pageList.add(returnRecords.get(i));
//            }
//        }
//
//        purchasePage.setRecords(pageList);
//        return purchasePage;
//    }


    public void setDay(PurchaseOrderEO planEO,int day){
        //1号
        if(day == 1){
            planEO.setPlanCount01(planEO.getPlanCount01() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount01()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus01(planEO.getStatus());
            planEO.setPurchaseOrderId01(planEO.getPurchaseOrderId());
        }else if(day == 2){

            planEO.setPlanCount02(planEO.getPlanCount02() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount02()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus02(planEO.getStatus());
            planEO.setPurchaseOrderId02(planEO.getPurchaseOrderId());
        }else if(day == 3){

            planEO.setPlanCount03(planEO.getPlanCount03() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount03()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus03(planEO.getStatus());
            planEO.setPurchaseOrderId03(planEO.getPurchaseOrderId());
        }else if(day == 4){

            planEO.setPlanCount04(planEO.getPlanCount04() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount04()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus04(planEO.getStatus());
            planEO.setPurchaseOrderId04(planEO.getPurchaseOrderId());
        }else if(day == 5){

            planEO.setPlanCount05(planEO.getPlanCount05() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount05()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus05(planEO.getStatus());
            planEO.setPurchaseOrderId05(planEO.getPurchaseOrderId());
        }else if(day == 6){

            planEO.setPlanCount06(planEO.getPlanCount06() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount06()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus06(planEO.getStatus());
            planEO.setPurchaseOrderId06(planEO.getPurchaseOrderId());
        }else if(day == 7){

            planEO.setPlanCount07(planEO.getPlanCount07() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount07()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus07(planEO.getStatus());
            planEO.setPurchaseOrderId07(planEO.getPurchaseOrderId());
        }else if(day == 8){

            planEO.setPlanCount08(planEO.getPlanCount08() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount08()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus08(planEO.getStatus());
            planEO.setPurchaseOrderId08(planEO.getPurchaseOrderId());
        }else if(day == 9){

            planEO.setPlanCount09(planEO.getPlanCount09() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount09()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus09(planEO.getStatus());
            planEO.setPurchaseOrderId09(planEO.getPurchaseOrderId());
        }else if(day == 10){

            planEO.setPlanCount10(planEO.getPlanCount10() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount10()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus10(planEO.getStatus());
            planEO.setPurchaseOrderId10(planEO.getPurchaseOrderId());
        }else if(day == 11){

            planEO.setPlanCount11(planEO.getPlanCount11() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount11()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus11(planEO.getStatus());
            planEO.setPurchaseOrderId11(planEO.getPurchaseOrderId());
        }else if(day == 12){

            planEO.setPlanCount12(planEO.getPlanCount12() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount12()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus12(planEO.getStatus());
            planEO.setPurchaseOrderId12(planEO.getPurchaseOrderId());
        }else if(day == 13){

            planEO.setPlanCount13(planEO.getPlanCount13() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount13()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus13(planEO.getStatus());
            planEO.setPurchaseOrderId13(planEO.getPurchaseOrderId());
        }else if(day == 14){

            planEO.setPlanCount14(planEO.getPlanCount14() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount14()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus14(planEO.getStatus());
            planEO.setPurchaseOrderId14(planEO.getPurchaseOrderId());
        }else if(day == 15){

            planEO.setPlanCount15(planEO.getPlanCount15() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount15()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus15(planEO.getStatus());
            planEO.setPurchaseOrderId15(planEO.getPurchaseOrderId());
        }else if(day == 16){

            planEO.setPlanCount16(planEO.getPlanCount16() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount16()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus16(planEO.getStatus());
            planEO.setPurchaseOrderId16(planEO.getPurchaseOrderId());
        }else if(day == 17){

            planEO.setPlanCount17(planEO.getPlanCount17() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount17()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus17(planEO.getStatus());
            planEO.setPurchaseOrderId17(planEO.getPurchaseOrderId());
        }else if(day == 18){

            planEO.setPlanCount18(planEO.getPlanCount18() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount18()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus18(planEO.getStatus());
            planEO.setPurchaseOrderId18(planEO.getPurchaseOrderId());
        }else if(day == 19){

            planEO.setPlanCount19(planEO.getPlanCount19() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount19()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus19(planEO.getStatus());
            planEO.setPurchaseOrderId19(planEO.getPurchaseOrderId());
        }else if(day == 20){

            planEO.setPlanCount20(planEO.getPlanCount20() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount20()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus20(planEO.getStatus());
            planEO.setPurchaseOrderId20(planEO.getPurchaseOrderId());
        }else if(day == 21){

            planEO.setPlanCount21(planEO.getPlanCount21() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount21()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus21(planEO.getStatus());
            planEO.setPurchaseOrderId21(planEO.getPurchaseOrderId());
        }else if(day == 22){
            planEO.setPlanCount22(planEO.getPlanCount22() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount22()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus22(planEO.getStatus());
            planEO.setPurchaseOrderId22(planEO.getPurchaseOrderId());
        }else if(day == 23){
            planEO.setPlanCount23(planEO.getPlanCount23() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount23()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus23(planEO.getStatus());
            planEO.setPurchaseOrderId23(planEO.getPurchaseOrderId());
        }else if(day == 24){
            planEO.setPlanCount24(planEO.getPlanCount24() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount24()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus24(planEO.getStatus());
            planEO.setPurchaseOrderId24(planEO.getPurchaseOrderId());
        }else if(day == 25){
            planEO.setPlanCount25(planEO.getPlanCount25() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount25()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus25(planEO.getStatus());
            planEO.setPurchaseOrderId25(planEO.getPurchaseOrderId());
        }else if(day == 26){
            planEO.setPlanCount26(planEO.getPlanCount26() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount26()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus26(planEO.getStatus());
            planEO.setPurchaseOrderId26(planEO.getPurchaseOrderId());
        }else if(day == 27){
            planEO.setPlanCount27(planEO.getPlanCount27() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount27()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus27(planEO.getStatus());
            planEO.setPurchaseOrderId27(planEO.getPurchaseOrderId());
        }else if(day == 28){
            planEO.setPlanCount28(planEO.getPlanCount28() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount28()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus28(planEO.getStatus());
            planEO.setPurchaseOrderId28(planEO.getPurchaseOrderId());
        }else if(day == 29){
            planEO.setPlanCount29(planEO.getPlanCount29() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount29()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus29(planEO.getStatus());
            planEO.setPurchaseOrderId29(planEO.getPurchaseOrderId());
        }else if(day == 30){
            planEO.setPlanCount30(planEO.getPlanCount30() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount30()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus30(planEO.getStatus());
            planEO.setPurchaseOrderId30(planEO.getPurchaseOrderId());
        }else if(day == 31){
            planEO.setPlanCount31(planEO.getPlanCount31() == null?planEO.getPlanDeliveryQuantity():planEO.getPlanCount31()+planEO.getPlanDeliveryQuantity());
            planEO.setStatus31(planEO.getStatus());
            planEO.setPurchaseOrderId31(planEO.getPurchaseOrderId());
        }
    }

    public boolean operateById(String action, String beginDate, String endDate, Long[] ids, UserEO userEO) throws BusinessException {
        String sqlStr= "";
        List<Long> operateList = new ArrayList<>();
        Long[] idTemp = new Long[]{};
        for(Long id:ids){
            if("".equals(sqlStr)){
                sqlStr = "('"+id+"'";
            }else{
                sqlStr = sqlStr + ",'"+id+"'";
            }
        }

        if(!sqlStr.isEmpty()){
            sqlStr = sqlStr +")";
            //过滤出符合的数据
            if("release".equals(action)){
                operateList = this.baseMapper.selectOrderInfoByIds(sqlStr,0,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId());
                this.release(operateList.toArray(idTemp),userEO.getUserId());
            }else if("cancelRealase".equals(action)){
                operateList = this.baseMapper.selectOrderInfoByIds(sqlStr,1,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId());
                this.cancelRealase(operateList.toArray(idTemp),userEO.getUserId());
            }
        }

        return true;
    }


    public boolean operateAll(String action, String beginDate, String endDate, UserEO userEO) throws BusinessException {
        Long[] ids = new Long[]{};
        List<Long> operateList = new ArrayList<>();
        if("release".equals(action)){
            operateList = this.baseMapper.selectAllOrderId(0,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId(),1);
            this.release(operateList.toArray(ids),userEO.getUserId());
        }else if("cancelRealase".equals(action)){
            operateList = this.baseMapper.selectAllOrderId(1,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId(),1);
            this.cancelRealase(operateList.toArray(ids),userEO.getUserId());
        }

        return true;
    }

    public boolean operateOutAll(String action, String beginDate, String endDate, UserEO userEO) throws BusinessException {
        Long[] ids = new Long[]{};
        List<Long> operateList = new ArrayList<>();
        if("release".equals(action)){
            operateList = this.baseMapper.selectAllOrderId(0,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId(),2);
            this.release(operateList.toArray(ids),userEO.getUserId());
        }else if("cancelRealase".equals(action)){
            operateList = this.baseMapper.selectAllOrderId(1,DateUtils.stringToDate(beginDate,"yyyy-MM-dd"),DateUtils.stringToDate(endDate,"yyyy-MM-dd"),userEO.getOrgId(),2);
            this.cancelRealase(operateList.toArray(ids),userEO.getUserId());
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result confirm(Long[] ids, UserEO user) throws BusinessException {
        Result result = new Result();

        if(ids==null || ids.length==0) {
            throw new BusinessException("请选择数据!");
        }

        String sqlStr = "";
        for(Long id : ids) {
            sqlStr += (id + ",");
        }

        if(!"".equals(sqlStr)) {
            sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
        }

        sqlStr = "(" + sqlStr + ")";

        List<PurchaseOrderEO> list = this.baseMapper.getByPurchaseOrderIds(sqlStr);
        Map map = new HashedMap();
        String errorMsg = "";
        Set serialIds = new HashSet(); // 客户需求周计划ID集合
        if(list!=null && list.size()>0) {
            for(PurchaseOrderEO purchaseOrder : list) {
                if(purchaseOrder.getIsChangeConfirm().intValue() == 1) {
                    String msg =  purchaseOrder.getOrgName() + " " +
                                    DateUtils.format(purchaseOrder.getMonthDate(), "yyyy-MM") + " " +
                                    purchaseOrder.getElementNo() + " " +
                                    purchaseOrder.getMaterialName();
                    map.put(msg, msg);
                } else {
                    purchaseOrder.setIsChangeConfirm(1);
                    if(purchaseOrder.getConfirmVersion() == null) {
                        purchaseOrder.setConfirmVersion(0);
                    }
                    purchaseOrder.setConfirmVersion(purchaseOrder.getConfirmVersion() + 1);
                    purchaseOrder.setLastConfirmTime(new Date());
                    if(purchaseOrder.getSerialId() != null) {
                        serialIds.add(purchaseOrder.getSerialId());
                    }
                }
            }

            if(map!=null && map.size()>0) {
                for(Object obj : map.keySet()) {
                    errorMsg += ("[" + map.get(obj) + "] 已确认<br/>");
                }
            }

            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }

            this.materialPlanService.confirmChange(serialIds, user);
            this.saveOrUpdateBatch(list);


        } else {
            throw new BusinessException("选择的数据已不存在，请刷新!");
        }

        return result;
    }


    public List<DeliveryNoteDetailEO> selectSupplierMonthReport(Criteria criteria) throws BusinessException{
        List<DeliveryNoteDetailEO> pageList = new ArrayList<>();
        List<DeliveryNoteDetailEO> tempList = new ArrayList<>();
        List<DeliveryNoteDetailEO> returnList = new ArrayList<>();
        int current = criteria.getCurrentPage();
        int size = criteria.getSize();
        Map mapCondition = criteriaToMap(criteria);
        Calendar calendar = Calendar.getInstance();
        pageList = this.baseMapper.selectSupplierMonthReport(mapCondition);

        Map<Object,DeliveryNoteDetailEO> map = new LinkedHashMap();

        if(null != pageList && pageList.size() > 0){
            for(DeliveryNoteDetailEO detailEO:pageList){
                if(null != map.get(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getSupplierId())){
                    //若存在则更新已存在数据的需求数
                    calendar.setTime(detailEO.getCurrentDay());
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    setCurrentDay(map.get(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getSupplierId()),detailEO,day);
                }else{
                    calendar.setTime(detailEO.getCurrentDay());
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    detailEO.setTotalActualReceiveQuantity(0d);
                    detailEO.setTotalDeliveryQuantity(0d);
                    detailEO.setTotalNotQualifiedQuantity(0d);
                    setCurrentDay(detailEO,detailEO,day);
                    map.put(detailEO.getMonthDate()+"==="+detailEO.getMaterialId()+"==="+detailEO.getSupplierId(),detailEO);
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

    public void setCurrentDay(DeliveryNoteDetailEO entity,DeliveryNoteDetailEO temp,int day){

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

    public List<PurchaseOrderEO> getPageByParentSerialId(Map map) {
        return this.baseMapper.getPageByParentSerialId(map);
    }
}
