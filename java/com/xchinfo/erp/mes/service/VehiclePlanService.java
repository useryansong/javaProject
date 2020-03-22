package com.xchinfo.erp.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.mapper.SupplierMapper;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.entity.VehiclePlanEO;
import com.xchinfo.erp.mes.entity.VehicleTrainPlanEO;
import com.xchinfo.erp.mes.mapper.VehiclePlanMapper;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhongy
 * @date 2019/6/20
 */
@Service
public class VehiclePlanService extends BaseServiceImpl<VehiclePlanMapper, VehiclePlanEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private OrgService orgService;


    @Autowired
    private DubDeliveryOrderService dubDeliveryOrderService;


    @Autowired
    private DubDeliveryOrderDetailService dubDeliveryOrderDetailService;


    @Autowired
    private VehicleTrainPlanService vehicleTrainPlanService;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private SupplierService supplierService;


    @Transactional(rollbackFor = Exception.class)
    public boolean saveTrain(VehicleTrainPlanEO entity) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "选择的归属机构权限不存在该用户权限,请确认!");
        // 是否存在
        VehicleTrainPlanEO vehicleTrainPlanEO = this.baseMapper.selectExistEntity(entity.getTrainDate(),entity.getOrgId(),entity.getTrainNumber());

        if(null == vehicleTrainPlanEO){
            if(null == entity.getFreight() || "".equals(entity.getFreight())){
                entity.setFreight(0d);
            }
            this.vehicleTrainPlanService.save(entity);
            //初次设置直接推送到送货单
            this.baseMapper.updateDOrserTranTime(entity);

        }else{
            //校验是否存在明细已完成的数据
            Integer count = this.baseMapper.selectFinishCount(entity);
            if(count > 0){
                throw new BusinessException("当前车次存在发货单明细已完成的数据，不允许操作！");
            }

            vehicleTrainPlanEO.setVehicleType(entity.getVehicleType());
            vehicleTrainPlanEO.setTrainTime(entity.getTrainTime());
            if(null == entity.getFreight() || "".equals(entity.getFreight())){
                vehicleTrainPlanEO.setFreight(0d);
            }else{
                vehicleTrainPlanEO.setFreight(entity.getFreight());
            }

            this.vehicleTrainPlanService.updateById(vehicleTrainPlanEO);
            this.baseMapper.updateDOrserTranTime(entity);
        }

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean save(VehiclePlanEO entity, Long userId) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
//        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "选择的归属机构权限不存在该用户权限,请确认!");
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("cmp_vehicle_plan", entity,user.getOrgId());
        AssertUtils.isBlank(code);
        entity.setVehiclePlanNo(code);
        entity.setStatus(0);
        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(VehiclePlanEO entity, Long userId) throws BusinessException {
//        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "选择的归属机构权限不存在该用户权限,请确认!");
        return super.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Long[] ids, Long userId) throws BusinessException {
        for(Long id : ids) {
//            VehiclePlanEO vehiclePlan = super.getById(id);
//            Boolean flag = this.orgService.checkUserPermissions(vehiclePlan.getOrgId(), userId);
//            if(flag.booleanValue()) {
//                super.removeById(id);
//            }
            super.removeById(id);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean release(Long[] ids, Long userId) throws BusinessException {
        for(Long id : ids) {
            VehiclePlanEO vehiclePlan = super.getById(id);
//            Boolean flag = this.orgService.checkUserPermissions(vehiclePlan.getOrgId(), userId);
//            if(flag.booleanValue()) {
//                vehiclePlan.setStatus(1);
//                super.updateById(vehiclePlan);
//            }
            vehiclePlan.setStatus(1);
            super.updateById(vehiclePlan);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRelease(Long[] ids, Long userId) throws BusinessException {
        for(Long id : ids) {
            VehiclePlanEO vehiclePlan = super.getById(id);
//            Boolean flag = this.orgService.checkUserPermissions(vehiclePlan.getOrgId(), userId);
//            if(flag.booleanValue()) {
//                vehiclePlan.setStatus(0);
//                super.updateById(vehiclePlan);
//            }
            vehiclePlan.setStatus(0);
            super.updateById(vehiclePlan);
        }
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean createAllPlan(String date, UserEO userEO) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

        //查询所有当日的周计划
        Date weekDate = DateUtils.stringToDate(date,"yyyy-MM-dd");

        //当日是否已存在排车计划
        Integer count = this.baseMapper.selectExistCount(weekDate,userEO.getOrgId());
        if(count > 0){
            throw new BusinessException("当日排车计划已存在，请确认");
        }

        List<MaterialPlanEO> materialPlanEOList = this.baseMapper.selectWeekPlanByDate(weekDate,userEO.getOrgId());

        if(null != materialPlanEOList && materialPlanEOList.size() > 0){
            //生产排产计划
            for(MaterialPlanEO entity:materialPlanEOList){

                VehiclePlanEO vehiclePlanEO =new VehiclePlanEO();
                vehiclePlanEO.setOrgId(userEO.getOrgId());
                vehiclePlanEO.setDeliveryDate(entity.getWeekDate());
                vehiclePlanEO.setMaterialId(entity.getMaterialId());
                vehiclePlanEO.setMaterialCode(entity.getMaterialCode());
                vehiclePlanEO.setMaterialName(entity.getMaterialName());
                vehiclePlanEO.setCustomerId(entity.getCustomerId());
                vehiclePlanEO.setCustomerCode(entity.getCustomerCode());
                vehiclePlanEO.setCustomerName(entity.getCustomerName());
                vehiclePlanEO.setElementNo(entity.getElementNo());
                vehiclePlanEO.setProjectNo(entity.getProjectNo());
                vehiclePlanEO.setAmount(entity.getRequireCount());
                vehiclePlanEO.setSnp(entity.getSnp());
                vehiclePlanEO.setStatus(0);
                String code = this.generateNextCode("cmp_vehicle_plan", entity,user.getOrgId());
                AssertUtils.isBlank(code);
                vehiclePlanEO.setVehiclePlanNo(code);
                //保存
                super.save(vehiclePlanEO);
            }

        }else {
            throw new BusinessException("当前日期不存在周计划，请确认");
        }


        return true;
    }

    public void monthSum(VehiclePlanEO footPlan,VehiclePlanEO vehiclePlanFour,VehiclePlanEO entity,String numberStr){
        if("01".equals(numberStr)) {
            if(null == footPlan.getTrainNumber01() || footPlan.getTrainNumber01().isEmpty()){
                footPlan.setTrainNumber01("0");
            }
            if(null == vehiclePlanFour.getTrainNumber01() || vehiclePlanFour.getTrainNumber01().isEmpty()){
                vehiclePlanFour.setTrainNumber01("0");
            }

            footPlan.setTrainNumber01(String.valueOf(Double.valueOf(footPlan.getTrainNumber01()) + Double.valueOf(entity.getTrainNumber01())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber01(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber01()) + (Double.valueOf(entity.getTrainNumber01()) / entity.getSnp())));
            }
        }else if ("02".equals(numberStr)){
            if(null == footPlan.getTrainNumber02() || footPlan.getTrainNumber02().isEmpty()){
                footPlan.setTrainNumber02("0");
            }
            if(null == vehiclePlanFour.getTrainNumber02() || vehiclePlanFour.getTrainNumber02().isEmpty()){
                vehiclePlanFour.setTrainNumber02("0");
            }

            footPlan.setTrainNumber02(String.valueOf(Double.valueOf(footPlan.getTrainNumber02()) + Double.valueOf(entity.getTrainNumber02())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber02(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber02()) + (Double.valueOf(entity.getTrainNumber02()) / entity.getSnp())));
            }
        }else if ("03".equals(numberStr)){
            if(null == footPlan.getTrainNumber03() || footPlan.getTrainNumber03().isEmpty()){
                footPlan.setTrainNumber03("0");
            }
            if(null == vehiclePlanFour.getTrainNumber03() || vehiclePlanFour.getTrainNumber03().isEmpty()){
                vehiclePlanFour.setTrainNumber03("0");
            }

            footPlan.setTrainNumber03(String.valueOf(Double.valueOf(footPlan.getTrainNumber03()) + Double.valueOf(entity.getTrainNumber03())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber03(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber03()) + (Double.valueOf(entity.getTrainNumber03()) / entity.getSnp())));
            }
        }else if ("04".equals(numberStr)){
            if(null == footPlan.getTrainNumber04() || footPlan.getTrainNumber04().isEmpty()){
                footPlan.setTrainNumber04("0");
            }
            if(null == vehiclePlanFour.getTrainNumber04() || vehiclePlanFour.getTrainNumber04().isEmpty()){
                vehiclePlanFour.setTrainNumber04("0");
            }

            footPlan.setTrainNumber04(String.valueOf(Double.valueOf(footPlan.getTrainNumber04()) + Double.valueOf(entity.getTrainNumber04())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber04(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber04()) + (Double.valueOf(entity.getTrainNumber04()) / entity.getSnp())));
            }
        }else if ("05".equals(numberStr)){
            if(null == footPlan.getTrainNumber05() || footPlan.getTrainNumber05().isEmpty()){
                footPlan.setTrainNumber05("0");
            }
            if(null == vehiclePlanFour.getTrainNumber05() || vehiclePlanFour.getTrainNumber05().isEmpty()){
                vehiclePlanFour.setTrainNumber05("0");
            }

            footPlan.setTrainNumber05(String.valueOf(Double.valueOf(footPlan.getTrainNumber05()) + Double.valueOf(entity.getTrainNumber05())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber05(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber05()) + (Double.valueOf(entity.getTrainNumber05()) / entity.getSnp())));
            }
        }else if ("06".equals(numberStr)){
            if(null == footPlan.getTrainNumber06() || footPlan.getTrainNumber06().isEmpty()){
                footPlan.setTrainNumber06("0");
            }
            if(null == vehiclePlanFour.getTrainNumber06() || vehiclePlanFour.getTrainNumber06().isEmpty()){
                vehiclePlanFour.setTrainNumber06("0");
            }

            footPlan.setTrainNumber06(String.valueOf(Double.valueOf(footPlan.getTrainNumber06()) + Double.valueOf(entity.getTrainNumber06())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber06(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber06()) + (Double.valueOf(entity.getTrainNumber06()) / entity.getSnp())));
            }
        }else if ("07".equals(numberStr)){
            if(null == footPlan.getTrainNumber07() || footPlan.getTrainNumber07().isEmpty()){
                footPlan.setTrainNumber07("0");
            }
            if(null == vehiclePlanFour.getTrainNumber07() || vehiclePlanFour.getTrainNumber07().isEmpty()){
                vehiclePlanFour.setTrainNumber07("0");
            }

            footPlan.setTrainNumber07(String.valueOf(Double.valueOf(footPlan.getTrainNumber07()) + Double.valueOf(entity.getTrainNumber07())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber07(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber07()) + (Double.valueOf(entity.getTrainNumber07()) / entity.getSnp())));
            }
        }else if ("08".equals(numberStr)){
            if(null == footPlan.getTrainNumber08() || footPlan.getTrainNumber08().isEmpty()){
                footPlan.setTrainNumber08("0");
            }
            if(null == vehiclePlanFour.getTrainNumber08() || vehiclePlanFour.getTrainNumber08().isEmpty()){
                vehiclePlanFour.setTrainNumber08("0");
            }

            footPlan.setTrainNumber08(String.valueOf(Double.valueOf(footPlan.getTrainNumber08()) + Double.valueOf(entity.getTrainNumber08())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber08(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber08()) + (Double.valueOf(entity.getTrainNumber08()) / entity.getSnp())));
            }
        }else if ("09".equals(numberStr)){
            if(null == footPlan.getTrainNumber09() || footPlan.getTrainNumber09().isEmpty()){
                footPlan.setTrainNumber09("0");
            }
            if(null == vehiclePlanFour.getTrainNumber09() || vehiclePlanFour.getTrainNumber09().isEmpty()){
                vehiclePlanFour.setTrainNumber09("0");
            }

            footPlan.setTrainNumber09(String.valueOf(Double.valueOf(footPlan.getTrainNumber09()) + Double.valueOf(entity.getTrainNumber09())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber09(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber09()) + (Double.valueOf(entity.getTrainNumber09()) / entity.getSnp())));
            }
        }else if ("10".equals(numberStr)){
            if(null == footPlan.getTrainNumber10() || footPlan.getTrainNumber10().isEmpty()){
                footPlan.setTrainNumber10("0");
            }
            if(null == vehiclePlanFour.getTrainNumber10() || vehiclePlanFour.getTrainNumber10().isEmpty()){
                vehiclePlanFour.setTrainNumber10("0");
            }

            footPlan.setTrainNumber10(String.valueOf(Double.valueOf(footPlan.getTrainNumber10()) + Double.valueOf(entity.getTrainNumber10())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber10(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber10()) + (Double.valueOf(entity.getTrainNumber10()) / entity.getSnp())));
            }
        }else if ("11".equals(numberStr)){
            if(null == footPlan.getTrainNumber11() || footPlan.getTrainNumber11().isEmpty()){
                footPlan.setTrainNumber11("0");
            }
            if(null == vehiclePlanFour.getTrainNumber11() || vehiclePlanFour.getTrainNumber11().isEmpty()){
                vehiclePlanFour.setTrainNumber11("0");
            }

            footPlan.setTrainNumber11(String.valueOf(Double.valueOf(footPlan.getTrainNumber11()) + Double.valueOf(entity.getTrainNumber11())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber11(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber11()) + (Double.valueOf(entity.getTrainNumber11()) / entity.getSnp())));
            }
        }else if ("12".equals(numberStr)){
            if(null == footPlan.getTrainNumber12() || footPlan.getTrainNumber12().isEmpty()){
                footPlan.setTrainNumber12("0");
            }
            if(null == vehiclePlanFour.getTrainNumber12() || vehiclePlanFour.getTrainNumber12().isEmpty()){
                vehiclePlanFour.setTrainNumber12("0");
            }

            footPlan.setTrainNumber12(String.valueOf(Double.valueOf(footPlan.getTrainNumber12()) + Double.valueOf(entity.getTrainNumber12())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber12(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber12()) + (Double.valueOf(entity.getTrainNumber12()) / entity.getSnp())));
            }
        }else if ("13".equals(numberStr)){
            if(null == footPlan.getTrainNumber13() || footPlan.getTrainNumber13().isEmpty()){
                footPlan.setTrainNumber13("0");
            }
            if(null == vehiclePlanFour.getTrainNumber13() || vehiclePlanFour.getTrainNumber13().isEmpty()){
                vehiclePlanFour.setTrainNumber13("0");
            }

            footPlan.setTrainNumber13(String.valueOf(Double.valueOf(footPlan.getTrainNumber13()) + Double.valueOf(entity.getTrainNumber13())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber13(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber13()) + (Double.valueOf(entity.getTrainNumber13()) / entity.getSnp())));
            }
        }else if ("14".equals(numberStr)){
            if(null == footPlan.getTrainNumber14() || footPlan.getTrainNumber14().isEmpty()){
                footPlan.setTrainNumber14("0");
            }
            if(null == vehiclePlanFour.getTrainNumber14() || vehiclePlanFour.getTrainNumber14().isEmpty()){
                vehiclePlanFour.setTrainNumber14("0");
            }

            footPlan.setTrainNumber14(String.valueOf(Double.valueOf(footPlan.getTrainNumber14()) + Double.valueOf(entity.getTrainNumber14())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber14(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber14()) + (Double.valueOf(entity.getTrainNumber14()) / entity.getSnp())));
            }
        }else if ("15".equals(numberStr)){
            if(null == footPlan.getTrainNumber15() || footPlan.getTrainNumber15().isEmpty()){
                footPlan.setTrainNumber15("0");
            }
            if(null == vehiclePlanFour.getTrainNumber15() || vehiclePlanFour.getTrainNumber15().isEmpty()){
                vehiclePlanFour.setTrainNumber15("0");
            }

            footPlan.setTrainNumber15(String.valueOf(Double.valueOf(footPlan.getTrainNumber15()) + Double.valueOf(entity.getTrainNumber15())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber15(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber15()) + (Double.valueOf(entity.getTrainNumber15()) / entity.getSnp())));
            }
        }else if ("16".equals(numberStr)){
            if(null == footPlan.getTrainNumber16() || footPlan.getTrainNumber16().isEmpty()){
                footPlan.setTrainNumber16("0");
            }
            if(null == vehiclePlanFour.getTrainNumber16() || vehiclePlanFour.getTrainNumber16().isEmpty()){
                vehiclePlanFour.setTrainNumber16("0");
            }

            footPlan.setTrainNumber16(String.valueOf(Double.valueOf(footPlan.getTrainNumber16()) + Double.valueOf(entity.getTrainNumber16())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber16(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber16()) + (Double.valueOf(entity.getTrainNumber16()) / entity.getSnp())));
            }
        }else if ("17".equals(numberStr)){
            if(null == footPlan.getTrainNumber17() || footPlan.getTrainNumber17().isEmpty()){
                footPlan.setTrainNumber17("0");
            }
            if(null == vehiclePlanFour.getTrainNumber17() || vehiclePlanFour.getTrainNumber17().isEmpty()){
                vehiclePlanFour.setTrainNumber17("0");
            }

            footPlan.setTrainNumber17(String.valueOf(Double.valueOf(footPlan.getTrainNumber17()) + Double.valueOf(entity.getTrainNumber17())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber17(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber17()) + (Double.valueOf(entity.getTrainNumber17()) / entity.getSnp())));
            }
        }else if ("18".equals(numberStr)){
            if(null == footPlan.getTrainNumber18() || footPlan.getTrainNumber18().isEmpty()){
                footPlan.setTrainNumber18("0");
            }
            if(null == vehiclePlanFour.getTrainNumber18() || vehiclePlanFour.getTrainNumber18().isEmpty()){
                vehiclePlanFour.setTrainNumber18("0");
            }

            footPlan.setTrainNumber18(String.valueOf(Double.valueOf(footPlan.getTrainNumber18()) + Double.valueOf(entity.getTrainNumber18())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber18(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber18()) + (Double.valueOf(entity.getTrainNumber18()) / entity.getSnp())));
            }
        }else if ("19".equals(numberStr)){
            if(null == footPlan.getTrainNumber19() || footPlan.getTrainNumber19().isEmpty()){
                footPlan.setTrainNumber19("0");
            }
            if(null == vehiclePlanFour.getTrainNumber19() || vehiclePlanFour.getTrainNumber19().isEmpty()){
                vehiclePlanFour.setTrainNumber19("0");
            }

            footPlan.setTrainNumber19(String.valueOf(Double.valueOf(footPlan.getTrainNumber19()) + Double.valueOf(entity.getTrainNumber19())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber19(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber19()) + (Double.valueOf(entity.getTrainNumber19()) / entity.getSnp())));
            }
        }else if ("20".equals(numberStr)){
            if(null == footPlan.getTrainNumber20() || footPlan.getTrainNumber20().isEmpty()){
                footPlan.setTrainNumber20("0");
            }
            if(null == vehiclePlanFour.getTrainNumber20() || vehiclePlanFour.getTrainNumber20().isEmpty()){
                vehiclePlanFour.setTrainNumber20("0");
            }

            footPlan.setTrainNumber20(String.valueOf(Double.valueOf(footPlan.getTrainNumber20()) + Double.valueOf(entity.getTrainNumber20())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber20(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber20()) + (Double.valueOf(entity.getTrainNumber20()) / entity.getSnp())));
            }
        }else if ("21".equals(numberStr)){
            if(null == footPlan.getTrainNumber21() || footPlan.getTrainNumber21().isEmpty()){
                footPlan.setTrainNumber21("0");
            }
            if(null == vehiclePlanFour.getTrainNumber21() || vehiclePlanFour.getTrainNumber21().isEmpty()){
                vehiclePlanFour.setTrainNumber21("0");
            }

            footPlan.setTrainNumber21(String.valueOf(Double.valueOf(footPlan.getTrainNumber21()) + Double.valueOf(entity.getTrainNumber21())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber21(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber21()) + (Double.valueOf(entity.getTrainNumber21()) / entity.getSnp())));
            }
        }else if ("22".equals(numberStr)){
            if(null == footPlan.getTrainNumber22() || footPlan.getTrainNumber22().isEmpty()){
                footPlan.setTrainNumber22("0");
            }
            if(null == vehiclePlanFour.getTrainNumber22() || vehiclePlanFour.getTrainNumber22().isEmpty()){
                vehiclePlanFour.setTrainNumber22("0");
            }

            footPlan.setTrainNumber22(String.valueOf(Double.valueOf(footPlan.getTrainNumber22()) + Double.valueOf(entity.getTrainNumber22())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber22(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber22()) + (Double.valueOf(entity.getTrainNumber22()) / entity.getSnp())));
            }
        }else if ("23".equals(numberStr)){
            if(null == footPlan.getTrainNumber23() || footPlan.getTrainNumber23().isEmpty()){
                footPlan.setTrainNumber23("0");
            }
            if(null == vehiclePlanFour.getTrainNumber23() || vehiclePlanFour.getTrainNumber23().isEmpty()){
                vehiclePlanFour.setTrainNumber23("0");
            }

            footPlan.setTrainNumber23(String.valueOf(Double.valueOf(footPlan.getTrainNumber23()) + Double.valueOf(entity.getTrainNumber23())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber23(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber23()) + (Double.valueOf(entity.getTrainNumber23()) / entity.getSnp())));
            }
        }else if ("24".equals(numberStr)){
            if(null == footPlan.getTrainNumber24() || footPlan.getTrainNumber24().isEmpty()){
                footPlan.setTrainNumber24("0");
            }
            if(null == vehiclePlanFour.getTrainNumber24() || vehiclePlanFour.getTrainNumber24().isEmpty()){
                vehiclePlanFour.setTrainNumber24("0");
            }

            footPlan.setTrainNumber24(String.valueOf(Double.valueOf(footPlan.getTrainNumber24()) + Double.valueOf(entity.getTrainNumber24())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber24(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber24()) + (Double.valueOf(entity.getTrainNumber24()) / entity.getSnp())));
            }
        }else if ("25".equals(numberStr)){
            if(null == footPlan.getTrainNumber25() || footPlan.getTrainNumber25().isEmpty()){
                footPlan.setTrainNumber25("0");
            }
            if(null == vehiclePlanFour.getTrainNumber25() || vehiclePlanFour.getTrainNumber25().isEmpty()){
                vehiclePlanFour.setTrainNumber25("0");
            }

            footPlan.setTrainNumber25(String.valueOf(Double.valueOf(footPlan.getTrainNumber25()) + Double.valueOf(entity.getTrainNumber25())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber25(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber25()) + (Double.valueOf(entity.getTrainNumber25()) / entity.getSnp())));
            }
        }else if ("26".equals(numberStr)){
            if(null == footPlan.getTrainNumber26() || footPlan.getTrainNumber26().isEmpty()){
                footPlan.setTrainNumber26("0");
            }
            if(null == vehiclePlanFour.getTrainNumber26() || vehiclePlanFour.getTrainNumber26().isEmpty()){
                vehiclePlanFour.setTrainNumber26("0");
            }

            footPlan.setTrainNumber26(String.valueOf(Double.valueOf(footPlan.getTrainNumber26()) + Double.valueOf(entity.getTrainNumber26())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber26(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber26()) + (Double.valueOf(entity.getTrainNumber26()) / entity.getSnp())));
            }
        }else if ("27".equals(numberStr)){
            if(null == footPlan.getTrainNumber27() || footPlan.getTrainNumber27().isEmpty()){
                footPlan.setTrainNumber27("0");
            }
            if(null == vehiclePlanFour.getTrainNumber27() || vehiclePlanFour.getTrainNumber27().isEmpty()){
                vehiclePlanFour.setTrainNumber27("0");
            }

            footPlan.setTrainNumber27(String.valueOf(Double.valueOf(footPlan.getTrainNumber27()) + Double.valueOf(entity.getTrainNumber27())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber27(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber27()) + (Double.valueOf(entity.getTrainNumber27()) / entity.getSnp())));
            }
        }else if ("28".equals(numberStr)){
            if(null == footPlan.getTrainNumber28() || footPlan.getTrainNumber28().isEmpty()){
                footPlan.setTrainNumber28("0");
            }
            if(null == vehiclePlanFour.getTrainNumber28() || vehiclePlanFour.getTrainNumber28().isEmpty()){
                vehiclePlanFour.setTrainNumber28("0");
            }

            footPlan.setTrainNumber28(String.valueOf(Double.valueOf(footPlan.getTrainNumber28()) + Double.valueOf(entity.getTrainNumber28())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber28(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber28()) + (Double.valueOf(entity.getTrainNumber28()) / entity.getSnp())));
            }
        }else if ("29".equals(numberStr)){
            if(null == footPlan.getTrainNumber29() || footPlan.getTrainNumber29().isEmpty()){
                footPlan.setTrainNumber29("0");
            }
            if(null == vehiclePlanFour.getTrainNumber29() || vehiclePlanFour.getTrainNumber29().isEmpty()){
                vehiclePlanFour.setTrainNumber29("0");
            }

            footPlan.setTrainNumber29(String.valueOf(Double.valueOf(footPlan.getTrainNumber29()) + Double.valueOf(entity.getTrainNumber29())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber29(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber29()) + (Double.valueOf(entity.getTrainNumber29()) / entity.getSnp())));
            }
        }else if ("30".equals(numberStr)){
            if(null == footPlan.getTrainNumber30() || footPlan.getTrainNumber30().isEmpty()){
                footPlan.setTrainNumber30("0");
            }
            if(null == vehiclePlanFour.getTrainNumber30() || vehiclePlanFour.getTrainNumber30().isEmpty()){
                vehiclePlanFour.setTrainNumber30("0");
            }

            footPlan.setTrainNumber30(String.valueOf(Double.valueOf(footPlan.getTrainNumber30()) + Double.valueOf(entity.getTrainNumber30())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber30(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber30()) + (Double.valueOf(entity.getTrainNumber30()) / entity.getSnp())));
            }
        }else if ("31".equals(numberStr)){
            if(null == footPlan.getTrainNumber31() || footPlan.getTrainNumber31().isEmpty()){
                footPlan.setTrainNumber31("0");
            }
            if(null == vehiclePlanFour.getTrainNumber31() || vehiclePlanFour.getTrainNumber31().isEmpty()){
                vehiclePlanFour.setTrainNumber31("0");
            }

            footPlan.setTrainNumber31(String.valueOf(Double.valueOf(footPlan.getTrainNumber31()) + Double.valueOf(entity.getTrainNumber31())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber31(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber31()) + (Double.valueOf(entity.getTrainNumber31()) / entity.getSnp())));
            }
        }else if ("32".equals(numberStr)){
            if(null == footPlan.getTrainNumber32() || footPlan.getTrainNumber32().isEmpty()){
                footPlan.setTrainNumber32("0");
            }
            if(null == vehiclePlanFour.getTrainNumber32() || vehiclePlanFour.getTrainNumber32().isEmpty()){
                vehiclePlanFour.setTrainNumber32("0");
            }

            footPlan.setTrainNumber32(String.valueOf(Double.valueOf(footPlan.getTrainNumber32()) + Double.valueOf(entity.getTrainNumber32())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber32(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber32()) + (Double.valueOf(entity.getTrainNumber32()) / entity.getSnp())));
            }
        }else if ("33".equals(numberStr)){
            if(null == footPlan.getTrainNumber33() || footPlan.getTrainNumber33().isEmpty()){
                footPlan.setTrainNumber33("0");
            }
            if(null == vehiclePlanFour.getTrainNumber33() || vehiclePlanFour.getTrainNumber33().isEmpty()){
                vehiclePlanFour.setTrainNumber33("0");
            }

            footPlan.setTrainNumber33(String.valueOf(Double.valueOf(footPlan.getTrainNumber33()) + Double.valueOf(entity.getTrainNumber33())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber33(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber33()) + (Double.valueOf(entity.getTrainNumber33()) / entity.getSnp())));
            }
        }else if ("34".equals(numberStr)){
            if(null == footPlan.getTrainNumber34() || footPlan.getTrainNumber34().isEmpty()){
                footPlan.setTrainNumber34("0");
            }
            if(null == vehiclePlanFour.getTrainNumber34() || vehiclePlanFour.getTrainNumber34().isEmpty()){
                vehiclePlanFour.setTrainNumber34("0");
            }

            footPlan.setTrainNumber34(String.valueOf(Double.valueOf(footPlan.getTrainNumber34()) + Double.valueOf(entity.getTrainNumber34())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber34(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber34()) + (Double.valueOf(entity.getTrainNumber34()) / entity.getSnp())));
            }
        }else if ("35".equals(numberStr)){
            if(null == footPlan.getTrainNumber35() || footPlan.getTrainNumber35().isEmpty()){
                footPlan.setTrainNumber35("0");
            }
            if(null == vehiclePlanFour.getTrainNumber35() || vehiclePlanFour.getTrainNumber35().isEmpty()){
                vehiclePlanFour.setTrainNumber35("0");
            }

            footPlan.setTrainNumber35(String.valueOf(Double.valueOf(footPlan.getTrainNumber35()) + Double.valueOf(entity.getTrainNumber35())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber35(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber35()) + (Double.valueOf(entity.getTrainNumber35()) / entity.getSnp())));
            }
        }else if ("36".equals(numberStr)){
            if(null == footPlan.getTrainNumber36() || footPlan.getTrainNumber36().isEmpty()){
                footPlan.setTrainNumber36("0");
            }
            if(null == vehiclePlanFour.getTrainNumber36() || vehiclePlanFour.getTrainNumber36().isEmpty()){
                vehiclePlanFour.setTrainNumber36("0");
            }

            footPlan.setTrainNumber36(String.valueOf(Double.valueOf(footPlan.getTrainNumber36()) + Double.valueOf(entity.getTrainNumber36())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber36(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber36()) + (Double.valueOf(entity.getTrainNumber36()) / entity.getSnp())));
            }
        }else if ("37".equals(numberStr)){
            if(null == footPlan.getTrainNumber37() || footPlan.getTrainNumber37().isEmpty()){
                footPlan.setTrainNumber37("0");
            }
            if(null == vehiclePlanFour.getTrainNumber37() || vehiclePlanFour.getTrainNumber37().isEmpty()){
                vehiclePlanFour.setTrainNumber37("0");
            }

            footPlan.setTrainNumber37(String.valueOf(Double.valueOf(footPlan.getTrainNumber37()) + Double.valueOf(entity.getTrainNumber37())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber37(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber37()) + (Double.valueOf(entity.getTrainNumber37()) / entity.getSnp())));
            }
        }else if ("38".equals(numberStr)){
            if(null == footPlan.getTrainNumber38() || footPlan.getTrainNumber38().isEmpty()){
                footPlan.setTrainNumber38("0");
            }
            if(null == vehiclePlanFour.getTrainNumber38() || vehiclePlanFour.getTrainNumber38().isEmpty()){
                vehiclePlanFour.setTrainNumber38("0");
            }

            footPlan.setTrainNumber38(String.valueOf(Double.valueOf(footPlan.getTrainNumber38()) + Double.valueOf(entity.getTrainNumber38())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber38(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber38()) + (Double.valueOf(entity.getTrainNumber38()) / entity.getSnp())));
            }
        }else if ("39".equals(numberStr)){
            if(null == footPlan.getTrainNumber39() || footPlan.getTrainNumber39().isEmpty()){
                footPlan.setTrainNumber39("0");
            }
            if(null == vehiclePlanFour.getTrainNumber39() || vehiclePlanFour.getTrainNumber39().isEmpty()){
                vehiclePlanFour.setTrainNumber39("0");
            }

            footPlan.setTrainNumber39(String.valueOf(Double.valueOf(footPlan.getTrainNumber39()) + Double.valueOf(entity.getTrainNumber39())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber39(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber39()) + (Double.valueOf(entity.getTrainNumber39()) / entity.getSnp())));
            }
        }else if ("40".equals(numberStr)){
            if(null == footPlan.getTrainNumber40() || footPlan.getTrainNumber40().isEmpty()){
                footPlan.setTrainNumber40("0");
            }
            if(null == vehiclePlanFour.getTrainNumber40() || vehiclePlanFour.getTrainNumber40().isEmpty()){
                vehiclePlanFour.setTrainNumber40("0");
            }

            footPlan.setTrainNumber40(String.valueOf(Double.valueOf(footPlan.getTrainNumber40()) + Double.valueOf(entity.getTrainNumber40())));
            //计算架数
            if (null != entity.getSnp() && entity.getSnp() > 0) {
                vehiclePlanFour.setTrainNumber40(String.valueOf(Double.valueOf(vehiclePlanFour.getTrainNumber40()) + (Double.valueOf(entity.getTrainNumber40()) / entity.getSnp())));
            }
        }



    }

    public void setDefault(VehiclePlanEO entity){
        entity.setTrainNumber01("0.0");
        entity.setTrainNumber02("0.0");
        entity.setTrainNumber03("0.0");
        entity.setTrainNumber04("0.0");
        entity.setTrainNumber05("0.0");
        entity.setTrainNumber06("0.0");
        entity.setTrainNumber07("0.0");
        entity.setTrainNumber08("0.0");
        entity.setTrainNumber09("0.0");
        entity.setTrainNumber10("0.0");
        entity.setTrainNumber11("0.0");
        entity.setTrainNumber12("0.0");
        entity.setTrainNumber13("0.0");
        entity.setTrainNumber14("0.0");
        entity.setTrainNumber15("0.0");
        entity.setTrainNumber16("0.0");
        entity.setTrainNumber17("0.0");
        entity.setTrainNumber18("0.0");
        entity.setTrainNumber19("0.0");
        entity.setTrainNumber20("0.0");
        entity.setTrainNumber21("0.0");
        entity.setTrainNumber22("0.0");
        entity.setTrainNumber23("0.0");
        entity.setTrainNumber24("0.0");
        entity.setTrainNumber25("0.0");
        entity.setTrainNumber26("0.0");
        entity.setTrainNumber27("0.0");
        entity.setTrainNumber28("0.0");
        entity.setTrainNumber29("0.0");
        entity.setTrainNumber30("0.0");
        entity.setTrainNumber31("0.0");
        entity.setTrainNumber32("0.0");
        entity.setTrainNumber33("0.0");
        entity.setTrainNumber34("0.0");
        entity.setTrainNumber35("0.0");
        entity.setTrainNumber36("0.0");
        entity.setTrainNumber37("0.0");
        entity.setTrainNumber38("0.0");
        entity.setTrainNumber39("0.0");
        entity.setTrainNumber40("0.0");
    }

    public Map selectNewPage(Criteria criteria, UserEO userEO) throws BusinessException {
        IPage<VehiclePlanEO> page = super.selectPage(criteria);

        Map<String,Object> map =  new HashMap<>();

        map.put("records",page.getRecords());
        map.put("current",page.getCurrent());
        map.put("pages",page.getPages());
        map.put("size",page.getSize());
        map.put("total",page.getTotal());
        String date = "";
        for(Criterion criterion:criteria.getCriterions()){
            if("vp.delivery_date".equals(criterion.getField())){
                date = criterion.getValue();
            }

        }

        if (null != date && !"".equals(date)){
            getMonthSum(page.getRecords(), date, userEO, map);
        }

        return map;
    }

    public List selectTrains(String date,String setType, UserEO userEO) throws BusinessException {
        List mapList = new ArrayList();

        VehiclePlanEO temp = this.baseMapper.selectSumCount(DateUtils.stringToDate(date,"yyyy-MM-dd"),userEO.getOrgId());


        if(null!= temp.getTrainNumber01() && Double.valueOf(temp.getTrainNumber01()) > 0){

            mapList.add(getMap("01"));
        }
        if(null!= temp.getTrainNumber02() && Double.valueOf(temp.getTrainNumber02()) > 0){
            mapList.add(getMap("02"));
        }
        if(null!= temp.getTrainNumber03() && Double.valueOf(temp.getTrainNumber03()) > 0){
            mapList.add(getMap("03"));
        }
        if(null!= temp.getTrainNumber04() && Double.valueOf(temp.getTrainNumber04()) > 0){
            mapList.add(getMap("04"));
        }
        if(null!= temp.getTrainNumber05() && Double.valueOf(temp.getTrainNumber05()) > 0){
            mapList.add(getMap("05"));
        }
        if(null!= temp.getTrainNumber06() && Double.valueOf(temp.getTrainNumber06()) > 0){
            mapList.add(getMap("06"));
        }
        if(null!= temp.getTrainNumber07() && Double.valueOf(temp.getTrainNumber07()) > 0){
            mapList.add(getMap("07"));
        }
        if(null!= temp.getTrainNumber08() && Double.valueOf(temp.getTrainNumber08()) > 0){
            mapList.add(getMap("08"));
        }
        if(null!= temp.getTrainNumber09() && Double.valueOf(temp.getTrainNumber09()) > 0){
            mapList.add(getMap("09"));
        }
        if(null!= temp.getTrainNumber10() && Double.valueOf(temp.getTrainNumber10()) > 0){
            mapList.add(getMap("10"));
        }

        if(null!= temp.getTrainNumber11() && Double.valueOf(temp.getTrainNumber11()) > 0){
            mapList.add(getMap("11"));
        }
        if(null!= temp.getTrainNumber12() && Double.valueOf(temp.getTrainNumber12()) > 0){
            mapList.add(getMap("12"));
        }
        if(null!= temp.getTrainNumber13() && Double.valueOf(temp.getTrainNumber13()) > 0){
            mapList.add(getMap("13"));
        }
        if(null!= temp.getTrainNumber14() && Double.valueOf(temp.getTrainNumber14()) > 0){
            mapList.add(getMap("14"));
        }
        if(null!= temp.getTrainNumber15() && Double.valueOf(temp.getTrainNumber15()) > 0){
            mapList.add(getMap("15"));
        }
        if(null!= temp.getTrainNumber16() && Double.valueOf(temp.getTrainNumber16()) > 0){
            mapList.add(getMap("16"));
        }
        if(null!= temp.getTrainNumber17() && Double.valueOf(temp.getTrainNumber17()) > 0){
            mapList.add(getMap("17"));
        }
        if(null!= temp.getTrainNumber18() && Double.valueOf(temp.getTrainNumber18()) > 0){
            mapList.add(getMap("18"));
        }
        if(null!= temp.getTrainNumber19() && Double.valueOf(temp.getTrainNumber19()) > 0){
            mapList.add(getMap("19"));
        }
        if(null!= temp.getTrainNumber20() && Double.valueOf(temp.getTrainNumber20()) > 0){
            mapList.add(getMap("20"));
        }
        if(null!= temp.getTrainNumber21() && Double.valueOf(temp.getTrainNumber21()) > 0){
            mapList.add(getMap("21"));
        }
        if(null!= temp.getTrainNumber22() && Double.valueOf(temp.getTrainNumber22()) > 0){
            mapList.add(getMap("22"));
        }
        if(null!= temp.getTrainNumber23() && Double.valueOf(temp.getTrainNumber23()) > 0){
            mapList.add(getMap("23"));
        }
        if(null!= temp.getTrainNumber24() && Double.valueOf(temp.getTrainNumber24()) > 0){
            mapList.add(getMap("24"));
        }
        if(null!= temp.getTrainNumber25() && Double.valueOf(temp.getTrainNumber25()) > 0){
            mapList.add(getMap("25"));
        }
        if(null!= temp.getTrainNumber26() && Double.valueOf(temp.getTrainNumber26()) > 0){
            mapList.add(getMap("26"));
        }
        if(null!= temp.getTrainNumber27() && Double.valueOf(temp.getTrainNumber27()) > 0){
            mapList.add(getMap("27"));
        }
        if(null!= temp.getTrainNumber28() && Double.valueOf(temp.getTrainNumber28()) > 0){
            mapList.add(getMap("28"));
        }
        if(null!= temp.getTrainNumber29() && Double.valueOf(temp.getTrainNumber29()) > 0){
            mapList.add(getMap("29"));
        }
        if(null!= temp.getTrainNumber30() && Double.valueOf(temp.getTrainNumber30()) > 0){
            mapList.add(getMap("30"));
        }
        if(null!= temp.getTrainNumber31() && Double.valueOf(temp.getTrainNumber31()) > 0){
            mapList.add(getMap("31"));
        }
        if(null!= temp.getTrainNumber32() && Double.valueOf(temp.getTrainNumber32()) > 0){
            mapList.add(getMap("32"));
        }
        if(null!= temp.getTrainNumber33() && Double.valueOf(temp.getTrainNumber33()) > 0){
            mapList.add(getMap("33"));
        }
        if(null!= temp.getTrainNumber34() && Double.valueOf(temp.getTrainNumber34()) > 0){
            mapList.add(getMap("34"));
        }
        if(null!= temp.getTrainNumber35() && Double.valueOf(temp.getTrainNumber35()) > 0){
            mapList.add(getMap("35"));
        }
        if(null!= temp.getTrainNumber36() && Double.valueOf(temp.getTrainNumber36()) > 0){
            mapList.add(getMap("36"));
        }
        if(null!= temp.getTrainNumber37() && Double.valueOf(temp.getTrainNumber37()) > 0){
            mapList.add(getMap("37"));
        }
        if(null!= temp.getTrainNumber38() && Double.valueOf(temp.getTrainNumber38()) > 0){
            mapList.add(getMap("38"));
        }
        if(null!= temp.getTrainNumber39() && Double.valueOf(temp.getTrainNumber39()) > 0){
            mapList.add(getMap("39"));
        }
        if(null!= temp.getTrainNumber40() && Double.valueOf(temp.getTrainNumber40()) > 0){
            mapList.add(getMap("40"));
        }
        return mapList;
    }

    public Map getMap(String value){
        Map map = new HashMap<>();
        map.put("trainNumber",value);
        map.put("name","车次-"+value);

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addDeliveryOrder(String trainNumber,Long[] ids, UserEO userEO) throws BusinessException {
        List<VehiclePlanEO> vehiclePlanEOList = new ArrayList<>();
        // 查询归属机构对应的供应商
        OrgEO org = this.orgService.getById(userEO.getOrgId());
        if(org.getFullName().contains("上海沿浦金属制品股份有限公司")) {
            org.setFullName("上海沿浦金属制品股份有限公司");
        }
        SupplierEO supplier = this.supplierService.getBySupplierName(org.getFullName());
        if(supplier == null) {
            throw new BusinessException(org.getOrgName() + "不存在对应的供应商，请先添加对应的供应商!");
        }

        //校验入库单是否已生产
        for(Long id : ids){
            VehiclePlanEO temp = this.baseMapper.selectById(id);
            if(("01".equals(trainNumber) && null != temp.getVoucherNo01() && !"".equals(temp.getVoucherNo01()))
                    || ("02".equals(trainNumber) && null != temp.getVoucherNo02() && !"".equals(temp.getVoucherNo02()))
                    || ("03".equals(trainNumber) && null != temp.getVoucherNo03() && !"".equals(temp.getVoucherNo03()))
                    || ("04".equals(trainNumber) && null != temp.getVoucherNo04() && !"".equals(temp.getVoucherNo04()))
                    || ("05".equals(trainNumber) && null != temp.getVoucherNo05() && !"".equals(temp.getVoucherNo05()))
                    || ("06".equals(trainNumber) && null != temp.getVoucherNo06() && !"".equals(temp.getVoucherNo06()))
                    || ("07".equals(trainNumber) && null != temp.getVoucherNo07() && !"".equals(temp.getVoucherNo07()))
                    || ("08".equals(trainNumber) && null != temp.getVoucherNo08() && !"".equals(temp.getVoucherNo08()))
                    || ("09".equals(trainNumber) && null != temp.getVoucherNo09() && !"".equals(temp.getVoucherNo09()))
                    || ("10".equals(trainNumber) && null != temp.getVoucherNo10() && !"".equals(temp.getVoucherNo10()))
                    || ("11".equals(trainNumber) && null != temp.getVoucherNo11() && !"".equals(temp.getVoucherNo11()))
                    || ("12".equals(trainNumber) && null != temp.getVoucherNo12() && !"".equals(temp.getVoucherNo12()))
                    || ("13".equals(trainNumber) && null != temp.getVoucherNo13() && !"".equals(temp.getVoucherNo13()))
                    || ("14".equals(trainNumber) && null != temp.getVoucherNo14() && !"".equals(temp.getVoucherNo14()))
                    || ("15".equals(trainNumber) && null != temp.getVoucherNo15() && !"".equals(temp.getVoucherNo15()))
                    || ("16".equals(trainNumber) && null != temp.getVoucherNo16() && !"".equals(temp.getVoucherNo16()))
                    || ("17".equals(trainNumber) && null != temp.getVoucherNo17() && !"".equals(temp.getVoucherNo17()))
                    || ("18".equals(trainNumber) && null != temp.getVoucherNo18() && !"".equals(temp.getVoucherNo18()))
                    || ("19".equals(trainNumber) && null != temp.getVoucherNo19() && !"".equals(temp.getVoucherNo19()))
                    || ("20".equals(trainNumber) && null != temp.getVoucherNo20() && !"".equals(temp.getVoucherNo20()))
                    || ("21".equals(trainNumber) && null != temp.getVoucherNo21() && !"".equals(temp.getVoucherNo21()))
                    || ("22".equals(trainNumber) && null != temp.getVoucherNo22() && !"".equals(temp.getVoucherNo22()))
                    || ("23".equals(trainNumber) && null != temp.getVoucherNo23() && !"".equals(temp.getVoucherNo23()))
                    || ("24".equals(trainNumber) && null != temp.getVoucherNo24() && !"".equals(temp.getVoucherNo24()))
                    || ("25".equals(trainNumber) && null != temp.getVoucherNo25() && !"".equals(temp.getVoucherNo25()))
                    || ("26".equals(trainNumber) && null != temp.getVoucherNo26() && !"".equals(temp.getVoucherNo26()))
                    || ("27".equals(trainNumber) && null != temp.getVoucherNo27() && !"".equals(temp.getVoucherNo27()))
                    || ("28".equals(trainNumber) && null != temp.getVoucherNo28() && !"".equals(temp.getVoucherNo28()))
                    || ("29".equals(trainNumber) && null != temp.getVoucherNo29() && !"".equals(temp.getVoucherNo29()))
                    || ("30".equals(trainNumber) && null != temp.getVoucherNo30() && !"".equals(temp.getVoucherNo30()))
                    || ("31".equals(trainNumber) && null != temp.getVoucherNo31() && !"".equals(temp.getVoucherNo31()))
                    || ("32".equals(trainNumber) && null != temp.getVoucherNo32() && !"".equals(temp.getVoucherNo32()))
                    || ("33".equals(trainNumber) && null != temp.getVoucherNo33() && !"".equals(temp.getVoucherNo33()))
                    || ("34".equals(trainNumber) && null != temp.getVoucherNo34() && !"".equals(temp.getVoucherNo34()))
                    || ("35".equals(trainNumber) && null != temp.getVoucherNo35() && !"".equals(temp.getVoucherNo35()))
                    || ("36".equals(trainNumber) && null != temp.getVoucherNo36() && !"".equals(temp.getVoucherNo36()))
                    || ("37".equals(trainNumber) && null != temp.getVoucherNo37() && !"".equals(temp.getVoucherNo37()))
                    || ("38".equals(trainNumber) && null != temp.getVoucherNo38() && !"".equals(temp.getVoucherNo38()))
                    || ("39".equals(trainNumber) && null != temp.getVoucherNo39() && !"".equals(temp.getVoucherNo39()))
                    || ("40".equals(trainNumber) && null != temp.getVoucherNo40() && !"".equals(temp.getVoucherNo40())))
            {
                throw new BusinessException("零件号："+temp.getElementNo()+"的物料，车次为：【车次-"+trainNumber+"】数据已生成过出库单，请确认");
            }else{
                if(("01".equals(trainNumber) && null != temp.getTrainNumber01() && !temp.getTrainNumber01().isEmpty() && Double.valueOf(temp.getTrainNumber01()) > 0)
                   || ("02".equals(trainNumber) && null != temp.getTrainNumber02() && !temp.getTrainNumber02().isEmpty() && Double.valueOf(temp.getTrainNumber02()) > 0)
                        ||("03".equals(trainNumber) && null != temp.getTrainNumber03() && !temp.getTrainNumber03().isEmpty() && Double.valueOf(temp.getTrainNumber03()) > 0)
                        ||("04".equals(trainNumber) && null != temp.getTrainNumber04() && !temp.getTrainNumber04().isEmpty() && Double.valueOf(temp.getTrainNumber04()) > 0)
                        ||("05".equals(trainNumber) && null != temp.getTrainNumber05() && !temp.getTrainNumber05().isEmpty() && Double.valueOf(temp.getTrainNumber05()) > 0)
                        ||("06".equals(trainNumber) && null != temp.getTrainNumber06() && !temp.getTrainNumber06().isEmpty() && Double.valueOf(temp.getTrainNumber06()) > 0)
                        ||("07".equals(trainNumber) && null != temp.getTrainNumber07() && !temp.getTrainNumber07().isEmpty() && Double.valueOf(temp.getTrainNumber07()) > 0)
                        ||("08".equals(trainNumber) && null != temp.getTrainNumber08() && !temp.getTrainNumber08().isEmpty() && Double.valueOf(temp.getTrainNumber08()) > 0)
                        ||("09".equals(trainNumber) && null != temp.getTrainNumber09() && !temp.getTrainNumber09().isEmpty() && Double.valueOf(temp.getTrainNumber09()) > 0)
                        ||("10".equals(trainNumber) && null != temp.getTrainNumber10() && !temp.getTrainNumber10().isEmpty() && Double.valueOf(temp.getTrainNumber10()) > 0)
                        ||("11".equals(trainNumber) && null != temp.getTrainNumber11() && !temp.getTrainNumber11().isEmpty() && Double.valueOf(temp.getTrainNumber11()) > 0)
                        ||("12".equals(trainNumber) && null != temp.getTrainNumber12() && !temp.getTrainNumber12().isEmpty() && Double.valueOf(temp.getTrainNumber12()) > 0)
                        ||("13".equals(trainNumber) && null != temp.getTrainNumber13() && !temp.getTrainNumber13().isEmpty() && Double.valueOf(temp.getTrainNumber13()) > 0)
                        ||("14".equals(trainNumber) && null != temp.getTrainNumber14() && !temp.getTrainNumber14().isEmpty() && Double.valueOf(temp.getTrainNumber14()) > 0)
                        ||("15".equals(trainNumber) && null != temp.getTrainNumber15() && !temp.getTrainNumber15().isEmpty() && Double.valueOf(temp.getTrainNumber15()) > 0)
                        ||("16".equals(trainNumber) && null != temp.getTrainNumber16() && !temp.getTrainNumber16().isEmpty() && Double.valueOf(temp.getTrainNumber16()) > 0)
                        ||("17".equals(trainNumber) && null != temp.getTrainNumber17() && !temp.getTrainNumber17().isEmpty() && Double.valueOf(temp.getTrainNumber17()) > 0)
                        ||("18".equals(trainNumber) && null != temp.getTrainNumber18() && !temp.getTrainNumber18().isEmpty() && Double.valueOf(temp.getTrainNumber18()) > 0)
                        ||("19".equals(trainNumber) && null != temp.getTrainNumber19() && !temp.getTrainNumber19().isEmpty() && Double.valueOf(temp.getTrainNumber19()) > 0)
                        ||("20".equals(trainNumber) && null != temp.getTrainNumber20() && !temp.getTrainNumber20().isEmpty() && Double.valueOf(temp.getTrainNumber20()) > 0)
                        ||("21".equals(trainNumber) && null != temp.getTrainNumber21() && !temp.getTrainNumber21().isEmpty() && Double.valueOf(temp.getTrainNumber21()) > 0)
                        ||("22".equals(trainNumber) && null != temp.getTrainNumber22() && !temp.getTrainNumber22().isEmpty() && Double.valueOf(temp.getTrainNumber22()) > 0)
                        ||("23".equals(trainNumber) && null != temp.getTrainNumber23() && !temp.getTrainNumber23().isEmpty() && Double.valueOf(temp.getTrainNumber23()) > 0)
                        ||("24".equals(trainNumber) && null != temp.getTrainNumber24() && !temp.getTrainNumber24().isEmpty() && Double.valueOf(temp.getTrainNumber24()) > 0)
                        ||("25".equals(trainNumber) && null != temp.getTrainNumber25() && !temp.getTrainNumber25().isEmpty() && Double.valueOf(temp.getTrainNumber25()) > 0)
                        ||("26".equals(trainNumber) && null != temp.getTrainNumber26() && !temp.getTrainNumber26().isEmpty() && Double.valueOf(temp.getTrainNumber26()) > 0)
                        ||("27".equals(trainNumber) && null != temp.getTrainNumber27() && !temp.getTrainNumber27().isEmpty() && Double.valueOf(temp.getTrainNumber27()) > 0)
                        ||("28".equals(trainNumber) && null != temp.getTrainNumber28() && !temp.getTrainNumber28().isEmpty() && Double.valueOf(temp.getTrainNumber28()) > 0)
                        ||("29".equals(trainNumber) && null != temp.getTrainNumber29() && !temp.getTrainNumber29().isEmpty() && Double.valueOf(temp.getTrainNumber29()) > 0)
                        ||("30".equals(trainNumber) && null != temp.getTrainNumber30() && !temp.getTrainNumber30().isEmpty() && Double.valueOf(temp.getTrainNumber30()) > 0)
                        ||("31".equals(trainNumber) && null != temp.getTrainNumber31() && !temp.getTrainNumber31().isEmpty() && Double.valueOf(temp.getTrainNumber31()) > 0)
                        ||("32".equals(trainNumber) && null != temp.getTrainNumber32() && !temp.getTrainNumber32().isEmpty() && Double.valueOf(temp.getTrainNumber32()) > 0)
                        ||("33".equals(trainNumber) && null != temp.getTrainNumber33() && !temp.getTrainNumber33().isEmpty() && Double.valueOf(temp.getTrainNumber33()) > 0)
                        ||("34".equals(trainNumber) && null != temp.getTrainNumber34() && !temp.getTrainNumber34().isEmpty() && Double.valueOf(temp.getTrainNumber34()) > 0)
                        ||("35".equals(trainNumber) && null != temp.getTrainNumber35() && !temp.getTrainNumber35().isEmpty() && Double.valueOf(temp.getTrainNumber35()) > 0)
                        ||("36".equals(trainNumber) && null != temp.getTrainNumber36() && !temp.getTrainNumber36().isEmpty() && Double.valueOf(temp.getTrainNumber36()) > 0)
                        ||("37".equals(trainNumber) && null != temp.getTrainNumber37() && !temp.getTrainNumber37().isEmpty() && Double.valueOf(temp.getTrainNumber37()) > 0)
                        ||("38".equals(trainNumber) && null != temp.getTrainNumber38() && !temp.getTrainNumber38().isEmpty() && Double.valueOf(temp.getTrainNumber38()) > 0)
                        ||("39".equals(trainNumber) && null != temp.getTrainNumber39() && !temp.getTrainNumber39().isEmpty() && Double.valueOf(temp.getTrainNumber39()) > 0)
                        ||("40".equals(trainNumber) && null != temp.getTrainNumber40() && !temp.getTrainNumber40().isEmpty() && Double.valueOf(temp.getTrainNumber40()) > 0)
                ){
                    vehiclePlanEOList.add(temp);
                }
            }
        }

        //获取排车计划
        VehicleTrainPlanEO vehicleTrainPlanEO = this.baseMapper.selectExistEntity(vehiclePlanEOList.get(0).getDeliveryDate(),userEO.getOrgId(),trainNumber);
        if(null == vehicleTrainPlanEO){
            throw new BusinessException("请先设置车次-"+trainNumber+"的排车基本信息");
        }

        DeliveryOrderEO deliveryOrderEO = new DeliveryOrderEO();
        deliveryOrderEO.setCustomerId(vehiclePlanEOList.get(0).getCustomerId());
        deliveryOrderEO.setDestinationCode(vehiclePlanEOList.get(0).getCustomerCode());
        deliveryOrderEO.setDestinationName(vehiclePlanEOList.get(0).getCustomerName());
        deliveryOrderEO.setDeliveryDate(vehiclePlanEOList.get(0).getDeliveryDate());
        deliveryOrderEO.setDeliveryType(1);
        deliveryOrderEO.setOrgId(userEO.getOrgId());
        deliveryOrderEO.setStatus(1);
        deliveryOrderEO.setDeliveryTime(vehicleTrainPlanEO.getTrainTime());
        deliveryOrderEO.setVehicleType(vehicleTrainPlanEO.getVehicleType());
        deliveryOrderEO.setTrainNumber(trainNumber);
        deliveryOrderEO.setSupplierId(supplier.getSupplierId());

        // 生成业务编码
        String code = this.generateNextCode("wms_delivery_order", deliveryOrderEO,userEO.getOrgId());
        AssertUtils.isBlank(code);
        deliveryOrderEO.setVoucherNo(code);
        this.dubDeliveryOrderService.save(deliveryOrderEO);

        Boolean existFlag = false;

        Double amount = 0d;
        for (VehiclePlanEO entity:vehiclePlanEOList){
            //查询物料库存



            if("01".equals(trainNumber) && null != entity.getTrainNumber01() && !entity.getTrainNumber01().isEmpty() && Double.valueOf(entity.getTrainNumber01()) > 0){
                amount = Double.valueOf(entity.getTrainNumber01());
                entity.setVoucherNo01(deliveryOrderEO.getVoucherNo());

            }else if("02".equals(trainNumber) && null != entity.getTrainNumber02() && !entity.getTrainNumber02().isEmpty() && Double.valueOf(entity.getTrainNumber02()) > 0){
                amount = Double.valueOf(entity.getTrainNumber02());
                entity.setVoucherNo02(deliveryOrderEO.getVoucherNo());

            }else if("03".equals(trainNumber) && null != entity.getTrainNumber03() && !entity.getTrainNumber03().isEmpty() && Double.valueOf(entity.getTrainNumber03()) > 0){
                amount = Double.valueOf(entity.getTrainNumber03());
                entity.setVoucherNo03(deliveryOrderEO.getVoucherNo());

            }else if("04".equals(trainNumber) && null != entity.getTrainNumber04() && !entity.getTrainNumber04().isEmpty() && Double.valueOf(entity.getTrainNumber04()) > 0){
                amount = Double.valueOf(entity.getTrainNumber04());
                entity.setVoucherNo04(deliveryOrderEO.getVoucherNo());

            }else if("05".equals(trainNumber) && null != entity.getTrainNumber05() && !entity.getTrainNumber05().isEmpty() && Double.valueOf(entity.getTrainNumber05()) > 0){
                amount = Double.valueOf(entity.getTrainNumber05());
                entity.setVoucherNo05(deliveryOrderEO.getVoucherNo());

            }else if("06".equals(trainNumber) && null != entity.getTrainNumber06() && !entity.getTrainNumber06().isEmpty() && Double.valueOf(entity.getTrainNumber06()) > 0){
                amount = Double.valueOf(entity.getTrainNumber06());
                entity.setVoucherNo06(deliveryOrderEO.getVoucherNo());

            }else if("07".equals(trainNumber) && null != entity.getTrainNumber07() && !entity.getTrainNumber07().isEmpty() && Double.valueOf(entity.getTrainNumber07()) > 0){
                amount = Double.valueOf(entity.getTrainNumber07());
                entity.setVoucherNo07(deliveryOrderEO.getVoucherNo());

            }else if("08".equals(trainNumber) && null != entity.getTrainNumber08() && !entity.getTrainNumber08().isEmpty() && Double.valueOf(entity.getTrainNumber08()) > 0){
                amount = Double.valueOf(entity.getTrainNumber08());
                entity.setVoucherNo08(deliveryOrderEO.getVoucherNo());

            }else if("09".equals(trainNumber) && null != entity.getTrainNumber09() && !entity.getTrainNumber09().isEmpty() && Double.valueOf(entity.getTrainNumber09()) > 0){
                amount = Double.valueOf(entity.getTrainNumber09());
                entity.setVoucherNo09(deliveryOrderEO.getVoucherNo());

            }else if("10".equals(trainNumber) && null != entity.getTrainNumber10() && !entity.getTrainNumber10().isEmpty() && Double.valueOf(entity.getTrainNumber10()) > 0){
                amount = Double.valueOf(entity.getTrainNumber10());
                entity.setVoucherNo10(deliveryOrderEO.getVoucherNo());

            }else if("11".equals(trainNumber) && null != entity.getTrainNumber11() && !entity.getTrainNumber11().isEmpty() && Double.valueOf(entity.getTrainNumber11()) > 0){
                amount = Double.valueOf(entity.getTrainNumber11());
                entity.setVoucherNo11(deliveryOrderEO.getVoucherNo());

            }else if("12".equals(trainNumber) && null != entity.getTrainNumber12() && !entity.getTrainNumber12().isEmpty() && Double.valueOf(entity.getTrainNumber12()) > 0){
                amount = Double.valueOf(entity.getTrainNumber12());
                entity.setVoucherNo12(deliveryOrderEO.getVoucherNo());

            }else if("13".equals(trainNumber) && null != entity.getTrainNumber13() && !entity.getTrainNumber13().isEmpty() && Double.valueOf(entity.getTrainNumber13()) > 0){
                amount = Double.valueOf(entity.getTrainNumber13());
                entity.setVoucherNo13(deliveryOrderEO.getVoucherNo());

            }else if("14".equals(trainNumber) && null != entity.getTrainNumber14() && !entity.getTrainNumber14().isEmpty() && Double.valueOf(entity.getTrainNumber14()) > 0){
                amount = Double.valueOf(entity.getTrainNumber14());
                entity.setVoucherNo14(deliveryOrderEO.getVoucherNo());

            }else if("15".equals(trainNumber) && null != entity.getTrainNumber15() && !entity.getTrainNumber15().isEmpty() && Double.valueOf(entity.getTrainNumber15()) > 0){
                amount = Double.valueOf(entity.getTrainNumber15());
                entity.setVoucherNo15(deliveryOrderEO.getVoucherNo());

            }else if("16".equals(trainNumber) && null != entity.getTrainNumber16() && !entity.getTrainNumber16().isEmpty() && Double.valueOf(entity.getTrainNumber16()) > 0){
                amount = Double.valueOf(entity.getTrainNumber16());
                entity.setVoucherNo16(deliveryOrderEO.getVoucherNo());

            }else if("17".equals(trainNumber) && null != entity.getTrainNumber17() && !entity.getTrainNumber17().isEmpty() && Double.valueOf(entity.getTrainNumber17()) > 0){
                amount = Double.valueOf(entity.getTrainNumber17());
                entity.setVoucherNo17(deliveryOrderEO.getVoucherNo());

            }else if("18".equals(trainNumber) && null != entity.getTrainNumber18() && !entity.getTrainNumber18().isEmpty() && Double.valueOf(entity.getTrainNumber18()) > 0){
                amount = Double.valueOf(entity.getTrainNumber18());
                entity.setVoucherNo18(deliveryOrderEO.getVoucherNo());

            }else if("19".equals(trainNumber) && null != entity.getTrainNumber19() && !entity.getTrainNumber19().isEmpty() && Double.valueOf(entity.getTrainNumber19()) > 0){
                amount = Double.valueOf(entity.getTrainNumber19());
                entity.setVoucherNo19(deliveryOrderEO.getVoucherNo());

            }else if("20".equals(trainNumber) && null != entity.getTrainNumber20() && !entity.getTrainNumber20().isEmpty() && Double.valueOf(entity.getTrainNumber20()) > 0){
                amount = Double.valueOf(entity.getTrainNumber20());
                entity.setVoucherNo20(deliveryOrderEO.getVoucherNo());

            }else if("21".equals(trainNumber) && null != entity.getTrainNumber21() && !entity.getTrainNumber21().isEmpty() && Double.valueOf(entity.getTrainNumber21()) > 0){
                amount = Double.valueOf(entity.getTrainNumber21());
                entity.setVoucherNo21(deliveryOrderEO.getVoucherNo());

            }else if("22".equals(trainNumber) && null != entity.getTrainNumber22() && !entity.getTrainNumber22().isEmpty() && Double.valueOf(entity.getTrainNumber22()) > 0){
                amount = Double.valueOf(entity.getTrainNumber22());
                entity.setVoucherNo22(deliveryOrderEO.getVoucherNo());

            }else if("23".equals(trainNumber) && null != entity.getTrainNumber23() && !entity.getTrainNumber23().isEmpty() && Double.valueOf(entity.getTrainNumber23()) > 0){
                amount = Double.valueOf(entity.getTrainNumber23());
                entity.setVoucherNo23(deliveryOrderEO.getVoucherNo());

            }else if("24".equals(trainNumber) && null != entity.getTrainNumber24() && !entity.getTrainNumber24().isEmpty() && Double.valueOf(entity.getTrainNumber24()) > 0){
                amount = Double.valueOf(entity.getTrainNumber24());
                entity.setVoucherNo24(deliveryOrderEO.getVoucherNo());

            }else if("25".equals(trainNumber) && null != entity.getTrainNumber25() && !entity.getTrainNumber25().isEmpty() && Double.valueOf(entity.getTrainNumber25()) > 0){
                amount = Double.valueOf(entity.getTrainNumber25());
                entity.setVoucherNo25(deliveryOrderEO.getVoucherNo());

            }else if("26".equals(trainNumber) && null != entity.getTrainNumber26() && !entity.getTrainNumber26().isEmpty() && Double.valueOf(entity.getTrainNumber26()) > 0){
                amount = Double.valueOf(entity.getTrainNumber26());
                entity.setVoucherNo26(deliveryOrderEO.getVoucherNo());

            }else if("27".equals(trainNumber) && null != entity.getTrainNumber27() && !entity.getTrainNumber27().isEmpty() && Double.valueOf(entity.getTrainNumber27()) > 0){
                amount = Double.valueOf(entity.getTrainNumber27());
                entity.setVoucherNo27(deliveryOrderEO.getVoucherNo());

            }else if("28".equals(trainNumber) && null != entity.getTrainNumber28() && !entity.getTrainNumber28().isEmpty() && Double.valueOf(entity.getTrainNumber28()) > 0){
                amount = Double.valueOf(entity.getTrainNumber28());
                entity.setVoucherNo28(deliveryOrderEO.getVoucherNo());

            }else if("29".equals(trainNumber) && null != entity.getTrainNumber29() && !entity.getTrainNumber29().isEmpty() && Double.valueOf(entity.getTrainNumber29()) > 0){
                amount = Double.valueOf(entity.getTrainNumber29());
                entity.setVoucherNo29(deliveryOrderEO.getVoucherNo());

            }else if("30".equals(trainNumber) && null != entity.getTrainNumber30() && !entity.getTrainNumber30().isEmpty() && Double.valueOf(entity.getTrainNumber30()) > 0){
                amount = Double.valueOf(entity.getTrainNumber30());
                entity.setVoucherNo30(deliveryOrderEO.getVoucherNo());

            }else if("31".equals(trainNumber) && null != entity.getTrainNumber31() && !entity.getTrainNumber31().isEmpty() &&  Double.valueOf(entity.getTrainNumber31()) > 0){
                amount = Double.valueOf(entity.getTrainNumber31());
                entity.setVoucherNo31(deliveryOrderEO.getVoucherNo());

            }else if("32".equals(trainNumber) && null != entity.getTrainNumber32() && !entity.getTrainNumber32().isEmpty() && Double.valueOf(entity.getTrainNumber32()) > 0){
                amount = Double.valueOf(entity.getTrainNumber32());
                entity.setVoucherNo32(deliveryOrderEO.getVoucherNo());

            }else if("33".equals(trainNumber) && null != entity.getTrainNumber33() && !entity.getTrainNumber33().isEmpty() && Double.valueOf(entity.getTrainNumber33()) > 0){
                amount = Double.valueOf(entity.getTrainNumber33());
                entity.setVoucherNo33(deliveryOrderEO.getVoucherNo());

            }else if("34".equals(trainNumber) && null != entity.getTrainNumber34() && !entity.getTrainNumber34().isEmpty() && Double.valueOf(entity.getTrainNumber34()) > 0){
                amount = Double.valueOf(entity.getTrainNumber34());
                entity.setVoucherNo34(deliveryOrderEO.getVoucherNo());

            }else if("35".equals(trainNumber) && null != entity.getTrainNumber35() && !entity.getTrainNumber35().isEmpty() && Double.valueOf(entity.getTrainNumber35()) > 0){
                amount = Double.valueOf(entity.getTrainNumber35());
                entity.setVoucherNo35(deliveryOrderEO.getVoucherNo());

            }else if("36".equals(trainNumber) && null != entity.getTrainNumber36() && !entity.getTrainNumber36().isEmpty() && Double.valueOf(entity.getTrainNumber36()) > 0){
                amount = Double.valueOf(entity.getTrainNumber36());
                entity.setVoucherNo36(deliveryOrderEO.getVoucherNo());

            }else if("37".equals(trainNumber) && null != entity.getTrainNumber37() && !entity.getTrainNumber37().isEmpty() && Double.valueOf(entity.getTrainNumber37()) > 0){
                amount = Double.valueOf(entity.getTrainNumber37());
                entity.setVoucherNo37(deliveryOrderEO.getVoucherNo());

            }else if("38".equals(trainNumber) && null != entity.getTrainNumber38() && !entity.getTrainNumber38().isEmpty() && Double.valueOf(entity.getTrainNumber38()) > 0){
                amount = Double.valueOf(entity.getTrainNumber38());
                entity.setVoucherNo38(deliveryOrderEO.getVoucherNo());

            }else if("39".equals(trainNumber) && null != entity.getTrainNumber39() && !entity.getTrainNumber39().isEmpty() && Double.valueOf(entity.getTrainNumber39()) > 0){
                amount = Double.valueOf(entity.getTrainNumber39());
                entity.setVoucherNo39(deliveryOrderEO.getVoucherNo());

            }else if("40".equals(trainNumber) && null != entity.getTrainNumber40() && !entity.getTrainNumber40().isEmpty() && Double.valueOf(entity.getTrainNumber40()) > 0){
                amount = Double.valueOf(entity.getTrainNumber40());
                entity.setVoucherNo40(deliveryOrderEO.getVoucherNo());

            }else{
                continue;
            }

            StockAccountEO stockAccountEO = this.baseMapper.selectStockCount(entity.getMaterialId());
            if(stockAccountEO==null || stockAccountEO.getCount()==null) {
                throw new BusinessException("零件号："+entity.getElementNo()+"的零件库存不存在，请确认！");
            }
            if(stockAccountEO!=null && stockAccountEO.getCount()!=null && stockAccountEO.getCount()<amount){
                throw new BusinessException("零件号："+entity.getElementNo()+"的零件库存不足，当前库存:" + stockAccountEO.getCount() + "，请确认！");
            }

            DeliveryOrderDetailEO deliveryOrderDetailTemp = new DeliveryOrderDetailEO();
            deliveryOrderDetailTemp.setDeliveryOrderId(deliveryOrderEO.getDeliveryId());
            deliveryOrderDetailTemp.setMaterialId(entity.getMaterialId());
            deliveryOrderDetailTemp.setMaterialCode(entity.getMaterialCode());
            deliveryOrderDetailTemp.setMaterialName(entity.getMaterialName());
            deliveryOrderDetailTemp.setInventoryCode(entity.getInventoryCode());
            deliveryOrderDetailTemp.setElementNo(entity.getElementNo());

            deliveryOrderDetailTemp.setFigureNumber(entity.getFigureNumber());
            deliveryOrderDetailTemp.setFigureVersion(entity.getFigureVersion());
            deliveryOrderDetailTemp.setWarehouseId(stockAccountEO.getWarehouseId());
            deliveryOrderDetailTemp.setUnitId(entity.getUnitId());
            deliveryOrderDetailTemp.setDeliveryAmount(amount);
            deliveryOrderDetailTemp.setRelDeliveryAmount(0d);
            deliveryOrderDetailTemp.setStatus(1);

            this.dubDeliveryOrderDetailService.save(deliveryOrderDetailTemp);
            this.baseMapper.updateById(entity);

            existFlag = true;
        }

        if(!existFlag){
            this.dubDeliveryOrderService.removeById(deliveryOrderEO.getDeliveryId());
        }


        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeliveryOrder(String voucherNo,String trainNumber,String date) throws BusinessException {

        Integer count = this.baseMapper.selectExistOrder(voucherNo);
        if(count > 0){
            throw new BusinessException("送货单存在已完成送货的明细数据，不予许撤回操作！");
        }

        String headSql = "voucher_no"+trainNumber+" = ''";
        String backSql = "voucher_no"+trainNumber+" = '"+voucherNo+"'";



        //删除出库单和出库明细
        //先删除出库单明细
        this.baseMapper.deleteDeliveryOrderDetail(voucherNo);

        //删除出库单
        this.baseMapper.deleteDeliveryOrder(voucherNo);

        this.baseMapper.updateVoucherNo(headSql,backSql,DateUtils.stringToDate(date,"yyyy-MM-dd"));

        return true;
    }

    public List<VehiclePlanEO> getProjectNo(String date, UserEO userEO) {
//        List mapList = new ArrayList();

        List<VehiclePlanEO> list = this.baseMapper.getAllProjectNo(DateUtils.stringToDate(date,"yyyy-MM-dd"));

//        for(String str:list){
//            Map map = new HashMap();
//            map.put("projectNo",str);
//            mapList.add(map);
//        }

        return list;
    }


    @Transactional(rollbackFor = Exception.class)
    public List<VehiclePlanEO> importExecl(List list,String date, UserEO userEO) throws BusinessException {
        List<VehiclePlanEO> planEOList = new ArrayList<>();
        Integer index= 0;
        if(list!=null) {
            if (list.size() > 0) {
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                if(mapList!=null && mapList.size()>1) {
                    //循环遍历数据
                    for(int i=0;i < mapList.size();i++) {
                        Map map = mapList.get(i);
                        if (i < 1) {
                            continue;
                        }

                        index ++;

                        //零件号
                        String elementNo = (String) map.get("0");
                        if(null==elementNo || elementNo.isEmpty()){
                            continue;
                        }

                        //先初始化，把错误信息带回
                        VehiclePlanEO vehiclePlanEO = new VehiclePlanEO();
                        vehiclePlanEO.setIndex(index);

                        //校验物料档案中是否存在这个零件
                        MaterialEO materialEO = this.baseMapper.selectMaterialInfo(elementNo,userEO.getOrgId());
                        if(null == materialEO){
                            vehiclePlanEO.setMsg("0");
                            vehiclePlanEO.setElementNo(elementNo);
                            planEOList.add(vehiclePlanEO);
                            continue;
                        }
                        //查询物料默认客户
                        CustomerEO customerDefault = this.baseMapper.selectDefaultCustomer(materialEO.getMaterialId());

                        //总数
                        String amount = (String) map.get("4");
                        if(null==amount || amount.isEmpty()){
                            vehiclePlanEO.setMsg("1");
                            vehiclePlanEO.setElementNo(elementNo);
                            planEOList.add(vehiclePlanEO);
                            continue;
                        }

                        CustomerEO tempCustomer = new CustomerEO();

                        //客户
                        String customerName = (String) map.get("3");
                        if(null==customerName || customerName.isEmpty()){
                            //校验默认客户
                            if(null == customerDefault){
                                vehiclePlanEO.setMsg("2");
                                vehiclePlanEO.setElementNo(elementNo);
                                planEOList.add(vehiclePlanEO);
                                continue;
                            }else{
                                tempCustomer = customerDefault;
                            }
                        }else{
                            //导入的客户有值，则查询客户信息
                            CustomerEO execlCustomer = this.baseMapper.selectCustomerInfo(customerName);
                            if(null == execlCustomer){
                                //校验默认客户
                                if(null == customerDefault){
                                    vehiclePlanEO.setMsg("2");
                                    vehiclePlanEO.setElementNo(elementNo);
                                    planEOList.add(vehiclePlanEO);
                                    continue;
                                }else{
                                    tempCustomer = customerDefault;
                                }

                            }else{
                                tempCustomer = execlCustomer;
                            }
                        }

                        //项目号
                        String projectNo = (String) map.get("2");
                        if(null==projectNo || projectNo.isEmpty()){
                            projectNo = "";
                        }

                        //最小包装数
                        String snp = (String) map.get("5");
                        if((null==snp || snp.isEmpty()) && null != materialEO.getSnp()){
                            snp = String.valueOf(materialEO.getSnp());
                        }

                        //判断当天数据是否存在
                        vehiclePlanEO.setMaterialId(materialEO.getMaterialId());
                        vehiclePlanEO.setMaterialCode(materialEO.getMaterialCode());
                        vehiclePlanEO.setMaterialName(materialEO.getMaterialName());
                        vehiclePlanEO.setProjectNo(projectNo);
                        if (null == snp || snp.isEmpty()){
                            vehiclePlanEO.setSnp(0d);
                        }else{
                            vehiclePlanEO.setSnp(Double.valueOf(snp));
                        }
                        vehiclePlanEO.setElementNo(elementNo);
                        vehiclePlanEO.setAmount(Double.valueOf(amount));
                        vehiclePlanEO.setStatus(0);
                        vehiclePlanEO.setOrgId(userEO.getOrgId());
                        vehiclePlanEO.setCustomerId(tempCustomer.getCustomerId());
                        vehiclePlanEO.setCustomerCode(tempCustomer.getCustomerCode());
                        vehiclePlanEO.setCustomerName(tempCustomer.getCustomerName());
                        //车次-01
                        String trainNumber01 = (String) map.get("6");
                        if(null != trainNumber01 && !trainNumber01.isEmpty() && Double.valueOf(trainNumber01) > 0){
                            vehiclePlanEO.setTrainNumber01(trainNumber01);
                        }
                        //车次-02
                        String trainNumber02 = (String) map.get("7");
                        if(null != trainNumber02 && !trainNumber02.isEmpty() && Double.valueOf(trainNumber02) > 0){
                            vehiclePlanEO.setTrainNumber02(trainNumber02);
                        }
                        //车次-03
                        String trainNumber03 = (String) map.get("8");
                        if(null != trainNumber03 && !trainNumber03.isEmpty() && Double.valueOf(trainNumber03) > 0){
                            vehiclePlanEO.setTrainNumber03(trainNumber03);
                        }
                        //车次-04
                        String trainNumber04 = (String) map.get("9");
                        if(null != trainNumber04 && !trainNumber04.isEmpty() && Double.valueOf(trainNumber04) > 0){
                            vehiclePlanEO.setTrainNumber04(trainNumber04);
                        }
                        //车次-05
                        String trainNumber05 = (String) map.get("10");
                        if(null != trainNumber05 && !trainNumber05.isEmpty() && Double.valueOf(trainNumber05) > 0){
                            vehiclePlanEO.setTrainNumber05(trainNumber05);
                        }
                        //车次-06
                        String trainNumber06 = (String) map.get("11");
                        if(null != trainNumber06 && !trainNumber06.isEmpty() && Double.valueOf(trainNumber06) > 0){
                            vehiclePlanEO.setTrainNumber06(trainNumber06);
                        }
                        //车次-07
                        String trainNumber07 = (String) map.get("12");
                        if(null != trainNumber07 && !trainNumber07.isEmpty() && Double.valueOf(trainNumber07) > 0){
                            vehiclePlanEO.setTrainNumber07(trainNumber07);
                        }

                        //车次-08
                        String trainNumber08 = (String) map.get("13");
                        if(null != trainNumber08 && !trainNumber08.isEmpty() && Double.valueOf(trainNumber08) > 0){
                            vehiclePlanEO.setTrainNumber08(trainNumber08);
                        }
                        //车次-09
                        String trainNumber09 = (String) map.get("14");
                        if(null != trainNumber09 && !trainNumber09.isEmpty() && Double.valueOf(trainNumber09) > 0){
                            vehiclePlanEO.setTrainNumber09(trainNumber09);
                        }
                        //车次-10
                        String trainNumber10 = (String) map.get("15");
                        if(null != trainNumber10 && !trainNumber10.isEmpty() && Double.valueOf(trainNumber10) > 0){
                            vehiclePlanEO.setTrainNumber10(trainNumber10);
                        }
                        //车次-11
                        String trainNumber11 = (String) map.get("16");
                        if(null != trainNumber11 && !trainNumber11.isEmpty() && Double.valueOf(trainNumber11) > 0){
                            vehiclePlanEO.setTrainNumber11(trainNumber11);
                        }
                        //车次-12
                        String trainNumber12 = (String) map.get("17");
                        if(null != trainNumber12 && !trainNumber12.isEmpty() && Double.valueOf(trainNumber12) > 0){
                            vehiclePlanEO.setTrainNumber12(trainNumber12);
                        }
                        //车次-13
                        String trainNumber13 = (String) map.get("18");
                        if(null != trainNumber13 && !trainNumber13.isEmpty() && Double.valueOf(trainNumber13) > 0){
                            vehiclePlanEO.setTrainNumber13(trainNumber13);
                        }
                        //车次-14
                        String trainNumber14 = (String) map.get("19");
                        if(null != trainNumber14 && !trainNumber14.isEmpty() && Double.valueOf(trainNumber14) > 0){
                            vehiclePlanEO.setTrainNumber14(trainNumber14);
                        }
                        //车次-15
                        String trainNumber15 = (String) map.get("20");
                        if(null != trainNumber15 && !trainNumber15.isEmpty() && Double.valueOf(trainNumber15) > 0){
                            vehiclePlanEO.setTrainNumber15(trainNumber15);
                        }
                        //车次-16
                        String trainNumber16 = (String) map.get("21");
                        if(null != trainNumber16 && !trainNumber16.isEmpty() && Double.valueOf(trainNumber16) > 0){
                            vehiclePlanEO.setTrainNumber16(trainNumber16);
                        }
                        //车次-17
                        String trainNumber17 = (String) map.get("22");
                        if(null != trainNumber17 && !trainNumber17.isEmpty() && Double.valueOf(trainNumber17) > 0){
                            vehiclePlanEO.setTrainNumber17(trainNumber17);
                        }
                        //车次-18
                        String trainNumber18 = (String) map.get("23");
                        if(null != trainNumber18 && !trainNumber18.isEmpty() && Double.valueOf(trainNumber18) > 0){
                            vehiclePlanEO.setTrainNumber18(trainNumber18);
                        }
                        //车次-19
                        String trainNumber19 = (String) map.get("24");
                        if(null != trainNumber19 && !trainNumber19.isEmpty() && Double.valueOf(trainNumber19) > 0){
                            vehiclePlanEO.setTrainNumber19(trainNumber19);
                        }
                        //车次-20
                        String trainNumber20 = (String) map.get("25");
                        if(null != trainNumber20 && !trainNumber20.isEmpty() && Double.valueOf(trainNumber20) > 0){
                            vehiclePlanEO.setTrainNumber20(trainNumber20);
                        }
                        //车次-21
                        String trainNumber21 = (String) map.get("26");
                        if(null != trainNumber21 && !trainNumber21.isEmpty() && Double.valueOf(trainNumber21) > 0){
                            vehiclePlanEO.setTrainNumber21(trainNumber21);
                        }
                        //车次-22
                        String trainNumber22 = (String) map.get("27");
                        if(null != trainNumber22 && !trainNumber22.isEmpty() && Double.valueOf(trainNumber22) > 0){
                            vehiclePlanEO.setTrainNumber22(trainNumber22);
                        }
                        //车次-23
                        String trainNumber23 = (String) map.get("28");
                        if(null != trainNumber23 && !trainNumber23.isEmpty() && Double.valueOf(trainNumber23) > 0){
                            vehiclePlanEO.setTrainNumber23(trainNumber23);
                        }
                        //车次-24
                        String trainNumber24 = (String) map.get("29");
                        if(null != trainNumber24 && !trainNumber24.isEmpty() && Double.valueOf(trainNumber24) > 0){
                            vehiclePlanEO.setTrainNumber24(trainNumber24);
                        }//车次-25
                        String trainNumber25 = (String) map.get("30");
                        if(null != trainNumber25 && !trainNumber25.isEmpty() && Double.valueOf(trainNumber25) > 0){
                            vehiclePlanEO.setTrainNumber25(trainNumber25);
                        }//车次-26
                        String trainNumber26 = (String) map.get("31");
                        if(null != trainNumber26 && !trainNumber26.isEmpty() && Double.valueOf(trainNumber26) > 0){
                            vehiclePlanEO.setTrainNumber26(trainNumber26);
                        }//车次-27
                        String trainNumber27 = (String) map.get("32");
                        if(null != trainNumber27 && !trainNumber27.isEmpty() && Double.valueOf(trainNumber27) > 0){
                            vehiclePlanEO.setTrainNumber27(trainNumber27);
                        }//车次-28
                        String trainNumber28 = (String) map.get("33");
                        if(null != trainNumber28 && !trainNumber28.isEmpty() && Double.valueOf(trainNumber28) > 0){
                            vehiclePlanEO.setTrainNumber28(trainNumber28);
                        }//车次-29
                        String trainNumber29 = (String) map.get("34");
                        if(null != trainNumber29 && !trainNumber29.isEmpty() && Double.valueOf(trainNumber29) > 0){
                            vehiclePlanEO.setTrainNumber29(trainNumber29);
                        }//车次-30
                        String trainNumber30 = (String) map.get("35");
                        if(null != trainNumber30 && !trainNumber30.isEmpty() && Double.valueOf(trainNumber30) > 0){
                            vehiclePlanEO.setTrainNumber30(trainNumber30);
                        }//车次-31
                        String trainNumber31 = (String) map.get("36");
                        if(null != trainNumber31 && !trainNumber31.isEmpty() && Double.valueOf(trainNumber31) > 0){
                            vehiclePlanEO.setTrainNumber31(trainNumber31);
                        }//车次-32
                        String trainNumber32 = (String) map.get("37");
                        if(null != trainNumber32 && !trainNumber32.isEmpty() && Double.valueOf(trainNumber32) > 0){
                            vehiclePlanEO.setTrainNumber32(trainNumber32);
                        }//车次-33
                        String trainNumber33 = (String) map.get("38");
                        if(null != trainNumber33 && !trainNumber33.isEmpty() && Double.valueOf(trainNumber33) > 0){
                            vehiclePlanEO.setTrainNumber33(trainNumber33);
                        }//车次-34
                        String trainNumber34 = (String) map.get("39");
                        if(null != trainNumber34 && !trainNumber34.isEmpty() && Double.valueOf(trainNumber34) > 0){
                            vehiclePlanEO.setTrainNumber34(trainNumber34);
                        }//车次-35
                        String trainNumber35 = (String) map.get("40");
                        if(null != trainNumber35 && !trainNumber35.isEmpty() && Double.valueOf(trainNumber35) > 0){
                            vehiclePlanEO.setTrainNumber35(trainNumber35);
                        }//车次-36
                        String trainNumber36 = (String) map.get("41");
                        if(null != trainNumber36 && !trainNumber36.isEmpty() && Double.valueOf(trainNumber36) > 0){
                            vehiclePlanEO.setTrainNumber36(trainNumber36);
                        }//车次-37
                        String trainNumber37 = (String) map.get("42");
                        if(null != trainNumber37 && !trainNumber37.isEmpty() && Double.valueOf(trainNumber37) > 0){
                            vehiclePlanEO.setTrainNumber37(trainNumber37);
                        }//车次-38
                        String trainNumber38 = (String) map.get("43");
                        if(null != trainNumber38 && !trainNumber38.isEmpty() && Double.valueOf(trainNumber38) > 0){
                            vehiclePlanEO.setTrainNumber38(trainNumber38);
                        }//车次-39
                        String trainNumber39 = (String) map.get("44");
                        if(null != trainNumber39 && !trainNumber39.isEmpty() && Double.valueOf(trainNumber39) > 0){
                            vehiclePlanEO.setTrainNumber39(trainNumber39);
                        }//车次-40
                        String trainNumber40 = (String) map.get("45");
                        if(null != trainNumber40 && !trainNumber40.isEmpty() && Double.valueOf(trainNumber40) > 0){
                            vehiclePlanEO.setTrainNumber40(trainNumber40);
                        }
                        planEOList.add(vehiclePlanEO);
                    }
                }
            }else{
                throw new BusinessException("请确认文件有内容！");
            }

        }else{
            throw new BusinessException("服务器解析文件出错！");
        }

        return planEOList;
    }

    @Transactional(rollbackFor = Exception.class)
    public String importInsert(VehiclePlanEO[] entitys, UserEO userEO) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String msg = "";
        for(VehiclePlanEO entity:entitys){
            //查询当前物料客户的排车计划是否已存在
            VehiclePlanEO tempEntity = this.baseMapper.selectExistVehiclePlan(entity.getCustomerId(),entity.getMaterialId(),entity.getDeliveryDate(),entity.getOrgId());
            if(null == tempEntity){
                String code = this.generateNextCode("cmp_vehicle_plan", entity,user.getOrgId());
                AssertUtils.isBlank(code);
                entity.setVehiclePlanNo(code);
                super.save(entity);
            }else{

                if(null != tempEntity.getDeliveryNoteNo() && !tempEntity.getDeliveryNoteNo().isEmpty()){
                    msg = msg +"零件号 ["+tempEntity.getElementNo()+"] 已存在内部交易数据，不会进行覆盖操作！<br/>";
                    continue;
                }

//                tempEntity.setProjectNo(entity.getProjectNo());
                tempEntity.setAmount(entity.getAmount());
                tempEntity.setSnp(entity.getSnp());

                //单据号-01
                if((null == tempEntity.getVoucherNo01() || "".equals(tempEntity.getVoucherNo01())) && (null != entity.getTrainNumber01() && !entity.getTrainNumber01().isEmpty() && Integer.valueOf(entity.getTrainNumber01())>=0)){
                    tempEntity.setTrainNumber01(entity.getTrainNumber01());
                }
                //单据号-02
                if((null == tempEntity.getVoucherNo02() || "".equals(tempEntity.getVoucherNo02())) && (null != entity.getTrainNumber02() && !entity.getTrainNumber02().isEmpty() && Integer.valueOf(entity.getTrainNumber02())>=0)){
                    tempEntity.setTrainNumber02(entity.getTrainNumber02());
                }
                //单据号-03
                if((null == tempEntity.getVoucherNo03() || "".equals(tempEntity.getVoucherNo03())) && (null != entity.getTrainNumber03() && !entity.getTrainNumber03().isEmpty() && Integer.valueOf(entity.getTrainNumber03())>=0)){
                    tempEntity.setTrainNumber03(entity.getTrainNumber03());
                }
                //单据号-04
                if((null == tempEntity.getVoucherNo04() || "".equals(tempEntity.getVoucherNo04())) && (null != entity.getTrainNumber04() && !entity.getTrainNumber04().isEmpty() && Integer.valueOf(entity.getTrainNumber04())>=0)){
                    tempEntity.setTrainNumber04(entity.getTrainNumber04());
                }
                //单据号-05
                if((null == tempEntity.getVoucherNo05() || "".equals(tempEntity.getVoucherNo05())) && (null != entity.getTrainNumber05() && !entity.getTrainNumber05().isEmpty() && Integer.valueOf(entity.getTrainNumber05())>=0)){
                    tempEntity.setTrainNumber05(entity.getTrainNumber05());
                }
                //单据号-06
                if((null == tempEntity.getVoucherNo06() || "".equals(tempEntity.getVoucherNo06())) && (null != entity.getTrainNumber06() && !entity.getTrainNumber06().isEmpty() && Integer.valueOf(entity.getTrainNumber06())>=0)){
                    tempEntity.setTrainNumber06(entity.getTrainNumber06());
                }
                //单据号-07
                if((null == tempEntity.getVoucherNo07() || "".equals(tempEntity.getVoucherNo07())) && (null != entity.getTrainNumber07() && !entity.getTrainNumber07().isEmpty() && Integer.valueOf(entity.getTrainNumber07())>=0)){
                    tempEntity.setTrainNumber07(entity.getTrainNumber07());
                }
                //单据号-08
                if((null == tempEntity.getVoucherNo08() || "".equals(tempEntity.getVoucherNo08())) && (null != entity.getTrainNumber08() && !entity.getTrainNumber08().isEmpty() && Integer.valueOf(entity.getTrainNumber08())>=0)){
                    tempEntity.setTrainNumber08(entity.getTrainNumber08());
                }
                //单据号-09
                if((null == tempEntity.getVoucherNo09() || "".equals(tempEntity.getVoucherNo09())) && (null != entity.getTrainNumber09() && !entity.getTrainNumber09().isEmpty() && Integer.valueOf(entity.getTrainNumber09())>=0)){
                    tempEntity.setTrainNumber09(entity.getTrainNumber09());
                }
                //单据号-10
                if((null == tempEntity.getVoucherNo10() || "".equals(tempEntity.getVoucherNo10())) && (null != entity.getTrainNumber10() && !entity.getTrainNumber10().isEmpty() && Integer.valueOf(entity.getTrainNumber10())>=0)){
                    tempEntity.setTrainNumber10(entity.getTrainNumber10());
                }
                //单据号-11
                if((null == tempEntity.getVoucherNo11() || "".equals(tempEntity.getVoucherNo11())) && (null != entity.getTrainNumber11() && !entity.getTrainNumber11().isEmpty() && Integer.valueOf(entity.getTrainNumber11())>=0)){
                    tempEntity.setTrainNumber11(entity.getTrainNumber11());
                }
                //单据号-12
                if((null == tempEntity.getVoucherNo12() || "".equals(tempEntity.getVoucherNo12())) && (null != entity.getTrainNumber12() && !entity.getTrainNumber12().isEmpty() && Integer.valueOf(entity.getTrainNumber12())>=0)){
                    tempEntity.setTrainNumber12(entity.getTrainNumber12());
                }
                //单据号-13
                if((null == tempEntity.getVoucherNo13() || "".equals(tempEntity.getVoucherNo13())) && (null != entity.getTrainNumber13() && !entity.getTrainNumber13().isEmpty() && Integer.valueOf(entity.getTrainNumber13())>=0)){
                    tempEntity.setTrainNumber13(entity.getTrainNumber13());
                }
                //单据号-14
                if((null == tempEntity.getVoucherNo14() || "".equals(tempEntity.getVoucherNo14())) && (null != entity.getTrainNumber14() && !entity.getTrainNumber14().isEmpty() && Integer.valueOf(entity.getTrainNumber14())>=0)){
                    tempEntity.setTrainNumber14(entity.getTrainNumber14());
                }
                //单据号-15
                if((null == tempEntity.getVoucherNo15() || "".equals(tempEntity.getVoucherNo15())) && (null != entity.getTrainNumber15() && !entity.getTrainNumber15().isEmpty() && Integer.valueOf(entity.getTrainNumber15())>=0)){
                    tempEntity.setTrainNumber15(entity.getTrainNumber15());
                }
                //单据号-16
                if((null == tempEntity.getVoucherNo16() || "".equals(tempEntity.getVoucherNo16())) && (null != entity.getTrainNumber16() && !entity.getTrainNumber16().isEmpty() && Integer.valueOf(entity.getTrainNumber16())>=0)){
                    tempEntity.setTrainNumber16(entity.getTrainNumber16());
                }
                //单据号-17
                if((null == tempEntity.getVoucherNo17() || "".equals(tempEntity.getVoucherNo17())) && (null != entity.getTrainNumber17() && !entity.getTrainNumber17().isEmpty() && Integer.valueOf(entity.getTrainNumber17())>=0)){
                    tempEntity.setTrainNumber17(entity.getTrainNumber17());
                }
                //单据号-18
                if((null == tempEntity.getVoucherNo18() || "".equals(tempEntity.getVoucherNo18())) && (null != entity.getTrainNumber18() && !entity.getTrainNumber18().isEmpty() && Integer.valueOf(entity.getTrainNumber18())>=0)){
                    tempEntity.setTrainNumber18(entity.getTrainNumber18());
                }
                //单据号-19
                if((null == tempEntity.getVoucherNo19() || "".equals(tempEntity.getVoucherNo19())) && (null != entity.getTrainNumber19() && !entity.getTrainNumber19().isEmpty() && Integer.valueOf(entity.getTrainNumber19())>=0)){
                    tempEntity.setTrainNumber19(entity.getTrainNumber19());
                }
                //单据号-20
                if((null == tempEntity.getVoucherNo20() || "".equals(tempEntity.getVoucherNo20())) && (null != entity.getTrainNumber20() && !entity.getTrainNumber20().isEmpty() && Integer.valueOf(entity.getTrainNumber20())>=0)){
                    tempEntity.setTrainNumber20(entity.getTrainNumber20());
                }
                //单据号-21
                if((null == tempEntity.getVoucherNo21() || "".equals(tempEntity.getVoucherNo21())) && (null != entity.getTrainNumber21() && !entity.getTrainNumber21().isEmpty() && Integer.valueOf(entity.getTrainNumber21())>=0)){
                    tempEntity.setTrainNumber21(entity.getTrainNumber21());
                }
                //单据号-22
                if((null == tempEntity.getVoucherNo22() || "".equals(tempEntity.getVoucherNo22())) && (null != entity.getTrainNumber22() && !entity.getTrainNumber22().isEmpty() && Integer.valueOf(entity.getTrainNumber22())>=0)){
                    tempEntity.setTrainNumber22(entity.getTrainNumber22());
                }
                //单据号-23
                if((null == tempEntity.getVoucherNo23() || "".equals(tempEntity.getVoucherNo23())) && (null != entity.getTrainNumber23() && !entity.getTrainNumber23().isEmpty() && Integer.valueOf(entity.getTrainNumber23())>=0)){
                    tempEntity.setTrainNumber23(entity.getTrainNumber23());
                }
                //单据号-24
                if((null == tempEntity.getVoucherNo24() || "".equals(tempEntity.getVoucherNo24())) && (null != entity.getTrainNumber24() && !entity.getTrainNumber24().isEmpty() && Integer.valueOf(entity.getTrainNumber24())>=0)){
                    tempEntity.setTrainNumber24(entity.getTrainNumber24());
                }
                //单据号-25
                if((null == tempEntity.getVoucherNo25() || "".equals(tempEntity.getVoucherNo25())) && (null != entity.getTrainNumber25() && !entity.getTrainNumber25().isEmpty() && Integer.valueOf(entity.getTrainNumber25())>=0)){
                    tempEntity.setTrainNumber25(entity.getTrainNumber25());
                }
                //单据号-26
                if((null == tempEntity.getVoucherNo26() || "".equals(tempEntity.getVoucherNo26())) && (null != entity.getTrainNumber26() && !entity.getTrainNumber26().isEmpty() && Integer.valueOf(entity.getTrainNumber26())>=0)){
                    tempEntity.setTrainNumber26(entity.getTrainNumber26());
                }
                //单据号-27
                if((null == tempEntity.getVoucherNo27() || "".equals(tempEntity.getVoucherNo27())) && (null != entity.getTrainNumber27() && !entity.getTrainNumber27().isEmpty() && Integer.valueOf(entity.getTrainNumber27())>=0)){
                    tempEntity.setTrainNumber27(entity.getTrainNumber27());
                }
                //单据号-28
                if((null == tempEntity.getVoucherNo28() || "".equals(tempEntity.getVoucherNo28())) && (null != entity.getTrainNumber28() && !entity.getTrainNumber28().isEmpty() && Integer.valueOf(entity.getTrainNumber28())>=0)){
                    tempEntity.setTrainNumber28(entity.getTrainNumber28());
                }
                //单据号-29
                if((null == tempEntity.getVoucherNo29() || "".equals(tempEntity.getVoucherNo29())) && (null != entity.getTrainNumber29() && !entity.getTrainNumber29().isEmpty() && Integer.valueOf(entity.getTrainNumber29())>=0)){
                    tempEntity.setTrainNumber29(entity.getTrainNumber29());
                }
                //单据号-30
                if((null == tempEntity.getVoucherNo30() || "".equals(tempEntity.getVoucherNo30())) && (null != entity.getTrainNumber30() && !entity.getTrainNumber30().isEmpty() && Integer.valueOf(entity.getTrainNumber30())>=0)){
                    tempEntity.setTrainNumber30(entity.getTrainNumber30());
                }
                //单据号-31
                if((null == tempEntity.getVoucherNo31() || "".equals(tempEntity.getVoucherNo31())) && (null != entity.getTrainNumber31() && !entity.getTrainNumber31().isEmpty() && Integer.valueOf(entity.getTrainNumber31())>=0)){
                    tempEntity.setTrainNumber31(entity.getTrainNumber31());
                }
                //单据号-32
                if((null == tempEntity.getVoucherNo32() || "".equals(tempEntity.getVoucherNo32())) && (null != entity.getTrainNumber32() && !entity.getTrainNumber32().isEmpty() && Integer.valueOf(entity.getTrainNumber32())>=0)){
                    tempEntity.setTrainNumber32(entity.getTrainNumber32());
                }
                //单据号-33
                if((null == tempEntity.getVoucherNo33() || "".equals(tempEntity.getVoucherNo33())) && (null != entity.getTrainNumber33() && !entity.getTrainNumber33().isEmpty() && Integer.valueOf(entity.getTrainNumber33())>=0)){
                    tempEntity.setTrainNumber33(entity.getTrainNumber33());
                }
                //单据号-34
                if((null == tempEntity.getVoucherNo34() || "".equals(tempEntity.getVoucherNo34())) && (null != entity.getTrainNumber34() && !entity.getTrainNumber34().isEmpty() && Integer.valueOf(entity.getTrainNumber34())>=0)){
                    tempEntity.setTrainNumber34(entity.getTrainNumber34());
                }
                //单据号-35
                if((null == tempEntity.getVoucherNo35() || "".equals(tempEntity.getVoucherNo35())) && (null != entity.getTrainNumber35() && !entity.getTrainNumber35().isEmpty() && Integer.valueOf(entity.getTrainNumber35())>=0)){
                    tempEntity.setTrainNumber35(entity.getTrainNumber35());
                }
                //单据号-36
                if((null == tempEntity.getVoucherNo36() || "".equals(tempEntity.getVoucherNo36())) && (null != entity.getTrainNumber36() && !entity.getTrainNumber36().isEmpty() && Integer.valueOf(entity.getTrainNumber36())>=0)){
                    tempEntity.setTrainNumber36(entity.getTrainNumber36());
                }
                //单据号-37
                if((null == tempEntity.getVoucherNo37() || "".equals(tempEntity.getVoucherNo37())) && (null != entity.getTrainNumber37() && !entity.getTrainNumber37().isEmpty() && Integer.valueOf(entity.getTrainNumber37())>=0)){
                    tempEntity.setTrainNumber37(entity.getTrainNumber37());
                }
                //单据号-38
                if((null == tempEntity.getVoucherNo38() || "".equals(tempEntity.getVoucherNo38())) && (null != entity.getTrainNumber38() && !entity.getTrainNumber38().isEmpty() && Integer.valueOf(entity.getTrainNumber38())>=0)){
                    tempEntity.setTrainNumber38(entity.getTrainNumber38());
                }
                //单据号-39
                if((null == tempEntity.getVoucherNo39() || "".equals(tempEntity.getVoucherNo39())) && (null != entity.getTrainNumber39() && !entity.getTrainNumber39().isEmpty() && Integer.valueOf(entity.getTrainNumber39())>=0)){
                    tempEntity.setTrainNumber39(entity.getTrainNumber39());
                }
                //单据号-40
                if((null == tempEntity.getVoucherNo40() || "".equals(tempEntity.getVoucherNo40())) && (null != entity.getTrainNumber40() && !entity.getTrainNumber40().isEmpty() && Integer.valueOf(entity.getTrainNumber40())>=0)){
                    tempEntity.setTrainNumber40(entity.getTrainNumber40());
                }

                super.updateById(tempEntity);
            }
        }

        return msg;
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


    public DeliveryOrderEO getVoucherInfoByNo(String voucherNo, UserEO userEO) throws BusinessException {
        return this.baseMapper.getVoucherInfoByNo(voucherNo);
    }

    public DeliveryOrderEO generateErpVoucherNo3(String voucherNo) throws BusinessException {
        DeliveryOrderEO deliveryOrder = this.baseMapper.getVoucherInfoByNo(voucherNo);
        if(deliveryOrder == null) {
            throw new BusinessException("数据已不存在，请刷新!");
        }
        String erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("export_to_sale_order_" + deliveryOrder.getOrgId());
        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
            deliveryOrder.setErpVoucherNo3(erpVoucherNo);
            this.deliveryOrderMapper.updateErpVoucherNoByIds("erp_voucher_no3", erpVoucherNo, "(" + deliveryOrder.getDeliveryId() + ")");
        } else {
            throw new BusinessException("生成销售订单U8单据号错误!");
        }
        return deliveryOrder;
    }

    public Map getMonthSum(List<VehiclePlanEO> vehiclePlans, String date, UserEO userEO, Map map) {
        if(null != vehiclePlans && vehiclePlans.size()>0){

            List<VehiclePlanEO> list = new ArrayList<>();

            //通过查询的数据汇总和计算架数
            //汇总
            VehiclePlanEO footPlan = new VehiclePlanEO();
            footPlan.setProjectNo("汇总");
            setDefault(footPlan);
            //架数
            VehiclePlanEO vehiclePlanFour = new VehiclePlanEO();
            vehiclePlanFour.setProjectNo("架数");
            setDefault(vehiclePlanFour);

            Integer count = 0;
            for (VehiclePlanEO entity : vehiclePlans){
                //01
                if(null != entity.getTrainNumber01() && !entity.getTrainNumber01().trim().isEmpty()){
                    if(null!= entity.getVoucherNo01() && !entity.getVoucherNo01().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo01());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"01");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"01");
                    }
                }

                //02
                if(null != entity.getTrainNumber02() && !entity.getTrainNumber02().trim().isEmpty()){
                    if(null!= entity.getVoucherNo02() && !entity.getVoucherNo02().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo02());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"02");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"02");
                    }
                }
                //03
                if(null != entity.getTrainNumber03() && !entity.getTrainNumber03().trim().isEmpty()){
                    if(null!= entity.getVoucherNo03() && !entity.getVoucherNo03().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo03());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"03");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"03");
                    }
                }

                //04
                if(null != entity.getTrainNumber04() && !entity.getTrainNumber04().trim().isEmpty()){
                    if(null!= entity.getVoucherNo04() && !entity.getVoucherNo04().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo04());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"04");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"04");
                    }
                }
                //05
                if(null != entity.getTrainNumber05() && !entity.getTrainNumber05().trim().isEmpty()){
                    if(null!= entity.getVoucherNo05() && !entity.getVoucherNo05().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo05());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"05");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"05");
                    }
                }
                //06
                if(null != entity.getTrainNumber06() && !entity.getTrainNumber06().trim().isEmpty()){
                    if(null!= entity.getVoucherNo06() && !entity.getVoucherNo06().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo06());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"06");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"06");
                    }
                }
                //07
                if(null != entity.getTrainNumber07() && !entity.getTrainNumber07().trim().isEmpty()){
                    if(null!= entity.getVoucherNo07() && !entity.getVoucherNo07().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo07());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"07");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"07");
                    }
                }
                //08
                if(null != entity.getTrainNumber08() && !entity.getTrainNumber08().trim().isEmpty()){
                    if(null!= entity.getVoucherNo08() && !entity.getVoucherNo08().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo08());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"08");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"08");
                    }
                }
                //09
                if(null != entity.getTrainNumber09() && !entity.getTrainNumber09().trim().isEmpty()){
                    if(null!= entity.getVoucherNo09() && !entity.getVoucherNo09().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo09());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"09");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"09");
                    }
                }
                //10
                if(null != entity.getTrainNumber10() && !entity.getTrainNumber10().trim().isEmpty()){
                    if(null!= entity.getVoucherNo10() && !entity.getVoucherNo10().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo10());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"10");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"10");
                    }
                }
                //11
                if(null != entity.getTrainNumber11() && !entity.getTrainNumber11().trim().isEmpty()){
                    if(null!= entity.getVoucherNo11() && !entity.getVoucherNo11().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo11());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"11");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"11");
                    }
                }
                //12
                if(null != entity.getTrainNumber12() && !entity.getTrainNumber12().trim().isEmpty()){
                    if(null!= entity.getVoucherNo12() && !entity.getVoucherNo12().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo12());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"12");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"12");
                    }
                }
                //13
                if(null != entity.getTrainNumber13() && !entity.getTrainNumber13().trim().isEmpty()){
                    if(null!= entity.getVoucherNo13() && !entity.getVoucherNo13().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo13());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"13");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"13");
                    }
                }
                //14
                if(null != entity.getTrainNumber14() && !entity.getTrainNumber14().trim().isEmpty()){
                    if(null!= entity.getVoucherNo14() && !entity.getVoucherNo14().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo14());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"14");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"14");
                    }
                }
                //15
                if(null != entity.getTrainNumber15() && !entity.getTrainNumber15().trim().isEmpty()){
                    if(null!= entity.getVoucherNo15() && !entity.getVoucherNo15().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo15());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"15");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"15");
                    }
                }
                //16
                if(null != entity.getTrainNumber16() && !entity.getTrainNumber16().trim().isEmpty()){
                    if(null!= entity.getVoucherNo16() && !entity.getVoucherNo16().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo16());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"16");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"16");
                    }
                }
                //17
                if(null != entity.getTrainNumber17() && !entity.getTrainNumber17().trim().isEmpty()){
                    if(null!= entity.getVoucherNo17() && !entity.getVoucherNo17().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo17());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"17");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"17");
                    }
                }
                //18
                if(null != entity.getTrainNumber18() && !entity.getTrainNumber18().trim().isEmpty()){
                    if(null!= entity.getVoucherNo18() && !entity.getVoucherNo18().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo18());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"18");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"18");
                    }
                }
                //19
                if(null != entity.getTrainNumber19() && !entity.getTrainNumber19().trim().isEmpty()){
                    if(null!= entity.getVoucherNo19() && !entity.getVoucherNo19().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo19());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"19");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"19");
                    }
                }
                //20
                if(null != entity.getTrainNumber20() && !entity.getTrainNumber20().trim().isEmpty()){
                    if(null!= entity.getVoucherNo20() && !entity.getVoucherNo20().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo20());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"20");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"20");
                    }
                }
                //21
                if(null != entity.getTrainNumber21() && !entity.getTrainNumber21().trim().isEmpty()){
                    if(null!= entity.getVoucherNo21() && !entity.getVoucherNo21().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo21());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"21");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"21");
                    }
                }
                //22
                if(null != entity.getTrainNumber22() && !entity.getTrainNumber22().trim().isEmpty()){
                    if(null!= entity.getVoucherNo22() && !entity.getVoucherNo22().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo22());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"22");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"22");
                    }
                }
                //23
                if(null != entity.getTrainNumber23() && !entity.getTrainNumber23().trim().isEmpty()){
                    if(null!= entity.getVoucherNo23() && !entity.getVoucherNo23().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo23());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"23");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"23");
                    }
                }
                //24
                if(null != entity.getTrainNumber24() && !entity.getTrainNumber24().trim().isEmpty()){
                    if(null!= entity.getVoucherNo24() && !entity.getVoucherNo24().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo24());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"24");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"24");
                    }
                }
                //25
                if(null != entity.getTrainNumber25() && !entity.getTrainNumber25().trim().isEmpty()){
                    if(null!= entity.getVoucherNo25() && !entity.getVoucherNo25().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo25());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"25");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"25");
                    }
                }
                //26
                if(null != entity.getTrainNumber26() && !entity.getTrainNumber26().trim().isEmpty()){
                    if(null!= entity.getVoucherNo26() && !entity.getVoucherNo26().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo26());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"26");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"26");
                    }
                }
                //27
                if(null != entity.getTrainNumber27() && !entity.getTrainNumber27().trim().isEmpty()){
                    if(null!= entity.getVoucherNo27() && !entity.getVoucherNo27().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo27());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"27");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"27");
                    }
                }
                //28
                if(null != entity.getTrainNumber28() && !entity.getTrainNumber28().trim().isEmpty()){
                    if(null!= entity.getVoucherNo28() && !entity.getVoucherNo28().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo28());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"28");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"28");
                    }
                }
                //29
                if(null != entity.getTrainNumber29() && !entity.getTrainNumber29().trim().isEmpty()){
                    if(null!= entity.getVoucherNo29() && !entity.getVoucherNo29().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo29());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"29");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"29");
                    }
                }
                //30
                if(null != entity.getTrainNumber30() && !entity.getTrainNumber30().trim().isEmpty()){
                    if(null!= entity.getVoucherNo30() && !entity.getVoucherNo30().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo30());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"30");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"30");
                    }
                }
                //31
                if(null != entity.getTrainNumber31() && !entity.getTrainNumber31().trim().isEmpty()){
                    if(null!= entity.getVoucherNo31() && !entity.getVoucherNo31().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo31());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"31");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"31");
                    }
                }
                //32
                if(null != entity.getTrainNumber32() && !entity.getTrainNumber32().trim().isEmpty()){
                    if(null!= entity.getVoucherNo32() && !entity.getVoucherNo32().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo32());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"32");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"32");
                    }
                }
                //33
                if(null != entity.getTrainNumber33() && !entity.getTrainNumber33().trim().isEmpty()){
                    if(null!= entity.getVoucherNo33() && !entity.getVoucherNo33().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo33());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"33");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"33");
                    }
                }
                //34
                if(null != entity.getTrainNumber34() && !entity.getTrainNumber34().trim().isEmpty()){
                    if(null!= entity.getVoucherNo34() && !entity.getVoucherNo34().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo34());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"34");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"34");
                    }
                }
                //35
                if(null != entity.getTrainNumber35() && !entity.getTrainNumber35().trim().isEmpty()){
                    if(null!= entity.getVoucherNo35() && !entity.getVoucherNo35().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo35());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"35");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"35");
                    }
                }
                //36
                if(null != entity.getTrainNumber36() && !entity.getTrainNumber36().trim().isEmpty()){
                    if(null!= entity.getVoucherNo36() && !entity.getVoucherNo36().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo36());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"36");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"36");
                    }
                }
                //37
                if(null != entity.getTrainNumber37() && !entity.getTrainNumber37().trim().isEmpty()){
                    if(null!= entity.getVoucherNo37() && !entity.getVoucherNo37().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo37());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"37");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"37");
                    }
                }
                //38
                if(null != entity.getTrainNumber38() && !entity.getTrainNumber38().trim().isEmpty()){
                    if(null!= entity.getVoucherNo38() && !entity.getVoucherNo38().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo38());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"38");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"38");
                    }
                }
                //39
                if(null != entity.getTrainNumber39() && !entity.getTrainNumber39().trim().isEmpty()){
                    if(null!= entity.getVoucherNo39() && !entity.getVoucherNo39().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo39());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"39");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"39");
                    }
                }
                //40
                if(null != entity.getTrainNumber40() && !entity.getTrainNumber40().trim().isEmpty()){
                    if(null!= entity.getVoucherNo40() && !entity.getVoucherNo40().trim().isEmpty()){
                        count = this.baseMapper.selectExistFinishOrder(entity.getVoucherNo40());
                        if(count == 0){
                            //汇总，架数计算
                            monthSum(footPlan,vehiclePlanFour,entity,"40");
                        }
                    }else{
                        //汇总，架数计算
                        monthSum(footPlan,vehiclePlanFour,entity,"40");
                    }
                }


            }
