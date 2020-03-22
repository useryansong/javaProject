package com.xchinfo.erp.wms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.*;
import com.xchinfo.erp.bsc.service.*;
import com.xchinfo.erp.common.U8DBConnectInfo;
import com.xchinfo.erp.mes.entity.VehiclePlanEO;
import com.xchinfo.erp.mes.entity.VehicleTrainPlanEO;
import com.xchinfo.erp.mes.mapper.VehiclePlanMapper;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.MaterialReceiveEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.service.PurchaseOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.service.UserService;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.conf.service.ParamsService;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.mapper.DeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
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
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;
import org.yecat.mybatis.utils.jdbc.SqlActuator;

import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class DeliveryOrderService extends BaseServiceImpl<DeliveryOrderMapper, DeliveryOrderEO> {
    @Autowired
    private DeliveryOrderDetailMapper deliveryOrderDetailMapper;

    @Autowired
    private DeliveryOrderDetailService deliveryOrderDetailService;

    @Autowired
    private StockAccountService stockAccountService;


    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrgService orgService;


    @Autowired
    private SupplierService supplierService;


    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private BomService bomService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private VehiclePlanMapper vehiclePlanMapper;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private MaterialReceiveService materialReceiveService;

    @Autowired
    private ParamsService paramsService;

    @Autowired
    private U8DBConnectInfo u8DBConnectInfo;

    @Autowired
    private DeliveryWorkOrderService deliveryWorkOrderService;

    @Autowired
    private UserService userService;


    static Set<Long>  procedureLock =new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */


    public List<DeliveryOrderEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }


    public List<DeliveryOrderDetailEO> listDetailsByDelivery(Long orderId) {
        return this.deliveryOrderDetailMapper.selectByDelivery(orderId);
    }

    @Override
    public DeliveryOrderEO getById(Serializable id) {
        DeliveryOrderEO deliveryOrder = this.baseMapper.selectById(id);
        // 查找出库单明细
        List<DeliveryOrderDetailEO> details = this.deliveryOrderDetailMapper.selectByDelivery((Long) id);
        deliveryOrder.setDetails(details);
        return deliveryOrder;
    }

//    @Override
//    public boolean save(DeliveryOrderEO entity) throws BusinessException {
//        // 保存出库单对象
//        this.baseMapper.insert(entity);
//
//        // 保存出库单明细对象
//        if (entity.getDetails()!=null && entity.getDetails().size()>0){
//            for (DeliveryOrderDetailEO detail : entity.getDetails()){
//                detail.setDeliveryOrderId(entity.getDeliveryId());
//            }
//            this.deliveryOrderDetailService.saveBatch(entity.getDetails());
//        }
//
//        return true;
//    }

    private DeliveryOrderEO setByDeliveryType(DeliveryOrderEO entity) {
        if(entity.getDeliveryType().intValue() == 1) {
            CustomerEO customer = this.customerService.getById(entity.getCustomerId());
            entity.setDestinationName(customer.getCustomerName());
            entity.setDestinationCode(customer.getErpCode());
        }
        if(entity.getDeliveryType().intValue()==2 ) {
            return entity;
        }
        if(entity.getDeliveryType().intValue() == 3) {
            SupplierEO supplier = this.supplierService.getById(entity.getSupplierId());
            entity.setDestinationName(supplier.getSupplierName());
            entity.setDestinationCode(supplier.getErpCode());
        }
        if(entity.getDeliveryType().intValue() == 5) {
            OrgEO org = this.orgService.getById(entity.getDestinationOrgId());
            entity.setDestinationName(org.getOrgName());
            entity.setDestinationCode(org.getOrgCode());
        }

        return entity;
    }

    public DeliveryOrderEO saveEntity(DeliveryOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("wms_delivery_order", entity,user.getOrgId());
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);
        entity = setByDeliveryType(entity);
        // 保存出库单对象
        this.baseMapper.insert(entity);
        if(entity.getDeliveryDate() == null){
            entity.setDeliveryDate(new Date());
        }

        // 保存出库单明细对象
        if (entity.getDetails()!=null && entity.getDetails().size()>0){
            for (DeliveryOrderDetailEO detail : entity.getDetails()){
                detail.setDeliveryOrderId(entity.getDeliveryId());
            }
            this.deliveryOrderDetailService.saveBatch(entity.getDetails());
        }

        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean updateById(DeliveryOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        if(entity.getDetails()!=null && entity.getDetails().size()>0) {
            // 删除出库单明细
            this.deliveryOrderDetailService.removeByDelivery(entity.getDeliveryId());
            // 保存出库单明细
            this.deliveryOrderDetailService.saveOrUpdateBatch(entity.getDetails());
        }
        if(entity.getDeliveryDate() == null){
            entity.setDeliveryDate(new Date());
        }
        entity = setByDeliveryType(entity);
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = DeliveryOrderEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        logger.info("======== DeliveryOrderServiceImpl.removeByIds() ========");
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        for(Serializable id: idList){

            DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(deliveryOrderEO.getOrgId(),userId)) {
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            // 删除出库单明细
            this.deliveryOrderDetailService.removeByDelivery((Long) id);
        }
        return super.removeByIds(idList);
    }

    @Override
    public IPage<DeliveryOrderEO> selectPage(Criteria criteria) {
        IPage<DeliveryOrderEO> deliveryOrders = super.selectPage(criteria);
        for (DeliveryOrderEO deliveryOrder : deliveryOrders.getRecords()){
            List<DeliveryOrderDetailEO> details = this.deliveryOrderDetailMapper.selectByDelivery(deliveryOrder.getDeliveryId());
            deliveryOrder.setDetails(details);
        }
        return deliveryOrders;
    }

    /*public int updateStatusById(Long deliveryId, Integer status){
        return this.baseMapper.updateStatusById(deliveryId, status);
    }*/


    public boolean updateStatusById(Long[] idList, Integer status,Integer oldStatus) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        for(Long id:idList){

            DeliveryOrderEO deliveryOrderEOs = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(deliveryOrderEOs.getOrgId(),userId)) {
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }


            if (status.intValue() == 1) {

                int count = this.baseMapper.selectDetailCountById(id);
                if(count == 0){
                    //更新状态
                    this.baseMapper.updateStatusById(id,status,oldStatus);
                    continue;
                }

                /*DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectById(id);
                List<DeliveryOrderDetailEO> deliveryOrderDetailEOS = this.baseMapper.selectDetailById(id);
                for ( DeliveryOrderDetailEO deliveryOrderDetailEO :deliveryOrderDetailEOS) {

                    List<StockAccountEO> messages = this.baseMapper.getMessage(deliveryOrderDetailEO.getDeliveryDetailId());
                    for (StockAccountEO message : messages) {

                        if (message.getCount() - message.getDeliveryAmount() < 0) {
                            throw new BusinessException("出库失败，物料【" + message.getMaterialName() +
                                    "】在【" + message.getWarehouseName() + "】【" + message.getLocationName() + "】库存不足，出库数量:" +
                                    message.getDeliveryAmount() + "库存数量:" + message.getCount() + ",请确认");
                        }


                    }

                    StockAccountEO accountEO = new StockAccountEO();

                    accountEO.setVoucherId(deliveryOrderDetailEO.getDeliveryDetailId());
                    accountEO.setVoucherDate(deliveryOrderEO.getDeliveryDate());

                    if (deliveryOrderEO.getDeliveryType() == 1) {
                        accountEO.setVoucherType(7);
                    } else if (deliveryOrderEO.getDeliveryType() == 2) {
                        accountEO.setVoucherType(8);
                    } else if (deliveryOrderEO.getDeliveryType() == 3) {
                        accountEO.setVoucherType(9);
                    } else {
                        accountEO.setVoucherType(12);
                    }

                    accountEO.setMaterialId(deliveryOrderDetailEO.getMaterialId());
                    accountEO.setMaterialCode(deliveryOrderDetailEO.getMaterialCode());
                    accountEO.setMaterialName(deliveryOrderDetailEO.getMaterialName());
                    accountEO.setInventoryCode(deliveryOrderDetailEO.getInventoryCode());
                    accountEO.setElementNo(deliveryOrderDetailEO.getElementNo());
                    accountEO.setSpecification(deliveryOrderDetailEO.getSpecification());
                    accountEO.setUnitId(deliveryOrderDetailEO.getUnitId());
                    accountEO.setFigureNumber(deliveryOrderDetailEO.getFigureNumber());
                    accountEO.setFigureVersion(deliveryOrderDetailEO.getFigureVersion());
                    accountEO.setWarehouseId(deliveryOrderDetailEO.getWarehouseId());
                    accountEO.setWarehouseLocationId(deliveryOrderDetailEO.getWarehouseLocationId());
                    accountEO.setAmount(deliveryOrderDetailEO.getDeliveryAmount());
                    accountEO.setRemarks(deliveryOrderDetailEO.getRemarks());

                    this.stockAccountService.save(accountEO);

                    this.deliveryOrderDetailMapper.updateStatusById(deliveryOrderDetailEO.getDeliveryDetailId(),status);
                }*/

            }
            else if (status.intValue() == 0){

                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }

                //删除台账
                //this.baseMapper.deleteStockById(id);
            }
            this.baseMapper.updateStatusById(id, status,oldStatus);
        }

        return true;
    }


    // 判断所有数据是否为指定状态
    private boolean isWantedStatus(Long[] deliveryIds, Integer status){
        if(deliveryIds!=null && deliveryIds.length>0){
            for(int i=1; i<deliveryIds.length; i++){
                DeliveryOrderEO deliveryOrder = super.getById(deliveryIds[i]);
                if(deliveryOrder.getStatus().intValue() != status.intValue()){
                    return false;
                }
            }
        }else{
            throw new BusinessException("请先选择数据!");
        }
        return true;
    }

