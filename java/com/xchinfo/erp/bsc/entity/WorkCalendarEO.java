package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xchinfo.erp.annotation.BusinessLogField;
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
@TableName("bsc_work_calendar")
@KeySequence("bsc_work_calendar")
public class WorkCalendarEO extends AbstractAuditableEntity<WorkCalendarEO> {
    private static final long serialVersionUID = 8895628974557450606L;

    @TableId(type = IdType.INPUT)
    private Long calendarId;/** 日历ID */

    @NotNull(message = "班组不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("班组")
    private Long shiftTypeId;/** 班组 */

    @TableField(exist = false)
    @BusinessLogField("班组名称")
    private String shiftTypeName;/** 班组名称 */

    @NotNull(message = "班次不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("班次")
    private Long shiftId;/** 班次 */

    @TableField(exist = false)
    @BusinessLogField("班次名称")
    private String shiftName;/** 班次名称 */

    @NotNull(message = "日期不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("日期")
    private Date shiftDate;/** 日期 */

    @NotBlank(message = "开始时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("开始时间")
    private String startTime;/** 开始时间 */

    @NotBlank(message = "结束时间不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @BusinessLogField("结束时间")
    private String endTime;/** 结束时间 */

    @Override
    public Serializable getId() {
        return this.calendarId;
    }

    /** 日历ID */
    public Long getCalendarId(){
        return this.calendarId;
    }
    /** 日历ID */
    public void setCalendarId(Long calendarId){
        this.calendarId = calendarId;
    }
    /** 班组 */
    public Long getShiftTypeId(){
        return this.shiftTypeId;
    }
    /** 班组 */
    public void setShiftTypeId(Long shiftTypeId){
        this.shiftTypeId = shiftTypeId;
    }
    /** 班次 */
    public Long getShiftId(){
        return this.shiftId;
    }
    /** 班次 */
    public void setShiftId(Long shiftId){
        this.shiftId = shiftId;
    }
    /** 日期 */
    public Date getShiftDate(){
        return this.shiftDate;
    }
    /** 日期 */
    public void setShiftDate(Date shiftDate){
        this.shiftDate = shiftDate;
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
    /** 班次名称 */
    public String getShiftName() {
        return shiftName;
    }
    /** 班次名称 */
    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }
}
