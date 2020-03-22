package com.xchinfo.erp.sys.org.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.bsc.entity.SupplierEO;
import com.xchinfo.erp.bsc.service.SupplierService;
import com.xchinfo.erp.common.HrDBConnectInfo;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import com.xchinfo.erp.sys.org.mapper.OrgMapper;
import com.xchinfo.erp.utils.CommonUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.utils.JsonUtil;
import org.yecat.core.utils.Result;
import org.yecat.core.utils.StringUtils;
import org.yecat.core.validator.AssertUtils;
import org.yecat.mybatis.service.impl.BaseServiceImpl;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import org.yecat.mybatis.utils.TreeUtils;
import org.yecat.mybatis.utils.jdbc.SqlActuator;

import java.io.Serializable;
import java.util.*;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@Service
public class OrgService extends BaseServiceImpl<OrgMapper, OrgEO> {

	@Autowired
	private OrgMapper orgMapper;
	
    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private HrDBConnectInfo hrDBConnectInfo;


    public List<OrgEO> selectTreeList(Map<String, Object> map) throws BusinessException {
        List<OrgEO> orgs = this.baseMapper.getList(new QueryWrapper<>());

        return TreeUtils.build(orgs, Long.valueOf(0));
    }

    public List<OrgEO> selectOrgTreeList(Map<String, Object> map) throws BusinessException {
        List<OrgEO> orgs = this.baseMapper.selectOrgTreeList(1);// 只查询机构数据，类型为1

        return TreeUtils.build(orgs, Long.valueOf(0));
    }

    public List<OrgEO> selectTreeSelectList(Map<String, Object> map) throws BusinessException {
        List<OrgEO> orgs = this.baseMapper.selectTreeList();

        return TreeUtils.build(orgs, Long.valueOf(0));
    }


