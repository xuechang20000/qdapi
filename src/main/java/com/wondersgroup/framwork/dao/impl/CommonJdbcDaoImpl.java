package com.wondersgroup.framwork.dao.impl;

import com.wondersgroup.framwork.dao.CommonJdbcDao;
import com.wondersgroup.framwork.dao.bo.DataBaseType;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.framwork.dao.bo.SqlCreator;
import com.wondersgroup.framwork.dao.mapper.ObjectRowMapper;
import com.wondersgroup.framwork.dao.utils.ClassUtils;
import com.wondersgroup.framwork.dao.utils.SqlPageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
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
        List<Object[]> args=new ArrayList<Object[]>();
        SqlCreator sqlCreator=null;
        for (T t:list){
            sqlCreator=ClassUtils.getSqlCreator(t,isIncludeNull);
            if ("INSERT".equalsIgnoreCase(insertOrUpdate))
                sqlCreator.generateInsertSql();
            else
                sqlCreator.generateUpdateSql();
            args.add(sqlCreator.getArgs().toArray());
        }
        this.jdbcTemplate.batchUpdate(sqlCreator.getSql(),args);
    }
    /**
     * 批量更新对象，更新所有字段
     * @param list
     */
    public <T> void updateBatch(List<T> list){
        this.updateBatchObjects(list,"UPDATE",true);
    }
    /**
     * 批量更新对象,只更新非空字段
     * @param list
     */
    public <T>void updateBatchBySelect(List<T> list){
        this.updateBatchObjects(list,"UPDATE",false);
    }
    /**
     * 批量插入对象,只更新非空字段
     * @param list
     */
    public <T> void insertBatchBySelect(List<T> list){
        this.updateBatchObjects(list,"INSERT",false);
    }
    /**
     * 批量插入对象,插入所有字段
     * @param list
     */
    public <T> void insertBatch(List<T> list){
        this.updateBatchObjects(list,"INSERT",true);
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
        SqlCreator sqlCreator=ClassUtils.getSqlCreator(object,isIncludeNull);
        sqlCreator.generateInsertSql();
        this.jdbcTemplate.update(sqlCreator.getSql(),
                sqlCreator.getArgs().toArray());
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

}
