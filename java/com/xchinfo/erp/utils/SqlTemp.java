package com.xchinfo.erp.utils;


import com.xchinfo.erp.bsc.entity.CustomerEO;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlTemp {




    public static void main(String[] args) throws Exception {

        SqlTemp sqlTemp = new SqlTemp();
//        sqlTemp.getCustomerSql();
 //         sqlTemp.getMaterialSql();
//        sqlTemp.getMachineSql();
    //    sqlTemp.getMaterialSql();
        sqlTemp.getWltzSql();

    }

    //数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public void getMaterialSql() throws Exception{
        String str = "";
        //起步初始值
        Integer sum = 6840;
        Integer id = 7844;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\Users\\Administrator\\Desktop\\新建 XLS 工作表.xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }

                    if(str.contains((String) map.get("2"))){
                        continue;
                    }
                    str+=((String) map.get("2"))+";";

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
                    }

                    //存货编码 2
                    String inventoryCode = (String) map.get("2");
                    //项目号 3
                    String projectNo = (String) map.get("3");
                    //零件号 4
                    String elementNo = (String) map.get("4");
                    //版本号 5
                    String figureVersion = (String) map.get("5");
                    //物料名称 6
                    String materialName = (String) map.get("6");

                    //存货大类编码 10
                    String materialType = (String) map.get("10");

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


                    //存货大类名称 11
                    String custom_string_field1 = (String) map.get("11");

                    //供应商 19
                    String custom_string_field2 = (String) map.get("19");
                    if(null == custom_string_field2 || "".equals(custom_string_field2.trim())){
                        custom_string_field2 = "null";
                    }

                    //仓库 0
                    String custom_string_field3 = (String) map.get("0");

                    //主计量单位 7
                    String custom_string_field4 = (String) map.get("7");

                    //次计量 8
                    String custom_string_field5 = (String) map.get("8");


                    fileDate += "INSERT INTO bsc_material" +
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
                            "'"+inventoryCode+"'," +
                            "'"+projectNo+"'," +
                            "'"+elementNo+"'," +
                            "'"+figureVersion+"'," +
                            "'"+materialName+"'," +
                            "'"+materialType+"'," +
                            "'"+isPurchase+"'," +
                            "'"+isProduct+"'," +
                            ""+minStock+"," +
                            ""+maxStock+"," +
                            ""+snp+"," +
                            ""+standardPackingFee+"," +
                            ""+standardFee+"," +
                            ""+conversionFactor+"," +
                            "'"+custom_string_field1+"'," +
                            (custom_string_field2=="null"?null+",":"'"+custom_string_field2+"',") +
                            "'"+custom_string_field3+"'," +
                            "'"+custom_string_field4+"'," +
                            "'"+custom_string_field5+"'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'1'," +
                            "'0'," +
                            "'0'," +
                            "'0'," +
                            "'3');\n";
                }
            }
        }

        writaDate("C:\\Users\\Administrator\\Desktop\\物料档案sql.sql",fileDate);




    }


    //数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public void getMaterialSql2() throws Exception{
        String str = "";
        //起步初始值
        Integer sum = 5888;
        Integer id = 6892;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\Users\\Administrator\\Desktop\\武汉沿浦在制品零件清单(20190726).xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=1;i < mapList.size();i++) {
                    Map map = mapList.get(i);

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
                    }

                    String elementNo = (String) map.get("1");
                    String projectNo = (String) map.get("2");
                    String materialName = (String) map.get("3");
                    String memo = (String) map.get("4");
                    String materialType = "0302";

                    fileDate += "INSERT INTO bsc_material" +
                            "(material_id," +
                            "material_code," +
                            "project_no," +
                            "element_no," +
                            "material_name," +
                            "material_type," +
                            "memo," +
                            "main_warehouse_id," +
                            "custom_string_field3," +
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
                            "'"+projectNo+"'," +
                            "'"+elementNo+"'," +
                            "'"+materialName+"'," +
                            "'"+materialType+"'," +
                            "'"+memo+"'," +
                            "'"+ "1048" +"'," +
                            "'"+ "A2" +"'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "'2019-06-14 14:21:06'," +
                            "'[admin]管理员'," +
                            "'2019-06-14 14:21:06'," +
                            "'1'," +
                            "'0'," +
                            "'0'," +
                            "'0'," +
                            "'3');\n";
                }
            }
        }

        writaDate("C:\\Users\\Administrator\\Desktop\\物料档案sql.txt",fileDate);
    }

    //数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public void getMachineSql() throws Exception{
        String str = "";
        //起步初始值
        Integer sum = 0;
        Integer id = 1138;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\yuanchang\\郑州设备清单.xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }

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
                    }

                    //设备名称 2
                    String machineName = (String) map.get("2");

                    //规格型号 3
                    String specification = (String) map.get("3");

                    //生产区域 4
                    String location = "";
                    if (map.get("4").equals("焊装")){
                        location = "01";
                    }
                    else if(map.get("4").equals("冲压")){
                        location = "02";
                    }
                    else if(map.get("4").equals("仓库")){
                        location = "03";
                    }
                    else if(map.get("4").equals("模具")){
                        location = "04";
                    }
                    else if(map.get("4").equals("折弯")){
                        location = "05";
                    }
                    else if(map.get("4").equals("注塑")){
                        location = "06";
                    }
                    //固定资产编号 4
                    String capital_assets_no = (String) map.get("1");


                    fileDate += "INSERT INTO bsc_machine" +
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
                            "('"+id+"'," +
                            "'"+code+"'," +
                            "'"+machineName+"'," +
                            "'01'," +
                            "'"+specification+"'," +
                            "'"+specification+"'," +
                            "'118'," +
                            "'"+location+"'," +
                            "'"+capital_assets_no+"'," +
                            "'1229'," +
                            "'王巍'," +
                            "'1'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "'2019-07-22 00:00:06'," +
                            "'[admin]管理员'," +
                            "'2019-07-22 00:00:06');\n";
                }
            }
        }

        writaDate("C:\\ds\\郑州设备清单sql.txt",fileDate);

    }


    //数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public void getCustomerSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 17;
        Integer id = 1017;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\Users\\chengxingxing\\Desktop\\sql文件生成\\zh客户数据.xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }

                    CustomerEO customerEO = new CustomerEO();


