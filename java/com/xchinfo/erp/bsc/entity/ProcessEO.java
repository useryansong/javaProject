package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.AuditableTreeNode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_process")
@KeySequence("bsc_process")
public class ProcessEO extends AuditableTreeNode<ProcessEO> {
    private static final long serialVersionUID = -596170869063744329L;

    @TableId(type = IdType.INPUT)
    private Long processId;/** 主键 */

    private Long materialId;/** 物料 */

    @TableField(exist = false)
    private String materialCode;/** 物料编码 */

    @TableField(exist = false)
    private String materialName;/** 物料名称 */

    private String processCode;/** 工序编码 */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("工序名称")
    private String processName;/** 工序名称 */

    private Long parentProcess;/** 父工序 */

    @TableField(exist = false)
    private String parentProcessCode;/** 父工序编码*/

    @TableField(exist = false)
    private String parentProcessName;/** 父工序名称*/

    private Integer prepareTime;/** 准备时间 */

    private Integer maxWaitingTime;/** 最大等待时间 */

    private Integer minWaitingTime;/** 最小等待时间 */

    private Long defaultMachineId;/** 默认设备 */

    @TableField(exist = false)
    private String defaultMachineName;/** 默认设备名称 */

    private Integer operatorNumber;/** 操作人数 */

    private Integer lotAmount;/** 批加工数量 */

    private Integer durationTime;/** 加工时长 */

    @NotNull(message = "状态不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("状态")
    private Integer status;/** 状态;0-无效;1-有效 */

    private Integer duration;/** 加工时间 */

    @NotNull(message = "加工难度不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("加工难度")
    private Integer productLevel;/** 加工难度;1-最低；5-最高 */

    @NotNull(message = "转运方式不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("转运方式")
    private Integer transferType;/** 转运方式;1-整批;2-即时;3-延迟 */

    @Length(max = 200, message = "转运间隔长度不能超过 200 个字符")
    @BusinessLogField("转运间隔")
    private Integer transferTime;/** 转运间隔;延迟转运时使用 */

    @TableField(exist = false)
    private List<ProcessMachineEO> machines;/** 可用机器 */

    @TableField(exist = false)
    private List<ProcessMaterialEO> materials;/** 所需物料 */

    @TableField(exist = false)
    private List<ProcessToolEO> tools;/** 可用工具 */

    @TableField(exist = false)
    private List<ProcessSkillEO> skills;/** 所需技能 */

    @TableField(exist = false)
    private List<ProcessConstraintEO> constraints;/** 约束条件*/

    @Override
    public Long getId() {
        return this.processId;
    }

    /** 主键 */
    public Long getProcessId(){
        return this.processId;
    }
    /** 主键 */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 工序编码 */
    public String getProcessCode(){
        return this.processCode;
    }
    /** 工序编码 */
    public void setProcessCode(String processCode){
        this.processCode = processCode;
    }
    /** 工序名称 */
    public String getProcessName(){
        return this.processName;
    }
    /** 工序名称 */
    public void setProcessName(String processName){
        this.processName = processName;
    }
    /** 父工序 */
    public Long getParentProcess(){
        return this.parentProcess;
    }
    /** 父工序 */
    public void setParentProcess(Long parentProcess){
        this.parentProcess = parentProcess;
    }
    /** 准备时间 */
    public Integer getPrepareTime(){
        return this.prepareTime;
    }
    /** 准备时间 */
    public void setPrepareTime(Integer prepareTime){
        this.prepareTime = prepareTime;
    }
    /** 最大等待时间 */
    public Integer getMaxWaitingTime(){
        return this.maxWaitingTime;
    }
    /** 最大等待时间 */
    public void setMaxWaitingTime(Integer maxWaitingTime){
        this.maxWaitingTime = maxWaitingTime;
    }
    /** 最小等待时间 */
    public Integer getMinWaitingTime(){
        return this.minWaitingTime;
    }
    /** 最小等待时间 */
    public void setMinWaitingTime(Integer minWaitingTime){
        this.minWaitingTime = minWaitingTime;
    }
    /** 状态;0-无效;1-有效 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-无效;1-有效 */
    public void setStatus(Integer status){
        this.status = status;
    }
    /** 加工时间 */
    public Integer getDuration() {
        return duration;
    }
    /** 加工时间 */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    /** 加工难度;1-最低；5-最高 */
    public Integer getProductLevel(){
        return this.productLevel;
    }
    /** 加工难度;1-最低；5-最高 */
    public void setProductLevel(Integer productLevel){
        this.productLevel = productLevel;
    }
    /** 转运方式;1-整批;2-即时;3-延迟 */
    public Integer getTransferType(){
        return this.transferType;
    }
    /** 转运方式;1-整批;2-即时;3-延迟 */
    public void setTransferType(Integer transferType){
        this.transferType = transferType;
    }
    /** 转运间隔;延迟转运时使用 */
    public Integer getTransferTime(){
        return this.transferTime;
    }
    /** 转运间隔;延迟转运时使用 */
    public void setTransferTime(Integer transferTime){
        this.transferTime = transferTime;
    }

    public List<ProcessMachineEO> getMachines() {
        return machines;
    }

    public List<ProcessMaterialEO> getMaterials() {
        return materials;
    }

    public List<ProcessToolEO> getTools() {
        return tools;
    }

    public List<ProcessSkillEO> getSkills() {
        return skills;
    }

    public void setMachines(List<ProcessMachineEO> machines) {
        this.machines = machines;
    }

    public void setMaterials(List<ProcessMaterialEO> materials) {
        this.materials = materials;
    }

    public void setTools(List<ProcessToolEO> tools) {
        this.tools = tools;
    }

    public void setSkills(List<ProcessSkillEO> skills) {
        this.skills = skills;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getParentProcessCode() {
        return parentProcessCode;
    }

    public void setParentProcessCode(String parentProcessCode) {
        this.parentProcessCode = parentProcessCode;
    }

    public String getParentProcessName() {
        return parentProcessName;
    }

    public void setParentProcessName(String parentProcessName) {
        this.parentProcessName = parentProcessName;
    }

    public List<ProcessConstraintEO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ProcessConstraintEO> constraints) {
        this.constraints = constraints;
    }

    public Long getDefaultMachineId() {
        return defaultMachineId;
    }

    public void setDefaultMachineId(Long defaultMachineId) {
        this.defaultMachineId = defaultMachineId;
    }

    public String getDefaultMachineName() {
        return defaultMachineName;
    }

    public void setDefaultMachineName(String defaultMachineName) {
        this.defaultMachineName = defaultMachineName;
    }

    public Integer getOperatorNumber() {
        return operatorNumber;
    }

    public void setOperatorNumber(Integer operatorNumber) {
        this.operatorNumber = operatorNumber;
    }

    public Integer getLotAmount() {
        return lotAmount;
    }

    public void setLotAmount(Integer lotAmount) {
        this.lotAmount = lotAmount;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    @Override
    public Long getPid() {
        return this.parentProcess;
    }
}
