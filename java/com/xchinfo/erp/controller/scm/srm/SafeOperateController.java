package com.xchinfo.erp.controller.scm.srm;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yecat.core.utils.Result;

import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.srm.service.SafeOperateService;

/**
 * @author roman.li
 * @date 2019/3/13
 * @update
 */
@RestController
@RequestMapping("/srm/SafeOperate")
public class SafeOperateController extends BaseController {
    
	@Autowired
	private SafeOperateService safeOperateService;
    
    @PostMapping("getMachineList")
    public Result getMachineList(){
    	logger.info("======== SafeOperateController.getMachineList() ========");
		Map<String, Object> res = safeOperateService.getMachineList(getUser());
		return new Result().ok(res);
    }

    /*@GetMapping("sendMsg")
    public Result sendMsg(){
    	logger.info("======== SafeOperateController.sendMsg() ========");
		safeOperateService.sendMsg();
		return new Result();
    }*/
    
    
    @Scheduled(cron="0 */1 * * * *")
    public void sendMsg(){
        logger.info("======== SafeOperateController.sendMsg() ========");
        safeOperateService.sendMsg();

    }
    

    
}