/*    @Override
    public void releaseByIds(Long[] deliveryIds) {
        if(!isWantedStatus(deliveryIds, 0)){
            throw new BusinessException("存在不是新建状态的数据,请刷新!");
        }

        if(deliveryIds!=null && deliveryIds.length>0){
            for(int i=0; i<deliveryIds.length; i++){
                this.baseMapper.updateStatusById(deliveryIds[i], 1);
            }
        }
    }

    @Override
    public void cancelReleaseByIds(Long[] deliveryIds) {
        if(!isWantedStatus(deliveryIds, 1)){
            throw new BusinessException("存在不是已发布的数据,请刷新!");
        }

        if(deliveryIds!=null && deliveryIds.length>0){
            for(int i=0; i<deliveryIds.length; i++){
                this.baseMapper.updateStatusById(deliveryIds[i], 0);
            }
        }
    }*/



    public DeliveryOrderEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        // List<DeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        DeliveryOrderEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //机构权限校验
        if(null != orderEO){
            if(!checkPer(orderEO.getOrgId(),userId)){
                throw new BusinessException("出库单的归属机构权限不存在该用户权限，请确认！");
            }
        }else{
            throw new BusinessException("单据号不存在，请确认！");
        }

        orderEO.setTotalRelDeliveryQuantity(0d);

        Double totalCount = this.baseMapper.selectTotalCount(orderEO.getDeliveryId());
        if(null != totalCount){
            orderEO.setTotalDeliveryQuantity(totalCount);
        }else{
            orderEO.setTotalDeliveryQuantity(0d);
        }

        List<DeliveryOrderDetailEO> details = this.baseMapper.getByDeliveryId(orderEO.getDeliveryId());

        for(DeliveryOrderDetailEO detailEO:details){
            // orderEO.setTotalDeliveryQuantity(orderEO.getTotalDeliveryQuantity() + detailEO.getDeliveryAmount());
            if(detailEO.getStatus() == 2){
                orderEO.setTotalRelDeliveryQuantity(orderEO.getTotalRelDeliveryQuantity() + detailEO.getRelDeliveryAmount());
            }
        }
        orderEO.setDetails(details);
        return orderEO;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean deliveryOne(Long Id, Double amount, String action,Long userId,String userName)  {
        try {
            if(procedureLock.contains(Id)){

                throw  new BusinessException("当前数据正在操作中，无法操作请刷新后重试");
            }else{
                procedureLock.add(Id);
                DeliveryOrderDetailEO detailEO = this.deliveryOrderDetailService.getByDeliveryOrderDetailId(Id);
                //判断状态
                if("add".equals(action) && detailEO.getStatus() != 1){

                    throw new BusinessException("出库单明细状态非已发布状态，请刷新确认");
                }else if("remove".equals(action) && detailEO.getStatus() != 2){
                    throw new BusinessException("出库单明细状态非已完成状态，请刷新确认");
                }

                //判断出库明细是否都已完成
                DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectById(detailEO.getDeliveryOrderId());

                //判断内部交易（去校验物料在本机构是否存在）
                if(null != detailEO.getDeliveryNoteNo() && !detailEO.getDeliveryNoteNo().isEmpty()){
                    MaterialEO materialEO = this.baseMapper.selectMaterialInfo(detailEO.getElementNo(),deliveryOrderEO.getOrgId());
                    if(null == materialEO){
                        throw new BusinessException("该物料零件号["+detailEO.getElementNo()+"]在当前归属机构下不存在，请确认");
                    }else if(materialEO.getMaterialId().longValue() != detailEO.getMaterialId().longValue()){
                        detailEO.setInnerMaterialId(detailEO.getMaterialId());
                        detailEO.setMaterialId(materialEO.getMaterialId());
                        detailEO.setMaterialCode(materialEO.getMaterialCode());
                        detailEO.setMaterialName(materialEO.getMaterialName());
                        detailEO.setInventoryCode(materialEO.getInventoryCode());
                        detailEO.setWarehouseId(materialEO.getMainWarehouseId());
                        detailEO.setUnitId(materialEO.getFirstMeasurementUnit());
                        detailEO.setRemarks("");
                        detailEO.setIsMatch(1);
                    }
                }


                Integer type = 12;
                if (deliveryOrderEO.getDeliveryType() == 1) {
                    type = 7;
                } else if (deliveryOrderEO.getDeliveryType() == 2) {
                    type = 8;
                } else if (deliveryOrderEO.getDeliveryType() == 3) {
                    type = 9;
                } else {
                    type = 12;
                }

                if("add".equals(action)) {
                    detailEO.setStatus(2);
                    detailEO.setRelDeliveryAmount(amount);
                    deliveryOrderEO.setType(deliveryOrderEO.getChildDeliveryType());
                    this.setStock(detailEO,deliveryOrderEO);
                }else{
                    if( type==7 && detailEO.getCheckStatus()==1){
                        throw new BusinessException("该出库单明细已完成对账，无法进行撤回操作!");
                    }
                    detailEO.setStatus(1);
                    detailEO.setRelDeliveryAmount(0d);
                    this.deleteStockByDetailId(detailEO.getDeliveryDetailId(),type);
                }

                this.deliveryOrderDetailMapper.updateById(detailEO);

                // 获取工厂是否推送出库工单配置
                JSONObject object = ExcelUtils.parseJsonFile("config/work_order.json");
                JSONObject productoutstock = object.getJSONObject("productoutstock");
                boolean flag = productoutstock.getBoolean(deliveryOrderEO.getOrgCode().toLowerCase());
                // 推送出库工单
                if(flag) {
                    UserEO user = this.userService.getById(userId);
                    this.deliveryWorkOrderService.addByDeliveryOrderDetail(detailEO, deliveryOrderEO.getOrgId(), 4, user);
                }

                if("add".equals(action) && (deliveryOrderEO.getStatus() == 0 || deliveryOrderEO.getStatus() == 2)){
                    throw new BusinessException("出库单状态为新建或已完成状态、不能进行出库操作,请确认!");
                }

                if(!checkPer(deliveryOrderEO.getOrgId(),userId)){
                    throw new BusinessException("出库单的归属机构权限不存在该用户权限，请确认！");
                }

                Integer finishCount = this.baseMapper.selectDetailFinishCount(deliveryOrderEO.getDeliveryId());
                if(finishCount > 0){
                    deliveryOrderEO.setStatus(1);
                    deliveryOrderEO.setDeliveryUserId("");
                    deliveryOrderEO.setDeliveryUserName("");
                }else {
                    deliveryOrderEO.setStatus(2);
                    deliveryOrderEO.setDeliveryUserId(userId+"");
                    deliveryOrderEO.setDeliveryUserName(userName);
                }

                this.baseMapper.updateById(deliveryOrderEO);

            }
        }catch (Exception e){
            throw e;
        }finally {
            procedureLock.remove(Id);
        }

        return true;
    }


    public List<DeliveryOrderEO> getList(Map map) {
        return this.baseMapper.getList(map);
    }



    public boolean deleteDetailById(Long id) throws BusinessException {
        return this.baseMapper.deleteDetailById(id);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }



    public boolean deleteStockByDetailId(Long Id, Integer type) throws BusinessException {
        return this.baseMapper.deleteStockByDetailId(Id,type);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean setStock(DeliveryOrderDetailEO deliveryOrderDetailEO, DeliveryOrderEO deliveryOrderEO) throws BusinessException {

//        List<StockAccountEO> messages = this.baseMapper.getMessage(deliveryOrderDetailEO.getDeliveryDetailId());
//        for (StockAccountEO message : messages) {
//
//            if (null == message.getCount() || message.getCount() - message.getDeliveryAmount() < 0) {
//                throw new BusinessException("出库失败，物料【" + message.getMaterialName() +
//                        "】在【" + message.getWarehouseName() + "】【" + message.getLocationName() + "】库存不足，出库数量:" +
//                        message.getDeliveryAmount() + "库存数量:" + message.getCount() + ",请确认");
//            }


        List<StockAccountEO> stockAccountEOs = this.baseMapper.selectStockCount(deliveryOrderDetailEO.getMaterialId());

        if( deliveryOrderDetailEO.getRelDeliveryAmount() < 0 &&  deliveryOrderEO.getDeliveryType() != 2 &&  deliveryOrderEO.getDeliveryType() != 3){
            throw new BusinessException("出库数量不能为负 ,请确认！");
        }

        //剩余未出数量
        Double tempAccount = deliveryOrderDetailEO.getRelDeliveryAmount();

        for (StockAccountEO stockAccountEO : stockAccountEOs){

            if(null != stockAccountEO.getCount() && stockAccountEO.getCount() >= tempAccount){

                //入库操作
                StockAccountEO accountEO = new StockAccountEO();
                accountEO.setVoucherId(deliveryOrderDetailEO.getDeliveryDetailId());
                //accountEO.setVoucherDate(deliveryOrderEO.getDeliveryDate());
                accountEO.setVoucherDate(new Date());

                if (deliveryOrderEO.getDeliveryType() == 1) {
                    accountEO.setVoucherType(7);
                } else if (deliveryOrderEO.getDeliveryType() == 2) {
                    accountEO.setVoucherType(8);
                } else if (deliveryOrderEO.getDeliveryType() == 3) {
                    accountEO.setVoucherType(9);
                } else if (deliveryOrderEO.getDeliveryType() == 4){
                    accountEO.setVoucherType(10);
                } else {
                    accountEO.setVoucherType(12);
                    if(null != deliveryOrderEO.getType()){
                        accountEO.setChildVoucherType(deliveryOrderEO.getType());
                        if(accountEO.getChildVoucherType() == 4){
                            accountEO.setRemarks("采购退货出库");
                        }else if(accountEO.getChildVoucherType() == 5){
                            accountEO.setRemarks("委外退货出库");
                        }
                    }else {
                        accountEO.setRemarks(deliveryOrderDetailEO.getRemarks());
                    }
                }

                accountEO.setMaterialId(deliveryOrderDetailEO.getMaterialId());
                accountEO.setMaterialCode(deliveryOrderDetailEO.getMaterialCode());
                accountEO.setMaterialName(deliveryOrderDetailEO.getMaterialName());
                accountEO.setInventoryCode(deliveryOrderDetailEO.getInventoryCode());
                accountEO.setElementNo(deliveryOrderDetailEO.getElementNo());
                accountEO.setSpecification(deliveryOrderDetailEO.getSpecification());
                accountEO.setUnitId(deliveryOrderDetailEO.getUnitId());
                accountEO.setFigureNumber(deliveryOrderDetailEO.getFigureNumber());
                accountEO.setFigureVersion(deliveryOrderDetailEO.getFigureVersion());
                accountEO.setWarehouseId(deliveryOrderDetailEO.getWarehouseId());
                accountEO.setWarehouseLocationId(deliveryOrderDetailEO.getWarehouseLocationId());
                accountEO.setAmount(tempAccount);

                //检查是否存在相同的出库记录，防止卡顿出现多条相同的记录
                int count= this.baseMapper.selectStockByCondition(accountEO.getVoucherId(),accountEO.getMaterialId(),accountEO.getVoucherType(),accountEO.getWarehouseId(),accountEO.getWarehouseLocationId(),tempAccount);
                if(count>0){
                    throw new BusinessException("该条记录已存在，请刷新后重试");
                }
                this.stockAccountService.save(accountEO);
                //差值
                tempAccount = tempAccount - tempAccount;

                break;

            }else if(null != stockAccountEO.getCount() && stockAccountEO.getCount() < tempAccount){


                //入库操作
                StockAccountEO accountEO = new StockAccountEO();
                accountEO.setVoucherId(deliveryOrderDetailEO.getDeliveryDetailId());
                accountEO.setVoucherDate(deliveryOrderEO.getDeliveryDate());

                if (deliveryOrderEO.getDeliveryType() == 1) {
                    accountEO.setVoucherType(7);
                } else if (deliveryOrderEO.getDeliveryType() == 2) {
                    accountEO.setVoucherType(8);
                } else if (deliveryOrderEO.getDeliveryType() == 3) {
                    accountEO.setVoucherType(9);
                } else if (deliveryOrderEO.getDeliveryType() == 4){
                    accountEO.setVoucherType(10);
                } else {
                    accountEO.setVoucherType(12);
                    if(null != deliveryOrderEO.getType()){
                        accountEO.setChildVoucherType(deliveryOrderEO.getType());
                        if(accountEO.getChildVoucherType() == 4){
                            accountEO.setRemarks("采购退货出库");
                        }else if(accountEO.getChildVoucherType() == 5){
                            accountEO.setRemarks("委外退货出库");
                        }
                    }else {
                        accountEO.setRemarks(deliveryOrderDetailEO.getRemarks());
                    }
                }

                accountEO.setMaterialId(deliveryOrderDetailEO.getMaterialId());
                accountEO.setMaterialCode(deliveryOrderDetailEO.getMaterialCode());
                accountEO.setMaterialName(deliveryOrderDetailEO.getMaterialName());
                accountEO.setInventoryCode(deliveryOrderDetailEO.getInventoryCode());
                accountEO.setElementNo(deliveryOrderDetailEO.getElementNo());
                accountEO.setSpecification(deliveryOrderDetailEO.getSpecification());
                accountEO.setUnitId(deliveryOrderDetailEO.getUnitId());
                accountEO.setFigureNumber(deliveryOrderDetailEO.getFigureNumber());
                accountEO.setFigureVersion(deliveryOrderDetailEO.getFigureVersion());
                accountEO.setWarehouseId(deliveryOrderDetailEO.getWarehouseId());
                accountEO.setWarehouseLocationId(stockAccountEO.getWarehouseLocationId());
                accountEO.setAmount(stockAccountEO.getCount());

                //检查是否存在相同的出库记录，防止卡顿出现多条相同的记录
                int count= this.baseMapper.selectStockByCondition(accountEO.getVoucherId(),accountEO.getMaterialId(),accountEO.getVoucherType(),accountEO.getWarehouseId(),accountEO.getWarehouseLocationId(),tempAccount);
                if(count>0){
                    throw new BusinessException("该条记录已存在，请刷新后重试");
                }

                this.stockAccountService.save(accountEO);

                //差值
                tempAccount = tempAccount - stockAccountEO.getCount();

            }

        }

        if (tempAccount>0){
            throw new BusinessException("零件号："+deliveryOrderDetailEO.getElementNo()+"的零件库存不足，库存差值："+tempAccount+" ,请确认！");
        }

        return true;

    }


    @Transactional(rollbackFor = Exception.class)
    public Result addDeliveryOrder(Long materialId, Double amount, Long receiveOrderDetailId, UserEO user) throws BusinessException {
        Result result = new Result();
        int successCount = 0;
        String errorMsg = "";
        MaterialEO material = this.baseMapper.getMaterialById(materialId);
        OrgEO org = this.orgService.getById(material.getOrgId());

        // 查询零件号对应的bom(零件号,归属机构需要一样)
        List<BomEO> bomList = this.bomService.getByElementNoAndOrg(material.getElementNo(), material.getOrgId());
        if(bomList==null || bomList.size()==0) {
//            throw new BusinessException("未找到对应的Bom数据!");
            result.setMsg("操作成功!<br/>生成" + successCount + "条领料单明细!");
            return result;
        }

        BomEO bom = bomList.get(0);// 若真有多条数据,只取第一条数据

        // 是否是特例的零件号，即如果零件号配置的零件号,则直接跳过不扣料
        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/exclude.json");
        JSONArray elementNoArr = (JSONArray) jsonObject.get(org.getOrgCode().toLowerCase());
        if(elementNoArr!=null && elementNoArr.size()>0) {
            for(int p=0; p<elementNoArr.size(); p++) {
                if(elementNoArr.get(p).equals(bom.getElementNo())) {
                    result.setMsg("操作成功!<br/>生成" + successCount + "条领料单明细!");
                    return result;
                }
            }
        }

        // 1.根据零件号找到的bom：若bom为总成或者分总成bom,则拆解寻找该bom下的子节点数据，不管是否为-W或物料是否为委外属性
        // 2.若找到的子节点数据为-W结尾或物料是委外属性的数据,则不继续往下找,
        //   带-W的直接取该-W子节点数据的值去查询物料关系中该-W节点对应的黑件,物料是委外属性的,直接使用该物料
        //   若既是-W又是委外属性的物料，忽略委外属性，-W去找对应件
        // 3.若找到的子节点数据不为-W结尾的数据且不为叶子节点,则继续往下找其叶子节点,将其叶子节点使用
        // 4.若找到的数据为叶子节点,则直接使用
        List<BomEO> boms = this.bomService.getAllLeavesForW(bom);
        // 未找到对应的Bom的子节点数据,则提示
        if(boms==null || boms.size()==0) {
//            throw new BusinessException("未找到对应的Bom的子节点数据!");
            result.setMsg("操作成功!<br/>生成" + successCount + "条领料单明细!");
            return result;
        }

        String paramValue = this.paramsService.getParamByKey("sys_production_report_is_judge_quantity");
        for(BomEO bomTemp : boms) {
            // 判断物料信息是否存在
            if(null == bomTemp.getMaterialId() || bomTemp.getMaterialId().longValue() == 0){
                errorMsg += ("BOM节点下面的叶子节点零件号:"+bomTemp.getElementNo()+" 对应的物料信息不存在!\n");
                continue;
            }

            if(bomTemp.getMaterialName()==null || bomTemp.getMaterialName().equals("")) {
                errorMsg += ("BOM节点下面的叶子节点零件号:"+bomTemp.getElementNo()+" 对应的物料名称为空!\n");
            }

            if(bomTemp.getMaterialCode()==null || bomTemp.getMaterialCode().equals("")) {
                errorMsg += ("BOM节点下面的叶子节点零件号:"+bomTemp.getElementNo()+" 对应的物料编码为空!\n");
            }

            if(Integer.valueOf(paramValue).intValue() == 1) {
                // 查找物料库存
                Double count = this.baseMapper.getStockCountByMaterialId(bomTemp.getMaterialId());
                if (count == null) {
                    count = Double.valueOf(0);
                }
                Double rest = Double.valueOf(0);
                if (count.doubleValue() > 0) {
                    rest = bomTemp.getAmount() * amount - count;
                    if (rest > 0) { // 库存不足时
                        errorMsg += ("物料 [" + bomTemp.getElementNo() + " " + bomTemp.getMaterialName() + "]库存不足:" +
                                "应扣数量" + bomTemp.getAmount() * amount + ",实际库存" + count + ",差额" + rest + "!\n");
                    }
                } else {
                    rest = count - bomTemp.getAmount() * amount;
                    errorMsg += ("物料 [" + bomTemp.getElementNo() + " " + bomTemp.getMaterialName() + "]库存不足:" +
                            "应扣数量" + bomTemp.getAmount() * amount + ",实际库存" + count + ",差额" + (rest * -1) + "!\n");
                }
            }
        }

        if(!"".equals(errorMsg)) {
            result.setCode(500);
            result.setMsg(errorMsg);
            return result;
        }

        // 找到对应的Bom的子节点数据,生产领料单(出库单)和明细(出库单明细)
        DeliveryOrderEO deliveryOrder = new DeliveryOrderEO();
        deliveryOrder.setDeliveryType(2);
        deliveryOrder.setOrgId(material.getOrgId());
        deliveryOrder.setDestinationOrgId(material.getOrgId());
        deliveryOrder.setDeliveryDate(new Date());
        deliveryOrder.setStatus(2);
        deliveryOrder.setDeliveryUserId(user.getUserId() + "");
        deliveryOrder.setDeliveryUserName(user.getUserName());
        deliveryOrder.setRelationId(receiveOrderDetailId);
        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("wms_delivery_order", deliveryOrder,material.getOrgId());
        AssertUtils.isBlank(voucherNo);
        deliveryOrder.setVoucherNo(voucherNo);
        this.baseMapper.insert(deliveryOrder);
        for(BomEO bomTemp : boms) {
            MaterialEO materialTemp = this.baseMapper.getMaterialById(bomTemp.getMaterialId());
            DeliveryOrderDetailEO deliveryOrderDetail = new DeliveryOrderDetailEO();
            deliveryOrderDetail.setDeliveryOrderId(deliveryOrder.getDeliveryId());
            deliveryOrderDetail.setMaterialId(bomTemp.getMaterialId());
            deliveryOrderDetail.setMaterialCode(bomTemp.getMaterialCode());
            deliveryOrderDetail.setMaterialName(bomTemp.getMaterialName());
            deliveryOrderDetail.setInventoryCode(bomTemp.getInventoryCode());
            deliveryOrderDetail.setElementNo(bomTemp.getElementNo());
            deliveryOrderDetail.setSpecification(materialTemp.getSpecification());
            deliveryOrderDetail.setUnitId(materialTemp.getFirstMeasurementUnit());
            deliveryOrderDetail.setFigureNumber(bomTemp.getFigureNumber());
            deliveryOrderDetail.setFigureVersion(bomTemp.getFigureVersion());
            deliveryOrderDetail.setWarehouseId(materialTemp.getMainWarehouseId());
            deliveryOrderDetail.setDeliveryAmount(bomTemp.getAmount() * amount);
            deliveryOrderDetail.setRelDeliveryAmount(bomTemp.getAmount() * amount);
            deliveryOrderDetail.setDestinationName(org.getOrgName());
            deliveryOrderDetail.setDestinationCode(org.getOrgCode());
            deliveryOrderDetail.setStatus(2);
            boolean flag = this.deliveryOrderDetailService.save(deliveryOrderDetail);
            if(flag) {
                successCount ++;
            }

//            // 查询基准物料库存数据是否存在,不存在则创建
//            StockAccountEO stockAccount = this.stockAccountService.getByMaterialIdAndWarehouseId(bomTemp.getMaterialId(), bomTemp.getMainWarehouseId());
//            if(stockAccount == null) {
//                stockAccount = new StockAccountEO();
//                stockAccount.setVoucherType(0);
//                stockAccount.setAmount(Double.valueOf(0));
//                stockAccount.setMaterialId(bomTemp.getMaterialId());
//                stockAccount.setWarehouseId(bomTemp.getMainWarehouseId());
//                stockAccount.setMaterialCode(bomTemp.getMaterialCode());
//                stockAccount.setMaterialName(bomTemp.getMaterialName());
//                stockAccount.setInventoryCode(bomTemp.getInventoryCode());
//                stockAccount.setElementNo(bomTemp.getElementNo());
//                stockAccount.setSpecification(bomTemp.getSpecification());
//                this.stockAccountService.save(stockAccount);
//            }


            // 生成物料库存生产领料类型数据
            StockAccountEO stockAccountTemp = new StockAccountEO();
            stockAccountTemp.setAmount(bomTemp.getAmount() * amount);
            stockAccountTemp.setVoucherType(8);
            stockAccountTemp.setWarehouseId(bomTemp.getMainWarehouseId());
            stockAccountTemp.setMaterialCode(bomTemp.getMaterialCode());
            stockAccountTemp.setMaterialName(bomTemp.getMaterialName());
            stockAccountTemp.setInventoryCode(bomTemp.getInventoryCode());
            stockAccountTemp.setElementNo(bomTemp.getElementNo());
            stockAccountTemp.setSpecification(bomTemp.getSpecification());
            stockAccountTemp.setMaterialId(bomTemp.getMaterialId());
            stockAccountTemp.setVoucherId(deliveryOrderDetail.getDeliveryDetailId());
            stockAccountTemp.setVoucherDate(new Date());
            stockAccountTemp.setUnitId(bomTemp.getUnitId());
            this.stockAccountService.save(stockAccountTemp);
        }

        result.setMsg("操作成功!<br/>生成" + successCount + "条领料单明细!");
        return result;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrderByDetailId(Long receiveOrderDetailId) throws BusinessException {

        DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectByDetailId(receiveOrderDetailId);

        if(null!= deliveryOrderEO){
            //先删除物料库存
            this.baseMapper.deleteSCStockById(deliveryOrderEO.getDeliveryId());

            // 删除出库单明细
            this.deliveryOrderDetailService.removeByDelivery(deliveryOrderEO.getDeliveryId());
            //删除
            super.removeById(deliveryOrderEO.getDeliveryId());
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean setTran(Long[] ids,String trainNumber,UserEO userEO) throws BusinessException{
        String  errMsg = "";
        for(Long id:ids){
            DeliveryOrderEO deliveryOrderEO = super.getById(id);
            //不存在或者已完成则跳过
            if(null == deliveryOrderEO || deliveryOrderEO.getStatus() == 2 || !userEO.getOrgId().equals(deliveryOrderEO.getOrgId())){
                continue;
            }

            Integer detailFinishCount = this.baseMapper.selectCompleteDetail(id);
            if(detailFinishCount > 0){
                errMsg = errMsg+"单据号 ["+deliveryOrderEO.getVoucherNo()+"] 存在明细已完成状态，不允许设置车次!<br/>";
                continue;
            }

            //首次设置
            if(null== deliveryOrderEO.getTrainNumber() || deliveryOrderEO.getTrainNumber().isEmpty()){
                //更新排车计划相应的车次数据
                this.updateTran(trainNumber,deliveryOrderEO,"amount",deliveryOrderEO.getVoucherNo());

            }else if(!trainNumber.equals(deliveryOrderEO.getTrainNumber())){
                //非首次设置，且当前车次数据与设置车次数据不一样，则需更新
                //先把老的更新为空
                this.updateTran(deliveryOrderEO.getTrainNumber(),deliveryOrderEO,"0","");
                this.updateTran(trainNumber,deliveryOrderEO,"amount",deliveryOrderEO.getVoucherNo());
            }

            //查询排车信息
            VehicleTrainPlanEO vehicleTrainPlanEO = this.vehiclePlanMapper.selectExistEntity(deliveryOrderEO.getDeliveryDate(),deliveryOrderEO.getOrgId(),trainNumber);
            if(null == vehicleTrainPlanEO){
                deliveryOrderEO.setTrainNumber(trainNumber);
                deliveryOrderEO.setDeliveryTime("");
                deliveryOrderEO.setVehicleType("");
            }else{
                deliveryOrderEO.setTrainNumber(trainNumber);
                deliveryOrderEO.setDeliveryTime(vehicleTrainPlanEO.getTrainTime());
                deliveryOrderEO.setVehicleType(vehicleTrainPlanEO.getVehicleType());

            }
            super.updateById(deliveryOrderEO);
        }

        if(!errMsg.isEmpty()){
            throw new BusinessException(errMsg);
        }

        return true;
    }

    public void updateTran(String insertTrainNumber,DeliveryOrderEO deliveryOrderEO,String field,String field2){
        String strSql = "";
        String trainNumber = "";

        for(int i=1;i<=40;i++){
            if(i<10){
                trainNumber = "0"+i;
            }else{
                trainNumber = i+"";
            }

            if(insertTrainNumber.equals(trainNumber)){
                strSql = "train_number"+trainNumber+"=  CAST("+field+" AS signed),voucher_no"+trainNumber+"='"+field2+"' where delivery_note_no = '"+deliveryOrderEO.getVoucherNo()+"'";
            }
        }
        if(!strSql.isEmpty()){
            this.baseMapper.updateVehiclePlanByNo(strSql);
        }
    }

    public Double getTran(String date,String trainNumber,UserEO userEO){
        Double result = 0d;

        VehiclePlanEO vehiclePlanEO = this.vehiclePlanMapper.selectJSSumCount(DateUtils.stringToDate(date,"yyyy-MM-dd"),userEO.getOrgId());

        if("01".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber01() && !vehiclePlanEO.getTrainNumber01().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber01());

        }else if("02".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber02() && !vehiclePlanEO.getTrainNumber02().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber02());

        }else if("03".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber03() && !vehiclePlanEO.getTrainNumber03().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber03());

        }else if("04".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber04() && !vehiclePlanEO.getTrainNumber04().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber04());

        }else if("05".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber05() && !vehiclePlanEO.getTrainNumber05().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber05());

        }else if("06".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber06() && !vehiclePlanEO.getTrainNumber06().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber06());

        }else if("07".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber07() && !vehiclePlanEO.getTrainNumber07().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber07());

        }else if("08".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber08() && !vehiclePlanEO.getTrainNumber08().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber08());

        }else if("09".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber09() && !vehiclePlanEO.getTrainNumber09().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber09());

        }else if("10".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber10() && !vehiclePlanEO.getTrainNumber10().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber10());

        }else if("11".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber11() && !vehiclePlanEO.getTrainNumber11().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber11());

        }else if("12".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber12() && !vehiclePlanEO.getTrainNumber12().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber12());

        }else if("13".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber13() && !vehiclePlanEO.getTrainNumber13().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber13());

        }else if("14".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber14() && !vehiclePlanEO.getTrainNumber14().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber14());

        }else if("15".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber15() && !vehiclePlanEO.getTrainNumber15().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber15());

        }else if("16".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber16() && !vehiclePlanEO.getTrainNumber16().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber16());

        }else if("17".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber17() && !vehiclePlanEO.getTrainNumber17().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber17());

        }else if("18".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber18() && !vehiclePlanEO.getTrainNumber18().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber18());

        }else if("19".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber19() && !vehiclePlanEO.getTrainNumber19().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber19());

        }else if("20".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber20() && !vehiclePlanEO.getTrainNumber20().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber20());

        }else if("21".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber21() && !vehiclePlanEO.getTrainNumber21().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber21());

        }else if("22".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber22() && !vehiclePlanEO.getTrainNumber22().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber22());

        }else if("23".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber23() && !vehiclePlanEO.getTrainNumber23().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber23());

        }else if("24".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber24() && !vehiclePlanEO.getTrainNumber24().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber24());

        }else if("25".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber25() && !vehiclePlanEO.getTrainNumber25().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber25());

        }else if("26".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber26() && !vehiclePlanEO.getTrainNumber26().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber26());

        }else if("27".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber27() && !vehiclePlanEO.getTrainNumber27().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber27());

        }else if("28".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber28() && !vehiclePlanEO.getTrainNumber28().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber28());

        }else if("29".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber29() && !vehiclePlanEO.getTrainNumber29().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber29());

        }else if("30".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber30() && !vehiclePlanEO.getTrainNumber30().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber30());

        }else if("31".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber31() && !vehiclePlanEO.getTrainNumber31().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber31());

        }else if("32".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber32() && !vehiclePlanEO.getTrainNumber32().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber32());

        }else if("33".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber33() && !vehiclePlanEO.getTrainNumber33().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber33());

        }else if("34".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber34() && !vehiclePlanEO.getTrainNumber34().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber34());

        }else if("35".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber35() && !vehiclePlanEO.getTrainNumber35().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber35());

        }else if("36".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber36() && !vehiclePlanEO.getTrainNumber36().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber36());

        }else if("37".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber37() && !vehiclePlanEO.getTrainNumber37().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber37());

        }else if("38".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber38() && !vehiclePlanEO.getTrainNumber38().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber38());

        }else if("39".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber39() && !vehiclePlanEO.getTrainNumber39().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber39());

        }else if("40".equals(trainNumber) && null != vehiclePlanEO.getTrainNumber40() && !vehiclePlanEO.getTrainNumber40().isEmpty()){

            result = Double.valueOf(vehiclePlanEO.getTrainNumber40());

        }

        return  result;
    }

    public List<DeliveryOrderEO> getPage(Map map) {
        return this.baseMapper.getPage(map);
    }

    private DeliveryOrderDetailEO getEntity(Long purchaseOrderId, Long deliveryOrderId){
        QueryWrapper<DeliveryOrderDetailEO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", purchaseOrderId);
        queryWrapper.eq("delivery_order_id", deliveryOrderId);
        DeliveryOrderDetailEO deliveryOrderDetail = this.deliveryOrderDetailMapper.selectOne(queryWrapper);
        return deliveryOrderDetail!=null?deliveryOrderDetail:null;
    }

    public String addBatchDetail(Long[] purchaseOrderIds, Long deliveryOrderId) throws BusinessException {
        if(purchaseOrderIds==null || purchaseOrderIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        // 是否需要判断数量
        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
        JSONObject outsideDeliveryOrderJsonObject = jsonObject.getJSONObject("outsideDeliveryOrder");
        boolean isLimitQuantity = outsideDeliveryOrderJsonObject.getBoolean("isLimitQuantity");

        // 是否需要查询物料关系表
        JSONObject bomJsonObject = jsonObject.getJSONObject("bom");
        boolean isQueryMaterialRelationship = bomJsonObject.getBoolean("isQueryMaterialRelationship");

        int sum = 0;
        String errorMsg = "";
        for(Long purchaseOrderId : purchaseOrderIds){
            PurchaseOrderEO purchaseOrder = this.purchaseOrderService.getById(purchaseOrderId);
            DeliveryOrderDetailEO deliveryOrderDetailFromDb = getEntity(purchaseOrderId, deliveryOrderId);
            if(deliveryOrderDetailFromDb != null){
                errorMsg += "添加 ["+ purchaseOrder.getVoucherNo() +"] 订单到出库单时出错:当前出库单已经添加了此订单!<br/>";
            }else{
                if(isLimitQuantity) {
                    Double sumDeliveryAmount = 0d;
                    List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailMapper.getByPurchaseOrderId(purchaseOrderId);
                    if (deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
                        for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                            if(deliveryOrderDetail.getStatus()==0 || deliveryOrderDetail.getStatus()==1) {
                                sumDeliveryAmount += deliveryOrderDetail.getDeliveryAmount();
                            }
                            if(deliveryOrderDetail.getStatus() == 2) {
                                sumDeliveryAmount += deliveryOrderDetail.getRelDeliveryAmount();
                            }
                        }
                    }

                    if(sumDeliveryAmount >= purchaseOrder.getActualDeliveryQuantity()) {
                        errorMsg += "添加 ["+ purchaseOrder.getVoucherNo() +"] 订单到出库单时出错,超出订单实际送货数量!<br/>";
                    }

                    if(sumDeliveryAmount < purchaseOrder.getActualDeliveryQuantity()) {
                        DeliveryOrderDetailEO deliveryOrderDetail = new DeliveryOrderDetailEO();
                        MaterialEO materialTemp = this.materialService.getById(purchaseOrder.getMaterialId());
                        if(materialTemp != null) {
                            MaterialEO material;

                            if(isQueryMaterialRelationship) {
                                // 需要查询物料关系表
                                material = this.materialService.getParentMaterial(materialTemp.getMaterialId());
                            } else {
                                // 判断materialTemp的白件是否存在,即零件号后面带-W去查询物料表是否存在
                                QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
                                wrapper.eq("element_no", materialTemp.getElementNo() + "-W");
                                wrapper.eq("org_id", materialTemp.getOrgId());
                                material = this.materialService.getOne(wrapper);
                            }

                            if(material == null) {
                                errorMsg += "未找到订单 [" + purchaseOrder.getVoucherNo() + "] 对应的白件零件号,请在物料档案中设置!<br/>";
                                continue;
                            }

                            deliveryOrderDetail.setOrderId(purchaseOrderId);
                            deliveryOrderDetail.setDeliveryOrderId(deliveryOrderId);
                            deliveryOrderDetail.setMaterialCode(material.getMaterialCode());
                            deliveryOrderDetail.setMaterialId(material.getMaterialId());
                            deliveryOrderDetail.setMaterialName(material.getMaterialName());
                            deliveryOrderDetail.setInventoryCode(material.getInventoryCode());
                            deliveryOrderDetail.setElementNo(material.getElementNo());
                            deliveryOrderDetail.setSpecification(material.getSpecification());
                            deliveryOrderDetail.setUnitId(material.getFirstMeasurementUnit());
                            deliveryOrderDetail.setFigureNumber(material.getFigureNumber());
                            deliveryOrderDetail.setFigureVersion(material.getFigureVersion());
                            deliveryOrderDetail.setWarehouseId(material.getMainWarehouseId());
                            deliveryOrderDetail.setUnitName(purchaseOrder.getUnitName());
                            deliveryOrderDetail.setDeliveryAmount(Double.valueOf(0));
                            deliveryOrderDetail.setRelDeliveryAmount(Double.valueOf(0));
                            deliveryOrderDetail.setStatus(0);
                            boolean flag = this.deliveryOrderDetailService.save(deliveryOrderDetail);
                            if (flag) {
                                sum += 1;
                            }
                        }
                    } else {
                        errorMsg += "添加 ["+ purchaseOrder.getVoucherNo() +"] 订单到出库单时出错,物料不存在!<br/>";
                    }
                } else {
                    DeliveryOrderDetailEO deliveryOrderDetail = new DeliveryOrderDetailEO();
                    MaterialEO materialTemp = this.materialService.getById(purchaseOrder.getMaterialId());
                    if(materialTemp != null) {
                        MaterialEO material;

                        if(isQueryMaterialRelationship) {
                            // 需要查询物料关系表
                            material = this.materialService.getParentMaterial(materialTemp.getMaterialId());
                        } else {
                            // 判断materialTemp的白件是否存在,即零件号后面带-W去查询物料表是否存在
                            QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
                            wrapper.eq("element_no", materialTemp.getElementNo() + "-W");
                            wrapper.eq("org_id", materialTemp.getOrgId());
                            material = this.materialService.getOne(wrapper);
                        }

                        if(material == null) {
                            errorMsg += "未找到订单 [" + purchaseOrder.getVoucherNo() + "] 对应的白件零件号,请在物料档案中设置!<br/>";
                            continue;
                        }

                        deliveryOrderDetail.setOrderId(purchaseOrderId);
                        deliveryOrderDetail.setDeliveryOrderId(deliveryOrderId);
                        deliveryOrderDetail.setMaterialCode(material.getMaterialCode());
                        deliveryOrderDetail.setMaterialId(material.getMaterialId());
                        deliveryOrderDetail.setMaterialName(material.getMaterialName());
                        deliveryOrderDetail.setInventoryCode(material.getInventoryCode());
                        deliveryOrderDetail.setElementNo(material.getElementNo());
                        deliveryOrderDetail.setSpecification(material.getSpecification());
                        deliveryOrderDetail.setUnitId(material.getFirstMeasurementUnit());
                        deliveryOrderDetail.setFigureNumber(material.getFigureNumber());
                        deliveryOrderDetail.setFigureVersion(material.getFigureVersion());
                        deliveryOrderDetail.setWarehouseId(material.getMainWarehouseId());
                        deliveryOrderDetail.setUnitName(purchaseOrder.getUnitName());
                        deliveryOrderDetail.setDeliveryAmount(Double.valueOf(0));
                        deliveryOrderDetail.setRelDeliveryAmount(Double.valueOf(0));
                        deliveryOrderDetail.setStatus(0);
                        boolean flag = this.deliveryOrderDetailService.save(deliveryOrderDetail);
                        if (flag) {
                            sum += 1;
                        }
                    } else {
                        errorMsg += "添加 ["+ purchaseOrder.getVoucherNo() +"] 订单到出库单时出错,物料不存在!<br/>";
                    }
                }
            }
        }

        String successMsg = "已添加" + sum + "项<br/>";
        return successMsg + errorMsg;
    }

    public Result updateDeliveryDetail(DeliveryOrderDetailEO entity) {
        Result result = new Result();
        String msg = "";
        PurchaseOrderEO purchaseOrder = this.purchaseOrderService.getById(entity.getOrderId());

        // 是否需要判断数量
        JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
        jsonObject = jsonObject.getJSONObject("outsideDeliveryOrder");
        boolean isLimitQuantity = jsonObject.getBoolean("isLimitQuantity");

        if(isLimitQuantity) {
            List<DeliveryOrderDetailEO> deliveryOrderDetails = this.deliveryOrderDetailMapper.getByPurchaseOrderId(entity.getOrderId());
            Double sumDeliveryAmount = 0d;
            if (deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
                for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                    if(deliveryOrderDetail.getDeliveryDetailId().longValue() != entity.getDeliveryDetailId().longValue()) {
                        if(deliveryOrderDetail.getStatus()==0 || deliveryOrderDetail.getStatus()==1) {
                            sumDeliveryAmount += deliveryOrderDetail.getDeliveryAmount();
                        }
                        if(deliveryOrderDetail.getStatus() == 2) {
                            sumDeliveryAmount += deliveryOrderDetail.getRelDeliveryAmount();
                        }
                    }
                }
            }

            Double rest = purchaseOrder.getActualDeliveryQuantity() - sumDeliveryAmount;
            if(entity.getStatus()==0 || entity.getStatus()==1) {
                sumDeliveryAmount += entity.getDeliveryAmount();
            }
            if(entity.getStatus() == 2) {
                sumDeliveryAmount += entity.getRelDeliveryAmount();
            }
            if(sumDeliveryAmount > purchaseOrder.getActualDeliveryQuantity()) {
                result.setCode(500);
                msg = "超出订单实际送货数量,可出库的剩余数量为" + rest + "!";
            } else {
                this.deliveryOrderDetailService.updateById(entity);
                msg = "修改成功!";
            }
        } else {
            this.deliveryOrderDetailService.updateById(entity);
            msg = "修改成功!";
        }

        result.setMsg(msg);
        return result;
    }

    public boolean removeByIds(Long[] deliveryOrderIds, Long userId) throws BusinessException {
        if(deliveryOrderIds==null || deliveryOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        // 判断用户是否拥有出库单的归属机构权限
        String alertMsg = "";
        for(Long deliveryOrderId : deliveryOrderIds) {
            DeliveryOrderEO deliveryOrder = super.getById(deliveryOrderId);
            Boolean flag = this.orgService.checkUserPermissions(deliveryOrder.getOrgId(), userId);
            if(!flag.booleanValue()) {
                alertMsg += ("出库单【" + deliveryOrder.getVoucherNo() + "】的归属机构权限不存在该用户权限!<br/>");
            }
        }

        if(!"".equals(alertMsg)) {
            throw new BusinessException(alertMsg);
        }

        for(Long deliveryOrderId : deliveryOrderIds) {
            this.deliveryOrderDetailService.removeByDeliveryOrderId(deliveryOrderId);
        }
        return super.removeByIds(deliveryOrderIds);
    }

    public String releaseByIds(Long[] deliveryOrderIds) throws BusinessException {
        if(deliveryOrderIds==null || deliveryOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        String errorMsg = "";
        int successCount = 0;
        int errorCount = 0;
        for(Long deliveryOrderId : deliveryOrderIds) {
            DeliveryOrderEO deliveryOrder = this.getById(deliveryOrderId);
            if(deliveryOrder.getStatus() == 0) {
                deliveryOrder.setStatus(1);
                super.updateById(deliveryOrder);
                this.deliveryOrderDetailService.updateStatusByDeliveryOrderId(1, deliveryOrderId);
                successCount ++;
            } else {
                errorMsg += ("出库单" + deliveryOrder.getVoucherNo() + "状态已不是新建,无法发布!<br/>");
                errorCount ++;
            }
        }

        String successMsg = "发布成功" + successCount + "条数据!<br/>" + "发布失败" + errorCount + "条数据!<br/>";
        return successMsg + errorMsg;
    }

    public String cancelReleaseByIds(Long[] deliveryOrderIds) {
        if(deliveryOrderIds==null || deliveryOrderIds.length==0) {
            throw new BusinessException("请选择数据!");
        }

        String errorMsg = "";
        int successCount = 0;
        int errorCount = 0;
        for(Long deliveryOrderId : deliveryOrderIds) {
            DeliveryOrderEO deliveryOrder = this.getById(deliveryOrderId);
            List<DeliveryOrderDetailEO> list = this.deliveryOrderDetailService.getByDeliveryOrderId(deliveryOrderId);
            boolean flag = true;
            if(list!=null && list.size()>0) {
                for(DeliveryOrderDetailEO deliveryOrderDetail : list) {
                    if(deliveryOrderDetail.getStatus() == 2) {
                        flag = false;
                        break;
                    }
                }
            }

            if(!flag) {
                errorMsg += ("出库单" + deliveryOrder.getVoucherNo() + "存在已完成的明细,不能取消发布!<br/>");
                errorCount ++;
            }
            if(flag) {
                if(deliveryOrder.getStatus() == 1) {
                    deliveryOrder.setStatus(0);
                    super.updateById(deliveryOrder);
                    this.deliveryOrderDetailService.updateStatusByDeliveryOrderId(0, deliveryOrderId);
                    successCount ++ ;
                }
            }
        }

        String successMsg = "取消发布成功" + successCount + "条数据!<br/>" + "取消发布失败" + errorCount + "条数据!<br/>";
        return successMsg + errorMsg;
    }


    //按库位查找物料库存分页查找
    public IPage<StockAccountEO> checkStockByLocation(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<StockAccountEO> wrapper = new QueryWrapper<StockAccountEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<StockAccountEO> totalList = this.baseMapper.checkStockByLocation(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<StockAccountEO> list = this.baseMapper.checkStockByLocation(param);

        IPage<StockAccountEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;

    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deliveryOneByLocation(Long Id, Double amount, String action,Long userId,String userName,Long locationId,Long stockFinishFlag) throws BusinessException {


        DeliveryOrderDetailEO detailEO = this.deliveryOrderDetailService.getById(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("出库单明细状态非已发布状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 2){
            throw new BusinessException("出库单明细状态非已完成状态，请刷新确认");
        }

        //判断出库明细是否都已完成
        DeliveryOrderEO deliveryOrderEO = this.baseMapper.selectById(detailEO.getDeliveryOrderId());
        Integer type = 12;
        if (deliveryOrderEO.getDeliveryType() == 1) {
            type = 7;
        } else if (deliveryOrderEO.getDeliveryType() == 2) {
            type = 8;
        } else if (deliveryOrderEO.getDeliveryType() == 3) {
            type = 9;
        } else {
            type = 12;
        }

        if("add".equals(action)) {
            if (stockFinishFlag == 1){
                detailEO.setStatus(2);
            }

            Double sumStockCount = this.baseMapper.selectSumStockCount(detailEO.getDeliveryDetailId(),type);

            if(null != sumStockCount){
                detailEO.setRelDeliveryAmount(amount+sumStockCount);
            }else{
                detailEO.setRelDeliveryAmount(amount);
            }

            deliveryOrderEO.setType(deliveryOrderEO.getChildDeliveryType());
            if(locationId==0){
                detailEO.setWarehouseLocationId(null);
            }else{
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                detailEO.setWarehouseId(locationEO.getWarehouseId());
                detailEO.setWarehouseLocationId(locationId);
            }
            this.setStockByLocation(detailEO,deliveryOrderEO,amount);
        }else{
            detailEO.setStatus(1);
            detailEO.setRelDeliveryAmount(0d);
            this.deleteStockByDetailId(detailEO.getDeliveryDetailId(),type);
        }

        this.deliveryOrderDetailService.updateById(detailEO);


        if("add".equals(action) && (deliveryOrderEO.getStatus() == 0 || deliveryOrderEO.getStatus() == 2)){
            throw new BusinessException("出库单状态为新建或已完成状态、不能进行出库操作,请确认!");
        }

        if(!checkPer(deliveryOrderEO.getOrgId(),userId)){
            throw new BusinessException("出库单的归属机构权限不存在该用户权限，请确认！");
        }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(deliveryOrderEO.getDeliveryId());
        if(finishCount > 0){
            deliveryOrderEO.setStatus(1);
            deliveryOrderEO.setDeliveryUserId("");
            deliveryOrderEO.setDeliveryUserName("");
        }else {
            deliveryOrderEO.setStatus(2);
            deliveryOrderEO.setDeliveryUserId(userId+"");
            deliveryOrderEO.setDeliveryUserName(userName);
        }

        this.baseMapper.updateById(deliveryOrderEO);

        return true;
    }

    public boolean setStockByLocation(DeliveryOrderDetailEO deliveryOrderDetailEO, DeliveryOrderEO deliveryOrderEO,Double amount) throws BusinessException {

//        List<StockAccountEO> messages = this.baseMapper.getMessage(deliveryOrderDetailEO.getDeliveryDetailId());
//        for (StockAccountEO message : messages) {
//
//            if (null == message.getCount() || message.getCount() - message.getDeliveryAmount() < 0) {
//                throw new BusinessException("出库失败，物料【" + message.getMaterialName() +
//                        "】在【" + message.getWarehouseName() + "】【" + message.getLocationName() + "】库存不足，出库数量:" +
//                        message.getDeliveryAmount() + "库存数量:" + message.getCount() + ",请确认");
//            }


/*        StockAccountEO stockAccountEO = this.baseMapper.selectStockCount(deliveryOrderDetailEO.getMaterialId());
        if(null == stockAccountEO || null == stockAccountEO.getCount() || stockAccountEO.getCount() < deliveryOrderDetailEO.getRelDeliveryAmount()){
            Double currentAccount = 0d;
            Double realAmount = 0d;
            if(null != stockAccountEO && null != stockAccountEO.getCount()){
                currentAccount = stockAccountEO.getCount();
            }
            //差值
            realAmount = deliveryOrderDetailEO.getRelDeliveryAmount() - currentAccount;
            throw new BusinessException("零件号："+deliveryOrderDetailEO.getElementNo()+"的零件库存不足，当前库存："+currentAccount+",库存差值："+realAmount+" ,请确认！");
        }*/

        StockAccountEO accountEO = new StockAccountEO();

        accountEO.setVoucherId(deliveryOrderDetailEO.getDeliveryDetailId());
        accountEO.setVoucherDate(deliveryOrderEO.getDeliveryDate());

        if (deliveryOrderEO.getDeliveryType() == 1) {
            accountEO.setVoucherType(7);
        } else if (deliveryOrderEO.getDeliveryType() == 2) {
            accountEO.setVoucherType(8);
        } else if (deliveryOrderEO.getDeliveryType() == 3) {
            accountEO.setVoucherType(9);
        } else if (deliveryOrderEO.getDeliveryType() == 4){
            accountEO.setVoucherType(10);
        } else {
            accountEO.setVoucherType(12);
            if(null != deliveryOrderEO.getType()){
                accountEO.setChildVoucherType(deliveryOrderEO.getType());
                if(accountEO.getChildVoucherType() == 4){
                    accountEO.setRemarks("采购退货出库");
                }else if(accountEO.getChildVoucherType() == 5){
                    accountEO.setRemarks("委外退货出库");
                }
            }else {
                accountEO.setRemarks(deliveryOrderDetailEO.getRemarks());
            }
        }

        accountEO.setMaterialId(deliveryOrderDetailEO.getMaterialId());
        accountEO.setMaterialCode(deliveryOrderDetailEO.getMaterialCode());
        accountEO.setMaterialName(deliveryOrderDetailEO.getMaterialName());
        accountEO.setInventoryCode(deliveryOrderDetailEO.getInventoryCode());
        accountEO.setElementNo(deliveryOrderDetailEO.getElementNo());
        accountEO.setSpecification(deliveryOrderDetailEO.getSpecification());
        accountEO.setUnitId(deliveryOrderDetailEO.getUnitId());
        accountEO.setFigureNumber(deliveryOrderDetailEO.getFigureNumber());
        accountEO.setFigureVersion(deliveryOrderDetailEO.getFigureVersion());
        accountEO.setWarehouseId(deliveryOrderDetailEO.getWarehouseId());
        accountEO.setWarehouseLocationId(deliveryOrderDetailEO.getWarehouseLocationId());
        accountEO.setAmount(amount);


        return this.stockAccountService.save(accountEO);
    }

    public void getSumDeliveryAmount(List<DeliveryOrderEO> list) {
        String sqlStr = "(";
        if(list!=null && list.size()>0) {
            for(DeliveryOrderEO deliveryOrder : list) {
                sqlStr += (deliveryOrder.getDeliveryId() + ",");
            }
        }
        if(sqlStr.equals("(")) {
            sqlStr += "'々∑')";
        } else {
            sqlStr = (sqlStr.substring(0, sqlStr.length()-1) + ")");
        }
        List<DeliveryOrderDetailEO> deliveryOrderDetails =  this.deliveryOrderDetailMapper.getSumDeliveryAmountByDeliveryOrderIds(sqlStr);
        if(deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
            for(DeliveryOrderEO deliveryOrder : list) {
                for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                    if(deliveryOrder.getDeliveryId().longValue() == deliveryOrderDetail.getDeliveryOrderId().longValue()) {
                        deliveryOrder.setSumDeliveryAmount(deliveryOrderDetail.getDeliveryAmount()==null?Double.valueOf(0):deliveryOrderDetail.getDeliveryAmount());
                    }
                }
            }
        } else {
            for(DeliveryOrderEO deliveryOrder : list) {
                deliveryOrder.setSumDeliveryAmount(Double.valueOf(0));
            }
        }
    }


    public boolean changeMaterial(Long Id,Long materialId,UserEO userEO) throws BusinessException {
        DeliveryOrderDetailEO detailEO = this.deliveryOrderDetailService.getById(Id);
        DeliveryOrderEO orderEO = this.baseMapper.selectById(detailEO.getDeliveryOrderId());

        if(orderEO.getOrgId().longValue() != userEO.getOrgId().longValue()){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        if(detailEO.getStatus() == 2){
            throw new BusinessException("该明细数据已完成，不可变更物料数据，请确认！");
        }

        MaterialEO materialEO =this.materialService.getById(materialId);
        detailEO.setMaterialId(materialEO.getMaterialId());
        detailEO.setElementNo(materialEO.getElementNo());
        detailEO.setMaterialCode(materialEO.getMaterialCode());
        detailEO.setMaterialName(materialEO.getMaterialName());
        detailEO.setInventoryCode(materialEO.getInventoryCode());
        detailEO.setWarehouseId(materialEO.getMainWarehouseId());
        detailEO.setUnitId(materialEO.getFirstMeasurementUnit());
        detailEO.setRemarks("");
        detailEO.setIsMatch(1);

        this.deliveryOrderDetailMapper.updateById(detailEO);

        return true;

    }

    @Transactional(rollbackFor = Exception.class)
    public Result syncU8MaterialOutStock(Long[] receiveOrderIds, UserEO user) throws BusinessException {
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
        Map mesMap = new HashedMap();
        Map u8NewMap = new HashedMap();
        String errorMsg = "";
        List<DeliveryOrderEO> newDeliveryOrders = new ArrayList<>();
        List<DeliveryOrderEO> deliveryOrders = this.baseMapper.getU8ListByReceiveOrderIds(receiveOrderIds);
        if(deliveryOrders!=null && deliveryOrders.size()>0) {
            for(DeliveryOrderEO deliveryOrder : deliveryOrders) {
                // 判断出库单状态(只有完成状态提交同步)
                if(deliveryOrder.getStatus().intValue() != 2) {
                    errorMsg += (deliveryOrder.getVoucherNo() + "的状态不是已完成!\n");
                }

                // 判断出库单明细的仓库id是否有值
                List<DeliveryOrderDetailEO> details = deliveryOrder.getDetails();
                if(details!=null && details.size()>0) {
                    for(DeliveryOrderDetailEO deliveryOrderDetail : details) {
                        if(deliveryOrderDetail.getWarehouseId() == null) {
                            errorMsg += (deliveryOrder.getVoucherNo() + "中的" + deliveryOrderDetail.getElementNo() + "没有设置仓库!" + "\n");
                        }
                    }
                }

                // 判断出库单同步状态(只有状态是未同步,部分成功或全部失败的才能提交同步)
                if(deliveryOrder.getSyncStatus() == null) {
                    deliveryOrder.setSyncStatus(0);
                }
                if(deliveryOrder.getSyncStatus().intValue()!=0 &&
                        deliveryOrder.getSyncStatus().intValue()!=3 &&
                        deliveryOrder.getSyncStatus().intValue()!=4) {
                    continue;
                } else {
                    newDeliveryOrders.add(deliveryOrder);
                }
            }

            if(!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            // 解析mes系统的出库单及明细数据(根据出库单ID及明细仓库ID分条目，且每个存货编码只能出现1次)
            if(newDeliveryOrders!=null && newDeliveryOrders.size()>0) {
                String u8Str = "";
                for(DeliveryOrderEO deliveryOrder : newDeliveryOrders) {
                    u8Str += (deliveryOrder.getDeliveryId() + ",");
                    List<DeliveryOrderDetailEO> details = deliveryOrder.getDetails();
                    if(details!=null && details.size()>0) {
                        for(DeliveryOrderDetailEO deliveryOrderDetail : details) {
                            String key = deliveryOrder.getDeliveryId() + "-" + deliveryOrderDetail.getWarehouseId();
                            if(mesMap.get(key) != null) {
                                Map map = (Map) mesMap.get(key);
                                JSONObject jsonObject = (JSONObject) map.get("Data");
                                JSONArray jsonArray = (JSONArray) jsonObject.get("details");
                                if(jsonArray != null) {
                                    boolean flag = false; // details节点是否包含存货编码，false表示不包含，true表示包含
                                    for(Object o : jsonArray) {
                                        JSONObject jo = (JSONObject) o;
                                        if(jo.get("productcode")!=null && jo.get("productcode").equals(deliveryOrderDetail.getInventoryCode())) {
                                            flag = true;
                                            Double d = (Double) jo.get("productcount");
                                            if(d == null) {
                                                d = Double.valueOf(0);
                                            }

                                            jo.put("productcount", d + deliveryOrderDetail.getRelDeliveryAmount());
                                        }
                                    }

                                    if(!flag) {
                                        JSONObject ic = new JSONObject();
                                        ic.put("productcode", deliveryOrderDetail.getInventoryCode());
                                        ic.put("productcount", deliveryOrderDetail.getRelDeliveryAmount());
                                        ic.put("batchno", "");
                                        ic.put("price", 0);
                                        jsonArray.add(ic);
                                    }
                                }

                                mesMap.put(key, map);
                            } else {
                                JSONObject property = (JSONObject) object.get("materialoutstock");
                                JSONObject orgProperty = (JSONObject) property.get(deliveryOrder.getOrgCode().toLowerCase());

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("date", DateUtils.format(deliveryOrder.getDeliveryDate(), "yyyy-MM-dd"));
                                jsonObject.put("whcode", deliveryOrderDetail.getErpCode());
                                String inventoryCode = deliveryOrderDetail.getInventoryCode();
                                if(inventoryCode == null) {
                                    jsonObject.put("rdcode", "20202");
                                } else {
                                    if(inventoryCode.startsWith("01")) {
                                        jsonObject.put("rdcode", "20202");
                                    } else if(inventoryCode.startsWith("02") || inventoryCode.startsWith("03")) {
                                        jsonObject.put("rdcode", "20102");
                                    } else {
                                        jsonObject.put("rdcode", "20202");
                                    }
                                }
                                jsonObject.put("stockno", "");
                                jsonObject.put("orderno", "");
                                jsonObject.put("depcode", orgProperty.get("depcode"));// 随部门变化的，需要配置文件
                                jsonObject.put("maker", user.getRealName());

                                JSONArray jsonArray = new JSONArray();
                                JSONObject ic = new JSONObject();
                                ic.put("productcount", deliveryOrderDetail.getRelDeliveryAmount());
                                ic.put("productcode", deliveryOrderDetail.getInventoryCode());
                                ic.put("price", 0);
                                ic.put("batchno", "");
                                jsonArray.add(ic);
                                jsonObject.put("details", jsonArray);

                                Map map = new HashedMap();
                                map.put("Type", "materialoutstock");
                                map.put("RootOrgID", deliveryOrder.getOrgId());
                                map.put("VoucherID", deliveryOrder.getDeliveryId());
                                map.put("VoucherTableName", "wms_delivery_order");
                                map.put("WarehouseID", deliveryOrderDetail.getWarehouseId());
                                map.put("OrderNumber", deliveryOrder.getVoucherNo());
                                map.put("VoucherDate", DateUtils.format(deliveryOrder.getDeliveryDate(), "yyyy-MM-dd"));
                                map.put("Data", jsonObject);

                                mesMap.put(key, map);
                            }
                        }
                    }
                }

                // 查询U8_API_Message中的出库单表对应的所有数据
                if("".equals(u8Str)) {
                    u8Str = "(-1)";
                } else {
                    u8Str = "(" + u8Str.substring(0, u8Str.length()-1) + ")";
                }
                String selectSql = "select * from U8_API_Message where VoucherTableName = 'wms_delivery_order' and Type='materialoutstock' and VoucherID in" + u8Str;
                System.out.println(selectSql);
                List<Map<String,Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                if(u8List!=null && u8List.size()>0) {
                    int u8Count;
                    for(Map map : u8List) {
                        Short Status = (Short) map.get("Status");
                        Long WarehouseID = (Long) map.get("WarehouseID");
                        Long VoucherID = (Long) map.get("VoucherID");
                        u8Map.put(VoucherID + "-" + WarehouseID, Status);

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

        Map mesCountMap = new HashedMap();
        int mesCount;
        Map commitCountMap = new HashedMap();
        int commitCount;
        Map<Long, String> doMap = new HashedMap();
        for(Object o : mesMap.keySet()) {
            // 统计每条数据拆分得到的数量
            Long deliveryOrderId = Long.valueOf(o.toString().split("-")[0]);
            if(mesCountMap.get(""+deliveryOrderId) == null) {
                mesCount = 1;
            } else {
                mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
            }
            mesCountMap.put(""+deliveryOrderId, mesCount);

            if(!u8Map.containsKey(o)) {
                if(commitCountMap.get(""+deliveryOrderId) == null) {
                    commitCount = 1;
                } else {
                    commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                }
                commitCountMap.put(""+deliveryOrderId, commitCount);
            }

            doMap.put(deliveryOrderId,  "共拆分为" + mesCountMap.get(""+deliveryOrderId) + "条单据," +
                    "之前已成功" + (u8CountMap.get(""+deliveryOrderId)==null?0:u8CountMap.get(""+deliveryOrderId)) + "条," +
                    "本次提交" + (commitCountMap.get(""+deliveryOrderId)==null?0:commitCountMap.get(""+deliveryOrderId)) + "条  ");
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
                Long deliveryOrderId = Long.valueOf(o.toString().split("-")[0]);
                String newMsg = "";
                if(mesMap.keySet().contains(o) && u8NewMap.keySet().contains(o)) { // U8与MES都存在
                    // 统计每条数据拆分得到的数量
                    if(mesCountMap.get(""+deliveryOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
                    }
                    mesCountMap.put(""+deliveryOrderId, mesCount);

                    if(commitCountMap.get(""+deliveryOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                    }
                    commitCountMap.put(""+deliveryOrderId, commitCount);

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
                    if(mesCountMap.get(""+deliveryOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
                    }
                    mesCountMap.put(""+deliveryOrderId, mesCount);

                    if(commitCountMap.get(""+deliveryOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                    }
                    commitCountMap.put(""+deliveryOrderId, commitCount);

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

                doMap.put(deliveryOrderId, (doMap.get(deliveryOrderId)==null?"":doMap.get(deliveryOrderId)) + newMsg);
            }
        }

        if(!string.equals("")) {
            int ec = SqlActuator.excute(string, u8DBConnectInfo);
            if(ec <= 0) {
                throw new BusinessException("同步失败,未执行到U8!");
            }
        }

        if(doMap!=null && doMap.size()>0) {
            int sc = this.baseMapper.syncU8Update(doMap);
            if(sc <= 0) {
                throw new BusinessException("同步失败,未更新同步信息!");
            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result synU8SaleOutStock(Long[] deliveryOrderIds, UserEO user) {
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
        List<DeliveryOrderEO> deliveryOrders = this.baseMapper.getU8ListByDeliveryOrderIds(deliveryOrderIds);
        if(deliveryOrders!=null && deliveryOrders.size()>0) {
            for(DeliveryOrderEO deliveryOrder : deliveryOrders) {
                // 判断出库单状态(只有完成状态提交同步)
                if(deliveryOrder.getStatus().intValue() != 2) {
                    errorMsg += (deliveryOrder.getVoucherNo() + "的状态不是已完成!\n");
                }

                // 判断出库单同步状态(只有状态是未同步,部分成功或全部失败的才能提交同步)
                if(deliveryOrder.getSyncPdStatus() == null) {
                    deliveryOrder.setSyncPdStatus(0);
                }
                if(deliveryOrder.getSyncPdStatus().intValue()!=0 &&
                        deliveryOrder.getSyncPdStatus().intValue()!=3 &&
                        deliveryOrder.getSyncPdStatus().intValue()!=4) {
                    errorMsg += (deliveryOrder.getVoucherNo() + "的同步状态不是未同步,部分成功,全部失败三者中的一个!\n");
                }

                // 判断出库单明细的仓库id是否有值
                List<DeliveryOrderDetailEO> details = deliveryOrder.getDetails();
                if(details!=null && details.size()>0) {
                    for(DeliveryOrderDetailEO deliveryOrderDetail : details) {
                        if(deliveryOrderDetail.getWarehouseId() == null) {
                            errorMsg += (deliveryOrder.getVoucherNo() + "中的" + deliveryOrderDetail.getElementNo() + "没有设置仓库!" + "\n");
                        }
                    }
                }
            }

            if(!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            // 解析mes系统的出库单及明细数据(根据出库单ID及明细仓库ID分条目，且每个存货编码只能出现1次)
            if(deliveryOrders!=null && deliveryOrders.size()>0) {
                String u8Str = "";
                for(DeliveryOrderEO deliveryOrder : deliveryOrders) {
                    u8Str += (deliveryOrder.getDeliveryId() + ",");
                    List<DeliveryOrderDetailEO> details = deliveryOrder.getDetails();
                    if(details!=null && details.size()>0) {
                        for(DeliveryOrderDetailEO deliveryOrderDetail : details) {
                            String key = deliveryOrder.getDeliveryId() + "-" + deliveryOrderDetail.getWarehouseId();
                            if(mesMap.get(key) != null) {
                                Map map = (Map) mesMap.get(key);
                                JSONObject jsonObject = (JSONObject) map.get("Data");
                                JSONArray jsonArray = (JSONArray) jsonObject.get("details");
                                if(jsonArray != null) {
                                    boolean flag = false; // details节点是否包含存货编码，false表示不包含，true表示包含
                                    for(Object o : jsonArray) {
                                        JSONObject jo = (JSONObject) o;
                                        if(jo.get("productcode")!=null && jo.get("productcode").equals(deliveryOrderDetail.getInventoryCode())) {
                                            flag = true;
                                            Double d = (Double) jo.get("productcount");
                                            if(d == null) {
                                                d = Double.valueOf(0);
                                            }

                                            jo.put("productcount", d + deliveryOrderDetail.getRelDeliveryAmount());
                                        }
                                    }

                                    if(!flag) {
                                        JSONObject ic = new JSONObject();
                                        ic.put("productcode", deliveryOrderDetail.getInventoryCode());
                                        ic.put("productcount", deliveryOrderDetail.getRelDeliveryAmount());
                                        ic.put("batchno", "");
                                        ic.put("price", 0);
                                        jsonArray.add(ic);
                                    }
                                }

                                mesMap.put(key, map);
                            } else {
                                JSONObject property = (JSONObject) object.get("saleoutstock");
                                JSONObject orgProperty = (JSONObject) property.get(deliveryOrder.getOrgCode().toLowerCase());

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("date", DateUtils.format(deliveryOrder.getDeliveryDate(), "yyyy-MM-dd"));
                                jsonObject.put("whcode", deliveryOrderDetail.getErpCode());
                                jsonObject.put("rdcode", orgProperty.get("rdcode")); // 随变，需要配置文件
                                jsonObject.put("stcode", orgProperty.get("stcode"));  // 随变，需要配置文件
                                jsonObject.put("orderno", "");
                                jsonObject.put("stockno", "");
                                jsonObject.put("taxrate", orgProperty.get("taxrate")); // 随变，需要配置文件
                                jsonObject.put("maker", user.getRealName());
                                jsonObject.put("depcode", orgProperty.get("depcode"));// 随变化，需要配置文件
                                jsonObject.put("customercode", deliveryOrder.getDestinationCode());

                                JSONArray jsonArray = new JSONArray();
                                JSONObject ic = new JSONObject();
                                ic.put("productcount", deliveryOrderDetail.getRelDeliveryAmount());
                                ic.put("productcode", deliveryOrderDetail.getInventoryCode());
                                ic.put("price", 0);
                                ic.put("batchno", "");
                                jsonArray.add(ic);
                                jsonObject.put("details", jsonArray);

                                Map map = new HashedMap();
                                map.put("Type", "saleoutstock");
                                map.put("RootOrgID", deliveryOrder.getOrgId());
                                map.put("VoucherID", deliveryOrder.getDeliveryId());
                                map.put("VoucherTableName", "wms_delivery_order");
                                map.put("WarehouseID", deliveryOrderDetail.getWarehouseId());
                                map.put("OrderNumber", deliveryOrder.getVoucherNo());
                                map.put("VoucherDate", DateUtils.format(deliveryOrder.getDeliveryDate(), "yyyy-MM-dd"));
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
                    String selectSql = "select * from U8_API_Message where VoucherTableName = 'wms_delivery_order' and Type='saleoutstock' and VoucherID in" + u8Str;
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
        Map<Long, String> doMap = new HashedMap();
        for(Object o : mesMap.keySet()) {
            // 统计每条数据拆分得到的数量
            Long deliveryOrderId = Long.valueOf(o.toString().split("-")[0]);
            if(mesCountMap.get(""+deliveryOrderId) == null) {
                mesCount = 1;
            } else {
                mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
            }
            mesCountMap.put(""+deliveryOrderId, mesCount);

            if(!u8Map.containsKey(o)) {
                if(commitCountMap.get(""+deliveryOrderId) == null) {
                    commitCount = 1;
                } else {
                    commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                }
                commitCountMap.put(""+deliveryOrderId, commitCount);
            }

            doMap.put(deliveryOrderId,  "共拆分为" + mesCountMap.get(""+deliveryOrderId) + "条单据," +
                    "之前已成功" + (u8CountMap.get(""+deliveryOrderId)==null?0:u8CountMap.get(""+deliveryOrderId)) + "条," +
                    "本次提交" + (commitCountMap.get(""+deliveryOrderId)==null?0:commitCountMap.get(""+deliveryOrderId)) + "条  ");
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
                Long deliveryOrderId = Long.valueOf(o.toString().split("-")[0]);
                String newMsg = "";
                if(mesMap.keySet().contains(o) && u8NewMap.keySet().contains(o)) { // U8与MES都存在
                    // 统计每条数据拆分得到的数量
                    if(mesCountMap.get(""+deliveryOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
                    }
                    mesCountMap.put(""+deliveryOrderId, mesCount);

                    if(commitCountMap.get(""+deliveryOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                    }
                    commitCountMap.put(""+deliveryOrderId, commitCount);

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
                    if(mesCountMap.get(""+deliveryOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+deliveryOrderId) + 1;
                    }
                    mesCountMap.put(""+deliveryOrderId, mesCount);

                    if(commitCountMap.get(""+deliveryOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+deliveryOrderId) + 1;
                    }
                    commitCountMap.put(""+deliveryOrderId, commitCount);

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

                doMap.put(deliveryOrderId, (doMap.get(deliveryOrderId)==null?"":doMap.get(deliveryOrderId)) + newMsg);
            }
        }

        if(!string.equals("")) {
            int ec = SqlActuator.excute(string, u8DBConnectInfo);
            if(ec <= 0) {
                throw new BusinessException("同步失败,未执行到U8!");
            }
        }

        if(doMap!=null && doMap.size()>0) {
            int sc = this.baseMapper.syncU8PdUpdate(doMap);
            if(sc <= 0) {
                throw new BusinessException("同步失败,未更新同步信息!");
            }
        }

        return result;
    }
}
