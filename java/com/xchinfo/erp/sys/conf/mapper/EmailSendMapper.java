package com.xchinfo.erp.sys.conf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.conf.entity.EmailSendEO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailSendMapper extends BaseMapper<EmailSendEO> {

    int sendSeccess(EmailSendEO emailSendEO);

    int sendFail(EmailSendEO emailSendEO);

}
