package com.xchinfo.erp.bsc;

import com.xchinfo.erp.bsc.entity.BomEO;

import java.util.Date;

public class PoorMaterialVO extends BomEO {

    private String voucherNo;

    private String inventoryCode;

    private String warehouseCode;

    private String createBillName;

    private Date createdTime;

    private Double yamount;/** 数量 */

    private Double xamount;/** 数量 */

    private String erpVoucherNo1;/** 材料出库单流水号 */

    private String deliveryTypeCode;/** 出库类别编码 */


    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    @Override
    public String getInventoryCode() {
        return inventoryCode;
    }

    @Override
    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCreateBillName() {
        return createBillName;
    }

    public void setCreateBillName(String createBillName) {
        this.createBillName = createBillName;
    }

    @Override
    public Date getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Double getYamount() {
        return yamount;
    }

    public void setYamount(Double yamount) {
        this.yamount = yamount;
    }

    public Double getXamount() {
        return xamount;
    }

    public void setXamount(Double xamount) {
        this.xamount = xamount;
    }

    public String getErpVoucherNo1() {
        return erpVoucherNo1;
    }

    public void setErpVoucherNo1(String erpVoucherNo1) {
        this.erpVoucherNo1 = erpVoucherNo1;
    }

    public String getDeliveryTypeCode() {
        return deliveryTypeCode;
    }

    public void setDeliveryTypeCode(String deliveryTypeCode) {
        this.deliveryTypeCode = deliveryTypeCode;
    }
}
