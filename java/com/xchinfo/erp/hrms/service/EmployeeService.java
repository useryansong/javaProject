package com.xchinfo.erp.hrms.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.common.HrDBConnectInfo;
import com.xchinfo.erp.common.Pagination;
import com.xchinfo.erp.hrms.entity.*;
import com.xchinfo.erp.hrms.mapper.EmployeeMapper;
import com.xchinfo.erp.log.entity.BizLogEO;
import com.xchinfo.erp.log.service.BizLogService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.DateUtils;
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
public class EmployeeService extends BaseServiceImpl<EmployeeMapper, EmployeeEO>{

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    private EmployeeExperienceService employeeExperienceService;

    @Autowired
    private EmployeePositionService employeePositionService;

    @Autowired
    private EmployeeEducationService employeeEducationService;

    @Autowired
    private EmployeeFamilyService employeeFamilyService;

    @Autowired
    private EmployeeLanguageService employeeLanguageService;

    @Autowired
    private EmployeeSkillService employeeSkillService;

    @Autowired
    private EmployeeTrainingService employeeTrainingService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private HrDBConnectInfo hrDBConnectInfo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(EmployeeEO entity) throws BusinessException {
        logger.info("======== EmployeeServiceImpl.save() ========");
        // 生成编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("hr_employee", entity);
        AssertUtils.isBlank(code);
        entity.setEmployeeNo(code);

        // 保存员工信息
        this.baseMapper.insert(entity);

        // 保存任职信息
        EmployeePositionEO position = new EmployeePositionEO();
        position.setEmployeeId(entity.getEmployeeId());
        position.setOrgId(entity.getOrgId());
        position.setPositionId(entity.getPositionId());
        position.setPostType(1);
        position.setStartDate(entity.getAppointDate());
        position.setEndDate(DateUtils.stringToDate("9999-12-31", DateUtils.DATE_PATTERN));

        this.employeePositionService.save(position);

        // 保存工作经历
        if (entity.getExperiences().size() > 0){
            for (EmployeeExperienceEO experience : entity.getExperiences()){
                experience.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeExperienceService.saveBatch(entity.getExperiences());
        }

        // 保存教育信息
        if (entity.getEducations().size() > 0){
            for (EmployeeEducationEO education : entity.getEducations()){
                education.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeEducationService.saveBatch(entity.getEducations());
        }

        // 保存家庭信息
        if (entity.getFamilys().size() > 0){
            for (EmployeeFamilyEO family : entity.getFamilys()){
                family.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeFamilyService.saveBatch(entity.getFamilys());
        }

        // 保存语言信息
        if (entity.getLanguages().size() > 0){
            for (EmployeeLanguageEO language : entity.getLanguages()){
                language.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeLanguageService.saveBatch(entity.getLanguages());
        }

        // 保存技能信息
        if (entity.getSkills().size() > 0){
            for (EmployeeSkillEO skill : entity.getSkills()){
                skill.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeSkillService.saveBatch(entity.getSkills());
        }

        // 保存培训信息
        if (entity.getTrainings().size() > 0){
            for (EmployeeTrainingEO training : entity.getTrainings()){
                training.setEmployeeId(entity.getEmployeeId());
            }

            this.employeeTrainingService.saveBatch(entity.getTrainings());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = EmployeeEO.class)
    public boolean removeById(Serializable id) throws BusinessException {
        logger.info("======== EmployeeServiceImpl.removeById() ========");
        // 获取员工信息
        EmployeeEO employee = this.baseMapper.selectById(id);
        if (!"0".equals(employee.getStatus())){
            throw new BusinessException("员工已经生效，不能删除!");
        }

        // 删除任职信息
        this.employeePositionService.removeByEmployee((Long) id);

        // 删除工作经历
        this.employeeExperienceService.removeByEmployee((Long) id);

        // 删除教育信息
        this.employeeEducationService.removeByEmployee((Long) id);

        // 删除家庭信息
        this.employeeFamilyService.removeByEmployee((Long) id);

        // 删除语言信息
        this.employeeLanguageService.removeByEmployee((Long) id);

        // 删除技能信息
        this.employeeSkillService.removeByEmployee((Long) id);

        // 删除培训信息
        this.employeeTrainingService.removeByEmployee((Long) id);

        return super.removeById(id);
    }

    public void enableEmployee(Long employeeId) {
        logger.info("======== EmployeeServiceImpl.enableEmployee() ========");

        this.baseMapper.enableEmployee(employeeId);

        // 组织业务日志对象
        BizLogEO bizLog = new BizLogEO();
        bizLog.setOptEntity("hr_employee");
        bizLog.setEntityId(employeeId.toString());

        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        String realName = user.getRealName();

        bizLog.setUserName(userName);
        bizLog.setOperation("由" + realName + "["+ userName +"]" + " 启用 ");
        bizLog.setCreatedBy(userName);
        bizLog.setCreatedTime(new Date());

        this.bizLogService.save(bizLog);
    }

    public Pagination selectPage(Map map){
        int pageSize = Integer.parseInt(map.get("size").toString());
        List<Map> list = employeeMapper.getEmployeeList(map);
        int count = employeeMapper.getEmployeeListCount(map);
        return Pagination.getPagination(list,count,pageSize);
    }


    public Pagination selectWorkingGroupEmployeePage(Map map){
        int pageSize = Integer.parseInt(map.get("size").toString());
        List<Map> list = employeeMapper.getEmployeeList(map);
        int count = employeeMapper.getEmployeeListCount(map);
        return Pagination.getPagination(list,count,pageSize);
    }


    public List<EmployeeEO> listAll(Long userId) {
        return this.baseMapper.selectAll(userId);
    }


    /**
     * 同步hr系统数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result syncHR(){
        String selectSql = "select * from hr_employee";
        List<Map<String,Object>> list = SqlActuator.excuteQuery(selectSql,hrDBConnectInfo);
        if(list!=null && list.size()>0){
            Set ids = new HashSet();
            for(int i = 0 ; i < list.size(); i++){
                Map<String,Object> temp = list.get(i);
                Long employee_id = (Long)temp.get("employee_id");
                String employee_no = (String)temp.get("employee_no");
                String employee_name = (String)temp.get("employee_name");
                String employee_code = (String)temp.get("employee_code");
                Long org_id = (Long)temp.get("org_id");
                Long position_id = (Long)temp.get("position_id");
                String gender = (String)temp.get("gender");
                String employ_type = (String)temp.get("employ_type");
                String contract_type = (String)temp.get("contract_type");
                Integer inservice = (Integer)temp.get("inservice");
                Long root_org_id = (Long)temp.get("root_org_id");
                ids.add(employee_id);
                EmployeeEO employeeEO = new EmployeeEO();
                employeeEO.setEmployeeId(employee_id);
                employeeEO.setEmployeeNo(employee_code);
                employeeEO.setEmployeeName(employee_name);
                employeeEO.setOrgId(org_id);
                employeeEO.setPositionId(position_id);
                employeeEO.setGender(gender);
                employeeEO.setEmployType(employ_type);
                employeeEO.setContractType(contract_type);
                employeeEO.setInservice(inservice);
                employeeEO.setRootOrgId(root_org_id);
                this.saveOrupdate(employeeEO);
            }
            //删除数据
            Map param = new HashMap();
            param.put("ids",ids);
            this.baseMapper.deleteByIds(param);

        }
        return new Result().ok(true);
    }

    public boolean saveOrupdate(EmployeeEO employeeEO){
        if(employeeEO.getEmployeeId() ==448){
            int a=1;
        }
        EmployeeEO old = this.baseMapper.selectByIdOne(employeeEO.getEmployeeId());
        boolean res = false;
        if(old!=null){
            old.setEmployeeNo(employeeEO.getEmployeeNo());
            old.setEmployeeName(employeeEO.getEmployeeName());
            old.setOrgId(employeeEO.getOrgId());
            old.setPositionId(employeeEO.getPositionId());
            old.setGender(employeeEO.getGender());
            old.setEmployType(employeeEO.getEmployType());
            old.setContractType(employeeEO.getContractType());
            old.setInservice(employeeEO.getInservice());
            old.setRootOrgId(employeeEO.getRootOrgId());
            res = this.updateById(old);
        }else{
            CommonUtil.setFill(employeeEO);
            int count = this.baseMapper.insertByHR(employeeEO);
            if(count>0){
                res = true;
            }
        }
        return res;
    }
}
