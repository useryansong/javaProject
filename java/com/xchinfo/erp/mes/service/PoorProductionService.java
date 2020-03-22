package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.PoorMaterialVO;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.service.BomService;
import com.xchinfo.erp.mes.entity.PoorProductionEO;
import com.xchinfo.erp.mes.mapper.PoorProductionMapper;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.wms.service.ReceiveOrderService;
import com.xchinfo.erp.wms.service.StockAccountService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.JsonUtil;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

@Service
public class PoorProductionService extends BaseServiceImpl<PoorProductionMapper, PoorProductionEO> {
    @Autowired
    private BomService bomService;

    @Autowired
    private StockAccountService stockAccountService;

    @Autowired
    private ReceiveOrderService receiveOrderService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public boolean updateStatusById(Long[] ids,int status) throws BusinessException {
//        for(Long id : ids){
//            this.baseMapper.updateStatusById(status,id);
//        }
//        return true;

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();

        String sqlStr = "";
        if(ids==null || ids.length==0) {
            throw new BusinessException("请选择数据!");
        }
        for(Long id : ids){
            sqlStr += (id + ",");
        }
        if(!"".equals(sqlStr)) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }

        List<PoorProductionEO> poorProductions = this.baseMapper.getByIds(sqlStr);
        if(status == 0) { // 发布
            List<StockAccountEO> stockAccounts = new ArrayList<>();
            List<ReceiveOrderEO> receiveOrders = new ArrayList<>();
            if(poorProductions!=null && poorProductions.size()>0) {
                for(PoorProductionEO poorProduction : poorProductions) {
                    if(poorProduction.getPoorHandle().intValue() == 1 ) { // 返修
                        ReceiveOrderEO receiveOrder = new ReceiveOrderEO();
                        Long receiveOrderId = this.businessCodeGenerator.getNextval("wms_receive_order");
                        receiveOrder.setReceiveId(receiveOrderId);
                        receiveOrder.setPoorProductionId(poorProduction.getPoorProductionId());
                        receiveOrder.setReceiveType(6);
                        receiveOrder.setChildReceiveType(5);
                        receiveOrder.setVoucherType(1);
                        String voucherNo = this.businessCodeGenerator.generateNextCode("wms_receive_order", receiveOrder, poorProduction.getOrgId());
                        receiveOrder.setVoucherNo(voucherNo);
                        receiveOrder.setReceiveDate(new Date());
                        receiveOrder.setOrgId(poorProduction.getOrgId());
                        receiveOrder.setStatus(0);
                        receiveOrder.setCreatedBy("["+user.getUserName()+"]"+user.getRealName());
                        receiveOrder.setLastModifiedBy("["+user.getUserName()+"]"+user.getRealName());
                        receiveOrders.add(receiveOrder);

                        StockAccountEO stockAccount = new StockAccountEO();
                        Long accountId = this.businessCodeGenerator.getNextval("wms_stock_account");
                        stockAccount.setAccountId(accountId);
                        stockAccount.setVoucherType(6);
                        stockAccount.setVoucherId(receiveOrderId);
                        stockAccount.setVoucherDate(new Date());
                        stockAccount.setAmount(Double.valueOf(poorProduction.getNumber()));
                        stockAccount.setMaterialCode(poorProduction.getMaterialCode());
                        stockAccount.setMaterialId(poorProduction.getMaterialId());
                        stockAccount.setWarehouseId(poorProduction.getMainWarehouseId());
                        stockAccount.setMaterialCode(poorProduction.getMaterialCode());
                        stockAccount.setMaterialName(poorProduction.getMaterialName());
                        stockAccount.setInventoryCode(poorProduction.getInventoryCode());
                        stockAccount.setElementNo(poorProduction.getElementNo());
                        stockAccount.setSpecification(poorProduction.getSpecification());
                        stockAccount.setCreatedBy("["+user.getUserName()+"]"+user.getRealName());
                        stockAccount.setLastModifiedBy("["+user.getUserName()+"]"+user.getRealName());
                        stockAccounts.add(stockAccount);
                        poorProduction.setAccountId(accountId);
                        this.baseMapper.updateAccountIdById(accountId, poorProduction.getPoorProductionId());
                    }
                }
            }

            if(stockAccounts!=null && stockAccounts.size()>0) {
                this.stockAccountService.addBatch(stockAccounts);
            }
            if(receiveOrders!=null && receiveOrders.size()>0) {
                this.receiveOrderService.addBatch(receiveOrders);
            }
        } else if(status ==  1) { // 取消发布
            String sql = "";
            if(poorProductions!=null && poorProductions.size()>0) {
                for(PoorProductionEO poorProduction : poorProductions) {
                    if(poorProduction.getAccountId() != null) {
                        sql += (poorProduction.getAccountId() + ",");
                    }
                }

                if(!"".equals(sql)) {
                    sql = "(" + sql.substring(0, sql.length()-1) + ")";
                    this.stockAccountService.deleteByIds(sql);
                }

                this.baseMapper.clearAccountIdByIds(sqlStr);
                this.receiveOrderService.deleteByPoorProductionIds(sqlStr);
            }
        }

