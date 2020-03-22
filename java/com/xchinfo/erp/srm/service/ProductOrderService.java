package com.xchinfo.erp.srm.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.BomEO;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.entity.MaterialCustomerEO;
import com.xchinfo.erp.bsc.mapper.BomMapper;
import com.xchinfo.erp.bsc.service.BomService;
import com.xchinfo.erp.mes.entity.MaterialPlanEO;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.mes.service.MaterialPlanTempService;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ProductOrderReleaseDetailEO;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.mapper.ProductOrderMapper;
import com.xchinfo.erp.srm.mapper.ScheduleOrderMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.conf.service.ParamsService;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.Criteria;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhongy
 * @date 2019/9/2
 */
@Service
public class ProductOrderService extends BaseServiceImpl<ProductOrderMapper, ProductOrderEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private MaterialPlanTempService materialPlanService;

    @Autowired
    private ScheduleOrderMapper scheduleOrderMapper;

    @Autowired
    private BomMapper bomMapper;

    @Autowired
    private BomService bomService;

    @Autowired
    private PurchaseOrderTempService purchaseOrderService;

    @Autowired
    private ProductOrderReleaseDetailService productOrderReleaseDetailService;

    @Autowired
    private ParamsService paramsService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = ProductOrderEO.class)
    public boolean save(ProductOrderEO entity) throws BusinessException {
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        // 生成业务编码
        String voucherNo = this.businessCodeGenerator.generateNextCode("srm_product_order", entity,user.getOrgId());
        AssertUtils.isBlank(voucherNo);
        entity.setVoucherNo(voucherNo);
        return super.save(entity);
    }

    public List<ProductOrderEO> getBySerialIds(String sqlStr) {
        return this.baseMapper.getBySerialIds(sqlStr);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductOrderIds(String deleteSqlStr) {
        this.baseMapper.deleteByProductOrderIds(deleteSqlStr);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result confirm(Long[] ids, UserEO user) throws BusinessException {
        Result result = new Result();

        if(ids==null || ids.length==0) {
            throw new BusinessException("请选择数据!");
        }

        String sqlStr = "";
        for(Long id : ids) {
            sqlStr += (id + ",");
        }

        if(!"".equals(sqlStr)) {
            sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
        }

        sqlStr = "(" + sqlStr + ")";

        List<ProductOrderEO> list = this.baseMapper.getByProductOrderIds(sqlStr);
        Map map = new HashedMap();
        String errorMsg = "";
        Set serialIds = new HashSet(); // 客户需求周计划ID集合
        if(list!=null && list.size()>0) {
            for(ProductOrderEO productOrder : list) {
                if(productOrder.getIsChangeConfirm().intValue() == 1) {
                    String msg =  productOrder.getOrgName() + " " +
                            DateUtils.format(productOrder.getPlanFinishDate(), "yyyy-MM") + " " +
                            productOrder.getMaterialId() + " " +
                            productOrder.getElementNo();
                    map.put(msg, msg);
                } else {
                    productOrder.setIsChangeConfirm(1);
                    productOrder.setLastConfirmTime(new Date());
                    if(productOrder.getConfirmVersion() == null) {
                        productOrder.setConfirmVersion(0);
                    }
                    productOrder.setConfirmVersion(productOrder.getConfirmVersion() + 1);
                    if(productOrder.getSerialId() != null) {
                        serialIds.add(productOrder.getSerialId());
                    }
                }
            }

            if(map!=null && map.size()>0) {
                for(Object obj : map.keySet()) {
                    errorMsg += ("[" + map.get(obj) + "] 已确认<br/>");
                }
            }

            if(!"".equals(errorMsg)) {
                result.setCode(500);
                result.setMsg(errorMsg);
                return result;
            }

            this.materialPlanService.confirmChange(serialIds, user);
            this.saveOrUpdateBatch(list);
        } else {
            throw new BusinessException("选择的数据已不存在，请刷新!");
        }

        return result;
    }


    public List<ProductOrderEO> getByProductOrderIds(String sqlStr) {
        return this.baseMapper.getByProductOrderIds(sqlStr);
    }

    public List<ProductOrderEO> selectSchedulingPage(Map map, boolean isProduceReportQuantityUnderZero) {
        List<ProductOrderEO> list = this.baseMapper.selectSchedulingPage(map);
        List<ProductOrderEO> tempList = new ArrayList<>();


        String sqlStr = "";
        if(list!=null && list.size()>0) {
            for(ProductOrderEO productOrder : list) {
                sqlStr += (productOrder.getMaterialId() + ",");
            }
        }

        if(!"".equals(sqlStr)) {
            sqlStr = "(" + sqlStr.substring(0, sqlStr.length()-1) + ")";
        } else {
            sqlStr = "(-1)";
        }

        String planFinishDateStart;
        String planFinishDateEnd;
        Date preDate;
        int interval;
        if(map.get("planFinishDateStart") != null) {
            planFinishDateStart = (String) map.get("planFinishDateStart");
            planFinishDateEnd = (String) map.get("planFinishDateEnd");
            preDate = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), -1);
            interval = Integer.valueOf((String) map.get("interval")); // 间隔天数
        } else {
            planFinishDateStart = DateUtils.format(new Date(), "yyyy-MM-dd");
            planFinishDateEnd = DateUtils.format(DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), 7), "yyyy-MM-dd");
            preDate = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), -1);
            interval = 7; // 间隔天数
        }


        // 查询库存
        List<ProductOrderEO> counts = this.baseMapper.getCounts(sqlStr, planFinishDateEnd);


        List<ProductOrderEO> planProduceQuantitys;

        List<ScheduleOrderEO> actualProduceQuantitys;
        if(map.get("workingProcedureType").equals("0")){
            // 焊接排产 查询具体日期生产订单需求数量(同一天的同一个物料需求数量合并)
            planProduceQuantitys = this.baseMapper.getBiwPlanProduceQuantitys(sqlStr, planFinishDateStart, planFinishDateEnd);

            // 焊接排产 查询具体日期排产单生产数量(同一天的同一个物料生产数量合并)
            actualProduceQuantitys = this.scheduleOrderMapper.getBiwActualProduceQuantitys(sqlStr, planFinishDateStart, planFinishDateEnd);
        }else{
            // 冲压排产 查询具体日期生产订单需求数量(同一天的同一个物料需求数量合并)
            planProduceQuantitys = this.baseMapper.getPlanProduceQuantitys(sqlStr, planFinishDateStart, planFinishDateEnd);

            // 冲压排产(冲压件的最后工序的计划生产数量) 查询具体日期排产单生产数量(同一天的同一个物料生产数量合并)
            actualProduceQuantitys = this.scheduleOrderMapper.getActualProduceQuantitys(sqlStr, planFinishDateStart, planFinishDateEnd);
        }




        // 查询具体日期生产日报入库数量(同一天的同一个物料数量合并)
        List<ProductOrderEO> produceReportQuantitys = this.baseMapper.getProduceReportQuantitys(sqlStr, planFinishDateStart, planFinishDateEnd);

        if(list!=null && list.size()>0) {
            Set hasAddSet = new HashSet();
            if(isProduceReportQuantityUnderZero) {
                for(int j=0; j<list.size(); j++) {
                    ProductOrderEO productOrder = list.get(j);
                    Map tempMap1 = new HashedMap();
                    Double count = 0d; //  期初
                    /*for(ProductOrderEO poqPo : produceReportQuantitys ) {
                        if(productOrder.getMaterialId().longValue() == poqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Double d = tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i)==null?0d: (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i);

                                if(d < 0) {
                                    if(!hasAddSet.contains(j)) {
                                        hasAddSet.add(j);
                                        break;
                                    }
                                } else {
                                    tempMap1.put(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i, d + (poqPo.getProduceReportQuantity()==null?0d:poqPo.getProduceReportQuantity()));
                                }
                            }
                        }
                    }*/

                    for(ProductOrderEO ppqPo : planProduceQuantitys) {
                        if(productOrder.getMaterialId().longValue() == ppqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(ppqPo.getPlanFinishDate()!=null && ppqPo.getPlanFinishDate().getTime()==date.getTime()) {
                                    Double d = tempMap1.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i)==null?0d: (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i);
                                    tempMap1.put(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i, d + (ppqPo.getPlanProduceQuantity()==null?0d:ppqPo.getPlanProduceQuantity()));
                                }
                            }
                        }
                    }

                    for(ScheduleOrderEO apqPo : actualProduceQuantitys) {
                        if(productOrder.getMaterialId().longValue() == apqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(apqPo.getPlanProductDate()!=null && apqPo.getPlanProductDate().getTime()==date.getTime()) {
                                    Double d = tempMap1.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i)==null?0d: (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i);
                                    tempMap1.put(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i, d + (apqPo.getActualProduceQuantity()==null?0d:apqPo.getActualProduceQuantity()));
                                }
                            }
                        }
                    }

                    for(ProductOrderEO poqPo : produceReportQuantitys ) {
                        if(productOrder.getMaterialId().longValue() == poqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(poqPo.getPlanFinishDate()!=null && poqPo.getPlanFinishDate().getTime()==date.getTime()) {
                                    Double d = tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i)==null?0d: (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i);

                                    tempMap1.put(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i, d + (poqPo.getProduceReportQuantity()==null?0d:poqPo.getProduceReportQuantity()));
                                }
                            }
                        }
                    }

                    for(ProductOrderEO countPo : counts) {
                        if(productOrder.getMaterialId().longValue() == countPo.getMaterialId().longValue()) {
                            if(countPo.getPlanFinishDate()==null || countPo.getPlanFinishDate().getTime()<=preDate.getTime()) {
                                count += countPo.getCount();
                            }
                        }
                    }

                    tempMap1.put(productOrder.getMaterialId()+"-"+"count" + 0, count);
                    for(int i=1; i<=interval; i++) {
                        // 当天的生产减去当天的需求数量
                        Double actualProduceQuantity = 0d;
                        Double planProduceQuantity = 0d;
                        if(tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i) != null) {
                            actualProduceQuantity = (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i);
                        } else {
                            if(tempMap1.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i) != null) {
                                actualProduceQuantity = (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i);
                            }
                        }
                        if(tempMap1.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i) != null) {
                            planProduceQuantity = (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i);
                        }
                        Double sub = actualProduceQuantity - planProduceQuantity;
                        // 前一天的库存
                        Double rest = (Double) tempMap1.get(productOrder.getMaterialId()+"-"+"count" + (i-1));
                        if (sub + rest <0){
                            if(!hasAddSet.contains(j)) {
                                hasAddSet.add(j);
                                break;
                            }
                        }else{
                            tempMap1.put(productOrder.getMaterialId()+"-"+"count" + i, sub + rest);
                        }

                    }

                }

                if(hasAddSet.size() > 0) {
                    for(Object obj : hasAddSet) {
                        tempList.add(list.get((Integer) obj));
                    }
                }
            } else {
                tempList = list;
            }


            if(tempList!=null && tempList.size()>0) {
                for(ProductOrderEO productOrder : tempList) {
                    Double count = 0d; //  期初
                    Map tempMap = new HashedMap();

                    for(ProductOrderEO ppqPo : planProduceQuantitys) {
                        if(productOrder.getMaterialId().longValue() == ppqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(ppqPo.getPlanFinishDate()!=null && ppqPo.getPlanFinishDate().getTime()==date.getTime()) {
                                    Double d = tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i)==null?0d: (Double) tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i);
                                    tempMap.put(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i, d + (ppqPo.getPlanProduceQuantity()==null?0d:ppqPo.getPlanProduceQuantity()));
                                }
                            }
                        }
                    }

                    for(ScheduleOrderEO apqPo : actualProduceQuantitys) {
                        if(productOrder.getMaterialId().longValue() == apqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(apqPo.getPlanProductDate()!=null && apqPo.getPlanProductDate().getTime()==date.getTime()) {
                                    Double d = tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i)==null?0d: (Double) tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i);
                                    tempMap.put(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i, d + (apqPo.getActualProduceQuantity()==null?0d:apqPo.getActualProduceQuantity()));
                                }
                            }
                        }
                    }

                    for(ProductOrderEO poqPo : produceReportQuantitys ) {
                        if(productOrder.getMaterialId().longValue() == poqPo.getMaterialId().longValue()) {
                            for(int i=1; i<=interval; i++) {
                                Date date = DateUtils.addDateDays(DateUtils.stringToDate(planFinishDateStart, "yyyy-MM-dd"), i-1);
                                if(poqPo.getPlanFinishDate()!=null && poqPo.getPlanFinishDate().getTime()==date.getTime()) {
                                    Double d = tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i)==null?0d: (Double) tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i);

                                    tempMap.put(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i, d + (poqPo.getProduceReportQuantity()==null?0d:poqPo.getProduceReportQuantity()));
                                }
                            }
                        }
                    }

                    for(ProductOrderEO countPo : counts) {
                        if(productOrder.getMaterialId().longValue() == countPo.getMaterialId().longValue()) {
                            if(countPo.getPlanFinishDate()==null || countPo.getPlanFinishDate().getTime()<=preDate.getTime()) {
                                count += countPo.getCount();
                            }
                        }
                    }

                    tempMap.put(productOrder.getMaterialId()+"-"+"count" + 0, count);
                    for(int i=1; i<=interval; i++) {
                        // 当天的生产减去当天的需求数量
                        Double actualProduceQuantity = 0d;
                        Double planProduceQuantity = 0d;
                        if(tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i) != null) {
                            actualProduceQuantity = (Double) tempMap.get(productOrder.getMaterialId()+"-"+"produceReportQuantity" + i);
                        } else {
                            if(tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i) != null) {
                                actualProduceQuantity = (Double) tempMap.get(productOrder.getMaterialId()+"-"+"actualProduceQuantity" + i);
                            }
                        }
                        if(tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i) != null) {
                            planProduceQuantity = (Double) tempMap.get(productOrder.getMaterialId()+"-"+"planProduceQuantity" + i);
                        }
                        Double sub = actualProduceQuantity - planProduceQuantity;
                        // 前一天的库存
                        Double rest = (Double) tempMap.get(productOrder.getMaterialId()+"-"+"count" + (i-1));
                        tempMap.put(productOrder.getMaterialId()+"-"+"count" + i, sub + rest);

                    }

                    productOrder.setCount(count);
                    productOrder.setMap(tempMap);
                }
            }
        }

        return tempList;
    }

    public List<ProductOrderEO> getPageByParentSerialId(Map map) {
        return this.baseMapper.getPageByParentSerialId(map);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result importFromExcel(List list, String begintitle,Date begindate,Date enddate,UserEO userEO, String robot, String rivet, String handwork, String punching) throws BusinessException {
        Long orgId=userEO.getOrgId();
        //List<ScheduleOrderEO> planList = new ArrayList<>();
        Integer beginCount = 0;
        String errorMsg = "";
        Result result =  new Result<>();
        //导入之前将所有的查看状态变为1
        this.baseMapper.updateAllCheckStatus();

        if(list!=null){
            if(list.size()>0){
                //机器人
                if (robot.equals("1")){
                    List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                    if(mapList!=null && mapList.size()>1){

                        //标题匹配
                        Integer size = mapList.get(0).size();
                        logger.info("========长度 ========"+size);
                        for(int j=4;j<size;j++){
                            String time = (String)mapList.get(0).get(j+"");
                            if(time.trim().contains(begintitle)){
                                beginCount = j;
                                break;
                            }
                        }

                        if (beginCount == 0){
                            throw new BusinessException("请确认机器人sheet页文件标题头的日期是否存在选择的日期！");
                        }
                        //先循环整个数据检查数据的合法性
                        for(int i=3;i < mapList.size();i++) {

                            Map map = mapList.get(i);

                            //初始参数校验
                            //设备机台编号
                            String machineNo = (String) map.get("0");
                            if (machineNo.equals("")) {
                                errorMsg = errorMsg + "机器人第" + (i + 1) + "行设备机台编号为空\n";
                            } else {
                                MachineEO machineEO = this.baseMapper.selectMachineEO(machineNo, orgId);
                                if (machineEO == null) {
                                    errorMsg = errorMsg + "机器人第" + (i + 1) + "行设备机台编号对应的设备不存在\n";
                                }
                            }

                            //零件号
                            String elementNo = (String) map.get("1");
                            if (elementNo.equals("")) {
                                errorMsg = errorMsg + "机器人第" + (i + 1) + "行零件号为空\n";
                            } else {
                                //根据零件号去工序表查找最大工序号
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);
                                if (workingProcedureTimeEO == null) {
                                    errorMsg = errorMsg + "机器人第" + (i + 1) + "行零件号对应工序不存在\n";
                                }
                            }

                        }

                        //将错误信息返回，同时让客户必须保证数据的正确性
                        if (!errorMsg.isEmpty()) {
                            throw new BusinessException(errorMsg);
                        }

                        for(int i=3;i < mapList.size();i++){
                            //第一行表头
                            Map  map0 = mapList.get(0);
                            //第三行表头
                            Map  map2 = mapList.get(2);

                            Map  map = mapList.get(i);

                            // 从选中的日期开始,往后推移1个星期
                            for(int p=beginCount; p<beginCount+14; p++) {
                                //计划数量
                                String planCount = (String) map.get(""+p);
                                if (planCount == null){
                                    planCount = "";
                                }

                                //初始参数校验
                                //设备机台编号
                                String machineNo = (String) map.get("0");
                                MachineEO machineEO = this.baseMapper.selectMachineEO(machineNo,orgId);

                                //排产日期
                                String planDate;
                                if (p%2==0){
                                    planDate = (String) map0.get(""+(p-1));
                                }else{
                                    planDate = (String) map0.get(""+p);
                                }


                                Calendar cal = Calendar.getInstance();
                                int year = cal.get(Calendar.YEAR);
                                String  PlanDate2  = planDate.replace("月","-").replace("日","");
                                String newPlanDate = year + "-"+ PlanDate2;
                                Date date = DateUtils.stringToDate(newPlanDate,"yyyy-MM-dd");
                                String checkdate = DateUtils.format(date,"yyyy-MM-dd");
                                //班次
                                String classOrder = (String) map2.get(""+p);

                                //零件号
                                String elementNo = (String) map.get("1");
                                //根据零件号去工序表查找最大工序号
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);

                                if (!planCount.equals("") && !planCount.equals("0")) {
                                    //根据物料ID和日期及班次及设备机台号查询生产计划是否存在,不存在则新增生产计划和排产计划
                                    ProductOrderEO productOrderEO = this.baseMapper.selectProductOrderEO(workingProcedureTimeEO.getMaterialId(), orgId, checkdate, classOrder,machineEO.getMachineId());
                                    if (productOrderEO == null) {
                                        //根据物料ID和日期查询生产计划是否存在，默认为一个物料当天只存在一个生产计划
                                        ProductOrderEO productOrderEOtemp = this.baseMapper.selectProductOrderEOtemp(workingProcedureTimeEO.getMaterialId(), orgId, checkdate);
                                        if (productOrderEOtemp !=null){
                                            productOrderEOtemp.setPlanProduceQuantity(Double.valueOf(planCount)+productOrderEOtemp.getPlanProduceQuantity());
                                            productOrderEOtemp.setHasScheduleQuantity(Double.valueOf(planCount)+productOrderEOtemp.getHasScheduleQuantity());
                                            this.baseMapper.updateById(productOrderEOtemp);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEOtemp.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("1");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }else {

                                            //保存生产计划

                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("机器人导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("1");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }

                                    } else {
                                        //如果存在则删除原来的生产计划和排产计划，重新插入生产计划和排产计划
                                        if (productOrderEO.getProduceStatus() == 0) {
                                            super.removeById(productOrderEO.getProductOrderId());
                                            this.scheduleOrderMapper.removeByProductOrderId(productOrderEO.getProductOrderId());

                                            //保存生产计划
                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("机器人导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("1");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        } else {
                                            throw new BusinessException("机器人第" + (i + 1) + "行零件号对应的生产计划已存在,且已开始生产");
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                //铆接
                if (rivet.equals("2")){
                    List<Map> mapList = (List<Map>) list.get(1); //根据sheet获取
                    if(mapList!=null && mapList.size()>1){

                        //标题匹配
                        Integer size = mapList.get(0).size();
                        logger.info("========长度 ========"+size);
                        for(int j=4;j<size;j++){
                            String time = (String)mapList.get(0).get(j+"");
                            if(time.trim().contains(begintitle)){
                                beginCount = j;
                                break;
                            }
                        }

                        if (beginCount == 0){
                            throw new BusinessException("请确认铆接sheet页文件标题头的日期是否存在选择的日期！");
                        }

                        for(int i=3;i < mapList.size();i++) {

                            Map map = mapList.get(i);

                            //初始参数校验
                            //设备机台编号
                            String machineNo = (String) map.get("0");
                            if (machineNo.equals("")) {
                                errorMsg = errorMsg + "铆接第" + (i + 1) + "行设备机台编号为空\n";
                            } else {
                                MachineEO  machineEO = this.baseMapper.selectMachineEO(machineNo, orgId);
                                if (machineEO == null) {
                                    errorMsg = errorMsg + "铆接第" + (i + 1) + "行设备机台编号对应的设备不存在\n";
                                }
                            }

                            //零件号
                            String elementNo = (String) map.get("1");
                            if (elementNo.equals("")) {
                                errorMsg = errorMsg + "铆接第" + (i + 1) + "行零件号为空\n";
                            } else {
                                //根据零件号去工序表查找最大工序号
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);
                                if (workingProcedureTimeEO == null) {
                                    errorMsg = errorMsg + "铆接第" + (i + 1) + "行零件号对应工序不存在\n";
                                }
                            }
                        }

                        //将错误信息返回，同时让客户必须保证数据的正确性
                        if(!errorMsg.isEmpty()){
                            throw new BusinessException(errorMsg);
                        }

                        for(int i=3;i < mapList.size();i++){
                            //第一行表头
                            Map  map0 = mapList.get(0);
                            //第三行表头
                            Map  map2 = mapList.get(2);

                            Map  map = mapList.get(i);

                            // 从选中的日期开始,往后推移1个星期
                            for(int p=beginCount; p<beginCount+14; p++) {
                                //计划数量
                                String planCount = (String) map.get(""+p);
                                if (planCount == null){
                                    planCount = "";
                                }
                                //设备机台编号
                                String machineNo = (String) map.get("0");
                                MachineEO machineEO = this.baseMapper.selectMachineEO(machineNo,orgId);

                                //排产日期
                                String planDate;
                                if (p%2==0){
                                    planDate = (String) map0.get(""+(p-1));
                                }else{
                                    planDate = (String) map0.get(""+p);
                                }


                                Calendar cal = Calendar.getInstance();
                                int year = cal.get(Calendar.YEAR);
                                String  PlanDate2  = planDate.replace("月","-").replace("日","");
                                String newPlanDate = year + "-"+ PlanDate2;
                                Date date = DateUtils.stringToDate(newPlanDate,"yyyy-MM-dd");
                                String checkdate = DateUtils.format(date,"yyyy-MM-dd");
                                //班次
                                String classOrder = (String) map2.get(""+p);

                                //零件号
                                String elementNo = (String) map.get("1");
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);

                                if (!planCount.equals("") && !planCount.equals("0")) {

                                    //根据物料ID和日期查询生产计划是否存在,不存在则新增生产计划和排产计划
                                    ProductOrderEO productOrderEO = this.baseMapper.selectProductOrderEO(workingProcedureTimeEO.getMaterialId(), orgId, checkdate, classOrder,machineEO.getMachineId());
                                    if (productOrderEO == null) {
                                        //根据物料ID和日期查询生产计划是否存在，默认为一个物料当天只存在一个生产计划
                                        ProductOrderEO productOrderEOtemp = this.baseMapper.selectProductOrderEOtemp(workingProcedureTimeEO.getMaterialId(), orgId, checkdate);
                                        if (productOrderEOtemp !=null) {
                                            productOrderEOtemp.setPlanProduceQuantity(Double.valueOf(planCount) + productOrderEOtemp.getPlanProduceQuantity());
                                            productOrderEOtemp.setHasScheduleQuantity(Double.valueOf(planCount) + productOrderEOtemp.getHasScheduleQuantity());
                                            this.baseMapper.updateById(productOrderEOtemp);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEOtemp.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("2");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }else{

                                            //保存生产计划
                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("铆接导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("2");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }
                                    } else {
                                        //如果存在则删除原来的生产计划和排产计划，重新插入生产计划和排产计划
                                        if (productOrderEO.getProduceStatus() == 0) {
                                            super.removeById(productOrderEO.getProductOrderId());
                                            this.scheduleOrderMapper.removeByProductOrderId(productOrderEO.getProductOrderId());

                                            //保存生产计划
                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("铆接导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("2");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);

                                        } else {
                                            throw new BusinessException("铆接第" + (i + 1) + "行零件号对应的生产计划已存在,且已开始生产");
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                //手工装配
                if (handwork.equals("3")){
                    List<Map> mapList = (List<Map>) list.get(2); //根据sheet获取
                    if(mapList!=null && mapList.size()>1){

                        //标题匹配
                        Integer size = mapList.get(0).size();
                        logger.info("========长度 ========"+size);
                        for(int j=4;j<size;j++){
                            String time = (String)mapList.get(0).get(j+"");
                            if(time.trim().contains(begintitle)){
                                beginCount = j;
                                break;
                            }
                        }

                        if (beginCount == 0){
                            throw new BusinessException("请确认手工装配sheet页文件标题头的日期是否存在选择的日期！");
                        }
                        for(int i=3;i < mapList.size();i++){
                            Map  map = mapList.get(i);
                            //初始参数校验
                            //设备机台编号
                            String machineNo = (String) map.get("0");
                            if (machineNo.equals("")) {
                                errorMsg = errorMsg + "手工装配第" + (i + 1) + "行设备机台编号为空\n";
                            } else {
                                MachineEO machineEO = this.baseMapper.selectMachineEO(machineNo, orgId);
                                if (machineEO == null) {
                                    errorMsg = errorMsg + "手工装配第" + (i + 1) + "行设备机台编号对应的设备不存在\n";
                                }
                            }

                            //零件号
                            String elementNo = (String) map.get("1");
                            if (elementNo.equals("")) {
                                errorMsg = errorMsg + "手工装配第" + (i + 1) + "行零件号为空\n";
                            } else {
                                //根据零件号去工序表查找最大工序号
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);
                                if (workingProcedureTimeEO == null) {
                                    errorMsg = errorMsg + "手工装配第" + (i + 1) + "行零件号对应工序不存在\n";
                                }
                            }
                        }

                        //将错误信息返回，同时让客户必须保证数据的正确性
                        if(!errorMsg.isEmpty()){
                            throw new BusinessException(errorMsg);
                        }

                        for(int i=3;i < mapList.size();i++){
                            //第一行表头
                            Map  map0 = mapList.get(0);
                            //第三行表头
                            Map  map2 = mapList.get(2);

                            Map  map = mapList.get(i);

                            // 从选中的日期开始,往后推移1个星期
                            for(int p=beginCount; p<beginCount+14; p++) {
                                //计划数量
                                String planCount = (String) map.get(""+p);
                                if (planCount == null){
                                    planCount = "";
                                }
                                String beforePlanCount = (String) map.get(""+(p-1));
                                //初始参数校验
                                //设备机台编号
                                String machineNo = (String) map.get("0");
                                MachineEO machineEO =this.baseMapper.selectMachineEO(machineNo,orgId);

                                //排产日期
                                String planDate;
                                if (p%2==0){
                                    planDate = (String) map0.get(""+(p-1));
                                }else{
                                    planDate = (String) map0.get(""+p);
                                }

                                Calendar cal = Calendar.getInstance();
                                int year = cal.get(Calendar.YEAR);
                                String  PlanDate2  = planDate.replace("月","-").replace("日","");
                                String newPlanDate = year + "-"+ PlanDate2;
                                Date date = DateUtils.stringToDate(newPlanDate,"yyyy-MM-dd");
                                String checkdate = DateUtils.format(date,"yyyy-MM-dd");
                                //班次
                                String classOrder = (String) map2.get(""+p);

                                //零件号
                                String elementNo = (String) map.get("1");
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selectProcedureTimeEO(elementNo, orgId);

                                if (!planCount.equals("") && !planCount.equals("0")) {
                                    //根据物料ID和日期查询生产计划是否存在,不存在则新增生产计划和排产计划
                                    ProductOrderEO productOrderEO = this.baseMapper.selectProductOrderEO(workingProcedureTimeEO.getMaterialId(), orgId, checkdate, classOrder,machineEO.getMachineId());
                                    if (productOrderEO == null) {
                                        //根据物料ID和日期查询生产计划是否存在，默认为一个物料当天只存在一个生产计划
                                        ProductOrderEO productOrderEOtemp = this.baseMapper.selectProductOrderEOtemp(workingProcedureTimeEO.getMaterialId(), orgId, checkdate);
                                        if (productOrderEOtemp !=null) {
                                            productOrderEOtemp.setPlanProduceQuantity(Double.valueOf(planCount) + productOrderEOtemp.getPlanProduceQuantity());
                                            productOrderEOtemp.setHasScheduleQuantity(Double.valueOf(planCount) + productOrderEOtemp.getHasScheduleQuantity());
                                            this.baseMapper.updateById(productOrderEOtemp);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEOtemp.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("3");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }else{
                                            //保存生产计划
                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("手工装配导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("3");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);
                                        }


                                    } else {
                                        //如果存在则删除原来的生产计划和排产计划，重新插入生产计划和排产计划
                                        if (productOrderEO.getProduceStatus() == 0) {
                                            super.removeById(productOrderEO.getProductOrderId());
                                            this.scheduleOrderMapper.removeByProductOrderId(productOrderEO.getProductOrderId());

                                            //保存生产计划
                                            ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                            productOrderEO1.setOrgId(orgId);
                                            // 生成业务编码
                                            String voucherNo = this.businessCodeGenerator.generateNextCodeNoOrgId("srm_product_order", productOrderEO1);
                                            AssertUtils.isBlank(voucherNo);
                                            productOrderEO1.setVoucherNo(voucherNo);
                                            productOrderEO1.setMaterialId(workingProcedureTimeEO.getMaterialId());
                                            productOrderEO1.setElementNo(elementNo);
                                            productOrderEO1.setPlanFinishDate(date);
                                            productOrderEO1.setPlanProduceQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setHasScheduleQuantity(Double.valueOf(planCount));
                                            productOrderEO1.setProduceStatus(0);
                                            productOrderEO1.setScheduleStatus(2);
                                            productOrderEO1.setMemo("手工装配导入排产单反向生成生产订单");
                                            super.save(productOrderEO1);

                                            //保存排产单
                                            ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                            // 生成业务编码
                                            String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                            scheduleOrderEO.setVoucherNo(voucherNo1);
                                            scheduleOrderEO.setOrgId(orgId);
                                            scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                            scheduleOrderEO.setPlanProductDate(date);
                                            scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                            scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                            scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                            scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                            scheduleOrderEO.setStatus(0);
                                            scheduleOrderEO.setClassOrder(classOrder);
                                            scheduleOrderEO.setImportType("3");
                                            scheduleOrderEO.setCheckStatus(0L);

                                            this.scheduleOrderMapper.insert(scheduleOrderEO);

                                        } else {
                                            throw new BusinessException("手工装配第" + (i + 1) + "行零件号对应的生产计划已存在,且已开始生产");
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                //冲压
                if (punching.equals("4")){
                    List<Map> mapList = (List<Map>) list.get(3); //根据sheet获取

                    if(mapList!=null && mapList.size()>1){

                        for(int i=1;i < mapList.size();i++) {

                            Map map = mapList.get(i);
                            //零件号
                            String elementNo = (String) map.get("2");
                            if (elementNo.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行零件号为空\n";
                            }

                            //设备机台编号
                            String machineNo = (String) map.get("1");
                            if (machineNo.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行设备机台编号为空\n";
                            } else {
                                MachineEO  machineEO = this.baseMapper.selectMachineEO(machineNo, orgId);
                                if (machineEO == null) {
                                    errorMsg = errorMsg + "冲压表第" + (i + 1) + "行设备机台编号对应的设备不存在\n";
                                }
                            }

                            //计划数量
                            String planCount = (String) map.get("6");
                            if (planCount.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行计划数量为空\n";
                            }

                            //排产日期
                            String planDate = (String) map.get("3");
                            if (planDate.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行排产日期为空\n";
                            }

                            //班次
                            String classOrder = (String) map.get("4");
                            if (classOrder.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行班次为空\n";
                            }

                            //生产工序号
                            String workingProcedureCode = (String) map.get("5");
                            if (workingProcedureCode.equals("")) {
                                errorMsg = errorMsg + "冲压表第" + (i + 1) + "行班次为空\n";
                            } else {
                                WorkingProcedureTimeEO workingProcedureTimeEO = this.baseMapper.selecProcedureTimeEOByProcedureCode(elementNo, workingProcedureCode, orgId);
                                if (workingProcedureTimeEO == null) {
                                    errorMsg = errorMsg + "冲压表第" + (i + 1) + "行零件号对应的生产工序号不存在\n";
                                }
                            }
                        }

                        //将错误信息返回，同时让客户必须保证数据的正确性
                        if(!errorMsg.isEmpty()){
                            throw new BusinessException(errorMsg);
                        }

                        for(int i=1;i < mapList.size();i++){

                            Map  map = mapList.get(i);
                            //零件号
                            String elementNo = (String) map.get("2");

                            //设备机台编号
                            String machineNo = (String) map.get("1");
                            MachineEO machineEO =this.baseMapper.selectMachineEO(machineNo,orgId);

                            //计划数量
                            String planCount = (String) map.get("6");

                            //排产日期
                            String  planDate = (String) map.get("3");

                            //班次
                            String classOrder = (String) map.get("4");

                            //生产工序号
                            String workingProcedureCode = (String) map.get("5");
                            WorkingProcedureTimeEO workingProcedureTimeEO =this.baseMapper.selecProcedureTimeEOByProcedureCode(elementNo,workingProcedureCode,orgId);

                            Calendar cal = Calendar.getInstance();
                            int year = cal.get(Calendar.YEAR);
                            String  PlanDate2  = planDate.replace("月","-").replace("日","");
                            String newPlanDate = year + "-"+ PlanDate2;
                            Date date = DateUtils.stringToDate(newPlanDate,"yyyy-MM-dd");
                            if(begindate.getTime() <= date.getTime() && date.getTime() <= enddate.getTime()){
                                //保存排产单
                                ScheduleOrderEO scheduleOrderEO = new ScheduleOrderEO();
                                // 生成业务编码
                                String voucherNo1 = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrderEO, orgId);
                                scheduleOrderEO.setVoucherNo(voucherNo1);
                                scheduleOrderEO.setOrgId(orgId);
                                //scheduleOrderEO.setProductOrderId(productOrderEO1.getProductOrderId());
                                scheduleOrderEO.setPlanProductDate(date);
                                scheduleOrderEO.setWorkingProcedureTimeId(workingProcedureTimeEO.getWorkingProcedureTimeId());
                                scheduleOrderEO.setMachineId(machineEO.getMachineId());
                                scheduleOrderEO.setMachineCode(machineEO.getMachineCode());
                                scheduleOrderEO.setPlanProduceQuantity(Double.valueOf(planCount));
                                scheduleOrderEO.setStatus(0);
                                scheduleOrderEO.setClassOrder(classOrder);
                                scheduleOrderEO.setImportType("4");
                                scheduleOrderEO.setImportMaterialId(workingProcedureTimeEO.getMaterialId());
                                scheduleOrderEO.setImportStatus(0);
                                scheduleOrderEO.setImportElement(elementNo);
                                scheduleOrderEO.setImportProcedureCode(workingProcedureCode);
                                scheduleOrderEO.setCheckStatus(0L);

                                this.scheduleOrderMapper.insert(scheduleOrderEO);
                            }

                        }

                        //获取最大工序号的排产单反向生成生产订单
                        List<ScheduleOrderEO> scheduleOrderEOS = this.baseMapper.getScheduleOrderEO();
                        for (ScheduleOrderEO scheduleOrderEO:scheduleOrderEOS){
                            String checkdate = DateUtils.format(scheduleOrderEO.getPlanProductDate(),"yyyy-MM-dd");
                            ProductOrderEO productOrderEO = this.baseMapper.selectProductOrderEO2(scheduleOrderEO.getImportMaterialId(),orgId,checkdate,scheduleOrderEO.getClassOrder(),scheduleOrderEO.getImportProcedureCode());
                            if (productOrderEO == null){

                                ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                productOrderEO1.setOrgId(orgId);
                                // 生成业务编码
                                String voucherNo = this.businessCodeGenerator.generateNextCode("srm_product_order", productOrderEO1, orgId);
                                AssertUtils.isBlank(voucherNo);
                                productOrderEO1.setVoucherNo(voucherNo);
                                productOrderEO1.setMaterialId(scheduleOrderEO.getImportMaterialId());
                                productOrderEO1.setElementNo(scheduleOrderEO.getImportElement());
                                productOrderEO1.setPlanFinishDate(scheduleOrderEO.getPlanProductDate());
                                productOrderEO1.setPlanProduceQuantity(scheduleOrderEO.getPlanProduceQuantity());
                                productOrderEO1.setHasScheduleQuantity(scheduleOrderEO.getPlanProduceQuantity());
                                productOrderEO1.setProduceStatus(0);
                                productOrderEO1.setScheduleStatus(2);
                                productOrderEO1.setMemo("冲压导入排产单反向生成生产订单");
                                super.save(productOrderEO1);

                                //更新剩余排产单的导入状态和生产订单ID
                                this.baseMapper.updateScheduleEO(scheduleOrderEO.getImportMaterialId(),orgId,productOrderEO1.getProductOrderId(),scheduleOrderEO.getPlanProductDate());

                            }else{

                                if(productOrderEO.getProduceStatus()==0){
                                    /*super.removeById(productOrderEO.getProductOrderId());

                                    ProductOrderEO productOrderEO1 = new ProductOrderEO();
                                    productOrderEO1.setOrgId(orgId);
                                    // 生成业务编码
                                    String voucherNo = this.businessCodeGenerator.generateNextCode("srm_product_order", productOrderEO1, orgId);
                                    AssertUtils.isBlank(voucherNo);
                                    productOrderEO1.setVoucherNo(voucherNo);
                                    productOrderEO1.setMaterialId(scheduleOrderEO.getImportMaterialId());
                                    productOrderEO1.setElementNo(scheduleOrderEO.getImportElement());
                                    productOrderEO1.setPlanFinishDate(scheduleOrderEO.getPlanProductDate());
                                    productOrderEO1.setPlanProduceQuantity(scheduleOrderEO.getPlanProduceQuantity());
                                    productOrderEO1.setHasScheduleQuantity(Double.valueOf(scheduleOrderEO.getPlanProduceQuantity()));
                                    productOrderEO1.setProduceStatus(0);
                                    productOrderEO1.setScheduleStatus(2);
                                    productOrderEO1.setMemo("冲压导入排产单反向生成生产订单");
                                    super.save(productOrderEO1);*/

                                    //更新剩余排产单的导入状态和生产订单ID
                                    this.baseMapper.updateScheduleEO(scheduleOrderEO.getImportMaterialId(),orgId,productOrderEO.getProductOrderId(),scheduleOrderEO.getPlanProductDate());

                                }else{
                                    throw new BusinessException("冲压排产单零件号"+ scheduleOrderEO.getElementNo()+"对应的"+ scheduleOrderEO.getPlanProductDate() +"生产计划已存在,且已开始生产");
                                }

                            }

                        }

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
        //return true;
    }

    // 生产拆分明细,采购订单,委外订单
    private void generatePurchaseOrders(ProductOrderEO productOrder, UserEO user, Map mapObj, Integer isMerge) {
        List<BomEO> purchaseBoms = (List<BomEO>) mapObj.get("purchaseBoms"); // 需产生采购订单的Bom
        List<ProductOrderReleaseDetailEO> pords = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        if(purchaseBoms!=null && purchaseBoms.size()>0) {
            for(BomEO bomItem : purchaseBoms) {
                PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
                if(isMerge.intValue() == 0) {
                    purchaseOrder.setType(1);
                    purchaseOrder.setSupplierId(bomItem.getSupplierId());
                    purchaseOrder.setOrgId(productOrder.getOrgId());
                    purchaseOrder.setMaterialId(bomItem.getMaterialId());
                    purchaseOrder.setMaterialName(bomItem.getMaterialName());
                    purchaseOrder.setSpecification(bomItem.getSpecification());
                    purchaseOrder.setMaterialCode(bomItem.getMaterialCode());
                    purchaseOrder.setInventoryCode(bomItem.getInventoryCode());
                    purchaseOrder.setElementNo(bomItem.getElementNo());
                    purchaseOrder.setUnitName(bomItem.getUnitName());
                    purchaseOrder.setCreateDate(now);
                    purchaseOrder.setPlanArriveDate(DateUtils.addDateDays(productOrder.getPlanFinishDate(), -1));
                    purchaseOrder.setPlanDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                    purchaseOrder.setNotDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                    purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
                    purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
                    purchaseOrder.setReturnedQuantity(Double.valueOf(0));
                    purchaseOrder.setStatus(0);
                    purchaseOrder.setCreateUserId(user.getUserId());
                    purchaseOrder.setCreateUserName(user.getUserName());
                    purchaseOrder.setIsChangeConfirm(1);
                    purchaseOrder.setMonthDate(calendar.getTime());
                    this.purchaseOrderService.save(purchaseOrder);
                }

                ProductOrderReleaseDetailEO pord = new ProductOrderReleaseDetailEO();
                pord.setProductOrderId(productOrder.getProductOrderId());
                pord.setType(1);
                pord.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
                pord.setOrgId(productOrder.getOrgId());
                pord.setMaterialId(bomItem.getMaterialId());
                pord.setElementNo(bomItem.getElementNo());
                pord.setPlanArriveDate(DateUtils.addDateDays(productOrder.getPlanFinishDate(), -1));
                pord.setPlanDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                pords.add(pord);
            }
        }

        List<BomEO> outsideBoms = (List<BomEO>) mapObj.get("outsideBoms");  // 需产生委外订单的Bom
        if(outsideBoms!=null && outsideBoms.size()>0) {
            for(BomEO bomItem : outsideBoms) {
                PurchaseOrderEO purchaseOrder = new PurchaseOrderEO();
                if(isMerge.intValue() == 0) {
                    purchaseOrder.setType(2);
                    purchaseOrder.setSupplierId(bomItem.getSupplierId());
                    purchaseOrder.setOrgId(productOrder.getOrgId());
                    purchaseOrder.setMaterialId(bomItem.getMaterialId());
                    purchaseOrder.setMaterialName(bomItem.getMaterialName());
                    purchaseOrder.setSpecification(bomItem.getSpecification());
                    purchaseOrder.setMaterialCode(bomItem.getMaterialCode());
                    purchaseOrder.setInventoryCode(bomItem.getInventoryCode());
                    purchaseOrder.setElementNo(bomItem.getElementNo());
                    purchaseOrder.setUnitName(bomItem.getUnitName());
                    purchaseOrder.setCreateDate(now);
                    purchaseOrder.setPlanArriveDate(DateUtils.addDateDays(productOrder.getPlanFinishDate(), -1));
                    purchaseOrder.setPlanDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                    purchaseOrder.setNotDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                    purchaseOrder.setActualDeliveryQuantity(Double.valueOf(0));
                    purchaseOrder.setQualifiedQuantity(Double.valueOf(0));
                    purchaseOrder.setReturnedQuantity(Double.valueOf(0));
                    purchaseOrder.setStatus(0);
                    purchaseOrder.setCreateUserId(user.getUserId());
                    purchaseOrder.setCreateUserName(user.getUserName());
                    purchaseOrder.setIsChangeConfirm(1);
                    purchaseOrder.setMonthDate(calendar.getTime());
                    this.purchaseOrderService.save(purchaseOrder);
                }

                ProductOrderReleaseDetailEO pord = new ProductOrderReleaseDetailEO();
                pord.setProductOrderId(productOrder.getProductOrderId());
                pord.setType(2);
                pord.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
                pord.setOrgId(productOrder.getOrgId());
                pord.setMaterialId(bomItem.getMaterialId());
                pord.setElementNo(bomItem.getElementNo());
                pord.setPlanArriveDate(DateUtils.addDateDays(productOrder.getPlanFinishDate(), -1));
                pord.setPlanDeliveryQuantity(bomItem.getAmount() * productOrder.getPlanProduceQuantity());
                pords.add(pord);
            }
        }

        if(pords!=null && pords.size()>0) {
            this.productOrderReleaseDetailService.saveBatch(pords);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result release(Long[] ids, UserEO user) throws BusinessException {
        Result result = new Result();

        if(ids==null || ids.length==0) {
            throw new BusinessException("请选择数据!");
        }

        String sqlStr = "";
        for(Long id : ids) {
            sqlStr += (id + ",");
        }
        if(!"".equals(sqlStr)) {
            sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
        }
        sqlStr = "(" + sqlStr + ")";

        List<ProductOrderEO> productOrders = this.baseMapper.getByProductOrderIds(sqlStr);
        if(productOrders!=null && productOrders.size()>0) {
            Set set = new HashSet();
            String errorMsg = "";

            for(ProductOrderEO productOrder : productOrders) {
                if(productOrder.getReleaseStatus().intValue() == 1) {
                    errorMsg += ("&nbsp&nbsp" + productOrder.getVoucherNo() + "<br/>");
                }
                set.add(productOrder.getMaterialId());
            }

            if(!"".equals(errorMsg)) {
                errorMsg = "生产计划:<br/>" + errorMsg + "已创建过物料订单,请检查!";
                throw new BusinessException(errorMsg);
            }

            if(set!=null && set.size()>0) {
                List<BomEO> boms = this.bomMapper.getByMaterialIds(set);
                Map bomMap = new HashedMap();
                if(boms!=null && boms.size()>0) {
                    for(BomEO bom : boms) {
                        if(bom.getParentBomId().intValue() != 0) {
                            for(ProductOrderEO productOrder : productOrders) {
                                if(productOrder.getMaterialId().longValue() == bom.getMaterialId().longValue()) {
                                    errorMsg += ("&nbsp&nbsp" + productOrder.getVoucherNo() + "<br/>");
                                }
                            }
                        }

                        if(!bomMap.containsKey(bom.getMaterialId())) {
                            bomMap.put(bom.getMaterialId(), bom);
                        }
                    }

                    if(!"".equals(errorMsg)) {
                        errorMsg = "生产计划:<br/>" + errorMsg + "的物料不是总成,请检查!";
                        throw new BusinessException(errorMsg);
                    }

                    // 是否需要查询物料关系表
                    Map releaseMap = new HashedMap();
                    JSONObject jsonObject = ExcelUtils.parseJsonFile("config/config.json");
                    JSONObject bomJsonObject = jsonObject.getJSONObject("bom");
                    boolean isQueryMaterialRelationship = bomJsonObject.getBoolean("isQueryMaterialRelationship");
                    for(Object obj : bomMap.keySet()) {
                        BomEO bom = (BomEO) bomMap.get(obj);
                        Map bomsMap = this.bomService.getAllBomsForReleaseWeekPlan(bom, isQueryMaterialRelationship);
                        errorMsg += bomsMap.get("errorMsg");
                        releaseMap.put(bom.getMaterialId(), bomsMap);
                    }

                    if(!"".equals(errorMsg)) {
                        throw new BusinessException(errorMsg);
                    }

                    String paramValue = this.paramsService.getParamByKey("sys_product_order_release_detail_is_merge");
                    for(ProductOrderEO productOrder : productOrders) {
                        Map mapObj = (Map) releaseMap.get(productOrder.getMaterialId());
                        this.generatePurchaseOrders(productOrder, user, mapObj, Integer.valueOf(paramValue).intValue());
                        productOrder.setReleaseStatus(1);
                    }

                    this.updateBatchById(productOrders);
                } else {
                    throw new BusinessException("选择的数据不存在Bom,请检查!");
                }
            } else {
                throw new BusinessException("选择的数据不存在物料,请检查!");
            }
        } else {
            throw new BusinessException("选择数据已不存在,请刷新!");
        }

        return result;
    }
}
