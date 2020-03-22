package com.xchinfo.erp.controller.sys.auth;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.form.sys.PasswordForm;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import com.xchinfo.erp.scm.srm.entity.ReturnOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.auth.service.RoleService;
import com.xchinfo.erp.sys.auth.service.UserService;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.CommonUtil;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.catalina.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 用户服务
 *
 * @author Yansong Shi
 * @date 2017/10/10
 * @update
 */
@RestController
@RequestMapping("/sys/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private RoleService roleService;



    /**
     * 查找
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:user:info")
    public Result<IPage<UserEO>> list(@RequestBody Criteria criteria){
        logger.info("======== UserController.list() ========");

        IPage<UserEO> page = this.userService.selectPage(criteria);
        List<UserEO> list = page.getRecords();
        if(list!=null && list.size()>0) {
            for(UserEO user : list) {
                // 设置完整的归属机构名称
                String completeOrgName = this.orgService.getCompleteOrgName(user.getLastOrgId()!=null?user.getLastOrgId():user.getOrgId());
                user.setOrgName(completeOrgName);
                // 设置用户的角色名称
                List<Long> roleIds = this.userService.selectUserRoleIds(user.getUserId());
                user.setRoleIds(roleIds);
                String roleNames = this.roleService.getRoleNames(roleIds);
                user.setRoleNames(roleNames);
            }
        }

        return new Result<IPage<UserEO>>().ok(page);
    }

    /**
     * 查询用户信息
     *
     * @return
     */
    @GetMapping("info")
    public Result<UserEO> info(){
        logger.info("======== UserController.info("+getUser().getUserName()+") ========");
        UserEO user = this.userService.selectByIdWithIdentity(getUserId());
        return new Result<UserEO>().ok(user);
    }

    /**
     * 根据ID查找用户
     *
     * @param userId
     * @return
     */
    @GetMapping("{userId}")
    @RequiresPermissions("sys:user:info")
    public Result<UserEO> info(@PathVariable("userId") Long userId){
        logger.info("======== UserController.info(user => "+userId+") ========");

        UserEO user = this.userService.selectByIdWithIdentity(userId);

        return new Result<UserEO>().ok(user);
    }

    /**
     * 创建
     *
     * @param user
     * @return
     */
    @PostMapping
    @OperationLog("创建用户")
    @RequiresPermissions("sys:user:create")
    public Result create(@RequestBody UserEO user){
        logger.info("======== UserController.create(userId => "+user.getUserId()+") ========");

        ValidatorUtils.validateEntity(user, AddGroup.class, DefaultGroup.class);

        this.userService.save(user);

        return new Result();
    }

    /**
     * 更新
     *
     * @param user
     * @return
     */
    @PutMapping
    @OperationLog("更新用户")
//    @RequiresPermissions("sys:user:update")
    public Result update(@RequestBody UserEO user){
        logger.info("======== UserController.update(userId => "+user.getUserId()+") ========");

        ValidatorUtils.validateEntity(user, UpdateGroup.class, DefaultGroup.class);

        this.userService.updateById(user);

        return new Result();
    }

    /**
     * 删除
     *
     * @param userIds
     * @return
     */
    @DeleteMapping
    @OperationLog("删除用户")
//    @RequiresPermissions("sys:user:delete")
    public Result delete(@RequestBody Long[] userIds){
        logger.info("======== UserController.delete() ========");

        AssertUtils.isArrayEmpty(userIds, "id");

        this.userService.removeByIds(userIds);

        return new Result();
    }

    /**
     * 启用用户
     *
     * @param userId
     * @return
     */
    @PostMapping("enable")
    @OperationLog("启用用户")
//    @RequiresPermissions("sys:user:enable")
    public Result enableUser(@RequestBody Long userId){
        logger.info("======== UserController.enableUser() ========");

        AssertUtils.isNull(userId, "id");

        this.userService.updateUserStatus(userId, 1);

        return new Result();
    }

    /**
     * 禁用用户
     *
     * @param userId
     * @return
     */
    @PostMapping("disable")
    @OperationLog("禁用用户")
//    @RequiresPermissions("sys:user:disable")
    public Result disableUser(@RequestBody Long userId){
        logger.info("======== UserController.disableUser() ========");

        AssertUtils.isNull(userId, "id");

        this.userService.updateUserStatus(userId, 0);

        return new Result();
    }

    /**
     * 挂起用户
     *
     * @param userId
     * @return
     */
    @PostMapping("hangup")
    @OperationLog("挂起用户")
//    @RequiresPermissions("sys:user:hangup")
    public Result hangupUser(@RequestBody Long userId){
        logger.info("======== UserController.hangupUser() ========");

        AssertUtils.isNull(userId, "id");

        this.userService.updateUserStatus(userId, 2);

        return new Result();
    }

    @PostMapping("resetPw")
    @OperationLog("重置密码")
    @RequiresPermissions("sys:user:resetPw")
    public Result resetPassword(@RequestBody UserEO user){
        logger.info("======== UserController.resetPassword(user => "+user.getUserName()+") ========");

        this.userService.updateUserPassword(user);

        return new Result();
    }

    @PostMapping("updatePswd")
    @OperationLog("修改密码")
    public Result password(@RequestBody PasswordForm password){
        logger.info("======== UserController.password(user => "+getUserName()+") ========");

        UserEO user = getUser();

        // 校验数据
        ValidatorUtils.validateEntity(password);

        // 校验密码
        if (!user.getPassword().equals(new Sha256Hash(password.getPassword(), user.getSalt()).toHex())) {
            return new Result().error("原密码不正确，请重新输入!");
        }

        user.setPasswordPlaintext("");
        user.setPassword(password.getNewPassword());

        // 更新用户密码
        this.userService.updateUserPassword(user);
        Result result = new Result();
        result.setMsg("修改成功!");
        return result;
    }

    /**
     * 查询所有用户
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<UserEO>> list(){
        logger.info("======== UserController.list() ========");

        List<UserEO> businessagent = this.userService.listAll();

        return new Result<List<UserEO>>().ok(businessagent);
    }


    /**
     * 创建
     *
     * @param employee
     * @return
     */
    @PostMapping("addFromEmployee")
    @OperationLog("创建用户")
    @RequiresPermissions("sys:user:create")
    public Result addFromEmployee(@RequestBody EmployeeEO employee){
        logger.info("======== UserController.addFromEmployee(userId => "+employee.getEmployeeId()+") ========");
        UserEO user = new UserEO();
        user.setUserName(employee.getEmployeeNo());
        user.setRealName(employee.getEmployeeName());
        user.setOrgId(employee.getRootOrgId());
        user.setLastOrgId(employee.getOrgId());
        user.setGender(employee.getGender());
        user.setUserType(1);
        user.setStatus(1);
        user.setSuperAdmin(0);
        String password = CommonUtil.generateRandomString(6, null);
        user.setPasswordPlaintext(password);
        user.setPassword(password);
        user.setCheckPassword(password);
        user.setRoleIds(new ArrayList<>());
        this.userService.save(user);
        return new Result();
    }

    @PostMapping("resetPassword")
    @OperationLog("重置密码")
    @RequiresPermissions("sys:user:resetPw")
    public Result resetPw(@RequestBody UserEO user){
        logger.info("======== UserController.resetPassword(user => "+user.getUserName()+") ========");
        String password = CommonUtil.generateRandomString(6, null);
        user.setPassword(password);
        user.setPasswordPlaintext(password);
        this.userService.resetPassword(user);

        return new Result();
    }

    @PostMapping("setStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("sys:user:setStatus")
    public Result setStatus(@RequestBody UserEO user){
        logger.info("======== UserController.setStatus(user => "+user.getUserName()+") ========");
        this.userService.setStatus(user);
        return new Result();
    }

    @PostMapping("authorize")
    @OperationLog("角色授权")
    @RequiresPermissions("sys:user:authorize")
    public Result authorize(@RequestBody UserEO user){
        logger.info("======== UserController.authorize(user => "+user.getUserName()+") ========");
        this.userService.authorize(user);
        return new Result();
    }

    @PostMapping("authData")
    @OperationLog("数据授权")
    @RequiresPermissions("sys:user:authData")
    public Result authData(@RequestBody UserEO user){
        logger.info("======== UserController.authData(user => "+user.getUserName()+") ========");
        this.userService.authData(user);
        return new Result();
    }

    @PostMapping("setInnerOrg")
    @OperationLog("内部供应商设置")
    public Result setInnerOrg(@RequestBody UserEO user){
        logger.info("======== UserController.setInnerOrg(user => "+user.getUserName()+") ========");
        logger.info("======== setInnerOrg ========"+user.getInnerOrg().longValue());
        logger.info("======== setInnerOrg ========"+(user.getInnerOrg().longValue() ==  -1));
        this.userService.setInnerOrg(user);
        return new Result();
    }

    /**
     * 查找所有符合条件的用户(不分页)
     * @param criteria
     * @return
     */
    @PostMapping("listAll")
    public Result<List<UserEO>> listAll(@RequestBody Criteria criteria) {
        criteria.setCurrentPage(1);
        criteria.setSize(999999999);

        List<UserEO> list = this.userService.selectPage(criteria).getRecords();

        if(list!=null && list.size()>0) {
            for(UserEO user : list) {
                // 设置完整的归属机构名称
                String completeOrgName = this.orgService.getCompleteOrgName(user.getLastOrgId()!=null?user.getLastOrgId():user.getOrgId());
                user.setOrgName(completeOrgName);
                // 设置用户的角色名称
                List<Long> roleIds = this.userService.selectUserRoleIds(user.getUserId());
                user.setRoleIds(roleIds);
                String roleNames = this.roleService.getRoleNames(roleIds);
                user.setRoleNames(roleNames);
                Integer status = user.getStatus();
                //借用用户头像字段
                if(status==0){
                    user.setAvatar("禁用");
                }
                if(status==1){
                    user.setAvatar("正常");
                }
                if(status==2){
                    user.setAvatar("维修");
                }
                if(status==3){
                    user.setAvatar("其他");
                }
                Integer userType = user.getUserType();
                //借用手机号字段
                if(userType==1){
                    user.setMobile("内部用户");
                }
                if(userType==2){
                    user.setMobile("供应商用户");
                }
                if(userType==3){
                    user.setMobile("客户用户");
                }
            }
        }

        return new Result<List<UserEO>>().ok(list);
    }

    /**
     * 导出
     * @param request
     * @param response
     * @param users 需要导出的数据
     * @return
     */
    @PostMapping("export")
    @OperationLog("导出")
    @RequiresPermissions("sys:user:export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody UserEO[] users){

        //配置的.json文件
        JSONObject jsonObject = ExcelUtils.parseJsonFile("user.json");
        //导出Excel
        ExcelUtils.exportExcel(response, Arrays.asList(users), jsonObject);
    }
}
