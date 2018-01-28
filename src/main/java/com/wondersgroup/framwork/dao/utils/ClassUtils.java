package com.wondersgroup.framwork.dao.utils;

import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.framwork.dao.annotation.Id;
import com.wondersgroup.framwork.dao.annotation.Sequence;
import com.wondersgroup.framwork.dao.annotation.Table;
import com.wondersgroup.framwork.dao.bo.ColumnType;
import com.wondersgroup.framwork.dao.bo.SqlCreator;
import com.wondersgroup.framwork.dao.impl.CommonJdbcDaoImpl;
import com.wondersgroup.framwork.dao.mapper.ObjectRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    private final static Logger logger = LoggerFactory.getLogger(ObjectRowMapper.class);

    //获取类所有字段
    public static List<ColumnType> getAllColumn(Class clazz){
        Field[] fields= clazz.getDeclaredFields();
        List<ColumnType> list=new ArrayList<>();
        for(Field field:fields){
            list.add(new ColumnType(field.getName()));
        }
        return list;
    }


    /**
     * 获取类所有字段及值
     * @param object
     * @return
     */
    public static List<ColumnType> getAllColumn(Object object){
        Class c=object.getClass();
        Method[] methods = c.getDeclaredMethods();
        Field idField=getIdColumn(c);
        Long sequenceValue=getSequenceValue(c);
        List<ColumnType> list=new ArrayList<>();
        String fieldName;
        ColumnType columnType;
        Method setIdMethod=null;
        for(Method method:methods){
            //获取ID列的set方法
            if (method.getName().startsWith("set")&&
                    method.getName().substring(3).equalsIgnoreCase(idField.getName()))
                setIdMethod=method;
            //如果结果中没有改field项则跳过
            if (method.getName().startsWith("get")) {
                fieldName = method.getName().substring(3);
            } else {
                continue;
            }
            columnType=new ColumnType();
            columnType.setColumnName(fieldName);
            try {
                if (fieldName.equalsIgnoreCase(idField.getName())&&sequenceValue!=null){
                    columnType.setValue(sequenceValue);
                }else {
                    Object obj=method.invoke(object);
                    columnType.setValue(obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            list.add(columnType);
        }
        if (setIdMethod!=null)
            try {
            //设置ID属性值
                setIdMethod.invoke(object,sequenceValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return list;
    }

    /**
     * 获取表名
     */
    public  static String getTableName(Class clazz){
        Table table=(Table)clazz.getAnnotation(Table.class);
        return  (table!=null&&table.name()!=null)?table.name():clazz.getName();
    }
    public static Method getSetMethodByFieldName(Class clazz,String fieldName,Class... paramTpes) throws  NoSuchMethodException{
        return clazz.getDeclaredMethod("set"+fieldName.substring(3),paramTpes);
    }
    /**
     * 获取主键sequence名称
     */
    public  static String getSequenceName(Class clazz){
        Field field=getIdColumn(clazz);
        Sequence annotation=field.getAnnotation(Sequence.class);
        if (annotation==null)
            return null;
        return annotation.sequencename();
    }
    /**
     * 获取主键sequence值
     */
    public  static  Long getSequenceValue(Class clazz){
        if(CommonJdbcDaoImpl.databaseProductName.equalsIgnoreCase("MySQL"))
            return null;
        String sequenceName=getSequenceName(clazz);
        return CommonJdbcUtils.getSequence(sequenceName);
    }
    /**
     * 获取Id属性
     */
    public  static Field getIdColumn(Class clazz){
        Field[] fields=clazz.getDeclaredFields();
        Annotation annotation;
        Field field=null;
        int i=0;
        for (Field field1:fields){
            if (field1.getAnnotation(Id.class)!=null) {
                i++;
                field=field1;
            };
        }
       if (i==0) {
           logger.error("class"+clazz.getName()+"不存在Id属性");
           throw new RuntimeException("class"+clazz.getName()+"不存在Id属性");
       }else if (i>1){
           logger.error("class"+clazz.getName()+"存在多个Id属性");
           throw new RuntimeException("class"+clazz.getName()+"存在多个Id属性");
       }
       return field;
    }

    /**
     * 获取一个sql生成器
     * @param object
     * @param isIncludeNull
     * @return
     */
    public static  SqlCreator getSqlCreator(Object object,boolean isIncludeNull){
        Class clazz=object.getClass();
        return  new SqlCreator(getTableName(clazz),getIdColumn(clazz).getName(),
                getAllColumn(object),isIncludeNull);
    }

    /**
     * PreparedStatement 填充参数
     * @param ps
     * @param args
     */
    public  static void prepareStatement(PreparedStatement ps,List<Object> args){
        int i=1;
        for (Object object:args){
            try {
                if (object==null){
                    ps.setObject(i++,null);
                    continue;
                }
                Class c=object.getClass();
                if(c.equals(Blob.class)){//如果接收参数是blob类型
                    ps.setBlob(i++,(Blob)object);
                }else if(c.equals(Clob.class)){
                    ps.setClob(i++,(Clob)object);
                }else if(c.equals(java.sql.Time.class)){
                    ps.setTime(i++,(Time) object);
                }else if(c.equals(java.sql.Date.class)){
                    ps.setDate(i++,(Date) object);
                }else if(c.equals(java.sql.Timestamp.class)){
                    ps.setTimestamp(i++,(java.sql.Timestamp)object);
                }else if(c.equals(java.util.Date.class)){
                    java.util.Date date=(java.util.Date)object;
                    ps.setDate(i++,new java.sql.Date(date.getTime()));
                }else if(c.equals(BigDecimal.class)){
                    ps.setBigDecimal(i++,(BigDecimal)object);
                }else if(c.equals(int.class)||c.equals(Integer.class)){
                    ps.setInt(i++,(Integer) object);
                    //long
                }else if(c.equals(long.class)||c.equals(Long.class)){
                    ps.setLong(i++,(Long) object);
                    //short
                }else if(c.equals(short.class)||c.equals(Short.class)){
                    ps.setShort(i++,(Short) object);
                    //float
                }else if(c.equals(float.class)||c.equals(Float.class)){
                    ps.setFloat(i++,(Float) object);
                    //double
                }else if(c.equals(double.class)||c.equals(Double.class)){
                    ps.setDouble(i++,(Double) object);
                    //String
                }else if(c.equals(String.class)){
                    ps.setString(i++,(String) object);
                    //boolean
                }else if(c.equals(Boolean.class)){
                    ps.setBoolean(i++,(Boolean) object);
                }else {
                    throw  new RuntimeException("无效的列类型"+c.getClass().getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置对象主键值
     * @param object
     * @param idValue
     */
    public static void setIDValue(Object object,Long idValue){
        Field idField=getIdColumn(object.getClass());
        Method method= getSetMethod(object.getClass(),idField);
        try {
            if (idField.getType().equals(Long.class))
                method.invoke(object,idField)  ;
            else
                method.invoke(object,idValue.intValue())  ;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public static  Method getSetMethod(Class clazz,Field field) {
        String fieldName=field.getName();
        Method method=null;
        try {
            method=  clazz.getDeclaredMethod("set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1)
                    ,new Class[]{field.getType()});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }
}
