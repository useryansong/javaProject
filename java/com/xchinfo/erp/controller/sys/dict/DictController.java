package com.xchinfo.erp.controller.sys.dict;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xchinfo.erp.annotation.OperationLog;
import com.xchinfo.erp.controller.BaseController;
import com.xchinfo.erp.sys.dict.entity.DictEO;
import com.xchinfo.erp.sys.dict.service.DictService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yecat.core.utils.Result;
import org.yecat.core.validator.ValidatorUtils;
import org.yecat.core.validator.group.AddGroup;
import org.yecat.core.validator.group.DefaultGroup;
import org.yecat.core.validator.group.UpdateGroup;
import org.yecat.mybatis.utils.Criteria;

import java.util.List;

/**
 * 字典Controller
 *
 * @author roman.li
 * @date 2017/10/18
 * @update
 */
@RestController
@RequestMapping("/sys/dict")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    /**
     * 根据查询条件查询字典
     *
     * @param criteria
     * @return
     */
    @PostMapping("page")
    @RequiresPermissions("sys:dict:info")
    public Result<IPage<DictEO>> page(@RequestBody Criteria criteria){
        logger.info("========= DictController.page() =========");

        IPage<DictEO> page = this.dictService.selectPage(criteria);

        return new Result<IPage<DictEO>>().ok(page);
    }

    /**
     * 查找字典列表
     *
     * @return
     */
    @GetMapping("list")
    public Result<List<DictEO>> list(){
        logger.info("========= DictController.list() =========");

        List<DictEO> dicts = this.dictService.list();

        return new Result<List<DictEO>>().ok(dicts);
    }

    /**
     * 根据字典编码查找字典项
     *
     * @param dictCode
     * @return
     */
    @GetMapping("find/{dictCode}")
    public Result find(@PathVariable("dictCode") String dictCode){
        logger.info("========= DictController.list(dictCode => "+dictCode+") =========");

        List<DictEO> dicts = this.dictService.selectByDictCode(dictCode);

        return new Result().ok(dicts);
    }

    /**
     * 根据字典编码查找字典
     *
     * @param dictId
     * @return
     */
    @GetMapping("{dictId}")
    @RequiresPermissions("sys:dict:info")
    public Result<DictEO> info(@PathVariable Long dictId){
        logger.info("========= DictController.info(dictId => "+dictId+") =========");

        DictEO dict = this.dictService.getById(dictId);

        return new Result<DictEO>().ok(dict);
    }

    /**
     * 新增字典
     *
     * @param dict
     * @return
     */
    @PostMapping
    @OperationLog("新增字典")
    @RequiresPermissions("sys:dict:create")
    public Result create(@RequestBody DictEO dict){
        logger.info("========= DictController.create(dict => "+dict.getDictCode()+") =========");

        // 校验输入
        ValidatorUtils.validateEntity(dict, AddGroup.class, DefaultGroup.class);

        this.dictService.save(dict);

        return new Result();
    }

    /**
     * 更新字典
     *
     * @param dict
     * @return
     */
    @PutMapping
    @OperationLog("更新字典")
    @RequiresPermissions("sys:dict:update")
    public Result update(@RequestBody DictEO dict){
        logger.info("========= DictController.update(dict => "+dict.getDictCode()+") =========");

        // 校验输入
        ValidatorUtils.validateEntity(dict, UpdateGroup.class, DefaultGroup.class);

        this.dictService.updateById(dict);

        return new Result();
    }

    /**
     * 根据字典编码删除字典
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog("删除字典")
    @RequiresPermissions("sys:dict:delete")
    public Result delete(@RequestBody Long[] ids){
        logger.info("========= DictController.delete() =========");

        this.dictService.removeByIds(ids);

        return new Result();
    }

    /**
     * 设置状态
     *
     * @param entity
     * @return
     */
    @PostMapping("setStatus")
    @OperationLog("设置状态")
    @RequiresPermissions("sys:dict:setStatus")
    public Result setStatus(@RequestBody DictEO entity){
        logger.info("======== WarehouseLocationController.setStatus ========");
        entity.setStatus(entity.getStatus()==0?1:0);
        this.dictService.updateStatus(entity, getUserId());
        return new Result();
    }
}
