package com.xchinfo.erp.hrms.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@TableName("hr_employee")
@KeySequence("hr_employee")
public class EmployeeEO extends AbstractAuditableEntity<EmployeeEO> {

    private static final long serialVersionUID = -3513311737148653100L;

    @TableId(type = IdType.INPUT)
    private Long employeeId;/** 员工ID */

    @NotBlank(message = "工号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "工号长度不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("工号")
    private String employeeNo;/** 员工编号 */

    @NotBlank(message = "姓名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "姓名长度不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("姓名")
    private String employeeName;/** 员工姓名 */

    private Long orgId;/** 机构 */

    @TableField(exist = false)
    @NotBlank(message = "机构不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("机构")
    private String orgName;/** 机构名称 */
    @TableField(exist = false)
    @BusinessLogField("公司")
    private String rootOrgName;/** 公司名称 */
    private Long positionId;/** 岗位 */
    private Long rootOrgId;/** 公司id */
    @TableField(exist = false)
    @NotBlank(message = "岗位不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("岗位")
    private String positionName;

    @NotBlank(message = "性别不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("性别")
    private String gender;/** 性别 */

    @BusinessLogField("雇佣类型")
    private String employType;/** 雇佣类型 */

    private String contractType;/** 用工形式: 正式工,劳务工 */

    @BusinessLogField("出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;/** 出生日期 */

    @BusinessLogField("国籍")
    private String nationality;/** 国籍 */

    @BusinessLogField("籍贯")
    private String homeTown;/** 籍贯 */

    @BusinessLogField("民族")
    private String nation;/** 民族 */

    @BusinessLogField("户口所在地")
    @Length(max = 1024, message = "户口所在地长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String registeredResidence;/** 户口所在地 */

    @BusinessLogField("政治面貌")
    private String politicalType;/** 政治面貌 */

    @BusinessLogField("婚姻状态")
    private String maritalStatus;/** 婚姻状态 */

    @BusinessLogField("入职日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointDate;/** 入职日期 */

    @BusinessLogField("试用期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date probationPeriod;/** 试用期 */

    @BusinessLogField("转正时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date obtainmentDate;/** 转正时间 */

    @BusinessLogField("离职时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leavingDate;/** 离职时间 */

    @BusinessLogField("开始工作时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startWorkDate;/** 开始工作时间 */

    @BusinessLogField("是否关键员工")
    private boolean isKeyEmp;/** 是否关键员工;0-否；1-是 */

    @BusinessLogField("手机号码")
    @Length(max = 100, message = "手机号码长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;/** 手机号码 */

    @BusinessLogField("办公电话")
    @Length(max = 100, message = "办公电话长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String officeTelephone;/** 办公电话 */

    @BusinessLogField("电子邮件")
    @Length(max = 100, message = "电子邮件长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String email;/** 电子邮件 */

    @BusinessLogField("家庭电话")
    @Length(max = 100, message = "家庭电话长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String familyTelephone;/** 家庭电话 */

    @BusinessLogField("家庭地址")
    @Length(max = 1024, message = "家庭电话长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String familyAddress;/** 家庭地址 */

    @BusinessLogField("邮政编码")
    @Length(max = 100, message = "邮政编码长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String zipCode;/** 邮政编码 */

    @BusinessLogField("紧急联系人")
    @Length(max = 100, message = "紧急联系人长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String emergencyContact;/** 紧急联系人 */

    @BusinessLogField("紧急联系电话")
    @Length(max = 200, message = "紧急联系电话长度不能超过200", groups = {AddGroup.class, UpdateGroup.class})
    private String emergencyPhone;/** 紧急联系电话 */

    @BusinessLogField("合同签订日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractSigningDate;/** 合同签订日 */

    @BusinessLogField("合同起始日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractStartDate;/** 合同起始日 */

    @BusinessLogField("合同到期日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDate;/** 合同到期日 */

    @BusinessLogField("状态")
    private Integer status;/** 状态;0-新建；1-在职；0-离职 */
    @BusinessLogField("在职状态")
    private Integer inservice;//'在职状态: 0, 未设置 1,在职, 2 离职'
    @Length(max = 1024, message = "自定义字符1长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String customStringField1;/** 自定义字符1 */

    @Length(max = 1024, message = "自定义字符2长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String customStringField2;/** 自定义字符2 */

    @Length(max = 1024, message = "自定义字符3长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String customStringField3;/** 自定义字符3 */

    @Length(max = 1024, message = "自定义字符4长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String customStringField4;/** 自定义字符4 */

    @Length(max = 1024, message = "自定义字符5长度不能超过1024", groups = {AddGroup.class, UpdateGroup.class})
    private String customStringField5;/** 自定义字符5 */

    private Double customNumField1;/** 自定义数值1 */

    private Double customNumField2;/** 自定义数值2 */

    private Double customNumField3;/** 自定义数值3 */

    private Double customNumField4;/** 自定义数值4 */

    private Double customNumField5;/** 自定义数值5 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customDateField1;/** 自定义日期1 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customDateField2;/** 自定义日期2 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customDateField3;/** 自定义日期3 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customDateField4;/** 自定义日期4 */

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customDateField5;/** 自定义日期5 */

    @TableField(exist = false)
    @BusinessLogField("在职信息")
    private List<EmployeePositionEO> positions = new ArrayList<>();/** 在职信息 */

    @TableField(exist = false)
    @BusinessLogField("工作经历")
    private List<EmployeeExperienceEO> experiences = new ArrayList<>();/** 工作经历 */

    @TableField(exist = false)
    @BusinessLogField("教育经历")
    private List<EmployeeEducationEO> educations = new ArrayList<>();/** 教育经历 */

    @TableField(exist = false)
    @BusinessLogField("家庭")
    private List<EmployeeFamilyEO> familys = new ArrayList<>();/** 家庭 */

    @TableField(exist = false)
    @BusinessLogField("语言")
    private List<EmployeeLanguageEO> languages = new ArrayList<>();/** 语言 */

    @TableField(exist = false)
    @BusinessLogField("技能")
    private List<EmployeeSkillEO> skills = new ArrayList<>();/** 技能 */

    @TableField(exist = false)
    @BusinessLogField("培训")
    private List<EmployeeTrainingEO> trainings = new ArrayList<>();/** 培训 */

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public Long getPositionId() {
        return positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmployType() {
        return employType;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public String getNation() {
        return nation;
    }

    public String getRootOrgName() {
        return rootOrgName;
    }

    public void setRootOrgName(String rootOrgName) {
        this.rootOrgName = rootOrgName;
    }

    public String getRegisteredResidence() {
        return registeredResidence;
    }

    public String getPoliticalType() {
        return politicalType;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public Date getAppointDate() {
        return appointDate;
    }

    public Date getProbationPeriod() {
        return probationPeriod;
    }

    public Date getObtainmentDate() {
        return obtainmentDate;
    }

    public Date getLeavingDate() {
        return leavingDate;
    }

    public Date getStartWorkDate() {
        return startWorkDate;
    }

    public boolean getIsKeyEmp() {
        return isKeyEmp;
    }

    public String getPhone() {
        return phone;
    }

    public String getOfficeTelephone() {
        return officeTelephone;
    }

    public String getEmail() {
        return email;
    }

    public String getFamilyTelephone() {
        return familyTelephone;
    }

    public String getFamilyAddress() {
        return familyAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public Date getContractSigningDate() {
        return contractSigningDate;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public Integer getStatus() {
        return status;
    }

    public String getCustomStringField1() {
        return customStringField1;
    }

    public String getCustomStringField2() {
        return customStringField2;
    }

    public String getCustomStringField3() {
        return customStringField3;
    }

    public String getCustomStringField4() {
        return customStringField4;
    }

    public String getCustomStringField5() {
        return customStringField5;
    }

    public Double getCustomNumField1() {
        return customNumField1;
    }

    public Double getCustomNumField2() {
        return customNumField2;
    }

    public Double getCustomNumField3() {
        return customNumField3;
    }

    public Double getCustomNumField4() {
        return customNumField4;
    }

    public Double getCustomNumField5() {
        return customNumField5;
    }

    public Date getCustomDateField1() {
        return customDateField1;
    }

    public Date getCustomDateField2() {
        return customDateField2;
    }

    public Date getCustomDateField3() {
        return customDateField3;
    }

    public Date getCustomDateField4() {
        return customDateField4;
    }

    public Date getCustomDateField5() {
        return customDateField5;
    }

    public List<EmployeePositionEO> getPositions() {
        return positions;
    }

    public List<EmployeeExperienceEO> getExperiences() {
        return experiences;
    }

    public List<EmployeeEducationEO> getEducations() {
        return educations;
    }

    public List<EmployeeFamilyEO> getFamilys() {
        return familys;
    }

    public List<EmployeeLanguageEO> getLanguages() {
        return languages;
    }

    public List<EmployeeSkillEO> getSkills() {
        return skills;
    }

    public List<EmployeeTrainingEO> getTrainings() {
        return trainings;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmployType(String employType) {
        this.employType = employType;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setRegisteredResidence(String registeredResidence) {
        this.registeredResidence = registeredResidence;
    }

    public void setPoliticalType(String politicalType) {
        this.politicalType = politicalType;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setAppointDate(Date appointDate) {
        this.appointDate = appointDate;
    }

    public void setProbationPeriod(Date probationPeriod) {
        this.probationPeriod = probationPeriod;
    }

    public void setObtainmentDate(Date obtainmentDate) {
        this.obtainmentDate = obtainmentDate;
    }

    public void setLeavingDate(Date leavingDate) {
        this.leavingDate = leavingDate;
    }

    public void setStartWorkDate(Date startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    public void setIsKeyEmp(boolean isKeyEmp) {
        this.isKeyEmp = isKeyEmp;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOfficeTelephone(String officeTelephone) {
        this.officeTelephone = officeTelephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFamilyTelephone(String familyTelephone) {
        this.familyTelephone = familyTelephone;
    }

    public void setFamilyAddress(String familyAddress) {
        this.familyAddress = familyAddress;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public void setContractSigningDate(Date contractSigningDate) {
        this.contractSigningDate = contractSigningDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCustomStringField1(String customStringField1) {
        this.customStringField1 = customStringField1;
    }

    public void setCustomStringField2(String customStringField2) {
        this.customStringField2 = customStringField2;
    }

    public void setCustomStringField3(String customStringField3) {
        this.customStringField3 = customStringField3;
    }

    public void setCustomStringField4(String customStringField4) {
        this.customStringField4 = customStringField4;
    }

    public void setCustomStringField5(String customStringField5) {
        this.customStringField5 = customStringField5;
    }

    public void setCustomNumField1(Double customNumField1) {
        this.customNumField1 = customNumField1;
    }

    public void setCustomNumField2(Double customNumField2) {
        this.customNumField2 = customNumField2;
    }

    public void setCustomNumField3(Double customNumField3) {
        this.customNumField3 = customNumField3;
    }

    public void setCustomNumField4(Double customNumField4) {
        this.customNumField4 = customNumField4;
    }

    public void setCustomNumField5(Double customNumField5) {
        this.customNumField5 = customNumField5;
    }

    public void setCustomDateField1(Date customDateField1) {
        this.customDateField1 = customDateField1;
    }

    public void setCustomDateField2(Date customDateField2) {
        this.customDateField2 = customDateField2;
    }

    public void setCustomDateField3(Date customDateField3) {
        this.customDateField3 = customDateField3;
    }

    public void setCustomDateField4(Date customDateField4) {
        this.customDateField4 = customDateField4;
    }

    public void setCustomDateField5(Date customDateField5) {
        this.customDateField5 = customDateField5;
    }

    public void setPositions(List<EmployeePositionEO> positions) {
        this.positions = positions;
    }

    public void setExperiences(List<EmployeeExperienceEO> experiences) {
        this.experiences = experiences;
    }

    public void setEducations(List<EmployeeEducationEO> educations) {
        this.educations = educations;
    }

    public void setFamilys(List<EmployeeFamilyEO> familys) {
        this.familys = familys;
    }

    public void setLanguages(List<EmployeeLanguageEO> languages) {
        this.languages = languages;
    }

    public void setSkills(List<EmployeeSkillEO> skills) {
        this.skills = skills;
    }

    public void setTrainings(List<EmployeeTrainingEO> trainings) {
        this.trainings = trainings;
    }

    public Integer getInservice() {
        return inservice;
    }

    public void setInservice(Integer inservice) {
        this.inservice = inservice;
    }

    public Long getRootOrgId() {
        return rootOrgId;
    }

    public void setRootOrgId(Long rootOrgId) {
        this.rootOrgId = rootOrgId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    @Override
    public Serializable getId() {
        return this.employeeId;
    }
}
