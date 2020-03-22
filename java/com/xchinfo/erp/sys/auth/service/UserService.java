package com.xchinfo.erp.sys.auth.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.sys.auth.entity.UserTokenEO;
import com.xchinfo.erp.sys.auth.mapper.UserTokenMapper;
import com.xchinfo.erp.sys.conf.entity.EmailSendEO;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import com.xchinfo.erp.sys.conf.service.EmailService;
import com.xchinfo.erp.sys.conf.service.EmailTemplateService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.utils.StringUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.mapper.UserMapper;
import java.io.Serializable;
import java.util.*;

/**
 * @author Yansong Shi
 * @date 2017/10/9
 * @update
 */
@Service
public class UserService extends BaseServiceImpl<UserMapper, UserEO> {

    //12小时后过期
    private final static int EXPIRE = 3600 * 12;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    @Lazy
    private SupplierService supplierService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    public UserEO queryByUserName(String userName) {
        logger.info("======== UserService.queryByUserName(userName => "+userName+") ========");

        return baseMapper.queryByUserName(userName);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) throws BusinessException {
        logger.info("======== UserService.updateUserStatus() ========");

        UserEO user = this.getById(userId);
        user.setStatus(status);
        this.updateById(user);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map createToken(Long userId,Integer loginType) {
        logger.info("======== UserService.createToken(userId -> "+userId+") ========");

        // 根据随机数生成Token
        String token = TokenGenerator.generateValue();

        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expireDate = new Date(now.getTime() + EXPIRE * 1000);

        // 判断当前用户是否生成过Token
        List<UserTokenEO> userTokens = this.userTokenMapper.queryByUserId(userId,loginType);
        logger.info("======== ============="+loginType);

        if (null == userTokens||userTokens.size()==0){
            UserTokenEO userToken = new UserTokenEO();

            userToken.setLoginType(loginType);
            userToken.setUserId(userId);
            userToken.setToken(token);
            userToken.setExpireTime(expireDate);
            userToken.setUpdateTime(now);

            // 保存Token
            userToken.insert();
        } else {
            UserTokenEO userToken = userTokens.get(0);
            userToken.setLoginType(loginType);
            userToken.setToken(token);
            userToken.setExpireTime(expireDate);
            userToken.setUpdateTime(now);

            // 更新Token
            userToken.updateById();
        }

        Map<String, Object> map = new HashMap<>(3);
        map.put("token", token);
        map.put("expire", EXPIRE);

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteToken(Long userId,Integer loginType) {
        logger.info("======== UserService.deleteToken(user -> "+userId+") ========");

        this.userTokenMapper.deleteToken(userId,loginType);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserPassword(UserEO user) {
        // 对用户密码进行加密,sha256加密
//        UserEO currUser = this.getById(user.getUserId());

        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());

//        return this.updateById(currUser);
        return super.updateById(user);
    }

    public UserEO selectByIdWithIdentity(Long userId) {
        // 查询用户信息
        UserEO user = this.baseMapper.selectById(userId);

        // 查询用户角色信息
        List<Long> roleIds = this.baseMapper.selectUserRoleIds(userId);
        user.setRoleIds(roleIds);

        //查询用户权限机构数据
        List<Long> orgData = this.baseMapper.getUserOrgsData(userId);
        user.setUserOrgsData(orgData);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(UserEO user) throws BusinessException {
        // 检查用户名是否被使用
        if (retBool(this.baseMapper.countByUserName(user.getUserName()))){
            throw new BusinessException("用户名已经被使用!");
        }

        // 检查用户输入密码是否一致
        if (!user.getPassword().equals(user.getCheckPassword())){
            throw new BusinessException("两次输入密码不一致!");
        }

        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());

        // 保存用户
        if (!super.save(user))
            throw new BusinessException("用户保存失败!");

        // 保存用户权限
        for (Long roleId : user.getRoleIds()){
            this.baseMapper.insertUserAuth(user.getUserId(), roleId);
        }

        //初始化用户机构权限，用户默认有本机构
        this.baseMapper.insertUserOrg(user.getUserId(),user.getOrgId());

        // 供应商用户，则更新供应商账号开通状态
        if (user.getUserType() == 2 && !retBool(this.baseMapper.updateSupplierAccountStatus(user.getUserName(), 1))){
            throw new BusinessException("更新供应商账户开通状态失败!");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(UserEO user) throws BusinessException {
        // 删除用户权限
        this.baseMapper.deleteUserAuth(user.getUserId());

        // 插入用户权限
        if (null != user.getRoleIds()){
            for (Long roleId : user.getRoleIds()){
                this.baseMapper.insertUserAuth(user.getUserId(), roleId);
            }
        }

        // 更新用户数据
        if (!super.updateById(user))
            throw new BusinessException("用户保存失败!");

        // 供应商用户，则更新供应商账号开通状态
        if (user.getUserType() == 2 && !retBool(this.baseMapper.updateSupplierAccountStatus(user.getUserName(), user.getStatus()))){
            throw new BusinessException("更新供应商账户开通状态失败!");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = UserEO.class)
    public boolean removeByIds(Serializable[] ids) throws BusinessException {
        for (Serializable userId : ids){
            // 检查是不是超级管理员
            UserEO user = this.getById(userId);
            if (user.getSuperAdmin() == 1)
                throw new BusinessException("[" + user.getRealName() + "]超级管理员不能删除!");

            // 删除用户权限
            this.baseMapper.deleteUserAuth((Long) userId);

            // 删除用户
            if (!super.removeById(userId))
                throw new BusinessException("用户删除失败!");

            // 供应商用户，则更新供应商账号开通状态
            if (user.getUserType() == 2 && !retBool(this.baseMapper.updateSupplierAccountStatus(user.getUserName(), 0))){
                throw new BusinessException("更新供应商账户开通状态失败!");
            }
        }

        return true;
    }

    public List<UserEO> listAll() {
        return this.baseMapper.selectAll();
    }

    public boolean checkAuditableInfo(String entity, String idField, Serializable id, String user) {
        return retBool(this.baseMapper.checkAuditableInfo(entity, idField, id, user));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(UserEO user) {
        // 对用户密码进行加密,sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        return super.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean setStatus(UserEO user) {
        return super.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean authorize(UserEO user) {
        UserEO userFromDb = super.getById(user.getUserId());
        userFromDb.setRoleIds(user.getRoleIds());
        // 删除用户权限
        this.baseMapper.deleteUserAuth(userFromDb.getUserId());

        // 插入用户权限
        if (null != userFromDb.getRoleIds()){
            for (Long roleId : userFromDb.getRoleIds()){
                this.baseMapper.insertUserAuth(userFromDb.getUserId(), roleId);
            }
        }

        return super.updateById(userFromDb);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean authData(UserEO user) {
        // 删除用户权限
        this.baseMapper.deleteUserOrg(user.getUserId());

        // 插入用户权限
        if (null != user.getUserOrgsData()){
            for (Long orgId : user.getUserOrgsData()){
                this.baseMapper.insertUserOrg(user.getUserId(), orgId);
            }
        }

        return true;
    }
    public List<Long> selectUserRoleIds(Long userId) {
        return this.baseMapper.selectUserRoleIds(userId);
    }

    public Result<String> sendCheckCode(Map map){
        String username = map.get("username").toString();//用户名
        String yhlb = map.get("yhlb").toString();//用户类别 0内部员工 2 供应商
        Long emailTemplateId = Long.parseLong(map.get("emailTemplateId").toString());//模板id
        //获取用户邮箱信息
        UserEO user = this.queryByUserName(username);
        if(user==null){
            return new Result<String>().error(33,"找不到该用户");
        }
        String email = user.getEmail();
        String realName = user.getRealName();
        if(yhlb.equals("1")){
            List<SupplierEO>  list  = supplierService.getSupplierByCode(username);
            if(list==null||list.size()==0){
                return new Result<String>().error(33,"找不到供应商");
            }
            email = list.get(0).getContactEmail();
            realName=list.get(0).getSupplierName();
        }
        String time = "30";
        if(StringUtils.isEmpty(email)){
            return new Result<String>().error(33,"找不到用户邮箱");
        }
        String checkCode = getSix();
        EmailTemplateEO emailTemplateEO = emailTemplateService.getById(emailTemplateId);
        if(emailTemplateEO==null){
            return new Result<String>().error(33,"找不到邮件模板");
        }
        Map param = new HashMap();
        param.put("name",realName);
        param.put("time",time);
        param.put("checkCode",checkCode);
        param.put("date", DateUtils.getDateStr());
        String content = emailService.templateReplace(emailTemplateEO.getContent(),param);
        EmailSendEO emailSend = new EmailSendEO();
        emailSend.setContent(content);
        emailSend.setReciverEmail(email);
        emailSend.setTitle(emailTemplateEO.getTitle());
        boolean res = emailService.sendHtmlMail(emailSend,emailTemplateEO);
        if(!res){
            return new Result<String>().error(33,"发送验证码到邮箱失败，您的邮箱可能无效");
        }
        return new Result<String>().ok(checkCode);
    }
    /**
     * 重置密码，并发送新密码到邮箱
     *
     * @return
     */
    public Result<String> resetPswd(Map map){
        String username = map.get("username").toString();//用户名
        String yhlb = map.get("yhlb").toString();//用户类别 0内部员工 2 供应商
        Long emailTemplateId = Long.parseLong(map.get("emailTemplateId").toString());//模板id
        //获取用户邮箱信息
        UserEO user = this.queryByUserName(username);
        if(user==null){
            return new Result<String>().error(33,"找不到该用户");
        }
        String email = user.getEmail();
        String realName = user.getRealName();
        if(yhlb.equals("1")){
            List<SupplierEO>  list  = supplierService.getSupplierByCode(username);
            if(list==null||list.size()==0){
                return new Result<String>().error(33,"找不到供应商");
            }
            email = list.get(0).getContactEmail();
            realName=list.get(0).getSupplierName();
        }
        String time = "30";
        if(StringUtils.isEmpty(email)){
            return new Result<String>().error(33,"找不到用户邮箱");
        }
        String newPsd = getSix();
        EmailTemplateEO emailTemplateEO = emailTemplateService.getById(emailTemplateId);
        if(emailTemplateEO==null){
            return new Result<String>().error(33,"找不到邮件模板");
        }
        String oldPsd = user.getPassword();
        String oldSalt = user.getSalt();
        //重置密码
        user.setPassword(newPsd);
        if(!this.resetPassword(user)){
            return new Result<String>().error(33,"重置密码失败，请重试");
        }
        //发送邮件
        Map param = new HashMap();
        param.put("newPsd",newPsd);
        param.put("date", DateUtils.getDateStr());
        String content = emailService.templateReplace(emailTemplateEO.getContent(),param);
        EmailSendEO emailSend = new EmailSendEO();
        emailSend.setContent(content);
        emailSend.setReciverEmail(email);
        emailSend.setTitle(emailTemplateEO.getTitle());
        boolean res = emailService.sendHtmlMail(emailSend,emailTemplateEO);
        if(!res){
            //密码回滚
            user.setPassword(oldPsd);
            user.setSalt(oldSalt);
            this.updateById(user);
            return new Result<String>().error(33,"发送新密码到邮箱失败，您的邮箱可能无效");
        }

        return new Result<String>().ok(email);
    }

    /**
     * 获取六位数字的验证码
     * @return
     */
    private static String getSix(){
        Random rad=new Random();

        String result  = rad.nextInt(1000000) +"";

        if(result.length()!=6){
            return getSix();
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean setInnerOrg(UserEO user) throws BusinessException {

        if(user.getInnerOrg().longValue() ==  -1){
            this.baseMapper.setInnerOrgNull(user.getUserId());
        }else{
            super.updateById(user);
        }

        return true;
    }

}
