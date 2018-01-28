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
        for(Method method:methods){
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
        return list;
    }

    /**
     * 获取表名
     */
    public  static String getTableName(Class clazz){
        Table table=(Table)clazz.getAnnotation(Table.class);
        return  (table!=null&&table.name()!=null)?table.name():clazz.getName();
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

}
