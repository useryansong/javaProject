package com.xchinfo.erp.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlErpCode {
    private static Integer id = 1010;
    private static String addr = "C:\\Users\\Administrator\\Desktop\\";
    private static Integer orgId = 3;
    private static String template = "C:\\Users\\Administrator\\Desktop\\冲压材料 BOM 消耗定额.xlsx";

    public static void main(String[] args) throws Exception {

    }
    //供应商数据
    public static void getGysErpCodeSql() throws Exception{

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

                    fileDate += "INSERT INTO bsc_supplier_erp_code " +
                            "(supplier_erp_code_id ," +
                            "org_id," +
                            "erpcode," +
                            "version," +
                            "created_by," +
                            "created_time," +
                            "last_modified_by," +
                            "last_modified_time) " +
                            "VALUES " +
                            "("+id+"," +
                            orgId+"," +
                            "'"+((String) map.get("1")).trim()+"'," +
                            "1," +
                            "'"+((String) map.get("1")).trim()+"'," +
                            "now()," +
                            "'[admin]管理员'," +
                            "now()" +
                            ");\n";
                }
            }
        }

        writaDate(addr+"erpcode.sql",fileDate);
    }

    public static void writaDate(String path,String context) throws Exception{
        File file =new File(path);
        Writer out =new FileWriter(file);
        out.write(context);
        out.close();
    }


}
