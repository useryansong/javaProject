package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;


@TableName("cmp_vehicle_plan")
@KeySequence("cmp_vehicle_plan")
public class VehiclePlanEO extends AbstractAuditableEntity<VehiclePlanEO> {

    private static final long serialVersionUID = 4233198609292132222L;
    @TableId(type = IdType.INPUT)
    private Long vehiclePlanId;/** 排车计划ID */

    private String vehiclePlanNo;/** 排车计划编号 */

    private Long orgId;/** 归属机构ID */

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date deliveryDate;/** 发货日期 */

    private Long customerId;/** 客户ID */

    private String customerCode;/** 客户编码 */

    private String customerName;/** 客户名称 */

    private Long materialId;/** 物料ID */

    private String materialCode;/** 物料编码 */

    private String materialName;/** 物料名称 */

    private String projectNo;/** 项目号 */

    private String elementNo;/** 零件号 */

    private Double amount;/** 总数 */

    private Double snp;/** SNP */

    private Integer status;/** 状态 */

    private String deliveryNoteNo;/** 送货单号 */

    private String trainNumber01;/** 车次01 */

    private String trainNumber02;/** 车次02 */

    private String trainNumber03;/** 车次03 */

    private String trainNumber04;/** 车次04 */

    private String trainNumber05;/** 车次05 */

    private String trainNumber06;/** 车次06 */

    private String trainNumber07;/** 车次07 */

    private String trainNumber08;/** 车次08 */

    private String trainNumber09;/** 车次09 */

    private String trainNumber10;/** 车次10 */

    private String trainNumber11;/** 车次11 */

    private String trainNumber12;/** 车次12 */

    private String trainNumber13;/** 车次13 */

    private String trainNumber14;/** 车次14 */

    private String trainNumber15;/** 车次15 */

    private String trainNumber16;/** 车次16 */

    private String trainNumber17;/** 车次17 */

    private String trainNumber18;/** 车次18 */

    private String trainNumber19;/** 车次19 */

    private String trainNumber20;/** 车次20 */

    private String trainNumber21;/** 车次21 */

    private String trainNumber22;/** 车次22 */

    private String trainNumber23;/** 车次23 */

    private String trainNumber24;/** 车次24 */

    private String trainNumber25;/** 车次25 */

    private String trainNumber26;/** 车次26 */

    private String trainNumber27;/** 车次27 */

    private String trainNumber28;/** 车次28 */

    private String trainNumber29;/** 车次29 */

    private String trainNumber30;/** 车次30 */

    private String trainNumber31;/** 车次31 */

    private String trainNumber32;/** 车次32 */

    private String trainNumber33;/** 车次33 */

    private String trainNumber34;/** 车次34 */

    private String trainNumber35;/** 车次35 */

    private String trainNumber36;/** 车次36 */

    private String trainNumber37;/** 车次37 */

    private String trainNumber38;/** 车次38 */

    private String trainNumber39;/** 车次39 */

    private String trainNumber40;/** 车次40 */

    private String voucherNo01;/** 出库单号01 */

    private String voucherNo02;/** 出库单号02 */

    private String voucherNo03;/** 出库单号03 */

    private String voucherNo04;/** 出库单号04 */

    private String voucherNo05;/** 出库单号05 */

    private String voucherNo06;/** 出库单号06 */

    private String voucherNo07;/** 出库单号07 */

    private String voucherNo08;/** 出库单号08 */

    private String voucherNo09;/** 出库单号09 */

    private String voucherNo10;/** 出库单号10 */

    private String voucherNo11;/** 出库单号11 */

    private String voucherNo12;/** 出库单号12 */

    private String voucherNo13;/** 出库单号13 */

    private String voucherNo14;/** 出库单号14 */

    private String voucherNo15;/** 出库单号15 */

    private String voucherNo16;/** 出库单号16 */

    private String voucherNo17;/** 出库单号17 */

    private String voucherNo18;/** 出库单号18 */

    private String voucherNo19;/** 出库单号19 */

    private String voucherNo20;/** 出库单号20 */

    private String voucherNo21;/** 出库单号21 */

    private String voucherNo22;/** 出库单号22 */

    private String voucherNo23;/** 出库单号23 */

