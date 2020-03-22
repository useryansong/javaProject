package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.hibernate.validator.constraints.Length;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@TableName("bsc_email_template")
@KeySequence("bsc_email_template")
public class EmailTemplateEO  extends AbstractAuditableEntity<EmailTemplateEO> {
    private static final long serialVersionUID = -2572415635815688266L;

    @TableId(type = IdType.INPUT)
    private Long emailTemplateId;/** 模板id */

    private String templateCode;/** 模板编码 */

    @NotNull(message = "模板名称不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "模板名称不能超过 200 个字符")
    private String templateName;/** 模板名称 */

    @NotNull(message = "SMTP 服务器地址不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String host;/** SMTP 服务器地址 */

    private String port;/** 端口 */

    @NotNull(message = "发送邮件用户名不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String username;/** 发送邮件用户名;例如：xxx@163.com */

    @NotNull(message = "发送邮件第三方客户端授权码不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String password;/** 发送邮件用户密码(明文);第三方客户端授权码 */

    @NotNull(message = "内容模板不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    private String content;/** 邮件内容模板;要替换的部分用el表达式 */

    @NotNull(message = "标题模板不能为空！", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 200, message = "标题模板不能超过 200 个字符")
    private String title;/** 邮件标题模板;要替换的部分用el表达式 */

    private Integer status;/** 状态;0：启用，1：停用 */

    @Override
    public Serializable getId() {
        return this.emailTemplateId;
    }

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
