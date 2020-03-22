package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.PbomEO;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.bsc.service.WarehouseService;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.scm.wms.entity.*;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.wms.mapper.InventoryMapper;
import com.xchinfo.erp.wms.mapper.LinedgeInventoryMapper;
import com.xchinfo.erp.wms.mapper.StockAccountMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.*;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class LinedgeInventoryService extends BaseServiceImpl<LinedgeInventoryMapper, LinedgeInventoryEO> {

    @Autowired
    private LinedgeInventoryDetailService linedgeInventoryDetailService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private LinedgeInventoryMaterialAssignmentService linedgeInventoryMaterialAssignmentService;

    @Autowired
    private TempInventoryService tempInventoryService;

    @Autowired
    private InventoryMapper inventoryMapper;

    public IPage<LinedgeInventoryDetailEO> selectDetailPage(Criteria criteria) {

        return this.linedgeInventoryDetailService.selectPage(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void importFromExcel(List list,String inventoryMonth) throws BusinessException{
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        if(list != null){
            if(list.size()>0){
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                String errorMsg = "";
                //判断是否为空
                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);
                        String tempErrorMsg="";
                        String elementNo = map.get("0")==null?null:((String) map.get("0")).trim();
                        if(elementNo==null || elementNo.equals("")) {
                            tempErrorMsg+="&nbsp;零件号为空&nbsp;";
                        }
                        String materialName = map.get("1")==null?null:((String) map.get("1")).trim();
                        if(materialName==null || materialName.equals("")) {
                            tempErrorMsg+="&nbsp;零件名称为空&nbsp;";
                        }
                        String locationBarCode = map.get("2")==null?null:((String) map.get("2")).trim();

                        String amount = map.get("3")==null?null:((String) map.get("3")).trim();
                        if(amount==null || amount.equals("")) {
                            tempErrorMsg+="&nbsp;数量为空&nbsp;";
                        }

                        int sorti = i+1;
                        if(!tempErrorMsg.equals("")){
                            errorMsg+="第"+sorti+"行&nbsp;"+tempErrorMsg+"<br>";
                        }
                        tempErrorMsg="";
                        for(int j=1; j<mapList.size(); j++) {
                            if(i>=j) continue;
                            int sortj = j+1;
                            Map temp = mapList.get(j);
                            String elementNoTemp = temp.get("0")==null?null:((String) temp.get("0")).trim();
                            if(elementNoTemp!=null && !elementNoTemp.equals("")
                                    &&elementNo!=null && !elementNo.equals("")){
                                if(elementNo.equals(elementNoTemp)){
                                    tempErrorMsg+="第"+sorti+"行零件号与第"+sortj+"行零件号重复<br>";
                                }
                            }
                        }
                        if(!tempErrorMsg.equals("")){
                            errorMsg+=tempErrorMsg;
                        }
                    }
                }
                if(!errorMsg.equals("")) {
                    throw new BusinessException(errorMsg); }
                //查看当前月份是否存在已发布的盘点单，有则报错
                int count =  this.baseMapper.getCount(inventoryMonth,user.getOrgId());
                if(count>0) {
                    throw new BusinessException("当前月份已存在发布过后的线边盘点单"); }

                //查看当月是否存在新建状态的盘点单，有则更新，无则新建
                LinedgeInventoryEO linedgeInventoryEOs =  this.baseMapper.getLinedgeInventory(inventoryMonth,user.getOrgId());
                if(linedgeInventoryEOs != null ) {
                    this.removeById(linedgeInventoryEOs.getLinedgeInventoryId());
                    this.baseMapper.removeByLinedgeInventoryId(linedgeInventoryEOs.getLinedgeInventoryId());

                }

                LinedgeInventoryEO linedgeInventoryEO = new LinedgeInventoryEO();

                linedgeInventoryEO.setInventoryMonth(inventoryMonth);
                linedgeInventoryEO.setInventoryDate(new Date());
                linedgeInventoryEO.setOrgId(user.getOrgId());
                linedgeInventoryEO.setStatus(2);
                linedgeInventoryEO.setInventoryUserId(user.getUserId());
                linedgeInventoryEO.setInventoryUserName(user.getRealName());

                this.save(linedgeInventoryEO);

                List<LinedgeInventoryDetailEO> dataList = new ArrayList<LinedgeInventoryDetailEO>();
                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);

                        String elementNo = (String) map.get("0");
                        String materialName = (String) map.get("1");
                        String locationBarCode = (String) map.get("2");
                        Double amount =Double.parseDouble((String) map.get("3"));
                        LinedgeInventoryDetailEO temp = new LinedgeInventoryDetailEO();

                        temp.setLinedgeInventoryId(linedgeInventoryEO.getLinedgeInventoryId());

                        temp.setElementNo(elementNo);
                        temp.setInventoryMonth(inventoryMonth);
                        temp.setMaterialName(materialName);
                        MaterialEO materialEO = this.baseMapper.getMaterialEO(elementNo,user.getOrgId());
                        if (materialEO == null){
                            throw new BusinessException("在物料表中不存在该零件号");
                        }
                        temp.setInventoryCode(materialEO.getInventoryCode());
                        temp.setMaterialId(materialEO.getMaterialId());

                        if (locationBarCode != null && !locationBarCode.equals("")){
                            temp.setLocationBarCode(locationBarCode);
                            WarehouseLocationEO warehouseLocationEO  = this.baseMapper.getLocationId(locationBarCode,user.getOrgId());
                            if(warehouseLocationEO!= null){
                                temp.setWarehouseLocationId(warehouseLocationEO.getWarehouseLocationId());

                                WarehouseEO warehouseEO  = this.warehouseService.getById(warehouseLocationEO.getWarehouseId());
                                temp.setWarehouseId(warehouseLocationEO.getWarehouseId());
                                temp.setWarehouseBarCode(warehouseEO.getBarCode());
                            }

                        }


                        temp.setAmount(amount);
                        temp.setStatus(2);
                        temp.setOrgId(user.getOrgId());
                        temp.setLocationBarCode(locationBarCode);
                        dataList.add(temp);
                    }

                }

                if(!errorMsg.equals("")) {
                    throw new BusinessException(errorMsg);
                }
                //保存明细数据
                if(dataList!=null && dataList.size()>0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        this.linedgeInventoryDetailService.save(dataList.get(i));
                    }
                }

            }else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long[] idList, Integer status,Integer oldStatus) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

        for(Long id:idList) {
            LinedgeInventoryEO linedgeInventoryEO = this.baseMapper.selectById(id);
            if (status==1){
                List<LinedgeInventoryDetailEO> linedgeInventoryDetailEOs = this.baseMapper.getlinedgeInventoryDetails(linedgeInventoryEO.getLinedgeInventoryId());
                for(LinedgeInventoryDetailEO linedgeInventoryDetailEO:linedgeInventoryDetailEOs) {

                    List<PbomEO> pbomEOS = this.baseMapper.getPbomEOs(linedgeInventoryDetailEO.getMaterialId(),user.getOrgId());
                    if(pbomEOS!=null && pbomEOS.size()!=0) {
                        for (PbomEO pbomEO : pbomEOS) {
                            if (pbomEO != null) {
                                /*if (pbomEO.getChildMaterialId() == 0 ){
                                    continue;
                                }*/

                                // 判断数据是否已经存在,存在则删除
                                List<LinedgeInventoryMaterialAssignmentEO> limaFromDb = this.linedgeInventoryMaterialAssignmentService.getList(linedgeInventoryEO.getLinedgeInventoryId(), linedgeInventoryDetailEO.getInventoryLinedgeDetailId(), pbomEO.getChildMaterialId(), 0);
                                if (limaFromDb != null && limaFromDb.size() > 0) {
                                    HashSet set = new HashSet();
                                    for (LinedgeInventoryMaterialAssignmentEO lima : limaFromDb) {
                                        set.add(lima.getMaterialAssignmentId());
                                    }
                                    this.linedgeInventoryDetailService.removeByIds(set);
                                }

                                LinedgeInventoryMaterialAssignmentEO linedgeInventoryMaterialAssignmentEO = new LinedgeInventoryMaterialAssignmentEO();

                                linedgeInventoryMaterialAssignmentEO.setLinedgeInventoryId(linedgeInventoryEO.getLinedgeInventoryId());
                                linedgeInventoryMaterialAssignmentEO.setInventoryLinedgeDetailId(linedgeInventoryDetailEO.getInventoryLinedgeDetailId());
                                linedgeInventoryMaterialAssignmentEO.setInventoryMonth(linedgeInventoryEO.getInventoryMonth());
                                linedgeInventoryMaterialAssignmentEO.setOrgId(linedgeInventoryEO.getOrgId());
                                linedgeInventoryMaterialAssignmentEO.setMaterialId(pbomEO.getChildMaterialId());
                                linedgeInventoryMaterialAssignmentEO.setElementNo(pbomEO.getChildElementNo());
                                linedgeInventoryMaterialAssignmentEO.setMaterialName(pbomEO.getChildMaterialName());
                                if (pbomEO.getChildMaterialId() != 0) {
                                    MaterialEO materialEO = this.materialService.getById(pbomEO.getChildMaterialId());
                                    linedgeInventoryMaterialAssignmentEO.setInventoryCode(materialEO.getInventoryCode());
                                }
                                Double MaterialAssignmentAmount = pbomEO.getAmount() * linedgeInventoryDetailEO.getAmount();
                                linedgeInventoryMaterialAssignmentEO.setAmount(MaterialAssignmentAmount);
                                StampingMaterialConsumptionQuotaEO stampingMaterialConsumptionQuotaEO = this.baseMapper.getConsumptionByElement(pbomEO.getChildElementNo(), user.getOrgId());
                                if (stampingMaterialConsumptionQuotaEO != null) {
                                    Double MaterialAssignmentWeight = MaterialAssignmentAmount * stampingMaterialConsumptionQuotaEO.getGrossWeight();
                                    linedgeInventoryMaterialAssignmentEO.setWeight(MaterialAssignmentWeight);
                                }
                                linedgeInventoryMaterialAssignmentEO.setStatus(0);
                                this.linedgeInventoryMaterialAssignmentService.save(linedgeInventoryMaterialAssignmentEO);
                            }

                        }
                    }
                }
            }else{
                //删除线边盘点物料分配表信息
                this.baseMapper.deleteMaterialAssignmentBylinedgeInventoryId(linedgeInventoryEO.getLinedgeInventoryId());
            }

            //更新状态
            this.baseMapper.updateStatusById(id, status, oldStatus);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean beginInventory(String inventoryDate,UserEO userEO) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        //Long orgId = this.baseMapper.getOrgId(userId);

        // 检查对应机构下对应日期是否存在未完成的盘点单
        List<InventoryEO> inventorys = this.inventoryMapper.selectInventoryNotFinish(userEO.getOrgId(),inventoryDate);
        if(inventorys==null || inventorys.size()==0) {
            throw new BusinessException("该机构下不存在" + inventoryDate + "的未完成的盘点单");
        }
        if(inventorys.size() > 1) {
            throw new BusinessException("该机构下不存在" + inventoryDate + "的未完成的盘点单不止一个");
        }

        //检查对应机构下对应日期是否已存在线边盘点单
        int count = this.baseMapper.selectInventory(userEO.getOrgId(),inventoryDate);
        if (count>0){
            throw new BusinessException("该机构下已存在线边盘点单");
        }

        LinedgeInventoryEO linedgeInventoryEO =new LinedgeInventoryEO();

        // 设置盘点单ID
        linedgeInventoryEO.setInventoryId(inventorys.get(0).getInventoryId());

        /*// 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_inventory", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);*/

        //设置盘点月份
        linedgeInventoryEO.setInventoryMonth(inventoryDate.substring(0, 7));

        //设置盘点日期
        linedgeInventoryEO.setInventoryDate(DateUtils.stringToDate(inventoryDate, "yyyy-MM-dd"));

        //设置归属机构
        linedgeInventoryEO.setOrgId(userEO.getOrgId());

        //设置状态
        linedgeInventoryEO.setStatus(0);

        linedgeInventoryEO.setInventoryUserId(user.getUserId());

        linedgeInventoryEO.setInventoryUserName(user.getRealName());


        // 先保存盘点单对象
        if (!retBool(this.baseMapper.insert(linedgeInventoryEO)))
            throw new BusinessException("保存线边盘点单失败！");

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean finishInventory(UserEO userEO,Long id) throws BusinessException {

        LinedgeInventoryEO linedgeInventoryEO = this.getById(id);

        List<TempInventoryEO> tempInventorys = this.baseMapper.getTempInventory(id);
        for(TempInventoryEO tempInventoryEO:tempInventorys){
            MaterialEO materialEO = this.materialService.getById(tempInventoryEO.getMaterialId());

            LinedgeInventoryDetailEO linedgeInventoryDetailEO = new LinedgeInventoryDetailEO();
            linedgeInventoryDetailEO.setLinedgeInventoryId(linedgeInventoryEO.getLinedgeInventoryId());
            linedgeInventoryDetailEO.setElementNo(materialEO.getElementNo());
            linedgeInventoryDetailEO.setInventoryMonth(linedgeInventoryEO.getInventoryMonth());
            linedgeInventoryDetailEO.setMaterialName(materialEO.getMaterialName());
            linedgeInventoryDetailEO.setInventoryCode(materialEO.getInventoryCode());
            linedgeInventoryDetailEO.setMaterialId(tempInventoryEO.getMaterialId());
            linedgeInventoryDetailEO.setAmount(tempInventoryEO.getAmount());
            linedgeInventoryDetailEO.setInventoryNo(tempInventoryEO.getInventoryNo());
            linedgeInventoryDetailEO.setWarehouseLocationId(tempInventoryEO.getLocationId());
            linedgeInventoryDetailEO.setStatus(0);
            linedgeInventoryDetailEO.setOrgId(userEO.getOrgId());
            this.linedgeInventoryDetailService.save(linedgeInventoryDetailEO);
        }


        //将临时盘点单的状态改为已使用
        this.baseMapper.updateTempStatus(id);
        //盘点单和盘点明细单状态改为完成
        this.baseMapper.updateStatusById(id, 2, 0);

        return true;
    }

    public MaterialEO getMaterialInfo(String elementNo,Long orgId) throws BusinessException {
        //ID为物料ID,查询是否存在盘点单
        MaterialEO materialEO = this.baseMapper.getMaterialEO(elementNo,orgId);
        if(null == materialEO){
            throw new BusinessException("物料零件号不存在，请确认！");
        }

       /* InventoryDetailEO inventoryDetailEO = this.baseMapper.selectExistIncentoryDetail(inventoryEO.getInventoryId(),elementNo);
        if(null ==  inventoryDetailEO){
            throw new BusinessException("不存在该物料的盘点明细，请确认！");
        }*/

        return materialEO;
    }



    public boolean inventorBlindyOne(Long Id, Double amount, Long locationId,String action, UserEO userEO, String inventoryNo) throws BusinessException {
        InventoryDetailEO detailEO = new InventoryDetailEO();
        if("add".equals(action)) {
            //Id为盘点明细ID

            MaterialEO materialEO = this.materialService.getById(Id);
            LinedgeInventoryEO linedgeInventoryEO = this.baseMapper.getLinedgeInventoryByOrgId(userEO.getOrgId());

            if(linedgeInventoryEO == null ){
                throw new BusinessException("没有找到对应的线边盘点单");
            }

            //查看是否存在相同物料和库位的数据，有的话就覆盖之前的数量
            TempInventoryEO tempInventoryEOByMaterialAndLocationId = this.baseMapper.getTempInventoryByMaterialAndLocationId(linedgeInventoryEO.getLinedgeInventoryId(),materialEO.getMaterialId(),locationId);
            if (tempInventoryEOByMaterialAndLocationId != null){
                tempInventoryEOByMaterialAndLocationId.setAmount(amount);
                this.tempInventoryService.updateById(tempInventoryEOByMaterialAndLocationId);

            }else{
                //新增临时盘点单
                TempInventoryEO tempInventoryEO = new TempInventoryEO();
                tempInventoryEO.setInventoryId(linedgeInventoryEO.getLinedgeInventoryId());
                tempInventoryEO.setMaterialId(materialEO.getMaterialId());
                tempInventoryEO.setLocationId(locationId);
                tempInventoryEO.setInventoryNo(inventoryNo);
                tempInventoryEO.setAmount(amount);
                tempInventoryEO.setInventoryMonth(linedgeInventoryEO.getInventoryMonth());
                tempInventoryEO.setOrgId(linedgeInventoryEO.getOrgId());
                tempInventoryEO.setInventoryDate(new Date());
                tempInventoryEO.setInventoryUserId(userEO.getUserId()+"");
                tempInventoryEO.setInventoryUserName(userEO.getUserName());
                tempInventoryEO.setStatus(0);

                this.tempInventoryService.save(tempInventoryEO);
            }

        }
        /*else{
            //Id为临时盘点的tempId
            TempInventoryEO tempInventoryEO = this.tempInventoryService.getById(Id);
            InventoryEO inventoryEO = this.baseMapper.selectById(tempInventoryEO.getInventoryId());
            if(inventoryEO.getStatus() == 2){
                throw new BusinessException("改次盘点已结束,不能进行撤回操作，请确认！");
            }

            this.tempInventoryService.removeById(tempInventoryEO.getTempId());

        }*/


        return true;
    }
}
