package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import com.xchinfo.erp.bsc.mapper.CustomerMapper;
import com.xchinfo.erp.bsc.mapper.PartnerAddressMapper;
import com.xchinfo.erp.bsc.mapper.PartnerContactMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class CustomerService extends BaseServiceImpl<CustomerMapper, CustomerEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private PartnerAddressMapper partnerAddressMapper;

    @Autowired
    private PartnerContactMapper partnerContactMapper;

    @Autowired
    private PartnerAddressService partnerAddressService;

    @Autowired
    private PartnerContactService partnerContactService;

    @Autowired
    private OrgService orgService;


    @Override
    public IPage<CustomerEO> selectPage(Criteria criteria) {
        IPage<CustomerEO> orders = super.selectPage(criteria);

        for (CustomerEO customer : orders.getRecords()){
            //查询地址
            List<PartnerAddressEO> partnerAddresses = this.partnerAddressMapper.selectByPartnerId((Long) customer.getId());
            customer.setAddresses(partnerAddresses);

            //查询联系人
            List<PartnerContactEO> partnerContactes = this.partnerContactMapper.selectByPartnerId((Long) customer.getId());
            customer.setContacts(partnerContactes);
        }

        return orders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = CustomerEO.class)
    public boolean save(CustomerEO entity) throws BusinessException {

        Integer count = this.baseMapper.selectCountByName(entity);

        if(count > 0){
            throw new BusinessException("客户名称已重复，请确认！");
        }

        List<OrgEO> orgs = this.orgService.selectTreeSelectList(new HashMap<>(1));
        if(null == orgs || orgs.size() < 1){
            entity.setOrgId(0l);
        }else{
            entity.setOrgId(orgs.get(0).getOrgId());
        }
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_customer", entity);
        AssertUtils.isBlank(code);
        entity.setCustomerCode(code);

        // 先保存客户对象
        if (!retBool(this.baseMapper.insert(entity))){
            throw new BusinessException("保存订单失败！");
        }

//        //保存地址对象
//        for (PartnerAddressEO addressEO : entity.getAddresses()){
//            addressEO.setPartnerId(entity.getCustomerId());
//            if (!retBool(this.partnerAddressMapper.insert(addressEO))){
//                throw new BusinessException("保存地址失败！");
//            }
//        }
//
//        //保存联系人
//        for (PartnerContactEO contactEO : entity.getContacts()){
//            contactEO.setPartnerId(entity.getCustomerId());
//            if (!retBool(this.partnerContactMapper.insert(contactEO))){
//                throw new BusinessException("保存联系人失败！");
//            }
//        }
        return true;
    }

    public List<CustomerEO> listAll() {
        return this.baseMapper.selectAll();
    }

    @Override
    public CustomerEO getById(Serializable id) {

        CustomerEO customer = this.baseMapper.selectById(id);

        logger.info("======== CustomerController.info(entity => "+id+") ========");
        //查询地址
        List<PartnerAddressEO> partnerAddresses = this.partnerAddressMapper.selectByPartnerId((Long) id);
        customer.setAddresses(partnerAddresses);

        logger.info("======== CustomerController.info(entity => "+id+") ========");
        //查询联系人
        List<PartnerContactEO> partnerContactes = this.partnerContactMapper.selectByPartnerId((Long) id);
        customer.setContacts(partnerContactes);

        return customer;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = CustomerEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {

        Integer result = 0;
        // 删除地址
        for (Serializable id : idList){
            result  = this.partnerAddressMapper.removeByPartnerId((Long) id);

            if (null == result || result < 0){
                throw new BusinessException("删除地址失败！");
            }

            result = this.partnerContactMapper.removeByPartnerId((Long) id);

            if (null == result || result < 0){
                throw new BusinessException("删除联系人失败！");
            }

        }

        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(CustomerEO entity) throws BusinessException {

        Integer result = 0;
        Integer count = this.baseMapper.selectCountByName(entity);

        if(count > 0){
            throw new BusinessException("客户名称已重复，请确认！");
        }

        super.updateById(entity);

//
//        //先删除，再保存
//        result  = this.partnerAddressMapper.removeByPartnerId(entity.getCustomerId());
//
//        int addressInt = entity.getAddresses().size();
//
//        if (null == result || result < 0 || (addressInt > 0 && !this.partnerAddressService.saveOrUpdateBatch(entity.getAddresses()))){
//            throw new BusinessException("更新保存地址失败！");
//        }
//
//        //先删除，再保存
//        result = this.partnerContactMapper.removeByPartnerId(entity.getCustomerId());
//        int contactInt = entity.getContacts().size();
//
//        if (null == result || result < 0 || (contactInt > 0 && !this.partnerContactService.saveOrUpdateBatch(entity.getContacts()))){
//            throw new BusinessException("更新保存联系人失败！");
//        }

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateByPartnerId(Long Id,String type) throws BusinessException {

        if(Constant.CONTACT.equals(type)){
            this.partnerContactService.removeById(Id);
        }else{
            this.partnerAddressService.removeById(Id);
        }

        return true;
    }

    /**
     * 修改状态
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long Id,Integer status){
        return this.baseMapper.updateStatusById(Id,status);
    }

    /**
     * 修改联系人
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog
    public boolean updateContactById(PartnerContactEO entity) throws BusinessException {

        //联系人只有一条时，设置默认为是(1)
        int count = this.partnerContactMapper.selectCountByPartnerId(entity.getPartnerId());
        //默认存在数
        int defaultCount = this.partnerContactMapper.selectCountCByContactId(entity.getContactId(),entity.getPartnerId());

        if((count == 1 || defaultCount == 0) && entity.getDefaultContact() == 0 ){
            //设置默认
            throw new BusinessException("默认联系人必须有一条默认数据！");
        }else if(count > 1 && entity.getDefaultContact() == 1){
            this.partnerContactMapper.updateDefaultByContactId(entity.getContactId());
        }

        return this.partnerContactService.saveOrUpdate(entity);
    }

    /**
     * 修改地址
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog
    public boolean updateAddressById(PartnerAddressEO entity) throws BusinessException {

        //地址只有一条时，设置默认为是(1)
        int count = this.partnerAddressMapper.selectCountByPartnerId(entity.getPartnerId());

        //默认收发货地址数
        int recieveDeliveryCount = this.partnerAddressMapper.selectCountRDByContactId(entity.getAddressId(),entity.getPartnerId());
        //默认开票地址数
        int invoiceCount = this.partnerAddressMapper.selectCountIByContactId(entity.getAddressId(),entity.getPartnerId());
        //默认收付款地址数
        int collectPaymentCount = this.partnerAddressMapper.selectCountCPByContactId(entity.getAddressId(),entity.getPartnerId());


        if((count == 1 || recieveDeliveryCount == 0) && entity.getDefaultRecieveDelivery() == 0){
            //设置默认
            throw new BusinessException("默认收发货地址必须有一条默认数据！");
        }

        if((count == 1 || invoiceCount == 0 ) && entity.getDefaultInvoice() == 0){
            //设置默认
            throw new BusinessException("默认开票地址数必须有一条默认数据！");
        }

        if((count == 1 || collectPaymentCount == 0) && entity.getDefaultCollectPayment() == 0){
            //设置默认
            throw new BusinessException("默认收付款地址必须有一条默认数据！");

        }

        //默认收发货地址
        if(count > 1 && entity.getDefaultRecieveDelivery()== 1){
            this.partnerAddressMapper.updateDefaultRDByContactId(entity.getAddressId());
        }

        //默认开票地址
        if(count > 1 && entity.getDefaultInvoice() == 1){
            this.partnerAddressMapper.updateDefaultIByContactId(entity.getAddressId());
        }


        //默认收付款地址
        if(count > 1 && entity.getDefaultCollectPayment() == 1){
            this.partnerAddressMapper.updateDefaultCPByContactId(entity.getAddressId());
        }

        return this.partnerAddressService.saveOrUpdate(entity);
    }

    /**
     * 获取地址信息
     *
     * @return
     */
    public PartnerAddressEO getAddressInfoById(Long Id){
        return this.partnerAddressService.getById(Id);
    }

    /**
     * 获取联系人
     *
     * @return
     */
    public PartnerContactEO getContactInfoById(Long Id){
        return this.partnerContactService.getById(Id);
    }


    /**
     * 获取联系人
     *
     * @return
     */
    public boolean updateAccountStatusById(Long Id,Integer status){
        return this.baseMapper.updateAccountStatusById(Id,status);
    }

    /**
     * 新增联系人
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addContact(PartnerContactEO entity){

        //联系人只有一条时，设置默认为是(1)
        int count = this.partnerContactMapper.selectCountByPartnerId(entity.getPartnerId());
        logger.info("======== 联系人 ========"+count);
        if(count == 0){
            //设置默认
            entity.setDefaultContact(1);
        }else if(count > 0 && entity.getDefaultContact() == 1){
            this.partnerContactMapper.updateDefaultByContactId(0L);
        }


        return this.partnerContactService.saveOrUpdate(entity);
    }


    /**
     * 新增地址
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addAddress(PartnerAddressEO entity){

        //地址只有一条时，设置默认为是(1)
        int count = this.partnerAddressMapper.selectCountByPartnerId(entity.getPartnerId());

        if(count == 0){
            //设置默认
            entity.setDefaultRecieveDelivery(1);
            entity.setDefaultInvoice(1);
            entity.setDefaultCollectPayment(1);
        }

        //默认收发货地址
        if(count > 0 && entity.getDefaultRecieveDelivery()== 1){
            this.partnerAddressMapper.updateDefaultRDByContactId(0L);
        }

        //默认开票地址
        if(count > 0 && entity.getDefaultInvoice() == 1){
            this.partnerAddressMapper.updateDefaultIByContactId(0L);
        }


        //默认收付款地址
        if(count > 0 && entity.getDefaultCollectPayment() == 1){
            this.partnerAddressMapper.updateDefaultCPByContactId(0L);
        }


        return this.partnerAddressService.saveOrUpdate(entity);
    }


    public List<CustomerEO> selectPageNew(Map map) throws BusinessException {

        return this.baseMapper.selectPageNew(map);
    }

    public List<Map> getApprovers(){
        String permissions = "basic:customer:approve";
        return this.baseMapper.getApprovers(permissions);
    }
    /**
     * 客户提交审核
     * @return
     */
    public boolean submit(Map map){
        String  approver = map.get("approver").toString();
        Long approverId = Long.parseLong(map.get("approverId").toString());
        Long customerId = Long.parseLong(map.get("customerId").toString());
        return this.baseMapper.submit(approverId,approver,customerId);
    }

    /**
     * 客户提交审核
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
        Long customerId = Long.parseLong(map.get("customerId").toString());
        //提交下一个审批人
        this.baseMapper.submit(approverId, approver, customerId);
        //保存意见


        return true;
    }
    /**
     * 客户审核完成
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
        Long customerId = Long.parseLong(map.get("customerId").toString());
        this.baseMapper.approve(result,customerId);
        //保存意见

        return true;
    }

    /**
     * 客户地址提交审核
     * @return
     */
    public boolean addressSubmit(Map map){
        String  approver = map.get("approver").toString();
        Long approverId = Long.parseLong(map.get("approverId").toString());
        Long addressId = Long.parseLong(map.get("addressId").toString());
        return this.baseMapper.addressSubmit(approverId,approver,addressId);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean approveAddressSubmit(Map map) throws BusinessException {
        //当前地址审批人
        String finalApprover = map.get("finalApprover").toString();
        Long finalApproverId = Long.parseLong(map.get("finalApproverId").toString());

        //下一个地址审批人
        String approver = map.get("approver").toString();
        Long approverId = Long.parseLong(map.get("approverId").toString());

        //地址审批意见及结果
        String opinion = map.get("opinion").toString();
        Long result = Long.parseLong(map.get("result").toString());//1通过，4不通过

        //地址id
        Long addressId = Long.parseLong(map.get("addressId").toString());
        //提交下一个审批人
        this.baseMapper.addressSubmit(approverId, approver, addressId);
        //保存意见


        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean approveAddress(Map map) throws BusinessException {
        //审批意见及结果
        String  opinion = map.get("opinion").toString();
        Long result = Long.parseLong(map.get("result").toString());//1通过，4不通过

        //当前审批人
        String  finalApprover = map.get("finalApprover").toString();
        Long finalApproverId = Long.parseLong(map.get("finalApproverId").toString());


        //客户id
        Long addressId = Long.parseLong(map.get("addressId").toString());
        this.baseMapper.approveAddress(result,addressId);
        //保存意见

        return true;
    }

    public CustomerEO getByCustomerName(String customerName) {
        return this.baseMapper.getByCustomerName(customerName);
    }
}
