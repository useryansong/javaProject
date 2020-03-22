package com.xchinfo.erp.scm.srm.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/9/2
 */
@TableName("srm_week_product_order")
@KeySequence("srm_week_product_order")
public class WeekProductOrderEO extends AbstractAuditableEntity<WeekProductOrderEO> {
    private static final long serialVersionUID = 7305835741285969790L;

    @TableId(type = IdType.INPUT)
    private Long productOrderId;/** 主键 */

    private Long orgId;/** 归属机构 */

    @TableField(exist = false)
    private String orgName;

    private Long serialId;/** 客户需求周计划ID */

    @TableField(exist = false)
    private Long parentSerialId;/** 客户需求周计划的父计划ID */

    private String voucherNo;/** 流水号 */

    private Long materialId;/** 物料ID */

    private String elementNo;/** 零件号 */

    private Date planFinishDate;/** 计划完成日期 */

    private Date actualFinishDate;/** 实际完成日期 */

    private Double planProduceQuantity;/** 计划生产数量 */

    private Double planRequireQuantity;/** 客户需求周计划需求数量 */

    private Double produceReportQuantity;/** 生产日报入库数量数量 */

    private Double hasProduceQuantity;/** 已生产数量 */

    private Double hasScheduleQuantity;/** 已排产数量 */

    private Integer produceStatus;/** 生产状态;0-待生产，1-生产中，2-生产完成 */

    private Integer scheduleStatus;/** 排产状态;0-待排产（新建），1-排产中，2-排产完成 */

    private String memo;/** 备注 */

    private Integer isChangeConfirm;/** 变更确认;0 -需确认,1-已确认 */

    private Integer confirmVersion;/** 确认版本号 */

    private Date lastConfirmTime;/** 最后确认时间 */

    @TableField(exist = false)
    private String materialName;

    @TableField(exist = false)
    private String inventoryCode;

    @TableField(exist = false)
    private String serialCode;/** 客户需求周计划的流水号 */

    @TableField(exist = false)
    private Double count; /** 期初库存 */

    @TableField(exist = false)
    private String projectNo; /** 项目号 */

    @TableField(exist = false)
    private Map map;

    @TableField(exist = false)
    private Double day1;

    @TableField(exist = false)
    private Double day2;

    @TableField(exist = false)
    private Double day3;

    @TableField(exist = false)
    private Double day4;

    @TableField(exist = false)
    private Double day5;

    @TableField(exist = false)
    private Double day6;

    @TableField(exist = false)
    private Double day7;

    @TableField(exist = false)
    private Double day8;

    @TableField(exist = false)
    private Double day9;

    @TableField(exist = false)
    private Double day10;

    @TableField(exist = false)
    private Double day11;

    @TableField(exist = false)
    private Double day12;

    @TableField(exist = false)
    private Double day13;

    @TableField(exist = false)
    private Double day14;

    @TableField(exist = false)
    private String type;


    @Override
    public Serializable getId() {
        return this.productOrderId;
    }

