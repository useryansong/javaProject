package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xchinfo.erp.annotation.BusinessLogField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_shift")
@KeySequence("bsc_shift")
public class ShiftEO extends AbstractAuditableEntity<ShiftEO> {
    private static final long serialVersionUID = -395056349208339786L;

    @TableId(type = IdType.INPUT)
    private Long shiftId;/** 班次ID */

    @NotBlank(message = "名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "名称长度不能超过 200 个字符")
    @BusinessLogField("班次名称")
    private String shiftName;/** 班次名称 */

    @NotNull(message = "班组不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("班组")
    private Long shiftTypeId;/** 班组 */

    @TableField(exist = false)
    @BusinessLogField("班组名称")
    private String shiftTypeName;/** 班组名称 */

    @NotNull(message = "开始日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("开始日期")
    private Date startDate;/** 开始日期 */

    @NotNull(message = "结束日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("结束日期")
    private Date endDate;/** 结束日期 */

    @NotBlank(message = "开始时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("开始时间")
    private String startTime;/** 开始时间 */

    @NotBlank(message = "结束时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("结束时间")
    private String endTime;/** 结束时间 */

    @Override
    public Serializable getId() {
        return this.shiftId;
    }

    /** 班次ID */
    public Long getShiftId(){
        return this.shiftId;
    }
    /** 班次ID */
    public void setShiftId(Long shiftId){
        this.shiftId = shiftId;
    }
    /** 班次名称 */
    public String getShiftName(){
        return this.shiftName;
    }
    /** 班次名称 */
    public void setShiftName(String shiftName){
        this.shiftName = shiftName;
    }
    /** 班组 */
    public Long getShiftTypeId(){
        return this.shiftTypeId;
    }
    /** 班组 */
    public void setShiftTypeId(Long shiftTypeId){
        this.shiftTypeId = shiftTypeId;
    }
    /** 开始日期 */
    public Date getStartDate(){
        return this.startDate;
    }
    /** 开始日期 */
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    /** 结束日期 */
    public Date getEndDate(){
        return this.endDate;
    }
    /** 结束日期 */
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
    /** 开始时间 */
    public String getStartTime(){
        return this.startTime;
    }
    /** 开始时间 */
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    /** 结束时间 */
    public String getEndTime(){
        return this.endTime;
    }
    /** 结束时间 */
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }
    /** 班组名称 */
    public String getShiftTypeName() {
        return shiftTypeName;
    }
    /** 班组名称 */
    public void setShiftTypeName(String shiftTypeName) {
        this.shiftTypeName = shiftTypeName;
    }
}
