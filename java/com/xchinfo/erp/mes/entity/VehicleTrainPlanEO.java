package com.xchinfo.erp.mes.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;


@TableName("cmp_vehicle_train_plan")
@KeySequence("cmp_vehicle_train_plan")
public class VehicleTrainPlanEO extends AbstractAuditableEntity<VehicleTrainPlanEO> {

    @TableId(type = IdType.INPUT)
    private Long trainId;/** 主键 */

    private Date trainDate;/** 日期 */

    private String trainTime;/** 发车时间 */

    private Long orgId;/** 所属机构 */

    private String vehicleType;/** 车型 */

    @TableField(exist = false)
    private String vehicleTypeName;/** 车型 */

    private Double freight;/** 运费 */

    private Integer status;/** 状态 */

    private String trainNumber;/** 车次 */

    @Override
    public Serializable getId() {
        return trainId;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Date getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Date trainDate) {
        this.trainDate = trainDate;
    }

    public String getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(String trainTime) {
        this.trainTime = trainTime;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }
}
