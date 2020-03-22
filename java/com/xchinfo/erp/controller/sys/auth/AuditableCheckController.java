package com.xchinfo.erp.controller.sys.auth;


import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;

import java.io.Serializable;

/**
 * 检查用户是否可修改或删除指定数据
 *
 * @author roman.li
 * @date 2019-06-14
 * @update
 */
@RestController
@RequestMapping("/sys/audit")
public class AuditableCheckController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 检查用户权限
     *
     * @param entity
     * @param id
     * @return
     */
    @GetMapping("{entity}/{idField}/{id}")
    public Result check(@PathVariable("entity") String entity, @PathVariable("idField") String idField, @PathVariable("id") Serializable id){
        logger.info("======== AuditableCheckController.check(entity -> "+entity+", id -> "+id+") ========");

        String user = "[" + this.getUser().getUserName() + "]" + this.getUser().getRealName();
        boolean res = this.userService.checkAuditableInfo(entity, idField, id, user);

        return new Result<>().ok(res);
    }
}
