package com.xchinfo.erp.controller.sys.auth;


import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.auth.entity.IdentityEO;
import com.xchinfo.erp.sys.auth.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;

/**
 * @author roman.li
 * @date 2017/10/30
 * @update
 */
@RestController
@RequestMapping("/sys/identity")
public class IdentityController extends BaseController {

    @Autowired
    private IdentityService identityService;

    /**
     * 根据用户信息查找授权信息
     *
     * @param userId
     * @return
     */
    @GetMapping("{userId}")
    public Result<IdentityEO> info(@PathVariable("userId") Long userId){
        logger.info("======== IdentityController.info(userId => "+userId+") ========");

        return this.identityService.selectByUserId(userId);
    }

    /**
     * 创建用户授权
     *
     * @param identity
     * @return
     */
    @PostMapping
    public Result create(@RequestBody IdentityEO identity){
        logger.info("======== IdentityController.create(userId => "+identity.getUserId()+") ========");

        this.identityService.save(identity);

        return new Result();
    }

    /**
     * 更新授权信息
     *
     * @param identity
     * @return
     */
    @PutMapping
    public Result update(@RequestBody IdentityEO identity){
        logger.info("======== IdentityController.update(userId => "+identity.getUserId()+") ========");

        this.identityService.updateById(identity);

        return new Result();
    }
}
