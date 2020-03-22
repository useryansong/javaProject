package com.xchinfo.erp.sys.conf.mapper;

import com.xchinfo.erp.sys.conf.entity.CodeRuleEO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@Mapper
public interface CodeRuleMapper extends BaseMapper<CodeRuleEO> {

    /**
     * 查找对应code的数据条数
     *
     * @param code
     * @return
     */
    @Select("select count(1) from sys_code_rule where rule_code = #{code}")
    int selectCountByCode(String code);

    @Select("select * from sys_code_rule where rule_code = #{code} and org_id=#{orgId}")
    CodeRuleEO selectByIdAndOrgId(@Param("code")String code,@Param("orgId")Long orgId);

//    @Select("select max(left(${colName}, #{seqLength})) from ${tableName} where org_id=#{orgId}")
//    String selectMaxCode(@Param("tableName") String tableName, @Param("colName") String colName,
//                         @Param("seqLength") int seqLength,@Param("orgId")Long orgId);
//
//    @Select("select max(right(${colName}, #{seqLength})) " +
//            "from ${tableName} " +
//            "where left(${colName}, #{prefixLength}) = #{prefix} " +
//            "and length(${colName}) = #{codeLength} and org_id=#{orgId}")
//    String selectMaxCodeWithPrefix(@Param("tableName") String tableName, @Param("colName") String colName,
//                                   @Param("seqLength") int seqLength, @Param("prefixLength") int prefixLength,
//                                   @Param("codeLength") int codeLength, @Param("prefix") String prefix,@Param("orgId")Long orgId);

    @Select("select max(left(${colName}, #{seqLength})) from ${tableName}")
    String selectMaxCode(@Param("tableName") String tableName, @Param("colName") String colName,
                         @Param("seqLength") int seqLength);

    @Select("select max(right(${colName}, #{seqLength})) " +
            "from ${tableName} " +
            "where left(${colName}, #{prefixLength}) = #{prefix} " +
            "and length(${colName}) = #{codeLength}")
    String selectMaxCodeWithPrefix(@Param("tableName") String tableName, @Param("colName") String colName,
                                   @Param("seqLength") int seqLength, @Param("prefixLength") int prefixLength,
                                   @Param("codeLength") int codeLength, @Param("prefix") String prefix);

    @Select("select * from sys_code_rule where rule_code = #{code}")
    CodeRuleEO selectByIdAndOrgIdNoOrgId(@Param("code")String code);



    @Select("select max(left(${colName}, #{seqLength})) from ${tableName}")
    String selectMaxCodeNoOrgId(@Param("tableName") String tableName, @Param("colName") String colName,
                         @Param("seqLength") int seqLength);

    @Select("select max(right(${colName}, #{seqLength})) " +
            "from ${tableName} " +
            "where left(${colName}, #{prefixLength}) = #{prefix} " +
            "and length(${colName}) = #{codeLength}")
    String selectMaxCodeWithPrefixNoOrgId(@Param("tableName") String tableName, @Param("colName") String colName,
                                   @Param("seqLength") int seqLength, @Param("prefixLength") int prefixLength,
                                   @Param("codeLength") int codeLength, @Param("prefix") String prefix);


    @Select("select nextval(#{seqName})")
    Long getNextval(@Param("seqName") String seqName);

    @Select("set @_syncStr ='';\n" +
            "set @_status = -1;\n" +
            "Call P_Get_SeqStr(#{syncCode}, @_syncStr,@_status);\n" +
            "select @_syncStr as seqnum;")
    String getErpVoucherNo(@Param("syncCode") String syncCode);

    @Select("select sync_value from sys_sequence_info where sync_code = #{syncCode}")
    String getSyncValue(@Param("syncCode") String syncCode);


//    @Select("select current_max_code from sys_code_rule where rule_code = #{ruleCode}")
//    String getCurrentMaxCode(String ruleCode);

//    @Update("update sys_code_rule set current_max_code = #{nextCode} where rule_code = #{ruleCode}")
//    int updateCurrentMaxCode(@Param("nextCode") String nextCode, @Param("ruleCode") String ruleCode);

//    @Update("update sys_code_rule set current_max_code = (select max(${colName}) from ${tableName} where ${colName} is not null) where rule_code = #{ruleCode}")
//    int updateByTable(@Param("tableName") String tableName, @Param("colName") String columnName, @Param("ruleCode") String ruleCode);
}
