package com.xchinfo.erp.aspect;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xchinfo.erp.annotation.BusinessLogField;
import com.xchinfo.erp.annotation.BusinessLogType;
import com.xchinfo.erp.annotation.EnableBusinessLog;
import com.xchinfo.erp.log.entity.BizLogEO;
import com.xchinfo.erp.log.service.BizLogService;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yecat.mybatis.service.IBaseService;
import org.yecat.mybatis.service.impl.BaseServiceImpl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 业务日志记录切面类
 *
 * @author roman.li
 * @date 2018/11/27
 * @update
 */
@Aspect
@Component
public class BusinessLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(BusinessLogAspect.class);
    @Autowired
    private BizLogService bizLogService;

    /**
     * 记录更新日志
     *
     * @param point
     */
    @Before("@annotation(businessLog)")
    public void doBefore(JoinPoint point, EnableBusinessLog businessLog) throws IllegalAccessException {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 获取Service类
        IBaseService service = (BaseServiceImpl)point.getTarget();

        // 创建和删除则不执行
        if (BusinessLogType.CREATE.equals(businessLog.value()) || BusinessLogType.DELETE.equals(businessLog.value())
                || BusinessLogType.BATCHDELETE.equals(businessLog.value())){
            return;
        }

        // 日志对象
        BizLogEO bizLog = new BizLogEO();

        // 获取用户信息
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        String realName = user.getRealName();
        bizLog.setUserName(userName);
        bizLog.setRealName(realName);
        bizLog.setCreatedBy(userName);
        bizLog.setCreatedTime(new Date());

        // 获取方法参数，获取第一个参数
        Object object = point.getArgs()[0];

        TableName tableName = object.getClass().getAnnotation(TableName.class);
        bizLog.setOptEntity(tableName.value());

        Field[] fields = object.getClass().getDeclaredFields();
        Object oldObject = null;
        for (Field field : fields){
            TableId tableIdAnnotation = field.getAnnotation(TableId.class);
            if (null != tableIdAnnotation){
                field.setAccessible(true);
                Serializable id = (Serializable) field.get(object);
                bizLog.setEntityId(id.toString());
                // 查找老对象
                oldObject = service.getById(id);
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("由" + realName + "["+ userName +"]" + " 修改 ");

        if (null != oldObject){
            sb.append(compareTwoObject(oldObject, object));
        }
        bizLog.setOperation(sb.toString());

        // 保存日志
        this.bizLogService.save(bizLog);
    }

    /**
     * 方法执行完成以后记录业务日志，记录新建和删除日志
     *
     * @param point
     */
    @AfterReturning("@annotation(businessLog)")
    public void doAfter(JoinPoint point, EnableBusinessLog businessLog) throws IllegalAccessException {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();

        // 更新则不执行
        if (BusinessLogType.UPDATE.equals(businessLog.value())){
            return;
        }

        // 日志对象
        BizLogEO bizLog = new BizLogEO();
        // 获取用户信息
        UserEO user = (UserEO) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        String realName = user.getRealName();
        bizLog.setUserName(userName);
        bizLog.setRealName(realName);
        bizLog.setCreatedBy(userName);
        bizLog.setCreatedTime(new Date());

        // 获取方法参数，获取第一个参数
        Object object = point.getArgs()[0];

        TableName tableName;

        if (BusinessLogType.CREATE.equals(businessLog.value())){
            tableName = object.getClass().getAnnotation(TableName.class);
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields){
                TableId tableIdAnnotation = field.getAnnotation(TableId.class);
                if (null != tableIdAnnotation){
                    field.setAccessible(true);
                    Serializable id = (Serializable) field.get(object);
                    bizLog.setEntityId((id.toString()));
                    break;// 找到ID值则退出循环
                }
            }

            bizLog.setOptEntity(tableName.value());
            bizLog.setOperation("由" + realName + "["+ userName +"]" + " 新建");

            // 保存日志
            this.bizLogService.save(bizLog);
        } else {
            tableName = businessLog.entityClass().getAnnotation(TableName.class);
            bizLog.setOptEntity(tableName.value());

            if (BusinessLogType.BATCHDELETE.equals(businessLog.value())){
                Serializable[] ids = (Serializable[]) object;
                for (Serializable id : ids){
                    bizLog.setEntityId(id.toString());

                    bizLog.setOperation("由" + realName + "["+ userName +"]" + " 删除");

                    // 保存日志
                    this.bizLogService.save(bizLog);
                }
            } else {
                bizLog.setEntityId(object.toString());
                bizLog.setOperation("由" + realName + "["+ userName +"]" + " 删除");

                // 保存日志
                this.bizLogService.save(bizLog);
            }
        }
    }

    /**
     * 获取两个对象同名属性内容不相同的列表
     * @param oldObject old对象
     * @param newObject new对象
     * @return
     * @throws IllegalAccessException
     */
    private String compareTwoObject(Object oldObject, Object newObject) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();

        //获取对象的属性列表
        Field[] oldFields = oldObject.getClass().getDeclaredFields();
        Field[] newFields = newObject.getClass().getDeclaredFields();

        //遍历属性列表field1
        for(int i=0; i < oldFields.length; i++) {
            BusinessLogField name = oldFields[i].getAnnotation(BusinessLogField.class);

            if(name != null) {// 只记录存在注解的字段
                for (int j = 0; j < newFields.length; j++) {
                    //如果field1[i]属性名与field2[j]属性名内容相同
                    if (oldFields[i].getName().equals(newFields[j].getName())) {
                        oldFields[i].setAccessible(true);
                        newFields[j].setAccessible(true);

                        //如果field1[i]属性值与field2[j]属性值内容不相同
                        if (!compareTwo(oldFields[i].get(oldObject), newFields[j].get(newObject))) {
                            // 拼接日志内容
                            sb.append(name.value())
                                    .append(":")
                                    .append(oldFields[i].get(oldObject))
                                    .append(" -> ")
                                    .append(newFields[j].get(newObject))
                                    .append(";");
                        }
                        break;
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * 对比两个数据是否内容相同
     *
     * @param  object1,object2
     * @return boolean类型
     */
    private boolean compareTwo(Object object1, Object object2){
        if(object1 == null && object2 == null){
            return true;
        }
        if(object1 == null && object2 != null){
            return false;
        }
        if(object1.equals(object2)){
            return true;
        }
        return false;
    }
}
