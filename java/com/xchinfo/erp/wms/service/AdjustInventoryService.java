package com.xchinfo.erp.wms.service;

import com.xchinfo.erp.scm.wms.entity.AdjustInventoryEO;
import com.xchinfo.erp.scm.wms.entity.InventoryDetailEO;
import com.xchinfo.erp.scm.wms.entity.InventoryEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.wms.mapper.AdjustInventoryMapper;
import com.xchinfo.erp.wms.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;
import java.util.List;

@Service
public class AdjustInventoryService extends BaseServiceImpl<AdjustInventoryMapper, AdjustInventoryEO> {

    @Autowired
    private InventoryDetailService inventoryDetailService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private StockAccountService stockAccountService;

    @Transactional(rollbackFor = Exception.class)
    public String insertAdjust(Long[] inventoryDetailIds, Long inventoryId, UserEO userEO) throws BusinessException {
        if(inventoryDetailIds==null || inventoryDetailIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        int sum = 0;
        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);
        Date date =new Date();
        for(Long inventoryDetailId : inventoryDetailIds){

            InventoryDetailEO inventoryDetailEO = this.inventoryDetailService.getById(inventoryDetailId);

            AdjustInventoryEO adjustInventoryEO = new AdjustInventoryEO();

            adjustInventoryEO.setInventoryId(inventoryId);
            adjustInventoryEO.setInventoryDetailId(inventoryDetailId);
            adjustInventoryEO.setOrgId(inventoryEO.getOrgId());
            adjustInventoryEO.setMaterialId(inventoryDetailEO.getMaterialId());
            adjustInventoryEO.setElementNo(inventoryDetailEO.getElementNo());
            adjustInventoryEO.setAmount(inventoryDetailEO.getAmount());
            adjustInventoryEO.setRelAmount(inventoryDetailEO.getRelAmount());
            adjustInventoryEO.setLinedgeAmount(inventoryDetailEO.getLinedgeAmount());
            adjustInventoryEO.setTotalAmount(inventoryDetailEO.getTotalAmount());
            double outamount=inventoryDetailEO.getTotalAmount()-inventoryDetailEO.getAmount();
            double lessamount = 0-outamount;
            if (outamount>0 ){
                adjustInventoryEO.setInventoryType(1);
                adjustInventoryEO.setDifAmount(outamount);
                adjustInventoryEO.setAdjAmount(outamount);
                adjustInventoryEO.setRemark("库存数量:"+ inventoryDetailEO.getAmount() +",调账数量:"+outamount +",盘盈 "+outamount+"个");
            }else{
                adjustInventoryEO.setInventoryType(2);
                adjustInventoryEO.setDifAmount(lessamount);
                adjustInventoryEO.setAdjAmount(lessamount);
                adjustInventoryEO.setRemark("库存数量:"+ inventoryDetailEO.getAmount() +",调账数量:"+lessamount +",盘亏 "+lessamount+"个");
            }

            adjustInventoryEO.setStatus(0);
            adjustInventoryEO.setAdjustUserId(userEO.getUserId());
            adjustInventoryEO.setAdjustUserName(userEO.getUserName());
            adjustInventoryEO.setInventoryDate(date);
            boolean flag = super.save(adjustInventoryEO);

            //更新盘点明细表中的调账状态及调账表创建人
            this.baseMapper.updateInventoryDetailAdjustStatus1(inventoryDetailId,userEO.getUserId(),userEO.getUserName());

            if (flag) {
                sum += 1;
            }
        }

        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else{
           if(waitamount>0){
               this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
           }else {
               this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
           }

        }

        //获取最新的库存数量并更新到盘点明细表中
        this.inventoryMapper.updateNewAmount(userEO.getOrgId(), inventoryId);

        String successMsg = "已添加" + sum + "项<br/>";
        return successMsg ;
    }

    @Transactional(rollbackFor = Exception.class)
    public String insertAllAdjust(Long inventoryId, UserEO userEO) throws BusinessException {

        List<InventoryDetailEO> inventoryDetailEOS = this.baseMapper.getInventoryDetailEOs(inventoryId);
        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);

