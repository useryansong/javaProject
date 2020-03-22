package com.xchinfo.erp.sys.conf.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.sys.conf.entity.FileUploadEO;
import com.xchinfo.erp.sys.conf.mapper.FileUploadMapper;
import org.springframework.stereotype.Service;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import org.yecat.mybatis.utils.TreeUtils;

import java.util.List;

@Service
public class FileUploadService  extends BaseServiceImpl<FileUploadMapper, FileUploadEO> {


    public List<FileUploadEO> selectTreeList(Integer type) {
        // 查询所有菜单
        List<FileUploadEO> treeList = this.list(
                new QueryWrapper<FileUploadEO>()
                        .eq(null != type, "type", type));

        // 构建树形结构
        return TreeUtils.build(treeList, Long.valueOf(0));
    }

}
