package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.bsc.entity.*;
import com.xchinfo.erp.bsc.mapper.WarehouseAreaMapper;
import com.xchinfo.erp.scm.srm.entity.PurchaseOrderEO;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.service.OrgService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/4/16
 * @update
 */
@Service
public class WarehouseAreaService extends BaseServiceImpl<WarehouseAreaMapper, WarehouseAreaEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public List<WarehouseAreaEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private OrgService orgService;

    @Transactional(rollbackFor = Exception.class)
    public boolean save(WarehouseAreaEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库区的归属机构权限不存在该用户权限,请确认!");

        //库区名称不允许重复
        String areaName = entity.getAreaName();
        WarehouseAreaEO warehouseArea = this.baseMapper.selectByName(areaName);
        if(warehouseArea != null) {
            throw new BusinessException("库区名称已存在！");
        }

        /*// 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("bsc_warehouse_area", entity);
        AssertUtils.isBlank(code);
        entity.setAreaCode(code);*/

        //库区编码重复校验
        if(this.baseMapper.checkAreaCode(entity.getOrgId(),entity.getWarehouseId(),entity.getAreaCode())>0){
            throw new BusinessException("库区编码已存在!");
        }

        // 设置条码
        String barCode = this.getBarCode(entity, entity.getAreaCode());
        WarehouseAreaEO warehouseAreaFromDb = this.baseMapper.getByBarCode(barCode);
        if(warehouseAreaFromDb != null){
            throw new BusinessException("条码已存在!");
        }
        entity.setBarCode(barCode);

        return super.save(entity);
    }

    public int updateStatus(WarehouseAreaEO entity, Long userId){
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库区的归属机构权限不存在该用户权限,请确认!");

        return this.baseMapper.updateStatusById(entity.getWarehouseAreaId(), entity.getStatus());
    }

    // 设置条码: 条码格式为: 仓库编码-库区编码,全局唯一
    private String getBarCode(WarehouseAreaEO entity, String areaCode){
        WarehouseEO warehouse = warehouseService.getById(entity.getWarehouseId());
        if(warehouse == null) {
            throw new BusinessException("仓库不存在,请刷新!");
        }
        return warehouse.getErpCode() + "-" + areaCode ;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WarehouseAreaEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库区的归属机构权限不存在该用户权限,请确认!");

        //库区名称不允许重复
        String areaName = entity.getAreaName();
        WarehouseAreaEO warehouseArea = this.baseMapper.selectByName(areaName);
        if(warehouseArea!=null && warehouseArea.getWarehouseAreaId().longValue()!=entity.getWarehouseAreaId().longValue()) {
            throw new BusinessException("库区名称已存在！");
        }
        //库区编码重复校验
        WarehouseAreaEO old = this.getById(entity.getWarehouseAreaId());
        if(old.getOrgId().longValue() !=entity.getOrgId().longValue()
                ||old.getWarehouseId().longValue() !=entity.getWarehouseId().longValue()
                ||!old.getAreaCode().equals(entity.getAreaCode())){
            if(this.baseMapper.checkAreaCode(entity.getOrgId(),entity.getWarehouseId(),entity.getAreaCode())>0){
                throw new BusinessException("库区编码已存在!");
            }
        }

        // 设置条码
        String barCode = this.getBarCode(entity, entity.getAreaCode());
        WarehouseAreaEO warehouseAreaFromDb = this.baseMapper.getByBarCode(barCode);
        if(warehouseAreaFromDb!=null &&
                warehouseAreaFromDb.getWarehouseAreaId().longValue()!=entity.getWarehouseAreaId().longValue()){
            throw new BusinessException("条码已存在!");
        }
        entity.setBarCode(barCode);

        boolean res = super.updateById(entity);
        if(res){
            if(!old.getBarCode().equals(entity.getBarCode())){
                //条码产生变化。同步更改该库区下面的库位的条码
                this.baseMapper.updateBarCodeForWarehouseLocation(entity.getWarehouseAreaId(),entity.getBarCode()+"-",old.getBarCode()+"-");
            }
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Long[] ids, Long userId) throws BusinessException {
        for (Serializable id : ids) {
            WarehouseAreaEO warehouseArea = this.baseMapper.selectById(id);
            //校验机构权限
            Boolean flag = this.orgService.checkUserPermissions(warehouseArea.getOrgId(), userId);
            if(!flag.booleanValue()){
                continue;
            }

            super.removeById(id);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importFromExcel(List list, Long orgId) throws BusinessException{
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        this.orgService.checkUserPermissions(orgId, user.getUserId(), "选择的归属机构权限不存在该用户权限,请确认!");
        if(list != null){
            if(list.size()>0){
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                String errorMsg = "";
                //判断是否为空
                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);
                        String tempErrorMsg="";
                        String warehouseCode = map.get("0")==null?null:((String) map.get("0")).trim();
                        if(warehouseCode==null || warehouseCode.equals("")) {
                            tempErrorMsg+="&nbsp;仓库编码为空&nbsp;";
                        }
                        String warehouseAreaCode = map.get("1")==null?null:((String) map.get("1")).trim();
                        if(warehouseAreaCode==null || warehouseAreaCode.equals("")) {
                            tempErrorMsg+="&nbsp;库区编码为空&nbsp;";
                        }
                        String warehouseAreaName = map.get("2")==null?null:((String) map.get("2")).trim();
                        if(warehouseAreaName==null || warehouseAreaName.equals("")) {
                            tempErrorMsg+="&nbsp;库区名称为空&nbsp;";
                        }
                        int sorti = i+1;
                        if(!tempErrorMsg.equals("")){
                            errorMsg+="第"+sorti+"行&nbsp;"+tempErrorMsg+"<br>";
                        }
                        tempErrorMsg="";
                        for(int j=1; j<mapList.size(); j++) {
                            if(i>=j) continue;
                            int sortj = j+1;
                            Map temp = mapList.get(j);
                            String warehouseAreaCodeTemp = temp.get("1")==null?null:((String) temp.get("1")).trim();
                            String warehouseCodeTemp = temp.get("0")==null?null:((String) temp.get("0")).trim();
                            if(warehouseCodeTemp!=null && !warehouseCodeTemp.equals("")
                                    &&warehouseCode!=null && !warehouseCode.equals("")){
                                if(!warehouseCodeTemp.equals(warehouseCode)){
                                    continue;
                                }
                            }
                            if(warehouseAreaCodeTemp!=null && !warehouseAreaCodeTemp.equals("")
                                    &&warehouseAreaCode!=null && !warehouseAreaCode.equals("")) {
                                if(warehouseAreaCode.equals(warehouseAreaCodeTemp)){
                                    tempErrorMsg+="第"+sorti+"行库区编码与第"+sortj+"行库区编码重复<br>";
                                }
                            }
                            String warehouseAreaNameTemp = temp.get("2")==null?null:((String) temp.get("2")).trim();
                            if(warehouseAreaNameTemp!=null && !warehouseAreaNameTemp.equals("")
                                &&warehouseAreaName!=null && !warehouseAreaName.equals("")) {
                                if(warehouseAreaName.equals(warehouseAreaNameTemp)){
                                    tempErrorMsg+="第"+sorti+"行库区名称与第"+sortj+"行库区名称重复<br>";
                                }
                            }
                        }
                        if(!tempErrorMsg.equals("")){
                            errorMsg+=tempErrorMsg;
                        }
                    }
                }
                if(!errorMsg.equals("")) {
                    throw new BusinessException(errorMsg);
                }
                List<WarehouseAreaEO> dataList = new ArrayList<WarehouseAreaEO>();
                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);
                        String tempErrorMsg="";
                        int sorti = i+1;
                        String warehouseCode = (String) map.get("0");
                        String warehouseAreaCode = (String) map.get("1");
                        String warehouseAreaName = (String) map.get("2");
                        //根据仓库编码获取仓库信息
                        QueryWrapper<WarehouseEO>  wrapper = new QueryWrapper<WarehouseEO>();
                        wrapper.eq("warehouse_code", warehouseCode);
                        wrapper.eq("org_id", orgId);
                        WarehouseEO warehouseEO = this.warehouseService.getOne(wrapper);
                        if(warehouseEO == null) {
                            tempErrorMsg+="该机构下找不到第"+sorti+"行的仓库编码"+warehouseCode+"<br>";
                        }
                        if(!tempErrorMsg.equals("")){
                            errorMsg+=tempErrorMsg;
                        }else{
                            QueryWrapper<WarehouseAreaEO> wrapperArea = new QueryWrapper<WarehouseAreaEO>();
                            wrapperArea.eq("warehouse_id", warehouseEO.getWarehouseId());
                            wrapperArea.eq("org_id", orgId);
                            wrapperArea.eq("area_code", warehouseAreaCode);
                            WarehouseAreaEO warehouseAreaEO = this.getOne(wrapperArea);
                            if(warehouseAreaEO!=null){
                                errorMsg+="该机构下第"+sorti+"行的库区编码"+warehouseAreaCode+"在数据库中已经存在<br>";
                                continue;
                            }
                            QueryWrapper<WarehouseAreaEO> wrapperArea2 = new QueryWrapper<WarehouseAreaEO>();
                            wrapperArea2.eq("warehouse_id", warehouseEO.getWarehouseId());
                            wrapperArea2.eq("org_id", orgId);
                            wrapperArea2.eq("area_name", warehouseAreaName);
                            WarehouseAreaEO warehouseAreaEO2 = this.getOne(wrapperArea2);
                            if(warehouseAreaEO2!=null){
                                errorMsg+="该机构下第"+sorti+"行的库区名称"+warehouseAreaName+"在数据库中已经存在<br>";
                                continue;
                            }
                            WarehouseAreaEO temp = new WarehouseAreaEO();
                            temp.setAreaCode(warehouseAreaCode);
                            temp.setAreaName(warehouseAreaName);
                            temp.setStatus(1);
                            temp.setOrgId(orgId);
                            temp.setWarehouseId(warehouseEO.getWarehouseId());
                            temp.setBarCode(warehouseEO.getErpCode()+"-"+warehouseAreaCode);
                            dataList.add(temp);

                        }
                    }

                }
                if(!errorMsg.equals("")) {
                    throw new BusinessException(errorMsg);
                }
                //保存数据
                if(dataList!=null && dataList.size()>0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        this.save(dataList.get(i));
                    }
                }
            }else{
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }
    }
}
