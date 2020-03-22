package com.xchinfo.erp.scm.wms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
public class AllocationEO extends AbstractAuditableEntity<AllocationEO> {
    private static final long serialVersionUID = -2418597593823604218L;

    @TableField(exist = false)
    private List<AllocationDetailEO> details;

    @Override
    public Serializable getId() {
        return null;
    }
}
