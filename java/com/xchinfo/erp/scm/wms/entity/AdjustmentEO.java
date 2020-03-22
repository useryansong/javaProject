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
public class AdjustmentEO extends AbstractAuditableEntity<AdjustmentEO> {
    private static final long serialVersionUID = 3232551182430977941L;

    @TableField(exist = false)
    private List<AdjustmentDetailEO> details;

    @Override
    public Serializable getId() {
        return null;
    }
}
