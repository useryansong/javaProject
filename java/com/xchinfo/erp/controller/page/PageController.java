package com.xchinfo.erp.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用来处理页面请求
 *
 * @author roman.li
 * @date 2019/4/28
 * @update
 */
@Controller
public class PageController {

    /**
     * 三级请求路径
     *
     * @param system
     * @param module
     * @param url
     * @return
     */
    @RequestMapping("{system}/{module}/{url}.html")
    public String page(@PathVariable("system") String system, @PathVariable("module") String module, @PathVariable("url") String url) {
        return system + "/" + module + "/" + url;
    }

    /**
     * 两级请求路径
     *
     * @param module
     * @param url
     * @return
     */
    @RequestMapping("{module}/{url}.html")
    public String page(@PathVariable("module") String module, @PathVariable("url") String url) {
        return module + "/" + url;
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(value = {"/", "index.html"})
    public String index(){
        return "index";
    }

    /**
     * 登录页
     *
     * @return
     */
    @RequestMapping("login.html")
    public String login(){
        return "login";
    }

    /**
     * 主页
     *
     * @return
     */
    @RequestMapping("main.html")
    public String main(){
        return "main";
    }
}
