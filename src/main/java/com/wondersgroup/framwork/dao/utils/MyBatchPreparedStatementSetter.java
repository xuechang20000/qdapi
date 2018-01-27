package com.wondersgroup.framwork.dao.utils;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MyBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
    private List<Object> list;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private Class clazz;
    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }


    public MyBatchPreparedStatementSetter(){}
    public MyBatchPreparedStatementSetter(List<Object> list){
        this.list=list;
        if(list!=null&&list.size()>0)  this.clazz=list.get(0).getClass();
    }
    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {

    }

    @Override
    public int getBatchSize() {
         return  this.list.size();
    }
}
