package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.CustomerEO;
import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import com.xchinfo.erp.bsc.service.CustomerService;
import com.xchinfo.erp.bsc.service.PartnerAddressService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.utils.CommonUtil;
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
import org.yecat.mybatis.utils.Criterion;

import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/customer")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;


    @Autowired
    private PartnerAddressService partnerAddressService;

    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:customer:info")
    public Result<IPage<CustomerEO>> page(@RequestBody Criteria criteria){
        logger.info("======== CustomerController.page() ========");
        //需根据用户查询机构数据
//        Criterion criterion = new Criterion();
//        criterion.setField("x.user_id");
//        criterion.setOp("eq");
//        criterion.setValue(getUserId()+"");
//        criteria.getCriterions().add(criterion);
        IPage<CustomerEO> page = this.customerService.selectPage(criteria);

        return new Result<IPage<CustomerEO>>().ok(page);
    }


    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("pageNew")
    @RequiresPermissions("basic:customer:info")
    public Result<IPage<CustomerEO>> pageNew(@RequestBody Criteria criteria){
        logger.info("======== CustomerController.pageNew() ========");
        //需根据用户查询机构数据
//        Criterion criterion = new Criterion();
//        criterion.setField("userId");
//        criterion.setOp("eq");
//        criterion.setValue(getUserId()+"");
//        criteria.getCriterions().add(criterion);

        Map map = CommonUtil.criteriaToMap(criteria);
        List<CustomerEO> totalList = this.customerService.selectPageNew(map);
        map.put("currentIndexFlag", true);
        List<CustomerEO> pageList = this.customerService.selectPageNew(map);
        IPage<CustomerEO> page = CommonUtil.listToPage(totalList, pageList, map);

        return new Result<IPage<CustomerEO>>().ok(page);
    }

    /**
     * 查询所有客户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<CustomerEO>> list(){
        logger.info("======== CustomerController.list() ========");

        List<CustomerEO> customers = this.customerService.listAll();

        return new Result<List<CustomerEO>>().ok(customers);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @RequiresPermissions("basic:customer:info")
    public Result<CustomerEO> info(@PathVariable("id") Long id){
        logger.info("======== CustomerController.info(entity => "+id+") ========");

        CustomerEO entity = this.customerService.getById(id);

        return new Result<CustomerEO>().ok(entity);
    }
    /**
     * 查找有审批权限的用户
     *
     * @param
     * @return
     */
    @GetMapping("getApprovers")
    @RequiresPermissions("basic:customer:info")
    public Result<List<Map>> getApprovers(){
        logger.info("======== CustomerController.getApprovers() ========");

        List<Map> list = this.customerService.getApprovers();

        return new Result<List<Map>>().ok(list);
    }
    /**
     * 提交
     *
     * @param
     * @return
     */
    @PostMapping("submit")
    @RequiresPermissions("basic:customer:submit")
    public Result submit(@RequestBody Map map){
        logger.info("======== CustomerController.submit(map) ========");

        boolean res = this.customerService.submit(map);

        return new Result().ok(res);
    }
    /**
     * 审批并提交
     *
     * @param
     * @return
     */
    @PostMapping("approveSubmit")
    @RequiresPermissions("basic:customer:approve")
    public Result approveSubmit(@RequestBody Map map){
        logger.info("======== CustomerController.approveSubmit(map) ========");

        boolean res = this.customerService.approveSubmit(map);

        return new Result().ok(res);
    }
    /**
     * 地址提交
     *
     * @param
     * @return
     */
    @PostMapping("address/submit")
    @RequiresPermissions("basic:customer:submit")
    public Result addressSubmit(@RequestBody Map map){
        logger.info("======== CustomerController.addressSubmit(map) ========");

        boolean res = this.customerService.addressSubmit(map);

        return new Result().ok(res);
    }
    /**
     * 审批完成
     *
     * @param
     * @return
     */
    @PostMapping("approve")
    @RequiresPermissions("basic:customer:approve")
    public Result approve(@RequestBody Map map){
        logger.info("======== CustomerController.approve(map) ========");

        boolean res = this.customerService.approve(map);

        return new Result().ok(res);
    }
    /**
     * 创建
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("创建客户")
    @RequiresPermissions("basic:customer:create")
    public Result create(@RequestBody CustomerEO entity){
        logger.info("======== CustomerController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);

        this.customerService.save(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("更新客户")
    @RequiresPermissions("basic:customer:update")
    public Result update(@RequestBody CustomerEO entity){
        logger.info("======== CustomerController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.customerService.updateById(entity);

        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除客户")
    @RequiresPermissions("basic:customer:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== CustomerController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");

        this.customerService.removeByIds(ids);

        return new Result();
    }


    /**
     * 设置状态
     *
     * @param
     * @return
     */
    @PostMapping("updateStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:customer:updateStatus")
    public Result updateStatus(@RequestParam("id") Long id,@RequestParam("status") Integer status){
        logger.info("======== CustomerController.update(id => "+id+") ========");

        logger.info("================"+status);

        this.customerService.updateStatusById(id,status);

        return new Result();
    }

    /**
     * 设置系统账号状态
     *
     * @param
     * @return
     */
    @PostMapping("updateAccountStatus")
    @OperationLog("设置账号状态")
    @RequiresPermissions("basic:customer:updateAccount")
    public Result updateAccountStatus(@RequestParam("id") Long id,@RequestParam("status") Integer status){
        logger.info("======== CustomerController.updateAccountStatus(id => "+id+") ========");

        this.customerService.updateAccountStatusById(id,status);

        return new Result();
    }

    /**
     * 更新联系人和地址
     *
     * @param
     * @return
     */
    @PostMapping("partnerId")
    @OperationLog("更新客户")
    public Result updateBypartnerId(@RequestParam("Id") Long Id,@RequestParam("type") String type){
        logger.info("======== CustomerController.updateBypartnerId(ID => "+Id+") ========");


        this.customerService.updateByPartnerId(Id,type);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PostMapping("contact")
    @OperationLog("更新客户联系人")
    public Result updateContcatInfo(@RequestBody PartnerContactEO entity){
        logger.info("======== CustomerController.updateContcatInfo(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.customerService.updateContactById(entity);

        return new Result();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PostMapping("address")
    @OperationLog("更新客户地址")
    public Result updateAddressInfo(@RequestBody PartnerAddressEO entity){
        logger.info("======== CustomerController.updateAddressInfo(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.customerService.updateAddressById(entity);

        return new Result();
    }

    /**
     * 根据ID查找地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("address/{id}")
    public Result<PartnerAddressEO> addressInfo(@PathVariable("id") Long id){
        logger.info("======== CustomerController.addressInfo(entity => "+id+") ========");

        PartnerAddressEO entity = this.customerService.getAddressInfoById(id);

        return new Result<PartnerAddressEO>().ok(entity);
    }

    /**
     * 根据ID查找联系人信息
     *
     * @param id
     * @return
     */
    @GetMapping("contact/{id}")
    public Result<PartnerContactEO> contactInfo(@PathVariable("id") Long id){
        logger.info("======== CustomerController.contactInfo(entity => "+id+") ========");

        PartnerContactEO entity = this.customerService.getContactInfoById(id);

        return new Result<PartnerContactEO>().ok(entity);
    }


    /**
     * 新增联系人
     *
     * @param entity
     * @return
     */
    @PostMapping("addContact")
    @OperationLog("新增联系人")
    @RequiresPermissions("basic:customer:addContact")
    public Result addContact(@RequestBody PartnerContactEO entity){
        logger.info("======== CustomerController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.customerService.addContact(entity);

        return new Result();
    }

    /**
     * 新增地址
     *
     * @param entity
     * @return
     */
    @PostMapping("addAddress")
    @OperationLog("新增地址")
    @RequiresPermissions("basic:customer:addAddress")
    public Result addAddress(@RequestBody PartnerAddressEO entity){
        logger.info("======== CustomerController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);

        this.customerService.addAddress(entity);

        return new Result();
    }


    /**
     * 查询用户的客户待审核查数据
     *
     * @param criteria
     * @return
     */
    @PostMapping("appPage")
//    @RequiresPermissions("basic:customer:info")
    public Result<IPage<CustomerEO>> appPage(@RequestBody Criteria criteria){
        logger.info("======== CustomerController.appPage() ========");
        //查询用户待审核数据
        Criterion criterion = new Criterion();
        criterion.setField("c.final_approver_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<CustomerEO> page = this.customerService.selectPage(criteria);

        return new Result<IPage<CustomerEO>>().ok(page);
    }


    /**
     * 查询用户的客户待审核查数据
     *
     * @param criteria
     * @return
     */
    @PostMapping("appAddressPage")
//    @RequiresPermissions("basic:customer:info")
    public Result<IPage<PartnerAddressEO>> appAddressPage(@RequestBody Criteria criteria){
        logger.info("======== CustomerController.appAddressPage() ========");
        //查询用户待审核数据
        Criterion criterion = new Criterion();
        criterion.setField("c.final_approver_id");
        criterion.setOp("eq");
        criterion.setValue(getUserId()+"");
        criteria.getCriterions().add(criterion);
        IPage<PartnerAddressEO> page = this.partnerAddressService.selectPage(criteria);

        return new Result<IPage<PartnerAddressEO>>().ok(page);
    }



    /**
     * 地址审批并提交
     *
     * @param
     * @return
     */
    @PostMapping("app/approveSubmit")
//    @RequiresPermissions("basic:customer:approve")
    public Result approveAddressSubmit(@RequestBody Map map){
        logger.info("======== CustomerController.approveAddressSubmit(map) ========");

        boolean res = this.customerService.approveAddressSubmit(map);

        return new Result().ok(res);
    }


    /**
     * 审批完成
     *
     * @param
     * @return
     */
    @PostMapping("app/approve")
//    @RequiresPermissions("basic:customer:approve")
    public Result approveAddress(@RequestBody Map map){
        logger.info("======== CustomerController.approveAddress(map) ========");

        boolean res = this.customerService.approveAddress(map);

        return new Result().ok(res);
    }

}