    /** 主键 */
    public Long getProductOrderId(){
        return this.productOrderId;
    }
    /** 主键 */
    public void setProductOrderId(Long productOrderId){
        this.productOrderId = productOrderId;
    }
    /** 归属机构 */
    public Long getOrgId(){
        return this.orgId;
    }
    /** 归属机构 */
    public void setOrgId(Long orgId){
        this.orgId = orgId;
    }
    /** 客户需求周计划ID */
    public Long getSerialId(){
        return this.serialId;
    }
    /** 客户需求周计划ID */
    public void setSerialId(Long serialId){
        this.serialId = serialId;
    }
    /** 流水号 */
    public String getVoucherNo(){
        return this.voucherNo;
    }
    /** 流水号 */
    public void setVoucherNo(String voucherNo){
        this.voucherNo = voucherNo;
    }
    /** 物料ID */
    public Long getMaterialId(){
        return this.materialId;
    }
    /** 物料ID */
    public void setMaterialId(Long materialId){
        this.materialId = materialId;
    }
    /** 零件号 */
    public String getElementNo(){
        return this.elementNo;
    }
    /** 零件号 */
    public void setElementNo(String elementNo){
        this.elementNo = elementNo;
    }
    /** 计划完成日期 */
    public Date getPlanFinishDate(){
        return this.planFinishDate;
    }
    /** 计划完成日期 */
    public void setPlanFinishDate(Date planFinishDate){
        this.planFinishDate = planFinishDate;
    }
    /** 实际完成日期 */
    public Date getActualFinishDate(){
        return this.actualFinishDate;
    }
    /** 实际完成日期 */
    public void setActualFinishDate(Date actualFinishDate){
        this.actualFinishDate = actualFinishDate;
    }
    /** 计划生产数量 */
    public Double getPlanProduceQuantity(){
        return this.planProduceQuantity;
    }
    /** 计划生产数量 */
    public void setPlanProduceQuantity(Double planProduceQuantity){
        this.planProduceQuantity = planProduceQuantity;
    }
    /** 客户需求周计划需求数量 */
    public Double getPlanRequireQuantity(){
        return this.planRequireQuantity;
    }
    /** 客户需求周计划需求数量 */
    public void setPlanRequireQuantity(Double planRequireQuantity){
        this.planRequireQuantity = planRequireQuantity;
    }
    /** 已生产数量 */
    public Double getHasProduceQuantity(){
        return this.hasProduceQuantity;
    }
    /** 已生产数量 */
    public void setHasProduceQuantity(Double hasProduceQuantity){
        this.hasProduceQuantity = hasProduceQuantity;
    }
    /** 已排产数量 */
    public Double getHasScheduleQuantity(){
        return this.hasScheduleQuantity;
    }
    /** 已排产数量 */
    public void setHasScheduleQuantity(Double hasScheduleQuantity){
        this.hasScheduleQuantity = hasScheduleQuantity;
    }
    /** 生产状态;0-待生产，1-生产中，2-生产完成 */
    public Integer getProduceStatus(){
        return this.produceStatus;
    }
    /** 生产状态;0-待生产，1-生产中，2-生产完成 */
    public void setProduceStatus(Integer produceStatus){
        this.produceStatus = produceStatus;
    }
    /** 排产状态;0-待排产（新建），1-排产中，2-排产完成 */
    public Integer getScheduleStatus(){
        return this.scheduleStatus;
    }
    /** 排产状态;0-待排产（新建），1-排产中，2-排产完成 */
    public void setScheduleStatus(Integer scheduleStatus){
        this.scheduleStatus = scheduleStatus;
    }
    /** 备注 */
    public String getMemo(){
        return this.memo;
    }
    /** 备注 */
    public void setMemo(String memo){
        this.memo = memo;
    }
    /** 变更确认;0 -需确认,1-已确认 */
    public Integer getIsChangeConfirm(){
        return this.isChangeConfirm;
    }
    /** 变更确认;0 -需确认,1-已确认 */
    public void setIsChangeConfirm(Integer isChangeConfirm){
        this.isChangeConfirm = isChangeConfirm;
    }
    /** 确认版本号 */
    public Integer getConfirmVersion(){
        return this.confirmVersion;
    }
    /** 确认版本号 */
    public void setConfirmVersion(Integer confirmVersion){
        this.confirmVersion = confirmVersion;
    }
    /** 最后确认时间 */
    public Date getLastConfirmTime(){
        return this.lastConfirmTime;
    }
    /** 最后确认时间 */
    public void setLastConfirmTime(Date lastConfirmTime){
        this.lastConfirmTime = lastConfirmTime;
    }

    public Long getParentSerialId() {
        return parentSerialId;
    }

    public void setParentSerialId(Long parentSerialId) {
        this.parentSerialId = parentSerialId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getSerialCode() {
        return serialCode;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getDay1() {
        return day1;
    }

    public void setDay1(Double day1) {
        this.day1 = day1;
    }

    public Double getDay2() {
        return day2;
    }

    public void setDay2(Double day2) {
        this.day2 = day2;
    }

    public Double getDay3() {
        return day3;
    }

    public void setDay3(Double day3) {
        this.day3 = day3;
    }

    public Double getDay4() {
        return day4;
    }

    public void setDay4(Double day4) {
        this.day4 = day4;
    }

    public Double getDay5() {
        return day5;
    }

    public void setDay5(Double day5) {
        this.day5 = day5;
    }

    public Double getDay6() {
        return day6;
    }

    public void setDay6(Double day6) {
        this.day6 = day6;
    }

    public Double getDay7() {
        return day7;
    }

    public void setDay7(Double day7) {
        this.day7 = day7;
    }

    public Double getDay8() {
        return day8;
    }

    public void setDay8(Double day8) {
        this.day8 = day8;
    }

    public Double getDay9() {
        return day9;
    }

    public void setDay9(Double day9) {
        this.day9 = day9;
    }

    public Double getDay10() {
        return day10;
    }

    public void setDay10(Double day10) {
        this.day10 = day10;
    }

    public Double getDay11() {
        return day11;
    }

    public void setDay11(Double day11) {
        this.day11 = day11;
    }

    public Double getDay12() {
        return day12;
    }

    public void setDay12(Double day12) {
        this.day12 = day12;
    }

    public Double getDay13() {
        return day13;
    }

    public void setDay13(Double day13) {
        this.day13 = day13;
    }

    public Double getDay14() {
        return day14;
    }

    public void setDay14(Double day14) {
        this.day14 = day14;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectNo() {

        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Double getProduceReportQuantity() {
        return produceReportQuantity;
    }

    public void setProduceReportQuantity(Double produceReportQuantity) {
        this.produceReportQuantity = produceReportQuantity;
    }
}
