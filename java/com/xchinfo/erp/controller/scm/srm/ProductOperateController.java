package com.xchinfo.erp.controller.scm.srm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.srm.entity.StopWarnNotifyUserEO;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.utils.CommonUtil;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/productOperate")
public class ProductOperateController extends BaseController {

	@Autowired
	private ScheduleOrderService scheduleOrderService;

	/**
	 * 获取排产单(生产订单详情)
	 * 
	 * @return
	 */
	@PostMapping("getScheduleOrder")
	public Result<IPage<ScheduleOrderEO>> getScheduleOrder(@RequestBody Criteria criteria) {
		logger.info("======== ProductOperateController.getScheduleOrder() ========");
		Map<String, Object> map = CommonUtil.criteriaToMap(criteria);
		map.put("orgId", getOrgId());
		
		if(map.get("inventoryDate") != null && map.get("inventoryDate").toString().trim().length() != 0){
			map.put("startDate", map.get("inventoryDate").toString()+" 00:00:00");
			map.put("endDate", map.get("inventoryDate").toString()+" 23:59:59");
		}
		
		List<ScheduleOrderEO> totalList = scheduleOrderService.getScheduleOrder(map);
		map.put("currentIndexFlag", true);
		List<ScheduleOrderEO> pageList = scheduleOrderService.getScheduleOrder(map);
		IPage<ScheduleOrderEO> page = CommonUtil.listToPage(totalList, pageList, map);
		return new Result<IPage<ScheduleOrderEO>>().ok(page);
	}
	
	/**
	 * 获取生产车间
	 * 
	 * @return
	 */
	@GetMapping("getWorkShop")
	public Map<String,Object> getWorkShop() {
		logger.info("======== ProductOperateController.getWorkShop() ========");
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("orgId", getOrgId());
		List<Map<String,Object>> workShopList = scheduleOrderService.getWorkShop(map);
		Map<String,Object> res = new HashMap<String,Object>();
		res.put("code", 0);
		res.put("list", workShopList);
		return res;
	}
	
	
	/**
	 * 调试
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("debugProduct")
	@OperationLog("调试")
	public Result debugProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.startProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.debugProduct(user,entity);
		return new Result();
	}
	
	/**
	 * 停止调试
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("stopDebugProduct")
	@OperationLog("停止调试")
	public Result stopDebugProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.startProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.stopDebugProduct(user,entity);
		return new Result();
	}
	

	/**
	 * 开始生产
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("startProduct")
	@OperationLog("开始生产")
	public Result startProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.startProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.startProduct(user,entity);
		return new Result();
	}
	
    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{scheduleOrderId}")
    public Result<ScheduleOrderEO> info(@PathVariable("scheduleOrderId") String scheduleOrderId){
        logger.info("======== ProductOperateController.info(entity => "+scheduleOrderId+") ========");
        Map<String,Object> condition = new HashMap<String,Object>();
        condition.put("scheduleOrderId", scheduleOrderId);
        List<ScheduleOrderEO> list = scheduleOrderService.getScheduleOrder(condition);
        return new Result<ScheduleOrderEO>().ok(list.get(0));
    }
	

	/**
	 * 停止生产
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("stopProduct")
	@OperationLog("停止生产")
	public Result stopProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.stopProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		
		scheduleOrderService.stopProduct(user,entity);
		return new Result();
	}
	
	/**
	 * 处理停机
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("dealStopProduct")
	@OperationLog("处理停机")
	public Result dealStopProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.stopProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.dealStopProduct(user,entity);
		return new Result();
	}
	
	
	/**
	 * 修改排班状态
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("callProduct")
	@OperationLog("修改停机告警通知人员状态")
	public Result callProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.callProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.callProduct(user,entity);
		return new Result();
	}
	
	/**
	 * 修改排班状态
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("dealCallProduct")
	@OperationLog("修改停机告警通知人员状态")
	public Result dealCallProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.dealCallProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.dealCallProduct(user,entity);
		return new Result();
	}
	
	
	/**
	 * 修改排班状态
	 * 
	 * @param
	 * @return
	 */
	@PostMapping("returnProduct")
	@OperationLog("修改停机告警通知人员状态")
	public Result returnProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== ProductOperateController.dealCallProduct(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.returnProduct(user,entity);
		return new Result();
	}
	
	
	@PostMapping("finishProduct")
	@OperationLog("修改停机告警通知人员状态")
	public Result finishProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.finishProduct(user,entity);
		return new Result();
	}
	
	@PostMapping("updateProduct")
	@OperationLog("修改停机告警通知人员状态")
	public Result updateProduct(@RequestBody ScheduleOrderEO entity) {
		logger.info("======== StopWarnLogController.updateStopWarnLog(ID => " + entity.getId() + ") ========");
		ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
		UserEO user = super.getUser();
		scheduleOrderService.updateProduct(user,entity);
		return new Result();
	}
	

}
