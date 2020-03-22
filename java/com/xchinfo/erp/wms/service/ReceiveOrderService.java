package com.xchinfo.erp.wms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.service.WarehouseLocationService;
import com.xchinfo.erp.common.U8DBConnectInfo;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.ExcelUtils;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
import com.xchinfo.erp.wms.mapper.ReceiveOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.ReceiveOrderMapper;
import org.apache.commons.collections.map.HashedMap;
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
import org.yecat.mybatis.utils.jdbc.SqlActuator;

import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class ReceiveOrderService extends BaseServiceImpl<ReceiveOrderMapper, ReceiveOrderEO> {

    @Autowired
    private ReceiveOrderDetailMapper receiveOrderDetailMapper;

    @Autowired
    private ReceiveOrderDetailService receiveOrderDetailService;

    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    static Set<Long> procedureLock =new HashSet<>();/** 防止页面发送多条相同的请求导致重复入库，新增一个程序锁 */

    @Autowired
    private U8DBConnectInfo u8DBConnectInfo;

    @Autowired
    private ReceiveWorkOrderService receiveWorkOrderService;


    @Override
    public IPage<ReceiveOrderEO> selectPage(Criteria criteria) {
        IPage<ReceiveOrderEO> orders = super.selectPage(criteria);

        for (ReceiveOrderEO order : orders.getRecords()){
            List<ReceiveOrderDetailEO> details = this.receiveOrderDetailMapper.selectByReceiveOrder(order.getReceiveId());
            order.setDetails(details);
        }

        return orders;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReceiveOrderEO saveEntity(ReceiveOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", entity,user.getOrgId());
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

        result = this.receiveOrderDetailMapper.removeByReceiveOrder((Long) id);
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

            ReceiveOrderEO receiveOrderEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(receiveOrderEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            result = this.receiveOrderDetailMapper.removeByReceiveOrder((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除入库单失败！");
            }
        }

        return super.removeByIds(idList);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ReceiveOrderEO entity,Long userId) throws BusinessException {

//        RpcContext rpcContext = RpcContext.getContext();
//
//        Long userId = Long.valueOf(rpcContext.getAttachment("userId"));
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        return super.updateById(entity);
    }

    @Override
    public ReceiveOrderEO getById(Serializable id) throws BusinessException{
        ReceiveOrderEO order = this.baseMapper.selectById(id);

        List<ReceiveOrderDetailEO> details = this.receiveOrderDetailMapper.selectByReceiveOrder((Long) id);

        order.setDetails(details);

        return order;
    }


    public List<ReceiveOrderDetailEO> listDetailsByOrder(Long orderId) {
        return this.receiveOrderDetailMapper.selectByReceiveOrder(orderId);
    }

    public IPage<ReceiveOrderDetailEO> selectDetailPage(Criteria criteria) {

        return this.receiveOrderDetailService.selectPage(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDetailIds(Serializable[] idList) throws BusinessException{

        return this.receiveOrderDetailService.removeByIds(idList);
    }

    public ReceiveOrderDetailEO getDetailById(Long Id) {
        return this.receiveOrderDetailMapper.selectById(Id);
    }


    public boolean saveDetail(ReceiveOrderDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountById(entity.getReceiveOrderId(),entity.getMaterialId());
        if (count > 0){
            throw new BusinessException("该入库单相关物料明细已存在，请确认！");
        }

        return this.receiveOrderDetailService.save(entity);
    }

    public boolean updateDetailById(ReceiveOrderDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountUpdateById(entity.getReceiveOrderId(),entity.getMaterialId(),entity.getReceiveDetailId());
        if(count > 0){
            throw new BusinessException("修改后的入库单相关物料明细已存在，请确认！");
        }
        return this.receiveOrderDetailService.updateById(entity);
    }

    public boolean updateStatusById(Long[] idList, Integer status,Integer oldStatus) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        for(Long id:idList){

            ReceiveOrderEO receiveOrderEOs = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(receiveOrderEOs.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            if(status==1){
                int count = this.baseMapper.selectDetailCountById(id);
                if(count == 0){
                    //更新状态
                    this.baseMapper.updateStatusById(id,status,oldStatus);
                    continue;
                }

                   /* ReceiveOrderEO receiveOrderEO =this.baseMapper.selectById(id);
                    List<ReceiveOrderDetailEO> receiveOrderDetailEOS = this.baseMapper.selectDetailById(id);
                    for ( ReceiveOrderDetailEO receiveOrderDetailEO :receiveOrderDetailEOS) {

                        StockAccountEO accountEO = new StockAccountEO();

                        accountEO.setVoucherId(receiveOrderDetailEO.getReceiveDetailId());
                        accountEO.setVoucherDate(receiveOrderEO.getReceiveDate());
                        accountEO.setVoucherType(receiveOrderEO.getReceiveType());
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
                        accountEO.setAmount(receiveOrderDetailEO.getReceiveAmount());
                        accountEO.setRemarks(receiveOrderDetailEO.getRemarks());
                        //保存台账
                        this.stockAccountService.save(accountEO);
                    }*/

            }
            else if (status==0){

                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }
                /*//删除台账
                this.baseMapper.deleteStockById(id);*/
            }
            //更新状态
            this.baseMapper.updateStatusById(id,status,oldStatus);
        }

        return true;
    }

    public ReceiveOrderEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        // List<DeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        ReceiveOrderEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        //校验机构权限
        if(null != orderEO) {
            if (!checkPer(orderEO.getOrgId(),userId)) {
                throw new BusinessException("入库单的归属机构下的数据该用户没有操作权限，请确认！");
            }
        }

        orderEO.setTotalRelReceiveQuantity(0d);

        Double totalCount = this.baseMapper.selectTotalCount(orderEO.getReceiveId());
        if(null != totalCount){
            orderEO.setTotalReceiveQuantity(totalCount);
        }else{
            orderEO.setTotalReceiveQuantity(0d);
        }

        List<ReceiveOrderDetailEO> details = this.baseMapper.getByReceiveId(orderEO.getReceiveId());

        for(ReceiveOrderDetailEO detailEO:details){
            //orderEO.setTotalReceiveQuantity(orderEO.getTotalReceiveQuantity() + detailEO.getReceiveAmount());
            if(detailEO.getStatus() == 2){
                orderEO.setTotalRelReceiveQuantity(orderEO.getTotalRelReceiveQuantity() + detailEO.getRelReceiveAmount());
            }
        }
        orderEO.setDetails(details);
        return orderEO;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean receiveOne(Long Id, Double amount, String action, Long userId, String userName) throws BusinessException {
        try {
            if(procedureLock.contains(Id)){

                throw  new BusinessException("当前数据正在操作中，无法操作请刷新后重试");
            }else{
                procedureLock.add(Id);
                UserEO userEO = new UserEO();
                userEO.setUserId(userId);
                userEO.setUserName(userName);

                ReceiveOrderDetailEO detailEO = this.receiveOrderDetailService.getById(Id);
                MaterialEO materialEO = this.baseMapper.selectMaterialEO(Id);
                //判断状态
                if("add".equals(action) && detailEO.getStatus() != 1){
                    throw new BusinessException("入库单明细状态非发布状态，请刷新确认");
                }else if("remove".equals(action) && detailEO.getStatus() != 2){
                    throw new BusinessException("入库单明细状态非已完成状态，请刷新确认");
                }

                //判断出库明细是否都已完成
                ReceiveOrderEO receiveOrderEO = this.baseMapper.selectById(detailEO.getReceiveOrderId());


                if("add".equals(action)) {
                    if(null != materialEO.getWarehouseLocationId()){
                        WarehouseLocationEO locationEO = this.warehouseLocationService.getById(materialEO.getWarehouseLocationId());
                        detailEO.setWarehouseId(locationEO.getWarehouseId());
                    }else{
                        detailEO.setWarehouseId(materialEO.getMainWarehouseId());
                    }
                    detailEO.setWarehouseLocationId(materialEO.getWarehouseLocationId());

                    detailEO.setStatus(2);
                    detailEO.setRelReceiveAmount(amount);
                    this.setStock(detailEO,receiveOrderEO);
                    if(receiveOrderEO.getReceiveType() == 2){
                        Result result = this.deliveryOrderService.addDeliveryOrder(detailEO.getMaterialId(),amount,detailEO.getReceiveDetailId(),userEO);
                        if(result.getCode() != 0) {
                            throw new BusinessException(result.getMsg());
                        }
                    }
                }else{
                    detailEO.setStatus(1);
                    detailEO.setRelReceiveAmount(0d);
                    this.deleteStockByDetailId(detailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
                    if(receiveOrderEO.getReceiveType() == 2){
                        this.deliveryOrderService.deleteOrderByDetailId(detailEO.getReceiveDetailId());
                    }
                }

                this.receiveOrderDetailService.updateById(detailEO);

                // 获取工厂是否推送入库工单配置
                JSONObject object = ExcelUtils.parseJsonFile("config/work_order.json");
                JSONObject productinstock = object.getJSONObject("productinstock");
                boolean flag = productinstock.getBoolean(receiveOrderEO.getOrgCode().toLowerCase());
                // 推送入库工单
                if(flag) {
                    this.receiveWorkOrderService.addByReceiveOrderDetail(detailEO, receiveOrderEO.getOrgId(), 2, userEO);
                }

                if("add".equals(action) && (receiveOrderEO.getStatus() == 0 || receiveOrderEO.getStatus() == 2)){
                    throw new BusinessException("入库单状态为新建或已完成状态、不能进行入库操作,请确认!");
                }


                //校验机构权限
                if(!checkPer(receiveOrderEO.getOrgId(),userId)){
                    throw new BusinessException("入库单的归属机构下的数据该用户没有操作权限，请确认！");
                }

                Integer finishCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
                if(finishCount > 0){
                    receiveOrderEO.setStatus(1);
                    receiveOrderEO.setReceiveUserId("");
                    receiveOrderEO.setReceiveUserName("");
                }else {
                    receiveOrderEO.setStatus(2);
                    receiveOrderEO.setReceiveUserId(userId+"");
                    receiveOrderEO.setReceiveUserName(userName);
                }
                this.baseMapper.updateById(receiveOrderEO);
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

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }


    public boolean deleteStockByDetailId(Long Id,Integer type) throws BusinessException {
        return this.baseMapper.deleteStockByDetailId(Id,type);
    }


    public boolean setStock(ReceiveOrderDetailEO receiveOrderDetailEO, ReceiveOrderEO receiveOrderEO) throws BusinessException {
        StockAccountEO accountEO = new StockAccountEO();

        accountEO.setVoucherId(receiveOrderDetailEO.getReceiveDetailId());
        //accountEO.setVoucherDate(receiveOrderEO.getReceiveDate());
        accountEO.setVoucherDate(new Date());
        accountEO.setVoucherType(receiveOrderEO.getReceiveType());
        if(null != receiveOrderEO.getType()){
            //成品退货入库
            accountEO.setChildVoucherType(receiveOrderEO.getType());
            if(accountEO.getChildVoucherType() == 3){
                accountEO.setRemarks("成品退货入库");
            }else if (accountEO.getChildVoucherType() == 8){
                accountEO.setRemarks("原材料退料入库");
            }
        }
        accountEO.setMaterialId(receiveOrderDetailEO.getMaterialId());
        accountEO.setMaterialCode(receiveOrderDetailEO.getMaterialCode());
        accountEO.setMaterialName(receiveOrderDetailEO.getMaterialName());

        accountEO.setUnitId(receiveOrderDetailEO.getUnitId());
        accountEO.setInventoryCode(receiveOrderDetailEO.getInventoryCode());
        accountEO.setElementNo(receiveOrderDetailEO.getElementNo());
        accountEO.setSpecification(receiveOrderDetailEO.getSpecification());

        accountEO.setFigureNumber(receiveOrderDetailEO.getFigureNumber());
        accountEO.setFigureVersion(receiveOrderDetailEO.getFigureVersion());
        accountEO.setWarehouseId(receiveOrderDetailEO.getWarehouseId());
        accountEO.setWarehouseLocationId(receiveOrderDetailEO.getWarehouseLocationId());
        accountEO.setAmount(receiveOrderDetailEO.getRelReceiveAmount());
        accountEO.setRemarks(receiveOrderDetailEO.getRemarks());

        //检查是否存在相同的出库记录，防止卡顿出现多条相同的记录
        int count= this.deliveryOrderMapper.selectStockByCondition(accountEO.getVoucherId(),accountEO.getMaterialId(),accountEO.getVoucherType(),accountEO.getWarehouseId(),accountEO.getWarehouseLocationId(),accountEO.getAmount());
        if(count>0){
            throw new BusinessException("该条记录已存在，请刷新后重试");
        }
        //保存台账
        return this.stockAccountService.save(accountEO);
    }

    public List<ReceiveOrderDetailEO> getByReceiveOrderIds(Long[] receiveOrderIds, Long userId) throws BusinessException {
        List<ReceiveOrderDetailEO> receiveOrderDetails = this.baseMapper.getByReceiveOrderIds(receiveOrderIds, userId);
        return receiveOrderDetails;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DeliveryOrderDetailEO> getDeliveryOrderDetails(Long[] receiveOrderIds, Long userId) throws BusinessException {
        List<DeliveryOrderDetailEO> deliveryOrderDetails = this.baseMapper.getDeliveryOrderDetails(receiveOrderIds, userId);

        Map map = new HashMap();
        if(deliveryOrderDetails!=null && deliveryOrderDetails.size()>0) {
            List<ReceiveOrderEO> receiveOrders = this.baseMapper.getByIds(receiveOrderIds);
            if(receiveOrders!=null && receiveOrders.size()>0) {
                for(ReceiveOrderEO receiveOrder : receiveOrders) {
                    String erpVoucherNo = receiveOrder.getErpVoucherNo1();
                    if(erpVoucherNo==null || erpVoucherNo.trim().equals("")) {
                        erpVoucherNo = this.businessCodeGenerator.getErpVoucherNo("delivery_order_" + receiveOrder.getOrgId());
                        if(erpVoucherNo.matches("^[A-Z0-9]+$")) {
                            this.baseMapper.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo, "(" + receiveOrder.getReceiveId() + ")");
                        }
                    }

                    map.put(receiveOrder.getReceiveId(), erpVoucherNo);
                }
            }

            for(DeliveryOrderDetailEO deliveryOrderDetail : deliveryOrderDetails) {
                String erpVoucherNo = map.get(deliveryOrderDetail.getReceiveOrderId()).toString();
                deliveryOrderDetail.setVoucherNoSub(erpVoucherNo.substring(2, erpVoucherNo.length()));
            }
        }

        return deliveryOrderDetails;
    }

    public Integer countAll() throws BusinessException {
        return this.baseMapper.countAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean receiveOnelocation(Long Id, Double amount, String action, Long userId, String userName,Long locationId) throws BusinessException {

        UserEO userEO = new UserEO();
        userEO.setUserId(userId);
        userEO.setUserName(userName);

        ReceiveOrderDetailEO detailEO = this.receiveOrderDetailService.getById(Id);
        MaterialEO materialEO = this.baseMapper.selectMaterialEO(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("入库单明细状态非发布状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 2){
            throw new BusinessException("入库单明细状态非已完成状态，请刷新确认");
        }

        //判断出库明细是否都已完成
        ReceiveOrderEO receiveOrderEO = this.baseMapper.selectById(detailEO.getReceiveOrderId());


        if("add".equals(action)) {
            if(null != locationId){
                WarehouseLocationEO locationEO = this.warehouseLocationService.getById(locationId);
                detailEO.setWarehouseId(locationEO.getWarehouseId());
            }else{
                detailEO.setWarehouseId(materialEO.getMainWarehouseId());
            }
            detailEO.setWarehouseLocationId(locationId);
            detailEO.setStatus(2);
            detailEO.setRelReceiveAmount(amount);
            this.setStock(detailEO,receiveOrderEO);
            if(receiveOrderEO.getReceiveType() == 2){
                this.deliveryOrderService.addDeliveryOrder(detailEO.getMaterialId(),amount,detailEO.getReceiveDetailId(),userEO);
            }
        }else{
            detailEO.setStatus(1);
            detailEO.setRelReceiveAmount(0d);
            this.deleteStockByDetailId(detailEO.getReceiveDetailId(),receiveOrderEO.getReceiveType());
            if(receiveOrderEO.getReceiveType() == 2){
                this.deliveryOrderService.deleteOrderByDetailId(detailEO.getReceiveDetailId());
            }
        }

        this.receiveOrderDetailService.updateById(detailEO);


        if("add".equals(action) && (receiveOrderEO.getStatus() == 0 || receiveOrderEO.getStatus() == 2)){
            throw new BusinessException("入库单状态为新建或已完成状态、不能进行入库操作,请确认!");
        }


        //校验机构权限
        if(!checkPer(receiveOrderEO.getOrgId(),userId)){
            throw new BusinessException("入库单的归属机构下的数据该用户没有操作权限，请确认！");
        }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(receiveOrderEO.getReceiveId());
        if(finishCount > 0){
            receiveOrderEO.setStatus(1);
            receiveOrderEO.setReceiveUserId("");
            receiveOrderEO.setReceiveUserName("");
        }else {
            receiveOrderEO.setStatus(2);
            receiveOrderEO.setReceiveUserId(userId+"");
            receiveOrderEO.setReceiveUserName(userName);
        }
        this.baseMapper.updateById(receiveOrderEO);

        return true;
    }

    public List<WarehouseLocationEO> getWarehouseLocation(Long id) {

        return this.baseMapper.getWarehouseLocation(id);
    }

    public void addBatch(List<ReceiveOrderEO> receiveOrders) {
        this.baseMapper.addBatch(receiveOrders);
    }

    public void deleteByPoorProductionIds(String sqlStr) {
        this.baseMapper.deleteByPoorProductionIds(sqlStr);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result syncU8ProductInStock(Long[] receiveOrderIds, UserEO user) throws BusinessException {
        Boolean isSubmitSync = false;
        // 获取全局是否同步配置
        JSONObject object = ExcelUtils.parseJsonFile("config/schedule.json");
        if(object != null) {
            if(object.getBoolean("isSubmitSync") != null) {
                isSubmitSync = object.getBoolean("isSubmitSync");
            }
        }
        if(!isSubmitSync.booleanValue()) {
            throw new BusinessException("全局同步已被禁止!");
        }

        Result result = new Result();
        List<ReceiveOrderEO> receiveOrders = this.baseMapper.getU8ListByReceiveOrderIds(receiveOrderIds);
        String errorMsg = "";
        Map u8Map = new HashedMap();
        Map u8NewMap = new HashedMap();
        Map u8IDMap = new HashedMap();
        Map u8CountMap = new HashedMap();
        Map mesMap = new HashedMap();
        if(receiveOrders!=null && receiveOrders.size()>0) {
            for(ReceiveOrderEO receiveOrder : receiveOrders) {
                // 判断入库单状态(只有完成状态提交同步)
                if(receiveOrder.getStatus().intValue() != 2) {
                    errorMsg += (receiveOrder.getVoucherNo() + "的状态不是已完成!\n");
                }

                // 判断入库单同步状态(只有状态是未同步,部分成功或全部失败的才能提交同步)
                if(receiveOrder.getSyncStatus() == null) {
                    receiveOrder.setSyncStatus(0);
                }
                if(receiveOrder.getSyncStatus().intValue()!=0 &&
                        receiveOrder.getSyncStatus().intValue()!=3 &&
                        receiveOrder.getSyncStatus().intValue()!=4) {
                    errorMsg += (receiveOrder.getVoucherNo() + "的同步状态不是未同步,部分成功,全部失败三者中的一个!\n");
                }

                // 判断入库单明细的仓库id是否有值
                List<ReceiveOrderDetailEO> details = receiveOrder.getDetails();
                if(details!=null && details.size()>0) {
                    for(ReceiveOrderDetailEO receiveOrderDetail : details) {
                        if(receiveOrderDetail.getWarehouseId() == null) {
                            errorMsg += (receiveOrder.getVoucherNo() + "中的" + receiveOrderDetail.getElementNo() + "没有设置仓库!" + "\n");
                        }
                    }
                }
            }

            if(!errorMsg.equals("")) {
                throw new BusinessException(errorMsg);
            }

            // 解析mes系统的入库单及明细数据(根据入库单ID及明细仓库ID分条目，且每个存货编码只能出现1次)
            if(receiveOrders!=null && receiveOrders.size()>0) {
                String u8Str = "";
                for(ReceiveOrderEO receiveOrder : receiveOrders) {
                    u8Str += (receiveOrder.getReceiveId() + ",");
                    List<ReceiveOrderDetailEO> details = receiveOrder.getDetails();
                    if(details!=null && details.size()>0) {
                        for(ReceiveOrderDetailEO receiveOrderDetail : details) {
                            String key = receiveOrder.getReceiveId() + "-" + receiveOrderDetail.getWarehouseId();
                            if(mesMap.get(key) != null) {
                                Map map = (Map) mesMap.get(key);
                                JSONObject jsonObject = (JSONObject) map.get("Data");
                                JSONArray jsonArray = (JSONArray) jsonObject.get("details");
                                if(jsonArray != null) {
                                    boolean flag = false; // details节点是否包含存货编码，false表示不包含，true表示包含
                                    for(Object o : jsonArray) {
                                        JSONObject jo = (JSONObject) o;
                                        if(jo.get("productcode")!=null && jo.get("productcode").equals(receiveOrderDetail.getInventoryCode())) {
                                            flag = true;
                                            Double d = (Double) jo.get("productcount");
                                            if(d == null) {
                                                d = Double.valueOf(0);
                                            }

                                            jo.put("productcount", d + receiveOrderDetail.getRelReceiveAmount());
                                        }
                                    }

                                    if(!flag) {
                                        JSONObject ic = new JSONObject();
                                        ic.put("productcode", receiveOrderDetail.getInventoryCode());
                                        ic.put("productcount", receiveOrderDetail.getRelReceiveAmount());
                                        ic.put("batchno", "");
                                        ic.put("price", 0);
                                        jsonArray.add(ic);
                                    }
                                }

                                mesMap.put(key, map);
                            } else {
                                JSONObject property = (JSONObject) object.get("productinstock");
                                JSONObject orgProperty = (JSONObject) property.get(receiveOrder.getOrgCode().toLowerCase());

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("date", DateUtils.format(receiveOrder.getReceiveDate(), "yyyy-MM-dd"));
                                jsonObject.put("whcode", receiveOrderDetail.getErpCode());
                                jsonObject.put("rdcode", orgProperty.get("rdcode")); // 随变，需要配置文件
                                jsonObject.put("orderno", "");
                                jsonObject.put("stockno", "");
                                jsonObject.put("maker", user.getRealName());
                                jsonObject.put("depcode", orgProperty.get("depcode"));// 随部门变化的，需要配置文件

                                JSONArray jsonArray = new JSONArray();
                                JSONObject ic = new JSONObject();
                                ic.put("productcode", receiveOrderDetail.getInventoryCode());
                                ic.put("productcount", receiveOrderDetail.getRelReceiveAmount());
                                ic.put("batchno", "");
                                ic.put("price", 0);
                                jsonArray.add(ic);
                                jsonObject.put("details", jsonArray);

                                Map map = new HashedMap();
                                map.put("Type", "productinstock");
                                map.put("RootOrgID", receiveOrder.getOrgId());
                                map.put("VoucherID", receiveOrder.getReceiveId());
                                map.put("VoucherTableName", "wms_receive_order");
                                map.put("WarehouseID", receiveOrderDetail.getWarehouseId());
                                map.put("OrderNumber", receiveOrder.getVoucherNo());
                                map.put("VoucherDate", DateUtils.format(receiveOrder.getReceiveDate(), "yyyy-MM-dd"));
                                map.put("Data", jsonObject);

                                mesMap.put(key, map);
                            }
                        }
                    }

                    // 查询U8_API_Message中的入库单表对应的所有数据
                    if("".equals(u8Str)) {
                        u8Str = "(-1)";
                    } else {
                        u8Str = "(" + u8Str.substring(0, u8Str.length()-1) + ")";
                    }
                    String selectSql = "select * from U8_API_Message where VoucherTableName = 'wms_receive_order' and Type='productinstock' and VoucherID in" + u8Str;
                    List<Map<String,Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                    if(u8List!=null && u8List.size()>0) {
                        int u8Count;
                        for(Map map : u8List) {
                            Long ID = (Long) map.get("ID");
                            Long VoucherID = (Long) map.get("VoucherID");
                            Long WarehouseID = (Long) map.get("WarehouseID");
                            Short Status = (Short) map.get("Status");
                            u8IDMap.put(VoucherID + "-" + WarehouseID, ID);
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
        }

        int mesCount;
        Map mesCountMap = new HashedMap();
        int commitCount;
        Map commitCountMap = new HashedMap();
        Map<Long, String> roMap = new HashedMap();
        for(Object o : mesMap.keySet()) {
            // 统计每条数据拆分得到的数量
            Long receiveOrderId = Long.valueOf(o.toString().split("-")[0]);
            if(mesCountMap.get(""+receiveOrderId) == null) {
                mesCount = 1;
            } else {
                mesCount = (int) mesCountMap.get(""+receiveOrderId) + 1;
            }
            mesCountMap.put(""+receiveOrderId, mesCount);

            if(!u8Map.containsKey(o)) {
                if(commitCountMap.get(""+receiveOrderId) == null) {
                    commitCount = 1;
                } else {
                    commitCount = (int) commitCountMap.get(""+receiveOrderId) + 1;
                }
                commitCountMap.put(""+receiveOrderId, commitCount);
            }

            roMap.put(receiveOrderId,  "共拆分为" + mesCountMap.get(""+receiveOrderId) + "条单据," +
                    "之前已成功" + (u8CountMap.get(""+receiveOrderId)==null?0:u8CountMap.get(""+receiveOrderId)) + "条," +
                    "本次提交" + (commitCountMap.get(""+receiveOrderId)==null?0:commitCountMap.get(""+receiveOrderId)) + "条  ");
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
                Long receiveOrderId = Long.valueOf(o.toString().split("-")[0]);
                String newMsg = "";
                if(mesMap.keySet().contains(o) && u8NewMap.keySet().contains(o)) { // U8与MES都存在
                    // 统计每条数据拆分得到的数量
                    if(mesCountMap.get(""+receiveOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+receiveOrderId) + 1;
                    }
                    mesCountMap.put(""+receiveOrderId, mesCount);

                    if(commitCountMap.get(""+receiveOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+receiveOrderId) + 1;
                    }
                    commitCountMap.put(""+receiveOrderId, commitCount);

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
                    if(mesCountMap.get(""+receiveOrderId) == null) {
                        mesCount = 1;
                    } else {
                        mesCount = (int) mesCountMap.get(""+receiveOrderId) + 1;
                    }
                    mesCountMap.put(""+receiveOrderId, mesCount);

                    if(commitCountMap.get(""+receiveOrderId) == null) {
                        commitCount = 1;
                    } else {
                        commitCount = (int) commitCountMap.get(""+receiveOrderId) + 1;
                    }
                    commitCountMap.put(""+receiveOrderId, commitCount);

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

                roMap.put(receiveOrderId, (roMap.get(receiveOrderId)==null?"":roMap.get(receiveOrderId)) + newMsg);
            }
        }

        if(!string.equals("")) {
            int ec = SqlActuator.excute(string, u8DBConnectInfo);
            if(ec <= 0) {
                throw new BusinessException("同步失败,未执行到U8!");
            }
        }

        if(roMap!=null && roMap.size()>0) {
            int sc = this.baseMapper.syncU8Update(roMap);
            if(sc <= 0) {
                throw new BusinessException("同步失败,未更新同步信息!");
            }
        }

        return result;
    }

    public IPage<ReceiveOrderDetailEO> selectProduceInReport(Criteria criteria) {

        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        QueryWrapper<ReceiveOrderDetailEO> wrapper = new QueryWrapper<ReceiveOrderDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<ReceiveOrderDetailEO> totalList = this.baseMapper.selectPageByViewMode(param);
        int total = totalList.size();
        int pages =  total/criteria.getSize();
        if(total % criteria.getSize() > 0){
            pages = pages +1;
        }

        param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        param.put("pageSize", criteria.getSize());
        List<ReceiveOrderDetailEO> list = this.baseMapper.selectPageByViewMode(param);

        IPage<ReceiveOrderDetailEO> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(criteria.getCurrentPage());
        page.setPages(pages);
        page.setSize(criteria.getSize());
        page.setTotal(total);
        return page;
    }

    public List<ReceiveOrderDetailEO> listAllDetails(Map map) {
        return this.baseMapper.selectPageByViewMode(map);
    }
}