    private String voucherNo24;/** 出库单号24 */

    private String voucherNo25;/** 出库单号25 */

    private String voucherNo26;/** 出库单号26 */

    private String voucherNo27;/** 出库单号27 */

    private String voucherNo28;/** 出库单号28 */

    private String voucherNo29;/** 出库单号29 */

    private String voucherNo30;/** 出库单号30 */

    private String voucherNo31;/** 出库单号31 */

    private String voucherNo32;/** 出库单号32 */

    private String voucherNo33;/** 出库单号33 */

    private String voucherNo34;/** 出库单号34 */

    private String voucherNo35;/** 出库单号35 */

    private String voucherNo36;/** 出库单号36 */

    private String voucherNo37;/** 出库单号37 */

    private String voucherNo38;/** 出库单号38 */

    private String voucherNo39;/** 出库单号39 */

    private String voucherNo40;/** 出库单号40 */


    @TableField(exist = false)
    private String orgName;/** 归属机构名称 */

    @TableField(exist = false)
    private String inventoryCode;

    @TableField(exist = false)
    private String figureNumber;

    @TableField(exist = false)
    private String figureVersion;

    @TableField(exist = false)
    private Long unitId;

    @TableField(exist = false)
    private String msg;

    @TableField(exist = false)
    private Integer index;/** 序列标志 */


    @Override
    public Serializable getId() {
        return this.vehiclePlanId;
    }


    public Long getVehiclePlanId() {
        return vehiclePlanId;
    }

    public void setVehiclePlanId(Long vehiclePlanId) {
        this.vehiclePlanId = vehiclePlanId;
    }

    public String getVehiclePlanNo() {
        return vehiclePlanNo;
    }

