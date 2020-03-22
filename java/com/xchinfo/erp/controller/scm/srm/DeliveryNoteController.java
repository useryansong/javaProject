package com.xchinfo.erp.controller.scm.srm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteDetailEO;
import com.xchinfo.erp.scm.srm.entity.DeliveryNoteEO;
import com.xchinfo.erp.srm.service.DeliveryNoteService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.entity.ParamsEO;
import com.xchinfo.erp.sys.conf.service.ParamsService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.Constant;
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
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/5/14
 */
@RestController
@RequestMapping("/srm/deliveryNote")
public class DeliveryNoteController extends BaseController {

    @Autowired
    private DeliveryNoteService deliveryNoteService;

    @Autowired
    private ParamsService paramsService;


    /**
     * 分页查找(送货单)
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("srm:deliveryNote:info")
    public Result<IPage<DeliveryNoteEO>> page(@RequestBody Criteria criteria){
        logger.info("======== DeliveryNoteController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        map.put("currentIndexFlag", true);
        List<DeliveryNoteEO> pageList = this.deliveryNoteService.getPage(map);
        IPage<DeliveryNoteEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<DeliveryNoteEO>>().ok(page);
    }

    /**
     * 创建送货单
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建送货单")
    @RequiresPermissions("srm:deliveryNote:create")
    public Result create(@RequestBody DeliveryNoteEO entity){
        logger.info("======== DeliveryNoteController.create(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        UserEO user = super.getUser();
        DeliveryNoteEO entityFromDb = this.deliveryNoteService.saveEntity(entity, user);
        return new Result<DeliveryNoteEO>().ok(entityFromDb);
    }

    /**
     * 更新
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新送货单")
    @RequiresPermissions("srm:deliveryNote:update")
    public Result update(@RequestBody DeliveryNoteEO entity){
        logger.info("======== DeliveryNoteController.update(ID => "+entity.getId()+") ========");
        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.deliveryNoteService.updateById(entity);
        return new Result();
    }

    /**
     * 根据ID查找
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("srm:deliveryNote:info")
    public Result<DeliveryNoteEO> info(@PathVariable("id") Long id){
        logger.info("======== DeliveryNoteController.info(entity => "+id+") ========");
        DeliveryNoteEO entity = this.deliveryNoteService.selectDetailById(id);
        return new Result<DeliveryNoteEO>().ok(entity);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除送货单")
    @RequiresPermissions("srm:deliveryNote:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== DeliveryNoteController.delete() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteService.removeByIds(ids);
        return new Result();
    }

    /**
     * 发布
     * @param ids
     * @return
     */
    @PostMapping("release")
    @OperationLog("发布送货单")
    @RequiresPermissions("srm:deliveryNote:update")
    public Result release(@RequestBody Long[] ids){
        logger.info("======== DeliveryNoteController.release() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteService.releaseByIds(ids,getUser());
        return new Result();
    }

    /**
     * 取消发布
     * @param ids
     * @return
     */
    @PostMapping("cancelRelease")
    @OperationLog("取消发布送货单")
    @RequiresPermissions("srm:deliveryNote:update")
    public Result cancelRelease(@RequestBody Long[] ids){
        logger.info("======== DeliveryNoteController.cancelRelease() ========");
        AssertUtils.isArrayEmpty(ids, "ids");
        this.deliveryNoteService.cancelReleaseByIds(ids,getUser());
        return new Result();
    }


    /***
     * 通过流水获取送货单明细
     * @param voucherNo
     * @return
     */
    @GetMapping("app/{voucherNo}")
    //@RequiresPermissions("srm:deliveryNote:info")
    public Result<DeliveryNoteEO> getDetailInfoByNo(@PathVariable("voucherNo") String voucherNo){
        logger.info("======== DeliveryNoteController.getDetailInfoByNo(entity => "+voucherNo+") ========");
        DeliveryNoteEO entity = this.deliveryNoteService.getDetailInfoByNo(voucherNo);
        return new Result<DeliveryNoteEO>().ok(entity);
    }



    /**
     * 打印修改状态
     * @param deliveryNote
     * @return
     */
    @PostMapping("updateStatusByPrint")
    @OperationLog("打印修改状态")
    @RequiresPermissions("srm:deliveryNote:update")
    public Result updateStatusByPrint(@RequestBody DeliveryNoteEO deliveryNote){
        logger.info("======== DeliveryNoteController.updateStatusByPrint() ========");
        this.deliveryNoteService.updateStatusByPrint(deliveryNote);
        return new Result();
    }



    /**
     * 单个收货
     * @param
     * @return
     */
    @PostMapping("app/receive")
    @OperationLog("收货")
    //@RequiresPermissions("srm:deliveryNote:update")
    public Result receiveOne(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action){
        logger.info("======== DeliveryNoteController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryNoteService.receiveOne(Id,amount,userId,userName,action);

        return new Result();
    }

    /**
     * 查找所有符合条件的送货单(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("list")
    @RequiresPermissions("srm:deliveryNote:info")
    public Result<List<DeliveryNoteEO>> list(@RequestBody Criteria criteria) {
        logger.info("======== DeliveryNoteController.list() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);
        return new Result<List<DeliveryNoteEO>>().ok(totalList);
    }


    @PostMapping("app/page")
    public Result<IPage<DeliveryNoteEO>> pageNew(@RequestBody Criteria criteria){
        logger.info("======== DeliveryNoteController.page() ========");
        Map map = CommonUtil.criteriaToMap(criteria);
        map.put("orgId", getOrgId());
        List<DeliveryNoteEO> totalList = this.deliveryNoteService.getPage(map);

        map.put("currentIndexFlag", true);
        List<DeliveryNoteEO> pageList = this.deliveryNoteService.getPage(map);
        //获取分页数据
        IPage<DeliveryNoteEO> page = CommonUtil.listToPage(totalList, pageList, map);
        return new Result<IPage<DeliveryNoteEO>>().ok(page);
    }

    /***
     *获取调入库位列表
     * @return id
     */
    @GetMapping("warehouseLocation/list")
    public Result<List<WarehouseLocationEO>> getWarehouseLocation(@RequestParam("id") Long id){
        logger.info("======== DeliveryNoteController.listWarehouseLocation() ========");

        List<WarehouseLocationEO> warehouses = this.deliveryNoteService.getWarehouseLocation(id);

        return new Result<List<WarehouseLocationEO>>().ok(warehouses);
    }

    /**
     * 单个收货按库位
     * @param
     * @return
     */
    @PostMapping("appLocation/receive")
    @OperationLog("收货")
    //@RequiresPermissions("srm:deliveryNote:update")
    public Result receiveOnelocation(@RequestParam("id") Long Id,@RequestParam("amount") Double amount,@RequestParam("action") String action,@RequestParam("locationId") Long locationId){
        logger.info("======== DeliveryNoteController.receiveOne() ========");
        String userName = getUserName();
        Long userId = getUserId();
        this.deliveryNoteService.receiveOnelocation(Id,amount,userId,userName,action,locationId);

        return new Result();
    }

    /**
     * 查询用户授权
     *
     * @return
     */
    @GetMapping("getParams")
    public Result<ParamsEO> getParamsEO(){
        logger.info("======== DeliveryNoteController.getParamsEO() ========");

        ParamsEO paramsEO = this.paramsService.getById(Constant.LOCATIONOPEN);

        return new Result<ParamsEO>().ok(paramsEO);
    }
}
