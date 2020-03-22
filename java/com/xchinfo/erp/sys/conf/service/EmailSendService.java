package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.entity.EmailSendEO;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import com.xchinfo.erp.sys.conf.mapper.EmailSendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.Date;

@Service
public class EmailSendService extends BaseServiceImpl<EmailSendMapper, EmailSendEO>{

    @Autowired
    @Lazy
    private EmailService emailService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    public int sendSeccess(EmailSendEO emailSendEO){
        return this.baseMapper.sendSeccess(emailSendEO);
    }

    public int sendFail(EmailSendEO emailSendEO){
        return this.baseMapper.sendFail(emailSendEO);
    }

    public boolean trySend(Long emailSendId){
        EmailSendEO emailSendEO = this.getById(emailSendId);
        Long templateId = emailSendEO.getEmailTemplateId();
        EmailTemplateEO emailTemplateEO = emailTemplateService.getById(templateId);
        boolean res = emailService.sendHtmlMail(emailSendEO,emailTemplateEO);
        if(res){
            //发送成功
            logger.info("===========邮件发送成功==========");
            emailSendEO.setActualDeliveryTime(new Date());
            this.sendSeccess(emailSendEO);
        }else{
            //发送失败
            logger.info("===========邮件发送失败==========");
            emailSendEO.setStatus(2);
            if(emailSendEO.getErrorcount()>=4){
                emailSendEO.setStatus(3);
            }
            this.sendFail(emailSendEO);
        }
        return  res;
    }
}
