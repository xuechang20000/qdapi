package com.wondersgroup.framwork.dao.utils;



import com.wondersgroup.framwork.dao.annotation.Column;
import com.wondersgroup.framwork.dao.annotation.Id;
import com.wondersgroup.framwork.dao.bo.ColumnType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    public static List<ColumnType> getAllColumn(Class clazz){
        Field[] fields= clazz.getDeclaredFields();
        List<ColumnType> list=new ArrayList<>();
        Annotation annotation;
        for(Field field:fields){
            list=new ArrayList<ColumnType>();
            annotation=field.getAnnotation(Id.class);
            if(annotation!=null) continue;
            list.add(new ColumnType(field.getName()));
        }
        return list;
    }
}
    public static List<ColumnType> getAllColumnNotID(Class clazz){
        Field[] fields= clazz.getDeclaredFields();
        List<ColumnType> list=new ArrayList<>();
        Annotation annotation;
        for(Field field:fields){
            list=new ArrayList<ColumnType>();
            annotation=field.getAnnotation(Id.class);
            if(annotation!=null) continue;
            list.add(new ColumnType(field.getName()));
        }
        return list;
    }
    }
