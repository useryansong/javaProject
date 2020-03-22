package com.xchinfo.erp.controller.sys.auth;


import com.alibaba.fastjson.JSONObject;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.MenuEO;
import com.xchinfo.erp.sys.auth.service.MenuService;
import com.xchinfo.erp.sys.auth.service.ShiroService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author roman.li
 * @project wms-web
 * @date 2018/5/11 12:44
 * @update
 */
@RestController
@RequestMapping("/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private ShiroService shiroService;

    /**
     * 导航菜单
     *
     * @return
     */
    @GetMapping("nav")
    public Result<List<MenuEO>> nav(){
        logger.info("======== MenuController.nav() ========");

        List<MenuEO> menuList = this.menuService.getUserMenuList(getUserId());

        return new Result<List<MenuEO>>().ok(menuList);
    }

    /**
     * 查询用户授权
     *
     * @return
     */
    @GetMapping("permissions")
    public Result<Set<String>> permissions(){
        logger.info("======== MenuController.permissions() ========");

        Set<String> perms = this.shiroService.getUserPermissions(getUserId());

        return new Result<Set<String>>().ok(perms);
    }

    /**
     * 查询菜单列表
     *
     * @param type 菜单类型 0：菜单 1：按钮  null：全部
     * @return
     */
    @GetMapping("tree")
    @RequiresPermissions("sys:menu:info")
    public List<MenuEO> tree(Integer type){
        logger.info("======== MenuController.list(type ==> "+type+") ========");

        List<MenuEO> menuList = this.menuService.selectTreeList(type);

        return menuList;
    }

    /**
     * 查询菜单列表
     *
     * @param type 菜单类型 0：菜单 1：按钮  null：全部
     * @return
     */
    @GetMapping("list")
    @RequiresPermissions("sys:menu:info")
    public Result<List<MenuEO>> list(Integer type){
        logger.info("======== MenuController.list(type ==> "+type+") ========");

        List<MenuEO> menuList = this.menuService.selectTreeList(type);

        return new Result<List<MenuEO>>().ok(menuList);
    }

    @GetMapping("roleMenu")
    public Result<List<MenuEO>> roleMenu(@RequestParam("roleId") Long roleId){
        List<MenuEO> menuList = this.menuService.roleMenu(roleId);

        return new Result<List<MenuEO>>().ok(menuList);
    }

    /**
     * 查询菜单
     *
     * @param menuId
     * @return
     */
    @GetMapping("{menuId}")
    @RequiresPermissions("sys:menu:info")
    public Result<MenuEO> info(@PathVariable("menuId") Long menuId){
        logger.info("======== MenuController.info(menuId ==> "+menuId+") ========");

        MenuEO menu = this.menuService.getById(menuId);

        return new Result<MenuEO>().ok(menu);
    }

    /**
     * 保存
     */
    @PostMapping
    @RequiresPermissions("sys:menu:create")
    @OperationLog("创建菜单")
    public Result create(@RequestBody MenuEO menu){
        logger.info("======== MenuController.create() ========");

        ValidatorUtils.validateEntity(menu, AddGroup.class, DefaultGroup.class);

        this.menuService.save(menu);

        return new Result();
    }

    /**
     * 修改
     */
    @PutMapping
    @RequiresPermissions("sys:menu:update")
    @OperationLog("更新菜单")
    public Result update(@RequestBody MenuEO menu){
        logger.info("======== MenuController.update() ========");
        ValidatorUtils.validateEntity(menu, UpdateGroup.class, DefaultGroup.class);

        this.menuService.updateById(menu);

        return new Result();
    }

    /**
     * 删除
     */
    @DeleteMapping("{menuId}")
    @RequiresPermissions("sys:menu:delete")
    @OperationLog("删除菜单")
    public Result delete(@PathVariable("menuId") Long menuId){
        logger.info("======== MenuController.delete(menuId ==> "+menuId+") ========");

        AssertUtils.isNull(menuId, "id");

        this.menuService.removeById(menuId);

        return new Result();
    }


    /**
     * 查询APP功能菜单
     *
     * @param
     * @return
     */
    @GetMapping("app")
    public Result appMenu(){
        logger.info("======== MenuController.appMenu() ========");

        Map<String,Object> obj = this.menuService.selectAppList(getUserId());

        Result result = new Result<Map<String,Object>>().ok(obj);
        result.setMsg(getUserName()+"["+getUserId()+"]");

        return result;
    }

}
