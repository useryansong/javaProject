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
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyEO;
import com.xchinfo.erp.srm.service.StopWarnNotifyService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/stopWarnNotify")
public class StopWarnNotifyController extends BaseController {

	@Autowired
	private StopWarnNotifyService stopWarnNotifyService;

	/**
	 * 分页查找
	 *
	 * @param criteria
	 * @return
	 */
	@PostMapping("page")
//	@RequiresPermissions("srm:stopWarnNotify:info")
	public Result<IPage<StopWarnNotifyEO>> page(@RequestBody Criteria criteria) {
		logger.info("======== StopWarnLogController.list() ========");

		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		List<StopWarnNotifyEO> totalList = stopWarnNotifyService.getPage(map);
		map.put("currentIndexFlag", true);
		List<StopWarnNotifyEO> pageList = stopWarnNotifyService.getPage(map);
		IPage<StopWarnNotifyEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<StopWarnNotifyEO>>().ok(page);
	}

	/**
	 * 根据ID查找
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
//	@RequiresPermissions("srm:stopWarnNotify:info")
	public Result<StopWarnNotifyEO> info(@PathVariable("id") Long id) {
		logger.info("======== StopWarnLogController.info(entity => " + id + ") ========");
		StopWarnNotifyEO entity = stopWarnNotifyService.getById(id);
		return new Result<StopWarnNotifyEO>().ok(entity);
	}

	/**
	 * 新增停机告警通知级别
	 * 
	 * @param entity
	 * @return
	 */
	@PostMapping("addStopWarnNotify")
	@OperationLog("新增停机告警通知级别")
	public Result<StopWarnNotifyEO> addStopWarnNotify(@RequestBody StopWarnNotifyEO entity) {
		logger.info("======== StopWarnLogController.addStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		StopWarnNotifyEO entityFromDb = stopWarnNotifyService.saveEntity(entity, user);
		return new Result();
	}

	/**
	 * 更新停机告警通知级别
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("updateStopWarnNotify")
	@OperationLog("更新停机告警通知级别")
	public Result<StopWarnNotifyEO> updateStopWarnNotify(@RequestBody StopWarnNotifyEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		stopWarnNotifyService.updateStopWarnNotify(user,entity);
		
		return new Result();
	}

	/**
	 * 删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@OperationLog("删除停机告警通知级别")
	public Result delete(@RequestBody Long[] ids) {
		logger.info("======== StopWarnLogController.delete() ========");
		AssertUtils.isArrayEmpty(ids, "id");
		stopWarnNotifyService.removeByIds(ids);
		return new Result();
	}

	
}
