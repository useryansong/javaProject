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
import com.xchinfo.erp.srm.mapper.StopWarnLogMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;

@Service
public class StopWarnLogService extends BaseServiceImpl<StopWarnLogMapper, StopWarnLogEO> {

	@Autowired
	private StopWarnLogMapper stopWarnLogMapper;
	
	@Autowired
	public BusinessCodeGenerator businessCodeGenerator;

	public List<StopWarnLogEO> getPage(Map map) {
		return this.baseMapper.getPage(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public StopWarnLogEO saveEntity(StopWarnLogEO entity, UserEO user) throws BusinessException {

		if(entity.getStopReanson() == null || entity.getStopReanson().trim().length() == 0){
			throw new BusinessException("当前原因不能为空");
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("stopReanson", entity.getStopReanson().trim());
		map.put("orgId", entity.getOrgId());
		List<StopWarnLogEO> list = stopWarnLogMapper.getPage(map);
		
		if(list!=null && list.size() > 0){
			throw new BusinessException("当前原因已存在");
		}
		
		// 生成业务编码
		entity.setCreatedBy(user.getUserName());
		entity.setCreatedTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		super.save(entity);

		return entity;
	}

	public void updateStopWarnLog(StopWarnLogEO entity, UserEO user) {
		
		if(entity.getStopReanson() == null || entity.getStopReanson().trim().length() == 0){
			throw new BusinessException("当前原因不能为空");
		}
		
		StopWarnLogEO current = stopWarnLogMapper.selectById(entity.getStopWarnLogId());
		
		if(!current.getStopReanson().trim().equals(entity.getStopReanson().trim())){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("stopReanson", entity.getStopReanson().trim());
			map.put("orgId", entity.getOrgId());
			map.put("currentId",entity.getStopWarnLogId());
			List<StopWarnLogEO> list = stopWarnLogMapper.getPage(map);
			if(list!= null  && list.size() > 0){
				throw new BusinessException("当前原因已存在");
			}
		}
		
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		stopWarnLogMapper.updateById(entity);
		
	}

}
