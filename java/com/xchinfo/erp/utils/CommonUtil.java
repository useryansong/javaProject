package com.xchinfo.erp.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.yecat.core.utils.JsonUtil;
import org.yecat.mybatis.entity.support.AbstractAuditableEntity;
import org.yecat.mybatis.utils.Criteria;
import org.yecat.mybatis.utils.Criterion;

import java.util.*;

/**
 * @author zhongye
 * @date 2019/5/14
 */
public class CommonUtil {

    /**
     * 将分页的查询条件转为map
     * @param criteria   前端传递的查询条件
     * @return currentPage:当前第几页  pageSize:每页条数  currentIndex:当前页从多少条开始
     */
    public static Map criteriaToMap(Criteria criteria) {
        Map<String, Object> map = new HashedMap();
        // 循环查询条件，拼接where字符串
        List<Criterion> criterions = criteria.getCriterions();
        if(criterions!=null && criterions.size()>0){
            for (Criterion criterion : criterions) {
                if (null!=criterion.getValue() && !"".equals(criterion.getValue())) {
                    map.put(criterion.getField(), criterion.getValue());
                }
            }
        }
        map.put("currentPage", criteria.getCurrentPage());
        map.put("pageSize", criteria.getSize());
        map.put("currentIndex", (criteria.getCurrentPage() - 1) * criteria.getSize());
        map.put("order", criteria.getOrder());
        map.put("orderField", criteria.getOrderField());
        return map;
    }

    /**
     * 生成分页信息
     * @param totalList    总数据
     * @param pageList     当前页的数据
     * @param criteriaMap  携带分页信息的map,即criteriaToMap方法解析出的分页信息
     */
    public static IPage listToPage(List totalList, List pageList, Map criteriaMap){
        IPage page = new Page<>();
        int total = totalList.size();
        int pageSize = (int)criteriaMap.get("pageSize");
        int pages =  total/pageSize;
        if(total % pageSize > 0){
            pages = pages +1;
        }
        page.setRecords(pageList);
        page.setCurrent((int) criteriaMap.get("currentPage"));
        page.setPages(pages);
        page.setSize(pageSize);
        page.setTotal(total);
        return page;
    }

    /**
     * 使用指定的字符串,生成指定长度的随机字符串
     * @param length 指定长度
     * @param str 指定的字符串,如果为null,默认使用"0123456789"
     * @return
     */
    public static String generateRandomString(int length, String str) {
        if (str == null) {
            str = "0123456789";
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static Object convertNullToEmpty(Object obj){
        if(obj==null){
            return "";
        }
        return  obj;
    }

    /**
     * 填充公共字段
     * @param entity
     * @return
     */
    public static void setFill(AbstractAuditableEntity entity){
        Object obj = (Object) SecurityUtils.getSubject().getPrincipal();
        Map user = JsonUtil.fromJson(JsonUtil.toJson(obj),Map.class);
        String userName = "";
        String name = "";
        String auditName ="";
        if(user!=null){
            userName = user.get("userName").toString();
            name = user.get("realName").toString();
            auditName = "[" + userName + "]" + name;
        }
        entity.setCreatedBy(auditName);
        entity.setLastModifiedBy(auditName);
        entity.setCreatedTime(new Date());
        entity.setLastModifiedTime(new Date());
    }

    /**
     * 生成分页信息
     * @param dataList    数据
     * @param pageSize    每页行数
     * @param currentPage 当前第几页
     */
    public static IPage getPageInfo(List dataList, int pageSize, int currentPage){
        IPage page = new Page<>();
        if(dataList==null || dataList.size()==0) {
            page.setRecords(dataList);
            page.setCurrent(currentPage);
            page.setPages(1);
            page.setSize(pageSize);
            page.setTotal(0);
        } else {
            int total = dataList.size();
            int pages =  total/pageSize;
            if(total % pageSize > 0){
                pages = pages +1;
            }

            int fromIndex = pageSize * (currentPage - 1);
            int toIndex = pageSize * currentPage;
            if(currentPage == pages) {
                toIndex = total;
            }

            page.setRecords(dataList.subList(fromIndex, toIndex));
            page.setCurrent(currentPage);
            page.setPages(pages);
            page.setSize(pageSize);
            page.setTotal(total);
        }

        return page;
    }

    /**
     * 生成分页信息
     * @param total    总数据条数
     * @param pageList     当前页的数据
     * @param criteriaMap  携带分页信息的map,即criteriaToMap方法解析出的分页信息
     */
    public static IPage listToPage(Integer total, List pageList, Map criteriaMap){
        IPage page = new Page<>();
        int pageSize = (int)criteriaMap.get("pageSize");
        int pages =  total/pageSize;
        if(total % pageSize > 0){
            pages = pages +1;
        }
        page.setRecords(pageList);
        page.setCurrent((int) criteriaMap.get("currentPage"));
        page.setPages(pages);
        page.setSize(pageSize);
        page.setTotal(total);
        return page;
    }
}
