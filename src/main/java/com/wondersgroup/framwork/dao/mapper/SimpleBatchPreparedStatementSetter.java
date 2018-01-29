package com.wondersgroup.framwork.dao.mapper;

import com.wondersgroup.framwork.dao.bo.DmlType;
import com.wondersgroup.framwork.dao.bo.SqlCreator;
import com.wondersgroup.framwork.dao.utils.ClassUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SimpleBatchPreparedStatementSetter<T> implements BatchPreparedStatementSetter{
    private List<T> list;
    private  boolean isIncludeNull;
    private  String dmlType;

    public String getDmlType() {
        return dmlType;
    }

    public void setDmlType(String dmlType) {
        this.dmlType = dmlType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    private String sql;
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isIncludeNull() {
        return isIncludeNull;
    }

    public void setIncludeNull(boolean includeNull) {
        isIncludeNull = includeNull;
    }

    public SimpleBatchPreparedStatementSetter(List<T> list, boolean isIncludeNull,String dmlType) {
        this.list = list;
        this.isIncludeNull = isIncludeNull;
        this.dmlType=dmlType;
        SqlCreator sqlCreator=ClassUtils.getSqlCreator(list.get(0),isIncludeNull);
        if (this.dmlType.equalsIgnoreCase(DmlType.INSERT))
            sqlCreator.generateInsertSql();
        else
            sqlCreator.generateUpdateSql();
        this.sql=sqlCreator.getSql();
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        SqlCreator sqlCreator= ClassUtils.getSqlCreator(this.list.get(i),isIncludeNull);
        if (this.dmlType.equalsIgnoreCase(DmlType.INSERT))
            sqlCreator.generateInsertSql();
        else
            sqlCreator.generateUpdateSql();
        if (this.sql==null) this.sql=sqlCreator.getSql();
        ClassUtils.prepareStatement(preparedStatement,sqlCreator.getArgs());
    }

    @Override
    public int getBatchSize() {
        return list.size();
    }
}
