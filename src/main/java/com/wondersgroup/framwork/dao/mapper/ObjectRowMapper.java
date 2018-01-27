package com.wondersgroup.framwork.dao.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ObjectRowMapper implements RowMapper{
    private final static Logger logger = LoggerFactory.getLogger(ObjectRowMapper.class);
    private Class className;
    public ObjectRowMapper(Class className){
        this.className = className;
    }
    /*
     * 该方法自动将数据库字段对应到Object中相应字段
     * 要求：数据库与Object中字段名相同
     *
     */
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        Object nt = new Object();
        Method[] methods = className.getMethods();
        String fieldName;
        try {
            nt = className.newInstance();
            ResultSetMetaData rsmd=rs.getMetaData();
            for (Method method : methods) {

                //如果结果中没有改field项则跳过
                if(method.getName().startsWith("set")){
                    fieldName=method.getName().substring(3);
                    if(!equalsField(rsmd,fieldName)) continue;
                }else{
                    continue;
                }
                Class c=method.getParameterTypes()[0];
                if(c.equals(Blob.class)){//如果接收参数是blob类型
                    method.invoke(nt, rs.getBlob(fieldName));
                }else if(c.equals(Clob.class)){
                    method.invoke(nt,rs.getClob(fieldName));
                }else if(c.equals(java.sql.Time.class)){
                    method.invoke(nt,rs.getTime(fieldName));
                }else if(c.equals(java.sql.Date.class)){
                    method.invoke(nt,rs.getDate(fieldName));
                }else if(c.equals(java.sql.Timestamp.class)||c.equals(java.util.Date.class)){
                    method.invoke(nt,rs.getTimestamp(fieldName));
                }else if(c.equals(BigDecimal.class)){
                    method.invoke(nt,rs.getBigDecimal(fieldName));
                }else{
                    String value = rs.getString(fieldName);
                    if(value==null) continue;
                    invokeMethod(nt,method,value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nt;
    }

    public boolean equalsField(ResultSetMetaData rsmd,String fieldName)throws SQLException{
        for(int i=1;i<=rsmd.getColumnCount();++i){
            if(rsmd.getColumnName(i).equalsIgnoreCase(fieldName)) return true;
        }
        return false;
    }
    public void invokeMethod(Object form,Method method,String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
        /**
         * 方法参数类型
         */
        Class c=method.getParameterTypes()[0];
        //Iteger
        if(c.equals(int.class)||c.equals(Integer.class)){
            method.invoke(form, Integer.valueOf(value));
            //long
        }else if(c.equals(long.class)||c.equals(Long.class)){
            method.invoke(form, Long.valueOf(value));
            //short
        }else if(c.equals(short.class)||c.equals(Short.class)){
            method.invoke(form, Short.valueOf(value));
            //float
        }else if(c.equals(float.class)||c.equals(Float.class)){
            method.invoke(form, Float.valueOf(value));
            //double
        }else if(c.equals(double.class)||c.equals(Double.class)){
            method.invoke(form, Double.valueOf(value));
            //String
        }else if(c.equals(String.class)){
            method.invoke(form, String.valueOf(value));
            //boolean
        }else if(c.equals(Boolean.class)){
            method.invoke(form, Boolean.valueOf(value));
        }else {
           logger.error("不支持的数据类型:"+c.getName());
        }

    }
    /*
     * 根据类型对具体对象属性赋值
     */
    public  void setFieldValue(Object form, Field field, String value) {

        String elemType = field.getType().toString();
        if (elemType.indexOf("boolean") != -1||elemType.indexOf("Boolean") != -1) {
            try {
                field.set(form, Boolean.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("byte") != -1||elemType.indexOf("Byte") != -1) {
            try {
                field.set(form, Byte.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("char") != -1||elemType.indexOf("Character") != -1) {
            try {
                field.set(form, Character.valueOf(value.charAt(0)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("String") != -1||elemType.indexOf("string") != -1) {
            try {
                field.set(form, String.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();

            }
        } else if (elemType.indexOf("double") != -1||elemType.indexOf("Double") != -1) {
            try {
                field.set(form, Double.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("float") != -1||elemType.indexOf("Float") != -1) {
            try {
                field.set(form, Float.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("int") != -1||elemType.indexOf("Integer") != -1) {
            try {
                field.set(form, Integer.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("long") != -1||elemType.indexOf("Long") != -1) {
            try {
                field.set(form, Long.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (elemType.indexOf("short") != -1||elemType.indexOf("Short") != -1) {
            try {
                field.set(form, Short.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                field.set(form, (Object) value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
