package com.xchinfo.erp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xchinfo.erp.bsc.entity.*;
import com.xchinfo.erp.bsc.service.*;
import com.xchinfo.erp.hrms.entity.EmployeeEO;
import com.xchinfo.erp.hrms.service.EmployeeService;
import com.xchinfo.erp.mes.entity.StampingMaterialConsumptionQuotaEO;
import com.xchinfo.erp.mes.entity.WorkingProcedureTimeEO;
import com.xchinfo.erp.mes.service.StampingMaterialConsumptionQuotaService;
import com.xchinfo.erp.mes.service.WorkingProcedureTimeService;
import com.xchinfo.erp.sys.dict.entity.DictEO;
import com.xchinfo.erp.sys.dict.service.DictService;
import com.xchinfo.erp.sys.org.entity.OrgEO;
import com.xchinfo.erp.sys.org.service.OrgService;
import com.xchinfo.erp.utils.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yecat.core.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongye
 * @date 2019/7/30
 */
@RestController
@RequestMapping("bsc/sql")
public class SqlController {

    @Autowired
    private StampingMaterialConsumptionQuotaService  smcqs;

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SupplierErpCodeService supplierErpCodeService;

    @Autowired
    private CustomerErpCodeService customerErpCodeService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialCustomerService materialCustomerService;

    @Autowired
    private MaterialSupplierService materialSupplierService;

    @Autowired
    private MaterialRelationshipService materialRelationshipService;

    @Autowired
    private WarehouseAreaService warehouseAreaService;

    @Autowired
    private BomService bomService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private DictService dictService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private WorkingProcedureTimeService workingProcedureTimeService;


    private static void writeData(String path,String context) throws Exception{
        File file = new File(path);
        Writer out = new FileWriter(file, true);
        out.write(context);
        out.close();
    }

    /**
     * 生成冲压材料耗用定额表的sql
     * */
    @PostMapping("generateCyclhydeSql")
    public void generateCyclhydeSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        List<Map> newMapList = new ArrayList<>();
        int contentRowStartIndex = 3;
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        Integer id = Integer.valueOf(request.getParameter("smcqId"));// 冲压定额id初始值
        String notExistData = "";
        String insertData = "";
        if(mapList!=null && mapList.size()>0){
            for(int i=contentRowStartIndex;i < mapList.size();i++) {
                String elementNo = (String) mapList.get(i).get("2");
                String inventoryCode = (String) mapList.get(i).get("15");
                QueryWrapper<StampingMaterialConsumptionQuotaEO> wrapper = new QueryWrapper<>();
                wrapper.eq("element_no", elementNo);
                wrapper.eq("org_id", orgId);
                wrapper.eq("Inventory_coding", inventoryCode);
                StampingMaterialConsumptionQuotaEO smcq = this.smcqs.getOne(wrapper);
                if(smcq == null) {
                    newMapList.add(mapList.get(i));
                }
            }
        }


        if(newMapList!=null && newMapList.size()>0) {
            for(int j=0; j<newMapList.size(); j++) {
                id += 1;
                Map map = newMapList.get(j);

                // 项目号
                String project = map.get("1")==null?null:((String) map.get("1")).trim();
                // 零件号
                String elementNo = map.get("2")==null?null:((String) map.get("2")).trim();
                // 零件名称
                String materialName = map.get("3")==null?null:((String) map.get("3")).trim();

                QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
                wrapper.eq("element_no", elementNo);
                wrapper.eq("org_id", orgId);
                wrapper.eq("status", 1);
                MaterialEO material = this.materialService.getOne(wrapper);
                if(material == null) {
                    notExistData += (elementNo + "\t\t\t\t" + materialName + "\n");
                }

                // 材料牌号
                String materialPcode = map.get("4")==null?null:((String) map.get("4")).trim();
                // 材料规格
                String materialSpecific = map.get("5")==null?null:((String) map.get("5")).trim();
                // 料厚
                String thickness = map.get("6")==null?null:((String) map.get("6")).trim();
                if(thickness==null || thickness.equals("")) {
                    thickness = "null";
                }
                // 料宽
                String width = map.get("7")==null?null:((String) map.get("7")).trim();
                if(width==null || width.equals("")) {
                    width = "null";
                }
                // 公差
                String tolerance = map.get("8")==null?null:((String) map.get("8")).trim();
                // 卷料
                String coilMaterial = map.get("9")==null?null:((String) map.get("9")).trim();
                if(coilMaterial==null || coilMaterial.equals("") || coilMaterial.equals("/")) {
                    coilMaterial = "null";
                }
                // 板料
                String sheetMetal = map.get("10")==null?null:((String) map.get("10")).trim();
                if(sheetMetal==null || sheetMetal.equals("") || sheetMetal.equals("/")) {
                    sheetMetal = "null";
                }
                // 步距
                String stepDistance = map.get("11")==null?null:((String) map.get("11")).trim();
                if(stepDistance==null || stepDistance.equals("")) {
                    stepDistance = "null";
                }
                // 每条板可冲零件数量
                String numberOfPunchablePartsPerBoard = map.get("12")==null?null:((String) map.get("12")).trim();
                if(numberOfPunchablePartsPerBoard==null || numberOfPunchablePartsPerBoard.equals("")) {
                    numberOfPunchablePartsPerBoard = "null";
                }
                // 单个零件净重（kg）
                String netWeight = map.get("13")==null?null:((String) map.get("13")).trim();
                if(netWeight==null || netWeight.equals("")) {
                    netWeight = "null";
                } else {
                    BigDecimal nwbg = new BigDecimal(netWeight).setScale(3, RoundingMode.UP);
                    netWeight = new Double(nwbg.doubleValue()).toString();
                }
                // 单个零件毛重（kg）
                String grossWeight = map.get("14")==null?null:((String) map.get("14")).trim();
                if(grossWeight==null || grossWeight.equals("")) {
                    grossWeight = "null";
                } else {
                    BigDecimal gwbg = new BigDecimal(grossWeight).setScale(3, RoundingMode.UP);
                    grossWeight = new Double(gwbg.doubleValue()).toString();
                }
                // 存货编码
                String inventoryCoding = map.get("15")==null?null:((String) map.get("15")).trim();

                insertData += "INSERT INTO mes_stamping_material_consumption_quota " +
                        "(stamping_material_consumption_quota_id ," +
                        "org_id," +
                        "project," +
                        "material_id," +
                        "material_code," +
                        "element_no," +
                        "material_name," +
                        "material_pcode," +
                        "material_specific," +
                        "thickness," +
                        "width," +
                        "tolerance," +
                        "step_distance," +
                        "number_of_punchable_parts_per_board," +
                        "net_weight," +
                        "gross_weight," +
                        "Inventory_coding," +
                        "coil_material," +
                        "sheet_metal," +
                        "status," +
                        "version," +
                        "created_by," +
                        "created_time," +
                        "last_modified_by," +
                        "last_modified_time) " +
                        "VALUES " +
                        "("+id+"," +
                        orgId+"," +
                        (project==null?"null,":"'" + project +"',") +
                        (material==null?"null,":"'" + material.getMaterialId() +"',") +
                        (material==null?"null,":"'" + material.getMaterialCode() +"',") +
                        (elementNo==null?"null,":"'" + elementNo + "',") +
                        (materialName==null?"null,":"'" + materialName + "',") +
                        (materialPcode==null?"null,":"'" + materialPcode + "',") +
                        (materialSpecific==null?"null,":"'" + materialSpecific + "',") +
                        thickness + "," +
                        width + "," +
                        (tolerance==null?"null,":"'" + tolerance + "',") +
                        stepDistance +"," +
                        numberOfPunchablePartsPerBoard +"," +
                        netWeight + "," +
                        grossWeight + "," +
                        (inventoryCoding==null?"null,":"'" + inventoryCoding + "',") +
                        coilMaterial + "," +
                        sheetMetal + "," +
                        "0," +
                        "1," +
                        "'[admin]管理员'," +
                        "now()," +
                        "'[admin]管理员'," +
                        "now()" +
                        ");\n";
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\冲压材料耗用定额.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\不存在的零件号.txt", notExistData);
    }

    @PostMapping("generateSupplierErpCodeSql")
    public void generateSupplierErpCodeSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        int contentRowStartIndex = 1;
        String errorData = "";
        String insertData = "";
        String updateData = "";
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        Integer supplierErpCodeId = this.supplierErpCodeService.getMaxSequence();
        if(mapList!=null && mapList.size()>0){
            for(int i=contentRowStartIndex;i < mapList.size();i++) {
                String supplierName = ((String) mapList.get(i).get("0")).trim();
                QueryWrapper<SupplierEO> wrapper = new QueryWrapper<>();
                wrapper.eq("supplier_name", supplierName);
                SupplierEO supplier = this.supplierService.getOne(wrapper);
                if(supplier == null) {
                    errorData += (supplierName + "<br>");
                    continue;
                }

                QueryWrapper<SupplierErpCodeEO> secWrapper = new QueryWrapper<>();
                secWrapper.eq("supplier_id", supplier.getSupplierId());
                secWrapper.eq("org_id", orgId);
                SupplierErpCodeEO supplierErpCode = this.supplierErpCodeService.getOne(secWrapper);
                String erpCode = ((String) mapList.get(i).get("1")).trim();
                if(supplierErpCode == null) {
                    supplierErpCodeId += 1;
                    insertData += "INSERT INTO bsc_supplier_erp_code" +
                            "(supplier_erp_code_id, org_id, supplier_id, erpcode, version, created_by, created_time, last_modified_by, last_modified_time) " +
                            "VALUES("+ supplierErpCodeId + "," +
                            orgId + "," +
                            supplier.getSupplierId() + "," +
                            "'" + erpCode + "'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now());\n";
                } else {
                    updateData += "UPDATE bsc_supplier_erp_code SET " +
                            "erpcode = '" + erpCode + "' " +
                            "WHERE supplier_erp_code_id = " + supplierErpCode.getSupplierErpCodeId() + ";\n";
                }
            }
        }

        if(!errorData.equals("")) {
            writeData("C:\\Users\\Administrator\\Desktop\\供应商不存在.html", "<html><body>"+errorData+"</body></html>");
        }
        writeData("C:\\Users\\Administrator\\Desktop\\供应商erp编码插入.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\供应商erp编码更新.sql", updateData);
        this.supplierErpCodeService.updateToMaxSequence(supplierErpCodeId);
    }

