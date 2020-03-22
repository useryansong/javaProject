package com.xchinfo.erp.controller.sys.conf;

import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.entity.FileUploadEO;
import com.xchinfo.erp.sys.conf.service.FileUploadService;
import com.xchinfo.erp.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/sys/fileUpload")
public class FileUploadController extends BaseController {

    @Autowired
    private FileUploadService fileUploadService;


    @GetMapping("list")
    public Result<List<FileUploadEO>> list(Integer type){
        logger.info("======== FileUploadController.list(type ==> "+type+") ========");

        List<FileUploadEO> menuList = this.fileUploadService.selectTreeList(type);

        return new Result<List<FileUploadEO>>().ok(menuList);
    }


    /**
     * 下载模板文件
     * @param req
     * @param resp
     * @param id
     * @return
     */
    @GetMapping("downloadFile/{id}")
    public Result downloadPlanFile(HttpServletRequest req, HttpServletResponse resp, @PathVariable("id") Long id) {
        Result result = new Result();
        FileUploadEO fileUploadEO = this.fileUploadService.getById(id);
        if(fileUploadEO != null){
            FileUtils.downloadFile(resp, fileUploadEO.getName()+"."+fileUploadEO.getPostfix(), fileUploadEO.getPath());
        }else{
            result.setCode(500);
            result.setMsg("数据库配置出错");
        }
        return result;
    }
}
