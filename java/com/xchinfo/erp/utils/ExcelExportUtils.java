package com.xchinfo.erp.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class ExcelExportUtils {
    public static void print(String[] colunm, List<Map<Object,Object>> list, HttpServletResponse response,String filename){
        // File excel = new File(classPath + File.separator + "ecoo-product-inventory-english.xlsx");
        //file = new FileInputStream(excel);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        //统一设置样式
        HSSFFont font1 = wb.createFont();
        font1.setFontName("仿宋_GB2312");
        font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        font1.setFontHeightInPoints((short) 16);
        HSSFFont font2 = wb.createFont();
        font2.setFontHeightInPoints((short) 12);
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setFont(font1);
        cellStyle1.setFillForegroundColor(HSSFColor.DARK_RED.index);
        cellStyle1.setFillForegroundColor((short) 13);
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setFont(font2);

        for (int i = 0;i < colunm.length;i++){
            String colName=colunm[i];
            sheet.getRow(0).createCell(i).setCellValue(colName);
            sheet.getRow(0).getCell(i).setCellStyle(cellStyle1);
            sheet.setColumnWidth(i,30*256);
        }
        sheet.getRow(0).setHeightInPoints(25);
        for (int j=0;j<list.size();j++){
            sheet.createRow(j+1);
            for (int i = 0;i < colunm.length;i++){
                String colName=(String)list.get(j).get(colunm[i]);
                sheet.getRow(j+1).createCell(i).setCellValue(colName);
                sheet.getRow(j+1).getCell(i).setCellStyle(cellStyle2);

            }
            sheet.getRow(j+1).setHeightInPoints(20);
        }


        OutputStream out = null;
        try {

            response.reset(); // 清除buffer缓存
            out = response.getOutputStream();
            String fileName = filename + ".xls";
            String downloadFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
            response.addHeader("Ajax-Download-FileName", downloadFileName);

            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
