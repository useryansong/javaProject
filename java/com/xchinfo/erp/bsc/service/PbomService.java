package com.xchinfo.erp.bsc.service;


import com.xchinfo.erp.bsc.entity.MaterialEO;
import com.xchinfo.erp.bsc.entity.PbomEO;
import com.xchinfo.erp.bsc.mapper.PbomMapper;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.Result;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PbomService extends BaseServiceImpl<PbomMapper, PbomEO> {



    //导入PBOM数据
    @Transactional(rollbackFor = Exception.class)
    public Result importExcel(List list, UserEO userEO) throws BusinessException {

        List<PbomEO> saveList = new ArrayList<>();
        String errorMsg = "";

        if(list!=null){
            if(list.size()>0) {
                List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
                if(mapList!=null && mapList.size()>1){
                    Integer rowTotal = mapList.size();

                    //先做物料的校验
                    //现获取当前机构所有物料信息
                    List<MaterialEO> allMaterial = this.baseMapper.selectAllMaterial(userEO.getOrgId());
                    Map<String,MaterialEO> allMap = new HashMap<>();


                    //把所有机构下的物料放进Map
                    for(MaterialEO materialEO:allMaterial){
                        allMap.put(materialEO.getElementNo(),materialEO);
                    }

                    //校验主物料MAP
                    Map<Integer,MaterialEO> mainMap = new HashMap<>();
                    //主物料零件号
                    Map mainElementNoMap = mapList.get(1);
                    Map mainProjectNoMap = mapList.get(2);
                    if(null != mainElementNoMap && mainElementNoMap.size() > 7){
                        for(int i=7;i<mainElementNoMap.size();i++){
                            String temp = (String) mainElementNoMap.get(i+"");
                            if(null != temp && !temp.isEmpty()){
                                //校验是否存在
                                if (null!=allMap.get(temp)){
                                    //先设置项目号
                                    allMap.get(temp).setProjectNo((String) mainProjectNoMap.get(i+""));
                                    mainMap.put(i,allMap.get(temp));
                                }else{
//                                    errorMsg = errorMsg + "第 ["+(i+1)+"] 列：主物料零件号 ["+temp+"] 在用户所属机构对应物料不存在\n";
                                    MaterialEO materialEO = new MaterialEO();
                                    materialEO.setElementNo(temp);
                                    materialEO.setProjectNo((String) mainProjectNoMap.get(i+""));
                                    materialEO.setMaterialName("");
                                    materialEO.setMaterialId(0l);
                                    mainMap.put(i,materialEO);
                                }
                            }else{
                                errorMsg = errorMsg + "第 ["+(i+1)+"] 列：总成零件号为空\n";
                                continue;
                            }
                        }
                    }

                    //校验子物料
                    for (int i=3;i<rowTotal;i++){
                        Map map = mapList.get(i);
                        String elementNo = (String) map.get("1");
                        String materialName = (String) map.get("5");
                        if(null != elementNo && !elementNo.isEmpty()){
                            //校验子物料是否存在
                            if(null!=allMap.get(elementNo)){
                                //若已有错误，则无需构造数据了
                                if (errorMsg.isEmpty()){
                                    //遍历单元格，数字大于0的数据构造出来
                                    for(int j=7;j<map.size();j++){
                                        String amount = (String) map.get(j+"");
                                        if(null != amount && !amount.isEmpty() && Double.valueOf(amount) > 0d){
                                            PbomEO pbomEO = new PbomEO();
                                            pbomEO.setMaterialId(mainMap.get(j).getMaterialId());
                                            pbomEO.setMaterialName(mainMap.get(j).getMaterialName());
                                            pbomEO.setElementNo(mainMap.get(j).getElementNo());
                                            pbomEO.setProjectNo(mainMap.get(j).getProjectNo());
                                            pbomEO.setChildMaterialId(allMap.get(elementNo).getMaterialId());
                                            pbomEO.setChildMaterialName(allMap.get(elementNo).getMaterialName());
                                            pbomEO.setChildElementNo(allMap.get(elementNo).getElementNo());
                                            pbomEO.setAmount(Double.valueOf(amount));
                                            pbomEO.setStatus(1);
                                            pbomEO.setOrgId(userEO.getOrgId());
                                            saveList.add(pbomEO);
                                        }
                                    }
                                }
                            }else{

                                //若已有错误，则无需构造数据了
                                if (errorMsg.isEmpty()){
                                    //遍历单元格，数字大于0的数据构造出来
                                    for(int j=7;j<map.size();j++){
                                        String amount = (String) map.get(j+"");
                                        if(null != amount && !amount.isEmpty() && Double.valueOf(amount) > 0d){
                                            PbomEO pbomEO = new PbomEO();
                                            pbomEO.setMaterialId(mainMap.get(j).getMaterialId());
                                            pbomEO.setMaterialName(mainMap.get(j).getMaterialName());
                                            pbomEO.setElementNo(mainMap.get(j).getElementNo());
                                            pbomEO.setProjectNo(mainMap.get(j).getProjectNo());
                                            pbomEO.setChildMaterialId(0l);
                                            pbomEO.setChildMaterialName(materialName);
                                            pbomEO.setChildElementNo(elementNo);
                                            pbomEO.setAmount(Double.valueOf(amount));
                                            pbomEO.setStatus(1);
                                            pbomEO.setOrgId(userEO.getOrgId());
                                            saveList.add(pbomEO);
                                        }
                                    }
                                }

                            }
                        }else{
                            errorMsg = errorMsg + "第 ["+(i+1)+"] 行：子物料零件号为空\n";
                            continue;
                        }
                    }

                    logger.info("========数据量的大小为 ========"+saveList.size());


                    //若不存在错误信息，则先删除主物料已存在的PBOM信息，再插入
                    if(errorMsg.isEmpty() && saveList.size() > 0){
                        String strSql = "";
                        //先删除已存在的主物料信息的数据
                        for (int i=0;i<saveList.size();i++){
                            if("".equals(strSql)){
                                strSql = "('"+saveList.get(i).getElementNo()+"'";
                            }else{
                                strSql = strSql + ",'"+saveList.get(i).getElementNo()+"'";
                            }
                        }
                        strSql = strSql + ")";
                        this.baseMapper.deleteByMainMaterilId(strSql,userEO.getOrgId());

                        //删除后做批量新增
//                        super.saveBatch(saveList,1000);

                        this.baseMapper.addBatch(saveList);
                    }
                }
            }else{//5
                throw new BusinessException("请确认文件有内容！");
            }
        }else{
            throw new BusinessException("服务器解析文件出错！");
        }

        Result result =  new Result();
        if(!errorMsg.isEmpty()){
            result.setCode(100);
            result.setMsg(errorMsg);
        }

        return result;
    }



}
