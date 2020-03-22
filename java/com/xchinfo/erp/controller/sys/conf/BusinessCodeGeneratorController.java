package com.xchinfo.erp.controller.sys.conf;


import com.xchinfo.erp.sys.conf.service.BusinessCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.AssertUtils;

import java.util.Map;

/**
 * 业务编码生成
 *
 * @author roman.li
 * @date 2018/12/12
 * @update
 */
@RestController
@RequestMapping("/sys/code")
public class BusinessCodeGeneratorController {

    @Autowired
    private BusinessCodeGenerator businessCodeGenerator;

    /**
     * 生成业务编码
     *
     * @param params
     * @return
     */
    @PostMapping("generate")
    public Result generate(@RequestBody Map params){
        String ruleCode = (String) params.get("rule");
        AssertUtils.isBlank(ruleCode);

        Object entity = params.get("entity");

        String code = this.businessCodeGenerator.generateNextCodeNoOrgId(ruleCode, entity);

        return new Result().ok(code);
    }

    @GetMapping("getErpVoucherNo")
    public Result getErpVoucherNo(@RequestBody Map map) {
        String syncCode = (String) map.get("syncCode");
        return new Result().ok(this.businessCodeGenerator.getErpVoucherNo(syncCode));
    }
}
