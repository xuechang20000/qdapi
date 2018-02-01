package com.wondersgroup.framwork.dao.utils;

import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.framwork.dao.annotation.*;
import com.wondersgroup.framwork.dao.bo.ColumnType;
import com.wondersgroup.framwork.dao.bo.SpDtoColumn;
import com.wondersgroup.framwork.dao.bo.SqlCreator;
import com.wondersgroup.framwork.dao.impl.CommonJdbcDaoImpl;
import com.wondersgroup.framwork.dao.mapper.ObjectRowMapper;
import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.oracore.OracleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static  Map<String,Object> getAllColumn(Object object){
        Class c=object.getClass();
        Method[] methods = c.getDeclaredMethods();
        Map<String,Object> columnsMap=new HashMap<String,Object>();

        Method getIdMethod=getIdMethod(c);//id的get方法
        Method setIdMethod=null;
        for(Method method:methods){
            //如果结果中没有改field项则跳过
            if (!method.getName().startsWith("get")) {
                continue;
            }
            try {
                if (method.getName().endsWith(getIdMethod.getName().substring(1))){///如果是ID列
                    setIdMethod=getSetMethod(c,method);
                }
                Object obj=method.invoke(object);
                columnsMap.put(method.getName().substring(3),obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

            try {

            //如果没有Id的set方法或者主健不为空，测不重置主键值
                if (setIdMethod==null||getIdMethod.invoke(object)!=null)
                    return  columnsMap;
                Long sequenceValue=ClassUtils.getSequenceValue(c);
                //设置ID属性值
                if (getIdMethod.getReturnType().equals(Integer.class)){
                    setIdMethod.invoke(object,sequenceValue.intValue());
                    columnsMap.put(getIdMethod.getName().substring(3),sequenceValue.intValue());
                }else{
                    setIdMethod.invoke(object,sequenceValue);
                    columnsMap.put(getIdMethod.getName().substring(3),sequenceValue);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return columnsMap;
    }

    /**
     * 获取表名
     */
    public  static String getTableName(Class clazz){
        Table table=(Table)clazz.getAnnotation(Table.class);
        return  (table!=null&&table.name()!=null)?table.name():clazz.getName();
    }
    public static Method getSetMethodByFieldName(Class clazz,String fieldName,Class... paramTpes) throws  NoSuchMethodException{
        return clazz.getDeclaredMethod("set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1)
                ,paramTpes);
    }
    /**
     * 获取主键sequence名称
     */
    public  static String getSequenceName(Class clazz){
        Method idGetMethod=getIdMethod(clazz);
        Sequence annotation=idGetMethod.getAnnotation(Sequence.class);
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
        if (sequenceName==null) return null;
        else
        return CommonJdbcUtils.getSequence(sequenceName);
    }

    /**
     * 获取Id属性
     */
    public  static Method getIdMethod(Class clazz){
        Method[] methods=clazz.getDeclaredMethods();
        Annotation annotation;
        Method method=null;
        int i=0;
        for (Method m:methods){
            if (m.getAnnotation(Id.class)!=null&&m.getName().startsWith("get")) {
                i++;
                method=m;
            };
        }
        if (i==0) {
            logger.error("class"+clazz.getName()+"不存在Id属性");
            throw new RuntimeException("class"+clazz.getName()+"不存在Id属性");
        }else if (i>1){
            logger.error("class"+clazz.getName()+"存在多个Id属性");
            throw new RuntimeException("class"+clazz.getName()+"存在多个Id属性");
        }
        return method;
    }
    /**
     * 获取一个sql生成器
     * @param object
     * @param isIncludeNull
     * @return
     */
    public static  SqlCreator getSqlCreator(Object object,boolean isIncludeNull){
        Class clazz=object.getClass();
        return  new SqlCreator(getTableName(clazz),getIdMethod(clazz).getName().substring(3),
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
            sqlTypeConverter(ps,i++,object);
        }
    }

    /**
     * 设置对象主键值
     * @param object
     * @param idValue
     */
    public static void setIDValue(Object object,Long idValue){
        Method idGetMethod=getIdMethod(object.getClass());
        Method method= getSetMethod(object.getClass(),idGetMethod);
        try {
            if (idGetMethod.getReturnType().equals(Long.class))
                method.invoke(object,idValue)  ;
            else
                method.invoke(object,idValue.intValue())  ;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据get方法获取set方法
     * @param clazz
     * @param getMethod
     * @return
     */
    public static  Method getSetMethod(Class clazz,Method getMethod) {
        String setMethodName="s"+getMethod.getName().substring(1);
        Method method=null;
        try {
            method=  clazz.getDeclaredMethod(setMethodName
                    ,new Class[]{getMethod.getReturnType()});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * 调用过程对象生成
     * @param spObj
     * @return
     */
    public static   Map<Integer,SpDtoColumn> generateSpDto(Object spObj){
        Class clazz=spObj.getClass();
        Callable annotation=(Callable) clazz.getAnnotation(Callable.class);
        if (annotation==null) throw  new RuntimeException("对象非Sp对象");
        Map<Integer,SpDtoColumn> map=new HashMap<Integer,SpDtoColumn>();
        Field[] fields=clazz.getDeclaredFields();
        SpDtoColumn spDtoColumn=null;
        for (Field field:fields){
            Annotation[] annotations= field.getAnnotations();
            if (annotations.length==0) throw  new RuntimeException("列"+field.getName()+"未添加注解");
            spDtoColumn=new SpDtoColumn();

            spDtoColumn.setName(field.getName());
            spDtoColumn.setSpName(annotation.name());
            spDtoColumn.setType(field.getType());

            InOrOut inOrOut=(InOrOut)annotations[0];
            spDtoColumn.setInOrOut(inOrOut.type());
            spDtoColumn.setOrder(inOrOut.order());
            if (inOrOut.type()==SpParamType.IN){

                try {
                    spDtoColumn.setValue(getGetMethod(clazz,field).invoke(spObj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            map.put(inOrOut.order(),spDtoColumn);
        }
        return map;
    }

    /**
     * 根据field获取method
     * @param clazz
     * @param field
     * @return
     */
    public static Method getGetMethod(Class clazz,Field field){
        Method[] methods=clazz.getDeclaredMethods();
        for (Method method:methods){
            if (method.getReturnType()==field.getType()&&
                    method.getName().substring(3).equalsIgnoreCase(field.getName()))
                return method;
        }
        return null;
    }


    /**
     * java，sql类型转换
     * @param ps
     * @param i
     * @param object
     */
    public static void sqlTypeConverter(PreparedStatement ps,int i,Object object){
            try {
                if (object==null){
                    ps.setObject(i,null);
                    return;
                }
                Class c=object.getClass();
                if(c.equals(Blob.class)){//如果接收参数是blob类型
                    ps.setBlob(i,(Blob)object);
                }else if(c.equals(Clob.class)){
                    ps.setClob(i,(Clob)object);
                }else if(c.equals(java.sql.Time.class)){
                    ps.setTime(i,(Time) object);
                }else if(c.equals(java.sql.Date.class)){
                    ps.setDate(i,(Date) object);
                }else if(c.equals(java.sql.Timestamp.class)){
                    ps.setTimestamp(i,(java.sql.Timestamp)object);
                }else if(c.equals(java.util.Date.class)){
                    java.util.Date date=(java.util.Date)object;
                    ps.setTimestamp(i,new java.sql.Timestamp(date.getTime()));
                }else if(c.equals(BigDecimal.class)){
                    ps.setBigDecimal(i,(BigDecimal)object);
                }else if(c.equals(int.class)||c.equals(Integer.class)){
                    ps.setInt(i,(Integer) object);
                    //long
                }else if(c.equals(long.class)||c.equals(Long.class)){
                    ps.setLong(i,(Long) object);
                    //short
                }else if(c.equals(short.class)||c.equals(Short.class)){
                    ps.setShort(i,(Short) object);
                    //float
                }else if(c.equals(float.class)||c.equals(Float.class)){
                    ps.setFloat(i,(Float) object);
                    //double
                }else if(c.equals(double.class)||c.equals(Double.class)){
                    ps.setDouble(i,(Double) object);
                    //String
                }else if(c.equals(String.class)){
                    ps.setString(i,(String) object);
                    //boolean
                }else if(c.equals(Boolean.class)){
                    ps.setBoolean(i,(Boolean) object);
                }else {
                    throw  new RuntimeException("无效的列类型"+c.getClass().getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    /**
     * java，sql类型转换
     * @param cs
     * @param i
     * @param c
     */
    public static void sqlTypeOutConverter(CallableStatement cs,int i,Class c){
        try {
            if(c.equals(int.class)||c.equals(Integer.class)){
                cs.registerOutParameter(i, OracleTypes.NUMBER);
                //long
            }else if(c.equals(long.class)||c.equals(Long.class)){
                cs.registerOutParameter(i, OracleTypes.NUMBER);
                //short
            }else if(c.equals(short.class)||c.equals(Short.class)){
                cs.registerOutParameter(i, OracleTypes.NUMBER);
                //float
            }else if(c.equals(float.class)||c.equals(Float.class)){
                cs.registerOutParameter(i, OracleTypes.NUMBER);
                //double
            }else if(c.equals(double.class)||c.equals(Double.class)){
                cs.registerOutParameter(i, OracleTypes.NUMBER);
                //String
            }else if(c.equals(String.class)){
                cs.registerOutParameter(i, OracleTypes.VARCHAR);
                //CURSOR
            }else if(c.equals(List.class)){
                cs.registerOutParameter(i,  OracleTypes.CURSOR);
                //boolean
            }else {
                throw  new RuntimeException("无效的列类型"+c.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * java，sql类型转换
     * @param cs
     * @param i
     * @param object
     */
    public static void sqlTypeOutConverter(CallableStatement cs,int i,Object object){
        sqlTypeOutConverter(cs,i,object.getClass());
    }
}
