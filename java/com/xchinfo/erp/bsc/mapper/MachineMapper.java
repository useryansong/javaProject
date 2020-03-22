package com.xchinfo.erp.bsc.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.bsc.entity.MachineEO;

/**
 * @author roman.li
 * @date 2019/3/7
 * @update
 */
@Mapper
public interface MachineMapper extends BaseMapper<MachineEO> {

    /**
     * 查找所有有效设备
     *
     * @return
     */
    @Select("select m.* from bsc_machine m inner join sys_org o on m.org_id = o.org_id " +
            //"        INNER JOIN sys_role_org z on z.org_id = o.org_id " +
            "        inner join v_user_perm_org x on x.org_id = o.org_id  where m.status = 1 and x.user_id = #{userId}")
    List<MachineEO> selectAll(Long userId);

    /**
     * 更改状态
     *
     * @return
     */
    @Update("update bsc_machine  set status = #{status} where machine_id =#{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") int status);

    /**
     * 查询设备名称是否存在
     *
     * @return
     */
    Integer selectCountByName(MachineEO machineEO);

	List<MachineEO> getStopMachineList(Map<String, Object> map);

	List<MachineEO> getCallMachineList(Map<String, Object> tmp);

	List<MachineEO> getRunMachineList(Map<String, Object> condition);

	List<MachineEO> getDebugMachineList(Map<String, Object> map);

/*    *//**
     * 查询设备所有数据
     *
     * @return
     *//*
    @Select("select * from bsc_machine")
    List<MachineEO> machineinfo();*/



}
