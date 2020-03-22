package com.xchinfo.erp.controller.scm.srm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.Gson;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.ConstraintEO;
import com.xchinfo.erp.bsc.service.ConstraintService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.MachineProductLogEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.srm.service.MachineProductLogService;
import com.xchinfo.erp.srm.service.SafeManagerService;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.utils.CommonUtil;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/safeManager")
public class SafeManagerController extends BaseController {
    
	@Autowired
	private MachineProductLogService machineProductLogService;
    
    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    public Result<IPage<MachineProductLogEO>> page(@RequestBody Criteria criteria){
    	logger.info("======== SafeManagerController.getScheduleOrder() ========");
		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		map.put("orgId", getOrgId());
		List<MachineProductLogEO> totalList = machineProductLogService.getList(map);
		
		String planFinishDateStart = (String) map.get("planFinishDateStart");
		if(planFinishDateStart != null && planFinishDateStart.trim().length() != 0){
			map.put("planFinishDateStart", planFinishDateStart+" 00:00:00");
		}
		
		String planFinishDateEnd = (String) map.get("planFinishDateEnd");
		if(planFinishDateEnd != null && planFinishDateEnd.trim().length() != 0){
			map.put("planFinishDateEnd", planFinishDateEnd+" 23:59:59");
		}
		
		map.put("currentIndexFlag", true);
		List<MachineProductLogEO> pageList = machineProductLogService.getList(map);
		IPage<MachineProductLogEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<MachineProductLogEO>>().ok(page);
    }

	/**
	 * 分页查找
	 *
	 * @param criteria
	 * @return
	 */
	@PostMapping("machineStopPage")
	public Result<IPage<MachineProductLogEO>> machineStopPage(@RequestBody Criteria criteria){
		logger.info("======== SafeManagerController.machineStopPage() ========");
		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		map.put("orgId", getOrgId());
		List<MachineProductLogEO> totalList = machineProductLogService.getList(map);

		String planFinishDateStart = (String) map.get("planFinishDateStart");
		if(planFinishDateStart != null && planFinishDateStart.trim().length() != 0){
			map.put("planFinishDateStart", planFinishDateStart+" 00:00:00");
		}

		String planFinishDateEnd = (String) map.get("planFinishDateEnd");
		if(planFinishDateEnd != null && planFinishDateEnd.trim().length() != 0){
			map.put("planFinishDateEnd", planFinishDateEnd+" 23:59:59");
		}

		map.put("currentIndexFlag", true);
		List<MachineProductLogEO> pageList = machineProductLogService.getList(map);
		pageList = machineProductLogService.expandMachineStop(pageList, map);
		IPage<MachineProductLogEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<MachineProductLogEO>>().ok(page);
	}
}
