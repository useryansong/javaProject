package com.xchinfo.erp.srm.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.common.U8DBConnectInfo;
import com.xchinfo.erp.mes.entity.MaterialDistributeEO;
import com.xchinfo.erp.mes.entity.VehiclePlanEO;
import com.xchinfo.erp.mes.mapper.VehiclePlanMapper;
import com.xchinfo.erp.mes.service.MaterialDistributeService;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryPlanEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.srm.mapper.DeliveryNoteDetailMapper;
import com.xchinfo.erp.srm.mapper.DeliveryNoteMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.ParamsEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.conf.service.ParamsService;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.Constant;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.service.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.jdbc.SqlActuator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@Service
public class DeliveryNoteService extends BaseServiceImpl<DeliveryNoteMapper, DeliveryNoteEO> {

    @Autowired
    public BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    @Lazy
    public DeliveryNoteDetailService deliveryNoteDetailService;

    @Autowired
    @Lazy
    public DeliveryPlanService deliveryPlanService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    @Lazy
    private ReceiveOrderService receiveOrderService;

    @Autowired
    private ReceiveOrderDetailService receiveOrderDetailService;

    @Autowired
    private MaterialDistributeService materialDistributeService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ParamsService paramsService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;


    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private VehiclePlanMapper vehiclePlanMapper;

    @Autowired
    private DeliveryNoteDetailMapper deliveryNoteDetailMapper;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private U8DBConnectInfo u8DBConnectInfo;

    @Autowired
    private ReceiveWorkOrderService receiveWorkOrderService;


    static Set<Long> procedureLock =new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */

