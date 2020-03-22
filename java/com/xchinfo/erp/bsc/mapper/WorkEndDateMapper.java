package com.xchinfo.erp.bsc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.WorkEndDateEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface WorkEndDateMapper extends BaseMapper<WorkEndDateEO> {
    /**
     * 根据日期查询数据
     * @return
     */
    @Select("select * from bsc_work_end_date " +
            "where work_end_date = #{workEndDate}")
    WorkEndDateEO selectByWorkEndDate(String workEndDate);

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update bsc_work_end_date  set status = #{status} where work_end_date_id =#{id}")
    boolean updateStatusById(@Param("status") int status, @Param("id") Long id);
}
