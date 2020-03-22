package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.service.CustomerService;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryDeliveryOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.SubsidiaryDeliveryOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.SubsidiaryDeliveryOrderMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class SubsidiaryDeliveryOrderService extends BaseServiceImpl<SubsidiaryDeliveryOrderMapper, SubsidiaryDeliveryOrderEO> {
    @Autowired
    private SubsidiaryDeliveryOrderDetailMapper deliveryOrderDetailMapper;

    @Autowired
    private SubsidiaryDeliveryOrderDetailService deliveryOrderDetailService;

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


    public List<SubsidiaryDeliveryOrderEO> listAll() {
        return this.baseMapper.selectAll();
    }


    public List<SubsidiaryDeliveryOrderDetailEO> listDetailsByDelivery(Long orderId) {
        return this.deliveryOrderDetailMapper.selectByDelivery(orderId);
    }

    @Override
    public SubsidiaryDeliveryOrderEO getById(Serializable id) {
        SubsidiaryDeliveryOrderEO deliveryOrder = this.baseMapper.selectById(id);
        // 查找出库单明细
        List<SubsidiaryDeliveryOrderDetailEO> details = this.deliveryOrderDetailMapper.selectByDelivery((Long) id);
        deliveryOrder.setDetails(details);
        return deliveryOrder;
    }



    private SubsidiaryDeliveryOrderEO setByDeliveryType(SubsidiaryDeliveryOrderEO entity) {
        OrgEO org = this.orgService.getById(entity.getDestinationOrgId());
        entity.setDestinationName(org.getOrgName());
        entity.setDestinationCode(org.getOrgCode());

        return entity;
    }


    public SubsidiaryDeliveryOrderEO saveEntity(SubsidiaryDeliveryOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }
        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("wms_subsidiary_delivery_order", entity,user.getOrgId());
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
            for (SubsidiaryDeliveryOrderDetailEO detail : entity.getDetails()){
                detail.setDeliveryOrderId(entity.getDeliveryId());
            }
            this.deliveryOrderDetailService.saveBatch(entity.getDetails());
        }

        return entity;


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean updateById(SubsidiaryDeliveryOrderEO entity) throws BusinessException {
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
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = SubsidiaryDeliveryOrderEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        logger.info("======== DeliveryOrderServiceImpl.removeByIds() ========");
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        for(Serializable id: idList){

            SubsidiaryDeliveryOrderEO subsidiaryDeliveryOrderEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(subsidiaryDeliveryOrderEO.getOrgId(),userId)) {
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            // 删除出库单明细
            this.deliveryOrderDetailService.removeByDelivery((Long) id);
        }
        return super.removeByIds(idList);
    }

    @Override
    public IPage<SubsidiaryDeliveryOrderEO> selectPage(Criteria criteria) {
        IPage<SubsidiaryDeliveryOrderEO> deliveryOrders = super.selectPage(criteria);
        for (SubsidiaryDeliveryOrderEO deliveryOrder : deliveryOrders.getRecords()){
            List<SubsidiaryDeliveryOrderDetailEO> details = this.deliveryOrderDetailMapper.selectByDelivery(deliveryOrder.getDeliveryId());
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
            if (status.intValue() == 1) {

                SubsidiaryDeliveryOrderEO subsidiaryDeliveryOrderEOs = this.baseMapper.selectById(id);
                //校验机构权限
                if(!checkPer(subsidiaryDeliveryOrderEOs.getOrgId(),userId)) {
                    throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
                }

                int count = this.baseMapper.selectDetailCountById(id);
                if(count == 0){
                    //更新状态
                    this.baseMapper.updateStatusById(id,status,oldStatus);
                    continue;
                }

                SubsidiaryDeliveryOrderEO SubsidiaryDeliveryOrderEO = this.baseMapper.selectById(id);
                List<SubsidiaryDeliveryOrderDetailEO> SubsidiaryDeliveryOrderDetailEOS = this.baseMapper.selectDetailById(id);
                for ( SubsidiaryDeliveryOrderDetailEO SubsidiaryDeliveryOrderDetailEO :SubsidiaryDeliveryOrderDetailEOS) {

                    List<StockAccountEO> messages = this.baseMapper.getMessage(SubsidiaryDeliveryOrderDetailEO.getDeliveryDetailId());
                    for (StockAccountEO message : messages) {

                        if (message.getCount() - message.getDeliveryAmount() < 0) {
                            throw new BusinessException("出库失败，物料【" + message.getMaterialName() +
                                    "】在【" + message.getWarehouseName() + "】【" + message.getLocationName() + "】库存不足，出库数量:" +
                                    message.getDeliveryAmount() + "库存数量:" + message.getCount() + ",请确认");
                        }


                    }

                    StockAccountEO accountEO = new StockAccountEO();

                    accountEO.setVoucherId(SubsidiaryDeliveryOrderDetailEO.getDeliveryDetailId());
                    accountEO.setVoucherDate(SubsidiaryDeliveryOrderEO.getDeliveryDate());

                    accountEO.setVoucherType(10);
                    accountEO.setMaterialId(SubsidiaryDeliveryOrderDetailEO.getMaterialId());
                    accountEO.setMaterialCode(SubsidiaryDeliveryOrderDetailEO.getMaterialCode());
                    accountEO.setMaterialName(SubsidiaryDeliveryOrderDetailEO.getMaterialName());
                    accountEO.setInventoryCode(SubsidiaryDeliveryOrderDetailEO.getInventoryCode());
                    accountEO.setElementNo(SubsidiaryDeliveryOrderDetailEO.getElementNo());
                    accountEO.setSpecification(SubsidiaryDeliveryOrderDetailEO.getSpecification());
                    accountEO.setUnitId(SubsidiaryDeliveryOrderDetailEO.getUnitId());
                    accountEO.setFigureNumber(SubsidiaryDeliveryOrderDetailEO.getFigureNumber());
                    accountEO.setFigureVersion(SubsidiaryDeliveryOrderDetailEO.getFigureVersion());
                    accountEO.setWarehouseId(SubsidiaryDeliveryOrderDetailEO.getWarehouseId());
                    accountEO.setWarehouseLocationId(SubsidiaryDeliveryOrderDetailEO.getWarehouseLocationId());
                    accountEO.setAmount(SubsidiaryDeliveryOrderDetailEO.getDeliveryAmount());
                    accountEO.setRemarks(SubsidiaryDeliveryOrderDetailEO.getRemarks());

                    this.stockAccountService.save(accountEO);

                    this.deliveryOrderDetailMapper.updateStatusById(SubsidiaryDeliveryOrderDetailEO.getDeliveryDetailId(),status);
                }

            }
            else if (status.intValue() == 0){
                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }
                //删除台账
                this.baseMapper.deleteStockById(id);
            }
            this.baseMapper.updateStatusById(id, status,oldStatus);
        }

        return true;
    }


    // 判断所有数据是否为指定状态
    private boolean isWantedStatus(Long[] deliveryIds, Integer status){
        if(deliveryIds!=null && deliveryIds.length>0){
            for(int i=1; i<deliveryIds.length; i++){
                SubsidiaryDeliveryOrderEO deliveryOrder = super.getById(deliveryIds[i]);
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



    public SubsidiaryDeliveryOrderEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        // List<SubsidiaryDeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        SubsidiaryDeliveryOrderEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);

//        orderEO.setTotalRelDeliveryQuantity(0d);
//
//        Double totalCount = this.baseMapper.selectTotalCount(orderEO.getDeliveryId());
//        if(null != totalCount){
//            orderEO.setTotalDeliveryQuantity(totalCount);
//        }else{
//            orderEO.setTotalDeliveryQuantity(0d);
//        }
//
//        List<SubsidiaryDeliveryOrderDetailEO> details = this.baseMapper.getByDeliveryId(orderEO.getDeliveryId());
//
//        for(SubsidiaryDeliveryOrderDetailEO detailEO:details){
//           // orderEO.setTotalDeliveryQuantity(orderEO.getTotalDeliveryQuantity() + detailEO.getDeliveryAmount());
//            if(detailEO.getStatus() == 2){
//                orderEO.setTotalRelDeliveryQuantity(orderEO.getTotalRelDeliveryQuantity() + detailEO.getRelDeliveryAmount());
//            }
//        }
//        orderEO.setDetails(details);
        return orderEO;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deliveryOne(Long Id, Double amount, String action,Long userId,String userName) throws BusinessException {
//        SubsidiaryDeliveryOrderEO subsidiaryDeliveryOrderEOs = this.baseMapper.selectById(Id);
//        //校验机构权限
//        if(!checkPer(subsidiaryDeliveryOrderEOs.getOrgId())) {
//            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
//        }
//
//        SubsidiaryDeliveryOrderDetailEO detailEO = this.deliveryOrderDetailService.getById(Id);
//        //判断状态
//        if("add".equals(action) && detailEO.getStatus() != 1){
//            throw new BusinessException("出库单明细状态非已发布状态，请刷新确认");
//        }else if("remove".equals(action) && detailEO.getStatus() != 2){
//            throw new BusinessException("出库单明细状态非已完成状态，请刷新确认");
//        }
//        if("add".equals(action)) {
//            detailEO.setStatus(2);
//            detailEO.setRelDeliveryAmount(amount);
//        }else{
//            detailEO.setStatus(1);
//            detailEO.setRelDeliveryAmount(0d);
//        }
//
//        this.deliveryOrderDetailService.updateById(detailEO);
//
//        //判断出库明细是否都已完成
//        SubsidiaryDeliveryOrderEO SubsidiaryDeliveryOrderEO = this.baseMapper.selectById(detailEO.getDeliveryOrderId());
//        if("add".equals(action) && (SubsidiaryDeliveryOrderEO.getStatus() == 0 || SubsidiaryDeliveryOrderEO.getStatus() == 2)){
//            throw new BusinessException("出库单状态为新建或已完成状态、不能进行出库操作,请确认!");
//        }
//
//        Integer finishCount = this.baseMapper.selectDetailFinishCount(SubsidiaryDeliveryOrderEO.getDeliveryId());
//        if(finishCount > 0){
//            SubsidiaryDeliveryOrderEO.setStatus(1);
//            SubsidiaryDeliveryOrderEO.setDeliveryUserId("");
//            SubsidiaryDeliveryOrderEO.setDeliveryUserName("");
//        }else {
//            SubsidiaryDeliveryOrderEO.setStatus(2);
//            SubsidiaryDeliveryOrderEO.setDeliveryUserId(userId+"");
//            SubsidiaryDeliveryOrderEO.setDeliveryUserName(userName);
//        }
//
//        this.baseMapper.updateById(SubsidiaryDeliveryOrderEO);

        return true;
    }


    public boolean deleteDetailById(Long id) throws BusinessException {
        return this.baseMapper.deleteDetailById(id);
    }


    public List<SubsidiaryDeliveryOrderEO> getList(Map map) {
        return this.baseMapper.getList(map);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }
}
