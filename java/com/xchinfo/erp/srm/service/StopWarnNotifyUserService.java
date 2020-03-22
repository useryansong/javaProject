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

import com.xchinfo.erp.scm.srm.entity.StopWarnLogEO;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyUserEO;
import com.xchinfo.erp.srm.mapper.StopWarnNotifyUserMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;

@Service
public class StopWarnNotifyUserService extends BaseServiceImpl<StopWarnNotifyUserMapper, StopWarnNotifyUserEO> {

	@Autowired
	public BusinessCodeGenerator businessCodeGenerator;
	
	@Autowired
	private StopWarnNotifyUserMapper stopWarnNotifyUserMapper;

	public List<StopWarnNotifyUserEO> getPage(Map map) {
		return this.baseMapper.getPage(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public StopWarnNotifyUserEO saveEntity(StopWarnNotifyUserEO entity, UserEO user) throws BusinessException {

		if(entity.getEmployeeId() == null ){
			throw new BusinessException("通知人不能为空");
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("employeeId", entity.getEmployeeId());
		map.put("stopWarnNotifyId", entity.getStopWarnNotifyId());
		List<StopWarnNotifyUserEO> list = stopWarnNotifyUserMapper.getPage(map);
		
		if(list!=null && list.size() > 0){
			throw new BusinessException("通知人已存在");
		}
		
		// 生成业务编码
//		entity.setUserCount(0);
		entity.setStatus(0);
		entity.setCreatedBy(user.getUserName());
		entity.setCreatedTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		super.save(entity);

		return entity;
	}

	public void updateStopWarnNotify(StopWarnNotifyUserEO entity, UserEO user) {
		
		if(entity.getEmployeeId() == null ){
			throw new BusinessException("通知人不能为空");
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("employeeId", entity.getEmployeeId());
		map.put("stopWarnNotifyId", entity.getStopWarnNotifyId());
		List<StopWarnNotifyUserEO> list = stopWarnNotifyUserMapper.getPage(map);
		
		if(list!=null && list.size() > 0){
			throw new BusinessException("通知人已存在");
		}
		
		
		
		
		
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		stopWarnNotifyUserMapper.updateById(entity);
		
	}

}