        this.baseMapper.updateStatusByIds(sqlStr, status);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = PoorProductionEO.class)
    public boolean save(PoorProductionEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("wms_receive_order", entity,user.getOrgId());
        AssertUtils.isBlank(code);

        //设置单据编号
        entity.setVoucherNo(code);
        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result<List<PoorMaterialVO>> getExportData(Long[] ids){
        List<PoorMaterialVO> result = new ArrayList<PoorMaterialVO>();
        Map param  = new HashMap();
        param.put("ids",ids);
        List<Map> poors = this.baseMapper.getDataByIds(param);//要导出的生产不良记录
        if(poors!=null && poors.size()>0) {
            String createBillName = ((UserEO) SecurityUtils.getSubject().getPrincipal()).getRealName();
            for(Map poor:poors){
                List<PoorMaterialVO> res = new ArrayList<PoorMaterialVO>();
                Long poorProductionId = (Long)poor.get("poor_production_id");
                Long orgId = (Long)poor.get("org_id");
                String elementNo = (String)poor.get("element_no");//零件号
                String voucherNo = (String)poor.get("voucher_no");//单据号
                String inventoryCode = (String)poor.get("inventory_code");//存货编码
                String warehouseCode = (String)poor.get("warehouse_code");//仓库编码
                String erpCode = (String)poor.get("erp_code");//ERP编码
                Date createdTime = (Date)poor.get("created_time");//日期
                int number = (int)poor.get("number");

                String erpVoucherNo1 = (String)poor.get("erp_voucher_no1");//导出生产不良材料单单据号
                if(erpVoucherNo1==null || erpVoucherNo1.trim().equals("")) {
                    erpVoucherNo1 = this.businessCodeGenerator.getErpVoucherNo("delivery_order_" + orgId);
                    if(erpVoucherNo1.matches("^[A-Z0-9]+$")) {
                        this.baseMapper.updateErpVoucherNoByIds("erp_voucher_no1", erpVoucherNo1, "(" + poorProductionId + ")");
                    }
                }

                List<BomEO > boms = this.baseMapper.getBom(orgId,elementNo);
                if(boms==null || boms.size()==0){
                    PoorMaterialVO vo = new PoorMaterialVO();
                    vo.setYamount(Double.valueOf(number));
                    vo.setXamount(Double.valueOf(number));
                    vo.setCreateBillName(createBillName);
                    vo.setCreatedTime(createdTime);
                    vo.setInventoryCode(inventoryCode);
                    vo.setDeliveryTypeCode("20202");
                    if(inventoryCode != null) {
                        if(inventoryCode.trim().substring(0,2).equals("02") ||
                                inventoryCode.trim().substring(0,2).equals("03")) {
                            vo.setDeliveryTypeCode("20102");
                        }
                    }
//                    vo.setVoucherNo(voucherNo);
                    vo.setErpCode(erpCode);
                    vo.setErpVoucherNo1(erpVoucherNo1.substring(2, erpVoucherNo1.length()));
                    res.add(vo);
                } else {
                    BomEO bom = boms.get(0);
                    List<BomEO> leaves = bomService.getAllLeavesForW(bom);
                    if(leaves!=null && leaves.size()>0) {
                        for(BomEO leaf:leaves){
                            double amount = leaf.getAmount()*number;
                            leaf.setAmount(amount);
                            PoorMaterialVO vo = JsonUtil.fromJson(JsonUtil.toJson(leaf),PoorMaterialVO.class);
                            vo.setYamount(amount);
                            vo.setXamount(amount);
                            vo.setCreateBillName(createBillName);
                            vo.setCreatedTime(createdTime);
                            vo.setInventoryCode(leaf.getInventoryCode());
                            vo.setDeliveryTypeCode("20202");
                            if(leaf.getInventoryCode() != null) {
                                if(leaf.getInventoryCode().trim().substring(0,2).equals("02") ||
                                        leaf.getInventoryCode().trim().substring(0,2).equals("03")) {
                                    vo.setDeliveryTypeCode("20102");
                                }
                            }
//                    vo.setVoucherNo(voucherNo);
                            vo.setErpCode(leaf.getErpCode());
                            vo.setErpVoucherNo1(erpVoucherNo1.substring(2, erpVoucherNo1.length()));
                            res.add(vo);
                        }
                    } else {
                        PoorMaterialVO vo = new PoorMaterialVO();
                        vo.setYamount(Double.valueOf(number));
                        vo.setXamount(Double.valueOf(number));
                        vo.setCreateBillName(createBillName);
                        vo.setCreatedTime(createdTime);
                        vo.setInventoryCode(inventoryCode);
                        vo.setDeliveryTypeCode("20202");
                        if(inventoryCode != null) {
                            if(inventoryCode.trim().substring(0,2).equals("02") ||
                                    inventoryCode.trim().substring(0,2).equals("03")) {
                                vo.setDeliveryTypeCode("20102");
                            }
                        }
//                    vo.setVoucherNo(voucherNo);
                        vo.setErpCode(erpCode);
                        vo.setErpVoucherNo1(erpVoucherNo1.substring(2, erpVoucherNo1.length()));
                        res.add(vo);
                    }
                }

                result.addAll(res);
            }
        }

        return new Result<List<PoorMaterialVO>>().ok(result);
    }

    public List<PoorProductionEO> listAll(Map map) {
        return this.baseMapper.listAll(map);
    }
}
