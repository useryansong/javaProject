package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.entity.EmailSendEO;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yecat.core.utils.DateUtils;
import org.yecat.core.utils.StringUtils;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.*;
import javax.mail.internet.MimeMessage;
@Service
public class EmailService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Lazy
    private EmailSendService emailSendService;

    @Autowired
    private EmailTemplateService emailTemplateService;

    private JavaMailSenderImpl getJavaMailSender(EmailTemplateEO emailTemplateEO) {
        // 获取邮箱发送实例
        String host = emailTemplateEO.getHost();
        String username = emailTemplateEO.getUsername();
        String password = emailTemplateEO.getPassword();
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost(host);
        javaMailSenderImpl.setPort(StringUtils.isEmpty(emailTemplateEO.getPort())?465:Integer.parseInt(emailTemplateEO.getPort()));
        javaMailSenderImpl.setUsername(username);
        javaMailSenderImpl.setPassword(password);
        javaMailSenderImpl.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        javaMailSenderImpl.setJavaMailProperties(properties);
        return javaMailSenderImpl;
    }

    public boolean sendSimpleMail(EmailSendEO emailSend,EmailTemplateEO emailTemplateEO) {
        logger.info("简单邮件开始发送");
        try {
            JavaMailSenderImpl javaMailSender = getJavaMailSender(emailTemplateEO);
            String username = javaMailSender.getUsername();
            logger.info("当前发送邮箱: " + username);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(emailSend.getReciverEmail());
            if(StringUtils.isNotEmpty(emailSend.getCopyAddress())){
                message.setCc(emailSend.getCopyAddress());
            }
            message.setSubject(emailSend.getTitle());
            message.setText(emailSend.getContent());
            javaMailSender.send(message);
            logger.info("简单邮件发送成功");
            return true;
        } catch (Exception e) {
            logger.error("简单邮件发送异常", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendHtmlMail(EmailSendEO emailSend,EmailTemplateEO emailTemplateEO) {
        logger.info("HTML邮件开始发送");
        try {
            JavaMailSenderImpl javaMailSender = getJavaMailSender(emailTemplateEO);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            String username = javaMailSender.getUsername();
            logger.info("当前发送邮箱: " + username);
            messageHelper.setFrom(username);
            messageHelper.setTo(emailSend.getReciverEmail());
            if(StringUtils.isNotEmpty(emailSend.getCopyAddress())){
                messageHelper.setCc(emailSend.getCopyAddress());
            }
            messageHelper.setSubject(emailSend.getTitle());
            messageHelper.setText(emailSend.getContent(), true);
            javaMailSender.send(message);
            logger.info("HTML邮件发送成功");
            return true;
        } catch (Exception e) {
            logger.error("HTML邮件发送异常", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 内容替换
     * @param template
     * @param param
     * @return
     */
    public String templateReplace(String template , Map param){
        Set keySet = param.keySet();
        Iterator iterator= keySet.iterator();
        while (iterator.hasNext()){
            String item = (String)iterator.next();
            String value = (String)param.get(item);
            if (template.indexOf("R"+item+"R")!=-1){
                template = template.replaceAll("R"+item+"R",value);
            }
        }
        return template;
    }

    /**
     * 定时任务
     */
//    @Scheduled(cron="0 0/5 * * * ?")
    //@Scheduled(cron="0/15 * * * * ?")
    private void emailSendTask(){
        logger.info("===========邮件发送任务开始执行==========");
        Criteria criteria = new Criteria();
        criteria.setCurrentPage(1);
        criteria.setSize(999999999);
        criteria.setOrder("desc");
        criteria.setOrderField("email_send_id");
        Criterion criterion = new Criterion();
        criterion.setField("t.status in (0,2) and 1");
        criterion.setOp("eq");
        criterion.setValue("1");
        criteria.getCriterions().add(criterion);
        Criterion criterion2 = new Criterion();
        criterion2.setField("t.estimated_delivery_time");
        criterion2.setOp("le");
        criterion2.setValue(DateUtils.getDateTime());
        criteria.getCriterions().add(criterion2);
        List<EmailSendEO> list = emailSendService.selectPage(criteria).getRecords();
        if(list!=null&&list.size()>0){
            for(EmailSendEO emailSend : list){
                Long templateId = emailSend.getEmailTemplateId();
                EmailTemplateEO emailTemplateEO = emailTemplateService.getById(templateId);
                boolean res = sendHtmlMail(emailSend,emailTemplateEO);
                if(res){
                    //发送成功
                    logger.info("===========邮件发送成功==========");
                    emailSend.setActualDeliveryTime(new Date());
                    emailSendService.sendSeccess(emailSend);
                }else{
                    //发送失败
                    logger.info("===========邮件发送失败==========");
                    emailSend.setStatus(2);
                    if(emailSend.getErrorcount()>=4){
                        emailSend.setStatus(3);
                    }
                    emailSendService.sendFail(emailSend);
                }
            }
        }
        logger.info("===========邮件发送任务执行完毕==========");
    }
}