        Date date =new Date();
        for(InventoryDetailEO inventoryDetailEO : inventoryDetailEOS){

            //InventoryDetailEO inventoryDetailEO = this.inventoryDetailService.getById(inventoryDetailId);

            AdjustInventoryEO adjustInventoryEO = new AdjustInventoryEO();

            adjustInventoryEO.setInventoryId(inventoryId);
            adjustInventoryEO.setInventoryDetailId(inventoryDetailEO.getInventoryDetailId());
            adjustInventoryEO.setOrgId(inventoryEO.getOrgId());
            adjustInventoryEO.setMaterialId(inventoryDetailEO.getMaterialId());
            adjustInventoryEO.setElementNo(inventoryDetailEO.getElementNo());
            adjustInventoryEO.setAmount(inventoryDetailEO.getAmount());
            adjustInventoryEO.setRelAmount(inventoryDetailEO.getRelAmount());
            adjustInventoryEO.setLinedgeAmount(inventoryDetailEO.getLinedgeAmount());
            adjustInventoryEO.setTotalAmount(inventoryDetailEO.getTotalAmount());
            double outamount=inventoryDetailEO.getTotalAmount()-inventoryDetailEO.getAmount();
            double lessamount = 0-outamount;
            if (outamount>0 ){
                adjustInventoryEO.setInventoryType(1);
                adjustInventoryEO.setDifAmount(outamount);
                adjustInventoryEO.setAdjAmount(outamount);
                adjustInventoryEO.setRemark("库存数量:"+ inventoryDetailEO.getAmount() +",调账数量:"+outamount +",盘盈 "+outamount+"个");
            }else{
                adjustInventoryEO.setInventoryType(2);
                adjustInventoryEO.setDifAmount(lessamount);
                adjustInventoryEO.setAdjAmount(lessamount);
                adjustInventoryEO.setRemark("库存数量:"+ inventoryDetailEO.getAmount() +",调账数量:"+lessamount +",盘亏 "+lessamount+"个");
            }

            adjustInventoryEO.setStatus(0);
            adjustInventoryEO.setAdjustUserId(userEO.getUserId());
            adjustInventoryEO.setAdjustUserName(userEO.getUserName());
            adjustInventoryEO.setInventoryDate(date);
            boolean flag = super.save(adjustInventoryEO);

            //更新盘点明细表中的调账状态及调账表创建人
            this.baseMapper.updateInventoryDetailAdjustStatus1(inventoryDetailEO.getInventoryDetailId(),userEO.getUserId(),userEO.getUserName());

        }

        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }


        String successMsg = "已全部成功添加到调节表<br/>";
        return successMsg ;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdjust(Long[] idList,Long inventoryId) throws BusinessException{
        for(Long AdjustId : idList){
            //更新盘点明细表中的调账状态及调账表创建人
            this.baseMapper.updateInventoryDetailAdjustStatus0(AdjustId,null,"");

        }
        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);

        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }


        return this.removeByIds(idList);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updatewaitingAdjust(AdjustInventoryEO[] adjustInventoryEOS) throws BusinessException {
        if(adjustInventoryEOS!=null && adjustInventoryEOS.length==0){
            throw new BusinessException("无数据!");
        }


        for(AdjustInventoryEO adjustInventoryEO : adjustInventoryEOS) {
            this.updateById(adjustInventoryEO);

        }

        String successMsg = "已成功保存";
        return successMsg;
    }

    @Transactional(rollbackFor = Exception.class)
    public String insertStock(Long[] adjustIds, Long inventoryId, UserEO userEO) throws BusinessException {
        if(adjustIds==null || adjustIds.length==0){
            throw new BusinessException("请选择数据！");
        }

        int sum = 0;
        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);
        for(Long adjustId : adjustIds){

            AdjustInventoryEO adjustInventoryEO = this.baseMapper.getadjustEO(adjustId);

            StockAccountEO accountEO = new StockAccountEO();

            accountEO.setVoucherId(adjustInventoryEO.getInventoryDetailId());
            accountEO.setVoucherDate(inventoryEO.getInventoryDate());
            double inventoryType=adjustInventoryEO.getInventoryType();

            /*double outamount=adjustInventoryEO.getAdjAmount()-adjustInventoryEO.getAmount();
            double lessamount = 0-outamount;*/
            if (inventoryType == 1 ){
                accountEO.setVoucherType(6);
                accountEO.setChildVoucherType(7);
                accountEO.setRemarks("库存数量:"+ adjustInventoryEO.getAmount() +",调账数量:"+adjustInventoryEO.getAdjAmount() +",盘盈 "+adjustInventoryEO.getAdjAmount()+"个");
            }else{
                accountEO.setVoucherType(12);
                accountEO.setChildVoucherType(6);
                accountEO.setRemarks("库存数量:"+ adjustInventoryEO.getAmount() +",调账数量:"+adjustInventoryEO.getAdjAmount() +",盘亏 "+adjustInventoryEO.getAdjAmount()+"个");
            }
            accountEO.setAmount(adjustInventoryEO.getAdjAmount());

            accountEO.setMaterialId(adjustInventoryEO.getMaterialId());
            accountEO.setMaterialCode(adjustInventoryEO.getMaterialCode());
            accountEO.setMaterialName(adjustInventoryEO.getMaterialName());
            accountEO.setInventoryCode(adjustInventoryEO.getInventoryCode());
            accountEO.setElementNo(adjustInventoryEO.getElementNo());
            accountEO.setSpecification(adjustInventoryEO.getSpecification());
            accountEO.setUnitId(adjustInventoryEO.getUnitId());
            accountEO.setFigureNumber(adjustInventoryEO.getFigureNumber());
            accountEO.setFigureVersion(adjustInventoryEO.getFigureVersion());
            accountEO.setWarehouseId(adjustInventoryEO.getWarehouseId());
            accountEO.setWarehouseLocationId(adjustInventoryEO.getWarehouseLocationId());

            //保存台账
            boolean flag = this.stockAccountService.save(accountEO);

            //更新调节表中的调账状态和库存表ID
            this.baseMapper.updateAdjustStatus(adjustId,1);

            //更新盘点明细表中的调账状态
            this.baseMapper.updateInventoryDetailAdjustStatus2(adjustInventoryEO.getInventoryDetailId());


            if (flag) {
                sum += 1;
            }
        }

        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else if (Math.abs(Doneamount-difamount)<=0 && Doneamount >0){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,3);
        }
        else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }

        String successMsg = "已提交调账" + sum + "条<br/>";
        return successMsg ;
    }


    @Transactional(rollbackFor = Exception.class)
    public String insertAllStock(Long inventoryId, UserEO userEO) throws BusinessException {

        int sum = 0;
        List<AdjustInventoryEO> adjustInventoryEOS = this.baseMapper.getAdjustInventoryEOs(inventoryId);
        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);
        for(AdjustInventoryEO adjustInventoryEO : adjustInventoryEOS){


            StockAccountEO accountEO = new StockAccountEO();

            accountEO.setVoucherId(adjustInventoryEO.getInventoryDetailId());
            accountEO.setVoucherDate(inventoryEO.getInventoryDate());
            double inventoryType=adjustInventoryEO.getInventoryType();
            /*double outamount=adjustInventoryEO.getAdjAmount()-adjustInventoryEO.getAmount();
            double lessamount = 0-outamount;*/
            if (inventoryType == 1 ){
                accountEO.setVoucherType(6);
                accountEO.setChildVoucherType(7);
                accountEO.setRemarks("库存数量:"+ adjustInventoryEO.getAmount() +",调账数量:"+adjustInventoryEO.getAdjAmount() +",盘盈 "+adjustInventoryEO.getAdjAmount()+"个");

            }else{
                accountEO.setVoucherType(12);
                accountEO.setChildVoucherType(6);
                accountEO.setRemarks("库存数量:"+ adjustInventoryEO.getAmount() +",调账数量:"+adjustInventoryEO.getAdjAmount() +",盘亏 "+adjustInventoryEO.getAdjAmount()+"个");

            }
            accountEO.setAmount(adjustInventoryEO.getAdjAmount());
            accountEO.setRemarks(adjustInventoryEO.getRemark());

            accountEO.setMaterialId(adjustInventoryEO.getMaterialId());
            accountEO.setMaterialCode(adjustInventoryEO.getMaterialCode());
            accountEO.setMaterialName(adjustInventoryEO.getMaterialName());
            accountEO.setInventoryCode(adjustInventoryEO.getInventoryCode());
            accountEO.setElementNo(adjustInventoryEO.getElementNo());
            accountEO.setSpecification(adjustInventoryEO.getSpecification());
            accountEO.setUnitId(adjustInventoryEO.getUnitId());
            accountEO.setFigureNumber(adjustInventoryEO.getFigureNumber());
            accountEO.setFigureVersion(adjustInventoryEO.getFigureVersion());
            accountEO.setWarehouseId(adjustInventoryEO.getWarehouseId());
            accountEO.setWarehouseLocationId(adjustInventoryEO.getWarehouseLocationId());

            //保存台账
            boolean flag = this.stockAccountService.save(accountEO);

            //更新调节表中的调账状态和库存表ID
            this.baseMapper.updateAdjustStatus(adjustInventoryEO.getAdjustId(),1);

            //更新盘点明细表中的调账状态
            this.baseMapper.updateInventoryDetailAdjustStatus2(adjustInventoryEO.getInventoryDetailId());


            if (flag) {
                sum += 1;
            }
        }

        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else if (Math.abs(Doneamount-difamount)<=0 && Doneamount >0){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,3);
        }
        else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }

        String successMsg = "已全部提交调账" + sum + "条<br/>";
        return successMsg ;
    }


    @Transactional(rollbackFor = Exception.class)
    public String doneStock(Long inventoryId) throws BusinessException {


        //更新盘点表的状态为已完成
        this.baseMapper.updateInventoryStatus(inventoryId);


        String successMsg = "已全部设置为调账完成<br/>";
        return successMsg ;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStock(Long[] idList,Long inventoryId) throws BusinessException{
        //批量删除台账
        this.stockAccountService.removeByIds(idList);

        for(Long accountId : idList){

            //更新盘点明细表中的调账状态
            this.baseMapper.deleteInventoryDetailAdjustStatus(accountId,1);

            //更新盘点表中的调账状态
            //this.baseMapper.updateInventoryAdjustStatus(inventoryId);

            //更新调节表中的调账状态和库存表ID
            this.baseMapper.deleteAdjustStatus(accountId,0);
        }

        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);
        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else if (Math.abs(Doneamount-difamount)<=0 && Doneamount >0){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,3);
        }
        else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public String deleteAllStock(Long inventoryId) throws BusinessException {

        //全部删除台账
        this.baseMapper.deleteAllStock(inventoryId);

        //更新盘点明细表中的调账状态
        this.baseMapper.deleteAllInventoryDetailStatus(inventoryId,1);


        //更新调节表中的调账状态和库存表ID
        this.baseMapper.deleteAllAdjustStatus(inventoryId,0);

        InventoryEO inventoryEO = this.inventoryService.getById(inventoryId);
        //待调账数量
        Double waitamount = this.baseMapper.getwaitAdjustAmount(inventoryId);

        //已调账数量
        Double Doneamount = this.baseMapper.getDoneAdjustAmount(inventoryId);

        //盘点差异物料数量
        Double difamount =inventoryEO.getDifMaterialAmount();


        if (Doneamount < difamount && Doneamount >0 ){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,2);
        }else if (Math.abs(Doneamount-difamount)<=0 && Doneamount >0){
            this.baseMapper.updateInventoryAdjustStatus(inventoryId,3);
        }
        else{
            if(waitamount>0){
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,1);
            }else {
                this.baseMapper.updateInventoryAdjustStatus(inventoryId,0);
            }

        }

        String successMsg = "已全部全部取消调账";
        return successMsg ;
    }

}
