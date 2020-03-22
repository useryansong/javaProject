package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.MaterialMapper;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.scm.wms.entity.ProductReturnDetailEO;
import com.xchinfo.erp.scm.wms.entity.ProductReturnEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.ProductReturnDetailMapper;
import com.xchinfo.erp.wms.mapper.ProductReturnMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yuanchang
 * @date 2019/5/17
 * @update
 */
@Service
public class ProductReturnService extends BaseServiceImpl<ProductReturnMapper, ProductReturnEO> {


    @Autowired
    private ProductReturnDetailMapper productReturnDetailMapper ;

    @Autowired
    private ProductReturnDetailService productReturnDetailService ;

    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private ReceiveOrderService receiveOrderService;

    @Autowired
    private ReceiveOrderDetailService receiveOrderDetailService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private OrgService orgService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private MaterialMapper materialMapper;

    static Set<Long> procedureLock =new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */

    @Override
    public IPage<ProductReturnEO> selectPage(Criteria criteria) {
        IPage<ProductReturnEO> orders = super.selectPage(criteria);

        for (ProductReturnEO order : orders.getRecords()){
            List<ProductReturnDetailEO> details = this.productReturnDetailMapper.selectByReceiveOrder(order.getReturnId());
            order.setDetails(details);
        }

        return orders;
    }


    @Transactional(rollbackFor = Exception.class)
    public ProductReturnEO saveEntity(ProductReturnEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_product_return", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);
        // 先保存订单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存入库单失败！");


        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) throws BusinessException {
        // 删除入库单明细
        Integer result = 0;

        result = this.productReturnDetailMapper.removeByReceiveOrder((Long) id);
        if (null == result || result < 0){
            throw new BusinessException("删除入库单失败！");
        }

        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        Integer result = 0;
        // 删除入库单明细
        for (Serializable id : idList){
            ProductReturnEO productReturnEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(productReturnEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            result = this.productReturnDetailMapper.removeByReceiveOrder((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除入库单失败！");
            }
        }

        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ProductReturnEO entity) throws BusinessException {


        return super.updateById(entity);
    }

    @Override
    public ProductReturnEO getById(Serializable id) throws BusinessException{
        ProductReturnEO order = this.baseMapper.selectById(id);

        List<ProductReturnDetailEO> details = this.productReturnDetailMapper.selectByReceiveOrder((Long) id);

        order.setDetails(details);

        return order;
    }


    public List<ProductReturnDetailEO> listDetailsByOrder(Long orderId) {
        return this.productReturnDetailMapper.selectByReceiveOrder(orderId);
    }


    public IPage<ProductReturnDetailEO> selectDetailPage(Criteria criteria) {

        return this.productReturnDetailService.selectPage(criteria);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDetailIds(Serializable[] idList) throws BusinessException{

        return this.productReturnDetailService.removeByIds(idList);
    }


    public ProductReturnDetailEO getDetailById(Long Id) {
        return this.productReturnDetailMapper.selectById(Id);
    }


    public boolean saveDetail(ProductReturnDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountById(entity.getReturnId(),entity.getMaterialId());
        if (count > 0){
            throw new BusinessException("该退货单相关物料明细已存在，请确认！");
        }

        return this.productReturnDetailService.save(entity);
    }


    public boolean updateDetailById(ProductReturnDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountUpdateById(entity.getReturnId(),entity.getMaterialId(),entity.getReturnDetailId());
        if(count > 0){
            throw new BusinessException("修改后的退货单相关物料明细已存在，请确认！");
        }
        return this.productReturnDetailService.updateById(entity);
    }


    public boolean updateStatusById(Long[] idList, Integer status,Integer oldStatus) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        for(Long id:idList) {

            ProductReturnEO productReturnEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(productReturnEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }
            /*if (status == 1) {
                int count = this.baseMapper.selectDetailCountById(id);
                if (count == 0) {
                    return true;
                }

                ProductReturnEO receiveOrderEO = this.baseMapper.selectById(id);
                List<ProductReturnDetailEO> receiveOrderDetailEOS = this.baseMapper.selectDetailById(id);
                for (ProductReturnDetailEO receiveOrderDetailEO : receiveOrderDetailEOS) {

                    StockAccountEO accountEO = new StockAccountEO();

                    accountEO.setVoucherId(receiveOrderDetailEO.getReturnDetailId());
                    accountEO.setVoucherDate(receiveOrderEO.getReturnDate());
                    accountEO.setVoucherType(6);
                    accountEO.setMaterialId(receiveOrderDetailEO.getMaterialId());
                    accountEO.setMaterialCode(receiveOrderDetailEO.getMaterialCode());
                    accountEO.setMaterialName(receiveOrderDetailEO.getMaterialName());
                    accountEO.setInventoryCode(receiveOrderDetailEO.getInventoryCode());
                    accountEO.setElementNo(receiveOrderDetailEO.getElementNo());
                    accountEO.setSpecification(receiveOrderDetailEO.getSpecification());
                    accountEO.setUnitId(receiveOrderDetailEO.getUnitId());
                    accountEO.setFigureNumber(receiveOrderDetailEO.getFigureNumber());
                    accountEO.setFigureVersion(receiveOrderDetailEO.getFigureVersion());
                    accountEO.setWarehouseId(receiveOrderDetailEO.getWarehouseId());
                    accountEO.setWarehouseLocationId(receiveOrderDetailEO.getWarehouseLocationId());
                    accountEO.setAmount(receiveOrderDetailEO.getReturnAmount());
                    accountEO.setRemarks(receiveOrderDetailEO.getRemarks());
                    //保存台账
                    this.stockAccountService.save(accountEO);

                    }
            } else if (status == 0) {
                    //删除台账
                    this.baseMapper.deleteStockById(id);
            }*/

            if (status==0){

                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }
            }

            //更新状态
            this.baseMapper.updateStatusById(id, status, oldStatus);
        }
        return true;
    }

    public ProductReturnEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        //List<ProductReturnDetailEO> productReturnDetailEOS = new ArrayList<>();
        ProductReturnEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);

