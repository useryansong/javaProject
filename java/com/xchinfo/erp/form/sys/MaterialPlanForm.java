package com.xchinfo.erp.form.sys;

import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import java.util.List;


public class MaterialPlanForm {

    List<PurchaseOrderEO> purchaseOrders;

    List<PurchaseOrderEO> outsideOrders;

    List<ProductOrderEO> productOrders;

    Long[] serialIds;

    public List<PurchaseOrderEO> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrderEO> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public List<ProductOrderEO> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(List<ProductOrderEO> productOrders) {
        this.productOrders = productOrders;
    }

    public Long[] getSerialIds() {
        return serialIds;
    }

    public void setSerialIds(Long[] serialIds) {
        this.serialIds = serialIds;
    }

    public List<PurchaseOrderEO> getOutsideOrders() {
        return outsideOrders;
    }

    public void setOutsideOrders(List<PurchaseOrderEO> outsideOrders) {
        this.outsideOrders = outsideOrders;
    }
}
