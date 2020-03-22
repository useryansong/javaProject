package com.xchinfo.erp.handler;

import com.alibaba.fastjson.JSON;
import com.xchinfo.erp.log.entity.ErrorLogEO;
import com.xchinfo.erp.log.service.ErrorLogService;
import com.xchinfo.erp.oauth2.ShiroUtils;
import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yecat.core.exception.BusinessException;
import org.yecat.core.exception.ErrorCode;
import org.yecat.core.utils.HttpContextUtils;
import org.yecat.core.utils.IPUtils;
import org.yecat.core.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 异常处理器
 *
 * @author roman.li
 * @date 2018/11/12
 * @update
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BusinessExceptionHandler.class);

    @Autowired
    private ErrorLogService errorLogService;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BusinessException.class)
    public Result handleBusinessException(BusinessException ex){
        Result result = new Result();
        result.error(ex.getCode(), ex.getMsg());

        return result;
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException ex){
        Result result = new Result();
        result.error(ErrorCode.DB_RECORD_EXISTS);

        return result;
    }

    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception ex){
        if (ex instanceof BusinessException){
            logger.error(ex.getMessage(), ex);
        }

        saveLog(ex);

        return new Result().error(ex.getMessage());
    }

    /**
     * 保存异常日志
     */
    private void saveLog(Exception ex){
        ErrorLogEO log = new ErrorLogEO();

        //请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setIp(IPUtils.getIpAddr(request));
        log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        log.setRequestUri(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        Map<String, String> params = HttpContextUtils.getParameterMap(request);
        log.setRequestParams(JSON.toJSONString(params));

        //异常信息,截取部分信息
        log.setErrorInfo(ExceptionUtils.getErrorStackTrace(ex).substring(0, 4096));

        //设置用户信息
        UserEO user = ShiroUtils.getUser();
        log.setCreatedBy(user.getUserName());
        log.setCreatedTime(new Date());

        //保存
        this.errorLogService.save(log);
    }
}
