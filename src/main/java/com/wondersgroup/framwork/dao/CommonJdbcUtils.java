package com.wondersgroup.framwork.dao;

import com.wondersgroup.framwork.dao.bo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Component
public class CommonJdbcUtils {
    public CommonJdbcDao getCommonJdbcDaoPrivate() {
        return commonJdbcDaoPrivate;
    }

    public void setCommonJdbcDaoPrivate(CommonJdbcDao commonJdbcDaoPrivate) {
        this.commonJdbcDaoPrivate = commonJdbcDaoPrivate;
    }

    @Resource
    private  CommonJdbcDao commonJdbcDaoPrivate;
    private static CommonJdbcDao commonJdbcDao;
    @PostConstruct
    public void init() {
        this.commonJdbcDao = commonJdbcDaoPrivate;
    }
    public static CommonJdbcDao getCommonJdbcDAO()
    {
        return commonJdbcDao;
    }

    /**
     * 查询列表
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(String sql, Class<T> clazz, Object... arguments){
        return  commonJdbcDao.queryList(sql, clazz, arguments);
    }

    /**
     * 查询分页
     * @param page
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     */
    public static <T> void queryPageList(Page page, String sql, Class<T> clazz, Object ... arguments){
         commonJdbcDao.queryPageList(page,sql,clazz,arguments);
    }

    /**
     * 查询第一条记录
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public static  <T> T queryFirst(String sql, Class<T> clazz, Object ... arguments){
        return  commonJdbcDao.queryFirst(sql,clazz,arguments);
    }

    /**
     * 查询记录总数
     * @param sql
     * @param arguments
     * @return
     */
    public static long queryCount(String sql,Object...arguments){
        return  commonJdbcDao.queryCount(sql,arguments);
    }

    /**
     * 查询对象
     * @param sql
     * @param clazz
     * @param arguments
     * @param <T>
     * @return
     */
    public static <T> T queryObject(String sql,Class<T> clazz,Object...arguments){
        return commonJdbcDao.queryObject(sql,clazz,arguments);
    }

    /**
     * 仅一条返回记录时返回查询Map结果
     * @param sql
     * @param arguments
     * @return
     */
    public static Map<String,Object> queryMap(String sql, Object...arguments){
        return commonJdbcDao.queryMap(sql,arguments);
    }
}
