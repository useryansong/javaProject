package com.xchinfo.erp.srm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import com.xchinfo.erp.scm.srm.entity.MachineProductLogEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.srm.mapper.MachineProductLogMapper;

@Service
public class MachineProductLogService extends BaseServiceImpl<MachineProductLogMapper, MachineProductLogEO> {

    @Autowired
    private MachineProductLogMapper machineProductLogMapper;

    public List<MachineProductLogEO> getList(Map<String, Object> map) {
        return machineProductLogMapper.getList(map);
    }


    public List<MachineProductLogEO> getMachineStopList(Map<String, Object> map) {
        List<MachineProductLogEO> list = machineProductLogMapper.getList(map);
        if(list!=null && list.size()>0) {
        }

        return list;
    }

    public List<MachineProductLogEO> expandMachineStop(List<MachineProductLogEO> pageList, Map<String, Object> map) {
        List<MachineProductLogEO> expandList = machineProductLogMapper.getExpandMachineStopList(map);
        List<MachineProductLogEO> presentList = new ArrayList<>();
        List<MachineProductLogEO> returnProductList = new ArrayList<>();

        if(pageList!=null && pageList.size()>0 && expandList!=null && expandList.size()>0) {
            for(MachineProductLogEO item : expandList) {
                if(item.getProductType().intValue() == 5) {
                    presentList.add(item);
                }
                if(item.getProductType().intValue() == 2) {
                    returnProductList.add(item);
                }
            }

            if(presentList!=null && presentList.size()>0) {
                for(MachineProductLogEO parent : pageList) {
                    for (MachineProductLogEO child : presentList) {
                        if (parent.getScheduleOrderId().longValue() == child.getScheduleOrderId().longValue() &&
                                parent.getMachineId().longValue() == child.getMachineId().longValue() &&
                                child.getParentProductLogId() != null &&
                                parent.getMachineProductLogId().longValue() == child.getParentProductLogId() &&
                                child.getProductType().intValue() == 5) {
                            // 处理到场时长
                            parent.setHandleTime(child.getCreatedTime());
                            parent.setPresentLast((child.getCreatedTime().getTime() - parent.getCreatedTime().getTime()) / 1000);
                            parent.setChildProductLogId(child.getMachineProductLogId());
                        }
                    }
                }

                if(returnProductList!=null && returnProductList.size()>0) {
                    for(MachineProductLogEO parent : pageList) {
                        for(MachineProductLogEO child : returnProductList) {
                            if(parent.getScheduleOrderId().longValue() == child.getScheduleOrderId().longValue() &&
                                    parent.getMachineId().longValue() == child.getMachineId().longValue() &&
                                    child.getParentProductLogId() != null && parent.getChildProductLogId()!=null &&
                                    parent.getChildProductLogId().longValue() == child.getParentProductLogId() &&
                                    child.getProductType().intValue() == 2) {
                                // 停机处理时长
                                parent.setReturnProductTime(child.getCreatedTime());
                                parent.setReturnProductLast((child.getCreatedTime().getTime() - parent.getHandleTime().getTime()) / 1000);
                            }
                        }
                    }
                }
            }
        }

        return pageList;
    }
}
