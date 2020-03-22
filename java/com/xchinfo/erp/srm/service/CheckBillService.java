package com.xchinfo.erp.srm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.scm.srm.entity.CheckBillDetailEO;
import com.xchinfo.erp.scm.srm.entity.CheckBillEO;
import com.xchinfo.erp.srm.mapper.CheckBillDetailMapper;
import com.xchinfo.erp.srm.mapper.CheckBillMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.commons.collections.map.HashedMap;
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

import java.io.Serializable;
import java.util.*;

/**
 * @author yuanchang
 * @date 2019/10/18
 */
@Service
public class CheckBillService extends BaseServiceImpl<CheckBillMapper, CheckBillEO> {


    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private CheckBillDetailMapper checkBillDetailMapper;

    @Autowired
    private CheckBillDetailService checkBillDetailService;

    @Autowired
    private OrgService orgService;

    @Override
    public IPage<CheckBillEO> selectPage(Criteria criteria) {
        IPage<CheckBillEO> orders = super.selectPage(criteria);

     /*for (CheckBillEO order : orders.getRecords()){
      List<CheckBillDetailEO> details = this.checkBillDetailMapper.selectByReceiveOrder(order.getCheckId());
      order.setCheckBillDetailEOS(details);
     }*/

        return orders;
    }

    @Transactional(rollbackFor = Exception.class)
    public CheckBillEO saveEntity(CheckBillEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_check_bill",entity);
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);
        // 先保存订单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存入库单失败！");

        if(entity.getCheckType()==0){
            // 保存供应商对账单明细对象
            //this.baseMapper.insertdetail(entity.getCheckId(),entity.getOrgId(),entity.getSupplierId(),entity.getEndDate(),userName);

            // 保存出库单明细对象
            if (entity.getCheckBillDetailEOS()!=null && entity.getCheckBillDetailEOS().size()>0){
                for (CheckBillDetailEO detail : entity.getCheckBillDetailEOS()){
                    detail.setCheckId(entity.getCheckId());
                }
                this.checkBillDetailService.saveBatch(entity.getCheckBillDetailEOS());

                //更新入库单中是否加入对账表状态为1：已加入
                // this.baseMapper.updateReceiveJoinStatus(entity.getCheckId());
            }


        }else{
            // 保存客户对账单明细对象
            //this.baseMapper.insertCustomerdetail(entity.getCheckId(),entity.getOrgId(),entity.getCustomerId(),entity.getEndDate(),userName);
            // 保存出库单明细对象
            if (entity.getCheckBillDetailEOS()!=null && entity.getCheckBillDetailEOS().size()>0){
                for (CheckBillDetailEO detail : entity.getCheckBillDetailEOS()){
                    detail.setCheckId(entity.getCheckId());
                }
                this.checkBillDetailService.saveBatch(entity.getCheckBillDetailEOS());

                //更新出库单中是否加入对账表状态为1：已加入
                //this.baseMapper.updateDeliveryJoinStatus(entity.getCheckId());
            }
        }

        //更新总数量到对账单中
        this.baseMapper.updateTotalQuantity(entity.getCheckId());



