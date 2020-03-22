package com.xchinfo.erp.hrms.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.common.HrDBConnectInfo;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.hrms.entity.PositionEO;
import com.xchinfo.erp.hrms.mapper.PositionMapper;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.JsonUtil;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.jdbc.SqlActuator;

import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2018/12/8
 * @update
 */
@Service
public class PositionService extends BaseServiceImpl<PositionMapper, PositionEO>{

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private HrDBConnectInfo hrDBConnectInfo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(PositionEO entity) throws BusinessException {
        logger.info("======== PositionService.save() ========");
        /*// 生成编码
        String code = this.businessCodeGenerator.generateNextCode("hr_position", entity);
        AssertUtils.isBlank(code);
        entity.setPositionCode(code);*/

        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(PositionEO entity) throws BusinessException {
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.BATCHDELETE, entityClass = PositionEO.class)
    public boolean removeByIds(Serializable[] ids) throws BusinessException {
        logger.info("======== PositionService.removeByIds() ========");

        for (Serializable id : ids){
            // 检查是否有使用
            if (retBool(this.baseMapper.countFromEmployee((Long) id))){
                PositionEO position = this.getById(id);

                throw new BusinessException("[" + position.getPositionName() + "]已经被使用，不能删除!");
            }
        }

        return super.removeByIds(ids);
    }

    public Pagination selectPage(Map map){
        int pageSize = Integer.parseInt(map.get("size").toString());
        List<Map> list = positionMapper.getPositionList(map);
        int count = positionMapper.getPositionListCount(map);
        return Pagination.getPagination(list,count,pageSize);
    }

    /**
     * 同步hr系统数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result syncHR(){
        String selectSql = "select * from sys_position";
        List<Map<String,Object>> list = SqlActuator.excuteQuery(selectSql,hrDBConnectInfo);
        if(list!=null && list.size()>0){
            Set ids = new HashSet();
            for(int i = 0 ; i < list.size() ; i++){
                Map<String,Object> temp = list.get(i);
                Long position_id = (Long)temp.get("position_id");
                String position_code = (String)temp.get("position_code");
                String position_name = (String)temp.get("position_name");
                Long org_id = (Long)temp.get("org_id");
                String description = (String)temp.get("description");
                Integer status = (Integer)temp.get("status");
                ids.add(position_id);
                PositionEO position = new PositionEO();
                position.setPositionId(position_id);
                position.setPositionCode(position_code);
                position.setDescription(description);
                position.setOrgId(org_id+"");
                position.setPositionName(position_name);
                position.setStatus(status);
                this.saveOrupdate(position);
            }
            //删除数据
            Map param = new HashMap();
            param.put("ids",ids);
            this.baseMapper.deleteByIds(param);

        }
        return new Result().ok(true);
    }

    public boolean saveOrupdate(PositionEO positionEO){

        PositionEO old = this.baseMapper.selectByIdOne(positionEO.getPositionId());
        boolean res = false;
        if(old!=null){
            old.setStatus(positionEO.getStatus());
            old.setPositionName(positionEO.getPositionName());
            old.setOrgId(positionEO.getOrgId());
            old.setDescription(positionEO.getDescription());
            old.setPositionCode(positionEO.getPositionCode());
            res = this.updateById(old);
        }else{
            CommonUtil.setFill(positionEO);
            int count = this.baseMapper.insertByHR(positionEO);
            if(count>0){
                res = true;
            }
        }
        return res;
    }
}
