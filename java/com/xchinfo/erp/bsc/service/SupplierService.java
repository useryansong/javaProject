package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.mapper.PartnerAddressMapper;
import com.xchinfo.erp.bsc.mapper.PartnerContactMapper;
import com.xchinfo.erp.bsc.mapper.SupplierMapper;
import com.xchinfo.erp.sys.auth.entity.RoleEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.service.RoleService;
import com.xchinfo.erp.sys.auth.service.UserService;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.utils.Constant;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class SupplierService extends BaseServiceImpl<SupplierMapper, SupplierEO> {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private PartnerAddressService partnerAddressService;

    @Autowired
    private PartnerContactService partnerContactService;

    @Autowired
    private PartnerAddressMapper partnerAddressMapper;

    @Autowired
    private PartnerContactMapper partnerContactMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    @Lazy
    private UserService userService;


    @Override
    public IPage<SupplierEO> selectPage(Criteria criteria) {
        IPage<SupplierEO> orders = super.selectPage(criteria);

        for (SupplierEO supplier : orders.getRecords()){
            //查询地址
            List<PartnerAddressEO> partnerAddresses = this.partnerAddressMapper.selectBySupplier((Long) supplier.getId());
            supplier.setAddresses(partnerAddresses);

            //查询联系人
            List<PartnerContactEO> partnerContactes = this.partnerContactMapper.selectBySupplier((Long) supplier.getId());
            supplier.setContacts(partnerContactes);
        }

        return orders;
    }

    @Override
    public SupplierEO getById(Serializable id) {
        SupplierEO supplier = this.baseMapper.selectById(id);

        // 查找地址
        List<PartnerAddressEO> addresses = this.partnerAddressMapper.selectBySupplier((Long) id);
        supplier.setAddresses(addresses);

        // 查找联系人
        List<PartnerContactEO> contacts = this.partnerContactMapper.selectBySupplier((Long) id);
        supplier.setContacts(contacts);

        return supplier;
    }

    @Override
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(SupplierEO entity) throws BusinessException {
        //供应商名称不允许重复
        String supplierName = entity.getSupplierName();
        SupplierEO supplier = this.baseMapper.selectByName(supplierName);
        if(supplier != null) {
            throw new BusinessException("供应商名称已存在！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_supplier", entity);
        AssertUtils.isBlank(code);
        entity.setOrgId(Long.valueOf(1));
        entity.setSupplierCode(code);

        // 保存供应商对象
        this.baseMapper.insert(entity);

        // 保存地址对象
        if (entity.getAddresses()!=null && entity.getAddresses().size() > 0){
            for (PartnerAddressEO partnerAddressEO : entity.getAddresses()){
                partnerAddressEO.setPartnerId(entity.getSupplierId());
            }
            this.partnerAddressService.saveBatch(entity.getAddresses());
        }

        // 保存联系人对象
        if (entity.getContacts()!=null && entity.getContacts().size() > 0){
            for (PartnerContactEO partnerContactEO : entity.getContacts()){
                partnerContactEO.setPartnerId(entity.getSupplierId());
            }
            this.partnerContactService.saveBatch(entity.getContacts());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SupplierEO entity) throws BusinessException {
        //供应商名称不允许重复
        String supplierName = entity.getSupplierName();
        SupplierEO supplier = this.baseMapper.selectByName(supplierName);
        if(supplier!=null && supplier.getSupplierId().longValue()!=entity.getSupplierId().longValue()) {
            throw new BusinessException("供应商名称已存在！");
        }

        if(entity.getAddresses()!=null && entity.getAddresses().size()>0) {
            // 删除地址
            this.partnerAddressService.removeBySupplier(entity.getSupplierId());
            // 保存地址
            this.partnerAddressService.saveOrUpdateBatch(entity.getAddresses());
        }
        if(entity.getContacts()!=null && entity.getContacts().size()>0) {

            // 删除联系人
            this.partnerContactService.removeBySupplier(entity.getSupplierId());
            // 保存联系人
            this.partnerContactService.saveOrUpdateBatch(entity.getContacts());
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = SupplierEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        logger.info("======== SupplierService.removeByIds() ========");

        for(Serializable id: idList){
            // 删除地址
            this.partnerAddressService.removeBySupplier((Long) id);
            // 删除联系人
            this.partnerContactService.removeBySupplier((Long) id);

        }
        return super.removeByIds(idList);
    }

    public List<SupplierEO> listAll() {
        return this.baseMapper.selectAll();
    }

    public int updateStatusById(Long supplierId, Integer status){
        return this.baseMapper.updateStatusById(supplierId, status);
    }


    // 使用指定的字符串,生成指定长度的随机字符串
    // @param length 指定长度
    // @param str 指定的字符串,如果为null,默认使用"0123456789"
    private static String generateRandomString(int length, String str) {
        if (str == null) {
            str = "0123456789";
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    public int setAccount(SupplierEO supplier) throws BusinessException {
        QueryWrapper<RoleEO> wrapper = new QueryWrapper<>();
        wrapper.eq("role_code", "gysRole");
        RoleEO role = this.roleService.getOne(wrapper);
        if(role == null) {
            throw new BusinessException("供应商角色不存在!");
        }

        UserEO user = this.userService.queryByUserName(supplier.getSupplierCode());
        if(supplier.getAccountStatus() == 1) {
            if(user == null) {
                user = new UserEO();
                user.setUserName(supplier.getSupplierCode());
                user.setRealName(supplier.getSupplierName());
                String password = generateRandomString(6, null);
                user.setPasswordPlaintext(password);
                user.setPassword(password);
                user.setCheckPassword(password);
                List<Long> list = new ArrayList<>();
                list.add(role.getRoleId());
                user.setRoleIds(list);
                user.setUserType(2);
                user.setSuperAdmin(0);
                user.setStatus(1);
                user.setOrgId(Long.valueOf(1));
                user.setGender("male");
                this.userService.save(user);
            } else {
                user.setStatus(1);
                this.userService.updateById(user);
            }
        } else {
            if(user != null) {
                user.setStatus(0);
                this.userService.updateById(user);
            }
        }

        return this.baseMapper.updateAccountStatusById(supplier.getSupplierId(), supplier.getAccountStatus());
    }

    /**
     * 获取联系人
     *
     * @return
     */
    public PartnerContactEO getContactById(Long Id){
        return this.partnerContactService.getById(Id);
    }

    /**
     * 获取地址信息
     *
     * @return
     */
    public PartnerAddressEO getAddressById(Long addressId){
        return this.partnerAddressService.queryById(addressId);
    }

    /**
     * 修改联系人
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContactById(PartnerContactEO entity) throws BusinessException {
        List<PartnerContactEO> partnerContacts = this.partnerContactMapper.selectBySupplier(entity.getPartnerId());
        if(partnerContacts != null){
            if(partnerContacts.size() == 1){
                if(entity.getDefaultContact() == 0){
                    throw new BusinessException("一个联系人只能设置为默认地址！");
                }
            }else{
                if(entity.getDefaultContact() == 0) {
                    int count = 0;
                    for (PartnerContactEO partnerContact : partnerContacts) {
                        if (partnerContact.getContactId().longValue() != entity.getContactId().longValue()) {
                            if(partnerContact.getDefaultContact() == 0){
                                count ++;
                            }
                        }
                    }
                    if((count + 1) == partnerContacts.size()){
                        throw new BusinessException("联系人必须有一个为默认地址！");
                    }
                }else if(entity.getDefaultContact() == 1){
                    for (PartnerContactEO partnerContact : partnerContacts) {
                        if (partnerContact.getContactId().longValue() != entity.getContactId().longValue()) {
                            partnerContact.setDefaultContact(0);
                            this.partnerContactService.updateById(partnerContact);
                        }
                    }
                }
            }
        }else{
            throw new BusinessException("数据已被删除,请刷新页面!");
        }
        return this.partnerContactService.updateById(entity);
    }

    /**
     * 修改地址
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddressById(PartnerAddressEO entity) throws BusinessException {
        List<PartnerAddressEO> partnerAddresses = this.partnerAddressMapper.selectBySupplier(entity.getPartnerId());
        if(partnerAddresses != null){
            if(partnerAddresses.size() == 1){
                if(entity.getDefaultRecieveDelivery() == 0){
                    throw new BusinessException("一个地址只能设置为默认收发货地址！");
                }
                if(entity.getDefaultInvoice() == 0){
                    throw new BusinessException("一个地址只能设置为默认收开票地址！");
                }
                if(entity.getDefaultCollectPayment() == 0){
                    throw new BusinessException("一个地址只能设置为默认收付款地址！");
                }
            }else{
                // 默认收发货地址
                if(entity.getDefaultRecieveDelivery() == 0) {
                    int count = 0;
                    for (PartnerAddressEO partnerAddress : partnerAddresses) {
                        if (partnerAddress.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            if(partnerAddress.getDefaultRecieveDelivery() == 0){
                                count ++;
                            }
                        }
                    }
                    if((count + 1) == partnerAddresses.size()){
                        throw new BusinessException("地址必须有一个为默认收发货地址！");
                    }
                }else if(entity.getDefaultRecieveDelivery() == 1){
                    for (PartnerAddressEO partnerAddress : partnerAddresses) {
                        if (partnerAddress.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            partnerAddress.setDefaultRecieveDelivery(0);
                            this.partnerAddressService.updateById(partnerAddress);
                        }
                    }
                }

                // 默认收开票地址
                if(entity.getDefaultInvoice() == 0) {
                    int count = 0;
                    for (PartnerAddressEO partnerAddres : partnerAddresses) {
                        if (partnerAddres.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            if(partnerAddres.getDefaultInvoice() == 0){
                                count ++;
                            }
                        }
                    }
                    if((count + 1) == partnerAddresses.size()){
                        throw new BusinessException("地址必须有一个为默认收开票地址！");
                    }
                }else if(entity.getDefaultInvoice() == 1){
                    for (PartnerAddressEO partnerAddress : partnerAddresses) {
                        if (partnerAddress.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            partnerAddress.setDefaultInvoice(0);
                            this.partnerAddressService.updateById(partnerAddress);
                        }
                    }
                }

                // 默认收付款地址
                if(entity.getDefaultCollectPayment() == 0) {
                    int count = 0;
                    for (PartnerAddressEO partnerAddres : partnerAddresses) {
                        if (partnerAddres.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            if(partnerAddres.getDefaultCollectPayment() == 0){
                                count ++;
                            }
                        }
                    }
                    if((count + 1) == partnerAddresses.size()){
                        throw new BusinessException("地址必须有一个为默认收付款地址！");
                    }
                }else if(entity.getDefaultCollectPayment() == 1){
                    for (PartnerAddressEO partnerAddress : partnerAddresses) {
                        if (partnerAddress.getAddressId().longValue() != entity.getAddressId().longValue()) {
                            partnerAddress.setDefaultCollectPayment(0);
                            this.partnerAddressService.updateById(partnerAddress);
                        }
                    }
                }
            }
        }else{
            throw new BusinessException("数据已被删除,请刷新页面!");
        }
        return this.partnerAddressService.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByPartnerId(Long id,String type) throws BusinessException {
        if(Constant.CONTACT.equals(type)){
            this.partnerContactService.removeById(id);
        }else{
            this.partnerAddressService.removeById(id);
        }

        return true;
    }

    public SupplierEO getBySupplierCode(String supplierCode) {
        return this.baseMapper.getBySupplierCode(supplierCode);
    }

    public void importFromExcel(List list, UserEO user) {
        List<Map> mapList = (List<Map>) list.get(0);
        for (int i = 1; i < mapList.size(); i++) {
            Map map = mapList.get(i);
            SupplierEO supplier = new SupplierEO();
            String supplierCode = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_supplier", supplier); // 编码
            supplier.setSupplierCode(supplierCode);
            supplier.setErpCode((String) map.get("1"));  // 供应商ERP编码
            supplier.setSupplierName((String) map.get("2"));  // 供应商名称
            supplier.setContactName((String) map.get("3"));   // 主要联络人
            supplier.setJobTitle((String) map.get("4"));      // 职务
            supplier.setContactPhone((String) map.get("5"));  // 电话
            supplier.setContactFax((String) map.get("6"));    // 传真
            supplier.setContactMobile((String) map.get("7")); // 手机
            supplier.setContactAddress((String) map.get("8"));// 地址
            supplier.setContactEmail((String) map.get("9"));  // 邮箱
            supplier.setStatus(1);                            // 默认状态
            supplier.setOrgId(Long.valueOf(118));
            supplier.setAccountStatus(0);
            super.save(supplier);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addPartnerContact(PartnerContactEO entity) throws BusinessException {
        List<PartnerContactEO> contacts = this.partnerContactMapper.selectBySupplier(entity.getPartnerId());
        if(contacts==null || contacts.size()==0) {
            // 第一次新增联系人时,即给供应商加第一个联系人时
            if(entity.getDefaultContact() == 0){
                entity.setDefaultContact(1);
//                throw new BusinessException("一个联系人只能设置为默认地址！");
            }
        } else {
            // 新增联系人设置为默认时,其他的默认需要修改为非默认
            if(entity.getDefaultContact() == 1) {
                for(PartnerContactEO partnerContact : contacts) {
                    if(partnerContact.getDefaultContact() == 1) {
                        partnerContact.setDefaultContact(0);
                        this.partnerContactMapper.updateById(partnerContact);
                    }
                }
            }
        }
        return this.partnerContactService.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addPartnerAddress(PartnerAddressEO entity) throws BusinessException {
        List<PartnerAddressEO> addresses = this.partnerAddressMapper.selectBySupplier(entity.getPartnerId());
        if(addresses==null || addresses.size()==0) {
            // 第一次新增地址时,即给供应商加第一个地址时
            if(entity.getDefaultCollectPayment() == 0){
                entity.setDefaultCollectPayment(1);
//                throw new BusinessException("一个地址只能设置为默认收付款地址！");
            }
            if(entity.getDefaultInvoice() == 0){
                entity.setDefaultInvoice(1);
//                throw new BusinessException("一个地址只能设置为默认收开票地址！");
            }
            if(entity.getDefaultRecieveDelivery() == 0){
                entity.setDefaultRecieveDelivery(1);
//                throw new BusinessException("一个地址只能设置为默认收发货地址！");
            }
        } else {
            // 新增联系人设置为默认地址时,其他的默认地址需要修改为非默认地址

            // 只能有一个地址设置为默认收付款地址
            if(entity.getDefaultCollectPayment() == 1){
                for(PartnerAddressEO partnerAddress : addresses) {
                    if(partnerAddress.getDefaultCollectPayment() == 1) {
                        partnerAddress.setDefaultCollectPayment(0);
                        this.partnerAddressMapper.updateById(partnerAddress);
                        break;
                    }
                }
            }
            // 只能有一个地址设置为默认收开票地址
            if(entity.getDefaultInvoice() == 1){
                for(PartnerAddressEO partnerAddress : addresses) {
                    if(partnerAddress.getDefaultInvoice() == 1) {
                        partnerAddress.setDefaultInvoice(0);
                        this.partnerAddressMapper.updateById(partnerAddress);
                        break;
                    }
                }
            }
            // 只能有一个地址设置为默认收发货地址
            if(entity.getDefaultRecieveDelivery() == 1){
                for(PartnerAddressEO partnerAddress : addresses) {
                    if(partnerAddress.getDefaultRecieveDelivery() == 1) {
                        partnerAddress.setDefaultRecieveDelivery(0);
                        this.partnerAddressMapper.updateById(partnerAddress);
                        break;
                    }
                }
            }
        }
        return this.partnerAddressService.save(entity);
    }

    public boolean setAddressStatus(PartnerAddressEO entity) {
        return this.partnerAddressService.updateById(entity);
    }

    public boolean setContactStatus(PartnerContactEO entity) {
        return this.partnerContactService.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addOrUpdateByExternal(SupplierEO entity) throws BusinessException {
        Result result = new Result();
        SupplierEO supplier1 = this.baseMapper.getBySupplierCode(entity.getSupplierCode());
        if(supplier1 != null) {  // 已存在于数据库,更新数据
            //供应商名称不允许重复
            SupplierEO supplier2 = this.baseMapper.selectByName(entity.getSupplierName());
            if(supplier2!=null && !supplier2.getSupplierCode().equals(entity.getSupplierCode())) {
                throw new BusinessException("供应商名称已存在!");
            } else {
                boolean updateUserFlag = true;
                // 供应商关闭,账号禁用保持一致,供应商启用可自行去调用设置账号接口
                if(entity.getStatus() != null) {
                    if(entity.getStatus() == 0) {
                        entity.setAccountStatus(entity.getStatus());
                    }
                }
                UserEO user = this.userService.queryByUserName(entity.getSupplierCode());
                if(user != null) {
                    if(entity.getShortName()!=null && !"".equals(entity.getShortName().trim())) {
                        user.setRealName(entity.getShortName());
                    } else {
                        user.setRealName(entity.getSupplierName());
                    }
                    if(entity.getStatus() == 0) {
                        user.setStatus(entity.getAccountStatus());
                    }
                    updateUserFlag = this.userService.updateById(user);
                }
                entity.setOrgId(Long.valueOf(1));
                entity.setSupplierId(supplier1.getSupplierId());
                boolean updateSupplierFlag = super.updateById(entity);
                if(updateUserFlag && updateSupplierFlag) {
                    result.setMsg("已更新!");
                } else {
                    throw new BusinessException("更新失败!");
                }
            }
        } else { // 不存在于数据库,新建数据
            entity.setStatus(1);
            entity.setAccountStatus(0);
            entity.setOrgId(Long.valueOf(1));
            boolean addSupplierFlag = super.save(entity);
            if(addSupplierFlag) {
                result.setMsg("已创建!");
            } else {
                throw new BusinessException("创建失败!");
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result setAccountByExternal(SupplierEO entity)  throws BusinessException {
        QueryWrapper<RoleEO> wrapper = new QueryWrapper<>();
        wrapper.eq("role_code", "gysRole");
        RoleEO role = this.roleService.getOne(wrapper);
        if(role == null) {
            throw new BusinessException("供应商角色不存在!");
        }

        Result result = new Result();
        SupplierEO supplier = this.baseMapper.getBySupplierCode(entity.getSupplierCode());
        if(supplier == null) {
            throw new BusinessException("数据库不存在供应商编码【" + entity.getSupplierCode() + "】!");
        } else {
//            entity.setStatus(supplier.getAccountStatus());
            int updateSupplierInt = this.baseMapper.updateAccountStatusById(supplier.getSupplierId(), supplier.getAccountStatus());
            UserEO user = this.userService.queryByUserName(entity.getSupplierCode());
            if(entity.getAccountStatus().intValue() == 1) {  // 开通供应商账号
                if(user == null) {
                    user = new UserEO();
                    user.setUserName(entity.getSupplierCode());
                    if(supplier.getShortName()!=null && !"".equals(supplier.getShortName().trim())) {
                        user.setRealName(supplier.getShortName());
                    } else {
                        user.setRealName(supplier.getSupplierName());
                    }
                    user.setPassword(entity.getPassword());
                    user.setPasswordPlaintext(entity.getPassword());
                    user.setCheckPassword(entity.getPassword());
                    List<Long> list = new ArrayList<>();
                    list.add(role.getRoleId());
                    user.setRoleIds(list);
                    user.setUserType(2);
                    user.setStatus(1);
                    user.setSuperAdmin(0);
                    boolean saveUserFlag = this.userService.save(user);
                    if(saveUserFlag && updateSupplierInt>0) {
                        result.setMsg("账号已开通!");
                    } else {
                        throw new BusinessException("账号开通失败!");
                    }
                } else {
                    user.setStatus(1);
                    List<Long> list = user.getRoleIds();
                    if(list != null) {
                        if(!list.contains(role.getRoleId())) {
                            list.add(role.getRoleId());
                        }
                    } else {
                        list = new ArrayList<>();
                        list.add(role.getRoleId());
                    }
                    if(entity.getPassword()!=null && !"".equals(entity.getPassword().trim())) {
                        user.setPasswordPlaintext(entity.getPassword());
                        user.setCheckPassword(entity.getPassword());
                        //sha256加密
                        String salt = RandomStringUtils.randomAlphanumeric(20);
                        user.setSalt(salt);
                        user.setPassword(new Sha256Hash(entity.getPassword(), salt).toHex());
                    }

                    user.setRoleIds(list);
                    boolean updateUserFlag = this.userService.updateById(user);
                    if(updateUserFlag && updateSupplierInt>0) {
                        result.setMsg("账号已开通!");
                    } else {
                        throw new BusinessException("账号开通失败!");
                    }
                }
            } else { // 关闭供应商账号
                if(user == null) {
                    throw new BusinessException("账号不存在!");
                } else {
                    user.setStatus(0);
                    List<Long> list = user.getRoleIds();
                    if(list == null) {
                        list = new ArrayList<>();
                        list.add(role.getRoleId());
                    } else {
                        if(!list.contains(role.getRoleId())) {
                            list.add(role.getRoleId());
                        }
                    }
                    user.setRoleIds(list);
                    boolean updateUserFlag = this.userService.updateById(user);
                    if(updateUserFlag && updateSupplierInt>0) {
                        result.setMsg("账号已关闭!");
                    } else {
                        throw new BusinessException("账号关闭失败!");
                    }
                }
            }
        }

        return result;
    }

    public List<SupplierEO>  getSupplierByCode(String code){
        return this.baseMapper.getSupplierByCode(code);
    }

    public SupplierEO getBySupplierName(String supplierName) {
        return this.baseMapper.getBySupplierName(supplierName);
    }
}
