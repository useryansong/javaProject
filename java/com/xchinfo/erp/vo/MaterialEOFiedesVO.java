package com.xchinfo.erp.vo;

import com.xchinfo.erp.bsc.entity.MaterialEO;

public class MaterialEOFiedesVO {
    private MaterialEO[] MaterialEOs;

    private Integer[] ids;

    public MaterialEO[] getMaterialEOs() {
        return MaterialEOs;
    }

    public void setMaterialEOs(MaterialEO[] materialEOs) {
        MaterialEOs = materialEOs;
    }

    public Integer[] getIds() {
        return ids;
    }

    public void setIds(Integer[] ids) {
        this.ids = ids;
    }
}
