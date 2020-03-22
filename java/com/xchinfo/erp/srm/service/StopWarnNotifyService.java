package com.xchinfo.erp.srm.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import com.alibaba.druid.filter.AutoLoad;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyEO;
import com.xchinfo.erp.srm.mapper.StopWarnNotifyMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;

@Service
public class StopWarnNotifyService extends BaseServiceImpl<StopWarnNotifyMapper, StopWarnNotifyEO> {

	@Autowired
	private StopWarnNotifyMapper stopWarnNotifyMapper;
	
	@Autowired
	public BusinessCodeGenerator businessCodeGenerator;

	public List<StopWarnNotifyEO> getPage(Map map) {
		return this.baseMapper.getPage(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public StopWarnNotifyEO saveEntity(StopWarnNotifyEO entity, UserEO user) throws BusinessException {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("warnLevel", entity.getWarnLevel());
		map.put("stopWarnLogId", entity.getStopWarnLogId());
		
		List<StopWarnNotifyEO> list = stopWarnNotifyMapper.getPage(map);
		
		if(list!=null && list.size() > 0){
			throw new BusinessException("当前告警等級已存在");
		}
		
		// 生成业务编码
		entity.setStatus(0);
		entity.setCreatedBy(user.getUserName());
		entity.setCreatedTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		super.save(entity);

		return entity;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateStopWarnNotify(UserEO user, StopWarnNotifyEO entity) {
		
		StopWarnNotifyEO current = stopWarnNotifyMapper.selectById(entity.getStopWarnNotifyId());
		if(current == null){
			throw new BusinessException("当前告警通知不存在");
		}
		if(current.getWarnLevel() != entity.getWarnLevel()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("warnLevel", entity.getWarnLevel());
			map.put("stopWarnLogId", entity.getStopWarnLogId());
			
			List<StopWarnNotifyEO> list = stopWarnNotifyMapper.getPage(map);
			
			if(list!=null && list.size() > 0){
				throw new BusinessException("当前告警等級已存在");
			}
		}
		
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		stopWarnNotifyMapper.updateById(entity);
		
	}

}
