package com.wondersgroup.framwork.dao.bo;

import java.lang.reflect.Field;
import java.util.*;

public class SqlCreator {
    private String sql;
    private String tableName;//表名
    private String idFileName;//主健字段名称
    private Map<String,Object> columnTypeList;//字段列表
    private boolean isIncludeNull;//是否处理空值
    private List<Object> args;

    public SqlCreator(String tableName, String idFileName, Map<String,Object> columnTypeList, boolean isIncludeNull) {
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

    public Map<String,Object> getColumnTypeList() {
        return columnTypeList;
    }

    public void setColumnTypeList(Map<String,Object> columnTypeList) {
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
            Object idValue=null;
            update.append(this.tableName).append(" SET ");
            int i=0;
            Set<Map.Entry<String,Object>> sets= columnTypeList.entrySet();
            Iterator<Map.Entry<String,Object>> iters=sets.iterator();
            while (iters.hasNext()){
                Map.Entry<String,Object> iter=iters.next();
                if (this.isIncludeNull==false&&iter.getValue()==null) continue;
                if (iter.getKey().equalsIgnoreCase(this.idFileName)){
                    where.append(iter.getKey()+" =? ");
                    idValue=iter.getValue();
                    continue;
                }
                if(i>0)
                    update.append(",").append(iter.getKey()+"=? ");
                else
                    update.append(iter.getKey()+"=?" );
                this.args.add(iter.getValue());
                i++;
            }
            this.args.add(idValue);
            this.sql= update.append(where).toString();
            if (idValue==null)
                throw  new RuntimeException("sql"+this.sql+"未传入ID值！");

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
        Set<Map.Entry<String,Object>> sets= columnTypeList.entrySet();
        Iterator<Map.Entry<String,Object>> iters=sets.iterator();
        while (iters.hasNext()){
            Map.Entry<String,Object> iter=iters.next();
            if (this.isIncludeNull==false&&iter.getValue()==null&&
                    !iter.getKey().equalsIgnoreCase(this.idFileName)) continue;
            if(i>0) {
                insert.append(",").append(iter.getKey());
                values.append(",?");
            }else {
                insert.append(iter.getKey());
                values.append("?");
            }
            this.args.add(iter.getValue());
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
