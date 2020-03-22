package com.xchinfo.erp.controller.bsc.scm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.PartnerAddressEO;
import com.xchinfo.erp.bsc.entity.PartnerContactEO;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.utils.ExcelUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/8
 * @update
 */
@RestController
@RequestMapping("/basic/supplier")
public class SupplierController extends BaseController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;



    /**
     * 分页查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("basic:supplier:info")
    public Result<IPage<SupplierEO>> page(@RequestBody Criteria criteria){
        logger.info("======== SupplierController.page() ========");

        IPage<SupplierEO> page = this.supplierService.selectPage(criteria);
        return new Result<IPage<SupplierEO>>().ok(page);
    }

    /**
     * 根据ID查找
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<SupplierEO> info(@PathVariable("id") Long id){
        logger.info("======== SupplierController.info(entity => "+id+") ========");

        SupplierEO entity = this.supplierService.getById(id);
        return new Result<SupplierEO>().ok(entity);
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping
    @OperationLog("新增供应商")
    @RequiresPermissions("basic:supplier:create")
    public Result create(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.create(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, AddGroup.class, DefaultGroup.class);
        this.supplierService.save(entity);
        return new Result();
    }

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    @PutMapping
    @OperationLog("修改供应商")
    @RequiresPermissions("basic:supplier:update")
    public Result update(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.update(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.updateById(entity);
        return new Result();
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除供应商")
    @RequiresPermissions("basic:supplier:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("======== SupplierController.delete() ========");

        AssertUtils.isArrayEmpty(ids, "id");
        this.supplierService.removeByIds(ids);
        return new Result();
    }

    /**
     * 查询所有供应商
     * @return
     */
    @GetMapping("list")
    public Result<List<SupplierEO>> list(){
        logger.info("======== SupplierController.list() ========");

        List<SupplierEO> suppliers = this.supplierService.listAll();
        return new Result<List<SupplierEO>>().ok(suppliers);
    }

    /**
     * 设置状态
     *
     * @param entity
     * @return
     */
    @PostMapping("setStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("basic:supplier:setStatus")
    public Result setStatus(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.setStatus ========");

        this.supplierService.updateStatusById(entity.getSupplierId(), entity.getStatus());
        return new Result();
    }

    /**
     * 设置系统账号
     *
     * @param entity
     * @return
     */
    @PostMapping("setAccount")
    @OperationLog("设置系统账号")
    @RequiresPermissions("basic:supplier:setAccount")
    public Result setAccount(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.setAccount ========");
        this.supplierService.setAccount(entity);
        return new Result();
    }

    /**
     * 新增联系人
     *
     * @param entity
     * @return
     */
    @PostMapping("addPartnerContact")
    @OperationLog("新增联系人")
    @RequiresPermissions("basic:supplier:addPartnerContact")
    public Result addPartnerContact(@RequestBody PartnerContactEO entity){
        logger.info("======== SupplierController.addPartnerContact(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.addPartnerContact(entity);
        return new Result();
    }

    /**
     * 新增地址
     *
     * @param entity
     * @return
     */
    @PostMapping("addPartnerAddress")
    @OperationLog("新增地址")
    @RequiresPermissions("basic:supplier:addPartnerAddress")
    public Result addPartnerAddress(@RequestBody PartnerAddressEO entity){
        logger.info("======== SupplierController.addPartnerAddress(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.addPartnerAddress(entity);
        return new Result();
    }

    /**
     * 刪除联系人或地址
     *
     * @param id
     * @param type
     * @return
     */
    @PostMapping("partnerId")
    @OperationLog("刪除联系人或地址")
    public Result removeByPartnerId(@RequestParam("id") Long id,@RequestParam("type") String type){
        logger.info("======== SupplierController.removeBypartnerId ========");

        this.supplierService.removeByPartnerId(id, type);
        return new Result();
    }

    /**
     * 根据ID查找联系人信息
     *
     * @param id
     * @return
     */
    @GetMapping("contact/{id}")
    public Result<PartnerContactEO> contactInfo(@PathVariable("id") Long id){
        logger.info("======== SupplierController.contactInfo(entity => "+id+") ========");

        PartnerContactEO entity = this.supplierService.getContactById(id);
        return new Result<PartnerContactEO>().ok(entity);
    }

    /**
     * 根据ID查找地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("address/{id}")
    public Result<PartnerAddressEO> addressInfo(@PathVariable("id") Long id){
        logger.info("======== SupplierController.addressInfo(entity => "+id+") ========");

        PartnerAddressEO entity = this.supplierService.getAddressById(id);
        return new Result<PartnerAddressEO>().ok(entity);
    }

    /**
     * 更新供应商联系人
     *
     * @param entity
     * @return
     */
    @PostMapping("contact")
    @OperationLog("更新供应商联系人")
    public Result updateContactInfo(@RequestBody PartnerContactEO entity){
        logger.info("======== SupplierController.updateContcatInfo(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.updateContactById(entity);
        return new Result();
    }

    /**
     * 更新供应商地址
     *
     * @param entity
     * @return
     */
    @PostMapping("address")
    @OperationLog("更新供应商地址")
    public Result updateAddressInfo(@RequestBody PartnerAddressEO entity){
        logger.info("======== SupplierController.updateAddressInfo(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.updateAddressById(entity);
        return new Result();
    }

    /**
     * 设置供应商联系人状态
     *
     * @param entity
     * @return
     */
    @PostMapping("contact/setStatus")
    @OperationLog("更新供应商联系人")
    public Result setContactStatus(@RequestBody PartnerContactEO entity){
        logger.info("======== SupplierController.setContactStatus(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.setContactStatus(entity);
        return new Result();
    }

    /**
     * 设置供应商地址状态
     *
     * @param entity
     * @return
     */
    @PostMapping("address/setStatus")
    @OperationLog("更新供应商地址")
    public Result setAddressStatus(@RequestBody PartnerAddressEO entity){
        logger.info("======== SupplierController.setAddressStatus(ID => "+entity.getId()+") ========");

        ValidatorUtils.validateEntity(entity, UpdateGroup.class, DefaultGroup.class);
        this.supplierService.setAddressStatus(entity);
        return new Result();
    }

    /**
     * 导入数据
     * @param request
     * @return
     */
    @PostMapping("import")
    public Result importFromExcel(HttpServletRequest request){
        logger.info("======== SupplierController.import ========");
        List list = ExcelUtils.getExcelData(request);
        UserEO user = super.getUser();
        this.supplierService.importFromExcel(list, user);
        return new Result();
    }


    /**
     * 新增/修改供应商(外部系统调用)
     * */
    @PostMapping("addOrUpdateByExternal")
    @RequiresPermissions("basic:supplier:create")
    public Result addOrUpdateByExternal(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.addOrUpdateByExternal ========");
        ValidatorUtils.validateEntity(entity, AddGroup.class, UpdateGroup.class);
        return this.supplierService.addOrUpdateByExternal(entity);
    }

    /**
     * 设置系统账号(外部系统调用)
     */
    @PostMapping("setAccountByExternal")
    @OperationLog("设置系统账号")
    @RequiresPermissions("basic:supplier:setAccount")
    public Result setAccountByExternal(@RequestBody SupplierEO entity){
        logger.info("======== SupplierController.setAccountByExternal ========");
        return this.supplierService.setAccountByExternal(entity);
    }
}
