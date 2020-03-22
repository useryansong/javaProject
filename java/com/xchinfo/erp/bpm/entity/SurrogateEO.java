package com.xchinfo.erp.bpm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author roman.li
 * @date 2019/3/19
 * @update
 */
@TableName("wf_surrogate")
@KeySequence("wf_surrogate")
public class SurrogateEO extends AbstractAuditableEntity<SurrogateEO> {
    private static final long serialVersionUID = -424456534444047336L;

    @TableId(type = IdType.INPUT)
    private Long surrogateId;/** 主键ID */

    private Long processId;/** 流程ID */

    private String operator;/** 授权人 */

    private String surrogate;/** 代理人 */

    private Date startDate;/** 开始时间 */

    private Date endDate;/** 结束时间 */

    private Integer status;/** 状态;0-禁用;1-可用 */

    @Override
    public Serializable getId() {
        return this.surrogateId;
    }

    /** 主键ID */
    public Long getSurrogateId(){
        return this.surrogateId;
    }
    /** 主键ID */
    public void setSurrogateId(Long surrogateId){
        this.surrogateId = surrogateId;
    }
    /** 流程ID */
    public Long getProcessId(){
        return this.processId;
    }
    /** 流程ID */
    public void setProcessId(Long processId){
        this.processId = processId;
    }
    /** 授权人 */
    public String getOperator(){
        return this.operator;
    }
    /** 授权人 */
    public void setOperator(String operator){
        this.operator = operator;
    }
    /** 代理人 */
    public String getSurrogate(){
        return this.surrogate;
    }
    /** 代理人 */
    public void setSurrogate(String surrogate){
        this.surrogate = surrogate;
    }
    /** 开始时间 */
    public Date getStartDate(){
        return this.startDate;
    }
    /** 开始时间 */
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    /** 结束时间 */
    public Date getEndDate(){
        return this.endDate;
    }
    /** 结束时间 */
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
    /** 状态;0-禁用;1-可用 */
    public Integer getStatus(){
        return this.status;
    }
    /** 状态;0-禁用;1-可用 */
    public void setStatus(Integer status){
        this.status = status;
    }
}
