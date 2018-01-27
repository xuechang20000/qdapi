package com.wondersgroup.framwork.dao.bo;

public class ColumnType {
    private String columnName;
    private boolean isNull;

    public  ColumnType(){

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

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }
}