    public OrgEO getById(Serializable id) {
        return this.baseMapper.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.CREATE)
    public boolean save(OrgEO org) throws BusinessException {
        // 生成业务编码
        String code = this.businessCodeGenerator.generateNextCodeNoOrgId("sys_org", org);
        AssertUtils.isBlank(code);
        org.setOrgCode(code);

        // 检查编码是否已经被使用
        if (retBool(baseMapper.countByOrgCode(org.getOrgCode()))){
            throw new BusinessException("机构编码已被使用，请重新输入!");
        }

        return super.save(org);
    }

    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(value = BusinessLogType.DELETE, entityClass = OrgEO.class)
    public boolean removeById(Serializable id) throws BusinessException {
        // 检查机构是否已被使用
        if (retBool(baseMapper.countUserByOrg((Long) id))){
            throw new BusinessException("机构已经被使用不能删除");
        }

        if (retBool(baseMapper.countByParentOrg((Long) id))){
            throw new BusinessException("机构已经被使用不能删除");
        }

        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @EnableBusinessLog(BusinessLogType.UPDATE)
    public boolean updateById(OrgEO entity) throws BusinessException {
        return super.updateById(entity);
    }

    public Boolean checkUserPermissions(Long orgId, Long userId) {

        Integer count = this.baseMapper.checkUserPermissions(orgId,userId);
        if(count > 0){
            return  true;
        }

        return false;
    }

    public void checkUserPermissions(Long orgId, Long userId, String msg) throws BusinessException {
        Boolean flag = checkUserPermissions(orgId, userId);
        if(!flag.booleanValue()) {
            throw new BusinessException(msg);
        }
    }

    public List<OrgEO> selectPermissionsTreeSelectList(Long userId) throws BusinessException {

        List<OrgEO> orgs = this.baseMapper.selectPermissionsTreeSelectList(userId);

        OrgEO orgEO = this.baseMapper.selectById(orgs.get(0).getParentOrgId());
        Long rootId = Long.valueOf(orgs.get(0).getParentOrgId());
        if(null == orgEO){
            rootId = Long.valueOf(orgs.get(0).getOrgId());
        }

        return TreeUtils.build(orgs, rootId);
    }

    public List<OrgEO> selectPermissionsTreeSelectListAll(Long userId) throws BusinessException {

        List<OrgEO> orgs = this.baseMapper.selectPermissionsTreeSelectListAll(userId);

        return TreeUtils.build(orgs);
    }
    public List<OrgEO> selectPermissionsTreeSelectListAllNew(Long userId) throws BusinessException {

        List<OrgEO> orgs = this.baseMapper.selectPermissionsTreeSelectListAllNew(userId);

        return TreeUtils.build(orgs);
    }

    public List<OrgEO> getSonOrgList(Long orgId) throws BusinessException {
        List<OrgEO> sonOrgs =new ArrayList<OrgEO>();
        List<OrgEO> orgs = getSonBoms(orgId, sonOrgs);

        return TreeUtils.build(orgs);
    }

    // 通过机构父节点实体获取该父节点其下所有的子节点清单数据
    private List<OrgEO> getSonBoms(Long orgId, List<OrgEO> sonOrgs){
        List<OrgEO> orgsTemp = this.baseMapper.getSonOrgList(orgId);
        if(orgsTemp!=null && orgsTemp.size()>0){
            for(int i=0; i<orgsTemp.size(); i++){
                sonOrgs.add(orgsTemp.get(i));
                sonOrgs = getSonBoms(orgsTemp.get(i).getOrgId(), sonOrgs);
            }
        }

        return sonOrgs;
    }

    public List<OrgEO> selectUserOrg(Long userId) throws BusinessException {
        return this.baseMapper.selectUserOrg(userId);
    }

    public List<OrgEO> listAll() {
        return this.baseMapper.listAll();
    }

    public List<OrgEO> selectBySupplier() {
        List<OrgEO> orgs = this.baseMapper.selectBySupplier();
        return TreeUtils.build(orgs, Long.valueOf(1));
    }

    public List<OrgEO> selectBySupplierDeliveryNote(String supplierCode) {
        SupplierEO supplier = this.supplierService.getBySupplierCode(supplierCode);
        List<OrgEO> orgs;
        if(supplier == null) {
            orgs = new ArrayList<>();
        } else {
            orgs = this.baseMapper.selectBySupplierDeliveryNote(supplier.getSupplierId());
        }
        return TreeUtils.build(orgs, Long.valueOf(1));
    }

    private List<OrgEO> getParentOrg(OrgEO org, List<OrgEO> list) {
        OrgEO parentOrg = this.baseMapper.getById(org.getParentOrgId());
        if(parentOrg != null) {
            list.add(parentOrg);
            getParentOrg(parentOrg, list);
        }

        return list;
    }

    public String getCompleteOrgName(Long orgId) {
        String completeOrgName = "";
        if(orgId == null) {
            return completeOrgName;
        }
        OrgEO org = this.getById(orgId);
        List<OrgEO> list = new ArrayList<>();
        list.add(org);
        list = getParentOrg(org, list);
        Collections.reverse(list);
        for(int i=0; i<list.size(); i++) {
            completeOrgName += (list.get(i).getOrgName() + "-");
        }
        if(!"".equals(completeOrgName)) {
            return completeOrgName.substring(0, completeOrgName.length()-1);
        } else {
            return completeOrgName;
        }
    }


    /**
     * 同步hr系统数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result syncHR(){
        String selectSql = "select * from sys_org";
        List<Map<String,Object>> list = SqlActuator.excuteQuery(selectSql,hrDBConnectInfo);
        if(list!=null && list.size()>0){
            Set ids = new HashSet();
            for(int i = 0 ; i < list.size(); i++){
                Map<String,Object> temp = list.get(i);
                Long org_id = (Long)temp.get("org_id");
                String org_code = (String)temp.get("org_code");
                String org_name = (String)temp.get("org_name");
                Long parent_org_id = (Long)temp.get("parent_org_id");
                String description = (String)temp.get("description");
                Integer status = (Integer)temp.get("status");
                String sort_no = (String)temp.get("sort_no");
                Integer org_type = (Integer)temp.get("org_type");
                ids.add(org_id);
                OrgEO orgEO = new OrgEO();
                orgEO.setOrgId(org_id);
                //orgEO.setOrgCode(org_code);
                orgEO.setOrgName(org_name);
                orgEO.setParentOrgId(parent_org_id);
                orgEO.setDescription(description);
                orgEO.setStatus(status);
                orgEO.setSortNo(sort_no);
                orgEO.setOrgType(org_type);
                this.saveOrupdate(orgEO);
            }
            //删除数据
            Map param = new HashMap();
            param.put("ids",ids);
            this.baseMapper.deleteByIds(param);

        }
        return new Result().ok(true);
    }

    public boolean saveOrupdate(OrgEO orgEO){
        OrgEO old = this.baseMapper.selectByIdOne(orgEO.getId());
        boolean res = false;
        if(old!=null){
            old.setOrgCode(orgEO.getOrgCode());
            old.setOrgName(orgEO.getOrgName());
            old.setParentOrgId(orgEO.getParentOrgId());
            old.setDescription(orgEO.getDescription());
            old.setStatus(orgEO.getStatus());
            old.setSortNo(orgEO.getSortNo());
            old.setOrgType(orgEO.getOrgType());
            res = this.updateById(old);
        }else{
            Object obj = (Object) SecurityUtils.getSubject().getPrincipal();
            Map user = JsonUtil.fromJson(JsonUtil.toJson(obj),Map.class);
            String userName = "";
            String name = "";
            String auditName ="";
            if(user!=null){
                userName = user.get("userName").toString();
                name = user.get("realName").toString();
                auditName = "[" + userName + "]" + name;
            }
            orgEO.setCreatedBy(auditName);
            orgEO.setLastModifiedBy(auditName);
            orgEO.setCreatedTime(new Date());
            orgEO.setLastModifiedTime(new Date());
            int count = this.baseMapper.insertByHR(orgEO);
            if(count>0){
                res = true;
            }
        }
        return res;
    }

    /**
     * 设置机构安全生产起始日
     * @param map
     * @return
     */
    public Result setStart(Map map){
        if(StringUtils.isEmpty(map.get("orgId").toString())){
            return new Result().error("组织机构不能为空");
        }
        if(StringUtils.isEmpty(map.get("startDate").toString())){
            return new Result().error("起始日期不能为空");
        }
        Long orgId = Long.parseLong(map.get("orgId").toString());
        String startDate = (String)map.get("startDate");
        int res = this.baseMapper.setStartSafeProductionDate(startDate,orgId);
        if(res==0){
            return new Result().error("保存失败");
        }
        return new Result().ok("保存成功");
    }

    /**
     *  首页报表，安全生产日
     * @return
     */
    public Map selectSafeProductionDays(){
        List<Map> list = this.baseMapper.selectSafeProductionDays();
        list.get(list.size()-1).put("days",list.get(list.size()-2).get("days"));//集团安全生产日为八家子工厂中最短的
        Map res = new HashMap();
        String[] yAxisData=new String[list.size()];
        Integer[] seriesData=new Integer[list.size()];
        String textname = "安全无事故天数";
        for(int i=0;i<list.size();i++){
            Map temp = list.get(i);
            yAxisData[i] = temp.get("org_name").toString();
            seriesData[i] = Integer.parseInt(temp.get("days").toString());
        }
        res.put("textname",textname);
        res.put("yAxisData",yAxisData);
        res.put("seriesData",seriesData);
        return res;
    }

}
