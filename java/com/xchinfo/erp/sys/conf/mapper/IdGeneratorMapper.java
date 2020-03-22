package com.xchinfo.erp.sys.conf.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author roman.li
 * @date 2019/3/5
 * @update
 */
@Mapper
public interface IdGeneratorMapper {

    /**
     * 根据Sequence获取下一个ID
     *
     * @param seqName
     * @return
     */
    @Select("select nextval(#{seqName})")
    Long nextId(String seqName);
}
