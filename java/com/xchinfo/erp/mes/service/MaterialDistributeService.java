package com.xchinfo.erp.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.MaterialSupplierEO;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.mes.mapper.MaterialDistributeMapper;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialDistributeService extends BaseServiceImpl<MaterialDistributeMapper, MaterialDistributeEO> {



    @Autowired
    private IDubPurchaseOrderService dubPurchaseOrderService;

    @Autowired
    private OrgService orgService;


    public IPage<MaterialDistributeEO> selectPage(Criteria criteria) throws BusinessException {
        IPage<MaterialDistributeEO> page = super.selectPage(criteria);
        List<MaterialDistributeEO> materialDistributes = page.getRecords();
        if(materialDistributes!=null && materialDistributes.size()>0) {
            String sqlStr = "";
            for(MaterialDistributeEO materialDistribute : materialDistributes) {
                sqlStr += (materialDistribute.getSerialDistributeId() + ",");
            }
            if(!"".equals(sqlStr)) {
                sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
            } else {
                sqlStr = "(-1)";
            }
            List<PurchaseOrderEO> purchaseOrders = this.dubPurchaseOrderService.getBySerialDistributeIds(sqlStr);
            if(purchaseOrders!=null && purchaseOrders.size()>0) {
                for(MaterialDistributeEO materialDistribute : materialDistributes) {
                    for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
                        if(purchaseOrder.getSerialDistributeId()!=null &&
                            materialDistribute.getSerialDistributeId().longValue() == purchaseOrder.getSerialDistributeId().longValue()) {
                            materialDistribute.setCreateDate(purchaseOrder.getCreateDate());
                            materialDistribute.setPlanArriveDate(purchaseOrder.getPlanArriveDate());
                        }
                    }
                }
            }
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = MaterialDistributeEO.class)
    public boolean save(MaterialDistributeEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        //判断计划是否存在
        Integer count = this.baseMapper.selectExistCount(entity.getMaterialId(),entity.getSupplierId(),entity.getWeekDate(),entity.getDistributeType());


        if (entity.getDistributeType() == 1){
            // 生成采购流水
            String code = this.generateNextCode("cmp_material_distribute_purchase", entity,user.getOrgId());
            AssertUtils.isBlank(code);
            entity.setSerialPurchaseCode(code);

        }else if(entity.getDistributeType() == 2){
            //委外计划存在则报错
//            if(count > 0){
//                throw new BusinessException("该委外计划已存在，请确认");
//            }
            // 生成委外流水
            String code = this.generateNextCode("cmp_material_distribute_out", entity,user.getOrgId());
            AssertUtils.isBlank(code);
            entity.setSerialOutSourceCode(code);

        }else if(entity.getDistributeType() == 3){
            // 生成生产流水
            String code = this.generateNextCode("cmp_material_distribute_product", entity,user.getOrgId());
            AssertUtils.isBlank(code);
            entity.setSerialProductCode(code);

        }else{
            throw new BusinessException("类型不对");
        }


        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBySerialId(Long Id) throws BusinessException {

        this.baseMapper.deleteBySerialId(Id);

        return true;
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MaterialDistributeEO entity, Long userId) throws BusinessException {

        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("物料计划归属机构权限不存在该用户权限，请确认！");
        }

        //校验修改后的计划是否存在
        Integer count = this.baseMapper.selectExistCountById(entity.getMaterialId(),entity.getSupplierId(),entity.getWeekDate(),entity.getSerialDistributeId(),entity.getDistributeType());
        if(count > 0){
            throw new BusinessException("修改后的委外计划已存在，请确认");
        }

        return super.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateStatusById(Long[] ids, Integer status, String action,Long userId,String userName) throws BusinessException {

        //状态已被更新的记录
        String oneMsg = "";

        //成功的信息
        String successMsg = "";

        //失败的信息
        String failMsg = "";

        //流水号
        String transCode = "";

        String msg = "";

        //设置采购计划，委外计划，生产计划状态
        for(Long id:ids){
            MaterialDistributeEO entity = this.getById(id);

            //若已被删除，则直接跳下一条
            if(null == entity){
                continue;
            }

            //校验机构权限(权限不足，则是因为机构权限被收回，操作后会刷新数据，机构权限不存在的数据将看不到了)
            if(!checkPer(entity.getOrgId(),userId)){
                continue;
            }

            if(entity.getDistributeType() == 1){
                transCode = entity.getSerialPurchaseCode();
            }else if(entity.getDistributeType() == 2){
                transCode = entity.getSerialOutSourceCode();
            }else if(entity.getDistributeType() == 3){
                transCode = entity.getSerialProductCode();
            }

            //发布
            if ("release".equals(action) && entity.getStatus() == 0 ){

                //若是发布,则往相应的订单中新增数据
                this.baseMapper.updateStatusById(id,status);

                //采购
                if(entity.getDistributeType() == 1){
                    //采购订单数据
//                    PurchaseOrderEO saveEntity = new PurchaseOrderEO();
//                    transFormPurchase(entity,saveEntity,"purchase",userId,userName);
//                    ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
//                    this.dubPurchaseOrderService.save(saveEntity);

                    //委外
                }else if(entity.getDistributeType() == 2){
                    //委外订单数据
                    PurchaseOrderEO saveEntity = new PurchaseOrderEO();
                    transFormPurchase(entity,saveEntity,"out",userId,userName);
                    ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
                    this.dubPurchaseOrderService.save(saveEntity);

                    //生产
                }else if(entity.getDistributeType() == 3){
                    //第二阶段

                }

                //发布信息
                if("".equals(successMsg)){
                    successMsg = "计划"+transCode+" 发布成功!<br/>";
                }else{
                    successMsg = successMsg + "计划"+transCode+" 发布成功!<br/>";
                }

                //取消发布
            }else if ("cancelRelease".equals(action) &&  entity.getStatus() == 1){

                Integer count = 0;

                //采购
                if(entity.getDistributeType() == 1){
                    //判断采购订单状态
                    count = this.baseMapper.selectPuchaseCount(id);

                    //委外
                }else if(entity.getDistributeType() == 2){
                    //判断委外订单状态
                    count = this.baseMapper.selectPuchaseCount(id);
                    //生产
                }else if(entity.getDistributeType() == 3){
                    //第二阶段

                }

                //不存在，则更新主表
                if(count == 0){
                    //取消发布
                    this.baseMapper.updateStatusById(id,status);
                    //删除子数据
                    if(entity.getDistributeType() == 1){
                        //删除采购订单数据
                        this.baseMapper.deletePuchaseById(id);

                    }else if(entity.getDistributeType() == 2){
                        //删除委外订单数据
                        this.baseMapper.deletePuchaseById(id);
                    }else if(entity.getDistributeType() == 3){
                        //删除采购订单数据

                    }

                    //取消发布信息
                    if("".equals(successMsg)){
                        successMsg = "计划"+transCode+" 取消发布成功!<br/>";
                    }else{
                        successMsg = successMsg + "计划"+transCode+" 取消发布成功!<br/>";
                    }
                }else{
                    //取消发布信息失败
                    if("".equals(failMsg)){
                        failMsg = "计划"+transCode+" 存在子订单不是新建状态，取消发布失败!<br/>";
                    }else{
                        failMsg = failMsg + "计划"+transCode+" 存在子订单不是新建状态，取消发布失败!<br/>";
                    }
                }



                //完成
            }else if("finish".equals(action)){

                this.baseMapper.updateStatusById(id,status);
            }else{

                if("".equals(oneMsg)){
                    oneMsg = "计划"+transCode+" 已不是最新状态，操作失败!<br/>";
                }else{
                    oneMsg = oneMsg + "计划"+transCode+" 已不是最新状态，操作失败!<br/>";
                }

            }

        }

        //组装返回数据
        if(!"".equals(successMsg)){
            //成功信息
            msg = msg + successMsg;
        }
        if(!"".equals(oneMsg)){
            //已被更改信息
            msg = msg + oneMsg;
        }
        if(!"".equals(failMsg)){
            //失败信息
            msg = msg + failMsg;
        }

        return msg;
    }

    //订单数据转换
    public void transFormPurchase(MaterialDistributeEO entity, PurchaseOrderEO saveEntity,String action,Long userId,String userName){
        //设置属性
        saveEntity.setSupplierId(entity.getSupplierId());
        saveEntity.setSerialDistributeId(entity.getSerialDistributeId());
        saveEntity.setOrgId(entity.getOrgId());
        saveEntity.setMaterialId(entity.getMaterialId());
        saveEntity.setMaterialCode(entity.getMaterialCode());
        saveEntity.setMaterialName(entity.getMaterialName());
        saveEntity.setSpecification(entity.getSpecification());
        saveEntity.setInventoryCode(entity.getInventoryCode());
        saveEntity.setElementNo(entity.getElementNo());
        saveEntity.setUnitName(entity.getUnitName());
        if("purchase".equals(action)){
            saveEntity.setType(1);
        }else{
            saveEntity.setType(2);
        }
        saveEntity.setCreateDate(DateUtils.stringToDate(DateUtils.getDateTime("yyyy-MM-dd"),"yyyy-MM-dd"));
        saveEntity.setPlanArriveDate(entity.getWeekDate());
        saveEntity.setPlanDeliveryQuantity(entity.getRequireCount());
        saveEntity.setNotDeliveryQuantity(0d);
        saveEntity.setActualDeliveryQuantity(0d);
        saveEntity.setQualifiedQuantity(0d);
        saveEntity.setReturnedQuantity(0d);
        saveEntity.setCreateUserId(userId);
        saveEntity.setCreateUserName(userName);
        //saveEntity.setChargeUserId(userId);
        //saveEntity.setChargeUserName(userName);
        saveEntity.setStatus(0);
        saveEntity.setPreStatus(0);


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

    public Integer selectPlanFinishCount(Long Id) {
        return this.baseMapper.selectPlanFinishCount(Id);
    }


    public List<MaterialSupplierEO> importPlanExecl(List list, String monthDate) throws BusinessException {

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
                            continue;
                        }

                        //供应商名称
                        String supplierName = (String) map.get("2");
                        if(null==supplierName || supplierName.isEmpty()){
                            continue;
                        }

                        //查询供应商，物料关系是否存在
                        MaterialSupplierEO materialSupplierEO = this.baseMapper.selectSupplierMaterial(supplierName,elementNo);
                        if(null == materialSupplierEO){
                            logger.info("========物料供应商不存在========"+elementNo);
                            continue;
                        }


                        //不允许采购
                        if(materialSupplierEO.getIsPurchase()!=1){
                            logger.info("========不允许采购========"+elementNo);
                            materialSupplierEO.setErrImportType(1);
                            planList.add(materialSupplierEO);
                            continue;
                        }

                        //下周一日期
//                        Date nestWeek = sdf.parse(sdf.format(calendar.getTime()));

                        //遍历查询单元格数据
                        for(int h=1;h<=days;h++){
                            //单元格数据
                            int j = h + 3;

                            //计划数量
                            String planCount = (String) map.get(""+j);
                            if(null == planCount || "0".equals(planCount)  || planCount.isEmpty()){
                                logger.info("========计划数量值不存在========"+elementNo);
                                continue;
                            }

                            //设置日计划日期
                            if (h<10){
                                dateStr = monthDate + "-0" +h;
                            }else{
                                dateStr = monthDate + "-" +h;
                            }
                            Date date = DateUtils.stringToDate(dateStr,"yyyy-MM-dd");

                            MaterialSupplierEO one = this.baseMapper.selectSupplierMaterial(supplierName,elementNo);

                            //判断日计划是否存在
                            Integer count = this.baseMapper.selectExistCount(materialSupplierEO.getMaterialId(),materialSupplierEO.getSupplierId(),date,1);
                            if(count > 0){
                                one.setErrImportType(2);
                                planList.add(one);
                                continue;
                            }



                            one.setWeekDate(date);
                            one.setRequireCount(Double.valueOf(planCount));

                            calendar.setTime(date);
                            int week = calendar.get(Calendar.WEEK_OF_YEAR);
                            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
                            if(weekday == 1){
                                week = week -1;
                            }

                            one.setWeeks(week);
                            one.setErrImportType(0);
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

        return planList;
    }
}
