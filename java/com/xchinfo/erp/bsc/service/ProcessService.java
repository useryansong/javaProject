package com.xchinfo.erp.bsc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.*;
import com.xchinfo.erp.bsc.mapper.*;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Service
public class ProcessService extends BaseServiceImpl<ProcessMapper, ProcessEO> {

    @Autowired
    private ProcessMachineMapper processMachineMapper;

    @Autowired
    private ProcessMaterialMapper processMaterialMapper;

    @Autowired
    private ProcessSkillMapper processSkillMapper;

    @Autowired
    private ProcessToolMapper processToolMapper;

    @Autowired
    private ProcessConstraintMapper processConstraintMapper;

    @Autowired
    private ProcessMachineService processMachineService;

    @Autowired
    private ProcessMaterialService processMaterialService;

    @Autowired
    private ProcessSkillService processSkillService;

    @Autowired
    private ProcessToolService processToolService;

    @Autowired
    private ProcessConstraintService processConstraintService;


    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public List<ProcessEO> listForTree(Map<String, Object> map) throws BusinessException {
        List<ProcessEO> processes = this.baseMapper.selectList(new QueryWrapper<>());

        return TreeUtils.build(processes, Long.valueOf(0));
    }

    public List<ProcessEO> listByMaterial(Long materialId) throws BusinessException {
        return this.baseMapper.selectByMaterial(materialId);
    }

    @Override
    public ProcessEO getById(Serializable id) {
        ProcessEO process = this.baseMapper.selectById(id);

        // 查找设备
        List<ProcessMachineEO> machines = this.processMachineMapper.selectByProcess((Long) id);
        process.setMachines(machines);

        // 查找物料
        List<ProcessMaterialEO> materials = this.processMaterialMapper.selectByProcess((Long) id);
        process.setMaterials(materials);

        // 查找工具
        List<ProcessToolEO> tools = this.processToolMapper.selectByProcess((Long) id);
        process.setTools(tools);

        // 查找技能
        List<ProcessSkillEO> skills = this.processSkillMapper.selectByProcess((Long) id);
        process.setSkills(skills);

        // 查找约束
        List<ProcessConstraintEO> constraints = this.processConstraintMapper.selectByProcess((Long) id);
        process.setConstraints(constraints);

        return process;
    }


    @Override
    public boolean save(ProcessEO entity) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("bsc_process", entity);
        AssertUtils.isBlank(code);
        entity.setProcessCode(code);

        //如果父节点为空则设为根节点
        if(entity.getParentProcess() == null){
            entity.setParentProcess(0L) ;
        }

        // 先保存工序对象
        if (!retBool(this.baseMapper.insert(entity))){
            throw new BusinessException("保存订单失败！");
        }

        //保存设备对象
        for (ProcessMachineEO machineEO : entity.getMachines()){
            machineEO.setProcessId(entity.getProcessId());
            if (!retBool(this.processMachineMapper.insert(machineEO))){
                throw new BusinessException("保存地址失败！");
            }
        }

        //保存物料对象
        for (ProcessMaterialEO materialEO : entity.getMaterials()){
            materialEO.setProcessId(entity.getProcessId());
            if (!retBool(this.processMaterialMapper.insert(materialEO))){
                throw new BusinessException("保存物料失败！");
            }
        }

        //保存技能对象
        for (ProcessSkillEO skillEO : entity.getSkills()){
            skillEO.setProcessId(entity.getProcessId());
            if (!retBool(this.processSkillMapper.insert(skillEO))){
                throw new BusinessException("保存技能失败！");
            }
        }

        //保存工具对象
        for (ProcessToolEO toolEO : entity.getTools()){
            toolEO.setProcessId(entity.getProcessId());
            if (!retBool(this.processToolMapper.insert(toolEO))){
                throw new BusinessException("保存工具失败！");
            }
        }

        //保存约束对象
        for (ProcessConstraintEO constraintEO : entity.getConstraints()){
            constraintEO.setProcessId(entity.getProcessId());
            if (!retBool(this.processConstraintMapper.insert(constraintEO))){
                throw new BusinessException("保存工具失败！");
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = ProcessEO.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) throws BusinessException {

        Integer result = 0;
        // 删除设备
        for (Serializable id : idList){
            result  = this.processMachineMapper.removeByProcessId((Long) id);

            if (null == result || result < 0){
                throw new BusinessException("删除设备失败！");
            }

            result  = this.processMaterialMapper.removeByProcessId((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除物料失败！");
            }

            result  = this.processSkillMapper.removeByProcessId((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除技能失败！");
            }

            result  = this.processToolMapper.removeByProcessId((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除工具失败！");
            }

            result  = this.processConstraintMapper.removeByProcessId((Long) id);
            if (null == result || result < 0){
                throw new BusinessException("删除约束失败！");
            }
        }

        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ProcessEO entity) throws BusinessException {

        Integer result = 0;

        super.updateById(entity);

        //先删除，再保存
        result  = this.processMachineMapper.removeByProcessId(entity.getProcessId());

        int MachineInt = entity.getMachines().size();

        if (null == result || result < 0 || (MachineInt > 0 && !this.processMachineService.saveOrUpdateBatch(entity.getMachines()))){
            throw new BusinessException("更新保存设备失败！");
        }

        //先删除，再保存
        result  = this.processMaterialMapper.removeByProcessId(entity.getProcessId());

        int materialInt = entity.getMaterials().size();

        if (null == result || result < 0 || (materialInt > 0 && !this.processMaterialService.saveOrUpdateBatch(entity.getMaterials()))){
            throw new BusinessException("更新保存物料失败！");
        }

        //先删除，再保存
        result  = this.processSkillMapper.removeByProcessId(entity.getProcessId());

        int skillInt = entity.getSkills().size();

        if (null == result || result < 0 || (skillInt > 0 && !this.processSkillService.saveOrUpdateBatch(entity.getSkills()))){
            throw new BusinessException("更新保存技能失败！");
        }

        //先删除，再保存
        result  = this.processToolMapper.removeByProcessId(entity.getProcessId());

        int toolInt = entity.getTools().size();

        if (null == result || result < 0 || (toolInt > 0 && !this.processToolService.saveOrUpdateBatch(entity.getTools()))){
            throw new BusinessException("更新保存约束失败！");
        }

        //先删除，再保存
        result  = this.processConstraintMapper.removeByProcessId(entity.getProcessId());

        int constraintInt = entity.getConstraints().size();

        if (null == result || result < 0 || (constraintInt > 0 && !this.processConstraintService.saveOrUpdateBatch(entity.getConstraints()))){
            throw new BusinessException("更新保存技能失败！");
        }

        return true;
    }

}
