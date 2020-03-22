package com.xchinfo.erp.srm.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.bsc.mapper.MachineMapper;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import com.xchinfo.erp.hrms.mapper.EmployeeMapper;
import com.xchinfo.erp.scm.srm.entity.MachineProductLogEO;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.srm.entity.StopWarnLogEO;
import com.xchinfo.erp.srm.mapper.MachineProductLogMapper;
import com.xchinfo.erp.srm.mapper.ProductOrderMapper;
import com.xchinfo.erp.srm.mapper.ScheduleOrderMapper;
import com.xchinfo.erp.srm.mapper.StopWarnLogMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.*;

/**
 * @author zhongy
 * @date 2019/9/11
 */
@Service
public class ScheduleOrderService extends BaseServiceImpl<ScheduleOrderMapper, ScheduleOrderEO> {

	@Autowired
	private EmployeeMapper employeeMapper;

	@Autowired
	private StopWarnLogMapper stopWarnLogMapper;

	@Autowired
	private MachineMapper machineMapper;

	@Autowired
	private ProductOrderMapper productOrderMapper;

	@Autowired
	private ProductOrderService productOrderService;

	@Autowired
	private BusinessCodeGenerator businessCodeGenerator;

	@Autowired
	private ScheduleOrderMapper scheduleOrderMapper;

	@Autowired
	private MachineProductLogMapper machineProductLogMapper;

