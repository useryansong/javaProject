package com.xchinfo.erp.controller.plm.mes;

/**
 * @author zhongy
 * @date 2019/9/11
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.srm.service.ProductOrderService;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/5/9
 */
@RestController
@RequestMapping("/mes/biwScheduling")
public class BiwSchedulingController extends BaseController {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private ScheduleOrderService scheduleOrderService;


    /**
     * 分页查找
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<ProductOrderEO>> page(@RequestBody Criteria criteria, @RequestParam("isProduceReportQuantityUnderZero") Boolean isProduceReportQuantityUnderZero) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.info("======== BiwSchedulingController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUser().getUserId());
        List<ProductOrderEO> totalList = this.productOrderService.selectSchedulingPage(map, isProduceReportQuantityUnderZero.booleanValue());
//        map.put("currentIndexFlag", true);
//        List<ProductOrderEO> pageList = this.productOrderService.selectSchedulingPage(map);
//        IPage<ProductOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        IPage<ProductOrderEO> page = CommonUtil.getPageInfo(totalList, criteria.getSize(), criteria.getCurrentPage());
        List<ProductOrderEO> pageList = page.getRecords();
        List<ProductOrderEO> newPageList = new ArrayList<>();
        if(pageList!=null && pageList.size()>0) {
            for(int i=0; i<pageList.size(); i++) {
                ProductOrderEO productOrder = pageList.get(i);
                Map tempMap = productOrder.getMap();

//                boolean flag = false;
//                if(tempMap!=null && tempMap.size()>0) {
//                    for(int k=1; k<=14; k++) {
//                        Method method = productOrder.getClass().getMethod("setDay" + k, Double.class);
//                        Object obj = tempMap.get(productOrder.getMaterialId()+"-"+"count" + k);
//                        if(obj!=null) {
//                            Double d = (Double) method.invoke(productOrder, tempMap.get(productOrder.getMaterialId()+"-"+"count" + k));
//                            if(d!=null && d<0) {
//                                flag = true;
//                                break;
//                            }
//                        }
//                    }
//                }

//                if(!flag) {
                    for(int j=1; j<=4; j++) {
                        if(j == 1) {
                            if(tempMap!=null && tempMap.size()>0) {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrder.getClass().getMethod("setDay" + k, Double.class);
                                    if(tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + k)==null) {
//                                    method.invoke(productOrder, Double.valueOf(0));
                                    } else {
                                        method.invoke(productOrder, tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + k));
                                    }
                                }
                            } else {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrder.getClass().getMethod("setDay" + k, Double.class);
//                                method.invoke(productOrder, Double.valueOf(0));
                                }
                            }

                            productOrder.setType("每日需求");
                            newPageList.add(productOrder);
                        }
                        if(j == 2) {
                            ProductOrderEO productOrderTemp = new ProductOrderEO();
                            if(tempMap!=null && tempMap.size()>0) {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
                                    if(tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + k)==null) {
//                                    method.invoke(productOrderTemp, Double.valueOf(0));
                                    } else {
                                        method.invoke(productOrderTemp, tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + k));
                                    }
                                }
                            } else {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
//                                method.invoke(productOrderTemp, Double.valueOf(0));
                                }
                            }

                            productOrderTemp.setType("生产计划");
                            newPageList.add(productOrderTemp);
                        }
                        if(j == 3) {
                            ProductOrderEO productOrderTemp = new ProductOrderEO();
                            if(tempMap!=null && tempMap.size()>0) {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
                                    if(tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + k)==null) {
//                                    method.invoke(productOrderTemp, Double.valueOf(0));
                                    } else {
                                        method.invoke(productOrderTemp, tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + k));
                                    }
                                }
                            } else {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
//                                method.invoke(productOrderTemp, Double.valueOf(0));
                                }
                            }

                            productOrderTemp.setType("生产入库");
                            newPageList.add(productOrderTemp);
                        }
                        if(j == 4) {
                            ProductOrderEO productOrderTemp = new ProductOrderEO();
                            if(tempMap!=null && tempMap.size()>0) {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
                                    if(tempMap.get(productOrder.getMaterialId()+"-"+"count" + k)==null) {
//                                    method.invoke(productOrderTemp, Double.valueOf(0));
                                    } else {
                                        method.invoke(productOrderTemp, tempMap.get(productOrder.getMaterialId()+"-"+"count" + k));
                                    }
                                }
                            } else {
                                for(int k=1; k<=14; k++) {
                                    Method method = productOrderTemp.getClass().getMethod("setDay" + k, Double.class);
//                                method.invoke(productOrderTemp, Double.valueOf(0));
                                }
                            }

                            productOrderTemp.setType("结存");
                            newPageList.add(productOrderTemp);
                        }
                    }
                }
//            else {
//                    continue;
//                }



//            }

            page.setRecords(newPageList);
            page.setTotal(pageList.size()*4);
        }

        return new Result<IPage<ProductOrderEO>>().ok(page);
    }

    @PostMapping("selectWorkingProcedureTime")
    public Result selectWorkingProcedureTime(@RequestBody ProductOrderEO productOrder){
        logger.info("======== BiwSchedulingController.selectWorkingProcedureTime ========");
        List<ScheduleOrderEO> scheduleOrders = this.scheduleOrderService.selectWorkingProcedureTime(productOrder);
        return new Result<List<ScheduleOrderEO>>().ok(scheduleOrders);
    }

    /**
     * 获取排产单(生产订单详情)
     * @return
     */
    @PostMapping("selectScheduleOrder")
    public Result<IPage<ScheduleOrderEO>> selectScheduleOrder(@RequestBody Criteria criteria){
        logger.info("======== BiwSchedulingController.selectScheduleOrder() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUser().getUserId());
        List<ScheduleOrderEO> totalList = this.scheduleOrderService.selectNewPage(map);
        map.put("currentIndexFlag", true);
        List<ScheduleOrderEO> pageList = this.scheduleOrderService.selectNewPage(map);
        IPage<ScheduleOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<ScheduleOrderEO>>().ok(page);
    }
}