        return entity;
    }


    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) throws BusinessException {

        // 删除入库单明细
        Integer result = 0;

        result = this.checkBillDetailMapper.removeByReceiveOrder((Long) id);
        if (null == result || result < 0){
            throw new BusinessException("删除入库单失败！");
        }

        return super.removeById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();

        Integer result = 0;
        // 删除对账单明细
        for (Serializable id : idList){

            CheckBillEO receiveOrderEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(receiveOrderEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            result = this.checkBillDetailMapper.removeByReceiveOrder((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除对账单失败！");
            }
        }

        return super.removeByIds(idList);
    }


    @Transactional(rollbackFor = Exception.class)
    public CheckBillEO updateById(CheckBillEO entity,Long userId) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }
        //this.checkBillDetailMapper.removeByReceiveOrder(entity.getCheckId());

        /*if(entity.getCheckType()==0){
            // 保存供应商对账单明细对象
            //this.baseMapper.insertdetail(entity.getCheckId(),entity.getOrgId(),entity.getSupplierId(),entity.getEndDate(),userName);
        }else{
            // 保存客户对账单明细对象
            //this.baseMapper.insertCustomerdetail(entity.getCheckId(),entity.getOrgId(),entity.getCustomerId(),entity.getEndDate(),userName);
        }*/
        if(entity.getCheckType()==0) {
            // 保存供应商对账单明细对象
            if (entity.getCheckBillDetailEOS() != null && entity.getCheckBillDetailEOS().size() > 0) {
                Set receiveDetailIds = new HashSet();
                Set returnDetailIds = new HashSet();
                List<CheckBillDetailEO> newdetails = new ArrayList<CheckBillDetailEO>();
                for (CheckBillDetailEO detail : entity.getCheckBillDetailEOS()) {
                    CheckBillDetailEO checkBillDetailEO= this.checkBillDetailMapper.getByReceiveDetailId(detail.getReceiveDetailId(),entity.getCheckId());
                    if (checkBillDetailEO == null){
                        detail.setCheckId(entity.getCheckId());
                        newdetails.add(detail);
                    }

                    if(detail.getResourceType().intValue() == 1) {
                        receiveDetailIds.add(detail.getReceiveDetailId());
                    }
                    if(detail.getResourceType().intValue() == 2) {
                        returnDetailIds.add(detail.getReceiveDetailId());
                    }
                }
                this.checkBillDetailService.saveOrUpdateBatch(newdetails);

                //更新入库单中是否加入对账表状态为1：已加入
                //this.baseMapper.updateReceiveJoinStatus(entity.getCheckId());

                //更新入库单明细的对账单ID及对账状态
                if(receiveDetailIds!=null && receiveDetailIds.size()>0) {
                    this.baseMapper.updateReceiveCheck(receiveDetailIds, 1);
                }
                //更新退货单单明细的对账单ID及对账状态
                if(returnDetailIds!=null && returnDetailIds.size()>0) {
                    this.baseMapper.updateReturnCheck(returnDetailIds, 1);
                }
            }
        }else{
            // 保存客户对账单明细对象
            if (entity.getCheckBillDetailEOS() != null && entity.getCheckBillDetailEOS().size() > 0) {
                List<CheckBillDetailEO> newdetails = new ArrayList<CheckBillDetailEO>();
                for (CheckBillDetailEO detail : entity.getCheckBillDetailEOS()) {
                    CheckBillDetailEO checkBillDetailEO= this.checkBillDetailMapper.getByDeliveryDetailId(detail.getDeliveryDetailId(),entity.getCheckId());
                    if (checkBillDetailEO == null){
                        detail.setCheckId(entity.getCheckId());
                        newdetails.add(detail);
                    }
                }

                this.checkBillDetailService.saveOrUpdateBatch(newdetails);
                //更新出库单中是否加入对账表状态为1：已加入
                //this.baseMapper.updateDeliveryJoinStatus(entity.getCheckId());
            }
        }

        super.updateById(entity);

        //更新总数量到对账单中
        this.baseMapper.updateTotalQuantity(entity.getCheckId());

        return entity;
    }

    @Override
    public CheckBillEO getById(Serializable id) throws BusinessException{
        CheckBillEO order = this.baseMapper.selectById(id);

        List<CheckBillDetailEO> details = this.checkBillDetailMapper.selectByReceiveOrder((Long) id);

        order.setCheckBillDetailEOS(details);

        return order;
    }

    public CheckBillEO getByCheckId(Serializable id) throws BusinessException{

        CheckBillEO order = this.baseMapper.getByCheckId(id);

        return order;
    }

    public List<CheckBillDetailEO> listDetailsByOrder(Long orderId) {
        return this.checkBillDetailMapper.selectByReceiveOrder(orderId);
    }

    public IPage<CheckBillDetailEO> selectDetailPage(Criteria criteria) {

        return this.checkBillDetailService.selectPage(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDetailIds(Serializable[] idList,Long checkId) throws BusinessException{
        Set checkBillDetailIds = new HashSet();
        if(idList!=null && idList.length>0) {
            for(Serializable id : idList) {
                Long idTemp = Long.valueOf((String) id);
                checkBillDetailIds.add(idTemp);
            }
        }

        // 修改数据来源的对账状态
        List<CheckBillDetailEO> checkBillDetails = this.baseMapper.getByIds(checkBillDetailIds);
        if(checkBillDetails!=null && checkBillDetails.size()>0) {
            Set receiveDetailIds = new HashSet();
            Set returnDetailIds = new HashSet();
            for(CheckBillDetailEO detail : checkBillDetails) {
                if(detail.getResourceType().intValue() == 1) {
                    receiveDetailIds.add(detail.getReceiveDetailId());
                }
                if(detail.getResourceType().intValue() == 2) {
                    returnDetailIds.add(detail.getReceiveDetailId());
                }
            }

            if(receiveDetailIds!=null && receiveDetailIds.size()>0) {
                this.baseMapper.updateReceiveCheck(receiveDetailIds, 0);
            }
            if(returnDetailIds!=null && returnDetailIds.size()>0) {
                this.baseMapper.updateReturnCheck(returnDetailIds, 0);
            }
        }

        this.checkBillDetailService.removeByIds(idList);

        //更新总数量到对账单中
        this.baseMapper.updateTotalQuantity(checkId);

        return true;

    }

    public CheckBillDetailEO getDetailById(Long Id) {
        return this.checkBillDetailMapper.selectById(Id);
    }


    public List<CheckBillDetailEO> selectPageByCondition(Criteria criteria) {


        Map<String, Object> param = new HashedMap();

        param.put("currIndex", 0);
        param.put("pageSize", 10000000);

        param.put("orderField",criteria.getOrderField());
        param.put("order",criteria.getOrder());
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<CheckBillDetailEO> totalList = this.baseMapper.selectPageByCondition(param);

        return totalList;
    }

    public List<CheckBillDetailEO> selectCustomerPageByCondition(Criteria criteria) {


        Map<String, Object> param = new HashedMap();
        param.put("orderField",criteria.getOrderField());
        param.put("order",criteria.getOrder());

        QueryWrapper<CheckBillDetailEO> wrapper = new QueryWrapper<CheckBillDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<CheckBillDetailEO> totalList = this.baseMapper.selectCustomerPageByCondition(param);

        return totalList;
    }

    public List<CheckBillDetailEO> hasSelectPage(Criteria criteria) {


        Map<String, Object> param = new HashedMap();
        param.put("orderField",criteria.getOrderField());
        param.put("order",criteria.getOrder());

        QueryWrapper<CheckBillDetailEO> wrapper = new QueryWrapper<CheckBillDetailEO>();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        for (Criterion criterion : criterions) {
            if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
                param.put(criterion.getField(), criterion.getValue());
            }
        }
        List<CheckBillDetailEO> totalList = this.baseMapper.hasSelectPage(param);

        return totalList;
    }



    public List<Map> getApprovers(){
        String permissions = "srm:checkBill:approve";
        return this.baseMapper.getApprovers(permissions);
    }

    /**
     * 对账单提交审核
     * @return
     */
    public boolean submit(Map map){
        String  approver = map.get("approver").toString();
        Long approverId = Long.parseLong(map.get("approverId").toString());
        Long checkId = Long.parseLong(map.get("checkId").toString());
        return this.baseMapper.submit(approverId,approver,checkId);
    }

    /**
     * 对账单审核完成
     * @return
     */
    public boolean approve(Map map){
        //当前审批人
        String  finalApprover = map.get("finalApprover").toString();
        Long finalApproverId = Long.parseLong(map.get("finalApproverId").toString());
        //审批意见及结果
        String  opinion = map.get("opinion").toString();
        Long result = Long.parseLong(map.get("result").toString());//1通过，4不通过

        //客户id
        Long checkId = Long.parseLong(map.get("checkId").toString());

        this.baseMapper.approve(result,checkId,opinion);
        if (result == 1){
            this.baseMapper.updateReceiveCheckStatus(checkId);
            this.baseMapper.updateReturnCheckStatus(checkId);
            this.baseMapper.updateDeliveryCheckStatus(checkId);
            this.baseMapper.updateCustomerReturnCheckStatus(checkId);
        }
        //保存意见

        return true;
    }

    /**
     * 对账单提交审核
     * @return
     */
    public boolean approveSubmit(Map map) {
        //当前审批人
        String finalApprover = map.get("finalApprover").toString();
        Long finalApproverId = Long.parseLong(map.get("finalApproverId").toString());
        //审批意见及结果
        String opinion = map.get("opinion").toString();
        Long result = Long.parseLong(map.get("result").toString());//1通过，4不通过

        //下一个审批人
        String approver = map.get("approver").toString();
        Long approverId = Long.parseLong(map.get("approverId").toString());
        //客户id
        Long checkId = Long.parseLong(map.get("checkId").toString());
        //提交下一个审批人
        this.baseMapper.submit(approverId, approver, checkId);
        //保存意见


        return true;
    }


    public List<CheckBillDetailEO> listexportDataByCheckId(Long checkId) {
        return this.baseMapper.listexportDataByCheckId(checkId);
    }

    public CheckBillEO getBeginDate( Long id,String endDate,Integer checkType) {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        if (checkType==0){
            return this.baseMapper.getSupplierBeginDate(id,endDate,user.getOrgId());
        }else{
            return this.baseMapper.getCustomerBeginDate(id,endDate,user.getOrgId());
        }
    }

    public List<CheckBillDetailEO> listExportTableDataByCheckId(Long checkId) {
        List<CheckBillDetailEO> newList = new ArrayList<>();
        Map map = new HashedMap();
        CheckBillEO checkBill = this.getById(checkId);
        if(checkBill != null) {
            List<CheckBillDetailEO> list = this.baseMapper.listExportTableDataByCheckId(checkId);
            for(CheckBillDetailEO cbd : list) {
                if(!map.containsKey(cbd.getMaterialId())) {
                    CheckBillDetailEO temp = new CheckBillDetailEO();
                    temp.setMaterialId(cbd.getMaterialId());
                    temp.setElementNo(cbd.getElementNo());
                    temp.setMaterialName(cbd.getMaterialName());
                    temp.setUnitName(cbd.getUnitName());
                    temp.setTotalQuantity(cbd.getTotalQuantity());
                    temp.setProjectNo(cbd.getProjectNo());
                    map.put(cbd.getMaterialId(), temp);
                    newList.add(temp);
                }

                if(cbd.getReceiveQuantity() != null) {
                    Double d = 0d;
                    if(map.get(cbd.getMaterialId()+"-ReceiveQuantity") != null) {
                        d = (Double) map.get(cbd.getMaterialId()+"-ReceiveQuantity");
                    }
                    map.put(cbd.getMaterialId()+"-ReceiveQuantity", d + cbd.getReceiveQuantity());
                }

                if(cbd.getReturnQuantity() != null) {
                    Double d = 0d;
                    if(map.get(cbd.getMaterialId()+"-ReturnQuantity") != null) {
                        d = (Double) map.get(cbd.getMaterialId()+"-ReturnQuantity");
                    }
                    map.put(cbd.getMaterialId()+"-ReturnQuantity", d + cbd.getReturnQuantity());
                }
            }

            if(newList!=null && newList.size()>0) {
                for(CheckBillDetailEO newCbd : newList) {
                    Map receiveMap = new HashedMap();
                    Map returnMap = new HashedMap();
                    for(CheckBillDetailEO cbd : list) {
                        String receiveDate = DateUtils.format(cbd.getReceiveDate(), "yyyy-MM-dd");
                        if(cbd.getMaterialId().longValue() == newCbd.getMaterialId().longValue()) {
                            if(cbd.getReceiveQuantity() != null) {
                                Double d = 0d;
                                if(receiveMap.get(receiveDate) != null) {
                                    d = (Double) receiveMap.get(receiveDate);
                                }
                                receiveMap.put(receiveDate, d + cbd.getReceiveQuantity());
                            }

                            if(cbd.getReturnQuantity() != null) {
                                Double d = 0d;
                                if(returnMap.get(receiveDate) != null) {
                                    d = (Double) returnMap.get(receiveDate);
                                }
                                returnMap.put(receiveDate, d + cbd.getReturnQuantity());
                            }
                        }
                    }

                    newCbd.setReceiveMap(receiveMap);
                    newCbd.setReturnMap(returnMap);
                    newCbd.setSumReceiveQuantity((Double) map.get(newCbd.getMaterialId()+"-ReceiveQuantity"));
                    newCbd.setSumReturnQuantity((Double) map.get(newCbd.getMaterialId()+"-ReturnQuantity"));
                }
            }
        }

        return newList;
    }
}
