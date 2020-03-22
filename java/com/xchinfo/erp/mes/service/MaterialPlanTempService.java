package com.xchinfo.erp.mes.service;


import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.mapper.MaterialPlanMapper;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.srm.service.ProductOrderTempService;
import com.xchinfo.erp.srm.service.PurchaseOrderTempService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.LogUtils;
import java.util.*;

@Service
public class MaterialPlanTempService extends BaseServiceImpl<MaterialPlanMapper, MaterialPlanEO>   {

    @Autowired
    private PurchaseOrderTempService purchaseOrderService;

    @Autowired
    private ProductOrderTempService productOrderService;


    public void confirmChange(Set serialIds, UserEO user) {
        // 周计划的变更状态计算逻辑:
        // 首先判断周计划的是否变更字段,如果为0则跳过,
        // 如果为1,则判断分解出的采购/委外/生产订单是否都已经确认了,如果都确认了,则把是否变更字段设置为0,变更详情字段设置为空，
        // 如果还有单据未确认,则统计出未确认的数量,比如采购订单未确认:xxx,委外订单未确认:xxx,生产订单未确认:xxx,写入变更详情字段
        String sqlStr = "";
        if(serialIds!=null && serialIds.size()>0) {
            for(Object obj : serialIds) {
                sqlStr += ((Long) obj + ",");
            }

            if(!"".equals(sqlStr)) {
                sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
            }

            sqlStr = "(" + sqlStr + ")";
        }
        List<MaterialPlanEO> materialPlans = this.baseMapper.getBySerialIds(sqlStr);
        Set serialIds1 = new HashSet();
        if(materialPlans!=null && materialPlans.size()>0) {
            for(MaterialPlanEO materialPlan : materialPlans) {
                if(materialPlan.getIsChange() == null) {
                    materialPlan.setIsChange(0);
                }
                if(materialPlan.getIsChange().intValue() == 1) {
                    serialIds1.add(materialPlan.getSerialId());
                }
            }
        }

        String sqlStr1 = "";
        if(serialIds1!=null && serialIds1.size()>0) {
            for(Object obj : serialIds) {
                sqlStr1 += ((Long) obj + ",");
            }

            if(!"".equals(sqlStr1)) {
                sqlStr1 = sqlStr1.substring(0, sqlStr1.length() - 1);
            }

            sqlStr1 = "(" + sqlStr1 + ")";

            List<PurchaseOrderEO> purchaseOrders = this.purchaseOrderService.getBySerialIds(sqlStr1);

            int notConfirmPurchaseCount = 0;
            int notConfirmOutsideCount = 0;
            int notConfirmProductCount = 0;
            Map map = new HashedMap();

            if(purchaseOrders!=null && purchaseOrders.size()>0) {
                for(PurchaseOrderEO purchaseOrder : purchaseOrders) {
                    if(purchaseOrder.getIsChangeConfirm().intValue() == 0) {
                        if(purchaseOrder.getType().intValue() == 1) {
                            notConfirmPurchaseCount += 1;
                        } else if(purchaseOrder.getType().intValue() == 2) {
                            notConfirmOutsideCount += 1;
                        }
                    }
                    map.put("purchase-"+purchaseOrder.getSerialId(), notConfirmPurchaseCount);
                    map.put("outside-"+purchaseOrder.getSerialId(), notConfirmOutsideCount);
                }
            }

            List<ProductOrderEO> productOrders = this.productOrderService.getBySerialIds(sqlStr1);
            if(productOrders!=null && productOrders.size()>0) {
                for(ProductOrderEO productOrder : productOrders) {
                    if(productOrder.getIsChangeConfirm().intValue() == 0) {
                        notConfirmProductCount += 1;
                    }
                    map.put("product-"+productOrder.getSerialId(), notConfirmProductCount);
                }
            }

            List<MaterialPlanEO> list = new ArrayList();
            if(map!=null && map.size()>0) {
                for(Object obj : map.keySet()) {
                    String msg = "";
                    Long serialId = Long.valueOf(obj.toString().split("-")[1]);
                    for(MaterialPlanEO materialPlan : materialPlans) {
                        if(materialPlan.getSerialId().longValue() == serialId.longValue()) {
                            if(((Integer) map.get("purchase-" + serialId)).intValue() != 0) {
                                msg += ("采购订单未确认: " + map.get("purchase-" + serialId) + "条,");
                            }
                            if(((Integer) map.get("outside-" + serialId)).intValue() != 0) {
                                msg += ("委外订单未确认: " + map.get("outside-" + serialId) + "条,");
                            }
                            if(((Integer) map.get("product-" + serialId)).intValue() != 0) {
                                msg += ("生产订单未确认: " + map.get("product-" + serialId) + "条,");
                            }

                            if(!"".equals(msg)) {
                                materialPlan.setChangeInfo(msg);
                            } else {
                                materialPlan.setChangeInfo(null);
                                materialPlan.setIsChange(0);
                            }
                            list.add(materialPlan);
                        }
                    }
                }

                if(list!=null && list.size()>0) {
                    this.saveOrUpdateBatch(list);
                    for(MaterialPlanEO materialPlan : list) {
                        // 记录日志
                        LogUtils.saveLog(user.getUserName()+"已确认周计划变更: " +
                                        "确认时间: " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "," +
                                        "周计划版本号 " + materialPlan.getPlanVersion()==null?"1":materialPlan.getPlanVersion() + "," +
                                        "周计划新需求数量: " + materialPlan.getRequireCount(),
                                "修改数据", "MaterialPlanEO",
                                materialPlan.getPreRequireCount()==null?"":materialPlan.getRequireCount()+"",
                                materialPlan.getRequireCount()+"", materialPlan.getOrgId(), materialPlan.getSerialId());
                    }
                }
            }
        }
    }
}