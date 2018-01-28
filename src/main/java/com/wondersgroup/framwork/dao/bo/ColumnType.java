package com.wondersgroup.framwork.dao.bo;

public class ColumnType {
    private String columnName;
    private Object value;
    public  ColumnType(){

    }
    public  ColumnType(String columnName,Object object){
        this.columnName=columnName;
        this.value=object;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }
    public ColumnType(String columnName){
        this.columnName=columnName;
    }
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

}
