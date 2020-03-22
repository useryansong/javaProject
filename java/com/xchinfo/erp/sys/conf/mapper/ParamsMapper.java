package com.xchinfo.erp.sys.conf.mapper;

import com.xchinfo.erp.sys.conf.entity.ParamsEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 系统配置参数数据库操作类
 *
 * @author roman.li
 * @date 2017/10/12
 * @update
 */
@Mapper
public interface ParamsMapper extends BaseMapper<ParamsEO> {

    /**
     * 检查参数名是否被使用
     *
     * @param key
     * @return
     */
    @Select("select count(1) from sys_params where param_key = #{key}")
    Integer countByKey(String key);

    /**
     * 根据参数名查找有效的参数值
     *
     * @param key
     * @return
     */
    @Select("select param_value from sys_params where param_key = #{key} and status = 1")
    String getParamByKey(String key);
}
