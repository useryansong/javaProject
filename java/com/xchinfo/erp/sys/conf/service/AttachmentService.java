package com.xchinfo.erp.sys.conf.service;

import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import com.xchinfo.erp.sys.conf.mapper.AttachmentMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

/**
 * @author zhongye
 * @date 2019/5/10
 */
@Service
public class AttachmentService extends BaseServiceImpl<AttachmentMapper, AttachmentEO> {

    public AttachmentEO getById(Long attachmentId) {
        return this.baseMapper.getById(attachmentId);
    }
}
