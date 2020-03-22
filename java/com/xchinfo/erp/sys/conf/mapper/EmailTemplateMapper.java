package com.xchinfo.erp.sys.conf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.conf.entity.EmailTemplateEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmailTemplateMapper extends BaseMapper<EmailTemplateEO> {
    /**
     * 更改状态
     *
     * @return
     */
    @Update("update bsc_email_template  set status = #{status} where email_template_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);

}
