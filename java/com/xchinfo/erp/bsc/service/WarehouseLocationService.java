package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.bsc.entity.WarehouseAreaEO;
import com.xchinfo.erp.bsc.entity.WarehouseEO;
import com.xchinfo.erp.bsc.entity.WarehouseLocationEO;
import com.xchinfo.erp.bsc.mapper.WarehouseLocationMapper;
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
public class WarehouseLocationService extends BaseServiceImpl<WarehouseLocationMapper, WarehouseLocationEO> {
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private WarehouseAreaService warehouseAreaService;
    @Autowired
    private OrgService orgService;

    public List<WarehouseLocationEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }


    // 设置条码: 条码格式为: 仓库编码-库区编码-库位编码,全局唯一
    private String getBarCode(WarehouseLocationEO entity, String locationCode){
        WarehouseEO warehouse = warehouseService.getById(entity.getWarehouseId());
        if(warehouse == null) {
            throw new BusinessException("仓库不存在,请刷新!");
        }
        WarehouseAreaEO warehouseArea = warehouseAreaService.getById(entity.getWarehouseAreaId());
        if(warehouseArea == null) {
            throw new BusinessException("库区不存在,请刷新!");
        }
        return warehouse.getErpCode() + "-" + warehouseArea.getAreaCode() + "-" + locationCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean save(WarehouseLocationEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库位的归属机构权限不存在该用户权限,请确认!");

        //库位名称不允许重复
        String locationName = entity.getLocationName();
        WarehouseLocationEO warehouseLocation = this.baseMapper.selectByName(locationName);
        if(warehouseLocation != null) {
            throw new BusinessException("库位名称已存在！");
        }

        /*// 生成业务编码
        String code = this.businessCodeGenerator.generateNextCode("bsc_warehouse_location", entity);
        AssertUtils.isBlank(code);
        entity.setLocationCode(code);*/
        //库位编码重复校验
        if(this.baseMapper.checkLocationCode(entity.getOrgId(),entity.getWarehouseId(),entity.getWarehouseAreaId(),entity.getLocationCode())>0){
            throw new BusinessException("库位编码已存在!");
        }

        // 设置条码
        String barCode = this.getBarCode(entity, entity.getLocationCode());
        WarehouseLocationEO warehouseLocationFromDb = this.baseMapper.getByBarCode(barCode);
        if(warehouseLocationFromDb != null){
            throw new BusinessException("条码已存在!");
        }
        entity.setBarCode(barCode);

        return super.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WarehouseLocationEO entity, Long userId) throws BusinessException {
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库位的归属机构权限不存在该用户权限,请确认!");

        //库位名称不允许重复
        String locationName = entity.getLocationName();
        WarehouseLocationEO warehouseLocation = this.baseMapper.selectByName(locationName);
        if(warehouseLocation!=null && warehouseLocation.getWarehouseLocationId().longValue()!=entity.getWarehouseLocationId().longValue()) {
            throw new BusinessException("库位名称已存在！");
        }

        //库位编码重复校验
        WarehouseLocationEO old = this.getById(entity.getWarehouseLocationId());
        if(old.getOrgId().longValue() !=entity.getOrgId().longValue()
                ||old.getWarehouseId().longValue() !=entity.getWarehouseId().longValue()
                ||old.getWarehouseAreaId().longValue() !=entity.getWarehouseAreaId().longValue()
                ||!old.getLocationCode().equals(entity.getLocationCode())){
            if(this.baseMapper.checkLocationCode(entity.getOrgId(),entity.getWarehouseId(),entity.getWarehouseAreaId(),entity.getLocationCode())>0){
                throw new BusinessException("库位编码已存在!");
            }
        }

        // 设置条码
        String barCode = this.getBarCode(entity, entity.getLocationCode());
        WarehouseLocationEO warehouseLocationFromDb = this.baseMapper.getByBarCode(barCode);
        if(warehouseLocationFromDb!=null &&
                warehouseLocationFromDb.getWarehouseLocationId().longValue()!=entity.getWarehouseLocationId().longValue()){
            throw new BusinessException("条码已存在!");
        }
        entity.setBarCode(barCode);

        return super.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Long[] ids, Long userId) throws BusinessException {
        for (Serializable id : ids) {
            WarehouseLocationEO warehouseLocation = this.baseMapper.selectById(id);
            //校验机构权限
            Boolean flag = this.orgService.checkUserPermissions(warehouseLocation.getOrgId(), userId);
            if(!flag.booleanValue()){
                continue;
            }

            super.removeById(id);
        }

        return true;
    }


    public int updateStatus(WarehouseLocationEO entity, Long userId){
        this.orgService.checkUserPermissions(entity.getOrgId(), userId, "库位的归属机构权限不存在该用户权限,请确认!");

        return this.baseMapper.updateStatusById(entity.getWarehouseLocationId(), entity.getStatus());
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
                        String warehouseLocationCode = map.get("2")==null?null:((String) map.get("2")).trim();
                        if(warehouseLocationCode==null || warehouseLocationCode.equals("")) {
                            tempErrorMsg+="&nbsp;库位编码为空&nbsp;";
                        }
                        String warehouseLocationName = map.get("3")==null?null:((String) map.get("3")).trim();
                        if(warehouseLocationName==null || warehouseLocationName.equals("")) {
                            tempErrorMsg+="&nbsp;库位名称为空&nbsp;";
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
                            String warehouseLocationCodeTemp = temp.get("2")==null?null:((String) temp.get("2")).trim();
                            String warehouseCodeTemp = temp.get("0")==null?null:((String) temp.get("0")).trim();
                            String warehouseAreaCodeTemp = temp.get("1")==null?null:((String) temp.get("1")).trim();
                            if(warehouseCodeTemp!=null && !warehouseCodeTemp.equals("")
                                    &&warehouseCode!=null && !warehouseCode.equals("")
                                    &&warehouseAreaCode!=null && !warehouseAreaCode.equals("")
                                    &&warehouseAreaCodeTemp!=null && !warehouseAreaCodeTemp.equals("")){
                                if(!warehouseCodeTemp.equals(warehouseCode)||!warehouseAreaCodeTemp.equals(warehouseAreaCode)){
                                    continue;
                                }
                            }
                            if(warehouseLocationCodeTemp!=null && !warehouseLocationCodeTemp.equals("")
                                    &&warehouseLocationCode!=null && !warehouseLocationCode.equals("")) {
                                if(warehouseLocationCode.equals(warehouseLocationCodeTemp)){
                                    tempErrorMsg+="第"+sorti+"行库位编码与第"+sortj+"行库位编码重复<br>";
                                }
                            }
                            String warehouseLocationNameTemp = temp.get("3")==null?null:((String) temp.get("3")).trim();
                            if(warehouseLocationNameTemp!=null && !warehouseLocationNameTemp.equals("")
                                    &&warehouseLocationName!=null && !warehouseLocationName.equals("")) {
                                if(warehouseLocationName.equals(warehouseLocationNameTemp)){
                                    tempErrorMsg+="第"+sorti+"行库位名称与第"+sortj+"行库位名称重复<br>";
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
                List<WarehouseLocationEO> dataList = new ArrayList<WarehouseLocationEO>();
                if(mapList!=null && mapList.size()>0){
                    for(int i=1; i<mapList.size(); i++){
                        Map map = mapList.get(i);
                        int sorti = i+1;
                        String warehouseCode = (String) map.get("0");
                        String warehouseAreaCode = (String) map.get("1");
                        String warehouseLocationCode = (String) map.get("2");
                        String warehouseLocationName = (String) map.get("3");
                        //根据仓库编码获取仓库信息
                        QueryWrapper<WarehouseEO> wrapper = new QueryWrapper<WarehouseEO>();
                        wrapper.eq("warehouse_code", warehouseCode);
                        wrapper.eq("org_id", orgId);
                        WarehouseEO warehouseEO = this.warehouseService.getOne(wrapper);
                        if(warehouseEO == null) {
                            errorMsg+="该机构下找不到第"+sorti+"行的仓库编码"+warehouseCode+"<br>";
                        }else{

                            QueryWrapper<WarehouseAreaEO> wrapperArea = new QueryWrapper<WarehouseAreaEO>();
                            wrapperArea.eq("warehouse_id", warehouseEO.getWarehouseId());
                            wrapperArea.eq("org_id", orgId);
                            wrapperArea.eq("area_code", warehouseAreaCode);
                            WarehouseAreaEO warehouseAreaEO = this.warehouseAreaService.getOne(wrapperArea);
                            if(warehouseAreaEO == null) {
                                errorMsg+="该机构下找不到第"+sorti+"行的仓库编码为"+warehouseCode+"的下面库区编码 "+warehouseAreaCode+"<br>";
                            }else {
                                QueryWrapper<WarehouseLocationEO> wrapperLocation = new QueryWrapper<WarehouseLocationEO>();
                                wrapperLocation.eq("warehouse_id", warehouseEO.getWarehouseId());
                                wrapperLocation.eq("org_id", orgId);
                                wrapperLocation.eq("warehouse_area_id", warehouseAreaEO.getWarehouseAreaId());
                                wrapperLocation.eq("location_code", warehouseLocationCode);
                                WarehouseLocationEO warehouseLocationEO = this.getOne(wrapperLocation);
                                if(warehouseLocationEO != null) {
                                    errorMsg+="该机构下第"+sorti+"行的库位编码"+warehouseLocationCode+"在数据库中已经存在<br>";
                                    continue;
                                }

                                QueryWrapper<WarehouseLocationEO> wrapperLocation2 = new QueryWrapper<WarehouseLocationEO>();
                                wrapperLocation2.eq("warehouse_id", warehouseEO.getWarehouseId());
                                wrapperLocation2.eq("org_id", orgId);
                                wrapperLocation2.eq("warehouse_area_id", warehouseAreaEO.getWarehouseAreaId());
                                wrapperLocation2.eq("location_name", warehouseLocationName);
                                WarehouseLocationEO warehouseLocationEO2 = this.getOne(wrapperLocation2);
                                if(warehouseLocationEO2 != null) {
                                    errorMsg+="该机构下第"+sorti+"行的库位名称"+warehouseLocationName+"在数据库中已经存在<br>";
                                    continue;
                                }
                                WarehouseLocationEO temp = new WarehouseLocationEO();
                                temp.setStatus(1);
                                temp.setOrgId(orgId);
                                temp.setWarehouseId(warehouseEO.getWarehouseId());
                                temp.setWarehouseAreaId(warehouseAreaEO.getWarehouseAreaId());
                                temp.setLocationName(warehouseLocationName);
                                temp.setLocationCode(warehouseLocationCode);
                                temp.setBarCode(warehouseEO.getErpCode() + "-" + warehouseAreaCode + "-" + warehouseLocationCode);
                                dataList.add(temp);


                            }
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
