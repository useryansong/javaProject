package com.xchinfo.erp.bsc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@TableName("bsc_material_specific")
@KeySequence("bsc_material_specific")
public class MaterialSpecificEO extends AbstractAuditableEntity<MaterialSpecificEO> {
    private static final long serialVersionUID = -7513854305422754149L;

    @TableId(type = IdType.INPUT)
    private Long specificId;/** 主键 */

    private Long materialId;/** 物料 */

    private String specification;/** 规格型号 */

    private Integer isDefault;/** 是否默认 */

    private Long sizeUnit;/** 尺寸单位 */

    private Double length;/** 长 */

    private Double width;/** 宽 */

    private Double height;/** 高 */

    private Double bulk;/** 体积 */

    @Override
    public Serializable getId() {
        return this.specificId;
    }

    /** 主键 */
    public Long getSpecificId(){
        return this.specificId;
    }
    /** 主键 */
    public void setSpecificId(Long specificId){
        this.specificId = specificId;
    }
    /** 物料 */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料 */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 规格型号 */
    public String getSpecification(){
        return this.specification;
    }
    /** 规格型号 */
    public void setSpecification(String specification){
        this.specification = specification;
    }
    /** 是否默认 */
    public Integer getIsDefault(){
        return this.isDefault;
    }
    /** 是否默认 */
    public void setIsDefault(Integer isDefault){
        this.isDefault = isDefault;
    }
    /** 尺寸单位 */
    public Long getSizeUnit(){
        return this.sizeUnit;
    }
    /** 尺寸单位 */
    public void setSizeUnit(Long sizeUnit){
        this.sizeUnit = sizeUnit;
    }
    /** 长 */
    public Double getLength(){
        return this.length;
    }
    /** 长 */
    public void setLength(Double length){
        this.length = length;
    }
    /** 宽 */
    public Double getWidth(){
        return this.width;
    }
    /** 宽 */
    public void setWidth(Double width){
        this.width = width;
    }
    /** 高 */
    public Double getHeight(){
        return this.height;
    }
    /** 高 */
    public void setHeight(Double height){
        this.height = height;
    }
    /** 体积 */
    public Double getBulk(){
        return this.bulk;
    }
    /** 体积 */
    public void setBulk(Double bulk){
        this.bulk = bulk;
    }
}
