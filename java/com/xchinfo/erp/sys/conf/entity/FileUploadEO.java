package com.xchinfo.erp.sys.conf.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.AuditableTreeNode;



@TableName("sys_file_upload")
@KeySequence("sys_file_upload")
public class FileUploadEO extends AuditableTreeNode<FileUploadEO> {

    @TableId(type = IdType.INPUT)
    private Long fileId;/** 主键ID */

    private Long parentFileId;/** 父ID */

    private Integer type;/** 类型;1:目录，2:文档，3:远程 */

    private String url;/** 远程url */

    private String path;/** 路径 */

    private String name;/** 名称 */

    private String postfix;/** 后缀 */

    @TableField(exist = false)
    private String textField;/**下载*/

    @Override
    public Long getId() {
        return this.fileId;
    }

    @Override
    public Long getPid() {
        return this.parentFileId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getParentFileId() {
        return parentFileId;
    }

    public void setParentFileId(Long parentFileId) {
        this.parentFileId = parentFileId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }
}
