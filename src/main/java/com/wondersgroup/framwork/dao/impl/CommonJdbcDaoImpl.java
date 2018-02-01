package com.wondersgroup.framwork.dao.impl;

import com.wondersgroup.framwork.dao.CommonJdbcDao;
import com.wondersgroup.framwork.dao.bo.DataBaseType;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.framwork.dao.bo.SqlCreator;
import com.wondersgroup.framwork.dao.mapper.ObjectRowMapper;
import com.wondersgroup.framwork.dao.mapper.SimpleBatchPreparedStatementSetter;
import com.wondersgroup.framwork.dao.mapper.SimpleCallableStatementCallback;
import com.wondersgroup.framwork.dao.utils.ClassUtils;
import com.wondersgroup.framwork.dao.utils.SqlPageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommonJdbcDaoImpl implements CommonJdbcDao {
    private Logger logger= LoggerFactory.getLogger(CommonJdbcDaoImpl.class);
    public static  String databaseProductName="";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDataBaseProductName(){
        Connection connection=null;
        try {
             connection=this.jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData meta = connection.getMetaData();
            databaseProductName = meta.getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection=null;
        }
    }
    protected DataSource getDataSource(){
        return this.jdbcTemplate.getDataSource();
    }
    public Connection getConnection(){
        return DataSourceUtils.getConnection(this.getDataSource());
    }

    /**
     * 查询列表
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public  <T> List<T> queryList(String sql, Class<T> clazz, Object ... arguments){
        List result = null;
        Long startMillis=System.currentTimeMillis();
        if ((arguments.length == 1) && (List.class.isAssignableFrom(arguments[0].getClass()))) {
            List argumentsList = (List)arguments[0];
            result = this.jdbcTemplate.query(sql, argumentsList.toArray(), new ObjectRowMapper(clazz));
        } else {
            result = this.jdbcTemplate.query(sql, arguments, new ObjectRowMapper(clazz));
        }
        logger.info(String.format("sqltime:%d--%s",System.currentTimeMillis()-startMillis,sql));
        return ((result == null) ? new ArrayList() : result);
    }

    /**
     * 分页查询
     * @param page
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     */
    public <T> void queryPageList(Page page,String sql,Class<T> clazz,Object ... arguments){
        //查询记录总数
        Long total=this.queryCount(sql,arguments);

        sql=SqlPageUtils.handlerPagingSQL(sql,databaseProductName);

        //计算开始，结束记录
        page.calculate();
        page.setTotal(total);
        //重置参数
        Object args[]=new Object[arguments.length+2];
        for (int i=0;i<arguments.length;i++){
            args[i]=arguments[i];
        }

        if(databaseProductName.equalsIgnoreCase(DataBaseType.ORACLE)) {
            args[arguments.length]=page.getEndNum();
            args[arguments.length+1]=page.getStartNum();
        }else {//mysql数据库
            args[arguments.length]=page.getStartNum();
            args[arguments.length+1]=page.getPageSize();
        }

        List<T> list=this.queryList(sql,clazz,args);
        page.setResultList(list);
    }

    /**
     * 查询单条记录
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public  <T> T queryFirst(String sql, Class<T> clazz, Object ... arguments){
        Page page=new Page(0,1);
        page.calculate();
        this.queryPageList(page,sql,clazz,arguments);
        List<T> list=page.getResultList();
        if(list!=null&&list.size()>0) return list.get(0);
        return null;
    }
    /**
     * 查询总数
     * @param sql
     * @param arguments
     * @return
     */
    public long queryCount(String sql,Object...arguments){
        sql=SqlPageUtils.getCountSql(sql,databaseProductName);
        return  (long) this.queryObject(sql,Integer.class,arguments);
    }

    /**
     *
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public <T> T queryObject(String sql,Class<T> clazz,Object...arguments){
        return  this.jdbcTemplate.queryForObject(sql,clazz,arguments);
    }

    /**
     * 查询map结果
     * @param sql
     * @param arguments
     * @return
     */
    public Map<String,Object> queryMap(String sql,Object...arguments){
        sql=SqlPageUtils.handlerPagingSQL(sql,databaseProductName);

        //重置参数
        Object args[]=new Object[arguments.length+2];
        for (int i=0;i<arguments.length;i++){
            args[i]=arguments[i];
        }
        if(databaseProductName.equals(DataBaseType.ORACLE)) {
            args[arguments.length]=1;
            args[arguments.length+1]=0;
        }else {//mysql数据库
            args[arguments.length]=0;
            args[arguments.length+1]=1;
        }
        return  this.jdbcTemplate.queryForMap(sql,args);
    }



    /**
     * 批量更新对象
     * @param list
     * @param insertOrUpdate
     * @param isIncludeNull
     */
    public <T> void updateBatchObjects(List<T> list,String insertOrUpdate, boolean isIncludeNull){
        if (list==null||list.size()==0) return;

        SimpleBatchPreparedStatementSetter simpleBatchPreparedStatementSetter
                =new SimpleBatchPreparedStatementSetter(list,isIncludeNull,insertOrUpdate);
        this.jdbcTemplate.batchUpdate(simpleBatchPreparedStatementSetter.getSql(),simpleBatchPreparedStatementSetter);
    }
    /**
     * 批量更新对象，更新所有字段
     * @param list
     */
    public <T> void updateBatch(List<T> list){
        this.updateBatchObjects(list,"update",true);
    }
    /**
     * 批量更新对象,只更新非空字段
     * @param list
     */
    public <T>void updateBatchBySelect(List<T> list){
        this.updateBatchObjects(list,"update",false);
    }
    /**
     * 批量插入对象,只更新非空字段
     * @param list
     */
    public <T> void insertBatchBySelect(List<T> list){
        if ("MySQL".equalsIgnoreCase(databaseProductName)){
            for (T t:list){
                insertObject(t,false);
            }
        }else {
            this.updateBatchObjects(list,"insert",false);
        }
    }
    /**
     * 批量插入对象,插入所有字段
     * @param list
     */
    public <T> void insertBatch(List<T> list){
        if ("MySQL".equalsIgnoreCase(databaseProductName)){
            for (T t:list){
              insertObject(t,true);
            }
        }else {
            this.updateBatchObjects(list,"insert",true);
        }

    }
    /**
     * 更新对象
     * @param object 对象
     * @param isIncludeNull 是否包括空值
     */
    public void updateObject(Object object,boolean isIncludeNull){
        SqlCreator sqlCreator=ClassUtils.getSqlCreator(object,isIncludeNull);
        sqlCreator.generateUpdateSql();
        this.jdbcTemplate.update(sqlCreator.getSql(),
                sqlCreator.getArgs().toArray());
    }
    /**
     * 插入对象
     * @param object 对象
     * @param isIncludeNull 是否包括空值
     */
    public void insertObject(Object object,boolean isIncludeNull){
        final SqlCreator sqlCreator=ClassUtils.getSqlCreator(object,isIncludeNull);
        sqlCreator.generateInsertSql();
        if ("MySQL".equalsIgnoreCase(databaseProductName)){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps=connection.prepareStatement(sqlCreator.getSql(), Statement.RETURN_GENERATED_KEYS);
                    ClassUtils.prepareStatement(ps,sqlCreator.getArgs());
                    return ps;
                }
            },keyHolder);
            //回写主键值
            ClassUtils.setIDValue(object,keyHolder.getKey().longValue());
        }else {
            this.jdbcTemplate.update(sqlCreator.getSql(),
               sqlCreator.getArgs().toArray());
        }


    }
    /**
     * 更新对象所有值包括空值
     * @param object 对象
     */
    public void update(Object object){
       this.updateObject(object,true);
    }
    /**
     * 更新对象，不包括空值
     * @param object 对象
     */
    public void updateSelect(Object object){
        this.updateObject(object,false);
    }
    /**
     * 插入对象所有值包括空值
     * @param object 对象
     */
    public void insert(Object object){
        this.insertObject(object,true);
    }

    /**
     * 获取sequence值（oracle）
     * @param sequenceName
     * @return
     */
    public Long getSequence(String sequenceName){
        return  this.queryObject("select "+sequenceName+".nextval from dual",Long.class);
    }

    /**
     * sql执行
     * @param sql
     * @param args
     * @return
     */
    public int execute(String sql,Object...args){
        return  this.jdbcTemplate.update(sql,args);
    }

    /**
     * 调用存储过程
     * @param spObj
     * @param spName
     */
    public  void  callProcedure(Object spObj,String ...spName) {

        SimpleCallableStatementCallback simpleCallableStatementCallback
                =new SimpleCallableStatementCallback(spName.length>0?spName[0]:null,spObj);
        this.jdbcTemplate.execute(simpleCallableStatementCallback.getSql(),simpleCallableStatementCallback);
    }
}
