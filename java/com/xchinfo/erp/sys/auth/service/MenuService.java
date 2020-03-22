package com.xchinfo.erp.sys.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.sys.auth.entity.MenuEO;
import com.xchinfo.erp.sys.auth.entity.RoleEO;
import com.xchinfo.erp.sys.auth.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @project wms-sys-provider
 * @date 2018/5/11 14:04
 * @update
 */
@Service
public class MenuService extends BaseServiceImpl<MenuMapper, MenuEO> {

    @Autowired
    private RoleService roleService;


    public List<MenuEO> getUserMenuList(Long userId) {

        List<MenuEO> userMenus = this.baseMapper.getUserMenuList(userId);

        return TreeUtils.build(userMenus);
    }

    @Override
    public MenuEO getById(Serializable id) {
        return this.baseMapper.getById(id);
    }

    public List<MenuEO> selectTreeList(Integer type) {
        // 查询所有菜单
        List<MenuEO> menus = this.list(
                new QueryWrapper<MenuEO>()
                        .eq(null != type, "type", type)
                        .eq("status", 1));

        // 构建树形结构
        return TreeUtils.build(menus, Long.valueOf(0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = MenuEO.class)
    public boolean removeById(Serializable id) throws BusinessException {
        // 判断菜单是否有子菜单或按钮
        if (retBool(this.baseMapper.countByParentMenu((Long) id)))
            throw new BusinessException("菜单下存在子菜单或按钮，不能删除！");

        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(MenuEO entity) throws BusinessException {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(MenuEO entity) throws BusinessException {
        return super.updateById(entity);
    }

    public Map<String,Object> selectAppList(Long userId) throws BusinessException {

        List<MenuEO> userMenus = this.baseMapper.getUserAppMenuList(userId);
        if(null == userMenus || userMenus.size() == 0){
            throw new BusinessException("该用户无权限列表，请确认！");
        }

        Map<String,Object> map=getNewMap();

        for (MenuEO menuEO:userMenus){
            if(menuEO.getUrl().contains("gn01")){
                map.put("gn01",1);
                map.put("gnmc01",menuEO.getName());
                map.put("sm01",1);

            }else if(menuEO.getUrl().contains("gn02")){
                map.put("gn02",1);
                map.put("gnmc02",menuEO.getName());
                map.put("sm02",1);

            }else if(menuEO.getUrl().contains("gn03")){
                map.put("gn03",1);
                map.put("gnmc03",menuEO.getName());
                map.put("sm03",1);

            }else if(menuEO.getUrl().contains("gn04")){
                map.put("gn04",1);
                map.put("gnmc04",menuEO.getName());
                map.put("sm04",1);

            }else if(menuEO.getUrl().contains("gn05")){
                map.put("gn05",1);
                map.put("gnmc05",menuEO.getName());
                map.put("sm05",0);

            }else if(menuEO.getUrl().contains("gn06")){
                map.put("gn06",1);
                map.put("gnmc06",menuEO.getName());
                map.put("sm06",1);

            }else if(menuEO.getUrl().contains("gn07")){
                map.put("gn07",1);
                map.put("gnmc07",menuEO.getName());
                map.put("sm07",1);

            }else if(menuEO.getUrl().contains("gn08")){
                map.put("gn08",1);
                map.put("gnmc08",menuEO.getName());
                map.put("sm08",1);

            }else if(menuEO.getUrl().contains("gn09")){
                map.put("gn09",0);
                map.put("gnmc09",menuEO.getName());
                map.put("sm09",0);

            }else if(menuEO.getUrl().contains("gn10")){
                map.put("gn10",1);
                map.put("gnmc10",menuEO.getName());
                map.put("sm10",1);
                map.put("closescan10",1);

            }else if(menuEO.getUrl().contains("gn11")){
                map.put("gn11",1);
                map.put("gnmc11",menuEO.getName());
                map.put("sm11",0);

            }else if(menuEO.getUrl().contains("gn12")){
                map.put("gn12",1);
                map.put("gnmc12",menuEO.getName());
                map.put("sm12",0);

            }else if(menuEO.getUrl().contains("gn13")){
                map.put("gn13",1);
                map.put("gnmc13",menuEO.getName());
                map.put("sm13",1);
                map.put("closescan13",1);

            }else if(menuEO.getUrl().contains("gn14")){
                map.put("gn14",1);
                map.put("gnmc14",menuEO.getName());
                map.put("sm14",0);

            }else if(menuEO.getUrl().contains("gn15")){
                map.put("gn15",1);
                map.put("gnmc15",menuEO.getName());
                map.put("sm15",0);

            }else if(menuEO.getUrl().contains("gn16")){
                map.put("gn16",1);
                map.put("gnmc16",menuEO.getName());
                map.put("sm16",0);

            }else if(menuEO.getUrl().contains("gn17")){
                map.put("gn17",1);
                map.put("gnmc17",menuEO.getName());
                map.put("sm17",0);

            }else if(menuEO.getUrl().contains("gn18")){
                map.put("gn18",1);
                map.put("gnmc18",menuEO.getName());
                map.put("sm18",0);

            }else if(menuEO.getUrl().contains("gn19")){
                map.put("gn19",1);
                map.put("gnmc19",menuEO.getName());
                map.put("sm19",0);

            }else if(menuEO.getUrl().contains("gn20")){
                map.put("gn20",1);
                map.put("gnmc20",menuEO.getName());
                map.put("sm20",0);

            }else if(menuEO.getUrl().contains("gn21")){
                map.put("gn21",1);
                map.put("gnmc21",menuEO.getName());
                map.put("sm21",0);

            }else if(menuEO.getUrl().contains("gn22")){
                map.put("gn22",1);
                map.put("gnmc22",menuEO.getName());
                map.put("sm22",0);

            }else if(menuEO.getUrl().contains("gn23")){
                map.put("gn23",1);
                map.put("gnmc23",menuEO.getName());
                map.put("sm23",0);

            }else if(menuEO.getUrl().contains("gn24")){
                map.put("gn24",1);
                map.put("gnmc24",menuEO.getName());
                map.put("sm24",0);

            }
        }

//        String returnStr = "{";
//        for (String key : map.keySet()) {
//            Object value = map.get(key);
//            if("{".equals(returnStr)){
//                returnStr += key +":"+value;
//            }else{
//                returnStr += ","+key +":"+value;
//            }
//        }
//        returnStr += "}";
//
//        JSONObject jsonObject = JSONObject.parseObject(returnStr);

        return map;
    }

    public Map<String,Object> getNewMap(){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("sysName","沿浦MES系统");
        map.put("gn01",0);
        map.put("gn02",0);
        map.put("gn03",0);
        map.put("gn04",0);
        map.put("gn05",0);
        map.put("gn06",0);
        map.put("gn07",0);
        map.put("gn08",0);
        map.put("gn09",0);
        map.put("gn10",0);
        map.put("gn11",0);
        map.put("gn12",0);
        map.put("gn13",0);
        map.put("gn14",0);
        map.put("gn15",0);
        map.put("gn16",0);
        map.put("gn17",0);
        map.put("gn18",0);
        map.put("gn19",0);
        map.put("gn20",0);
        map.put("gn21",0);
        map.put("gn22",0);
        map.put("gn23",0);
        map.put("gn24",0);
        map.put("gnmc01","");
        map.put("gnmc02","");
        map.put("gnmc03","");
        map.put("gnmc04","");
        map.put("gnmc05","");
        map.put("gnmc06","");
        map.put("gnmc07","");
        map.put("gnmc08","");
        map.put("gnmc09","");
        map.put("gnmc10","");
        map.put("gnmc11","");
        map.put("gnmc12","");
        map.put("gnmc13","");
        map.put("gnmc14","");
        map.put("gnmc15","");
        map.put("gnmc16","");
        map.put("gnmc17","");
        map.put("gnmc18","");
        map.put("gnmc19","");
        map.put("gnmc20","");
        map.put("gnmc21","");
        map.put("gnmc22","");
        map.put("gnmc23","");
        map.put("gnmc24","");
        map.put("sm01",0);
        map.put("sm02",0);
        map.put("sm03",0);
        map.put("sm04",0);
        map.put("sm05",0);
        map.put("sm06",0);
        map.put("sm07",0);
        map.put("sm08",0);
        map.put("sm09",0);
        map.put("sm10",0);
        map.put("sm11",0);
        map.put("sm12",0);
        map.put("sm13",0);
        map.put("sm14",0);
        map.put("sm15",0);
        map.put("sm16",0);
        map.put("sm17",0);
        map.put("sm18",0);
        map.put("sm19",0);
        map.put("sm20",0);
        map.put("sm21",0);
        map.put("sm22",0);
        map.put("sm23",0);
        map.put("sm24",0);
        map.put("scantype01",0);
        map.put("scantype02",0);
        map.put("scantype03",0);
        map.put("scantype04",0);
        map.put("scantype05",0);
        map.put("scantype06",0);
        map.put("scantype07",0);
        map.put("scantype08",0);
        map.put("scantype09",0);
        map.put("scantype10",0);
        map.put("scantype11",0);
        map.put("scantype12",0);
        map.put("scantype13",0);
        map.put("scantype14",0);
        map.put("scantype15",0);
        map.put("scantype16",0);
        map.put("scantype17",0);
        map.put("scantype18",0);
        map.put("scantype19",0);
        map.put("scantype20",0);
        map.put("scantype21",0);
        map.put("scantype22",0);
        map.put("scantype23",0);
        map.put("scantype24",0);
        return map;
    }

    private boolean isInMenuIds(Long menuId, List<Long> menuIds) {
        for(int i=0; i<menuIds.size(); i++) {
            if(menuIds.get(i).longValue() == menuId.longValue()) {
                return true;
            }
        }
        return false;
    }

    private void setChecked(List tree, List<Long> menuIds) {
        for(int i=0; i<tree.size(); i++) {
            Object obj = tree.get(i);
            List children = ((MenuEO) obj).getChildren();
            if(children!=null && children.size()>0) {
                ((MenuEO) obj).setChecked(false);
                setChecked(children, menuIds);
            } else {
                if(isInMenuIds(((MenuEO) obj).getMenuId(), menuIds)) {
                    ((MenuEO) obj).setChecked(true);
                }
            }
        }
    }

    public List<MenuEO> roleMenu(Long roleId) {
        // 查询所有菜单
        List<MenuEO> menus = this.list(
                new QueryWrapper<MenuEO>().eq("status", 1));

        RoleEO role = this.roleService.getById(roleId);
        List<Long> menuIds = role.getMenuIds();
        // 构建树形结构
        List tree = TreeUtils.build(menus, Long.valueOf(0));
        setChecked(tree, menuIds);
        return tree;
    }
}
