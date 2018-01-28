package com.wondersgroup.framwork.dao.bo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SqlCreator {
    private String sql;
    private String tableName;//表名
    private String idFileName;//主健字段名称
    private List<ColumnType> columnTypeList;//字段列表
    private boolean isIncludeNull;//是否处理空值
    private List<Object> args;

    public SqlCreator(String tableName, String idFileName, List<ColumnType> columnTypeList, boolean isIncludeNull) {
        this.tableName = tableName;
        this.idFileName = idFileName;
        this.columnTypeList = columnTypeList;
        this.isIncludeNull = isIncludeNull;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdFileName() {
        return idFileName;
    }

    public void setIdFileName(String idFileName) {
        this.idFileName = idFileName;
    }

    public List<ColumnType> getColumnTypeList() {
        return columnTypeList;
    }

    public void setColumnTypeList(List<ColumnType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }


    public boolean isIncludeNull() {
        return isIncludeNull;
    }

    public void setIncludeNull(boolean includeNull) {
        isIncludeNull = includeNull;
    }

    /**
     * 生成update语句
     */
    public void generateUpdateSql(){
            StringBuffer update=new StringBuffer("UPDATE ");
            StringBuffer where=new StringBuffer(" WHERE ");
            this.args=new ArrayList<Object>();
            Object idVaue=null;
            update.append(this.tableName).append(" SET ");
            int i=0;
            for (ColumnType columnType:this.columnTypeList){
                if (this.isIncludeNull==false&&columnType.getValue()==null) continue;
                if (columnType.getColumnName().equalsIgnoreCase(this.idFileName)){
                    where.append(columnType.getColumnName()+" =? ");
                    idVaue=columnType.getValue();
                    continue;
                }
                if(i>0)
                    update.append(",").append(columnType.getColumnName()+"=? ");
                else
                    update.append(columnType.getColumnName()+"=?" );
                this.args.add(columnType.getValue());
                i++;
            }
            this.args.add(idVaue);
            this.sql= update.append(where).toString();
    }

    /**
     * 生成insert语句
     */
    public void generateInsertSql(){
        StringBuffer insert=new StringBuffer("INSERT INTO ");
        StringBuffer values=new StringBuffer(" VALUES (");
        this.args=new ArrayList<Object>();
        insert.append(this.tableName).append(" (");
        int i=0;
        for (ColumnType columnType:this.columnTypeList){
            if (this.isIncludeNull==false&&columnType.getValue()==null&&
                    !columnType.getColumnName().equalsIgnoreCase(this.idFileName)) continue;
            if(i>0) {
                insert.append(",").append(columnType.getColumnName());
                values.append(",?");
            }else {
                insert.append(columnType.getColumnName());
                values.append("?");
            }
            this.args.add(columnType.getValue());
            i++;
        }
        this.sql= insert.append(")").append(values).append(")").toString();
    }

    /**
     * 生成主键值
     */
    public void generateIdValue(String dbType){

    }
}
