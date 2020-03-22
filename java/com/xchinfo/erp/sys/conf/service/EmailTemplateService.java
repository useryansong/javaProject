package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.mes.entity.MaterialRequirementEO;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import com.xchinfo.erp.sys.conf.mapper.EmailTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
@Service
public class EmailTemplateService extends BaseServiceImpl<EmailTemplateMapper, EmailTemplateEO>{
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    public boolean updateStatusById(Long[] ids,int status){
        for(Long id : ids){
            this.baseMapper.updateStatusById(status,id);
        }
        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.CREATE, entityClass = EmailTemplateEO.class)
    public boolean save(EmailTemplateEO entity) throws BusinessException {

        // 生成业务编码
        String code = businessCodeGenerator.generateNextCodeNoOrgId("bsc_email_template", entity);
        AssertUtils.isBlank(code);

        entity.setTemplateCode(code);

        return super.save(entity);
    }

}
