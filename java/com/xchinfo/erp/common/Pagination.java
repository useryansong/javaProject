package com.xchinfo.erp.common;


import java.io.Serializable;
import java.util.List;

public class Pagination<T> implements Serializable{
    /**
     * 分页数据集合
     */
    private List<T> rows;
    /**
     * 总数据量
     */
    private int total;
    /**
     * 总页数
     */
    private int pageTotal;
    /**
     * 本页数据长度
     */
    private int rowsSize;

    public Pagination() {
        this.rows = null;
        this.total = 0;
        this.pageTotal = 0;
        this.rowsSize=0;
    }

    public static Pagination getInstance() {
        return new Pagination();
    }

    public List<T> getRows() {
        return this.rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageTotal() {
        return this.pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getRowsSize() {
        return rowsSize;
    }

    public void setRowsSize(int rowsSize) {
        this.rowsSize = rowsSize;
    }
    public static Pagination getPagination(List list , int count , int pageSize){
        Pagination pagination = new Pagination();
        double cot = count;
        double paS = pageSize;
        int pageTotal = (int) Math.ceil(cot / paS);
        pagination.setPageTotal(pageTotal);
        pagination.setTotal(count);
        pagination.setRows(list);
        pagination.setRowsSize(list.size());
        return pagination;
    }
}

