package com.xchinfo.erp.srm.service;

import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.service.MaterialService;
import com.xchinfo.erp.scm.srm.entity.ProductOrderReleaseDetailEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.srm.mapper.ProductOrderReleaseDetailMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.*;

/**
 * @author zhongy
 * @date 2019/12/24
 */
@Service
public class ProductOrderReleaseDetailService extends BaseServiceImpl<ProductOrderReleaseDetailMapper, ProductOrderReleaseDetailEO> {

    @Autowired
    private PurchaseOrderTempService purchaseOrderService;

    @Autowired
    private MaterialService materialService;


    public List<ProductOrderReleaseDetailEO> getList(Map map) {
        return this.baseMapper.getList(map);
    }

    public Result getMinAndMaxPlanArriveDate(Integer type) {
        Map map = this.baseMapper.getMinAndMaxPlanArriveDate(type);
        Result result = new Result();
        result.setData(map);
        return result;
    }

    public Result getSummary(Map map) {
        // 获取汇总
        List<ProductOrderReleaseDetailEO> summaryList = this.baseMapper.getSummary(map);
        // 获取包含的明细id
        if(summaryList!=null && summaryList.size()>0) {
            List<ProductOrderReleaseDetailEO> detailList = this.baseMapper.getDetail(map);
            if(detailList!=null && detailList.size()>0) {
                for(ProductOrderReleaseDetailEO summary : summaryList) {
                    List<ProductOrderReleaseDetailEO> details = new ArrayList<>();
                    Long summaryMaterialId = summary.getMaterialId();
                    for(ProductOrderReleaseDetailEO detail : detailList) {
                        Long detailMaterialId = detail.getMaterialId();
                        if(summaryMaterialId.longValue() == detailMaterialId.longValue()) {
                            details.add(detail);
                        }
                    }
                    summary.setProductOrderReleaseDetails(details);
                }
            }
        }
        Result result = new Result();
        result.setData(summaryList);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result releaseToPurchaseOrder(List<PurchaseOrderEO> purchaseOrders, UserEO user, Integer type) throws BusinessException {
        String errorMsg = "";
        String quantityErrorMsg = "";
        List<Long> productOrderReleaseDetailIds = new ArrayList<>();
        Map map = new HashedMap();
        if(purchaseOrders!=null && purchaseOrders.size()>0) {
            for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
                // 检查供应商是否为空
                if(purchaseOrder.getSupplierId() == null) {
                    errorMsg += (purchaseOrder.getElementNo() + "<br/>");
                }

                if(purchaseOrder.getProductOrderReleaseDetails()!=null && purchaseOrder.getProductOrderReleaseDetails().size()>0) {
                    for(ProductOrderReleaseDetailEO pord : purchaseOrder.getProductOrderReleaseDetails()) {
                        productOrderReleaseDetailIds.add(pord.getProductOrderReleaseDetailId());
                    }
                }

                map.put(purchaseOrder.getMaterialId(), purchaseOrder.getPlanDeliveryQuantity());
            }
        }

        if(!"".equals(errorMsg)) {
            errorMsg = "以下物料:<br/>" + errorMsg + "供应商为空,请检查!";
            throw new BusinessException(errorMsg);
        }

        List<ProductOrderReleaseDetailEO> list = this.baseMapper.getByIds(productOrderReleaseDetailIds);
        Set<Long> statusChecks = new HashSet<>(); // 状态是否检查
        Set<Long> quantityChecks = new HashSet<>(); // 数量是否检查
        if(list!=null && list.size()>0) {
            for(ProductOrderReleaseDetailEO pord : list) {
                // 检查包含的明细状态是否为新建
                if(!statusChecks.contains(pord.getMaterialId())) {
                    if(pord.getStatus().intValue() != 0) {
                        errorMsg += (pord.getElementNo() + "<br/>");
                        statusChecks.add(pord.getMaterialId());
                    }
                }

                // 检查采购数量是否正确
                Double snp = pord.getSnp();
                if(snp==null || snp.doubleValue()==0) {
                    snp = 1d;
                }

//                Double sumRelDeliveryQuantity = Math.ceil(pord.getSumPlanDeliveryQuantity()/snp) * snp;
//                if(!quantityChecks.contains(pord.getMaterialId())) {
//                    if (sumRelDeliveryQuantity.doubleValue() != ((Double) map.get(pord.getMaterialId())).doubleValue()) {
//                        quantityErrorMsg += (pord.getElementNo() + "<br/>");
//                        quantityChecks.add(pord.getMaterialId());
//                    }
//                }
            }
        }

        if(!"".equals(errorMsg)) {
            errorMsg = "以下物料:<br/>" + errorMsg + "存在非新建数据,请检查!";
            throw new BusinessException(errorMsg);
        }

        if(!"".equals(quantityErrorMsg)) {
            quantityErrorMsg = "以下物料:<br/>" + quantityErrorMsg + "采购数量不正确,请检查!";
            throw new BusinessException(quantityErrorMsg);
        }

        for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
            MaterialEO material = this.materialService.getById(purchaseOrder.getMaterialId());

            purchaseOrder.setType(type);
            purchaseOrder.setMaterialName(material.getMaterialName());
            purchaseOrder.setSpecification(material.getSpecification());
            purchaseOrder.setMaterialCode(material.getMaterialCode());
            purchaseOrder.setInventoryCode(material.getInventoryCode());
            purchaseOrder.setUnitName(material.getUnitName());
            purchaseOrder.setCreateDate(new Date());
            purchaseOrder.setNotDeliveryQuantity(purchaseOrder.getPlanDeliveryQuantity());
            purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
            purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
            purchaseOrder.setReturnedQuantity(Double.valueOf(0));
            purchaseOrder.setStatus(0);
            purchaseOrder.setCreateUserId(user.getUserId());
            purchaseOrder.setCreateUserName(user.getUserName());
            purchaseOrder.setIsChangeConfirm(1);

            Calendar calendar = Calendar.getInstance();
            Date now = purchaseOrder.getPlanArriveDate();
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            purchaseOrder.setMonthDate(calendar.getTime());
            this.purchaseOrderService.save(purchaseOrder);

            this.baseMapper.reverseByIds(productOrderReleaseDetailIds, 1, purchaseOrder.getPurchaseOrderId(), purchaseOrder.getVoucherNo());
        }

        return new Result();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByIds(List<Long> productOrderReleaseDetailIds, Integer status) throws BusinessException {
        this.baseMapper.reverseByIds(productOrderReleaseDetailIds, status, null, null);
    }
}
