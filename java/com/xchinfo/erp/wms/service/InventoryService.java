package com.xchinfo.erp.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.scm.wms.entity.*;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.wms.mapper.InventoryDetailMapper;
import com.xchinfo.erp.wms.mapper.InventoryMapper;
import com.xchinfo.erp.wms.mapper.LinedgeInventoryMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/4/18
 * @update
 */
@Service
public class InventoryService extends BaseServiceImpl<InventoryMapper, InventoryEO> {


    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    @Autowired
    private InventoryDetailService IInventoryDetailService;

    @Autowired
    private TempInventoryService tempInventoryService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private LinedgeInventoryMapper linedgeInventoryMapper;


    @Override
    public IPage<InventoryEO> selectPage(Criteria criteria) {
        IPage<InventoryEO> orders = super.selectPage(criteria);

        // 列表查找盘点单明细
       /* for (InventoryEO order : orders.getRecords()){
            List<InventoryDetailEO> details = this.inventoryDetailMapper.selectByInventory(order.getInventoryId());
            order.setDetails(details);
        }*/

        return orders;
    }



    @Transactional(rollbackFor = Exception.class)
    public InventoryEO saveEntity(InventoryEO entity) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)) {
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_inventory", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);

        // 先保存盘点单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存盘点单失败！");

        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {
        Integer result = 0;
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();
        // 删除盘点单明细
        for (Serializable id : idList){


            InventoryEO inventoryEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(inventoryEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            result  = this.inventoryDetailMapper.removeByInventoryId((Long) id);

            if (null == result || result < 0){
                throw new BusinessException("删除盘点单明细失败！");
            }

        }

        return super.removeByIds(idList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(InventoryEO entity) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        //校验机构权限
        if(!checkPer(entity.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }
        return super.updateById(entity);
    }

    @Override
    public InventoryEO getById(Serializable id) {
        InventoryEO inventory = super.getById(id);

        // 查找盘点单明细
        List<InventoryDetailEO> details = this.inventoryDetailMapper.selectByInventory((Long) id);
        inventory.setDetails(details);

        return inventory;
    }



    public List<InventoryDetailEO> listDetailsByInventory(Long inventoryId) {
        return this.inventoryDetailMapper.selectByInventory(inventoryId);
    }


    public IPage<InventoryDetailEO> selectDetailPage(Criteria criteria){
        return this.IInventoryDetailService.selectPage(criteria);
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDetailIds(Serializable[] idList) throws BusinessException{

        return this.IInventoryDetailService.removeByIds(idList);
    }


    public boolean updateStatusById(Long[] idList, Integer status) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        Long userId = user.getUserId();

        for(Long id:idList){

            InventoryEO inventoryEO = this.baseMapper.selectById(id);
            //校验机构权限
            if(!checkPer(inventoryEO.getOrgId(),userId)){
                throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
            }

            if (status==0){

                int count = this.baseMapper.selectCompleteDetail(id);
                if(count > 0){
                    throw new BusinessException("取消发布失败，订单明细中存在已完成的数据状态!");
                }
            }

            this.baseMapper.updateStatusById(id,status);
        }

        return true;
    }



    public InventoryEO getDetailInfoByNo(Long Id) throws BusinessException {
        InventoryEO inventoryEO = this.baseMapper.selectById(Id);
        //校验机构权限
        if(null != inventoryEO) {
            UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
            String userName = user.getUserName();
            Long userId = user.getUserId();

            if (!checkPer(inventoryEO.getOrgId(),userId)) {
                throw new BusinessException("盘点单的归属机构下的数据该用户没有操作权限，请确认！");
            }
        }

        List<InventoryDetailEO> details = this.baseMapper.getByInventoryId(inventoryEO.getInventoryId());
        inventoryEO.setDetails(details);
        return inventoryEO;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean inventoryOne(Long Id, Double amount, String action, Long userId, String userName) throws BusinessException {
        InventoryDetailEO detailEO = this.IInventoryDetailService.getById(Id);
        //判断状态
        if("add".equals(action) && detailEO.getStatus() != 1){
            throw new BusinessException("盘点单明细状态非已发布状态，请刷新确认");
        }else if("remove".equals(action) && detailEO.getStatus() != 2){
            throw new BusinessException("盘点单明细状态非已完成状态，请刷新确认");
        }

        if(null == detailEO.getAmount() || "".equals(detailEO.getAmount())){
            detailEO.setAmount(0d);
        }

        if("add".equals(action)) {
            detailEO.setStatus(2);
            detailEO.setRelAmount(amount);

            if(amount > detailEO.getAmount()){
                detailEO.setInventoryType(1);
            }else{
                detailEO.setInventoryType(2);
            }

        }else{
            detailEO.setStatus(1);
            detailEO.setRelAmount(0d);
            detailEO.setInventoryType(0);
        }

        this.IInventoryDetailService.updateById(detailEO);

        //判断盘点明细是否都已完成
        InventoryEO inventoryEO = this.baseMapper.selectById(detailEO.getInventoryId());
        if("add".equals(action) && (inventoryEO.getStatus() == 0 || inventoryEO.getStatus() == 2)){
            throw new BusinessException("盘点单状态为新建或已完成状态、不能进行盘点操作,请确认!");
        }

        if (!checkPer(inventoryEO.getOrgId(),userId)) {
            throw new BusinessException("盘点单的归属机构下的数据该用户没有操作权限，请确认！");
        }

        Integer finishCount = this.baseMapper.selectDetailFinishCount(inventoryEO.getInventoryId());
        if(finishCount > 0){
            inventoryEO.setStatus(1);
            inventoryEO.setInventoryUserId("");
            inventoryEO.setInventoryUserName("");
        }else {
            inventoryEO.setStatus(2);
            inventoryEO.setInventoryUserId(userId+"");
            inventoryEO.setInventoryUserName(userName);
        }

        this.baseMapper.updateById(inventoryEO);

        return true;
    }


    public InventoryDetailEO getDetailById(Long Id) {
        return this.inventoryDetailMapper.selectById(Id);
    }


    public boolean updateDetailById(InventoryDetailEO entity) throws BusinessException {

        return this.IInventoryDetailService.updateById(entity);

    }


    public boolean deleteDetailById(Long id) throws BusinessException {
        return this.baseMapper.deleteDetailById(id);
    }


    public Boolean checkPer(Long orgId,Long userId){

        return this.orgService.checkUserPermissions(orgId,userId);
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean beginInventory(UserEO userEO,String beginDate) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        //Long orgId = this.baseMapper.getOrgId(userId);

        //检查对应机构下是否还有盘点单未完成
        int count = this.baseMapper.selectInventory(userEO.getOrgId());
        if (count>0){
            throw new BusinessException("存在未完成或者调账未完成的盘点单，请先完成盘点后再开始盘点");
        }

        InventoryEO entity =new InventoryEO();

        // Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(beginDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Month=beginDate.substring(0,7);

        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_inventory", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);

        //设置盘点日期
        entity.setInventoryDate(date);

        //设置盘点日期
        entity.setInventoryMonth(Month);

        //设置归属机构
        entity.setOrgId(userEO.getOrgId());

        //设置状态
        entity.setStatus(0);

        //设置调账状态，默认无需调账
        entity.setAdjustStatus(4);

        // 先保存盘点单对象
        if (!retBool(this.baseMapper.insert(entity)))
            throw new BusinessException("保存盘点单失败！");

//        InventoryDetailEO inventoryDetailEO =new InventoryDetailEO();

        // 保存盘点单明细对象
        if (!retBool(this.baseMapper.insertdetail(entity.getInventoryId(),entity.getOrgId(),userEO.getOrgId(),Month,beginDate)))
            throw new BusinessException("保存盘点单失败！");

        this.baseMapper.updateAmount(entity.getInventoryId());

        return true;
    }

    private void updateLinedgeAmountNew(Long inventoryId) {
        LinedgeInventoryEO linedgeInventory = this.linedgeInventoryMapper.getLinedgeInventoryByInventoryId(inventoryId);
        if(linedgeInventory != null) {
            this.baseMapper.updateLinedgeAmountNew(inventoryId, linedgeInventory.getLinedgeInventoryId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean finishInventory(UserEO userEO,Long id) throws BusinessException {

        //将临时盘点单的实际数量更新到盘点单明细中
        this.baseMapper.updateRelAcount(userEO.getOrgId());

        //将临时盘点单的状态改为已使用
        this.baseMapper.updateTempStatus(userEO.getOrgId());

        //获取最新的库存数量并更新到盘点明细表中
        this.baseMapper.updateNewAmount(userEO.getOrgId(),id);

        //获取最新的线边盘点明细数量并更新到盘点明细表中
//        this.baseMapper.updateLinedgeAmount(id);


        //获取最新的线边盘点明细拆分数量并更新到盘点明细表中
//        this.baseMapper.updateLinedgeAssignmentAmount(id);

        this.updateLinedgeAmountNew(id);

        //更新线边盘点数量到盘点总数量（线边盘点数量=盘点总数量）
        this.baseMapper.updateLinedgeLinedgeAmount(id);

        //更新盘点总数量（盘点实际数量+线边盘点数量=盘点总数量）
        this.baseMapper.updateLinedgeTotalAmount(id);

        //将所有未盘点的数量全部更新为0
        this.baseMapper.updateRelAcountToZero(id);

        //获取盘点差异物料数量并更新到盘点表中
        this.baseMapper.updateDifMaterialAmount(id);

        //根据盘点差异物料数量更新盘点单状态
        this.baseMapper.updateInventoryAdjustStatus(id);

        //根据实际盘点数量更新库存
       /* List<InventoryDetailEO> inventoryDetailEOS = this.baseMapper.getdifamount(userEO.getOrgId());
        for ( InventoryDetailEO inventoryDetailEO :inventoryDetailEOS) {

            StockAccountEO accountEO = new StockAccountEO();

            accountEO.setVoucherId(inventoryDetailEO.getInventoryDetailId());
            accountEO.setVoucherDate(inventoryDetailEO.getCreatedTime());
            double outamount=inventoryDetailEO.getRelAmount()-inventoryDetailEO.getAmount();
            double lessamount = 0-outamount;
            if (outamount>0 ){
                accountEO.setVoucherType(6);
                accountEO.setChildVoucherType(7);
                accountEO.setAmount(outamount);
                accountEO.setRemarks("库存数量:"+ inventoryDetailEO.getAmount() +",盘点数量:"+inventoryDetailEO.getRelAmount() +",盘盈 "+outamount+"个");
            }else{
                accountEO.setVoucherType(12);
                accountEO.setChildVoucherType(6);
                accountEO.setAmount(lessamount);
                accountEO.setRemarks("库存数量:"+ inventoryDetailEO.getAmount() +",盘点数量:"+inventoryDetailEO.getRelAmount() +",盘亏 "+lessamount+"个");
            }

            accountEO.setMaterialId(inventoryDetailEO.getMaterialId());
            accountEO.setMaterialCode(inventoryDetailEO.getMaterialCode());
            accountEO.setMaterialName(inventoryDetailEO.getMaterialName());
            accountEO.setInventoryCode(inventoryDetailEO.getInventoryCode());
            accountEO.setElementNo(inventoryDetailEO.getElementNo());
            accountEO.setSpecification(inventoryDetailEO.getSpecification());
            accountEO.setUnitId(inventoryDetailEO.getUnitId());
            accountEO.setFigureNumber(inventoryDetailEO.getFigureNumber());
            accountEO.setFigureVersion(inventoryDetailEO.getFigureVersion());
            accountEO.setWarehouseId(inventoryDetailEO.getWarehouseId());
            accountEO.setWarehouseLocationId(inventoryDetailEO.getWarehouseLocationId());

            //保存台账
            this.stockAccountService.save(accountEO);
        }*/

        //盘点单和盘点明细单状态改为完成
        this.baseMapper.updateInventoryStatus(userEO.getOrgId());

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateLinedge(UserEO userEO,Long id) throws BusinessException {

        //获取最新的线边盘点明细数量并更新到盘点明细表中
//        this.baseMapper.updateLinedgeAmount(id);


        //获取最新的线边盘点明细拆分数量并更新到盘点明细表中
//        this.baseMapper.updateLinedgeAssignmentAmount(id);

        this.updateLinedgeAmountNew(id);

        //更新线边盘点数量到盘点总数量（线边盘点数量=盘点总数量）
        this.baseMapper.updateLinedgeLinedgeAmount(id);

        //更新盘点总数量（盘点实际数量+线边盘点数量=盘点总数量）
        this.baseMapper.updateLinedgeTotalAmount(id);

        return true;
    }

    public boolean unlockInventory(Long id, Integer status) throws BusinessException {

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        Long userId = user.getUserId();

        InventoryEO inventoryEO = this.baseMapper.selectById(id);
        //校验机构权限
        if(!checkPer(inventoryEO.getOrgId(),userId)){
            throw new BusinessException("此归属机构下的数据该用户没有操作权限，请确认！");
        }


        this.baseMapper.updateStatusById(id,status);


        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateInventory(Long id,UserEO userEO) throws BusinessException {

        //获取物料信息表中最新物料插入到盘点明细表中
        this.baseMapper.updateNewInventory(id,userEO.getOrgId(),userEO.getUserId());

        return true;
    }

    public InventoryDetailEO getInventoryDetailInfo(String elementNo,Long orgId) throws BusinessException {
        //ID为物料ID,查询是否存在盘点单
        InventoryEO inventoryEO = this.baseMapper.selectExistIncentory(orgId);
        if(null == inventoryEO){
            throw new BusinessException("不存在盘点单，请确认！");
        }

        InventoryDetailEO inventoryDetailEO = this.baseMapper.selectExistIncentoryDetail(inventoryEO.getInventoryId(),elementNo);
        if(null ==  inventoryDetailEO){
            throw new BusinessException("不存在该物料的盘点明细，请确认！");
        }

        return inventoryDetailEO;
    }


    public IPage<TempInventoryEO> inventoryAllDetailInfo(Criteria criteria) throws BusinessException {

        return this.tempInventoryService.selectPage(criteria);
    }


    public boolean inventorBlindyOne(Long Id, Double amount,Long locationId, String action, Long userId, String userName, String inventoryNo) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        InventoryDetailEO detailEO = new InventoryDetailEO();
        if("add".equals(action)) {
            //Id为盘点明细ID
            detailEO = this.IInventoryDetailService.getById(Id);
            InventoryEO inventoryEO = this.baseMapper.selectById(detailEO.getInventoryId());
            if(inventoryEO.getStatus() == 2){
                throw new BusinessException("该次盘点已结束，请确认！");
            }
            //查看是否存在相同物料和库位的数据，有的话就覆盖之前的数量
            TempInventoryEO tempInventoryEOByMaterialAndLocationId = this.linedgeInventoryMapper.getTempInventoryByMaterialAndLocationId(inventoryEO.getInventoryId(),detailEO.getMaterialId(),locationId);
            if (tempInventoryEOByMaterialAndLocationId != null){
                tempInventoryEOByMaterialAndLocationId.setAmount(amount);
                this.tempInventoryService.updateById(tempInventoryEOByMaterialAndLocationId);

            }else {

                TempInventoryEO tempInventoryEO = new TempInventoryEO();
                tempInventoryEO.setInventoryId(inventoryEO.getInventoryId());
                tempInventoryEO.setMaterialId(detailEO.getMaterialId());
                tempInventoryEO.setLocationId(locationId);
                tempInventoryEO.setInventoryNo(inventoryNo);
                tempInventoryEO.setAmount(amount);
                tempInventoryEO.setInventoryDate(new Date());
                tempInventoryEO.setInventoryUserId(userId + "");
                tempInventoryEO.setInventoryUserName(userName);
                tempInventoryEO.setStatus(0);
                tempInventoryEO.setOrgId(user.getOrgId());

                this.tempInventoryService.save(tempInventoryEO);
            }

        }else{
            //Id为临时盘点的tempId
            TempInventoryEO tempInventoryEO = this.tempInventoryService.getById(Id);

            InventoryEO inventoryEO = this.baseMapper.selectById(tempInventoryEO.getInventoryId());

            LinedgeInventoryEO linedgeInventoryEO =linedgeInventoryMapper.selectById(tempInventoryEO.getInventoryId());
            //盘点撤回
            if (inventoryEO.getInventoryId() !=null && linedgeInventoryEO == null){
                if(inventoryEO.getStatus() == 2){
                    throw new BusinessException("该次盘点已结束,不能进行撤回操作，请确认！");
                }
            }
            //线边盘点撤回
            if (linedgeInventoryEO !=null && inventoryEO.getInventoryId() == null){
                if(linedgeInventoryEO.getStatus() == 2 || linedgeInventoryEO.getStatus() == 1){
                    throw new BusinessException("该次线边盘点已结束,不能进行撤回操作，请确认！");
                }
            }

            this.tempInventoryService.removeById(tempInventoryEO.getTempId());

        }


        return true;

    }

    @Transactional(rollbackFor = Exception.class)
    public Result importFromExcel(List list, String inventoryId, UserEO userEO) throws BusinessException {
        Long orgId=userEO.getOrgId();
        String errorMsg = "";
        Result result =  new Result<>();
        if(list!=null){
            if(list.size()>0) {
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                if (mapList != null && mapList.size() > 1) {
                    //先循环整个数据检查数据的合法性
                    for (int i = 1; i < mapList.size(); i++) {

                        Map map = mapList.get(i);

                        //零件号
                        String elementNo = (String) map.get("1");
                        if (elementNo.equals("")) {
                            errorMsg = errorMsg + "第 [" + (i + 1) + "] 行：零件号为空\n";
                        } else {
                            MaterialEO  materialEO = this.baseMapper.selectMaterialEO(elementNo, orgId);
                            if (materialEO == null) {
                                errorMsg = errorMsg + "第 [" + (i + 1) + "] 行零件号对应的物料不存在\n";
                            }
                        }
                    }

                    //将错误信息返回，同时让客户必须保证数据的正确性
                    if(!errorMsg.isEmpty()){
                        throw new BusinessException(errorMsg);
                    }

                    for (int i = 1; i < mapList.size(); i++) {
                        Map map = mapList.get(i);

                        String amount = "";
                        if (map.get("3")==""|| map.get("3") == null ){
                            amount = "0";
                        }else{
                            amount = (String)map.get("3");
                        }


                        //零件号
                        String elementNo = (String) map.get("1");
                        MaterialEO materialEO =this.baseMapper.selectMaterialEO(elementNo,orgId);
                        InventoryEO inventoryEO= this.baseMapper.selectById(inventoryId);

                        //保存临时盘点单
                        TempInventoryEO tempInventoryEO = new TempInventoryEO();

                        tempInventoryEO.setOrgId(orgId);
                        tempInventoryEO.setInventoryId(Long.parseLong(inventoryId));
                        tempInventoryEO.setMaterialId(materialEO.getMaterialId());
                        tempInventoryEO.setAmount(Double.parseDouble(amount));
                        tempInventoryEO.setInventoryDate(inventoryEO.getInventoryDate());
                        tempInventoryEO.setInventoryMonth(inventoryEO.getInventoryMonth());
                        tempInventoryEO.setStatus(0);
                        tempInventoryEO.setInventoryUserId(userEO.getUserId().toString());
                        tempInventoryEO.setInventoryUserName(userEO.getUserName());

                        this.tempInventoryService.save(tempInventoryEO);
                    }
                }
            }

            else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }


        return result;
    }

}
