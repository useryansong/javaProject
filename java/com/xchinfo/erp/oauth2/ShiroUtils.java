package com.xchinfo.erp.oauth2;

import com.xchinfo.erp.sys.auth.entity.UserEO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.yecat.core.exception.BusinessException;

/**
 * Shiro工具类
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月12日 上午9:49:19
 */
public class ShiroUtils {
	/**  加密算法 */
	public final static String hashAlgorithmName = "SHA-256";
	/**  循环次数 */
	public final static int hashIterations = 16;

	public static String sha256(String password, String salt) {
		return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
	}

	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static UserEO getUser() {
		return (UserEO)SecurityUtils.getSubject().getPrincipal();
	}

	public static Long getUserId() {
		return getUser().getUserId();
	}
	
	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
	}

	public static boolean isLogin() {
		return SecurityUtils.getSubject().getPrincipal() != null;
	}

	public static String getLoginName()
	{
		return getUser().getUserName();
	}

	public static String getIp()
	{
		return getSubject().getSession().getHost();
	}

	public static String getSessionId()
	{
		return String.valueOf(getSubject().getSession().getId());
	}

	public static void logout() {
		SecurityUtils.getSubject().logout();
	}
	
	public static String getKaptcha(String key) {
		Object kaptcha = getSessionAttribute(key);
		if(kaptcha == null){
			throw new BusinessException("验证码已失效");
		}
		getSession().removeAttribute(key);
		return kaptcha.toString();
	}

}
