package com.xchinfo.erp.controller.sys.auth;


import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.conf.service.ParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author roman.li
 * @date 2018/11/20
 * @update
 */
@RestController
@RequestMapping("/system")
public class SystemController extends BaseController {

    @Autowired
    private ParamsService paramsService;

    /**
     * 获取系统相关信息
     *
     * @return
     */
    @GetMapping("info")
    public Result info(){
        logger.info("======== SystemController.info() ========");

        Map<String, String> params = new HashMap<>();
        params.put("appName", this.paramsService.getParamByKey("sys_app_name"));
        params.put("appMiniName", this.paramsService.getParamByKey("sys_app_name_mini"));
        params.put("companyName", this.paramsService.getParamByKey("sys_app_company"));
        params.put("copyright", this.paramsService.getParamByKey("sys_app_copyright"));

        return new Result().ok(params);
    }
}
