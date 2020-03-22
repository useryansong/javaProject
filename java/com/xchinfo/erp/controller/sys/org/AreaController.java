package com.xchinfo.erp.controller.sys.org;


import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.org.entity.AreaEO;
import com.xchinfo.erp.sys.org.service.AreaService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;

import java.util.HashMap;
import java.util.List;

/**
 * @author roman.li
 * @project wms-web
 * @date 2018/6/8 18:42
 * @update
 */
@RestController
@RequestMapping("/sys/area")
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    /**
     * 查找
     *
     * @return
     */
    @GetMapping("list")
    @RequiresPermissions("sys:area:info")
    public Result<List<AreaEO>> list(){
        logger.info("======== AreaController.list() ========");

        List<AreaEO> areas = this.areaService.selectTreeList(new HashMap<>(1));

        return new Result<List<AreaEO>>().ok(areas);
    }

    /**
     * 根据主键查找
     *
     * @param areaId
     * @return
     */
    @GetMapping("{areaId}")
    @RequiresPermissions("sys:area:info")
    public Result<AreaEO> info(@PathVariable("areaId") Long areaId){
        logger.info("======== AreaController.info(areaId => "+areaId+") ========");

        AreaEO area = this.areaService.getById(areaId);

        return new Result<AreaEO>().ok(area);
    }

    /**
     * 创建
     *
     * @param area
     * @return
     */
    @PostMapping
    @RequiresPermissions("sys:area:create")
    @OperationLog("创建区域")
    public Result create(@RequestBody AreaEO area){
        logger.info("======== AreaController.create(AreaCode => "+area.getAreaCode()+") ========");

        this.areaService.save(area);

        return new Result();
    }

    /**
     * 更新
     *
     * @param area
     * @return
     */
    @PutMapping
    @RequiresPermissions("sys:area:update")
    @OperationLog("更细区域")
    public Result update(@RequestBody AreaEO area){
        logger.info("======== AreaController.update(AreaCode => "+area.getAreaCode()+") ========");

        this.areaService.updateById(area);

        return new Result();
    }

    /**
     * 删除
     *
     * @param areaId
     * @return
     */
    @DeleteMapping("{areaId}")
    @ResponseBody
    @OperationLog("删除区域")
    public Result delete(@PathVariable("areaId") Long areaId){
        logger.info("======== AreaController.delete(areaId => "+areaId+") ========");

        this.areaService.removeById(areaId);

        return new Result();
    }
}
