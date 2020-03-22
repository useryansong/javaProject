package com.xchinfo.erp.mes.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.mes.mapper.WorkingProcedureTimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WorkingProcedureTimeService extends BaseServiceImpl<WorkingProcedureTimeMapper, WorkingProcedureTimeEO>{
    @Autowired
    private WorkingProcedureTimeMapper workingProcedureTimeMapper;

    public Pagination selectPage(Map map){
        int pageSize = Integer.parseInt(map.get("size").toString());
        List<Map> list = workingProcedureTimeMapper.getPageList(map);
        int count = workingProcedureTimeMapper.getPageListCount(map);
        return Pagination.getPagination(list,count,pageSize);
    }

    public List<Map> hasProject(Long userId){
        return workingProcedureTimeMapper.hasProject(userId);
    }

    public List<Map> hasWorkShop(Map map){
        return workingProcedureTimeMapper.hasWorkShop(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.UPDATE, entityClass = WorkingProcedureTimeEO.class)
    public boolean updateById(WorkingProcedureTimeEO entity) throws BusinessException {
        // 工序号判重
        String WorkingProcedureTypeName = entity.getWorkingProcedureTypeName();


        if("冲压".equals(WorkingProcedureTypeName)){
            Long operationNumber = entity.getOperationNumber();
            BigDecimal ct = entity.getCt();
            Long mqNumber = entity.getMqNumber();
            entity.setCtPer(ct.multiply(new BigDecimal(operationNumber)).divide(new BigDecimal(mqNumber),2));
        }
        return super.updateById(entity);
    }
    public Result update(WorkingProcedureTimeEO entity) throws BusinessException {

        if(entity.getWorkingProcedureCode().equals("") ){
            // 生成工序号
            String WorkingProcedureTypeName = entity.getWorkingProcedureTypeName();
            String preFixx = entity.getPreffix();//工序号前缀
            String maxCode="";

            if("冲压".equals(WorkingProcedureTypeName)){
                //冲压
                maxCode =this.baseMapper.getMaxGxhByType(preFixx,entity.getOrgId());
                Long operationNumber = entity.getOperationNumber();
                BigDecimal ct = entity.getCt();
                Long mqNumber = entity.getMqNumber();
                entity.setCtPer(ct.multiply(new BigDecimal(operationNumber)).divide(new BigDecimal(mqNumber),2));
            }else{
                //焊装
                maxCode =this.baseMapper.getMaxGxhByType(preFixx,entity.getOrgId());
            }
            int code = Integer.parseInt(maxCode.substring(1,maxCode.length()).replaceAll("^(0+)", ""))+5;
            if(code<10000){
                //前面补零
                for(int i=0;i<5-(code+"").length();i++){
                    preFixx+=0;
                }
            }
            entity.setWorkingProcedureCode(preFixx+code);
        }

        WorkingProcedureTimeEO old = this.getById(entity.getWorkingProcedureTimeId());
        if(!old.getWorkingProcedureCode().equals(entity.getWorkingProcedureCode())){
            //手动更改过工序号，需要判重
            if(this.baseMapper.checkRepeat(entity.getWorkingProcedureCode(),entity.getOrgId())>0){
                return new Result().error("工序号重复");
            }
        }
        if(super.updateById(entity)){
            //将该物料的对应的其他工序改为非最后一道工序
            if(entity.getIsLastProcedure() == 1){
                this.baseMapper.updateOtherProcedureStatus(entity.getWorkingProcedureTimeId(),entity.getMaterialId());
            }
            return new Result().ok("保存成功");
        }else{
            return new Result().error("保存失败");
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = WorkingProcedureTimeEO.class)
    public boolean save(WorkingProcedureTimeEO entity) throws BusinessException {

        if(entity.getWorkingProcedureCode().equals("") ){
            // 生成工序号
            String WorkingProcedureTypeName = entity.getWorkingProcedureTypeName();
            String preFixx = entity.getPreffix();//工序号前缀
            String maxCode="";

            if("冲压".equals(WorkingProcedureTypeName)){
                //冲压
                maxCode =this.baseMapper.getMaxGxhByType(preFixx,entity.getOrgId());
                Long operationNumber = entity.getOperationNumber();
                BigDecimal ct = entity.getCt();
                Long mqNumber = entity.getMqNumber();
                entity.setCtPer(ct.multiply(new BigDecimal(operationNumber)).divide(new BigDecimal(mqNumber),2));
            }else{
                //焊装
                maxCode =this.baseMapper.getMaxGxhByType(preFixx,entity.getOrgId());
            }
            int code = Integer.parseInt(maxCode.substring(1,maxCode.length()).replaceAll("^(0+)", ""))+5;
            if(code<10000){
                //前面补零
                for(int i=0;i<5-(code+"").length();i++){
                    preFixx+=0;
                }
            }
            entity.setWorkingProcedureCode(preFixx+code);
        }else{
            if(this.baseMapper.checkRepeat(entity.getWorkingProcedureCode(),entity.getOrgId())>0){
                throw new BusinessException("此归属机构下的工序号已存在，请确认！");
            }
        }

        entity.setProject(entity.getProject().trim());
        super.save(entity);
        //将该物料的对应的其他工序改为非最后一道工序
        if(entity.getIsLastProcedure() == 1){
            this.baseMapper.updateOtherProcedureStatus(entity.getWorkingProcedureTimeId(),entity.getMaterialId());
        }
        return true;
    }

    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }
}
