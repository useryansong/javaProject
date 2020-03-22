package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import java.io.Serializable;

/**
 * @author zhongye
 * @date 2019/5/10
 */
@TableName("sys_attachment")
public class AttachmentEO extends AbstractAuditableEntity<AttachmentEO> {
    private static final long serialVersionUID = -2893698726917380802L;

    @TableId(type = IdType.INPUT)
    private Long attachmentId;/** 附件ID */

    private String attachmentName;/** 附件名称(含后缀) */

    private String attachmentUrl;/** 附件路径 */

    @Override
    public Serializable getId() {
        return null;
    }

    /** 附件ID */
    public Long getAttachmentId(){
        return this.attachmentId;
    }
    /** 附件ID */
    public void setAttachmentId(Long attachmentId){
        this.attachmentId = attachmentId;
    }
    /** 附件名称(含后缀) */
    public String getAttachmentName(){
        return this.attachmentName;
    }
    /** 附件名称(含后缀) */
    public void setAttachmentName(String attachmentName){
        this.attachmentName = attachmentName;
    }
    /** 附件路径 */
    public String getAttachmentUrl(){
        return this.attachmentUrl;
    }
    /** 附件路径 */
    public void setAttachmentUrl(String attachmentUrl){
        this.attachmentUrl = attachmentUrl;
    }
}
