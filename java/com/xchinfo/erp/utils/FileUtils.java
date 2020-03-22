package com.xchinfo.erp.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author zhongye
 * @date 2019/5/10
 */
public class FileUtils {

    /**
     *  下载文件
     * @param resp  response响应对象
     * @param name  文件名称(含后缀)
     * @param realPath  存放文件的路径(不包含文件名的路径)
     */
    public static void downloadFile(HttpServletResponse resp, String name, String realPath) {
        String path = realPath + File.separator + name;
        File file = new File(path);

        String fileName = null;
        try {
            fileName = URLEncoder.encode(name,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        resp.reset(); // 清除buffer缓存
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        resp.addHeader("Ajax-Download-FileName", fileName);

        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            resp.setHeader("Content-Length", String.valueOf(bis.available()));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
