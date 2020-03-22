package com.xchinfo.erp.controller.scm.srm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.StopWarnLogEO;
import com.xchinfo.erp.srm.service.StopWarnLogService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/stopWarnLog")
public class StopWarnLogController extends BaseController {

	@Autowired
	private StopWarnLogService stopWarnLogService;

	/**
	 * 分页查找
	 *
	 * @param criteria
	 * @return
	 */
	@PostMapping("page")
	@RequiresPermissions("srm:stopWarnLog:info")
	public Result<IPage<StopWarnLogEO>> page(@RequestBody Criteria criteria) {
		logger.info("======== StopWarnLogController.list() ========");

		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		map.put("orgId", getOrgId());
		List<StopWarnLogEO> totalList = stopWarnLogService.getPage(map);
		map.put("currentIndexFlag", true);
		List<StopWarnLogEO> pageList = stopWarnLogService.getPage(map);
		IPage<StopWarnLogEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<StopWarnLogEO>>().ok(page);
	}

	/**
	 * 根据ID查找
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	@RequiresPermissions("srm:stopWarnLog:info")
	public Result<StopWarnLogEO> info(@PathVariable("id") Long id) {
		logger.info("======== StopWarnLogController.info(entity => " + id + ") ========");
		StopWarnLogEO entity = stopWarnLogService.getById(id);
		return new Result<StopWarnLogEO>().ok(entity);
	}

	/**
	 * 新增停机告警
	 * 
	 * @param entity
	 * @return
	 */
	@PostMapping("addStopWarnLog")
	@OperationLog("新增停机告警")
	public Result<StopWarnLogEO> addStopWarnLog(@RequestBody StopWarnLogEO entity) {
		logger.info("======== StopWarnLogController.addStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		StopWarnLogEO entityFromDb = stopWarnLogService.saveEntity(entity, user);
		return new Result();
	}

	/**
	 * 更新停机告警
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("updateStopWarnLog")
	@OperationLog("更新停机告警")
	public Result<StopWarnLogEO> updateStopWarnLog(@RequestBody StopWarnLogEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		stopWarnLogService.updateStopWarnLog(entity, user);
		return new Result();
	}

	/**
	 * 删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@OperationLog("删除停机告警")
	public Result delete(@RequestBody Long[] ids) {
		logger.info("======== StopWarnLogController.delete() ========");
		AssertUtils.isArrayEmpty(ids, "id");
		stopWarnLogService.removeByIds(ids);
		return new Result();
	}

	
}