	public List<ScheduleOrderEO> getWorkingProcedureTime(ProductOrderEO productOrder) {
		return this.baseMapper.getWorkingProcedureTime(productOrder.getElementNo(), productOrder.getOrgId(),
				productOrder.getProductOrderId());
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveEntity(ScheduleOrderEO scheduleOrder, UserEO user) throws BusinessException {
		ProductOrderEO productOrder = this.productOrderService.getById(scheduleOrder.getProductOrderId());
		List<ScheduleOrderEO> scheduleOrders = this.baseMapper.getList(scheduleOrder.getProductOrderId(),
				scheduleOrder.getWorkingProcedureTimeId());

		// 工序已排产总数量
		Double sumPlanProduceQuantity = 0d;
		if (scheduleOrders != null && scheduleOrders.size() > 0) {
			for (ScheduleOrderEO so : scheduleOrders) {
				sumPlanProduceQuantity += so.getPlanProduceQuantity();
			}
		}
		sumPlanProduceQuantity += scheduleOrder.getPlanProduceQuantity();
		String errorMsg = "";
		if (productOrder.getPlanProduceQuantity() < sumPlanProduceQuantity) {
			errorMsg += "添加的排产数量不能超过未排产数量!<br/>";
		}
		if (scheduleOrder.getPlanProductDate().getTime() < productOrder.getPlanFinishDate().getTime()) {
			errorMsg += "预计生产日期不能早于生产订单的计划完成日期!<br/>";
		}
		if (!"".equals(errorMsg)) {
			throw new BusinessException(errorMsg);
		}

		String voucherNo = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrder,
				productOrder.getOrgId());
		scheduleOrder.setVoucherNo(voucherNo);
		scheduleOrder.setStatus(0);
		// scheduleOrder.setOperatorId(user.getUserId());
		// scheduleOrder.setOperatorName(user.getUserName());
		scheduleOrder.setActualProduceQuantity(Double.valueOf(0));
		scheduleOrder.setOrgId(productOrder.getOrgId());
		super.save(scheduleOrder);
		this.updateScheduleStatus(productOrder);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateScheduleStatus(ProductOrderEO productOrder) {
		List<ScheduleOrderEO> list = this.baseMapper.getListByProductOrderId(productOrder.getProductOrderId());
		List<ScheduleOrderEO> scheduleOrders = this.baseMapper.getWorkingProcedureTime(productOrder.getElementNo(),
				productOrder.getOrgId(), productOrder.getProductOrderId());

		int scheduleStatus = 2; // 生产订单排产状态

		Set hasSet = new HashSet<>(); // 已排产的工序
		Set allSet = new HashSet<>(); // 所有的工序
		String maxWorkingProcedureCode = "";// 最大工序号
		Double sumPlanProduceQuantitys = 0d;

		if (list == null || list.size() == 0) {
			scheduleStatus = 0;
		} else {
			for (ScheduleOrderEO scheduleOrder : list) {
				hasSet.add(scheduleOrder.getWorkingProcedureTimeId());
			}
		}

		if (scheduleOrders != null && scheduleOrders.size() > 0) {
			maxWorkingProcedureCode = scheduleOrders.get(scheduleOrders.size() - 1).getWorkingProcedureCode();
			for (ScheduleOrderEO scheduleOrder : scheduleOrders) {
				allSet.add(scheduleOrder.getWorkingProcedureTimeId());
				if (scheduleOrder.getWorkingProcedureCode().equals(maxWorkingProcedureCode)) {
					sumPlanProduceQuantitys += scheduleOrder.getSumPlanProduceQuantity();
				}
			}
		}

		if (hasSet != null && hasSet.size() > 0) {
			if (hasSet.containsAll(allSet)) {
				for (ScheduleOrderEO scheduleOrder : list) {
					// if(scheduleOrder.getStatus() != 3) {
					// scheduleStatus = 1;
					// break;
					// } else {
					Double sumPlanProduceQuantity = 0d;
					for (ScheduleOrderEO so : scheduleOrders) {
						if (so.getWorkingProcedureTimeId().longValue() == scheduleOrder.getWorkingProcedureTimeId()
								.longValue()) {
							sumPlanProduceQuantity = so.getSumPlanProduceQuantity();
						}
					}

					if (sumPlanProduceQuantity < productOrder.getPlanProduceQuantity()) {
						scheduleStatus = 1;
						break;
					}
					// }
				}
			} else {
				scheduleStatus = 1;
			}
		}

		productOrder.setHasScheduleQuantity(sumPlanProduceQuantitys);
		productOrder.setScheduleStatus(scheduleStatus);
		this.productOrderService.updateById(productOrder);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteScheduleOrder(Long[] scheduleOrderIds) throws BusinessException {
		String sqlStr = "";

		if (scheduleOrderIds == null || scheduleOrderIds.length == 0) {
			throw new BusinessException("请选择数据!");
		}

		for (Long scheduleOrderId : scheduleOrderIds) {
			sqlStr += (scheduleOrderId + ",");
		}

		if (!"".equals(sqlStr)) {
			ScheduleOrderEO scheduleOrder = this.getById(scheduleOrderIds[0]);
			sqlStr = "(" + sqlStr.substring(0, sqlStr.length() - 1) + ")";

			List<ScheduleOrderEO> scheduleOrders = this.baseMapper.getScheduleOrderByIds(sqlStr);
			if (scheduleOrders != null && scheduleOrders.size() > 0) {
				for (ScheduleOrderEO so : scheduleOrders) {
					if (so.getStatus().intValue() != 0) {
						throw new BusinessException("存在非新建状态的数据!");
					}
				}
			}

			this.baseMapper.deleteScheduleOrder(sqlStr);

			ProductOrderEO productOrder = this.productOrderService.getById(scheduleOrder.getProductOrderId());
			this.updateScheduleStatus(productOrder);
		} else {
			return;
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void saveEntities(ScheduleOrderEO[] scheduleOrders, UserEO user) throws BusinessException {
		if (scheduleOrders == null || scheduleOrders.length == 0) {
			throw new BusinessException("请选择数据!");
		}

		ProductOrderEO productOrder = this.productOrderService.getById(scheduleOrders[0].getProductOrderId());
		String errorMsg = "";
		for (ScheduleOrderEO scheduleOrder : scheduleOrders) {
			List<ScheduleOrderEO> sos = this.baseMapper.getList(scheduleOrder.getProductOrderId(),
					scheduleOrder.getWorkingProcedureTimeId());

			// 工序已排产总数量
			Double sumPlanProduceQuantity = 0d;
			if (sos != null && sos.size() > 0) {
				for (ScheduleOrderEO so : sos) {
					sumPlanProduceQuantity += so.getPlanProduceQuantity();
				}
			}
			sumPlanProduceQuantity += scheduleOrder.getPlanProduceQuantity();
			if (productOrder.getPlanProduceQuantity() < sumPlanProduceQuantity) {
				errorMsg += "工序[" + scheduleOrder.getWorkingProcedureName() + "]添加的排产数量不能超过未排产数量!<br/>";
			}
			if (scheduleOrder.getPlanProductDate().getTime() > productOrder.getPlanFinishDate().getTime()) {
				errorMsg += "工序[" + scheduleOrder.getWorkingProcedureName() + "]排产的生产日期不能早于生产订单的计划日期!<br/>";
			}
		}

		if (!"".equals(errorMsg)) {
			throw new BusinessException(errorMsg);
		}

		for (ScheduleOrderEO scheduleOrder : scheduleOrders) {
			String voucherNo = this.businessCodeGenerator.generateNextCode("srm_schedule_order", scheduleOrder,
					productOrder.getOrgId());
			scheduleOrder.setVoucherNo(voucherNo);
			scheduleOrder.setStatus(0);
			// scheduleOrder.setOperatorId(user.getUserId());
			// scheduleOrder.setOperatorName(user.getUserName());
			scheduleOrder.setActualProduceQuantity(Double.valueOf(0));
			scheduleOrder.setOrgId(productOrder.getOrgId());
			super.save(scheduleOrder);
		}
		this.updateScheduleStatus(productOrder);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateScheduleOrder(ScheduleOrderEO scheduleOrder) throws BusinessException {
		ScheduleOrderEO scheduleOrderFromDb = this.getById(scheduleOrder.getScheduleOrderId());
		if (scheduleOrderFromDb.getStatus().intValue() != 0) {
			throw new BusinessException("非新建状态数据不允许修改!");
		}

		ProductOrderEO productOrder = this.productOrderService.getById(scheduleOrder.getProductOrderId());
		List<ScheduleOrderEO> scheduleOrders = this.baseMapper.getList(scheduleOrder.getProductOrderId(),
				scheduleOrder.getWorkingProcedureTimeId());

		// 工序已排产总数量
		Double sumPlanProduceQuantity = 0d;
		if (scheduleOrders != null && scheduleOrders.size() > 0) {
			for (ScheduleOrderEO so : scheduleOrders) {
				if (so.getScheduleOrderId().longValue() != scheduleOrder.getScheduleOrderId().longValue()) {
					sumPlanProduceQuantity += so.getPlanProduceQuantity();
				}
			}
		}
		sumPlanProduceQuantity += scheduleOrder.getPlanProduceQuantity();
		String errorMsg = "";
		// if(productOrder.getPlanProduceQuantity() < sumPlanProduceQuantity) {
		// errorMsg += "添加的排产数量不能超过未排产数量!<br/>";
		// }
		// if(scheduleOrder.getPlanProductDate().getTime() <
		// productOrder.getPlanFinishDate().getTime()) {
		// errorMsg += "预计生产日期不能早于生产订单的计划完成日期!<br/>";
		// }
		if (!"".equals(errorMsg)) {
			throw new BusinessException(errorMsg);
		}

		super.updateById(scheduleOrder);
		this.updateScheduleStatus(productOrder);
	}

	public void addBatchScheduleOrder(Long[] productOrderIds, UserEO user) throws BusinessException {
		if (productOrderIds == null || productOrderIds.length == 0) {
			throw new BusinessException("请选择数据!");
		}

		String sqlStr = "";
		for (Long productOrderId : productOrderIds) {
			sqlStr += (productOrderId + ",");
		}
		if (!"".equals(sqlStr)) {
			sqlStr = "(" + sqlStr.substring(0, sqlStr.length() - 1) + ")";
		} else {
			sqlStr = "(-1)";
		}

		List<ProductOrderEO> productOrders = this.productOrderService.getByProductOrderIds(sqlStr);
		if (productOrders != null && productOrders.size() > 0) {
			for (ProductOrderEO productOrder : productOrders) {
				if (productOrder.getScheduleStatus() == 2) { // 排产完成直接跳过
					continue;
				}

				List<ScheduleOrderEO> scheduleOrders = this.baseMapper.getWorkingProcedureTime(
						productOrder.getElementNo(), productOrder.getOrgId(), productOrder.getProductOrderId());
				if (scheduleOrders != null && scheduleOrders.size() > 0) {
					for (ScheduleOrderEO scheduleOrder : scheduleOrders) {
						Double notProduceQuantity = productOrder.getPlanProduceQuantity()
								- scheduleOrder.getSumPlanProduceQuantity();
						if (notProduceQuantity > Double.valueOf(0)) {
							ScheduleOrderEO so = new ScheduleOrderEO();

							String voucherNo = this.businessCodeGenerator.generateNextCode("srm_schedule_order",
									scheduleOrder, productOrder.getOrgId());
							so.setVoucherNo(voucherNo);
							so.setOperatorId(user.getUserId());
							so.setStatus(0);
							so.setOperatorName(user.getUserName());
							so.setActualProduceQuantity(Double.valueOf(0));
							so.setOrgId(productOrder.getOrgId());
							so.setProductOrderId(productOrder.getProductOrderId());
							so.setPlanProduceQuantity(notProduceQuantity);
							so.setPlanProductDate(productOrder.getPlanFinishDate());
							so.setWorkingProcedureTimeId(scheduleOrder.getWorkingProcedureTimeId());
							super.save(so);
							this.updateScheduleStatus(productOrder);
						}
					}
				}
			}
		}
	}

	public List<ScheduleOrderEO> getScheduleOrder(Map<String, Object> map) {
		return scheduleOrderMapper.getScheduleOrder(map);
	}

	public List<Map<String, Object>> getWorkShop(Map<String, Object> map) {
		return scheduleOrderMapper.getWorkShop(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public void startProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if(machine.getProductStatus() == null) {
				machine.setProductStatus(0);
			}
			if (machine.getProductStatus() != 0) {
				throw new BusinessException("该设备不是空闲状态不能开始生产!");
			}
			if (machine.getDebugStatus() == 1) {
				throw new BusinessException("该设备是调试状态不能开始生产!");
			}
			
			// 修改设备生产状态生产中
			machine.setProductStatus(1);
			machine.setScheduleOrderId(entity.getScheduleOrderId());
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(0);
			machineProductLog.setStartTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("开始生产");
			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();

		}

		EmployeeEO emp = employeeMapper.selectById(entity.getOperatorId());

		entity.setStartProductLogId(machineProductLogId);
		entity.setOperatorName(emp.getEmployeeName());
		entity.setStartTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStatus(2);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void stopProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getProductStatus() != 1) {
				throw new BusinessException("该设备不是生产状态不能停机!");
			}
			if (machine.getDebugStatus() == 1) {
				throw new BusinessException("该设备是调试状态不能停机!");
			}
			
			// 修改设备生产状态为停机
			StopWarnLogEO stopWarnLogEO = stopWarnLogMapper.selectById(entity.getStopWarnLogId());

			// 查看停机原因 正常停机把设备状态改为正常停机 异常停机吧设备状态改为异常停机
			switch (stopWarnLogEO.getStopType()) {
			case 0:
				machine.setProductStatus(2);
				break;
			case 1:
				machine.setProductStatus(3);
				break;
			default:
				throw new BusinessException("停机状态不在系统范围中");
			}
			machine.setStopWarnLogId(entity.getStopWarnLogId());
			machine.setNeedDealStop(1);// 停机时把设备 需要处理停机改为1
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(1);
			machineProductLog.setStopTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("停机");
			//machineProductLog.setParentProductLogId(current.getStartProductLogId());
			machineProductLog.setStopWarnLogId(entity.getStopWarnLogId());

			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setStopProductLogId(machineProductLogId);
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStopStatus(1);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void dealStopProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);
		Long machineProductLogId = null;
		
		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}
		
		ScheduleOrderEO current = list.get(0);

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getProductStatus() != 2 && machine.getProductStatus() != 3) {
				throw new BusinessException("该设备不是停机状态不能处理停机!");
			}
			if (machine.getDebugStatus() == 1) {
				throw new BusinessException("该设备是调试状态不能处理停机!");
			}
			
			// 修改设备生产状态为停机
			StopWarnLogEO stopWarnLogEO = stopWarnLogMapper.selectById(current.getStopWarnLogId());

			// 查看停机原因 正常停机把设备状态改为正常停机 异常停机吧设备状态改为异常停机
			switch (stopWarnLogEO.getStopType()) {
			case 0:
				machine.setProductStatus(2);
				break;
			case 1:
				machine.setProductStatus(3);
				break;
			default:
				throw new BusinessException("停机状态不在系统范围中");
			}
			machine.setStopWarnLogId(entity.getStopWarnLogId());
			machine.setNeedDealStop(0);// 处理停机的人员到场吧设备需要停机状态改为0 仅仅只是修改设备状态让告警停下来
										// 不影响设备生产状态 排产单状态 不记录日志
			machineMapper.updateById(machine);
			
			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(5);
			machineProductLog.setStopTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("处理停机");
			machineProductLog.setParentProductLogId(current.getStopProductLogId());
			machineProductLog.setStopWarnLogId(entity.getStopWarnLogId());

			MachineProductLogEO parentLog = machineProductLogMapper.selectById(current.getStopProductLogId());
			machineProductLog.setDuration((System.currentTimeMillis() - parentLog.getCreatedTime().getTime()) / 1000);
			
			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();

		}
		
		entity.setDealStopProductLogId(machineProductLogId);
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		//entity.setStopStatus(1);
		scheduleOrderMapper.updateById(entity);
		
	}

