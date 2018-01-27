package com.wondersgroup.framwork.dao.impl;

import com.wondersgroup.framwork.dao.CommonJdbcDao;
import com.wondersgroup.framwork.dao.bo.DataBaseType;
import com.wondersgroup.framwork.dao.bo.Page;
import com.wondersgroup.framwork.dao.mapper.ObjectRowMapper;
import com.wondersgroup.framwork.dao.utils.MyBatchPreparedStatementSetter;
import com.wondersgroup.framwork.dao.utils.SqlPageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConfigurationProperties(prefix = "application.database")
public class CommonJdbcDaoImpl implements CommonJdbcDao {
    private Logger logger= LoggerFactory.getLogger(CommonJdbcDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
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

        sql=SqlPageUtils.handlerPagingSQL(sql,type);

        //计算开始，结束记录
        page.calculate();
        page.setTotal(total);
        //重置参数
        Object args[]=new Object[arguments.length+2];
        for (int i=0;i<arguments.length;i++){
            args[i]=arguments[i];
        }

        if(type.equals(DataBaseType.ORACLE)) {
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
        sql=SqlPageUtils.getCountSql(sql,type);
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
        sql=SqlPageUtils.handlerPagingSQL(sql,type);

        //重置参数
        Object args[]=new Object[arguments.length+2];
        for (int i=0;i<arguments.length;i++){
            args[i]=arguments[i];
        }
        if(type.equals(DataBaseType.ORACLE)) {
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
     */
    public void batchUpdate(List<Object> list){
        StringBuffer sql=new StringBuffer();
        this.jdbcTemplate.batchUpdate(sql.toString(),new MyBatchPreparedStatementSetter(list));
    }
}
