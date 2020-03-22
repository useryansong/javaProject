package com.xchinfo.erp.utils;

import com.alibaba.fastjson.JSONObject;
import com.xchinfo.erp.common.U8DBConnectInfo;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.scm.wms.entity.DeliveryOrderEO;
import com.xchinfo.erp.scm.wms.entity.ReceiveOrderEO;
import com.xchinfo.erp.srm.mapper.DeliveryNoteMapper;
import com.xchinfo.erp.wms.mapper.DeliveryOrderMapper;
import com.xchinfo.erp.wms.mapper.ReceiveOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.StringUtils;
import org.yecat.mybatis.utils.jdbc.SqlActuator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/11/22
 */
@Configuration
@EnableScheduling
public class ScheduleTask implements SchedulingConfigurer {

    @Autowired
    private U8DBConnectInfo u8DBConnectInfo;

    @Autowired
    private ReceiveOrderMapper receiveOrderMapper;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    @Autowired
    private DeliveryNoteMapper deliveryNoteMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) throws BusinessException {
        // 从配置中获取执行周期
        JSONObject jo = ExcelUtils.parseJsonFile("config/schedule.json");
        if(jo == null) {
            System.out.println("执行U8同步定时任务: schedule.json配置出错,文件无内容!");
        } else {
            // 获取全局是否定时配置
            Boolean isCheckSync = false;
            if (jo.getBoolean("isCheckSync") != null) {
                isCheckSync = jo.getBoolean("isCheckSync");
            }

            if (!isCheckSync) {
                System.out.println("执行U8同步定时任务: 全局检查同步结果已被禁止!");
            } else {
                String productInStockCron = jo.getString("productInStockCron");
                String materialOutStockCron = jo.getString("materialOutStockCron");
                String saleOutStockCron = jo.getString("saleOutStockCron");
                String arrivedGoodsCron = jo.getString("arrivedGoodsCron");
                // 合法性校验
                if (StringUtils.isEmpty(productInStockCron)) {
                    System.out.println("执行U8成品入库同步定时任务: productInStockCron为空!");
                }
                if (StringUtils.isEmpty(materialOutStockCron)) {
                    System.out.println("执行U8生产领料同步定时任务: materialOutStockCron为空");
                }
                if (StringUtils.isEmpty(saleOutStockCron)) {
                    System.out.println("执行U8销售出库同步定时任务: saleOutStockCron为空!");
                }
                if (StringUtils.isEmpty(arrivedGoodsCron)) {
                    System.out.println("执行U8采购到货同步定时任务: arrivedGoodsCron为空!");
                }
            }
        }

        // 成品入库(生产日报导出)
        taskRegistrar.addTriggerTask(
                // 添加任务内容(Runnable)
                () -> System.out.println("执行U8成品入库同步定时任务: " + LocalDateTime.now().toLocalTime()),
                // 设置执行周期(Trigger)
                triggerContext -> {
                    // 合法性校验
                    JSONObject jsonObject = ExcelUtils.parseJsonFile("config/schedule.json");
                    String cron = jsonObject.getString("productInStockCron")==null?"0 */3 * * * ?":jsonObject.getString("productInStockCron");
                    Boolean flag = jsonObject.getBoolean("isCheckSync")==null?false:jsonObject.getBoolean("isCheckSync");
                    if(!flag) {
                        System.out.println("执行U8同步定时任务: 全局检查同步结果已被禁止!");
                        // 返回执行周期(Date)
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    } else {
                        if (!StringUtils.isEmpty(cron)) {
                            String updateU8Sql = "";
                            List<ReceiveOrderEO> receiveOrders = new ArrayList<>();
                            // 执行逻辑代码
                            // 从U8_API_Message查询,按同步单据类型(Type),VoucherID,和VoucherTableName分组,所有检查状态为0的
                            String selectSql = "select Type,VoucherID,VoucherTableName from U8_API_Message where Type='productinstock' group by Type,VoucherID,VoucherTableName";
                            List<Map<String, Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                            if (u8List != null && u8List.size() > 0) {
                                for (Map u8Map : u8List) {
                                    Long VoucherID = (Long) u8Map.get("VoucherID");
                                    String VoucherTableName = (String) u8Map.get("VoucherTableName");
                                    String sql = "select ID,HandleData,Status,IsChecked from U8_API_Message where VoucherID='" + VoucherID + "' and VoucherTableName='" + VoucherTableName + "' and Type='productinstock'";
                                    List<Map<String, Object>> u8InfoList = SqlActuator.excuteQuery(sql, u8DBConnectInfo);
                                    if (u8InfoList != null && u8InfoList.size() > 0) {
                                        int count = u8InfoList.size();
                                        int unSync = 0;
                                        int success = 0;
                                        int fail = 0;
                                        String successMsg = "";
                                        String failMsg = "";
                                        for (Map u8InfoMap : u8InfoList) {
                                            Short Status = (Short) u8InfoMap.get("Status");
                                            Short IsChecked = (Short) u8InfoMap.get("IsChecked");

                                            if(Status != null) {
                                                if(Status == 0) { // 尚未同步
                                                    unSync += 1;
                                                } else {
                                                    String HandleData = (String) u8InfoMap.get("HandleData");
                                                    if(Status == 1) { // 同步成功
                                                        successMsg += HandleData + "  ";
                                                        success +=1 ;
                                                    } else if(Status == 2) { // 同步失败
                                                        failMsg += HandleData + "  ";
                                                        fail += 1;
                                                    }
                                                    if(IsChecked==0) {
                                                        updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
                                                    }
                                                }

//                                              if(Status!=null && (Status==1||Status==2) && IsChecked==0) {
//                                                String HandleData = (String) u8InfoMap.get("HandleData");
//
//                                                if (Status==1) {
//                                                    successMsg += HandleData + "  ";
//                                                    success += 1;
//                                                } else if (Status==2) {
//                                                    failMsg += HandleData + "  ";
//                                                }
//                                                updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
//                                            }
                                            } else {
                                                unSync += 1;
                                            }
                                        }

                                        if (!successMsg.equals("")) {
                                            successMsg = "同步成功:" + successMsg + "   ";
                                        }
                                        if (!failMsg.equals("")) {
                                            failMsg = "同步失败:" + failMsg + "   ";
                                        }

                                        if(unSync == count) { // 尚未同步
                                            continue;
                                        } else {
                                            String msg = successMsg + failMsg;
                                            ReceiveOrderEO receiveOrder = new ReceiveOrderEO();
                                            receiveOrder.setReceiveId(VoucherID);
                                            receiveOrder.setSyncResult(msg);
                                            if(success == count) { // 全部成功
                                                receiveOrder.setSyncStatus(2);
                                            } else if (fail == count) { // 全部失败
                                                receiveOrder.setSyncStatus(4);
                                            } else { // 部分成功
                                                receiveOrder.setSyncStatus(3);
                                            }
                                            receiveOrders.add(receiveOrder);
                                        }
                                    }
                                }

                                if (!updateU8Sql.equals("")) {
                                    SqlActuator.excute(updateU8Sql, u8DBConnectInfo);
                                }
                                if (receiveOrders != null && receiveOrders.size() > 0) {
                                    this.receiveOrderMapper.updateByU8(receiveOrders);
                                }
                            }

                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        } else {
                            System.out.println("执行U8成品入库同步定时任务: schedule.json配置出错,productInStockCron节点值为空!");
                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        }
                    }
                }
        );

        // 生产领料 (生产日报导出生产领料单)
        taskRegistrar.addTriggerTask(
                // 添加任务内容(Runnable)
                () -> System.out.println("执行U8生产领料同步定时任务: " + LocalDateTime.now().toLocalTime()),
                // 设置执行周期(Trigger)
                triggerContext -> {
                    // 合法性校验
                    JSONObject jsonObject = ExcelUtils.parseJsonFile("config/schedule.json");
                    String cron = jsonObject.getString("materialOutStockCron")==null?"0 */3 * * * ?":jsonObject.getString("materialOutStockCron");
                    Boolean flag = jsonObject.getBoolean("isCheckSync")==null?false:jsonObject.getBoolean("isCheckSync");
                    if(!flag) {
                        System.out.println("执行U8同步定时任务: 全局检查同步结果已被禁止!");
                        // 返回执行周期(Date)
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    } else {
                        if (!StringUtils.isEmpty(cron)) {
                            String updateU8Sql = "";
                            List<DeliveryOrderEO> deliveryOrders = new ArrayList<>();
                            // 执行逻辑代码
                            // 从U8_API_Message查询,按同步单据类型(Type),VoucherID,和VoucherTableName分组,,所有检查状态为0的
                            String selectSql = "select Type,VoucherID,VoucherTableName from U8_API_Message where Type='materialoutstock' group by Type,VoucherID,VoucherTableName";
                            List<Map<String, Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                            if (u8List != null && u8List.size() > 0) {
                                for (Map u8Map : u8List) {
                                    Long VoucherID = (Long) u8Map.get("VoucherID");
                                    String VoucherTableName = (String) u8Map.get("VoucherTableName");
                                    String sql = "select ID,HandleData,Status,IsChecked from U8_API_Message where VoucherID='" + VoucherID + "' and VoucherTableName='" + VoucherTableName + "' and Type='materialoutstock'";
                                    List<Map<String, Object>> u8InfoList = SqlActuator.excuteQuery(sql, u8DBConnectInfo);
                                    if (u8InfoList != null && u8InfoList.size() > 0) {
                                        int count = u8InfoList.size();
                                        int unSync = 0;
                                        int success = 0;
                                        int fail = 0;
                                        String successMsg = "";
                                        String failMsg = "";
                                        for (Map u8InfoMap : u8InfoList) {
                                            Short Status = (Short) u8InfoMap.get("Status");
                                            Short IsChecked = (Short) u8InfoMap.get("IsChecked");

                                            if(Status != null) {
                                                if(Status == 0) { // 尚未同步
                                                    unSync += 1;
                                                } else {
                                                    String HandleData = (String) u8InfoMap.get("HandleData");
                                                    if(Status == 1) { // 同步成功
                                                        successMsg += HandleData + "  ";
                                                        success +=1 ;
                                                    } else if(Status == 2) { // 同步失败
                                                        failMsg += HandleData + "  ";
                                                        fail += 1;
                                                    }
                                                    if(IsChecked==0) {
                                                        updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
                                                    }
                                                }

//                                                if(Status!=null && (Status==1||Status==2) && IsChecked==0) {
//                                                    String HandleData = (String) u8InfoMap.get("HandleData");
//
//                                                    if (Status==1) {
//                                                        successMsg += HandleData + "  ";
//                                                        success += 1;
//                                                    } else if (Status==2) {
//                                                        failMsg += HandleData + "  ";
//                                                    }
//                                                    updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
//                                                }
                                            } else {
                                                unSync += 1;
                                            }
                                        }

                                        if (!successMsg.equals("")) {
                                            successMsg = "同步成功:" + successMsg + "   ";
                                        }
                                        if (!failMsg.equals("")) {
                                            failMsg = "同步失败:" + failMsg + "   ";
                                        }

                                        if(unSync == count) { // 尚未同步
                                            continue;
                                        } else {
                                            String msg = successMsg + failMsg;
                                            DeliveryOrderEO deliveryOrder = new DeliveryOrderEO();
                                            deliveryOrder.setDeliveryId(VoucherID);
                                            deliveryOrder.setSyncResult(msg);
                                            if(success == count) { // 全部成功
                                                deliveryOrder.setSyncStatus(2);
                                            } else if (fail == count) { // 全部失败
                                                deliveryOrder.setSyncStatus(4);
                                            } else { // 部分成功
                                                deliveryOrder.setSyncStatus(3);
                                            }
                                            deliveryOrders.add(deliveryOrder);
                                        }

                                    }
                                }

                                if (!updateU8Sql.equals("")) {
                                    SqlActuator.excute(updateU8Sql, u8DBConnectInfo);
                                }
                                if (deliveryOrders != null && deliveryOrders.size() > 0) {
                                    this.deliveryOrderMapper.updateByU8(deliveryOrders);
                                }
                            }

                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        } else {
                            System.out.println("执行U8生产领料同步定时任务: schedule.json配置出错,materialOutStockCron节点值为空!");
                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        }
                    }
                }
        );

        // 销售出库 (送货计划导出为销售订单)
        taskRegistrar.addTriggerTask(
                // 添加任务内容(Runnable)
                () -> System.out.println("执行U8销售出库同步定时任务: " + LocalDateTime.now().toLocalTime()),
                // 设置执行周期(Trigger)
                triggerContext -> {
                    // 合法性校验
                    JSONObject jsonObject = ExcelUtils.parseJsonFile("config/schedule.json");
                    String cron = jsonObject.getString("saleOutStockCron")==null?"0 */3 * * * ?":jsonObject.getString("saleOutStockCron");
                    Boolean flag = jsonObject.getBoolean("isCheckSync")==null?false:jsonObject.getBoolean("isCheckSync");
                    if(!flag) {
                        System.out.println("执行U8同步定时任务: 全局检查同步结果已被禁止!");
                        // 返回执行周期(Date)
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    } else {
                        if (!StringUtils.isEmpty(cron)) {
                            String updateU8Sql = "";
                            List<DeliveryOrderEO> deliveryOrders = new ArrayList<>();
                            // 执行逻辑代码
                            // 从U8_API_Message查询,按同步单据类型(Type),VoucherID,和VoucherTableName分组,,所有检查状态为0的
                            String selectSql = "select Type,VoucherID,VoucherTableName from U8_API_Message where Type='saleoutstock' group by Type,VoucherID,VoucherTableName";
                            List<Map<String, Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                            if (u8List != null && u8List.size() > 0) {
                                for (Map u8Map : u8List) {
                                    Long VoucherID = (Long) u8Map.get("VoucherID");
                                    String VoucherTableName = (String) u8Map.get("VoucherTableName");
                                    String sql = "select ID,HandleData,Status,IsChecked from U8_API_Message where VoucherID='" + VoucherID + "' and VoucherTableName='" + VoucherTableName + "' and Type='saleoutstock'";
                                    List<Map<String, Object>> u8InfoList = SqlActuator.excuteQuery(sql, u8DBConnectInfo);
                                    if (u8InfoList != null && u8InfoList.size() > 0) {
                                        int count = u8InfoList.size();
                                        int unSync = 0;
                                        int success = 0;
                                        int fail = 0;
                                        String successMsg = "";
                                        String failMsg = "";
                                        for (Map u8InfoMap : u8InfoList) {
                                            Short Status = (Short) u8InfoMap.get("Status");
                                            Short IsChecked = (Short) u8InfoMap.get("IsChecked");

                                            if(Status != null) {
                                                if(Status == 0) { // 尚未同步
                                                    unSync += 1;
                                                } else {
                                                    String HandleData = (String) u8InfoMap.get("HandleData");
                                                    if(Status == 1) { // 同步成功
                                                        successMsg += HandleData + "  ";
                                                        success +=1 ;
                                                    } else if(Status == 2) { // 同步失败
                                                        failMsg += HandleData + "  ";
                                                        fail += 1;
                                                    }
                                                    if(IsChecked==0) {
                                                        updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
                                                    }
                                                }

//                                                if(Status!=null && (Status==1||Status==2) && IsChecked==0) {
//                                                    String HandleData = (String) u8InfoMap.get("HandleData");
//
//                                                    if (Status==1) {
//                                                        successMsg += HandleData + "  ";
//                                                        success += 1;
//                                                    } else if (Status==2) {
//                                                        failMsg += HandleData + "  ";
//                                                    }
//                                                    updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
//                                                }
                                            } else {
                                                unSync += 1;
                                            }
                                        }

                                        if (!successMsg.equals("")) {
                                            successMsg = "同步成功:" + successMsg + "   ";
                                        }
                                        if (!failMsg.equals("")) {
                                            failMsg = "同步失败:" + failMsg + "   ";
                                        }


                                        if(unSync == count) { // 尚未同步
                                            continue;
                                        } else {
                                            String msg = successMsg + failMsg;
                                            DeliveryOrderEO deliveryOrder = new DeliveryOrderEO();
                                            deliveryOrder.setDeliveryId(VoucherID);
                                            deliveryOrder.setSyncPdResult(msg);
                                            if(success == count) { // 全部成功
                                                deliveryOrder.setSyncPdStatus(2);
                                            } else if (fail == count) { // 全部失败
                                                deliveryOrder.setSyncPdStatus(4);
                                            } else { // 部分成功
                                                deliveryOrder.setSyncPdStatus(3);
                                            }
                                            deliveryOrders.add(deliveryOrder);
                                        }
                                    }
                                }

                                if (!updateU8Sql.equals("")) {
                                    SqlActuator.excute(updateU8Sql, u8DBConnectInfo);
                                }
                                if (deliveryOrders != null && deliveryOrders.size() > 0) {
                                    this.deliveryOrderMapper.updateByPdU8(deliveryOrders);
                                }
                            }

                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        } else {
                            System.out.println("执行U8销售出库同步定时任务: schedule.json配置出错,saleOutStockCron节点值为空!");
                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        }
                    }
                }
        );

        // 采购到货 (采购收货导出)
        taskRegistrar.addTriggerTask(
                // 添加任务内容(Runnable)
                () -> System.out.println("执行U8采购到货同步定时任务: " + LocalDateTime.now().toLocalTime()),
                // 设置执行周期(Trigger)
                triggerContext -> {
                    // 合法性校验
                    JSONObject jsonObject = ExcelUtils.parseJsonFile("config/schedule.json");
                    String cron = jsonObject.getString("arrivedGoodsCron")==null?"0 */3 * * * ?":jsonObject.getString("arrivedGoodsCron");
                    Boolean flag = jsonObject.getBoolean("isCheckSync")==null?false:jsonObject.getBoolean("isCheckSync");
                    if(!flag) {
                        System.out.println("执行U8同步定时任务: 全局检查同步结果已被禁止!");
                        // 返回执行周期(Date)
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    } else {
                        if (!StringUtils.isEmpty(cron)) {
                            String updateU8Sql = "";
                            List<DeliveryNoteEO> deliveryNotes = new ArrayList<>();
                            // 执行逻辑代码
                            // 从U8_API_Message查询,按同步单据类型(Type),VoucherID,和VoucherTableName分组,所有检查状态为0的
                            String selectSql = "select Type,VoucherID,VoucherTableName from U8_API_Message where Type='arrivedgoods' group by Type,VoucherID,VoucherTableName";
                            List<Map<String, Object>> u8List = SqlActuator.excuteQuery(selectSql, u8DBConnectInfo);
                            if (u8List != null && u8List.size() > 0) {
                                for (Map u8Map : u8List) {
                                    Long VoucherID = (Long) u8Map.get("VoucherID");
                                    String VoucherTableName = (String) u8Map.get("VoucherTableName");
                                    String sql = "select ID,HandleData,Status,IsChecked from U8_API_Message where VoucherID='" + VoucherID + "' and VoucherTableName='" + VoucherTableName + "' and Type='arrivedgoods'";
                                    List<Map<String, Object>> u8InfoList = SqlActuator.excuteQuery(sql, u8DBConnectInfo);
                                    if (u8InfoList != null && u8InfoList.size() > 0) {
                                        int count = u8InfoList.size();
                                        int unSync = 0;
                                        int success = 0;
                                        int fail = 0;
                                        String successMsg = "";
                                        String failMsg = "";
                                        for (Map u8InfoMap : u8InfoList) {
                                            Short Status = (Short) u8InfoMap.get("Status");
                                            Short IsChecked = (Short) u8InfoMap.get("IsChecked");

                                            if(Status != null) {
                                                if(Status == 0) { // 尚未同步
                                                    unSync += 1;
                                                } else {
                                                    String HandleData = (String) u8InfoMap.get("HandleData");
                                                    if(Status == 1) { // 同步成功
                                                        successMsg += HandleData + "  ";
                                                        success +=1 ;
                                                    } else if(Status == 2) { // 同步失败
                                                        failMsg += HandleData + "  ";
                                                        fail += 1;
                                                    }
                                                    if(IsChecked==0) {
                                                        updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
                                                    }
                                                }

//                                                if(Status!=null && (Status==1||Status==2) && IsChecked==0) {
//                                                    String HandleData = (String) u8InfoMap.get("HandleData");
//
//                                                    if (Status==1) {
//                                                        successMsg += HandleData + "  ";
//                                                        success += 1;
//                                                    } else if (Status==2) {
//                                                        failMsg += HandleData + "  ";
//                                                    }
//                                                    updateU8Sql += "update U8_API_Message set IsChecked=1 where ID='" + u8InfoMap.get("ID") + "'";
//                                                }
                                            } else {
                                                unSync += 1;
                                            }
                                        }

                                        if (!successMsg.equals("")) {
                                            successMsg = "同步成功:" + successMsg + "   ";
                                        }
                                        if (!failMsg.equals("")) {
                                            failMsg = "同步失败:" + failMsg + "   ";
                                        }

                                        if(unSync == count) { // 尚未同步
                                            continue;
                                        } else {
                                            String msg = successMsg + failMsg;
                                            DeliveryNoteEO deliveryNote = new DeliveryNoteEO();
                                            deliveryNote.setDeliveryNoteId(VoucherID);
                                            deliveryNote.setSyncResult(msg);
                                            if(success == count) { // 全部成功
                                                deliveryNote.setSyncStatus(2);
                                            } else if (fail == count) { // 全部失败
                                                deliveryNote.setSyncStatus(4);
                                            } else { // 部分成功
                                                deliveryNote.setSyncStatus(3);
                                            }
                                            deliveryNotes.add(deliveryNote);
                                        }
                                    }
                                }

                                if (!updateU8Sql.equals("")) {
                                    SqlActuator.excute(updateU8Sql, u8DBConnectInfo);
                                }
                                if (deliveryNotes != null && deliveryNotes.size() > 0) {
                                    this.deliveryNoteMapper.updateByU8(deliveryNotes);
                                }
                            }

                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        } else {
                            System.out.println("执行U8采购到货同步定时任务: schedule.json配置出错,saleOutStockCron节点值为空!");
                            // 返回执行周期(Date)
                            return new CronTrigger(cron).nextExecutionTime(triggerContext);
                        }
                    }
                }
        );
    }
}