    public List<DeliveryNoteEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }


    @Transactional(rollbackFor = Exception.class)
    public DeliveryNoteEO saveEntity(DeliveryNoteEO entity, UserEO user) throws BusinessException {
        // 供应商用户的用户名为供应商编码
        SupplierEO supplier = this.supplierService.getBySupplierCode(user.getUserName());
        if(supplier == null) {
            throw new BusinessException("用户不存在供应商!");
        }

        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("srm_delivery_note", entity,Long.valueOf(1));
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);
        entity.setSupplierId(supplier.getSupplierId());
        entity.setTotalDeliveryQuantity(Double.valueOf(0));
        entity.setTotalReceiveQuantity(Double.valueOf(0));
        entity.setTotalQualifiedQuantity(Double.valueOf(0));
        entity.setTotalNotQualifiedQuantity(Double.valueOf(0));
        entity.setStatus(0);
        entity.setHasNotQualified(0);
        entity.setCreatedTime(new Date());
        entity.setChargeUserId(user.getUserId());
        entity.setChargeUserName(user.getUserName());
        super.save(entity);

        return entity;
    }


    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(Long[] ids) throws BusinessException {
        if(ids==null || ids.length==0){
            throw new BusinessException("请选择数据!");
        }

        for(Long id : ids){
            // 删除送货单明细
            this.deliveryNoteDetailService.removeByDeliveryNoteId((Long) id);
        }

        super.removeByIds(ids);
    }


    @Transactional(rollbackFor = Exception.class)
    public Result releaseByIds(Long[] deliveryNoteIds,UserEO userEO) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        int successCount = 0;
        int failCount = 0;

        Integer count = 0;
        //先查询内部供应商
        if(null !=  userEO.getInnerOrg()) {
            count = this.baseMapper.selectInnerOrgExist(userEO.getInnerOrg());
        }

        if(deliveryNoteIds!=null && deliveryNoteIds.length>0){
            for(int i=0; i<deliveryNoteIds.length; i++){
                // 判断是否有送货单明细,有送货单明细才能发布
                List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getByDeliveryNoteId(deliveryNoteIds[i]);
                if(deliveryNoteDetails==null || deliveryNoteDetails.size()==0) {
                    failCount += 1;
                } else {
                    //查询送货单
                    DeliveryNoteEO deliveryNoteEO = super.getById(deliveryNoteIds[i]);
                    //更新状态
                    if(null != deliveryNoteEO && deliveryNoteEO.getStatus() == 0) {
                        this.baseMapper.updateStatusById(deliveryNoteIds[i], 1);
                    }
                    successCount += 1;


                    //内部供应商
                    if(count > 0) {
                        //查询系统参数
                        ParamsEO paramsEO = this.paramsService.getById(Constant.INNERTRAN);
                        //开关打开，则生产出库单以及出库单明细和排车计划(value == 1 则为开关打开)
                        if (null != paramsEO && "1".equals(paramsEO.getParamValue())) {
                            //查询客户
                            CustomerEO customerEO = this.baseMapper.selectCustomerByName(deliveryNoteEO.getFullName());
                            if(null == customerEO){
                                throw new BusinessException("客户 ["+deliveryNoteEO.getFullName()+"] 数据异常或不存在!");
                            }

                            //生成出库单
                            DeliveryOrderEO deliveryOrderTemp = new DeliveryOrderEO();

                            deliveryOrderTemp.setDeliveryDate(deliveryNoteEO.getDeliveryDate());
                            deliveryOrderTemp.setDeliveryType(1);
                            deliveryOrderTemp.setOrgId(userEO.getInnerOrg());
                            deliveryOrderTemp.setStatus(1);
                            deliveryOrderTemp.setIsInnerTran(1);
                            deliveryOrderTemp.setCustomerId(customerEO.getCustomerId());
                            deliveryOrderTemp.setDestinationCode(customerEO.getCustomerCode());
                            deliveryOrderTemp.setDestinationName(customerEO.getCustomerName());
                            deliveryOrderTemp.setVoucherNo(deliveryNoteEO.getVoucherNo());
                            this.deliveryOrderService.saveOrUpdate(deliveryOrderTemp);

                            List<DeliveryNoteDetailEO>  list = this.deliveryNoteDetailMapper.getByDeliveryNoteId(deliveryNoteEO.getDeliveryNoteId(), null, null);
                            for(DeliveryNoteDetailEO deliveryNoteDetailEO:list){

                                MaterialEO materialEO = this.baseMapper.selectMaterialEO(deliveryNoteDetailEO.getElementNo(),deliveryOrderTemp.getOrgId());
                                if(null == materialEO){
                                    //生成出库单明细
                                    DeliveryOrderDetailEO deliveryOrderDetailTemp = new DeliveryOrderDetailEO();
                                    deliveryOrderDetailTemp.setDeliveryOrderId(deliveryOrderTemp.getDeliveryId());
                                    deliveryOrderDetailTemp.setMaterialId(deliveryNoteDetailEO.getMaterialId());
                                    deliveryOrderDetailTemp.setMaterialCode(deliveryNoteDetailEO.getMaterialCode());
                                    deliveryOrderDetailTemp.setMaterialName(deliveryNoteDetailEO.getMaterialName());
                                    deliveryOrderDetailTemp.setElementNo(deliveryNoteDetailEO.getElementNo());
                                    deliveryOrderDetailTemp.setInventoryCode(deliveryNoteDetailEO.getInventoryCode());
                                    deliveryOrderDetailTemp.setFigureNumber(deliveryNoteDetailEO.getFigureNumber());
                                    deliveryOrderDetailTemp.setFigureVersion(deliveryNoteDetailEO.getFigureVersion());
                                    deliveryOrderDetailTemp.setWarehouseId(deliveryNoteDetailEO.getMainWarehouseId());
                                    //                                    deliveryOrderDetailTemp.setWarehouseLocationId(deliveryNoteDetailEO.getWarehouseLocationId());
                                    deliveryOrderDetailTemp.setUnitId(deliveryNoteDetailEO.getUnitId());
                                    deliveryOrderDetailTemp.setDeliveryAmount(deliveryNoteDetailEO.getDeliveryQuantity());
                                    deliveryOrderDetailTemp.setIsMatch(0);
                                    deliveryOrderDetailTemp.setStatus(1);
                                    deliveryOrderDetailTemp.setDeliveryNoteNo(deliveryNoteEO.getVoucherNo());
                                    deliveryOrderDetailTemp.setInnerMaterialId(deliveryNoteDetailEO.getMaterialId());
                                    deliveryOrderDetailTemp.setRemarks("该零件在当前机构不存在！");
                                    this.deliveryOrderDetailService.save(deliveryOrderDetailTemp);

                                    //生成排车计划
                                    VehiclePlanEO vehiclePlanEO =new VehiclePlanEO();
                                    vehiclePlanEO.setOrgId(userEO.getInnerOrg());
                                    vehiclePlanEO.setDeliveryDate(deliveryNoteEO.getDeliveryDate());
                                    vehiclePlanEO.setMaterialId(deliveryNoteDetailEO.getMaterialId());
                                    vehiclePlanEO.setMaterialCode(deliveryNoteDetailEO.getMaterialCode());
                                    vehiclePlanEO.setMaterialName(deliveryNoteDetailEO.getMaterialName());
                                    vehiclePlanEO.setCustomerId(customerEO.getCustomerId());
                                    vehiclePlanEO.setCustomerCode(customerEO.getCustomerCode());
                                    vehiclePlanEO.setCustomerName(customerEO.getCustomerName());
                                    vehiclePlanEO.setDeliveryNoteNo(deliveryNoteEO.getVoucherNo());
                                    vehiclePlanEO.setElementNo(deliveryNoteDetailEO.getElementNo());
                                    vehiclePlanEO.setProjectNo(deliveryNoteDetailEO.getProjectNo());
                                    vehiclePlanEO.setAmount(deliveryNoteDetailEO.getDeliveryQuantity());
                                    vehiclePlanEO.setSnp(deliveryNoteDetailEO.getSnp());
                                    vehiclePlanEO.setStatus(0);

                                    String code = this.businessCodeGenerator.generateNextCode("cmp_vehicle_plan", vehiclePlanEO,user.getOrgId());
                                    AssertUtils.isBlank(code);
                                    vehiclePlanEO.setVehiclePlanNo(code);
                                    this.vehiclePlanMapper.insert(vehiclePlanEO);

                                }else {
                                    //生成出库单明细
                                    DeliveryOrderDetailEO deliveryOrderDetailTemp = new DeliveryOrderDetailEO();
                                    deliveryOrderDetailTemp.setDeliveryOrderId(deliveryOrderTemp.getDeliveryId());
                                    deliveryOrderDetailTemp.setMaterialId(materialEO.getMaterialId());
                                    deliveryOrderDetailTemp.setInnerMaterialId(deliveryNoteDetailEO.getMaterialId());
                                    deliveryOrderDetailTemp.setMaterialCode(materialEO.getMaterialCode());
                                    deliveryOrderDetailTemp.setMaterialName(materialEO.getMaterialName());
                                    deliveryOrderDetailTemp.setElementNo(materialEO.getElementNo());
                                    deliveryOrderDetailTemp.setInventoryCode(materialEO.getInventoryCode());
                                    deliveryOrderDetailTemp.setFigureNumber(materialEO.getFigureNumber());
                                    deliveryOrderDetailTemp.setFigureVersion(materialEO.getFigureVersion());
                                    deliveryOrderDetailTemp.setWarehouseId(materialEO.getMainWarehouseId());
                                    // deliveryOrderDetailTemp.setWarehouseLocationId(deliveryNoteDetailEO.getWarehouseLocationId());
                                    deliveryOrderDetailTemp.setIsMatch(1);
                                    deliveryOrderDetailTemp.setUnitId(materialEO.getFirstMeasurementUnit());
                                    deliveryOrderDetailTemp.setDeliveryAmount(deliveryNoteDetailEO.getDeliveryQuantity());
                                    deliveryOrderDetailTemp.setStatus(1);
                                    deliveryOrderDetailTemp.setDeliveryNoteNo(deliveryNoteEO.getVoucherNo());

                                    this.deliveryOrderDetailService.save(deliveryOrderDetailTemp);

                                    //生成排车计划
                                    VehiclePlanEO vehiclePlanEO =new VehiclePlanEO();
                                    vehiclePlanEO.setOrgId(userEO.getInnerOrg());
                                    vehiclePlanEO.setDeliveryDate(deliveryNoteEO.getDeliveryDate());
                                    vehiclePlanEO.setMaterialId(materialEO.getMaterialId());
                                    vehiclePlanEO.setMaterialCode(materialEO.getMaterialCode());
                                    vehiclePlanEO.setMaterialName(materialEO.getMaterialName());
                                    vehiclePlanEO.setCustomerId(customerEO.getCustomerId());
                                    vehiclePlanEO.setCustomerCode(customerEO.getCustomerCode());
                                    vehiclePlanEO.setCustomerName(customerEO.getCustomerName());
                                    vehiclePlanEO.setDeliveryNoteNo(deliveryNoteEO.getVoucherNo());
                                    vehiclePlanEO.setElementNo(materialEO.getElementNo());
                                    vehiclePlanEO.setProjectNo(materialEO.getProjectNo());
                                    vehiclePlanEO.setAmount(deliveryNoteDetailEO.getDeliveryQuantity());
                                    vehiclePlanEO.setSnp(materialEO.getSnp());
                                    vehiclePlanEO.setStatus(0);

                                    String code = this.businessCodeGenerator.generateNextCode("cmp_vehicle_plan", vehiclePlanEO,user.getOrgId());
                                    AssertUtils.isBlank(code);
                                    vehiclePlanEO.setVehiclePlanNo(code);
                                    this.vehiclePlanMapper.insert(vehiclePlanEO);

                                }
                            }
                        }
                    }

                }
            }
        }
        Result result = new Result();
        String msg = "总共" + deliveryNoteIds.length +"条!<br/>发布成功：" + successCount + "条!<br/>发布失败：" + failCount + "条!";
        result.setMsg(msg);
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelReleaseByIds(Long[] deliveryNoteIds,UserEO userEO) {
        String errMsg = "";
        Integer count = 0;
        //先查询内部供应商
        if(null !=  userEO.getInnerOrg()) {
            count = this.baseMapper.selectInnerOrgExist(userEO.getInnerOrg());
        }
        if(deliveryNoteIds!=null && deliveryNoteIds.length>0){
            for(int i=0; i<deliveryNoteIds.length; i++) {
                //查询送货单
                DeliveryNoteEO deliveryNoteEO = super.getById(deliveryNoteIds[i]);
                //更新状态
                if (null != deliveryNoteEO && deliveryNoteEO.getStatus() != 3) {
                    this.baseMapper.updateStatusById(deliveryNoteIds[i], 0);
                }

                //内部供应商
                if (count > 0) {
                    //先检验出库单明细是否存在已完成
                    Integer existCount = this.baseMapper.selectFinishDetail(deliveryNoteEO.getVoucherNo());
                    if (existCount > 0) {
                        errMsg = errMsg + "送货单 [" + deliveryNoteEO.getVoucherNo() + "] 内部交易存在出库单明细已完成的情况，请确认！<br/>";
                    } else {
                        //删除排车计划
                        this.baseMapper.deleteVehiclePlanByNo(deliveryNoteEO.getVoucherNo());
                        //删除出库明细
                        this.baseMapper.deleteDeliveryDetailByNo(deliveryNoteEO.getVoucherNo());
                        //删除出库单
                        this.baseMapper.deleteDeliveryByNo(deliveryNoteEO.getVoucherNo());
                    }

                }

            }
        }

        if(!errMsg.isEmpty()){
            throw new BusinessException(errMsg);
        }
    }

    // 暂时注释代码
    public DeliveryNoteEO selectDetailById(Serializable id) {
        DeliveryNoteEO deliveryNote = super.getById(id);
        List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getByDeliveryNoteId((Long) id);
        deliveryNote.setDeliveryNoteDetails(deliveryNoteDetails);
        return deliveryNote;
    }

    public DeliveryNoteEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        DeliveryNoteEO noteEO = this.baseMapper.getDetailInfoByNo(voucherNo);
        if(null != noteEO){
            UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
            String userName = user.getUserName();
            Long userId = user.getUserId();
            //校验机构权限(权限不足，则是因为机构权限被收回，操作后会刷新数据，机构权限不存在的数据将看不到了)
            if(!checkPer(noteEO.getOrgId(),userId)){
                throw new BusinessException("送货单的归属机构权限不存在该用户权限，请确认！");
            }

        }else{
            throw new BusinessException("单据号不存在，请确认！");
        }
        List<DeliveryNoteDetailEO> deliveryNoteDetails = this.baseMapper.getByDeliveryNoteId(noteEO.getDeliveryNoteId());
        noteEO.setDeliveryNoteDetails(deliveryNoteDetails);
        return noteEO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusById(Long deliveryNoteId, Integer status) {
        this.baseMapper.updateStatusById(deliveryNoteId, status);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByPrint(DeliveryNoteEO deliveryNote) {
        this.baseMapper.updateStatusById(deliveryNote.getDeliveryNoteId(), 2);
        List<DeliveryNoteDetailEO> deliveryNoteDetails = this.deliveryNoteDetailService.getByDeliveryNoteId(deliveryNote.getDeliveryNoteId());
        if(deliveryNoteDetails!=null && deliveryNoteDetails.size()>0) {
            for(DeliveryNoteDetailEO deliveryNoteDetail : deliveryNoteDetails) {
                if(deliveryNoteDetail.getStatus()==0 || deliveryNoteDetail.getStatus()==1) {
                    this.deliveryNoteDetailService.updateStatusById(deliveryNoteDetail.getDeliveryNoteDetailId(), 2);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(DeliveryNoteEO deliveryNote) throws BusinessException {
        DeliveryNoteEO deliveryNoteFromDb = super.getById(deliveryNote.getDeliveryNoteId());
        if(deliveryNoteFromDb == null) {
            throw new BusinessException("数据已不存在!");
        }

        // 如果归属机构被修改,删掉该送货单下的明细,需要重新添加明细
        if(deliveryNote.getOrgId().longValue() != deliveryNoteFromDb.getOrgId().longValue()) {
            this.deliveryNoteDetailService.removeByDeliveryNoteId(deliveryNote.getDeliveryNoteId());
        }

        return super.updateById(deliveryNote);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean receiveOne(Long Id, Double amount,Long userId,String userName,String action) throws BusinessException {
        try {
            if(procedureLock.contains(Id)){

                throw  new BusinessException("当前数据正在操作中，无法操作请刷新后重试");
            }else{
                procedureLock.add(Id);
                UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
                //查询送货明细
                DeliveryNoteDetailEO deliveryNoteDetailEO = this.baseMapper.selectDeliveryNoteDetailInfo(Id);
                //判断状态
                if("add".equals(action) && deliveryNoteDetailEO.getStatus() != 2){
                    throw new BusinessException("送货单明细状态非送货中，请刷新确认");
                }else if("remove".equals(action) && deliveryNoteDetailEO.getStatus() != 3){
                    throw new BusinessException("送货单明细状态非已完成，请刷新确认");
                }
                if("add".equals(action)) {
                    deliveryNoteDetailEO.setStatus(3);
                    deliveryNoteDetailEO.setActualReceiveQuantity(amount);
                    deliveryNoteDetailEO.setQualifiedQuantity(amount);
                }else{
                    deliveryNoteDetailEO.setStatus(2);
                    deliveryNoteDetailEO.setActualReceiveQuantity(0d);
                    deliveryNoteDetailEO.setQualifiedQuantity(0d);
                }
                this.deliveryNoteDetailService.updateById(deliveryNoteDetailEO);

                //判断送货单是否都已完成(完成则更新为已完成，存在未完成的则更新成送货中)
                DeliveryNoteEO deliveryNoteEO = this.baseMapper.selectById(deliveryNoteDetailEO.getDeliveryNoteId());
                //判断入库单是否存在
                ReceiveOrderEO receiveOrderEO = this.baseMapper.selectReceiveOrderCount(deliveryNoteEO.getDeliveryNoteId());
                if(null == receiveOrderEO){
                    ReceiveOrderEO receiveOrderTemp = new ReceiveOrderEO();
                    receiveOrderTemp.setDeliveryNoteId(deliveryNoteEO.getDeliveryNoteId());
                    receiveOrderTemp.setReceiveDate(new Date());
                    receiveOrderTemp.setOrgId(deliveryNoteEO.getOrgId());
                    receiveOrderTemp.setStatus(1);
                    // 生成业务编码
                    String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", receiveOrderTemp,user.getOrgId());
                    AssertUtils.isBlank(code);
                    receiveOrderTemp.setVoucherNo(code);

                    if(deliveryNoteEO.getType() == 1){
                        receiveOrderTemp.setReceiveType(1);
                    }else{
                        receiveOrderTemp.setReceiveType(3);
                    }

                    this.receiveOrderService.save(receiveOrderTemp);

                    receiveOrderEO = receiveOrderTemp;
                }

                //判断入库单明细是否存在
                ReceiveOrderDetailEO receiveOrderDetailEO = this.baseMapper.selectReceiveOrderDetailCount(deliveryNoteDetailEO.getDeliveryNoteDetailId(),receiveOrderEO.getReceiveId());
                if (null == receiveOrderDetailEO){

                    //若是委外订单，并且零件号带 -W 的
                    if(deliveryNoteEO.getType() == 2 &&  (deliveryNoteDetailEO.getElementNo().endsWith("-W") || deliveryNoteDetailEO.getElementNo().endsWith("-w"))){
                        String newElementNo = deliveryNoteDetailEO.getElementNo().substring(0,deliveryNoteDetailEO.getElementNo().length() -2).trim();

                        MaterialEO materialEO = this.baseMapper.selectMaterialEO(newElementNo,deliveryNoteEO.getOrgId());
                        if(null!= materialEO){
                            deliveryNoteDetailEO.setMaterialId(materialEO.getMaterialId());
                            deliveryNoteDetailEO.setMaterialCode(materialEO.getMaterialCode());
                            deliveryNoteDetailEO.setMaterialName(materialEO.getMaterialName());
                            deliveryNoteDetailEO.setInventoryCode(materialEO.getInventoryCode());

                            deliveryNoteDetailEO.setFigureNumber(materialEO.getFigureNumber());
                            deliveryNoteDetailEO.setFigureVersion(materialEO.getFigureVersion());
                            deliveryNoteDetailEO.setUnitId(materialEO.getFirstMeasurementUnit());
                            deliveryNoteDetailEO.setMainWarehouseId(materialEO.getMainWarehouseId());
                            deliveryNoteDetailEO.setWarehouseLocationId(materialEO.getWarehouseLocationId());
                            deliveryNoteDetailEO.setElementNo(materialEO.getElementNo());

                        }else{
                            throw new BusinessException("物料档案中白件对应的物料不存在，请确认");
                        }

                    }

                    ReceiveOrderDetailEO receiveOrderDetailEOTemp = new ReceiveOrderDetailEO();
                    receiveOrderDetailEOTemp.setReceiveOrderId(receiveOrderEO.getReceiveId());
                    receiveOrderDetailEOTemp.setOrderId(deliveryNoteDetailEO.getDeliveryNoteDetailId());
                    receiveOrderDetailEOTemp.setMaterialId(deliveryNoteDetailEO.getMaterialId());
                    receiveOrderDetailEOTemp.setMaterialCode(deliveryNoteDetailEO.getMaterialCode());
                    receiveOrderDetailEOTemp.setMaterialName(deliveryNoteDetailEO.getMaterialName());
                    receiveOrderDetailEOTemp.setInventoryCode(deliveryNoteDetailEO.getInventoryCode());
                    receiveOrderDetailEOTemp.setElementNo(deliveryNoteDetailEO.getElementNo());
                    receiveOrderDetailEOTemp.setSpecification(deliveryNoteDetailEO.getSpecification());
                    receiveOrderDetailEOTemp.setFigureNumber(deliveryNoteDetailEO.getFigureNumber());
                    receiveOrderDetailEOTemp.setUnitId(deliveryNoteDetailEO.getUnitId());
                    receiveOrderDetailEOTemp.setFigureVersion(deliveryNoteDetailEO.getFigureVersion());

                    MaterialEO materialEO = this.materialMapper.selectById(deliveryNoteDetailEO.getMaterialId());
                    if(null != materialEO.getWarehouseLocationId()){
                        WarehouseLocationEO locationEO = this.warehouseLocationService.getById(materialEO.getWarehouseLocationId());
                        receiveOrderDetailEOTemp.setWarehouseId(locationEO.getWarehouseId());
                    }else{
                        receiveOrderDetailEOTemp.setWarehouseId(materialEO.getMainWarehouseId());
                    }
                    receiveOrderDetailEOTemp.setWarehouseLocationId(deliveryNoteDetailEO.getWarehouseLocationId());
                    receiveOrderDetailEOTemp.setReceiveAmount(deliveryNoteDetailEO.getDeliveryQuantity());
                    receiveOrderDetailEOTemp.setRelReceiveAmount(amount);
                    receiveOrderDetailEOTemp.setStatus(2);

                    this.receiveOrderDetailService.save(receiveOrderDetailEOTemp);
                    receiveOrderDetailEO = receiveOrderDetailEOTemp;
                }else {
                    //如果存在，则重新获取物料的默认库位和对应仓库
                    MaterialEO materialEO = this.materialMapper.selectById(receiveOrderDetailEO.getMaterialId());
                    receiveOrderDetailEO.setWarehouseLocationId(materialEO.getWarehouseLocationId());
                    if (null != materialEO.getWarehouseLocationId()) {
                        WarehouseLocationEO locationEO = this.warehouseLocationService.getById(materialEO.getWarehouseLocationId());
                        receiveOrderDetailEO.setWarehouseId(locationEO.getWarehouseId());
                    } else {
                        receiveOrderDetailEO.setWarehouseId(materialEO.getMainWarehouseId());
                    }
                }

                //已存在则更新状态
                if("add".equals(action)) {
                    receiveOrderDetailEO.setStatus(2);
                    receiveOrderDetailEO.setRelReceiveAmount(amount);
                    //新增库存
                    this.receiveOrderService.setStock(receiveOrderDetailEO,receiveOrderEO);
                }else{
                    if(receiveOrderEO.getReceiveType() == 1 || receiveOrderEO.getReceiveType() == 1){
                        if (receiveOrderDetailEO.getCheckStatus() == 1){
                            throw new BusinessException("该入库单明细已完成对账，无法进行撤回操作!");
                        }
                    }

                    receiveOrderDetailEO.setStatus(1);
                    receiveOrderDetailEO.setRelReceiveAmount(0d);
                    //删除库存
                    this.receiveOrderService.deleteStockByDetailId(receiveOrderDetailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
                }

                this.receiveOrderDetailService.updateById(receiveOrderDetailEO);

                // 获取工厂是否推送入库工单配置
                JSONObject object = ExcelUtils.parseJsonFile("config/work_order.json");
                JSONObject purchaseinstock = object.getJSONObject("purchaseinstock");
                boolean flag = purchaseinstock.getBoolean(deliveryNoteEO.getOrgCode().toLowerCase());
                // 推送入库工单
                if(flag) {
                    this.receiveWorkOrderService.addByReceiveOrderDetail(receiveOrderDetailEO, deliveryNoteEO.getOrgId(), 1, user);
                }

                Integer finishReceiveOrderDetailCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
                if(finishReceiveOrderDetailCount > 0){
                    receiveOrderEO.setStatus(1);
                    receiveOrderEO.setReceiveUserId("");
                    receiveOrderEO.setReceiveUserName("");
                }else {
                    receiveOrderEO.setReceiveUserId(userId+"");
                    receiveOrderEO.setReceiveUserName(userName);
                    receiveOrderEO.setStatus(2);
                }
                this.receiveOrderService.updateById(receiveOrderEO);




                if("add".equals(action) && (deliveryNoteEO.getStatus() == 0 || deliveryNoteEO.getStatus() == 3)){
                    throw new BusinessException("送货单状态为新建或已完成状态、不能进行收货操作,请确认!");
                }

                //校验机构权限（送货单权限）
                if(!checkPer(deliveryNoteEO.getOrgId(),userId)){
                    throw new BusinessException("送货单的归属机构权限不存在该用户权限，请确认！");
                }


                Integer finishCount = this.baseMapper.selectNoteCount(deliveryNoteEO.getDeliveryNoteId());

                if("add".equals(action)) {
                    Double total = nvl(deliveryNoteEO.getTotalReceiveQuantity()) + amount;
                    Double totalQualified =nvl(deliveryNoteEO.getTotalQualifiedQuantity()) + amount;
                    deliveryNoteEO.setTotalReceiveQuantity(total);
                    deliveryNoteEO.setTotalQualifiedQuantity(totalQualified);
                }else{
                    Double total = nvl(deliveryNoteEO.getTotalReceiveQuantity()) - amount;
                    Double totalQualified = nvl(deliveryNoteEO.getTotalQualifiedQuantity()) - amount;
                    deliveryNoteEO.setTotalReceiveQuantity(total);
                    deliveryNoteEO.setTotalQualifiedQuantity(totalQualified);
                }

                if(finishCount == 0){
                    //更新成已完成
                    deliveryNoteEO.setReceiveDate(new Date());
                    deliveryNoteEO.setStatus(3);
                    deliveryNoteEO.setReceiveUserId(userId+"");
                    deliveryNoteEO.setReceiveUserName(userName);
                }else if(finishCount > 0){
                    //更新成送货中
                    deliveryNoteEO.setReceiveUserName("");
                    deliveryNoteEO.setReceiveUserId("");
                    deliveryNoteEO.setReceiveDate(null);
                    this.baseMapper.updateDateNull(deliveryNoteEO.getDeliveryNoteId());
                    deliveryNoteEO.setStatus(2);
                }
                this.baseMapper.updateById(deliveryNoteEO);


                //查询送货计划
                DeliveryPlanEO deliveryPlanEO = this.deliveryPlanService.getById(deliveryNoteDetailEO.getDeliveryPlanId());
                if("add".equals(action) && deliveryPlanEO.getStatus() != 1 && deliveryPlanEO.getStatus() != 9){
                    throw new BusinessException("送货计划状态为新建或已完成，不能进行收货操作，请确认！");
                }

                Double totalDelivery = 0d;
                Double totalReceive = 0d;
                Double TotalQualifiedQuantity = 0d;
                Double returnQuantity = deliveryPlanEO.getReturnedQuantity();

                if("add".equals(action)) {
                    totalDelivery = nvl(deliveryPlanEO.getActualDeliveryQuantity()) + amount;
                    totalReceive = nvl(deliveryPlanEO.getActualReceiveQuantity()) + amount;
                    TotalQualifiedQuantity = nvl(deliveryPlanEO.getQualifiedQuantity()) + amount;
                }else{
                    totalDelivery = nvl(deliveryPlanEO.getActualDeliveryQuantity()) - amount;
                    totalReceive = nvl(deliveryPlanEO.getActualReceiveQuantity()) - amount;
                    TotalQualifiedQuantity = nvl(deliveryPlanEO.getQualifiedQuantity()) - amount;
                }

                if(totalReceive > (deliveryPlanEO.getPlanDeliveryQuantity()+returnQuantity)){
                    BigDecimal planCount = new BigDecimal(nvl(deliveryPlanEO.getPlanDeliveryQuantity()));
                    BigDecimal actualCount = new BigDecimal(nvl(deliveryPlanEO.getActualReceiveQuantity()));
                    BigDecimal needCount = planCount.subtract(actualCount) ;
                    throw new BusinessException("送货计划收货数量已超标，只需数量"+needCount+"!");
                }

                deliveryPlanEO.setActualDeliveryQuantity(totalDelivery);
                deliveryPlanEO.setActualReceiveQuantity(totalReceive);
                deliveryPlanEO.setQualifiedQuantity(TotalQualifiedQuantity);
                logger.info(totalReceive +"========"+ deliveryPlanEO.getPlanDeliveryQuantity());
                if((totalReceive - deliveryPlanEO.getPlanDeliveryQuantity()) == 0d  && deliveryPlanEO.getStatus() != 9){

                    deliveryPlanEO.setStatus(2);

                }else if(deliveryPlanEO.getStatus() != 9){

                    deliveryPlanEO.setStatus(1);
                }
                this.deliveryPlanService.updateById(deliveryPlanEO);

                //查询订单数据
                PurchaseOrderEO purchaseOrderEO =  this.purchaseOrderService.getById(deliveryPlanEO.getPurchaseOrderId());
                if("add".equals(action) && purchaseOrderEO.getStatus() == 2){
                    throw new BusinessException("采购订单状态为已完成，不能进行收货操作，请确认！");
                }

                if("add".equals(action)) {
                    totalReceive = nvl(purchaseOrderEO.getActualDeliveryQuantity()) + amount;
                    TotalQualifiedQuantity = nvl(purchaseOrderEO.getQualifiedQuantity()) + amount;
                }else{
                    totalReceive = nvl(purchaseOrderEO.getActualDeliveryQuantity()) - amount;
                    TotalQualifiedQuantity = nvl(purchaseOrderEO.getQualifiedQuantity()) - amount;
                }


                if(totalReceive > (purchaseOrderEO.getPlanDeliveryQuantity()+returnQuantity)){
                    BigDecimal planCount = new BigDecimal(nvl(purchaseOrderEO.getPlanDeliveryQuantity()));
                    BigDecimal actualCount = new BigDecimal(nvl(purchaseOrderEO.getActualDeliveryQuantity()));
                    BigDecimal needCount =  planCount.subtract(actualCount);
                    throw new BusinessException("采购订单收货数量已超标，只需数量"+needCount+"!");
                }

                purchaseOrderEO.setActualDeliveryQuantity(totalReceive);
                purchaseOrderEO.setQualifiedQuantity(TotalQualifiedQuantity);

                if((totalReceive - purchaseOrderEO.getPlanDeliveryQuantity()) == 0d  && purchaseOrderEO.getStatus() != 9){

                    purchaseOrderEO.setStatus(2);

                }else if(purchaseOrderEO.getStatus() != 9){

                    purchaseOrderEO.setStatus(1);
                }
                this.purchaseOrderService.updateById(purchaseOrderEO);

                //查询采购计划
                if(null != purchaseOrderEO.getSerialDistributeId() && !"".equals(purchaseOrderEO.getSerialDistributeId())) {
                    MaterialDistributeEO materialDistributeEO = this.materialDistributeService.getById(purchaseOrderEO.getSerialDistributeId());
                    if (purchaseOrderEO.getStatus() == 2 && materialDistributeEO.getStatus() != 9) {
                        materialDistributeEO.setStatus(3);
                    } else if (purchaseOrderEO.getStatus() == 1 && materialDistributeEO.getStatus() != 9) {
                        materialDistributeEO.setStatus(2);
                    }
                    this.materialDistributeService.updateById(materialDistributeEO);

                    //月需求计划
                    //           MaterialPlanEO materialPlanEO = this.materialPlanService.getById(materialDistributeEO.getSerialId());
                    //           Integer planFinishCount = this.materialDistributeService.selectPlanFinishCount(materialPlanEO.getSerialId());
                    //           if(planFinishCount == 0){
                    //               materialPlanEO.setStatus(3);
                    //           }else{
                    //               materialPlanEO.setStatus(2);
                    //           }
                    //
                    //            this.materialPlanService.updateById(materialPlanEO);

                }

            }
        }catch (Exception e){
            throw e;
        }finally {
            procedureLock.remove(Id);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean receiveOnelocation(Long Id, Double amount,Long userId,String userName,String action,Long locationId) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        //查询送货明细
        DeliveryNoteDetailEO deliveryNoteDetailEO = this.baseMapper.selectDeliveryNoteDetailInfo(Id);
        //判断状态
        if("add".equals(action) && deliveryNoteDetailEO.getStatus() != 2){
            throw new BusinessException("送货单明细状态非送货中，请刷新确认");
        }else if("remove".equals(action) && deliveryNoteDetailEO.getStatus() != 3){
            throw new BusinessException("送货单明细状态非已完成，请刷新确认");
        }
        if("add".equals(action)) {
            deliveryNoteDetailEO.setStatus(3);
            deliveryNoteDetailEO.setActualReceiveQuantity(amount);
            deliveryNoteDetailEO.setQualifiedQuantity(amount);
        }else{
            deliveryNoteDetailEO.setStatus(2);
            deliveryNoteDetailEO.setActualReceiveQuantity(0d);
            deliveryNoteDetailEO.setQualifiedQuantity(0d);
        }
        this.deliveryNoteDetailService.updateById(deliveryNoteDetailEO);

        //判断送货单是否都已完成(完成则更新为已完成，存在未完成的则更新成送货中)
        DeliveryNoteEO deliveryNoteEO = this.baseMapper.selectById(deliveryNoteDetailEO.getDeliveryNoteId());
        //判断入库单是否存在
        ReceiveOrderEO receiveOrderEO = this.baseMapper.selectReceiveOrderCount(deliveryNoteEO.getDeliveryNoteId());
        if(null == receiveOrderEO){
            ReceiveOrderEO receiveOrderTemp = new ReceiveOrderEO();
            receiveOrderTemp.setDeliveryNoteId(deliveryNoteEO.getDeliveryNoteId());
            receiveOrderTemp.setReceiveDate(deliveryNoteEO.getDeliveryDate());
            receiveOrderTemp.setOrgId(deliveryNoteEO.getOrgId());
            receiveOrderTemp.setStatus(1);
            // 生成业务编码
            String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", receiveOrderTemp,user.getOrgId());
            AssertUtils.isBlank(code);
            receiveOrderTemp.setVoucherNo(code);

            if(deliveryNoteEO.getType() == 1){
                receiveOrderTemp.setReceiveType(1);
            }else{
                receiveOrderTemp.setReceiveType(3);
            }

            this.receiveOrderService.save(receiveOrderTemp);

            receiveOrderEO = receiveOrderTemp;
        }

        //判断入库单明细是否存在
        ReceiveOrderDetailEO receiveOrderDetailEO = this.baseMapper.selectReceiveOrderDetailCount(deliveryNoteDetailEO.getDeliveryNoteDetailId(),receiveOrderEO.getReceiveId());
        if (null == receiveOrderDetailEO){

            //若是委外订单，并且零件号带 -W 的
            if(deliveryNoteEO.getType() == 2 &&  (deliveryNoteDetailEO.getElementNo().endsWith("-W") || deliveryNoteDetailEO.getElementNo().endsWith("-w"))){
                String newElementNo = deliveryNoteDetailEO.getElementNo().substring(0,deliveryNoteDetailEO.getElementNo().length() -2).trim();

                MaterialEO materialEO = this.baseMapper.selectMaterialEO(newElementNo,deliveryNoteEO.getOrgId());
                if(null!= materialEO){
                    deliveryNoteDetailEO.setMaterialId(materialEO.getMaterialId());
                    deliveryNoteDetailEO.setMaterialCode(materialEO.getMaterialCode());
                    deliveryNoteDetailEO.setMaterialName(materialEO.getMaterialName());
                    deliveryNoteDetailEO.setInventoryCode(materialEO.getInventoryCode());

                    deliveryNoteDetailEO.setFigureNumber(materialEO.getFigureNumber());
                    deliveryNoteDetailEO.setFigureVersion(materialEO.getFigureVersion());
                    deliveryNoteDetailEO.setUnitId(materialEO.getFirstMeasurementUnit());
                    deliveryNoteDetailEO.setMainWarehouseId(materialEO.getMainWarehouseId());
                    deliveryNoteDetailEO.setWarehouseLocationId(materialEO.getWarehouseLocationId());
                    deliveryNoteDetailEO.setElementNo(materialEO.getElementNo());

                }else{
                    throw new BusinessException("物料档案中白件对应的物料不存在，请确认");
                }

            }
            ReceiveOrderDetailEO receiveOrderDetailEOTemp = new ReceiveOrderDetailEO();
            receiveOrderDetailEOTemp.setReceiveOrderId(receiveOrderEO.getReceiveId());
            receiveOrderDetailEOTemp.setOrderId(deliveryNoteDetailEO.getDeliveryNoteDetailId());
            receiveOrderDetailEOTemp.setMaterialId(deliveryNoteDetailEO.getMaterialId());
            receiveOrderDetailEOTemp.setMaterialCode(deliveryNoteDetailEO.getMaterialCode());
            receiveOrderDetailEOTemp.setMaterialName(deliveryNoteDetailEO.getMaterialName());
            receiveOrderDetailEOTemp.setInventoryCode(deliveryNoteDetailEO.getInventoryCode());
            receiveOrderDetailEOTemp.setElementNo(deliveryNoteDetailEO.getElementNo());
            receiveOrderDetailEOTemp.setSpecification(deliveryNoteDetailEO.getSpecification());
            receiveOrderDetailEOTemp.setFigureNumber(deliveryNoteDetailEO.getFigureNumber());
            receiveOrderDetailEOTemp.setUnitId(deliveryNoteDetailEO.getUnitId());
            receiveOrderDetailEOTemp.setFigureVersion(deliveryNoteDetailEO.getFigureVersion());
            if (null != locationId){
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                receiveOrderDetailEOTemp.setWarehouseId(locationEO.getWarehouseId());
            }else{
                receiveOrderDetailEOTemp.setWarehouseId(deliveryNoteDetailEO.getMainWarehouseId());
            }
            receiveOrderDetailEOTemp.setWarehouseLocationId(locationId);
            receiveOrderDetailEOTemp.setReceiveAmount(deliveryNoteDetailEO.getDeliveryQuantity());
            receiveOrderDetailEOTemp.setRelReceiveAmount(amount);
            receiveOrderDetailEOTemp.setStatus(2);

            this.receiveOrderDetailService.save(receiveOrderDetailEOTemp);
            receiveOrderDetailEO = receiveOrderDetailEOTemp;
        }else{
            if (null != locationId){
                receiveOrderDetailEO.setWarehouseLocationId(locationId);
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                receiveOrderDetailEO.setWarehouseId(locationEO.getWarehouseId());
            }else{
                receiveOrderDetailEO.setWarehouseLocationId(null);
                receiveOrderDetailEO.setWarehouseId(deliveryNoteDetailEO.getMainWarehouseId());
            }

        }

        //已存在则更新状态
        if("add".equals(action)) {
            receiveOrderDetailEO.setStatus(2);
            receiveOrderDetailEO.setRelReceiveAmount(amount);
            //新增库存
            this.receiveOrderService.setStock(receiveOrderDetailEO,receiveOrderEO);
        }else{
            receiveOrderDetailEO.setStatus(1);
            receiveOrderDetailEO.setRelReceiveAmount(0d);
            //删除库存
            this.receiveOrderService.deleteStockByDetailId(receiveOrderDetailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
        }

        this.receiveOrderDetailService.updateById(receiveOrderDetailEO);

        Integer finishReceiveOrderDetailCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
        if(finishReceiveOrderDetailCount > 0){
            receiveOrderEO.setStatus(1);
            receiveOrderEO.setReceiveUserId("");
            receiveOrderEO.setReceiveUserName("");
        }else {
            receiveOrderEO.setReceiveUserId(userId+"");
            receiveOrderEO.setReceiveUserName(userName);
            receiveOrderEO.setStatus(2);
        }
        this.receiveOrderService.updateById(receiveOrderEO);




        if("add".equals(action) && (deliveryNoteEO.getStatus() == 0 || deliveryNoteEO.getStatus() == 3)){
            throw new BusinessException("送货单状态为新建或已完成状态、不能进行收货操作,请确认!");
        }

        //校验机构权限（送货单权限）
        if(!checkPer(deliveryNoteEO.getOrgId(),userId)){
            throw new BusinessException("送货单的归属机构权限不存在该用户权限，请确认！");
        }


        Integer finishCount = this.baseMapper.selectNoteCount(deliveryNoteEO.getDeliveryNoteId());

        if("add".equals(action)) {
            Double total = nvl(deliveryNoteEO.getTotalReceiveQuantity()) + amount;
            Double totalQualified =nvl(deliveryNoteEO.getTotalQualifiedQuantity()) + amount;
            deliveryNoteEO.setTotalReceiveQuantity(total);
            deliveryNoteEO.setTotalQualifiedQuantity(totalQualified);
        }else{
            Double total = nvl(deliveryNoteEO.getTotalReceiveQuantity()) - amount;
            Double totalQualified = nvl(deliveryNoteEO.getTotalQualifiedQuantity()) - amount;
            deliveryNoteEO.setTotalReceiveQuantity(total);
            deliveryNoteEO.setTotalQualifiedQuantity(totalQualified);
        }

        if(finishCount == 0){
            //更新成已完成
            deliveryNoteEO.setReceiveDate(new Date());
            deliveryNoteEO.setStatus(3);
            deliveryNoteEO.setReceiveUserId(userId+"");
            deliveryNoteEO.setReceiveUserName(userName);
        }else if(finishCount > 0){
            //更新成送货中
            deliveryNoteEO.setReceiveUserName("");
            deliveryNoteEO.setReceiveUserId("");
            deliveryNoteEO.setReceiveDate(null);
            this.baseMapper.updateDateNull(deliveryNoteEO.getDeliveryNoteId());
            deliveryNoteEO.setStatus(2);
        }
        this.baseMapper.updateById(deliveryNoteEO);


        //查询送货计划
        DeliveryPlanEO deliveryPlanEO = this.deliveryPlanService.getById(deliveryNoteDetailEO.getDeliveryPlanId());
        if("add".equals(action) && deliveryPlanEO.getStatus() != 1 && deliveryPlanEO.getStatus() != 9){
            throw new BusinessException("送货计划状态为新建或已完成，不能进行收货操作，请确认！");
        }

        Double totalDelivery = 0d;
        Double totalReceive = 0d;
        Double TotalQualifiedQuantity = 0d;

        if("add".equals(action)) {
            totalDelivery = nvl(deliveryPlanEO.getActualDeliveryQuantity()) + amount;
            totalReceive = nvl(deliveryPlanEO.getActualReceiveQuantity()) + amount;
            TotalQualifiedQuantity = nvl(deliveryPlanEO.getQualifiedQuantity()) + amount;
        }else{
            totalDelivery = nvl(deliveryPlanEO.getActualDeliveryQuantity()) - amount;
            totalReceive = nvl(deliveryPlanEO.getActualReceiveQuantity()) - amount;
            TotalQualifiedQuantity = nvl(deliveryPlanEO.getQualifiedQuantity()) - amount;
        }

        if(totalReceive > deliveryPlanEO.getPlanDeliveryQuantity()){
            BigDecimal planCount = new BigDecimal(nvl(deliveryPlanEO.getPlanDeliveryQuantity()));
            BigDecimal actualCount = new BigDecimal(nvl(deliveryPlanEO.getActualReceiveQuantity()));
            BigDecimal needCount = planCount.subtract(actualCount) ;
            throw new BusinessException("送货计划收货数量已超标，只需数量"+needCount+"!");
        }

        deliveryPlanEO.setActualDeliveryQuantity(totalDelivery);
        deliveryPlanEO.setActualReceiveQuantity(totalReceive);
        deliveryPlanEO.setQualifiedQuantity(TotalQualifiedQuantity);
        logger.info(totalReceive +"========"+ deliveryPlanEO.getPlanDeliveryQuantity());
        if((totalReceive - deliveryPlanEO.getPlanDeliveryQuantity()) == 0d  && deliveryPlanEO.getStatus() != 9){

            deliveryPlanEO.setStatus(2);

        }else if(deliveryPlanEO.getStatus() != 9){

            deliveryPlanEO.setStatus(1);
        }
        this.deliveryPlanService.updateById(deliveryPlanEO);

        //查询订单数据
        PurchaseOrderEO purchaseOrderEO =  this.purchaseOrderService.getById(deliveryPlanEO.getPurchaseOrderId());
        if("add".equals(action) && purchaseOrderEO.getStatus() == 2){
            throw new BusinessException("采购订单状态为已完成，不能进行收货操作，请确认！");
        }

        if("add".equals(action)) {
            totalReceive = nvl(purchaseOrderEO.getActualDeliveryQuantity()) + amount;
            TotalQualifiedQuantity = nvl(purchaseOrderEO.getQualifiedQuantity()) + amount;
        }else{
            totalReceive = nvl(purchaseOrderEO.getActualDeliveryQuantity()) - amount;
            TotalQualifiedQuantity = nvl(purchaseOrderEO.getQualifiedQuantity()) - amount;
        }


        if(totalReceive > purchaseOrderEO.getPlanDeliveryQuantity()){
            BigDecimal planCount = new BigDecimal(nvl(purchaseOrderEO.getPlanDeliveryQuantity()));
            BigDecimal actualCount = new BigDecimal(nvl(purchaseOrderEO.getActualDeliveryQuantity()));
            BigDecimal needCount =  planCount.subtract(actualCount);
            throw new BusinessException("采购订单收货数量已超标，只需数量"+needCount+"!");
        }

        purchaseOrderEO.setActualDeliveryQuantity(totalReceive);
        purchaseOrderEO.setQualifiedQuantity(TotalQualifiedQuantity);

        if((totalReceive - purchaseOrderEO.getPlanDeliveryQuantity()) == 0d  && purchaseOrderEO.getStatus() != 9){

            purchaseOrderEO.setStatus(2);

        }else if(purchaseOrderEO.getStatus() != 9){

            purchaseOrderEO.setStatus(1);
        }
        this.purchaseOrderService.updateById(purchaseOrderEO);

        //查询采购计划
        if(null != purchaseOrderEO.getSerialDistributeId() && !"".equals(purchaseOrderEO.getSerialDistributeId())) {
            MaterialDistributeEO materialDistributeEO = this.materialDistributeService.getById(purchaseOrderEO.getSerialDistributeId());
            if (purchaseOrderEO.getStatus() == 2 && materialDistributeEO.getStatus() != 9) {
                materialDistributeEO.setStatus(3);
            } else if (purchaseOrderEO.getStatus() == 1 && materialDistributeEO.getStatus() != 9) {
                materialDistributeEO.setStatus(2);
            }
            this.materialDistributeService.updateById(materialDistributeEO);

            //月需求计划
//           MaterialPlanEO materialPlanEO = this.materialPlanService.getById(materialDistributeEO.getSerialId());
//           Integer planFinishCount = this.materialDistributeService.selectPlanFinishCount(materialPlanEO.getSerialId());
//           if(planFinishCount == 0){
//               materialPlanEO.setStatus(3);
//           }else{
//               materialPlanEO.setStatus(2);
//           }
//
//            this.materialPlanService.updateById(materialPlanEO);

        }



        return true;
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    public Double nvl(Double value){
        if(null == value || "".equals(value)){
            return 0d;
        }

        return value;
    }

    public List<WarehouseLocationEO> getWarehouseLocation(Long id) {
        //查询送货明细
        DeliveryNoteDetailEO deliveryNoteDetailEO = this.baseMapper.selectDeliveryNoteDetailInfo(id);

        return this.baseMapper.getWarehouseLocation(deliveryNoteDetailEO.getMaterialId());
    }

    public List<DeliveryNoteEO> getByIds(String sqlStr) {
        return this.baseMapper.getByIds(sqlStr);
    }

    public void updateErpVoucherNoByIds(String column, String erpVoucherNo, String sqlStr) {
        this.baseMapper.updateErpVoucherNoByIds(column, erpVoucherNo, sqlStr);
    }

    public void changeErpVoucherNo2(DeliveryNoteEO deliveryNote) throws BusinessException {
        String erpVoucherNo = this.businessCodeGenerator.getSyncValue("export_to_purchase_order_" + deliveryNote.getOrgId());
        String inputErpVoucherNo = deliveryNote.getErpVoucherNo2();
        String erpVoucherNoPrefix = erpVoucherNo.substring(0,2);
        String erpVoucherNoWithoutPrefix = erpVoucherNo.substring(2, erpVoucherNo.length());
        String inputErpVoucherPrefix = inputErpVoucherNo.substring(0,2);
        String inputErpVoucherNoWithoutPrefix = inputErpVoucherNo.substring(2, erpVoucherNo.length());
        if(erpVoucherNoPrefix.equals(inputErpVoucherPrefix) &&
                erpVoucherNoWithoutPrefix.length()==inputErpVoucherNoWithoutPrefix.length() &&
                inputErpVoucherNoWithoutPrefix.matches("^[0-9]+$")) {
            Long erpVoucherNoWithoutPrefixLong = Long.valueOf(erpVoucherNoWithoutPrefix);
            Long inputErpVoucherNoWithoutPrefixLong = Long.valueOf(inputErpVoucherNoWithoutPrefix);
            if(inputErpVoucherNoWithoutPrefixLong.longValue() > erpVoucherNoWithoutPrefixLong.longValue()) {
                throw new BusinessException("输入的U8采购订单单据号不能超过" + erpVoucherNo + "，请重新输入!");
            } else {
                this.updateErpVoucherNoByIds("erp_voucher_no2",inputErpVoucherNo, "(" + deliveryNote.getDeliveryNoteId() + ")");
            }
        } else {
            throw new BusinessException("输入的U8采购订单单据号格式不正确，请重新输入!");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result synU8ArrivedGoods(Long[] deliveryNoteIds, UserEO user) {
        // 获取全局是否同步配置
        JSONObject object = ExcelUtils.parseJsonFile("config/schedule.json");
        Boolean isSubmitSync = false;
        if(object != null) {
            if(object.getBoolean("isSubmitSync") != null) {
                isSubmitSync = object.getBoolean("isSubmitSync");
            }
        }
        if(!isSubmitSync.booleanValue()) {
            throw new BusinessException("全局同步已被禁止!");
        }

        Result result = new Result();
        Map u8CountMap = new HashedMap();
        Map u8Map = new HashedMap();
        Map u8NewMap = new HashedMap();
        Map mesMap = new HashedMap();
        Map u8IDMap = new HashedMap();
        String errorMsg = "";
        List<DeliveryNoteEO> deliveryNotes = this.baseMapper.getU8ListByDeliveryNoteIds(deliveryNoteIds);
        if(deliveryNotes!=null && deliveryNotes.size()>0) {
            for(DeliveryNoteEO deliveryNote : deliveryNotes) {
                // 判断送货单状态(只有完成状态提交同步)
                if(deliveryNote.getStatus().intValue() != 3) {
                    errorMsg += (deliveryNote.getVoucherNo() + "的状态不是已完成!\n");
                }

                // 判断送货单同步状态(只有状态是未同步,部分成功或全部失败的才能提交同步)
                if(deliveryNote.getSyncStatus() == null) {
                    deliveryNote.setSyncStatus(0);
                }
                if(deliveryNote.getSyncStatus().intValue()!=0 &&
                        deliveryNote.getSyncStatus().intValue()!=3 &&
                        deliveryNote.getSyncStatus().intValue()!=4) {
                    errorMsg += (deliveryNote.getVoucherNo() + "的同步状态不是未同步,部分成功,全部失败三者中的一个!\n");
                }

                // 判断送货单明细的仓库id是否有值
                List<DeliveryNoteDetailEO> details = deliveryNote.getDeliveryNoteDetails();
                if(details!=null && details.size()>0) {
                    for(DeliveryNoteDetailEO deliveryNoteDetail : details) {
                        if(deliveryNoteDetail.getMainWarehouseId() == null) {
                            errorMsg += (deliveryNote.getVoucherNo() + "中的" + deliveryNoteDetail.getElementNo() + "没有设置仓库!" + "\n");
                        }
                    }
                }
            }

            if(!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            // 解析mes系统的出库单及明细数据(根据送货单ID及明细仓库ID分条目，且每个存货编码只能出现1次)
            if(deliveryNotes!=null && deliveryNotes.size()>0) {
                String u8Str = "";
                for(DeliveryNoteEO deliveryNote : deliveryNotes) {
                    u8Str += (deliveryNote.getDeliveryNoteId() + ",");
                    List<DeliveryNoteDetailEO> details = deliveryNote.getDeliveryNoteDetails();
                    if(details!=null && details.size()>0) {
                        for(DeliveryNoteDetailEO deliveryNoteDetail : details) {
                            String key = deliveryNote.getDeliveryNoteId() + "-" + deliveryNoteDetail.getMainWarehouseId();
                            if(mesMap.get(key) != null) {
                                Map map = (Map) mesMap.get(key);
                                JSONObject jsonObject = (JSONObject) map.get("Data");
                                JSONArray jsonArray = (JSONArray) jsonObject.get("details");
                                if(jsonArray != null) {
                                    boolean flag = false; // details节点是否包含存货编码，false表示不包含，true表示包含
                                    for(Object o : jsonArray) {
                                        JSONObject jo = (JSONObject) o;
                                        if(jo.get("productcode")!=null && jo.get("productcode").equals(deliveryNoteDetail.getInventoryCode())) {
                                            flag = true;
                                            Double d = (Double) jo.get("productcount");
                                            if(d == null) {
                                                d = Double.valueOf(0);
                                            }

                                            jo.put("productcount", d + deliveryNoteDetail.getActualReceiveQuantity());
                                        }
                                    }

                                    if(!flag) {
                                        JSONObject ic = new JSONObject();
                                        ic.put("productcode", deliveryNoteDetail.getInventoryCode());
                                        ic.put("productcount", deliveryNoteDetail.getActualReceiveQuantity());
                                        ic.put("batchno", "");
                                        ic.put("price", 0);
                                        jsonArray.add(ic);
                                    }
                                }

                                mesMap.put(key, map);
                            } else {
                                JSONObject property = (JSONObject) object.get("arrivedgoods");
                                JSONObject orgProperty = (JSONObject) property.get(deliveryNote.getOrgCode().toLowerCase());

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("date", DateUtils.format(deliveryNote.getDeliveryDate(), "yyyy-MM-dd"));
                                jsonObject.put("whcode", deliveryNoteDetail.getWarehouseErpCode());
                                jsonObject.put("purchasetypecode", orgProperty.get("purchasetypecode")); // 随变，需要配置文件
                                jsonObject.put("vencode", deliveryNoteDetail.getErpCode());
                                jsonObject.put("stockno", "");
                                jsonObject.put("taxrate", orgProperty.get("taxrate")); // 随变，需要配置文件
                                jsonObject.put("maker", user.getRealName());
                                jsonObject.put("depcode", orgProperty.get("depcode"));// 随变化，需要配置文件
                                if(orgProperty.get("voucherNo") != null) { // 随变化，需要配置文件,每个工厂对应的字段不一致
                                    jsonObject.put((String) orgProperty.get("voucherNo"), deliveryNote.getVoucherNo());
                                }

                                JSONArray jsonArray = new JSONArray();
                                JSONObject ic = new JSONObject();
                                ic.put("productcount", deliveryNoteDetail.getActualReceiveQuantity());
                                ic.put("productcode", deliveryNoteDetail.getInventoryCode());
                                ic.put("price", 0);
                                ic.put("batchno", "");
                                jsonArray.add(ic);
                                jsonObject.put("details", jsonArray);

                                Map map = new HashedMap();
                                map.put("Type", "arrivedgoods");
                                map.put("RootOrgID", deliveryNote.getOrgId());
                                map.put("VoucherID", deliveryNote.getDeliveryNoteId());
                                map.put("VoucherTableName", "srm_delivery_note");
                                map.put("WarehouseID", deliveryNoteDetail.getMainWarehouseId());
                                map.put("OrderNumber", deliveryNote.getVoucherNo());
                                map.put("VoucherDate", DateUtils.format(deliveryNote.getDeliveryDate(), "yyyy-MM-dd"));
                                map.put("Data", jsonObject);

                                mesMap.put(key, map);
                            }
                        }
                    }

                    // 查询U8_API_Message中的出库单表对应的所有数据
                    if("".equals(u8Str)) {
                        u8Str = "(-1)";
                    } else {
                        u8Str = "(" + u8Str.substring(0, u8Str.length()-1) + ")";
                    }
                    String selectSql = "select * from U8_API_Message where VoucherTableName = 'wms_delivery_order' and Type='arrivedgoods' and VoucherID in" + u8Str;
                    List<Map<String,Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                    if(u8List!=null && u8List.size()>0) {
                        int u8Count;
                        for(Map map : u8List) {
                            Short Status = (Short) map.get("Status");
                            Long WarehouseID = (Long) map.get("WarehouseID");
                            Long ID = (Long) map.get("ID");
                            Long VoucherID = (Long) map.get("VoucherID");
                            u8Map.put(VoucherID + "-" + WarehouseID, Status);
                            u8IDMap.put(VoucherID + "-" + WarehouseID, ID);

                            if(u8CountMap.get(""+VoucherID) == null) {
                                u8Count = 1;
                            } else {
                                u8Count = (int) u8CountMap.get(""+VoucherID) + 1;
                            }
                            u8CountMap.put(""+VoucherID, u8Count);

                            String key = VoucherID + "-" + WarehouseID;
                            u8NewMap.put(key, map);
                        }
                    }
                }
            }
        }

        Map mesCountMap = new HashedMap();
        int mesCount;
        Map commitCountMap = new HashedMap();
        int commitCount;
        Map<Long, String> dnMap = new HashedMap();
        for(Object o : mesMap.keySet()) {
            // 统计每条数据拆分得到的数量
            Long deliveryNoteId = Long.valueOf(o.toString().split("-")[0]);
            if(mesCountMap.get(""+deliveryNoteId) == null) {
                mesCount = 1;
            } else {
                mesCount = (int) mesCountMap.get(""+deliveryNoteId) + 1;
            }
            mesCountMap.put(""+deliveryNoteId, mesCount);

            if(!u8Map.containsKey(o)) {
                if(commitCountMap.get(""+deliveryNoteId) == null) {
                    commitCount = 1;
                } else {
                    commitCount = (int) commitCountMap.get(""+deliveryNoteId) + 1;
                }
                commitCountMap.put(""+deliveryNoteId, commitCount);
            }

            dnMap.put(deliveryNoteId,  "共拆分为" + mesCountMap.get(""+deliveryNoteId) + "条单据," +
                    "之前已成功" + (u8CountMap.get(""+deliveryNoteId)==null?0:u8CountMap.get(""+deliveryNoteId)) + "条," +
                    "本次提交" + (commitCountMap.get(""+deliveryNoteId)==null?0:commitCountMap.get(""+deliveryNoteId)) + "条  ");
        }

        Set keys = new HashSet();
        for(Object o : mesMap.keySet()) {
            keys.add(o);
        }
        for(Object o : u8NewMap.keySet()) {
            keys.add(o);
        }

        String string = "";
        if(keys!=null && keys.size()>0) {
            for(Object o : keys) {
                Long deliveryNoteId = Long.valueOf(o.toString().split("-")[0]);
                String newMsg = "";
                if(mesMap.keySet().contains(o) && u8NewMap.keySet().contains(o)) { // U8与MES都存在
                    // 统计每条数据拆分得到的数量
                    if(mesCountMap.get(""+deliveryNoteId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryNoteId) + 1;
                    }
                    mesCountMap.put(""+deliveryNoteId, mesCount);

                    if(commitCountMap.get(""+deliveryNoteId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryNoteId) + 1;
                    }
                    commitCountMap.put(""+deliveryNoteId, commitCount);

                    Map u8MapItem = (Map) u8NewMap.get(o);
                    Map mesMapItem = (Map) mesMap.get(o);
                    Short status = (Short) u8MapItem.get("Status");
                    if(status == 1) { // 成功,比较data,写入MES单据中
                        JSONObject mesJsonObject = (JSONObject) mesMapItem.get("Data");
                        JSONObject u8JsonObject = JSONObject.parseObject((String) u8MapItem.get("Data"));
                        JSONArray mesJsonArray = (JSONArray) mesJsonObject.get("details");
                        JSONArray u8JsonArray = (JSONArray) u8JsonObject.get("details");

                        Set jaSet = new HashSet();
                        Map mesJaMap = new HashedMap();
                        Map u8JaMap = new HashedMap();
                        if(mesJsonArray!=null && mesJsonArray.size()>0) {
                            for(Object mo : mesJsonArray) {
                                JSONObject mjo = (JSONObject) mo;
                                String mproductcode = mjo.getString("productcode");
                                Double mproductcount = mjo.getDouble("productcount");
                                jaSet.add(mproductcode);
                                mesJaMap.put(mproductcode, mproductcount);
                            }
                        }

                        if(u8JsonArray!=null && u8JsonArray.size()>0) {
                            for (Object uo : u8JsonArray) {
                                JSONObject ujo = (JSONObject) uo;
                                String uproductcode = ujo.getString("productcode");
                                Double uproductcount = ujo.getDouble("productcount");
                                jaSet.add(uproductcode);
                                u8JaMap.put(uproductcode, uproductcount);
                            }
                        }

                        for(Object jaSetO : jaSet) {
                            if(mesJaMap.keySet().contains(jaSetO) && u8JaMap.keySet().contains(jaSetO)) {
                                Double mproductcount = (Double) mesJaMap.get(jaSetO);
                                Double uproductcount = (Double) u8JaMap.get(jaSetO);
                                if(mproductcount != uproductcount) {
                                    newMsg += "已同步单据" + u8MapItem.get("HandleData") + ",原物料" + jaSetO.toString() + "数量" + uproductcount + ",现数量需要变更为" + mproductcount + ",请核查  ";
                                }
                            }

                            if(!mesJaMap.keySet().contains(jaSetO) && u8JaMap.keySet().contains(jaSetO)) {
                                newMsg += "已同步单据" + u8MapItem.get("HandleData") + ",原存在物料" + jaSetO.toString() + ",现不存在,请核查  ";
                            }

                            if(mesJaMap.keySet().contains(jaSetO) && !u8JaMap.keySet().contains(jaSetO)) {
                                newMsg += "已同步单据" + u8MapItem.get("HandleData") + ",原无物料" + jaSetO.toString() + ",现需增加,请核查  ";
                            }
                        }
                    }
                    if(status == 2) { // 失败,覆盖data节点
                        string += "update U8_API_Message set " +
                                "IsChecked=0," +
                                "Status=0," +
                                "HandleDate=null," +
                                "HandleData=null," +
                                "Data='" + mesMapItem.get("Data").toString() + "' " +
                                "where ID='"+ u8MapItem.get("ID") +"';\n";
                    }
                }

                if(!mesMap.keySet().contains(o) && u8NewMap.keySet().contains(o)) { // 仅U8存在
                    Map u8MapItem = (Map) u8NewMap.get(o);
                    Short status = (Short) u8MapItem.get("Status");
                    if(status == 1) { // 成功,写入MES单据中
                        newMsg += "原" + u8MapItem.get("HandleData") + "单据已同步成功,现已无此单据,请核查  ";
                    }
                }

                if(mesMap.keySet().contains(o) && !u8NewMap.keySet().contains(o)) { // 仅MES存在
                    // 统计每条数据拆分得到的数量
                    if(mesCountMap.get(""+deliveryNoteId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryNoteId) + 1;
                    }
                    mesCountMap.put(""+deliveryNoteId, mesCount);

                    if(commitCountMap.get(""+deliveryNoteId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryNoteId) + 1;
                    }
                    commitCountMap.put(""+deliveryNoteId, commitCount);

                    Map map = (Map) mesMap.get(o);
                    string += "insert into U8_API_Message(" +
                            "Type," +
                            "Data," +
                            "CreateDate," +
                            "IsChecked," +
                            "RootOrgID," +
                            "VoucherID," +
                            "VoucherTableName," +
                            "WarehouseID," +
                            "OrderNumber, " +
                            "VoucherDate" +
                            ")values(" +
                            "'" + map.get("Type") + "'," +
                            "'" + map.get("Data").toString() + "'," +
                            "getdate()" + "," +
                            "0" + "," +
                            map.get("RootOrgID") + "," +
                            map.get("VoucherID") + "," +
                            "'" + map.get("VoucherTableName") + "'," +
                            map.get("WarehouseID") + "," +
                            "'" + map.get("OrderNumber") + "'," +
                            "'" + map.get("VoucherDate") + "'" +
                            ");\n";
                }

                dnMap.put(deliveryNoteId, (dnMap.get(deliveryNoteId)==null?"":dnMap.get(deliveryNoteId)) + newMsg);
            }
        }

        if(!string.equals("")) {
            int ec = SqlActuator.excute(string, u8DBConnectInfo);
            if(ec <= 0) {
                throw new BusinessException("同步失败,未执行到U8!");
            }
        }

        if(dnMap!=null && dnMap.size()>0) {
            int sc = this.baseMapper.syncU8Update(dnMap);
            if(sc <= 0) {
                throw new BusinessException("同步失败,未更新同步信息!");
            }
        }

        return result;
    }

    public Integer getCount(Map map) {
        return this.baseMapper.getCount(map);
    }
}