	@Transactional(rollbackFor = Exception.class)
	public void callProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getCallStatus() != 0) {
				throw new BusinessException("该设备不是未呼叫状态不能呼叫!");
			}
			// 修改设备呼叫状态为停机
			machine.setCallStatus(1);
			machine.setScheduleOrderId(entity.getScheduleOrderId());
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setCallType(1);
			machineProductLog.setCallTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLogDesc("呼叫");
			machineProductLog.setLastModifiedTime(new Date());
			//machineProductLog.setParentProductLogId(current.getStartProductLogId());
			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setCallProductLogId(machineProductLogId);

		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setCallStatus(1);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void dealCallProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getCallStatus() != 1) {
				throw new BusinessException("该设备不是呼叫状态不能完成呼叫!");
			}
			// 修改设备呼叫状态为未呼叫
			machine.setCallStatus(0);
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setCallType(2);
			machineProductLog.setDealCallTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("处理呼叫");
			machineProductLog.setParentProductLogId(current.getCallProductLogId());

			MachineProductLogEO parentLog = machineProductLogMapper.selectById(current.getCallProductLogId());
			machineProductLog.setDuration((System.currentTimeMillis() - parentLog.getCreatedTime().getTime()) / 1000);

			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setDealCallProductLogId(machineProductLogId);

		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setCallStatus(2);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void returnProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getProductStatus() != 2 && machine.getProductStatus() != 3) {// 2是正常停机
																						// 3是异常停机
				throw new BusinessException("该设备不是停机状态不能恢复生产!");
			}
			if (machine.getDebugStatus() == 1) {
				throw new BusinessException("该设备是调试状态不能恢复生产!");
			}
			
			// 修改设备生产状态为生产中
			machine.setProductStatus(1);
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(2);
			machineProductLog.setReturnProductTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("恢复生产");
			machineProductLog.setParentProductLogId(current.getDealStopProductLogId());
			machineProductLog.setStopWarnLogId(entity.getStopWarnLogId());
			MachineProductLogEO parentLog = machineProductLogMapper.selectById(current.getDealStopProductLogId());
			machineProductLog.setDuration((System.currentTimeMillis() - parentLog.getCreatedTime().getTime()) / 1000);

			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setReturnProductLogId(machineProductLogId);
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStopStatus(2);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void finishProduct(UserEO user, ScheduleOrderEO entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			if (machine.getProductStatus() != 1) {
				throw new BusinessException("该设备不是生产状态不能完成!");
			}
			if (machine.getDebugStatus() == 1) {
				throw new BusinessException("该设备是调试状态不能完成!");
			}
			
			// 修改设备生产状态为空闲
			machine.setProductStatus(0);
			// 完成的时候不管设备呼叫状态如何都修改为初始
			machine.setCallStatus(0);
			machine.setScheduleOrderId(null);
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(3);
			machineProductLog.setFinishTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("完成生产，实际数量变为" + entity.getActualProduceQuantity());
//			machineProductLog.setParentProductLogId(current.getStartProductLogId());

			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setFinishProductLogId(machineProductLogId);
		entity.setFinishTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStatus(3);
		scheduleOrderMapper.updateById(entity);

		ProductOrderEO productOrder = productOrderMapper.selectById(current.getProductOrderId());
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("productOrderId", current.getProductOrderId());
		tmp.put("maxElementOrder", 1);
		List<ScheduleOrderEO> productOrderList = scheduleOrderMapper.getScheduleOrder(tmp);

		ScheduleOrderEO productOrderScheduleMax = productOrderList.get(0);

		double planCount = 0;// 排班计划总数
		double actCount = 0;// 实际计划总数
		boolean finishFlag = true;// 排班全部完成 true 有一个未完成 false

		for (ScheduleOrderEO scheduleOrder : productOrderList) {

			if (scheduleOrder.getPlanProduceQuantity() != null) {
				planCount += scheduleOrder.getPlanProduceQuantity();
			}

			if (scheduleOrder.getActualProduceQuantity() != null) {
				actCount += scheduleOrder.getActualProduceQuantity();
			}

			if (scheduleOrder.getStatus() != 3) {
				finishFlag = false;
			}

		}

		// 如果排产完成
		if (productOrder.getScheduleStatus() == 2) {
			// 检查所有排产单是否已经生产完成
			if (finishFlag) {
				// 生产订单的生产状态标注为已完成
				productOrder.setProduceStatus(2);
			} else {
				// 如果没有全部完成
				if (actCount > 0) {
					// 如果实际生产数量大于0，状态生产中
					productOrder.setProduceStatus(1);
				} else {
					// 如果实际生产数量==0，状态不变
					productOrder.setProduceStatus(0);
				}
			}
		} else {
			// 如果排产未完成
			// 检查所有排产单是否已经生产完成
			if (finishFlag) {
				// 如果实际生产数量大于生产计划总数，状态已完成
				if (productOrderScheduleMax.getActualProduceQuantity() >= productOrder.getPlanProduceQuantity()) { // 最大工序
					// 生产订单的生产状态标注为已完成
					productOrder.setProduceStatus(2);
				} else if (actCount > 0) {
					// 如果实际生产数量大于0，状态生产中
					productOrder.setProduceStatus(1);
				} else {
					// 如果实际生产数量==0，状态不变
					productOrder.setProduceStatus(0);
				}
			}

		}
		productOrder.setHasProduceQuantity(productOrderScheduleMax.getActualProduceQuantity()); // 最大工序
		productOrderMapper.updateById(productOrder);

	}

	public List<ScheduleOrderEO> selectWorkingProcedureTime(ProductOrderEO productOrder) {
		String planFinishDate = productOrder.getPlanFinishDate() == null ? null
				: DateUtils.format(productOrder.getPlanFinishDate(), "yyyy-MM-dd");
		return this.baseMapper.selectWorkingProcedureTime(productOrder.getElementNo(), productOrder.getOrgId(),
				planFinishDate);
	}

	public List<ScheduleOrderEO> selectNewPage(Map map) {
		return this.baseMapper.selectNewPage(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateProduct(UserEO user, ScheduleOrderEO entity) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {
			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setProductType(4);
			machineProductLog.setFinishTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc(
					"修改生产，实际数量从" + current.getActualProduceQuantity() + "变为" + entity.getActualProduceQuantity());
//			machineProductLog.setParentProductLogId(current.getStartProductLogId());
			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setFinishProductLogId(machineProductLogId);
		entity.setFinishTime(new Date());
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setStatus(3);
		scheduleOrderMapper.updateById(entity);

		ProductOrderEO productOrder = productOrderMapper.selectById(current.getProductOrderId());
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("productOrderId", current.getProductOrderId());
		tmp.put("maxElementOrder", 1);
		List<ScheduleOrderEO> productOrderList = scheduleOrderMapper.getScheduleOrder(tmp);
		ScheduleOrderEO productOrderScheduleMax = productOrderList.get(0);

		double planCount = 0;// 排班计划总数
		double actCount = 0;// 实际计划总数
		boolean finishFlag = true;// 排班全部完成 true 有一个未完成 false

		for (ScheduleOrderEO scheduleOrder : productOrderList) {
			if (scheduleOrder.getPlanProduceQuantity() != null) {
				planCount += scheduleOrder.getPlanProduceQuantity();
			}
			if (scheduleOrder.getActualProduceQuantity() != null) {
				actCount += scheduleOrder.getActualProduceQuantity();
			}
			if (scheduleOrder.getStatus() != 3) {
				finishFlag = false;
			}
		}

		// 如果排产完成
		if (productOrder.getScheduleStatus() == 2) {
			// 检查所有排产单是否已经生产完成
			if (finishFlag) {
				// 生产订单的生产状态标注为已完成
				productOrder.setProduceStatus(2);
			} else {
				// 如果没有全部完成
				if (actCount > 0) {
					// 如果实际生产数量大于0，状态生产中
					productOrder.setProduceStatus(1);
				} else {
					// 如果实际生产数量==0，状态不变
					productOrder.setProduceStatus(0);
				}
			}
		} else {
			// 如果排产未完成
			// 检查所有排产单是否已经生产完成
			if (finishFlag) {
				// 如果实际生产数量大于生产计划总数，状态已完成
				if (productOrderScheduleMax.getActualProduceQuantity() >= productOrder.getPlanProduceQuantity()) {
					// 生产订单的生产状态标注为已完成
					productOrder.setProduceStatus(2);
				} else if (actCount > 0) {
					// 如果实际生产数量大于0，状态生产中
					productOrder.setProduceStatus(1);
				} else {
					// 如果实际生产数量==0，状态不变
					productOrder.setProduceStatus(0);
				}
			}

		}
		productOrder.setHasProduceQuantity(productOrderScheduleMax.getActualProduceQuantity());
		productOrderMapper.updateById(productOrder);

	}

	@Transactional(rollbackFor = Exception.class)
	public void debugProduct(UserEO user, ScheduleOrderEO entity) {
		// 1.排班 状态调试
		// 2.设备状态调试
		// 3.日志
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			/*if (machine.getProductStatus() != 0) {
				throw new BusinessException("该设备不是空闲状态不能调试!");
			}*/

			// 调试设备生产状态为调试，设备其他状态不变
			machine.setStopWarnLogId(entity.getStopWarnLogId());
			machine.setDebugStatus(1);
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setDebugType(0);
			machineProductLog.setDebugTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("调试开始");
			machineProductLog.setParentProductLogId(current.getStartProductLogId());

			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		entity.setDebugProductLogId(machineProductLogId);
		entity.setLastModifiedBy(user.getUserName());
		entity.setLastModifiedTime(new Date());
		entity.setDebugStatus(0);
		scheduleOrderMapper.updateById(entity);

	}

	@Transactional(rollbackFor = Exception.class)
	public void stopDebugProduct(UserEO user, ScheduleOrderEO entity) {
		// 1.排班 状态调试
		// 2.设备状态调试
		// 3.日志
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scheduleOrderId", entity.getScheduleOrderId());
		List<ScheduleOrderEO> list = scheduleOrderMapper.getScheduleOrder(map);

		if (list == null || list.size() == 0) {
			throw new BusinessException("该排班未找到!");
		}

		ScheduleOrderEO current = list.get(0);

		Long machineProductLogId = null;

		if (current.getMachineId() != null) {

			MachineEO machine = machineMapper.selectById(current.getMachineId());
			/*if (machine.getProductStatus() != 1) {
				throw new BusinessException("该设备不是调试状态不能停止调试!");
			}*/

			// 调试设备生产状态为不调试，设备其他状态不变
			machine.setStopWarnLogId(entity.getStopWarnLogId());
			machine.setDebugStatus(0);
			machineMapper.updateById(machine);

			MachineProductLogEO machineProductLog = new MachineProductLogEO();
			machineProductLog.setScheduleOrderId(entity.getScheduleOrderId());
			machineProductLog.setMachineId(current.getMachineId());
			machineProductLog.setDebugType(1);
			machineProductLog.setDebugCount(entity.getDebugQuantity());
			machineProductLog.setStopDebugTime(new Date());
			machineProductLog.setCreatedTime(new Date());
			machineProductLog.setCreatedBy(user.getUserName());
			machineProductLog.setLastModifiedBy(user.getUserName());
			machineProductLog.setLastModifiedTime(new Date());
			machineProductLog.setLogDesc("停止调试");
			machineProductLog.setParentProductLogId(current.getDebugProductLogId());
			MachineProductLogEO parentLog = machineProductLogMapper.selectById(current.getDebugProductLogId());
			machineProductLog.setDuration((System.currentTimeMillis() - parentLog.getCreatedTime().getTime()) / 1000);
			
			machineProductLogMapper.insert(machineProductLog);
			machineProductLogId = machineProductLog.getMachineProductLogId();
		}

		current.setDebugQuantity(current.getDebugQuantity()==null ? 0 : current.getDebugQuantity() +entity.getDebugQuantity());
		current.setDebugProductLogId(machineProductLogId);
		current.setLastModifiedBy(user.getUserName());
		current.setLastModifiedTime(new Date());
		current.setDebugStatus(1);
		scheduleOrderMapper.updateById(current);

	}

    public List<ScheduleOrderEO> listAllWorkOrder(Map map) {
	    return this.baseMapper.listAllWorkOrder(map);
    }

    public ScheduleOrderEO getWorkOrderById(Long scheduleOrderId) {
	    return this.baseMapper.getWorkOrderById(scheduleOrderId);
    }

	public IPage<ScheduleOrderEO> selectProduceDoneReport(Criteria criteria) {

		Map<String, Object> param = new HashedMap();

		param.put("currIndex", 0);
		param.put("pageSize", 10000000);

		QueryWrapper<ScheduleOrderEO> wrapper = new QueryWrapper<ScheduleOrderEO>();
		// 循环查询条件，拼接where字符串
		List<Criterion> criterions = criteria.getCriterions();
		for (Criterion criterion : criterions) {
			if (null != criterion.getValue() && !"".equals(criterion.getValue())) {
				param.put(criterion.getField(), criterion.getValue());
			}
		}
		List<ScheduleOrderEO> totalList = this.baseMapper.selectPageByViewMode(param);
		int total = totalList.size();
		int pages =  total/criteria.getSize();
		if(total % criteria.getSize() > 0){
			pages = pages +1;
		}

		param.put("currIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
		param.put("pageSize", criteria.getSize());
		List<ScheduleOrderEO> list = this.baseMapper.selectPageByViewMode(param);

		IPage<ScheduleOrderEO> page = new Page<>();
		page.setRecords(list);
		page.setCurrent(criteria.getCurrentPage());
		page.setPages(pages);
		page.setSize(criteria.getSize());
		page.setTotal(total);
		return page;
	}

    public List<ScheduleOrderEO> getHumanAssessPage(Map map) {
	    return this.baseMapper.getHumanAssessPage(map);
    }

    public void changeOpenStatus(Long scheduleOrderId, Integer openStatus) {
		this.baseMapper.changeOpenStatus(scheduleOrderId, openStatus);
    }
}