    public void setVehiclePlanNo(String vehiclePlanNo) {
        this.vehiclePlanNo = vehiclePlanNo;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTrainNumber01() {
        return trainNumber01;
    }

    public void setTrainNumber01(String trainNumber01) {
        this.trainNumber01 = trainNumber01;
    }

    public String getTrainNumber02() {
        return trainNumber02;
    }

    public void setTrainNumber02(String trainNumber02) {
        this.trainNumber02 = trainNumber02;
    }

    public String getTrainNumber03() {
        return trainNumber03;
    }

    public void setTrainNumber03(String trainNumber03) {
        this.trainNumber03 = trainNumber03;
    }

    public String getTrainNumber04() {
        return trainNumber04;
    }

    public void setTrainNumber04(String trainNumber04) {
        this.trainNumber04 = trainNumber04;
    }

    public String getTrainNumber05() {
        return trainNumber05;
    }

    public void setTrainNumber05(String trainNumber05) {
        this.trainNumber05 = trainNumber05;
    }

    public String getTrainNumber06() {
        return trainNumber06;
    }

    public void setTrainNumber06(String trainNumber06) {
        this.trainNumber06 = trainNumber06;
    }

    public String getTrainNumber07() {
        return trainNumber07;
    }

    public void setTrainNumber07(String trainNumber07) {
        this.trainNumber07 = trainNumber07;
    }

    public String getTrainNumber08() {
        return trainNumber08;
    }

    public void setTrainNumber08(String trainNumber08) {
        this.trainNumber08 = trainNumber08;
    }

    public String getTrainNumber09() {
        return trainNumber09;
    }

    public void setTrainNumber09(String trainNumber09) {
        this.trainNumber09 = trainNumber09;
    }

    public String getTrainNumber10() {
        return trainNumber10;
    }

    public void setTrainNumber10(String trainNumber10) {
        this.trainNumber10 = trainNumber10;
    }

    public String getTrainNumber11() {
        return trainNumber11;
    }

    public void setTrainNumber11(String trainNumber11) {
        this.trainNumber11 = trainNumber11;
    }

    public String getTrainNumber12() {
        return trainNumber12;
    }

    public void setTrainNumber12(String trainNumber12) {
        this.trainNumber12 = trainNumber12;
    }

    public String getTrainNumber13() {
        return trainNumber13;
    }

    public void setTrainNumber13(String trainNumber13) {
        this.trainNumber13 = trainNumber13;
    }

    public String getTrainNumber14() {
        return trainNumber14;
    }

    public void setTrainNumber14(String trainNumber14) {
        this.trainNumber14 = trainNumber14;
    }

    public String getTrainNumber15() {
        return trainNumber15;
    }

    public void setTrainNumber15(String trainNumber15) {
        this.trainNumber15 = trainNumber15;
    }

    public String getTrainNumber16() {
        return trainNumber16;
    }

    public void setTrainNumber16(String trainNumber16) {
        this.trainNumber16 = trainNumber16;
    }

    public String getTrainNumber17() {
        return trainNumber17;
    }

    public void setTrainNumber17(String trainNumber17) {
        this.trainNumber17 = trainNumber17;
    }

    public String getTrainNumber18() {
        return trainNumber18;
    }

    public void setTrainNumber18(String trainNumber18) {
        this.trainNumber18 = trainNumber18;
    }

    public String getTrainNumber19() {
        return trainNumber19;
    }

    public void setTrainNumber19(String trainNumber19) {
        this.trainNumber19 = trainNumber19;
    }

    public String getTrainNumber20() {
        return trainNumber20;
    }

    public void setTrainNumber20(String trainNumber20) {
        this.trainNumber20 = trainNumber20;
    }

    public String getTrainNumber21() {
        return trainNumber21;
    }

    public void setTrainNumber21(String trainNumber21) {
        this.trainNumber21 = trainNumber21;
    }

    public String getTrainNumber22() {
        return trainNumber22;
    }

    public void setTrainNumber22(String trainNumber22) {
        this.trainNumber22 = trainNumber22;
    }

    public String getTrainNumber23() {
        return trainNumber23;
    }

    public void setTrainNumber23(String trainNumber23) {
        this.trainNumber23 = trainNumber23;
    }

    public String getTrainNumber24() {
        return trainNumber24;
    }

    public void setTrainNumber24(String trainNumber24) {
        this.trainNumber24 = trainNumber24;
    }

    public String getTrainNumber25() {
        return trainNumber25;
    }

    public void setTrainNumber25(String trainNumber25) {
        this.trainNumber25 = trainNumber25;
    }

    public String getTrainNumber26() {
        return trainNumber26;
    }

    public void setTrainNumber26(String trainNumber26) {
        this.trainNumber26 = trainNumber26;
    }

    public String getTrainNumber27() {
        return trainNumber27;
    }

    public void setTrainNumber27(String trainNumber27) {
        this.trainNumber27 = trainNumber27;
    }

    public String getTrainNumber28() {
        return trainNumber28;
    }

    public void setTrainNumber28(String trainNumber28) {
        this.trainNumber28 = trainNumber28;
    }

    public String getTrainNumber29() {
        return trainNumber29;
    }

    public void setTrainNumber29(String trainNumber29) {
        this.trainNumber29 = trainNumber29;
    }

    public String getTrainNumber30() {
        return trainNumber30;
    }

    public void setTrainNumber30(String trainNumber30) {
        this.trainNumber30 = trainNumber30;
    }

    public String getTrainNumber31() {
        return trainNumber31;
    }

    public void setTrainNumber31(String trainNumber31) {
        this.trainNumber31 = trainNumber31;
    }

    public String getTrainNumber32() {
        return trainNumber32;
    }

    public void setTrainNumber32(String trainNumber32) {
        this.trainNumber32 = trainNumber32;
    }

    public String getTrainNumber33() {
        return trainNumber33;
    }

    public void setTrainNumber33(String trainNumber33) {
        this.trainNumber33 = trainNumber33;
    }

    public String getTrainNumber34() {
        return trainNumber34;
    }

    public void setTrainNumber34(String trainNumber34) {
        this.trainNumber34 = trainNumber34;
    }

    public String getTrainNumber35() {
        return trainNumber35;
    }

    public void setTrainNumber35(String trainNumber35) {
        this.trainNumber35 = trainNumber35;
    }

    public String getTrainNumber36() {
        return trainNumber36;
    }

    public void setTrainNumber36(String trainNumber36) {
        this.trainNumber36 = trainNumber36;
    }

    public String getTrainNumber37() {
        return trainNumber37;
    }

    public void setTrainNumber37(String trainNumber37) {
        this.trainNumber37 = trainNumber37;
    }

    public String getTrainNumber38() {
        return trainNumber38;
    }

    public void setTrainNumber38(String trainNumber38) {
        this.trainNumber38 = trainNumber38;
    }

    public String getTrainNumber39() {
        return trainNumber39;
    }

    public void setTrainNumber39(String trainNumber39) {
        this.trainNumber39 = trainNumber39;
    }

    public String getTrainNumber40() {
        return trainNumber40;
    }

    public void setTrainNumber40(String trainNumber40) {
        this.trainNumber40 = trainNumber40;
    }

    public String getVoucherNo01() {
        return voucherNo01;
    }

    public void setVoucherNo01(String voucherNo01) {
        this.voucherNo01 = voucherNo01;
    }

    public String getVoucherNo02() {
        return voucherNo02;
    }

    public void setVoucherNo02(String voucherNo02) {
        this.voucherNo02 = voucherNo02;
    }

    public String getVoucherNo03() {
        return voucherNo03;
    }

    public void setVoucherNo03(String voucherNo03) {
        this.voucherNo03 = voucherNo03;
    }

    public String getVoucherNo04() {
        return voucherNo04;
    }

    public void setVoucherNo04(String voucherNo04) {
        this.voucherNo04 = voucherNo04;
    }

    public String getVoucherNo05() {
        return voucherNo05;
    }

    public void setVoucherNo05(String voucherNo05) {
        this.voucherNo05 = voucherNo05;
    }

    public String getVoucherNo06() {
        return voucherNo06;
    }

    public void setVoucherNo06(String voucherNo06) {
        this.voucherNo06 = voucherNo06;
    }

    public String getVoucherNo07() {
        return voucherNo07;
    }

    public void setVoucherNo07(String voucherNo07) {
        this.voucherNo07 = voucherNo07;
    }

    public String getVoucherNo08() {
        return voucherNo08;
    }

    public void setVoucherNo08(String voucherNo08) {
        this.voucherNo08 = voucherNo08;
    }

    public String getVoucherNo09() {
        return voucherNo09;
    }

    public void setVoucherNo09(String voucherNo09) {
        this.voucherNo09 = voucherNo09;
    }

    public String getVoucherNo10() {
        return voucherNo10;
    }

    public void setVoucherNo10(String voucherNo10) {
        this.voucherNo10 = voucherNo10;
    }

    public String getVoucherNo11() {
        return voucherNo11;
    }

    public void setVoucherNo11(String voucherNo11) {
        this.voucherNo11 = voucherNo11;
    }

    public String getVoucherNo12() {
        return voucherNo12;
    }

    public void setVoucherNo12(String voucherNo12) {
        this.voucherNo12 = voucherNo12;
    }

    public String getVoucherNo13() {
        return voucherNo13;
    }

    public void setVoucherNo13(String voucherNo13) {
        this.voucherNo13 = voucherNo13;
    }

    public String getVoucherNo14() {
        return voucherNo14;
    }

    public void setVoucherNo14(String voucherNo14) {
        this.voucherNo14 = voucherNo14;
    }

    public String getVoucherNo15() {
        return voucherNo15;
    }

    public void setVoucherNo15(String voucherNo15) {
        this.voucherNo15 = voucherNo15;
    }

    public String getVoucherNo16() {
        return voucherNo16;
    }

    public void setVoucherNo16(String voucherNo16) {
        this.voucherNo16 = voucherNo16;
    }

    public String getVoucherNo17() {
        return voucherNo17;
    }

    public void setVoucherNo17(String voucherNo17) {
        this.voucherNo17 = voucherNo17;
    }

    public String getVoucherNo18() {
        return voucherNo18;
    }

    public void setVoucherNo18(String voucherNo18) {
        this.voucherNo18 = voucherNo18;
    }

    public String getVoucherNo19() {
        return voucherNo19;
    }

    public void setVoucherNo19(String voucherNo19) {
        this.voucherNo19 = voucherNo19;
    }

    public String getVoucherNo20() {
        return voucherNo20;
    }

    public void setVoucherNo20(String voucherNo20) {
        this.voucherNo20 = voucherNo20;
    }

    public String getVoucherNo21() {
        return voucherNo21;
    }

    public void setVoucherNo21(String voucherNo21) {
        this.voucherNo21 = voucherNo21;
    }

    public String getVoucherNo22() {
        return voucherNo22;
    }

    public void setVoucherNo22(String voucherNo22) {
        this.voucherNo22 = voucherNo22;
    }

    public String getVoucherNo23() {
        return voucherNo23;
    }

    public void setVoucherNo23(String voucherNo23) {
        this.voucherNo23 = voucherNo23;
    }

    public String getVoucherNo24() {
        return voucherNo24;
    }

    public void setVoucherNo24(String voucherNo24) {
        this.voucherNo24 = voucherNo24;
    }

    public String getVoucherNo25() {
        return voucherNo25;
    }

    public void setVoucherNo25(String voucherNo25) {
        this.voucherNo25 = voucherNo25;
    }

    public String getVoucherNo26() {
        return voucherNo26;
    }

    public void setVoucherNo26(String voucherNo26) {
        this.voucherNo26 = voucherNo26;
    }

    public String getVoucherNo27() {
        return voucherNo27;
    }

    public void setVoucherNo27(String voucherNo27) {
        this.voucherNo27 = voucherNo27;
    }

    public String getVoucherNo28() {
        return voucherNo28;
    }

    public void setVoucherNo28(String voucherNo28) {
        this.voucherNo28 = voucherNo28;
    }

    public String getVoucherNo29() {
        return voucherNo29;
    }

    public void setVoucherNo29(String voucherNo29) {
        this.voucherNo29 = voucherNo29;
    }

    public String getVoucherNo30() {
        return voucherNo30;
    }

    public void setVoucherNo30(String voucherNo30) {
        this.voucherNo30 = voucherNo30;
    }

    public String getVoucherNo31() {
        return voucherNo31;
    }

    public void setVoucherNo31(String voucherNo31) {
        this.voucherNo31 = voucherNo31;
    }

    public String getVoucherNo32() {
        return voucherNo32;
    }

    public void setVoucherNo32(String voucherNo32) {
        this.voucherNo32 = voucherNo32;
    }

    public String getVoucherNo33() {
        return voucherNo33;
    }

    public void setVoucherNo33(String voucherNo33) {
        this.voucherNo33 = voucherNo33;
    }

    public String getVoucherNo34() {
        return voucherNo34;
    }

    public void setVoucherNo34(String voucherNo34) {
        this.voucherNo34 = voucherNo34;
    }

    public String getVoucherNo35() {
        return voucherNo35;
    }

    public void setVoucherNo35(String voucherNo35) {
        this.voucherNo35 = voucherNo35;
    }

    public String getVoucherNo36() {
        return voucherNo36;
    }

    public void setVoucherNo36(String voucherNo36) {
        this.voucherNo36 = voucherNo36;
    }

    public String getVoucherNo37() {
        return voucherNo37;
    }

    public void setVoucherNo37(String voucherNo37) {
        this.voucherNo37 = voucherNo37;
    }

    public String getVoucherNo38() {
        return voucherNo38;
    }

    public void setVoucherNo38(String voucherNo38) {
        this.voucherNo38 = voucherNo38;
    }

    public String getVoucherNo39() {
        return voucherNo39;
    }

    public void setVoucherNo39(String voucherNo39) {
        this.voucherNo39 = voucherNo39;
    }

    public String getVoucherNo40() {
        return voucherNo40;
    }

    public void setVoucherNo40(String voucherNo40) {
        this.voucherNo40 = voucherNo40;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Double getSnp() {
        return snp;
    }

    public void setSnp(Double snp) {
        this.snp = snp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getFigureNumber() {
        return figureNumber;
    }

    public void setFigureNumber(String figureNumber) {
        this.figureNumber = figureNumber;
    }

    public String getFigureVersion() {
        return figureVersion;
    }

    public void setFigureVersion(String figureVersion) {
        this.figureVersion = figureVersion;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getDeliveryNoteNo() {
        return deliveryNoteNo;
    }

    public void setDeliveryNoteNo(String deliveryNoteNo) {
        this.deliveryNoteNo = deliveryNoteNo;
    }
}
