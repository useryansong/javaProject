package com.xchinfo.erp.sys.auth.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户实体
 *
 * @author roman.li
 * @date 2017/10/9
 * @update
 */
@TableName("sys_user")
@KeySequence("sys_user")
public class UserEO extends AbstractAuditableEntity<UserEO> {

    private static final long serialVersionUID = -7571268178020333833L;

    @TableId(type = IdType.INPUT)
    private Long userId;//用户ID

    @NotBlank(message = "姓名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "姓名长度不能超过 200 个字符")
    @BusinessLogField("姓名")
    private String realName;//姓名

    @TableField("user_name")
    @NotBlank(message = "用户名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "用户名长度不能超过 200 个字符")
    @BusinessLogField("用户名")
    private String userName;//用户名

    @BusinessLogField("用户类型")
    private Integer userType;//用户类型：1-内部用户；2-供应商用户；3-客户用户

    private Long orgId; // 机构ID

    private Long lastOrgId;/** 部门 */

    private Long innerOrg;/** 内部机构（供应商） */

    @TableField(exist = false)
    private String innerOrgName; // 内部机构名称

    @TableField(exist = false)
    @BusinessLogField("机构")
    private String orgName; // 机构名称

    @TableField(exist = false)
    private String orgCode;// 机构编码

    private String gender;// 性别：1-男；2-女；3-保密

    @NotBlank(message = "密码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(min = 6, max = 1000, message = "密码长度在 6 到 1000 个字符")
    private String password;//密码

    private String passwordPlaintext;/** 密码明文 */

    @TableField(exist = false)
    private String checkPassword;// 校验密码

    private String salt;//密码串

    @TableField("password_change_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date passwordChangeDate;//密码修改时间

//    @NotBlank(message = "电子邮件不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "电子邮件长度不能超过 200 个字符")
    @BusinessLogField("电子邮件")
    private String email;//电子邮件

    @Length(max = 200, message = "手机号码长度不能超过 200 个字符")
    @BusinessLogField("手机")
    private String mobile;//手机

    @BusinessLogField("状态")
    private Integer status;//状态：0-无效；1-有效；2-挂起

    private Integer superAdmin;// 是否超级管理员;0-否；1-是

    private String avatar;//用户头像

    @Length(max = 1000, message = "备注长度不能超过 1000 个字符")
    private String remarks;//备注

    @NotEmpty(message = "请选择角色!")
    @TableField(exist = false)
    private List<Long> roleIds;// 用户角色

    @TableField(exist = false)
    private String roleNames;// 用户角色名称

    @TableField(exist = false)
    private List<Long> orgIds;// 用户机构列表

    @TableField(exist = false)
    private List<Long> userOrgsData;// 用户机构列表(存在sys_user_org表中的数据)

    @TableField(exist = false)
    private Set<String> permisions;// 用户权限

    private Long supplierId; /** 供应商ID */


    public Long getUserId() {
        return userId;
    }

    public String getRealName() {
        return realName;
    }

    public String getUserName() {
        return userName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public String getSalt() {
        return salt;
    }

    public Date getPasswordChangeDate() {
        return passwordChangeDate;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRemarks() {
        return remarks;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public List<Long> getOrgIds() {
        return orgIds;
    }

    public Set<String> getPermisions() {
        return permisions;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setPasswordChangeDate(Date passwordChangeDate) {
        this.passwordChangeDate = passwordChangeDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public void setOrgIds(List<Long> orgIds) {
        this.orgIds = orgIds;
    }

    public void setPermisions(Set<String> permisions) {
        this.permisions = permisions;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    @Override
    public Serializable getId() {
        return this.userId;
    }
    /** 密码明文 */
    public String getPasswordPlaintext(){
        return this.passwordPlaintext;
    }
    /** 密码明文 */
    public void setPasswordPlaintext(String passwordPlaintext){
        this.passwordPlaintext = passwordPlaintext;
    }

    public Long getLastOrgId() {
        return this.lastOrgId;
    }

    public void setLastOrgId(Long lastOrgId) {
        this.lastOrgId = lastOrgId;
    }

    public String getRoleNames() {
        return this.roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public Long getInnerOrg() {
        return innerOrg;
    }

    public void setInnerOrg(Long innerOrg) {
        this.innerOrg = innerOrg;
    }

    public String getInnerOrgName() {
        return innerOrgName;
    }

    public void setInnerOrgName(String innerOrgName) {
        this.innerOrgName = innerOrgName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public List<Long> getUserOrgsData() {
        return userOrgsData;
    }

    public void setUserOrgsData(List<Long> userOrgsData) {
        this.userOrgsData = userOrgsData;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}