    @PostMapping("generateWarehouseAreaSql")
    public void generateWarehouseAreaSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        int contentRowStartIndex = 1;
        String errorData = "";
        String fileData = "";
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        int id =1000;
        List<String> c = new ArrayList<>();
        if(mapList!=null && mapList.size()>0){
            for(int i=contentRowStartIndex;i < mapList.size();i++) {

                String areaName = ((String) mapList.get(i).get("4")).trim();
                if(c.contains(areaName)){
                    continue;
                }
                c.add(areaName);
                id += 1;
                fileData += "INSERT INTO bsc_warehouse_area" +
                        "(warehouse_area_id,bar_code, org_id, area_code, area_name, warehouse_id, status, version, created_by, created_time, last_modified_by, last_modified_time) " +
                        "VALUES("+ id + "," +
                        "'A1-" + areaName.substring(0,2) + "'," +
                        orgId + "," +
                        "'" + areaName.substring(0,2) + "'," +
                        "'" + areaName + "'," +
                        1047+"," +
                        "1," +
                        "1," +
                        "'[admin]管理员'," +
                        "now()," +
                        "'[admin]管理员'," +
                        "now());";
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\库区.sql", fileData);
    }

    @PostMapping("generateWarehouseLocationSql")
    public void generateWarehouseLocationSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        int contentRowStartIndex = 1;
        String errorData = "";
        String fileData = "";
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        int id =1000;
        List<String> c = new ArrayList<>();
        if(mapList!=null && mapList.size()>0){
            for(int i=contentRowStartIndex;i < mapList.size();i++) {

                String a = ((String) mapList.get(i).get("3")).trim();
                if(c.contains(a)){
                    continue;
                }else{

                    c.add(a);
                }
                String areacode = a.split("-")[0];
                String locationcode = a.substring(3,a.length());
                QueryWrapper<WarehouseAreaEO> wrapperArea = new QueryWrapper<WarehouseAreaEO>();
                wrapperArea.eq("warehouse_id", 1047);
                wrapperArea.eq("org_id", orgId);
                wrapperArea.eq("area_code", a.substring(0,2));
                WarehouseAreaEO warehouseAreaEO = this.warehouseAreaService.getOne(wrapperArea);
                id += 1;
                fileData += "INSERT INTO bsc_warehouse_location" +
                        "(warehouse_location_id,bar_code, org_id, location_code, location_name, warehouse_id,warehouse_area_id, status, version, created_by, created_time, last_modified_by, last_modified_time) " +
                        "VALUES("+ id + "," +
                        "'" + warehouseAreaEO.getBarCode()+"-"+ locationcode + "'," +
                        orgId + "," +
                        "'" + locationcode + "'," +
                        "'" + a + "'," +
                        1047+"," +
                        warehouseAreaEO.getWarehouseAreaId()+","+
                        "1," +
                        "1," +
                        "'[admin]管理员'," +
                        "now()," +
                        "'[admin]管理员'," +
                        "now());";
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\库位.sql", fileData);
    }

    @PostMapping("generateCustomerErpCodeSql")
    public void generateCustomerErpCodeSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(1); //根据sheet获取
        int contentRowStartIndex = 1;
        String errorData = "";
        String insertData = "";
        String updateData = "";
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        Integer customerErpCodeId = this.customerErpCodeService.getMaxSequence();
        if(mapList!=null && mapList.size()>0){
            for(int i=contentRowStartIndex;i < mapList.size();i++) {
                String customerName = ((String) mapList.get(i).get("0")).trim();
                QueryWrapper<CustomerEO> wrapper = new QueryWrapper<>();
                wrapper.eq("customer_name", customerName);
                CustomerEO customer = this.customerService.getOne(wrapper);
                if(customer == null) {
                    errorData += (customerName + "<br>");
                    continue;
                }

                QueryWrapper<CustomerErpCodeEO> cecWrapper = new QueryWrapper<>();
                cecWrapper.eq("customer_id", customer.getCustomerId());
                cecWrapper.eq("org_id", orgId);
                CustomerErpCodeEO customerErpCode = this.customerErpCodeService.getOne(cecWrapper);
                String erpCode = ((String) mapList.get(i).get("1")).trim();
                if(customerErpCode == null) {
                    customerErpCodeId += 1;
                    insertData += "INSERT INTO bsc_customer_erp_code" +
                            "(customer_erp_code_id, org_id, customer_id, erpcode, version, created_by, created_time, last_modified_by, last_modified_time) " +
                            "VALUES("+ customerErpCodeId + "," +
                            orgId + "," +
                            customer.getCustomerId() + "," +
                            "'" + erpCode + "'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now());\n";
                } else {
                    updateData += "UPDATE bsc_customer_erp_code " +
                            "SET erpcode = '" + erpCode + "' " +
                            "WHERE customer_erp_code_id = " + customerErpCode.getCustomerErpCodeId() + ";\n";
                }
            }
        }

        if(!errorData.equals("")) {
            writeData("C:\\Users\\Administrator\\Desktop\\客户不存在.html", "<html><body>"+errorData+"</body></html>");
        }
        writeData("C:\\Users\\Administrator\\Desktop\\客户erp编码插入.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\客户erp编码更新.sql", updateData);
        this.customerErpCodeService.updateToMaxSequence(customerErpCodeId);
    }

    @GetMapping("generateMaterialRelationshipSql")
    public void generateMaterialRelationshipSql() throws Exception {
        String fileData = "";
        String notExistData = "";
        QueryWrapper<MaterialEO> parentWrapper = new QueryWrapper<>();
        parentWrapper.likeLeft("element_no", "-W");
        parentWrapper.eq("status", 1);
        List<MaterialEO> list = this.materialService.list(parentWrapper);
        if(list!=null && list.size()>0) {
            for(MaterialEO parentMaterial : list) {
                String elementNo = parentMaterial.getElementNo().substring(0, parentMaterial.getElementNo().length()-2);
                Long orgId = parentMaterial.getOrgId();
                QueryWrapper<MaterialEO> childWrapper = new QueryWrapper<>();
                childWrapper.eq("element_no", elementNo);
                childWrapper.eq("org_id", orgId);
                childWrapper.eq("status", 1);
                MaterialEO childMaterial = this.materialService.getOne(childWrapper);
                if(childMaterial != null) {
                    Long parentMaterialId = parentMaterial.getMaterialId();
                    Long childMaterialId = childMaterial.getMaterialId();
                    MaterialRelationshipEO mr = this.materialRelationshipService.get(parentMaterialId, childMaterialId);
                    if(mr != null) {
                        continue;
                    } else {
                        fileData += "INSERT INTO bsc_material_relationship" +
                                "(parent_material_id, child_material_id) " +
                                "VALUES("+ parentMaterialId + "," +
                                childMaterialId + ");\n";
                    }

                } else {
                    notExistData += (parentMaterial.getElementNo()+ "\t\t\t\t" + parentMaterial.getMaterialName() + "\n");
                }
            }
        }
        writeData("C:\\Users\\Administrator\\Desktop\\不存在黑件的物料.txt", notExistData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料关系.sql", fileData);
    }

    @PostMapping("generateMaterialSql")
    public void generateMaterialSql(HttpServletRequest request) throws Exception {
        List list = ExcelUtils.getExcelData(request);
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        int contentRowStartIndex = 2;
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        Integer sum = Integer.valueOf(request.getParameter("materialCode")); // 物料编码初始值
        Integer id = Integer.valueOf(request.getParameter("materialId"));// 物料id初始值
        String insertData = "";
        String updateData = "";
        String customerNotExistData = "";
        String insertMaterialCustomerData = "";
        String updateMaterialCustomerData = "";
        String supplierNotExistData = "";
        String insertMaterialSupplierData = "";
        String updateMaterialSupplierData = "";
        if(mapList!=null && mapList.size()>0) {
            for (int i = contentRowStartIndex; i < mapList.size(); i++) {
                Map map = mapList.get(i);
                for(Object key : map.keySet()) {
                    if(map.get(key)==null || map.get(key).toString().trim().equals("")) {
                        map.put(key, null);
                    }
                }

                //仓库 0
                String custom_string_field3 = (String) map.get("0");
                //存货编码 2
                String inventoryCode = (String) map.get("2");
                //项目号 3
                String projectNo = (String) map.get("3");
                //零件号 4
                String elementNo = (String) map.get("4");
                // 查询对应机构下的零件号是否存在，存在则生成更新sql，不存在则生成插入sql
                //版本号 5
                String figureVersion = (String) map.get("5");
                //物料名称 6
                String materialName = (String) map.get("6");
                //主计量单位 7
                String custom_string_field4 = (String) map.get("7");
                //次计量 8
                String custom_string_field5 = (String) map.get("8");
                //存货大类编码 10
                String materialType = (String) map.get("10");
                //存货大类名称 11
                String custom_string_field1 = (String) map.get("11");
                //允许采购 14
                String isPurchase = (String) map.get("14");
                if(null != isPurchase && !"".equals(isPurchase)){
                    isPurchase = "1";
                }else {
                    isPurchase = "0";
                }
                //允许自制 15
                String isProduct = (String) map.get("15");
                if(null != isProduct && !"".equals(isProduct)){
                    isProduct = "1";
                }else {
                    isProduct = "0";
                }
                //最小库存 16
                String minStock = (String) map.get("16");
                if(null == minStock || "".equals(minStock)){
                    minStock = "null";
                }
                //最大库存 17
                String maxStock = (String) map.get("17");
                if(null == maxStock || "".equals(maxStock)){
                    maxStock = "null";
                }
                //客户 18
                String custom_string_field6 = (String) map.get("18");
                //供应商 19
                String custom_string_field2 = (String) map.get("19");
                //SNP 20
                String snp = (String) map.get("20");
                if(null == snp || "".equals(snp)){
                    snp = "null";
                }
                //标准包装费 21
                String standardPackingFee = (String) map.get("21");
                if(null == standardPackingFee || "".equals(standardPackingFee)){
                    standardPackingFee = "null";
                }
                //标准运费 22
                String standardFee = (String) map.get("22");
                if(null == standardFee || "".equals(standardFee)){
                    standardFee = "null";
                }
                //折算系数 23
                String conversionFactor = (String) map.get("23");
                if(null == conversionFactor || "".equals(conversionFactor)){
                    conversionFactor = "null";
                }

                QueryWrapper<MaterialEO> wrapper = new QueryWrapper<>();
                wrapper.eq("element_no", elementNo);
                wrapper.eq("org_id", orgId);
                wrapper.eq("status", 1);
                MaterialEO material = this.materialService.getOne(wrapper);
                if(material != null) {
                    updateData += "UPDATE bsc_material SET " +
                            "inventory_code = " + (inventoryCode==null?null+",":"'"+inventoryCode+"',") +
                            "project_no = " + (projectNo==null?null+",":"'"+projectNo+"',") +
                            "figure_version = " + (figureVersion==null?null+",":"'"+figureVersion +"',") +
                            "material_name = " + (materialName==null?null+",":"'"+materialName +"',") +
                            "material_type = " + (materialType==null?null+",":"'"+materialType +"',") +
                            "is_purchase = " + "'"+isPurchase+"'," +
                            "is_product = " + "'"+isProduct+"'," +
                            "min_stock = " + ""+minStock+"," +
                            "max_stock = " + ""+maxStock+"," +
                            "snp = " + ""+snp+"," +
                            "standard_packing_fee = " + ""+standardPackingFee+"," +
                            "standard_fee = " + ""+standardFee+"," +
                            "conversion_factor = " + ""+conversionFactor+"," +
                            "custom_string_field1 = " + (custom_string_field1==null?null+",":"'"+custom_string_field1 +"',") +
                            "custom_string_field2 = " + (custom_string_field2==null?null+",":"'"+custom_string_field2+"',") +
                            "custom_string_field3 = " + (custom_string_field3==null?null+",":"'"+custom_string_field3 +"',") +
                            "custom_string_field4 = " + (custom_string_field4==null?null+",":"'"+custom_string_field4 +"',") +
                            "custom_string_field5 = " + (custom_string_field5==null?null+",":"'"+custom_string_field5 +"',") +
                            "custom_string_field6 = " + (custom_string_field6==null?null:"'"+custom_string_field6 + "'") +
                            " WHERE material_id = " + material.getMaterialId() + ";\n";
                } else {
                    //物料编码和物料名称都有了
                    sum = sum+1;
                    id = id+1;
                    String code = "M000"+sum;
                    if(sum<10){
                        code = "M000"+sum;
                    }else if(sum<100){
                        code = "M00"+sum;
                    }else if(sum<1000){
                        code = "M0"+sum;
                    }else if(sum<10000){
                        code = "M"+sum;
                    }else if(sum<100000){
                        code = "M"+sum;
                    }

                    insertData += "INSERT INTO bsc_material" +
                            "(material_id," +
                            "material_code," +
                            "inventory_code," +
                            "project_no," +
                            "element_no," +
                            "figure_version," +
                            "material_name," +
                            "material_type," +
                            "is_purchase," +
                            "is_product," +
                            "min_stock," +
                            "max_stock," +
                            "snp," +
                            "standard_packing_fee," +
                            "standard_fee," +
                            "conversion_factor," +
                            "custom_string_field1," +
                            "custom_string_field2," +
                            "custom_string_field3," +
                            "custom_string_field4," +
                            "custom_string_field5," +
                            "custom_string_field6," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time," +
                            "status," +
                            "is_exist_child," +
                            "is_sale," +
                            "consumed_products," +
                            "org_id" +
                            ") " +
                            "VALUES " +
                            "('"+id+"'," +
                            "'"+code+"'," +
                            (inventoryCode==null?null+",":"'"+inventoryCode+"',") +
                            (projectNo==null?null+",":"'"+projectNo+"',") +
                            "'"+elementNo+"'," +
                            (figureVersion==null?null+",":"'"+figureVersion +"',") +
                            (materialName==null?null+",":"'"+materialName +"',") +
                            (materialType==null?null+",":"'"+materialType +"',") +
                            "'"+isPurchase+"'," +
                            "'"+isProduct+"'," +
                            ""+minStock+"," +
                            ""+maxStock+"," +
                            ""+snp+"," +
                            ""+standardPackingFee+"," +
                            ""+standardFee+"," +
                            ""+conversionFactor+"," +
                            (custom_string_field1==null?null+",":"'"+custom_string_field1+"',") +
                            (custom_string_field2==null?null+",":"'"+custom_string_field2+"',") +
                            (custom_string_field3==null?null+",":"'"+custom_string_field3+"',") +
                            (custom_string_field4==null?null+",":"'"+custom_string_field4+"',") +
                            (custom_string_field5==null?null+",":"'"+custom_string_field5+"',") +
                            (custom_string_field6==null?null+",":"'"+custom_string_field6+"',") +
                            "'1'," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'1'," +
                            "'0'," +
                            "'0'," +
                            "'0'," +
                            orgId + ");\n";
                }

                // 物料档案客户字段有值
                if(custom_string_field6!=null && !custom_string_field6.trim().equals("")) {
                    CustomerEO customer = this.customerService.getByCustomerName(custom_string_field6);
                    if(customer == null) {
                        customerNotExistData += (elementNo + "\t\t\t\t" + custom_string_field6 + "\n");
                    } else {
                        if(material != null) {
                            MaterialCustomerEO materialCustomer = this.materialCustomerService.getByMaterialIdAndCustomerId(material.getMaterialId(), customer.getCustomerId());
                            if(materialCustomer == null) {
                                insertMaterialCustomerData += "INSERT INTO bsc_material_customer" +
                                        "(material_customer_id," +
                                        "material_id," +
                                        "customer_id," +
                                        "customer_material_code," +
                                        "customer_material_name," +
                                        "is_default," +
                                        "version," +
                                        "created_by," +
                                        "created_time," +
                                        "last_modified_by," +
                                        "last_modified_time" +
                                        ") " +
                                        "VALUES " +
                                        "(nextval('bsc_material_customer')," +
                                        "'"+material.getMaterialId()+"'," +
                                        "'"+customer.getCustomerId()+"'," +
                                        "'"+customer.getCustomerCode()+"'," +
                                        "'"+customer.getCustomerName()+"'," +
                                        "'"+1+"'," +
                                        "'"+1+"'," +
                                        "'[admin]管理员'," +
                                        "now()," +
                                        "'[admin]管理员'," +
                                        "now());\n";
                            } else {
                                updateMaterialCustomerData += "UPDATE bsc_material_customer " +
                                        "SET is_default = 1 WHERE material_customer_id = " + materialCustomer.getMaterialCustomerId() +";\n";
                            }
                        } else {
                            insertMaterialCustomerData += "INSERT INTO bsc_material_customer" +
                                    "(material_customer_id," +
                                    "material_id," +
                                    "customer_id," +
                                    "customer_material_code," +
                                    "customer_material_name," +
                                    "is_default," +
                                    "version," +
                                    "created_by," +
                                    "created_time," +
                                    "last_modified_by," +
                                    "last_modified_time" +
                                    ") " +
                                    "VALUES " +
                                    "(nextval('bsc_material_customer')," +
                                    "'"+id+"'," +
                                    "'"+customer.getCustomerId()+"'," +
                                    "'"+customer.getCustomerCode()+"'," +
                                    "'"+customer.getCustomerName()+"'," +
                                    "'"+1+"'," +
                                    "'"+1+"'," +
                                    "'[admin]管理员'," +
                                    "now()," +
                                    "'[admin]管理员'," +
                                    "now());\n";
                        }
                    }
                }

                // 物料档案供应商字段有值
                if(custom_string_field2!=null && !custom_string_field2.trim().equals("")) {
                    SupplierEO supplier = this.supplierService.getBySupplierName(custom_string_field2);
                    if(supplier == null) {
                        supplierNotExistData += (elementNo + "\t\t\t\t" + custom_string_field2 + "\n");
                    } else {
                        if(material != null) {
                            MaterialSupplierEO materialSupplier = this.materialSupplierService.getByMaterialIdAndSupplierId(material.getMaterialId(), supplier.getSupplierId());
                            if(materialSupplier == null) {
                                insertMaterialSupplierData += "INSERT INTO bsc_material_supplier" +
                                        "(material_supplier_id," +
                                        "material_id," +
                                        "supplier_id," +
                                        "supplier_material_name," +
                                        "supplier_material_code," +
                                        "is_default," +
                                        "supply_type," +
                                        "version," +
                                        "created_by," +
                                        "created_time," +
                                        "last_modified_by," +
                                        "last_modified_time" +
                                        ") " +
                                        "VALUES " +
                                        "(nextval('bsc_material_supplier')," +
                                        "'"+material.getMaterialId()+"'," +
                                        "'"+supplier.getSupplierId()+"'," +
                                        "'"+supplier.getSupplierName()+"'," +
                                        "'"+supplier.getSupplierCode()+"'," +
                                        "'"+1+"'," +
                                        "'"+1+"'," +
                                        "'"+1+"'," +
                                        "'[admin]管理员'," +
                                        "now()," +
                                        "'[admin]管理员'," +
                                        "now());\n";
                            } else {
                                updateMaterialSupplierData += "UPDATE bsc_material_supplier " +
                                        "SET is_default = 1 WHERE material_supplier_id = " + materialSupplier.getMaterialSupplierId() +";\n";
                            }
                        } else {
                            insertMaterialSupplierData += "INSERT INTO bsc_material_supplier" +
                                    "(material_supplier_id," +
                                    "material_id," +
                                    "supplier_id," +
                                    "supplier_material_name," +
                                    "is_default," +
                                    "supply_type," +
                                    "version," +
                                    "created_by," +
                                    "created_time," +
                                    "last_modified_by," +
                                    "last_modified_time" +
                                    ") " +
                                    "VALUES " +
                                    "(nextval('bsc_material_supplier')," +
                                    "'"+id+"'," +
                                    "'"+supplier.getSupplierId()+"'," +
                                    "'"+supplier.getSupplierName()+"'," +
                                    "'"+1+"'," +
                                    "'"+1+"'," +
                                    "'"+1+"'," +
                                    "'[admin]管理员'," +
                                    "now()," +
                                    "'[admin]管理员'," +
                                    "now());\n";
                        }
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\物料档案更新.sql", updateData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料档案插入.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\客户不存在.sql", customerNotExistData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料客户插入.sql", insertMaterialCustomerData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料客户更新.sql", updateMaterialCustomerData);
        writeData("C:\\Users\\Administrator\\Desktop\\供应商不存在.sql", supplierNotExistData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料供应商插入.sql", insertMaterialSupplierData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料供应商更新.sql", updateMaterialSupplierData);
    }

    @PostMapping("generateMaterialRelationshipByExcel")
    public void generateMaterialRelationshipByExcel(HttpServletRequest request) throws Exception {
        String fileData = "";
        String parentNotExistData = "";
        String childNotExistData = "";
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
        if(mapList!=null && mapList.size()>0) {
            for(int i=1; i<mapList.size(); i++) {
                Map map = mapList.get(i);
                String childElementNo = (String) map.get("1");
                String parentElementNo = (String) map.get("2");

                QueryWrapper<MaterialEO> parentWrapper = new QueryWrapper<>();
                parentWrapper.eq("element_no", parentElementNo);
                parentWrapper.eq("org_id", orgId);
                parentWrapper.eq("status", 1);
                MaterialEO parentMaterial = this.materialService.getOne(parentWrapper);
                if(parentMaterial == null) {
                    parentNotExistData += (parentElementNo + "\n");
                }

                QueryWrapper<MaterialEO> childWrapper = new QueryWrapper<>();
                childWrapper.eq("element_no", childElementNo);
                childWrapper.eq("status", 1);
                childWrapper.eq("org_id", orgId);
                MaterialEO childMaterial = this.materialService.getOne(childWrapper);
                if(childMaterial == null) {
                    childNotExistData += (childElementNo + "\n");
                }

                if(parentMaterial!=null && childMaterial!=null) {
                    Long parentMaterialId = parentMaterial.getMaterialId();
                    Long childMaterialId = childMaterial.getMaterialId();
                    MaterialRelationshipEO mr = this.materialRelationshipService.get(parentMaterialId, childMaterialId);
                    if(mr != null) {
                        continue;
                    } else {
                        fileData += "INSERT INTO bsc_material_relationship" +
                                "(parent_material_id, child_material_id) " +
                                "VALUES("+ parentMaterialId + "," +
                                childMaterialId + ");\n";
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\不存在白件的物料.txt", parentNotExistData);
        writeData("C:\\Users\\Administrator\\Desktop\\不存在黑件的物料.txt", childNotExistData);
        writeData("C:\\Users\\Administrator\\Desktop\\物料关系.sql", fileData);
    }

    // 设备表导入
    @PostMapping("generateMachineSql")
    public void generateMachineSql(HttpServletRequest request) throws Exception{
        String updateData = "";
        String insertData = "";
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        Long machineCode = Long.valueOf(request.getParameter("machineCode")); // 起始值
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
            if(mapList!=null && mapList.size()>0){//标题不需要
                for(int i=2;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    for(Object key : map.keySet()) {
                        if(map.get(key)==null || map.get(key).toString().trim().equals("")) {
                            map.put(key, null);
                        }
                    }

                    // 固定资产编号 1
                    String capitalAssetsNo = (String) map.get("1");
                    // 设备名称 2
                    String machineName = (String) map.get("2");
                    // 规格型号 3
                    String specification = (String) map.get("3");
                    // 生产区域 4
                    String location = null;
                    if (map.get("4").equals("焊接")){
                        location = "01";
                    } else if(map.get("4").equals("冲压")){
                        location = "02";
                    } else if(map.get("4").equals("仓库")){
                        location = "03";
                    } else if(map.get("4").equals("模具")){
                        location = "04";
                    } else if(map.get("4").equals("折弯")){
                        location = "05";
                    } else if(map.get("4").equals("注塑")){
                        location = "06";
                    } else if(map.get("4").equals("项目")){
                        location = "07";
                    } else if(map.get("4").equals("铆接")){
                        location = "08";
                    } else if(map.get("4").equals("装配")){
                        location = "09";
                    }
                    // 维护责任人 5
                    String maintenanceName = (String) map.get("5");
                    // 维护负责人的id
                    Long maintenance = null;
                    if(map.get("5") != null) {
                        QueryWrapper<EmployeeEO> wrapper = new QueryWrapper<>();
                        wrapper.eq("employee_name", maintenanceName);
                        wrapper.eq("root_org_id", orgId);
                        EmployeeEO employee = this.employeeService.getOne(wrapper);
                        if(employee != null) {
                            maintenance = employee.getEmployeeId();
                        }
                    }

                    QueryWrapper<MachineEO> wrapper = new QueryWrapper<>();
                    wrapper.eq("capital_assets_no", capitalAssetsNo);
                    wrapper.eq("org_id", orgId);
                    MachineEO machine = this.machineService.getOne(wrapper);
                    if(machine!=null) { // 存在则覆盖更新
                        updateData += "UPDATE bsc_machine SET " +
                                "capital_assets_no = " + (capitalAssetsNo==null?null+",":"'"+capitalAssetsNo+"',") +
                                "machine_name = " + (machineName==null?null+",":"'"+machineName+"',") +
                                "specification = " + (specification==null?null+",":"'"+specification+"',") +
                                "specific_model = " + (specification==null?null+",":"'"+specification +"',");
                        if(maintenanceName != null) {
                            updateData += "maintenance = " + (maintenance==null?null+",":"'"+maintenance +"',") +
                                    "maintenance_name = " + (maintenanceName==null?null+",":"'"+maintenanceName +"',");
                        }

                        updateData += "location = " + (location==null?null+",":"'"+location +"'") +
                                " WHERE machine_id = " + machine.getMachineId() + ";\n";
                    } else { // 不存在则新增
                        machineCode = machineCode + 1;
                        String code = "M000"+machineCode;
                        if(machineCode<10){
                            code = "M000"+machineCode;
                        }else if(machineCode<100){
                            code = "M00"+machineCode;
                        }else if(machineCode<1000){
                            code = "M0"+machineCode;
                        }else if(machineCode<10000){
                            code = "M"+machineCode;
                        }

                        insertData += "INSERT INTO bsc_machine" +
                                "(machine_id," +
                                "machine_code," +
                                "machine_name," +
                                "machine_type," +
                                "specification," +
                                "specific_model," +
                                "org_id," +
                                "location," +
                                "capital_assets_no," +
                                "maintenance," +
                                "maintenance_name," +
                                "status," +
                                "version," +
                                "created_by," +
                                "created_time," +
                                "last_modified_by," +
                                "last_modified_time" +
                                ") " +
                                "VALUES " +
                                "(nextval('bsc_machine')," +
                                "'"+code+"'," +
                                (machineName==null?null+",":"'"+machineName+"',") +
                                "'01'," +
                                (specification==null?null+",":"'"+specification+"',") +
                                (specification==null?null+",":"'"+specification+"',") +
                                orgId + "," +
                                (location==null?null+",":"'"+location+"',") +
                                (capitalAssetsNo==null?null+",":"'"+capitalAssetsNo+"',") +
                                (maintenance==null?null+",":"'"+maintenance+"',") +
                                (maintenanceName==null?null+",":"'"+maintenanceName+"',") +
                                "'1'," +
                                "'1'," +
                                "'[admin]管理员'," +
                                "now()," +
                                "'[admin]管理员'," +
                                "now());\n";
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\设备新增.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\设备更新.sql", updateData);
    }

    // 焊接工序工时导入
    @PostMapping("generateWorkingProcedureTimeSql")
    public void generateWorkingProcedureTimeSql(HttpServletRequest request) throws Exception{
        String updateData = "";
        String insertData = "";
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        OrgEO org = this.orgService.getById(orgId);
        // 获取归属机构的所有子机构
        QueryWrapper<OrgEO> oqw = new QueryWrapper<>();
        oqw.like("org_code", org.getOrgCode());
        List<OrgEO> orgs = this.orgService.list(oqw);
        Map<String, Long> orgMap = new HashedMap();
        if(orgs!=null && list.size()>0) {
            for(OrgEO oe : orgs) {
                orgMap.put(oe.getOrgName(), oe.getOrgId());
            }
        }
        // 查询业务字典
        QueryWrapper<DictEO> dqw1 = new QueryWrapper<>();
        dqw1.eq("type_id", 0);
        dqw1.eq("dict_code", "sc_working_procedure_type");
        DictEO dict1 = this.dictService.getOne(dqw1);
        HashMap<String, Long> dictMap1 = new HashMap();
        if(dict1 != null) {
            QueryWrapper<DictEO> dqw2 = new QueryWrapper<>();
            dqw2.eq("type_id", dict1.getDictId());
            List<DictEO> dicts1 = this.dictService.list(dqw2);
            if(dicts1!=null && dicts1.size()>0) {
                for(DictEO de : dicts1) {
                    dictMap1.put(de.getDictName(), de.getDictId());
                }
            }
        }

        QueryWrapper<DictEO> dqw3 = new QueryWrapper<>();
        dqw3.eq("dict_code", "bsc_men_type");
        dqw3.eq("type_id", 0);
        DictEO dict2 = this.dictService.getOne(dqw3);
        HashMap<String, Long> dictMap2= new HashMap();
        if(dict2 != null) {
            QueryWrapper<DictEO> dqw4 = new QueryWrapper<>();
            dqw4.eq("type_id", dict2.getDictId());
            List<DictEO> dicts2 = this.dictService.list(dqw4);
            if(dicts2!=null && dicts2.size()>0) {
                for(DictEO de : dicts2) {
                    dictMap2.put(de.getDictName(), de.getDictId());
                }
            }
        }


        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
            if(mapList!=null && mapList.size()>0){//标题不需要
                for(int i=3;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    for(Object key : map.keySet()) {
                        if(map.get(key)==null || map.get(key).toString().trim().equals("")) {
                            map.put(key, null);
                        }
                    }

                    // 项目 1
                    String project = (String) map.get("1");
                    // 零件号 2
                    String elementNo = (String) map.get("2");
                    // 通过零件号获取物料id,code
                    Long materialId = null;
                    String materialCode = null;
                    QueryWrapper<MaterialEO> mqw = new QueryWrapper<>();
                    mqw.eq("element_no", elementNo);
                    mqw.eq("status", 1);
                    mqw.eq("org_id", orgId);
                    MaterialEO material = this.materialService.getOne(mqw);
                    if(material != null) {
                        materialId = material.getMaterialId();
                        materialCode = material.getMaterialCode();
                    }
                    // 零部件名称 3
                    String materialName = (String) map.get("3");
                    // 工序号 4
                    String workingProcedureCode = (String) map.get("4");
                    // 工序名称 5
                    String workingProcedureName = (String) map.get("5");
                    // 工序类型 6
                    String workingProcedureTypeName = (String) map.get("6");
                    Long workingProcedureType = null;
                    if(map.get("6") != null) {
                        workingProcedureType = dictMap1.get(map.get("6"));
                    }
                    // 生产车间名称 7
                    String workshopName = (String) map.get("7");
                    // 根据生产车间名称获取对应机构下的生产车间id
                    Long workshopId = null;
                    if(map.get("7") != null) {
                        workshopId = orgMap.get(map.get("7"));
                    }
                    // 设备固定资产编号(设备编号) 8
                    String capitalAssetsNo = (String) map.get("8");
                    // 设备名称 9
                    String machineName = (String) map.get("9");
                    // 通过设备固定资产编号获取设备id,code
                    Long machineId = null;
                    String machineCode = null;
                    QueryWrapper<MachineEO> wrapper = new QueryWrapper<>();
                    wrapper.eq("capital_assets_no", capitalAssetsNo);
                    wrapper.eq("org_id", orgId);
                    MachineEO machine = this.machineService.getOne(wrapper);
                    if(machine != null) {
                        machineId = machine.getMachineId();
                        machineCode = machine.getMachineCode();
                    }
                    // 操作人数 10
                    Integer operationNumber = null;
                    if(map.get("10") != null) {
                        operationNumber = Integer.valueOf((String) map.get("10"));
                    }
                    // 人员性质 11
                    String menTypeName = (String) map.get("11");
                    Long menType = null;
                    if(map.get("11") != null) {
                        menType = dictMap2.get(map.get("11"));
                    }
                    // 辆份数量 12
                    Integer lfNumber = null;
                    if(map.get("12") != null) {
                        lfNumber = Integer.valueOf((String) map.get("12"));
                    }
                    // 模腔数 13
                    Integer mqNumber = null;
                    if(map.get("13") != null) {
                        mqNumber = Integer.valueOf((String) map.get("13"));
                    }
                    // 现有CT(S) 15
                    Double ct = null;
                    if(map.get("15") != null) {
                        ct = Double.valueOf((String) map.get("15"));
                    }
                    // 单件CT(S)(模腔数大于1的才存在单件CT(S))
                    Double ctPer = null;
                    if(mqNumber!=null && mqNumber.intValue()>1) {
                        ctPer = ct;
                    }

                    // 判断零件号是否存在工序(导入文件里面都是焊接的工序，每个零件号只有一道工序，所以按零件号覆盖就可以),存在则覆盖,不存在则插入
                    QueryWrapper<WorkingProcedureTimeEO> wptqw = new QueryWrapper<>();
                    wptqw.eq("element_no", elementNo);
                    wptqw.eq("working_procedure_type", workingProcedureType);
                    wptqw.eq("org_id", orgId);
                    WorkingProcedureTimeEO wpt = this.workingProcedureTimeService.getOne(wptqw);
                    if(wpt != null) {
                        updateData += "UPDATE mes_working_procedure_time SET " +
                                "project = " + (project==null?null+",":"'"+project+"',") +
                                "material_id = " + (materialId==null?null+",":"'"+materialId+"',") +
                                "material_code = " + (materialCode==null?null+",":"'"+materialCode +"',") +
                                "element_no = " + (elementNo==null?null+",":"'"+elementNo +"',") +
                                "material_name = " + (materialName==null?null+",":"'"+materialName +"',") +
                                "working_procedure_code = " + (workingProcedureCode==null?null+",":"'"+workingProcedureCode +"',") +
                                "working_procedure_name = " + (workingProcedureName==null?null+",":"'"+workingProcedureName +"',") +
                                "working_procedure_type = " + (workingProcedureType==null?null+",":"'"+workingProcedureType +"',") +
                                "workshop_id = " + (workshopId==null?null+",":"'"+workshopId +"',") +
                                "machine_id = " + (machineId==null?null+",":"'"+machineId +"',") +
                                "machine_code = " + (machineCode==null?null+",":"'"+machineCode +"',") +
                                "capital_assets_no = " + (capitalAssetsNo==null?null+",":"'"+capitalAssetsNo +"',") +
                                "machine_name = " + (machineName==null?null+",":"'"+machineName+"',") +
                                "men_type = " + (menType==null?null+",":"'"+menType +"',") +
                                "operation_number = " + (operationNumber==null?null+",":"'"+operationNumber +"',") +
                                "lf_number = " + (lfNumber==null?null+",":"'"+lfNumber +"',") +
                                "mq_number = " + (mqNumber==null?null+",":"'"+mqNumber + "',") +
                                "ct = " + (ct==null?null+",":"'"+ct + "',") +
                                "ct_per = " + (ctPer==null?null+",":"'"+ctPer + "',") +
                                "working_procedure_type_name = " + (workingProcedureTypeName==null?null+",":"'"+workingProcedureTypeName + "',") +
                                "workshop_name = " + (workshopName==null?null+",":"'"+workshopName + "',") +
                                "men_type_name = " + (menTypeName==null?null+",":"'"+menTypeName + "' ") +
                                " WHERE working_procedure_time_id = " + wpt.getWorkingProcedureTimeId() + ";\n";
                    } else {
                        insertData += "INSERT INTO mes_working_procedure_time " +
                            "(working_procedure_time_id," +
                            "org_id," +
                            "project," +
                            "material_id," +
                            "material_code," +
                            "element_no," +
                            "material_name," +
                            "working_procedure_code," +
                            "working_procedure_name," +
                            "working_procedure_type," +
                            "workshop_id," +
                            "machine_id," +
                            "machine_code," +
                            "capital_assets_no," +
                            "machine_name," +
                            "men_type," +
                            "operation_number," +
                            "lf_number," +
                            "mq_number," +
                            "ct," +
                            "ct_per," +
                            "status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time," +
                            "working_procedure_type_name," +
                            "workshop_name," +
                            "men_type_name" +
                            ")VALUES " +
                            "(nextval('mes_working_procedure_time')," +
                            orgId+"," +
                            (project==null?null+",":"'"+project+"',") +
                            (materialId==null?null+",":"'"+materialId+"',") +
                            (materialCode==null?null+",":"'"+materialCode+"',") +
                            (elementNo==null?null+",":"'"+elementNo+"',") +
                            (materialName==null?null+",":"'"+materialName+"',") +
                            (workingProcedureCode==null?null+",":"'"+workingProcedureCode+"',") +
                            (workingProcedureName==null?null+",":"'"+workingProcedureName+"',") +
                            (workingProcedureType==null?null+",":"'"+workingProcedureType+"',") +
                            (workshopId==null?null+",":"'"+workshopId+"',") +
                            (machineId==null?null+",":"'"+machineId+"',") +
                            (machineCode==null?null+",":"'"+machineCode+"',") +
                            (capitalAssetsNo==null?null+",":"'"+capitalAssetsNo+"',") +
                            (machineName==null?null+",":"'"+machineName+"',") +
                            (menType==null?null+",":"'"+menType+"',") +
                            (operationNumber==null?null+",":"'"+operationNumber+"',") +
                            (lfNumber==null?null+",":"'"+lfNumber+"',") +
                            (mqNumber==null?null+",":"'"+mqNumber+"',") +
                            (ct==null?null+",":"'"+ct+"',") +
                            (ctPer==null?null+",":"'"+ctPer+"',") +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()," +
                            (workingProcedureTypeName==null?null+",":"'"+workingProcedureTypeName+"',") +
                            (workshopName==null?null+",":"'"+workshopName+"',") +
                            (menTypeName==null?null+",":"'"+menTypeName+"'") +
                            ");\n";
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\焊接工序工时新增.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\焊接工序工时更新.sql", updateData);
    }

    // 冲压工序工时导入
    @PostMapping("generateWorkingProcedureTimeCySql")
    public void generateWorkingProcedureTimeCySql(HttpServletRequest request) throws Exception {
        String insertData = "";
        List list = ExcelUtils.getExcelData(request);
        Long orgId = Long.valueOf(request.getParameter("orgId")); // 归属机构id
        OrgEO org = this.orgService.getById(orgId);
        // 获取归属机构的所有子机构
        QueryWrapper<OrgEO> oqw = new QueryWrapper<>();
        oqw.like("org_code", org.getOrgCode());
        List<OrgEO> orgs = this.orgService.list(oqw);
        Map<String, Long> orgMap = new HashedMap();
        if(orgs!=null && list.size()>0) {
            for(OrgEO oe : orgs) {
                orgMap.put(oe.getOrgName(), oe.getOrgId());
            }
        }
        // 查询业务字典
        QueryWrapper<DictEO> dqw1 = new QueryWrapper<>();
        dqw1.eq("type_id", 0);
        dqw1.eq("dict_code", "sc_working_procedure_type");
        DictEO dict1 = this.dictService.getOne(dqw1);
        HashMap<String, Long> dictMap1 = new HashMap();
        if(dict1 != null) {
            QueryWrapper<DictEO> dqw2 = new QueryWrapper<>();
            dqw2.eq("type_id", dict1.getDictId());
            List<DictEO> dicts1 = this.dictService.list(dqw2);
            if(dicts1!=null && dicts1.size()>0) {
                for(DictEO de : dicts1) {
                    dictMap1.put(de.getDictName(), de.getDictId());
                }
            }
        }

        QueryWrapper<DictEO> dqw3 = new QueryWrapper<>();
        dqw3.eq("dict_code", "bsc_men_type");
        dqw3.eq("type_id", 0);
        DictEO dict2 = this.dictService.getOne(dqw3);
        HashMap<String, Long> dictMap2= new HashMap();
        if(dict2 != null) {
            QueryWrapper<DictEO> dqw4 = new QueryWrapper<>();
            dqw4.eq("type_id", dict2.getDictId());
            List<DictEO> dicts2 = this.dictService.list(dqw4);
            if(dicts2!=null && dicts2.size()>0) {
                for(DictEO de : dicts2) {
                    dictMap2.put(de.getDictName(), de.getDictId());
                }
            }
        }


        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
            if(mapList!=null && mapList.size()>0){//标题不需要
                for(int i=1;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    for(Object key : map.keySet()) {
                        if(map.get(key)==null || map.get(key).toString().trim().equals("")) {
                            map.put(key, null);
                        }
                    }

                    // 项目 1
                    String project = (String) map.get("1");
                    // 零件号 2
                    String elementNo = (String) map.get("2");
                    // 通过零件号获取物料id,code
                    Long materialId = null;
                    String materialCode = null;
                    QueryWrapper<MaterialEO> mqw = new QueryWrapper<>();
                    mqw.eq("element_no", elementNo);
                    mqw.eq("status", 1);
                    mqw.eq("org_id", orgId);
                    MaterialEO material = this.materialService.getOne(mqw);
                    if(material != null) {
                        materialId = material.getMaterialId();
                        materialCode = material.getMaterialCode();
                    }
                    // 零部件名称 3
                    String materialName = (String) map.get("3");
                    // 工序号 4
                    String workingProcedureCode = (String) map.get("4");
                    // 工序名称 5
                    String workingProcedureName = (String) map.get("5");
                    // 工序类型 6
                    map.put("6", "冲压");
                    String workingProcedureTypeName = (String) map.get("6");
                    Long workingProcedureType = null;
                    if(map.get("6") != null) {
                        workingProcedureType = dictMap1.get(map.get("6"));
                    }
                    // 生产车间名称 7
                    map.put("7", "冲压车间");
                    String workshopName = (String) map.get("7");
                    // 根据生产车间名称获取对应机构下的生产车间id
                    Long workshopId = null;
                    if(map.get("7") != null) {
                        workshopId = orgMap.get(map.get("7"));
                    }
                    // 设备固定资产编号(设备编号) 8
                    String capitalAssetsNo = (String) map.get("8");
                    // 设备名称 9
                    String machineName = (String) map.get("9");
                    // 通过设备固定资产编号获取设备id,code
                    Long machineId = null;
                    String machineCode = null;
                    QueryWrapper<MachineEO> wrapper = new QueryWrapper<>();
                    wrapper.eq("capital_assets_no", capitalAssetsNo);
                    wrapper.eq("org_id", orgId);
                    MachineEO machine = this.machineService.getOne(wrapper);
                    if(machine != null) {
                        machineId = machine.getMachineId();
                        machineCode = machine.getMachineCode();
                    }
                    // 操作人数 10
                    Integer operationNumber = null;
                    System.out.println(i);
                    if(map.get("10") != null) {
                        operationNumber = Integer.valueOf((String) map.get("10"));
                    }
                    // 人员性质 11
                    map.put("11", "冲压工");
                    String menTypeName = (String) map.get("11");
                    Long menType = null;
                    if(map.get("11") != null) {
                        menType = dictMap2.get(map.get("11"));
                    }
                    // 辆份数量 12
                    Integer lfNumber = null;
                    if(map.get("12") != null) {
                        lfNumber = Integer.valueOf((String) map.get("12"));
                    }
                    // 模腔数 13
                    Integer mqNumber = null;
                    if(map.get("13") != null) {
                        mqNumber = Integer.valueOf((String) map.get("13"));
                    }
                    // 现有CT(S) 15
                    Double ct = null;
                    if(map.get("15") != null) {
                        ct = Double.valueOf((String) map.get("15"));
                        BigDecimal ctbg = new BigDecimal(ct).setScale(2, RoundingMode.UP);
                        ct = new Double(ctbg.doubleValue());
                    }
                    // 单件CT(S)(模腔数大于1的才存在单件CT(S))
                    Double ctPer = null;
                    if(mqNumber!=null && mqNumber.intValue()>1) {
                        ctPer = ct;
                    }
                    // 工序编码 16(新建一个字段存起来(保存起来反写))
                    String customStringField1 = (String) map.get("16");
                    // 设备型号 17(新建一个字段存起来(保存起来反写))
                    String customStringField2 = (String) map.get("17");

                    insertData += "INSERT INTO mes_working_procedure_time " +
                            "(working_procedure_time_id," +
                            "org_id," +
                            "project," +
                            "material_id," +
                            "material_code," +
                            "element_no," +
                            "material_name," +
                            "working_procedure_code," +
                            "working_procedure_name," +
                            "working_procedure_type," +
                            "workshop_id," +
                            "machine_id," +
                            "machine_code," +
                            "capital_assets_no," +
                            "machine_name," +
                            "men_type," +
                            "operation_number," +
                            "lf_number," +
                            "mq_number," +
                            "ct," +
                            "ct_per," +
                            "status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time," +
                            "working_procedure_type_name," +
                            "workshop_name," +
                            "men_type_name," +
                            "custom_string_field1," +
                            "custom_string_field2" +
                            ")VALUES " +
                            "(nextval('mes_working_procedure_time')," +
                            orgId+"," +
                            (project==null?null+",":"'"+project+"',") +
                            (materialId==null?null+",":"'"+materialId+"',") +
                            (materialCode==null?null+",":"'"+materialCode+"',") +
                            (elementNo==null?null+",":"'"+elementNo+"',") +
                            (materialName==null?null+",":"'"+materialName+"',") +
                            (workingProcedureCode==null?null+",":"'"+workingProcedureCode+"',") +
                            (workingProcedureName==null?null+",":"'"+workingProcedureName+"',") +
                            (workingProcedureType==null?null+",":"'"+workingProcedureType+"',") +
                            (workshopId==null?null+",":"'"+workshopId+"',") +
                            (machineId==null?null+",":"'"+machineId+"',") +
                            (machineCode==null?null+",":"'"+machineCode+"',") +
                            (capitalAssetsNo==null?null+",":"'"+capitalAssetsNo+"',") +
                            (machineName==null?null+",":"'"+machineName+"',") +
                            (menType==null?null+",":"'"+menType+"',") +
                            (operationNumber==null?null+",":"'"+operationNumber+"',") +
                            (lfNumber==null?null+",":"'"+lfNumber+"',") +
                            (mqNumber==null?null+",":"'"+mqNumber+"',") +
                            (ct==null?null+",":"'"+ct+"',") +
                            (ctPer==null?null+",":"'"+ctPer+"',") +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()," +
                            (workingProcedureTypeName==null?null+",":"'"+workingProcedureTypeName+"',") +
                            (workshopName==null?null+",":"'"+workshopName+"',") +
                            (menTypeName==null?null+",":"'"+menTypeName+"',") +
                            (customStringField1==null?null+",":"'"+customStringField1+"',") +
                            (customStringField2==null?null+",":"'"+customStringField2+"'") +
                            ");\n";
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\冲压工序工时新增.sql", insertData);
    }


    @PostMapping("generateCustomerSql")
    public void generateCustomerSql(HttpServletRequest request) throws Exception{
        List list = ExcelUtils.getExcelData(request);
        Integer sum = Integer.valueOf(request.getParameter("sum"));// 客户编码初始值
        String insertData = "";
        String existData = "";

        if(null != list && list.size()>0) {
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
            if (mapList != null && mapList.size() > 0) {//标题不需要
                for (int i = 1; i < mapList.size(); i++) {
                    Map map = mapList.get(i);
                    for (Object key : map.keySet()) {
                        if (map.get(key) == null || map.get(key).toString().trim().equals("")) {
                            map.put(key, null);
                        }
                    }

                    // 客户名称 3
                    String customerName = (String) map.get("3");

                    // 判断客户是否存在
                    QueryWrapper<CustomerEO> wrapper = new QueryWrapper<>();
                    wrapper.eq("customer_name", customerName);
                    wrapper.ne("status", 0);
                    CustomerEO customer = this.customerService.getOne(wrapper);
                    if(customer != null) {
                        existData += (customerName + "\n");
                    } else {
                        sum = sum+1;
                        String customerCode = "C000"+sum;
                        if(sum<10){
                            customerCode = "C000"+sum;
                        }else if(sum<100){
                            customerCode = "C00"+sum;
                        }else if(sum<1000){
                            customerCode = "C0"+sum;
                        }else if(sum<10000){
                            customerCode = "C"+sum;
                        }else if(sum<100000){
                            customerCode = "C"+sum;
                        }

                        insertData += "INSERT INTO bsc_customer" +
                                    "(customer_id," +
                                    "customer_code," +
                                    "customer_name," +
                                    "org_id," +
                                    "status," +
                                    "account_status," +
                                    "custom_string_field5," +
                                    "version," +
                                    "created_by," +
                                    "created_time," +
                                    "last_modified_by," +
                                    "last_modified_time) " +
                                    "VALUES " +
                                    "(nextval('bsc_customer')," +
                                    (customerCode==null?null+",":"'"+customerCode+"',") +
                                    (customerName==null?null+",":"'"+customerName+"',") +
                                    "'1'," +
                                    "'2'," +
                                    "'0'," +
                                    "'弘圣'," +
                                    "'1'," +
                                    "'[admin]管理员'," +
                                    "now()," +
                                    "'[admin]管理员'," +
                                    "now());\n";
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\客户插入.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\客户已存在.sql", existData);
    }

    @PostMapping("generateSupplierSql")
    public void generateSupplierSql(HttpServletRequest request) throws Exception{
        List list = ExcelUtils.getExcelData(request);
        Integer sum = Integer.valueOf(request.getParameter("sum"));// 供应商编码初始值
        String insertData = "";
        String existData = "";

        if(null != list && list.size()>0) {
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取
            if (mapList != null && mapList.size() > 0) {//标题不需要
                for (int i = 1; i < mapList.size(); i++) {
                    Map map = mapList.get(i);
                    for (Object key : map.keySet()) {
                        if (map.get(key) == null || map.get(key).toString().trim().equals("")) {
                            map.put(key, null);
                        }
                    }

                    // 供应商ERP编码 1
                    String erpCode = (String) map.get("1");
                    // 供应商全称 2
                    String supplierName = (String) map.get("2");
                    // 供应商简称 3
                    String shortName = (String) map.get("3");
                    // 主要联络人 4
                    String contactName = (String) map.get("4");
                    // 职务 5
                    String jobTitle = (String) map.get("5");
                    // 电话 6
                    String contactPhone = (String) map.get("6");
                    // 传真 7
                    String contactFax = (String) map.get("7");
                    // 手机 8
                    String contactMobile = (String) map.get("8");
                    // 地址 9
                    String contactAddress = (String) map.get("9");
                    // 邮箱 10
                    String contactEmail = (String) map.get("10");
                    // 开户行 11
                    String depositBank = (String) map.get("11");
                    // 税号(纳税登记号) 12
                    String taxNo = (String) map.get("12");
                    // 注册地址 13
                    String address = (String) map.get("13");

                    // 判断供应商是否存在
                    QueryWrapper<SupplierEO> wrapper = new QueryWrapper<>();
                    wrapper.eq("supplier_name", supplierName);
                    wrapper.eq("status", 1);
                    SupplierEO supplier = this.supplierService.getOne(wrapper);
                    if(supplier != null) {
                        existData += (supplierName + "\n");
                    } else {
                        sum = sum+1;
                        String supplierCode = "S000"+sum;
                        if(sum<10){
                            supplierCode = "S000"+sum;
                        }else if(sum<100){
                            supplierCode = "S00"+sum;
                        }else if(sum<1000){
                            supplierCode = "S0"+sum;
                        }else if(sum<10000){
                            supplierCode = "S"+sum;
                        }else if(sum<100000){
                            supplierCode = "S"+sum;
                        }

                        insertData += "INSERT INTO bsc_supplier" +
                                "(supplier_id," +
                                "supplier_code," +
                                "supplier_name," +
                                "short_name," +
                                "supplier_type," +
                                "erp_code," +
                                "org_id," +
                                "tax_no," +
                                "address," +
                                "deposit_bank," +
                                "contact_name," +
                                "job_title," +
                                "contact_address," +
                                "contact_phone," +
                                "contact_mobile," +
                                "contact_fax," +
                                "contact_email," +
                                "status," +
                                "account_status," +
                                "custom_string_field5," +
                                "version," +
                                "created_by," +
                                "created_time," +
                                "last_modified_by," +
                                "last_modified_time) " +
                                "VALUES " +
                                "(nextval('bsc_supplier')," +
                                (supplierCode==null?null+",":"'"+supplierCode+"',") +
                                (supplierName==null?null+",":"'"+supplierName+"',") +
                                (shortName==null?null+",":"'"+shortName+"',") +
                                "'02'," +
                                (erpCode==null?null+",":"'"+erpCode+"',") +
                                "'1'," +
                                (taxNo==null?null+",":"'"+taxNo+"',") +
                                (address==null?null+",":"'"+address+"',") +
                                (depositBank==null?null+",":"'"+depositBank+"',") +
                                (contactName==null?null+",":"'"+contactName+"',") +
                                (jobTitle==null?null+",":"'"+jobTitle+"',") +
                                (contactAddress==null?null+",":"'"+contactAddress+"',") +
                                (contactPhone==null?null+",":"'"+contactPhone+"',") +
                                (contactMobile==null?null+",":"'"+contactMobile+"',") +
                                (contactFax==null?null+",":"'"+contactFax+"',") +
                                (contactEmail==null?null+",":"'"+contactEmail+"',") +
                                "'1'," +
                                "'0'," +
                                "'弘圣'," +
                                "'1'," +
                                "'[admin]管理员'," +
                                "now()," +
                                "'[admin]管理员'," +
                                "now());\n";
                    }
                }
            }
        }

        writeData("C:\\Users\\Administrator\\Desktop\\供应商插入.sql", insertData);
        writeData("C:\\Users\\Administrator\\Desktop\\供应商已存在.sql", existData);
    }
}
