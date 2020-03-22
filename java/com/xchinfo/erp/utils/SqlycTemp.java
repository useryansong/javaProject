package com.xchinfo.erp.utils;


import com.xchinfo.erp.bsc.entity.CustomerEO;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlycTemp {




    public static void main(String[] args) throws Exception {

        SqlycTemp sqlTemp = new SqlycTemp();

        sqlTemp.getWltzSql();
        //sqlTemp.getLocation();

    }


    //郑州物料台账表数据初始化
    public  void getWltzSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;
        Integer id = 1000;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\12yue\\12月期初库存汇总表（柳州工厂）.xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }
                    id = id+1;



                    String main_warehouse_id = "";
                    if (map.get("0").equals ("A1")){
                        main_warehouse_id = "1068";
                    }else if(map.get("0").equals ("A2")){
                        main_warehouse_id = "1069";
                    }else if(map.get("0").equals ("A3")){
                        main_warehouse_id = "1070";
                    }else if(map.get("0").equals ("A102")){
                        main_warehouse_id = "1080";
                    }else if(map.get("0").equals ("A202")){
                        main_warehouse_id = "1067";
                    }else if(map.get("0").equals ("A31")){
                        main_warehouse_id = "1081";
                    }else if(map.get("0").equals ("A9")){
                        main_warehouse_id = "1065";
                    }

                    String amount = "";
                    if (map.get("11")==""|| map.get("11") == null ){
                        amount = "0";
                    }else{
                        amount = (String)map.get("11");
                    }

                    String material_name = "";
                    if (map.get("6")==""|| map.get("6") == null ){
                        material_name = (String)map.get("4");
                    }else{
                        material_name = (String)map.get("6");
                    }

                    String first_measurement_unit = "";
                    if (map.get("7").equals ("KG")){
                        first_measurement_unit = "1015";
                    }else{
                        first_measurement_unit = "1014";
                    }

                    fileDate += "INSERT INTO wms_temp_inventory " +
                            "(temp_id ," +
                            "inventory_id,"+
                            "material_id,"+
                            "org_id,"+
                            "material_name,"+
                            "element_no,"+
                            "inventory_code,"+
                            "main_warehouse_id,"+
                            "first_measurement_unit,"+
                            "material_type,"+
                            "project_no,"+
                            "custom_string_field1,"+
                            "amount,"+
                            "inventory_date," +
                            "status," +
                            "inventory_user_id," +
                            "inventory_user_name," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time) " +
                            "VALUES " +
                            "("+id+"," +
                            "1001," +
                            "'20000'," +
                            "'116'," +
                            "'"+material_name+"'," +
                            "'"+((String) map.get("4")).trim()+"'," +
                            "'"+((String) map.get("2")).trim()+"'," +
                            ""+main_warehouse_id+"," +
                            ""+first_measurement_unit+"," +
                            "'"+((String) map.get("9")).trim()+"'," +
                            "'"+((String) map.get("3")).trim()+"'," +
                            "'"+((String) map.get("10")).trim()+"'," +
                            ""+amount+"," +
                            "'2019-11-30'," +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "1," +
                            "'[admin]管理员'," +
                            "'2019-11-30 14:22:06'," +
                            "'[admin]管理员'," +
                            "'2019-11-30 14:22:06'" +
                            ");\n";
                }
            }
        }

        writaDate("C:\\12yue\\柳州期初库存sql.txt",fileDate);
    }

    //郑州物料台账表数据初始化
    public  void getLocation() throws Exception{

        String str = "";
        //起步初始值
        Integer id = 1;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("C:\\kuwei\\20190803 武汉工厂焊接原材料库位表(2).xlsx");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }
                    id = id+1;




                    fileDate += "INSERT INTO wms_temp_location " +
                            "(temp_id ," +
                            "location_name,"+
                            "element_no) " +
                            "VALUES " +
                            "("+id+"," +
                            "'"+((String) map.get("3")).trim()+"'," +
                            "'"+((String) map.get("1")).trim()+"'" +
                            ");\n";
                }
            }
        }

        writaDate("C:\\kuwei\\库位sql.txt",fileDate);
    }

    public void writaDate(String path,String context) throws Exception{
        File file =new File(path);
        Writer out =new FileWriter(file);
        out.write(context);
        out.close();
    }


}
