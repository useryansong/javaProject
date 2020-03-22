package com.xchinfo.erp.controller.scm.srm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.srm.service.ScheduleOrderService;
import com.xchinfo.erp.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/12/17
 */
@RestController
@RequestMapping("/srm/scheduleOrder")
public class ScheduleOrderController extends BaseController {

    @Autowired
    private ScheduleOrderService scheduleOrderService;

    /**
     *
     * @param criteria
     * @return
     */
    @PostMapping("listAllWorkOrder")
    public Result<List<ScheduleOrderEO>> listAllWorkOrder(@RequestBody Criteria criteria) {
        logger.info("======== ScheduleOrderController.listAllWorkOrder ========");

        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUser().getUserId());

        List<ScheduleOrderEO> list = this.scheduleOrderService.listAllWorkOrder(map);
        return new Result<List<ScheduleOrderEO>>().ok(list);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("getWorkOrderById/{id}")
    public Result<ScheduleOrderEO> getWorkOrderById(@PathVariable("id") Long id) {
        logger.info("======== ScheduleOrderController.getWorkOrderById(entity => " + id + ") ========");
        ScheduleOrderEO entity = this.scheduleOrderService.getWorkOrderById(id);
        return new Result<ScheduleOrderEO>().ok(entity);
    }

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("getHumanAssessPage")
    public Result<IPage<ScheduleOrderEO>> getHumanAssessPage(@RequestBody Criteria criteria) {
        logger.info("======== ScheduleOrderController.getHumanAssessPage() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("userId", getUserId()+"");
        List<ScheduleOrderEO> list = this.scheduleOrderService.getHumanAssessPage(map);
        IPage<ScheduleOrderEO> page = CommonUtil.getPageInfo(list, criteria.getSize(), criteria.getCurrentPage());
        return new Result<IPage<ScheduleOrderEO>>().ok(page);
    }

    /**
     * 打开/关闭
     *
     * @return
     */
    @PostMapping("changeOpenStatus")
    public Result changeOpenStatus(@RequestParam("scheduleOrderId") Long scheduleOrderId, @RequestParam("openStatus") Integer openStatus) {
        logger.info("======== ScheduleOrderController.changeOpenStatus() ========");
        this.scheduleOrderService.changeOpenStatus(scheduleOrderId, openStatus);
        return new Result();
    }
}
