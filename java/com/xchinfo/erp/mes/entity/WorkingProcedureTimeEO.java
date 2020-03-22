package com.xchinfo.erp.mes.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author roman.c
 */
@TableName("mes_working_procedure_time")
@KeySequence("mes_working_procedure_time")
public class WorkingProcedureTimeEO extends AbstractAuditableEntity<WorkingProcedureTimeEO> {
    private static final long serialVersionUID = -2572410235815688266L;
    @TableId(type = IdType.INPUT)
    private Long workingProcedureTimeId;/** 工序工时ID */

    @TableField(exist = false)
    private String orgName;/** 所属机构 */

    private Long orgId;/** 所属机构id */

    @NotNull(message = "项目不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "项目长度不能超过 200 个字符")
    private String project;/** 项目 */

    private Long materialId;/**  物料ID */

    //@NotNull(message = "产品编号不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String materialCode;/** 物料编号 */

    private String elementNo;/** 零件号 */

    private String materialName;/** 零部件名称 */

    private String workingProcedureCode;/** 工序号 */

    @NotNull(message = "工序名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "工序名称长度不能超过 200 个字符")
    private String workingProcedureName;/** 工序名称 */

    @NotNull(message = "工序类型不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long workingProcedureType;/**  工序类型 */

    private String workingProcedureTypeName;/** 工序类型名称 */

    @TableField(exist = false)
    private String preffix;/** 工序类型号前缀 */

    private Long workshopId;/**  生产车间id */

    private String workshopName;/** 生产车间名称 */

    private Long machineId;/**  设备ID */

    //@NotNull(message = "生产设备不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String machineCode;/**  设备编号 */

    private String capitalAssetsNo;/** 设备固定资产编号 */

    private String machineName;/** 设备名称 */

    @NotNull(message = "人员性质不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private Long menType;/**  人员性质 */

    private String menTypeName;/** 人员性质名称 */

    private Long operationNumber = 1L; /**  操作人数 */

    private Long lfNumber = 1L; /**  辆份数量 */

    private Long mqNumber = 1L; /**  模腔数 */

    @NotNull(message = "现有CT(S)不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal ct; /**  现有CT(S) */

    private BigDecimal ctPer; /**  单件CT(S) */

    private Long status; /**  状态;0-启用;1-停用 */

    private Long isLastProcedure; /**  是否最后一道工序0:否 ,1：是 */

    private String customStringField1;/** 自定义字符1 */

    private String customStringField2;/** 自定义字符2 */

    private String customStringField3;/** 自定义字符3 */

    private String customStringField4;/** 自定义字符4 */

    private String customStringField5;/** 自定义字符5 */

    @Override
    public Serializable getId() {
        return this.workingProcedureTimeId;
    }

    public Long getWorkingProcedureTimeId() {
        return workingProcedureTimeId;
    }

    public void setWorkingProcedureTimeId(Long workingProcedureTimeId) {
        this.workingProcedureTimeId = workingProcedureTimeId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getWorkingProcedureCode() {
        return workingProcedureCode;
    }

    public void setWorkingProcedureCode(String workingProcedureCode) {
        this.workingProcedureCode = workingProcedureCode;
    }

    public String getWorkingProcedureName() {
        return workingProcedureName;
    }

    public void setWorkingProcedureName(String workingProcedureName) {
        this.workingProcedureName = workingProcedureName;
    }

    public Long getWorkingProcedureType() {
        return workingProcedureType;
    }

    public void setWorkingProcedureType(Long workingProcedureType) {
        this.workingProcedureType = workingProcedureType;
    }

    public String getWorkingProcedureTypeName() {
        return workingProcedureTypeName;
    }

    public void setWorkingProcedureTypeName(String workingProcedureTypeName) {
        this.workingProcedureTypeName = workingProcedureTypeName;
    }

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getCapitalAssetsNo() {
        return capitalAssetsNo;
    }

    public void setCapitalAssetsNo(String capitalAssetsNo) {
        this.capitalAssetsNo = capitalAssetsNo;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Long getMenType() {
        return menType;
    }

    public void setMenType(Long menType) {
        this.menType = menType;
    }

    public String getMenTypeName() {
        return menTypeName;
    }

    public void setMenTypeName(String menTypeName) {
        this.menTypeName = menTypeName;
    }

    public Long getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(Long operationNumber) {
        this.operationNumber = operationNumber;
    }

    public Long getLfNumber() {
        return lfNumber;
    }

    public void setLfNumber(Long lfNumber) {
        this.lfNumber = lfNumber;
    }

    public Long getMqNumber() {
        return mqNumber;
    }

    public void setMqNumber(Long mqNumber) {
        this.mqNumber = mqNumber;
    }

    public BigDecimal getCt() {
        return ct;
    }

    public void setCt(BigDecimal ct) {
        this.ct = ct;
    }

    public BigDecimal getCtPer() {
        return ctPer;
    }

    public void setCtPer(BigDecimal ctPer) {
        this.ctPer = ctPer;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getPreffix() {
        return preffix;
    }

    public void setPreffix(String preffix) {
        this.preffix = preffix;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getIsLastProcedure() {
        return isLastProcedure;
    }

    public void setIsLastProcedure(Long isLastProcedure) {
        this.isLastProcedure = isLastProcedure;
    }

    public String getCustomStringField1() {
        return customStringField1;
    }

    public void setCustomStringField1(String customStringField1) {
        this.customStringField1 = customStringField1;
    }

    public String getCustomStringField2() {
        return customStringField2;
    }

    public void setCustomStringField2(String customStringField2) {
        this.customStringField2 = customStringField2;
    }

    public String getCustomStringField3() {
        return customStringField3;
    }

    public void setCustomStringField3(String customStringField3) {
        this.customStringField3 = customStringField3;
    }

    public String getCustomStringField4() {
        return customStringField4;
    }

    public void setCustomStringField4(String customStringField4) {
        this.customStringField4 = customStringField4;
    }

    public String getCustomStringField5() {
        return customStringField5;
    }

    public void setCustomStringField5(String customStringField5) {
        this.customStringField5 = customStringField5;
    }
}
