package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;

@TableName("bsc_email_send")
@KeySequence("bsc_email_send")
public class EmailSendEO  extends AbstractAuditableEntity<EmailSendEO> {
    private static final long serialVersionUID = -2572782335815688266L;

    @TableId(type = IdType.INPUT)
    private Long emailSendId;/** 邮件发送id */

    private String reciverEmail;/** 收件人邮箱 */

    private String reciverName;/** 收件人姓名 */

    private String copyAddress;/** 抄送地址 */

    private Long emailTemplateId;/** 邮件模板id */

    @TableField(exist = false)
    private String templateName;/** 模板名称 */

    private String title;/** 邮件主题 */

    private String content;/** 邮件内容 */

    private Date estimatedDeliveryTime;/** 预计发送时间 */

    private Date actualDeliveryTime;/** 实际发送时间 */

    private Integer status;/** 发送状态;0待发送,1已发送,无错误,2,已发送未成功 3,已关闭 */

    private Integer errorcount;/** 发送错误次数 */

    private String resultObj;/** 发送结果报告对象类型 */

    private Long resultId;/** 发送结果报表对象ID */

    @Override
    public Serializable getId() {
        return this.emailSendId;
    }

    public Long getEmailSendId() {
        return emailSendId;
    }

    public void setEmailSendId(Long emailSendId) {
        this.emailSendId = emailSendId;
    }

    public String getReciverEmail() {
        return reciverEmail;
    }

    public void setReciverEmail(String reciverEmail) {
        this.reciverEmail = reciverEmail;
    }

    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public String getCopyAddress() {
        return copyAddress;
    }

    public void setCopyAddress(String copyAddress) {
        this.copyAddress = copyAddress;
    }

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public Date getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(Date actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorcount() {
        return errorcount;
    }

    public void setErrorcount(Integer errorcount) {
        this.errorcount = errorcount;
    }

    public String getResultObj() {
        return resultObj;
    }

    public void setResultObj(String resultObj) {
        this.resultObj = resultObj;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
