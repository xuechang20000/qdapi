package com.wondersgroup.framwork.dao.bo;

import java.util.List;

public class Page<T>{
    private int pageNum;//第几页
    private int pageSize;//每页显示行数
    private long total;//总记录数
    private long totalPage;//总页数
    private long startNum;//开始行号
    private long endNum;//截止行号

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }

    public long getEndNum() {
        return endNum;
    }

    public void setEndNum(long endNum) {
        this.endNum = endNum;
    }
    public void calculate(){
        this.startNum=(this.pageNum-1)*this.pageSize;
        this.startNum=this.startNum<=0?0:this.startNum;
        this.endNum=this.startNum+pageSize;
    }
    private List<T> resultList;

    public Page(){

    }
    public Page(long total){
        this.total=total;
    }
    public Page(int pageNum,int pageSize){
        this.pageNum=pageNum;
        this.pageSize=pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

}
