package com.xchinfo.erp.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlTest {
    private static Integer id = 1010;
    private static String addr = "C:\\Users\\Administrator\\Desktop\\";
    private static Integer orgId = 3;
    private static String template = "C:\\Users\\Administrator\\Desktop\\冲压材料 BOM 消耗定额.xlsx";

    public static void main(String[] args) throws Exception {
        /*BigDecimal c=new BigDecimal(15.36);
        Long a = 2L;
        Long b = 3L;
        System.out.print(c.multiply(new BigDecimal(a)).divide(new BigDecimal(b),2));*/
        /*String str = "000000001234034120";
        String newStr = str.replaceAll("^(0+)", "");
        System.out.println(newStr.substring(1,newStr.length()));*/
        getProjectSql();
        //getHzgsSql();
        //getHzgsSql();
//        getCygsSql();
    }
    //冲压材料耗用定额数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public static void getCyclhydeSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList(template);
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=3;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    /*if (i < 1) {
                        continue;
                    }*/
                    id = id+1;

                    fileDate += "INSERT INTO mes_stamping_material_consumption_quota " +
                            "(stamping_material_consumption_quota_id ," +
                            "org_id," +
                            "project," +
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
                            "'"+((String) map.get("1")).trim()+"'," +
                            "'"+((String) map.get("2")).trim()+"'," +
                            "'"+((String) map.get("3")).trim()+"'," +
                            "'"+((String) map.get("4")).trim()+"'," +
                            "'"+((String) map.get("5")).trim()+"'," +
                            ""+((String) map.get("6")).trim()+"," +
                            ""+((String) map.get("7")).trim()+"," +
                            "'"+((String) map.get("8")).trim()+"'," +
                            ""+((String) map.get("11")).trim()+"," +
                            ""+((String) map.get("12")).trim()+"," +
                            /*""+(int)Double.parseDouble(map.get("13").toString())+"," +
                            ""+(int)Double.parseDouble(map.get("14").toString())+"," +*/
                            ""+(String)(map.get("13"))+"," +
                            ""+(String)(map.get("14"))+"," +
                            "'"+((String) map.get("15")).trim()+"'," +
                            "'"+((String) map.get("9")).trim()+"'," +
                            ""+(String)(map.get("10"))+"," +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()" +
                            ");\n";
                }
            }
        }

        writaDate(addr+"冲压材料耗用定额.sql",fileDate);
    }
    //冲压工时数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public static void getCygsSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList(template);
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>0){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    /*if (i < 1) {
                        continue;
                    }*/
                    id = id+1;

                    fileDate += "INSERT INTO mes_working_procedure_time " +
                            "(working_procedure_time_id," +
                            "org_id," +
                            "project," +
                            "element_no," +
                            "working_procedure_code," +
                            "working_procedure_name," +
                            "operation_number," +
                            "mq_number," +
                            "ct," +
                            "status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time,working_procedure_type_name,workshop_name,men_type_name,capital_assets_no,machine_name,material_name) " +
                            "VALUES " +
                            "("+id+"," +
                            orgId+"," +
                            "'"+((String) map.get("1")).trim()+"'," +
                            "'"+(String) map.get("2")+"'," +
                            "'"+(String) map.get("4")+"'," +
                            "'"+(String) map.get("5")+"'," +
                            ""+(String) map.get("10")+"," +
                            ""+(String) map.get("12")+"," +
                            ""+(String) map.get("13")+"," +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "'2019-07-24 14:21:06'," +
                            "'[admin]管理员'," +
                            "'2019-07-24 14:21:06'," +
                            "'"+((String) map.get("6")).trim()+"'," +
                            "'"+((String) map.get("7")).trim()+"'," +
                            "'"+((String) map.get("11")).trim()+"'," +
                            "'"+((String) map.get("8")).trim()+"'," +
                            "'"+((String) map.get("9")).trim()+"'," +
                            "'"+((String) map.get("3")).trim()+"'" +
                            ");\n";
                }
            }
        }

        writaDate(addr+"02标准工时-冲压 - 副本sql.txt",fileDate);
    }
    //焊装工时数据都是武汉沿浦的（归属机构都是武汉沿浦）
    public static void getHzgsSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList(template);
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>0){//标题不需要
                for(int i=0;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    /*if (i < 1) {
                        continue;
                    }*/
                    id = id+1;

                    fileDate += "INSERT INTO mes_working_procedure_time " +
                            "(working_procedure_time_id," +
                            "org_id," +
                            "project," +
                            "element_no," +
                            "working_procedure_code," +
                            "working_procedure_name," +
                            "operation_number," +
                            "lf_number," +
                            "mq_number," +
                            "ct," +
                            "status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time,working_procedure_type_name," +
                            "workshop_name,men_type_name,capital_assets_no,machine_name,material_name) " +
                            "VALUES " +
                            "("+id+"," +
                            orgId+"," +
                            "'"+((String) map.get("1")).trim()+"'," +
                            "'"+(String) map.get("2")+"'," +
                            "'"+(String) map.get("4")+"'," +
                            "'"+(String) map.get("5")+"'," +
                            ""+(String) map.get("10")+"," +
                            ""+(String) map.get("12")+"," +
                            ""+(String) map.get("13")+"," +
                            ""+(String) map.get("14")+"," +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "'2019-07-24 14:21:06'," +
                            "'[admin]管理员'," +
                            "'2019-07-24 14:21:06'," +
                            "'"+((String) map.get("6")).trim()+"'," +
                            "'"+((String) map.get("7")).trim()+"'," +
                            "'"+((String) map.get("11")).trim()+"'," +
                            "'"+((String) map.get("8")).trim()+"'," +
                            "'"+((String) map.get("9")).trim()+"'," +
                            "'"+((String) map.get("3")).trim()+"'" +
                            ");\n";
                }
            }
        }

        writaDate(addr+"01标准工时-焊装 - 副本sql.txt",fileDate);
    }

    public static void writaDate(String path,String context) throws Exception{
        File file =new File(path);
        Writer out =new FileWriter(file);
        out.write(context);
        out.close();
    }

    //项目数据导入
    public static void getProjectSql() throws Exception{

        String str = "";
        //起步初始值
        Integer sum = 0;

        List list = new ArrayList();
        String fileDate = "";
        list = ExcelUtils.dataToList("D:\\sql文件生成\\武汉U8项目20190807.xls");
        if(null != list && list.size()>0){
            List<Map> mapList = (List<Map>) list.get(0); //根据sheet获取

            if(mapList!=null && mapList.size()>1){//标题不需要
                for(int i=3;i < mapList.size();i++) {
                    Map map = mapList.get(i);
                    if (i < 1) {
                        continue;
                    }
                    id = id+1;

                    fileDate += "INSERT INTO bsc_project " +
                            "(project_id ," +
                            "org_id," +
                            "project_type," +
                            "project_code," +
                            "project_name," +
                            "project_type_code," +
                            "project_type_name," +
                            "status," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time) " +
                            "VALUES " +
                            "("+id+"," +
                            orgId+"," +
                            "'"+((String) map.get("1")).trim()+"'," +
                            "'"+((String) map.get("2")).trim()+"'," +
                            "'"+((String) map.get("3")).trim()+"'," +
                            "'"+((String) map.get("5")).trim()+"'," +
                            "'"+((String) map.get("6")).trim()+"'," +
                            "0," +
                            "1," +
                            "'[admin]管理员'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()" +
                            ");\n";
                }
            }
        }

        writaDate(addr+"冲压材料耗用定额.sql",fileDate);
    }

}