        if(null == orderEO){
            throw new BusinessException("单据号不存在，请确认！");
        }
//        //校验机构权限
//        if(null != orderEO) {
//            if (!checkPer(orderEO.getOrgId())) {
//                throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
//            }
//        }

        orderEO.setTotalRelReturnQuantity(0d);

        Double totalCount = this.baseMapper.selectTotalCount(orderEO.getReturnId());
        if(null != totalCount){
            orderEO.setTotalReturnQuantity(totalCount);
        }else{
            orderEO.setTotalReturnQuantity(0d);
        }

        List<ProductReturnDetailEO> details = this.baseMapper.getByReturnId(orderEO.getReturnId());

        for(ProductReturnDetailEO detailEO:details){
            //orderEO.setTotalReturnQuantity(orderEO.getTotalReturnQuantity() + detailEO.getReturnAmount());
            if(detailEO.getStatus() == 2){
                orderEO.setTotalRelReturnQuantity(orderEO.getTotalRelReturnQuantity() + detailEO.getRelReturnAmount());
            }
        }
        orderEO.setDetails(details);
        return orderEO;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean returnOne(Long Id, Double amount, String action, Long userId, String userName) throws BusinessException {
        try {
            if(procedureLock.contains(Id)){

                throw  new BusinessException("当前数据正在操作中，无法操作请刷新后重试");
            }else{
                procedureLock.add(Id);
                UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
                ProductReturnDetailEO detailEO = this.productReturnDetailService.getById(Id);
                //判断状态
                if("add".equals(action) && detailEO.getStatus() != 1){
                    throw new BusinessException("退货单明细状态非已发布状态，请刷新确认");
                }else if("remove".equals(action) && detailEO.getStatus() != 2){
                    throw new BusinessException("退货明细状态非已完成状态，请刷新确认");
                }
                if("add".equals(action)) {
                    detailEO.setStatus(2);
                    detailEO.setRelReturnAmount(amount);
                }else{
                    detailEO.setStatus(1);
                    detailEO.setRelReturnAmount(0d);
                }

                this.productReturnDetailService.updateById(detailEO);

                //判断退货库明细是否都已完成
                ProductReturnEO productReturnEO = this.baseMapper.selectById(detailEO.getReturnId());

                //判断入库单是否存在
                ReceiveOrderEO receiveOrderEO = this.baseMapper.selectReceiveOrderCount(productReturnEO.getReturnId());
                if(null == receiveOrderEO){
                    ReceiveOrderEO receiveOrderTemp = new ReceiveOrderEO();
                    receiveOrderTemp.setDeliveryNoteId(productReturnEO.getReturnId());
                    receiveOrderTemp.setReceiveDate(productReturnEO.getReturnDate());
                    receiveOrderTemp.setOrgId(productReturnEO.getOrgId());
                    receiveOrderTemp.setStatus(1);
                    receiveOrderTemp.setReceiveType(6);
                    receiveOrderTemp.setChildReceiveType(3);
                    // 生成业务编码
                    String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", receiveOrderTemp,user.getOrgId());
                    AssertUtils.isBlank(code);
                    receiveOrderTemp.setVoucherNo(code);
                    this.receiveOrderService.save(receiveOrderTemp);

                    receiveOrderEO = receiveOrderTemp;
                }

                //判断入库单明细是否存在
                ReceiveOrderDetailEO receiveOrderDetailEO = this.baseMapper.selectReceiveOrderDetailCount(detailEO.getReturnDetailId(),receiveOrderEO.getReceiveId());
                if (null == receiveOrderDetailEO){
                    ReceiveOrderDetailEO receiveOrderDetailEOTemp = new ReceiveOrderDetailEO();
                    receiveOrderDetailEOTemp.setReceiveOrderId(receiveOrderEO.getReceiveId());
                    receiveOrderDetailEOTemp.setOrderId(detailEO.getReturnDetailId());
                    receiveOrderDetailEOTemp.setMaterialId(detailEO.getMaterialId());
                    receiveOrderDetailEOTemp.setMaterialCode(detailEO.getMaterialCode());
                    receiveOrderDetailEOTemp.setMaterialName(detailEO.getMaterialName());
                    receiveOrderDetailEOTemp.setInventoryCode(detailEO.getInventoryCode());

                    receiveOrderDetailEOTemp.setFigureNumber(detailEO.getFigureNumber());
                    receiveOrderDetailEOTemp.setUnitId(detailEO.getUnitId());
                    receiveOrderDetailEOTemp.setElementNo(detailEO.getElementNo());
                    receiveOrderDetailEOTemp.setFigureVersion(detailEO.getFigureVersion());

                    //receiveOrderDetailEOTemp.setWarehouseId(detailEO.getWarehouseId());

                    MaterialEO materialEO = this.materialMapper.selectById(detailEO.getMaterialId());
                    if(null != materialEO.getWarehouseLocationId()){
                        WarehouseLocationEO locationEO = this.warehouseLocationService.getById(materialEO.getWarehouseLocationId());
                        receiveOrderDetailEOTemp.setWarehouseId(locationEO.getWarehouseId());
                    }else{
                        receiveOrderDetailEOTemp.setWarehouseId(materialEO.getMainWarehouseId());
                    }

                    receiveOrderDetailEOTemp.setWarehouseLocationId(detailEO.getWarehouseLocationId());
                    receiveOrderDetailEOTemp.setReceiveAmount(detailEO.getReturnAmount());
                    receiveOrderDetailEOTemp.setRelReceiveAmount(amount);
                    receiveOrderDetailEOTemp.setStatus(2);

                    this.receiveOrderDetailService.save(receiveOrderDetailEOTemp);
                    receiveOrderDetailEO = receiveOrderDetailEOTemp;
                }else{
                    //如果存在，则重新获取物料的默认库位和对应仓库
                    MaterialEO materialEO = this.materialMapper.selectById(receiveOrderDetailEO.getMaterialId());
                    receiveOrderDetailEO.setWarehouseLocationId(materialEO.getWarehouseLocationId());
                    if(null != materialEO.getWarehouseLocationId()){
                        WarehouseLocationEO locationEO = this.warehouseLocationService.getById(materialEO.getWarehouseLocationId());
                        receiveOrderDetailEO.setWarehouseId(locationEO.getWarehouseId());
                    }else{
                        receiveOrderDetailEO.setWarehouseId(materialEO.getMainWarehouseId());
                    }
                }

                //成品退货入库类型
                receiveOrderEO.setType(3);

                //已存在则更新状态
                if("add".equals(action)) {
                    receiveOrderDetailEO.setRelReceiveAmount(amount);
                    receiveOrderDetailEO.setStatus(2);

                    //新增库存
                    this.receiveOrderService.setStock(receiveOrderDetailEO,receiveOrderEO);
                }else{
                    receiveOrderDetailEO.setStatus(1);
                    receiveOrderDetailEO.setRelReceiveAmount(0d);

                    //删除库存
                    this.receiveOrderService.deleteStockByDetailId(receiveOrderDetailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
                }

                this.receiveOrderDetailService.updateById(receiveOrderDetailEO);

                //
                Integer finishReceiveOrderDetailCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
                if(finishReceiveOrderDetailCount > 0){
                    receiveOrderEO.setReceiveUserId("");
                    receiveOrderEO.setStatus(1);
                    receiveOrderEO.setReceiveUserName("");
                }else {
                    receiveOrderEO.setReceiveUserId(userId+"");
                    receiveOrderEO.setReceiveUserName(userName);
                    receiveOrderEO.setStatus(2);
                }
                this.receiveOrderService.updateById(receiveOrderEO);




                if("add".equals(action) && (productReturnEO.getStatus() == 0 || productReturnEO.getStatus() == 2)){
                    throw new BusinessException("退货单状态为新建或已完成状态、不能进行退货操作,请确认!");
                }

        //      if (!checkPer(productReturnEO.getOrgId())) {
        //                throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
        //      }

                Integer finishCount = this.baseMapper.selectDetailFinishCount(productReturnEO.getReturnId());
                if(finishCount > 0){
                    productReturnEO.setStatus(1);
                    productReturnEO.setReturnUserId("");
                    productReturnEO.setReturnUserName("");
                }else {
                    productReturnEO.setStatus(2);
                    productReturnEO.setReturnUserId(userId+"");
                    productReturnEO.setReturnUserName(userName);
                }
                this.baseMapper.updateById(productReturnEO);
            }
        }catch (Exception e){
            throw e;
        }finally {
            procedureLock.remove(Id);
        }
        return true;
    }


    public boolean deleteDetailById(Long id) throws BusinessException {
        return this.baseMapper.deleteDetailById(id);
    }

    public Boolean checkPer(Long orgId, Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean returnOnelocation(Long Id, Double amount, String action, Long userId, String userName,Long locationId) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        ProductReturnDetailEO detailEO = this.productReturnDetailService.getById(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("退货单明细状态非已发布状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 2){
            throw new BusinessException("退货明细状态非已完成状态，请刷新确认");
        }
        if("add".equals(action)) {
            detailEO.setStatus(2);
            detailEO.setRelReturnAmount(amount);
        }else{
            detailEO.setStatus(1);
            detailEO.setRelReturnAmount(0d);
        }

        this.productReturnDetailService.updateById(detailEO);

        //判断退货库明细是否都已完成
        ProductReturnEO productReturnEO = this.baseMapper.selectById(detailEO.getReturnId());

        //判断入库单是否存在
        ReceiveOrderEO receiveOrderEO = this.baseMapper.selectReceiveOrderCount(productReturnEO.getReturnId());
        if(null == receiveOrderEO){
            ReceiveOrderEO receiveOrderTemp = new ReceiveOrderEO();
            receiveOrderTemp.setDeliveryNoteId(productReturnEO.getReturnId());
            receiveOrderTemp.setReceiveDate(productReturnEO.getReturnDate());
            receiveOrderTemp.setOrgId(productReturnEO.getOrgId());
            receiveOrderTemp.setStatus(1);
            receiveOrderTemp.setReceiveType(6);
            receiveOrderTemp.setChildReceiveType(3);
            // 生成业务编码
            String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", receiveOrderTemp,user.getOrgId());
            AssertUtils.isBlank(code);
            receiveOrderTemp.setVoucherNo(code);
            this.receiveOrderService.save(receiveOrderTemp);

            receiveOrderEO = receiveOrderTemp;
        }

        //判断入库单明细是否存在
        ReceiveOrderDetailEO receiveOrderDetailEO = this.baseMapper.selectReceiveOrderDetailCount(detailEO.getReturnDetailId(),receiveOrderEO.getReceiveId());
        if (null == receiveOrderDetailEO){
            ReceiveOrderDetailEO receiveOrderDetailEOTemp = new ReceiveOrderDetailEO();
            receiveOrderDetailEOTemp.setReceiveOrderId(receiveOrderEO.getReceiveId());
            receiveOrderDetailEOTemp.setOrderId(detailEO.getReturnDetailId());
            receiveOrderDetailEOTemp.setMaterialId(detailEO.getMaterialId());
            receiveOrderDetailEOTemp.setMaterialCode(detailEO.getMaterialCode());
            receiveOrderDetailEOTemp.setMaterialName(detailEO.getMaterialName());
            receiveOrderDetailEOTemp.setInventoryCode(detailEO.getInventoryCode());

            receiveOrderDetailEOTemp.setFigureNumber(detailEO.getFigureNumber());
            receiveOrderDetailEOTemp.setUnitId(detailEO.getUnitId());
            receiveOrderDetailEOTemp.setElementNo(detailEO.getElementNo());
            receiveOrderDetailEOTemp.setFigureVersion(detailEO.getFigureVersion());
            receiveOrderDetailEOTemp.setWarehouseId(detailEO.getWarehouseId());
            if (null != locationId){
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                receiveOrderDetailEOTemp.setWarehouseId(locationEO.getWarehouseId());
            }else{
                receiveOrderDetailEOTemp.setWarehouseId(detailEO.getMainWarehouseId());
            }
            receiveOrderDetailEOTemp.setWarehouseLocationId(detailEO.getWarehouseLocationId());
            receiveOrderDetailEOTemp.setReceiveAmount(detailEO.getReturnAmount());
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
                receiveOrderDetailEO.setWarehouseId(detailEO.getMainWarehouseId());
            }
        }

        //成品退货入库类型
        receiveOrderEO.setType(3);

        //已存在则更新状态
        if("add".equals(action)) {
            receiveOrderDetailEO.setRelReceiveAmount(amount);
            receiveOrderDetailEO.setStatus(2);

            //新增库存
            this.receiveOrderService.setStock(receiveOrderDetailEO,receiveOrderEO);
        }else{
            receiveOrderDetailEO.setStatus(1);
            receiveOrderDetailEO.setRelReceiveAmount(0d);

            //删除库存
            this.receiveOrderService.deleteStockByDetailId(receiveOrderDetailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
        }

        this.receiveOrderDetailService.updateById(receiveOrderDetailEO);

        //
        Integer finishReceiveOrderDetailCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
        if(finishReceiveOrderDetailCount > 0){
            receiveOrderEO.setReceiveUserId("");
            receiveOrderEO.setStatus(1);
            receiveOrderEO.setReceiveUserName("");
        }else {
            receiveOrderEO.setReceiveUserId(userId+"");
            receiveOrderEO.setReceiveUserName(userName);
            receiveOrderEO.setStatus(2);
        }
        this.receiveOrderService.updateById(receiveOrderEO);




        if("add".equals(action) && (productReturnEO.getStatus() == 0 || productReturnEO.getStatus() == 2)){
            throw new BusinessException("退货单状态为新建或已完成状态、不能进行退货操作,请确认!");
        }

//      if (!checkPer(productReturnEO.getOrgId())) {
//                throw new BusinessException("退货单的归属机构下的数据该用户没有操作权限，请确认！");
//      }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(productReturnEO.getReturnId());
        if(finishCount > 0){
            productReturnEO.setStatus(1);
            productReturnEO.setReturnUserId("");
            productReturnEO.setReturnUserName("");
        }else {
            productReturnEO.setStatus(2);
            productReturnEO.setReturnUserId(userId+"");
            productReturnEO.setReturnUserName(userName);
        }
        this.baseMapper.updateById(productReturnEO);

        return true;
    }

    public List<WarehouseLocationEO> getWarehouseLocation(Long id) {

        return this.baseMapper.getWarehouseLocation(id);
    }
}
