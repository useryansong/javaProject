package com.xchinfo.erp.sys.conf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.conf.entity.AttachmentEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhongye
 * @date 2019/5/10
 */
@Mapper
public interface AttachmentMapper  extends BaseMapper<AttachmentEO> {

    @Select("select * from sys_attachment where attachment_id = #{attachmentId}")
    AttachmentEO getById(Long attachmentId);
}