//                    if(str.contains((String) map.get("1"))){
//                        continue;
//                    }
//                    str+=((String) map.get("1"))+";";
                    sum = sum+1;
                    id = id+1;

                    String code = "C00"+sum;
                    if(sum<10){
                        code = "C000"+sum;
                    }

                    fileDate += "INSERT INTO bsc_customer" +
                            "(customer_id," +
                            "customer_code," +
                            "customer_name," +
                            "erp_code," +
                            "org_id," +
                            "contact_name," +
                            "job_title," +
                            "contact_mobile," +
                            "contact_address," +
                            "contact_email," +
                            "status," +
                            "account_status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time) " +
                            "VALUES " +
                            "('"+id+"'," +
                            "'"+code+"'," +
                            "'"+(String) map.get("2")+"'," +
                            "'"+(String) map.get("1")+"'," +
                            "'1'," +
                            "'"+(String) map.get("3")+"'," +
                            "'"+(String) map.get("4")+"'," +
                            "'"+(String) map.get("7")+"'," +
                            "'"+((String) map.get("8")).trim()+"'," +
                            "'"+((String) map.get("9")).trim()+"'," +
                            "'1'," +
                            "'0'," +
                            "'1'," +
                            "'[admin]管理员'," +
                            "'2019-06-13 14:21:06'," +
                            "'[admin]管理员'," +
                            "'2019-06-13 14:21:06');\n";
                }
            }
        }

        writaDate("C:\\Users\\chengxingxing\\Desktop\\sql文件生成\\sql\\客户档案sql.txt",fileDate);
    }

    //郑州物料台账表数据初始化
    public  void getWltzSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;
        Integer id = 11701;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\yuanchang\\武汉期初库存.xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }
                    id = id+1;

                    String amount = "";
                    if (map.get("12")==""){
                        amount = "0";
                    }else{
                        amount = (String)map.get("12");
                    }

                    fileDate += "INSERT INTO wms_stock_account " +
                            "(account_id ," +
                            "material_id,"+
                            "material_code,"+
                            "material_name,"+
                            "voucher_type," +
                            "element_no," +
                            "inventory_code," +
                            "specification," +
                            "amount," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time) " +
                            "VALUES " +
                            "("+id+"," +
                            "8520," +
                            "'8520'," +
                            "'8520'," +
                            "0," +
                            "'"+((String) map.get("4")).trim()+"'," +
                            "'"+((String) map.get("2")).trim()+"'," +
                            "'8520'," +
                            ""+amount+"," +
                            "1," +
                            "'[admin]管理员'," +
                            "'2019-07-22 14:21:06'," +
                            "'[admin]管理员'," +
                            "'2019-07-22 14:21:06'" +
                            ");\n";
                }
            }
        }

        writaDate("C:\\ds\\武汉期初库存sql.txt",fileDate);
    }

    public void writaDate(String path,String context) throws Exception{
        File file =new File(path);
        Writer out =new FileWriter(file);
        out.write(context);
        out.close();
    }


}
