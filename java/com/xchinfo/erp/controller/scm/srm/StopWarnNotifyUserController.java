package com.xchinfo.erp.controller.scm.srm;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyUserEO;
import com.xchinfo.erp.srm.service.StopWarnNotifyUserService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/stopWarnNotifyUser")
public class StopWarnNotifyUserController extends BaseController {

	@Autowired
	private StopWarnNotifyUserService stopWarnNotifyUserService;

	/**
	 * 分页查找
	 *
	 * @param criteria
	 * @return
	 */
	@PostMapping("page")
//	@RequiresPermissions("srm:stopWarnNotify:info")
	public Result<IPage<StopWarnNotifyUserEO>> page(@RequestBody Criteria criteria) {
		logger.info("======== StopWarnLogController.list() ========");

		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		List<StopWarnNotifyUserEO> totalList = stopWarnNotifyUserService.getPage(map);
		map.put("currentIndexFlag", true);
		List<StopWarnNotifyUserEO> pageList = stopWarnNotifyUserService.getPage(map);
		IPage<StopWarnNotifyUserEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<StopWarnNotifyUserEO>>().ok(page);
	}

	/**
	 * 根据ID查找
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
//	@RequiresPermissions("srm:stopWarnNotify:info")
	public Result<StopWarnNotifyUserEO> info(@PathVariable("id") Long id) {
		logger.info("======== StopWarnLogController.info(entity => " + id + ") ========");
		StopWarnNotifyUserEO entity = stopWarnNotifyUserService.getById(id);
		return new Result<StopWarnNotifyUserEO>().ok(entity);
	}

	/**
	 * 新增停机告警通知人员
	 * 
	 * @param entity
	 * @return
	 */
	@PostMapping("addStopWarnNotifyUser")
	@OperationLog("新增停机告警通知人员")
	public Result<StopWarnNotifyUserEO> addStopWarnNotify(@RequestBody StopWarnNotifyUserEO entity) {
		logger.info("======== StopWarnLogController.addStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		
		StopWarnNotifyUserEO entityFromDb = stopWarnNotifyUserService.saveEntity(entity, user);
		return new Result();
	}

	/**
	 * 更新停机告警通知人员
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("updateStopWarnNotifyUser")
	@OperationLog("更新停机告警通知人员")
	public Result<StopWarnNotifyUserEO> updateStopWarnNotify(@RequestBody StopWarnNotifyUserEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		stopWarnNotifyUserService.updateStopWarnNotify(entity,user);
		
		return new Result();
	}

	/**
	 * 修改停机告警通知人员状态
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("changeNotifyUserStatus")
	@OperationLog("修改停机告警通知人员状态")
	public Result<StopWarnNotifyUserEO> changeNotifyUserStatus(@RequestBody StopWarnNotifyUserEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStatus(entity.getStatus() == 0 ? 1 : 0);
		stopWarnNotifyUserService.updateById(entity);
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
		stopWarnNotifyUserService.removeByIds(ids);
		return new Result();
	}

	
}
