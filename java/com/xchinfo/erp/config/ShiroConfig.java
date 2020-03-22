package com.xchinfo.erp.config;

import com.xchinfo.erp.oauth2.OAuth2Filter;
import com.xchinfo.erp.oauth2.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author roman.li
 * @date 2017/11/24
 * @update
 */
@Configuration
public class ShiroConfig {
    // 登录地址
    @Value("${shiro.user.loginUrl}")
    private String loginUrl;

    // 权限认证失败地址
    @Value("${shiro.user.unauthorizedUrl}")
    private String unauthorizedUrl;

    @Bean("sessionManager")
    public SessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuth2Realm);
        securityManager.setSessionManager(sessionManager);

        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        // Shiro的核心安全接口,这个属性是必须的
        shiroFilter.setSecurityManager(securityManager);
        // 身份认证失败，则跳转到登录页面的配置
        shiroFilter.setLoginUrl(loginUrl);
        // 权限认证失败，则跳转到指定页面
        shiroFilter.setUnauthorizedUrl(unauthorizedUrl);

        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/login.html", "anon");
        filterMap.put("/sys/forgetpw/sendCheckCode", "anon");
        filterMap.put("/sys/forgetpw/resetPswd", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/service/**", "anon");

        filterMap.put("/**/*.css", "anon");
        filterMap.put("/**/*.js", "anon");
        filterMap.put("/**/*.html", "anon");
        filterMap.put("/**/*.otf", "anon");
        filterMap.put("/**/*.eot", "anon");
        filterMap.put("/**/*.ttf", "anon");
        filterMap.put("/**/*.woff", "anon");
        filterMap.put("/**/*.woff2", "anon");
        filterMap.put("/**/*.svg", "anon");

        filterMap.put("/statics/**", "anon");
        filterMap.put("/favicon.ico**", "anon");
        filterMap.put("/erp.png**", "anon");
        filterMap.put("/statics/css/**", "anon");
        filterMap.put("/statics/fonts/**", "anon");
        filterMap.put("/statics/images/**", "anon");
        filterMap.put("/statics/js/**", "anon");
        filterMap.put("/statics/libs/**", "anon");
        filterMap.put("/statics/plugins/**", "anon");
        filterMap.put("/statics/document/**", "anon");
        filterMap.put("/statics/excelTemplates/**", "anon");
        filterMap.put("/captcha/captchaImage**", "anon");

        filterMap.put("/css/**", "anon");
        filterMap.put("/docs/**", "anon");
        filterMap.put("/fonts/**", "anon");
        filterMap.put("/img/**", "anon");
        filterMap.put("/ajax/**", "anon");
        filterMap.put("/js/**", "anon");
        filterMap.put("/erp/**", "anon");

        filterMap.put("/", "anon");
        filterMap.put("/**", "oauth2");

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