//                VehiclePlanEO footPlan = this.baseMapper.selectSumCount(DateUtils.stringToDate(date,"yyyy-MM-dd"),userEO.getOrgId());
//
//
//
//                //架数
//                VehiclePlanEO vehiclePlanFour = this.baseMapper.selectJSSumCount(DateUtils.stringToDate(date,"yyyy-MM-dd"),userEO.getOrgId());
//                vehiclePlanFour.setProjectNo("架数");



            //汇总
            list.add(footPlan);

            //架数
            list.add(vehiclePlanFour);

            //车型
            VehiclePlanEO vehiclePlanOne = new VehiclePlanEO();
            vehiclePlanOne.setProjectNo("车型");


            //时间
            VehiclePlanEO vehiclePlanTwo = new VehiclePlanEO();
            vehiclePlanTwo.setProjectNo("时间");
            //运费
            VehiclePlanEO vehiclePlanThree = new VehiclePlanEO();
            vehiclePlanThree.setProjectNo("运费");

            List<VehicleTrainPlanEO> vehicleTrainPlanEOList = this.baseMapper.selectTrainPlans(DateUtils.stringToDate(date,"yyyy-MM-dd"),userEO.getOrgId());

            for(VehicleTrainPlanEO planEO:vehicleTrainPlanEOList){
                if("01".equals(planEO.getTrainNumber())){
                    vehiclePlanOne.setTrainNumber01(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber01(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber01(planEO.getFreight()+"");
                }else if("02".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber02(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber02(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber02(planEO.getFreight()+"");
                }else if("03".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber03(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber03(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber03(planEO.getFreight()+"");
                }else if("04".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber04(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber04(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber04(planEO.getFreight()+"");
                }else if("05".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber05(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber05(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber05(planEO.getFreight()+"");
                }else if("06".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber06(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber06(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber06(planEO.getFreight()+"");
                }else if("07".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber07(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber07(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber07(planEO.getFreight()+"");

                }else if("08".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber08(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber08(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber08(planEO.getFreight()+"");

                }else if("09".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber09(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber09(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber09(planEO.getFreight()+"");

                }else if("10".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber10(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber10(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber10(planEO.getFreight()+"");

                }else if("11".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber11(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber11(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber11(planEO.getFreight()+"");

                }else if("12".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber12(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber12(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber12(planEO.getFreight()+"");

                }else if("13".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber13(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber13(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber13(planEO.getFreight()+"");

                }else if("14".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber14(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber14(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber14(planEO.getFreight()+"");
                }else if("15".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber15(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber15(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber15(planEO.getFreight()+"");
                }else if("16".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber16(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber16(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber16(planEO.getFreight()+"");
                }else if("17".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber17(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber17(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber17(planEO.getFreight()+"");
                }else if("18".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber18(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber18(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber18(planEO.getFreight()+"");
                }else if("19".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber19(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber19(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber19(planEO.getFreight()+"");
                }else if("20".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber20(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber20(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber20(planEO.getFreight()+"");
                }else if("21".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber21(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber21(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber21(planEO.getFreight()+"");
                }else if("22".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber22(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber22(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber22(planEO.getFreight()+"");
                }else if("23".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber23(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber23(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber23(planEO.getFreight()+"");
                }else if("24".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber24(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber24(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber24(planEO.getFreight()+"");
                }else if("25".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber25(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber25(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber25(planEO.getFreight()+"");
                }else if("26".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber26(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber26(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber26(planEO.getFreight()+"");
                }else if("27".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber27(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber27(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber27(planEO.getFreight()+"");
                }else if("28".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber28(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber28(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber28(planEO.getFreight()+"");
                }else if("29".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber29(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber29(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber29(planEO.getFreight()+"");
                }else if("30".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber30(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber30(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber30(planEO.getFreight()+"");
                }else if("31".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber31(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber31(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber31(planEO.getFreight()+"");
                }else if("32".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber32(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber32(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber32(planEO.getFreight()+"");
                }else if("33".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber33(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber33(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber33(planEO.getFreight()+"");
                }else if("34".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber34(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber34(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber34(planEO.getFreight()+"");
                }else if("35".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber35(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber35(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber35(planEO.getFreight()+"");
                }else if("36".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber36(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber36(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber36(planEO.getFreight()+"");
                }else if("37".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber37(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber37(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber37(planEO.getFreight()+"");
                }else if("38".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber38(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber38(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber38(planEO.getFreight()+"");
                }else if("39".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber39(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber39(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber39(planEO.getFreight()+"");
                }else if("40".equals(planEO.getTrainNumber())){

                    vehiclePlanOne.setTrainNumber40(planEO.getVehicleTypeName());
                    vehiclePlanTwo.setTrainNumber40(planEO.getTrainTime());
                    vehiclePlanThree.setTrainNumber40(planEO.getFreight()+"");
                }
            }

            list.add(vehiclePlanOne);
            list.add(vehiclePlanTwo);
            list.add(vehiclePlanThree);

            map.put("footer",list);

        }else {
            List<VehiclePlanEO> list = new ArrayList<>();
            map.put("footer",list);
        }

        return map;
    }

    public List<VehiclePlanEO> getByVehiclePlanIds(Long[] vehiclePlanIds) throws BusinessException {
        String sqlStr = "";
        if (vehiclePlanIds!=null && vehiclePlanIds.length!=0) {
            for (Long vehiclePlanId : vehiclePlanIds) {
                sqlStr += (vehiclePlanId + ",");
            }
        }
        if(sqlStr.equals("")) {
            sqlStr = "(-1)";
        } else {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        }

        return this.baseMapper.getByVehiclePlanIds(sqlStr);
    }
}
