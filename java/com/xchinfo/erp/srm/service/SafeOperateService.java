package com.xchinfo.erp.srm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import com.google.gson.Gson;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.mapper.MachineMapper;
import com.xchinfo.erp.config.SpringContextHolder;
import com.xchinfo.erp.scm.srm.entity.MachineWarnMsgEO;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyEO;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyUserEO;
import com.xchinfo.erp.scm.wms.entity.StockAccountEO;
import com.xchinfo.erp.srm.mapper.MachineProductLogMapper;
import com.xchinfo.erp.srm.mapper.MachineWarnMsgMapper;
import com.xchinfo.erp.srm.mapper.StopWarnNotifyMapper;
import com.xchinfo.erp.srm.mapper.StopWarnNotifyUserMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.MsgUtil;
import com.xchinfo.erp.wms.mapper.StockAccountMapper;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class SafeOperateService {

	
	private static final org.apache.shiro.mgt.SecurityManager securityManager = (org.apache.shiro.mgt.SecurityManager) SpringContextHolder
			.getBean("securityManager");
	
	@Autowired
	private StockAccountMapper stockAccountMapper;
	
	@Autowired
	private MachineWarnMsgService machineWarnMsgService;
	
	@Autowired
	private MachineMapper machineMapper;
	
	@Autowired
	private StopWarnNotifyMapper stopWarnNotifyMapper;
	
	@Autowired
	private StopWarnNotifyUserMapper stopWarnNotifyUserMapper;
	
	@Autowired
	private MachineProductLogMapper machineProductLogMapper;
	
	@Autowired
	private MachineWarnMsgMapper machineWarnMsgMapper;
	
	
	public Map<String,Object> getMachineList(UserEO userEO) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgId", userEO.getOrgId());
		List<MachineEO> debugList = machineMapper.getDebugMachineList(map);
		List<MachineEO> stopList = machineMapper.getStopMachineList(map);
		List<MachineEO> callList = machineMapper.getCallMachineList(map);
		List<MachineEO> runList = machineMapper.getRunMachineList(map);
		
		List<MachineEO> list = new ArrayList<MachineEO>();
		list.addAll(debugList);
		list.addAll(stopList);
		list.addAll(callList);
		list.addAll(runList);
		
		Map<String,Object> res = new HashMap<String,Object>();
		res.put("list", list);
		return res;
	}

	
	
	/*public void sendMsg(){
		
		stockAccountMapper.insertSynchro();
	}*/
	
	
	//@Scheduled(cron="0 */5 * * * *")
	public void sendMsg() {
		
		if(org.apache.shiro.util.ThreadContext.getSecurityManager() == null){
			org.apache.shiro.util.ThreadContext.bind(securityManager);
		}
		
		
		List<MachineWarnMsgEO> list = new ArrayList<MachineWarnMsgEO>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		//查询所有停机的设备
		List<MachineEO> stopList = machineMapper.getStopMachineList(map);
		
		if(stopList == null || stopList.size() == 0){
			return;
		}
		
		for(MachineEO machine : stopList){
			
			//查看设备是否已经有人到场，对应的设备告警是否是允许发短信
			if(machine.getNeedDealStop() == 1 && machine.getIsMsg() == 0){
				
				Map<String,Object> tmp = new HashMap<String,Object>();
				tmp.put("stopWarnLogId", machine.getStopWarnLogId());
				//查询告警的所有等级
				List<StopWarnNotifyEO> notifyList = stopWarnNotifyMapper.getPage(tmp);
				
				if(notifyList == null || notifyList.size() == 0){
					continue;
				}
				
				for(StopWarnNotifyEO stopWarnNotify : notifyList){
					
					//如果当前时间-停机时间 >= 停机时长   就是要短信通知    ---   如果小于就找下一个
					if(System.currentTimeMillis() - machine.getStopTime().getTime() < stopWarnNotify.getStopDuration()*60*1000){
						continue;
					}
					Map<String,Object> tmp_map = new HashMap<String,Object>();
					tmp_map.put("stop_warn_notify_id", stopWarnNotify.getStopWarnNotifyId());
					//获取这些要发送短信的对象
					List<StopWarnNotifyUserEO> userList = stopWarnNotifyUserMapper.getPage(tmp_map);
					
					if(userList == null || userList.size() == 0){
						continue;
					}
					
					for(StopWarnNotifyUserEO  notifyUser : userList){
						
						if(notifyUser.getPhone() == null || notifyUser.getPhone().trim().length() == 0){
							continue;
						}
						
						Map<String,Object> tmp_condition = new HashMap<String,Object>();
						tmp_condition.put("stopWarnLogId", machine.getStopWarnLogId());
						tmp_condition.put("stopWarnId", machine.getStopWarnId());
						tmp_condition.put("stopWarnNotifyId", stopWarnNotify.getStopWarnNotifyId());
						tmp_condition.put("stopWarnNotifyUserId", notifyUser.getStopWarnNotifyUserId());
						tmp_condition.put("phone", notifyUser.getPhone());
						List<MachineWarnMsgEO> msgList = machineWarnMsgMapper.getPage(tmp_condition);
						
						if(msgList != null && msgList.size() != 0){
							continue;
						}
						
						//TODO   调用短信接口发送短信
						MachineWarnMsgEO msg = new MachineWarnMsgEO();
						msg.setMachineId(machine.getMachineId());
						msg.setStopWarnLogId(machine.getStopWarnLogId());
						msg.setStopWarnNotifyId(stopWarnNotify.getStopWarnNotifyId());
						msg.setStopWarnNotifyUserId(notifyUser.getStopWarnNotifyUserId());
						msg.setPhone(notifyUser.getPhone());
						msg.setMsgDesc(notifyUser.getEmployeeName()+",您有系统通知（请勿直接回复）："+machine.getMachineNumber()+"设备已停机，停机原因："+machine.getStopReanson()+",请及时处理！");
						msg.setWarnLevel(stopWarnNotify.getWarnLevel());
						msg.setStopReanson(machine.getStopReanson());
						msg.setOrgId(machine.getOrgId());
						msg.setMachineProductLogId(machine.getStopWarnId());
//						machineWarnMsgService.saveOrUpdate(msg);
						
						try {
							if(MsgUtil.sendSms( msg.getMsgDesc(), notifyUser.getPhone())){
								list.add(msg);
								machineWarnMsgMapper.insertMsg(msg);
							}
						} catch (IOException e) {
							continue;
						}
						
					}
				}
				
			}
			
		}
		
		System.out.println("list:"+new Gson().toJson(list));
		
	}

	
    
}
