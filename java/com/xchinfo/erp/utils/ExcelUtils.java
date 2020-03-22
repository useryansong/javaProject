package com.xchinfo.erp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yecat.core.exception.BusinessException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhongye
 * @date 2019/5/10
 */
public class ExcelUtils {

    // 获取单元格中的值
    private static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
//        // 把数字当成String来读，避免出现1读成1.0的情况
//        if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
//            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//        }
        // 判断数据的类型
        switch (cell.getCellType()){
            case HSSFCell.CELL_TYPE_NUMERIC: //数字
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date tempValue = cell.getDateCellValue();
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    cellValue = simpleFormat.format(tempValue);
                }else {
                    // 把数字当成String来读，避免出现1读成1.0的情况
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getStringCellValue());
                }
                break;
            case HSSFCell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case HSSFCell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case HSSFCell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    // 获取要存放文件的路径(临时存储)
    private static String getFilePath(String fileName)  throws FileNotFoundException {
        String guid = UUID.randomUUID().toString().replaceAll("-", "");
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String fileNameWithoutExt = fileName.replaceAll(extName, "");
        String fileNewName = fileNameWithoutExt + "-" + guid + extName;
        // 获取项目根目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) {
            path = new File("");
        }
        // 生成上传目录
        File upload = new File(path.getAbsolutePath(),"statics/upload/xls/");
        if(!upload.exists()) {
            upload.mkdirs();
        }
        String filePath = upload + File.separator + fileNewName;
        return filePath;
    }

    // xls, xlsx文件的数据封装成List返回,适合多sheet
    public static List dataToList(String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);
        Workbook workbook = null;

        try {
            // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            workbook = WorkbookFactory.create(is);
//            if(filePath.endsWith(".xls")){
//                // 2003
//                workbook = new HSSFWorkbook(is);
//            }else if(filePath.endsWith(".xlsx")){
//                // 2007
//                workbook = new XSSFWorkbook(is);
//            }
        } catch (IOException e) {

        }

        List list = new ArrayList();

        if(workbook != null) {
            for(int sheetNum=0; sheetNum<workbook.getNumberOfSheets(); sheetNum++){
                // 获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }

                // 获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                // 获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();

                List<Map> mapList = new ArrayList<Map>();

                // 循环所有行,从最左侧开始,起始值为0
                for (int rowNum=firstRowNum; rowNum<=lastRowNum; rowNum++) {
                    // 获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    // 获得当前行的开始列,从0开始
                    int firstCellNum = row.getFirstCellNum();
                    // 获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    String[] cells = new String[row.getLastCellNum()];

                    Map map = new HashMap();

                    // 循环当前行
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                        if(getCellValue(cell) != null) {
                            cells[cellNum] = getCellValue(cell).trim();
                        }
                        map.put("" + cellNum, cells[cellNum]);
                    }
                    mapList.add(map);
                }
                list.add(mapList);
            }
            workbook.close();
            is.close();
        }
        return list;
    }

    /**
     *  解析获取xls, xlsx的有效数据封装成List
     * @param request  request响应对象
     */
    public static List getExcelData(HttpServletRequest request) throws BusinessException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartHttpServletRequest.getFile("file");

        if(mf == null){
            throw new BusinessException("没有文件对象,请正确设置！");
        }

        List list = new ArrayList();
        try {
            byte[] content = mf.getBytes();
            //文件放置本地
            String filePath = getFilePath(mf.getOriginalFilename());
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(content);
            outputStream.close();
            File file = new File(filePath);
            if(file.isFile() && file.exists()){ // 判断文件是否存在
                list = dataToList(filePath);
                new File(filePath).delete(); // 删除本地放置的临时文件
            }else{
                throw new BusinessException("找不到指定文件！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Object getPropertyValue(Class cls, Object obj, Field field) {
        //设置可以访问私有变量
        field.setAccessible(true);
        //获取属性的名字
        String name = field.getName();
        //将属性名字的首字母大写
        name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
        //整合出属性的getter方法
        Method m = null;
        try {
            m = cls.getMethod("get"+name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            object = m.invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return object;
    }

    // 格式化日期为字符串
    private static String formatDateToString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    //响应到客户端
    private static void setResponseHeader(HttpServletResponse response, String fileName, Workbook wb) {
        try {
            String downloadFileName = URLEncoder.encode(fileName,"UTF-8");
            response.reset(); // 清除buffer缓存
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
            response.addHeader("Ajax-Download-FileName", downloadFileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setCellValue(Cell cell, Object obj, JSONObject field) {
        Class cls = obj.getClass();
        String property = (String) field.get("property");
        //得到所有属性
        Field[] selfProperties = cls.getDeclaredFields();
        Class superClass = cls.getSuperclass();
        Field[] properties;
        if (superClass != null) {
            Field[] superProperties = superClass.getDeclaredFields();
            properties = ArrayUtils.addAll(selfProperties, superProperties);
        } else {
            properties = selfProperties;
        }

        for(int j=0; j<properties.length; j++) {
            if(properties[j].getName().equals(property)) {
                Object object = getPropertyValue(cls, obj, properties[j]);
                if(object instanceof String) {
                    cell.setCellValue((String) object);
                    break;
                }
                if(object instanceof Integer) {
                    cell.setCellValue((Integer) object);
                    break;
                }
                if(object instanceof Double) {
                    cell.setCellValue((Double) object);
                    break;
                }
                if(object instanceof Long) {
                    cell.setCellValue((Long) object);
                    break;
                }
                if(object instanceof Date) {
                    String pattern = (String) field.get("pattern");
                    if(pattern!=null && !pattern.trim().equals("")) {
                        cell.setCellValue(formatDateToString((Date)object, pattern));
                    }else{
                        cell.setCellValue(formatDateToString((Date)object, "yyyy/MM/dd"));
                    }
                    break;
                }
            }
        }
    }

    /**
     * 无模板文件直接导出数据成Excel到浏览器中
     * @param response
     * @param dataList 内容
     * @param jsonObject 配置的json文件转化成的JSONObject对象
     * @return
     */
    public static void exportExcel(HttpServletResponse response, List dataList, JSONObject jsonObject) throws BusinessException {
        String fileName = jsonObject.getString("fileName");
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        String sheetName = jsonObject.getString("sheetName");;
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;
        //创建标题
        JSONArray fields = (JSONArray) jsonObject.get("fields");
        for(int i=0; i<fields.size(); i++){
            JSONObject field = fields.getJSONObject(i);
            String name = field.getString("name");
            cell = row.createCell(i);
            cell.setCellValue(name);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, name.getBytes().length*2*128); //按照标题的长度设置列宽
        }

        for(int i=0; i<dataList.size(); i++) {
            row = sheet.createRow(i+1);
            for(int k=0; k<fields.size(); k++){
                JSONObject field = fields.getJSONObject(k);
                int cellIndex = (int) field.get("cellIndex");
                cell = row.createCell(cellIndex);
                cell.setCellStyle(style);
                setCellValue(cell, dataList.get(i), field);
            }
        }

        //响应到客户端
        setResponseHeader(response, fileName, wb);
    }

    /**
     * 无模板特殊格式文件直接导出数据成Excel到浏览器中
     */
    public static void exportExcels(HttpServletResponse response, List dataList, String fullName,String inventoryDate,JSONObject jsonObject) throws BusinessException {
        String fileName = jsonObject.getString("fileName");
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        String sheetName = jsonObject.getString("sheetName");;
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);


        HSSFRow row0 = sheet.createRow(0);
        HSSFCell cell0 = row0.createCell((short) 0);
        cell0.setCellValue(fullName);

        CellRangeAddress region0 = new CellRangeAddress(0, 0, (short) 0, (short) 8); //参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列
        sheet.addMergedRegion(region0);

        HSSFCellStyle cellStyle0 = wb.createCellStyle();
        HSSFFont font0 = wb.createFont();
        font0.setFontName("宋体");
        font0.setFontHeightInPoints((short) 16);  //字体大小
        cellStyle0.setFont(font0);
        cellStyle0.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cell0.setCellStyle(cellStyle0);


        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = row1.createCell((short) 0);
        cell1.setCellValue("盘点差异表"+inventoryDate);

        CellRangeAddress region1 = new CellRangeAddress(1, 1, (short) 0, (short) 8); //参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列
        sheet.addMergedRegion(region1);

        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        HSSFFont font1 = wb.createFont();
        font1.setFontName("宋体");
        font1.setFontHeightInPoints((short) 14);  //字体大小
        cellStyle1.setFont(font1);
        cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cell1.setCellStyle(cellStyle1);

        // 第三步，在sheet中添加表头第2行
        HSSFRow row = sheet.createRow(2);


        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式



        //声明列对象
        HSSFCell cell = null;
        //创建标题
        JSONArray fields = (JSONArray) jsonObject.get("fields");
        for(int i=0; i<fields.size(); i++){
            JSONObject field = fields.getJSONObject(i);
            String name = field.getString("name");
            cell = row.createCell(i);
            cell.setCellValue(name);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, name.getBytes().length*2*128); //按照标题的长度设置列宽
        }
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(8, 11000);
        for(int i=2; i<dataList.size(); i++) {
            row = sheet.createRow(i+1);
            for(int k=0; k<fields.size(); k++){
                JSONObject field = fields.getJSONObject(k);
                int cellIndex = (int) field.get("cellIndex");
                cell = row.createCell(cellIndex);
                cell.setCellStyle(style);
                setCellValue(cell, dataList.get(i), field);
            }
        }

        //响应到客户端
        setResponseHeader(response, fileName, wb);
    }

    // 读取json文件并转化为字符串
    private static String jsonRead(File file){
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
    }

    /**
     * 解析配置的.json文件
     * @param fileName 文件名称(带后缀的文件名称)
     * @return
     */
    public static JSONObject parseJsonFile(String fileName) throws BusinessException {
        ClassPathResource resource = new ClassPathResource("statics/json/"+ fileName);
        JSONObject jsonObject = null;
        try {
            InputStream inputStream = resource.getInputStream();
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            File file = File.createTempFile(prefix, suffix);
            FileUtils.copyInputStreamToFile(inputStream, file);
            if(!file.exists()) {
                throw new BusinessException(fileName + "文件不存在,请先配置该文件!");
            }
            String jsonData = jsonRead(file);
            jsonObject = JSONObject.parseObject(jsonData);
            file.delete();
            inputStream.close();
        } catch (FileNotFoundException e) {
            throw new BusinessException(fileName + "文件不存在,请先配置该文件!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    // 获取Workbook
    private static Workbook getWorkbook(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        try {
            wb = WorkbookFactory.create(in);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        in.close();
        return wb;
    }

    /**
     * 根据模板文件导出数据成Excel到浏览器中(供给U8等系统使用Excel文件)
     * @param response
     * @param templateFileUrl 模板文件路径(不带文件名)
     * @param templateFileName 模板文件名称
     * @param jsonObject 配置的json文件转化成的JSONObject对象
     * @param dataList 要导出的数据
     * @return
     */
    public static void exportByTemplate(HttpServletResponse response, String templateFileUrl, String templateFileName,
                              JSONObject jsonObject, List dataList) throws BusinessException {
        File templateFile = new File(templateFileUrl + File.separator + templateFileName);
        if(!templateFile.exists()) {
            try {
                response.addHeader("msg", URLEncoder.encode(templateFileName + "模板文件不存在!","UTF-8"));
                throw new BusinessException(templateFileName + "模板文件不存在!");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            // 复制模板文件到指定文件夹下
            String filePath = getFilePath(templateFileName);
            File copyFile = new File(filePath);
            FileUtils.copyFile(templateFile, copyFile);
            // 读取复制后的模板文件
            Workbook workBook = getWorkbook(copyFile);
            String sheetName = (String) jsonObject.get("sheetName");
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheet(sheetName);
            // 删除原有数据，除了属性列
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            if(rowNumber > 0) {
                for (int i = 1; i <= rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    if(row != null) {
                        sheet.removeRow(row);
                    }
                }
            }

            for(int i=0; i<dataList.size(); i++) {
                Row row = sheet.createRow(i+1);
                JSONArray fields = (JSONArray) jsonObject.get("fields");
                for(int k=0; k<fields.size(); k++){
                    JSONObject field = fields.getJSONObject(k);
                    int cellIndex = (int) field.get("cellIndex");
                    Cell cell = row.createCell(cellIndex);
                    String value = (String) field.get("value");
                    if(value!=null && value.trim()!="") {
                        cell.setCellValue(value);
                    } else {
                        setCellValue(cell, dataList.get(i), field);
                    }
                }
            }

            // 响应到客户端
            setResponseHeader(response, (String) jsonObject.get("fileName"), workBook);
            new File(filePath).delete(); // 删除本地放置的临时文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成二维码
    private static void generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        // 生成二维码目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        File qrCode = new File(path.getAbsolutePath(),"statics/qrcode/" + text + ".png");
        if(!qrCode.getParentFile().exists()){
            qrCode.getParentFile().mkdirs();
        }
        if(!qrCode.exists()) {
            qrCode.createNewFile();
        }
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCode.toPath());
    }

    /**
     * 根据模板文件导出数据成Excel到浏览器中(供给U8等系统使用Excel文件)
     * @param response
     * @param templateFileUrl 模板文件路径(不带文件名)
     * @param templateFileName 模板文件名称
     * @param jsonObject 配置的json文件转化成的JSONObject对象
     * @param dataList 要导出的数据
     * @return
     */
    public static void printExport(HttpServletResponse response, String templateFileUrl, String templateFileName,
                                   JSONObject jsonObject, List dataList) throws BusinessException, FileNotFoundException {
        File templateFile = new File(templateFileUrl + File.separator + templateFileName);
        if(!templateFile.exists()) {
            try {
                response.addHeader("msg", URLEncoder.encode(templateFileName + "模板文件不存在!","UTF-8"));
                throw new BusinessException(templateFileName + "模板文件不存在!");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            int pageSize = (Integer) jsonObject.get("pageSize");
            int pageNum = dataList.size() / pageSize;
            int rest = dataList.size() % pageSize;
            if(rest > 0) {
                pageNum += 1;
            }
            // 复制模板文件到指定文件夹下
            String filePath = getFilePath(templateFileName);
            File copyFile = new File(filePath);
            FileUtils.copyFile(templateFile, copyFile);
            // 读取复制后的模板文件
            Workbook workBook = getWorkbook(copyFile);
            String sheetName = (String) jsonObject.get("sheetName");
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheet(sheetName);
            int titleRow = (Integer) jsonObject.get("titleRow");
            int startRow = (Integer) jsonObject.get("startRow");

            // 生成二维码图片
            Object obj = dataList.get(0);
            Class cls = obj.getClass();
            String qrCodeProperty = (String) jsonObject.get("qrCodeProperty");
            Field property = cls.getDeclaredField(qrCodeProperty);
            String qrCodeContent = getPropertyValue(cls, obj, property).toString();
            generateQRCodeImage(qrCodeContent, 150, 150);

            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File qrCode = new File(path.getAbsolutePath(),"statics/qrcode/" + qrCodeContent + ".png");
            BufferedImage bufferImg = ImageIO.read(qrCode);
            ImageIO.write(bufferImg, "png", byteArrayOut);

            JSONObject qrCodeLocation = (JSONObject) jsonObject.get("qrCodeLocation");
            int col1 = (int) qrCodeLocation.get("col1");
            int row1 = (int) qrCodeLocation.get("row1");
            int col2 = (int) qrCodeLocation.get("col2");
            int row2 = (int) qrCodeLocation.get("row2");

            if(pageNum == 1) {
                Row row = sheet.getRow(titleRow);
                JSONArray titles = (JSONArray) jsonObject.get("titles");
                for(int k=0; k<titles.size(); k++){
                    JSONObject field = titles.getJSONObject(k);
                    int cellIndex = (int) field.get("cellIndex");
                    Cell cell = row.getCell(cellIndex);
                    String value = (String) field.get("value");
                    if(value!=null && value.trim()!="") {
                        cell.setCellValue(value);
                    } else {
                        setCellValue(cell, dataList.get(0), field);
                    }
                }

                if (templateFileName.endsWith(".xls")) { // 2003
                    HSSFPatriarch patriarch = (HSSFPatriarch) sheet.createDrawingPatriarch();
                    JSONObject HSSFClientAnchor = (JSONObject) qrCodeLocation.get("HSSFClientAnchor");
                    int dx1 = (int) HSSFClientAnchor.get("dx1");
                    int dy1 = (int) HSSFClientAnchor.get("dy1");
                    int dx2 = (int) HSSFClientAnchor.get("dx2");
                    int dy2 = (int) HSSFClientAnchor.get("dy2");
                    HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2,(short) col1, row1, (short) col2, row2);
                    anchor.setAnchorType(ClientAnchor.AnchorType.byId(3));
                    patriarch.createPicture(anchor, workBook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
                }

                if (templateFileName.endsWith(".xlsx")) { // 2007
                    XSSFDrawing patriarch = (XSSFDrawing) sheet.createDrawingPatriarch();
                    JSONObject XSSFClientAnchor = (JSONObject) qrCodeLocation.get("XSSFClientAnchor");
                    int dx1 = (int) XSSFClientAnchor.get("dx1");
                    int dy1 = (int) XSSFClientAnchor.get("dy1");
                    int dx2 = (int) XSSFClientAnchor.get("dx2");
                    int dy2 = (int) XSSFClientAnchor.get("dy2");
                    XSSFClientAnchor anchor = new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1,  col2, row2);
                    patriarch.createPicture(anchor, workBook.addPicture(byteArrayOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG));
                }

                for(int i=0; i<dataList.size(); i++) {
                    row = sheet.getRow(startRow+i);
                    JSONArray fields = (JSONArray) jsonObject.get("fields");
                    for(int k=0; k<fields.size(); k++){
                        JSONObject field = fields.getJSONObject(k);
                        int cellIndex = (int) field.get("cellIndex");
                        Cell cell = row.getCell(cellIndex);
                        String value = (String) field.get("value");
                        if(value!=null && value.trim()!="") {
                            cell.setCellValue(value);
                        } else {
                            setCellValue(cell, dataList.get(i), field);
                        }
                    }
                }

                JSONObject voucherNoJsonObject = (JSONObject) jsonObject.get("voucherNo");
                row = sheet.getRow((Integer) voucherNoJsonObject.get("rowIndex"));
                Cell cell =  row.getCell((Integer) voucherNoJsonObject.get("cellIndex"));
                property = cls.getDeclaredField((String) voucherNoJsonObject.get("property"));
                Object erpVoucherNo3 = getPropertyValue(cls, obj, property);
                if(erpVoucherNo3!=null && !erpVoucherNo3.toString().trim().equals("")) {
                    cell.setCellValue(erpVoucherNo3.toString());
                }
            } else if(pageNum > 1){
                if(rest == 0) {
                    for(int o=1; o<=pageNum; o++) {
                        if(o == 1) {
                            List tempList = new ArrayList();
                            for(int r=0; r<o*pageSize; r++) {
                                tempList.add(dataList.get(r));
                            }

                            Row row = sheet.getRow(titleRow);
                            JSONArray titles = (JSONArray) jsonObject.get("titles");
                            for(int k=0; k<titles.size(); k++){
                                JSONObject field = titles.getJSONObject(k);
                                int cellIndex = (int) field.get("cellIndex");
                                Cell cell = row.getCell(cellIndex);
                                String value = (String) field.get("value");
                                if(value!=null && value.trim()!="") {
                                    cell.setCellValue(value);
                                } else {
                                    setCellValue(cell, tempList.get(0), field);
                                }
                            }

                            for(int i=0; i<tempList.size(); i++) {
                                row = sheet.getRow(startRow+i);
                                JSONArray fields = (JSONArray) jsonObject.get("fields");
                                for(int k=0; k<fields.size(); k++){
                                    JSONObject field = fields.getJSONObject(k);
                                    int cellIndex = (int) field.get("cellIndex");
                                    Cell cell = row.getCell(cellIndex);
                                    String value = (String) field.get("value");
                                    if(value!=null && value.trim()!="") {
                                        cell.setCellValue(value);
                                    } else {
                                        setCellValue(cell, tempList.get(i), field);
                                    }
                                }
                            }

                            JSONObject voucherNoJsonObject = (JSONObject) jsonObject.get("voucherNo");
                            row = sheet.getRow((Integer) voucherNoJsonObject.get("rowIndex"));
                            Cell cell =  row.getCell((Integer) voucherNoJsonObject.get("cellIndex"));
                            cell.setCellValue(qrCodeContent);
                        } else if(o>1 && o<=pageNum) {
                            sheet = workBook.cloneSheet(0);
                            workBook.setSheetName(o-1, "sheet" + o);

                            List tempList = new ArrayList();
                            for(int r=(o-1)*pageSize; r<o*pageSize; r++) {
                                tempList.add(dataList.get(r));
                            }

                            for(int i=0; i<tempList.size(); i++) {
                                Row row = sheet.getRow(startRow+i);
                                JSONArray fields = (JSONArray) jsonObject.get("fields");
                                for(int k=0; k<fields.size(); k++){
                                    JSONObject field = fields.getJSONObject(k);
                                    int cellIndex = (int) field.get("cellIndex");
                                    Cell cell = row.getCell(cellIndex);
                                    String value = (String) field.get("value");
                                    if(value!=null && value.trim()!="") {
                                        cell.setCellValue(value);
                                    } else {
                                        setCellValue(cell, tempList.get(i), field);
                                    }
                                }
                            }
                        }
                    }
                } else if(rest > 0) {
                    for(int o=1; o<=pageNum; o++) {
                        if(o == 1) {
                            List tempList = new ArrayList();
                            for(int r=0; r<o*pageSize; r++) {
                                tempList.add(dataList.get(r));
                            }

                            Row row = sheet.getRow(titleRow);
                            JSONArray titles = (JSONArray) jsonObject.get("titles");
                            for(int k=0; k<titles.size(); k++){
                                JSONObject field = titles.getJSONObject(k);
                                int cellIndex = (int) field.get("cellIndex");
                                Cell cell = row.getCell(cellIndex);
                                String value = (String) field.get("value");
                                if(value!=null && value.trim()!="") {
                                    cell.setCellValue(value);
                                } else {
                                    setCellValue(cell, tempList.get(0), field);
                                }
                            }

                            for(int i=0; i<tempList.size(); i++) {
                                row = sheet.getRow(startRow+i);
                                JSONArray fields = (JSONArray) jsonObject.get("fields");
                                for(int k=0; k<fields.size(); k++){
                                    JSONObject field = fields.getJSONObject(k);
                                    int cellIndex = (int) field.get("cellIndex");
                                    Cell cell = row.getCell(cellIndex);
                                    String value = (String) field.get("value");
                                    if(value!=null && value.trim()!="") {
                                        cell.setCellValue(value);
                                    } else {
                                        setCellValue(cell, tempList.get(i), field);
                                    }
                                }
                            }

                            JSONObject voucherNoJsonObject = (JSONObject) jsonObject.get("voucherNo");
                            row = sheet.getRow((Integer) voucherNoJsonObject.get("rowIndex"));
                            Cell cell =  row.getCell((Integer) voucherNoJsonObject.get("cellIndex"));
                            cell.setCellValue(qrCodeContent);
                        } else if(o>1 && o<pageNum) {
                            sheet = workBook.cloneSheet(0);
                            workBook.setSheetName(o-1, "sheet" + o);

                            List tempList = new ArrayList();
                            for(int r=(o-1)*pageSize; r<o*pageSize; r++) {
                                tempList.add(dataList.get(r));
                            }

                            for(int i=0; i<tempList.size(); i++) {
                                Row row = sheet.getRow(startRow+i);
                                JSONArray fields = (JSONArray) jsonObject.get("fields");
                                for(int k=0; k<fields.size(); k++){
                                    JSONObject field = fields.getJSONObject(k);
                                    int cellIndex = (int) field.get("cellIndex");
                                    Cell cell = row.getCell(cellIndex);
                                    String value = (String) field.get("value");
                                    if(value!=null && value.trim()!="") {
                                        cell.setCellValue(value);
                                    } else {
                                        setCellValue(cell, tempList.get(i), field);
                                    }
                                }
                            }
                        } else if(o == pageNum) {
                            sheet = workBook.cloneSheet(0);
                            workBook.setSheetName(o-1, "sheet" + o);

                            List tempList = new ArrayList();
                            for(int r=(o-1)*pageSize; r<dataList.size(); r++) {
                                tempList.add(dataList.get(r));
                            }

                            // 清空数据内容
                            int rowNumber = sheet.getLastRowNum();
                            if(rowNumber > 0) {
                                for (int i = startRow; i <= rowNumber; i++) {
                                    Row row = sheet.getRow(i);
                                    if(row != null) {
                                        JSONArray fields = (JSONArray) jsonObject.get("fields");
                                        for(int k=0; k<fields.size(); k++){
                                            JSONObject field = fields.getJSONObject(k);
                                            int cellIndex = (int) field.get("cellIndex");
                                            Cell cell = row.getCell(cellIndex);
                                            cell.setCellValue("");
                                        }
                                    }
                                }
                            }

                            for(int i=0; i<tempList.size(); i++) {
                                Row row = sheet.getRow(startRow+i);
                                JSONArray fields = (JSONArray) jsonObject.get("fields");
                                for(int k=0; k<fields.size(); k++){
                                    JSONObject field = fields.getJSONObject(k);
                                    int cellIndex = (int) field.get("cellIndex");
                                    Cell cell = row.getCell(cellIndex);
                                    String value = (String) field.get("value");
                                    if(value!=null && value.trim()!="") {
                                        cell.setCellValue(value);
                                    } else {
                                        setCellValue(cell, tempList.get(i), field);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 响应到客户端
            setResponseHeader(response, (String) jsonObject.get("fileName"), workBook);
            new File(filePath).delete(); // 删除本地放置的临时文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
