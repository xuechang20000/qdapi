package com.wondersgroup.framwork.dao.mapper;

import com.wondersgroup.framwork.dao.annotation.SpParamType;
import com.wondersgroup.framwork.dao.bo.SpDtoColumn;
import com.wondersgroup.framwork.dao.utils.ClassUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class SimpleCallableStatementCallback implements CallableStatementCallback {
    private  String spName;
    private Object spdto;
    private  Map<Integer,SpDtoColumn> map;
    private  String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, SpDtoColumn> getMap() {
        return map;
    }

    public void setMap(Map<Integer, SpDtoColumn> map) {
        this.map = map;
    }

    public SimpleCallableStatementCallback(String spName, Object spdto) {
        this.spName = spName;
        this.spdto = spdto;
        this.map= ClassUtils.generateSpDto(this.spdto);
        generateSql();
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public Object getSpdto() {
        return spdto;
    }

    public void setSpdto(Object spdto) {
        this.spdto = spdto;
    }

    @Override
    public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
        Map<Integer,SpDtoColumn> map= ClassUtils.generateSpDto(this.spdto);

        for (SpDtoColumn spDtoColumn:map.values()){
            if (spDtoColumn.getInOrOut().equals(SpParamType.IN))
            ClassUtils.sqlTypeConverter(cs,spDtoColumn.getOrder(),spDtoColumn.getValue());
            else
             ClassUtils.sqlTypeOutConverter(cs,spDtoColumn.getOrder(),spDtoColumn.getType());
       }
        /**
         * 执行查询
         */
        cs.execute();
        /**
         * 获取结果集
         */
        generateCallableStatement(cs);
        cs.close();
        return null;
    }
    public void generateSql(){
        StringBuffer callParamSql=new StringBuffer("(");
        for (SpDtoColumn spDtoColumn:map.values()){
            if (this.spName==null)
                this.spName=spDtoColumn.getSpName();
           callParamSql.append("?,");
        }
        this.sql= new StringBuffer("{call ").append(this.spName).
                append(callParamSql.substring(0,callParamSql.length()-1)).append(")}").toString();
    }

    public void generateCallableStatement(CallableStatement cs){
        Method setMethod=null;
        for (SpDtoColumn spDtoColumn:map.values()){
            if (spDtoColumn.getInOrOut().equals(SpParamType.Out)){
                try {
                    //获取set方法
                    setMethod=ClassUtils.getSetMethodByFieldName(this.getSpdto().getClass(),spDtoColumn.getName(),spDtoColumn.getType());
                    if (List.class.isAssignableFrom(spDtoColumn.getType())){
                        generateResultStatement((ResultSet) cs.getObject(spDtoColumn.getOrder()),spDtoColumn.getName(),setMethod);
                    }else if (spDtoColumn.getType().equals(String.class)){
                        Object object=cs.getObject(spDtoColumn.getOrder());
                        setMethod.invoke(this.getSpdto(),String.valueOf(object));
                    }else if (spDtoColumn.getType().equals(Integer.class)||spDtoColumn.getType().equals(Long.class)){
                        Object object=cs.getObject(spDtoColumn.getOrder());
                        setMethod.invoke(this.getSpdto(),Integer.valueOf(object.toString()));
                    }else {
                        throw new RuntimeException("不支持的参数类型"+spDtoColumn.getType());
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void generateResultStatement(ResultSet rs,String fieldName,Method setMethod) throws  SQLException{
        List list=new ArrayList();
        Map map=null;
        ResultSetMetaData rsmt=rs.getMetaData();
        int i= rsmt.getColumnCount();
        int j=1;
        String colunmName;
        String value;
        while (rs.next()){//每一行进行循环
            map=new HashMap();
            while (j<=i){//每一列循环
                colunmName= rsmt.getColumnName(j++);
                value=rs.getString(colunmName);
                map.put(colunmName,value);
            }
            j=1;
        list.add(map);
        }
        try {
            setMethod.invoke(this.getSpdto(),list);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
