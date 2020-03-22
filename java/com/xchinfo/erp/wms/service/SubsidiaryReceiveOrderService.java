package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderDetailEO;
import com.xchinfo.erp.scm.wms.entity.SubsidiaryReceiveOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.SubsidiaryReceiveOrderDetailMapper;
import com.xchinfo.erp.wms.mapper.SubsidiaryReceiveOrderMapper;
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
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class SubsidiaryReceiveOrderService extends BaseServiceImpl<SubsidiaryReceiveOrderMapper, SubsidiaryReceiveOrderEO> {

    @Autowired
    private SubsidiaryReceiveOrderMapper subsidiaryReceiveOrderMapper;

    @Autowired
    private SubsidiaryReceiveOrderDetailMapper subsidiaryReceiveOrderDetailMapper;

    @Autowired
    private SubsidiaryReceiveOrderDetailService subsidiaryReceiveOrderDetailService;

    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Override
    public IPage<SubsidiaryReceiveOrderEO> selectPage(Criteria criteria) {
        IPage<SubsidiaryReceiveOrderEO> orders = super.selectPage(criteria);

        for (SubsidiaryReceiveOrderEO order : orders.getRecords()){
            List<SubsidiaryReceiveOrderDetailEO> details = this.subsidiaryReceiveOrderDetailMapper.selectByReceiveOrder(order.getReceiveId());
            order.setDetails(details);
        }

        return orders;
    }


    @Transactional(rollbackFor = Exception.class)
    public SubsidiaryReceiveOrderEO saveEntity(SubsidiaryReceiveOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_subsidiary_receive_order", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);
        // 先保存订单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存辅料入库单失败！");


        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) throws BusinessException {
        // 删除入库单明细
        Integer result = 0;

        result = this.subsidiaryReceiveOrderDetailMapper.removeByReceiveOrder((Long) id);
        if (null == result || result < 0){
            throw new BusinessException("删除辅料入库单失败！");
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

            SubsidiaryReceiveOrderEO subsidiaryReceiveOrderEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(subsidiaryReceiveOrderEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            result = this.subsidiaryReceiveOrderDetailMapper.removeByReceiveOrder((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除辅料入库单失败！");
            }
        }

        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SubsidiaryReceiveOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        return super.updateById(entity);
    }

    @Override
    public SubsidiaryReceiveOrderEO getById(Serializable id) throws BusinessException{
        SubsidiaryReceiveOrderEO order = this.baseMapper.selectById(id);

        List<SubsidiaryReceiveOrderDetailEO> details = this.subsidiaryReceiveOrderDetailMapper.selectByReceiveOrder((Long) id);

        order.setDetails(details);

        return order;
    }



    public List<SubsidiaryReceiveOrderDetailEO> listDetailsByOrder(Long orderId) {
        return this.subsidiaryReceiveOrderDetailMapper.selectByReceiveOrder(orderId);
    }


    public IPage<SubsidiaryReceiveOrderDetailEO> selectDetailPage(Criteria criteria) {

        return this.subsidiaryReceiveOrderDetailService.selectPage(criteria);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDetailIds(Serializable[] idList) throws BusinessException{

        return this.subsidiaryReceiveOrderDetailService.removeByIds(idList);
    }


    public SubsidiaryReceiveOrderDetailEO getDetailById(Long Id) {
        return this.subsidiaryReceiveOrderDetailMapper.selectById(Id);
    }


    public boolean saveDetail(SubsidiaryReceiveOrderDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountById(entity.getReceiveOrderId(),entity.getMaterialId());
        if (count > 0){
            throw new BusinessException("该辅料入库单相关物料明细已存在，请确认！");
        }

        return this.subsidiaryReceiveOrderDetailService.save(entity);
    }


    public boolean updateDetailById(SubsidiaryReceiveOrderDetailEO entity) throws BusinessException {
        Integer count = this.baseMapper.selectCountUpdateById(entity.getReceiveOrderId(),entity.getMaterialId(),entity.getReceiveDetailId());
        if(count > 0){
            throw new BusinessException("修改后的辅料入库单相关物料明细已存在，请确认！");
        }
        return this.subsidiaryReceiveOrderDetailService.updateById(entity);
    }


    public boolean updateStatusById(Long[] idList, Integer status,Integer oldStatus) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        for(Long id:idList){
            SubsidiaryReceiveOrderEO subsidiaryReceiveOrderEOs = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(subsidiaryReceiveOrderEOs.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }
            if(status==1){
                int count = this.baseMapper.selectDetailCountById(id);
                if(count == 0){
                    //更新状态
                    this.baseMapper.updateStatusById(id,status,oldStatus);
                    continue;
                }

                SubsidiaryReceiveOrderEO SubsidiaryReceiveOrderEO =this.baseMapper.selectById(id);
                List<SubsidiaryReceiveOrderDetailEO> SubsidiaryReceiveOrderDetailEOS = this.baseMapper.selectDetailById(id);
                for ( SubsidiaryReceiveOrderDetailEO SubsidiaryReceiveOrderDetailEO :SubsidiaryReceiveOrderDetailEOS) {

                    StockAccountEO accountEO = new StockAccountEO();

                    accountEO.setVoucherId(SubsidiaryReceiveOrderDetailEO.getReceiveDetailId());
                    accountEO.setVoucherDate(SubsidiaryReceiveOrderEO.getReceiveDate());
                    accountEO.setVoucherType(4);
                    accountEO.setMaterialId(SubsidiaryReceiveOrderDetailEO.getMaterialId());
                    accountEO.setMaterialCode(SubsidiaryReceiveOrderDetailEO.getMaterialCode());
                    accountEO.setMaterialName(SubsidiaryReceiveOrderDetailEO.getMaterialName());
                    accountEO.setInventoryCode(SubsidiaryReceiveOrderDetailEO.getInventoryCode());
                    accountEO.setElementNo(SubsidiaryReceiveOrderDetailEO.getElementNo());
                    accountEO.setSpecification(SubsidiaryReceiveOrderDetailEO.getSpecification());
                    accountEO.setUnitId(SubsidiaryReceiveOrderDetailEO.getUnitId());
                    accountEO.setFigureNumber(SubsidiaryReceiveOrderDetailEO.getFigureNumber());
                    accountEO.setFigureVersion(SubsidiaryReceiveOrderDetailEO.getFigureVersion());
                    accountEO.setWarehouseId(SubsidiaryReceiveOrderDetailEO.getWarehouseId());
                    accountEO.setWarehouseLocationId(SubsidiaryReceiveOrderDetailEO.getWarehouseLocationId());
                    accountEO.setAmount(SubsidiaryReceiveOrderDetailEO.getReceiveAmount());
                    accountEO.setRemarks(SubsidiaryReceiveOrderDetailEO.getRemarks());
                    //保存台账
                    this.stockAccountService.save(accountEO);
                }

            }
            else if (status==0){

                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }
                //删除台账
                this.baseMapper.deleteStockById(id);
            }
            //更新状态
            this.baseMapper.updateStatusById(id,status,oldStatus);
        }

        return true;
    }


    public SubsidiaryReceiveOrderEO getDetailInfoByNo(String voucherNo) throws BusinessException {
        // List<DeliveryOrderDetailEO> deliveryOrderDetails = new ArrayList<>();
        SubsidiaryReceiveOrderEO orderEO = this.baseMapper.getDetailInfoByNo(voucherNo);

        orderEO.setTotalRelReceiveQuantity(0d);

        Double totalCount = this.baseMapper.selectTotalCount(orderEO.getReceiveId());
        if(null != totalCount){
            orderEO.setTotalReceiveQuantity(totalCount);
        }else{
            orderEO.setTotalReceiveQuantity(0d);
        }

        List<SubsidiaryReceiveOrderDetailEO> details = this.baseMapper.getByReceiveId(orderEO.getReceiveId());

        for(SubsidiaryReceiveOrderDetailEO detailEO:details){
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
        SubsidiaryReceiveOrderEO subsidiaryReceiveOrderEOs = this.baseMapper.selectById(Id);
        //校验机构权限
        if(!checkPer(subsidiaryReceiveOrderEOs.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        SubsidiaryReceiveOrderDetailEO detailEO = this.subsidiaryReceiveOrderDetailService.getById(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("辅料入库单明细状态非发布状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 2){
            throw new BusinessException("辅料入库单明细状态非已完成状态，请刷新确认");
        }

        if("add".equals(action)) {
            detailEO.setStatus(2);
            detailEO.setRelReceiveAmount(amount);
        }else{
            detailEO.setStatus(1);
            detailEO.setRelReceiveAmount(0d);
        }

        this.subsidiaryReceiveOrderDetailService.updateById(detailEO);

        //判断出库明细是否都已完成
        SubsidiaryReceiveOrderEO SubsidiaryReceiveOrderEO = this.baseMapper.selectById(detailEO.getReceiveOrderId());
        if("add".equals(action) && (SubsidiaryReceiveOrderEO.getStatus() == 0 || SubsidiaryReceiveOrderEO.getStatus() == 2)){
            throw new BusinessException("辅料入库单状态为新建或已完成状态、不能进行入库操作,请确认!");
        }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(SubsidiaryReceiveOrderEO.getReceiveId());
        if(finishCount > 0){
            SubsidiaryReceiveOrderEO.setStatus(1);
            SubsidiaryReceiveOrderEO.setReceiveUserId("");
            SubsidiaryReceiveOrderEO.setReceiveUserName("");
        }else {
            SubsidiaryReceiveOrderEO.setStatus(2);
            SubsidiaryReceiveOrderEO.setReceiveUserId(userId+"");
            SubsidiaryReceiveOrderEO.setReceiveUserName(userName);
        }
        this.baseMapper.updateById(SubsidiaryReceiveOrderEO);

        return true;
    }


    public boolean deleteDetailById(Long id) throws BusinessException {
        return this.baseMapper.deleteDetailById(id);
    }

    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }
}
