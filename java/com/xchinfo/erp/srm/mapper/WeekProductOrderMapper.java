package com.xchinfo.erp.srm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MachineEO;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.scm.srm.entity.ProductOrderEO;
import com.xchinfo.erp.scm.srm.entity.ScheduleOrderEO;
import com.xchinfo.erp.scm.srm.entity.WeekProductOrderEO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongy
 * @date 2019/9/2
 */
@Mapper
public interface WeekProductOrderMapper extends BaseMapper<WeekProductOrderEO> {


}
