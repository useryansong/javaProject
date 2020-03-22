package com.xchinfo.erp.sys.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xchinfo.erp.sys.dict.entity.DictEO;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;

/**
 * 字典数据操作类
 *
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@Mapper
public interface DictMapper extends BaseMapper<DictEO> {

    /**
     * 查询DictCode对应的数据条数
     *
     * @param dictCode
     * @return
     */
    @Select("select count(1) from sys_dict where dict_code = #{dictCode}")
    int selectCountByDictCode(String dictCode);

    /**
     * 查询字典组下DictCode对应的数据条数
     *
     * @param typeId
     * @return
     */
    @Select("select count(1) from sys_dict where type_id = #{typeId}")
    int selectCountByDictCodeInDictGroup(@Param("typeId") Long typeId);

    /**
     * 查找字典及字典项
     *
     * @param dictCode
     * @return
     */
    @Select("select d.* " +
            "from sys_dict d " +
            "where d.type_id in (select dict_id from sys_dict where dict_code = #{dictCode}) " +
            "      and d.type = 2 and d.status=1 " +
            "order by d.order_num asc ")
    List<DictEO> selectByDictCode(String dictCode);

    @Select("select * from sys_dict " +
            "where dict_id = #{dictId} ")
    @Results(value = {
            @Result(id = true, property = "dictId", column = "dict_id"),
            @Result(property = "typeId", column = "type_id"),
            @Result(property = "dictCode", column = "dict_code"),
            @Result(property = "dictName", column = "dict_name"),
            @Result(property = "status", column = "status"),
            @Result(property = "type", column = "type"),
            @Result(property = "orderNum", column = "order_num"),
            @Result(property = "remarks", column = "remarks"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "lastModifiedTime", column = "last_modified_time"),
            @Result(property = "lastModifiedBy", column = "last_modified_by"),
            @Result(property = "version", column = "version"),
            @Result(property = "dicts", column = "dict_id",
                    many = @Many(select = "com.xchinfo.erp.sys.dict.mapper.DictMapper.selectDictsByType"))
    })
    DictEO getById(Serializable dictId);

    @Select("select * from sys_dict d " +
            "where type_id = #{typeId} ")
    List<DictEO> selectDictsByType(Long typeId);

    /**
     * 根据字典ID删除字典项
     *
     * @param typeId
     */
    @Delete("delete from sys_dict where type_id = #{typeId}")
    Integer deleteByTypeId(Long typeId);
}
